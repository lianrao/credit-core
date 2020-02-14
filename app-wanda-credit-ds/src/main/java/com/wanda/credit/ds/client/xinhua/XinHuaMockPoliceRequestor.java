package com.wanda.credit.ds.client.xinhua;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.IPUtils;
import com.wanda.credit.base.util.RandomUtils;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_xinhua_mock")
public class XinHuaMockPoliceRequestor extends BaseXHuaSourceRequestor implements IDataSourceRequestor {
	private Logger logger = LoggerFactory.getLogger(XinHuaMockPoliceRequestor.class);
	protected String CODE_EQUAL = "gajx_001";
	protected String CODE_NOEQUAL = "gajx_002";
	protected String CODE_NOEXIST = "gajx_003";

	@Autowired
	private IPropertyEngine propertyEngine;
	
	@Autowired
	private INciicCheckService nciicCheckService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		String request_url = propertyEngine.readById("ds_xinhua_facePhoto_url");
		int incache_days = Integer.valueOf(propertyEngine.readById("ds_police_incacheTime"));//公安数据缓存时间(天)
		float random_min = Float.valueOf(propertyEngine.readById("ds_police_test_random_min"));//公安数据缓存时间(天)
		float random_max = Float.valueOf(propertyEngine.readById("ds_police_test_random_max"));//公安数据缓存时间(天)
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();	
		Map<String, Object> reqparam = new HashMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(request_url);
		logObj.setBiz_code3(IPUtils.getLocalIP());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易成功");
		
		try{
			logger.info("{} 新华开始解析传入的参数" , prefix);
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString().toUpperCase();

			logger.info("{} 新华解析传入的参数成功" , prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			//加密敏感信息
			String encardNo = GladDESUtils.encrypt(cardNo);

			String cardNo_check = "一致";
			String name_check = "不一致";

			if(!nciicCheckService.inCachedCountJuHe(name, encardNo,incache_days)){
				logger.info("{} mock请求开始..." , prefix);
				Thread.sleep(Math.round(RandomUtils.getRandom(random_min, random_max)));
				logger.info("{} mock请求结束" , prefix);
				retdata.put("resultGmsfhm", cardNo_check);
				retdata.put("resultXm", name_check);
				retdata.put("xp_content", "");
				rets.clear();
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "采集成功!");
			}else{
				logger.info("{} mock请求开始..." , prefix);
				Thread.sleep(Math.round(RandomUtils.getRandom(random_min, random_max)));
				logger.info("{} mock请求结束" , prefix);
				retdata.put("resultGmsfhm", cardNo_check);
				retdata.put("resultXm", name_check);
				retdata.put("xp_content", "");
				rets.clear();
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "采集成功!");
			}
		}catch(Exception e){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
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
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		logger.info("{} 身份验证End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	}
}
