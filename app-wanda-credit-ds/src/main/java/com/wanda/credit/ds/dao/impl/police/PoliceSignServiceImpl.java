package com.wanda.credit.ds.dao.impl.police;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.police.Police_Sign_Result;
import com.wanda.credit.ds.dao.iface.police.IPoliceSignService;
@Service
@Transactional
public class PoliceSignServiceImpl  extends BaseServiceImpl<Police_Sign_Result> implements IPoliceSignService{
	private final  Logger logger = LoggerFactory.getLogger(PoliceSignServiceImpl.class);
	@Override
	public void batchSave(Police_Sign_Result result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
	@Override
	public Map<String, Object> getSignOfDate(String date1) {
		String sql =" SELECT SIGN_DATE,SIGN_DATA FROM CPDB_DS.T_DS_POLICE_SIGN WHERE SEARCH_DATE = ? ";
		Map<String, Object> result = this.daoService.getJdbcTemplate().queryForMap(sql, date1);
		return result;
	}
}
