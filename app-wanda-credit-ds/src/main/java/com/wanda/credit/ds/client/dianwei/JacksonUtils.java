package com.wanda.credit.ds.client.dianwei;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;

/**
 * 
 * Jackson 工具类
 *
 */
public class JacksonUtils {

	/**
	 * 
	 * @param val
	 * @param cls
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static <T> T parseJsonFromString(String val, Class<T> cls) throws JsonParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(val, cls);
	}

	/**
	 * 
	 * @param obj
	 * @param filters
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String serialObject(Object obj, FilterProvider filters) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		if (filters != null) {
			mapper.setFilterProvider(filters);
		}
		return mapper.writeValueAsString(obj);
	}

	/**
	 * 
	 * @param val
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static JsonNode parseJsonFromString(String val) throws JsonParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(val);
	}

	/**
	 * 
	 * @param obj
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String serialObject(Object obj) throws JsonProcessingException {
		return serialObject(obj, null);
	}

}
