package com.wanda.credit.ds.client.juxinli;

import java.util.Date;
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
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.service.IJXLEBusiMobileSubmitService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyNextDataSourcePojo;
import com.wanda.credit.ds.dao.domain.juxinli.trade.JXLMEbusiTradePojo;
import com.wanda.credit.ds.dao.iface.juxinli.trade.IJXLEBusiTradeService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 查询当前请求采集请求的状态
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlMobileEBusiSubmitStatus")
public class JXLMobileEBusiSubStatusRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {

	private final static Logger logger = LoggerFactory.getLogger(JXLMobileEBusiSubStatusRequestor.class);
	
	@Autowired
	private IJXLEBusiMobileSubmitService jxlEBusiMobileSubmitService;
//	@Autowired
//	protected IJXLEBusiTradeService jxlEBusiTradeService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long startTime = System.currentTimeMillis();
		Date nowDate = new Date();
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		JXLMEbusiTradePojo trade = new JXLMEbusiTradePojo();
		
		//获取请求参数
		String requestId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
		
		trade.setTrade_flag(JXLConst.TF_GET_SUB_RES);
		trade.setTrade_id(trade_id);
		trade.setRequestId(requestId);
		trade.setCrt_time(nowDate);
		trade.setUpd_time(nowDate);
	
		try{
			
			//通过requestId获取对应的Token
			ApplyBasicInfoPojo applyBasicInfo = requestId2Token(requestId);
			//判断该requestId是否存在
			if (applyBasicInfo == null) {
				logger.info("{} 聚信立提交采集请求传入的requestId不存在" + requestId ,prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT);
				rets.put(Conts.KEY_RET_MSG, "request_id不存在");
				trade.setRet_code(CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT.getRet_sub_code());
				jxlEBusiTradeService.add(trade);
				return rets;
			}
			
			ApplyNextDataSourcePojo queryNextDs = jxlEBusiMobileSubmitService.queryNextDs(requestId);
			
			if (queryNextDs == null) {
				
				logger.info("{} 该requestId已完成所有采集请求" ,prefix);
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "所有采集请求结束");
			}else {	
				
				Map<String, String> nextDsMap = new HashMap<String, String>();
				nextDsMap.put(JXLConst.WEBSITE_EN_NAME, queryNextDs.getWebsite());
				nextDsMap.put(JXLConst.WEBSITE_CN_NAME, queryNextDs.getName());
				nextDsMap.put(JXLConst.CATEGORY_EN_NAME, queryNextDs.getCategory());
				nextDsMap.put(JXLConst.CATEGORY_CN_NAME, queryNextDs.getCategory_name());
				retdata.put(JXLConst.EBUSI_NEXT_DATASOURCE, JSONObject.toJSONString(nextDsMap));
				
				logger.info("{} 该requestId未完成所有采集请求" + retdata ,prefix);
				
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_SUBMIT_ING);
				rets.put(Conts.KEY_RET_MSG, "未完成所有采集请求");
			}
		}catch (Exception e) {
			logger.error("{} 获取采集请求状态异常" + e.getMessage() ,prefix);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "获取所有采集请求状态异常");
		}
		
		try{
			if (rets.containsKey(Conts.KEY_RET_STATUS)) {
				CRSStatusEnum retstatus = CRSStatusEnum.valueOf(rets.get(Conts.KEY_RET_STATUS).toString());
				trade.setRet_code(retstatus.getRet_sub_code());
			}
			jxlEBusiTradeService.add(trade);
			
		}catch(Exception e){
			logger.error("{} 保存交易信息异常,trade_id为" + trade_id);
		}
		
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 提交request_id查询当前requestId对应采集请求状态总共耗时时间为（ms）" + tradeTime ,prefix);
		return rets;
	}

}
