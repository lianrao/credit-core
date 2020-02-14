package com.wanda.credit.ds.client.policeAuthV2.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fri.ctid.security.jit.Api;
import com.wanda.credit.base.util.ExceptionUtil;
import cn.com.jit.new_vstk.exception.NewCSSException;
import sun.misc.BASE64Encoder;

/**
 * Created by on 2018/3/21.
 * 签名数据
 */
public class DataSignature {
	private final static Logger logger = LoggerFactory
			.getLogger(DataSignature.class);
    private static String certID = "ywzd114";
    protected static String filePath = DataSignature.class.getClassLoader().
    		getResource("").getPath()+"/depends/ds/police/cssconfigV2.properties";
    private static Api api = new Api();
    public static void init(){
    	try{
    		logger.info("{} 初始化公安一所连接开始...");
    		api.initConnection(filePath); 
    		logger.info("{} 初始化公安一所连接完成");
    	}catch(Exception e){
    		logger.info("初始化公安一所连接失败:{}",ExceptionUtil.getTrace(e));
    	}
    }
    //签名方法(吉大正元)
    public  String signature(String trade_id,byte[] data) throws Exception{
    	logger.info("{} 签名开始,文件路径:{}",trade_id,filePath);
    	byte[] signReturn = api.p7Sign(data,certID);
        logger.info("{} 签名完成",trade_id);
        BASE64Encoder be = new BASE64Encoder();
        String sr = new String(be.encodeBuffer(signReturn));
    	
        return sr;
    }

    //验签方法(吉大正元)
    public  boolean encryptEnvelope(byte[] data ,byte [] sign) throws NewCSSException {
        com.fri.ctid.security.bjca.Api.initConnection(filePath);
        boolean result = api.p7SignVerify(data,sign);
        return result;
    }
}