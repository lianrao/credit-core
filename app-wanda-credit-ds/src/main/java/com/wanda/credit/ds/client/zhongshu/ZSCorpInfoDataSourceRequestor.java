package com.wanda.credit.ds.client.zhongshu;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.longcredit.common.Encryption.Authcode;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Alter;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Basic;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Basic_New;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Caseinfo;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Dealin;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Entinv;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Filiation;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Finalshareholder;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Frinv;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Frposition;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Liquidation;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Mordetail;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Morguainfo;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Person;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Person_New;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Punishbreak;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Punished;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Shareholder;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Shareholder_New;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Sharesfrost;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Corp_Sharesimpawn;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Order;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Org_Basic;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Org_Detail;
import com.wanda.credit.ds.dao.iface.IZSOrderService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RandomUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
@DataSourceClass(bindingDataSourceId="ds_zsCorpQuery")
public class ZSCorpInfoDataSourceRequestor extends BaseZSDataSourceRequestor 
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZSCorpInfoDataSourceRequestor.class);
	private final String KEYTYPE_CORPNAME = "2";
	private final String KEYTYPE_CORPINST = "3";
	private final String KEYTYPE_CORPCODE = "5";
	private  String userId; 
	private  String userPwd; 
	private  String orders;
	private String address;
	
	@Autowired
	public IZSOrderService orderService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		int months = Integer.parseInt(propertyEngine.readById("ds_zs_incache_month"));
		Map<String, Object> rets =null;
		//请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id("ds_zsCorpQuery");//log:供应商id	
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setReq_url(address);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);	//初始值-失败
		logObj.setIncache("0");//不缓存
		Map<String,Object> paramIn = new HashMap<String,Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		String resource_ds_tag = Conts.TAG_SYS_ERROR;
		String enRegiCode = "";
		try {
			logger.info("{} 开始请求远程中数企业信息服务...",prefix);
			rets = new HashMap<String, Object>();
			String regiCode = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); //企业注册号
			String keyType = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //企业查询类型
			String acct_id = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //用户id
			//记录入参到入参记录表			
			paramIn.put("regicode", regiCode);
			paramIn.put("keyType", keyType);
			paramIn.put("acct_id", acct_id);
			if(keyType.equals(KEYTYPE_CORPNAME) || keyType.equals(KEYTYPE_CORPINST) || keyType.equals(KEYTYPE_CORPCODE)){					
				
				String xmlkey = RandomUtil.random(50);
				String xmlValue = toXmlStr(userId,regiCode,keyType,orders,userPwd);
				// des加密
				String encryptXmlDataValue = new Authcode().AuthcodeEncode(URLEncoder.encode(xmlValue, "UTF-8"), xmlkey);
				// RSA加密
				String encryptXmlDataKey = PublicEncrypt(xmlkey); 
				String reqXML = toXmlStr(encryptXmlDataKey,encryptXmlDataValue);
				// 请求远程ws
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, xmlValue, new String[] { trade_id }));
				
				JaxWsProxyFactoryBean j = new JaxWsProxyFactoryBean();
				j.setAddress(address);
				j.setServiceClass(EntInfoQueryService.class);
				EntInfoQueryService entInfoQueryService = (EntInfoQueryService) j.create();
				
				// 超时设置  
				Client proxy = ClientProxy.getClient(entInfoQueryService);   
				HTTPConduit conduit = (HTTPConduit) proxy.getConduit(); 
				HTTPClientPolicy httpClientPolicy =  new  HTTPClientPolicy();  
				httpClientPolicy.setConnectionTimeout(timeout); 
				httpClientPolicy.setAllowChunking( false );   
				httpClientPolicy.setReceiveTimeout(timeout);
				conduit.setClient(httpClientPolicy); 
				String encryptResult = entInfoQueryService.queryEntInfoForXml(reqXML);
				logger.info("{} 远程中数企业信息查询成功!",prefix);
				//log:返回时间
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				
				String key = getXmlNodeText(encryptResult, "key");
				String value = getXmlNodeText(encryptResult, "value");
				// RSA解密
				String desDecryptKey = PublicDecrypt(key);
				// des解密
				String decryptXmlStr = new Authcode().AuthcodeDecode(value,desDecryptKey);
				decryptXmlStr = URLDecoder.decode(decryptXmlStr, "UTF-8"); //FileUtils.readFileToString(new File("d:\\zs.xml"),"GBK");// 
				logger.error("{} 中数数据源返回数据:{}",prefix,formatXML(decryptXmlStr));
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, decryptXmlStr, new String[] { trade_id }));
				Document doc = DocumentHelper.parseText(decryptXmlStr);
				Node errCodeNode = doc.selectSingleNode("//DATA/ERRORCODE");
				Node errMsgNode = doc.selectSingleNode("//DATA/ERRORMSG");
				if(errCodeNode!=null && StringUtils.isNotEmpty(errCodeNode.getText())){
					
					//log:交易状态信息
					logObj.setState_msg(errMsgNode.getStringValue());
					//根据dsid和retCode到返回码对照表获取bizCode-BizName
					DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_zsCorpQuery", errCodeNode.getText());
					//log 返回码
					logObj.setBiz_code1(dataSourceBizCodeVO == null?errCodeNode.getText():dataSourceBizCodeVO.getBizCode());
					
					rets.clear();
					if(keyType.equals(KEYTYPE_CORPNAME)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_NAME_EXCEPTION);
					}else if(keyType.equals(KEYTYPE_CORPCODE)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_CODE_EXCEPTION);
					}else{
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_NOTFOUND_EXCEPTION);
					}					
					rets.put(Conts.KEY_RET_MSG, "中数数据源请求失败,返回原因:"+ errMsgNode.getStringValue());
					logger.error("{} 中数数据源请求失败,错误代码:{},错误原因:{}", new String[]{prefix,errCodeNode.getStringValue(),errMsgNode.getStringValue()});
				}else{
					//log:交易状态
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);					
					enRegiCode = regiCode;	
					logger.info("{} 中数数据库查询开始...",prefix);
					Map<String,Object> getResultMap = orderService.inCached(enRegiCode,acct_id,months);
					if("1".equals(getResultMap.get("STAT"))){
						resource_tag = Conts.TAG_FOUND_OLDRECORDS;
					}else{
						resource_tag = Conts.TAG_FOUND_NEWRECORDS;
					}
					Map<String,Object> getResultDsMap = orderService.inCachedDs(enRegiCode,months);
					if("1".equals(getResultDsMap.get("STAT"))){
						resource_ds_tag = Conts.TAG_FOUND_OLDRECORDS;
					}else{
						resource_ds_tag = Conts.TAG_FOUND_NEWRECORDS;
					}
					logger.info("{} 中数数据库查询结束!",prefix);
					List<ZS_Corp_Basic> basics = null;
					List<ZS_Corp_Shareholder> shareholders = null;
					List<ZS_Corp_Shareholder_New> shareholdersNew = null;
					List<ZS_Corp_Person> persons = null;
					List<ZS_Corp_Person_New> personsNew = null;
					List<ZS_Corp_Frposition> frpositions = null;
					List<ZS_Corp_Entinv> entinvs = null;
					List<ZS_Corp_Frinv> frinvs = null;
					List<ZS_Corp_Sharesimpawn> sharesimpawns = null;
					List<ZS_Corp_Punishbreak> punishbreaks = null;
					List<ZS_Corp_Punished> punisheds = null;
					List<ZS_Corp_Sharesfrost> sharesfrosts = null;
					List<ZS_Corp_Alter> alters = null;
					List<ZS_Corp_Filiation> filiations = null;
					List<ZS_Corp_Mordetail> mordetails = null;
					List<ZS_Corp_Morguainfo> morguainfos = null;
					List<ZS_Corp_Dealin> dealins = null;
					List<ZS_Corp_Liquidation>  liquidations = null;
					List<ZS_Corp_Caseinfo> caseinfos = null;
					List<ZS_Corp_Finalshareholder> finalshareholders = null;
					//非企业组织信息
					List<ZS_Org_Detail>  orgdetails = null;
					List<ZS_Org_Basic>  orgbasics = null;
					ObjectMapper om = new ObjectMapper();
					//om.configure(SerializationConfig.Feature.INDENT_OUTPUT,true);  格式化输出
					List<Element> list = getXmlNodeList(decryptXmlStr,"ORDERLIST");
					ZS_Order order = convert2Object(list.get(0),ZS_Order.class);
					TreeMap<String, Object> retdata = new TreeMap<String, Object>();
					if(order !=null){
						order.setTRADE_ID(trade_id);
						order.setAcct_id(acct_id);
						//log 返回的业务状态
						//根据dsid和retCode到返回码对照表获取bizCode-BizName
						DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_zsCorpQuery", order.getSTATUS());
						//log 返回码
						logObj.setBiz_code1(dataSourceBizCodeVO == null?order.getSTATUS():dataSourceBizCodeVO.getBizCode());
						
						if ("1".equals(order.getSTATUS())) {
							//解析返回报文			
//							Node orgCode = doc.selectSingleNode("//DATA/ORGDETAIL");
							Node orgCode = doc.selectSingleNode("//DATA/ORDERLIST");
							if(orgCode != null){
								list = getXmlNodeList(decryptXmlStr,"BASIC");
								//企业照面信息
								ZS_Corp_Basic basic = convert2Object(list.get(0),ZS_Corp_Basic.class);
								basic.setORDER(order);
								basics = new ArrayList<ZS_Corp_Basic>();
								basics.add(basic);
								retdata.put("basic",  basic!=null ? om.writeValueAsString(basic) : null);
								ZS_Corp_Basic_New basicNew = convert2Object(list.get(0),ZS_Corp_Basic_New.class);
								retdata.put("basicNew",  basicNew!=null ? om.writeValueAsString(basicNew) : null);
								//企业股东及出资信息
								list = getXmlNodeList(decryptXmlStr,"SHAREHOLDER");
								if(list!=null){
									shareholders = new ArrayList<ZS_Corp_Shareholder>();
									shareholdersNew = new ArrayList<ZS_Corp_Shareholder_New>();
									ZS_Corp_Shareholder sh = null;
									ZS_Corp_Shareholder_New shNew = null;
									for(Element e : list){
										sh = convert2Object(e,ZS_Corp_Shareholder.class);
										sh.setORDER(order);
										shareholders.add(sh);
										shNew = convert2Object(e,ZS_Corp_Shareholder_New.class);
										shareholdersNew.add(shNew);
									}
									retdata.put("shareholders",  shareholders!=null ? om.writeValueAsString(shareholders) : null);
									retdata.put("shareholdersNew",  shareholdersNew!=null ? om.writeValueAsString(shareholdersNew) : null);
								}
								
								//企业主要管理人员信息?
								list = getXmlNodeList(decryptXmlStr,"PERSON");
								if(list!=null){
									persons = new ArrayList<ZS_Corp_Person>();
									personsNew = new ArrayList<ZS_Corp_Person_New>();
									ZS_Corp_Person ps = null;
									ZS_Corp_Person_New psNew = null;
									for(Element e : list){
										ps = convert2Object(e,ZS_Corp_Person.class);
										ps.setORDER(order);
										persons.add(ps);
										psNew = convert2Object(e,ZS_Corp_Person_New.class);
										personsNew.add(psNew);
									}
									retdata.put("persons",  persons!=null ? om.writeValueAsString(persons) : null);
									retdata.put("personsNew",  personsNew!=null ? om.writeValueAsString(personsNew) : null);
								}
								
								//企业法定代表人对外投资信息
								list = getXmlNodeList(decryptXmlStr,"FRINV");
								if(list!=null){
									frinvs = new ArrayList<ZS_Corp_Frinv>();
									ZS_Corp_Frinv fi = null;
									for(Element e : list){
										fi = convert2Object(e,ZS_Corp_Frinv.class);
										fi.setORDER(order);
										frinvs.add(fi);
									}
									retdata.put("frinvs",  frinvs!=null ? om.writeValueAsString(frinvs) : null);
								}
								
								//企业法定代表人其他公司任职信息
								list = getXmlNodeList(decryptXmlStr,"FRPOSITION");
								if(list!=null){
									frpositions = new ArrayList<ZS_Corp_Frposition>();
									ZS_Corp_Frposition fst = null;
									for(Element e : list){
										fst = convert2Object(e,ZS_Corp_Frposition.class);
										fst.setORDER(order);
										frpositions.add(fst);
									}
									retdata.put("frpositions",  frpositions!=null ? om.writeValueAsString(frpositions) : null);
								}
								//企业对外投资信息?
								list = getXmlNodeList(decryptXmlStr,"ENTINV");
								if(list!=null){
									entinvs = new ArrayList<ZS_Corp_Entinv>();
									ZS_Corp_Entinv ev = null;
									for(Element e : list){
										ev = convert2Object(e,ZS_Corp_Entinv.class);
										ev.setORDER(order);
										entinvs.add(ev);
									}
									retdata.put("entinvs",  entinvs!=null ? om.writeValueAsString(entinvs) : null);
								}
								
								//股权出质历史信息
								list = getXmlNodeList(decryptXmlStr,"SHARESIMPAWN");
								if(list!=null){
									sharesimpawns = new ArrayList<ZS_Corp_Sharesimpawn>();
									ZS_Corp_Sharesimpawn sip = null;
									for(Element e : list){
										sip = convert2Object(e,ZS_Corp_Sharesimpawn.class);
										sip.setORDER(order);
										sharesimpawns.add(sip);
									}
									retdata.put("sharesimpawns",  sharesimpawns!=null ? om.writeValueAsString(sharesimpawns) : null);
								}
								//失信被执行人信息
								list = getXmlNodeList(decryptXmlStr,"PUNISHBREAK");
								if(list!=null){
									punishbreaks = new ArrayList<ZS_Corp_Punishbreak>();
									ZS_Corp_Punishbreak pb = null;
									for(Element e : list){
										pb = convert2Object(e,ZS_Corp_Punishbreak.class);
										pb.setORDER(order);
										punishbreaks.add(pb);
									}
									retdata.put("punishbreaks",  punishbreaks!=null ? om.writeValueAsString(punishbreaks) : null);
								}
								
								
								//被执行人信息
								list = getXmlNodeList(decryptXmlStr,"PUNISHED");
								if(list!=null){
									punisheds = new ArrayList<ZS_Corp_Punished>();
									ZS_Corp_Punished pe = null;
									for(Element e : list){
										pe = convert2Object(e,ZS_Corp_Punished.class);
										pe.setORDER(order);
										punisheds.add(pe);
									}
									retdata.put("punisheds",  punisheds!=null ? om.writeValueAsString(punisheds) : null);
								}
								//股权冻结历史信息
								list = getXmlNodeList(decryptXmlStr,"SHARESFROST");
								if(list!=null){
									sharesfrosts = new ArrayList<ZS_Corp_Sharesfrost>();
									ZS_Corp_Sharesfrost sf = null;
									for(Element e : list){
										sf = convert2Object(e,ZS_Corp_Sharesfrost.class);
										sf.setORDER(order);
										sharesfrosts.add(sf);
									}
									retdata.put("sharesfrosts",  sharesfrosts!=null ? om.writeValueAsString(sharesfrosts) : null);
								}
								//企业历史变更信息
								list = getXmlNodeList(decryptXmlStr,"ALTER");
								if(list!=null){
									alters = new ArrayList<ZS_Corp_Alter>();
									ZS_Corp_Alter alter = null;
									for(Element e : list){
										alter = convert2Object(e,ZS_Corp_Alter.class);
										alter.setORDER(order);
										alters.add(alter);
									}
									retdata.put("alters",  alters!=null ? om.writeValueAsString(alters) : null);
								}
								//企业分支机构信息 
								list = getXmlNodeList(decryptXmlStr,"FILIATION");
								if(list!=null){
									filiations = new ArrayList<ZS_Corp_Filiation>();
									ZS_Corp_Filiation filiation = null;
									for(Element e : list){
										filiation = convert2Object(e,ZS_Corp_Filiation.class);
										filiation.setORDER(order);
										filiations.add(filiation);
									}
									retdata.put("filiations",  filiations!=null ? om.writeValueAsString(filiations) : null);
								}
								//动产抵押信息 
								list = getXmlNodeList(decryptXmlStr,"MORDETAIL");
								if(list!=null){
									mordetails = new ArrayList<ZS_Corp_Mordetail>();
									ZS_Corp_Mordetail mordetail = null;
									for(Element e : list){
										mordetail = convert2Object(e,ZS_Corp_Mordetail.class);
										mordetail.setORDER(order);
										mordetails.add(mordetail);
									}
									retdata.put("mordetails",  mordetails!=null ? om.writeValueAsString(mordetails) : null);
								}
								//动产抵押物信息 
								list = getXmlNodeList(decryptXmlStr,"MORGUAINFO");
								if(list!=null){
									morguainfos = new ArrayList<ZS_Corp_Morguainfo>();
									ZS_Corp_Morguainfo morguainfo = null;
									for(Element e : list){
										morguainfo = convert2Object(e,ZS_Corp_Morguainfo.class);
										morguainfo.setORDER(order);
										morguainfos.add(morguainfo);
									}
									retdata.put("morguainfos",  morguainfos!=null ? om.writeValueAsString(morguainfos) : null);
								}
								//企业年检信息 
								list = getXmlNodeList(decryptXmlStr,"DEALIN");
								if(list!=null){
									dealins = new ArrayList<ZS_Corp_Dealin>();
									ZS_Corp_Dealin dealin = null;
									for(Element e : list){
										dealin = convert2Object(e,ZS_Corp_Dealin.class);
										dealin.setORDER(order);
										dealins.add(dealin);
									}
									retdata.put("dealins",  dealins!=null ? om.writeValueAsString(dealins) : null);
								}
								//清算信息 
								list = getXmlNodeList(decryptXmlStr,"LIQUIDATION");
								if(list!=null){
									liquidations = new ArrayList<ZS_Corp_Liquidation>();
									ZS_Corp_Liquidation liquidation = null;
									for(Element e : list){
										liquidation = convert2Object(e,ZS_Corp_Liquidation.class);
										liquidation.setORDER(order);
										liquidations.add(liquidation);
									}
									retdata.put("liquidations",  liquidations!=null ? om.writeValueAsString(liquidations) : null);
								}
								//行政处罚历史信息 
								list = getXmlNodeList(decryptXmlStr,"CASEINFO");
								if(list!=null){
									caseinfos = new ArrayList<ZS_Corp_Caseinfo>();
									ZS_Corp_Caseinfo caseinfo = null;
									for(Element e : list){
										caseinfo = convert2Object(e,ZS_Corp_Caseinfo.class);
										caseinfo.setORDER(order);
										caseinfos.add(caseinfo);
									}
									retdata.put("caseinfos",  caseinfos!=null ? om.writeValueAsString(caseinfos) : null);
								}
								//最终控股股东信息  List<ZS_Corp_Finalshareholder> finalshareholders
								list = getXmlNodeList(decryptXmlStr,"FINALSHAREHOLDER");
								if(list!=null){
									finalshareholders = new ArrayList<ZS_Corp_Finalshareholder>();
									ZS_Corp_Finalshareholder finalshareholder = null;
									for(Element e : list){
										finalshareholder = convert2Object(e,ZS_Corp_Finalshareholder.class);
										finalshareholder.setORDER(order);
										finalshareholders.add(finalshareholder);
									}
									retdata.put("finalshareholders",  finalshareholders!=null ? om.writeValueAsString(finalshareholders) : null);
								}
								
								order.setBASICS(basics);
								order.setSHAREHOLDERS(shareholders);
								order.setPERSONS(persons);
								order.setFRPOSITIONS(frpositions);
								order.setZSENTINVS(entinvs);
								order.setFRINVS(frinvs);
								order.setSHARESIMPAWNS(sharesimpawns);
								order.setPUNISHBREAKS(punishbreaks);
								order.setPUNISHEDS(punisheds);
								order.setSHARESFROSTS(sharesfrosts);
								order.setALTER(alters);
								order.setFILIATION(filiations);
								order.setMORDETAIL(mordetails);
								order.setMORGUAINFO(morguainfos);
								order.setDEALIN(dealins);
								order.setLIQUIDATION(liquidations);
								order.setCASEINFO(caseinfos);
								order.setFINALSHAREHOLDER(finalshareholders);
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
								rets.put(Conts.KEY_RET_DATA, retdata);
								rets.put(Conts.KEY_RET_MSG, "中数数据查询成功!");
								logger.info("{} 中数数据查询成功!",prefix);
							}else{
								//组织机构(非企业)数据插入
								list = getXmlNodeList(decryptXmlStr,"ORGDETAIL");
								if(list!=null){
									orgdetails = new ArrayList<ZS_Org_Detail>();
									ZS_Org_Detail orgdetail = null;
									for(Element e : list){
										orgdetail = convert2Object(e,ZS_Org_Detail.class);
										orgdetail.setORDER(order);
										orgdetails.add(orgdetail);
									}
									retdata.put("orgdetails",  orgdetails!=null ? om.writeValueAsString(orgdetails) : null);
								}
								order.setORGDETAIL(orgdetails);
								Node orgBasicFalg = doc.selectSingleNode("//*/ORGBASIC");
								if(orgBasicFalg != null){
									list = getXmlNodeList(decryptXmlStr,"ORGBASIC");
									if(list!=null){
										orgbasics = new ArrayList<ZS_Org_Basic>();
										ZS_Org_Basic orgbasic = null;
										for(Element e : list){
											orgbasic = convert2Object(e,ZS_Org_Basic.class);
											orgbasic.setORDER(order);
											orgbasics.add(orgbasic);
										}
										retdata.put("orgbasics",  orgbasics!=null ? om.writeValueAsString(orgbasics) : null);
									}
									order.setORGBASIC(orgbasics);
								}
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
								rets.put(Conts.KEY_RET_DATA, retdata);
								rets.put(Conts.KEY_RET_MSG, "中数非企业数据查询成功!");
								logger.info("{} 中数非企业数据查询成功!",prefix);
							}
							
						}else{
							rets.clear();
							if(keyType.equals(KEYTYPE_CORPNAME)){
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_NAME_EXCEPTION);
								rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_NAME_EXCEPTION.ret_msg);
							}else if(keyType.equals(KEYTYPE_CORPCODE)){
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_CODE_EXCEPTION);
								rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_CODE_EXCEPTION.ret_msg);
							}else{
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_NOTFOUND_EXCEPTION);
								rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_NOTFOUND_EXCEPTION.ret_msg);
							}	
							logger.warn("{} 没有查询到任何工商证照信息",prefix);
							resource_tag = Conts.TAG_UNFOUND;
							resource_ds_tag = Conts.TAG_UNFOUND;
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							return rets;
						}
						try{
							orderService.add(order);
						}catch(Exception e){
							logger.error(prefix+" 数据源处理时异常：{}",e);
						}						
					}else{
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "中数数据查询异常!");
						logger.error("{} 中数数据查询异常!",prefix);
					}
				}
			}else{
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "中数查询传入类型错误!");
				logger.error("{} 中数查询传入类型错误,传入类型为：{}",prefix,keyType);
			}
			
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			resource_ds_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
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
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return rets;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
	public String getOrders() {
		return orders;
	}
	public void setOrders(String orders) {
		this.orders = orders;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
