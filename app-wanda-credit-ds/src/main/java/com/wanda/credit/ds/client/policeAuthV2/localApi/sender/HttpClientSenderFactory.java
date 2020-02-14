package com.wanda.credit.ds.client.policeAuthV2.localApi.sender;
import cn.com.jit.new_vstk.config.NewConfig;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
public class HttpClientSenderFactory {
	private static ConcurrentMap<String, HttpClientSender> senderMap = new ConcurrentHashMap();

	  public static HttpClientSender getInstance(String path, NewConfig config) {
	    HttpClientSender sender = (HttpClientSender)senderMap.get(path);
	    if (sender == null) {
	      synchronized (HttpClientSenderFactory.class) {
	        if (sender == null) {
	          sender = new HttpClientSender(config);
	          senderMap.put(path, sender);
	        }
	      }
	    }
	    return sender;
	  }

	  public static void reloadSenderMap(String path, NewConfig config) {
	    HttpClientSender sender = null;
	    synchronized (HttpClientSenderFactory.class) {
	      sender = new HttpClientSender(config);
	      senderMap.put(path, sender);
	    }
	  }
}
