/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年11月10日 上午11:32:32 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.jiAo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;
import com.wanda.credit.dsconfig.main.ResolveContext;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JiAoMobileCheckServiceImpl extends BaseServiceImpl<GeoMobileCheck> implements
		IJiAoMobileCheckService {

    private Logger logger = LoggerFactory.getLogger(JiAoMobileCheckServiceImpl.class);
	
	@Autowired
	private DaoService daoService;
	
	public void merge(){
		
		String queryHql = "From GeoMobileCheck g where g.name =:name and g.cardNo =:cardNo and g.mobileNo =:mobileNo order by g.update_time desc";
		
		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("cardNo", );
//		params.put("mobileNo", value);
//		params.put("name", value);
		List<GeoMobileCheck> existList = daoService.findByHQL(queryHql, params);
		
		if (existList != null && existList.size() > 0) {
			
		}else{
			
		}
	}
	@Override
	public boolean inCachedCount(String name, String mobile,int days) {		
		String sql ="SELECT COUNT(1) CNT FROM CPDB_DS.T_DS_GEO_PHONE_CHECK WHERE MOBILENO = ?  AND NAME = ? AND CHECKRESULT='0' AND CREATE_TIME>=(SYSDATE-?) ";
		Integer result = this.daoService.findOneBySql(sql, new Object[]{mobile,name,days},Integer.class);
		return result>0;
	}
	public Map<String, Object> findGeoMobileCheck(String name, String mobileNo){
        String sql = "SELECT T1.PROVINCE,T1.CITY,T1.ATTRIBUTE,T1.CHECKRESULT FROM "
        		+ "CPDB_DS.T_DS_GEO_PHONE_CHECK T1,(SELECT T.MOBILENO,MAX(T.CREATE_TIME) AS CREATE_TIME"
        		+" FROM CPDB_DS.T_DS_GEO_PHONE_CHECK T WHERE T.NAME=? AND T.MOBILENO=? AND T.CHECKRESULT='0' "
        		+" GROUP BY T.MOBILENO) T2 WHERE T1.NAME=? AND T1.MOBILENO=? AND "
        		+ " T1.CHECKRESULT='0' AND T1.CREATE_TIME=T2.CREATE_TIME AND ROWNUM<=1";

        Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql,name, mobileNo,name,mobileNo);
	    return resultMap;
    }
    public GeoMobileCheck findGeoMobileCheck(String name, String cardNo, String mobileNo){
        GeoMobileCheck geoMobileCheck = new GeoMobileCheck();

        String sql = "select * from (select t.* from CPDB_DS.T_DS_GEO_PHONE_CHECK t where " +
                "name = ? and cardno = ? and mobileno = ? order by update_time desc)" +
                " where rownum = 1";

        RowMapper<GeoMobileCheck> rw =  BeanPropertyRowMapper.newInstance(GeoMobileCheck.class);

        geoMobileCheck = daoService.findOneBySql(sql, new Object[]{name, cardNo, mobileNo}, rw);

        return geoMobileCheck;
    }

	@Override
	public void saveMobileName(String trade_id, String mobile, String name,
			String cardNo, String data) {
		// TODO Auto-generated method stub
		String sql = "insert into cpdb_ds.t_ds_jiao_mobilename_result(id,trade_id,name,cardno,mobile,content) "
				+" values(cpdb_ds.seq_t_ds_mobilename_result.nextval,?,?,?,?,?) ";
		daoService.getJdbcTemplate().update(sql,trade_id,name,cardNo,mobile,data);
	}

	@Override
	public String findDataByMobilename(String mobile, String name, int date) {
		List<Map<String, Object>> ls = daoService.getJdbcTemplate().queryForList(
				"select content from cpdb_ds.t_ds_jiao_mobilename_result " + "where name = ? and mobile = ?  "
						+ " and create_time >= (SYSDATE - ?) "
						+ " order by create_time desc",
				new Object[] { name,mobile ,date});
		String content = CollectionUtils.isNotEmpty(ls) ? (String) ls.get(0).get("content") : null;
		return content;
	}

	@Override
	public void saveFahaiDetail(String trade_id, String ds_id, String datatype,
			String entryId, String content) {
		String sql = "INSERT INTO CPDB_DS.T_DS_STORE_CLOB(ID,TRADE_ID,DS_ID,CONTENT,QRY1,QRY2)"
				+ " VALUES(CPDB_DS.SEQ_T_DS_STORE_CLOB.NEXTVAL,?,?,?,?,?)";
		daoService.getJdbcTemplate().update(sql,trade_id,ds_id,content,datatype,entryId);
	}

	@Override
	public String findFahaiDetail(String trade_id, String ds_id,
			String datatype, String entryId,int date) {
		List<Map<String, Object>> ls = daoService.getJdbcTemplate().queryForList(
				" select content from CPDB_DS.T_DS_STORE_CLOB a where a.ds_id=? and a.qry1=? "
				+ "and a.qry2=? and a.create_time>= (SYSDATE - ?)  order by create_time desc ",
				new Object[] { ds_id,datatype,entryId,date});
		String content = CollectionUtils.isNotEmpty(ls) ? (String) ls.get(0).get("content") : null;
		return content;
	}
}
