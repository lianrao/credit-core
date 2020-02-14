package com.wanda.credit.ds.client.jiewei;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.dao.iface.IJieWeiQueryCorpInfoService;

/**
 * 处理捷为工商信息查询输出辅助类
 * @author changsheng.wu
 * */
@Component
public class JieWeiFormatOutputSupport {

	private final Logger logger = LoggerFactory
			.getLogger(JieWeiFormatOutputSupport.class);

	@Autowired
	private IJieWeiQueryCorpInfoService corpDaoService;
  	
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@Autowired
	public IPropertyEngine propertyEngine;

    /**
     * @param refid 数据库主键ID
     * 根据缓存数据的主键ID
     * 获取最近30*3天 该企业的工商信息
     * */
	public Map<String,Object> fetchFromCachedData(String refid) {		
		/**顶节点*/
		Map<String,Object> top = new HashMap<String, Object>();

		/**以map结构读取reports信息*/
		Map<String,Object> reports = readMappedReports(refid);

		/**以map结构读取report信息*/
		Map<String,Object> report = readMappedReport(refid);
		
	    /**以map结构读取corpBaseNationalInfo信息*/
		Map<String,Object> base = readMappedBase(refid);
		
	    /**以map结构读取nationalCorpShareholderInfo信息*/
		Map<String,Object> holder = readMappedShareholder(refid);

		/**以map结构读取nationalCorpManagementInfo信息*/
		Map<String,Object> management = readMappedManagement(refid);

	    /**以map结构读取nationalCorpBranchInfo信息*/
		Map<String,Object> branch = readMappedBranch(refid);

	    /**以map结构读取penaltyInfos信息*/
		Map<String,Object> penalty = readMappedPenalty(refid);

		/**组织各个map后返回*/
		top.put("cisReports", reports);
		reports.put("cisReport", report);
		
		if(base != null){
			report.put("corpBaseNationalInfo", base);
		}
		if(holder != null){
			report.put("nationalCorpShareholderInfo", holder);
		}
		if(management != null){
			report.put("nationalCorpManagementInfo", management);
		}
		if(branch != null){
			report.put("nationalCorpBranchInfo", branch);
		}
		if(penalty != null){
			report.put("penaltyInfos", penalty);
		}
		
		return top;
	}

	private Map<String, Object> readMappedPenalty(String refid) {
		List<Map<String,Object>> penaltys = corpDaoService.getCorpPenaltyInfos(refid);
		if(penaltys == null || penaltys.size() == 0) return null;
		
		Map<String,Object> retMap = new HashMap<String, Object>();
		fetchCommonPro(penaltys.get(0),retMap);
		/**1 才代表查得*/
		String resultFlag = (retMap.get("treatResult") !=null ? retMap.get("treatResult").toString() : null);
		if("1".equals(resultFlag)){
			/**其他属性保存到item 数组节点去*/
	        List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
	        retMap.put("item", items);
	        Map<String,Object> itemMap;
	        for(Map<String,Object> penalty : penaltys){
	        	itemMap = new HashMap<String, Object>();
	    		items.add(itemMap);
	     		itemMap.put("recordNo", penalty.get("RECORDNO"));
	    		itemMap.put("affair", penalty.get("AFFAIR"));
	    		itemMap.put("panalty", penalty.get("PANALTY"));
	    		itemMap.put("execDepartment", penalty.get("EXECDEPARTMENT"));
	    		itemMap.put("recordDate", penalty.get("RECORDDATE"));
	        }	
		}
		return retMap;
	}



	private Map<String, Object> readMappedBranch(String refid) {
		List<Map<String,Object>> branchs = corpDaoService.getCorpBranch(refid);
		if(branchs == null || branchs.size() == 0) return null;
		
		Map<String,Object> retMap = new HashMap<String, Object>();
		fetchCommonPro(branchs.get(0),retMap);
		/**1 才代表查得*/
		String resultFlag = (retMap.get("treatResult") !=null ? retMap.get("treatResult").toString() : null);
		if("1".equals(resultFlag)){
			/**其他属性保存到item 数组节点去*/
	        List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
	        retMap.put("item", items);
	        Map<String,Object> itemMap;
	        for(Map<String,Object> branch : branchs){
	        	itemMap = new HashMap<String, Object>();
	    		items.add(itemMap);
	     		itemMap.put("registerNo", branch.get("REGISTERNO"));
	    		itemMap.put("branchName", branch.get("BRANCHNAME"));
	    		itemMap.put("registerDepartment", branch.get("REGISTERDEPARTMENT"));
	        }	
		}		
		return retMap;
	}



	private Map<String, Object> readMappedManagement(String refid) {
		List<Map<String,Object>> managers = corpDaoService.getCorpManagement(refid);
		if(managers == null || managers.size() == 0) return null;
		
		Map<String,Object> retMap = new HashMap<String, Object>();
		fetchCommonPro(managers.get(0),retMap);
		/**1 才代表查得*/
		String resultFlag = (retMap.get("treatResult") !=null ? retMap.get("treatResult").toString() : null);
		if("1".equals(resultFlag)){
			/**其他属性保存到item 数组节点去*/
	        List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
	        retMap.put("item", items);
	        Map<String,Object> itemMap;
	        for(Map<String,Object> manager : managers){
	        	itemMap = new HashMap<String, Object>();
	    		items.add(itemMap);
	    		itemMap.put("name", manager.get("NAME"));
	    		itemMap.put("role", manager.get("ROLE"));
	        }	
		}		
		return retMap;
	}



