/**   
* @Description: 法海风控信息列表查询(企业)
* @author nan.liu
* @date 2019年1月25日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.fahai;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.conn.ConnectTimeoutException;
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
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


@DataSourceClass(bindingDataSourceId="ds_fahai_riskmanage_new")
public class FahaiRiskManagerRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(FahaiRiskManagerRequestor.class);	
	@Autowired
	private IPropertyEngine propertyEngine;
	public Map<String, Object> request(String trade_id, DataSource ds) {
		String url = propertyEngine.readById("ds_fahai_riskmanage_url").trim();
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 法海-企业负面列表查询Begin {}" , prefix ,start);	
		String authcode = propertyEngine.readById("ds_fahai_detail_authcode");

		String resource_tag = Conts.TAG_SYS_ERROR;
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();
		Map<String, Object> req_param = new HashMap<String,Object>();
		//计费标签
		Set<String> tags = new HashSet<String>();
		tags.add(Conts.TAG_SYS_ERROR);
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(url);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);		
		try{//datatype,pname,pageno,range,crawlTime
			logger.info("{} 开始解析传入的参数" , prefix);
			String dataType = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			String pageno = "";
			String range = "";
			String crawlTime = "";
			if(ParamUtil.findValue(ds.getParams_in(), "pageno")!=null){
				pageno = ParamUtil.findValue(ds.getParams_in(), "pageno").toString();
			}
			if(ParamUtil.findValue(ds.getParams_in(), "range")!=null){
				range = ParamUtil.findValue(ds.getParams_in(), "range").toString();
			}
			if(ParamUtil.findValue(ds.getParams_in(), "crawlTime")!=null){
				crawlTime = ParamUtil.findValue(ds.getParams_in(), "crawlTime").toString();
			}
			logger.info("{} 解析传入的参数成功" , prefix);
			req_param.put("datatype", dataType);
			req_param.put("pname", name);
			req_param.put("pageno", pageno);
			req_param.put("range", range);
			req_param.put("crawlTime", crawlTime);
			String params = getFaHParams(authcode,name,dataType,pageno,range,crawlTime);
			logger.info("{} 开始远程调用,请求url:{}",trade_id,url+"?"+params);
			Map<String, Object> rspMsg = RequestHelper.doGetRetFull(url+"?"+params, null, null, false,null,
	                "UTF-8");
			String res = String.valueOf(rspMsg.get("res_body_str"));
			logger.info("{} 调用成功,返回信息:{}",trade_id,res);
			if (StringUtil.isEmpty(res) || "null".equals(res)) {
				logger.error("{} 返回信息为空",trade_id);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;
			}
			JSONObject json = JSONObject.parseObject(res);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			if(!"s".equals(json.getString("code"))){//调用失败
				resource_tag = Conts.TAG_TST_FAIL;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				resource_tag = Conts.TAG_TST_SUCCESS;
				JSONObject result = getJsonKeys(json,"code,msg,searchSeconds");
					
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_DATA, result);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "采集成功!");
			}		
		}catch(Exception e){
			logger.error("{} 法海负面交易处理异常：{}" , prefix , ExceptionUtil.getTrace(e));
			resource_tag = Conts.TAG_TST_FAIL;
			if (e instanceof ConnectTimeoutException) {				
				logger.error("{} 连接远程数据源超时" , prefix);				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				//设置标签
				resource_tag = Conts.TAG_SYS_TIMEOUT;
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{			
			rets.put(Conts.KEY_RET_TAG,new String[]{resource_tag});
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			long dsLogStart = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, req_param, logObj);
			logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
		}
		logger.info("{} 法海负面详情End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;	
	}
	public static String getFaHParams(String authCode,String pname,String dataType
			,String pageno,String range,String crawlTime){
		//{reqUrl}?authCode=${authCode}&pname=${pname}&idcardNo=${cardNo}&pageno=${pageno}&range=${range}&dataType=${datatype}
		String params1 = "authCode="+authCode+"&q=pname:"+pname+
				"&dataType="+dataType;
		String params2 = "";
		if(!StringUtil.isEmpty(crawlTime)){
			params2 = "authCode="+authCode+"&q=pname:"+pname+"@sortTime:"+crawlTime+"&dataType="+dataType;
		}
		if(!StringUtil.isEmpty(pageno)){
			params1 = params1+"&pageno="+pageno;
			params2 = params2+"&pageno="+pageno;
		}else{
			params1 = params1+"&pageno=1";
			params2 = params2+"&pageno=1";
		}
		if(!StringUtil.isEmpty(range)){
			params1 = params1+"&range="+range;
			params2 = params2+"&range="+range;
		}else{
			params1 = params1+"&range=10";
			params2 = params2+"&range=10";
		}
		if(!StringUtil.isEmpty(crawlTime)){
			return params2;
		}
		return params1;
	}
	public JSONObject getJsonKeys(JSONObject jsonObject,String exceptKeys){
		JSONObject json = new JSONObject();
		for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
			if(!isExcekey(exceptKeys,entry.getKey())){
				json.put(entry.getKey(), entry.getValue());
			}
        }
		return json;
	}
	public boolean isExcekey(String exceptKeys,String key){
		for(String exceptKey:exceptKeys.split(",")){
			if(exceptKey.equals(key)){
            	return true;
            }
		} 
		return false;
	}
}
