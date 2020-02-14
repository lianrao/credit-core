package com.wanda.credit.ds.client.wangshu;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.common.template.PropertyEngine;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年2月23日 上午10:19:34 
 *  
 */
@Service("wdWangShuTokenService")
public class WDWangShuTokenService extends BaseWDWangShuDataSourceRequestor
implements InitializingBean,DisposableBean {
	private  final  Logger logger = LoggerFactory.getLogger(WDWangShuTokenService.class);
	private  Timer timer;
	private String token;
	
	@Autowired
	private PropertyEngine propertyEngine;

    public String getToken(){
    	String always = propertyEngine.readById("wshutoken_get_always");
    	if("1".equals(always)){
    		logger.info("开始获取网数token..");
			try {
				token = getNewToken();
			} catch (Exception e) {
				throw new RuntimeException("token 获取异常",e);
			}
			logger.info("成功获取网数token..");
    	}
    	return token;
    }
    
    public void setToken(String token) {
		this.token = token;
	}

	public String getNewToken() throws Exception{
		boolean flag = "1".equals(propertyEngine.readById("wshutoken_getnew_always"));
		if(flag){
			String url = buildTokenUrl();
	    	Map<String,Object> rspMap = doGetForHttpAndHttps(url,"wangshu token timer", null, null);
	    	String tokenRawStr = (String)rspMap.get("rspstr");
	    	JSONObject jsnObj = (JSONObject) JSONObject.parse(tokenRawStr);
	    	String tokenstr = jsnObj.getString("token");
	    	if(StringUtils.isNotBlank(tokenstr)){
	        	return tokenstr;
	    	}else{
	    		String msg = "网数获取token交易有返回,但找不到token信息";
	    		logger.error("{} 收到交易响应数据: {}",msg,tokenRawStr);
	    		throw new Exception(msg);
	    	}
		}
    	return "";
    }
    
	private String buildTokenUrl() {
		String apitoken = propertyEngine.readById("wdwangshu_apitoken");
		String apikey = propertyEngine.readById("wdwangshu_apikey");
		String url = propertyEngine.readById("wdwangshu_token_url");
		StringBuffer sb = new StringBuffer();
		sb.append(url).append("?apikey=").append(apikey)
		.append("&apitoken=").append(apitoken);
		return sb.toString();
	}
   
	@Override
	public void afterPropertiesSet() throws Exception {		 
		 timer = new Timer();  
		 Tasker tasker = new Tasker(this);
		 String timerStr= propertyEngine.readById("wdws_token_timer");
		 if(StringUtils.isNotBlank(timerStr)){
		     timer.schedule(tasker,30*1000,Long.valueOf(timerStr));
		 }else{
			 /**默认每50分钟去获取一次*/
		     timer.schedule(tasker,30*1000,50*60*1000);
		 }
		 logger.info("获取网数token定时任务启动成功");
	}

	private class Tasker extends TimerTask{
		WDWangShuTokenService service ;
		public Tasker(WDWangShuTokenService zhongshuTokenService) {
			this.service = zhongshuTokenService;
		}
        boolean flag = true;
		@Override
		public void run() {
			try{
				logger.info("开始获取网数token..");
				service.token = service.getNewToken();
				if(!flag)flag = true;
				logger.info("成功获取网数token..");
			}catch(Exception ex){
				flag = false;
				while (!flag){
					try {
						/**间隔5s试一次 直到成功*/
						TimeUnit.SECONDS.sleep(5);
						service.token = service.getNewToken();
						flag = true;						
					} catch (Exception e) {
						flag = false;
						logger.error("获取网数token定时任务执行失败",ex);
					    
					}
				}
				logger.error("获取网数token定时任务执行失败",ex);
			}
		}
		
	}

	@Override
	public void destroy() throws Exception {
	 if(timer != null){
		 timer.cancel();
		 timer = null;
	 }	
	}
}
