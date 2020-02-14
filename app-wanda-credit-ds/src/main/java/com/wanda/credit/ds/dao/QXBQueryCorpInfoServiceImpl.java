package com.wanda.credit.ds.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.ds.dao.domain.qxb.Abnormal_items;
import com.wanda.credit.ds.dao.domain.qxb.Branches;
import com.wanda.credit.ds.dao.domain.qxb.Changerecords;
import com.wanda.credit.ds.dao.domain.qxb.Contact;
import com.wanda.credit.ds.dao.domain.qxb.CorpBasic;
import com.wanda.credit.ds.dao.domain.qxb.Eemployees;
import com.wanda.credit.ds.dao.domain.qxb.Partners;
import com.wanda.credit.ds.dao.domain.qxb.Partners_real;
import com.wanda.credit.ds.dao.domain.qxb.Partners_should;
import com.wanda.credit.ds.dao.domain.qxb.QxbBaseDomain;
import com.wanda.credit.ds.dao.domain.qxb.Websites;
import com.wanda.credit.ds.dao.iface.IQXBQueryCorpInfoService;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年12月7日 上午11:13:16 
 *  
 */
@SuppressWarnings("unchecked")
@Service
public class QXBQueryCorpInfoServiceImpl implements IQXBQueryCorpInfoService{
	private final Logger logger = LoggerFactory
			.getLogger(QXBQueryCorpInfoServiceImpl.class);

