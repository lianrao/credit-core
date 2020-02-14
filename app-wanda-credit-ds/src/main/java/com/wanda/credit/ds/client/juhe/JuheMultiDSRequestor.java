/**   
* @Description: 聚合多头借贷数据源
* @author nan.liu
* @date 2018年2月22日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juhe;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
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
import com.wanda.credit.ds.client.juhe.bean.JuheMultiBean;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


@DataSourceClass(bindingDataSourceId="ds_juhe_multiple")
public class JuheMultiDSRequestor extends BaseJuheDSRequestor implements
		IDataSourceRequestor {	
	private Logger logger = LoggerFactory.getLogger(JuheMultiDSRequestor.class);
	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	public Map<String, Object> request(String trade_id, DataSource ds) {		
		String juhe_url = propertyEngine.readById("ds_juhe_police_multi_url").trim();
		String juhe_key = propertyEngine.readById("juhe_multi_token_client_id");
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		//初始化对象
		Map<String, Object> rets = initRets();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();		
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(juhe_url);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		
		try{
			logger.info("{} 开始解析传入的参数" , prefix);
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			logger.info("{} 解析传入的参数成功" , prefix);
			
			//加密敏感信息
			String enmobile = synchExecutorService.encrypt(mobile);			
			if(!(mobile.length() == 11 && StringUtil.isPositiveInt(mobile))){
				logger.info("{} 手机号码格式错误" , prefix);
				logObj.setState_msg("手机号码格式错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
				return rets;
			}	
			String token = getToken(trade_id,false);
			Map<String, String> req_params = new HashMap<>();
			req_params.put("mobile",mobile);
			req_params.put("user_id",juhe_key);
			req_params.put("origin", "0");
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json");
			headers.put("Authorization", "Bearer "+token);
			
			Map<String, Object> postRsp = RequestHelper.doPostRetFull(juhe_url, null,
					headers, req_params, null, null, true);
			JuheMultiBean multi = JSONObject.parseObject(
					String.valueOf(postRsp.get("res_body_str")),JuheMultiBean.class);
			retdata.put("result", multi);
			logObj.setState_msg("交易成功");
			rets.clear();
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception e){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
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
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			long dsLogStart = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
		}
		logger.info("{} 多头借贷End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;	
	}
}
