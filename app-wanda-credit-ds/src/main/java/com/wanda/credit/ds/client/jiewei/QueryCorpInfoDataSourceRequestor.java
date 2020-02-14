package com.wanda.credit.ds.client.jiewei;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.xzy.Query;


/**
 * 捷为工商信息查询数据源
 * @author changsheng.wu
 * @date 2016年3月10日
 */
@SuppressWarnings("unchecked")
@DataSourceClass(bindingDataSourceId="ds_jiewei_queryCorpInfo")
public class QueryCorpInfoDataSourceRequestor extends BaseJieWeiDataSourceRequestor
		implements IDataSourceRequestor {
	
	private final Logger logger = LoggerFactory
			.getLogger(QueryCorpInfoDataSourceRequestor.class);

	private final static String DS_ID = "ds_jiewei_queryCorpInfo";
	
	@Autowired
	private JieWeiFormatOutputSupport outputSupport;

	@Autowired
	public IPropertyEngine propertyEngine;

	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String,Object>();
		List<String> tags = new ArrayList<String>();
		String initTag = Conts.TAG_SYS_ERROR;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(DS_ID);
		logObj.setReq_url("http://182.92.221.159:8080/gstest/servlet/Dosearch");
		// 默认交易失败
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		/**数据总线*/
		Map<String,Object> ctx = new HashMap<String,Object>();
		ctx.put("prefix", prefix);

		try {
			CRSStatusEnum retStatus = CRSStatusEnum.STATUS_SUCCESS;
			String retMsg = "采集成功";
			
			/** 得到输入参数 企业名称 和 (registerNo:企业营业执照号或者信用代码) 必输其一 省份必输入 */
			String corpName = (String)ParamUtil
					.findValue(ds.getParams_in(), "corpName");
			String registerNo = (String)ParamUtil
					.findValue(ds.getParams_in(), "registerNo");
			String province = (String)ParamUtil
					.findValue(ds.getParams_in(), "province"); // 身份代码

			if(StringUtils.isBlank(corpName) && StringUtils.isBlank(registerNo)){
            	logger.error("{} 数据源参数校验不通过: {}",prefix,"企业名称和(营业证号或社会统一信用号)必输其一");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
                return rets;
			}
            if(StringUtils.isBlank(province) ){
            	logger.error("{} 数据源参数校验不通过: {}",prefix,"省份代码不能为空");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
                return rets;
			}
            

			ctx.put("corpName", corpName);
			ctx.put("registerNo", registerNo);
			ctx.put("province", province);
			/**/
			ctx.put("trade_id", trade_id);

			DataSourceLogEngineUtil.writeParamIn(trade_id, CommonUtil.sliceMap(ctx, 
					new String[]{"corpName","registerNo","province"}),logObj);

			/**取缓存数据*/
			String id = outputSupport.getCachedKey(corpName, registerNo, province);
			if(StringUtils.isNotBlank(id)){
				logObj.setIncache("1");
				logger.info("{} 开始从本地读取  corpName:{} registerNo:[{}] province:[{}] 的企业工商信息数据",
						new Object[]{prefix,corpName, registerNo, province});
				retdata.putAll(outputSupport.fetchFromCachedData(id));
				initTag = Conts.TAG_INCACHE_FOUND ;
				logger.info("{} 本地数据读取完毕  ",new Object[]{prefix});
			}else{
				logObj.setIncache("0");
				/** 拼装请求报文 */
				String reqXml = buildRequestMsg(ctx);
				
				logger.info("{} 开始工商信息查询请求:",prefix);
				/** 发送请求报文 */
		        long start = System.currentTimeMillis();
				String rspXml = executeClient(reqXml);
				logger.info("{} 工商信息查询响应信息:{} 耗时{}ms",
						new Object[]{prefix,rspXml,System.currentTimeMillis() - start});
				
				/**检查响应信息是否成功*/
				if(!checkRspXml(trade_id,rspXml,ctx)){
					initTag = Conts.TAG_UNFOUND_OTHERS;
				    logObj.setState_msg((String)ctx.get("baseinfoErrmsg"));
					logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				    logger.warn("{} 企业基本信息查询出错:{}",prefix,ctx.get("baseinfoErrmsg"));
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + (String)ctx.get("baseinfoErrmsg"));
					return rets;			 
				}
				
				logger.info("{} 工商信息开始落库",prefix);
				/** 解析并落地响应报文 */
				 parseRspXmlAndSave2DB(rspXml,ctx);
				logger.info("{} 工商信息保存成功",prefix);

				 /** 保存请求报文到DB 必须在解析代码后*/
				 saveRequest2DB(ctx);				 
				 handleBizcode(logObj,(String)ctx.get("baseinfoTreateResult"));				 
				 initTag = handleTag(trade_id,(String)ctx.get("baseinfoTreateResult"));
				 
				 /**从数据库中读出 Jsn 格式并返回*/
				 retdata.putAll(outputSupport.fetchFromCachedData((String)ctx.get("masterId")));

			}
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			rets.put(Conts.KEY_RET_STATUS, retStatus);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, retMsg);
		}catch(TimeoutException ex){
			/** 保存请求报文到DB 设置超时时间为1*/
			ctx.put("timeoutflag", "1");
			saveRequest2DB(ctx);
            logger.error(prefix+" 工商信息查询超时",ex);
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT);
			rets.put(Conts.KEY_RET_MSG, "数据源请求超时异常! 详细信息:" + ex.getMessage());
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			initTag = Conts.TAG_SYS_TIMEOUT;
		}catch (Exception ex) {
			initTag = Conts.TAG_SYS_ERROR;
			logger.error(prefix + " 数据源处理时异常", ex);
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logObj.setState_msg(rets.get(Conts.KEY_RET_MSG).toString());
		}finally{
			tags.add(initTag);
			logObj.setTag(StringUtils.join(tags, ";"));
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
            DataSourceLogEngineUtil.writeLog(trade_id, logObj);

		}
		return rets;
	}
	
	private String handleTag(String trade_id, String treatresult) {
		if("1".equals(treatresult))return Conts.TAG_FOUND;
		if("2".equals(treatresult))return Conts.TAG_UNFOUND;
		if("3".equals(treatresult))return Conts.TAG_UNFOUND_OTHERS;
		return "";
	}
	/**
	 * 检查返回信息是否存在数据
	 * @return false 檢查失敗  true 檢查成功
	 * */
	private boolean checkRspXml(String trade_id, String rspXml,
			Map<String, Object> ctx) {
		Document rspDoc = null;
		try {
			rspDoc = DocumentHelper.parseText(filtRspBody(rspXml));
		} catch (DocumentException e) {
            logger.error("{} 解析rspXml  {}",trade_id,e);
            return false;
		}
		Element baseEle = (Element) rspDoc
				.selectSingleNode("//corpBaseNationalInfo");
		if (baseEle != null) {
			String treatResult = baseEle.attributeValue("treatResult");
			ctx.put("baseinfoTreateResult", treatResult);			
			/** 如果企业基本信息 查询出错 设置出错信息到数据总线用于返回 提示错误 */
			if("1".equals(treatResult) || "2".equals(treatResult)){
				return true;
			}else if ("3".equals(treatResult) && 
					StringUtils.isNotBlank(baseEle.attributeValue("errorMessage"))) {
				ctx.put("baseinfoErrmsg", baseEle.attributeValue("errorMessage"));				
			}
		} else {
			ctx.put("baseinfoErrmsg", "查询返回数据为空");
		}

		return false;
	}
	/** 处理业务状态码信息 */
	private void handleBizcode(DataSourceLogVO logObj,String baseinfoTreatResult) {		
		if (StringUtils.isBlank(baseinfoTreatResult))
			return;
		DataSourceBizCodeVO bizCodeVo = DataSourceLogEngineUtil.
				fetchBizCodeByRetCode(DS_ID, baseinfoTreatResult);
		if (bizCodeVo != null) {
			logObj.setBiz_code1(bizCodeVo.getBizCode());
		}
	}
	/**
	 * 数据校验在处理方法里面*/
	@Override
	public Map<String, Object> valid(String trade_id, DataSource ds){
		Map<String, Object> rets = new HashMap<String, Object>();		
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_MSG, "数据源参数校验通过!");
		return rets;
	}

    private String executeClient(String requestXml) throws TimeoutException {
	     String timeout = propertyEngine.readById("jiewei_ds_timeout");
         String res = Query.queryReport(requestXml,Integer.valueOf(timeout));
         return res;
	}
    
    public static void main(String[] args) {}
}
