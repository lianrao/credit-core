/**   
* @Description: 4.2报告只返回运营商相关数据
* @author xiaobin.hou  
* @date 2016年3月18日 下午4:20:05 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.requestor.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.BasicJuXinLiDataSourceRequestor;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlReport_mobileData")
public class JXLReport_4_2OnlyMobileRequestor extends BasicJuXinLiDataSourceRequestor implements
		IDataSourceRequestor {
	
	
	private final  Logger logger = LoggerFactory.getLogger(JXLReport_4_2OnlyMobileRequestor.class);
	
	private final static String DSID = "ds_jxlReport_mobileData";
	
	@Resource(name="ds_jxlReportData_4_2")
	private IDataSourceRequestor newReportDSReq;

	@SuppressWarnings("unchecked")
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		List<Object> behaviorDelList = new ArrayList<Object>();
		behaviorDelList.add("person_addr_changed");
		behaviorDelList.add("lottery_buying");
		behaviorDelList.add("virtual_buying");
		behaviorDelList.add("person_ebusiness_info");
		behaviorDelList.add("ebusiness_info");
		behaviorDelList.add("dwell_used_time");
		
		logger.info("{} 获取报告数据并只返回报告中运营商数据 BEGIN，tradeId=" + trade_id ,prefix);
		long startTime = System.currentTimeMillis();
		
		String requestId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
		
		DataSource allReportDs = new DataSource();
		List<Param> params = new ArrayList<Param>();
		
		Param p1 = new Param();
		p1.setId(JXLConst.EBUSI_REQUEST_ID);
		p1.setValue(requestId);	
		params.add(p1);
		allReportDs.setParams_in(params);
		
		allReportDs.setId(DSID);
		
		
		Map<String, Object> rets = newReportDSReq.request(trade_id, allReportDs);
		
		if (CRSStatusEnum.STATUS_SUCCESS.equals(rets.get(Conts.KEY_RET_STATUS))
				&& rets.containsKey(Conts.KEY_RET_DATA)) {
			
			Map<String, Object> retData = (Map<String, Object>)rets.get(Conts.KEY_RET_DATA);
			//去除电商月消费节点
			retData.remove(JXLConst.REPORTDATA_EBUSI_EXPENSE);
			//去除快递信息节点
			retData.remove(JXLConst.REPORTDATA_DELIVER_ADDRESS);
			if (retData.containsKey(JXLConst.REPORTDATA_APPLICATION_CHECK)) {
				String applicationCheck = (String)retData.get(JXLConst.REPORTDATA_APPLICATION_CHECK);
				JSONArray appCheckJsonArr = JSONObject.parseArray(applicationCheck);
				for (int i = 0; i < appCheckJsonArr.size(); i++) {
					JSONObject checkPointJsonObj = appCheckJsonArr.getJSONObject(i);
					JSONObject jsonObject = checkPointJsonObj.getJSONObject(JXLConst.APPLICATION_CHECK_POINTS);
					if (jsonObject != null) {
						jsonObject.remove(JXLConst.APPLICATION_CHECK_BUSINESS);
					}
				}
				
				retData.put(JXLConst.REPORTDATA_APPLICATION_CHECK, JSONObject.toJSONString(appCheckJsonArr));
			}
			//去除
			if (retData.containsKey(JXLConst.REPORTDATA_BEHAVIOR_CHECK)) {
				String behaCheck = (String)retData.get(JXLConst.REPORTDATA_BEHAVIOR_CHECK);
				JSONArray jsonArr = new JSONArray();
				JSONArray behaviorJsonArr = JSONObject.parseArray(behaCheck);
				for (int i = 0; i < behaviorJsonArr.size(); i++) {
					JSONObject jsonObject = behaviorJsonArr.getJSONObject(i);
					if (!behaviorDelList.contains(jsonObject.get(JXLConst.BEHAVIOR_CHECK_POINT))) {
						jsonArr.add(jsonObject);
					}
				}
				
				retData.put(JXLConst.REPORTDATA_BEHAVIOR_CHECK, JSONObject.toJSONString(jsonArr));
			}
			//去除 total_count total_amount节点
			if (retData.containsKey(JXLConst.REPORTDATA_COLL_CONTACT)) {
				String contactColl = (String)retData.get(JXLConst.REPORTDATA_COLL_CONTACT);
				JSONArray contactCollJsonArr = JSONObject.parseArray(contactColl);
				for (int i = 0; i < contactCollJsonArr.size(); i++) {
					JSONObject jsonObject = contactCollJsonArr.getJSONObject(i);
					jsonObject.remove(JXLConst.CONTACT_COLL_TOTAL_AMOUNT);
					jsonObject.remove(JXLConst.CONTACT_COLL_TOTAL_COUNT);
					
					contactCollJsonArr.set(i, jsonObject);
				}
				
				retData.put(JXLConst.REPORTDATA_COLL_CONTACT, JSONObject.toJSONString(contactCollJsonArr));
			}
		}
		
		
		logger.info("{} 准备根据用户身份证-姓名-手机号获取报告数据 BEGIN，tradeId=" + trade_id ,prefix);
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 根据用户身份证号-姓名-手机号获取报告数据总共耗时时间为（ms）" + tradeTime ,prefix);
		return rets;
	}

	
	
	

}
