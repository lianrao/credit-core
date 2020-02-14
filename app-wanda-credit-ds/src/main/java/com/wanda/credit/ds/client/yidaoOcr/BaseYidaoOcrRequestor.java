package com.wanda.credit.ds.client.yidaoOcr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.JSONUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_Idcard_result;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_License_result;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_bank_result;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_driver_result;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_veCard_result;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoBankService;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoDriverService;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoIdcardService;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoLicenseService;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoVeCardService;

@Service
public class BaseYidaoOcrRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(BaseYidaoOcrRequestor.class);
	@Autowired
	private FileEngine fileEngines;
	public Map<String, Object> dirverCard(String trade_id, String prefix,
			String resource_tag, Map<String, Object> respResult,
			Map<String, Object> rets, JSONObject respMap, String recotype,
			String fpath1, IExecutorFileService fileService,
			IYidaoDriverService yidaoDriverService) {

		Map<String, Object> resp_result = new HashMap<String, Object>();

		String fpath = null;

		Yidao_driver_result driver = new Yidao_driver_result();

		driver = JSONUtil.convertJson2Object((JSONObject) JSONUtil
				.getJsonValueByKey(respMap, "Result", false),
				Yidao_driver_result.class);

		driver.setReq_image(fpath1);
		String jsonString = com.alibaba.fastjson.JSONObject
				.toJSONString(respMap.get("Result"));

		Map parseObject = com.alibaba.fastjson.JSON.parseObject(jsonString,
				Map.class);

		String croppedImage = (String) parseObject.get("cropped_image");
		if (StringUtils.isNotEmpty(croppedImage)) {
			logger.info("{} 图片上传征信存储开始...", prefix);
			try {
				fpath = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, croppedImage,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath);
				driver.setCropped_image(fpath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		respResult.put("name", driver.getName());
		respResult.put("gender", driver.getGender());
		respResult.put("nation", driver.getNation());
		respResult.put("cardNo", driver.getCardno());
		respResult.put("address", driver.getAddress());
		respResult.put("birthdate", driver.getBirthdate());
		respResult.put("issuedate", driver.getIssuedate());
		respResult.put("driverclass", driver.getDriverclass());
		respResult.put("validdate", driver.getValiddate());
		respResult.put("cropped_image", croppedImage);
		resp_result.put("result", respResult);
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_DATA, resp_result);
		rets.put(Conts.KEY_RET_MSG, "查询驾驶证成功!");
		rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		logger.info("{} 易道OCR识别成功!", prefix);

		driver.setError(respMap.get("Error").toString());
		driver.setDetails(respMap.get("Details").toString());
		driver.setTrade_id(trade_id);
		driver.setReq_image(fpath);
		driver.setRecotype(recotype);
		try {
			yidaoDriverService.add(driver);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return rets;
	}

	public Map<String, Object> idCard(String trade_id, String prefix,
			String resource_tag, Map<String, Object> respResult,
			Map<String, Object> rets, JSONObject respMap, String recotype,
			String fpath1, IExecutorFileService fileService,
			IYidaoIdcardService yidaoIdcardService,
			IExecutorSecurityService synchExecutorService, String side,
			DataSourceLogVO logObj) {
		try {
			String fpath = null;
			Yidao_Idcard_result yidao = new Yidao_Idcard_result();
			String jsonString = com.alibaba.fastjson.JSONObject
					.toJSONString(respMap.get("Result"));
			Map parseObject = com.alibaba.fastjson.JSON.parseObject(jsonString,
					Map.class);
			String croppedImage = (String) parseObject.get("cropped_image");
			if (parseObject.size() >= 7) {
				if ("back".equals(side)) {
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_RIGHT_WRONG);
					rets.put(Conts.KEY_RET_MSG, "输入身份证对应面有误,请重新出入!");
					resource_tag = Conts.TAG_TST_FAIL;
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					return rets;

				}

				Map<String, Object> resp_result = new HashMap<String, Object>();
				yidao = JSONUtil.convertJson2Object((JSONObject) JSONUtil
						.getJsonValueByKey(respMap, "Result", false),
						Yidao_Idcard_result.class);
				yidao.setGender(parseObject.get("sex").toString());
				yidao.setNation(parseObject.get("people").toString());
				yidao.setBirthdate(parseObject.get("birthday").toString());
				yidao.setIdno(parseObject.get("id_number").toString());

				yidao.setReq_image(fpath1);
				respResult.put("cardNo", yidao.getIdno());
				yidao.setIdno(synchExecutorService.encrypt(yidao.getIdno()));
				respResult.put("name", yidao.getName());
				respResult.put("gender", yidao.getGender());
				respResult.put("nation", yidao.getNation());
				respResult.put("birthdate", yidao.getBirthdate());
				respResult.put("address", yidao.getAddress());
				respResult.put("side", "front");
				respResult.put("cropped_image", croppedImage);
				logger.info("{} 易道OCR识别成功!", prefix);
				if (StringUtils.isNotEmpty(croppedImage)) {
					logger.info("{} 图片上传征信存储开始...", prefix);
					fpath = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, croppedImage,trade_id);
					logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath);
					yidao.setCropped_image(fpath);
				}
				String hp = com.alibaba.fastjson.JSONObject
						.toJSONString(parseObject.get("head_portrait"));
				Map hpMap = com.alibaba.fastjson.JSON
						.parseObject(hp, Map.class);
				String head_image = (String) hpMap.get("image");
				respResult.put("head_image", head_image);
				resp_result.put("result", respResult);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, resp_result);
				rets.put(Conts.KEY_RET_MSG, "查询身份证成功!");
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });

				if (StringUtils.isNotEmpty(head_image)) {
					logger.info("{} 图片上传征信存储开始...", prefix);
					fpath = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, head_image,trade_id);
					logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath);
					yidao.setHead_portrait(fpath);
				}
			} else {
				if ("front".equals(side)) {
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_RIGHT_WRONG);
					rets.put(Conts.KEY_RET_MSG, "输入身份证对应面有误,请重新出入!");
					resource_tag = Conts.TAG_TST_FAIL;
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					return rets;
				}

				Map<String, Object> resp_result = new HashMap<String, Object>();

				yidao = JSONUtil.convertJson2Object((JSONObject) JSONUtil
						.getJsonValueByKey(respMap, "Result", false),
						Yidao_Idcard_result.class);
				yidao.setReq_image(fpath1);
				yidao.setIssuedby(parseObject.get("issue_authority").toString());
				yidao.setValidthru(parseObject.get("validity").toString());
				if (StringUtils.isNotEmpty(croppedImage)) {
					logger.info("{} 图片上传征信存储开始...", prefix);
//					fpath = fileService.upload(croppedImage, FileType.JPG,
//							FileArea.DS, trade_id);
					fpath = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, croppedImage,trade_id);
					logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath);
					yidao.setCropped_image(fpath);
				}
				respResult.put("issuedyb", yidao.getIssuedby());
				respResult.put("validthru", yidao.getValidthru());
				respResult.put("side", "back");
				respResult.put("cropped_image", croppedImage);
				resp_result.put("result", respResult);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, resp_result);
				rets.put(Conts.KEY_RET_MSG, "查询身份证成功!");
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				logger.info("{} 易道OCR识别成功!", prefix);
			}

			yidao.setError(respMap.get("Error").toString());
			yidao.setDetails(respMap.get("Details").toString());
			yidao.setTrade_id(trade_id);
			yidao.setReq_image(fpath1);
			yidao.setRecotype(recotype);
			yidaoIdcardService.add(yidao);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rets;
	}

	public Map<String, Object> bankCard(String trade_id, String prefix,
			String resource_tag, Map<String, Object> respResult,
			Map<String, Object> rets, JSONObject respMap, String recotype,
			String fpath1, IExecutorFileService fileService,
			IYidaoBankService yidaoBankService,
			IExecutorSecurityService synchExecutorService) {
		String fpath = null;
		Map<String, Object> resp_result = new HashMap<String, Object>();
		try {
			String jsonString = com.alibaba.fastjson.JSONObject
					.toJSONString(respMap.get("Result"));
			Map parseObject = com.alibaba.fastjson.JSON.parseObject(jsonString,
					Map.class);
			String croppedImage = (String) parseObject.get("cropped_image");

			Yidao_bank_result bank = new Yidao_bank_result();
			bank = JSONUtil.convertJson2Object((JSONObject) JSONUtil
					.getJsonValueByKey(respMap, "Result", false),
					Yidao_bank_result.class);
			bank.setReq_image(fpath1);
			rets.clear();
			respResult.put("cardId", bank.getCardno());

			bank.setCardno(synchExecutorService.encrypt(bank.getCardno()));
			if (StringUtils.isNotEmpty(croppedImage)) {
				logger.info("{} 图片上传征信存储开始...", prefix);
//				fpath = fileService.upload(croppedImage, FileType.JPG,
//						FileArea.DS, trade_id);
				fpath = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, croppedImage,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath);
				bank.setCropped_image(fpath);
			}
			respResult.put("bankName", bank.getBankname());
			respResult.put("cardName", bank.getCardname());
			respResult.put("cardType", bank.getCardtype());
			respResult.put("expmonth", bank.getExpmonth());
			respResult.put("expyear", bank.getExpyear());
			respResult.put("cropped_image", croppedImage);
			resp_result.put("result", respResult);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, resp_result);
			rets.put(Conts.KEY_RET_MSG, "查询银行卡成功!");
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
			logger.info("{} 易道OCR识别成功!", prefix);

			bank.setError(respMap.get("Error").toString());
			bank.setDetails(respMap.get("Details").toString());
			bank.setTrade_id(trade_id);
			bank.setReq_image(fpath1);
			bank.setRecotype(recotype);
			yidaoBankService.add(bank);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rets;
	}

	public Map<String, Object> businessLicense(String trade_id, String prefix,
			String resource_tag, Map<String, Object> respResult,
			Map<String, Object> rets, JSONObject respMap, String recotype,
			String fpath1, IExecutorFileService fileService,
			IYidaoLicenseService yidaoLicenseService) {
		Map<String, Object> resp_result = new HashMap<String, Object>();
		String fpath = null;
		try {
			String jsonString = com.alibaba.fastjson.JSONObject
					.toJSONString(respMap.get("Result"));
			Map parseObject = com.alibaba.fastjson.JSON.parseObject(jsonString,
					Map.class);
			JSONObject json = JSONObject.fromObject(jsonString);
			Set<String> set = json.keySet();
			String croppedImage = (String) parseObject.get("cropped_image");
			Yidao_License_result License = new Yidao_License_result();
			License.setAddress(parseObject.get("住所").toString());
			License.setStart_date(parseObject.get("成立日期").toString());
			License.setTerm_start(parseObject.get("营业期限").toString());
			License.setName(parseObject.get("名称").toString());
			License.setOper_name(parseObject.get("法定代表人").toString());
			License.setEcon_kind(parseObject.get("类型").toString());
			for (String key : set) {
				System.out.println("key==" + key);
				if (key.contains("统一社会信用代码")) {
					License.setCredit_no(parseObject.get(key).toString());
					respResult.put("credit_no", License.getCredit_no());
				}else if (key.contains("号")) {
					License.setRegist_code(parseObject.get(key).toString());
					respResult.put("regist_code", License.getRegist_code());
				}
			}
			if (parseObject.get("经营范围").toString().isEmpty()||(parseObject.get("经营范围"))!=null) {
				License.setScope(parseObject.get("经营范围").toString());
				respResult.put("scope", License.getScope());
			}
			if (!(null == parseObject.get("登记机关"))) {
				License.setRegist_org(parseObject.get("登记机关").toString());
				respResult.put("regist_org", License.getRegist_org());
			}
			if (!(null == parseObject.get("核准日期"))) {
				License.setCheck_date(parseObject.get("核准日期").toString());
				respResult.put("check_date", License.getCheck_date());
			}
			if (!(null == parseObject.get("经营状态"))) {
				License.setOper_state(parseObject.get("经营状态").toString());
				respResult.put("oper_state", License.getOper_state());
			}
			License.setRegist_capi(parseObject.get("注册资本").toString());
			License.setReq_image(fpath1);
			if (StringUtils.isNotEmpty(croppedImage)) {
				logger.info("{} 图片上传征信存储开始...", prefix);
//				fpath = fileService.upload(croppedImage, FileType.JPG,
//						FileArea.DS, trade_id);
				fpath = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, croppedImage,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath);
				License.setCropped_image(fpath);
			}
			respResult.put("address", License.getAddress());
			respResult.put("start_date", License.getStart_date());
			respResult.put("term_start", License.getTerm_start());
			respResult.put("name", License.getName());
			respResult.put("oper_name", License.getOper_name());
			respResult.put("econ_kind", License.getEcon_kind());
			respResult.put("cropped_image", croppedImage);
			respResult.put("regist_capi", License.getRegist_capi());
			resp_result.put("result", respResult);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, resp_result);
			rets.put(Conts.KEY_RET_MSG, "查询营业执照成功!");
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
			logger.info("{} 易道OCR识别成功!", prefix);

			License.setError(respMap.get("Error").toString());
			License.setDetails(respMap.get("Details").toString());
			License.setTrade_id(trade_id);
			License.setReq_image(fpath1);
			License.setRecotype(recotype);
			yidaoLicenseService.add(License);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rets;
	}
	public String HKMaCard(String trade_id, String basecontet, 
			Map<String, Object> rets,IPropertyEngine propertyEngine) throws Exception {
		String resource_tag = "";
		BaiduOcrRequestor baidu = new BaiduOcrRequestor(propertyEngine.readById("baidu_ocr_appid"),
				propertyEngine.readById("baidu_ocr_appkey"),
				propertyEngine.readById("baidu_ocr_appsercret"));
		HashMap<String, String> options = new HashMap<String, String>();
		com.alibaba.fastjson.JSONObject json = baidu.getOcrRsp(trade_id, propertyEngine.readById("baidu_ocr_url"), 
				basecontet, propertyEngine.readById("baidu_ocr_templateSign"), options);
		if(json == null){
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源请求失败!");
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
			return resource_tag;
		}
		String error_code = json.getString("error_code");
		if("0".equals(error_code)){
			HashMap<String, Object> retdata = new HashMap<String, Object>();
			HashMap<String, String> result = new HashMap<String, String>();
			com.alibaba.fastjson.JSONObject data = com.alibaba.fastjson.JSONObject.parseObject(json.getString("data"));
			com.alibaba.fastjson.JSONArray arr = com.alibaba.fastjson.JSONObject.parseArray(data.getString("ret"));
			for(Object tmp:arr){
				com.alibaba.fastjson.JSONObject tmp1 = (com.alibaba.fastjson.JSONObject) tmp;
				result.put(tmp1.getString("word_name"), tmp1.getString("word"));
			}
			retdata.put("result", result);
			resource_tag = Conts.TAG_TST_SUCCESS;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "查询港澳通行证成功!");
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		}else{
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.clear();
			rets.put(
					Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_YIDAO_RECOGNITION_ERRO);
			rets.put(Conts.KEY_RET_MSG,
					"证件识别失败,返回原因:" + json.getString("error_msg"));
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		}
		return resource_tag;
	}
}
