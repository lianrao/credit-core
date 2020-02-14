package com.wanda.credit.ds.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.AllAuthenBankCard;

@Service
public class JieAnAuthCardServiceImpl  {
	private final  Logger logger = LoggerFactory.getLogger(JieAnAuthCardServiceImpl.class);
	@Autowired
	DaoService daoService;

	
	
	public void saveAuthCard(String dsId, String tradeId,String name, String cardNo, String cardId,
			String phone, Map data,String[] respInfo, String reqType) throws Exception {
        AllAuthenBankCard perobj = new AllAuthenBankCard();
        perobj.setName(name);
        perobj.setCardid(cardId);
        perobj.setCardno(cardNo);
        perobj.setMobile(phone);
        perobj.setTypeno(reqType);
        perobj.setTrade_id(tradeId);
        perobj.setSeq((String) data.get("resTxnId"));
        perobj.setDs_id(dsId);

		perobj.setSyscode(respInfo[0]);
		perobj.setSysmsg(respInfo[1]);
		daoService.create(perobj);
	}

	/*
	 00:三要素 01:四要素 02:二要素
	 String PROD_ID = "CARD2N"; 
	if(StringUtils.isNotBlank(cardNo)){
		reqData.put("CERT_ID",cardNo);
		PROD_ID = "CARD3CN";
	}
	if(PROD_ID.equals("CARD3CN") 
			&& StringUtils.isNotBlank(phone)){
		reqData.put("MP",phone);
		PROD_ID = "CARD4";
	}*/
    private void setTypeNo(AllAuthenBankCard perobj, Map data) {
       String reqType = (String) data.get("reqType");
       if("CARD2N".equals(reqType))perobj.setTypeno("02");
       else if("CARD3CN".equals(reqType))perobj.setTypeno("00");
       else if("CARD4".equals(reqType))perobj.setTypeno("01");       
    }

}