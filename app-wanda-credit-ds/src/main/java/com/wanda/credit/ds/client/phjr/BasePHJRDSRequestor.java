/**   
* @Description: 普惠金融接口基础类 
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:22:56 
* @version V1.0   
*/
package com.wanda.credit.ds.client.phjr;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.phjr.bean.ReqBusiData;
import com.wanda.credit.ds.dao.iface.phjr.IPHJRMobileRegisterService;
import com.wanda.credit.ds.dao.iface.phjr.IPHJRUserInfoService;

/**
 * @author xiaobin.hou
 *
 */
public class BasePHJRDSRequestor extends BaseDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(BasePHJRDSRequestor.class);
	
	@Autowired
	protected IPropertyEngine propertyEngine;
	@Autowired
	protected IExecutorFileService fileService;
	@Autowired
	protected IPHJRMobileRegisterService mobileRegisterService;
	@Autowired
	protected IPHJRUserInfoService	userInfoServie;
	
	protected final static String PH_MOBILE = "telephone";
	protected final static String PH_SMS_CODE = "authCheckCode";
	protected final static String PH_SMS_TYPE = "smsType";
	protected final static String PH_TOKEN ="token";
	protected final static String PH_AUTH_ID ="authId";
	protected final static String PH_USER_ID ="userId";
	protected final static String PH_NAME ="clientName";
	protected final static String PH_CARDNO_TYPE ="certificateKind";
	protected final static String PH_CARDNO ="certificateNo";
	protected final static String PH_CARDNO_ADDR ="certificateAddress";
	protected final static String PH_PUB_DATE ="publishDate";
	protected final static String PH_INVALID_DATE ="invalidDate";
	protected final static String PH_RESULT ="result";
	protected final static String PH_TIME ="time";
    protected final static String PH_USE_FLAG = "useFlag";
	
	
	protected final static String PH_HTTP_DATA = "data";
	protected final static String PH_HTTP_KEY = "key";
	protected final static String PH_HTTP_CHANNEL = "channel";
	protected final static String PH_HTTP_SIGN = "sign";
	protected final static String PH_HTTP_CORRECTNAME = "certificateFile";
	protected final static String PH_HTTP_CORRECTFILE= "certificateFileContent";
	protected final static String PH_HTTP_OPPOSITENAME = "certificateFileBack";
	protected final static String PH_HTTP_OPPOSITEFILE = "certificateFileBackContent";
	protected final static String PH_HTTP_FACE1NAME = "faceFile1";
	protected final static String PH_HTTP_FACE1FILE = "faceFile1Content";
	protected final static String PH_HTTP_FACE2NAME = "faceFile2";
	protected final static String PH_HTTP_FACE2FILE = "faceFile2Content";
	protected final static String PH_HTTP_FACE3NAME = "faceFile3";
	protected final static String PH_HTTP_FACE3FILE = "faceFile3Content";
	protected final static String PH_HTTP_ERRCODE = "errCode";
	protected final static String PH_HTTP_ERRMSG = "errMsg";
		
	protected boolean isHttps(){
		boolean isHttps = false;
		String httpsOrNo = propertyEngine.readById("pujr_isHttps");
		if ("1".equals(httpsOrNo)) {
			isHttps = true;
		}
		return isHttps;
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
	
	protected DataSourceLogVO buildLogObj(String dsId, String url) {
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(dsId);
		logObj.setReq_url(url);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		return logObj;
	}
	
	/**
	 * 
	 * @param paramIn
	 * @param trade_id
	 * @param logObj
	 * @return
	 */
	protected boolean saveParamIn(Map<String, Object> paramIn,String trade_id, DataSourceLogVO logObj) {
		boolean isSave = true;
		try {
			long start = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			logger.info("{} 保存请求参数成功,耗时为 {}", trade_id , System.currentTimeMillis() - start	);
		} catch (Exception e) {
			logger.info("{}保存入参信息异常 {}", trade_id, e.getMessage());
			isSave = false;
		}
		return isSave;
	}
	
	/**
	 * 
	 * @param serviceId
	 * @param busiNo
	 * @param iemi
	 * @param channel
	 * @param busiObj
	 * @param strs
	 * @return
	 */
	protected ReqBusiData buildBusiData(String serviceId, String busiNo,
			String iemi, String channel, Map<String, Object> busiObj,String... strs) {
		ReqBusiData busiData = new ReqBusiData();
		busiData.setServiceId(serviceId);
		busiData.setBusiNo(busiNo);
		busiData.setDeviceId(iemi);
		busiData.setTime(System.currentTimeMillis());
		busiData.setChannel(channel);
		busiData.setBusiObject(busiObj);
		return busiData;
	}
	
	protected String buildStr2Sign(String data, String rsaEncKey, String channel) {
		StringBuffer bf = new StringBuffer();
		bf.append(PH_HTTP_CHANNEL).append("=").append(channel)
			.append("&").append(PH_HTTP_DATA).append("=").append(data)
			.append("&").append(PH_HTTP_KEY).append("=").append(rsaEncKey);
		return bf.toString();
	}
}
