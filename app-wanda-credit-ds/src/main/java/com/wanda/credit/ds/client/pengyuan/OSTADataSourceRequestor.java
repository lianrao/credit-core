package com.wanda.credit.ds.client.pengyuan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.converter.WsDateConverter;
import com.wanda.credit.base.converter.WsDoubleConverter;
import com.wanda.credit.base.converter.WsIntConverter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.pengyuan.bean.Py_osta_Item;
import com.wanda.credit.ds.client.pengyuan.bean.Py_osta_PersonBase;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_osta_info;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYOstaService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**   
* @Description: 鹏元-职业资格信息查询
* @author xiaobin.hou  
* @date 2016年8月15日 下午4:20:05 
* @version V1.0   
*/
@DataSourceClass(bindingDataSourceId="ds_py_osta")
public class OSTADataSourceRequestor extends BasePengYuanDataSourceRequestor implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(OSTADataSourceRequestor.class);
	
	private final static String OSTA_RESULT = "osta_result";
	private final static String PERSON_BASE_INFO = "person_base_info";
	private final static String OSTA_DETAIL = "osta_detail";
	private final String KEY_RESULT = "treatResult";
	
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@Autowired
	private IPYOstaService pyOstaService;
	
	private String queryReasonID = "102";

	/**
	 * 构建鹏元-职业资格请求信息
	 * @param queryType
	 * @param reportIds
	 * @param name
	 * @param cardNo
	 * @param queryReasonID
	 * @param refID
	 * @return
	 */
	private String buildRequestBody(String queryType,String reportIds, String name,String cardNo,String queryReasonID,String refID){
		StringBuffer conditionXML = new StringBuffer();
		conditionXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?><conditions><condition queryType=\"");
		conditionXML.append(queryType);
		conditionXML.append("\">");
		conditionXML.append("<item><name>name</name><value>");
		conditionXML.append(name);
		conditionXML.append("</value></item>");
		conditionXML.append("<item><name>documentNo</name><value>");
		conditionXML.append(cardNo);
		conditionXML.append("</value></item>");
		conditionXML.append("<item><name>subreportIDs</name><value>");
		conditionXML.append(reportIds);
		conditionXML.append("</value></item>");
		conditionXML.append("<item><name>queryReasonID</name><value>");
		conditionXML.append(queryReasonID);
		conditionXML.append("</value></item>");
		conditionXML.append("<item><name>refID</name><value>");
		conditionXML.append(refID);
		conditionXML.append("</value></item>");
		conditionXML.append("</condition></conditions>");
		return conditionXML.toString();
	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;		
		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		List<String> tags = new ArrayList<String>();
		
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "请求失败");
		//请求日志信息持久化类
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(DynamicConfigLoader.get("sys.credit.client.pengyuan.osta.url"));
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("请求失败");

		try {
			
			String name = (String)ParamUtil.findValue(ds.getParams_in(), paramIds[0]);
			String cardNo = ((String)ParamUtil.findValue(ds.getParams_in(), paramIds[1])).toUpperCase();//如果身份证后一位为X转成大写保存
			String crptedCardNo = synchExecutorService.encrypt(cardNo);
			
			logger.info("{} 保存请求参数" , trade_id);
			saveParamIn(name,cardNo,trade_id,logObj);
			
			//校验身份证号是否合法
			logger.info("{} 校验身份证号码是否合法" , prefix);
			String valiRes = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(valiRes)) {
				logger.info("{} 身份证号码不合法： {}"  , prefix,valiRes);
				rets.clear();
				tags.add(Conts.TAG_UNFOUND);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.ret_msg);
				return rets;
			}
			

			Py_osta_PersonBase personBase = null;
			
			//判断缓存中是否有数据
			boolean inCached = false;
			List<Py_osta_info> cahcheData  = pyOstaService.queryCacheData(name,crptedCardNo,1);
			
			if (cahcheData != null && cahcheData.size() > 0) {
				try{
					inCached = true;
					retdata = domain2Out(cahcheData);
					
					tags.add(Conts.TAG_INCACHE_FOUND);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功!");
					rets.put(Conts.KEY_RET_DATA, retdata);
//					rets.put(Conts.KEY_RET_TAG, tags);
					
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					logObj.setBiz_code1(Conts.TAG_INCACHE_FOUND);
					logObj.setIncache("1");
					
					return rets;
				}catch(Exception e){
					logger.info("{} 获取缓存数据异常 {}" , prefix , e.getMessage());
					tags.clear();
					inCached = false;
				}				
			}
			
			if(!inCached){
				/**是否启用 缓存数据 否*/
				logObj.setIncache("0");	
				logger.info("{} 请求职业资格查询开始......", new String[] { prefix});
				String reqXML  = buildRequestBody(queryType,reportIds,name,cardNo,queryReasonID,trade_id);
				String respXML = ostaStub.queryReport(userId, userPwd, reqXML);
			
//				String respXML = CommonUtil.readFile(
//						"d:\\xiaobin.hou\\桌面\\temp\\a.txt", "GBK");
				logger.info("{} 请求查询职业资格信息成功，开始解析返回报文" , prefix);
				// 解析返回报文
				Document rspDoc = DocumentHelper.parseText(filtRspBody(respXML));
				/** 获取响应状态信息 */
				Node status = rspDoc.selectSingleNode("//result/status");
				
				if (status == null ||  !"1".equals(status.getStringValue())) {
					
					logger.info("{} 获取响应状态失败或鹏元返回状态异常 {}" , prefix , respXML);

					Node errMsg = rspDoc.selectSingleNode("//result/errorMessage");
					Node errCode = rspDoc.selectSingleNode("//result/errorCode");

					if (errMsg != null) {
						logObj.setState_msg(errMsg.getStringValue());						
					}
					if (errCode != null) {
						logObj.setBiz_code2(errCode.getStringValue());
					}
					tags.add(Conts.TAG_UNFOUND);
					return rets;
				} 
				
				logger.info("{} 请求查询职业资格信息状态码成功" , prefix);

				XStream stream = new XStream(new DomDriver());
				stream.registerConverter(new WsDateConverter("yyyy-MM-dd",
						new String[] { "yyyyMMdd", "yyyy" }));
				stream.registerConverter(new WsIntConverter());
				stream.registerConverter(new WsDoubleConverter());
				// 校验开始...
				Node rtnode = rspDoc.selectSingleNode("//*/ostaInfo");

				if (rtnode == null) {
					logger.info("{} 返回报文没有ostaInfo标签 {}" , prefix ,respXML);
					tags.add(Conts.TAG_UNFOUND);
					return rets;
				}
				
				Element ostaInfoElement = (Element) rtnode;

				if (ArrayUtils.contains(new String[] { "2" },
						ostaInfoElement.attributeValue(KEY_RESULT))) {
					
					tags.add(Conts.TAG_UNFOUND);
					
					logger.info("{} 鹏元-返回未查得" , prefix);
					logObj.setBiz_code1(Conts.TAG_UNFOUND);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					// 职业资格信息-未查得
					retdata.put(OSTA_RESULT, "0");// 未查到
					retdata.put(PERSON_BASE_INFO, "");
					retdata.put(OSTA_DETAIL, "");
					rets.clear();
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功！");

					/* 将未查得信息暂时不缓存到数据库中 */
					
					return rets;
				}
				
				if (ArrayUtils.contains(new String[] { "3" },
						ostaInfoElement.attributeValue(KEY_RESULT))) {
					logger.info("{} 鹏元返回其他原因未查得" , prefix);
					//TODO 职业资格信息-其他原因未查得
					logObj.setBiz_code1(Conts.TAG_UNFOUND_OTHERS);
					tags.add(Conts.TAG_UNFOUND_OTHERS);
					return rets;
				}

				// 结束
				Node personBaseNode = rspDoc.selectSingleNode("//*/personBaseInfo");
				if (personBaseNode != null) {
					Element element = (Element) personBaseNode;
					stream.alias("personBaseInfo", Py_osta_PersonBase.class);
					personBase = (Py_osta_PersonBase) stream.fromXML(element.asXML());
					//将身份证号保存到cardNo字段从而方便输出
					personBase.setCardNo(personBase.getDocumentNo());
					personBase.setDocumentNo(null);
					
					List<Element> itemElements = ((Element) rtnode).elements("item");
					List<Py_osta_Item> itemList = new ArrayList<Py_osta_Item>();
					if (itemElements != null && itemElements.size() > 0) {
						logger.info("{} 鹏元返回职业资格信息个数为：{}" , prefix ,itemElements.size());

						for (Element itemElement : itemElements) {
							Py_osta_Item item = new Py_osta_Item();
							stream.alias("item", Py_osta_Item.class);
							item = (Py_osta_Item) stream.fromXML(itemElement.asXML());
							itemList.add(item);
						}
						
						List<Py_osta_info> domainList = bean2Domain(itemList,personBase,trade_id,crptedCardNo);
						
						pyOstaService.addOstaInfo(domainList,name,crptedCardNo);
						
					}
					
					tags.add(Conts.TAG_FOUND);
					
					logObj.setBiz_code1(Conts.TAG_FOUND);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					
					JSONObject personJson = JSONObject.fromObject(personBase);
					personJson.remove("documentNo");
					retdata.put(PERSON_BASE_INFO, personJson.toString());
					retdata.put(OSTA_DETAIL, JSONArray.fromObject(itemList).toString());
					retdata.put(OSTA_RESULT, "1");
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_MSG, "请求成功!");
				}else{
					tags.add(Conts.TAG_UNFOUND);
					retdata.put(OSTA_RESULT, "0");// 未查到
					retdata.put(PERSON_BASE_INFO, "");
					retdata.put(OSTA_DETAIL, "");
				}
				return rets;			

		
			}
		}catch (Exception e) {
			
			logger.error("{} 数据源处理时异常 {}", prefix , e.getMessage());
			logObj.setBiz_code1(Conts.TAG_SYS_ERROR);
			tags.clear();
			tags.add(Conts.TAG_UNFOUND);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "交易失败");
			/**如果是超时异常 记录超时信息*/
		    if(isTimeoutException(e)){
		    	logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);	
		    	logObj.setBiz_code1(Conts.TAG_SYS_TIMEOUT);
		    }
		    
		}finally{			
			rets.put(Conts.KEY_RET_TAG, (String[])tags.toArray(new String[1]));
			logObj.setTag(StringUtils.join(tags,","));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		}
		return rets;
	}
	
	

   /**
	 * @param cahcheData
 * @return
	 * @throws Exception 
	 */
	private TreeMap<String, Object> domain2Out(List<Py_osta_info> cahcheData) throws Exception {
		
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		
		List<Py_osta_Item> itemList = new ArrayList<Py_osta_Item>();
		Py_osta_PersonBase person = new Py_osta_PersonBase();
		for (Py_osta_info ostaInfo : cahcheData) {
			Py_osta_Item item = new Py_osta_Item();
			BeanUtils.copyProperties(item, ostaInfo);
			BeanUtils.copyProperties(person, ostaInfo);
			itemList.add(item);
		}
		
		String encCardNo = person.getCardNo();
		if (!StringUtil.isEmpty(encCardNo)) {
			person.setCardNo(synchExecutorService.decrypt(encCardNo));
		}else{
			person.setCardNo("");
		}
		JSONObject personJsonObj = JSONObject.fromObject(person);
		personJsonObj.remove("documentNo");
		retData.put(PERSON_BASE_INFO, personJsonObj.toString());
		retData.put(OSTA_DETAIL, JSONArray.fromObject(itemList).toString());
		retData.put(OSTA_RESULT, "1");
		return retData;
	}
