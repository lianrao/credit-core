package com.wanda.credit.ds.dao.iface;

import java.util.Map;
import java.util.TreeMap;

public interface IUnionPayPaintService {
	public void save(String trade_id, String result, Map<String, String> params);
	/**商户画像数据落地*/
	public void saveMerchant(String trade_id, String result, Map<String, String> params);
	
	/**
	 * 是否存在当月数据-商户画像
	 * @param trade_id
	 * @param mid
	 * @return 当月数据对应的主键ID
	 */
	public String isExistCurentMonthRecord(String trade_id, String mid);
	/**
	 * 查询缓存的商户画像数据
	 * @param trade_id
	 * @param mid
	 * @param resultId
	 * @return 银联商户画像数据
	 */
	public TreeMap<String,Object> queryLastResult(String trade_id,String resultId);
	
	/**
	 * 是否存在个人画像对应数据 有返回对应的主键ID
	 * @param trade_id
	 * @param cardId
	 * @param period 周期，单位为天
	 * @return
	 */
	public Map<String, String> isExistPerPaintData(String trade_id,String cardId,int period);
	/**
	 * 查询缓存个人画像数据
	 * @param trade_id
	 * @param id
	 * @return 银联个人画像数据
	 */
	public Map<String,Object>  queryCachePerCacheData(String trade_id,String id);
	
	/**
	 * 报存消费画像数据
	 * @param trade_id
	 * @param result
	 * @param params
	 */
	public void saveNew(String trade_id, String result, Map<String, String> params);
	
	public Map<String, String> isExistPerPaintDataNew(String trade_id, String cardId ,int period);
}
