/**   
* @Description: 请求数据源拍拍信 BASE Requestor 
* @author nan.liu  
* @date 2018年1月31日 下午3:22:56 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ppxin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
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
public class BasePPXDSRequestor extends BaseDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(BasePPXDSRequestor.class);
	
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
	//调用拍拍信返回结果
	public final static String PPX_RESP_CODE = "api.resp.sys#success";
	public final static String PPX_RESP_RESULT = "success";
	public final static String PPX_QUERY_STATUS = "3";
	public final static String PPX_CHINA_UNICOM = "UNICOM";
	
	public Map<String, String> inTimeMap = null;
	public Map<String, String> statusMap = null;
	public Map<String, String> checkResMap = null;
	
	public Map<String, String> tagFound1Map = null;
	public Map<String, String> tagFound2Map = null;

	public Map<String, String> tagCacheFound1Map = null;
    public Map<String, String> tagCacheFound2Map = null;
    
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
			if("0".equals(mobilePojo.getCheckResult())){
				mobileService.add(mobilePojo);
			}			
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

	public static String getMsg(String url) {  
        BufferedReader in = null;  
        try {  
            URL realUrl = new URL(url);  
            // 打开和URL之间的连接  
            URLConnection connection = realUrl.openConnection();  
            // 设置通用的请求属性    
            connection.setConnectTimeout(5000);  
            connection.setReadTimeout(5000);  
            // 建立实际的连接  
            connection.connect();  
            // 定义 BufferedReader输入流来读取URL的响应  
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));  
            StringBuffer sb = new StringBuffer();  
            String line;  
            while ((line = in.readLine()) != null) {  
                sb.append(line);  
            }  
            return sb.toString();  
        } catch (Exception e) {  
            e.getStackTrace();
        }  
        // 使用finally块来关闭输入流  
        finally {  
            try {  
                if (in != null) {  
                    in.close();  
                }  
            } catch (Exception e2) {  
                e2.printStackTrace();  
            }  
        }  
        return null;  
    } 
	public Map<String,String> tagCacheFound1Map(){
        if (tagCacheFound1Map == null) {
            tagCacheFound1Map = new HashMap<String, String>();
            tagCacheFound1Map.put("0","三维验证一致");
            tagCacheFound1Map.put("1","三维验证不一致");
            tagCacheFound1Map.put("2","手机号身份证号验证一致；手机号姓名验证不一致");
            tagCacheFound1Map.put("3","手机号身份证号验证不一致，手机号姓名验证一致");
			tagCacheFound1Map.put("99","手机号T-1月前已离网");
        }

        return tagCacheFound1Map;
    }

    public Map<String,String> tagCacheFound2Map(){
        if (tagCacheFound2Map == null) {
            tagCacheFound2Map = new HashMap<String, String>();
            tagCacheFound2Map.put("4", "手机号证件类型不匹配，不再进行验证");
            tagCacheFound2Map.put("5", "手机号身份证号验证一致；手机号姓名验证结果未知");
            tagCacheFound2Map.put("6", "手机号身份证号验证不一致；手机号姓名验证结果未知");
        }

        return tagCacheFound2Map;
    }
    protected GeoMobileCheck isIncache(String name, String cardNo, String mobile, long cacheTime, String prefix){
        GeoMobileCheck findGeoMobileCheck = mobileService.findGeoMobileCheck(name, cardNo, mobile);

        if(findGeoMobileCheck != null){
            if(StringUtils.isEmpty(findGeoMobileCheck.getCheckResult())){
                logger.info("{} 缓存中没有对应数据", prefix);
                return null;
            }
            long updateTime = findGeoMobileCheck.getUpdate_time().getTime();
            if(cacheTime > (System.currentTimeMillis() - updateTime) / 1000){
                return findGeoMobileCheck;
            }else {
                logger.info("{} 缓存中有数据，但不在缓存有效期", prefix);
                return null;
            }
        }else {
            logger.info("{} 缓存中没有对应数据", prefix);
            return null;
        }
    }
    public String bulidAllCacheTag(String attri, String checkResult){
        Map<String, String> found1Map = tagCacheFound1Map();
        Map<String, String> found2Map = tagCacheFound2Map();
        String resource_tag = Conts.TAG_TST_FAIL;
        StringBuffer tagBf = new StringBuffer();
        if(CHINA_MOBILE.equals(attri)){
            tagBf.append("check_yd_incache_");
        }else if(CHINA_UNICOM.equals(attri)){
            tagBf.append("check_lt_incache_");
        }else if(CHINA_TELECOM.equals(attri)){
            tagBf.append("check_dx_incache_");
        }else {
            tagBf.append("check_other_incache_");
        }
        if(found1Map.containsKey(checkResult)){
            tagBf.append("found1");
        }else if(found2Map.containsKey(checkResult)){
            tagBf.append("found2");
        }
        resource_tag = tagBf.toString();
        return resource_tag;
    }
}
