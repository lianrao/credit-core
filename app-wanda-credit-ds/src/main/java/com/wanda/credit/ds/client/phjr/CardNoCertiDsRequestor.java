/**   
* @Description: 手机三维验证 集奥数据源
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.phjr;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.google.gson.Gson;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.MD5;
// import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
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
@DataSourceClass(bindingDataSourceId="ds_phjr_CardNoAuth")
public class CardNoCertiDsRequestor extends BasePHJRDSRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(CardNoCertiDsRequestor.class);

	@Override
	public Map<String, Object> request(String tradeId, DataSource ds) {
		final String prefix = tradeId + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 手机三要素验证-网数-集奥Begin {}", prefix, start);
		String url = propertyEngine.readById("phjr_IDAuth_url");
		if (StringUtil.isEmpty(url)) {
			url = "http://10.214.169.117:16014/loan-web-deploy/server/certificate.json";
			url = "https://app.wandaph.com/server/certificate.json";
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
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[5]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[6]).toString();
			String cardNoAddress = ParamUtil.findValue(ds.getParams_in(), paramIds[7]).toString();
			String publishDate = ParamUtil.findValue(ds.getParams_in(), paramIds[8]).toString();//Nov 28, 2017 4:49:38 PM
			String invalidDate = ParamUtil.findValue(ds.getParams_in(), paramIds[9]).toString();
			String correctSide = ParamUtil.findValue(ds.getParams_in(), paramIds[10]).toString();
			String oppositeSide = ParamUtil.findValue(ds.getParams_in(), paramIds[11]).toString();
			String fileType = ParamUtil.findValue(ds.getParams_in(), paramIds[12]).toString();
			String mhBusiNo = ParamUtil.findValue(ds.getParams_in(), "busi_no").toString();
			logger.info("{} 解析传入的参数成功" , prefix);
			//保存入参
			Map<String, Object> paramIn = buildParamIn(mobile);
			saveParamIn(paramIn, tradeId, logObj);
			String correctId = fileService.upload(correctSide, FileType.JPEG, FileArea.DS, tradeId);
			String oppositeId = fileService.upload(correctSide, FileType.JPEG, FileArea.DS, tradeId);
			//校验入参
			String validate = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(validate)) {
				logger.info("{} 身份证格式错误 {}" , prefix , validate);
				logObj.setState_msg("身份证格式错误" + cardNo);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
				return rets;
			}
			if(!(mobile.length() == 11 && StringUtil.isPositiveInt(mobile))){
				logger.info("{} 手机号码格式错误" , prefix);
				logObj.setState_msg("手机号码格式错误" + mobile);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
				return rets;
			}
			
			//构建业务参数
			Map<String, Object> busiObj = buildBusiObj(token, authId, userId,
					mobile, name, cardNo, cardNoAddress, changeDateFormat(publishDate,prefix),
					changeDateFormat(invalidDate,prefix));
			ReqBusiData busiData = buildBusiData(null,null,iemi,channel,busiObj);
			Gson gson = new Gson();
			String dataJsonStr = gson.toJson(busiData);
//			String dataJsonStr = JSONObject.toJSONString(busiData);
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
			params.put(PH_HTTP_CORRECTNAME, tradeId +"_1."+ fileType);
			params.put(PH_HTTP_CORRECTFILE, correctSide);
			params.put(PH_HTTP_OPPOSITENAME, tradeId +"_2."+ fileType);
			params.put(PH_HTTP_OPPOSITEFILE, oppositeSide);
			long postStart = System.currentTimeMillis();
			logger.info("{} 请求URL地址为 {}" , prefix,url);
			String postResult = RequestHelper.doPost(url, params, isHttps());
//			String postResult = RequestHelper.doPost(url, null, null, params, null, false);
// 			Map<String, Object> resMap = RequestHelper.doPostRetFull(url, null, null, params, null, null, false);
// 			String postResult = "";
// 			Object resObj = resMap.get(RequestHelper.HTTP_RES_BODYSTR);
// 			postResult = (resObj == null ? "" : resObj.toString());
// 			logger.info("{} http请求返回状态吗为 {}" , prefix , resMap.get(RequestHelper.HTTP_RES_CODE));
			long postCost = System.currentTimeMillis() - postStart;
			logger.info("{} 请求普惠金融耗时时间为 {} ms" , prefix ,postCost);
			logger.info("{} http请求返回信息为 {}" , prefix , postResult);
			
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
				retData.put("status", true);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setState_msg("请求成功");
				tags.clear();
				tags.add(Conts.TAG_TST_SUCCESS);
				
				userInfo = buildUserInfo(tradeId,ds.getParams_in(),correctId,oppositeId,true);
			}else{
				userInfo = buildUserInfo(tradeId,ds.getParams_in(),correctId,oppositeId,false);
				retData.put("status", false);
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
			String correctId, String oppositeId, boolean b) {
//		mobile,ieme,token,authId,userId,name,cardNo,cardNoAddress,
//		publishDate,invalidDate,correctSide,oppositeSide,fileType
		PHUserInfo userInfo = new PHUserInfo();
		Date nowTime = new Date();
		userInfo.setTrade_id(tradeId);
		userInfo.setCreate_date(nowTime);
		userInfo.setUpdate_date(nowTime);
		userInfo.setCardno(CryptUtil.encrypt(ParamUtil.findValue(params_in, "cardNo").toString()));
		userInfo.setCardtype("0");
		userInfo.setDevice_id(ParamUtil.findValue(params_in, "ieme").toString());
		userInfo.setIdcard_address(ParamUtil.findValue(params_in, "cardNoAddress").toString());
		userInfo.setIs_success(b + "");
		userInfo.setLogin_token(ParamUtil.findValue(params_in, "token").toString());
		userInfo.setMh_busno(ParamUtil.findValue(params_in, "busi_no").toString());
		userInfo.setName(ParamUtil.findValue(params_in, "name").toString());
		userInfo.setOpertype("1");
		userInfo.setPass_id(ParamUtil.findValue(params_in, "authId").toString());
		userInfo.setPhoto1_fid(correctId);
		userInfo.setPhoto2_fid(oppositeId);
		userInfo.setUser_id(ParamUtil.findValue(params_in, "userId").toString());
		userInfo.setValid_end(ParamUtil.findValue(params_in, "invalidDate").toString());
		userInfo.setValid_start(ParamUtil.findValue(params_in, "publishDate").toString());
		return userInfo;
	}







	private Date changeDateFormat(String date,String prefix) {
		if (date == null) {
			return new Date();
		}
		if (date.contains("/")) {
			date = date.replace("/", "");
		}
		if (date.contains(".")) {
			date = date.replace(".", "");
		}
		if (date.contains("-")) {
			date = date.replace("-", "");
		}
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			return format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			logger.info("{} 日期格式转化异常 {}" , prefix ,date);
		}
		return new Date();
	}







	/**
	 * 构建业务字段信息
	 * @param mobile
	 * @return
	 */
	private Map<String, Object> buildBusiObj(String token, String authId,
			String userId, String mobile, String name, String cardNo,
			String cardNoAddress, Date publishDate, Date invalidDate) {
		Map<String, Object> busiObjMap = new HashMap<String, Object>();
		busiObjMap.put(PH_TOKEN, token);
		busiObjMap.put(PH_AUTH_ID, authId);
		busiObjMap.put(PH_USER_ID, userId);
		busiObjMap.put(PH_MOBILE, mobile);
		busiObjMap.put(PH_NAME, name);
		busiObjMap.put(PH_CARDNO_TYPE, "0");
		busiObjMap.put(PH_CARDNO, cardNo);
		busiObjMap.put(PH_CARDNO_ADDR, cardNoAddress);
		busiObjMap.put(PH_PUB_DATE, publishDate);
		busiObjMap.put(PH_INVALID_DATE, invalidDate);
		return busiObjMap;
	}

	private Map<String, Object> buildParamIn(String mobile) {
		Map<String, Object> paramIn = new HashMap<String, Object>();
		paramIn.put("mobile", mobile);
		return paramIn;
	}

	
	
	
}
