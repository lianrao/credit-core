package com.wanda.credit.ds.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpBranch;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpManagement;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpNational;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpPenaltyInfos;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpReq;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpRsp;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpShareholder;
import com.wanda.credit.ds.dao.iface.IJieWeiQueryCorpInfoService;

@Service
@Transactional
public final class JieWeiQueryCorpInfoServiceImpl implements
		IJieWeiQueryCorpInfoService {

	@Autowired
	private DaoService daoService;

	@Override
	public void addCorpReq(JW_CorpReq corpReq) {
		daoService.create(corpReq);

	}

	@Override
	public void addCorpRsp(JW_CorpRsp corpRsp) {
		daoService.create(corpRsp);

	}

	@Override
	public void addCorpBranch(JW_CorpBranch corpBranch) {
		daoService.create(corpBranch);
	}

	@Override
	public void addCorpManagement(JW_CorpManagement corpManagement) {
		daoService.create(corpManagement);
	}

	@Override
	public void addCorpNational(JW_CorpNational corpNational) {
		daoService.create(corpNational);
	}

	@Override
	public void addCorpPenaltyInfos(JW_CorpPenaltyInfos corpPenaltyInfos) {
		daoService.create(corpPenaltyInfos);
	}

	@Override
	public void addCorpShareholder(JW_CorpShareholder shareholder) {
		daoService.create(shareholder);
	}

	@Override
	public Map<String, Object> getCorpRsp(String id) {
		String sql = "SELECT ID,REFID,TRADE_ID,BATNO,RECEIVETIME,BUILDENDTIME,"
				+ "REPORTID FROM CPDB_DS.T_DS_JW_CORP_RSP WHERE ID= ? ";
		Map<String, Object> result = this.daoService.getJdbcTemplate().queryForMap(sql,
				new Object[] { id });
		return result;
	}

	@Override
	public List<Map<String, Object>> getCorpBranch(String refid) {
		String sql = "SELECT ID,TRADE_ID,REFID,SUBREPORTTYPE,"
				+ " TREATRESULT,TREATERRORCODE,"
				+ " ERRORMESSAGE,REGISTERNO,BRANCHNAME,REGISTERDEPARTMENT "
				+ " FROM CPDB_DS.T_DS_JW_CORP_BRANCH WHERE REFID = ?";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql,
				new Object[] { refid });
		return result;
	}

	@Override
	public List<Map<String, Object>> getCorpManagement(String refid) {
		String sql = "SELECT ID,TRADE_ID,REFID,SUBREPORTTYPE,TREATRESULT,"
				+ " TREATERRORCODE,ERRORMESSAGE,NAME,ROLE"
				+ " FROM CPDB_DS.T_DS_JW_CORP_MANAGEMENT WHERE REFID = ?";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql,
				new Object[] { refid });
		return result;
	}

	@Override
	public List<Map<String, Object>> getCorpNational(String refid) {
		String sql = "SELECT ID,TRADE_ID,REFID,SUBREPORTTYPE,TREATRESULT,"
				+ " TREATERRORCODE,ERRORMESSAGE,"
				+ " CORPNAME,REGISTERNO,REGISTDATE,ARTIFICIALNAME,"
				+ " STATUS,REGISTFUND,MANAGERANGE,OPENDATE,"
				+ " MANAGEBEGINDATE,MANAGEENDDATE,CORPTYPE,REGISTERDEPARTMENT,"
				+ " REGISTERADDRESS FROM CPDB_DS.T_DS_JW_CORP_NATIONAL "
				+ " WHERE REFID = ?";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql,
				new Object[] { refid });
		return result;
	}

	@Override
	public List<Map<String, Object>> getCorpPenaltyInfos(String refid) {
		String sql = "SELECT ID,TRADE_ID,REFID,"
				+ " SUBREPORTTYPE,TREATRESULT,TREATERRORCODE,ERRORMESSAGE,"
				+ " RECORDNO,AFFAIR,PANALTY,EXECDEPARTMENT,"
				+ " RECORDDATE FROM CPDB_DS.T_DS_JW_CORP_PENALTYINFOS"
				+ " WHERE REFID = ?";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql,
				new Object[] { refid });
		return result;
	}

	@Override
	public List<Map<String, Object>> getCorpShareholder(String refid) {
		String sql = "SELECT ID,TRADE_ID,REFID,"
				+ " SUBREPORTTYPE,TREATRESULT,TREATERRORCODE,ERRORMESSAGE,"
				+ " NAME,TYPE,CERTTYPE,CERTID,CONTRIBUTIVETYPE,CONTRIBUTIVEFUND "
				+ " FROM CPDB_DS.T_DS_JW_CORP_SHAREHOLDER WHERE REFID = ?";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql,
				new Object[] { refid });
		return result;
	}

	@Override
	public String getCachedKey(String corpName, String registerNo,
			String province, Integer queryType,int cachedDays) {		
		/**或者根据工商注册号查询*/
		if(StringUtils.isNotBlank(registerNo)){
			String sql = "SELECT MAX(B.ID) "
					+ " FROM T_DS_JW_CORP_REQ A,T_DS_JW_CORP_RSP B ,T_DS_JW_CORP_NATIONAL C"
					+ " WHERE A.TRADE_ID = B.TRADE_ID AND B.ID = C.REFID   "
					+ " AND A.QUERYTYPE = ? "
					+ " AND (A.REGISTERNO= ?) "
					+ " AND A.PROVINCE =  ? "
					+ " AND C.TREATRESULT = 1  AND A.CREATE_DATE >= (SYSDATE - ?)";
			String id = this.daoService.getJdbcTemplate().queryForObject(sql,
					new Object[] { queryType,registerNo, province,cachedDays },
					String.class);
		    return id;
		}
		/**或者根据名称查询*/
		if(StringUtils.isNotBlank(corpName)){
			String sql = "SELECT MAX(B.ID) "
					+ " FROM T_DS_JW_CORP_REQ A,T_DS_JW_CORP_RSP B ,T_DS_JW_CORP_NATIONAL C"
					+ " WHERE A.TRADE_ID = B.TRADE_ID AND B.ID = C.REFID   "
					+ " AND A.QUERYTYPE = ? AND (A.CORPNAME = ?) "
					+ " AND A.PROVINCE =  ? "
					+ " AND C.TREATRESULT = 1  AND A.CREATE_DATE >= (SYSDATE - ?)";
			String id = this.daoService.getJdbcTemplate().queryForObject(sql,
					new Object[] { queryType, corpName, province,cachedDays },
					String.class);
		    return id;
		}	
	return null;	
	}
}
