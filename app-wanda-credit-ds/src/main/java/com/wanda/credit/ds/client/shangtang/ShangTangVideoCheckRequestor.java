package com.wanda.credit.ds.client.shangtang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImageUtils;
import com.wanda.credit.base.util.RandomUtils;
import com.wanda.credit.base.util.ReadVideo;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.aijin.AiJinFaceDataSourceRequestor;
import com.wanda.credit.ds.client.policeAuthV2.PoliceFacePhotoV2Requestor;
import com.wanda.credit.ds.client.yuanjian.YuanJFaceSourceRequestor;
import com.wanda.credit.ds.client.zhengtong.ZTFace251DataSourceRequestor;
import com.wanda.credit.ds.client.zhongsheng.ZhongSFaceDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.shangtang.ShangTang_videophoto_result;
import com.wanda.credit.ds.dao.iface.shangtang.IShangTangVideoPhotoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;

@DataSourceClass(bindingDataSourceId = "ds_shangtang_stateless")
public class ShangTangVideoCheckRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(ShangTangVideoCheckRequestor.class);
	@Autowired
	private IShangTangVideoPhotoService shangTangVideoPhotoService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private YuanJFaceSourceRequestor yuanjianFace;
	@Autowired
	private ZTFace251DataSourceRequestor zhengtFaceService;
	@Autowired
	private ZhongSFaceDataSourceRequestor zhongsFaceService;
	@Autowired
	private AiJinFaceDataSourceRequestor aijinFace;
	@Autowired
	private PoliceFacePhotoV2Requestor police;
	@Autowired
	private STGladVideoStalessRequestor stLiveStalessService;
	private static int thousand = 66;

	private static int tenThousand = 75;

	private static int oneHundredThousand = 80;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		String dest_vopath = null;
		String vopath = null;
		String impath = null;
		ShangTang_videophoto_result videophoto = new ShangTang_videophoto_result();
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String shangtang_url = propertyEngine.readById("shangtang_stateless_url");
		String shangtang_path = propertyEngine.readById("shangtang_stateless_path");
		String route206 = propertyEngine.readById("ds_206_send_guozt");
		final boolean doPrint = "1".equals(propertyEngine.readById("sys_log_print_switch"));
		final boolean doSTRequest = "1".equals(propertyEngine.readById("shangtang_stateless_dosht"));//是否走商汤
		String doSTRequestlive = propertyEngine.readById("shangtang_stateless_doshlive");//是否判断活体
		int size_limit = Integer.valueOf(propertyEngine.readById("shangtang_stateless_doshlive_size_limit"));//视频大小压缩限制
		int size_times_limit = Integer.valueOf(propertyEngine.readById("shangtang_stateless_doshlive_size_times"));//视频大小压缩轮询次数
		int size_wait_times = Integer.valueOf(propertyEngine.readById("shangtang_stateless_doshlive_size_wait"));//视频大小压缩后等待时间
		String video_commond = propertyEngine.readById("shangtang_stateless_doshlive_video_commond");
		boolean video_impress_flag = false;
		// 请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id("ds_shangtang_stateless");// log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setReq_url(shangtang_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("1");// 不缓存
		Map<String, Object> paramIn = new HashMap<String, Object>();
		Map<String, Object> respResult = new HashMap<String, Object>();

		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			rets = new HashMap<String, Object>();
			String acct_id = "";
			if(ParamUtil.findValue(ds.getParams_in(), "acct_id")!=null){
				acct_id = ParamUtil.findValue(ds.getParams_in(), "acct_id").toString();
			}
			String req_video1 = ParamUtil.findValue(
					ds.getParams_in(), paramIds[0]).toString(); // 视频
