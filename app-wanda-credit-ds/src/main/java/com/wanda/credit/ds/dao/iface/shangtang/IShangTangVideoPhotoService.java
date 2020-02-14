package com.wanda.credit.ds.dao.iface.shangtang;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.shangtang.ShangTang_videophoto_result;

public interface IShangTangVideoPhotoService extends IBaseService<ShangTang_videophoto_result>{
	/**
	 * 批量保存静默活体检测以及水印照人脸比对信息
	 * @param score
	 */
	void batchSave(List<ShangTang_videophoto_result> result);
	public String queryVideoFile(String trade_id,String param_code);
}
