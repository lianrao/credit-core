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
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImageUtils;
import com.wanda.credit.base.util.ReadVideo;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.policeAuthV2.PoliceFacePhotoV2Requestor;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.shangtang.IShangTangVideoPhotoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;
/***
 * 商汤数字活体检测
 * @author liunan
 *
 */
@DataSourceClass(bindingDataSourceId = "ds_shangtang_numVideo")
public class STGladNumberVideoRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(STGladNumberVideoRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IShangTangVideoPhotoService shangTangVideoPhotoService;
	@Autowired
	private PoliceFacePhotoV2Requestor police;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> retdatas = new HashMap<String, Object>();
		String dest_vopath = null;
		String vopath = null;
		String impath = null;
		logger.info("{} 商汤数字活体检测调用开始...", prefix);
		Map<String, Object> rets = null;
		String shangtang_url = propertyEngine.readById("shangtang_number_video_url");
		String shangtang_path = propertyEngine.readById("shangtang_stateless_path");
		String doSTRequestlive = propertyEngine.readById("shangtang_stateless_doshlive");//是否判断活体
		
		int size_limit = Integer.valueOf(propertyEngine.readById("shangtang_stateless_doshlive_size_limit"));//视频大小压缩限制
		int size_times_limit = Integer.valueOf(propertyEngine.readById("shangtang_stateless_doshlive_size_times"));//视频大小压缩轮询次数
		int size_wait_times = Integer.valueOf(propertyEngine.readById("shangtang_stateless_doshlive_size_wait"));//视频大小压缩后等待时间
		String video_commond = propertyEngine.readById("shangtang_stateless_doshlive_video_commond");
		Map<String, Object> reqparam = new HashMap<String, Object>();
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
			String acct_id = "";
			if(ParamUtil.findValue(ds.getParams_in(), "acct_id")!=null){
				acct_id = ParamUtil.findValue(ds.getParams_in(), "acct_id").toString();
			}
			String req_video1 = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); // 视频
			String number = ParamUtil.findValue(ds.getParams_in(),
					paramIds[1]).toString(); // 是否返回照片
			String name = ParamUtil.findValue(ds.getParams_in(),
					paramIds[2]).toString(); // 误识率
			String cardNo = ParamUtil.findValue(ds.getParams_in(),
					paramIds[3]).toString(); // 误识率

			String imgPath = ShangTangVideoCheckRequestor.mkdirTodayPath(shangtang_path);
			vopath = imgPath + File.separator + trade_id + ".mp4";
			dest_vopath = imgPath + File.separator + trade_id + "dest.mp4";
			impath = imgPath + File.separator + trade_id;
			logger.info("{} 商汤静默视频imgPath:{}", prefix,imgPath);
			logger.info("{} 商汤静默视频vopath:{}", prefix,vopath);
			logger.info("{} 商汤静默视频impath:{}", prefix,impath);
			reqparam.put("number", number);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
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
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){				
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			String req_video = shangTangVideoPhotoService.queryVideoFile(trade_id, req_video1);
			ImageUtils.decodeBase64ToImage(req_video, imgPath + File.separator, trade_id
					+ ".mp4");
			float video_size = ReadVideo.ReadVideoSize(new File(vopath))/1024/1024;
			logger.info("{} 视频大小:{} MB", prefix,video_size);
			if(video_size>=size_limit && isCheckSTLive(doSTRequestlive,acct_id)){
				logger.info("{} 视频大小超过2M，进行压缩", prefix);
				impressVideo(trade_id,video_commond,vopath,dest_vopath,size_times_limit,size_wait_times);
				vopath = dest_vopath;
				impath = imgPath + File.separator + trade_id+"dest";
			}
			String stresult = httpSTClientPost(vopath,number,prefix);
			logger.info("{} 商汤返回信息:{}", prefix,stresult);
			JSONObject respMap = JSONObject.fromObject(stresult);
			logObj.setBiz_code1(respMap.getString("request_id"));
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
			if("1000".equals(respMap.getString("code"))){
				resource_tag = Conts.TAG_TST_SUCCESS;				
				boolean passed = respMap.getBoolean("passed");
				if(!passed){
					logger.info("{} 活体检测不通过...",trade_id);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU_INSUFFICIENT_LIGHT);
					rets.put(Conts.KEY_RET_MSG, "数字活体检测不通过");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else{
					String base_image = respMap.getString("base64_image");
					Param image = new Param();
		            image.setId("query_image_content");
		            image.setValue(base_image);
		            
		            ds.getParams_in().add(image);
		            logger.info("{} 走公安一所通道...",trade_id);
					ds.setId("ds_policeAuth_photov2");
					retdatas = police.request(trade_id, ds);
					
					if(ShangTangVideoCheckRequestor.isSuccess(retdatas)){
						Map<String, Object> retdata = (Map<String, Object>) retdatas.get(Conts.KEY_RET_DATA);
						Map<String, Object> retdata_new = new HashMap<String, Object>();
						if ("0".equals(String.valueOf(retdata.get("pair_verify_result")))) {
							retdata_new.put("final_auth_result", "1");
						} else {
							retdata_new.put("final_auth_result", "0");
						}
						retdata_new.put("matched_similarity", retdata.get("pair_verify_similarity"));
						resource_tag = Conts.TAG_FOUND;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_DATA, retdata_new);
						rets.put(Conts.KEY_RET_MSG, "交易成功");
						rets.put(Conts.KEY_RET_TAG, new String[] { Conts.TAG_FOUND });
						logger.info("{} 静默活体检测水印照人脸比对成功!", trade_id);
					}else{
						logger.info("{} 静默活体检测水印照人脸比对失败!", trade_id);
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.valueOf(retdatas.get(
							      "retstatus").toString()));
						rets.put(Conts.KEY_RET_MSG, String.valueOf(retdatas.get(
							      "retmsg")));
						String[] resource_tags = (String[]) retdatas.get("rettag");
						resource_tag = resource_tags[0];
						rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					}
				}				
			}else if("2008".equals(respMap.getString("code"))){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_WANDA_FACE_FAIL);
				rets.put(Conts.KEY_RET_MSG, "无效的视频文件");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else if("4007".equals(respMap.getString("code"))){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_WANDA_OCR_FAIL);
				rets.put(Conts.KEY_RET_MSG, "活体检测异常");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else if("5015".equals(respMap.getString("code"))){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU_PHOTO_NOTANLASIS);
				rets.put(Conts.KEY_RET_MSG, "语音识别异常");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else if("1002".equals(respMap.getString("code"))){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU_PARAMETER_ERRO);
				rets.put(Conts.KEY_RET_MSG, "使用频率超过限制");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源调用失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 公安数据源厂商返回异常! ",prefix);
				return rets;
			}
		} catch (Exception ex) {
			resource_tag = Conts.TAG_TST_FAIL;
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_SHANGT_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "视频拍摄不符合要求,请重新拍摄!");
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
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,false);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,false);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		return rets;
	}
	public String httpSTClientPost(String vopath,String number, String prefix)
			throws ClientProtocolException, IOException, SignatureException {
		logger.info("{} 商汤活体数字检测调用开始...", prefix);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(propertyEngine.readById("shangtang_number_video_url"));
		FileBody fileVideo = new FileBody(new File(vopath));
		StringBody StringBody = new StringBody(number);
        StringBody return_image = new StringBody("true");
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("video_file", fileVideo);
		entity.addPart("number", StringBody);
        entity.addPart("return_image", return_image);
		post.setEntity(entity);
		post.setHeader("Authorization", GenerateString.genHeaderParam(
				propertyEngine.readById("shangtang_liveness_api_key"),
				propertyEngine.readById("shangtang_liveness_api_sec")));// 请将AUTHORIZATION替换为根据API_KEY和API_SECRET得到的签名认证串
		long startTime = System.currentTimeMillis();
		HttpResponse response = httpclient.execute(post);
		long endTime = System.currentTimeMillis();
		logger.info("{} 商汤活体数字检测请求耗时:{}", prefix, (endTime - startTime) + " ms");
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
