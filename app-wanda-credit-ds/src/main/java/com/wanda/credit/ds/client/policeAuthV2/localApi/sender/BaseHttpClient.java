package com.wanda.credit.ds.client.policeAuthV2.localApi.sender;
import cn.com.jit.new_vstk.config.NewConfig;
import cn.com.jit.new_vstk.exception.NewCSSException;
import cn.com.jit.new_vstk.former.BodyContentFormerFactory;
import cn.com.jit.new_vstk.former.FormerFactory;
import cn.com.jit.new_vstk.former.IBodyContentFormer;
import cn.com.jit.new_vstk.former.IRequestFormer;
import cn.com.jit.new_vstk.former.IResponseFormer;
import cn.com.jit.new_vstk.log.JITLog;
import cn.com.jit.new_vstk.log.JITLogFactory;
import cn.com.jit.new_vstk.utils.ConfigFactory;
import cn.com.jit.new_vstk.utils.ParamValidater;
import java.util.Properties;

public class BaseHttpClient {
	 public JITLog logger = JITLogFactory.getLog(getClass());
	  protected NewConfig config;
	  protected IRequestFormer requestFormer;
	  protected IResponseFormer responseFormer;
	  protected IBodyContentFormer bodyContentFormer;
	  protected HttpClientSender sender;

	  public BaseHttpClient()
	    throws NewCSSException
	  {
	    this("cssconfig.properties");
	  }

	  public BaseHttpClient(NewConfig config) {
	    this.config = config;

	    this.bodyContentFormer = BodyContentFormerFactory.getBodyContentFormer(config);
	    this.requestFormer = FormerFactory.getRequestFormer(config);
	    this.responseFormer = FormerFactory.getResponseFormer(config);
	    this.sender = new HttpClientSender(config);
	  }

	  public BaseHttpClient(Properties properties) throws NewCSSException {
	    this.config = new NewConfig();
	    this.config.initConfigParams(properties);

	    this.bodyContentFormer = BodyContentFormerFactory.getBodyContentFormer(this.config);
	    this.requestFormer = FormerFactory.getRequestFormer(this.config);
	    this.responseFormer = FormerFactory.getResponseFormer(this.config);
	    this.sender = new HttpClientSender(this.config);
	  }

	  public BaseHttpClient(String path) throws NewCSSException
	  {
	    if (ParamValidater.isNull(path)) {
	      return;
	    }
	    this.config = ConfigFactory.getInstance(path);

	    this.bodyContentFormer = BodyContentFormerFactory.getBodyContentFormer(this.config);
	    this.requestFormer = FormerFactory.getRequestFormer(this.config);
	    this.responseFormer = FormerFactory.getResponseFormer(this.config);
	    this.sender = HttpClientSenderFactory.getInstance(path, this.config);
	  }

	  public void setConfigPath(String path)
	    throws NewCSSException
	  {
	    if (ParamValidater.isNull(path)) {
	      path = "cssconfig.properties";
	    }
	    this.config = ConfigFactory.getInstance(path);
	    this.bodyContentFormer = BodyContentFormerFactory.getBodyContentFormer(this.config);
	    this.requestFormer = FormerFactory.getRequestFormer(this.config);
	    this.responseFormer = FormerFactory.getResponseFormer(this.config);
	    this.sender = HttpClientSenderFactory.getInstance(path, this.config);
	  }

	  public long getTime()
	  {
	    return System.nanoTime();
	  }

	  public void methodRunTime(long startRunTime, String info)
	  {
	    int runTime = (int)Math.rint((getTime() - startRunTime) * 1.0E-006D);
	    if (runTime > this.config.getRunTime())
	      this.logger.info("runTime[" + runTime + "(msec)],path[" + getClass().getName() + "]" + ",info[" + info + "]");
	  }

	  public NewConfig getConfig()
	  {
	    return this.config;
	  }

	  public void setConfig(NewConfig config) {
	    this.config = config;
	  }

	  public void setRequestFormer(IRequestFormer requestFormer) {
	    this.requestFormer = requestFormer;
	  }

	  public void setResponseFormer(IResponseFormer responseFormer) {
	    this.responseFormer = responseFormer;
	  }

	  public IRequestFormer getRequestFormer() {
	    return this.requestFormer;
	  }

	  public IResponseFormer getResponseFormer() {
	    return this.responseFormer;
	  }

	  public HttpClientSender getSender() {
	    return this.sender;
	  }

	  public void setSender(HttpClientSender sender) {
	    this.sender = sender;
	  }

	  public IBodyContentFormer getBodyContentFormer() {
	    return this.bodyContentFormer;
	  }
}
