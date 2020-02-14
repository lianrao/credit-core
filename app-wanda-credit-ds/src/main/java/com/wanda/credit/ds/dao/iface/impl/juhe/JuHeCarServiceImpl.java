/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年11月10日 上午11:32:32 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juhe;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.dao.iface.juhe.IJuHeCarSearchService;

/**
 * @author liunan
 */
@Service
@Transactional
public class JuHeCarServiceImpl extends BaseServiceImpl<GeoMobileCheck> implements IJuHeCarSearchService {

    private Logger logger = LoggerFactory.getLogger(JuHeCarServiceImpl.class);

	@Override
	public void saveCarDetail(String trade_id, String ds_id, String name, String carNumber, String content) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO CPDB_DS.T_DS_STORE_CLOB(ID,TRADE_ID,DS_ID,CONTENT,QRY1,QRY2)"
				+ " VALUES(CPDB_DS.SEQ_T_DS_STORE_CLOB.NEXTVAL,?,?,?,?,?)";
		this.daoService.getJdbcTemplate().update(sql,trade_id,ds_id,content,name,carNumber);
	}

	@Override
	public String findCarDetail(String ds_id, String name, String carNumber, int date) {
		List<Map<String, Object>> ls = daoService.getJdbcTemplate().queryForList(
				" select content from CPDB_DS.T_DS_STORE_CLOB a where a.ds_id=? and a.qry1=? "
				+ "and a.qry2=? and a.create_time>= (SYSDATE - ?)  order by create_time desc ",
				new Object[] { ds_id,name,carNumber,date});
		String content = CollectionUtils.isNotEmpty(ls) ? (String) ls.get(0).get("content") : null;
		return content;
	}

	@Override
	public boolean inCachedCount(String name, String carNumber,String dsid, int days) {
		String sql =" SELECT COUNT(1) CNT FROM CPDB_DS.T_DS_STORE_CLOB WHERE ds_id=? and QRY1 = ?  AND QRY2 = ? AND CREATE_TIME>=(SYSDATE-?) ";
		Integer result = this.daoService.findOneBySql(sql, new Object[]{dsid,name,carNumber,days},Integer.class);
		return result>0;
	}
	
}
