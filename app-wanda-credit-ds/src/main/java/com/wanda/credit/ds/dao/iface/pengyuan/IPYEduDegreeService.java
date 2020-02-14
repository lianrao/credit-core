package com.wanda.credit.ds.dao.iface.pengyuan;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Py_edu_degree;

public interface IPYEduDegreeService extends IBaseService<Py_edu_degree>{

	/**修改Py_edu_degree 对象的photo_id属性*/
	void updateDegreePhotoId(String id,String photoId);
}

