package com.wanda.credit.ds.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.dao.domain.Bairong_AuthenBankCard;
import com.wanda.credit.ds.dao.iface.IBairongAuthCardService;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年5月23日 下午4:22:58 
 *  
 */
@Service
public class BairongAuthCardServiceImpl
  implements IBairongAuthCardService {

	@Autowired
	DaoService daoService;
	
	@Override
	public void saveAuthCard3(String tradeId,String name, String cardNo, String cardId,
			JSONObject object) throws Exception {
		Bairong_AuthenBankCard perobj = buildPersisObjFromAuth3(object.getJSONObject("data"));
		perobj.setName(name);
		perobj.setCardId(CryptUtil.encrypt(cardId));
		perobj.setCardNo(CryptUtil.encrypt(cardNo));
		perobj.setTypeno("00");perobj.setTrade_id(tradeId);
		perobj.setSeq((String)object.get("seq"));
		daoService.create(perobj);
	}

	private Bairong_AuthenBankCard buildPersisObjFromAuth3(JSONObject jsndata) {
		Bairong_AuthenBankCard obj = new Bairong_AuthenBankCard();
		JSONObject flag = jsndata.getJSONObject("flag");
		JSONObject product = jsndata.getJSONObject("product");
		if(flag != null){
			obj.setFlag_bankfourpro(flag.get("flag_bankthree") != null? flag.get("flag_bankthree").toString() : null);
		}
		if(product != null){
			obj.setRespDesc(product.getString("resMsg"));
			obj.setRespCode(product.getString("respCode"));			
		}
		return obj;
	}

	@Override
	public void saveAuthCard4(String tradeId,String name, String cardNo, String cardId,
			String phone, JSONObject object) throws Exception {
		Bairong_AuthenBankCard perobj = buildPersisObjFromAuth4(object.getJSONObject("data"));
		perobj.setName(name);
		perobj.setCardId(CryptUtil.encrypt(cardId));
		perobj.setCardNo(CryptUtil.encrypt(cardNo));
		perobj.setMobile(CryptUtil.encrypt(phone));
		perobj.setTrade_id(tradeId);
		perobj.setTypeno("01");perobj.setSeq((String)object.get("seq"));
		daoService.create(perobj);
	}

	private Bairong_AuthenBankCard buildPersisObjFromAuth4(JSONObject jsndata) {
		Bairong_AuthenBankCard obj = new Bairong_AuthenBankCard();
		JSONObject flag = jsndata.getJSONObject("flag");
		JSONObject product = jsndata.getJSONObject("product");
		if(flag != null){
			obj.setFlag_bankfourpro(flag.get("flag_bankfourpro") != null? flag.get("flag_bankfourpro").toString() : null);
		}
		if(product != null){
			obj.setRespDesc(product.getString("msg"));
			obj.setRespCode(product.getString("result"));
			obj.setSysCode(product.getString("sysCode"));
			obj.setSysMsg(product.getString("sysMsg"));
		}
		return obj;
	}
	
}