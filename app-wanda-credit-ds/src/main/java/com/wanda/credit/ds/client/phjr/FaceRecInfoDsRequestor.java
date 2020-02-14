/**   
* @Description: 记录人脸识别信息
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.phjr;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.phjr.bean.ReqBusiData;
import com.wanda.credit.ds.client.phjr.bean.ResBodyBean;
import com.wanda.credit.ds.client.phjr.util.AesUtils;
import com.wanda.credit.ds.client.phjr.util.RSAUtil;
import com.wanda.credit.ds.dao.domain.phjr.PHUserInfo;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.dsconfig.commonfunc.RequestHelper;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_phjr_faceRec")
public class FaceRecInfoDsRequestor extends BasePHJRDSRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(FaceRecInfoDsRequestor.class);

	@Override
	public Map<String, Object> request(String tradeId, DataSource ds) {
		final String prefix = tradeId + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 普惠金融 人脸识别信息接口Begin {}", prefix, start);
		String url = propertyEngine.readById("phjr_face_url");
		if (StringUtil.isEmpty(url)) {
			url = "http://10.214.169.117:16014/loan-web-deploy/server/face.json";
			url = "https://app.wandaph.com/server/face.json";
		}
		//初始化对象
		Map<String, Object> rets = initRets();
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		//计费标签
		Set<String> tags = new HashSet<String>();
		tags.add(Conts.TAG_SYS_ERROR);
		//交易日志信息数据
		DataSourceLogVO logObj = buildLogObj(ds.getId(),url);
		PHUserInfo userInfo = null;
		try{
			String channel = propertyEngine.readById("phjr_channel");
			String publicKey = propertyEngine.readById("phjr_pub_key");
			if (StringUtil.isEmpty(channel)) {
				channel = "A9BC5F84C76E44518D41A323F059F0B2";
			}
			if (StringUtil.isEmpty(publicKey)) {
				publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCe4JuweoMJLVYe/37IvHsCtX4hygUz/mMCi28C3gEZYW3zzTUan1SBRV0fYWUJiPIHHdAuZ1pZBcYBGZUGTPL3TG84xDvKFAtEb0un6um8RFuHosv3Tbb/4422Swl5EqTh8OIobR2ZoXCJxGEfQsqwhY8NObRwoxiNksmmmVHWewIDAQAB";//RSA加密公钥
				
			}

			//获取入参
			logger.info("{} 开始解析传入的参数" , prefix);
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String iemi = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			String token = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
			String authId = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();
			String userId = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();
			String result = ParamUtil.findValue(ds.getParams_in(), paramIds[5]).toString();
			String time = ParamUtil.findValue(ds.getParams_in(), paramIds[6]).toString();
			String facePhoto1 = ParamUtil.findValue(ds.getParams_in(), paramIds[7]).toString();
			String facePhoto2 = ParamUtil.findValue(ds.getParams_in(), paramIds[8]).toString();
			String facePhoto3 = ParamUtil.findValue(ds.getParams_in(), paramIds[9]).toString();
			String fileType = ParamUtil.findValue(ds.getParams_in(), paramIds[10]).toString();
			String mhBusiNo = ParamUtil.findValue(ds.getParams_in(), "busi_no").toString();
			logger.info("{} 解析传入的参数成功" , prefix);
			//保存入参
			Map<String, Object> paramIn = buildParamIn(mobile,iemi,token,authId,userId,result,time,fileType);
			saveParamIn(paramIn, tradeId, logObj);
			//上传图片到swift
			String swiftId1 = fileService.upload(facePhoto1, FileType.JPEG, FileArea.DS, tradeId);
			String swiftId2 = fileService.upload(facePhoto2, FileType.JPEG, FileArea.DS, tradeId);
			String swiftId3 = fileService.upload(facePhoto3, FileType.JPEG, FileArea.DS, tradeId);
			//校验入参
			if(!(mobile.length() == 11 && StringUtil.isPositiveInt(mobile))){
				logger.info("{} 手机号码格式错误" , prefix);
				logObj.setState_msg("手机号码格式错误" + mobile);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
				return rets;
			}
			
			if (!StringUtil.isIntNumeric(time)) {
				logger.info("{} 人脸识别时间格式错误 {}" , prefix ,time);
				logObj.setState_msg("操作时间格式错误" + time);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID.getRet_msg());
				return rets;
			}
			
			boolean authRes = false;
			if ("1".equals(result)) {
				authRes = true;
			}
			
			
			Map<String, Object> busiObj = buildBusiObj(token,authId,userId,mobile,authRes,time);
			ReqBusiData busiData = buildBusiData(null,null,iemi,channel,busiObj);
			String dataJsonStr = JSONObject.toJSONString(busiData);
			logger.info("{} 请求原始参数为 {}" , prefix ,dataJsonStr);
			String uuidKey = UUID.randomUUID().toString().replace("-", "");
			logger.info("{} UUID为 {}", prefix , uuidKey);
			String rsaEncKey = RSAUtil.rsaEncrypt(uuidKey, publicKey);
			logger.info("{} UUID加密后为 {}", prefix , rsaEncKey);
			String data = AesUtils.encrypt2HexStr(dataJsonStr, uuidKey);
			logger.info("{} data加密后为  {}", prefix , data);
			String str2sign = buildStr2Sign(data,rsaEncKey,channel);
			String sign = MD5.ecodeByMD5(str2sign).toUpperCase();
			logger.info("{} 签名为  {}", prefix , sign);
			Map<String, String> params = new HashMap<String, String>();
			params.put(PH_HTTP_CHANNEL, channel);
			params.put(PH_HTTP_DATA, data);
			params.put(PH_HTTP_KEY, rsaEncKey);
			params.put(PH_HTTP_SIGN, sign);
			params.put(PH_HTTP_FACE1NAME, tradeId+"_1." + fileType);
			params.put(PH_HTTP_FACE1FILE, facePhoto1);
			params.put(PH_HTTP_FACE2NAME, tradeId+"_2." + fileType);
			params.put(PH_HTTP_FACE2FILE, facePhoto2);
			params.put(PH_HTTP_FACE3NAME, tradeId+"_3." + fileType);
			params.put(PH_HTTP_FACE3FILE, facePhoto3);
			
			long postStart = System.currentTimeMillis();
			String postResult = RequestHelper.doPost(url, params, isHttps());
			long postCost = System.currentTimeMillis() - postStart;
			logger.info("{} 请求普惠金融耗时时间为 {} ms" , prefix ,postCost);
			logger.info("{} http请求返回结果为 {}" , prefix , postResult);
			if (postCost >= 10000) {
				//http请求超过10S
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求普惠金融时间超过10秒");
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;
			}
			
			if (StringUtil.isEmpty(postResult)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("http请求结果为空");
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;
			}
			
			ResBodyBean resBodyBean = JSONObject.parseObject(postResult, ResBodyBean.class);
			if (resBodyBean == null) {
				logger.error("{} 普惠金融内容转Json为空 {}" , prefix , postResult);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;
			}
			retData.clear();
			retData.put(PH_HTTP_ERRCODE, resBodyBean.getErrCode());
			retData.put(PH_HTTP_ERRMSG, resBodyBean.getErrMsg());
			logObj.setState_msg(resBodyBean.getErrMsg());
			logObj.setBiz_code1(resBodyBean.getErrCode());
			if (resBodyBean.isSuccess()) {
				retData.put("status",true);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setState_msg("请求成功");
				tags.clear();
				tags.add(Conts.TAG_TST_SUCCESS);
				
				userInfo = buildUserInfo(tradeId,ds.getParams_in(),swiftId1,swiftId2,swiftId3,true);
			}else{
				userInfo = buildUserInfo(tradeId,ds.getParams_in(),swiftId1,swiftId2,swiftId3,false);
				retData.put("status",false);
				logger.info("{} 普惠金融返回交易失败 {}",prefix,resBodyBean);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg(postResult);
				logObj.setBiz_code1(resBodyBean.getErrCode());
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_SUCCESS.getRet_msg());
			rets.put(Conts.KEY_RET_DATA, retData);
		}catch(Exception e){
			logger.error("{} 处理异常 {}" , prefix ,e.getMessage());
		}finally{
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[tags.size()]));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(StringUtils.join(tags, ";"));
			long dsLogStart = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeLog(tradeId,logObj);
			if (userInfo != null) {
            	try {
					userInfoServie.add(userInfo);
				} catch (ServiceException addEx) {
					addEx.printStackTrace();
					logger.error("{} 保存数据异常 {}" , prefix ,addEx.getMessage());
				}
			}
			logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
		}
		
		
		return rets;
	}

	

	



	private PHUserInfo buildUserInfo(String tradeId, List<Param> params_in,
			String swiftId1, String swiftId2, String swiftId3, boolean b) {
		PHUserInfo userInfo = new PHUserInfo();
		Date nowTime = new Date();
		userInfo.setTrade_id(tradeId);
		userInfo.setCreate_date(nowTime);
		userInfo.setUpdate_date(nowTime);
		userInfo.setAuth_result(ParamUtil.findValue(params_in, "result").toString());
		userInfo.setAuth_timecost(ParamUtil.findValue(params_in, "time").toString());
		userInfo.setDevice_id(ParamUtil.findValue(params_in, "ieme").toString());
		userInfo.setIs_success(b+"");
		userInfo.setLogin_token(ParamUtil.findValue(params_in, "token").toString());
		userInfo.setOpertype("2");
		userInfo.setPass_id(ParamUtil.findValue(params_in, "authId").toString());
		userInfo.setPhoto1_fid(swiftId1);
		userInfo.setPhoto2_fid(swiftId2);
		userInfo.setPhoto3_fid(swiftId3);
		userInfo.setUser_id(ParamUtil.findValue(params_in, "userId").toString());
		return userInfo;
	}







	/**
	 * 构建业务字段信息
	 * @param mobile
	 * @return
	 */
	private Map<String, Object> buildBusiObj(String token, String authId,
			String userId, String mobile, boolean result, String time) {
		Map<String, Object> busiObjMap = new HashMap<String, Object>();
		busiObjMap.put(PH_TOKEN, token);
		busiObjMap.put(PH_AUTH_ID, authId);
		busiObjMap.put(PH_USER_ID, userId);
		busiObjMap.put(PH_MOBILE, mobile);
		busiObjMap.put(PH_RESULT, result);
		busiObjMap.put(PH_TIME,Integer.parseInt(time));
		return busiObjMap;
	}

	private Map<String, Object> buildParamIn(String mobile, String iemi, String token, String authId, String userId, String result, String time, String fileType) {
		Map<String, Object> paramIn = new HashMap<String, Object>();
		paramIn.put("mobile", mobile);
		paramIn.put("iemi", iemi);
		paramIn.put("token", token);
		paramIn.put("authId", authId);
		paramIn.put("userId", userId);
		paramIn.put("result", result);
		paramIn.put("time", time);
		paramIn.put("fileType", fileType);
		return paramIn;
	}

	
	
	
}
