package com.wanda.credit.ds.dao;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.zhongshunew.ZS_Order;
import com.wanda.credit.ds.dao.iface.IZSNewOrderService;

@Service
@Transactional
public class ZSNewOrderServiceImpl extends BaseServiceImpl<ZS_Order> implements IZSNewOrderService {
	private static final long serialVersionUID = 1L;
	@Override
	public Map<String, Object> inCached(ZS_Order order){ //,String months) {
//		String sql = "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM (SELECT CASE  WHEN TO_CHAR(D.CREATED,'yyyymm') =  "
//				+ "  TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
//				+ " WHERE D.KEY = ? AND D.STATUS = '1' AND ACCT_ID=?) ";
		String allparam = order.getENTID()+order.getCREDITCODE()+order.getREGNO()+
				order.getENTNAME()+order.getORGCODE()+order.getENTTYPE()+
				order.getMASK()+order.getVERSION();
		String sql = 
				  "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM "
				+ "  (SELECT CASE WHEN TO_CHAR(D.CREATE_date,'yyyymm') = TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA, "
				+ "     D.ENTID || D.CREDITCODE || D.REGNO || D.ENTNAME || D.ORGCODE || D.ENTTYPE || D.MASK || D.VERSION as allparam"
				+ "  FROM CPDB_DS.T_DS_ZS_NEW_ORDER D "
				+ "  WHERE "
				+ "     D.code='200' and D.ACCT_ID = ? and d.entname=?)"
				+ "WHERE allparam = ? ";
		Map<String, Object> resultMap = 
				this.daoService.getJdbcTemplate().queryForMap(sql,order.getAcct_id(),order.getENTNAME(),allparam);
	    return resultMap;
	}
	@Override
	public Map<String, Object> inCachedDs(ZS_Order order){ //,String months) {
//		String sql = "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM (SELECT CASE  WHEN TO_CHAR(D.CREATED,'yyyymm') =  "
//				+ "  TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
//				+ " WHERE D.KEY = ? AND D.STATUS = '1') ";
		String allparam = order.getENTID()+order.getCREDITCODE()+order.getREGNO()+
				order.getENTNAME()+order.getORGCODE()+order.getENTTYPE()+
				order.getMASK()+order.getVERSION();
		String sql = 
				  "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM "
				+ "  (SELECT CASE WHEN TO_CHAR(D.CREATE_date,'yyyymm') = TO_CHAR(SYSDATE,'yyyymm') THEN  1 ELSE  0 END STA, "
				+ "     D.ENTID || D.CREDITCODE || D.REGNO || D.ENTNAME || D.ORGCODE || D.ENTTYPE || D.MASK || D.VERSION as allparam"
				+ "  FROM CPDB_DS.T_DS_ZS_NEW_ORDER D "
				+ "  WHERE "
				+ "     D.code='200' and d.entname=?)"
				+ "WHERE allparam = ? ";
		Map<String, Object> resultMap = 
				this.daoService.getJdbcTemplate().queryForMap(sql,order.getENTNAME(),allparam);
	    return resultMap;
	}

	@Override
	public void saveCorpInfo(String trade_id, String ds_id, String content, String name, String creditcode,
			String regno, String orgcode) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO CPDB_DS.T_DS_STORE_CLOB(ID,TRADE_ID,DS_ID,CONTENT,QRY1,QRY2,QRY3,QRY4)"
				+ " VALUES(CPDB_DS.SEQ_T_DS_STORE_CLOB.NEXTVAL,?,?,?,?,?,?,?)";
		this.daoService.getJdbcTemplate().update(sql,trade_id,ds_id,content,name,creditcode,regno,orgcode);
	}
	
}