/**
     * 记录请求状态信息
	 * @param name
	 * @param cardNo
	 * @param logObj 
	 * @param trade_id 
	 */
	private void saveParamIn(String name, String cardNo, String trade_id, DataSourceLogVO logObj) {
		/**记录请求状态信息*/
		try{
			
			Map<String,Object> paramIn = new HashMap<String,Object>();
			paramIn.put("name", name);
			paramIn.put("cardNo",cardNo);			
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn,logObj);	
		}catch(Exception e){
			logger.info("{} 请求参数保存异常 {}" , trade_id , e.getMessage());
		}
		
		
	}

	/**
	 * 判断ex异常是否是超时异常：SocketTimeoutException
	 * */
	private boolean isTimeoutException(Exception ex) {
		if (ex == null)
			return false;
		String exeMsg = ex.getMessage();
		if (exeMsg != null
				&& exeMsg.toLowerCase().indexOf("sockettimeout") > -1) {
			return true;
		}
		exeMsg = ex.toString();
		if (exeMsg != null
				&& exeMsg.toLowerCase().indexOf("sockettimeout") > -1) {
			return true;
		}
		return false;
	}
   
   /**
     * 转化成持久化类用于数据保存
	 * @param itemList
	 * @param personBase
	 * @param encCardNo 
	 * @param tradeId 
	 * @return
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 */
	private static List<Py_osta_info> bean2Domain(List<Py_osta_Item> itemList,
			Py_osta_PersonBase personBase, String trade_id, String encCardNo) throws IllegalAccessException, Exception {
		
		List<Py_osta_info> domainList = new ArrayList<Py_osta_info>();

		if (itemList != null && itemList.size() > 0 && personBase != null) {
			Date nowTime = new Date();
			for (Py_osta_Item item : itemList) {
				Py_osta_info info = new Py_osta_info();
				
				BeanUtils.copyProperties(info, item);
				
				info.setTrade_id(trade_id);
				info.setAge(personBase.getAge());
				info.setBirthday(personBase.getBirthday());
				info.setCardNo(encCardNo);
				info.setGender(personBase.getGender());
				info.setName(personBase.getName());
				info.setOriginalAddress(personBase.getOriginalAddress());
				info.setVerifyResult(personBase.getVerifyResult());
				info.setCreate_date(nowTime);
				info.setUpdate_date(nowTime);
				info.setTreatResult("1");
				
				domainList.add(info);
			}
		}
		return domainList;
	};
	

}
