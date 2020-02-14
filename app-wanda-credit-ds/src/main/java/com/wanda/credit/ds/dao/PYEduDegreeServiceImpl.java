package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Py_edu_degree;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduDegreeService;
@SuppressWarnings("serial")
@Service
@Transactional
public class PYEduDegreeServiceImpl  extends BaseServiceImpl<Py_edu_degree> implements IPYEduDegreeService {

	private static String UPDATE_PHOTOID_SQL = "update cpdb_ds.T_DS_PY_EDU_DEGREES set photo_id = ? WHERE ID = ?";
	
	@Override
	public void updateDegreePhotoId(String id, String photoId) {		
           this.daoService.getJdbcTemplate().
           update(UPDATE_PHOTOID_SQL, new Object[]{photoId,id});
	}


}
