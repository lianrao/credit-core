package com.wanda.credit.ds.client.qianhai;

import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.util.RandomUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.IQHLogService;

public abstract class BaseQHDataSourceRequestor extends BaseDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(BaseQHDataSourceRequestor.class);

	// 前海成功返回码
	protected final String SUCC_CODE = "E000000";
	// 默认证件类型:身份证
	protected final String DEFAULT_IDTYPE = "0";
	protected final String TRADE_ID = "trade_id";
	protected final String NAME = "name";
	protected final String IDNO = "idNo";

	private DateFormat defaultDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/** 查询原因 默认个人查询 */
	private String reasonCode = "04";

	
	/** 证件类型 默认身份证 */
	protected String idType = "0";
	
	@Autowired
	protected IQHLogService qhlogService;

	/*
	 * protected String url =
	 * "https://test-qhzx.pingan.com.cn:5443/do/dmz/query/blacklist/v1/MSC8004";
	 * protected String key = "SK803@!QLF-D25WEDA5E52DA";
	 */
	// 公私钥的路径等信息

	private static String privateKeyPath = "/depends/ds/qianhai/credoo_glad.jks";
	private static String privateKeyPasswd = "glad2321";
	private static String privateKeyAlias = "gladKey";
	private static String publicKeyPath = "/depends/ds/qianhai/credoo_glad.cer";

	protected String url;
	protected String key;

	// 公私钥
	protected static PrivateKey privateKey;
	protected static PublicKey publicKey;

	/**
	 * 初始化连接资源
	 * 
	 * @throws Exception
	 */
	public static void init() throws Exception {
		if (privateKey == null) {
			privateKey = QHDataSourceUtils.getPrivateKey(getResPathFromCP(privateKeyPath), privateKeyPasswd, privateKeyAlias);
		}
		if (publicKey == null) {
			publicKey = QHDataSourceUtils.getPublicKey(getResPathFromCP(publicKeyPath));
		}
	}

	protected Map<String, Object> executeClient(Map<String, Object> context) throws Exception {
		Map<String, Object> retMap = postHttpsRequest(context);
		return retMap;
	}

	private Map<String, Object> postHttpsRequest(Map<String, Object> context) throws Exception {
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> requestData = new HashMap<String, Object>();

		/** 拼装header信息 */
		requestData.put("header", buildRequestHeader((String) context.get(TRADE_ID)));

		/** 拼装body信息 */
		Map<String, Object> bodyData = buildRequestBody(context);
		// 转化业务数据到JSON对象
		JSONObject bodyJsn = JSONObject.fromObject(bodyData);
		logger.info("{} 调用接口入参信息:{}", context.get(TRADE_ID), JSON.toJSONString(bodyJsn));
		// 加密业务数据
		String encbodyJsn = QHDataSourceUtils.encrypt(bodyJsn.toString().getBytes(), key);
		requestData.put("busiData", encbodyJsn);

		/** 拼装安全信息 */
		// 得到签名信息
		String signature = QHDataSourceUtils.signData(privateKey, encbodyJsn);
		requestData.put("securityInfo", buildRequestTail(signature));

		StringBuffer sb = new StringBuffer();
		// logger.info("{} QH-reqDataJsn>>>{}", context.get(TRADE_ID),
		// sb.append("{\"header\":").append(requestData.get("header")).append("}").append(",\"busiData\":").append(bodyJsn.toString()).append(",\"securityInfo\":").append(requestData.get("securityInfo")).append("}").toString());

		/** 发送https请求并等待相应 */
		JSONObject reqJsn = JSONObject.fromObject(requestData);

		// logger.info("{} QH-reqDataJsn[encrypted]>>>{}",
		// context.get(TRADE_ID), reqJsn.toString());
		DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(context.get(TRADE_ID).toString(), reqJsn.toString(), new String[] { context.get(TRADE_ID).toString() }));
		long start = System.currentTimeMillis();
		String rspStr = QHDataSourceUtils.sendJsonWithHttps(url, reqJsn.toString());
		logger.info("{} 调用接口耗时 {} ms", context.get(TRADE_ID), System.currentTimeMillis() - start);

		logger.info("{} 前海返回信息:{}", context.get(TRADE_ID), rspStr);
		DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(context.get(TRADE_ID).toString(), rspStr.toString(), new String[] { context.get(TRADE_ID).toString() }));
		JSONObject rspJsn = JSONObject.fromObject(rspStr);
		if (!rspJsn.containsKey("header")) {
			throw new QHDataSourceException("返回数据异常[没有header节点]");
		}

		JSONObject resltJsn = (JSONObject) rspJsn.get("header");
		String rtCode = resltJsn.getString("rtCode");

		if (resltJsn != null && SUCC_CODE.equals(rtCode)) {
			/** 签名验证 */
			QHDataSourceUtils.verifyData(publicKey, rspJsn.getString("busiData"), rspJsn.getJSONObject("securityInfo").getString("signatureValue"));

			String busiData = QHDataSourceUtils.decrypt(rspJsn.getString("busiData"), key);
//			logger.info("{} QH-rspBusiDataJsn>>>{}", context.get(TRADE_ID), busiData);
			// 设置返回值
			retMap.put("busiJsn", JSONObject.fromObject(busiData));
		}

		retMap.put("resltJsn", resltJsn);
		return retMap;

	}

	/**
	 * 构建请求头信息
	 * */
	protected Map<String, Object> buildRequestHeader(String trade_id) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("transNo", trade_id);
		data.put("transDate", date2Str(new Date()));
		data.put("orgCode", this.getOrgCode());
		data.put("chnlId", this.getChnlId());
		data.put("authCode", this.getAuthCode());
		data.put("authDate", this.getAuthDate());
		return data;
	}

	/**
	 * 构建业务数据信息
	 * */
	protected abstract Map<String, Object> buildRequestBody(Map<String, Object> data);

	/**
	 * 构建结尾的安全信息
	 * */
	protected Map<String, Object> buildRequestTail(String signature) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("signatureValue", signature);
		data.put("userName", getUserName());
		data.put("userPassword", QHDataSourceUtils.digest(getUserPassword().getBytes()));
		return data;
	}

	/**
	 * 用'yyyy-MM-dd HH:mm:ss' 格式日期
	 * */
	protected String date2Str(Date date) {
		if (date == null) {
			return defaultDateFormatter.format(new Date());
		} else {
			return defaultDateFormatter.format(date);
		}
	}

	/**
	 * 生产批号 随机生成
	 * */
	protected String buildBatchNo() {
		String randomStr = RandomUtil.random(10);
		return randomStr;
	}

	/**
	 * 授权码 随机生成
	 * */
	protected String buildEntityAuthCode() {
		String randomStr = RandomUtil.random(5);
		return randomStr;
	}

	/** 前海提供的机构代码 */
	private String orgCode;

	/** 前海提供的机构代码 */
	private String chnlId;

	/** 前海提供的机构授权码 */
	private String authCode;

	/** 前海提供的机构授权时间 */
	private String authDate;

	/** 前海提供的机构用户名信息 */
	private String userName;

	/** 前海提供的密码信息 */
	private String userPassword;

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getChnlId() {
		return chnlId;
	}

	public void setChnlId(String chnlId) {
		this.chnlId = chnlId;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAuthDate() {
		return authDate;
	}

	public void setAuthDate(String authDate) {
		this.authDate = authDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		BaseQHDataSourceRequestor.privateKeyPath = privateKeyPath;
	}

	public String getPublicKeyPath() {
		return publicKeyPath;
	}

	public void setPublicKeyPath(String publicKeyPath) {
		BaseQHDataSourceRequestor.publicKeyPath = publicKeyPath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPrivateKeyPasswd() {
		return privateKeyPasswd;
	}

	public void setPrivateKeyPasswd(String privateKeyPasswd) {
		BaseQHDataSourceRequestor.privateKeyPasswd = privateKeyPasswd;
	}

	public String getPrivateKeyAlias() {
		return privateKeyAlias;
	}

	public void setPrivateKeyAlias(String privateKeyAlias) {
		BaseQHDataSourceRequestor.privateKeyAlias = privateKeyAlias;
	}

	/**
	 * 判断ex异常是否是超时异常：SocketTimeoutException
	 * */
	protected boolean isTimeoutException(Exception ex) {
		if (ex == null)
			return false;
		String exeMsg = ex.getMessage();
		if (exeMsg != null && exeMsg.toLowerCase().indexOf("sockettimeout") > -1) {
			return true;
		}
		exeMsg = ex.toString();
		if (exeMsg != null && exeMsg.toLowerCase().indexOf("sockettimeout") > -1) {
			return true;
		}
		return false;
	}

	public static String getResPathFromCP(String relatedPath) {
		URL url = BaseQHDataSourceRequestor.class.getResource(relatedPath);
		if (url == null) {
			throw new RuntimeException("资源文件[" + relatedPath + "]不存在");
		}
		return url.getPath();
	}
	
	
	protected String buildTagFromQHDslog(String ds_id,String encryCardNo) 
			throws Exception{
		int count = qhlogService.queryLogOnCurrMonth(ds_id, encryCardNo);
		if(count > 0){
			return Conts.TAG_FOUND_OLDRECORDS;
		}
		return Conts.TAG_FOUND_NEWRECORDS;
	}

}