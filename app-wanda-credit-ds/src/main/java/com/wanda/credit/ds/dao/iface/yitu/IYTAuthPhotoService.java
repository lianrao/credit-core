package com.wanda.credit.ds.dao.iface.yitu;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.yitu.Yitu_auth_photo;

public interface IYTAuthPhotoService extends IBaseService<Yitu_auth_photo>{
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<Yitu_auth_photo> result);
}
