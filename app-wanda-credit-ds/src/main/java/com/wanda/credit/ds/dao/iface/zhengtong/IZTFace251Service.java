package com.wanda.credit.ds.dao.iface.zhengtong;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.zhengtong.ZT_Face_Result;

public interface IZTFace251Service extends IBaseService<ZT_Face_Result>{
	/**
	 * 客户信息认证-高清人像比对服务
	 * @param score
	 */
	void save(List<ZT_Face_Result> result);
}
