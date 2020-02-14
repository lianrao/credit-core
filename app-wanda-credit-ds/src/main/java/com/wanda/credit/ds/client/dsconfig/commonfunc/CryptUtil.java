package com.wanda.credit.ds.client.dsconfig.commonfunc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanda.credit.api.iface.IExecutorSecurityService;
/** 
* @Title: 加解密工具类
* @Package com.bill99.ifs.crs.util
* @Description: 加解密工具类
* @author wuchsh
* @date 2017年02月04日 下午15:16:36 
* @version V1.0
*/ 
@Service
public class CryptUtil {
	
	private static IExecutorSecurityService synchExecutorService;

	public IExecutorSecurityService getSynchExecutorService() {
		return CryptUtil.synchExecutorService;
	}
	
	@Autowired
	public  void setSynchExecutorService(
			IExecutorSecurityService synchExecutorService) {
		CryptUtil.synchExecutorService = synchExecutorService;
	}

	
	public static String encrypt(String str) {
		return synchExecutorService.encrypt(str);
	}

	
	public static String decrypt(String str) {
		return synchExecutorService.decrypt(str);
	}

	
}
