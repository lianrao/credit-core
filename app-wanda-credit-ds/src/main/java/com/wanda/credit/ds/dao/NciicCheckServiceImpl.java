package com.wanda.credit.ds.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
@Service
@Transactional
public class NciicCheckServiceImpl  extends BaseServiceImpl<Nciic_Check_Result> implements INciicCheckService{
	private final  Logger logger = LoggerFactory.getLogger(NciicCheckServiceImpl.class);
	@Override
	public Map<String, Object> inCached(String name, String cardNo) {
		String sql = "SELECT CARD_CHECK, NAME_CHECK, IMAGE_FILE, STATUS, ERROR_MESG  FROM CPDB_DS.T_DS_NCIIC_RESULT T1 "
				+ " ,(SELECT D.CARDNO, MAX(D.CREATE_TIME) AS CREATE_TIME  FROM CPDB_DS.T_DS_NCIIC_RESULT D  WHERE D.CARDNO = ? AND D.NAME=? AND D.STATUS IN ('00', '01') "
				+ " GROUP BY D.CARDNO) T2 WHERE T1.CARDNO = ? AND T1.NAME=? AND T1.CREATE_TIME = T2.CREATE_TIME AND T1.STATUS IN ('00', '01')  AND ROWNUM<=1 ";
		Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, cardNo,name,cardNo,name);
	    return resultMap;
	}
	@Override
	public Map<String, Object> inCachedJuHe(String name, String cardNo) {
		String sql = "SELECT CARD_CHECK, NAME_CHECK, IMAGE_FILE, STATUS, ERROR_MESG  FROM CPDB_DS.T_DS_NCIIC_RESULT T1 "
				+ " ,(SELECT D.CARDNO, MAX(D.CREATE_TIME) AS CREATE_TIME  FROM CPDB_DS.T_DS_NCIIC_RESULT D  WHERE D.CARDNO = ? AND D.NAME=? AND D.STATUS IN ('00', '01','04','05') "
				+ " GROUP BY D.CARDNO) T2 WHERE T1.CARDNO = ? AND T1.NAME=? AND T1.CREATE_TIME = T2.CREATE_TIME AND T1.STATUS IN ('00', '01','04','05')  AND ROWNUM<=1 ";
		Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, cardNo,name,cardNo,name);
	    return resultMap;
	}

	@Override
	public void batchSave(List<Nciic_Check_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean inCachedCount(String name, String cardNo,int days) {		
		String sql ="SELECT COUNT(1) CNT FROM CPDB_DS.T_DS_NCIIC_RESULT WHERE CARDNO = ?  AND NAME = ? AND CREATE_TIME>=(sysdate-?) AND STATUS IN ('00','01')";
		Integer result = this.daoService.findOneBySql(sql, new Object[]{cardNo,name,days},Integer.class);
		return result>0;
	}
	@Override
	public boolean inCachedCountJuHe(String name, String cardNo,int days) {		
		String sql ="SELECT COUNT(1) CNT FROM CPDB_DS.T_DS_NCIIC_RESULT WHERE CARDNO = ?  AND NAME = ? AND CREATE_TIME>=(sysdate-?) AND STATUS IN ('00','01','04','05')";
		Integer result = this.daoService.findOneBySql(sql, new Object[]{cardNo,name,days},Integer.class);
		return result>0;
	}
	@Override
	public void updateCard(String name, String cardNo,String photo){
		try {
			String sql = "UPDATE T_DS_NCIIC_RESULT T SET T.IMAGE_FILE=?,T.UPDATE_TIME=SYSDATE WHERE T.CARDNO=? AND T.NAME=?  AND T.STATUS='00' ";
			this.daoService.getJdbcTemplate().update(sql,photo,cardNo,name);		
		}catch (Exception e) {
			logger.error("更新数据失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
