package com.wanda.credit.ds.dao;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Order;
import com.wanda.credit.ds.dao.iface.IZSOrderService;

@Service
@Transactional
public class ZSOrderServiceImpl extends BaseServiceImpl<ZS_Order> implements IZSOrderService {
	@Override
	public Map<String, Object> inCached(String key,String acct_id,int months) {
		String sql = "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM (SELECT CASE  WHEN TO_CHAR(D.CREATED,'yyyymm') =  "
				+ "  TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
				+ " WHERE D.KEY = ? AND D.STATUS = '1' AND ACCT_ID=?) ";
		Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, key,acct_id);
	    return resultMap;
	}
	@Override
	public Map<String, Object> inCachedDs(String key,int months) {
		String sql = "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM (SELECT CASE  WHEN TO_CHAR(D.CREATED,'yyyymm') =  "
				+ "  TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
				+ " WHERE D.KEY = ? AND D.STATUS = '1') ";
		Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, key);
	    return resultMap;
	}
	@Override
	public Map<String, Object> inCachedProd(String key,String key_new,String acct_id) {
		String sql = "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM (SELECT CASE  WHEN TO_CHAR(D.CREATED,'yyyymm') =  "
				+ "  TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
				+ " WHERE D.KEY = ? AND D.STATUS = '1' AND ACCT_ID=?  UNION SELECT CASE  WHEN TO_CHAR(D.CREATED,'yyyymm') =  "
				+ "  TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
				+ "WHERE D.KEY = ? AND D.STATUS = '1' AND ACCT_ID=?) ";
		Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, key,acct_id,key_new,acct_id);
	    return resultMap;
	}
	@Override
	public Map<String, Object> inCachedDsNew(String key,String key_new) {
		String sql = "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM (SELECT CASE  WHEN TO_CHAR(D.CREATED,'yyyymm') =  "
				+ "  TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
				+ " WHERE D.KEY = ? AND D.STATUS = '1' UNION SELECT CASE  WHEN TO_CHAR(D.CREATED,'yyyymm') = "
				+ " TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D"
				+ " WHERE D.KEY = ? AND D.STATUS = '1') ";
		Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, key,key_new);
	    return resultMap;
	}
	@Override
	public Map<String, Object> inCacheForQueryByPositionCachedDs(String key,String acct_id) {
		String sql = "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM (SELECT CASE  WHEN to_char(D.CREATED,'yyyy-mm') = to_char(SYSDATE,'yyyy-mm')  "
				+ " THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
				+ " WHERE D.KEY = ? AND D.STATUS = '1' AND ACCT_ID=?) ";
		Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql,key,acct_id);
	    return resultMap;
	}
	
}
