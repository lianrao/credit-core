package com.wanda.credit.ds.client.yituNew;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.map.ObjectMapper;
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
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.YT_Auth_Result;
import com.wanda.credit.ds.dao.iface.IYTAuthService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_yitu_authPhoto102")
public class YiTuLocalFacialPhotographContrast extends BaseYiTuNewSourceRequestor implements IDataSourceRequestor{
	private final Logger logger = LoggerFactory.getLogger(YiTuLocalFacialPhotographContrast.class);
	private final String MSG_SUCC = "交易成功";
	private final String PHOTO_SIZE_FAIL = "2";
	private String verifImgApi;
	private String yitu_saas_address;
	private String accessId;
	private String accessKey;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	private IYTAuthService ytAuthService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String yitu_url =  propertyEngine.readById("yitu_local_address01");
		String yitu_sign_key =  propertyEngine.readById("yitu_signKey");
		//请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());//log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setReq_url(yitu_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);	//初始值-失败
		logObj.setIncache("0");//不缓存
		Map<String,Object> paramIn = new HashMap<String,Object>();
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			rets = new HashMap<String, Object>();
			YT_Auth_Result ytAuth = new YT_Auth_Result();
			ytAuth.setTrade_id(trade_id);
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); // 身份证号码
			String photo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 网纹照片数据
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 姓名
			String transType = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString(); // 传输类型
			String query_Image_Content = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString(); //活体照片数据
			
			enCardNo = synchExecutorService.encrypt(cardNo);
			//记录入参到入参记录表			
			paramIn.put("cardNo", cardNo);
			paramIn.put("name", name);
			paramIn.put("transType", transType);
			String sendCard = MD5.uppEncodeByMD5(cardNo+transType);
			String photoContent = "";
			photoContent = photo;
			String queryImageContent = "";
			queryImageContent = query_Image_Content;
					
			String fpath = null;
			if(StringUtils.isNotEmpty(photoContent)){
				logger.info("{} 图片上传征信存储开始...", prefix);
				fpath = fileService.upload(photoContent, FileType.JPG, FileArea.DS,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix,fpath);
			}
			//根据dsid和retCode到返回码对照表获取bizCode-BizName
			DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_yitu_auth", PHOTO_SIZE_FAIL);
			
			if(formatStrSize(photoContent)){
				logObj.setBiz_code1(dataSourceBizCodeVO == null?PHOTO_SIZE_FAIL:dataSourceBizCodeVO.getBizCode());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" );
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				//log:交易状态信息,返回码保存
				logObj.setState_msg(CRSStatusEnum.STATUS_FAILED_DS_YITU4_EXCEPTION.ret_msg);
				logObj.setBiz_code1(CRSStatusEnum.STATUS_FAILED_DS_YITU4_EXCEPTION.ret_code);
				logger.error("{} 照片大小不符合依图要求！", prefix);
			}else{
				ytAuth.setCardno(enCardNo);
				ytAuth.setName(name);
				ytAuth.setPhoto_id(fpath);
				ytAuth.setStatus(STATUS_YITU_NO1);
				ytAuth.setTrans_type(transType);
				logger.info("{} 人脸识别UseId为 : {}", prefix,sendCard);
				Map<String, Object> params = paramOptions(photoContent,sendCard,queryImageContent);
				Map<String, String> headers = new TreeMap<String, String>();
				        
				
			
				headers.put("x-access-id", accessId);
				if("01".equals(yitu_sign_key)){
					headers.put("x-signature", generateSignature(pkNew, accessKey, new ObjectMapper().writeValueAsString(params), userDefinedContent));
				}else{
					headers.put("x-signature", generateSignature(pkNew, yitu_sign_key, new ObjectMapper().writeValueAsString(params), userDefinedContent));
				}				
				logger.info("{} 开始发送照片至外部依图进行人脸识别...", prefix);
				String res = RequestHelper.doPost(yitu_url + verifImgApi, null, headers, params,
						ContentType.create("text/plain", Consts.UTF_8),true);
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, res, new String[] { trade_id }));
				JSONObject respMap = JSONObject.fromObject(res);
				
				ytAuth.setRtn(respMap.get("rtn").toString());
				ytAuth.setMessage(respMap.get("message").toString());
				ytAuth.setGlobal_request_id(respMap.get("global_request_id").toString());
			
				//log:交易状态信息,返回码保存				
				logObj.setState_msg(respMap.get("message").toString());
				if(StringUtils.isNotEmpty(respMap.get("rtn").toString())){
					dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_yitu_auth", respMap.get("rtn").toString());
					logObj.setBiz_code1(dataSourceBizCodeVO == null?respMap.get("rtn").toString():dataSourceBizCodeVO.getBizCode());
				}else{
					logObj.setBiz_code1(respMap.get("rtn").toString());
				}				
				if ((Integer) respMap.get("rtn") < 0) {			
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + respMap.get("message").toString());
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.error("{} 外部依图返回识别失败", prefix);
				} else {
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//交易成功
					logObj.setState_msg(MSG_SUCC);
					Map<String, Object> respResult = new HashMap<String, Object>();
					respResult.put("server_idx", "01");
					
					ytAuth.setPair_result(respMap.get("pair_verify_result").toString());
					String verify_similarity = respMap.getString("pair_verify_similarity").toString();
					int similer_len = verify_similarity.length();
					if(similer_len>=20){
						ytAuth.setPair_similarity(verify_similarity.substring(0,20));
					}else{
						ytAuth.setPair_similarity(verify_similarity);
					}					
					
					rets.clear();
					respResult.put("rtn", 0);
					respResult.put("pair_verify_similarity",ytAuth.getPair_similarity());
					if("0".equals(ytAuth.getPair_result())){
						respResult.put("pair_verify_result", "0");
					}else{
						respResult.put("pair_verify_result", "1");
					}
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA, respResult);
					rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
					resource_tag = Conts.TAG_FOUND;
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.info("{} 人脸识别成功!", prefix);
				}
				ytAuthService.add(ytAuth);
			}			
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ex.getMessage());
			
			/**如果是超时异常 记录超时信息*/
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		    if(ExceptionUtil.isTimeoutException(ex)){		    	
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
		    	resource_tag = Conts.TAG_SYS_TIMEOUT;
		    }
		    logObj.setState_msg(ex.getMessage());
		    rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{
			//log入库
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}
		return rets;
	}
	
	public Map<String, Object> paramOptions(String photoContent, String sendCard, String queryImageContent){
		
		boolean yitu_same_check = "1".equals(propertyEngine.readById("yitu_same_check"));//是否开启同人检查
		boolean yitu_image_list = "1".equals(propertyEngine.readById("yitu_image_list"));//是否返回大礼包解析图片列表
		int yitu_image_type = Integer.parseInt(propertyEngine.readById("yitu_image_type"));//查询图片的类型
		int database_image_type = Integer.parseInt(propertyEngine.readById("database_image_type"));//登记照片的类型
	    boolean  yitu_auto_flip = "1".equals(propertyEngine.readById("yitu_auto_flip"));//是否开启自动镜像优化
	    boolean  yitu_auto_rotate = "1".equals(propertyEngine.readById("yitu_auto_rotate"));//是否开启查询照的自动旋转识别
	    boolean  yitu_auto_rotate_database = "1".equals(propertyEngine.readById("yitu_auto_rotate_database"));//是否开启登记照的自动旋转识别
	    String yitu_negative_rate = propertyEngine.readById("yitu_negative_rate");//期望误报率
	    int yitu_max_faces = Integer.parseInt(propertyEngine.readById("yitu_max_faces"));//查询照片中至多几个人脸
	    boolean  yitu_enable_verify = "1".equals(propertyEngine.readById("yitu_enable_verify"));//是否返回比对的详细信息
	    boolean  yitu_return_face = "1".equals(propertyEngine.readById("yitu_return_face"));//是否换回查询照每张人脸的位
	    boolean  yitu_screen_check = "1".equals(propertyEngine.readById("yitu_screen_check"));//是否开启防屏幕翻拍检测
	    String  yitu_screen_switch = (propertyEngine.readById("yitu_screen_switch"));//防屏幕翻拍检测
	    boolean  yitu_dark_illumination = "1".equals(propertyEngine.readById("yitu_dark_illumination"));//是否开启光线昏暗检测
	    String  yitu_dark_illumination_threshold = (propertyEngine.readById("yitu_dark_illumination_threshold"));//光线昏暗检测
	    boolean  yitu_picture_check = "1".equals(propertyEngine.readById("yitu_picture_check"));//是否开启防照片检测
	    String  yitu_picture_switch = (propertyEngine.readById("yitu_picture_switch"));//防照片检测
	    boolean  yitu_check_anti = "1".equals(propertyEngine.readById("yitu_check_anti"));//是否开启眼部遮挡检测
	    String  yitu_check_threshold = (propertyEngine.readById("yitu_check_threshold"));//眼布遮挡检测
	    boolean  yitu_hole_check = "1".equals(propertyEngine.readById("yitu_hole_check"));//是否开启防孔洞检测
	    String  yitu_hole_switch = (propertyEngine.readById("yitu_hole_switch"));//防孔洞检测
		
		Map<String, Object> params = new TreeMap<String, Object>();
		
		params.put("query_image_content", queryImageContent);//活体照片数据
		params.put("query_image_package_check_same_person", yitu_same_check);//是否开启同人检查
		params.put("query_image_package_return_image_list", yitu_image_list);//是否返回大礼包解析图片列表
		params.put("query_image_type", yitu_image_type);//查询图片的类型
		params.put("database_image_content", photoContent);//网纹照数据
		params.put("database_image_type", database_image_type);//登记照片的类型;
		params.put("yitu_auto_flip", yitu_auto_flip);//是否开启自动镜像优化
		params.put("auto_rotate_for_query",yitu_auto_rotate);//是否开启查询照的自动旋转识别
		params.put("auto_rotate_for_database",yitu_auto_rotate_database);//是否开启登记照自动旋转识别
		params.put("true_negative_rate", yitu_negative_rate);////期望误报率
		params.put("max_faces_allowed", yitu_max_faces);//查询照片中至多几个人脸
		params.put("enable_verify_detail", yitu_enable_verify);//是否返回比对的详细信息
		params.put("return_face_rect", yitu_return_face);//是否返回查询照每张人脸的位
		params.put("query_image_package_check_anti_screen", yitu_screen_check);//是否开启防视频翻拍
		params.put("query_image_package_check_anti_screen_threshold", yitu_screen_switch);//防视频翻拍阈值
		params.put("query_image_package_check_dark_illumination", yitu_dark_illumination);//是否开启光线昏暗检测
		params.put("query_image_package_check_dark_illumination_threshold", yitu_dark_illumination_threshold);//光线昏暗检测
		params.put("query_image_package_check_anti_picture", yitu_picture_check);//是否开启防照片检测
		params.put("query_image_package_check_anti_picture_threshold", yitu_picture_switch);////防照片检测
		params.put("query_image_package_check_anti_eye_blockage", yitu_check_anti);////是否开启眼部遮挡检测
		params.put("query_image_package_check_anti_eye_blockage_threshold", yitu_check_threshold);//眼布遮挡检测
		params.put("query_image_package_check_anti_hole", yitu_hole_check);//是否开启防孔洞检测
		params.put("query_image_package_check_anti_hole_threshold", yitu_hole_switch);//防孔洞检测
		params.put("session_id", sendCard); // 加密身份证号码,避免被泄露
		
		
		return params;
		
	}
	public String getVerifImgApi() {
		return verifImgApi;
	}
	public void setVerifImgApi(String verifImgApi) {
		this.verifImgApi = verifImgApi;
	}
	public String getYitu_saas_address() {
		return yitu_saas_address;
	}
	public void setYitu_saas_address(String yitu_saas_address) {
		this.yitu_saas_address = yitu_saas_address;
	}
	public String getAccessId() {
		return accessId;
	}
	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
}
