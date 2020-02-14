package com.wanda.credit.ds.client.policeAuthV2.localApi.sender;
import cn.com.jit.new_vstk.Bean.BodyContent;
import cn.com.jit.new_vstk.Bean.ExtraConfig;
import cn.com.jit.new_vstk.Bean.HttpRespResult;
import cn.com.jit.new_vstk.algorithm.loadBalancing.ConsistentHashAlg;
import cn.com.jit.new_vstk.algorithm.loadBalancing.LoadBalanceingAlg;
import cn.com.jit.new_vstk.algorithm.probe.HealthProbe;
import cn.com.jit.new_vstk.algorithm.probe.HttpHealthProbe;
import cn.com.jit.new_vstk.config.NewConfig;
import cn.com.jit.new_vstk.dataAggregator.IRecevieCollector;
import cn.com.jit.new_vstk.exception.NewCSSException;
import cn.com.jit.new_vstk.log.JITLog;
import cn.com.jit.new_vstk.log.JITLogFactory;

import cn.com.jit.new_vstk.utils.GMUtils;
import cn.com.jit.new_vstk.utils.ParamValidater;
import cn.com.jit.new_vstk.utils.SystemProperties;
import cn.com.jit.new_vstk.utils.VstkConstants;
import jar.org.apache.commons.lang.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientSender extends AbstractHttpClientSender{
	private static final JITLog logger = JITLogFactory.getLog(HttpClientSender.class);

	  private static int retryNum = SystemProperties.retry;
	  private LoadBalanceingAlg lbAlg;
	  private HealthProbe healthProbe;

	  public HttpClientSender(NewConfig config){
	    super(config);
	    this.lbAlg = new ConsistentHashAlg();
	    this.healthProbe = new HttpHealthProbe(config);
	  }

	  public HttpRespResult sendwithRetry(Map<String, String> httpHeader, 
			  BodyContent bodyContent,String sendUrl) throws NewCSSException{
	    String METHOD_NAME = "HttpClientSender.sendwithRetry";
	    int sendNum = 0;
	    int tempReadTimeOut = this.config.getFirstReadTimeout();

	    int readTimeout = 300;
	    HttpRespResult result = null;
//	    List<String> serverUrlCopy = new ArrayList<String>(this.healthProbe.getAvailableUrls());
	    long errorId = System.currentTimeMillis();
	    IRecevieCollector rc = null;
	    while (true) {
	      try {
	        if (sendNum > retryNum) {
	          return result;
	        }

	        if (sendNum > 0) {
	          tempReadTimeOut = readTimeout;
	        }

	        putErrLogFlag(httpHeader, sendNum);

	        if (StringUtils.isBlank(sendUrl)) {
	          logger.info("http request can't select right connection,errorId=" + errorId);
	          sendNum++;
	        }
	        else {
	          rc = bodyContent.getRecevieCollector();
	          logger.info("http send begin ...");
	          result = send(httpHeader, bodyContent, rc, new ExtraConfig(tempReadTimeOut, sendUrl));
	          logger.info("http send end");
	          String errorCode = (String)result.getRespHeader().get(VstkConstants.ERROR_CODE);

	          if (GMUtils.isSM2Communication(this.config))
	          {
	            break;
	          }
	          if (errorCode.equals("0")) {
	            if (sendNum > 0) {
	              logger.info("http request retry success,errorId=" + errorId);
	            }
	            break;
	          }
	          sendNum++;
	          String errorMsg = (String)result.getRespHeader().get(VstkConstants.ERROR_MSG);
	          loggerError(sendNum, errorId, errorCode, errorMsg, null);
	        }
	      } catch (NewCSSException e) {
	        sendNum = retryNum;
	        sendNum++; if (sendNum > retryNum) {
	          loggerError(sendNum, errorId, e.getCode(), e.getDescription(), e);
	          throw e;
	        }
	      } catch (IOException e) {
	        HttpHealthProbe.removeUrl(sendUrl);
	        sendNum++; if (sendNum > retryNum) {
	          loggerError(sendNum, errorId, "-10702003", "发送请求超时！", e);
	          throw new NewCSSException("-10702003", "." + METHOD_NAME, "发送请求超时！", e.toString());
	        }
	      }
	      catch (Exception e) {
	        HttpHealthProbe.removeUrl(sendUrl);
	        sendNum++; if (sendNum > retryNum) {
	          loggerError(sendNum, errorId, "-10701105", "签名时向服务器发送请求报文时产生异常！", e);
	          throw new NewCSSException("-10701105", "." + METHOD_NAME, "签名时向服务器发送请求报文时产生异常！", e.toString());
	        }
	      }

	    }

	    if (rc != null)
	      rc.close();
	    return result;
	  }

	  private void putErrLogFlag(Map<String, String> httpHeader, int sendNum) {
	    if ((retryNum > 0) && (sendNum != retryNum))
	      httpHeader.put("errorlog", "true");
	    else
	      httpHeader.put("errorlog", "true");
	  }

	  private int getConfigIntParam(String paramName, String value, int defaultNum){
	    int currentNum = 0;
	    if (ParamValidater.isNotNull(value)) {
	      currentNum = Integer.parseInt(value);
	    }
	    else if (logger.isInfoEnabled()) {
	      logger.info(paramName + " value is null , set the default value is " + defaultNum);
	    }
	    return currentNum == 0 ? defaultNum : currentNum;
	  }

	  private static void loggerError(int sendNum, long errorId, String errorCode, String errMsg, Exception e) {
	    logger.error("****************************************");
	    logger.error("http request failed,total retry=" + retryNum + ",current retry num=" + sendNum);
	    logger.error("errorId: " + errorId);
	    if (errorCode != null) {
	      logger.error("errorCode: " + errorCode);
	    }
	    if (errMsg != null) {
	      logger.error("errorMessage: " + errMsg);
	    }
	    if (e != null)
	      logger.error("exception message:" + e.getMessage(), e);
	  }

	  public HealthProbe getHealthProbe()
	  {
	    return this.healthProbe;
	  }

	  public static void main(String[] args) {
	    List<String> test = new ArrayList<String>();
	    test.add("192.168.1.1");
	    test.add("192.168.1.2");
	  }
}