//			String req_image = ParamUtil.findValue(
//					ds.getParams_in(), paramIds[1]).toString(); // 水印照
			String return_image = ParamUtil.findValue(ds.getParams_in(),
					paramIds[1]).toString(); // 是否返回照片
			String negative_rate = ParamUtil.findValue(ds.getParams_in(),
					paramIds[2]).toString(); // 误识率
			String name = ParamUtil.findValue(ds.getParams_in(),
					paramIds[3]).toString(); // 是否返回照片
			String cardNo = ParamUtil.findValue(ds.getParams_in(),
					paramIds[4]).toString(); // 误识率
			paramIn.put("return_image", return_image);
			paramIn.put("negative_rate", negative_rate);
			String imgPath = mkdirTodayPath(shangtang_path);
			vopath = imgPath + File.separator + trade_id + ".mp4";
			dest_vopath = imgPath + File.separator + trade_id + "dest.mp4";
			impath = imgPath + File.separator + trade_id;
			logger.info("{} 商汤静默视频imgPath:{}", prefix,imgPath);
			logger.info("{} 商汤静默视频vopath:{}", prefix,vopath);
			logger.info("{} 商汤静默视频impath:{}", prefix,impath);
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
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
			ImageUtils.decodeBase64ToImage(req_video, imgPath + File.separator, trade_id
					+ ".mp4");
			float video_size = ReadVideo.ReadVideoSize(new File(vopath))/1024/1024;
			logger.info("{} 视频大小:{} MB", prefix,video_size);
			if(video_size>=size_limit && isCheckSTLive(doSTRequestlive,acct_id)){
				logger.info("{} 视频大小超过2M，进行压缩", prefix);
				impressVideo(trade_id,video_commond,vopath,dest_vopath,size_times_limit,size_wait_times);
				vopath = dest_vopath;
				impath = imgPath + File.separator + trade_id+"dest";
				video_impress_flag = true;
			}
			if(isCheckSTLive(doSTRequestlive,acct_id)){
				logger.info("{} 进行商汤静默视频活体检测数据源调用...", prefix);
				DataSource ds_live = new DataSource();
				List<Param> params_in = new ArrayList<Param>();
				Param voapaths = new Param();
				voapaths.setId("vopath");
				voapaths.setValue(vopath);
				params_in.add(voapaths);
				ds_live.setParams_in(params_in);
				ds_live.setId("ds_shangtang_liveness");
				rets = stLiveStalessService.request(trade_id, ds_live);
				if(!isSuccess(rets)){
					logger.error("{} 商汤静默视频活体检测未通过", prefix);
					if(video_impress_flag){
						rets = stLiveStalessService.request(trade_id, ds_live);
						if(!isSuccess(rets)){
							logger.error("{} 商汤静默视频活体再次检测未通过", prefix);
							return rets;
						}
					}else{
						return rets;
					}
					
				}		
				rets.clear();
			}
			String result = "";
			if(doSTRequest){
				result = httpSTClientPost(vopath,name,cardNo, prefix);
			}else{
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				resource_tag = httpOtherClientPost(route206,vopath,impath,name,cardNo,trade_id,ds,rets,negativeRate,return_image);
				return rets;
			}
			if(doPrint)
				logger.info("{} 商汤返回信息:{}", prefix,result);
			JSONObject respMap = JSONObject.fromObject(result);
			logger.info("{} 商汤返回码:{}", prefix,respMap.getString("code"));
			
			if (Integer.parseInt(respMap.getString("code")) > 1000) {
//				logObj.setBiz_code1(respMap.getString("message"));
				logObj.setBiz_code2(respMap.getString("code"));
				resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				if ("3003".equals(respMap.getString("code"))) {
					rets.clear();
					resource_tag = Conts.TAG_UNMATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if ("3005".equals(respMap.getString("code"))) {
					rets.clear();
					resource_tag = Conts.TAG_MATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if ("1200".equals(respMap.getString("code")) || "3004".equals(respMap.getString("code"))) {
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_VIDEOFACE_ERRO);
					rets.put(Conts.KEY_RET_MSG,
							"入参不符合要求! ");
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
				if ("false".equals(respMap.getString("passed"))) {
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
					CRSStatusEnum.STATUS_FAILED_DS_SHANGT_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "视频拍摄不符合要求,请重新拍摄!");
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
//			FileUtis.deleteFile(vopath);
//			FileUtis.deleteFile(impath);
		}
		return rets;
	}

	public String httpSTClientPost(String vopath,String name1,String cardNo1, String prefix)
			throws ClientProtocolException, IOException {
		logger.info("{} 商汤调用开始...", prefix);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(propertyEngine.readById("shangtang_url"));
		FileBody fileVideo = new FileBody(new File(vopath));
		StringBody returnImage = new StringBody(propertyEngine.readById(
				"shangtang_return_image").toString());
		StringBody name = new StringBody(name1);
		StringBody cardNo = new StringBody(cardNo1);
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("video_file", fileVideo);
		entity.addPart("name", name);
        entity.addPart("idnumber", cardNo);
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
	public String httpOtherClientPost(String route206,String vopath, String imagepath,String name1,
			String cardNo1, String trade_id,DataSource ds,Map<String, Object> rets,int negativeRate,String return_image){
		logger.info("{} 其他渠道调用开始...", trade_id);
		Map<String, Object> retdatas = null;
		String resource_tag = Conts.TAG_FOUND;
		String commondtext = propertyEngine.readById("shangtang_stateless_commondtext");
		boolean eye_check = "0".equals(propertyEngine.readById("shangtang_stateless_eye_check"));
		int max_times = Integer.valueOf(propertyEngine.readById("shangtang_stateless_wait_times"));
		int turn_times = Integer.valueOf(propertyEngine.readById("shangtang_stateless_turn_times"));//轮询次数限制
		String pex = "0.jpg";
		
		try {  
			String commond = ReadVideo.getVideoParam(trade_id, vopath, imagepath+pex, commondtext);
			logger.info("{} 视频截取图片开始,执行命令:{}", trade_id,commond);
            Runtime runtime = Runtime.getRuntime();  
            runtime.exec(commond);//Runtime.exec(new String[] {"/bin/sh", "-c", command})
            logger.info("{} 视频截取图片完成", trade_id);
            int count1=1;
            int close_cnt = 0;
            String base_image = "";
			while(true){
				if(count1 > 0)
					logger.info("{} 轮询查询文件是否存在,轮询序号:{}",trade_id,count1);
				if(count1>=max_times){
					logger.info("{} 轮询查询文件是否存在主程序最大容忍次数已到,系统将自动停止！",trade_id);
					break;
				}
				if(count1%turn_times==0){
					logger.info("{} 轮询查询文件为{}的倍数,开始执行命令",trade_id,turn_times);
					pex = count1+".jpg";
					commond = ReadVideo.getVideoParam(trade_id, vopath, imagepath+pex, commondtext);
					logger.info("{} 视频截取图片开始,执行命令:{}", trade_id,commond);
					runtime.exec(commond);
					logger.info("{} 轮询查询文件为10的倍数,执行命令完成", trade_id);
				}
				File file=new File(imagepath+pex);
				if(file.exists()){
					logger.info("{} 图片截取成功:{}",trade_id,imagepath+pex);
					base_image = ImageUtils.encodeFileToBase64(imagepath+pex);
					String eyecheck = checkEyeOpen(propertyEngine.readById("shangtang_stateless_eyecheck_url"),
							base_image,trade_id);
					if(eyecheck.equals("eyeopen") || eye_check){
						break;						
					}else{
						close_cnt++;
						FileUtis.deleteFile(imagepath+pex);
					}
				}
				if(close_cnt>=3){
					resource_tag = Conts.TAG_SYS_ERROR;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_SHANGT_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "视频拍摄不符合要求,请重新拍摄!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return resource_tag;
	            }
				Thread.sleep(50);//模拟ds3处理需要时间
				count1++;
			}
            //query_image_content          
            Param image = new Param();
            image.setId("query_image_content");
            image.setValue(base_image);
            
            ds.getParams_in().add(image);
            
            if(route206.equals("yuanjian")){
				logger.info("{} 走远鉴通道...",trade_id);
				ds.setId("ds_yuanjian_face");
				retdatas = yuanjianFace.request(trade_id, ds);
			}else if(route206.equals("yuanjin")){
				logger.info("{} 走爰金通道...",trade_id);
				ds.setId("ds_aijin_facePhoto");
				retdatas = aijinFace.request(trade_id, ds);
			}else if(route206.equals("zhongsheng")){
				logger.info("{} 走中胜通道...",trade_id);
				ds.setId("ds_zhongsheng_face");
				retdatas = zhongsFaceService.request(trade_id, ds);				
			}else if(route206.equals("zhengtong")){
				logger.info("{} 走政通通道...",trade_id);
				ds.setId("ds_zhengt_face251");
				retdatas = zhengtFaceService.request(trade_id, ds);					
			}else{					
				logger.info("{} 走公安一所通道...",trade_id);
				ds.setId("ds_policeAuth_photov2");
				retdatas = police.request(trade_id, ds);
			}
            
            logger.error("{} 调用数据源返回信息：{}",trade_id,JSON.toJSONString(retdatas));
            retdatas.put("base64_image", base_image);
            
            if(isSuccess(retdatas)){
				Map<String, Object> retdata = (Map<String, Object>) retdatas.get(Conts.KEY_RET_DATA);
				
				if ("0".equals(String.valueOf(retdata.get("pair_verify_result")))) {
					retdata.put("passed", "true");
				} else {
					retdata.put("passed", "false");
				}
				retdata.put("verification_score", retdata.get("pair_verify_similarity"));
				if ("0".equals(return_image)) {
					retdata.put("base64_image", base_image);
				} else {
					retdata.put("base64_image", "");
				}
				resource_tag = Conts.TAG_FOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_MSG, "静默活体检测水印照人脸比对成功!");
				rets.put(Conts.KEY_RET_TAG, new String[] { Conts.TAG_FOUND });
				logger.info("{} 静默活体检测水印照人脸比对成功!", trade_id);
			}else{
				logger.info("{} 静默活体检测水印照人脸比对失败!", trade_id);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.valueOf(retdatas.get(
					      "retstatus").toString()));
//				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_MSG, String.valueOf(retdatas.get(
					      "retmsg")));
				String[] resource_tags = (String[]) retdatas.get("rettag");
				resource_tag = resource_tags[0];
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
			}
        } catch (Exception e) {
        	logger.error("{} 视频截取失败：{}",trade_id,ExceptionUtil.getTrace(e));
            resource_tag = Conts.TAG_SYS_ERROR;
            rets.clear();
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_SHANGT_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "视频拍摄不符合要求,请重新拍摄!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
        } 
		return resource_tag;
	}
	public static boolean isSuccess(Map<String, Object> params_out)
	  {
	    if (params_out == null)
	      return false;
	    CRSStatusEnum retstatus = CRSStatusEnum.valueOf(params_out.get(
	      "retstatus").toString());
	    return CRSStatusEnum.STATUS_SUCCESS.equals(retstatus);
	  }
	public String checkEyeOpen(String url,String base64Image,String trade_id) {
		String result = "1";
		try {
			logger.info("{} 开始判断眼睛是否睁开...",trade_id);
			String data = "data:image/jpg;base64,"+base64Image;
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader(HTTP.CONTENT_TYPE,
					"application/x-www-form-urlencoded");
			StringEntity se = new StringEntity("imguri="+URLEncoder.encode(data,"utf-8"),"utf-8");
			httpPost.setEntity(se);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(content));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String res = sb.toString();
			if(!StringUtils.isEmpty(res)){
				com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject)JSON.parse(res);
				result = json.getString("result");
			}			
			logger.info("{} 判断眼睛是否睁开返回结果:{}",trade_id,result);
		} catch (Exception e) {
			result = "eyeopen";
			logger.info("{} 判断眼睛是否睁开异常:{}",trade_id,e.getMessage());
		}
		return result;
	}
	public static String mkdirTodayPath(String basePath){
		String typePath = basePath + DateUtil.getSimpleDate(new Date(), "yyyyMMdd");
		if(!new File(typePath).exists())
			new File(typePath).mkdirs();
		return typePath;
	}
	public boolean isCheckSTLive(String properties,String acct_id){
		if(StringUtil.isEmpty(acct_id))
			return false;
		for(String property:properties.split(",")){
			String[] tmp = property.split(":");
			if(tmp[0].equals(acct_id) && "1".equals(tmp[1])){
				return true;
			}
		}
		return false;
	}
	public void impressVideo(String trade_id,String commondtext,String videoPath,String descpath,int times,int wait_time){
		String commond = String.format(commondtext, videoPath,descpath);
		logger.info("{} 视频截取图片开始,执行命令:{}", trade_id,commond);
		try{
			Runtime runtime = Runtime.getRuntime();  
	        runtime.exec(commond);//Runtime.exec(new String[] {"/bin/sh", "-c", command})
	        logger.info("{} 视频截取图片完成", trade_id);
	        int count1=1;
			while(true){
				if(count1 > 0)
					logger.info("{} 轮询截取文件是否存在,轮询序号:{}",trade_id,count1);
				if(count1>=times){
					logger.info("{} 轮询截取文件是否存在主程序最大容忍次数已到,系统将自动停止！",trade_id);
					break;
				}				
				File file=new File(descpath);
				if(file.exists()){
					logger.info("{} 视频压缩成功:{}",trade_id,descpath);
					Thread.sleep(wait_time);
					break;
				}			
				Thread.sleep(100);//模拟ds3处理需要时间
				count1++;
			}
		}catch(Exception e){
			
		}
        
	}
}
