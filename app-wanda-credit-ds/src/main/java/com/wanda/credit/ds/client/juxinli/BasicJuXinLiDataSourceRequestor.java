package com.wanda.credit.ds.client.juxinli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.juxinli.bean.AccessToken;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.DataSourceTime;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.MobileEBusiDataSource;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyNextDataSourcePojo;
import com.wanda.credit.ds.dao.domain.juxinli.trade.JXLMEbusiTradePojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLBasicInfoService;
import com.wanda.credit.ds.dao.iface.juxinli.trade.IJXLEBusiTradeService;

/**
 * 聚信立客户端请求
 * 
 * @author xiaobin.hou
 * 
 */
public class BasicJuXinLiDataSourceRequestor extends BaseDataSourceRequestor {

	private final static Logger logger = LoggerFactory.getLogger(BasicJuXinLiDataSourceRequestor.class);
	@Autowired
	protected IJXLBasicInfoService jxlBasicInfoService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	protected IJXLEBusiTradeService jxlEBusiTradeService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	
	public JsonObject getEbusiRawData(String url,String clientSecret,String accessToken,String token,int timeOut ,String trade_id){
		StringBuffer eBusiRawDataUrl = new StringBuffer();
		
		eBusiRawDataUrl.append(url).append("?").append(JXLConst.CLIENT_SECRET).append("=").append(clientSecret)
		.append("&").append(JXLConst.ACCESS_TOKEN).append("=").append(accessToken)
		.append("&").append(JXLConst.COLL_TOKEN).append("=").append(token);
		
		logger.info("{} 根据token获取电商原始数据URL为" + eBusiRawDataUrl.toString());
		
		JsonObject jsonResponse = getJsonResponse(eBusiRawDataUrl.toString(), timeOut ,trade_id);
		return jsonResponse;
	}
	
	public JsonObject getReportDataByToken(String url,String clientSecret,String accessToken, String token, int timeOut ,String tradeId) {
		StringBuffer reportDataUrlBf = new StringBuffer();
		reportDataUrlBf.append(url).append("?").append(JXLConst.CLIENT_SECRET).append("=").append(clientSecret)
			.append("&").append(JXLConst.ACCESS_TOKEN).append("=").append(accessToken).append("&").append(JXLConst.COLL_TOKEN).append("=").append(token);
		
		logger.info("{} 聚信立根据Token获取报告数据URL为" + reportDataUrlBf.toString());
		
		JsonObject jsonResponse = getJsonResponse(reportDataUrlBf.toString(), timeOut ,tradeId);
		
		return jsonResponse;
	}
	/**
	 * 获取工商信息原始数据
	 * @param url 
	 * @param clientSecret 机构标示码，聚信立分配
	 * @param accessToken 安全凭证码
	 * @param token	采集流程生成的token
	 * @return
	 */
	public JsonObject getEnterpriseRawData(String url,String clientSecret,String accessToken,String token,int timeOut,String tradeId){
		
		StringBuffer accTokenUrlBf = new StringBuffer();
		
		accTokenUrlBf.append(url).append("?").append(JXLConst.CLIENT_SECRET).append("=").append(clientSecret)
			.append("&").append(JXLConst.ACCESS_TOKEN).append("=").append(accessToken)
			.append("&").append(JXLConst.COLL_TOKEN).append("=").append(token);
		
		logger.info("{} 连接聚信立获取工商信息原始数据Url为" + accTokenUrlBf.toString());
		
		JsonObject jsonResponse = getJsonResponse(accTokenUrlBf.toString(),timeOut,tradeId);
		
		return jsonResponse;
	}
	
	
	/**
	 * 提交工商采集信息
	 * @param url Post请求的地址
	 * @param jsonData 请求参数，json格式数据
	 * @return
	 */
	public JsonObject postEnterpriseColl(String url,String jsonStrData,int timeOut , String tradeId){
		
		JsonObject collResJson = postJsonData(url, jsonStrData,timeOut,tradeId);		
		return collResJson;
		
	}
	
