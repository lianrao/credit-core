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
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * 启信宝股东出资比例数据源
 * @author nan.liu
 * @date 2018年1月28日
 */
@DataSourceClass(bindingDataSourceId="ds_qxb_shareRatio")
public class QXBQueryShareRatioRequestor extends BaseQXBDataSourceRequestor
		implements IDataSourceRequestor {	
	private final Logger logger = LoggerFactory
			.getLogger(QXBQueryShareRatioRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 股东出资比例调用开始...",prefix);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String,Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		String ratio_url = propertyEngine.readById("qxb_qryCorp_shareRatio_url");
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(ratio_url);
		// 默认交易失败
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		/**数据总线*/
		Map<String,Object> ctx = new HashMap<String,Object>();
		try {
			/** name 企业名称*/
			String name = (String)ParamUtil.findValue(ds.getParams_in(), "name");
			
			ctx.put("name", name);
			ctx.put("trade_id", trade_id);

			logObj.setIncache("0");
			/** 拼装请求报文 */
			String reqUrl = buildRequestUrl(name,ratio_url);			
			logger.info("{} 开始股东信息查询请求:",prefix);
			/** 发送请求报文 */
	        String rspStr = RequestHelper.doGet(reqUrl, null, false);
	        logger.info("{} 开始股东信息查询成功:{}",prefix,rspStr);
			JSONObject rspJsn = (JSONObject)JSONObject.parse(rspStr);
			logObj.setBiz_code1(rspJsn.getString("sign"));
			/**检查响应信息是否成功*/
			if(isSuccessful(rspJsn)){
				retdata.put("data", rspJsn.get("data"));
				resource_tag = Conts.TAG_FOUND;
			    logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			    rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}else if(isUnfound(rspJsn)){
			    logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				resource_tag = Conts.TAG_UNFOUND;
			    logObj.setState_msg((String)rspJsn.get("message"));
			    logger.warn("{} 未查得股东信息:{}",prefix,rspJsn.get("message"));
			    rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_DS_QIXINBAO_SHARE_CODE_FAIL);
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_MSG, "未查得股东信息");
				return rets;
			}else {
				resource_tag = Conts.TAG_SYS_ERROR;
			    logObj.setState_msg((String)rspJsn.get("message"));
			    logger.error("{} 股东信息查询出错:{}",prefix,rspJsn.get("message"));
				rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;				
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "交易成功");
		}catch (Exception ex) {
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
	    	rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
		    logger.error(prefix + " 数据源处理时异常", ex);
			if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				resource_tag = Conts.TAG_SYS_TIMEOUT;
			} else {
				resource_tag = Conts.TAG_SYS_ERROR;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			}
       }finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, ctx, logObj);
		}
		return rets;
	}

	private boolean isUnfound(JSONObject rspJsn) {
		if("201".equals(rspJsn.getString("status"))){
			return true;
		}
		return false;
	}

	private boolean isSuccessful(JSONObject rspJsn) {
		if("200".equals(rspJsn.getString("status"))){
			return true;
		}
		return false;
	}

	private String buildRequestUrl(String name,String url) {
		String req_url = new StringBuilder(url).
				append("?appkey=").append(propertyEngine.readById("qxb_qryCorp_appkey"))
				.append("&keyword=").append(name).toString();	
		return req_url;
	}
}
