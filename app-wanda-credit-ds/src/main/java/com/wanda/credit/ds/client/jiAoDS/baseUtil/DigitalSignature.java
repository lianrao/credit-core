package com.wanda.credit.ds.client.jiAoDS.baseUtil;

import java.util.TreeMap;
/**
 * 数字签名，使用SHA1+签名密钥进行签名，
 * 数字签名需要在参数加密前进行签名,
 * 如果某个接口需要做签名验证需要在Controller对应的方法里面调用serverDigitalSignatureCheck
 * 改到在过滤器里面直接验证了
 * @author wushujia
 *
 */
public class DigitalSignature {
	/**
	 * 客户端对参数和流数据进行签名
	 * @param paraMap 如果不需要参数签名那么传空，如果要传那就得全部传
	 * @param headMap 如果不需要head签名那么传空，如果要传那就得全部传
	 * @param str 传输流字符串
	 * @param key 签名key(双方约定好的签名key)
	 * @return 返回值需要放到header里面传给服务端 headName : digitalSignature
	 */
	public static String clientDigitalSignature(TreeMap<String,String> paraMap,TreeMap<String,String> headMap,String str,String key){
		if(str==null||"".equals(str)||"null".equals(str)){
			str = "" ;
		}
		StringBuilder sb = new StringBuilder();
		String p = "b" ;  // a or b a代表进行参数签名
		String h = "b" ;  // a or b a代表进行head签名
		if(paraMap!=null){
			p = "a" ;
			for (String mkey : paraMap.keySet()) {
				sb.append(mkey).append("=").append(paraMap.get(mkey)) ;
		    }
		}
		if(headMap!=null){
			h = "a" ;
			for (String mkey : headMap.keySet()) {
				sb.append(mkey).append("=").append(headMap.get(mkey)) ;
		    }
		}
		sb.append(str).append("~").append(key);
		String sbStr = sb.toString() ;
		String result = EncoderHandler.encode("SHA1", sbStr);
		String signature = p+h+result ;
		System.out.println("sbStr:"+sbStr+";signature:"+signature);
		return signature ;
	}
	
}
