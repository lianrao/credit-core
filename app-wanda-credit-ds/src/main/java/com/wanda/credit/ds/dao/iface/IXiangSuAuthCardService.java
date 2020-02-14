package com.wanda.credit.ds.dao.iface;

import java.util.Map;

public interface IXiangSuAuthCardService {

	void saveAuthCard(String dsId,String tradeId, String name, String cardNo, String cardId, String phone,
                       Map object) throws Exception;

}
