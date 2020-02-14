/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年8月10日 上午11:08:56 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.wUnionpay;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.wUnionpay.UnionPayCardAuthedPojo;
import com.wanda.credit.ds.dao.domain.wUnionpay.UnionPayPreLoanPojo;
import com.wanda.credit.ds.dao.iface.wUnionpay.UnionPayCardAuthedService;
import com.wanda.credit.ds.dao.iface.wUnionpay.UnionPayPreLoanService;
import com.wanda.credit.ds.dao.iface.wUnionpay.WUnionPayService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class WUnionPayServiceImpl implements WUnionPayService {
	
	@Autowired
	private UnionPayPreLoanService preLoanService;
	@Autowired
	private UnionPayCardAuthedService cardAuthService;
	@Autowired
	private DaoService daoService;
	
	/*
	 *  贷前银联卡交易数据查询流水表
	 */
	@Override
	public void addPreLoanData(UnionPayPreLoanPojo preLoanPojo) throws Exception {

		Date nowTime = new Date();
		preLoanPojo.setCreate_time(nowTime);
		preLoanPojo.setUpdate_time(nowTime);
		preLoanService.add(preLoanPojo);
		
	}

	
	@Override
	public void addCardAuthed(String name, String encCardNo, String encCardId,
			String encMobile, String cardNo, String cardId, String mobile) throws Exception {
		
		Date nowTime = new Date();
		
		UnionPayCardAuthedPojo cardAuthed = new UnionPayCardAuthedPojo();

		cardAuthed.setCard(encCardId);
		cardAuthed.setIdCard(encCardNo);
		cardAuthed.setName(name);
		List<UnionPayCardAuthedPojo> cacheList = cardAuthService.query(cardAuthed);
		
		if (cacheList != null && cacheList.size() > 0) {
			UnionPayCardAuthedPojo cahceAuthed = cacheList.get(0);
			cahceAuthed.setMobile(encMobile);
			cahceAuthed.setMobile_attrib(mobile.substring(0, 7));
			cahceAuthed.setUpdate_time(nowTime);
			
			daoService.getSession().saveOrUpdate(cahceAuthed);
		}else{
			UnionPayCardAuthedPojo newCardAuthed = new UnionPayCardAuthedPojo();
			
			newCardAuthed.setCard(encCardId);
			newCardAuthed.setIdCard(encCardNo);
			newCardAuthed.setMobile(encMobile);
			newCardAuthed.setName(name);
			//手机号码归属地
			newCardAuthed.setMobile_attrib(mobile.substring(0, 7));
			if (cardNo.length() == 15) {
				cardNo = cardNo.substring(0, 6) + "19" + cardNo.substring(6, 15);
	        }
			//身份证号-发证地方
			String cardNoAttr = cardNo.substring(0, 6);
			newCardAuthed.setIdcard_attrib(cardNoAttr);
			//性别
			String sexCode = cardNo.substring(14, 17);
			int sexInt = Integer.parseInt(sexCode);
			newCardAuthed.setSex(sexInt % 2 == 0 ? "女":"男");
			//身份证号-出生日期
			String birth = cardNo.substring(6, 14);
			newCardAuthed.setBirthday(birth);
			//银行卡-缩略卡
			int cardLen = cardId.length();
			String cardPan = cardId.substring(0, 6) + cardId.substring(cardLen-4, cardLen);
			newCardAuthed.setCard_pan(cardPan);
			
			newCardAuthed.setCreate_time(nowTime);
			newCardAuthed.setUpdate_time(nowTime);
			
			cardAuthService.add(newCardAuthed);
		}
		
	}


	@Override
	public boolean inCachePreData(String name, String encCardNo,
			String encCardId, String encMobile) {
		UnionPayCardAuthedPojo authedPojo = new UnionPayCardAuthedPojo();
		authedPojo.setName(name);
		authedPojo.setCard(encCardId);
		authedPojo.setIdCard(encCardNo);
		authedPojo.setMobile(encMobile);
		
		List<UnionPayCardAuthedPojo> cacheAuthedList = cardAuthService.query(authedPojo);
		
		if (cacheAuthedList != null && cacheAuthedList.size() >0) {
			return true;
		}else{			
			return false;
		}
	}


	
	public UnionPayPreLoanPojo queryPreData(String name, String encCardNo,
			String encCardId, String encMobile) {
		
		String hql = "from UnionPayPreLoanPojo l where l.name =:name and l.idCard =:cardNo and l.card =:cardId and l.mobile =:mobile and l.respCode =:respCode order by l.create_time desc";

		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("name", name);
		params.put("cardNo", encCardNo);
		params.put("cardId", encCardId);
		params.put("mobile", encMobile);
		params.put("respCode", "00");
		
		List<UnionPayPreLoanPojo> loanCacheList = daoService.findByHQL(hql, params);
		
		if (loanCacheList != null && loanCacheList.size() > 0) {
			return loanCacheList.get(0);
		}else{
			return null;			
		}
	}

}
