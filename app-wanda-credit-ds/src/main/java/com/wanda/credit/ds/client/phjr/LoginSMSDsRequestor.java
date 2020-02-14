/**   
* @Description: 验证短信验证码 
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.phjr;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.MD5;
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
@DataSourceClass(bindingDataSourceId="ds_phjr_smsAuth")
public class LoginSMSDsRequestor extends BasePHJRDSRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(LoginSMSDsRequestor.class);

	@Override
	public Map<String, Object> request(String tradeId, DataSource ds) {
		final String prefix = tradeId + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 普惠金融验证短信验证码 Begin {}", prefix, start);
		String url = propertyEngine.readById("phjr_login_url");
		if (StringUtil.isEmpty(url)) {
			url = "http://10.214.169.117:16014/loan-web-deploy/server/dojson.json";
			url = "https://app.wandaph.com/server/dojson.json";
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
			String serviceId = "loginSmsService";//服务接口ID 普惠金融提供
			String busiNo = "6000141"; //交易码 普惠金融提供			
			
			//获取入参
			logger.info("{} 开始解析传入的参数" , prefix);
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String iemi = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			String authCheckCode = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
			String mhBusiNo = ParamUtil.findValue(ds.getParams_in(), "busi_no").toString();
			logger.info("{} 解析传入的参数成功" , prefix);
			//保存入参
			Map<String, Object> paramIn = buildParamIn(mobile);
			saveParamIn(paramIn, tradeId, logObj);
			//校验入参
			if(!(mobile.length() == 11 && StringUtil.isPositiveInt(mobile))){
				logger.info("{} 手机号码格式错误" , prefix);
				logObj.setState_msg("手机号码格式错误" + mobile);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
				return rets;
			}
			
			Map<String, Object> busiObj = buildBusiObj(mobile,authCheckCode);
			ReqBusiData busiData = buildBusiData(serviceId,busiNo,iemi,channel,busiObj);
			logger.info("{} 构建BusiObj成功 {}", prefix , busiObj);
			String dataJsonStr = JSONObject.toJSONString(busiData);
			logger.info("{} 请求数据json报文为 {}", prefix , dataJsonStr);
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
			
			long postStart = System.currentTimeMillis();
			logger.info("{} 请求URL信息地址为  {}" , prefix , url);
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
				String resData = resBodyBean.getData();
				Map parseObject = JSONObject.parseObject(resData, Map.class);
				retData.putAll(parseObject);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setState_msg("请求成功");
				
				userInfo = buildUserInfo(tradeId,mobile,mhBusiNo,iemi,true,parseObject);
				
				tags.clear();
				tags.add(Conts.TAG_TST_SUCCESS);
			}else{
				userInfo = buildUserInfo(tradeId,mobile,busiNo,iemi,false,null);
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

	

	



	private PHUserInfo buildUserInfo(String tradeId, String mobile,
			String busiNo, String iemi, boolean isSuccess, Map resMap) {
		Date nowTime = new Date();
		PHUserInfo userInfo = new PHUserInfo();
		userInfo.setTrade_id(tradeId);
		userInfo.setMh_busno(busiNo);
		userInfo.setMobile(CryptUtil.encrypt(mobile));
		userInfo.setDevice_id(iemi);
		userInfo.setIs_success("false");
		userInfo.setCreate_date(nowTime);
		userInfo.setUpdate_date(nowTime);
		userInfo.setOpertype("0");
		if (isSuccess) {
			userInfo.setIs_success("token");
			if (resMap.containsKey("token")) {
				userInfo.setLogin_token(resMap.get("token").toString());
			}
			if (resMap.containsKey("authId")) {
				userInfo.setPass_id(resMap.get("authId").toString());
			}
			if (resMap.containsKey("userId")) {
				userInfo.setUser_id(resMap.get("userId").toString());
			}
		}
		return userInfo;
	}







	/**
	 * 构建业务字段信息
	 * @param mobile
	 * @return
	 */
	private Map<String, Object> buildBusiObj(String mobile,String authCheckCode) {
		Map<String, Object> busiObjMap = new HashMap<String, Object>();
		busiObjMap.put(PH_MOBILE, mobile);
		busiObjMap.put(PH_SMS_TYPE, "0");
		busiObjMap.put(PH_SMS_CODE, authCheckCode);
		return busiObjMap;
	}

	private Map<String, Object> buildParamIn(String mobile) {
		Map<String, Object> paramIn = new HashMap<String, Object>();
		paramIn.put("mobile", mobile);
		return paramIn;
	}

	
	
	
}
