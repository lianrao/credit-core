package com.wanda.credit.ds.client.dsconfig.test.service;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.wanda.credit.dsconfig.main.ResolveContext;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年3月24日 下午1:40:11 
 *  
 */

@Service
public class MockService implements InitializingBean{
	private final  Logger logger = LoggerFactory.getLogger(MockService.class);
    private Map<String ,Object> dsdata = new HashMap<String ,Object>();
    
   @Override
	public void afterPropertiesSet() throws Exception {
		try {
			URL url = MockService.class.getResource("/mock");
			File dir;
			logger.info("corpdir {}",url.toURI());
			dir = new File(url.toURI());
			int i=0;
			for (File item : dir.listFiles(new java.io.FileFilter() {				
				@Override
				public boolean accept(File pathname) {
					if(pathname.getName().endsWith("txt"))return true;
					return false;
				}
			})){
				i++;
				String key = item.getName();
				key = key.substring(0,key.indexOf(".txt"));
				dsdata.put(key, IOUtils.toString(new FileInputStream(item)));
			}			
			
			logger.info("已经加载了 {} 个数据源",i);
		
		} catch (Exception e) {
			logger.error(">>>>>>error",e);
		}
	
	}
   
   public String mockRspData(Object key){
	   String orikey = null;
	   if(key instanceof TemplateModel){
		   try {
			   orikey = 
					   DeepUnwrap.unwrap((TemplateModel)key).toString();
		} catch (TemplateModelException e) {
			logger.error(">>>>>>error",e);
		}
	   }else{
		   orikey = key.toString();
	   }
	   if(dsdata.get(orikey) == null){
		   logger.error("{} mockRspData error {}",ResolveContext.getTradeId(),orikey);
	   }
	   return (String) dsdata.get(orikey);
   }
   
   public String sleep(){
	   try {
		Thread.sleep(500);
	} catch (InterruptedException e) {
		logger.error("sleep error",e);
	}
	   return "aa";   
   }
   
   public static void main(String[] args) {
	
   }
}
