package com.wanda.credit.ds.client.unionpay;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.IShuiJingMobileService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @Title: 外部数据源
 * @Description: 水晶球手机号风险数据
 * @author wenpeng.li@99bill.com
 * @date 2015年9月21日 下午09:38:31
 * @version V1.0
 */
@DataSourceClass(bindingDataSourceId="ds_shuijingqiu_mobile")
public class SJQMobileQiuDataSourceRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	@Autowired
	private IShuiJingMobileService iShuiJingMobileService;
	private final Logger logger = LoggerFactory
			.getLogger(SJQMobileQiuDataSourceRequestor.class);
	private String url;
	private String id;
	private String appKey;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		try {
			rets = new HashMap<String, Object>();
			Map<String, Object> respMap = null;
			String mobileParam = ParamUtil.findValue(ds.getParams_in(), paramIds[0])
					.toString();
			String[] mobiles = mobileParam.split(",");
			JSONObject json = new JSONObject();
			JSONObject data = new JSONObject();
			json.put("id", id);
			json.put("appKey", appKey);
			data.put("queryTp", "03");
			data.put("queryBadCardHolder", "1");
			data.put("queryOnlineBlack", "1");
			data.put("queryOtherBlack", "1");
			int hitBadCardHolder =0;
			int hitOnlineBlack =0;
			int hitOtherBlack =0;
			logger.info("{} 批量发送手机号进行评估风险...", prefix);
			for(String mobile: mobiles){
				data.put("mobile", mobile);
				json.put("data", data);
				String result = RequestHelper.sendPostRequest(url, json.toString());
				if (!StringUtils.isEmpty(result)) {
					respMap = new ObjectMapper().readValue(result, Map.class);// 转成map
					if ("0000".equals(respMap.get("respCd"))) {
						iShuiJingMobileService.save(trade_id, result, mobile);
						if(respMap.containsKey("data")){
							Map<String, Object> retdatas = (Map<String, Object>) respMap.get("data");
							hitBadCardHolder += Integer.parseInt( retdatas.get("hitBadCardHolder").toString());
							hitOnlineBlack += Integer.parseInt( retdatas.get("hitOnlineBlack").toString());
							hitOtherBlack += Integer.parseInt( retdatas.get("hitOtherBlack").toString());
						}else{
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_DS_SJQ1_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, "外部水晶球个人手机号风险返回数据为空!");
							logger.warn("外部水晶球个人手机号风险返回数据为空!");
							return rets;
						}
					}
				}else{
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_SJQ1_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "外部水晶球个人手机号风险返回数据为空!");
					logger.warn("外部水晶球个人手机号风险返回数据为空!");
					return rets;
				}
				
			}
			rets.clear();
			respMap = new HashMap<String, Object>();
			respMap.put("hitBadCardHolder", hitBadCardHolder+"");
			respMap.put("hitOnlineBlack", hitOnlineBlack+"");
			respMap.put("hitOtherBlack", hitOtherBlack+"");
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, respMap);
			rets.put(Conts.KEY_RET_MSG, "交易成功!");
			logger.info("{} 批量发送手机号进行评估风险成功!", prefix);
		} catch (Exception ex) {
			ex.printStackTrace();
			rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG,"外部水晶球个人手机号风险数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 外部水晶球个人手机号风险数据源处理时异常：{}", prefix, ex.getMessage());
		}
		return rets;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
}
