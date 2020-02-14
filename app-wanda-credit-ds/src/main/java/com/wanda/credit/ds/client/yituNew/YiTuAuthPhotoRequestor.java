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

/**
 * author:nan.liu
 * desc:依图人脸识别101.1照片比对
 * data:2017-06-21
 * version:1.0
 * */
@DataSourceClass(bindingDataSourceId="ds_yitu_authPhoto101")
public class YiTuAuthPhotoRequestor extends BaseYiTuNewSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(YiTuAuthPhotoRequestor.class);
	private final String MSG_SUCC = "交易成功";
	private final String PHOTO_SIZE_FAIL = "2";
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
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String yitu_url =  propertyEngine.readById("yitu_address01");
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
			String photo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 照片ID
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 姓名
			String transType = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString(); // 传输类型
			enCardNo = synchExecutorService.encrypt(cardNo);
			//记录入参到入参记录表			
			paramIn.put("cardNo", cardNo);
			paramIn.put("name", name);
			paramIn.put("transType", transType);
			String sendCard = MD5.uppEncodeByMD5(cardNo+transType);
			String photoContent = "";
			photoContent = photo;
					
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
			}else{ytAuth.setCardno(enCardNo);
				ytAuth.setName(name);
				ytAuth.setPhoto_id(fpath);
				ytAuth.setStatus(STATUS_YITU_NO1);
				ytAuth.setTrans_type(transType);
				logger.info("{} 人脸识别UseId为 : {}", prefix,sendCard);
				Map<String, Object> params = new TreeMap<String, Object>();
				Map<String, Object> param_user_info = new TreeMap<String, Object>();
				Map<String, Object> param_option = new TreeMap<String, Object>();
				Map<String, String> headers = new TreeMap<String, String>();				
				param_user_info.put("image_content", photoContent);
				param_option.put("true_negative_rate", true_negative_rate);// 99.9 这个参数是误报率,按千分之一误报率部署
				param_option.put("verify_type", 1);
				param_option.put("image_type", 301);
				params.put("session_id", sendCard); // 加密身份证号码,避免被泄露
				params.put("mode", 1);
				params.put("user_info", param_user_info);
				params.put("options", param_option);
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
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, res, new String[] { trade_id }));
				JSONObject respMap = JSONObject.fromObject(res);
				int rtn = (Integer) respMap.get("rtn");
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
					int similer_len = verify_similarity.length();
					if(similer_len>=20){
						ytAuth.setPair_similarity(verify_similarity.substring(0,20));
					}else{
						ytAuth.setPair_similarity(verify_similarity);
					}					
					rets.clear();
					respResult.put("rtn", 0);
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