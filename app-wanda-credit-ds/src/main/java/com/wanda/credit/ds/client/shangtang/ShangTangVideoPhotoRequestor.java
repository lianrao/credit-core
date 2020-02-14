package com.wanda.credit.ds.client.shangtang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

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

import com.bankcomm.gbicc.util.base64.BASE64Encoder;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImageUtils;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.shangtang.ShangTang_videophoto_result;
import com.wanda.credit.ds.dao.iface.shangtang.IShangTangVideoPhotoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId = "ds_shangtang_contrast")
public class ShangTangVideoPhotoRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(ShangTangVideoPhotoRequestor.class);
	@Autowired
	private IShangTangVideoPhotoService shangTangVideoPhotoService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	private static int thousand = 66;

	private static int tenThousand = 75;

	private static int oneHundredThousand = 80;

	private static List<String> result_list = new ArrayList<String>();
	static {
		result_list.add("1200");
		result_list.add("2003");
		result_list.add("2004");
		result_list.add("2005");
	}

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		String vopath = null;
		String impath = null;
		ShangTang_videophoto_result videophoto = new ShangTang_videophoto_result();
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String shangtang_url = propertyEngine.readById("shangtang_url");
		final boolean doPrint = "1".equals(propertyEngine.readById("sys_log_print_switch"));
		// 请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id("ds_shangtang_contrast");// log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setReq_url(shangtang_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("0");// 不缓存
		Map<String, Object> paramIn = new HashMap<String, Object>();
		Map<String, Object> respResult = new HashMap<String, Object>();

		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			rets = new HashMap<String, Object>();
			String req_video1 = ParamUtil.findValue(
					ds.getParams_in(), paramIds[0]).toString(); // 视频
			String req_image = ParamUtil.findValue(
					ds.getParams_in(), paramIds[1]).toString(); // 水印照
			String return_image = ParamUtil.findValue(ds.getParams_in(),
					paramIds[2]).toString(); // 是否返回照片
			String negative_rate = ParamUtil.findValue(ds.getParams_in(),
					paramIds[3]).toString(); // 误识率
			paramIn.put("return_image", return_image);
			paramIn.put("negative_rate", negative_rate);
			String imgPath = System.getProperty("user.dir")
					+ "\\src\\main\\resources\\files\\";
			vopath = imgPath + "" + trade_id + ".mp4";
			impath = imgPath + "" + trade_id + ".jpg";
			int negativeRate = 0;
			String req_video = shangTangVideoPhotoService.queryVideoFile(trade_id, req_video1);
			if ("1".equals(negative_rate)) {
				negativeRate = thousand;
			} else if ("2".equals(negative_rate)) {
				negativeRate = tenThousand;
			} else if ("3".equals(negative_rate)) {
				negativeRate = oneHundredThousand;
			} else {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_YITU_PARAMETER_ERRO);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败!");
				resource_tag = Conts.TAG_TST_FAIL;
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				logger.error("{} 人脸识别失败", prefix);
				return rets;
			}
			long size = req_video.length();
			logger.error("{} 视频大小:{} MB", prefix,size/(1024*1024));
			ImageUtils.decodeBase64ToImage(req_video, imgPath, trade_id
					+ ".mp4");
			ImageUtils.decodeBase64ToImage(req_image, imgPath, trade_id
					+ ".jpg");
