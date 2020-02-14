package com.wanda.credit.ds.client.qixinbao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.JsonFilter;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.zhongshunew.ZSCorpInfo_CUT_DataSourceRequestor;
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
import com.wanda.credit.ds.dao.iface.IQXBQueryCorpInfoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


/**
 * 启信宝工商信息查询数据源 
 * @author liunan
 * 
 * 
 * @date 2016年3月10日
 */
@SuppressWarnings("unchecked")
@DataSourceClass(bindingDataSourceId="ds_qxb_queryCorpInfo")
public class QXBQueryCorpInfoDataSourceRequestor extends BaseQXBDataSourceRequestor
		implements IDataSourceRequestor {	
	private final Logger logger = LoggerFactory
			.getLogger(QXBQueryCorpInfoDataSourceRequestor.class);

	private final static String DS_ID = "ds_qxb_queryCorpInfo";
	private final static String KEYTYPE = "2,3,5";
	@Autowired
	public IPropertyEngine propertyEngine;

	@Autowired
	private IQXBQueryCorpInfoService qxbQueryCorpInfoService;
	@Autowired
	private ZSCorpInfo_CUT_DataSourceRequestor zsCorpService;
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String,Object>();
		List<String> tags = new ArrayList<String>();
		String initTag = Conts.TAG_SYS_ERROR;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(DS_ID);
		logObj.setReq_url(propertyEngine.readById("qxb_qryCorp_url"));
		// 默认交易失败
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		/**数据总线*/
		Map<String,Object> ctx = new HashMap<String,Object>();
		ctx.put("prefix", prefix);
		boolean corp_mock = "1".equals(propertyEngine.readById("qxb_qryCorp_mock"));
		boolean corp_filter = "1".equals(propertyEngine.readById("qxb_qryCorp_filter"));
		boolean corp_zs_flag = "1".equals(propertyEngine.readById("qxb_qryCorp_zs_request"));//直接请求中数
		boolean request_zs_flag = false;
		boolean credit_null_flag = false;
		String keyWord = "";
		String keyType = "";
		String acct_id = "";
		try {
			CRSStatusEnum retStatus = CRSStatusEnum.STATUS_SUCCESS;
			String retMsg = "采集成功";
			
			/** keyword 是企业名称或者工商注册号 */
			keyWord = (String)ParamUtil
					.findValue(ds.getParams_in(), "keyWord");
			keyType = (String)ParamUtil
					.findValue(ds.getParams_in(), "keyType");
			acct_id = (String)ParamUtil
					.findValue(ds.getParams_in(), "acct_id");
			if(!KEYTYPE.contains(keyType)){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过:"+"查询关键字不能为空");
            	logger.error("{}传入参数类型错误 {}",prefix,keyType);
                return rets;
			}
			if(StringUtils.isBlank(keyWord) || StringUtils.isBlank(keyType)){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过:"+"查询关键字不能为空");
            	logger.error("{} 查询关键字不能为空:{}",prefix,rets.get(Conts.KEY_RET_MSG));
                return rets;
			}               
			int length = keyWord.length();
			if("3".equals(keyType) && (length!=13 && length!=15 && length!=18)){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_QIXINBAO_REGIST_FAIL);
				rets.put(Conts.KEY_RET_MSG, "企业注册号输入错误");
            	logger.error("{} 企业注册号输入错误:{}",prefix,rets.get(Conts.KEY_RET_MSG));
                return rets;
			}
			if("5".equals(keyType) && length<9){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_QIXINBAO_CODE_FAIL);
				rets.put(Conts.KEY_RET_MSG, "组织机构代码输入错误");
            	logger.error("{} 组织机构代码输入错误:{}",prefix,rets.get(Conts.KEY_RET_MSG));
                return rets;
			}
			ctx.put("keyWord", keyWord);
			ctx.put("trade_id", trade_id);

			DataSourceLogEngineUtil.writeParamIn(trade_id, CommonUtil.sliceMap(ctx, 
					new String[]{"keyWord"}),logObj);

			/**取缓存数据*/
