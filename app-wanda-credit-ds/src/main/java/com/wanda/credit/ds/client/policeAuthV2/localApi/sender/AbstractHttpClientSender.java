package com.wanda.credit.ds.client.policeAuthV2.localApi.sender;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.jit.new_vstk.Bean.BodyContent;
import cn.com.jit.new_vstk.Bean.ExtraConfig;
import cn.com.jit.new_vstk.Bean.HttpRespResult;
import cn.com.jit.new_vstk.config.NewConfig;
import cn.com.jit.new_vstk.dataAggregator.IRecevieCollector;
import cn.com.jit.new_vstk.dataCutter.ISendCutter;
import cn.com.jit.new_vstk.exception.NewCSSException;
import cn.com.jit.new_vstk.sender.httpClient.IHttpSender;
import cn.com.jit.new_vstk.utils.Base64Util;
import cn.com.jit.new_vstk.utils.ErrorProcess;
import cn.com.jit.new_vstk.utils.GMUtils;
import cn.com.jit.new_vstk.utils.ParamValidater;
import cn.com.jit.new_vstk.utils.SystemProperties;
import cn.com.jit.new_vstk.utils.VstkConstants;
import jar.com.jit.gson.JsonObject;
import jar.com.jit.gson.JsonParser;
import jar.org.apache.http.Header;
import jar.org.apache.http.HttpException;
import jar.org.apache.http.client.config.RequestConfig;
import jar.org.apache.http.client.methods.CloseableHttpResponse;
import jar.org.apache.http.client.methods.HttpPost;
import jar.org.apache.http.entity.ByteArrayEntity;
import jar.org.apache.http.impl.client.CloseableHttpClient;
import jar.org.apache.http.impl.client.HttpClients;

