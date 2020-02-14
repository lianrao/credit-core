package com.wanda.credit.ds.client.guoztCredit;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.ds.client.wangshu.BaseWDWangShuDataSourceRequestor;
import com.wanda.credit.ds.client.wangshu.WDWangShuTokenService;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.id5.gboss.GbossClient;
import cn.id5.gboss.GbossConfig;
import cn.id5.gboss.http.HttpResponseData;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.util.DESUtils;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseGuoZTDataSourcesRequestor extends BaseWDWangShuDataSourceRequestor {
	private final static Logger logger = LoggerFactory
			.getLogger(BaseGuoZTDataSourcesRequestor.class);
	protected static int timeout = Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.httpEdu.timeout"));
	protected String guoztUrl;
	protected String userId;//wdcredit2016
	protected String userPwd;//wdcredit2016_FT44$S~L
	protected String queryType;//1A020201
	protected String queryKey;//12345678
	protected static String url = DynamicConfigLoader.get("ds_guozt_police_url");
	protected static boolean encrypted = "1".equals(DynamicConfigLoader.get("ds_guozt_encrypted"));
	protected static String desCharset = DynamicConfigLoader.get("ds_guozt_desCharset");
	
	protected String CODE_EQUAL = "gajx_001";
	protected String CODE_NOEQUAL = "gajx_002";
	protected String CODE_NOEXIST = "gajx_003";
	protected String CODE_NOCHECK = "gajx_004";

	protected String DXCODE_EQUAL = "gztdx_001";
	protected String DXCODE_NOEQUAL = "gztdx_002";
	protected String DXCODE_NOEXIST = "gztdx_003";
	protected String DXCODE_NOCHECK = "gztdx_004";

    @Autowired
    protected WDWangShuTokenService tokenService;

	/**
	 * 国政通单条查询
	 * 
	 * @param param
	 * @return String
	 * @throws Exception
	 */
	public String singQurey(String param, String prefix,String url) throws Exception {
		String resultXML = "";
		// 获得WebServices的代理对象
		QueryValidatorServicesProxy proxy = new QueryValidatorServicesProxy();
		proxy.setEndpoint(url);
		QueryValidatorServices service = proxy.getQueryValidatorServices();
		// 对调用的参数进行加密
		String userName = DESUtils.encode(queryKey, userId);
		String password = DESUtils.encode(queryKey, userPwd);
		String datasource = DESUtils.encode(queryKey, queryType);
		logger.info("{}国政通单条数据源采集开始......", new String[] { prefix });
		resultXML = service.querySingle(userName, password, datasource,
				DESUtils.encode(queryKey, param));
		logger.info("{}远程国政通单条数据源采集成功！", new String[] { prefix });
		resultXML = DESUtils.decode(queryKey, resultXML);
		return resultXML;
	}
	/**
	 * 国政通单条查询
	 * 
	 * @param param
	 * @return String
	 * @throws Exception
	 */
	public String singQureyEdu(String param, String prefix,String url) throws Exception {
		String resultXML = "";
		// 获得WebServices的代理对象
		QueryValidatorServicesProxy proxy = new QueryValidatorServicesProxy();
		proxy.setEndpoint(url);
		QueryValidatorServices service = proxy.getQueryValidatorServices();
		// 对调用的参数进行加密
		String userName = DESUtils.encode(queryKey, userId);
		String password = DESUtils.encode(queryKey, userPwd);
		String datasource = DESUtils.encode(queryKey, queryType);
		logger.info("{}国政通单条数据源采集开始......", new String[] { prefix });
		resultXML = service.querySingle(userName, password, datasource,
				DESUtils.encode(queryKey, param));
		logger.info("{}远程国政通单条数据源采集成功！", new String[] { prefix });
		resultXML = DESUtils.decode(queryKey, resultXML);
		return resultXML;
	}
	/**
	 * 国政通批量查询
	 * 
	 * @param param
	 * @return String
	 * @throws Exception
	 */
	public String batchQurey(String param, String prefix) throws Exception {
		String resultXML = "";
		// 获得WebServices的代理对象
		QueryValidatorServicesProxy proxy = new QueryValidatorServicesProxy();
		proxy.setEndpoint(guoztUrl);
		QueryValidatorServices service = proxy.getQueryValidatorServices();
		// 对调用的参数进行加密
		String userName = DESUtils.encode(queryKey, userId);
		String password = DESUtils.encode(queryKey, userId);
		String datasource = DESUtils.encode(queryKey, userId);
		logger.info("{}国政通简项批量数据源采集开始......", new String[] { prefix });
		resultXML = service.queryBatch(userName, password, datasource,
				DESUtils.encode(queryKey, param));
		logger.info("{}远程国政通简项批量数据源采集成功！", new String[] { prefix });
		resultXML = DESUtils.decode(queryKey, resultXML);
		return resultXML;
	}

	/**
	 * 国政通单条查询
	 * 
	 * @param param
	 * @return String
	 * @throws Exception
	 */
	public String httpClientQuery(String param, String prefix,String url) throws Exception {
		GbossClient client = new GbossClient(getConfig(url));
		logger.info("{}国政通单条数据源采集开始......", new String[] { prefix });
		HttpResponseData httpdata = client.invokeSingle(queryType, param);
		logger.info("{}远程国政通单条数据源采集成功！", new String[] { prefix });
		String resultXML = httpdata.getData();
		return resultXML;
	}

	private GbossConfig getConfig(String url) throws Exception {
		GbossConfig config = new GbossConfig();
        config.setEndpoint(url);
		config.setDesKey(queryKey);
		config.setEncrypted(encrypted);
		//分配帐号
		config.setAccount(userId);
		//分配密码
		config.setAccountpwd(userPwd);
		config.setDesCharset(desCharset);
		config.setTimeout(timeout);
		return config;
	}

	/**
	 * 报文格式过滤
	 * 
	 * @param rspBody
	 * @return
	 */
	protected String filtRspBody(String rspBody) {
		rspBody = rspBody.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
				"");
		return rspBody;
	}

	/**
	 * 格式化XML
	 * 
	 * @param inputXML
	 * @return
	 * @throws Exception
	 */
	public String formatXML(String inputXML) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new StringReader(inputXML));
		String requestXML = null;
		XMLWriter writer = null;
		if (document != null) {
			try {
				StringWriter stringWriter = new StringWriter();
				OutputFormat format = new OutputFormat(" ", true);
				writer = new XMLWriter(stringWriter, format);
				writer.write(document);
				writer.flush();
				requestXML = stringWriter.getBuffer().toString();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return requestXML;
	}

	/**
	 * 从交易返回消息中提取参数值
	 * 
	 * @param key
	 * @param params_out
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object extractValueFromResult(String key,
			Map<String, Object> params_out) {
		Object retdataObj = params_out.get(Conts.KEY_RET_DATA);
		if (retdataObj != null) {
			if (retdataObj instanceof Map) {
				return ((Map<String, Object>) params_out
						.get(Conts.KEY_RET_DATA)).get(key);
			}
		}
		return null;
	}

	public static boolean isDateStr(String dateStr, String srcDateFormat) {
		try {
			final SimpleDateFormat src_sdf = new SimpleDateFormat(srcDateFormat);
			final Date date = src_sdf.parse(dateStr);
			// 把转成的日期再反转回来，再比较是否一致
			if (srcDateFormat.length() != dateStr.length()
					|| !dateStr.equals(src_sdf.format(date))) {
				return false;
			}
			return true;
		} catch (java.text.ParseException e) {
			return false;
		}
	}
	//判断是否拍拍贷账户,如果是返回true
	public boolean isStartTimeReturn(String edu_accts,String acct_id){
		if(StringUtil.isEmpty(acct_id) || StringUtil.isEmpty(edu_accts))
			return false;
		for(String acct_tmp:edu_accts.split(",")){
			if(acct_id.equals(acct_tmp)){
				return true;
			}
		}
		return false;
	}
	public String getGuoztUrl() {
		return guoztUrl;
	}

	public void setGuoztUrl(String guoztUrl) {
		this.guoztUrl = guoztUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

    protected Map<String,Object> doRequest(String trade_id, String url,boolean forceRefresh) throws Exception {
        Map<String,Object> header = new HashMap<String,Object>();
        if(forceRefresh){
            logger.info("{} 强制刷新token",trade_id);
            tokenService.setToken(tokenService.getNewToken());
            logger.info("{} 强制刷新token结束",trade_id);
        }else if(tokenService.getToken() == null){
            logger.info("{} 发起token请求",trade_id);
            tokenService.setToken(tokenService.getNewToken());
            logger.info("{} 发起token请求结束",trade_id);

        }
//        String token = PropertyEngine.get("tmp_tokenid");
        String token = tokenService.getToken();
        header.put("X-Access-Token",token);
        logger.info("{} tokenid {}",trade_id,token);
        logger.info("{} start request",trade_id);
        Map<String,Object> rspMap = doGetForHttpAndHttps(url, trade_id, header,null);
        logger.info("{} end request",trade_id);
        return rspMap;
    }

    protected boolean needRetry(int httpstatus, JSONObject rsponse) {
        if(httpstatus == 401){
            return true;
        }
        return false;
    }

    protected boolean isSuccess(JSONObject rspData) {
        if("2001".equals(rspData.getString("code"))){
            return true;
        }
        return false;
    }
}
