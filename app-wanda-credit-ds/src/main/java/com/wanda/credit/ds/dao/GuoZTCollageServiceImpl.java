package com.wanda.credit.ds.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.IGuoZCollageService;

@Service
@Transactional
public class GuoZTCollageServiceImpl extends BaseServiceImpl<Nciic_Check_Result> implements IGuoZCollageService {
	private final Logger logger = LoggerFactory.getLogger(GuoZTCollageServiceImpl.class);
	@Override
	public List<Map<String, Object>> getCollageByName(String name) {
		String sql ="SELECT D.COLLEGE,D.ADDRESS,D.CREATEDATE,D.COLGLEVEL,D.CHARACTER,D.COLGTYPE,D.IS211, "
				+" D.MANAGEDEPT  FROM CPDB_MK.T_ETL_PY_EDU_COLLEGES D WHERE D.COLLEGE=? ";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql, name);
		return result;
	}

}
