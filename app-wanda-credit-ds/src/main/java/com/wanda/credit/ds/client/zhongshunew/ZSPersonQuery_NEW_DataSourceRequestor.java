package com.wanda.credit.ds.client.zhongshunew;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.chinadaas.custom.ChinaDaasDataEncoder;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.iface.IZSNewOrderService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;

/**新中数新接口：// /person/pal：根据个人标识码查询人员信息  
 * add by wj 20180706*/
@DataSourceClass(bindingDataSourceId="ds_zsPersonQuery_new")
public class ZSPersonQuery_NEW_DataSourceRequestor extends BaseZS_NEW_DataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZSPersonQuery_NEW_DataSourceRequestor.class);
	@Autowired
	public IZSNewOrderService newOrderService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}新中数根据个人标识码查询人员信息请求开始...",  prefix);

		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setIncache("0");//不缓存
		logObj.setDs_id(ds.getId());
		
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{
			rets = new HashMap<String, Object>();
			String[] infos = propertyEngine.readByIds("ds_zs_new_p_pal",
					"ds_zs_new_url", "ds_zs_new_UID", "ds_zs_new_SECURITY_KEY",
					"ds_zs_new_encode_version", "ds_zs_new_encode_paramsIds",
					"ds_zs_new_notparamids");
			
			if(!StringUtil.areNotEmpty(infos)){
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_MSG, "模型参数有空值，请检查！");
				return rets;
			}
			String paramsStr = "?";
			for (String paramId : paramIds) {
				String paramValue = ParamUtil.findValue(ds.getParams_in(), paramId)!=null?ParamUtil.findValue(ds.getParams_in(), paramId).toString():"";
				if(!StringUtil.isEmpty(paramValue)){
					if("palgorithmid".equals(paramId)){
						reqparam.put(paramId, GladDESUtils.encrypt(paramValue));
					}else{
						reqparam.put(paramId, paramValue);
					}
				}
				if(!StringUtil.isEmpty(paramValue)
                		&& !infos[6].contains(paramId)){ //排除非数据源参数 如acct_id
					if(Arrays.asList(infos[5]).contains(paramId)){ //infos[5] palgorithmid 个人标识码
						paramValue= ChinaDaasDataEncoder.encode(paramValue,infos[4]); // infos[4] V1
					}
					paramsStr = paramsStr + paramId + "=" + URLEncoder.encode(paramValue,"utf-8") + "&";
				}			
			}
			paramsStr = paramsStr.substring(0, paramsStr.length()-1);
			
			logObj.setReq_url(infos[1] + infos[0]);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

			String url = infos[1] + infos[0] + paramsStr;
//			logger.info("{} 新中数请求URL及业务参数：{}", trade_id, url);
			logger.info("{} 新中数开始请求", trade_id);
			String res = callApi(url, prepareHeaders(infos[2], infos[3], trade_id), trade_id);
			logger.info("{} 新中数结束请求,响应数据:{}", trade_id, res);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
	        JSONObject json = JSONObject.fromObject(res);
	        String code = json.getString("CODE");
	        if("200".equals(code)){
	        	resource_tag = "zs_new_success";	        
		        rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_DATA, json.get("PERSON_INFO"));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "采集成功!");
				newOrderService.saveCorpInfo(trade_id, ds.getId(), json.getString("PERSON_INFO"), 
						GladDESUtils.encrypt(String.valueOf(ParamUtil.findValue(ds.getParams_in(), "palgorithmid"))), 
						String.valueOf(ParamUtil.findValue(ds.getParams_in(), "name")),"","");
	        }else{
	        	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_C_NOTFOUND_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "查询无记录");
				resource_tag = Conts.TAG_UNFOUND;
				logger.warn("{} 没有查询到任何工商证照信息",prefix);
	        }
		}catch(Exception ex){
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		return rets;
	}

}
