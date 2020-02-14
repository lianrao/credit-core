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

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.BasicJuXinLiDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseFormDetailPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseFormPojo;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFormService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_juxinliGetHouseForm")
public class JXLGetHouseFormDataSourceRequestor extends BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {

	private final static Logger logger = LoggerFactory.getLogger(JXLGetHouseFormDataSourceRequestor.class);
	private String httpsGetUrl;//发送Get请求的地址
	private int timeOut;

	@Autowired
	private IJXLHouseFormService houseFormService;

	public Map<String, Object> request(String trade_id, DataSource ds) {

		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setReq_url(httpsGetUrl);
		logObj.setIncache("0");
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			logger.info("{} 开始解析传入的参数", prefix);
			String regioncode = ParamUtil.findValue(ds.getParams_in(), "regioncode").toString();
			logger.info("{} 解析传入的参数成功", prefix);
			Map<String, Object> paramIn = new HashMap<String, Object>();
			paramIn.put("regioncode", regioncode);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			logger.info("{} 获取聚信立城市的公积金登陆方式及表单成功", prefix);
			JsonObject retJson = getJsonResponse(httpsGetUrl + regioncode, timeOut * 1000, prefix);
			if (retJson != null) {
				resource_tag = Conts.TAG_TST_FAIL;
				Gson gson = new Gson();
				Map<String, Object> retmap = gson.fromJson(retJson, Map.class);
				if (retmap.get("success") != null && "true".equals(retmap.get("success").toString())) {
					List<Map<String, Object>> data = (List<Map<String, Object>>) retmap.get("data");
					loadData(data, regioncode);
					retmap.clear();
					retmap.put("regioncode", regioncode);
					retmap.put("data", data);
					DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, retJson.toString(), new String[] { trade_id }));
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "获取公积金登陆方式及表单成功");
					rets.put(Conts.KEY_RET_DATA, retmap);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					resource_tag = Conts.TAG_TST_SUCCESS;
				} else {
					//聚信立返回错误信息
					rets.clear();
					//网络异常，建议结束采集流程
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					String resMsg = retJson.get("message").toString();
					rets.put(Conts.KEY_RET_MSG, resMsg);
					logObj.setState_msg(resMsg);
				}

			} else {
				logger.error("{} 聚信立返回结果为null", prefix);
				rets.clear();
				//TODO //向聚信立Post请求返回结果为null
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "暂时没有数据信息，请重试");
				logObj.setState_msg("聚信立返回结果为null");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("{} 获取聚信立城市的公积金登陆方式及表单请求异常", prefix, e.getMessage());
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "获取聚信立城市的公积金登陆方式及表单请求异常");
			if ((e instanceof ConnectTimeoutException) || (e instanceof SocketTimeoutException)) {
				logger.error("{} 连接获取聚信立城市的公积金登陆方式及表单请请求超时" + e.getMessage());
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
			} else {
				logger.error("{} 连接获取聚信立城市的公积金登陆方式及表单请返回失败! 详细信息:{}", e.getMessage(), prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("交易失败");
			}
		}

		logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		/** 记录响应状态信息 */
		try {
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
		} catch (Exception e) {
			logger.error("{} 日志表数据保存异常 {}", prefix, e.getMessage());
		}
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return rets;
	}

	private void loadData(List<Map<String, Object>> data, String regioCode) {
		HouseFormPojo houseForm;
		HouseFormDetailPojo houseFormDetail;

		List<HouseFormPojo> houseForms = new ArrayList<HouseFormPojo>();
		Date date = new Date();
		for (Map<String, Object> d : data) {
			List<Map<String, Object>> tabs = (List<Map<String, Object>>) d.get("tabs");
			for (Map<String, Object> tab : tabs) {
				houseForm = new HouseFormPojo();
				houseForm.setRegioCode(regioCode);
				houseForm.setWebsite(tab.get("website").toString());
				houseForm.setName(d.get("name").toString());
				houseForm.setLoginType(tab.get("type").toString());
				houseForm.setSortId(tab.get("sort").toString());
				houseForm.setDescript(tab.get("descript").toString());
				houseForm.setCreate_time(date);
				houseForm.setUpdate_time(date);
				removeElement(tab, new String[] { "id", "website" }, null);
				List<Map<String, Object>> field = (List<Map<String, Object>>) tab.get("field");
				for (Map<String, Object> f : field) {
					houseFormDetail = new HouseFormDetailPojo();
					houseFormDetail.setParameterName(f.get("parameter_name").toString());
					houseFormDetail.setParameterCode(f.get("parameter_code").toString());
					houseFormDetail.setParameterMessage(f.get("parameter_message").toString());
					houseFormDetail.setParameterErrMessage(f.get("parameter_err_message").toString());
					houseFormDetail.setParameterType(f.get("parameter_type").toString());
					houseFormDetail.setOrderby(String.valueOf(((Double) f.get("orderby")).intValue()));
					houseFormDetail.setStatus(String.valueOf(((Double) f.get("status")).intValue()));
					String grabType=String.valueOf(((Double) f.get("grab_type")).intValue());
					f.put("grab_type", grabType);
					houseFormDetail.setGrabType(grabType);
					houseFormDetail.setCategory(f.get("category").toString());
					houseFormDetail.setHouseForm(houseForm);
					houseFormDetail.setCreate_time(date);
					houseFormDetail.setUpdate_time(date);
					houseForm.getHouseFormDetails().add(houseFormDetail);
					removeElement(f, null, new String[] { "parameter_name", "parameter_code", "grab_type" });
				}
				houseForms.add(houseForm);
			}
			houseFormService.saveHouseFormBatch(houseForms);
		}
	}

	/**
	 * 批量移除map的元素
	 * 
	 * @date 2016年5月30日 上午11:04:53
	 * @author ou.guohao
	 * @param source
	 * @param removeKey 移除的key
	 * @param excludeKey 保留的 key
	 */
	private void removeElement(Map<String, Object> source, String[] removeKey, String[] excludeKey) {
		if (source != null && source instanceof Map) {
			if (removeKey != null) {
				for (String key : removeKey) {
					source.remove(key);
				}
			}

			if (excludeKey != null) {
				Iterator<Entry<String, Object>> iterator = source.entrySet().iterator();
				while (iterator.hasNext()) {
					boolean isRemove = true;
					Entry<String, Object> entry = iterator.next();
					for (String key : excludeKey) {
						if (key.equals(entry.getKey())) {
							isRemove = false;
							break;
						}
					}
					if (isRemove)
						iterator.remove();

				}
			}

		}
	}

	public String getHttpsGetUrl() {
		return httpsGetUrl;
	}

	public void setHttpsGetUrl(String httpsGetUrl) {
		this.httpsGetUrl = httpsGetUrl;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

}
