package com.wanda.credit.ds.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Zhongan_Check_Result;
import com.wanda.credit.ds.dao.iface.IZhonganService;
@Service
@Transactional
public class ZhonganServiceImpl  extends BaseServiceImpl<Zhongan_Check_Result> implements IZhonganService{
	private final  Logger logger = LoggerFactory.getLogger(ZhonganServiceImpl.class);

	@Override
	public Map<String, Object> inCached(String name, String cardNo) {
		String sql = "SELECT T1.MOBILE,T1.RESULT_CODE  FROM CPDB_DS.T_DS_ZHONGAN_RESULT T1  ,(SELECT D.CARDNO, MAX(D.CREATE_TIME) AS CREATE_TIME  FROM CPDB_DS.T_DS_ZHONGAN_RESULT D "
				+ "  WHERE D.CARDNO = ? AND D.NAME=? AND D.RESULT_CODE = '1001' GROUP BY D.CARDNO) T2 WHERE T1.CARDNO =? AND T1.NAME=? "
				+ "  AND T1.CREATE_TIME = T2.CREATE_TIME AND T1.RESULT_CODE = '1001'  AND ROWNUM<=1  ";
		Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, cardNo,name,cardNo,name);
	    return resultMap;
	}
	
	@Override
	public boolean inCachedCount(String name, String cardNo) {		
		String sql ="SELECT COUNT(1) CNT FROM CPDB_DS.T_DS_ZHONGAN_RESULT WHERE CARDNO = ?  AND NAME = ? AND RESULT_CODE = '1001' ";
		Integer result = this.daoService.findOneBySql(sql, new Object[]{cardNo,name},Integer.class);
		return result>0;
	}
	
	@Override
	public void batchSave(List<Zhongan_Check_Result> result) {
		// TODO Auto-generated method stub
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}

}
