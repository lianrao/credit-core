package com.wanda.credit.ds.client.zhongshunew;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.zhongshunew.ZS_Order;
import com.wanda.credit.ds.dao.iface.IQXBQueryCorpInfoService;
import com.wanda.credit.ds.dao.iface.IZSNewOrderService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**新中数新接口：// entinfo:企业详情查询企业版-简版  
 * add by wj 20180705*/
@DataSourceClass(bindingDataSourceId="ds_zsCorpQuery_cut")
public class ZSCorpInfo_CUT_DataSourceRequestor extends BaseZS_NEW_DataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZSCorpInfo_CUT_DataSourceRequestor.class);
	private static String[] paramsIds1 = {"mask","version","enttype"};//非重要业务参数ID
	
	@Autowired
	public IZSNewOrderService newOrderService;
	@Autowired
	private IQXBQueryCorpInfoService qxbQueryCorpInfoService;
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}新中数请求开始...",  prefix);

		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, String> reqparam = new HashMap<String, String>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setIncache("0");//不缓存
		logObj.setDs_id(ds.getId());
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		String resource_tag = Conts.TAG_SYS_ERROR;
		String resource_ds_tag = Conts.TAG_SYS_ERROR;
		
		try{
			//id,creditcode,regno,name,orgcode,mask,version,enttype
			//新中数企业ID,统一信用代码,企业注册号,企业名称,组织机构代码,查询掩码,个人标识版本号_返回个人标识码时生效 ,企业类型:1-企业 2-个体
			rets = new HashMap<String, Object>();
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			
			String[] infos = propertyEngine.readByIds("ds_zs_new_entinfo", 
					"ds_zs_cut_corpUrl", "ds_zs_cut_UID", "ds_zs_cut_SECURITY_KEY",
					"ds_zs_new_encode_version", "ds_zs_new_encode_paramsIds",
					"ds_zs_new_notparamids");
			String paramsStr = "?";
			for (String paramId : paramIds) {
				Object paramValue = ParamUtil.findValue(ds.getParams_in(), paramId);
                if(!StringUtil.isEmpty(paramValue)
                		&& !Arrays.asList(infos[6]).contains(paramId)) //排除非数据源参数 如acct_id
                	paramsStr = paramsStr + paramId + "=" + URLEncoder.encode(paramValue.toString(),"utf-8") + "&";
                else  if(StringUtil.isEmpty(paramValue)
                		&& "acct_id".equals(paramId)){ //acct_id
    				rets.put(Conts.KEY_RET_MSG, "acct_id不可为空!");
    				return rets;
            	}
                reqparam.put(paramId, String.valueOf(paramValue));
			}
			if(!StringUtil.areNotEmpty(infos)){
				rets.put(Conts.KEY_RET_MSG, "模型参数有空值，请检查！");
				return rets;
			}
			//id、creditcode、regno、name、orgcode中至少有一个不能为空,当同时传入这五个参数的时候，匹配优先级依次是：id,creditcode,regno,name,orgcode
			boolean flag = false;
			for (String paramId : paramIds) {
				if(!Arrays.asList(paramsIds1).contains(paramId)
						&&paramsStr.contains(paramId + "=")){
					flag = true;
				}
			}
			if(!flag){
				rets.put(Conts.KEY_RET_MSG, "主要业务参数全部为空，请检查!");
				return rets;
			}
			paramsStr = paramsStr.substring(0, paramsStr.length()-1);
			
			logObj.setReq_url(infos[1] + infos[0]);
			
			ZS_Order order = new ZS_Order();
			order.setTRADE_ID(trade_id);
			order.setZS_API(infos[0]);
			order.setCODE("-1");order.setMSG("尚未请求数据源");
			
			String url = infos[1] + infos[0] + paramsStr;
			logger.info("{} 新中数开始请求-----", prefix);
			String res = callApi(url, prepareHeaders(infos[2], infos[3], trade_id), trade_id);
			logger.info("{} 新中数结束请求-----", prefix);
			
	        JSONObject json = JSONObject.fromObject(res);
	      //保存企业详情到数据库
	        Map<String ,String> tagMap = saveIntoDB(json, order, ds.getParams_in());
	        resource_tag = tagMap.get("tag");
	        resource_ds_tag = tagMap.get("ds_tag");
	        rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
	        logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
	        Map<String,Object> retdata = new HashMap<String,Object>();
	        if("200".equals(json.getString("CODE"))){	
	        	qxbQueryCorpInfoService.addAppCorpRsp(trade_id, buildZSOfQixinbao(json.getJSONObject("ENT_INFO")));
	        	retdata.put("found", "1");
	        	rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "企业详情采集成功!");					
	        }else{
	        	retdata.put("found", "0");
	        	rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "企业详情采集成功!");		
	        }	
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			resource_ds_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常");
			logger.error(prefix+" 数据源处理时异常：{}",ex);
			
			/**如果是超时异常 记录超时信息*/
		    if(ExceptionUtil.isTimeoutException(ex)){	
		    	resource_tag = Conts.TAG_SYS_TIMEOUT;
		    	resource_ds_tag = Conts.TAG_SYS_TIMEOUT;
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);		    	
		    }
		    logObj.setState_msg(ex.getMessage());
		    rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{
			//log入库
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_ds_tag);
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, JSONObject.fromObject(reqparam), logObj);
		}
		return rets;
	}
	private JSONObject buildZSOfQixinbao(JSONObject enf){
		JSONObject data = enf.getJSONObject("BASIC");
		JSONObject result = new JSONObject();
		JSONArray arr1 = new JSONArray();
		if(data.getString("INDUSTRYCONAME")!=null){
			arr1.add(data.getString("INDUSTRYCONAME"));
		}		
		//主要人员信息
		JSONArray employee = new JSONArray();
		JSONArray employees = enf.getJSONArray("PERSON");
		for(Object tmp:employees){
			JSONObject json = (JSONObject) tmp;
			JSONObject json_tmp = new JSONObject();
			json_tmp.put("name", json.getString("PERNAME")!=null?json.getString("PERNAME"):"-");
			json_tmp.put("job_title", json.getString("POSITION")!=null?json.getString("POSITION"):"-");
			employee.add(json_tmp);
		}
		//股东信息
		JSONArray partner = new JSONArray();
		JSONArray partners = enf.getJSONArray("SHAREHOLDER");
		for(Object tmp:partners){
			JSONObject json = (JSONObject) tmp;
			JSONObject json_tmp = new JSONObject();
			//认缴出资
			JSONArray should_capi_items = new JSONArray();
			JSONObject should_capi = new JSONObject();
			should_capi.put("should_capi_date", json.getString("CONDATE")!=null?json.getString("CONDATE"):"-");
			should_capi.put("invest_type", json.getString("CONFORM")!=null?json.getString("CONFORM"):"-");
			should_capi.put("shoud_capi", json.getString("SUBCONAM")!=null?json.getString("SUBCONAM"):"-");
			should_capi_items.add(should_capi);
			//实缴出资
			JSONArray real_capi_items = new JSONArray();
			JSONObject real_capi = new JSONObject();
			real_capi.put("real_capi_date", "-");
			real_capi.put("invest_type", "-");
			real_capi.put("real_capi", json.getString("ACCONAM")!=null?json.getString("ACCONAM"):"-");
			real_capi_items.add(real_capi);
			
			json_tmp.put("name", json.getString("SHANAME")!=null?json.getString("SHANAME"):"-");
			json_tmp.put("identify_no", "-");
			json_tmp.put("identify_type", "非公示项");
			json_tmp.put("should_capi_items", should_capi_items);
			json_tmp.put("real_capi_items", real_capi_items);
			partner.add(json_tmp);
		}
		result.put("name", data.getString("ENTNAME")!=null?data.getString("ENTNAME"):"-");
		result.put("econ_kind", data.getString("ENTTYPE")!=null?data.getString("ENTTYPE"):"-");
		result.put("regist_capi", data.getString("REGCAP")!=null?data.getString("REGCAP"):"-");
		result.put("address", data.getString("DOM")!=null?data.getString("DOM"):"-");
		result.put("reg_no", data.getString("REGNO")!=null?data.getString("REGNO"):"-");
		result.put("scope", data.getString("ZSOPSCOPE")!=null?data.getString("ZSOPSCOPE"):"-");
		result.put("term_start", data.getString("OPFROM")!=null?data.getString("OPFROM"):"-");
		result.put("term_end", data.getString("OPTO")!=null?data.getString("OPTO"):"-");
		result.put("oper_name", data.getString("FRNAME")!=null?data.getString("FRNAME"):"-");
		result.put("check_date", data.getString("APPRDATE")!=null?data.getString("APPRDATE"):"-");
		result.put("start_date", data.getString("ESDATE")!=null?data.getString("ESDATE"):"-");
		result.put("end_date", data.getString("CANDATE")!=null?data.getString("CANDATE"):"-");
		result.put("status", data.getString("ENTSTATUS")!=null?data.getString("ENTSTATUS"):"-");
		result.put("org_no", data.getString("ORGCODES")!=null?data.getString("ORGCODES"):"-");
		result.put("credit_no", data.getString("CREDITCODE")!=null?data.getString("CREDITCODE"):"-");
		result.put("city", data.getString("REGCITY")!=null?data.getString("REGCITY"):"-");
		result.put("domains", arr1);
		result.put("employees", employee);
		result.put("partners", partner);
		result.put("abnormal_items", new JSONArray());
		result.put("belong_org", data.getString("REGORG")!=null?data.getString("REGORG"):"-");
		result.put("branches", new JSONArray());
		result.put("changerecords", new JSONArray());
		result.put("contact", new JSONObject());
		result.put("history_names", new JSONArray());
		result.put("id", "-");
		result.put("is_quoted", "-");
		result.put("province", "-");
		result.put("quoted_type", new JSONArray());
		result.put("websites", new JSONArray());
		return result;
	}
	private Map<String ,String> saveIntoDB(JSONObject json, ZS_Order order, List<Param> params) throws ServiceException{
		Map<String ,String> tagMap = new HashMap<String, String>();
		String tag = "";
		String ds_tag = "";
		logger.info("{} 开始组织数据bean对象=====", order.getTRADE_ID());
		order.setAcct_id(String.valueOf(ParamUtil.findValue(params, "acct_id")));
		
		order.setENTID(String.valueOf(ParamUtil.findValue(params, "id")));
		order.setCREDITCODE(String.valueOf(ParamUtil.findValue(params, "creditcode")));
		order.setREGNO(String.valueOf(ParamUtil.findValue(params, "regno")));
		order.setORGCODE(String.valueOf(ParamUtil.findValue(params, "orgcode")));
		order.setENTNAME(String.valueOf(ParamUtil.findValue(params, "name")));
		
		order.setMASK(String.valueOf(ParamUtil.findValue(params, "mask")));
		order.setVERSION(String.valueOf(ParamUtil.findValue(params, "version")));
		order.setENTTYPE(String.valueOf(ParamUtil.findValue(params, "enttype")));

		order.setCODE(json.getString("CODE"));
        order.setMSG(json.getString("MSG"));

		if(StringUtil.isEmpty(json) || !"200".equals(json.getString("CODE"))){
			tag = Conts.TAG_UNFOUND;
			ds_tag = Conts.TAG_UNFOUND;
		} else {
    		logger.info("{} 新中数企业照面信息数据库查询开始...", order.getTRADE_ID());
    		//String months = propertyEngine.readById("ds_zs_incache_month");
    		//按acct_id查询
    		Map<String,Object> getResultMap = newOrderService.inCached(order);//, months);
    		if("1".equals(getResultMap.get("STAT"))){
    			tag = Conts.TAG_FOUND_OLDRECORDS;
    		}else{
    			tag = Conts.TAG_FOUND_NEWRECORDS;
    		}
    		//去除acct_id因素
    		Map<String,Object> getResultDsMap = newOrderService.inCachedDs(order);//, months);
    		if("1".equals(getResultDsMap.get("STAT"))){
    			ds_tag = Conts.TAG_FOUND_OLDRECORDS;
    		}else{
    			ds_tag = Conts.TAG_FOUND_NEWRECORDS;
    		}
    		logger.info("{} 新中数企业照面信息数据库查询结束!", order.getTRADE_ID());
	        
//        	JSONObject ent_info_json = json.getJSONObject("ENT_INFO");
//        	if(!StringUtil.isEmpty(ent_info_json)){
//        		//1-企业照面（基本）信息
//        		String basicinfo = ent_info_json.getString("BASIC");
//        		ZS_Corp_Basic basic =
//        				JSON.parseObject(basicinfo, new TypeReference<ZS_Corp_Basic>(){});
//        		basic.setORDER(order);
//        		basic.setAcct_id(order.getAcct_id());
//        		order.setBASIC(basic);
//
//        		//2-主要管理人员
//        		String personList = ent_info_json.getString("PERSON");
//        		ArrayList<ZS_Corp_Person> persons = 
//        				JSON.parseObject(personList, new TypeReference<ArrayList<ZS_Corp_Person>>(){});
//        		for (ZS_Corp_Person zs_Corp_Person : persons) {
//        			zs_Corp_Person.setORDER(order);
//				}
//        		order.setPERSONS(persons);
//        		
//        		//3-法定代表人其他公司任职
//        		String frpositionList = ent_info_json.getString("FRPOSITION");
//        		ArrayList<ZS_Corp_Frposition> frpositions =
//        				JSON.parseObject(frpositionList, new TypeReference<ArrayList<ZS_Corp_Frposition>>(){});
//        		for (ZS_Corp_Frposition frposition : frpositions) {
//        			frposition.setORDER(order);
//				}
//        		order.setFRPOSITIONS(frpositions);
////        	
//        		//4-企业对外投资信息
//        		String zsentinvList = ent_info_json.getString("ENTINV");
//        		ArrayList<ZS_Corp_Entinv> zsentinvs =
//        				JSON.parseObject(zsentinvList, new TypeReference<ArrayList<ZS_Corp_Entinv>>(){});
//        		for (ZS_Corp_Entinv zsentinv : zsentinvs) {
//        			zsentinv.setORDER(order);
//				}
//        		order.setZSENTINVS(zsentinvs);
//        		
//        		//5/6-被执行人/关联被执行人信息
//        		String punishedList = ent_info_json.getString("PUNISHED");
//        		ArrayList<ZS_Corp_Punished> punisheds =
//        				JSON.parseObject(punishedList, new TypeReference<ArrayList<ZS_Corp_Punished>>(){});
//        		String relatedpunishedList = ent_info_json.getString("RELATEDPUNISHED");
//        		ArrayList<ZS_Corp_Punished> relatedpunisheds =
//        				JSON.parseObject(relatedpunishedList, new TypeReference<ArrayList<ZS_Corp_Punished>>(){});
//        		
//        		for (ZS_Corp_Punished punished : punisheds) {
//        			punished.setORDER(order);
//        			punished.setISRELATED("N");
//				}
//        		for (ZS_Corp_Punished punished : relatedpunisheds) {
//        			punished.setORDER(order);
//        			punished.setISRELATED("Y");
//				}
//        		punisheds.addAll(relatedpunisheds);
//        		order.setPUNISHEDS(punisheds);
//        		
//        		//7-变更信息
//        		String alterList = ent_info_json.getString("ALTER");
//        		ArrayList<ZS_Corp_Alter> alters =
//        				JSON.parseObject(alterList, new TypeReference<ArrayList<ZS_Corp_Alter>>(){});
//        		for (ZS_Corp_Alter alter : alters) {
//        			alter.setORDER(order);
//				}
//        		order.setALTER(alters);
//
//        		//8-分支机构
//        		String filiationList = ent_info_json.getString("FILIATION");
//        		ArrayList<ZS_Corp_Filiation> filiations =
//        				JSON.parseObject(filiationList, new TypeReference<ArrayList<ZS_Corp_Filiation>>(){});
//        		for (ZS_Corp_Filiation filiation : filiations) {
//        			filiation.setORDER(order);
//				}
//        		order.setFILIATION(filiations);
//
//        		//9-股权冻结信息
//        		String sharesfrostList = ent_info_json.getString("SHARESFROST");
//        		ArrayList<ZS_Corp_Sharesfrost> sharesfrosts =
//        				JSON.parseObject(sharesfrostList, new TypeReference<ArrayList<ZS_Corp_Sharesfrost>>(){});
//        		for (ZS_Corp_Sharesfrost obj : sharesfrosts) {
//        			obj.setORDER(order);
//				}
//        		order.setSHARESFROSTS(sharesfrosts);
//
//        		//10/11-失信被执行人/关联失信被执行人信息
//        		String punishbreakList = ent_info_json.getString("PUNISHBREAK");
//        		ArrayList<ZS_Corp_Punishbreak> punishbreaks =
//        				JSON.parseObject(punishbreakList, new TypeReference<ArrayList<ZS_Corp_Punishbreak>>(){});
//        		String relatedpunishbreakList = ent_info_json.getString("RELATEDPUNISHBREAK");
//        		ArrayList<ZS_Corp_Punishbreak> relatedpunishbreaks =
//        				JSON.parseObject(relatedpunishbreakList, new TypeReference<ArrayList<ZS_Corp_Punishbreak>>(){});
//        		
//        		for (ZS_Corp_Punishbreak obj : punishbreaks) {
//        			obj.setORDER(order);
//        			obj.setISRELATED("N");
//				}
//        		for (ZS_Corp_Punishbreak obj : relatedpunishbreaks) {
//        			obj.setORDER(order);
//        			obj.setISRELATED("Y");
//				}
//        		punishbreaks.addAll(relatedpunishbreaks);
//        		
//        		order.setPUNISHBREAKS(punishbreaks);
//
//        		//12-股东及出资信息
//        		String shareholderList = ent_info_json.getString("SHAREHOLDER");
//        		ArrayList<ZS_Corp_Shareholder> shareholders =
//        				JSON.parseObject(shareholderList, new TypeReference<ArrayList<ZS_Corp_Shareholder>>(){});
//        		for (ZS_Corp_Shareholder obj : shareholders) {
//        			obj.setORDER(order);
//				}
//        		order.setSHAREHOLDERS(shareholders);
// 	
//
//        		//13-企业照面（基本）信息
//        		String frinvList = ent_info_json.getString("FRINV");
//        		ArrayList<ZS_Corp_Frinv> frinvs =
//        				JSON.parseObject(frinvList, new TypeReference<ArrayList<ZS_Corp_Frinv>>(){});
//        		for (ZS_Corp_Frinv obj : frinvs) {
//        			obj.setORDER(order);
//				}
//        		order.setFRINVS(frinvs);
//
//        		//14-动产抵押-变更信息
//        		String mortgagealtList = ent_info_json.getString("MORTGAGEALT");
//        		ArrayList<ZS_Corp_Mortgagealt> mortgagealts =
//        				JSON.parseObject(mortgagealtList, new TypeReference<ArrayList<ZS_Corp_Mortgagealt>>(){});
//        		for (ZS_Corp_Mortgagealt obj : mortgagealts) {
//        			obj.setORDER(order);
//				}
//        		order.setMORTGAGEALTS(mortgagealts);
//
//        		//15-动产抵押-基本信息
//        		String mortgagebasicList = ent_info_json.getString("MORTGAGEBASIC");
//        		ArrayList<ZS_Corp_Mortgagebasic> mortgagebasics =
//        				JSON.parseObject(mortgagebasicList, new TypeReference<ArrayList<ZS_Corp_Mortgagebasic>>(){});
//        		for (ZS_Corp_Mortgagebasic obj : mortgagebasics) {
//        			obj.setORDER(order);
//				}
//        		order.setMORTGAGEBASICS(mortgagebasics);
//
//        		//16-动产抵押-注销信息
//        		String mortgagecanList = ent_info_json.getString("MORTGAGECAN");
//        		ArrayList<ZS_Corp_Mortgagecan> mortgagecans =
//        				JSON.parseObject(mortgagecanList, new TypeReference<ArrayList<ZS_Corp_Mortgagecan>>(){});
//        		for (ZS_Corp_Mortgagecan obj : mortgagecans) {
//        			obj.setORDER(order);
//				}
//        		order.setMORTGAGECANS(mortgagecans);
//        	
//        		//17-动产抵押-被担保主债权信息
//        		String mortgagedebtList = ent_info_json.getString("MORTGAGEDEBT");
//        		ArrayList<ZS_Corp_Mortgagedebt> mortgagedebts =
//        				JSON.parseObject(mortgagedebtList, new TypeReference<ArrayList<ZS_Corp_Mortgagedebt>>(){});
//        		for (ZS_Corp_Mortgagedebt obj : mortgagedebts) {
//        			obj.setORDER(order);
//				}
//        		order.setMORTGAGEDEBTS(mortgagedebts);
//
//        		//18-动产抵押-抵押物信息
//        		String mortgagepawnList = ent_info_json.getString("MORTGAGEPAWN");
//        		ArrayList<ZS_Corp_Mortgagepawn> mortgagepawns =
//        				JSON.parseObject(mortgagepawnList, new TypeReference<ArrayList<ZS_Corp_Mortgagepawn>>(){});
//        		for (ZS_Corp_Mortgagepawn obj : mortgagepawns) {
//        			obj.setORDER(order);
//				}
//        		order.setMORTGAGEPAWNS(mortgagepawns);
//
//        		//19-动产抵押-抵押人信息
//        		String mortgageperList = ent_info_json.getString("MORTGAGEPER");
//        		ArrayList<ZS_Corp_Mortgageper> mortgagepers =
//        				JSON.parseObject(mortgageperList, new TypeReference<ArrayList<ZS_Corp_Mortgageper>>(){});
//        		for (ZS_Corp_Mortgageper obj : mortgagepers) {
//        			obj.setORDER(order);
//				}
//        		order.setMORTGAGEPERS(mortgagepers);
//
//        		//20-动产抵押-登记信息 
//        		String mortgageregList = ent_info_json.getString("MORTGAGEREG");
//        		ArrayList<ZS_Corp_Mortgagereg> mortgageregs =
//        				JSON.parseObject(mortgageregList, new TypeReference<ArrayList<ZS_Corp_Mortgagereg>>(){});
//        		for (ZS_Corp_Mortgagereg obj : mortgageregs) {
//        			obj.setORDER(order);
//				}
//        		order.setMORTGAGEREGS(mortgageregs);
//
//        		//21-股权出质信息（新） 
//        		String stockpawnList = ent_info_json.getString("STOCKPAWN");
//        		ArrayList<ZS_Corp_Stockpawn> stockpawns =
//        				JSON.parseObject(stockpawnList, new TypeReference<ArrayList<ZS_Corp_Stockpawn>>(){});
//        		for (ZS_Corp_Stockpawn obj : stockpawns) {
//        			obj.setORDER(order);
//				}
//        		order.setSTOCKPAWNS(stockpawns);
////
//        		//22-股权出质信息（新）-变更信息
//        		String stockpawnaltList = ent_info_json.getString("STOCKPAWNALT");
//        		ArrayList<ZS_Corp_Stockpawnalt> stockpawnalts =
//        				JSON.parseObject(stockpawnaltList, new TypeReference<ArrayList<ZS_Corp_Stockpawnalt>>(){});
//        		for (ZS_Corp_Stockpawnalt obj : stockpawnalts) {
//        			obj.setORDER(order);
//				}
//        		order.setSTOCKPAWNALTS(stockpawnalts);
//
//        		//23-股权出质信息（新）-注销信息
//        		String stockpawnrevList = ent_info_json.getString("STOCKPAWNREV");
//        		ArrayList<ZS_Corp_Stockpawnrev> stockpawnrevs =
//        				JSON.parseObject(stockpawnrevList, new TypeReference<ArrayList<ZS_Corp_Stockpawnrev>>(){});
//        		for (ZS_Corp_Stockpawnrev obj : stockpawnrevs) {
//        			obj.setORDER(order);
//				}
//        		order.setSTOCKPAWNREVS(stockpawnrevs);
//
//        		//24-行政处罚基本信息
//        		String entcasebaseinfoList = ent_info_json.getString("ENTCASEBASEINFO");
//        		ArrayList<ZS_Corp_Caseinfo> entcasebaseinfos =
//        				JSON.parseObject(entcasebaseinfoList, new TypeReference<ArrayList<ZS_Corp_Caseinfo>>(){});
//        		for (ZS_Corp_Caseinfo obj : entcasebaseinfos) {
//        			obj.setORDER(order);
//				}
//        		order.setCASEINFO(entcasebaseinfos);
//        	}
        }

		tagMap.put("tag", tag);
		tagMap.put("ds_tag", ds_tag);
		
		logger.info("{} 开始保存数据=====", order.getTRADE_ID());
		try {
			newOrderService.add(order);
		} catch (Exception e) {
			logger.info("{} 报存数据失败=====", order.getTRADE_ID());
			logger.error("保存数据失败：", e);
		}
		
		logger.info("{} 保存数据结束=====", order.getTRADE_ID());
		return tagMap;
	}
}