	@Autowired
	private DaoService daoService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	/**
	 *保存返回信息*/
	@Override
	public void addAppCorpRsp(String trade_id,Map<String,Object> retdata){
		final String trade_ids = trade_id;
		final String retdatas = JSON.toJSONString(retdata);
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("{} 返回数据保存开始...", trade_ids);
				String sql = "INSERT INTO CPDB_APP.T_SYS_REQ_PARAM(TRADE_ID,KEY_CODE,VALUE) VALUES(?,?,?)";
				 daoService.getJdbcTemplate().update(sql, new Object[]{trade_ids,"P_B_B010",retdatas
                });
				 logger.info("{} 返回数据保存结束", trade_ids);
			}}).start();
		
	}
    @Override
	public void saveCorpInfo(String tradeId,JSONObject rspJsn,String keyword) {
		saveBasicInfo(tradeId,rspJsn,keyword);
		saveEmployees(tradeId,rspJsn.getJSONArray("employees"));
		saveBranches(tradeId,rspJsn.getJSONArray("branches"));
		saveChangerecords(tradeId,rspJsn.getJSONArray("changerecords"));
		savePartners(tradeId,rspJsn.getJSONArray("partners"));
		saveWebsites(tradeId,rspJsn.getJSONArray("websites"));		
		saveContact(tradeId,rspJsn.getJSONObject("contact"));		
		saveAbnormal_items(tradeId,rspJsn.getJSONArray("abnormal_items"));
	}

	private void saveAbnormal_items(String tradeId,JSONArray arr) {
		if(arr == null )return;
		 for(int i=0;i<arr.size();i++){
        	 convertAndSave(tradeId,arr.get(i),Abnormal_items.class);
         }	
	}

	private void saveContact(String tradeId,Object obj) {
		if(obj == null )return;
		Contact contact = convertPhase(tradeId,obj,Contact.class);
		if(contact != null){
			try {
				contact.setTelephone(synchExecutorService.encrypt(contact.getTelephone()));
			} catch (Exception e) {
				logger.error("encrype error",e);
			}
			try{
			daoService.create(contact); }catch(Exception e){
				logger.error("{} 插入数据库异常:{}",tradeId, ExceptionUtil.getTrace(e));
			}
		}
		
	}

	private void saveWebsites(String tradeId, JSONArray arr) {
		if (arr == null)
			return;
		for (int i = 0; i < arr.size(); i++) {
			try {
				Websites obj = JSONObject.parseObject(arr.get(i).toString(),
						Websites.class);
				if (obj.getWeb_url().getBytes("utf-8").length > 100) {
					obj.setWeb_url(obj.getWeb_url().substring(0, 100) + "...");
				}
				obj.setTrade_id(tradeId);
				daoService.create(obj);
			} catch (Exception e) {
				logger.error("{} 插入数据库异常:{}",tradeId, ExceptionUtil.getTrace(e));
			}
		}
	}

	private void savePartners(String tradeId,JSONArray arr) {
		if(arr == null)return ;
		 for(int i=0;i<arr.size();i++){
			 if(arr.get(i) == null )continue;
			 Partners partner = convertAndSave(tradeId,arr.get(i),Partners.class);
			 /**保存实际出资信息*/
			 JSONArray reals = 
					 arr.getJSONObject(i).getJSONArray("real_capi_items");
			 if(reals != null){
				 for(int j=0;j<reals.size();j++){
					 Partners_real real = convertPhase(tradeId,reals.get(j),Partners_real.class);
					 if(real!=null){
						 real.setPartnerid(partner.getId());
						 try{
						 daoService.create(real); 
						 }catch(Exception e){
							 logger.error("{} 插入数据库异常:{}",tradeId, ExceptionUtil.getTrace(e));
						 }
					 }
				 } 
			 }			 
			 /**保存应该出资信息*/
			 JSONArray shouds = 
					 arr.getJSONObject(i).getJSONArray("should_capi_items");
			if(shouds != null){
				 for(int j=0;j<shouds.size();j++){
					 Partners_should shoud = convertPhase(tradeId,shouds.get(j),Partners_should.class);
					 if(shoud!=null){
					  shoud.setPartnerid(partner.getId());
					 try{ daoService.create(shoud);}catch(Exception e){}
					 }
				 }
			}
         }	
	}

	private void saveChangerecords(String tradeId,JSONArray arr) {
		if(arr == null )return;
		 for(int i=0;i<arr.size();i++){
			 try{
				Changerecords obj = JSONObject.parseObject(arr.get(i).toString(),Changerecords.class);
				if(obj.getAfter_content().getBytes("utf-8").length > 4000){
					obj.setAfter_content(obj.getAfter_content().substring(0, 1000)+"...");
					}
				if(obj.getBefore_content().getBytes("utf-8").length > 4000){
					obj.setBefore_content(obj.getBefore_content().substring(0, 1000)+"...");
				}
				obj.setTrade_id(tradeId);
				daoService.create(obj);
			 }catch(Exception e){
				 logger.error("{} 插入数据库异常:{}",tradeId, ExceptionUtil.getTrace(e));
			 }
         }	
	}

	private void saveBranches(String tradeId,JSONArray arr) {
		if(arr == null )return;
		 for(int i=0;i<arr.size();i++){
        	 convertAndSave(tradeId,arr.get(i),Branches.class);
         }	
	}

	private void saveEmployees(String tradeId,JSONArray arr) {
		if(arr == null )return;
         for(int i=0;i<arr.size();i++){
        	 convertAndSave(tradeId,arr.get(i),Eemployees.class);
         }		
	}

	private <T extends QxbBaseDomain> T convertAndSave(String tradeId,Object object, Class<T> class1) {		
		if(object == null) return null;
		T obj = JSONObject.parseObject(object.toString(),class1);
		obj.setTrade_id(tradeId);
		try{
		  daoService.create(obj);
		}catch(Exception e){
			logger.error("{} 插入数据库异常:{}",tradeId, ExceptionUtil.getTrace(e));
		}
		return obj;
	}
	
	private <T extends QxbBaseDomain> T convertPhase(String tradeId,Object object, Class<T> class1) {
		if(object == null) return null;
		T obj = JSONObject.parseObject(object.toString(),class1);
		obj.setTrade_id(tradeId);
		return obj;
	}

	private void saveBasicInfo(String tradeId,JSONObject basicJsn,String keyword) {		
		CorpBasic obj = convertPhase(tradeId,basicJsn,CorpBasic.class);
		obj.setKeyword(keyword);
		obj.setDomain1(jsnArr2str(basicJsn.getJSONArray("domains")));
		obj.setCheck_date(cutshort(obj.getCheck_date(),50));
		try{
		   daoService.create(obj);
		}catch(Exception e){
			logger.error("{} 插入数据库异常:{}",tradeId, ExceptionUtil.getTrace(e));
		}
	}

	private String cutshort(String check_date, int maxsize) {
		
		try {
			if(check_date != null && 
					check_date.getBytes("utf-8").length > maxsize){
				return check_date.substring(0, maxsize/3)+"...";
				}
		} catch (UnsupportedEncodingException e) {
			 logger.error("error",e);
		}
		return check_date;
      }

	private String jsnArr2str(JSONArray jsonArray) {
		StringBuilder sb = new StringBuilder();
		if(jsonArray !=null){
			int i=0;
			for(Object item :jsonArray){
				if(i>0){sb.append(",");}
				sb.append(item);i++;
			}
		}
		return sb.toString();
	}

	@Override
	public CorpBasic queryBasicInfo(String tradeId) {
		CorpBasic basic = 
				daoService.findOneByHQL("From CorpBasic a where a.trade_id =:trade_id", newParamMap(tradeId));
		return basic;
	}

	private Map<String, Object> newParamMap(String tradeId) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("trade_id",tradeId);
		return params;
	}

	@Override
	public List<Branches> queryBranches(String tradeId) {
		List<Branches> obj = 
				daoService.findByHQL("From Branches a where a.trade_id =:trade_id", newParamMap(tradeId));
		return obj;
	}

	@Override
	public List<Changerecords> queryChangerecords(String tradeId) {
		List<Changerecords> obj = 
				daoService.findByHQL("From Changerecords a where a.trade_id =:trade_id", newParamMap(tradeId));
		return obj;
	}

	@Override
	public List<Eemployees> queryEmployees(String tradeId) {
		List<Eemployees> obj = 
				daoService.findByHQL("From Eemployees a where a.trade_id =:trade_id", newParamMap(tradeId));
		return obj;
	}

	@Override
	public List<Partners> queryPartners(String tradeId) {
		List<Partners> obj = 
				daoService.findByHQL("From Partners a where a.trade_id =:trade_id", newParamMap(tradeId));
		return obj;
	}

	@Override
	public List<Websites> queryWebsites(String tradeId) {
		List<Websites> obj = 
				daoService.findByHQL("From Websites a where a.trade_id =:trade_id", newParamMap(tradeId));
		return obj;
	}

	@Override
	public Contact queryContact(String tradeId) {
		Contact obj = 
				daoService.findOneByHQL("From Contact a where a.trade_id =:trade_id", newParamMap(tradeId));
		try {
			obj.setTelephone(synchExecutorService.decrypt(obj.getTelephone()));
		} catch (Exception e) {
			logger.error("decrypt error",e);
		}
		return obj;
	}

	@Override
	public List<Abnormal_items> queryAbnormal_items(String tradeId) {
		List<Abnormal_items> obj = 
				daoService.findByHQL("From Abnormal_items a where a.trade_id =:trade_id", newParamMap(tradeId));
		return obj;
	}

	@Override
	public List<Partners_should> queryShouldcapi(String tradeId,Long partnerId) {
		Map<String,Object> param = newParamMap(tradeId);
		param.put("partnerId",partnerId);
		List<Partners_should> obj = 
				daoService.findByHQL("From Partners_should a where a.trade_id =:trade_id and a.partnerid=:partnerId", param);
		return obj;
	}

	@Override
	public List<Partners_real> queryRealcapi(String tradeId,Long partnerId) {
		Map<String,Object> param = newParamMap(tradeId);
		param.put("partnerId",partnerId);
		List<Partners_real> obj = 
				daoService.findByHQL("From Partners_real a where a.trade_id =:trade_id and a.partnerid=:partnerId", param);
		return obj;
	}

	@Override
	public String getCachedTradeId(String keyWord, Integer cachedDays) {
		String trade_id = daoService.getJdbcTemplate().
		  queryForObject("SELECT MAX(a.trade_id) FROM "
		  		+ " cpdb_ds.t_ds_qxb_corp_basic a where "
		  		+ " a.keyword = ? and a.CREATE_time >= (SYSDATE - ?)", String.class,
		  		new Object[]{keyWord,cachedDays});
		
		return trade_id;
	}
	
	public static void  main(String[] args) throws FileNotFoundException, IOException{
/*		String jsn = IOUtils.toString(new FileInputStream("E:/temp/qxb.jsn"),"UTF-8");
		System.out.println(jsn);
		CorpBasic obj = JSONObject.parseObject(jsn, CorpBasic.class);
	    System.out.println("\n\n>>>>>>"+obj.getName());
*/	
		List obj = (List)JSONObject.parse("[1,2,3]");	
	    System.out.println("obj>>>"+ArrayUtils.toString(obj,","));
	}

}
