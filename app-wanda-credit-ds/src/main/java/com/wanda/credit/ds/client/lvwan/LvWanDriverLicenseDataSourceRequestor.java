package com.wanda.credit.ds.client.lvwan;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.lvwan.gb.sdk.common.ConfigServer;
import com.lvwan.gb.sdk.common.HttpUtil;
import com.lvwan.gb.sdk.util.VerifyUtil;
import com.lvwan.gb.sdk.util.json.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.lvwan.Lw_driver_license;
import com.wanda.credit.ds.dao.iface.ILWDriverLicenseService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 绿湾驾驶证查询
 * @author ou.guohao
 * @date 2016-07-11 14:56:00
 * */
public class LvWanDriverLicenseDataSourceRequestor extends BaseLvWanDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(LvWanDriverLicenseDataSourceRequestor.class);

	@Autowired
	private ILWDriverLicenseService driverLicenseService;

	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@Autowired
	public IPropertyEngine propertyEngine;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(DynamicConfigLoader.get("sys.credit.client.lvwan.host"));
		logObj.setIncache("0");
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		// 默认交易失败
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		Map<String, Object> params = null;
		try {
			params = CommonUtil.sliceMapIfNotBlank(ParamUtil.convertParams(ds.getParams_in()), new String[] { "cardNo", "name", "birthday", "archviesNo", "carModels", "firstGetDocDate", "validday", "status" });
			String cardNo = params.get("cardNo").toString(); //身份证号码
			//至少驾驶证号码+一个对比项
			if (params.keySet().size() < 2) {
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
				return rets;
			}
			if (!StringUtil.isCardNo(cardNo)) {
				logger.info("{} 驾驶证号码不合法！", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("驾驶证号码不合法!");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
				return rets;
			}
			String url;
			if ("Y".equals(params.get("status"))) {//查询状态
				url = ConfigServer.getInstance().getStatusDriverUrl();
			} else {
				url = ConfigServer.getInstance().getVerifyDriverUrl();
			}

			String sRequestMethod = "POST";
			//提交申请对比请求
			logger.info("{} 申请比对驾驶证基本信息", new String[] { prefix });
			HashMap<String, Object> reqParam = (HashMap<String, Object>) buildReqParam(params);
			String responeContent = HttpUtil.send(url, reqParam, sRequestMethod);
			logger.info("{} 申请返回结果：{}",prefix, responeContent);
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, responeContent, new String[] { trade_id }));
			JSONObject jsonObj = new JSONObject(responeContent);
			if (jsonObj.getInt("error") != 0) {
				logger.info("{} 申请比对驾驶证信息失败:{}", new String[] { prefix, ERROR_CODE.get(jsonObj.get("error")) });
				logObj.setState_msg(ERROR_CODE.get(jsonObj.get("error")));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "驾驶证信息核查失败!");
				return rets;
			}
			String tid = VerifyUtil.getTid(responeContent);
			logger.info("{} 返回task_id：{}", new String[] { prefix, tid });

			//获取结果
			url = ConfigServer.getInstance().getResultDriverUrl();
			HashMap<String, Object> resultParams = new HashMap<String, Object>();
			resultParams.put("tid", tid);
			sRequestMethod = "GET";
			logger.info("{} 查询驾驶证任务的结果", new String[] { prefix });
			//休眠后再获取结果
			Thread.sleep(Long.valueOf(propertyEngine.readById("lw_result_waittime")));
			String sResult = getResult(url, resultParams, sRequestMethod);
			//结果写到日志文件 
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, sResult, new String[] { trade_id }));
			jsonObj = new JSONObject(sResult);
			if (jsonObj.getInt("error") != 0) {
				logger.info("{} 查询驾驶证任务的结果失败！");
				if (jsonObj.getInt("error") == 44026) {
					logger.info("{} 查询驾驶证任务的结果失败,error=44026,请求超时！");
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				}
				logObj.setState_msg(ERROR_CODE.get(jsonObj.get("error")));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "驾驶证信息核查失败!");
			}
			jsonObj = jsonObj.getJSONObject("data");
			if (jsonObj.getInt("result") != 1) {
				logger.info("{} 查询驾驶证任务的结果失败 reuslt:" + jsonObj.get("result"));
				logObj.setState_msg("查询驾驶证任务的结果失败 reuslt:" + jsonObj.get("result"));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "驾驶证信息核查失败!");
				return rets;
			}
			logger.info("{} 驾驶证查询数据源采集成功", new String[] { prefix });
			Lw_driver_license driverLicense = getDriverLicense(jsonObj, params);
			driverLicense.setTrade_id(trade_id);
			driverLicense.setCardNo(synchExecutorService.encrypt(cardNo));
			driverLicenseService.add(driverLicense);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			logObj.setBiz_code1(handleBizcode(driverLicense));
			rets.put(Conts.KEY_RET_DATA, getVisibleData(driverLicense));
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		} catch (Exception e) {
			/** 如果是超时异常 记录超时信息 */
			if (CommonUtil.isTimeoutException(e)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("驾驶证查询数据源处理时异常! 详细信息:" + e.getMessage());
			}
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
			logger.error(prefix + " 驾驶证查询数据源处理时异常", e);
		} finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, params, logObj);
		}
		return rets;
	}

	/**
	 * 构造请求参数
	 * 
	 * @date 2016年7月12日 下午5:14:48
	 * @author ou.guohao
	 * @param params
	 * @return
	 */
	private Map<String, Object> buildReqParam(Map<String, Object> params) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jszh", params.get("cardNo"));
		map.put("xm", params.get("name"));
		map.put("csrq", params.get("birthday"));
		map.put("dabh", params.get("archviesNo"));
		map.put("zjcx", params.get("carModels"));
		map.put("cclzrq", params.get("firstGetDocDate"));
		map.put("yxqz", params.get("validday"));
		map = CommonUtil.sliceMapIfNotBlank(map, new String[] { "jszh", "xm", "csrq", "dabh", "zjcx", "cclzrq", "yxqz" });
		return map;
	}

	/**
	 * 把JSONObject转Lw_driver_license
	 * 
	 * @date 2016年7月12日 下午7:39:21
	 * @author ou.guohao
	 * @param jsonObj
	 * @return
	 */
	private Lw_driver_license getDriverLicense(JSONObject jsonObject, Map<String, Object> params) {
		com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(jsonObject.toString());
		Lw_driver_license driverLicense = new Lw_driver_license();
		driverLicense.setCardNo_checkResult(String.valueOf(jsonObj.get("jszh")));
		if (params.get("name") != null) {
			driverLicense.setName_checkResult(String.valueOf(jsonObj.get("xm")));
		}
		if (params.get("birthday") != null) {
			driverLicense.setBirthday_checkResult(String.valueOf(jsonObj.get("csrq")));
		}
		if (params.get("archviesNo") != null) {
			driverLicense.setArchviesNo_checkResult(String.valueOf(jsonObj.get("dabh")));
		}
		if (params.get("carModels") != null) {
			driverLicense.setCarModels_checkResult(String.valueOf(jsonObj.get("zjcx")));
		}
		if (params.get("firstGetDocDate") != null) {
			driverLicense.setFirstGetDocDate_checkResult(String.valueOf(jsonObj.get("cclzrq")));
		}
		if (params.get("validday") != null) {
			driverLicense.setValidday_checkResult(String.valueOf(jsonObj.get("yxqz")));
		}
		if ("Y".equals(params.get("status"))) {
			driverLicense.setState_code(String.valueOf(jsonObj.get("zt**")));
			driverLicense.setState_name(getStatusName(String.valueOf(jsonObj.get("zt**"))));
		}

		driverLicense.setCardNo((String) params.get("cardNo"));
		driverLicense.setName((String) params.get("name"));
		driverLicense.setBirthday((String) params.get("birthday"));
		driverLicense.setArchviesNo((String) params.get("archviesNo"));
		driverLicense.setCarModels((String) params.get("carModels"));
		driverLicense.setFirstGetDocDate((String) params.get("firstGetDocDate"));
		driverLicense.setValidday((String) params.get("validday"));
		return driverLicense;
	}

	/**
	 * 根据驾驶证状态编号获取状态名称
	 * 
	 * @date 2016年7月13日 下午3:34:11
	 * @author ou.guohao
	 * @param statusCode
	 * @return
	 */
	private String getStatusName(String statusCode) {
		if (statusCode != null && !"-1".equals(statusCode)) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < statusCode.length(); i++) {
				sb.append(STATUS_CODE.get(String.valueOf(statusCode.charAt(i))));
				if (i < statusCode.length() - 1)
					sb.append(";");
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 设置log的bizcode
	 * 
	 * @date 2016年7月13日 下午7:18:29
	 * @author ou.guohao
	 * @param driverLicense
	 * @return
	 */
	private String handleBizcode(Lw_driver_license driverLicense) {
		if (null == driverLicense)
			return null;
		StringBuffer sb = new StringBuffer();
		sb.append(getBizcodeTag("cardNo", driverLicense.getCardNo_checkResult())).append(";");
		if (driverLicense.getName_checkResult() != null)
			sb.append(getBizcodeTag("name", driverLicense.getName_checkResult())).append(";");
		if (driverLicense.getBirthday_checkResult() != null)
			sb.append(getBizcodeTag("birthday", driverLicense.getBirthday_checkResult())).append(";");
		if (driverLicense.getArchviesNo_checkResult() != null)
			sb.append(getBizcodeTag("archviesNo", driverLicense.getArchviesNo_checkResult())).append(";");
		if (driverLicense.getCarModels_checkResult() != null)
			sb.append(getBizcodeTag("carModels", driverLicense.getCarModels_checkResult())).append(";");
		if (driverLicense.getFirstGetDocDate_checkResult() != null)
			sb.append(getBizcodeTag("firstGetDocDate", driverLicense.getFirstGetDocDate_checkResult())).append(";");
		if (driverLicense.getValidday_checkResult() != null)
			sb.append(getBizcodeTag("validday", driverLicense.getValidday_checkResult())).append(";");
		if (driverLicense.getState_code() != null)
			sb.append(getBizcodeTag("status", driverLicense.getState_code())).append(";");
		return sb.toString();
	}

	private String getBizcodeTag(String name, String tag) {
		if (name == null)
			return null;
		String[] tags = BIZ_CODE.get(name);
		if (tags.length == 3) {
			if ("1".equals(tag))
				return tags[0];
			if ("0".equals(tag))
				return tags[1];
			if ("-1".equals(tag)) {
				return tags[2];
			}
		} else if ("status".equals(name)) {
			if ("-1".equals(tag))
				return tags[1];
			else
				return tags[0];
		}
		return null;
	}

	private Map<String, Object> getVisibleData(Lw_driver_license driverLicense) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cardNo_checkResult", driverLicense.getCardNo_checkResult());
		map.put("name_checkResult", driverLicense.getName_checkResult());
		map.put("birthday_checkResult", driverLicense.getBirthday_checkResult());
		map.put("archviesNo_checkResult", driverLicense.getArchviesNo_checkResult());
		map.put("carModels_checkResult", driverLicense.getCarModels_checkResult());
		map.put("validday_checkResult", driverLicense.getValidday_checkResult());
		map.put("firstGetDocDate_checkResult", driverLicense.getFirstGetDocDate_checkResult());
		map.put("status_code", driverLicense.getState_code());
		map.put("status_name", driverLicense.getState_name());
		return map;
	}

	/** 驾驶证状态 **/
	private static final Map<String, String> STATUS_CODE = new HashMap<String, String>();
	private static final Map<String, String[]> BIZ_CODE = new HashMap<String, String[]>();
	static {
		STATUS_CODE.put("A", "正常");
		STATUS_CODE.put("B", "超分");
		STATUS_CODE.put("C", "转出");
		STATUS_CODE.put("D", "暂扣");
		STATUS_CODE.put("E", "撤销");
		STATUS_CODE.put("F", "吊销");
		STATUS_CODE.put("G", "注销");
		STATUS_CODE.put("H", "违法未处理");
		STATUS_CODE.put("I", "事故未处理");
		STATUS_CODE.put("J", "停止使用");
		STATUS_CODE.put("K", "协查");
		STATUS_CODE.put("L", "锁定");
		STATUS_CODE.put("M", "逾期未换证");
		STATUS_CODE.put("N", "延期换证");
		STATUS_CODE.put("P", "延期体检");
		STATUS_CODE.put("R", "逾期未体检");
		STATUS_CODE.put("S", "逾期未审");
		STATUS_CODE.put("U", "扣留");
		STATUS_CODE.put("Z", "其他");

		BIZ_CODE.put("cardNo", new String[] { "lic_no_match", "lic_no_mismatch", "lic_no_unfound" });
		BIZ_CODE.put("name", new String[] { "name_match", "name_mismatch", "name_unfound" });
		BIZ_CODE.put("birthday", new String[] { "bir_date_match", "bir_date_mismatch", "bir_date_unfound" });
		BIZ_CODE.put("archviesNo", new String[] { "arch_no_match", "arch_no_mismatch", "arch_no_unfound" });
		BIZ_CODE.put("carModels", new String[] { "car_model_match", "car_model_mismatch", "car_model_unfound" });
		BIZ_CODE.put("firstGetDocDate", new String[] { "fir_date_match", "fir_date_mismatch", "fir_date_unfound" });
		BIZ_CODE.put("validday", new String[] { "valid_date_match", "valid_date_mismatch", "valid_date_unfound" });
		BIZ_CODE.put("status", new String[] { "status_found", "status_unfound" });

	}

}
