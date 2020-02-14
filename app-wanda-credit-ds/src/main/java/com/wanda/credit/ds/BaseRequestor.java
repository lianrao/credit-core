package com.wanda.credit.ds;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description  
 * @author liunan 
 * @version 1.0
 * @createdate 2017年1月24日 上午10:26:07 
 *  
 */
public class BaseRequestor {
	public static ThreadPoolExecutor pool;
	
	public void asynOperate(Runnable runner) {	
		if(pool ==null){
			synchronized (BaseRequestor.class) {
				if(pool ==null){
					pool = new ThreadPoolExecutor(30,500,60,TimeUnit.SECONDS,
				            new LinkedBlockingQueue<Runnable>(1000),new ThreadPoolExecutor.CallerRunsPolicy());
				    pool.allowCoreThreadTimeOut(true);
				}
			}
		}
		pool.execute(runner);	
	}
}
