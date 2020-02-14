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
import com.wanda.credit.api.dto.DataSource;
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
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Person_CaseInfo;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Person_Order;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Person_Punishbreak;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Person_Punished;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Person_Ryposfr;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Person_Ryposper;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Person_Rypossha;
import com.wanda.credit.ds.dao.iface.IZSOrderService;
import com.wanda.credit.ds.dao.iface.IZSPersonOrderService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_zsPersonNewQuery")
public class ZSPersonNewSourceRequestor  extends BaseZSDataSourceRequestor 
			implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZSPersonNewSourceRequestor.class);
	private  String userId; 
	private  String userPwd; 
	private  String orders;
	private  String keyType;
	private String address;
	
	@Autowired
	public IZSOrderService orderService;
	@Autowired
	public IZSPersonOrderService orderPersonService;
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
		logObj.setDs_id("ds_zsPersonNewQuery");//log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setReq_url(address);//log  请求地址
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);	//初始值-失败
		logObj.setIncache("0");//不缓存
		Map<String,Object> paramIn = new HashMap<String,Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		String resource_ds_tag = Conts.TAG_SYS_ERROR;
//		String enRegiCode = "";
		try {
			logger.info("{} 开始中数个人信息服务请求...",prefix);
			rets = new HashMap<String, Object>();
			String personId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); //身份证号	
			String acct_id = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //用户id
			//记录入参到入参表			
			paramIn.put("personId", personId);	
			paramIn.put("acct_id", acct_id);
			String xmlkey = RandomUtil.random(50);
			String xmlValue = toXmlStr(userId,personId,keyType,orders,userPwd);
			// des加密
			String encryptXmlDataValue = new Authcode().AuthcodeEncode(URLEncoder.encode(xmlValue, "UTF-8"), xmlkey);
			// RSA加密
			String encryptXmlDataKey = PublicEncrypt(xmlkey); 
			String reqXML = toXmlStr(encryptXmlDataKey,encryptXmlDataValue);
			// 请求远程ws
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, xmlValue, new String[] { trade_id }));
			logger.info("{} 开始请求远程中数个人信息服务...",prefix);
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
			logger.info("{} 请求远程中数个人信息服务结束!",prefix);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));			
			String key = getXmlNodeText(encryptResult, "key");
			String value = getXmlNodeText(encryptResult, "value");
			// RSA解密
			String desDecryptKey = PublicDecrypt(key);
			// des解密
			String decryptXmlStr = new Authcode().AuthcodeDecode(value,desDecryptKey);
			decryptXmlStr = URLDecoder.decode(decryptXmlStr, "UTF-8"); // FileUtils.readFileToString(new File("d:\\zs.xml"),"GBK");//
			logger.info("{} 中数数据源成功,返回数据:\n{}",prefix,formatXML(decryptXmlStr));
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, decryptXmlStr, new String[] { trade_id }));
			Document doc = DocumentHelper.parseText(decryptXmlStr);
			Node errCodeNode = doc.selectSingleNode("//DATA/ERRORCODE");
			Node errMsgNode = doc.selectSingleNode("//DATA/ERRORMSG");
			if(errCodeNode!=null && StringUtils.isNotEmpty(errCodeNode.getText())){
				//log:交易状态码
				logObj.setState_msg(errMsgNode.getStringValue());
				//根据dsid和retCode到返回码对照表获取bizCode-BizName
				DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_zsCorpQuery", errCodeNode.getText());
				//log 返回码
				logObj.setBiz_code1(dataSourceBizCodeVO == null?errCodeNode.getText():dataSourceBizCodeVO.getBizCode());
				
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_C_NOTFOUND_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "中数数据源请求失败,返回原因:"+ errMsgNode.getStringValue());
				logger.error("{} 中数数据源请求失败,错误代码:{},错误原因:{}", new String[]{prefix,errCodeNode.getStringValue(),errMsgNode.getStringValue()});
			}else{
				//log:交易状态
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logger.info("{} 中数数据库查询开始...",prefix);
				String oracle_code = personId.replaceAll("<n>", "＜n＞");
				Map<String,Object> getResultMap = orderService.inCachedProd(oracle_code,personId,acct_id);
				if("1".equals(getResultMap.get("STAT"))){
					resource_tag = Conts.TAG_FOUND_OLDRECORDS;
				}else{
					resource_tag = Conts.TAG_FOUND_NEWRECORDS;
				}
				Map<String,Object> getResultDsMap = orderService.inCachedDsNew(oracle_code,personId);
				if("1".equals(getResultDsMap.get("STAT"))){
					resource_ds_tag = Conts.TAG_FOUND_OLDRECORDS;
				}else{
					resource_ds_tag = Conts.TAG_FOUND_NEWRECORDS;
				}
				logger.info("{} 中数数据库查询结束!",prefix);
				List<ZS_Person_Ryposfr> ryposfrs = null;
				List<ZS_Person_Rypossha> ryposshas = null;
				List<ZS_Person_Ryposper> rypospers = null;
				List<ZS_Person_CaseInfo> caseinfos = null;
				List<ZS_Person_Punishbreak> punishbreaks = null;
				List<ZS_Person_Punished> punisheds = null;
				ObjectMapper om = new ObjectMapper();
				List<Element> list = getXmlNodeList(decryptXmlStr,"ORDERLIST");
				ZS_Person_Order order = convert2Object(list.get(0),ZS_Person_Order.class);
				TreeMap<String, Object> retdata = new TreeMap<String, Object>();
				if(order !=null){
					order.setTRADE_ID(trade_id);
					order.setAcct_id(acct_id);
					//根据dsid和retCode到返回码对照表获取bizCode-BizName
					DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_zsCorpQuery", order.getSTATUS());
					logObj.setBiz_code1(dataSourceBizCodeVO == null?order.getSTATUS():dataSourceBizCodeVO.getBizCode());
					
					if ("1".equals(order.getSTATUS())) {
						//企业法定代表人信息
						list = getXmlNodeList(decryptXmlStr,"RYPOSFR");
						if(list!=null){
							ryposfrs = new ArrayList<ZS_Person_Ryposfr>();
							ZS_Person_Ryposfr fr = null;
							for(Element e : list){
								fr = convert2Object(e,ZS_Person_Ryposfr.class);
								fr.setORDER(order);
								ryposfrs.add(fr);
							}
							retdata.put("ryposfrs",  ryposfrs!=null ? om.writeValueAsString(ryposfrs) : null);
						}
						//企业股东信息
						list = getXmlNodeList(decryptXmlStr,"RYPOSSHA");
						if(list!=null){
							ryposshas = new ArrayList<ZS_Person_Rypossha>();
							ZS_Person_Rypossha sha = null;
							for(Element e : list){
								sha = convert2Object(e,ZS_Person_Rypossha.class);
								sha.setORDER(order);
								ryposshas.add(sha);
							}
							retdata.put("ryposshas",  ryposshas!=null ? om.writeValueAsString(ryposshas) : null);
						}
						//企业主要管理人员信息
						list = getXmlNodeList(decryptXmlStr,"RYPOSPER");
						if(list!=null){
							rypospers = new ArrayList<ZS_Person_Ryposper>();
							ZS_Person_Ryposper per =null;
							for(Element e : list){
								per = convert2Object(e,ZS_Person_Ryposper.class);
								per.setORDER(order);
								rypospers.add(per);
							}
							retdata.put("rypospers",  rypospers!=null ? om.writeValueAsString(rypospers) : null);
						}
						//行政处罚历史信息
						list = getXmlNodeList(decryptXmlStr,"PERSONCASEINFO");
						if(list!=null){
							caseinfos = new ArrayList<ZS_Person_CaseInfo>();
							ZS_Person_CaseInfo brk = null;
							for(Element e : list){
								brk = convert2Object(e,ZS_Person_CaseInfo.class);
								brk.setORDER(order);
								caseinfos.add(brk);
							}
							retdata.put("caseinfos",  caseinfos!=null ? om.writeValueAsString(caseinfos) : null);
						}
						//个人失信被执行信息
						list = getXmlNodeList(decryptXmlStr,"PUNISHBREAK");
						if(list!=null){
							punishbreaks = new ArrayList<ZS_Person_Punishbreak>();
							ZS_Person_Punishbreak punishbreak = null;
							for(Element e : list){
								punishbreak = convert2Object(e,ZS_Person_Punishbreak.class);
								punishbreak.setORDER(order);
								punishbreaks.add(punishbreak);
							}
							retdata.put("punishbreaks",  punishbreaks!=null ? om.writeValueAsString(punishbreaks) : null);
						}
						//个人被执行信息
						list = getXmlNodeList(decryptXmlStr,"PUNISHED");
						if(list!=null){
							punisheds = new ArrayList<ZS_Person_Punished>();
							ZS_Person_Punished punished = null;
							for(Element e : list){
								punished = convert2Object(e,ZS_Person_Punished.class);
								punished.setORDER(order);
								punisheds.add(punished);
							}
							retdata.put("punisheds",  punisheds!=null ? om.writeValueAsString(punisheds) : null);
						}
						order.setRYPOSFRS(ryposfrs);
						order.setRYPOSPERS(rypospers);
						order.setRYPOSSHAS(ryposshas); 
						order.setCASEINFOS(caseinfos);
						order.setPUNISHBREAK(punishbreaks);
						order.setPUNISHED(punisheds);
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_DATA, retdata);
						rets.put(Conts.KEY_RET_MSG, "中数数据查询成功!");
						logger.info("{} 中数数据查询成功!",prefix);
					}else{
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_C_NOTFOUND_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ZS_C_NOTFOUND_EXCEPTION.ret_msg);
						resource_tag = Conts.TAG_UNFOUND;
						resource_ds_tag = Conts.TAG_UNFOUND;
						logger.warn("{} 没有查询到任何工商证照信息",prefix);
					}
					orderPersonService.add(order);
				}else{
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "中数数据查询异常!");
					logger.error("{} 中数数据查询异常!",prefix);
				}
			}
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			resource_ds_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			
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
	public String getKeyType() {
		return keyType;
	}
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
