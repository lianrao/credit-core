package com.wanda.credit.ds.client.juxinli;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.EBusiWebsite;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.MobileEBusiDataSource;
import com.wanda.credit.ds.client.juxinli.bean.mobile.Contact;
import com.wanda.credit.ds.client.juxinli.bean.mobile.GetTokenDataRes;
import com.wanda.credit.ds.client.juxinli.bean.mobile.GetTokenReq;
import com.wanda.credit.ds.client.juxinli.bean.mobile.ParamUserContact;
import com.wanda.credit.ds.client.juxinli.bean.mobile.ParamUserOtherInfo;
import com.wanda.credit.ds.client.juxinli.bean.mobile.ParamWebsite;
import com.wanda.credit.ds.client.juxinli.bean.mobile.UserBasicInfo;
import com.wanda.credit.ds.client.juxinli.service.IJXLEBusiApplyService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyNextDataSourcePojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLNextDatasourceService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 连接聚信立提交申请表单获取回执信息
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlEbusiGetToken")
public class JXLMobileEBusiTokenRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {

	private final static Logger logger = LoggerFactory.getLogger(JXLMobileEBusiTokenRequestor.class);
	@Autowired
	private IJXLEBusiApplyService jxlEBusiApplyService;
	@Autowired
	private IJXLNextDatasourceService jxlNextDataSourceService;
//	@Autowired
//	public IPropertyEngine propertyEngine;
	protected List<EBusiWebsite> ebusiWebList = null;
	protected UserBasicInfo basicInfo = null;
	protected List<Contact> contactList = null;
	
	private String httpsPostUrl;
	private String orgAccount;
	private int timeOut;
	
	
	
	
	
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String token = "";
		String requestId = "";
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		boolean doPrint = JXLConst.LOG_ON.equals(propertyEngine.readById("sys_log_print_switch"));
		
		long startTime = System.currentTimeMillis();
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		logger.info("{} 聚信立运营商电商提交申请获取TOKEN开始,trade_id=" + trade_id ,prefix);
		//获取请求参数 contacts
		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
		String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
		String mobileNo = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
		String ignoreMobile = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();
		String otherInfo = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();
		String websiteInfos = ParamUtil.findValue(ds.getParams_in(), paramIds[5]).toString();
		String contacts = ParamUtil.findValue(ds.getParams_in(), paramIds[6]).toString();
		

		if (doPrint) {
			StringBuffer paramsBf = new StringBuffer();
			paramsBf.append(name).append(";").append(cardNo).append(";").append(mobileNo)
				.append(";").append(ignoreMobile).append(";").append(otherInfo).append(";")
				.append(websiteInfos).append(";").append(contacts);		
			logger.info("{} 聚信立运营商电商提交申请获取TOKEN请求参数为【" + paramsBf.toString() + "】" ,prefix);
			
		}
		
		GetTokenReq tokenReq = null;
		try {
			tokenReq = parseParams(ignoreMobile,otherInfo,websiteInfos,contacts,name,cardNo,mobileNo);
			
		} catch (Exception e) {
			rets.clear();
			String exceInfo = e.getMessage();
			logger.error("{} 聚信立运营商电商提交申请传入参数异常" + e.getMessage() ,prefix);
			if(JXLConst.PARAMS_WEBSITE_ERROR.equals(exceInfo)){
				rets.put(Conts.KEY_RET_MSG, "传入参数错误!");
			}else if(JXLConst.PARMS_CONTACTS_ERROR.equals(exceInfo)){
				rets.put(Conts.KEY_RET_MSG, "传入参数错误!");
			}else if(JXLConst.PARMS_WEBSITE_SIZE_ZERO.equals(exceInfo)){
				rets.put(Conts.KEY_RET_MSG, "传入参数错误!");
			}else if(JXLConst.PARAM_IGNOREMOBILE_ERROR.equals(exceInfo)){
				rets.put(Conts.KEY_RET_MSG, "传入参数错误!");
			}else{
				rets.put(Conts.KEY_RET_MSG, "传入参数错误");
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_PARAM_ERROR);
		
			saveTradeInfo(trade_id, JXLConst.TF_GET_TOKEN, CRSStatusEnum.STATUS_FAILED.getRet_sub_code(), name, cardNo, mobileNo, requestId);
			return rets;
			
		}	
		
