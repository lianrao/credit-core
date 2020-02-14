package com.wanda.credit.ds.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.ds.dao.domain.Guozt_badInfo_check_result;
import com.wanda.credit.ds.dao.iface.IGuoZTBadInfoService;

@Service
@Transactional
public class GuoZTBadInfoServiceImpl extends BaseServiceImpl<Guozt_badInfo_check_result> implements IGuoZTBadInfoService {
	private final Logger logger = LoggerFactory.getLogger(GuoZTBadInfoServiceImpl.class);
	
	@Autowired
	private PropertyEngine propertyEngine;

	@Override
	public String inCached(String dsId, String name, String cardNo){
		
		String sql = "select max(trade_id) from cpdb_ds.t_ds_gzt_c_badinfod a "
				+ "where a.cardNo=? AND a.name=? and a.CREATE_DATE >=  add_months(sysdate,0-?)";
		List<String> result = 
				daoService.getJdbcTemplate().queryForList(sql, String.class,
						new Object[]{cardNo,name,Integer.valueOf(
								propertyEngine.readById("gztcrime_cacheMonth"))});
		if(CollectionUtils.isNotEmpty(result) && StringUtils.isNotBlank(result.get(0))){
			return result.get(0);			
		}
		return null;		

	}

	@Override
	public List<Guozt_badInfo_check_result> getBadInfoList(String tradeId) {
		String hql = "from Guozt_badInfo_check_result t where t.trade_id=:tradeId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tradeId", tradeId);
		return daoService.findByHQL(hql, params);

	}
	@Override
	public void saveCertiResult(String trade_id, String ds_id, String name, String cardNo, String content) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO CPDB_DS.T_DS_STORE_CLOB(ID,TRADE_ID,DS_ID,CONTENT,QRY1,QRY2)"
				+ " VALUES(CPDB_DS.SEQ_T_DS_STORE_CLOB.NEXTVAL,?,?,?,?,?)";
		this.daoService.getJdbcTemplate().update(sql,trade_id,ds_id,content,name,cardNo);
	}
	@Override
	public boolean inCachedCount(String name, String cardNo,String dsid, int days) {
		String sql =" SELECT COUNT(1) CNT FROM CPDB_DS.T_DS_STORE_CLOB WHERE ds_id=? and QRY1 = ?  AND QRY2 = ? AND CREATE_TIME>=(SYSDATE-?) ";
		Integer result = this.daoService.findOneBySql(sql, new Object[]{dsid,name,cardNo,days},Integer.class);
		return result>0;
	}
	@Override
	public String findCertiDetail(String ds_id, String name, String cardNo, int date) {
		List<Map<String, Object>> ls = daoService.getJdbcTemplate().queryForList(
				" select content from CPDB_DS.T_DS_STORE_CLOB a where a.ds_id=? and a.qry1=? "
				+ "and a.qry2=? and a.create_time>= (SYSDATE - ?)  order by create_time desc ",
				new Object[] { ds_id,name,cardNo,date});
		String content = CollectionUtils.isNotEmpty(ls) ? (String) ls.get(0).get("content") : null;
		return content;
	}
}
