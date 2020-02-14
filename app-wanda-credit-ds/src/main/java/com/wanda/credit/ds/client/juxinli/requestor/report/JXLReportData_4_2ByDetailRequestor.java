/**   
* @Description: 根据 
* @author xiaobin.hou  
* @date 2016年3月18日 下午4:20:05 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.requestor.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
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
@DataSourceClass(bindingDataSourceId="ds_jxlReportDataByDetail_4_2")
public class JXLReportData_4_2ByDetailRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor {
	
	
	private final  Logger logger = LoggerFactory.getLogger(JXLReportData_4_2ByDetailRequestor.class);
	private String[] paramIds;
	@Resource(name="ds_jxlReportData_4_2")
	private IDataSourceRequestor jxlReportData;
	@Autowired
	private IJXLBasicInfoService	jxlBasicInfoService;
	@Autowired
	private IJXLEBusiTradeService jxlEBusiTradeService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		boolean doPrint = JXLConst.LOG_ON.equals(propertyEngine.readById("sys_log_print_switch"));
		
		
		logger.info("{} 准备根据用户身份证-姓名-手机号获取报告数据 BEGIN，tradeId=" + trade_id ,prefix);
		long startTime = System.currentTimeMillis();
		Date nowDate = new Date();
		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		JXLMEbusiTradePojo trade = new JXLMEbusiTradePojo();
		
		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
		String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
		String mobileNo = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
		
		if (doPrint) {
			StringBuffer paramBf = new StringBuffer();
			paramBf.append("name=").append(name).append("cardNo=").append(cardNo)
				.append("mobileNo=").append(mobileNo);
			logger.info("{} 传入参数为" + paramBf.toString() ,prefix);
		}
		
		String encCardNo = null;
		String encMobileNo = null;
		try {
			encCardNo = synchExecutorService.encrypt(cardNo);
			encMobileNo = synchExecutorService.encrypt(mobileNo);
		} catch (Exception e1) {
			logger.error("{} 敏感信息加密异常 {}" , prefix , e1.getMessage());
			
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "获取采集状态失败");
			return rets;
		}		
		
		trade.setCell_phone(encMobileNo);
		trade.setId_card_no(encCardNo);
		trade.setName(name);
		trade.setTrade_flag(JXLConst.TF_GET_REPORT_BY_DET);
		trade.setTrade_id(trade_id);
		trade.setCrt_time(nowDate);
		trade.setUpd_time(nowDate);
		
		
		
		try{
			ApplyBasicInfoPojo basicInfo = new ApplyBasicInfoPojo();
			basicInfo.setName(name);
			basicInfo.setCell_phone(encMobileNo);
			basicInfo.setId_card_no(encCardNo);
			basicInfo.setRemark(JXLConst.MEBUSI_SUBMIT_All_FINISH);
			logger.info("{} 根据姓名，身份证号码，手机号，查询是否成功提交过采集请求" ,prefix);
			List<ApplyBasicInfoPojo> basicList = jxlBasicInfoService.queryAndOrderByCrt(basicInfo);
			
			if (basicList != null && basicList.size() > 0) {
				logger.info("{} 根据姓名，身份证号码，手机号查询到提交成功的采集请求总数为 " + basicList.size() ,prefix);
				
				String requestId = basicList.get(0).getRequestId();
				trade.setRequestId(requestId);
				
				logger.info("{} 姓名，身份证号，手机号对应的最新的请求序列号requestId为" + requestId ,prefix);
				
				if (StringUtil.areNotEmpty(requestId)) {
					DataSource reportDs = new DataSource();
					List<Param> params = new ArrayList<Param>();
					
					Param p1 = new Param();
					p1.setId(JXLConst.EBUSI_REQUEST_ID);
					p1.setValue(requestId);	
					params.add(p1);
					//开关参数：用户控制在JXLReportDataRequestor是否将交易信息保存到交易表中
					Param p2 = new Param();
					p2.setId(JXLConst.SWITCH_FLAG);
					p2.setValue(JXLConst.SWITCH_ON);	
					params.add(p2);
					
					reportDs.setParams_in(params);
					logger.info("{} 通过姓名，身份证号，手机号对应的requestId获取报告数据"  ,prefix);
					rets = jxlReportData.request(trade_id, reportDs); 
				}else{
					logger.info("{} 通过姓名，身份证号，手机号获取的交易序列号为空，当前没有对应数据" ,prefix);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_NO_REPORTDATA);
					rets.put(Conts.KEY_RET_MSG, "没有对应的报告数据，可能未提交采集请求或采集请求未完成");
				}
			}else{
				logger.info("{} 当前用户没有提交过提交过采集请求，或者提交采集请求未完成" ,prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_NO_REPORTDATA);
				rets.put(Conts.KEY_RET_MSG, "没有对应的报告数据，可能未提交采集请求或采集请求未完成");
			}
		}catch(Exception e){
			logger.error("{} 根据身份证，姓名，手机号获取报告数据异常 " + e.getMessage() ,prefix);
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "获取报告数据失败");
		}
		
		
		try{
			if (rets.containsKey(Conts.KEY_RET_STATUS)) {
				CRSStatusEnum retstatus = CRSStatusEnum.valueOf(rets.get(Conts.KEY_RET_STATUS).toString());
				trade.setRet_code(retstatus.getRet_sub_code());
			}
			jxlEBusiTradeService.add(trade);
			
		}catch(Exception e){
			logger.error("{} 交易信息落地失败，异常信息为" + e.getMessage() + "交易序列号为 " + trade_id ,prefix);
		}
		
		logger.info("{} 准备根据用户身份证-姓名-手机号获取报告数据 BEGIN，tradeId=" + trade_id ,prefix);
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 根据用户身份证号-姓名-手机号获取报告数据总共耗时时间为（ms）" + tradeTime ,prefix);
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

	public IDataSourceRequestor getJxlReportData() {
		return jxlReportData;
	}

	public void setJxlReportData(IDataSourceRequestor jxlReportData) {
		this.jxlReportData = jxlReportData;
	}

}