//			String tradeId = getCached(keyWord,keyType,propertyEngine.readById("qxb_qryCorp_cachdays"));
			String tradeId = null;
			if(StringUtils.isNotBlank(tradeId)){
				logObj.setIncache("1");
				logger.info("{} 开始从本地缓存读取  {} 的企业工商信息数据,产品编号:{}",
						new Object[]{prefix,keyWord,ds.getRefProdCode()});
				if("P_B_B010".equals(ds.getRefProdCode())){
					qxbQueryCorpInfoService.addAppCorpRsp(trade_id, fetchFromCachedData(tradeId));
				}else{
					retdata.putAll(fetchFromCachedData(tradeId));
				}				
//				retdata.putAll(fetchFromCachedData(tradeId));
				retdata.put("found", "1");
				initTag = Conts.TAG_INCACHE_FOUND ;
			    logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logger.info("{} 本地数据读取完毕  ",new Object[]{prefix});
			}else{
				logObj.setIncache("0");
				/** 拼装请求报文 */
				String reqUrl = buildRequestUrl(ctx);
				if(!corp_zs_flag){
					logger.info("{} 开始启信宝工商信息查询请求:{}",prefix,reqUrl);
					/** 发送请求报文 */
			        DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id,reqUrl,new String[0]));
			        String rspStr = "";
			        if(corp_mock){
			        	logger.info("{} 工商信息查询请求走mock",prefix);
			        	rspStr = getMockTxt();
					}else{
						rspStr = RequestHelper.doGet(reqUrl, null, false);
						logger.info("{} 工商信息查询请求完成",prefix);
					}
