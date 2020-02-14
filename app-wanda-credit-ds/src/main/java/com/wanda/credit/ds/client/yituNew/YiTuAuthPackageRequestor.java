package com.wanda.credit.ds.client.yituNew;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.wanda.credit.base.util.JSONUtil;
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.YT_Auth_Result;
import com.wanda.credit.ds.dao.domain.yitu.Yitu_auth_option;
import com.wanda.credit.ds.dao.domain.yitu.Yitu_auth_photo;
import com.wanda.credit.ds.dao.iface.IYTAuthService;
import com.wanda.credit.ds.dao.iface.yitu.IYTAuthOptionService;
import com.wanda.credit.ds.dao.iface.yitu.IYTAuthPhotoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * author:nan.liu
 * desc:依图人脸识别101.1接口,加密数据包比对
 * data:2017-06-21
 * version:1.0
 * */
@DataSourceClass(bindingDataSourceId="ds_yitu_authPack101")
public class YiTuAuthPackageRequestor extends BaseYiTuNewSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(YiTuAuthPackageRequestor.class);
	private final String MSG_SUCC = "交易成功";
	private String verifImgApi;
	private String yitu_saas_address;
	private String accessId;
	private String accessKey;
	private String true_negative_rate;

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	private IYTAuthService ytAuthService;	
	@Autowired
	private IYTAuthOptionService ytAuthOptionService;
	@Autowired
	private IYTAuthPhotoService ytAuthPhotoService;
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String yitu_url =  propertyEngine.readById("yitu_address01");
		String yitu_sign_key =  propertyEngine.readById("yitu_signKey");
		boolean doPrint = "1".equals(propertyEngine.readById("sys_log_print_switch"));
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
			String photo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 照片ID
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 照片ID
			String transType = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString(); // 传输类型
			enCardNo = synchExecutorService.encrypt(cardNo);
			//记录入参到入参记录表			
			paramIn.put("cardNo", cardNo);
			paramIn.put("name", name);
			paramIn.put("transType", transType);
			String sendCard = MD5.uppEncodeByMD5(cardNo+transType);
			String photoContent = "";
			photoContent = photo;
			ytAuth.setCardno(enCardNo);
			ytAuth.setName(name);
			ytAuth.setStatus(STATUS_YITU_NO2);
			ytAuth.setTrans_type(transType);
			logger.info("{} 人脸识别UseId为 : {}", prefix,sendCard);
			Map<String, Object> params = paramOptions(photoContent,sendCard);
			Map<String, String> headers = new TreeMap<String, String>();			
			// end
			headers.put("x-access-id", accessId);
			if("01".equals(yitu_sign_key)){
				headers.put("x-signature", generateSignature(pkNew, accessKey, new ObjectMapper().writeValueAsString(params), userDefinedContent));
			}else{
				headers.put("x-signature", generateSignature(pkNew, yitu_sign_key, new ObjectMapper().writeValueAsString(params), userDefinedContent));
			}			
			logger.info("{} 开始发送照片至外部依图进行人脸识别...", prefix);
			String res = RequestHelper.doPost(yitu_url + verifImgApi, null, headers, params,
					ContentType.create("text/plain", Consts.UTF_8),false);
			logger.info("{} 外部依图返回消息成功!", prefix);
			if(doPrint)
				logger.info("{} 外部返回消息为:\n{}", prefix,res);;
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, res, new String[] { trade_id }));
			JSONObject respMap = JSONObject.fromObject(res);
			Yitu_auth_option yituOption=new Yitu_auth_option();
			if(respMap.get("query_image_package_result") != null){
				yituOption = JSONUtil.convertJson2Object(
						(JSONObject) JSONUtil.getJsonValueByKey(respMap, "query_image_package_result", false),Yitu_auth_option.class);
			}
			yituOption.setCardno(enCardNo);
			yituOption.setTrade_id(trade_id);
			int rtn = (Integer) respMap.get("rtn");
			ytAuth.setRtn(respMap.get("rtn").toString());
			ytAuth.setMessage(respMap.get("message").toString());
			ytAuth.setGlobal_request_id(respMap.get("global_request_id").toString());
			//log:交易状态信息,返回码保存
			logObj.setState_msg(respMap.get("message").toString());
			if(StringUtils.isNotEmpty(respMap.get("rtn").toString())){
				DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_yitu_auth", respMap.get("rtn").toString());
				logObj.setBiz_code1(dataSourceBizCodeVO == null?respMap.get("rtn").toString():dataSourceBizCodeVO.getBizCode());
			}else{
				logObj.setBiz_code1(respMap.get("rtn").toString());
			}	
			boolean is_screen_flag = false;
			if("true".equalsIgnoreCase(yituOption.getIs_anti_screen_check_valid())){//防视频翻拍已开启
				if(!"true".equalsIgnoreCase(yituOption.getIs_anti_screen_check_passed())){//防视频翻拍未通过
					logger.error("{} 外部依图返回防视频翻拍未通过!", prefix);
					is_screen_flag = true;
				}
			}
			if (rtn < 0) {
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
				JSONObject verify_result = (JSONObject) respMap.get("verify_result");
				String pair_result = verify_result.get("is_pass").toString();
				ytAuth.setPair_result(pair_result);
				String verify_similarity = verify_result.get("similarity").toString();
				Map<String, Object> query_image_result=(Map<String, Object>) respMap.get("query_image_package_result");
				List<String> query_image = (List<String>) query_image_result.get("query_image_contents");
				threadYiTuPool.execute(new PhotoStoreRunnable(trade_id,enCardNo,name,query_image){
					@Override
					public void run() {
						try {
							List<Yitu_auth_photo> result_photo = new ArrayList<Yitu_auth_photo>();
							for(String photostr:getPhotoList()){
								if(StringUtils.isNotEmpty(photostr)){
									Yitu_auth_photo authPhoto = new Yitu_auth_photo();
									logger.info("{} 图片上传征信存储开始...", prefix);
									String fpath = fileService.upload(photostr, FileType.JPG, FileArea.DS,getTrade_id());
									logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix,fpath);
									authPhoto.setCardno(getCardNo());
									authPhoto.setName(getName());
									authPhoto.setTrade_id(getTrade_id());
									authPhoto.setPhoto_id(fpath);
									result_photo.add(authPhoto);
							    }
							}
							ytAuthPhotoService.add(result_photo);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});	
				int similer_len = verify_similarity.length();
				if(similer_len>=20){
					ytAuth.setPair_similarity(verify_similarity.substring(0,20));
				}else{
					ytAuth.setPair_similarity(verify_similarity);
				}				
				rets.clear();
				respResult.put("rtn", 0);
				respResult.put("photo_content",query_image.get(0));
				respResult.put("pair_verify_similarity", verify_result.get("similarity"));
				if("true".equalsIgnoreCase(pair_result)){
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
			ytAuthOptionService.add(yituOption);
			if(is_screen_flag){
				rets.clear();
				resource_tag = Conts.TAG_FOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + respMap.get("message").toString());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.error("{} 外部依图返回识别失败", prefix);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
	public Map<String, Object> paramOptions(String photoContent,String sendCard){
		//---------------------依图参数配置--------------
		boolean yitu_auto_rotate =  "1".equals(propertyEngine.readById("yitu_auto_rotate"));//开启自动旋转矫正
		boolean yitu_check_same_person =  "1".equals(propertyEngine.readById("yitu_same_check"));//开启同人检查
		boolean yitu_check_anti_screen =  "1".equals(propertyEngine.readById("yitu_screen_check"));//开启防视频翻拍
		String yitu_screen_switch = propertyEngine.readById("yitu_screen_switch");//防视频翻拍阈值high
		boolean yitu_check_anti_picture =  "1".equals(propertyEngine.readById("yitu_picture_check"));//开启防照片检测
		String yitu_picture_switch = propertyEngine.readById("yitu_picture_switch");//防照片检测阈值medium
		boolean yitu_check_anti_hole =  "1".equals(propertyEngine.readById("yitu_hole_check"));//开启防孔洞检测
		String yitu_hole_switch = propertyEngine.readById("yitu_hole_switch");//防孔洞检测阈值medium
				
		Map<String, Object> params = new TreeMap<String, Object>();
		Map<String, Object> param_user_info = new TreeMap<String, Object>();
		Map<String, Object> param_options = new TreeMap<String, Object>();			
		param_user_info.put("query_image_package", photoContent);
		param_options.put("query_image_package_return_image_list", true);
		param_options.put("true_negative_rate", true_negative_rate);// 99.9 这个参数是误报率,按千分之一误报率部署
		param_options.put("verify_type", 1);
		param_options.put("image_type", 301);
		param_options.put("auto_rotate", yitu_auto_rotate);
		param_options.put("query_image_package_check_same_person", yitu_check_same_person);
		param_options.put("query_image_package_check_anti_screen", yitu_check_anti_screen);
		param_options.put("query_image_package_check_anti_screen_threshold", yitu_screen_switch);
		param_options.put("query_image_package_check_anti_picture", yitu_check_anti_picture);
		param_options.put("query_image_package_check_anti_picture_threshold", yitu_picture_switch);
		param_options.put("query_image_package_check_anti_hole", yitu_check_anti_hole);
		param_options.put("query_image_package_check_anti_hole_threshold", yitu_hole_switch);
		params.put("session_id", sendCard); // 加密身份证号码,避免被泄露
		params.put("mode", 4); 
		params.put("user_info", param_user_info);
		params.put("options", param_options);
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

	public String getTrue_negative_rate() {
		return true_negative_rate;
	}

	public void setTrue_negative_rate(String true_negative_rate) {
		this.true_negative_rate = true_negative_rate;
	}

}