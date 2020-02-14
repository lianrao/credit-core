package com.wanda.credit.ds.client.zhongshunew;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


/**新中数新接口：空壳公司核验
 * /verification/ent-person-relation
 * add by wj 20180816*/
@DataSourceClass(bindingDataSourceId="ds_zsCorpShell_check")
public class ZSShellCompanySourceRequestor extends BaseZS_NEW_DataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZSShellCompanySourceRequestor.class);

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}新中数空壳公司请求开始...",  prefix);
		
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setIncache("0");//不缓存
		logObj.setDs_id(ds.getId());
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		String tag = Conts.TAG_SYS_ERROR;
		
		try{
			rets = new HashMap<String, Object>();
			rets.put(Conts.KEY_RET_TAG, new String[]{tag});
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			
			String zs_api = propertyEngine.readById("ds_zs_cut_corpUrl");
			String zs_url = propertyEngine.readById("ds_zsnew_corpshell_check_url");
			String zs_uid = propertyEngine.readById("ds_zs_new_corpshell_uid");
			String zs_key = propertyEngine.readById("ds_zs_new_corpshell_key");
			String paramsStr = "?";
			String entname = "";
			if(ParamUtil.findValue(ds.getParams_in(), "entname")!=null){
				entname = ParamUtil.findValue(ds.getParams_in(), "entname").toString();
			}
			String creditcode = "";
			if(ParamUtil.findValue(ds.getParams_in(), "creditcode")!=null){
				creditcode = ParamUtil.findValue(ds.getParams_in(), "creditcode").toString();
			}
			String orgcodes = "";
			if(ParamUtil.findValue(ds.getParams_in(), "orgcodes")!=null){
				orgcodes = ParamUtil.findValue(ds.getParams_in(), "orgcodes").toString();
			}
			String regno = "";
			if(ParamUtil.findValue(ds.getParams_in(), "regno")!=null){
				regno = ParamUtil.findValue(ds.getParams_in(), "regno").toString();
			}
			if(StringUtil.isEmpty(entname) && StringUtil.isEmpty(creditcode) && StringUtil.isEmpty(orgcodes) && StringUtil.isEmpty(regno)){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数不正确:不可全为空");
				return rets;
			}
//			<value>entname,creditcode,orgcodes,regno</value>
//			<!--企业名称,统一社会信用代码,组织机构代码,注册号-->
			for (String paramId : paramIds) {
				Object paramValue = ParamUtil.findValue(ds.getParams_in(), paramId);
                if(!StringUtil.isEmpty(paramValue)){
                	paramsStr = paramsStr + paramId + "=" + URLEncoder.encode(paramValue.toString(),"utf-8") + "&";              
                }
                reqparam.put(paramId, String.valueOf(paramValue));
			}
			if(ds!=null && ds.getParams_in()!=null){
				for(String paramId : paramIds){
					if(nullableIds !=null && ArrayUtils.contains(nullableIds, paramId))continue;
					if(StringUtil.isEmpty(ParamUtil.findValue(ds.getParams_in(),paramId))){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
						rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
						return rets;
					}
				}
			}
			paramsStr = paramsStr.substring(0, paramsStr.length()-1);
	        
			logObj.setReq_url( zs_api + zs_url);

			String url = zs_api + zs_url + paramsStr;
			logger.info("{} 新中数开始请求-----", prefix);
			String res = callApi(url, prepareHeaders(zs_uid, zs_key, trade_id), trade_id);
			logger.info("{} 新中数结束请求-----", prefix);

	        JSONObject json = null;
	        try {
	        	json = JSONObject.parseObject(res);
	        	JSONObject result = new JSONObject();
	        	result.put("DESC", json.get("MSG"));
	        	result.put("DATA", json.getJSONObject("DATA"));
	        	rets.put(Conts.KEY_RET_DATA, result);
			} catch (Exception e) {
	        	rets.put(Conts.KEY_RET_DATA, res);
				rets.put(Conts.KEY_RET_MSG, "响应数据转json异常!");
				logger.error(prefix+" 新中数响应数据转json异常：{}",e);
				return rets;
			}
	        if("200".equals(json.getString("CODE"))){
	        	tag = Conts.TAG_TST_SUCCESS;
	        }else if("201".equals(json.getString("CODE"))){
	        	tag = Conts.TAG_TST_FAIL;
	        	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "识别目标为非在营状态，不支持识别");
				logger.warn("{} 识别目标为非在营状态，不支持识别",prefix);
				return rets;
	        }else if("202".equals(json.getString("CODE"))){
	        	tag = Conts.TAG_TST_FAIL;
	        	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_NOTFOUND_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "识别目标非公司，不支持识别");
				logger.warn("{} 识别目标为非在营状态，不支持识别",prefix);
				return rets;
	        }else if("400".equals(json.getString("CODE"))){
	        	tag = Conts.TAG_TST_FAIL;
	        	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "参数校验不通过!");
				logger.warn("{} 识别目标为非在营状态，不支持识别",prefix);
				return rets;
	        }	        
	        rets.put(Conts.KEY_RET_TAG, new String[]{tag});
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功");			
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);		
		} catch (Exception ex) {
			tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error(prefix+" 数据源处理时异常：{}",ex);
			
			/**如果是超时异常 记录超时信息*/
		    if(ExceptionUtil.isTimeoutException(ex)){	
		    	tag = Conts.TAG_SYS_TIMEOUT;
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);		    	
		    }
		    logObj.setState_msg(ex.getMessage());
		    rets.put(Conts.KEY_RET_TAG, new String[]{tag});
		}finally{
			//log入库
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(tag);
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
}