//			if (StringUtils.isNotEmpty(req_video)) {
//				String videopath = null;
//				logger.info("{} 视频上传征信存储开始...", prefix);
//				videopath = fileService.upload(req_video, FileType.JPG,
//						FileArea.DS, trade_id);
//				logger.info("{} 视频上传征信存储成功,照片ID为：{}", prefix, videopath);
//				// 记录入参到入参记录表
//				paramIn.put("video_file", videopath);
//				videophoto.setReq_video(videopath);
//			}
//			if (StringUtils.isNotEmpty(req_image)) {
//				String pathimage = null;
//				logger.info("{} 图片上传征信存储开始...", prefix);
//				pathimage = fileService.upload(req_image, FileType.JPG,
//						FileArea.DS, trade_id);
//				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, pathimage);
//				paramIn.put("image_file", pathimage);
//				videophoto.setImage_file(pathimage);
//			}
			String result = httpClientPost(vopath, impath, prefix);
			if(doPrint)
				logger.info("{} 商汤返回信息:{}", prefix,result);
			JSONObject respMap = JSONObject.fromObject(result);
			logger.info("{} 商汤返回码:{}", prefix,respMap.getString("code"));
			
			if (Integer.parseInt(respMap.getString("code")) > 1000) {
				logObj.setBiz_code1(respMap.getString("message"));
				logObj.setBiz_code2(respMap.getString("code"));
				resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				if (result_list.contains(respMap.getString("code"))) {

					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_VIDEOFACE_ERRO);
					rets.put(Conts.KEY_RET_MSG,
							"入参不符合要求! 详细信息:" + respMap.getString("message"));
					rets.put("code", respMap.getString("code"));
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				} else if ("4007".equals(respMap.getString("code"))
						|| "4000".equals(respMap.getString("code"))) {
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG,
							"人脸识别失败! 详细信息:" + respMap.getString("message"));
					rets.put("code", respMap.getString("code"));
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				} else if ("1100".equals(respMap.getString("code"))
						|| "1101".equals(respMap.getString("code"))
						|| "1103".equals(respMap.getString("code"))) {
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_WANDA_FACE_FAIL);
					rets.put(Conts.KEY_RET_MSG,
							"人脸识别服务请求异常! 详细信息:" + respMap.getString("message"));
					rets.put("code", respMap.getString("code"));
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				} else if ("2008".equals(respMap.getString("code"))
						|| "2006".equals(respMap.getString("code"))
						|| "1002".equals(respMap.getString("code"))) {
					rets.put(
							Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_YITU_PHOTO_NOTANLASIS);
					rets.put(Conts.KEY_RET_MSG,
							"照片无法解析 ! 详细信息:" + respMap.getString("message"));
					rets.put("code", respMap.getString("code"));
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				} else {
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG,
							"数据源查询异常!");
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				}

			} else {
				resource_tag = Conts.TAG_TST_SUCCESS;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
				if ("false".equals(respMap.getString("passed").toString())) {
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_CHECK_FAILED);
					rets.put(Conts.KEY_RET_MSG, "活体检测未通过 !");
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					videophoto.setPassed(respMap.getString("passed"));
				} else {
					String base64_image = respMap.getString("base64_image");
					if (StringUtils.isNotEmpty(base64_image)) {
						logger.info("{} 图片上传征信存储开始...", prefix);
						base64_image = fileService.upload(base64_image,
								FileType.JPG, FileArea.DS, trade_id);
						logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix,
								base64_image);
					}
					videophoto.setPassed(respMap.getString("passed"));
				
					videophoto.setVerification_score(respMap.getString(
							"verification_score").toString());
					videophoto.setImage_id(respMap.getString("image_id"));
					videophoto.setImage_timestamp(respMap.getString(
							"image_timestamp").toString());
					videophoto.setBase64_image(base64_image);
					if ((Float.parseFloat(videophoto.getVerification_score()) * 100) >= negativeRate) {
						respResult.put("passed", "true");
					} else {
						respResult.put("passed", "false");
					}
					if ("0".equals(return_image)) {
						respResult.put("base64_image",
								respMap.get("base64_image"));
					} else {
						respResult.put("base64_image", "");
					}
					respResult.put("liveness_score", (Float.parseFloat(respMap
							.get("liveness_score").toString())) * 100);
					respResult.put("verification_score", (Float
							.parseFloat(respMap.get("verification_score")
									.toString())) * 100);
					respResult.put("image_id", respMap.get("image_id"));
					respResult.put("image_timestamp",
							respMap.get("image_timestamp"));
					respResult.put("code", respMap.getString("code"));
					respResult.put("request_id", respMap.get("request_id"));
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA, respResult);
					rets.put(Conts.KEY_RET_MSG, "静默活体检测水印照人脸比对成功!");
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					logger.info("{} 商汤静默活体检测水印照人脸比对成功!", prefix);
				
				}
				videophoto.setTrade_id(trade_id);
				videophoto.setCode(respMap.getString("code"));
				videophoto.setRequest_id(respMap.getString("request_id"));
				videophoto.setLiveness_score(respMap.getString(
						"liveness_score").toString());
				shangTangVideoPhotoService.add(videophoto);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			resource_tag = Conts.TAG_TST_FAIL;
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}", prefix, ex.getMessage());
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
			FileUtis.deleteFile(vopath);
			FileUtis.deleteFile(impath);
		}
		return rets;
	}

	public String httpClientPost(String vopath, String imagepath, String prefix)
			throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(propertyEngine.readById("shangtang_url"));
		FileBody fileVideo = new FileBody(new File(vopath));
		FileBody fileImage = new FileBody(new File(imagepath));
		StringBody returnImage = new StringBody(propertyEngine.readById(
				"shangtang_return_image").toString());
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("video_file", fileVideo);
		entity.addPart("image_file", fileImage);
		entity.addPart("return_image", returnImage);
		post.setEntity(entity);
		post.setHeader("Authorization", GenSignUtil.genauthorization(
				propertyEngine.readById("shangtang_api_key"),
				propertyEngine.readById("shangtang_api_sec")));// 请将AUTHORIZATION替换为根据API_KEY和API_SECRET得到的签名认证串
		long startTime = System.currentTimeMillis();
		HttpResponse response = httpclient.execute(post);
		long endTime = System.currentTimeMillis();
		logger.info("{} 请求耗时:{}", prefix, (endTime - startTime) + " ms");
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
