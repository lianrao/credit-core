/**   
* @Description: 请求数据源 集奥 BASE Requestor 
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:22:56 
* @version V1.0   
*/
package com.wanda.credit.ds.client.tianyi;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.wangshu.WDWangShuTokenService;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author xiaobin.hou
 *
 */
public class BaseTianyiDSRequestor extends BaseDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(BaseTianyiDSRequestor.class);
	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IJiAoMobileCheckService mobileService;
	@Autowired
	WDWangShuTokenService tokenService;
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
	
	public Map<String,Object> doRequest(String url,Map<String, String> params, String prefix,boolean force,boolean doPrint) throws Exception {
		Map<String,String> header = new HashMap<String,String>();
		String token = getToken(false, prefix);
		header.put("X-Access-Token",token);
		/**
		 * url,参数,Header,是否打印,requestConfig,字符集
		 */
		long postStart = System.currentTimeMillis();
		Map<String, Object> doGetRetFull = RequestHelper.doGetRetFull(url, params, header,doPrint, null, null);
		Integer firStauts = (Integer)doGetRetFull.get(RequestHelper.HTTP_RES_CODE);
		if (firStauts != 200) {
			logger.info("{} DMP返回异常第二次请求，第一次请求耗时 {}" , prefix , System.currentTimeMillis() - postStart);
			logger.info("{} http第一次请求结果为：{}",prefix , doGetRetFull);
			doGetRetFull.clear();
			token = getToken(true, prefix);
			header.put("X-Access-Token",token);
			doGetRetFull = RequestHelper.doGetRetFull(url, params, header,doPrint, null, null);
		}
		return doGetRetFull;
	}
	
	public String getToken(boolean force,String prefix) throws Exception{
		logger.info("{} 获取token信息，是否强制更新：{}" , prefix , force);
		if(force){
			logger.info("{} 强制刷新token",prefix);
			tokenService.setToken(tokenService.getNewToken());
		}else if(tokenService.getToken() == null){
			tokenService.setToken(tokenService.getNewToken());
		}
		
		return tokenService.getToken();
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
			String attribute = "电信";
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
			retData.put(ATTRIBUTE, "电信");
			retData.put(ATTRIBUTE_EN, CHINA_TELECOM);
		}
		return retData;
	}
	
	public boolean doPrint(String testFlag) {

		if ("1".equals(testFlag)) {
			return true;
		}

		return false;

	}
	
	public Map<String, String> getCheckResMap(){
		if (checkResMap == null) {
			checkResMap = new HashMap<String, String>();
			checkResMap.put("1", "0");
			checkResMap.put("2", "1");
			checkResMap.put("3", "2");
			checkResMap.put("4", "3");
            checkResMap.put("90002", "8");
		}
		return checkResMap;
	}

    public GeoMobileCheck getGeoMobileCheck(String name, String cardNo, String mobile){
        return mobileService.findGeoMobileCheck(name, cardNo, mobile);
    }

    public Map<String, String> getStatusResMap(){
        if (statusMap == null) {
            statusMap = new HashMap<String, String>();
            statusMap.put("1", "0");
            statusMap.put("2", "3");
            statusMap.put("3", "1");
            statusMap.put("4", "2");
            statusMap.put("700100", "5");
        }
        return statusMap;
    }

    public Map<String, String> getTimeResMap(){
        if (inTimeMap == null) {
            inTimeMap = new HashMap<String, String>();
            inTimeMap.put("03", "0");
            inTimeMap.put("06", "1");
            inTimeMap.put("12", "2");
            inTimeMap.put("24", "3");
            inTimeMap.put("99", "4");
            inTimeMap.put("700100", "5");
        }
        return inTimeMap;
    }
}
