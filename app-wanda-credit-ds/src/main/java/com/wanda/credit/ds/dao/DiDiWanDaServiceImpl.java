package com.wanda.credit.ds.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.IDiDiWanDaService;
@Service
@Transactional
public class DiDiWanDaServiceImpl  extends BaseServiceImpl<Nciic_Check_Result> implements IDiDiWanDaService{
	private final  Logger logger = LoggerFactory.getLogger(DiDiWanDaServiceImpl.class);
	@Override
	public List<Map<String, Object>> queryCrime(String cardNo, String trade_ids) {
		String sql ="SELECT CASECODE,CASEMONEY,CASEDATE,CASETIMES "
				+" FROM CPDB_MK.T_MK_DIDIBLACK_RESULT D WHERE D.TRADE_ID=? AND D.CARDNO=? ";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql, trade_ids,cardNo);
		return result;
	}
	

	@Override
	public List<Map<String, Object>> queryWhite(String cardNo, String trade_ids) {
		String sql ="SELECT D.CAR_F CARSYMB,D.P2P_AMT FINASSET,D.WD_EMP IDSYMB FROM CPDB_MK.T_ETL_DIDI_CREDITFLAG D WHERE D.IDCARD=? ";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql,cardNo);
		return result;
	}
}