	private Map<String, Object> readMappedShareholder(String refid) {
		List<Map<String,Object>> holders = corpDaoService.getCorpShareholder(refid);
		if(holders == null || holders.size() == 0) return null;
		
		Map<String,Object> retMap = new HashMap<String, Object>();
		fetchCommonPro(holders.get(0),retMap);
		/**1 才代表查得*/
		String resultFlag = (retMap.get("treatResult") !=null ? retMap.get("treatResult").toString() : null);
		if("1".equals(resultFlag)){
			/**其他属性保存到item 数组节点去*/
	        List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
	        retMap.put("item", items);
	        Map<String,Object> itemMap;
	        for(Map<String,Object> holder : holders){
	        	itemMap = new HashMap<String, Object>();
	    		items.add(itemMap);
	    		itemMap.put("name", holder.get("NAME"));
	    		itemMap.put("type", holder.get("TYPE"));
	    		itemMap.put("certType", holder.get("CERTTYPE"));
	    		try {
					itemMap.put("certID", synchExecutorService.decrypt((String)holder.get("CERTID")));
				} catch (Exception e) {
					logger.error("decrypt certid exception",e);
				}
	    		itemMap.put("contributiveType", holder.get("CONTRIBUTIVETYPE"));
	    		itemMap.put("contributiveFund", holder.get("CONTRIBUTIVEFUND"));
	        }	
		}		
		return retMap;
	}

	
    /**
     * @param source 源数据
     * @param dest  目标
     * 提取dest的4大公共属性给source
     * */
    private void fetchCommonPro(Map<String, Object> source,
			Map<String, Object> dest) {
    	dest.put("subReportType", source.get("SUBREPORTTYPE"));
    	dest.put("treatResult", source.get("TREATRESULT"));
    	dest.put("treatErrorCode", source.get("TREATERRORCODE"));
    	dest.put("errorMessage", source.get("ERRORMESSAGE"));		
	}



	/**
     * base's properies：
     * subReportType treatResult treatErrorCode errorMessage
     * item:[]
     * */
	private Map<String, Object> readMappedBase(String refid) {
		/**[理论上永远只有一条记录]*/
		List<Map<String,Object>> bases = corpDaoService.getCorpNational(refid);
		if(bases == null || bases.size() == 0) return null;
		/**只读取第一条记录*/
		Map<String,Object> base = bases.get(0);
		Map<String,Object> retMap = new HashMap<String, Object>();
		fetchCommonPro(base,retMap);
		/**其他属性保存到item 数组节点去[理论上永远只有一条记录]*/
        List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
        retMap.put("item", items);
		Map<String,Object> itemMap = new HashMap<String, Object>();
		items.add(itemMap);
		itemMap.put("corpName", base.get("CORPNAME"));
		itemMap.put("registerNo", base.get("REGISTERNO"));
		itemMap.put("registDate", base.get("REGISTDATE"));
		itemMap.put("artificialName", base.get("ARTIFICIALNAME"));
		itemMap.put("status", base.get("STATUS"));
		itemMap.put("registFund", base.get("REGISTFUND"));
		itemMap.put("manageRange", base.get("MANAGERANGE"));
		itemMap.put("openDate", base.get("OPENDATE"));
		itemMap.put("manageBeginDate", base.get("MANAGEBEGINDATE"));
		itemMap.put("manageEndDate", base.get("MANAGEENDDATE"));
		itemMap.put("corpType", base.get("CORPTYPE"));
		itemMap.put("registerDepartment", base.get("REGISTERDEPARTMENT"));
		itemMap.put("registerAddress", base.get("REGISTERADDRESS"));
		return retMap;
	}


	/**
	 * 读t_ds_jw_corp_rsp 表取 
	 * report's properties:
	 * reportID buildEndTime
	 * */
	private Map<String, Object> readMappedReport(String refid) {
		Map<String,Object> report = corpDaoService.getCorpRsp(refid);
		Map<String,Object> retMap = new HashMap<String, Object>();
		retMap.put("reportID", report.get("REPORTID"));
		retMap.put("buildEndTime", formatTime((Timestamp)report.get("BUILDENDTIME")));
		return retMap;
	}


	/**
	 * 读t_ds_jw_corp_rsp 表取 
	 * reports's properties:
	 * batNo  refID  receiveTime 
	 * */
	private Map<String, Object> readMappedReports(String refid) {
		Map<String,Object> reports = corpDaoService.getCorpRsp(refid);
		Map<String,Object> retMap = new HashMap<String, Object>();
		retMap.put("batNo", reports.get("BATNO"));
		/**
		 * 可以考虑不用输出给用户
		 * retMap.put("refID", reports.get("refID"));
		 * retMap.put("receiveTime", formatTime((Timestamp)reports.get("receiveTime")));
		 * */
		return retMap;
	}



	private Object formatTime(Timestamp timestamp) { 
		if(timestamp == null) return null ;
		return 
				BaseJieWeiDataSourceRequestor.sqlDate2Str(timestamp);
	}



	/**
	 *检查最近30*3天的缓存记录
	 *@param corpName 企业名称
	 *@param registerNo 工商注册号或者统一代码
	 *@param province：省份代码
	 *@return 返回主键记录的ID
	*/
	public String getCachedKey(String corpName,String registerNo,String province) {
		    String cachedDays = propertyEngine.readById("jiewei_cache_days");
			String id = getCachedKey(corpName, registerNo, province,Integer.valueOf(cachedDays));
            return id;
	}
    
	/**
	 *检查最近 @param cachedDays 天的缓存记录
	 *@param corpName 企业名称
	 *@param registerNo 工商注册号或者统一代码
	 *@param province：省份代码
	 *@return 返回主键记录的ID
	*/
	private String getCachedKey(String corpName,String registerNo,String province,int cachedDays) {
		String id = corpDaoService.getCachedKey(corpName, registerNo, province, 
				BaseJieWeiDataSourceRequestor.QUERYTYPE,cachedDays);
        return id;
}

	 
}