	/**
	 * 获取所有工商网站信息
	 * @return
	 */
	public JsonObject getAllEnterprise(String url,int timeOut,String tradeId){
		
		JsonObject jsonResponse = getJsonResponse(url,timeOut,tradeId);
		
		return jsonResponse;
		
	}
	
//	/**
//	 * 获取分析报告的结果
//	 * @param clientSecret
//	 * @param accessToken
//	 * @param name
//	 * @param idCardNum
//	 * @param phoneNum
//	 * @return
//	 */
//	public JsonObject accessReportData(String clientSecret,String accessToken,String name,String idCardNum,String phoneNum,int timeOut){
//		
//		StringBuffer reportDataUrlBF = new StringBuffer();
//		reportDataUrlBF.append(REPORTDATAURL).append("?client_secret=").append(clientSecret)
//			.append("&access_token=").append(accessToken).append("&name=").append(name)
//			.append("&idcard=").append(idCardNum).append("&phone=").append(phoneNum);
//		
//		JsonObject jsonResponse = getJsonResponse(reportDataUrlBF.toString(),timeOut);
//		
//		return jsonResponse;
//	}
	
	/**
	 * 根据token获取移动运营商的原始数据
	 * @param clientSecret
	 * @param accessToken
	 * @param token
	 * @return
	 */
	public JsonObject getMobileRawDataByToken(String url,String clientSecret,String accessToken,String token,int timeOut,String tradeId){
		
		StringBuffer mobilelDataUrlBf = new StringBuffer();
		
		mobilelDataUrlBf.append(url).append("?").append(JXLConst.CLIENT_SECRET).append("=").append(clientSecret)
		.append("&").append(JXLConst.ACCESS_TOKEN).append("=").append(accessToken)
		.append("&").append(JXLConst.COLL_TOKEN).append("=").append(token);
		
		JsonObject jsonResponse = getJsonResponse(mobilelDataUrlBf.toString(),timeOut,tradeId);
		
		return jsonResponse;
	}
	
	/**
	 * 提交采集请求数据
	 * @param jsonStrData
	 * @return
	 */
	public JsonObject submitCollReq(String url,String jsonStrData,int timeOut ,String tradeId){
		JsonObject postJsonData = postJsonData(url, jsonStrData,timeOut,tradeId);		
		return postJsonData;
	}
	
	/**
	 * 提交申请表单获取回执信息_获取token
	 * @param jsonStrData
	 * @return
	 */
	public JsonObject getMobileEBusiToken(String url,String jsonStrData,int timeOut,String tradeId){		
		JsonObject postJsonData = postJsonData(url, jsonStrData,timeOut,tradeId);		
		return postJsonData;
	}
	
	/**
	 * 
	 * @return
	 */
	public JsonObject getEBusiDataSources(String url,int timeOut,String tradeId) { 			
		JsonObject dataSourcesJsonObj = getJsonResponse(url,timeOut,tradeId);
		return dataSourcesJsonObj;
	}
	
	
	/**
	 * 获取聚信立安全凭证码
	 * @param orgName 客户名称
	 * @param clientSecret	客户标示码
	 * @param hours	安全凭证过期时效 1(1小时有效),24(24小时有效),per(永久有效)
	 * @return
	 */
	public JsonObject getAcceptReportToken(String url,String orgName, String clientSecret,
			String hours,int timeOut,String tradeId) {
		
		//拼装Get 请求URL
		StringBuffer urlSB = new StringBuffer();
		
		urlSB.append(url).append("?").append(JXLConst.CLIENT_SECRET).append("=").append(clientSecret)
			.append("&").append(JXLConst.ACCESS_TOKEN_HOURS).append("=").append(hours)
			.append("&").append(JXLConst.ORG_NAME).append("=").append(orgName);
		
		JsonObject jsonResponse = getJsonResponse(urlSB.toString(),timeOut ,tradeId);

		return jsonResponse;
	}
	
	/**
	 * http Get 请求
	 * @param url
	 * @return
	 */
	public JsonObject getJsonResponse(String url,int timeOut,String tradeId ) {
		
		boolean doPrint = JXLConst.LOG_ON.equals(propertyEngine.readById("sys_log_print_switch"));
		if (doPrint) {
			logger.info("{} 聚信立Http Get请求：{}", "请求URL为" + url);
		}
		
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut)
		.setConnectionRequestTimeout(timeOut).setStaleConnectionCheckEnabled(true).build();
		
