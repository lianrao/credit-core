package com.wanda.credit.ds.client.unionpay;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.IUnionPayPaintService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @Title: 外部数据源
 * @Description: 银联商户画像查询
 * @author wenpeng.li@99bill.com
 * @date 2015年9月21日 下午09:38:31
 * @version V1.0
 */
@DataSourceClass(bindingDataSourceId="ds_merchantUnionPayPaint")
public class MerchantUnionPayPaintDataSourceRequestor extends BaseDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(MerchantUnionPayPaintDataSourceRequestor.class);
	@Autowired
	private IUnionPayPaintService iUnionPayPaintService;
	private String url;
	private String account;
	private String privateKey;
	@Autowired
	private IPropertyEngine propertyEngine;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		StringBuffer paramsUrl = null;
		Map<String, String> params = null;
		Set<String> keySet = null;
		Iterator<String> iter = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setDs_id(ds.id);
		logObj.setReq_url(url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setIncache("0");
		String resource_tag = Conts.TAG_YL_01;// 不计费
		try {
			rets = new HashMap<String, Object>();
			String mid = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); // 商户MID
			String mname = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 商户名称
			String regNo = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 商户工商注册号
			String legalName = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString(); // 商户法人姓名
			paramsUrl = new StringBuffer();
			params = new TreeMap<String, String>();
			params = new TreeMap<String, String>();
			params.put("account", account);
			params.put("orderId", trade_id);
			// params.put("index", "all");
			// params.put("index",
			// "S5000,S5002,S5004,S5007,S5012,S5074,S5075,S5092,S5099,S5103,S5047,S5085");
			params.put("index", propertyEngine.readById("yl_m_qurey_ids"));
			params.put("mid", mid);
			params.put("mname", mname);
			params.put("regNo", regNo);
			params.put("legalName", legalName);
			keySet = params.keySet();
			iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				paramsUrl.append(key);
				paramsUrl.append(params.get(key));
			}
			paramsUrl.append(privateKey);
			params.put("sign", new MD5().get16MD5ofStr(paramsUrl.toString()).toUpperCase());
			logger.info("{} 银联商户画像查询开始...", prefix);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			String resp = RequestHelper.doGet(url, params, false);
			Map<String, Object> respMap = new ObjectMapper().readValue(resp, Map.class);// 转成map
			rets.clear();
			rets.put(Conts.KEY_RET_DATA, respMap);
			// modify by wangjing 2016-01-25
			params.put("bc_flag", "1");// 个人/企业标识：0-个人，1-企业
			iUnionPayPaintService.saveMerchant(trade_id, resp, params);
			logObj.setBiz_code1(String.valueOf(respMap.get("resCode")));
			if ("0000".equals(respMap.get("resCode"))) {
				logObj.setBiz_code2(String.valueOf(respMap.get("statCode")));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				if ("1001".equals(respMap.get("statCode")))
					resource_tag = Conts.TAG_YL_01;// 计费
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "银联商户画像查询成功!");
				logger.info("{} 银联商户画像查询成功!", prefix);
			} else {
				logger.error("{} 银联商户画像返回失败! 详细信息:{}", prefix, respMap.get("resMsg"));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PAINT_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "银联商户画像返回失败! 详细信息:" + respMap.get("resMsg"));
			}
		} catch (Exception ex) {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			// ex.printStackTrace();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			// logger.error("{} 数据源处理时异常：{}",prefix,ex.getMessage());
			logger.error(prefix + " 数据源处理时异常：：", ex);
		} finally {
			logObj.setTag(resource_tag);
			// ds日志：数据源入参调用概要信息记录
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			// Map<String, Object> reqParams =(Map)params;
			// ds日志：数据源入参信息记录
			DataSourceLogEngineUtil.writeParamIn(trade_id, (Map) params, logObj);
		}
		return rets;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
}
