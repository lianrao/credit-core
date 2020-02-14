package com.wanda.credit.ds.client.zhongshunew;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.chinadaas.custom.ChinaDaasDataEncoder;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.dao.domain.ZS_CorpBasicAuthen;
import com.wanda.credit.ds.dao.iface.IZSNewOrderService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**新中数新接口：verify/gsfour:根据企业名称、企业标识（注册号或统一社会信用代码）、人员姓名与个人标识码（脱敏后的身份证号）核验该人员是否在该企业中任法定代表人、股东或管理人员。 
 * add by wj 20180816*/
@DataSourceClass(bindingDataSourceId="ds_zsCorpBasicAuthen_new")
public class ZSCorpBasicAuthenDataSourceRequestor extends BaseZS_NEW_DataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZSCorpBasicAuthenDataSourceRequestor.class);
	
	@Autowired
	public IZSNewOrderService newOrderService;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}新中数企业四要素请求开始...",  prefix);
		
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, String> reqparam = new HashMap<String, String>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setIncache("0");//不缓存
		logObj.setDs_id(ds.getId());
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		String tag = Conts.TAG_SYS_ERROR;
		
		try{
			rets = new HashMap<String, Object>();
			rets.put(Conts.KEY_RET_TAG, new String[]{tag});
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			
			String[] infos = propertyEngine.readByIds("ds_zs_new_gsfour", 
					"ds_zs_new_url", "ds_zs_new_UID_2", "ds_zs_new_SECURITY_KEY_2",
					"ds_zs_new_encode_version", "ds_zs_new_encode_paramsIds",
					"ds_zs_new_notparamids");
			String paramsStr = "?";
			
//			<value>entName,entMark,name,cardNo</value>
//			<!--企业名称,企业标识,人员姓名,个人标识码-->
			for (String paramId : paramIds) {
				Object paramValue = ParamUtil.findValue(ds.getParams_in(), paramId);
                if(!StringUtil.isEmpty(paramValue)
                		&& !Arrays.asList(infos[6]).contains(paramId)){ //排除非数据源参数 如acct_id
                	if("cardNo".equals(paramId))
                		paramsStr = paramsStr + "cerno" + "=" + URLEncoder.encode(ChinaDaasDataEncoder.encode(paramValue.toString(),infos[4]),"utf-8") + "&";
                	else
                		paramsStr = paramsStr + paramId + "=" + URLEncoder.encode(paramValue.toString(),"utf-8") + "&";
                
                } else  if(StringUtil.isEmpty(paramValue)
                		&& "acct_id".equals(paramId)){ //acct_id
    				rets.put(Conts.KEY_RET_MSG, "acct_id不可为空!");
    				return rets;
            	}
                reqparam.put(paramId, String.valueOf(paramValue));
			}
			if(!StringUtil.areNotEmpty(infos)){
				rets.put(Conts.KEY_RET_MSG, "模型参数有空值，请检查！");
				return rets;
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
	        
			logObj.setReq_url( infos[1] + infos[0]);
			
			
			String url = infos[1] + infos[0] + paramsStr;
			logger.info("{} 新中数开始请求-----", prefix);
			String res = callApi(url, prepareHeaders(infos[2], infos[3], trade_id), trade_id);
			logger.info("{} 新中数结束请求-----", prefix);
			
	        //保存请求和响应数据到数据库
	        saveIntoDB(ds, trade_id, res);
			
	        JSONObject json = null;
	        try {
	        	json = JSONObject.fromObject(res);
	        	rets.put(Conts.KEY_RET_DATA, json.getJSONObject("result"));
			} catch (Exception e) {
	        	rets.put(Conts.KEY_RET_DATA, res);
				rets.put(Conts.KEY_RET_MSG, "响应数据转json异常!");
				logger.error(prefix+" 新中数响应数据转json异常：{}",e);
				return rets;
			}
	        if("200".equals(json.getString("code"))){
	        	tag = Conts.TAG_TST_SUCCESS;
	        } else {
	        	tag = Conts.TAG_TST_FAIL;
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
			DataSourceLogEngineUtil.writeParamIn(trade_id, JSONObject.fromObject(reqparam), logObj);
		}
		return rets;
	}
	
	private void saveIntoDB(DataSource ds, String tradeId, String res) throws ServiceException{
		ZS_CorpBasicAuthen perobj = new ZS_CorpBasicAuthen();
		perobj.setContent(res);
		perobj.setCreate_date(new Timestamp(System.currentTimeMillis()));
		perobj.setTrade_id(tradeId);
		perobj.setEntmark(String.valueOf(ParamUtil.findValue(ds.getParams_in(), "entMark")));
		perobj.setEntname(String.valueOf(ParamUtil.findValue(ds.getParams_in(), "entName")));
		perobj.setUpdate_date(new Timestamp(System.currentTimeMillis()));
		perobj.setName(String.valueOf(ParamUtil.findValue(ds.getParams_in(), "name")));
		perobj.setCardno(String.valueOf(CryptUtil.encrypt(String.valueOf(ParamUtil.findValue(ds.getParams_in(), "cardNo")))));;
		logger.info("{} 开始保存数据=====", tradeId);
		daoService.create(perobj);
		logger.info("{} 保存数据结束=====", tradeId);
	}
}