		JsonObject jsonObject = null;
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpGet get = new HttpGet(url);
		get.setConfig(requestConfig);
		CloseableHttpClient closeableHttpClient = null;
		try {
			closeableHttpClient = httpClientBuilder.build();
			HttpResponse resp = closeableHttpClient.execute(get);
			jsonObject = convertResponseBytes2JsonObj(resp);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("{} 聚信立Http Get请求：{}", tradeId,"发送Http请求失败："+ e.getMessage());
		}finally{
			if (null != closeableHttpClient) {
				try {
					closeableHttpClient.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
					logger.error("{} 聚信立Http Get请求：{}","关闭Http连接失败：" + ioe.getMessage());
				}
			}
		}
		return jsonObject;
	}
	
	/**
	 * http Post 请求
	 * @param url
	 * @param jsonStrData
	 * @return
	 */
	public JsonObject postJsonData( String url, String jsonStrData,int timeOut,String tradeId ) {
		
		logger.info("{} 聚信立Http Post请求：{}","请求URL为" + url);
		
		boolean doPrint = JXLConst.LOG_ON.equals(propertyEngine.readById("sys_log_print_switch"));
		if (doPrint) {
			logger.info("{} 聚信立Http Post请求：{}","POST JSON 数据为" + jsonStrData);
		}

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut)
		.setConnectionRequestTimeout(timeOut).setStaleConnectionCheckEnabled(true).build();
		
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpPost post = new HttpPost(url);
		post.setConfig(requestConfig);
		JsonObject jsonObject = null;
		CloseableHttpClient closeableHttpClient = null;
		try {
			closeableHttpClient = httpClientBuilder.build();
			// 修复 POST json 导致中文乱码
			HttpEntity entity = new StringEntity(jsonStrData, "UTF-8");
			post.setEntity(entity);
			post.setHeader("Content-type", "application/json");
			HttpResponse resp = closeableHttpClient.execute(post);
			jsonObject = convertResponseBytes2JsonObj(resp);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("{} 聚信立Http Post请求：{}",tradeId,"发送Http请求失败：" + e.getMessage());
		} finally{
			if (null != closeableHttpClient) {
				try {
					closeableHttpClient.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
					logger.error("{} 聚信立Http Post请求：{}","关闭Http连接失败：" + ioe.getMessage());
				}
			}
		}
		return jsonObject;
	}
    /**
     * HttpResponse ==> JsonObject
     * @param resp
     * @return
     */
    private JsonObject convertResponseBytes2JsonObj(HttpResponse resp) {
    	JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = null;

        try {        	
            InputStream respIs = resp.getEntity().getContent();
            byte[] respBytes = IOUtils.toByteArray(respIs);
            
            String result = new String(respBytes, Charset.forName("UTF-8"));
            if (null == result || result.length() == 0) {
                logger.error("{} 聚信立Http请求结果：{}","Http 请求结果为Null或为空字符串");
                return jsonObject;
            } else {
                if (result.startsWith("{") && result.endsWith("}")) {
                    jsonObject = (JsonObject) jsonParser.parse(result);
                } else {
                	logger.error("{} 聚信立Http请求结果：{}","请求结果不能转成JSON对象");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{} 聚信立Http请求结果：{}","Http 请求结果解析成JsonObject失败");
        }
        return jsonObject;
    }
    
    /**
     * 将聚信立的DataSource转化成Domain对象
     * @param nextDataSource
     * @return
     */
    protected ApplyNextDataSourcePojo dataSourceBean2Pojo(MobileEBusiDataSource nextDataSource) {
		
		if(nextDataSource == null){
			return null;
		}
		
		ApplyNextDataSourcePojo pojo = new ApplyNextDataSourcePojo();
		pojo.setCrt_time(new Date());
		pojo.setUpd_time(new Date());
		
		pojo.setDatasource_id(nextDataSource.getId());
		pojo.setWebsite(nextDataSource.getWebsite());
		pojo.setName(nextDataSource.getName());
		pojo.setCategory_name(nextDataSource.getCategory_name());
		pojo.setCategory(nextDataSource.getCategory());
		DataSourceTime update_time = nextDataSource.getUpdate_time();
		StringBuffer timeBf = new StringBuffer();		
		timeBf.append(update_time.getYear()).append("-")
			.append(update_time.getMonth()).append("-")
			.append(update_time.getDayOfMonth()).append(" ")
			.append(update_time.getHourOfDay()).append(":")
			.append(update_time.getMinute()).append(":")
			.append(update_time.getSecond());
		pojo.setDeal_time(timeBf.toString());
		
		pojo.setOffline_times(nextDataSource.getOffline_times());
		pojo.setStatus(nextDataSource.getStatus());
		pojo.setWebsite_code(nextDataSource.getWebsite_code());
		pojo.setReset_pwd_method(nextDataSource.getReset_pwd_method());
		pojo.setSms_required(nextDataSource.getSms_required());
		pojo.setRequired_captcha_user_identifi(nextDataSource.getRequired_captcha_user_identified());
		
		
		return pojo;
	}
    
    /**
     * 运营商和电商根绝requestId获取聚信立对应的Token
     * @param requestId
     * @return
     */
    public ApplyBasicInfoPojo requestId2Token(String requestId){
    	
    	logger.info("{} 需要查询的requestId为" + requestId);
    	
    	if (requestId == null || requestId.length() < 1) {
			return null;
		}
		
		try {
			//通过requestId获取对应的Token
			ApplyBasicInfoPojo basicInfo = jxlBasicInfoService.getValidTokenByRequestId(requestId);			
			if(null == basicInfo){
				logger.info("{} 没有Token和requestId相对应，该requestId无效");
				return null;
			}
			return basicInfo;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("{} 从数据库中根据requestId获取Token异常:" + e.getMessage());
			return null;
			
		}
    }
    
	public String getAcceptToken(String url, String orgAcc, String clientSec,
			String hours, int timeOut, String prefix) throws Exception {

    	String acceptToken = null;
    	
		logger.info("{} 连接聚信立获取安全凭证码,该安全码的有效时长为（单位h）" + hours, prefix);

		JsonObject acceptReportToken = getAcceptReportToken(
				url, orgAcc, clientSec, hours,timeOut * 1000, prefix);

		if (acceptReportToken == null) {
			logger.info("{} 连接聚信立获取安全凭证码失败,可能网络不通", prefix);
			throw new Exception(JXLConst.ACCEPT_TOKEN_RES_NULL);
		}

		AccessToken accToken = new Gson().fromJson(acceptReportToken,
				AccessToken.class);

		if ("true".equals(accToken.getSuccess())) {
			logger.info("{} 连接聚信立获取凭证安全码,聚信立返回TRUE", prefix);
			acceptToken = accToken.getAccess_token();
			if (StringUtil.isEmpty(acceptToken)) {
				logger.info("{} 连接聚信立获取安全凭证码，安全凭证码为空" , prefix);
				throw new Exception(JXLConst.ACCEPT_TOKEN_RES_ACCESS_TOKEN_NULL);
			}
		} else {
			logger.info("{} 连接聚信立获取凭证安全码,聚信立返回FALSE", prefix);
			logger.info("{} 连接聚信立获取凭证码返回信息为" + accToken, prefix);
			throw new Exception(JXLConst.ACCEPT_TOKEN_SUC_FALSE);
		}
		
		logger.info("{} 连接聚信立获取安全凭证码成功" , prefix);
		
		return acceptToken;
    }
    
    
    public boolean saveTradeInfo(String tradeId,String tradeFlag,String retCode,
    		String name,String idCardNo,String phoneNum,String requestId){
    	try{
    		
    		Date nowTime = new Date();
			JXLMEbusiTradePojo trade = new JXLMEbusiTradePojo();
			if (!StringUtil.isEmpty(idCardNo)) {
				String cryCardNo = synchExecutorService.encrypt(idCardNo);
				trade.setId_card_no(cryCardNo);
			}else{
				trade.setId_card_no("");
			}
			
			if (!StringUtil.isEmpty(phoneNum)) {
				String cryMobileNo = synchExecutorService.encrypt(phoneNum);
				trade.setCell_phone(cryMobileNo);
			}else{
				trade.setCell_phone("");
			}
			
			trade.setName(name);
			trade.setRequestId(requestId);
			trade.setRet_code(retCode);
			trade.setTrade_flag(tradeFlag);
			trade.setTrade_id(tradeId);
			trade.setCrt_time(nowTime);
			trade.setUpd_time(nowTime);
			
			jxlEBusiTradeService.add(trade);
			
		}catch(Exception e){
			logger.error("{} 交易信息保存失败，错误信息为：" + e.getMessage());
			return false;
		}
    	
    	return true;
    }
    

}
