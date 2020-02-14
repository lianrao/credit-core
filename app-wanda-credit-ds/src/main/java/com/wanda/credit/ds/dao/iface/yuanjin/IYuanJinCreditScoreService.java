package com.wanda.credit.ds.dao.iface.yuanjin;

import com.wanda.credit.ds.dao.domain.yuanjin.YJ_CreditScoreVO;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年8月30日 上午11:23:55 
 *  
 */
public interface IYuanJinCreditScoreService {

	void save(YJ_CreditScoreVO vo);

	/**
	 * 查询缓存数据 
	 * @param months 缓存的月份数*/
	YJ_CreditScoreVO queryCached(String name, String cardNo, String crptedCardNo2, int months);

}
