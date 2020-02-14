package com.wanda.credit.ds.dao.iface;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.ds.dao.domain.qxb.Abnormal_items;
import com.wanda.credit.ds.dao.domain.qxb.Branches;
import com.wanda.credit.ds.dao.domain.qxb.Changerecords;
import com.wanda.credit.ds.dao.domain.qxb.Contact;
import com.wanda.credit.ds.dao.domain.qxb.CorpBasic;
import com.wanda.credit.ds.dao.domain.qxb.Eemployees;
import com.wanda.credit.ds.dao.domain.qxb.Partners;
import com.wanda.credit.ds.dao.domain.qxb.Partners_real;
import com.wanda.credit.ds.dao.domain.qxb.Partners_should;
import com.wanda.credit.ds.dao.domain.qxb.Websites;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年12月7日 上午8:52:30 
 *  
 */
public interface IQXBQueryCorpInfoService {

	/**
	 *保存返回信息*/
	public void addAppCorpRsp(String trade_id,Map<String,Object> retdata);
	
	public void saveCorpInfo(String tradeId,JSONObject rspJsn,String keyword);

	public CorpBasic queryBasicInfo(String tradeId);

	public List<Branches> queryBranches(String tradeId);

	public List<Changerecords> queryChangerecords(String tradeId);

	public List<Eemployees> queryEmployees(String tradeId);

	public List<Partners> queryPartners(String tradeId);

	public List<Websites> queryWebsites(String tradeId);

	public Contact queryContact(String tradeId);

	public List<Abnormal_items> queryAbnormal_items(String tradeId);

	public List<Partners_should> queryShouldcapi(String tradeId,Long partnerId);

	public List<Partners_real> queryRealcapi(String tradeId,Long partnerId);

	public String getCachedTradeId(String keyWord, Integer valueOf);

}
