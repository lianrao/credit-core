package com.wanda.credit.ds.dao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.yiwei.YW_BankCard_Authen;
import com.wanda.credit.ds.dao.iface.IYWAuthenBankCardService;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年9月29日 下午2:55:04 
 *  
 */

@Service
@Transactional
public class YWAuthenBankCardServiceImpl implements IYWAuthenBankCardService {

	@Autowired
	private DaoService daoService;
	
	@Override
	public void addAuthenBackCard(Map<String, Object> contxt,
			JSONObject busiData) {
		if(busiData == null) return ;
		YW_BankCard_Authen vo = new YW_BankCard_Authen();
		vo.setTypeNo((String)contxt.get("typeNo"));
		vo.setTrade_id((String)contxt.get("trade_id"));
		vo.setCardNo((String)contxt.get("crptedCardNo"));
		vo.setCardId((String)contxt.get("crptedCardId"));
		vo.setName(busiData.getString("name"));
		vo.setMobile((String)contxt.get("crptedPhone"));
		vo.setRespCode(busiData.getString("respCode"));
		vo.setRespDesc(busiData.getString("respDesc"));
		daoService.create(vo);	
	}
}
