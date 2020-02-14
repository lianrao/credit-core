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
import com.wanda.credit.ds.dao.iface.IShuiJingBankCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @Title: 外部数据源
 * @Description: 水晶球银行卡号风险数据
 * @author wenpeng.li@99bill.com
 * @date 2015年9月21日 下午09:37:36
 * @version V1.0
 */
@DataSourceClass(bindingDataSourceId="ds_shuijingqiu_bankcard")
public class SJQBankCardQiuDataSourceRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	@Autowired
	private IShuiJingBankCardService iShuiJingBankCardService;
	private final Logger logger = LoggerFactory
			.getLogger(SJQBankCardQiuDataSourceRequestor.class);
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
			String cardNoParam = ParamUtil.findValue(ds.getParams_in(), paramIds[0])
					.toString();
			String[] cardNos = cardNoParam.split(",");
			JSONObject json = new JSONObject();
			JSONObject data = new JSONObject();
			json.put("id", id);
			json.put("appKey", appKey);
			data.put("queryTp", "03");
			data.put("queryBadCardHolder", "1");
			data.put("queryFraudTrans", "1");
			data.put("queryCaseRelated", "1");
			data.put("queryOfflineBlack", "1");
			data.put("queryOnlineBlack", "1");
			data.put("queryOtherBlack", "1");
			int hitFraudTrans=0;
			int hitCaseRelated=0;
			int hitOfflineBlack=0;
			int hitOnlineBlack=0;
			int hitOtherBlack=0;
			int hitBadCardHolder=0;
			for (String cardNo : cardNos) {
				data.put("cardNo", cardNo);
				json.put("data", data);
				logger.info("{} 开始发送银行卡号至外部水晶球进行评估风险...", prefix);
				String result = RequestHelper.sendPostRequest(url, json.toString());
				if (!StringUtils.isEmpty(result)) {
					respMap = new ObjectMapper().readValue(result, Map.class);

					logger.info("{} resultstr:{}", new String[] { prefix, result });
					logger.info("resultstr:{}", result);
					if ("0000".equals(respMap.get("respCd"))) {
						iShuiJingBankCardService.save(trade_id, cardNo, result);
						if(respMap.containsKey("data")){
							Map<String, Object> retdatas  = (Map<String, Object>) respMap.get("data");
							hitFraudTrans+=Integer.parseInt(retdatas.get("hitFraudTrans").toString());
							hitCaseRelated+=Integer.parseInt(retdatas.get("hitCaseRelated").toString());
							hitOfflineBlack+=Integer.parseInt(retdatas.get("hitOfflineBlack").toString());
							hitOnlineBlack+=Integer.parseInt(retdatas.get("hitOnlineBlack").toString());
							hitOtherBlack+=Integer.parseInt(retdatas.get("hitOtherBlack").toString());
							hitBadCardHolder+=Integer.parseInt(retdatas.get("hitBadCardHolder").toString());
						}else{
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS,
									CRSStatusEnum.STATUS_FAILED_DS_SJQ2_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, "外部水晶球个人银行卡号欺诈数据为空!");
							logger.warn("外部水晶球个人银行卡号欺诈数据为空!");
						}
					} else {
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_DS_SJQ2_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "外部水晶球个人银行卡号欺诈数据返回异常! 异常码："
								+ respMap.get("respCd").toString() + ",异常信息:"
								+ respMap.get("msg").toString());
						logger.warn("{}外部水晶球个人银行卡号欺诈数据返回异常! 代码:{},错误消息:{}",
								new String[] { prefix,
										respMap.get("respCd").toString(),
										respMap.get("msg").toString() });
					}
				} else {
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_SJQ2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "外部水晶球个人银行卡号欺诈数据为空!");
					logger.warn("外部水晶球个人银行卡号欺诈数据为空!");
				}
			}
			respMap=new HashMap<String, Object>();
			respMap.put("hitFraudTrans", hitFraudTrans+"");
			respMap.put("hitCaseRelated", hitCaseRelated+"");
			respMap.put("hitOfflineBlack", hitOfflineBlack+"");
			respMap.put("hitOnlineBlack", hitOnlineBlack+"");
			respMap.put("hitOtherBlack", hitOtherBlack+"");
			respMap.put("hitBadCardHolder", hitBadCardHolder+"");
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, respMap);
			rets.put(Conts.KEY_RET_MSG, "交易成功!");
			logger.info("{} 外部水晶球个人银行卡号欺诈数据返回成功!", prefix);
		} catch (Exception ex) {
			ex.printStackTrace();
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG,
					"外部水晶球个人银行卡号欺诈数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 外部水晶球个人银行卡号欺诈数据源处理时异常：{}", prefix, ex.getMessage());
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
