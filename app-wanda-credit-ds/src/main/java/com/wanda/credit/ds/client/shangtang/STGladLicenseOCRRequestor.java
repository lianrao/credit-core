package com.wanda.credit.ds.client.shangtang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SignatureException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
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
 * 商汤营业执照ocr
 * @author liunan
 *
 */
@DataSourceClass(bindingDataSourceId = "ds_shangtang_ocr_license")
public class STGladLicenseOCRRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(STGladLicenseOCRRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private FileEngine fileEngines;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 商汤营业执照ocr调用开始...", prefix);
		Map<String, Object> rets = null;
		String shangtang_url = propertyEngine.readById("shangtang_ocr_license_url");
		boolean impress_flag = "1".equals(propertyEngine.readById("shangtang_ocr_card_impress"));//是否进行压缩处理,1进行压缩
		double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_yidao_face_photo_comBase"));//压缩基数
		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_yidao_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
		int photo_limit = Integer.valueOf(propertyEngine.readById("ds_yidaoocr_auth_limit"));
		
		// 请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setDs_id(ds.getId());// log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setReq_url(shangtang_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("0");// 不缓存
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			rets = new HashMap<String, Object>();
			String req_image = ParamUtil.findValue(ds.getParams_in(),
					paramIds[0]).toString(); // 传入图片

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
			logger.info("{} 商汤营业执照ocr调用http开始...", prefix);
			String result = httpSTClientPost(file_full_path,prefix);
			logger.info("{} 商汤营业执照ocr调用http返回信息:{}", prefix,result);
			JSONObject respMap = JSONObject.fromObject(result);
			logObj.setBiz_code1(respMap.getString("request_id"));
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
			if("1000".equals(respMap.getString("code"))){
				resource_tag = Conts.TAG_TST_SUCCESS;		
				try{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA, respMap.get("data"));
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
	public String httpSTClientPost(String vopath, String prefix)
			throws ClientProtocolException, IOException, SignatureException {
		logger.info("{} 商汤营业执照ocr调用开始...", prefix);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(propertyEngine.readById("shangtang_ocr_license_url"));
		FileBody fileVideo = new FileBody(new File(vopath));

		MultipartEntity entity = new MultipartEntity();
		entity.addPart("license_image", fileVideo);

		post.setEntity(entity);
		post.setHeader("Authorization", GenerateString.genHeaderParam(
				propertyEngine.readById("shangtang_liveness_api_key"),
				propertyEngine.readById("shangtang_liveness_api_sec")));// 请将AUTHORIZATION替换为根据API_KEY和API_SECRET得到的签名认证串
		long startTime = System.currentTimeMillis();
		HttpResponse response = httpclient.execute(post);
		long endTime = System.currentTimeMillis();
		logger.info("{} 商汤营业执照ocr请求耗时:{}", prefix, (endTime - startTime) + " ms");
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
