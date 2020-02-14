package com.wanda.credit.ds.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanda.credit.ds.dao.iface.IXiangSuAuthCardService;
import org.springframework.stereotype.Service;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.dao.domain.AllAuthenBankCard;
@Service
@Transactional
public class XiangSuAuthCardServiceImpl implements IXiangSuAuthCardService{

	private final  Logger logger = LoggerFactory.getLogger(XiangSuAuthCardServiceImpl.class);
	@Autowired
	DaoService daoService;

	public void saveAuthCard(String dsId, String tradeId,String name, String cardNo, String cardId,
			String phone, Map data) throws Exception {
        AllAuthenBankCard perobj = new AllAuthenBankCard();
        perobj.setName(name);
        perobj.setCardid(cardId);
        perobj.setCardno(cardNo);
        perobj.setMobile(phone);
        perobj.setTrade_id(tradeId);
        perobj.setSeq((String) data.get("trade_no"));
        perobj.setDs_id(dsId);
        
        if(!StringUtil.isEmpty(phone)&&!StringUtil.isEmpty(name)&&!StringUtil.isEmpty(cardId)){
        	perobj.setTypeno("01");//四要素
       }else if(StringUtil.isEmpty(phone)&&StringUtil.isEmpty(name)&&!StringUtil.isEmpty(cardId)){
          perobj.setTypeno("02");//二要素
	   }else if(StringUtil.isEmpty(phone)&&StringUtil.isEmpty(cardId)&&!StringUtil.isEmpty(name)){
		perobj.setTypeno("02");//二要素
       }else if(StringUtil.isEmpty(name)&&StringUtil.isEmpty(cardId)&&!StringUtil.isEmpty(phone)){
	    perobj.setTypeno("02");//二要素
       }else{
    	   perobj.setTypeno("00");//三要素
       }
		perobj.setRespcode((String)data.get("code"));
		perobj.setRespdesc((String)data.get("desc"));
		
		 try{
	            //只保存一致不一致结果
	            if("2000".equals(data.get("respCode")) || "2001".equals(data.get("respCode"))) {
	            	perobj.setSyscode((String)data.get("respCode"));	
	            	perobj.setSyscode((String)data.get("resMsg"));	
	                daoService.create(perobj);
	            }
	        }catch (Exception e){
	            e.printStackTrace();
	            logger.info("{} 保存数据库失败 {}", tradeId, e);
	        }
		}
		
}
