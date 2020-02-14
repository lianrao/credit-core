package com.wanda.credit.ds.client.jiAoDS.baseUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 单例
 * @author wushujia
 *
 */
public class Token {
	private final Map<String,String> tokenIdMap = new ConcurrentHashMap<String,String>() ;
	private final Map<String,Long> getTokenTimeMap = new ConcurrentHashMap<String,Long>() ;
	private final Map<String,String> digitalSignatureKeyMap = new ConcurrentHashMap<String,String>() ;
	private Token(){}
	private final static Token token = new Token();
	public static Token getInstance() {
        return token ;
    }
	public Map<String, String> getTokenIdMap() {
		return tokenIdMap;
	}
	public Map<String, Long> getGetTokenTimeMap() {
		return getTokenTimeMap;
	}
	public Map<String, String> getDigitalSignatureKeyMap() {
		return digitalSignatureKeyMap;
	}
}
