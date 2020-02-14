package com.wanda.credit.ds.client.dsconfig.services;

import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.ds.action.DSConfigAction;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @description
 * @author wuchsh
 * @version 1.0
 * @createdate 2017年4月27日 下午8:38:30
 * 
 */
public class RedisAppender extends AbstractAppender {
	private static Logger logger = LoggerFactory.getLogger(DSConfigAction.class);

	private static RedisAppender instance;
	private static ShardedJedisPool pool;  
	
	public RedisAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
		super(name, filter, layout);

	}

	@PluginFactory
	public static RedisAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filters") Filter filter) {
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}

		instance = new RedisAppender(name, filter, layout);
		return instance;
	}

	public static RedisAppender getInstance() {
		return instance;
	}

	public static void initJedisPool(ShardedJedisPool pool){
		if(RedisAppender.pool == null){
			RedisAppender.pool = pool;
		}		
	}
	@Override
	public void append(final LogEvent event) {
		byte[] bytes = getLayout().toByteArray(event);
		ShardedJedis jedis = pool.getResource();
		try {
			jedis.ltrim("loggerlist", -4999, -1);
			/*if(reachMaxLength(jedis)){
				jedis.rpop("loggerlist");				
			}*/
			jedis.rpush("loggerlist", new String(bytes));
			jedis.expire("loggerlist", 60*10);
//			logger.info("push to redis length: {}",bytes.length);
		} catch (Exception e) {
			logger.error("append logEvent error",e);
		} finally {
			jedis.close();
		}
        
	}

	private boolean reachMaxLength(Jedis jedis) {
		Long length = jedis.llen("loggerlist");
		return length >= 5000 ;
	}
}