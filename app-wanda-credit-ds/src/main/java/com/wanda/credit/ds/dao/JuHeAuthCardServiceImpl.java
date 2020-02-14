package com.wanda.credit.ds.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.client.wangshu.BaseWDWangShuDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.Juhe_AuthenBankCard;
import com.wanda.credit.ds.dao.iface.IJuheAuthCardService;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年5月23日 下午4:22:58 
 *  
 */
@Service
public class JuHeAuthCardServiceImpl implements IJuheAuthCardService {

	private final  Logger logger = LoggerFactory.getLogger(JuHeAuthCardServiceImpl.class);
	@Autowired
	DaoService daoService;
	
	@Override
	public void saveAuthCard3(String tradeId,String name, String cardNo, String cardId,
			JSONObject object) throws Exception {
		Juhe_AuthenBankCard perobj = buildPersisObjFromAuth3(object.getJSONObject("data"));
		perobj.setName(name);
		perobj.setCardId(CryptUtil.encrypt(cardId));
		perobj.setCardNo(CryptUtil.encrypt(cardNo));
		perobj.setTypeno("00");perobj.setTrade_id(tradeId);
		perobj.setSeq((String)object.get("seq"));
		daoService.create(perobj);
	}

	private Juhe_AuthenBankCard buildPersisObjFromAuth3(JSONObject jsndata) {
		Juhe_AuthenBankCard obj = new Juhe_AuthenBankCard();
		Object resObj = jsndata.get("res");
		if(resObj == null){
			return obj;
		}
		String res = resObj.toString();
		if("1".equals(res)){
			obj.setRespCode("00");
			obj.setRespDesc("验证匹配");
		}else if("2".equals(res)){
			obj.setRespCode("01");
			obj.setRespDesc("验证不匹配");
		}
		return obj;
	}

	@Override
	public void saveAuthCard4(String tradeId,String name, String cardNo, String cardId,
			String phone, JSONObject object) throws Exception {
		Juhe_AuthenBankCard perobj = buildPersisObjFromAuth4(object.getJSONObject("data"));
		perobj.setName(name);
		perobj.setCardId(CryptUtil.encrypt(cardId));
		perobj.setCardNo(CryptUtil.encrypt(cardNo));
		perobj.setMobile(CryptUtil.encrypt(phone));
		perobj.setTrade_id(tradeId);
		perobj.setTypeno("01");perobj.setSeq((String)object.get("seq"));
		daoService.create(perobj);
	}

	private Juhe_AuthenBankCard buildPersisObjFromAuth4(JSONObject jsndata) {
		Juhe_AuthenBankCard obj = new Juhe_AuthenBankCard();
		Object resObj = jsndata.get("res");
		if(resObj == null){
			return obj;
		}
		String res = resObj.toString();
		if("1".equals(res)){
			obj.setRespCode("00");
			obj.setRespDesc("验证匹配");
		}else if("2".equals(res)){
			obj.setRespCode("01");
			obj.setRespDesc("验证不匹配");
		}else {
			obj.setRespCode(res);
			obj.setRespDesc(jsndata.getString("message"));
		}
		return obj;
	}

	@Override
	public Map<String, Object> queryCache(String prefix,String name, String cardNo,
			String cardId, String mobile, int cacheTime) {
		
		String threeSql = "select RESPCODE as RESPCODE,RESPDESC as RESPDESC from cpdb_ds.T_DS_JH_AuthenBankCard jh where jh.name = ? and jh.cardno = ? and jh.cardid = ? and jh.typeno = '00'  and jh.create_date >= ? order by jh.create_date desc";
		String fourSql = "select RESPCODE as RESPCODE,RESPDESC as RESPDESC from cpdb_ds.T_DS_JH_AuthenBankCard jh where jh.name = ? and jh.cardno = ? and jh.cardid = ? and jh.mobile = ? and jh.typeno = '01'  and jh.create_date >= ? order by jh.create_date desc";
		
		Date zero = getNextDay(cacheTime);
		logger.info("{} 缓存开始时间为 {}",prefix , zero);
		String encCardNo = CryptUtil.encrypt(cardNo);
		String encCardId = CryptUtil.encrypt(cardId);
		
		List<Map<String, Object>> queryForList = null;
		if (StringUtil.isEmpty(mobile)) {
			queryForList = daoService.getJdbcTemplate().queryForList(threeSql,name,encCardNo,encCardId,zero);
		}else{
			String encMobile = CryptUtil.encrypt(mobile);
			queryForList = daoService.getJdbcTemplate().queryForList(fourSql,name,encCardNo,encCardId,encMobile,zero);
		}
		
		Map<String, Object> result = null;
		if (queryForList != null && queryForList.size() > 0) {
			result = queryForList.get(0);
		}
		
		return result;
	}
	
	public Date getNextDay(int time) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -time);
		return calendar.getTime();
	}

}