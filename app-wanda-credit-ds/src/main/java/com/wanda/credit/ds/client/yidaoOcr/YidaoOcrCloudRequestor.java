package com.wanda.credit.ds.client.yidaoOcr;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImgCompress;
import com.wanda.credit.base.util.JsonFilter;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.tengxun.TXCloudIdcardOcr;
import com.wanda.credit.ds.client.tengxun.TengxunIdcardOcr;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;

@DataSourceClass(bindingDataSourceId = "ds_yidao_cloud_ocr")
public class YidaoOcrCloudRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(YidaoOcrCloudRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private FileEngine fileEngines;
	@Autowired
	private TengxunIdcardOcr txyunOcr;
	@Autowired
	private TXCloudIdcardOcr txyunCloudOcr;

	private static List<String> result_recotype = new ArrayList<String>();
	static {
		result_recotype.add("idCard");
		result_recotype.add("bankCard");
		result_recotype.add("driverCard");
		result_recotype.add("businessLicense");
		result_recotype.add("HKMaCard");
	}

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String card_txyun_switch = propertyEngine.readById("yidao_ocr_card_txyun");
		String yidao_bank_url = propertyEngine.readById("yidao_ocr_bank_url");
		String yidao_card_url = propertyEngine.readById("yidao_ocr_card_url");
		
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		
		double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_yidao_face_photo_comBase"));//压缩基数
		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_yidao_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
		int photo_limit = Integer.valueOf(propertyEngine.readById("ds_yidaoocr_auth_limit"));
		// 请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setDs_id(ds.getId());// log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("0");// 不缓存
		Map<String, Object> paramIn = new HashMap<String, Object>();

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
			reqparam.put("recotype", recotype);
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
				String file_full_path = fileEngines.getFullPathById(fpath1);
				ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
				String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,photo_limit);
				if(!StringUtil.isEmpty(comperss_rsp)){
					req_image = comperss_rsp;
				}
			}
			if("idCard".equals(recotype)){
				resource_tag = Conts.TAG_TST_FAIL;
				if("ds_tengxun_ocr".equals(card_txyun_switch)){
					ds.setId("ds_tengxun_ocr");
					rets = txyunOcr.request(trade_id, ds);
					return rets;
				}else if("ds_tengxun_cloud_ocr".equals(card_txyun_switch)){
					ds.setId("ds_tengxun_cloud_ocr");
					rets = txyunCloudOcr.request(trade_id, ds);
					return rets;
				}else{
					logger.info("{} 易道身份证ocr请求开始...", prefix);
					String result = RequestHelper.doPost(yidao_card_url, 
							paramOptions(req_image), false,time_out);
					logger.info("{} 易道身份证ocr请求完成", prefix);
					JSONObject respMap = JSONObject.fromObject(result);
					logger.info("{} 易道身份证ocr返回信息:{}", prefix,
							JSON.toJSONString(JsonFilter.getJsonKeys(com.alibaba.fastjson.JSONObject.parseObject(result).getJSONObject("result"), "crop_img,face_img")));
					logObj.setState_msg(respMap.getString("description"));
					logObj.setBiz_code1(respMap.getString("available_count"));
					logObj.setBiz_code2(respMap.getString("request_id"));
					if ("0".equals(respMap.getString("error_code"))) {
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
						resource_tag = Conts.TAG_TST_SUCCESS;
						try{							
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
							rets.put(Conts.KEY_RET_DATA, bankOutIdCardTag(respMap,side));
							rets.put(Conts.KEY_RET_MSG, "交易成功!");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						}catch(Exception e){
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS,
									CRSStatusEnum.STATUS_FAILED_DS_RIGHT_WRONG);
							rets.put(Conts.KEY_RET_MSG,
									"输入身份证对应面有误,请关注");
							rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
						}
						
					}else{
						logger.info("{} 易道身份证ocr识别异常:{}", prefix,respMap.getString("description"));
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
						rets.put(Conts.KEY_RET_MSG,
								"证件识别失败,返回原因:" + respMap.getString("description"));
						rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					}
				}			
			}else if("bankCard".equals(recotype)){
				logger.info("{} 易道ocr请求开始...", prefix);
				String result = RequestHelper.doPost(yidao_bank_url, 
						paramOptions(req_image), false,time_out);
				logger.info("{} 易道ocr请求完成", prefix);
				JSONObject respMap = JSONObject.fromObject(result);
				logObj.setState_msg(respMap.getString("description"));
				logObj.setBiz_code1(respMap.getString("available_count"));
				logObj.setBiz_code2(respMap.getString("request_id"));
				if ("0".equals(respMap.getString("error_code"))) {
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
					resource_tag = Conts.TAG_TST_SUCCESS;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA, bankOutTag(respMap));
					rets.put(Conts.KEY_RET_MSG, "交易成功!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else{
					logger.info("{} 易道银行卡ocr识别异常:{}", prefix,respMap.getString("description"));
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
					rets.put(Conts.KEY_RET_MSG,
							"证件识别失败,返回原因:" + respMap.getString("description"));
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				}
			}
			
		} catch (Exception ex) {
			resource_tag = Conts.TAG_TST_FAIL;
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
			rets.put(Conts.KEY_RET_MSG, "无合格图片");
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
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}

	public Map<String, String> paramOptions(String image) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("app_key", propertyEngine.readById("yidao_ocr_app_key"));
		params.put("app_secret", propertyEngine.readById("yidao_ocr_app_secret"));
		params.put("image_base64", image);

		return params;
	}
	public Map<String, Object> bankOutTag(JSONObject respMap){
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String, String> retdata = new HashMap<String, String>();
		JSONObject result = respMap.getJSONObject("result");
		JSONObject bank_name = result.getJSONObject("bank_name");
		JSONObject card_name = result.getJSONObject("card_name");
		JSONObject card_type = result.getJSONObject("card_type");
		JSONObject valid_thru = result.getJSONObject("valid_thru");
		JSONObject card_no = result.getJSONObject("card_no");
		retdata.put("bankname", bank_name.getString("words"));
		retdata.put("cardname", card_name.getString("words"));
		retdata.put("cardtype", card_type.getString("words"));
		retdata.put("cardno", card_no.getString("words"));
		retdata.put("validthru", valid_thru.getString("words"));
		resp.put("result", retdata);
		return resp;
	}
	public Map<String, Object> bankOutIdCardTag(JSONObject respMap,String side){
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String, String> retdata = new HashMap<String, String>();
		JSONObject result = respMap.getJSONObject("result");
		if("front".equals(side)){
			JSONObject name = result.getJSONObject("name");
			JSONObject gender = result.getJSONObject("gender");
			JSONObject nationality = result.getJSONObject("nationality");
			JSONObject idno = result.getJSONObject("idno");
			JSONObject address = result.getJSONObject("address");
			JSONObject birthdate = result.getJSONObject("birthdate");
			
			retdata.put("name", name.getString("words"));
			retdata.put("gender", gender.getString("words"));
			retdata.put("nation", nationality.getString("words"));
			retdata.put("cardNo", idno.getString("words"));
			retdata.put("address", address.getString("words"));
			retdata.put("birthdate", birthdate.getString("words"));
			
			retdata.put("cropped_image", result.getString("crop_img"));
			retdata.put("head_image", result.getString("face_img"));
			retdata.put("side", side);
			resp.put("result", retdata);
		}else{
			JSONObject issued = result.getJSONObject("issued");
			JSONObject valid = result.getJSONObject("valid");

			retdata.put("issuedby", issued.getString("words"));
			retdata.put("validthru", valid.getString("words"));
			retdata.put("cropped_image", result.getString("crop_img"));
			retdata.put("side", side);
			resp.put("result", retdata);
		}		
		return resp;
	}
}
