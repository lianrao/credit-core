package com.wanda.credit.ds.dao.iface;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.ds.dao.domain.AllAuthenBankCard;

public interface IAllAuthCardService {

	void saveAuthCard(String dsId,String tradeId, String name, String cardNo, String cardId, String phone,
                       Map object, String req_values) throws Exception;

	void savaJuHeAuthCard(String dsId,String tradeId,String name,String cardNo,String cardId,String phone,JSONObject object, String req_values);
	/**
	 * 快钱cnp卡鉴权数据保存
	 * @param dsId
	 * @param tradeId
	 * @param name
	 * @param cardNo
	 * @param cardId
	 * @param phone
	 * @param hm
	 */
	void saveKQCNP(String dsId, String tradeId, String name, String cardNo,
			String cardId, String phone,String respCode, String detailCode, String req_values);

    void saveJXAuthCard(String dsId, String tradeId,String name, String cardNo, String cardId,
                               String phone, Map object, String req_values);
    AllAuthenBankCard savaSuanHuaAuthCard(String dsId, String tradeId, String name,
			String cardNo, String cardId, String phone, JSONObject rspData, String req_values);
}
