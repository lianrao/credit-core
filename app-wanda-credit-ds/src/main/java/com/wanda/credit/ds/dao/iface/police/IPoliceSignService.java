package com.wanda.credit.ds.dao.iface.police;

import java.util.Map;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.police.Police_Sign_Result;

public interface IPoliceSignService extends IBaseService<Police_Sign_Result>{
	/**
	 * 批量保存静默活体检测以及水印照人脸比对信息
	 * @param score
	 */
	void batchSave(Police_Sign_Result result);
	
	public Map<String, Object> getSignOfDate(String date1);
}