//			        String rspStr = RequestHelper.doGet(reqUrl, null, false);
			        DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id,rspStr,new String[0]));
	                //logger.info("rspStr>>>{}",rspStr);
	                //logger.info("reqUrl>>>{}",reqUrl);
					JSONObject rspJsn = (JSONObject)JSONObject.parse(rspStr);
					/**检查响应信息是否成功*/
					if(isSuccessful(rspJsn)){
						logger.info("{} 启信宝工商信息查询数据成功",prefix);
						retdata.put("found", "1");
						initTag = Conts.TAG_FOUND;
					    logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					    logObj.setBiz_code1(rspJsn.getJSONObject("data").getString("id"));
						doSaveOper(trade_id,JsonFilter.getJsonKeys(rspJsn.getJSONObject("data"), "id"),keyWord);
											
						String credit_no = rspJsn.getJSONObject("data").getString("credit_no");
						if(StringUtil.isEmpty(credit_no) || "-".equals(credit_no)){
							credit_null_flag = true;
						}
						if("P_B_B010".equals(ds.getRefProdCode()) && !credit_null_flag){
							qxbQueryCorpInfoService.addAppCorpRsp(trade_id, visitBusiData(rspJsn,corp_filter));
						}else{
							retdata.putAll(visitBusiData(rspJsn,corp_filter));
						}
//						retdata.putAll(visitBusiData(rspJsn));
					}else if(isUnfound(rspJsn)){
					    logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						retdata.put("found", "0");
						initTag = Conts.TAG_UNFOUND;
					    logObj.setState_msg((String)rspJsn.get("message"));
					    logger.warn("{} 未查得企业信息:{},开始中数请求...",prefix,rspJsn.get("message"));
					    request_zs_flag = true;
					}else {
						initTag = Conts.TAG_SYS_ERROR;
					    logObj.setState_msg((String)rspJsn.get("message"));
					    logger.error("{} 企业基本信息查询出错:{}",prefix,rspJsn.get("message"));
						rets.put(Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
//						return rets;				
						request_zs_flag = true;
					}
				}	
			}
			rets.put(Conts.KEY_RET_STATUS, retStatus);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, retMsg);
		}catch (Exception ex) {
			request_zs_flag = true;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
	    	rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
		    logger.error("{} 数据源处理时异常:{}",prefix,ExceptionUtil.getTrace(ex));
			if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				initTag = Conts.TAG_SYS_TIMEOUT;
			} else {
				initTag = Conts.TAG_SYS_ERROR;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			}
       }finally{
    	   tags.add(initTag);
    	   logObj.setTag(StringUtils.join(tags, ";"));
    	   rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
    	   logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
    	   
    	   if(request_zs_flag || corp_zs_flag || credit_null_flag){
    		   logger.info("{} 启信宝未查询到数据,调用中数",prefix);
    		   if(credit_null_flag)
    		     logger.info("{} 启信宝统一信用代码为空,调用中数",prefix);  		   
				List<Param> params_in = new ArrayList<Param>();
				Param acct_ids = new Param();
				acct_ids.setId("acct_id");
				acct_ids.setValue(acct_id);
				ds.setId("ds_zsCorpQuery_cut");
				if("2".equals(keyType)){
					Param name = new Param();
					name.setId("name");
					name.setValue(keyWord);
					
					params_in.add(name);
					params_in.add(acct_ids);

					ds.setParams_in(params_in);
					rets = zsCorpService.request(trade_id, ds);
					logger.info("{} 中数返回:{}",prefix,JSON.toJSONString(rets));
					return rets;
				}else if("3".equals(keyType)){
					Param regno = new Param();
					regno.setId("regno");
					regno.setValue(keyWord);
					
					params_in.add(regno);
					params_in.add(acct_ids);

					ds.setParams_in(params_in);
					rets = zsCorpService.request(trade_id, ds);
					return rets;
				}else if("5".equals(keyType)){
					Param orgcode = new Param();
					orgcode.setId("orgcode");
					orgcode.setValue(keyWord);
					
					params_in.add(orgcode);
					params_in.add(acct_ids);

					ds.setParams_in(params_in);
					rets = zsCorpService.request(trade_id, ds);
					return rets;
				}
			}
			
            DataSourceLogEngineUtil.writeLog(trade_id, logObj);
		}
		return rets;
	}
	
	private Map<String, Object> visitBusiData(
			JSONObject rspJsn,boolean isFilter) {
		Map<String, Object> data = (Map<String, Object>)rspJsn.get("data");
		if(isFilter){
			JSONArray arr = (JSONArray) data.get("branches");
			JSONArray branches = new JSONArray();
			for(Object obj:arr){
				JSONObject json = (JSONObject) obj;
				JSONObject json1 = new JSONObject();
				json1.put("name", json.get("name"));
				branches.add(json1);
			}
			data.put("branches", branches);
		}		
		return data;
	}

	private void doSaveOper(String trade_id,JSONObject rspJsn,String keyword) {
		try{
			logger.error("{} 保存业务数据1开始...",trade_id);
			qxbQueryCorpInfoService.saveCorpInfo(trade_id,rspJsn,keyword);
			logger.error("{} 保存业务数据1结束",trade_id);
		}catch(Exception e){
			logger.error("{} 保存数据出错:{}",trade_id,ExceptionUtil.getTrace(e));
		}
		
	}

	private boolean isUnfound(JSONObject rspJsn) {
		if("201".equals(rspJsn.getString("status"))){
			return true;
		}
		return false;
	}

	private boolean isSuccessful(JSONObject rspJsn) {
		if("200".equals(rspJsn.getString("status"))){
			return true;
		}
		return false;
	}

	private String buildRequestUrl(Map<String, Object> ctx) {
		String url = new StringBuilder(propertyEngine.readById("qxb_qryCorp_url")).
				append("?appkey=").append(propertyEngine.readById("qxb_qryCorp_appkey"))
				.append("&keyword=").append(ctx.get("keyWord")).toString();
		
		return url;
	}

	private Map<String,Object> fetchFromCachedData(
			String tradeId) throws IllegalAccessException, InvocationTargetException {
		Map<String,Object> retData = new HashMap<String, Object>();

		CorpBasic basic = qxbQueryCorpInfoService.queryBasicInfo(tradeId);
		Map<String, Object> basicMap = bean2Map(basic);
		basicMap.remove("keyword");
        removeExtraKeys(basicMap);
        if(StringUtils.isNotBlank(basic.getDomain1())){
           retData.put("domains", basic.getDomain1().split(","));          
        }
        basicMap.remove("domain1");
		retData.putAll(basicMap);
        
		List<Eemployees> employees = qxbQueryCorpInfoService.queryEmployees(tradeId);
		if(CollectionUtils.isNotEmpty(employees)){
		  List<Map<String, Object>> employeesMap = beans2Maps(employees);
          retData.put("employees", employeesMap);
		}
		
		List<Branches> branches = qxbQueryCorpInfoService.queryBranches(tradeId);
		if(CollectionUtils.isNotEmpty(branches)){
		   List<Map<String, Object>> branchesMap = beans2Maps(branches);
           retData.put("branches", branchesMap);
		}
		List<Changerecords> changerecords = qxbQueryCorpInfoService.queryChangerecords(tradeId);
		if(CollectionUtils.isNotEmpty(changerecords)){
		   List<Map<String, Object>> changerecordsMap = beans2Maps(changerecords);
           retData.put("changerecords", changerecordsMap);
		}

		List<Partners> partners = qxbQueryCorpInfoService.queryPartners(tradeId);
		List<Map<String, Object>> partnersMap = beans2MapsNotRemoveExtraKeys(partners);
		if(CollectionUtils.isNotEmpty(partnersMap)){
		 for(Map<String, Object> partner : partnersMap){
			  if(partner == null)continue;
			  Long partnerid = Long.valueOf(partner.get("id").toString());
	 		  List<Partners_should> shoulds = qxbQueryCorpInfoService.queryShouldcapi(tradeId,partnerid);
	 		  List<Partners_real> reals = qxbQueryCorpInfoService.queryRealcapi(tradeId,partnerid);
	 		  partner.put("should_capi_items",beans2Maps(shoulds,"partnerid"));
	 		  partner.put("real_capi_items",beans2Maps(reals,"partnerid")); 	
	 		  removeExtraKeys(partner);
	 	    }
	        retData.put("partners", partnersMap);
		}


		List<Websites> websites = qxbQueryCorpInfoService.queryWebsites(tradeId);
        if(CollectionUtils.isNotEmpty(websites)){
    		List<Map<String, Object>> websitesMap = beans2Maps(websites);
            retData.put("websites", websitesMap);
        }
        
        Contact contact = qxbQueryCorpInfoService.queryContact(tradeId);
        if(contact !=null){
        	Map<String, Object> contactMap = bean2Map(contact);
            removeExtraKeys(contactMap);
            retData.put("contact", contactMap);	
        }		

		List<Abnormal_items> abnormal = qxbQueryCorpInfoService.queryAbnormal_items(tradeId);
		if(CollectionUtils.isNotEmpty(abnormal)){
		  List<Map<String, Object>> abnormalMap = beans2Maps(abnormal);
          retData.put("abnormal_items", abnormalMap);
		}
        return retData;
	}


	private void removeExtraKeys(Map<String, Object> map) {
		map.remove("trade_id");map.remove("id");
		
	}

	private String getCached(String keyWord, String cachedDays) {
		String tradeId = qxbQueryCorpInfoService.getCachedTradeId(
				keyWord,Integer.valueOf(cachedDays));
		return tradeId;
	}
   private <T> List<Map<String, Object>> beans2Maps(List<T> beans,String...removeKeys) {
	   List<Map<String, Object>> maps=new ArrayList<Map<String, Object>>();
	   for(T bean : beans){
		   if(bean == null)continue;
		   Map<String, Object> map = bean2Map(bean);
		   removeExtraKeys(map);
		   for(String key : removeKeys){map.remove(key);}
		   maps.add(map);   
	   }	  
		return maps;
	}
   
   private <T> List<Map<String, Object>> beans2MapsNotRemoveExtraKeys(List<T> beans) {
	   List<Map<String, Object>> maps=new ArrayList<Map<String, Object>>();
	   for(T bean : beans){
		   if(bean == null)continue;
		   Map<String, Object> map = bean2Map(bean);
		   maps.add(map);   
	   }	  
		return maps;
	}
   
    @SuppressWarnings("unchecked")  
    public <T> Map<String, Object> bean2Map(T javaBean) {  
        Map<String, Object> ret = new HashMap<String, Object>();  
        try {  
            Method[] methods = javaBean.getClass().getDeclaredMethods();  
            for (Method method : methods) {  
                if (method.getName().startsWith("get")) {  
                    String field = method.getName();  
                    field = field.substring(field.indexOf("get") + 3);  
                    field = field.toLowerCase().charAt(0) + field.substring(1);  
                    Object value = method.invoke(javaBean, (Object[]) null);  
                    if(value != null){
                      ret.put(field,value); 
                    }
                }  
            }  
        } catch (Exception e) {  
        	logger.error("bean2Map method error",e);
       	}  
        return ret;  
    }
}
