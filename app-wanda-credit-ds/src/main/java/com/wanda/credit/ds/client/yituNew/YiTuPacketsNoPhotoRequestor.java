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
import com.wanda.credit.ds.client.yituNew.beans.Yitu_verity_result;
import com.wanda.credit.ds.dao.domain.YT_Auth_Result;
import com.wanda.credit.ds.dao.domain.yitu.Yitu_auth_option;
import com.wanda.credit.ds.dao.domain.yitu.Yitu_auth_photo;
import com.wanda.credit.ds.dao.iface.IYTAuthService;
import com.wanda.credit.ds.dao.iface.yitu.IYTAuthOptionService;
import com.wanda.credit.ds.dao.iface.yitu.IYTAuthPhotoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * author:nan.liu
 * desc:依图人脸识别900.1接口,加密数据包比对
 * data:2017-06-21
 * version:1.0
 * */
@DataSourceClass(bindingDataSourceId="ds_yitu_authPack103")
public class YiTuPacketsNoPhotoRequestor extends BaseYiTuNewSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(YiTuPacketsNoPhotoRequestor.class);
	private final String MSG_SUCC = "交易成功";
	private String yitu_saas_address;
	private String accessId;
	private String accessKey;
	
	private static String thousand = "99.9";
	
	private static String tenThousand = "99.99";
	
	private static String OneHundredThousand = "99.999";

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

		Yitu_auth_option yituOption = new Yitu_auth_option();
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.error("{} 外部依图人脸识别开始...", prefix);
		Map<String, Object> rets = null;
		String yitu_url =  propertyEngine.readById("yitu_local_address02");
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
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 姓名
			String transType = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 传输类型
			String queryImagePackage = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString(); // 照片数据包
			String negativeRate = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();//期望误报率
			String returnImage = ParamUtil.findValue(ds.getParams_in(), paramIds[5]).toString();//是否需要返回解密照片
			//记录入参到入参记录表		
			paramIn.put("cardNo", cardNo);
			paramIn.put("name", name);
			paramIn.put("transType", transType);
			
			String yitu_negative_rate;//期望误报率
			if ("1".equals(negativeRate)) {
				yitu_negative_rate = thousand;
			}else if ("2".equals(negativeRate)) {
				yitu_negative_rate = tenThousand;
			}else if ("3".equals(negativeRate)) {
				yitu_negative_rate = OneHundredThousand;
			} else {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU_PARAMETER_ERRO);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败!");
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.error("{} 外部依图返回识别失败", prefix);				
				return rets;
			}
			enCardNo = synchExecutorService.encrypt(cardNo);

			String sendCard = MD5.uppEncodeByMD5(cardNo+transType);
			ytAuth.setCardno(enCardNo);
			ytAuth.setName(name);
			ytAuth.setStatus(STATUS_YITU_NO2);
			ytAuth.setTrans_type(transType);
			logger.info("{} 人脸识别UseId为 : {}", prefix,sendCard);
			Map<String, Object> params = paramOptions(name,cardNo,queryImagePackage,yitu_negative_rate);
			Map<String, String> headers = new TreeMap<String, String>();
			String string = new ObjectMapper().writeValueAsString(params);						
			// end
			headers.put("x-access-id", accessId);
			if("01".equals(yitu_sign_key)){
				headers.put("x-signature", generateSignature(pkNew, accessKey, string, userDefinedContent));
			}else{
				headers.put("x-signature", generateSignature(pkNew, yitu_sign_key, string, userDefinedContent));
			}			
			logger.info("{} 开始发送照片至外部依图进行人脸识别...", prefix);
			String res = RequestHelper.doPost(yitu_url, null, headers, params,
					ContentType.create("text/plain", Consts.UTF_8),false);
			if(doPrint)
				logger.info("{} 外部依图返回消息成功! {}",prefix,res);
			logger.info("{} 外部依图返回消息成功!",prefix);
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, res, new String[] { trade_id }));
			JSONObject respMap = JSONObject.fromObject(res);
		
			ytAuth.setRtn(respMap.get("rtn").toString());
			ytAuth.setMessage(respMap.get("message").toString());
			ytAuth.setGlobal_request_id(respMap.get("global_request_id").toString());
					
			int rtn = (Integer) respMap.get("rtn");			
			//log:交易状态信息,返回码保存
			logObj.setState_msg(respMap.get("message").toString());
			if(StringUtils.isNotEmpty(respMap.get("rtn").toString())){
				DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_yitu_auth", respMap.get("rtn").toString());
				logObj.setBiz_code1(dataSourceBizCodeVO == null?respMap.get("rtn").toString():dataSourceBizCodeVO.getBizCode());
			}else{
				logObj.setBiz_code1(respMap.get("rtn").toString());
			}	
			boolean is_screen_flag = false;
		
			if ((rtn < 0) || is_screen_flag) {
				rets.clear();
				if (rtn==-105||rtn==-6122||rtn==-6123) {
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU_PHOTO_NOTANLASIS);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + respMap.get("message").toString());
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.error("{} 外部依图返回识别失败:{}", prefix,respMap.get("message").toString()) ;
				}else if (rtn==-6134) {
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU_INSUFFICIENT_LIGHT);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + respMap.get("message").toString());
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.error("{} 外部依图返回识别失败:{}", prefix,respMap.get("message").toString());
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + respMap.get("message").toString());
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.error("{} 外部依图返回识别失败:{}", prefix,respMap.get("message").toString());
				}				
			} else {
				Yitu_verity_result yitu_result = null;
				if(respMap.get("query_image_package_result") !=null){
					 yituOption = JSONUtil.convertJson2Object(
							(JSONObject) JSONUtil.getJsonValueByKey(respMap, "query_image_package_result", false),Yitu_auth_option.class);
				}
				if(respMap.get("verify_result") !=null){
					yitu_result = JSONUtil.convertJson2Object(
							(JSONObject) JSONUtil.getJsonValueByKey(respMap, "verify_result", false),Yitu_verity_result.class);
				}
				if("true".equalsIgnoreCase(yituOption.getIs_anti_screen_check_valid())){//防视频翻拍已开启
					
					if(!"true".equalsIgnoreCase(yituOption.getIs_anti_screen_check_passed())){//防视频翻拍未通过
						is_screen_flag = true;
					}
				}
				ytAuth.setPair_similarity(yitu_result.getFinal_verify_score());
				ytAuth.setPair_result(yitu_result.getIs_pass());
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//交易成功
				logObj.setState_msg(MSG_SUCC);
				Map<String, Object> respResult = new HashMap<String, Object>();
				respResult.put("server_idx", "05");
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
				rets.clear();
				respResult.put("rtn", 0);
				respResult.put("pair_verify_similarity", yitu_result.getFinal_verify_score());
				if("true".equals(yitu_result.getIs_pass())){
					respResult.put("pair_verify_result", "1");
				}else{
					respResult.put("pair_verify_result", "0");
				}
				
				//是否返回解密照片
				if ("0".equals(returnImage)) {
					respResult.put("photo_content",query_image.get(0));
				}else{
					respResult.put("photo_content","");
				}
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, respResult);
				rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
				resource_tag = Conts.TAG_FOUND;
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.info("{} 人脸识别成功!", prefix);
			}
			
			yituOption.setTrade_id(trade_id);
			yituOption.setCardno(enCardNo);
			ytAuthService.add(ytAuth);
			ytAuthOptionService.add(yituOption);
			if(is_screen_flag){
				rets.clear();
				resource_tag = Conts.TAG_FOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU__RISK_USER);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + respMap.get("message").toString());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.error("{} 外部依图返回识别失败:{}", prefix,respMap.get("message").toString());
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
	
	public Map<String, Object> paramOptions(String name,String cardNo,String queryImagePackage, String yitu_negative_rate){
		Map<String, Object> params = new TreeMap<String, Object>();
		Map<String, Object> upload_idcard_image = new TreeMap<String, Object>();
		Map<String, Object> user_info1 = new TreeMap<String, Object>();
		Map<String, Object> options1 = new TreeMap<String, Object>();
		user_info1.put("name", name);
		user_info1.put("citizen_id", cardNo);
		options1.put("auto_rotate", false);
		upload_idcard_image.put("user_info", user_info1);
		upload_idcard_image.put("options", options1);
		Map<String, Object> verify_query_image = new TreeMap<String, Object>();
		Map<String, Object> user_info2 = new TreeMap<String, Object>();
		Map<String, Object> options2 = new TreeMap<String, Object>();
		user_info2.put("query_image_package", queryImagePackage);
		options2.put("auto_rotate", false);
		options2.put("true_negative_rate", yitu_negative_rate);
		options2.put("query_image_package_check_same_person", true);
		options2.put("query_image_package_return_image_list", true);
		options2.put("query_image_package_check_anti_screen", true);
		options2.put("query_image_package_check_anti_screen_threshold", "low");
		verify_query_image.put("user_info", user_info2);
		verify_query_image.put("options", options2);
		params.put("upload_idcard_image", upload_idcard_image);
		params.put("verify_query_image", verify_query_image);
		return params;
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