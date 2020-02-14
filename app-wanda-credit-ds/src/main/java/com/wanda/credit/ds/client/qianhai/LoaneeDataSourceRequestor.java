package com.wanda.credit.ds.client.qianhai;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.qianhai.LoaneeVO;
import com.wanda.credit.ds.dao.iface.IQHLoaneeService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @title 常贷客数据源
 * @date 2016-04-01
 * @author chsh.wu
 * */
@SuppressWarnings("unchecked")
@DataSourceClass(bindingDataSourceId="ds_qh_loanee_glad")
public class LoaneeDataSourceRequestor extends
		BaseQHDataSourceRequestor implements IDataSourceRequestor{
	private final Logger logger = LoggerFactory
			.getLogger(LoaneeDataSourceRequestor.class);

	@Autowired
	private IQHLoaneeService loaneeService;

	@Autowired
	private IExecutorSecurityService synchExecutorService;

	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String,Object> paramForLog = new HashMap<String,Object>(); 
		Map<String, Object> rets = null;
		List<String> tags = new ArrayList<String>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(this.getUrl());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try {
			/**获取请求参数*/
			rets = new HashMap<String, Object>();
			String name = ParamUtil.findValue(ds.getParams_in(), "name")
					.toString(); // 姓名
			String idNo = ParamUtil.findValue(ds.getParams_in(), "cardNo")
					.toString(); // 身份证号码
            String encryIdNo = synchExecutorService.encrypt(idNo);

			String authCode = ParamUtil.findValue(ds.getParams_in(), "authCode")
					.toString(); // 授权码
			String authDate = ParamUtil.findValue(ds.getParams_in(), "authDate")
					.toString(); // 授权时间
			String statsPeriod = (String)ParamUtil.findValue(ds.getParams_in(), "statsPeriod"); // 统计周期 月份为单位
			
			CRSStatusEnum retStatus = CRSStatusEnum.STATUS_FAILED_DS_QIANHAI_LOANEE_EXCEPTION;
			String retMsg = "数据源查询异常";
			// 缓存信息
			/*BlackListVO inCached = blackListService.inCached(name,
					DEFAULT_IDTYPE, idNo);

			if (inCached == null) {*/
				logger.info("{} 常贷客数据源采集开始......", new String[] { prefix });
				/**组装上下文数据*/ 
				Map<String, Object> context = new HashMap<String, Object>();
				context.put(TRADE_ID, trade_id);
				/**记录请求参数*/
                
                paramForLog.put(NAME, name);
                paramForLog.put(IDNO, idNo);
                paramForLog.put("entityAuthCode",authCode);
                paramForLog.put("entityAuthDate",authDate);
                paramForLog.put("statsPeriod", statsPeriod);
                /**位置最好不要轻易动 */
                context.putAll(paramForLog);
                DataSourceLogEngineUtil.writeParamIn(trade_id, paramForLog,logObj);
                /**发送请求 解析结果*/                
				Map<String, Object> retMap = executeClient(context);
				JSONObject resltJsn = (JSONObject) retMap.get("resltJsn");
				JSONObject busiJsn = (JSONObject) retMap.get("busiJsn");
				resltJsn = (resltJsn == null?new JSONObject() :resltJsn);
				busiJsn  = (busiJsn  == null?new JSONObject() :busiJsn);

				String rtCode = QHDataSourceUtils.getValFromJsnObj(resltJsn,"rtCode");
				String rtMsg  = QHDataSourceUtils.getValFromJsnObj(resltJsn,"rtMsg");

				/*if (resltJsn == null || busiJsn == null) {
					retMsg = "数据源厂商常贷客信息返回数据为空!";
					logger.error("{} 数据源厂商常贷客信息返回数据为空!",
							new String[] { prefix });
				} else */
				
				logObj.setIncache("0");
				//用于数据库存储
				resltJsn.put("trade_id", trade_id); 
				if (!SUCC_CODE.equals(rtCode)) {
					// 结果信息返回失败
//					retMsg = buildErrInfo(prefix, rtCode, rtMsg);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
					logObj.setState_msg(rtMsg);
					tags.add(Conts.TAG_SYS_ERROR);
				}else {
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
                    /**处理状态码信息*/
					handleBizcode(logObj,busiJsn);
					
					retStatus = CRSStatusEnum.STATUS_SUCCESS;
					retMsg = "采集成功";
					/**保存常贷客信息*/ 
					List<LoaneeVO> loanlist = loaneeService.addLoanee(busiJsn,trade_id,context);
					if(Conts.TAG_SYS_ERROR.equals(context.get(Conts.KEY_RET_TAG))){
						 //error
						 retStatus = CRSStatusEnum.STATUS_FAILED_DS_QIANHAI_LOANEE_EXCEPTION;
						 retMsg = "多头借贷查询失败";
						 logObj.setState_msg(retMsg);
						 tags.add(Conts.TAG_SYS_ERROR);
						 logger.error("{} 常贷客查询失败 {}",trade_id,(String)context.get("erMsg"));
					 }else if(CollectionUtils.isNotEmpty(loanlist)){
						 /**设置retdata 返回数据信息*/ 
						 retdata.put("found", "1");
		                 retdata.putAll(visitBusiData(busiJsn,context));
		                 String tag = buildTagFromQHDslog(ds.getId(), encryIdNo);
		 				 tags.add(tag);
		 				 logObj.setBiz_code1(tag);
						 logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						 if(Conts.TAG_FOUND_NEWRECORDS.equals(tag)){
						      qhlogService.addNewLog(trade_id,ds.getId(), name, encryIdNo);	 
						   }
					 }else{
						 //not found
						 retdata.put("hitOrgs","0");
						 retdata.put("stats",0);	
						 retdata.put("found", "0");
						 String tag = Conts.TAG_UNFOUND;;
		 				 tags.add(tag);
						 logObj.setBiz_code1(tag);
						 logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						 logger.warn("{} 没有查询到常贷客信息",trade_id);
						 if(Conts.TAG_FOUND_NEWRECORDS.equals(tag)){
						      qhlogService.addNewLog(trade_id,ds.getId(), name, encryIdNo);	 
						   }
					 }
				}				
			rets.put(Conts.KEY_RET_STATUS, retStatus);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, retMsg);
		} catch (Exception ex) {
			tags.clear();
			if(isTimeoutException(ex)){
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);	
		    	tags.add(Conts.TAG_SYS_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
				tags.add(Conts.TAG_SYS_ERROR);
			}
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error(prefix+" 数据源处理时异常", ex);
		}finally{
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setTag(org.apache.commons.lang.StringUtils.join(tags, ";"));
	    	logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
	    	DataSourceLogEngineUtil.writeLog(trade_id,logObj);
	    	DataSourceLogEngineUtil.writeParamIn(trade_id, paramForLog, logObj);
		}
		return rets;
	}

	/**处理log的bizcode值 */
	private void handleBizcode(DataSourceLogVO log, JSONObject busiJsn) {
		/** 查询成功 */
		log.setBiz_code1("QIANHLOAN_SUCC");
		List<Map<String, Object>> recrdls = (List<Map<String, Object>>) busiJsn
				.get("records");
		for (Map<String, Object> recrd : recrdls) {
			if (recrd == null)
				continue;
			String erCode = (String) recrd.get("erCode");
			if (StringUtils.hasText(erCode) && !"E000000".equals(erCode)) {
				/** 查询失败 */
				log.setBiz_code1("QIANHLOAN_FAIL");
				return;
			}
		}
	}

	private String buildErrInfo(String prefix, String errCode, String errMsg) {
		// 结果信息返回失败
		String retMsg = "数据源返回异常! 异常码：" + errCode + ",异常信息:" + errMsg;
		logger.error("{} 数据源返回异常! 代码:{},错误消息:{}", new String[] {
				prefix, errCode, errMsg });
		return retMsg;
	}
	

	@Override
	protected Map<String, Object> buildRequestBody(Map<String,Object> data) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("batchNo", buildBatchNo());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> record = new HashMap<String, Object>();
		record.put(NAME, data.get(NAME));
		record.put(IDNO, data.get(IDNO));
		record.put("reasonCode", getReasonCode());
		record.put("idType", idType);
		record.put("entityAuthCode", data.get("entityAuthCode"));
		record.put("entityAuthDate", data.get("entityAuthDate"));
		// 目前只支持单条查询
		record.put("seqNo", 1);
		list.add(record);
		body.put("records", list);
		return body;
	}
	
	/**
	 * 设置 stats 属性
	 * 根据 busiDate 统计最近 statsPeriod 个月内记录数目
	 * 
	 * */
	@SuppressWarnings("deprecation")
	private Map<String, Object> visitBusiData(JSONObject jsnObj,Map<String,Object> ctx) {
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		List<Map<String, Object>> recrdls = (List<Map<String, Object>>) jsnObj
				.get("records");
		retMap.put("records", recrdls);
		if(recrdls == null || recrdls.size() == 0)return retMap;
		/**单人业务发生机构数≥N(eg.15)家 没有找到就是0*/
		retMap.put("hitOrgs","0");
		Map<String, Object> firstRow = recrdls.get(0);
		if(firstRow != null){
		  String hitOrgs = (String)firstRow.get("amount");
		  if(StringUtils.hasText(hitOrgs)){
			    retMap.put("hitOrgs",hitOrgs);
			}
	    }
		
		/**计算statsPeriodInt前的 日期 为mindate*/
		String statsPeriodStr = (String)ctx.get("statsPeriod");
		int statsPeriodInt = 0;
		if(StringUtils.hasText(statsPeriodStr) && 
				statsPeriodStr.matches("^[1-9][0-9]{0,}$")){
			statsPeriodInt = Integer.parseInt(statsPeriodStr);
		}
		
		/**计算当前业务发生日期是否在 mindate之后*/
		if(statsPeriodInt > 0){
			Date min = DateUtils.addMonths(new Date(), Integer.parseInt("-"+statsPeriodInt));
			min.setDate(01);
			String minDateStr = format.format(min);
			int stats = 0;
			for(Map<String,Object> item : recrdls){
				String busiDateStr = (String)item.get("busiDate");
				if(!StringUtils.hasText(busiDateStr) || 
						"0".equals(busiDateStr) || "-1".equals(busiDateStr))continue;
				if(isInPeriod(busiDateStr,minDateStr))stats++;
			}
			retMap.put("stats",stats);	
		}
		return retMap;
	}

	/**
	 * 比较 busiDateStr 是否 大于等于 minDateStr
	 * */
    private boolean isInPeriod(String busiDateStr, String minDateStr) {
    	/**统一格式化成yyyy-mm-dd*/
    	if(!busiDateStr.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")){
    		busiDateStr = standrdDateStr(busiDateStr);
    	}
    	if(busiDateStr.compareTo(minDateStr)>=0)return true;
		return false;
	}

    /**
     * 针对不满足 yyyy-mm-dd 格式的字符串左边补齐0
     * */
	private String standrdDateStr(String busiDateStr) {
		String[] items = busiDateStr.split("-");
		items[0] = String.format(String.format("%04d",Integer.parseInt(items[0])));		
		items[1] = String.format(String.format("%02d",Integer.parseInt(items[1])));		
		items[2] = String.format(String.format("%02d",Integer.parseInt(items[2])));		
		return items[0]+"-"+items[1]+"-"+items[2];
	}

	public static void main(String[] args) {
		System.out.println(StringUtils.hasText(null));
	}
	
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

}
