package com.wanda.credit.ds.client.jixin;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * author: 
 *   功能：系统解包和组包方法
 */
public class TransUtil {

	private final  static Logger logger = LoggerFactory.getLogger(TransUtil.class);

	static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
	}

	private String merchkey;


	@SuppressWarnings({ "unchecked", "static-access" })
	public static byte[] packet(CommonBean trans,String signkey) throws Exception {
		
		if (signkey != null) {
			String resp = object2String(trans);
			logger.info("original reqdata {}",resp);
			Map<String, String> resMap = mapper.readValue(resp, Map.class);
			String sign = SignUtil.getSign(resMap, signkey);

			trans.setSign(sign);
			logger.info("original reqdata with sign {}",object2String(trans));

		}
		byte[] returnData = object2Byte(trans);

		return returnData;
	}



	public static CommonBean json2Trans(byte[] json) {

		CommonBean trans = null;
		try {
			trans = (CommonBean) mapper.readValue(json, CommonBean.class);
		} catch (Exception e) {
		}
		return trans;
	}

	public static CommonBean json2Trans(String json) {

		CommonBean trans = null;
		try {
			trans = (CommonBean) mapper.readValue(json, CommonBean.class);
		} catch (Exception e) {
		}
		return trans;
	}


	public static String object2json(Object obj) throws Exception {

		if (obj != null) {
			return mapper.writeValueAsString(obj);
		}

		return null;
	}

	public static byte[] object2Byte(Object object) {

		byte[] response = null;
		try {
			response = mapper.writeValueAsBytes(object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public static String object2String(Object o) {

		String response = null;
		try {
			if (null != o) {
				response = mapper.writeValueAsString(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	
}
