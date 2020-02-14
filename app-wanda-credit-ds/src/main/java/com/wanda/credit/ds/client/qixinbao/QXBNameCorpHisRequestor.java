/**   
* @Description: 数据源-启信宝-59.6根据人名和企业名称获得历史投资信息
* @author liunan
* @date 2019年4月3日 下午2:03:47 
* @version V1.0   
*/
package com.wanda.credit.ds.client.qixinbao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.qixinbao.bean.EnterListRes;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_qxb_historynameCorp")
public class QXBNameCorpHisRequestor extends BaseQXBDataSourceRequestor
		implements IDataSourceRequestor {	
	private final static Logger logger = LoggerFactory.getLogger(QXBNameCorpHisRequestor.class);
	
	private final static String RETDATA_DETAIL = "detail";
	@Autowired
	public IPropertyEngine propertyEngine;
	
	public Map<String, Object> request(String trade_id, DataSource ds) {		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		// 记录交易开始时间
		long startTime = System.currentTimeMillis();
		logger.info("{} 启信宝-根据企业全名和自然人名获取该自然人历史任法定代表人,对外投资及任职信息", prefix);
		// 组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		// 交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		// 计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{
			//获取请求参数
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String entname = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			reqparam.put("name", name);
			reqparam.put("entname", entname);
			
			//获取配置参数
			String reqUrl = propertyEngine.readById("qxb_enter_nameCorp_history_url");
			String appKey = propertyEngine.readById("qxb_enterList_appkey");
			logObj.setReq_url(reqUrl);
			//请求启信宝获取数据
			Map<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("company", entname);
			reqParams.put("name", name);
			reqParams.put("appkey", appKey);
			String httpGetRes = RequestHelper.doGet(reqUrl, reqParams, true);
			
			if (StringUtil.isEmpty(httpGetRes)) {
				logger.info("{} http请求启信宝返回内容为空：{}" , prefix , httpGetRes);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				return rets;
			}
			
			//数据处理
			EnterListRes resObj = JSONObject.parseObject(httpGetRes, EnterListRes.class);
			
			String status = resObj.getStatus();
			if (StringUtil.isEmpty(status) || !StringUtil.isNumeric(status)) {
				logger.info("{} 数据源返回信息中status异常：{}" , prefix , status);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}			
			int statusInt = Integer.parseInt(status);
			
			if (200 != statusInt) {
				logger.info("{} 数据源返回内容为：{}" , prefix , httpGetRes);
			}
			
			//记录日志信息表
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			logObj.setState_msg("交易成功");
			logObj.setBiz_code3(status);
			logger.info("{} 数据源返回包装开始..." , prefix);
			if(statusInt==200){
				retData = parse2Out(resObj,retData,prefix);

				resource_tag = Conts.TAG_FOUND;
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_SUCCESS.getRet_msg());
				rets.put(Conts.KEY_RET_DATA, retData);
			}else if(statusInt==201){
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JINGZHONG_IHCACHE_NULL);
				rets.put(Conts.KEY_RET_MSG, "查无记录");
				return rets;
			}else if(statusInt==208){
				logger.info("{} 传入参数格式有误", trade_id);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
						
		}catch(Exception e){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(e));
			if (ExceptionUtil.isTimeoutException(e)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + e.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}		
		logger.info("{}  启信宝-模糊搜索匹配企业信息 END,耗时：{}" ,prefix , System.currentTimeMillis() - startTime);
		
		return rets;
	}

	/**
	 * 解析数据源返回的数据并输出
	 * @param resObj
	 * @param retData
	 * @param prefix
	 * @return
	 */
	private TreeMap<String, Object> parse2Out(EnterListRes resObj,
			TreeMap<String, Object> retData, String prefix) {		
		String dataStr = resObj.getData();
		JSONObject data = JSONObject.parseObject(dataStr);		
		retData.put(RETDATA_DETAIL, data);
		return retData;
	}
}
