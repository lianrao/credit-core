package com.wanda.credit.ds;


import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.common.util.ModelUtils;
import com.wanda.credit.ds.client.guoztCar.BaseGuoZTCarSourcesRequestor;
import com.wanda.credit.ds.client.nciic.BaseNciicDataSourceRequestor;
import com.wanda.credit.ds.client.pengyuan.BasePengYuanDataSourceRequestor;
import com.wanda.credit.ds.client.policeAuthV2.data.DataSignature;
import com.wanda.credit.ds.client.policeAuthV2.data.DataSignatureLocal;
import com.wanda.credit.ds.client.qianhai.BaseQHDataSourceRequestor;
import com.wanda.credit.ds.client.yinlian.BaseYinlianDSRequestor;
import com.wanda.credit.ds.client.yitu.BaseYiTuDataSourceRequestor;
import com.wanda.credit.ds.client.yituNew.BaseYiTuNewSourceRequestor;
import com.wanda.credit.ds.client.yixin.BaseYiXinDataSourceRequestor;
import com.wanda.credit.ds.client.yuanjian.BaseYuanJSourceRequestor;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.client.zhongshu.BaseZSDataSourceRequestor;

public class StartupListener implements ServletContextListener{

	private static Logger logger = LoggerFactory.getLogger(StartupListener.class);
	/**
	 * start
	 * @throws UnknownHostException 
	 */
	public void start(){
		try {
			logger.info("==============================");
			logger.info("        Ds包初始化启动          ");
			this.init();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Ds包初始化启动失败! e:" + e);
		}
		logger.info("==============================");
		try {
			logger.info("服务暴露的ip: "+ java.net.InetAddress.getLocalHost().getHostAddress());
			logger.info("服务器时间: "+ DateUtil.getSimpleDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			logger.info("操作系统: "+ System.getProperty("os.name"));
			logger.info("==============================");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			logger.error("Get IP failed! e:" + e);
		}
	}
	
	public void init() throws Exception{
		ModelUtils.init(Conts.KEY_SYS_MODEL_CODE_PROPERTY_DEFINE); 
		BaseNciicDataSourceRequestor.init();
		logger.info("1.远程服务-公安-初始化连接资源成功");
		BaseYiTuDataSourceRequestor.init();
		BaseYiTuNewSourceRequestor.init();
		logger.info("2.远程服务-依图-初始化连接资源成功");
		BaseZSDataSourceRequestor.init();
		logger.info("3.远程服务-中数-初始化连接资源成功");	
		BaseGuoZTCarSourcesRequestor.init();
		logger.info("4.远程服务-国政通租车-初始化连接资源成功");
		BaseZTDataSourceRequestor.init();
		logger.info("6.远程服务-政通-初始化连接资源成功");
		BasePengYuanDataSourceRequestor.init();
		logger.info("7.远程服务-鹏元-初始化连接资源成功");
		BaseQHDataSourceRequestor.init();
		logger.info("8.远程服务-前海-初始化连接资源成功");
		BaseYiXinDataSourceRequestor.init();
		logger.info("9.远程服务-宜信-初始化连接资源成功");
        BaseYinlianDSRequestor.init();
        logger.info("10.远程服务-银总联-初始化连接资源成功");
        BaseYuanJSourceRequestor.init();
        logger.info("12.远程服务-远鉴-初始化连接资源成功");
        DataSignature.init();
        DataSignatureLocal.init();
        logger.info("13.远程服务-公安一所-初始化连接资源成功");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		this.start();
	}

}
