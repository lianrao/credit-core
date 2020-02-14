package com.wanda.credit.ds.client.shangtang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SignatureException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImgCompress;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;
/***
 * 商汤身份证ocr
 * @author liunan
 *
 */
@DataSourceClass(bindingDataSourceId = "ds_shangtang_ocr_card")
public class STGladCardOCRStalessRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(STGladCardOCRStalessRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private FileEngine fileEngines;
	
	private static List<String> result_recotype = new ArrayList<String>();
	static {
		result_recotype.add("idCard");
		result_recotype.add("bankCard");
	}
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 商汤身份证ocr调用开始...", prefix);
		Map<String, Object> rets = null;
		String shangtang_card_url = propertyEngine.readById("shangtang_ocr_card_url");
		String shangtang_bankcard_url = propertyEngine.readById("shangtang_ocr_bankcard_url");
		boolean impress_flag = "1".equals(propertyEngine.readById("shangtang_ocr_card_impress"));//是否进行压缩处理,1进行压缩
		double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_yidao_face_photo_comBase"));//压缩基数
		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_yidao_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
		int photo_limit = Integer.valueOf(propertyEngine.readById("ds_yidaoocr_auth_limit"));
		
		// 请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setDs_id(ds.getId());// log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setReq_url(shangtang_card_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("0");// 不缓存
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
			if (!(result_recotype.contains(recotype))) {
				logger.info("{} 输入类型有误:{}", prefix,recotype);
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_YIDAO_RECOTYPE);
				rets.put(Conts.KEY_RET_MSG, "输入的证件类型有误,请重新出入!");
				resource_tag = Conts.TAG_TST_FAIL;
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				return rets;
			}
			String fpath1 = null;
			String file_full_path = null;
			if (StringUtils.isNotEmpty(req_image)) {
				logger.info("{} 图片上传征信存储开始...", prefix);
				fpath1  = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, req_image,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath1);
				file_full_path = fileEngines.getFullPathById(fpath1);
				if(impress_flag){
					logger.info("{} 进行图片压缩的处理...", prefix);
					ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
					String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,photo_limit);
					if(!StringUtil.isEmpty(comperss_rsp)){
						req_image = comperss_rsp;
						String fpath2  = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, req_image,trade_id);
						logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath2);
						file_full_path = fileEngines.getFullPathById(fpath2);
					}
					logger.info("{} 进行图片压缩的处理完成", prefix);
				}
				
			}
			if("idCard".equals(recotype)){
				logger.info("{} 商汤身份证ocr调用http开始...", prefix);
				String result = httpSTClientPost(file_full_path,prefix,shangtang_card_url,recotype);
				logger.info("{} 商汤身份证ocr调用http返回信息:{}", prefix,result);
				JSONObject respMap = JSONObject.fromObject(result);
				logObj.setBiz_code1(respMap.getString("request_id"));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
				if("1000".equals(respMap.getString("code"))){
					resource_tag = Conts.TAG_TST_SUCCESS;
					if(!side.equals(respMap.getString("side"))){
						rets.put(Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_DS_RIGHT_WRONG);
						rets.put(Conts.KEY_RET_MSG,
								"输入身份证对应面有误,请关注");
						rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
						return rets;
					}		
					try{							
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_DATA, bankOutIdCardTag(respMap,side));
						rets.put(Conts.KEY_RET_MSG, "交易成功!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					}catch(Exception e){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
						rets.put(Conts.KEY_RET_MSG,
								"无合格图片,请关注");
						rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					}
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
					rets.put(Conts.KEY_RET_MSG, "无合格图片,请关注");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}
			}else{
				logger.info("{} 商汤银行卡ocr调用http开始...", prefix);
				String result = httpSTClientPost(file_full_path,prefix,shangtang_bankcard_url,recotype);
				logger.info("{} 商汤银行卡ocr调用http返回信息:{}", prefix,result);
				JSONObject respMap = JSONObject.fromObject(result);
				logObj.setBiz_code1(respMap.getString("request_id"));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
				if("1000".equals(respMap.getString("code"))){
					resource_tag = Conts.TAG_TST_SUCCESS;		
					try{							
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_DATA, bankOutTag(respMap.getJSONObject("info")));
						rets.put(Conts.KEY_RET_MSG, "交易成功!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					}catch(Exception e){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
						rets.put(Conts.KEY_RET_MSG,
								"无合格图片,请关注");
						rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					}
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
					rets.put(Conts.KEY_RET_MSG, "无合格图片,请关注");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}
			}
			
		} catch (Exception ex) {
			resource_tag = Conts.TAG_TST_FAIL;
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
			rets.put(Conts.KEY_RET_MSG,
					"无合格图片,请关注");
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
			executorDtoService.writeDsLog(trade_id,logObj,true);
		}
		return rets;
	}
	public Map<String, Object> bankOutIdCardTag(JSONObject respMap,String side){
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String, String> retdata = new HashMap<String, String>();
		JSONObject result = respMap.getJSONObject("info");
		if("front".equals(side)){		
			retdata.put("name", result.getString("name"));
			retdata.put("gender", result.getString("gender"));
			retdata.put("nation", result.getString("nation"));
			retdata.put("cardNo", result.getString("number"));
			retdata.put("address", result.getString("address"));
			retdata.put("birthdate", result.getString("year")+"年"+result.getString("month")+"月"+result.getString("day")+"日");
			
			retdata.put("cropped_image", "");
			retdata.put("head_image", "");
			retdata.put("side", side);
			resp.put("result", retdata);
		}else{
			retdata.put("issuedby", result.getString("authority"));
			retdata.put("validthru", result.getString("timelimit"));
			retdata.put("cropped_image", "");
			retdata.put("side", side);
			resp.put("result", retdata);
		}		
		return resp;
	}
	public Map<String, Object> bankOutTag(JSONObject respMap){
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String, String> retdata = new HashMap<String, String>();
		
		retdata.put("bankname", respMap.getString("bank_name"));
		retdata.put("cardno", respMap.getString("card_number"));
		retdata.put("validthru", "");
		retdata.put("cardtype", respMap.getString("card_type"));
		retdata.put("cardname", respMap.getString("card_name"));
		resp.put("result", retdata);
		return resp;
	}
	public String httpSTClientPost(String vopath, String prefix,String url,String type)
			throws ClientProtocolException, IOException, SignatureException {
		logger.info("{} 商汤身份证ocr调用开始...", prefix);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		FileBody fileVideo = new FileBody(new File(vopath));
		StringBody auto_rotate = new StringBody("true");
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("image_file", fileVideo);
		if("bankCard".equals(type)){
			entity.addPart("auto_rotate", auto_rotate);
		}
		post.setEntity(entity);
		post.setHeader("Authorization", GenerateString.genHeaderParam(
				propertyEngine.readById("shangtang_liveness_api_key"),
				propertyEngine.readById("shangtang_liveness_api_sec")));// 请将AUTHORIZATION替换为根据API_KEY和API_SECRET得到的签名认证串
		long startTime = System.currentTimeMillis();
		HttpResponse response = httpclient.execute(post);
		long endTime = System.currentTimeMillis();
		logger.info("{} 商汤身份证ocr请求耗时:{}", prefix, (endTime - startTime) + " ms");
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entitys = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entitys.getContent()));
			String line = reader.readLine();
			return line;
		} else {
			HttpEntity r_entity = response.getEntity();
			String responseString = EntityUtils.toString(r_entity);
			logger.info("{} 错误码是:{} 出错原因:{}", prefix, response.getStatusLine()
					.getStatusCode()
					+ "  "
					+ response.getStatusLine().getReasonPhrase(),
					responseString);
			return responseString;
		}
	}
}
