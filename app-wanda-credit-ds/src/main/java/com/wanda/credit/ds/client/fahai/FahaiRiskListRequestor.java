/**   
* @Description: 法海风控信息列表查询(个人)
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
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.fahai.IFahaiPersonService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


@DataSourceClass(bindingDataSourceId="ds_fahai_riskmge_list_person_new")
public class FahaiRiskListRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(FahaiRiskListRequestor.class);	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IFahaiPersonService fahaiService;
	public Map<String, Object> request(String trade_id, DataSource ds) {
		String url = propertyEngine.readById("ds_fahai_person_url").trim();
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 法海-法人负面列表查询Begin {}" , prefix ,start);	
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
		try{
			logger.info("{} 开始解析传入的参数" , prefix);
			String dataType = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
			String pageno = "";
			String range = "";
			if(ParamUtil.findValue(ds.getParams_in(), "pageno")!=null){
				pageno = ParamUtil.findValue(ds.getParams_in(), "pageno").toString();
			}
			if(ParamUtil.findValue(ds.getParams_in(), "range")!=null){
				range = ParamUtil.findValue(ds.getParams_in(), "range").toString();
			}
			logger.info("{} 解析传入的参数成功" , prefix);
			req_param.put("datatype", dataType);
			req_param.put("pname", name);
			req_param.put("cardNo", cardNo);
			req_param.put("pageno", pageno);
			req_param.put("range", range);
			//参数校验 - 身份证号码
			String validate = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(validate)) {
				logObj.setIncache("1");
				logger.info("{} 身份证格式校验错误： {}" , prefix , validate);
				logObj.setState_msg("身份证格式校验错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
				return rets;
			}
			String params = getFaHParams(authcode,name,cardNo,dataType,pageno,range);
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
				JSONObject result = new JSONObject();
				result.put("count", json.get("count"));
				result.put("entryList", json.get("entryList"));
				result.put("pageNo", json.get("pageNo"));
				result.put("range", json.get("range"));
					
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_DATA, result);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "采集成功!");
				try{
					fahaiService.batchSave(json.getJSONArray("entryList"), trade_id);
				}catch(Exception e){
					logger.error("{} 法海数据保存失败：{}" , prefix , ExceptionUtil.getTrace(e));
				}			
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
	public static String getFaHParams(String authCode,String pname,String cardNo,String dataType
			,String pageno,String range){
		//{reqUrl}?authCode=${authCode}&pname=${pname}&idcardNo=${cardNo}&pageno=${pageno}&range=${range}&dataType=${datatype}
		String params = "authCode="+authCode+"&pname="+pname+"&idcardNo="+cardNo+
				"&dataType="+dataType;
		if(!StringUtil.isEmpty(pageno)){
			params = params+"&pageno="+pageno;
		}else{
			params = params+"&pageno=1";
		}
		if(!StringUtil.isEmpty(range)){
			params = params+"&range="+range;
		}else{
			params = params+"&range=10";
		}
		return params;
	}
}
