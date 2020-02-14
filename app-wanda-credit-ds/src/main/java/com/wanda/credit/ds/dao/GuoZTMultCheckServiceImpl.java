package com.wanda.credit.ds.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.base.util.BeanUtil;
import com.wanda.credit.ds.dao.domain.Guozt_Mult_Check_Result;
import com.wanda.credit.ds.dao.iface.IGuoZTMultCheckService;

@Service
@Transactional
public class GuoZTMultCheckServiceImpl  extends BaseServiceImpl<Guozt_Mult_Check_Result> implements IGuoZTMultCheckService{
	private final  Logger logger = LoggerFactory.getLogger(GuoZTMultCheckServiceImpl.class);
	@Override
	public Guozt_Mult_Check_Result inCached(String name, String cardNo) {
		Guozt_Mult_Check_Result guoZTCheck = new Guozt_Mult_Check_Result();
		String sql = "SELECT T1.ID,T1.TRADE_ID,T1.CARDNO,T1.NAME,T1.CARD_CHECK,T1.NAME_CHECK,T1.SEX,T1.BIRTH_DAY,T1.ADDRESS,T1.CITY, "
				+ " T1.NATIONS,T1.WYBS_NUMBER,T1.ERROR_MESG,T1.STATUS,T1.IMAGE_FILE FROM CPDB_DS.T_DS_GUOZT_MULT_RESULT T1  "
				+ "  ,(SELECT D.CARDNO, MAX(D.CREATE_TIME) AS CREATE_TIME  FROM CPDB_DS.T_DS_GUOZT_MULT_RESULT D  WHERE D.CARDNO = ? AND D.NAME=? AND D.STATUS IN ('00', '01') "
				+" GROUP BY D.CARDNO) T2 WHERE T1.CARDNO = ? AND T1.NAME=? AND T1.CREATE_TIME = T2.CREATE_TIME AND T1.STATUS IN ('00', '01')  AND ROWNUM<=1 ";
		Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, cardNo,name,cardNo,name);
		try {
			guoZTCheck = (Guozt_Mult_Check_Result) BeanUtil.mapToObject(resultMap,Guozt_Mult_Check_Result.class);
		} catch (Exception e) {
			logger.error("查询数据失败，详细信息:{}" , e.getMessage());
		}
	    return guoZTCheck;
	}

	@Override
	public boolean inCachedMult(String name, String cardNo,int days) {		
		String sql ="SELECT COUNT(1) CNT FROM CPDB_DS.T_DS_GUOZT_MULT_RESULT WHERE CARDNO = ?  AND NAME = ? AND CREATE_TIME>=(sysdate-?) AND STATUS IN ('00','01')";
		Integer result = this.daoService.findOneBySql(sql, new Object[]{cardNo,name,days},Integer.class);
		logger.info("查询数据量01:{}" , result);
		return result>0;
	}
	@Override
	public void batchSave(List<Guozt_Mult_Check_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