public abstract class AbstractHttpClientSender
implements IHttpSender {
	private final static Logger logger = LoggerFactory
			.getLogger(AbstractHttpClientSender.class);
	  private static final String CLASS_NAME = AbstractHttpClientSender.class.getName();
	  private ErrorProcess errprocess = null;
	  protected NewConfig config;
	  protected int getConnTimeout = SystemProperties.getConnTimeout;

	  public AbstractHttpClientSender(NewConfig config) {
	    this.config = config;
	    this.errprocess = ErrorProcess.getInstance();
	  }

	  public HttpRespResult send(Map<String, String> httpHeader, 
			  BodyContent bodyContent, IRecevieCollector rc, ExtraConfig extraConfig) 
					  throws NewCSSException, IOException{
//	    String METHOD_NAME = "send(Map<String, String> httpHeader, byte[] priData, byte[] subData)";
	    CloseableHttpClient httpClient = HttpClients.custom().build();
	    HttpRespResult result = null;
	    CloseableHttpResponse response = null;

	    boolean isBigData = false;
	    ISendCutter sc = null;
	    try {
	      sc = bodyContent.getSendCutter();

	      int needSendNum = sc.needSendNum();
	      isBigData = needSendNum > 1;

//	      if (isBigData) {
//	        httpClient = HttpClientFactory.createInstance();
//
//	        httpHeader.put("token", UUID.randomUUID().toString());
//	      }
//	      else {
//	        httpClient = HttpClientFactory.getInstance();
//	      }
	      httpHeader.put(VstkConstants.TOTAL_LENGTH, sc.getActualLength() + "");
	      String url = extraConfig.serverUrl;
	      if (ParamValidater.isNull(url)) {
	        logger.error("参数不合法，url输入为空！");
	        throw new NewCSSException("-10702001", CLASS_NAME + "send(Map<String, String> httpHeader, byte[] priData, byte[] subData)", "参数不合法，url输入为空！");
	      }

	      HttpPost post = getHttpPost(url, extraConfig.readTimeout);

	      putContantsHeaders(httpHeader, post);

	      byte[] sendBody = null;
	      int sendedNum = 0;
	      while (sendedNum < needSendNum) {
	        sendedNum++;
	        sendBody = sc.nextSendData();
	        String messageBodyType = formMessageBodyType(needSendNum, sendedNum);
	        post = putVarParams(post, messageBodyType, sendBody);
	        response = sendAndRealException(httpClient, post);

	        if (isBigData) {
	          isBigDataCheckError(response);
	        }

	        if (needSendNum <= 1){
	          rc.putRecevieDataReset(response);
	        }
	        else if (sendedNum == 1) rc.putRecevieDataReset(response); else {
	          rc.putRecevieData(response);
	        }
	      }

	      result = formRespResult(rc, response);
	      checkBusinessError(result);
	    } catch (NewCSSException e) {
	      closeHttpPost(response);
	      closeHttpClient(httpClient);
	      logger.error("http operation is error", e);
	      throw e;
	    }catch (IOException e) {
	      closeHttpPost(response);
	      closeHttpClient(httpClient);
	      logger.error("http operation is error", e);
	      throw e;
	    }catch (Throwable e) {
	      closeHttpPost(response);
	      closeHttpClient(httpClient);
	      logger.error("http operation is error", e);
	      throw new NewCSSException("-10701105", CLASS_NAME + "send(Map<String, String> httpHeader, byte[] priData, byte[] subData)", "签名时向服务器发送请求报文时产生异常！");
	    }
	    finally {
	      closeHttpPost(response);
	      closeHttpClient(httpClient);
	      if (sc != null) sc.close();	      
	    }
	    return result;
	  }

	  private void isBigDataCheckError(CloseableHttpResponse response) throws NewCSSException {
	    String METHOD_NAME = "isBigDataCheckError";

	    Header errorCodeHeader = response.getFirstHeader(VstkConstants.ERROR_CODE);
	    Header errormsgHeader = response.getFirstHeader(VstkConstants.ERROR_MSG);
	    String errorCode = errorCodeHeader == null ? "" : errorCodeHeader.getValue();
	    String errormsg = errormsgHeader == null ? "" : Base64Util.decodeUTF8(errormsgHeader.getValue());

	    if (ParamValidater.isNull(errorCode)) {
	      closeHttpPost(response);
	      logger.error("获取返回报文失败！");
	      throw new NewCSSException("-10702012", METHOD_NAME, "获取返回报文失败！");
	    }
	    if (!errorCode.equals("0")) {
	      closeHttpPost(response);
	      logger.error(errormsg);
	      throw new NewCSSException(errorCode, METHOD_NAME, errormsg);
	    }
	  }

	  private void checkBusinessError(HttpRespResult result) throws NewCSSException {
	    String METHOD_NAME = "AbstractHttpClientSender.checkBusinessError";
	    String errorCode = null;
	    String errormsg = null;
	    Map<String,String> header = result.getRespHeader();
	    String contentType = (String)header.get("content-type");
	    if (null == contentType) contentType = (String)header.get("Content-Type");

	    if (GMUtils.isSM2Communication(this.config))
	    {
	      errorCode = parseErrorCode(result.getRespBody());
	    } else if (GMUtils.isXMLCommunication(this.config)) {
	      try {
	        String resultXML = new String(result.getRespBody(), "UTF-8");
	        if (resultXML.indexOf("status=\"true\"") >= 0)
	        {
	          errorCode = "0";
	          header.put(VstkConstants.ERROR_CODE, errorCode);
	        } else {
	          errorCode = parseErrorCode(resultXML);
	          errormsg = this.errprocess.getErrDesc(errorCode, ErrorProcess.vDefServerInfo);
	        }
	      } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	      }
	    } else if (contentType.startsWith("application/json")) {
	      String serverVersion = (String)header.get("server_version");
	      errorCode = (String)header.get(VstkConstants.ERROR_CODE);
	      if (((null == errorCode) || ("0".equals(errorCode))) && 
	        (null == serverVersion))
	        try
	        {
	          JsonObject jsonObjectResult = pressErrorCodeJson(result);
	          errorCode = jsonObjectResult.get("resultCode").getAsString();
	          header.put(VstkConstants.ERROR_CODE, errorCode);
	          if (!"0".equals(errorCode)) {
	            errormsg = jsonObjectResult.get("resultMsg").getAsString();
	            logger.error(errorCode + ":" + errormsg);

	            errormsg = new String(Base64Util.encode(errormsg.getBytes("UTF-8")));
	          }
	        }
	        catch (UnsupportedEncodingException e)
	        {
	        }
	    }
	    else {
	      errorCode = (String)header.get(VstkConstants.ERROR_CODE);
	      errormsg = (String)header.get(VstkConstants.ERROR_MSG);
	    }
	    if (!GMUtils.isXMLCommunication(this.config)) {
	      errormsg = errormsg == null ? "" : Base64Util.decodeUTF8(errormsg);
	    }

	    if (ParamValidater.isNull(errorCode)) {
	      logger.error("获取返回报文失败！");
	      throw new NewCSSException("-10702012", METHOD_NAME, "获取返回报文失败！");
	    }
	    if (!errorCode.equals("0")) {
	      logger.error(errormsg);
	      throw new NewCSSException(errorCode, METHOD_NAME, errormsg);
	    }
	  }

	  private JsonObject pressErrorCodeJson(HttpRespResult result)
	    throws UnsupportedEncodingException
	  {
	    String jsonString = new String(result.getRespBody(), "UTF-8");
	    JsonObject jsonObjectResult = new JsonParser().parse(jsonString).getAsJsonObject();
	    return jsonObjectResult;
	  }

	  private String parseErrorCode(byte[] body)
	    throws NewCSSException{
	    String errorCode = null;
	    Map<String,String> map = GMUtils.toBodyMap(body);
	    errorCode = (String)map.get("respValue");
	    return errorCode;
	  }

	  private static String parseErrorCode(String recevieXml){
	    try
	    {
	      int i = recevieXml.indexOf("errcode");
	      int j = recevieXml.indexOf("</Response>");
	      String[] split = recevieXml.substring(i, j).split("\"");
	      return split[1]; } catch (Exception e) {
	    }
	    return "parse ErrorCode failed,please see recevie xml!";
	  }

	  private HttpPost putVarParams(HttpPost httpPost, String messageBodyType, byte[] sendBody)
	  {
	    httpPost.setEntity(new ByteArrayEntity(sendBody));
	    httpPost.removeHeaders(VstkConstants.CONTENT_LENGTH);
	    httpPost.removeHeaders(VstkConstants.BODY_ORDER);
	    httpPost.addHeader(VstkConstants.BODY_ORDER, messageBodyType);
	    return httpPost;
	  }

	  private CloseableHttpResponse sendAndRealException(CloseableHttpClient httpclient, HttpPost httppost)
	    throws IOException, HttpException, NewCSSException{
	    printAllHeaderParams(httppost.getAllHeaders(), "send");
	    CloseableHttpResponse response = executeSend(httppost, httpclient);

	    printAllHeaderParams(response.getAllHeaders(), "receive");

	    checkErrorResult(response);
	    return response;
	  }

	  private int getConfigIntParam(String paramName, String value, int defaultNum) {
	    int currentNum = 0;
	    if (ParamValidater.isNotNull(value)) {
	      currentNum = Integer.parseInt(value);
	    }
	    else if (logger.isInfoEnabled()) {
	      logger.info(paramName + " value is null , set the default value is " + defaultNum);
	    }

	    return currentNum == 0 ? defaultNum : currentNum;
	  }

	  protected HttpPost getHttpPost(String url, int readTimeout)
	  {
	    HttpPost httppost = new HttpPost(url.toString());

	    String connectTimeOutStr = this.config.getConnectTimeOut();
	    int connTimeout = getConfigIntParam("connectTimeOut", connectTimeOutStr, 10000);
	    readTimeout = connTimeout;
	    logger.info("加签请求地址为:"+url);
	    logger.info("readTimeout为:"+readTimeout+";connTimeout为:"+connTimeout);
	    RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(this.getConnTimeout).setSocketTimeout(readTimeout).setConnectTimeout(connTimeout).build();

	    httppost.setConfig(requestConfig);
	    return httppost;
	  }

	  protected void putContantsHeaders(Map<String, String> httpHeader, HttpPost httppost){
	    for (Map.Entry<String,String> headerProperty : httpHeader.entrySet()) {
	      String key = (String)headerProperty.getKey();
	      String value = (String)headerProperty.getValue();
	      httppost.addHeader(key, value);
	    }
	  }

	  protected static HttpRespResult formRespResult(IRecevieCollector rc, CloseableHttpResponse response)
	    throws NewCSSException
	  {
	    String METHOD_NAME = "AbstractHttpClientSender.formRespResult";
	    HttpRespResult result = new HttpRespResult();
	    if (response == null) {
	      logger.error("获取返回报文失败！");
	      throw new NewCSSException("-10702012", METHOD_NAME, "获取返回报文失败！");
	    }
	    Header[] responseHeaders = response.getAllHeaders();
	    for (Header header : responseHeaders) {
	      String value = header.getValue();
	      result.getRespHeader().put(header.getName().toLowerCase(), value);
	    }

	    result.setRespBody(rc.generateRecData());
	    return result;
	  }

	  protected static CloseableHttpResponse executeSend(HttpPost httppost, CloseableHttpClient httpclient)
	    throws NewCSSException, IOException{
	    String METHOD_NAME = "AbstractHttpClientSender.executeSend";
	    CloseableHttpResponse response = null;
	    try {
	      long beginSend = System.currentTimeMillis();
	      response = httpclient.execute(httppost);
	      long afterSend = System.currentTimeMillis();
	      if (logger.isDebugEnabled()) {
	        logger.debug("send cost time:" + (afterSend - beginSend) + " ms");
	      }

	    }catch (IOException e){
	      logger.error("Send request is failed.");
	      logger.error("Send url:" + httppost.getURI().toASCIIString());
	      logger.error("errorCode: -10702022, errorMessage: 发送请求报文出现IO错误！");
	      throw e;
	    }catch (Exception e1) {
	      logger.error("Send request is failed.");
	      logger.error("errorCode: -10702007, errorMessage: 发送请求失败！");
	      throw new NewCSSException("-10702007", METHOD_NAME, "发送请求失败！", e1.toString());
	    }
	    return response;
	  }

	  protected static void closeHttpPost(CloseableHttpResponse response){
	    try{
	      if (response != null){
	        response.close();
	        response = null;
	      }
	    } catch (Exception e) {
	      if (logger.isErrorEnabled())
	        logger.error("Disconnect http request connection is exception.", e);
	    }
	  }

	  protected static void closeHttpClient(CloseableHttpClient httpClient){
	    try{
	      if (httpClient != null){
	        httpClient.close();
	      }
	    } catch (Exception e) {
	    	logger.error("Disconnect http url connection is exception.", e);
	    }
	  }

	  protected void checkErrorResult(CloseableHttpResponse response)
	    throws NewCSSException, IllegalStateException, IOException{
	    String METHOD_NAME = "AbstractHttpClientSender.checkErrorResult";
	    int statusCode = response.getStatusLine().getStatusCode();
	    if (statusCode != 200) {
	      logger.error("服务器响应异常");
	      throw new NewCSSException("-10701113", CLASS_NAME + METHOD_NAME, "参数不合法，签名时输入原文文件未找到！");
	    }
	  }

	  protected String formMessageBodyType(long needSendNum, int sendedNum)
	  {
	    String messageBodyType = null;
	    if (sendedNum == 1)
	      messageBodyType = VstkConstants.BODY_ORDER_FIRST;
	    else if (sendedNum >= needSendNum)
	      messageBodyType = VstkConstants.BODY_ORDER_LAST;
	    else {
	      messageBodyType = VstkConstants.BODY_ORDER_MIDDLE;
	    }
	    return messageBodyType;
	  }

	  protected void printAllHeaderParams(Header[] headers, String method) {
	    if (logger.isDebugEnabled())
	      for (Header header : headers)
	        logger.debug(method + " header param:" + header.getName() + ":" + header.getValue());
	  }
}
