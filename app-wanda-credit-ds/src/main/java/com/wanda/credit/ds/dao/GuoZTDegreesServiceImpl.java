package com.wanda.credit.ds.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.base.util.BeanUtil;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.ds.dao.domain.Guozt_degrees_check_result;
import com.wanda.credit.ds.dao.iface.IGuoZTDegreesService;

@Service
@Transactional
public class GuoZTDegreesServiceImpl extends BaseServiceImpl<Guozt_degrees_check_result> implements IGuoZTDegreesService {
	private final Logger logger = LoggerFactory.getLogger(GuoZTDegreesServiceImpl.class);
	@Override
	public boolean inCached(String name, String cardNo) {
		String sql ="SELECT COUNT(1) FROM CPDB_DS.T_DS_GZT_DEGREES D WHERE D.CARDNO=? AND D.NAME=? AND D.CREATE_DATE>SYSDATE-30 ";
		Integer result = this.daoService.findOneBySql(sql, new Object[]{cardNo,name},Integer.class);
		return result>0;

	}
	@Override
	public Guozt_degrees_check_result getDegreesByTradeId(String name,String cardNo) {
		Guozt_degrees_check_result guozt_result = new Guozt_degrees_check_result();
		try {
			String hql = "from Guozt_degrees_check_result t where t.cardNo=:cardNo and t.userName=:name and t.create_date >=:startTime  ";			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", name);
			params.put("cardNo", cardNo);
			params.put("startTime", DateUtil.addMonths(-1));
			
			List<Guozt_degrees_check_result> eduPerList = daoService.findByHQL(hql, params);
			if(eduPerList.size() > 0){
				guozt_result = eduPerList.get(0);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return guozt_result;
	}
	@Override
	public boolean inCachedDate(String name, String cardNo,int num) {
		String sql ="SELECT COUNT(1) FROM CPDB_DS.T_DS_GZT_DEGREES D WHERE D.CARDNO=? AND D.NAME=? AND D.CREATE_DATE>=SYSDATE-? ";
		Integer result = this.daoService.findOneBySql(sql, new Object[]{cardNo,name,num},Integer.class);
		return result>0;

	}
	@Override
	public Guozt_degrees_check_result getDegreesByTradeIdDate(String name,String cardNo,int num) {
		Guozt_degrees_check_result guozt_result = new Guozt_degrees_check_result();
		try {
			String hql = "from Guozt_degrees_check_result t where t.cardNo=:cardNo and t.userName=:name and t.create_date >=sysdate-:startTime  ";			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", name);
			params.put("cardNo", cardNo);
			params.put("startTime", num);
			
			List<Guozt_degrees_check_result> eduPerList = daoService.findByHQL(hql, params);
			if(eduPerList.size() > 0){
				guozt_result = eduPerList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return guozt_result;
	}
	@Override
	public Guozt_degrees_check_result getDegreesByTradeIdEver(String name,String cardNo) {
		Guozt_degrees_check_result guozt_result = new Guozt_degrees_check_result();
		try {
			String sql = "SELECT T1.NAME USERNAME,T1.CARDNO,T1.GRADUATE,T1.EDUCATIONDEGREE,T1.ENROLDATE,T1.SPECIALITYNAME,T1.GRADUATETIME,T1.STUDYRESULT,T1.STUDYSTYLE,T1.SCHOOLTYPE, "
					+"T1.IMAGE_FILE,T1.SCHOOLCITY,T1.SCHOOLTRADE,T1.ORGANIZATION,T1.SCHOOLNATURE,T1.SCHOOLCATEGORY,T1.GLEVEL,T1.EDUCATIONAPPROACH,T1.IS985, "
					+"T1.IS211,T1.CREATEDATE,T1.CREATEYEAR,T1.ACADEMICIANNUM,T1.BSHLDZNUM, T1.BSDNUM,T1.SSDNUM, T1.ZDXKNUM,T1.DSTUDYSTYLE,T1.STATUS1,T1.SOURCEID  FROM CPDB_DS.T_DS_GZT_DEGREES T1 "
					+",(SELECT D.CARDNO, MAX(D.CREATE_DATE) AS CREATE_DATE  FROM CPDB_DS.T_DS_GZT_DEGREES D  WHERE D.CARDNO = ? AND D.NAME=? "
					+"GROUP BY D.CARDNO) T2 WHERE T1.CARDNO = ? AND T1.NAME=? AND T1.CREATE_DATE = T2.CREATE_DATE   AND ROWNUM<=1 ";			
			Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, cardNo,name,cardNo,name);
			guozt_result = (Guozt_degrees_check_result) BeanUtil.mapToObject(resultMap, Guozt_degrees_check_result.class);
//			System.out.println("����ѧ������001:"+JSON.toJSONString(guozt_result));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return guozt_result;
	}
	
	@Override
	public String inCached(String dsId, String name, String cardNo) throws ParseException {
		String hql = "select cpdb_mk.DATASOURCE_LOG_SEARCH(?,?,?,?) from dual";
		SQLQuery query = daoService.getSession().createSQLQuery(hql);
		query.setParameter(0, dsId);
		query.setParameter(1, DateUtil.addDays(-30));
		query.setParameter(2, "name;cardNo");
		query.setParameter(3, name + ";" + cardNo);
		return (String)query.uniqueResult();

	}
	@Override
	public Guozt_degrees_check_result getDegreesByTradeId(String tradeId) {
		String hql = "from Guozt_degrees_check_result t where t.trade_id=:tradeId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tradeId", tradeId);
		return daoService.findOneByNamedHQL(hql, params);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void update(String name, String cardNo, Guozt_degrees_check_result retdegrees) {
		// TODO Auto-generated method stub
		Long id = (Long) retdegrees.getId();
		
		String grade = (String) retdegrees.getGraduate();
		String eductiondegree = (String) retdegrees.getEducationDegree();
		String gradetime = (String) retdegrees.getGraduateTime();
		String studytype = (String) retdegrees.getStudyStyle();
		String studystyle = (String) retdegrees.getDstudyStyle();

		String gradechk = (String) retdegrees.getGraduate_chk_rst();
		String educationchk = (String) retdegrees.getEducationdegree_chk_rst();
		String approchchk = (String) retdegrees.getEducationapproach_chk_rst();
		String gradetimechk = (String) retdegrees.getGraduatetime_chk_rst();

		List<Object> conditions = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("update ");
		sql.append("CPDB_DS.T_DS_GZT_DEGREES set");

		Date date=new Date();
		conditions.add(date);
		sql.append(" UPDATE_DATE = ? ,");
		if (StringUtils.isNotBlank(grade)) {
			conditions.add(grade);
			sql.append(" GRADUATE = ? ,");
		}
		if (StringUtils.isNotBlank(eductiondegree)) {
			conditions.add(eductiondegree);
			sql.append("  EDUCATIONDEGREE = ? ,");
		}
		if (StringUtils.isNotBlank(gradetime)) {
			conditions.add(gradetime);
			sql.append(" GRADUATETIME = ? ,");
		}
		if (StringUtils.isNotBlank(studytype)) {
			conditions.add(studytype);
			sql.append("  STUDYSTYLE = ? ,");
		}
		if (StringUtils.isNotBlank(studystyle)) {
			conditions.add(studystyle);
			sql.append("  DSTUDYSTYLE = ? ,");
		}
		if (StringUtils.isNotBlank(gradechk)) {
			conditions.add(gradechk);
			sql.append(" GRADUATE_CHK_RST = ? ,");
		}
		if (StringUtils.isNotBlank(educationchk)) {
			conditions.add(educationchk);
			sql.append("  EDUCATIONDEGREE_CHK_RST = ? ,");
		}
		if (StringUtils.isNotBlank(approchchk)) {
			conditions.add(approchchk);
			sql.append("  EDUCATIONAPPROACH_CHK_RST = ? ,");
		}
		if (StringUtils.isNotBlank(gradetimechk)) {
			conditions.add(gradetimechk);
			sql.append("  GRADUATETIME_CHK_RST = ? ,");
		}
		conditions.add(id);
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" where id = ?");
		String resql = sql.toString();
		daoService.getJdbcTemplate().update(resql, conditions.toArray(new Object[conditions.size()]));
		return;
	}
	
}
