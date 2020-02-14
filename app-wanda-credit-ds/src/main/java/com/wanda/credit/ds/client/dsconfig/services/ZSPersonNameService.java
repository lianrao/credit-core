package com.wanda.credit.ds.client.dsconfig.services;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.dsconfig.main.ResolveContext;
import com.wanda.credit.dsconfig.support.DsCfgUtil;


/** * @author  
 * mafei * @date 
 * 创建时间：2017年9月1日 下午5:49:51 * 
 * @version 1.0 * @parameter  * @since  
 * * @return  */
@Service("zsPersonNameService")
public class ZSPersonNameService {
	private final static Logger logger = LoggerFactory.getLogger(ZSPersonNameService.class);
	@Autowired
	private DaoService daoService;
		public Map<String, Object> inCached(String key,String acct_id,int months) {
			String sql = "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM (SELECT CASE  WHEN D.CREATED >=  "
					+ "  (SYSDATE-?) THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
					+ " WHERE D.KEY = ? AND D.STATUS = '1' AND ACCT_ID=?) ";
			Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql,months, key,acct_id);
			logger.info("{} ","查询成功");
		    return resultMap;
		}
		public Map<String, Object> inCachedDs(String key,int months) {
			String sql = "SELECT DECODE(NVL(SUM(STA),0), 0, '0', '1') STAT FROM (SELECT CASE  WHEN D.CREATED >=  "
					+ "  (SYSDATE-?) THEN  1 ELSE  0 END STA  FROM CPDB_DS.T_DS_ZS_ORDER D "
					+ " WHERE D.KEY = ? AND D.STATUS = '1') ";
			Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql,months, key);
		    return resultMap;
		}
}
