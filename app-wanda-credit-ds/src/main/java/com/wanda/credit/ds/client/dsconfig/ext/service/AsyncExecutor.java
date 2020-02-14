package com.wanda.credit.ds.client.dsconfig.ext.service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description
 * @author wuchsh
 * @version 1.0
 * @createdate 2017年3月8日 下午1:57:16
 * 
 */
public class AsyncExecutor {

	public static ThreadPoolExecutor pool;

	public static void asynOperate(Runnable runner) {
		if (pool == null) {
			synchronized (AsyncExecutor.class) {
				if (pool == null) {
					pool = new ThreadPoolExecutor(0, 50, 30, TimeUnit.SECONDS,
							new LinkedBlockingQueue<Runnable>(2));
				}
			}
		}
		pool.execute(runner);
	}
}