		//拼接Post请求地址
		StringBuffer postUrlBf = new StringBuffer();		
		if(httpsPostUrl != null && httpsPostUrl.endsWith("/")){
			postUrlBf.append(httpsPostUrl).append(orgAccount);
		}else{
			postUrlBf.append(httpsPostUrl).append("/").append(orgAccount);
		}
		logger.info("{} 聚信立运营商电商申请获取TOKEN提交Url为:" + postUrlBf.toString() ,prefix);
		JsonObject tokenResJsonObj = null;
		try{			
			String jsonStrData = JSONObject.toJSONString(tokenReq);
			
			long postStartTime = System.currentTimeMillis();
			logger.info("{} 向聚信立提交运营商电商申请请求，开始时间为 "  + new Date() ,prefix);
			tokenResJsonObj = getMobileEBusiToken(postUrlBf.toString(), jsonStrData, timeOut * 1000,prefix);
			long PostTime = System.currentTimeMillis() - postStartTime;
			logger.info("{} https请求聚信立耗时为（ms）" + PostTime ,prefix);
			if(doPrint){
				logger.info("{} 向聚信立提交运营商电商申请返回结果为" + tokenResJsonObj ,prefix);				
			}
			
			if(tokenResJsonObj != null && tokenResJsonObj.get(JXLConst.FLAG_SUCCESS)!=null){

				if(JXLConst.SUCCESS_RESULT.equals(tokenResJsonObj.get(JXLConst.FLAG_SUCCESS).getAsString())){
					JsonElement dataEle = tokenResJsonObj.get("data");
					if(dataEle != null){
						Gson gson = new Gson();
						GetTokenDataRes tokenRes = gson.fromJson(dataEle, GetTokenDataRes.class);
						token = tokenRes.getToken();
						MobileEBusiDataSource nextDataSource = tokenRes.getDatasource();
						String website = nextDataSource.getWebsite();
						String category = nextDataSource.getCategory();
						String websiteName = nextDataSource.getName();
						String categoryName = nextDataSource.getCategory_name();
						requestId = StringUtil.getRequstId(40);
						
						Map<String,String>  jsonMap = new HashMap<String, String>();
						jsonMap.put(JXLConst.WEBSITE_EN_NAME, website);
						jsonMap.put(JXLConst.WEBSITE_CN_NAME, websiteName);
						jsonMap.put(JXLConst.CATEGORY_EN_NAME, category);
						jsonMap.put(JXLConst.CATEGORY_CN_NAME, categoryName);
						
						String jsonString = JSONObject.toJSONString(jsonMap);
						
						rets.clear();
						retdata.put(JXLConst.EBUSI_NEXT_DATASOURCE, jsonString);
						retdata.put(JXLConst.EBUSI_REQUEST_ID, requestId);
						
						rets.put(Conts.KEY_RET_DATA, retdata);
						rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "提交申请成功");
						
						jxlEBusiApplyService.saveApplyData(tokenReq,requestId,token);
						ApplyNextDataSourcePojo nextDSPOjo = dataSourceBean2Pojo(nextDataSource);
						if (nextDSPOjo != null) {
							nextDSPOjo.setRequestId(requestId);
							nextDSPOjo.setSuccess("false");
							jxlNextDataSourceService.add(nextDSPOjo);
						}
						
						logger.info("{} 聚信立运营商电商提交申请获取TOKEN结束,trade_id=" + trade_id ,prefix);

						
				}else{
					throw new Exception("JUXINLI_DATA_NULL");
				}
			}else{
				logger.info("{} 聚信立提交运营商电商申请返回结果为" + tokenResJsonObj.get(JXLConst.FLAG_SUCCESS).toString() ,prefix);
				throw new Exception("JUXINLI_SUCCESS_NOT_TRUE");
			}
			
			}else{
				logger.info("{} 聚信立提交运营商电商申请返回结果为NULL" ,prefix);
				throw new Exception("JUXINLI_RETURN_NULL");
			}
		}catch (Exception e) {
			rets.clear();
			String eMsg = e.getMessage();
			if("JUXINLI_SUCCESS_NOT_TRUE".equals(eMsg)){
				logger.error("{} 聚信立运营商电商提交申请获取TOKEN返回结果中success对应值不为true" ,prefix);
				JsonElement msgEle = tokenResJsonObj.get("message");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				if(msgEle != null){
					String msgString = msgEle.getAsString();
					if(msgString.contains("token")){
						msgString = msgString.replaceAll("token", "request_id");
					}
					rets.put(Conts.KEY_RET_MSG, msgString);
				}else{
					rets.put(Conts.KEY_RET_MSG, "提交申请失败");					
				}
				
			}else if("JUXINLI_DATA_NULL".equals(eMsg)){
				logger.error("{} 聚信立运营商电商提交申请获取TOKEN返回结果中data对应值为null" ,prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "请求超时请重试");
			}else if("JUXINLI_RETURN_NULL".equals(eMsg)){
				logger.error("{} 聚信立运营商电商提交申请获取TOKEN返回结果为null" ,prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "请求超时请重试");
			}else{
				logger.error("{} 聚信立运营商电商提交申请获取TOken失败" + e.getMessage() ,prefix);
				rets.put(Conts.KEY_RET_MSG, "提交申请失败");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			}
			
			try {
				jxlEBusiApplyService.saveApplyData(tokenReq,requestId,"");
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error("{} 聚信立提交申请表单获取token失败将数据保存到数据库异常" ,prefix);
			}
			
		}
		
		String retCode = "";
		if (rets.containsKey(Conts.KEY_RET_STATUS)) {
			CRSStatusEnum retstatus = CRSStatusEnum.valueOf(rets.get(Conts.KEY_RET_STATUS).toString());
			retCode = retstatus.getRet_sub_code();
		}
		saveTradeInfo(trade_id, JXLConst.TF_GET_TOKEN, retCode, name, cardNo,mobileNo, requestId);
		
		logger.info("{} 聚信立运营商电商提交申请获取TOKEN结束,trade_id=" + trade_id ,prefix);
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 提交采集请求总共耗时时间为（ms）" + tradeTime ,prefix);
		return rets;
		
		
		
	}
	
	
	
	



	private GetTokenReq parseParams(String ignoreMobile, String otherInfo,
			String websiteInfos, String contacts,String name,String cardNo,String mobileNo) throws Exception {
		if (!websiteInfos.startsWith("[") || !websiteInfos.endsWith("]")) {
			logger.error("{} 聚信立运营商电商提交申请参数websiteInfos格式错误:" + websiteInfos);
			throw new Exception(JXLConst.PARAMS_WEBSITE_ERROR);
		}
		if (!contacts.startsWith("[") || !contacts.endsWith("]")) {
			logger.error("{} 聚信立运营商电商提交申请参数contacts格式错误:" + contacts);
			throw new Exception(JXLConst.PARMS_CONTACTS_ERROR);
		}
		List<ParamWebsite> paramWebsiteList = JSON.parseArray(websiteInfos, ParamWebsite.class);
		List<ParamUserContact> paramContactList = JSON.parseArray(contacts, ParamUserContact.class);
		//忽略运营商信息查询
		GetTokenReq req = new GetTokenReq();
		
		if(JXLConst.EBUSI_SKIP_MOBILE_YES.equals(ignoreMobile)){
			if(paramWebsiteList != null && paramWebsiteList.size() > 0){
				for (ParamWebsite website : paramWebsiteList) {
					if (StringUtil.isEmpty(website.getCategory_en_name())
							|| StringUtil.isEmpty(website.getWebsite_en_name())) {
						logger.error("{} 聚信立运营商电商提交申请忽略运营商但没有其他网站信息");
						throw new Exception(JXLConst.PARMS_WEBSITE_SIZE_ZERO);
					}
				}
			}else{
				logger.error("{} 聚信立运营商电商提交申请忽略运营商但没有其他网站信息");
				throw new Exception(JXLConst.PARMS_WEBSITE_SIZE_ZERO);
			}
			req.setSkip_mobile(true);
		}else if(JXLConst.EBUSI_SKIP_MOBILE_NO.equals(ignoreMobile)){
			req.setSkip_mobile(false);
		}else{
			logger.info("{} 聚信立运营商提交表单申请是否跳过手机运营商验证参数错误，传入参数为[" + ignoreMobile + "]");
			throw new Exception(JXLConst.PARAM_IGNOREMOBILE_ERROR);
		}
		
		
		if(paramWebsiteList != null && paramWebsiteList.size() > 0){
			ebusiWebList = new ArrayList<EBusiWebsite>();
			for (ParamWebsite paramWeb : paramWebsiteList) {
				EBusiWebsite ebusiWebSite = new EBusiWebsite();
				ebusiWebSite.setName(paramWeb.getWebsite_en_name());
				ebusiWebSite.setCategory(paramWeb.getCategory_en_name());
				ebusiWebList.add(ebusiWebSite);
			}
			req.setSelected_website(ebusiWebList);
		}
		
		if(paramContactList != null && paramContactList.size()>0){
			contactList = new ArrayList<Contact>();
			for (ParamUserContact paramUserContact : paramContactList) {
				Contact contact = new Contact();
				contact.setContact_name(paramUserContact.getContactName());
				contact.setContact_tel(paramUserContact.getContactTel());
				contact.setContact_type(paramUserContact.getContactType());
				contactList.add(contact);
			}
			req.setContacts(contactList);
		}
		
		
		ParamUserOtherInfo paramOtherInfo = JSON.parseObject(otherInfo, ParamUserOtherInfo.class);
		basicInfo = new UserBasicInfo();
		basicInfo.setHome_addr(paramOtherInfo.getHomeAddr());
		basicInfo.setHome_tel(paramOtherInfo.getHomeTel());
		basicInfo.setCell_phone_num2(paramOtherInfo.getMobileNo2());
		basicInfo.setWork_addr(paramOtherInfo.getWorkAddr());
		basicInfo.setWork_tel(paramOtherInfo.getWorkTel());
		
		basicInfo.setName(name);
		basicInfo.setId_card_num(cardNo);
		basicInfo.setCell_phone_num(mobileNo);
		
		
		req.setBasic_info(basicInfo);
		
		return req;
		
	}


	public String getHttpsPostUrl() {
		return httpsPostUrl;
	}

	public void setHttpsPostUrl(String httpsPostUrl) {
		this.httpsPostUrl = httpsPostUrl;
	}

	public String getOrgAccount() {
		return orgAccount;
	}

	public void setOrgAccount(String orgAccount) {
		this.orgAccount = orgAccount;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

}
