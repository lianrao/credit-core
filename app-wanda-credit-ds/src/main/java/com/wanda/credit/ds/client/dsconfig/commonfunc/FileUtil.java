package com.wanda.credit.ds.client.dsconfig.commonfunc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.ds.client.dsconfig.DefaultConfigurableDataSourceRequestor;
import com.wanda.credit.dsconfig.main.ResolveContext;
/** 
* @Title: 加解密工具类
* @Package com.bill99.ifs.crs.util
* @Description: 加解密工具类
* @author wuchsh
* @date 2017年02月04日 下午15:16:36 
* @version V1.0
*/ 
@Service
public class FileUtil {	
	private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	private static IExecutorFileService fileService;

	public IExecutorFileService getFileEngine() {
		return fileService;
	}
   
	@Autowired
	public void setFileSerivce(IExecutorFileService fileEngine) {
		FileUtil.fileService = fileEngine;
	}

	public String upload(String base64,String type,String area){
	   try {
		String fileId = fileService.upload(base64,FileType.match(type),FileArea.match(area),
				   ResolveContext.getTradeId());
		return fileId;
		} catch (Exception e) {
			logger.error("文件上传异常",e);
		}
	   //如果是null freemark 会出错
	   return "";
	}
	
	public String download(String fileId){
		   try {
				String base64 = fileService.download(fileId,ResolveContext.getTradeId());// 根据ID从征信存储区下载照片
			return base64;
			} catch (Exception e) {
				logger.error("文件下载异常",e);
			}
		   return "";
		}

}
