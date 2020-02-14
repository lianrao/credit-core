package com.wanda.credit.ds.client.dsconfig.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * @description
 * @author wuchsh
 * @version 1.0
 * @createdate 2017年1月18日 上午9:57:27
 * 
 */
public class DynamicLogger {
	private org.slf4j.Logger logger1 = LoggerFactory
			.getLogger(DynamicLogger.class);

	public void printLogInfo() throws IOException {
		/*
		 * Logger logger = Logger.getLogger("root");
		 * logger.removeAllAppenders(); logger.setAdditivity(false);//设置继承输出root
		 * Appender appender = null; PatternLayout layout = new PatternLayout();
		 * layout.setConversionPattern(
		 * "[%p]%d{yyyy-MM-dd HH:mm:ss,SSS} [%c]-[%M line:%L]%n %m%n"); appender
		 * =new
		 * DailyRollingFileAppender(layout,"e:/temp/data/ddddd.log","yyyy-MM-dd"
		 * ); logger.addAppender(appender); logger.setLevel(Level.DEBUG);
		 * logger.info("log创建成功。。。。。"); logger1.info("logger111111创建成功。。。。。");
		 * logger.warn("log创建成功。。。。。"); logger1.warn("logger111111创建成功。。。。。");
		 * logger.error("log创建成功。。。。。"); logger1.error("logger111111创建成功。。。。。");
		 * logger.debug("log创建成功。。。。。"); logger1.debug("logger111111创建成功。。。。。");
		 */

	}

	@Test
	public void testClassOrPackage() {
		logger1 = LoggerFactory.getLogger("xxx");

		logger1.info("aa1aa1aa1aa1aa1aa1aa1aa1");
	}

	@Test
	public void testLogOnlyToFile() {/*
		Logger logger = Logger.getLogger("aaa1");
		logger.info("aa1aa1aa1aa1aa1aa1aa1aa1");
		logger = Logger.getLogger("aaa2");
		logger.info("aa2aa2aa2aa2aa2aa2aa2aa2aa2aa2aa2");
	*/}

	@Test
	public void testRoot() {/*
		Logger logger1 = Logger.getRootLogger();
		logger1.addAppender(crateSocketTempAppender("localhost", 17777));
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("吴常胜我是杀手湿湿的");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");
		logger1.info("root1root1root1root1");

		logger1.info("root2root2root2root2");
		try {
			int a = 2 / 0;
		} catch (Exception e) {
			logger1.error("erroexception ", e);
		} finally {
			logger1.removeAppender("socketTempAppender");

		}

	*/}

	@Test
	public void testRootWithThrowMessage() {/*
		Logger logger1 = Logger.getRootLogger();
		logger1.addAppender(crateSocketTempAppender("localhost", 17777));
		// logger1.info("root1root1root1root1");
		int i = 0;
		try {
			int j = 10 / i;

		} catch (Exception e) {
			logger1.error("出错了", e);
		}
		try {
			logger1.info("root1root1root1root1");
			logger1.info("root1root1root1root1");
			logger1.info("root1root1root1root1");
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger1.removeAppender("socketTempAppender");
		logger1.info("root2root2root2root2");
	*/}

	private Appender crateSocketTempAppender(String host, int port) {
		/*
		 * Appender appender = new SocketAppender(host,port);
		 * appender.setName("socketTempAppender"); PatternLayout layout = new
		 * PatternLayout(); layout.setConversionPattern(
		 * "[%p]%d{yyyy-MM-dd HH:mm:ss,SSS} [%C]-[%M line:%L]%n %m%n");
		 * appender.setLayout(layout);
		 */
		return null;
	}

	@Test
	public void testRootWithThrowMessageToRedis() {
		final LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
		Configuration log4jCfg = ctx.getConfiguration();
	      LoggerConfig rootLoggerCfg = log4jCfg.getLoggerConfig("root");
	      org.apache.logging.log4j.core.Appender redisappender = createRedisTempAppender();
	      rootLoggerCfg.addAppender(redisappender, Level.INFO, null);
	      redisappender.start();
	      try {
	   	   logger1.info("ffffffffff");
		   logger1.info("ffffffffff");
		   logger1.info("ffffffffff");
		   logger1.info("ffffffffff");
		   logger1.info("ffffffffff");
		   logger1.info("ffffffffff");

				int a = 2 / 0;
			} catch (Exception e) {
				logger1.error("erroexception ", e);
			}
	}

	private org.apache.logging.log4j.core.Appender createRedisTempAppender() {
		LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
		Configuration log4jCfg = ctx.getConfiguration();
	    LoggerConfig rootLoggerCfg = log4jCfg.getLoggerConfig("root");
	    rootLoggerCfg.getAppenders().get("RollingFile").getLayout();  
		RedisAppender appender = RedisAppender.createAppender("socketTempAppender"
				,false, rootLoggerCfg.getAppenders().get("RollingFile").getLayout(),
				null);
		return appender;

	}
	
	@Test
	public void testAddAppender(){
	   ByteArrayOutputStream  os = new ByteArrayOutputStream(10000);
	   addAppender(os,"dscfglogger");	
	   logger1.info("ffffffffff");
	   System.out.println(">>>"+os.toString());
	};
	
	void addAppender(final java.io.OutputStream outputStream, final String outputStreamName) {
	    final LoggerContext context = LoggerContext.getContext(false);
	    final Configuration config = context.getConfiguration();
	    final PatternLayout layout = PatternLayout.createDefaultLayout(config);
//	    final org.apache.logging.log4j.core.Appender appender = OutputStreamAppender.createAppender(layout, null, outputStream, outputStreamName, false, true);
	    Appender appender = createRedisTempAppender();
	    appender.start();
	    //config.addAppender(appender);
	    updateLoggers(appender, config);
	}
	private void updateLoggers(final org.apache.logging.log4j.core.Appender appender, final Configuration config) {
	    final Level level = null;
	    final Filter filter = null;
	    for (final LoggerConfig loggerConfig : config.getLoggers().values()) {
	        //loggerConfig.addAppender(appender, level, filter);
	    }
	    config.getRootLogger().addAppender(appender, level, filter);
	}
	public static void main(String[] args) {
		DynamicLogger obj = new DynamicLogger();
		try {
			obj.printLogInfo();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("success!!!");
	}
}
