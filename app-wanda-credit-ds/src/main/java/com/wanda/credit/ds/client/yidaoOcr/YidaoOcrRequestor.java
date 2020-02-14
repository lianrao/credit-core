package com.wanda.credit.ds.client.yidaoOcr;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoBankService;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoDriverService;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoIdcardService;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoLicenseService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId = "ds_yidao_ocr")
public class YidaoOcrRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(YidaoOcrRequestor.class);
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IYidaoIdcardService yidaoIdcardService;
	@Autowired
	private IYidaoBankService yidaoBankService;
	@Autowired
	private IYidaoDriverService yidaoDriverService;
	@Autowired
	public IYidaoLicenseService yidaoLicenseService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	private BaseYidaoOcrRequestor bRequestor;
	@Autowired
	private FileEngine fileEngines;
	private static List<String> result_list = new ArrayList<String>();
	private static List<String> result_recotype = new ArrayList<String>();
	static {
		result_list.add("2");
		result_list.add("3");
		result_list.add("4");
		result_list.add("5");
		result_list.add("6");
		result_list.add("8");
		result_recotype.add("idCard");
		result_recotype.add("bankCard");
		result_recotype.add("driverCard");
		result_recotype.add("businessLicense");
		result_recotype.add("HKMaCard");
	}

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		RequestHelper rHelper = new RequestHelper();
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String yidao_url = propertyEngine.readById("yidao_ocr_url");
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		// 请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id("ds_yidao_ocr");// log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setReq_url(yidao_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("0");// 不缓存
		Map<String, Object> paramIn = new HashMap<String, Object>();
		Map<String, Object> respResult = new HashMap<String, Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			rets = new HashMap<String, Object>();
			String recotype = ParamUtil.findValue(ds.getParams_in(),
					paramIds[0]).toString(); // 证件类型
			String req_image = ParamUtil.findValue(ds.getParams_in(),
					paramIds[1]).toString(); // 传入图片
			String side = "";
			if (!(ParamUtil.findValue(ds.getParams_in(), paramIds[2])==null)) {
				 side =ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
			}
			// 记录入参到入参记录表
			paramIn.put("recotype", recotype);

			if (!(result_recotype.contains(recotype))) {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_YIDAO_RECOTYPE);
				rets.put(Conts.KEY_RET_MSG, "输入的证件类型有误,请重新出入!");
				resource_tag = Conts.TAG_TST_FAIL;
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				return rets;
			}

			String fpath1 = null;
			if (StringUtils.isNotEmpty(req_image)) {
				logger.info("{} 图片上传征信存储开始...", prefix);
				fpath1  = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, req_image,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath1);
			}
			if("HKMaCard".equals(recotype)){
				resource_tag = bRequestor.HKMaCard(trade_id,req_image,rets,propertyEngine);
			}else{
				Map<String, String> params = paramOptions(recotype, req_image);
				long startTime = System.currentTimeMillis();
				String result = rHelper.doPost(yidao_url, params, false,time_out);
				long endTime = System.currentTimeMillis();
				logger.info("{} 请求耗时:{}", prefix, (endTime - startTime) + " ms");
				JSONObject respMap = JSONObject.fromObject(result);

				if (Integer.parseInt(respMap.getString("Error")) > 0) {
					resource_tag = Conts.TAG_TST_FAIL;
					logObj.setBiz_code2(respMap.getString("Details"));
					logObj.setBiz_code1(respMap.getString("Error"));
					logger.error("{} 数据源处理时异常,返回业务码为:{},详细信息:{}", prefix,
							respMap.getString("Error"),
							respMap.getString("Details"));
					if (result_list.contains(respMap.getString("Error"))) {
						rets.put(Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG,
								"数据源处理时异常! 详细信息:" + respMap.getString("Details"));
						rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					} else if (respMap.getString("Error").equals("1")) {
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
						rets.clear();
						rets.put(
								Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_DS_YIDAO_RECOGNITION_ERRO);
						rets.put(Conts.KEY_RET_MSG,
								"证件识别失败,返回原因:" + respMap.get("Details").toString());
						rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					} else {
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
						rets.put(Conts.KEY_RET_MSG,
								"证件识别失败,返回原因:" + respMap.get("Details").toString());
						rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					}
				} else {
					logObj.setBiz_code1(respMap.getString("Error"));
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
					resource_tag = Conts.TAG_TST_SUCCESS;
					if ("idCard".equals(recotype)) {
						rets = bRequestor.idCard(trade_id, prefix, resource_tag,
								respResult, rets, respMap, recotype, fpath1,
								fileService, yidaoIdcardService,
								synchExecutorService, side, logObj);
					} else if ("bankCard".equals(recotype)) {
						rets = bRequestor
								.bankCard(trade_id, prefix, resource_tag,
										respResult, rets, respMap, recotype,
										fpath1, fileService, yidaoBankService,
										synchExecutorService);
					} else if ("driverCard".equals(recotype)) {
						rets = bRequestor.dirverCard(trade_id, prefix,
								resource_tag, respResult, rets, respMap, recotype,
								fpath1, fileService, yidaoDriverService);
					} else if ("businessLicense".equals(recotype)) {
						rets = bRequestor.businessLicense(trade_id, prefix,
								resource_tag, respResult, rets, respMap, recotype,
								fpath1, fileService, yidaoLicenseService);
					}
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			resource_tag = Conts.TAG_TST_FAIL;
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));

			/** 如果是超时异常 记录超时信息 */
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			if (ExceptionUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				resource_tag = Conts.TAG_SYS_TIMEOUT;
			}
			logObj.setState_msg(ex.getMessage());
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		} finally {
			// log入库
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}
		return rets;
	}

	public Map<String, String> paramOptions(String recotype, String image) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", propertyEngine.readById("yidao_ocr_username"));
		params.put("password", propertyEngine.readById("yidao_ocr_password"));
		params.put("recotype", recotype);
		params.put("image", image);
		params.put("b64", propertyEngine.readById("yidao_ocr_b64"));
		params.put("head_portrait",
				propertyEngine.readById("yidao_ocr_headPortrait"));
		params.put("crop_image",
				propertyEngine.readById("yidao_ocr_crop_image"));
		return params;
	}
}
