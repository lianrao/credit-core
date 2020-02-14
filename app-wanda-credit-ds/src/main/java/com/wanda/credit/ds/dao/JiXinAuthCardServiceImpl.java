package com.wanda.credit.ds.dao;

import java.util.Map;

import com.wanda.credit.base.util.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.AllAuthenBankCard;

@Service
public class JiXinAuthCardServiceImpl  {
	private final  Logger logger = LoggerFactory.getLogger(JiXinAuthCardServiceImpl.class);
	@Autowired
	DaoService daoService;

	public void saveAuthCard(String dsId, String tradeId,String name, String cardNo, String cardId,
			String phone, Map data,String[] respInfo, String reqType, String reqValuesMd5) throws Exception {
        AllAuthenBankCard perobj = new AllAuthenBankCard();
        perobj.setName(name);
        perobj.setCardid(cardId);
        perobj.setCardno(cardNo);
        perobj.setMobile(phone);
        perobj.setTypeno(reqType);
        perobj.setTrade_id(tradeId);
        perobj.setSeq((String) data.get("orderid"));
        perobj.setDs_id(dsId);

		perobj.setSyscode(respInfo[0]);
		perobj.setSysmsg(respInfo[1]);
		
		perobj.setRespcode((String)data.get("returncode"));
		perobj.setRespdesc((String)data.get("errtext"));

        perobj.setReq_values_md5(MD5.uppEncodeByMD5(reqValuesMd5));
		daoService.create(perobj);
	}
}