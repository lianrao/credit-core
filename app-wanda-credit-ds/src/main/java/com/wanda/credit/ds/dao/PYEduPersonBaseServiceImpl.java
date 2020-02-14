package com.wanda.credit.ds.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.base.util.BeanUtil;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.ds.dao.domain.Py_edu_college;
import com.wanda.credit.ds.dao.domain.Py_edu_degree;
import com.wanda.credit.ds.dao.domain.Py_edu_personBase;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduCollegeService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduDegreeService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduPersonBaseService;

@Service
@Transactional
public class PYEduPersonBaseServiceImpl extends BaseServiceImpl<Py_edu_personBase> implements IPYEduPersonBaseService {

	@Autowired
	private IPYEduDegreeService pyEduDegreeService;
	@Autowired
	private IPYEduCollegeService pyEduCollegeService;
	@Autowired
	private DaoService daoService;
	
	@Override
	public boolean inCached(String name,String cardNo) {
		Py_edu_personBase personBase = new Py_edu_personBase();
		personBase.setName(name);
		personBase.setDocumentNo(cardNo);
		personBase.setVerifyResult(1);
		return this.query(personBase).size()>0;
	}

	@Override
	public Py_edu_personBase queryPersonBase(String name, String cardNo) {
		Py_edu_personBase personBase = new Py_edu_personBase();
		try {
			String sql = "SELECT T1.ID,T1.TRADE_ID,T1.NAME ,T1.DOCUMENTNO,T1.DEGREE,T1.SPECIALTY,T1.COLLEGE,T1.GRADUATETIME,T1.GRADUATEYEARS, "
					+" T1.ORIGINALADDRESS,T1.VERIFYRESULT,T1.BIRTHDAY,T1.GENDER,T1.AGE,T1.RISKANDADVICEINFO,T1.CREATE_DATE,T1.REPORTID "
					+"  FROM CPDB_DS.T_DS_PY_EDU_PERSONBASE T1 "
					+",(SELECT D.DOCUMENTNO, MAX(D.CREATE_DATE) AS CREATE_DATE  FROM CPDB_DS.T_DS_PY_EDU_PERSONBASE D  WHERE D.DOCUMENTNO =? AND D.NAME=? "
					+" GROUP BY D.DOCUMENTNO) T2 WHERE T1.DOCUMENTNO =? AND T1.NAME=? AND T1.CREATE_DATE = T2.CREATE_DATE   AND ROWNUM<=1 ";			
			Map<String, Object> resultMap = this.daoService.getJdbcTemplate().queryForMap(sql, cardNo,name,cardNo,name);
			personBase = (Py_edu_personBase) BeanUtil.mapToObject(resultMap, Py_edu_personBase.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return personBase;
	}

	@Override
	public Py_edu_degree queryPersonDegree(String trade_id) {
		Py_edu_degree personDegree = new Py_edu_degree();
		personDegree.setTrade_id(trade_id);
		List<Py_edu_degree> list = pyEduDegreeService.query(personDegree);
		return list.size()>0 ? list.get(0) : null;
	}

	@Override
	public Py_edu_college queryPersonCollege(String trade_id) {
		Py_edu_college personCollege = new Py_edu_college();
		personCollege.setTrade_id(trade_id);
		List<Py_edu_college> list = pyEduCollegeService.query(personCollege);
		return list.size()>0 ? list.get(0) : null;
	}

	
	@Override
	public boolean inCachedMonth(String name, String crptedCardNo,int num) {
		boolean inCache = false;
		try {
			String sql ="SELECT COUNT(1) CNT FROM CPDB_DS.T_DS_PY_EDU_PERSONBASE D WHERE D.DOCUMENTNO=? AND D.NAME=? AND D.CREATE_DATE>=SYSDATE-? ";
			Integer result = this.daoService.findOneBySql(sql, new Object[]{crptedCardNo,name,num},Integer.class);
			inCache = result>0;
		} catch (Exception e) {
			e.printStackTrace();
			inCache = false;
		}
		return inCache;
	}

	@Override
	public void saveNewPerBase(Py_edu_personBase personBase) {
		//保存新的数据
		personBase.setCreate_date(new Date());
		daoService.create(personBase);	
	}

	public void saveNewPerBase(Py_edu_personBase personBase,
			Py_edu_degree degree, Py_edu_college college) throws Exception {

		if (personBase == null) {
			return;
		}		
		String tradeId = personBase.getTrade_id();				
		//保存新的数据
		personBase.setCreate_date(new Date());
		daoService.create(personBase);
		if (degree != null) {
			Py_edu_degree toSaveDegree = new Py_edu_degree();
			BeanUtils.copyProperties(toSaveDegree, degree);
			toSaveDegree.setTrade_id(tradeId);
			toSaveDegree.setPhoto(null);//照片信息不保存到数据库
			pyEduDegreeService.add(toSaveDegree);
		}		
		if (college != null) {
			college.setTrade_id(tradeId);
			pyEduCollegeService.add(college);
		}	
	}	
	
}
