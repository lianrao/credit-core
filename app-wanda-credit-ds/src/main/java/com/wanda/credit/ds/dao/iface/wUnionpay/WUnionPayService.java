/**   
* @Description: W项目-Service类
* @author xiaobin.hou  
* @date 2016年8月10日 上午11:08:17 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.wUnionpay;

import com.wanda.credit.ds.dao.domain.wUnionpay.UnionPayPreLoanPojo;

/**
 * @author xiaobin.hou
 *
 */
public interface WUnionPayService {

	/**
	 * 贷前银联卡交易数据查询流水表-保存
	 * @param preLoanPojo
	 */
	public void addPreLoanData(UnionPayPreLoanPojo preLoanPojo) throws Exception;

	/**
	 * 保存鉴权信息
	 * @param name
	 * @param encCardNo
	 * @param encCardId
	 * @param encMobile
	 * @param cardNo
	 * @param cardId
	 * @param mobile
	 */
	public void addCardAuthed(String name, String encCardNo, String encCardId,
			String encMobile, String cardNo, String cardId, String mobile) throws Exception;

	/**
	 * @param name
	 * @param encCardNo
	 * @param encCardId
	 * @param encMobile
	 * @return
	 */
	public boolean inCachePreData(String name, String encCardNo,
			String encCardId, String encMobile);

	/**
	 * @param name
	 * @param encCardNo
	 * @param encCardId
	 * @param encMobile
	 * @return
	 */
	public UnionPayPreLoanPojo queryPreData(String name, String encCardNo,
			String encCardId, String encMobile);

}
