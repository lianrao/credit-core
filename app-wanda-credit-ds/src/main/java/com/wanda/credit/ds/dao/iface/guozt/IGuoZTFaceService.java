package com.wanda.credit.ds.dao.iface.guozt;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.guozt.GuoZT_Face_Result;

public interface IGuoZTFaceService extends IBaseService<GuoZT_Face_Result>{
	/**
	 * 批量保存静默活体检测以及水印照人脸比对信息
	 * @param score
	 */
	void batchSave(List<GuoZT_Face_Result> result);
}
