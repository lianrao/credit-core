package com.wanda.credit.ds.client.jiAoDS;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.template.PropertyEngine;

/**
 * @description  
 * @author liunan
 * @version 1.0
 * @createdate 2018年8月16日 上午10:19:34 
 *  
 */
@Service("GeoTokenService")
public class GeoTokenService extends BaseJiaoDs
implements InitializingBean,DisposableBean {
	private  final  Logger logger = LoggerFactory.getLogger(GeoTokenService.class);
	private  Timer timer;
	private String token;
	
	@Autowired
	private PropertyEngine propertyEngine;

    public String getToken(String trade_id){
    	logger.info("开始获取集奥token..");
		try {
			String isGetNewToken = propertyEngine.readById("ds_jiao_getToken_flag");
			if("1".equals(isGetNewToken)){
				token = getToken(trade_id,true);
			}else{
				token = "";
			}
			
		} catch (Exception e) {
			throw new RuntimeException("集奥token获取异常",e);
		}
		logger.info("成功获取集奥token..");
    	return token;
    }
    
    public void setToken(String token) {
		this.token = token;
	}
	@Override
	public void afterPropertiesSet() throws Exception {		 
		 timer = new Timer();  
		 Tasker tasker = new Tasker(this);
		 /**默认每50分钟去获取一次*/
	     timer.schedule(tasker,30*1000,10*60*60*1000);
		 logger.info("获取集奥token定时任务启动成功");
	}

	private class Tasker extends TimerTask{
		GeoTokenService service ;
		public Tasker(GeoTokenService geoTokenService) {
			this.service = geoTokenService;
		}
		final String trade_id = StringUtil.getRandomNo();
        boolean flag = true;
		@Override
		public void run() {
			try{
				logger.info(trade_id+" 开始获取集奥token..");
				service.token = service.getToken(trade_id);
				if(!flag)flag = true;
				logger.info(trade_id+" 成功获取集奥token..");
			}catch(Exception ex){
				flag = false;
				while (!flag){
					try {
						/**间隔5s试一次 直到成功*/
						TimeUnit.SECONDS.sleep(5);
						service.token = service.getToken(trade_id);
						flag = true;						
					} catch (Exception e) {
						flag = false;
						logger.error("获取集奥token定时任务执行失败",ex);
					    
					}
				}
				logger.error("获取集奥token定时任务执行失败",ex);
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
