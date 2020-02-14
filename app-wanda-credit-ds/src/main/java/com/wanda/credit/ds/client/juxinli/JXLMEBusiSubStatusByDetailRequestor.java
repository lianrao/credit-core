/**   
* @Description: 根据身份证号，姓名，手机号获取采集请求结果状态
* @author xiaobin.hou  
* @date 2016年3月23日 下午2:20:07 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.trade.JXLMEbusiTradePojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLBasicInfoService;
import com.wanda.credit.ds.dao.iface.juxinli.trade.IJXLEBusiTradeService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlMEBusiSubmitStatusByDetail")
public class JXLMEBusiSubStatusByDetailRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor {
	
	private final Logger logger = LoggerFactory.getLogger(JXLMEBusiSubStatusByDetailRequestor.class);
	private String[] paramIds;
	
	@Autowired
	private IJXLBasicInfoService	jxlBasicInfoService;
	@Autowired
	protected IJXLEBusiTradeService jxlEBusiTradeService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;



	public Map<String, Object> request(String trade_id, DataSource ds) {

		logger.info("{} 准备根据用户身份证-姓名-手机号获取报告数据，tradeId=" + trade_id);
		boolean doPrint = JXLConst.LOG_ON.equals(propertyEngine.readById("sys_log_print_switch"));
		
		long startTime = System.currentTimeMillis();
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Date nowDate = new Date();
		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		JXLMEbusiTradePojo trade = new JXLMEbusiTradePojo();
		
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url("localhost");
		logObj.setIncache("0");
				
		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
		String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
		String mobileNo = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
		String period = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();
		
		
		String encCardNo = null;
		String encMobileNo = null;
		try {
			encCardNo = synchExecutorService.encrypt(cardNo);
			encMobileNo = synchExecutorService.encrypt(mobileNo);
		} catch (Exception e1) {
			logger.error("{} 敏感信息加密异常" , prefix);
			
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "获取采集状态失败");
			return rets;
		}
		
		
		/*保存请求参数*/
		Map<String,Object> paramIn = new HashMap<String,Object>();
		paramIn.put("name", name);
		paramIn.put("cardNo",encCardNo);
		paramIn.put("mobileNo",encMobileNo);
		DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn,logObj);
		
		if (doPrint) {
			StringBuffer paramBf = new StringBuffer();
			paramBf.append("name=").append(name).append("cardNo=").append(cardNo)
				.append("mobileNo=").append(mobileNo).append("period").append(period);
			logger.info("{} 传入参数为" + paramBf.toString() ,prefix);
			
		}
		
		trade.setCell_phone(encMobileNo);
		trade.setId_card_no(encCardNo);
		trade.setName(name);
		trade.setTrade_flag(JXLConst.TF_GET_SUB_RES_BY_DET);
		trade.setTrade_id(trade_id);
		trade.setCrt_time(nowDate);
		trade.setUpd_time(nowDate);
		
		
		try{
			if(!StringUtil.isPositiveInt(period)){
				logger.error("{} 运营商根据具体信息获取采集状态用户传入参数period错误，传入参数为[" + period + "]" ,prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "参数period应该为一个整数字符串");
				throw new Exception("参数period不是整数字符串");
			}
			if(period.length() > 7){
				logger.error("{} 运营商根据具体信息获取采集状态用户传入参数period长度过长" ,prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "参数period长度最大长度为7");
				throw new Exception("period长度大于7");
			}
		}catch(Exception e){
			
			logger.error("{} 参数period不合法" , prefix);
			try {
				trade.setRet_code(CRSStatusEnum.STATUS_FAILED.getRet_sub_code());
				jxlEBusiTradeService.add(trade);
				
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("交易失败");
				DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			} catch (Exception ex) {
				logger.error("{} 交易数据落地失败 " + ex.getMessage() ,prefix);
			}
			return rets;
		}
		
		int periodInt = Integer.parseInt(period);
		
		try {
			
			
			
			List<ApplyBasicInfoPojo> basicList = jxlBasicInfoService.queryCollectbyPeriod(name, encCardNo, encMobileNo, periodInt);
		
			if (basicList != null && basicList.size() > 0) {
				logger.info("{} 运营商根据详细信息获取采集成功的请求个数为" + basicList.size() ,prefix);
				
				ApplyBasicInfoPojo basicInfo = basicList.get(0);
				retdata.put(JXLConst.LAST_COLL_DATE, DateUtil.getSimpleDate(basicInfo.getCrt_time(), "yyyy-MM-dd"));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "所有采集请求结束");
				rets.put(Conts.KEY_RET_DATA, retdata);
				
				trade.setRequestId(basicInfo.getRequestId());
			}else{
				logger.info("{} 运营商根据详细信息获取采集成功个数为0" ,prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_SUBMIT_ING);
				rets.put(Conts.KEY_RET_MSG, "未完成所有采集请求");	
			}
			
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			logObj.setState_msg("交易成功");
			
		} catch (Exception e) {
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setState_msg("交易失败");
			logger.error("{} 获取采集状态异常" + e.getMessage(),prefix);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "获取采集状态失败");
		}
		
		
		try {
			if (rets.containsKey(Conts.KEY_RET_STATUS)) {
				CRSStatusEnum retstatus = CRSStatusEnum.valueOf(rets.get(Conts.KEY_RET_STATUS).toString());
				trade.setRet_code(retstatus.getRet_sub_code());
			}
			jxlEBusiTradeService.add(trade);
		} catch (Exception e) {
			logger.error("{} 交易数据落地失败 " + e.getMessage() ,prefix);
		}
		
		/**记录响应状态信息*/
		try{
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		}catch(Exception e){
			logger.error("{} 日志表数据保存异常 {}" , prefix , ExceptionUtil.getTrace(e));
		}
		
		long tradeTime = (System.currentTimeMillis() - startTime);
		logger.info("{} 通过身份证号，姓名，手机号获取采集请求状态总共耗时时间为（ms）" + tradeTime ,prefix);

		return rets;
	}
	

	public Map<String, Object> valid(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		try{
			rets = new HashMap<String, Object>();
			if(ds!=null && ds.getParams_in()!=null){
				for(String paramId : paramIds){
					if(null==ParamUtil.findValue(ds.getParams_in(),paramId)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
						rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
						return rets;
					}
				}
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "数据源参数校验通过!");
		}catch(Exception ex){
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ex.getMessage());
			ex.printStackTrace();
		}
		return rets;
	}


	public String[] getParamIds() {
		return paramIds;
	}


	public void setParamIds(String[] paramIds) {
		this.paramIds = paramIds;
	}
	

}
