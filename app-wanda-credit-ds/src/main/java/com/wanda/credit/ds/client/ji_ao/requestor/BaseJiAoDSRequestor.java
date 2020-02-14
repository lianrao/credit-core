/**   
* @Description: 请求数据源 集奥 BASE Requestor 
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:22:56 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ji_ao.requestor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.ji_ao.bean.BigDataLogin;
import com.wanda.credit.ds.client.ji_ao.bean.MobileInfo;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ji_ao.bean.WSJiAoReqBean;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;

/**
 * @author xiaobin.hou
 *
 */
public class BaseJiAoDSRequestor extends BaseDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(BaseJiAoDSRequestor.class);
	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IJiAoMobileCheckService mobileService;
	//号码归属地-省
	public final static String PROVICE = "province";
	//号码归属地-市
	public final static String CITY = "city";
	//号码所属运营商-中文
	public final static String ATTRIBUTE = "attribute";
	//号码所属运营商-英文缩写
	public final static String ATTRIBUTE_EN = "attribute_en";
	//三维验证结果
	public final static String CHECK_RESULT = "checkResult";
	//在网时长
	public final static String MOBILE_IN_TIME = "inTime";
	//手机号码状态
	public final static String MOBILE_STATUS = "mobileState";
	//常量-三大运营商缩写
	public final static String CHINA_MOBILE = "CMCC";
	public final static String CHINA_UNICOM = "CUCC";
	public final static String CHINA_TELECOM = "CTCC";
	public final static String CHINA_OTHERS = "others";
	
	public Map<String, String> inTimeMap = null;
	public Map<String, String> statusMap = null;
	public Map<String, String> checkResMap = null;
	
	public Map<String, String> tagFound1Map = null;
	public Map<String, String> tagFound2Map = null;

	protected String loginUser;
	protected String loginPaw;
	protected String method;
	
	public WSJiAoReqBean buildReqBean(String name,String cardNo,String mobile,String type){
		
		loginUser = propertyEngine.readById("ds_jiAo_mobile_user");
		loginPaw = propertyEngine.readById("ds_jiAo_mobile_pwd");
		method = propertyEngine.readById("ds_jiAo_method");
		
		WSJiAoReqBean reqBean = new WSJiAoReqBean();
		
		MobileInfo info = new MobileInfo();
		info.setIdno(cardNo);
		info.setName(name);
		info.setMobile(mobile);
		info.setInnerIfType(type);
		
		BigDataLogin login = new BigDataLogin();
		login.setPassword(loginPaw);
		login.setUsername(loginUser);
		
		reqBean.setMethod(method);
		reqBean.setParams(info);
		reqBean.setLogin(login);

		return reqBean;
	}
	
	/**
	 * @param name
	 * @param enccardNo
	 * @param encMobile
	 * @param location
	 * @param retData
	 * @return
	 */
	protected GeoMobileCheck parseToSave(String tradeId,String name, String enccardNo,
			String encMobile, MobileLocation location,
			TreeMap<String, Object> retData) {
		GeoMobileCheck mobilePojo = new GeoMobileCheck();
		Date nowTime = new Date();
		try{
			mobilePojo.setTrade_id(tradeId);
			mobilePojo.setName(name);
			mobilePojo.setCardNo(enccardNo);
			mobilePojo.setMobileNo(encMobile);
			if (location != null) {
				mobilePojo.setProvince(location.getProvince());
				mobilePojo.setCity(location.getCity());
				mobilePojo.setAttribute(location.getIsp());
			}
			
			if (retData.containsKey(CHECK_RESULT)) {
				mobilePojo.setCheckResult(retData.get(CHECK_RESULT).toString());
			}
			if (retData.containsKey(MOBILE_IN_TIME)) {
				mobilePojo.setIntime(retData.get(MOBILE_IN_TIME).toString());
			}
			if (retData.containsKey(MOBILE_STATUS)) {
				mobilePojo.setMobileState(retData.get(MOBILE_STATUS).toString());
			}
			mobilePojo.setCreate_time(nowTime);
			mobilePojo.setUpdate_time(nowTime);
			mobileService.add(mobilePojo);
		}catch(Exception e){
			logger.info("{} 信息保存数据库异常" , tradeId);
		}
		
		return mobilePojo;
	}
	
	
	/**
	 * @param personName
	 * @param enccardNo
	 * @param encMobile
	 * @return
	 */
	protected boolean saveParamIn(String name, String cardNo, String mobile,
			String getInTime, String getStatus,
			String trade_id, DataSourceLogVO logObj) {
		boolean isSave = true;
		try {
			Map<String, Object> paramIn = new HashMap<String, Object>();
			paramIn.put("name", name);
			paramIn.put("cardNo", cardNo);
			paramIn.put("mobile", mobile);
			paramIn.put("getInTime", getInTime);
			paramIn.put("getStatus", getStatus);
			long start = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			logger.info("{} 保存请求参数成功,耗时为 {}", trade_id , System.currentTimeMillis() - start	);
		} catch (Exception e) {
			logger.info("{}保存入参信息异常{}", trade_id, e.getMessage());
			isSave = false;
		}

		return isSave;
	}
	/**
	 * 初始化数据源返回的初始化对象 Map<String,Object>
	 * @return
	 */
	protected Map<String, Object> initRets(){
		
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "交易失败");
		
		return rets;
		
	}
	
	/**
	 * @param location
	 * @param retData
	 * @return
	 */
	protected TreeMap<String, Object> parseLocatin(MobileLocation location,
			TreeMap<String, Object> retData) {

		if (location != null) {
			retData.put(PROVICE, location.getProvince());
			retData.put(CITY, location.getCity());
			String attribute = location.getIsp();
			retData.put(ATTRIBUTE, attribute);
			
			if (attribute != null && attribute.trim().length() > 0) {
				if (attribute.contains("移动")) {
					retData.put(ATTRIBUTE_EN, CHINA_MOBILE);
				}else if(attribute.contains("联通")){
					retData.put(ATTRIBUTE_EN, CHINA_UNICOM);
				}else if(attribute.contains("电信")){
					retData.put(ATTRIBUTE_EN, CHINA_TELECOM);
				}else{
					retData.put(ATTRIBUTE_EN, CHINA_OTHERS);
				}
			}else{
				retData.put(ATTRIBUTE_EN, CHINA_OTHERS);
			}
		}else{
			retData.put(PROVICE, "");
			retData.put(CITY, "");
			retData.put(ATTRIBUTE, "");
			retData.put(ATTRIBUTE_EN, CHINA_OTHERS);
		}
		return retData;
	}
	
	public boolean doPrint(String testFlag) {

		if ("1".equals(testFlag)) {
			return true;
		}

		return false;

	}
	
	public Map<String,String> getInTimeMap(){
		
		if (inTimeMap == null) {
			inTimeMap = new HashMap<String, String>();
			inTimeMap.put("03", "0");
			inTimeMap.put("04", "1");
			inTimeMap.put("1", "2");
			inTimeMap.put("2", "3");
			inTimeMap.put("3", "4");
		}
		
		return inTimeMap;
	}
	
	public Map<String,String> getStatusMap(){
		
		if (statusMap == null) {
			statusMap = new HashMap<String, String>();
			statusMap.put("0", "0");
			statusMap.put("1", "1");
			statusMap.put("2", "2");
			statusMap.put("3", "3");
			statusMap.put("4", "4");
		}
		
		return statusMap;
	}
	
	public Map<String, String> getCheckResMap(){
		
		if (checkResMap == null) {
			checkResMap = new HashMap<String, String>();
			checkResMap.put("0", "0");
			checkResMap.put("1", "1");
			checkResMap.put("4", "2");
			checkResMap.put("5", "3");
			checkResMap.put("6", "4");
			checkResMap.put("2", "5");
			checkResMap.put("3", "6");
//			checkResMap.put("7", "");
		}
		
		return checkResMap;
	}
	
	public Map<String,String> tagFound1Map(){
		if (tagFound1Map == null) {
			tagFound1Map = new HashMap<String, String>();
			tagFound1Map.put("0","三维验证一致");
			tagFound1Map.put("1","三维验证不一致");
			tagFound1Map.put("4","手机号身份证号验证一致；手机号姓名验证不一致");
			tagFound1Map.put("5","手机号身份证号验证不一致，手机号姓名验证一致");
		}
		
		return tagFound1Map;
	}
	
	public Map<String,String> tagFound2Map(){
		if (tagFound2Map == null) {
			tagFound2Map = new HashMap<String, String>();
			tagFound2Map.put("6", "手机号证件类型不匹配，不再进行验证");
			tagFound2Map.put("2", "手机号身份证号验证一致；手机号姓名验证结果未知");
			tagFound2Map.put("3", "手机号身份证号验证不一致；手机号姓名验证结果未知");
		}
		
		return tagFound2Map;
	}


//	public void setLoginUser(String loginUser) {
//		this.loginUser = loginUser;
//	}
//
//
//	public void setLoginPaw(String loginPaw) {
//		this.loginPaw = loginPaw;
//	}
//
//
//	public void setMethod(String method) {
//		this.method = method;
//	}

}
