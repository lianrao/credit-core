/**   
 * @Description: 连接聚信立获取支持公积金的省市县地区信息
 * @author xiaobin.hou  
 * @date 2016年5月24日 下午6:41:15 
 * @version V1.0   
 */
package com.wanda.credit.ds.client.juxinli.requestor.housefund;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.ds.client.juxinli.BasicJuXinLiDataSourceRequestor;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFundCitysService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlHouseFund_allCities")
public class JXLHourseFundAllCitiesRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {

	private final static Logger logger = LoggerFactory
			.getLogger(JXLHourseFundAllCitiesRequestor.class);
	
	private final static String HOUSE_FUND_SUCCESS_MSG = "获取支持的省市县地区成功";
	private final static String SUPPORT_DISTRICT_SIZE_FLAG = "support_province_num";
	@Autowired
	private IJXLHouseFundCitysService jxlHouseFundCitysService;

	private String allCitiesUrl;
	private int timeOut;

	public Map<String, Object> request(String trade_id, DataSource ds) {

		long startTime = System.currentTimeMillis();
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		// 组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		if (!StringUtil.isEmpty(ds.getId())) {
			logObj.setDs_id(ds.getId());
		}else{
			logObj.setDs_id("ds_jxlHouseFund_allCities");
		}

		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(allCitiesUrl);
		logObj.setIncache("0");

		logger.info("{} 连接聚信立获取公积金支持的省市区信息开始 {}", prefix, new Date());
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			logger.info("{} 连接聚信立获取公积金支持的省市县地区信息", prefix);

			long postStart = System.currentTimeMillis();
			JsonObject allCitiesJsonObj = getJsonResponse(allCitiesUrl, timeOut * 1000 ,prefix);

			logger.info("{} 连接聚信立获取公积金支持的省市县地区信息耗时 {}", prefix,
					System.currentTimeMillis() - postStart);

			if (allCitiesJsonObj == null) {
				logger.error("{} 聚信立返回信息为空", prefix);
				throw new Exception(JXLConst.RES_NULL);
			}

			JsonElement successEle = allCitiesJsonObj.get("success");

			if (successEle == null) {
				logger.info("{} 聚信立返回结果中success节点为空", prefix);
				logger.info("{} http请求聚信立返回结果为 {}", prefix,
						allCitiesJsonObj.getAsString());
				throw new Exception(JXLConst.RES_SUC_NULL);
			}

			if (successEle.getAsBoolean()) {
				logger.info("{} 调用聚信立获取省市县地区信息接口成功" , prefix);
			}else{
				logger.info("{} 调用聚信立获取省市县地区信息接口失败" , prefix);
				logger.info("{} http请求聚信立返回结果为 {}", prefix,
						allCitiesJsonObj.getAsString());
				throw new Exception(JXLConst.RES_SUC_NOT_TRUE);
			}
			JsonElement dataEle = allCitiesJsonObj.get("data");
			
			if (dataEle == null) {
				logger.info("{} 聚信立返回data节点内容为空" , prefix);
				logger.info("{} http请求聚信立返回结果为 {}", prefix,
						allCitiesJsonObj.getAsString());
				throw new Exception(JXLConst.RES_DATA_NULL);
			}
			
			//交易日志信息数据
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			logObj.setState_msg((String) DataSourceLogEngineUtil.ERRMAP
					.get(DataSourceLogEngineUtil.TRADE_STATE_SUCC));
			resource_tag = Conts.TAG_TST_SUCCESS;
			JsonArray dataJsonArr = dataEle.getAsJsonArray();
			List<Map<String, String>> allCityDataList = parseData(dataJsonArr,new ArrayList<Map<String, String>>(),prefix);
			
			logger.info("{} 清除旧的数据保存新的支持的城市信息" , prefix);
			jxlHouseFundCitysService.addCityDataList(allCityDataList);
			logger.info("{} 清除旧的数据保存新的支持的城市信息成功" , prefix);
			TreeMap<String, Object> retData = new TreeMap<String, Object>();
			if (allCityDataList == null || allCityDataList.size() < 1) {				
				logger.info("{} 目前聚信立公积金没有支持的城市信息" , prefix);
				retData.put(SUPPORT_DISTRICT_SIZE_FLAG, 0);		
			}else{
				retData = jxlHouseFundCitysService.queryCityDataAndOutput();
				retData.put(SUPPORT_DISTRICT_SIZE_FLAG, retData.keySet().size());	
				logger.info("{} 聚信立公积金支持的城市个数为 {} " , prefix , allCityDataList.size());
			}			
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, HOUSE_FUND_SUCCESS_MSG);
			rets.put(Conts.KEY_RET_DATA, retData);
			
		} catch (Exception e) {
			String excepMsg = e.getMessage();

			if ((e instanceof ConnectTimeoutException)
					|| (e instanceof SocketTimeoutException)) {
				logger.error("{} 连接聚信立获取支持的省市县地区超时" + e.getMessage());
				// 交易日志信息数据
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
			} else {
				// 交易日志信息数据
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("交易失败");
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "获取支持的省市县地区失败");
			}
		}
		
		/**记录响应状态信息*/
		try{
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		}catch(Exception e){
			logger.error("{} 日志表数据保存异常 {}" , prefix , e.getMessage());
		}
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 聚信立-公积金提交采集请求总共耗时时间为（ms） {}", prefix, tradeTime);
		return rets;
	}

	public Map<String, Object> valid(String trade_id, DataSource ds) {

		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;

		logger.info("{} 没有必要参数直接跳过", prefix);

		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_MSG, "数据源参数校验通过!");

		return rets;
	}

	public List<Map<String, String>> parseData(JsonArray jsonArr,
			List<Map<String, String>> mapList ,String prefix) {

		Iterator<JsonElement> iterator = jsonArr.iterator();

		while (iterator.hasNext()) {
			JsonElement next = iterator.next();
			if (next.isJsonObject()) {
				JsonObject asJsonObject = next.getAsJsonObject();
				Set<Entry<String, JsonElement>> entrySet = asJsonObject
						.entrySet();
				Map<String, String> cityMap = new HashMap<String, String>();
				for (Entry<String, JsonElement> entry : entrySet) {

					String key = entry.getKey();
					JsonElement value = entry.getValue();
					if (value.isJsonObject()) {
						cityMap.put(key, value.getAsString());
					} else if (value.isJsonArray()) {
						parseData(value.getAsJsonArray(), mapList, prefix);
					} else {
						String strValue = value.getAsString();
						cityMap.put(key, strValue);
					}

				}
				mapList.add(cityMap);
			}
		}

		return mapList;
	}

	public void setAllCitiesUrl(String allCitiesUrl) {
		this.allCitiesUrl = allCitiesUrl;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
}
