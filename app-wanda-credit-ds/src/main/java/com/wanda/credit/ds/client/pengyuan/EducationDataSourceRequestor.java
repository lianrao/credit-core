package com.wanda.credit.ds.client.pengyuan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.rpc.ServiceException;

import org.apache.commons.io.IOUtils;
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
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.converter.WsDateConverter;
import com.wanda.credit.base.converter.WsDoubleConverter;
import com.wanda.credit.base.converter.WsIntConverter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ModelUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.guoztCredit.BaseGuoZTIncacheRequestor;
import com.wanda.credit.ds.dao.domain.Py_edu_college;
import com.wanda.credit.ds.dao.domain.Py_edu_degree;
import com.wanda.credit.ds.dao.domain.Py_edu_degreeNew;
import com.wanda.credit.ds.dao.domain.Py_edu_personBase;
import com.wanda.credit.ds.dao.iface.IGuoZTDegreesService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduCollegeService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduDegreeService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduPersonBaseService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_educationQuery")
public class EducationDataSourceRequestor extends BasePengYuanNewSourceRequestor implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(EducationDataSourceRequestor.class);

	private static WebServiceSingleQueryOfUnzipSoapBindingStub dynEduStub = null;
	@Autowired
	private IPYEduPersonBaseService pyEduPersonBaseService;
	@Autowired
	private IPYEduDegreeService pyEduDegreeService;
	@Autowired
	private IPYEduCollegeService pyEduCollegeService;
	private final String KEY_RESULT = "treatResult";
	@Autowired
	private BaseGuoZTIncacheRequestor baseIncaheGZTService;
	@Autowired
	private IExecutorFileService fileService;

	@Autowired
	private IPropertyEngine propertyEngine;

	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	private IGuoZTDegreesService degreesService;

	/**
	 * 构建鹏元请求信息
	 * @param queryType
	 * @param reportIds
	 * @param name
	 * @param cardNo
	 * @return
	 */
	private String buildRequestBody(String queryType,String reportIds, String name,String cardNo){
		StringBuffer conditionXML = new StringBuffer();
		conditionXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?><conditions><condition queryType=\""+queryType+"\">");
		conditionXML.append("<item><name>name</name><value>");
		conditionXML.append(name);
		conditionXML.append("</value></item>");
		conditionXML.append("<item><name>documentNo</name><value>");
		conditionXML.append(cardNo);
		conditionXML.append("</value></item>");
		conditionXML.append("<item><name>subreportIDs</name><value>");
		conditionXML.append(reportIds);
		conditionXML.append("</value></item>");
		conditionXML.append("</condition></conditions>");
		return conditionXML.toString();
	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		Map<String, Object> model_param = new HashMap<String, Object>();
		List<String> tags = new ArrayList<String>();
		String initTag = Conts.TAG_SYS_ERROR;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id("ds_educationQuery");
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(propertyEngine.readById("sys.credit.client.pengyuan.url"));
		logObj.setIncache("0");	
		String edu_acctId = propertyEngine.readById("ds_edu_acctId");
		/**记录请求状态信息*/
		Map<String,Object> paramIn = new HashMap<String,Object>();
		try {
			TreeMap<String, Object> retdata = new TreeMap<String, Object>();
			String name = (String)ParamUtil.findValue(ds.getParams_in(), paramIds[0]);
			String cardNo = ((String)ParamUtil.findValue(ds.getParams_in(), paramIds[1])).toUpperCase();//如果身份证后一位为X转成大写保存
			String degreeLevel = (String)ParamUtil.findValue(ds.getParams_in(), "degreeLevel");
			String acct_id = (String)ParamUtil.findValue(ds.getParams_in(), "acct_id");
			String crptedCardNo = synchExecutorService.encrypt(cardNo);

			Py_edu_personBase personBase = new Py_edu_personBase();
			Py_edu_degreeNew degreeNew = null;
			degreeNew = new Py_edu_degreeNew();
			Py_edu_degree degree = new Py_edu_degree();
			Py_edu_college college = new Py_edu_college();
			int topDegreeCode = 0;
			String topDegree = "";
		
			paramIn.put("name", name);
			paramIn.put("cardNo",cardNo);					
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			//校验身份证号是否合法
			String valiRes = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(valiRes)) {
				logger.info("{} 身份证号码不合法"  , prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
				return rets;
			}
			int gender = 0;
			String birthDays="";
			Map<String,String> mapresult = baseIncaheGZTService.IdNOToSex(cardNo,prefix);
			gender = Integer.valueOf(mapresult.get("gender"));
			birthDays = mapresult.get("birthDays");
			boolean is_eduAcct = isStartTimeReturn(edu_acctId,acct_id);
			boolean inCached = false;
			boolean inCachGuoZT = false;
			int date_num = 0;
			String incache_flag = "0";
			model_param.put("AGES", IdNOToAge(cardNo));
			model_param = ModelUtils.calculate("M_credit_eduModel", ParamUtil.convertParams(model_param),false);
			if(model_param!=null){
				date_num = (int)Double.parseDouble(extractValueFromResult("INCACHE_NUM",model_param).toString());
				incache_flag = extractValueFromResult("INCACHE_FLAG",model_param).toString();
				if("1".equals(incache_flag) || "2".equals(incache_flag)){
					inCached = pyEduPersonBaseService.inCachedMonth(name,crptedCardNo,date_num*30);
					logger.info(" 判断是否走缓存{},{}", incache_flag,inCached);
					if(!inCached){
						inCached = degreesService.inCachedDate(name, crptedCardNo,date_num*30);
						inCachGuoZT = true;
					}
					logger.info("{} 查询模型获得数据:{} {}", new String[] { prefix,incache_flag,extractValueFromResult("INCACHE_NUM",model_param).toString() });
				}
			}
			Map<String,String> photoData = new HashMap<String, String>();
			if(!inCached){
				/**是否启用 缓存数据 否*/				
				logger.info("{}学历数据源采集开始......", new String[] { prefix});
				String reqXML  = buildRequestBody(queryType,reportIds,name,cardNo);
//		 		logger.info("请求参数>>>{}",reqXML);
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id,reqXML,new String[]{trade_id}));
		 		WebServiceSingleQueryOfUnzipSoapBindingStub dynStubEdu = createStubEdu(trade_id);
		 		String respXML = dynStubEdu.queryReport(userId, userPwd, reqXML);
		 		DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id,respXML,new String[]{trade_id}));
//				logger.info("{}学历数据源查询完成,返回数据:\n{}", new String[] { prefix,respXML});
				if (respXML == null || respXML.length() == 0) {
					logger.error("{} 鹏源学历查询返回异常！", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "学历查询失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
					logObj.setState_msg("鹏源学历查询返回异常");
					logObj.setBiz_code3(trade_id); 
					return rets;
				}
				//解析返回报文
				Document rspDoc = DocumentHelper.parseText(filtRspBody(respXML));
				Node status = rspDoc.selectSingleNode("//result/status");
				/**记录响应状态信息*/				
				if(status!=null && !"1".equals(status.getStringValue())){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "远程数据源返回失败!");
					logger.error("{} 远程数据源返回失败：{}",prefix);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);	
					logObj.setBiz_code3(trade_id); 
					Node errMsg = rspDoc.selectSingleNode("//result/errorMessage");
					if(errMsg != null){
						logObj.setState_msg(errMsg.getStringValue());						
					}
					tags.add(initTag);
					logObj.setTag(StringUtils.join(tags, ";"));
					rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
					return rets;
				}else{
					logger.info("{} 鹏元学历信息开始分析...",prefix);
					XStream stream = new XStream(new DomDriver());
					stream.registerConverter(new WsDateConverter("yyyy-MM-dd",
							new String[] { "yyyyMMdd", "yyyy" }));
					stream.registerConverter(new WsIntConverter());
					stream.registerConverter(new WsDoubleConverter());
					omitFields(stream);
					//校验开始...
					Node rtnode = rspDoc.selectSingleNode("//*/lastEducationInfo");
					if(rtnode!=null){
						logObj.setBiz_code1(errorMap.get(((Element) rtnode).attributeValue(KEY_RESULT)));
						logObj.setBiz_code3(trade_id); 
						if(ArrayUtils.contains(new String[]{"2"},  ((Element) rtnode).attributeValue(KEY_RESULT))){
							// add by wangjing 2015-12-15 快易花风控准入规则学历校验
							retdata.put("topDegreeCode", topDegreeCode);
							retdata.put("topDegree", topDegree);
							retdata.put("edu_result", "1");//未查到
							rets.put(Conts.KEY_RET_DATA, retdata);
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
							// end
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_EDU_NOTFOUND_EXCEPTION.ret_msg);
							logger.warn("{} 没有查询到任何学历信息",prefix);
							
							initTag = Conts.TAG_UNFOUND;
							tags.add(initTag);
							logObj.setTag(StringUtils.join(tags, ";"));
							rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
							/*保存未查的信息到数据库-未查得是学校信息和学位信息为0*/
							personBase = new Py_edu_personBase();
							personBase.setTrade_id(trade_id);
							personBase.setCollege("0");
							personBase.setDegree("0");
							personBase.setName(name);
							personBase.setDocumentNo(crptedCardNo);
							personBase.setVerifyResult(null);
							personBase.setCreate_date(new Date());
							Element cisReport = (Element)rspDoc.selectSingleNode("//*/cisReport");
							personBase.setReportId(cisReport.attributeValue("reportID"));
							pyEduPersonBaseService.saveNewPerBase(personBase);
						logger.info("{}未查得是学校信息和学位信息时personBase身份证的值{}", new String[] { personBase.getDocumentNo()});
							/**/
							return rets;
						}else if(ArrayUtils.contains(new String[]{"3"},  ((Element) rtnode).attributeValue(KEY_RESULT))){
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION.ret_msg);
							logger.warn("{} 学历信息查询失败 :{}",prefix,((Element) rtnode).attributeValue("errorMessage"));
							initTag = Conts.TAG_UNFOUND_OTHERS;
							tags.add(initTag);
							logObj.setTag(StringUtils.join(tags, ";"));
							rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
							return rets;
						}
					}
					//结束
					Node node = rspDoc.selectSingleNode("//*/personBaseInfo");
					if(node!=null){
						initTag = Conts.TAG_FOUND;					
						Element element = (Element) node;
						stream.alias("personBaseInfo", Py_edu_personBase.class);
						personBase = (Py_edu_personBase) stream
								.fromXML(element.asXML());
						if(personBase!=null){
							personBase.setTrade_id(trade_id);
							node = rspDoc.selectSingleNode("//*/riskAndAdviceInfo");
							if(node!=null)
								personBase.setRiskAndAdviceInfo(node.getStringValue());
							/**wcs add record reportid*/
							Element cisReport = (Element)rspDoc.selectSingleNode("//*/cisReport");
							personBase.setReportId(cisReport.attributeValue("reportID"));
							personBase.setDocumentNo(crptedCardNo);
						logger.info("{}学历数据源采集成功时personBase身份证的值{}", new String[] { personBase.getDocumentNo()});
						}
					}
					node = rspDoc.selectSingleNode("//*/educationInfo");
					if(node!=null){
						List<Element> elements = ((Element) node).elements("item");
						for (Element e : elements) {
							Node nd = e.selectSingleNode("degreeInfo");
							stream.alias("degreeInfo", Py_edu_degree.class);
							degree = (Py_edu_degree) stream.fromXML(nd.asXML());
							if(degree!=null){
								String checkdegree=degree.getDegree();
								String checkstudyStyle=degree.getStudyStyle();
								if(checkdegree!=null&&checkstudyStyle!=null){
									String studyType  =checkType(checkdegree, checkstudyStyle);
									degree.setStudyType(studyType);
								}else{
									degree.setStudyType("其他");
								}
								degree.setTrade_id(trade_id);
								degree.setBirthday(birthDays);
								String photoContent = null;
								if(StringUtils.isNotBlank(degree.getPhoto())){
									photoContent = degree.getPhoto()
											.trim().replaceAll("	", "")
											.replaceAll("\r|\n", "");
									String photoId = savePhoto(prefix,degree.getPhotoStyle(),
											photoContent,new String[]{trade_id,cardNo});
									photoData.put("fileId", photoId);
									degree.setPhoto_id(photoId);
								}
								degree.setPhoto(photoContent);
							}
							nd = e.selectSingleNode("collegeInfo");
							stream.alias("collegeInfo", Py_edu_college.class);
							college = (Py_edu_college) stream.fromXML(nd.asXML());
						}
					}					
					pyEduPersonBaseService.saveNewPerBase(personBase,degree,college);
				logger.info("{}最终采集成功时degree身份证的值++++{}", new String[] { degree.getBirthday()});
				}
				logger.info("{}学历数据源采集成功", new String[] { prefix});
			}else{
				/**是否启用 缓存数据 是*/
				logObj.setIncache("1");	
				if(inCachGuoZT){
					//查询国政通数据表
					logObj.setBiz_code1("guozt_data");
					logger.info("{}查询国政通缓存学历数据", new String[] { prefix});
					Map<String, String> mapSex = baseIncaheGZTService.IdNOToSex(cardNo, prefix);
					Map<String, String> mapStr = new HashMap<String, String>();
					mapStr.put("trade_id",trade_id);
					mapStr.put("incache_flag",incache_flag);
					mapStr.put("name",name);
					mapStr.put("enCardNo",crptedCardNo);
					mapStr.put("cardNo",cardNo);
					mapStr.put("gender",mapSex.get("gender"));
					mapStr.put("birthDays",mapSex.get("birthDays"));				
					rets = baseIncaheGZTService.getEduIncacheGuoZT(mapStr,date_num,is_eduAcct);
					if(rets == null){
						rets = new HashMap<String, Object>();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "学历查询失败!");
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
					}
					String[] initTags = (String[]) rets.get(Conts.KEY_RET_TAG);
					initTag = initTags[0];
					return rets;
				}else{
					//查询鹏元数据表
					logger.info("{}查询鹏元缓存学历数据", new String[] { prefix});
					personBase = pyEduPersonBaseService.queryPersonBase(name, crptedCardNo);
					initTag = Conts.TAG_INCACHE_UNFOUND;	
					logger.info("{}查询鹏元缓存学历数据,college值:{}", new String[] { prefix,personBase.getCollege()});
					if (!"0".equals(personBase.getCollege())) {
						initTag = Conts.TAG_INCACHE_FOUND;					
						degree = pyEduPersonBaseService.queryPersonDegree(personBase.getTrade_id());
						String photoId = "";
						String cachePhoto = "";
						String photoStyle = "";
						if (degree !=null) {
							String checkdegree=degree.getDegree();
							String checkstudyStyle=degree.getStudyStyle();
							if(checkdegree!=null&&checkstudyStyle!=null){
								String studyType  =checkType(checkdegree, checkstudyStyle);
								degree.setStudyType(studyType);
							}else{
								degree.setStudyType("其他");
							}
							photoId = degree.getPhoto_id();
							cachePhoto = degree.getPhoto();
							photoStyle=degree.getPhotoStyle();
							if(StringUtils.isNotBlank(photoStyle)){
								degree.setPhotoStyle(photoStyle);
							}else{
								degree.setPhotoStyle("");
							}
						}
						if(StringUtils.isBlank(photoId) && StringUtils.isNotBlank(cachePhoto)){
							/**历史数据*/
							photoId = savePhoto(prefix,degree.getPhotoStyle(), degree.getPhoto(), 
									new String[]{trade_id,cardNo});
							pyEduDegreeService.updateDegreePhotoId(degree.getId(), photoId);
						}else{
							degree.setPhoto(fileService.download(photoId,trade_id));
							logger.info("{}缓存数据中此人学历数据photoId!", new String[] { photoId});
							
						}
						photoData.put("fileId", photoId);
						college = pyEduPersonBaseService.queryPersonCollege(personBase.getTrade_id());
					}
					logger.info("{}缓存数据中存在此人学历数据!", new String[] { prefix});
				}				
			}
			logger.info("{} 鹏元学历信息开始封装!",prefix);
			//personBase.setDocumentNo(cardNo);
			personBase.setRiskAndAdviceInfo("");
			personBase.setVerifyResult(null);
			personBase.setSpecialty("");
			
			Py_edu_personBase personBaseNew = personBase.clone();
			personBaseNew.setDocumentNo(cardNo);
//			重新拼装degree
			degreeNew.setCollege(degree.getCollege());
			degreeNew.setStartTime("");
			degreeNew.setGraduateTime(degree.getGraduateTime());
			degreeNew.setStudyStyle(degree.getStudyStyle());
			degreeNew.setStudyType(degree.getStudyStyle());
			degreeNew.setSpecialty("");
			degreeNew.setDegree(degree.getDegree());
			degreeNew.setStudyResult(degree.getStudyResult());
			degreeNew.setPhoto(degree.getPhoto());
			degreeNew.setPhoto_id(degree.getPhoto_id());
			degreeNew.setPhotoStyle("");
			degreeNew.setTrade_id(trade_id);
			degreeNew.setLevelNo("");
			degreeNew.setIsKeySubject("");
			degreeNew.setId(trade_id);
			/*如果从缓存中获取的结果是未查得-直接返回数据*/
			if ("0".equals(personBase.getCollege())) {
				retdata.put("topDegreeCode", topDegreeCode);
				retdata.put("topDegree", topDegree);
				retdata.put("edu_result", "1");//未查到
			}else{
				retdata.put("personBase", personBaseNew);
				retdata.put("degree", degreeNew);
				retdata.put("college", college);
				// add by wangjing 2015-12-15 用于快易花风控准入规则学历校验
				if(personBase != null){
				  topDegreeCode = BaseGuoZTIncacheRequestor.degreeMap.get(personBase.getDegree());
				  topDegree = personBase.getDegree();
				}
				retdata.put("topDegreeCode", topDegreeCode);
				retdata.put("topDegree", topDegree);
				// end
				retdata.put("edu_result", "0");//查到
				if(StringUtils.isNotBlank(degreeLevel)){
					degreeLevel = degreeLevel.trim();
					retdata.put("degreeLevelCheck", baseIncaheGZTService.check(degreeLevel,topDegree));
				}
			}			
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");			
			rets.put("photoData", photoData);

			tags.add(initTag);
			logObj.setTag(StringUtils.join(tags, ";"));
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));			
		}catch (Exception e) {
			initTag = Conts.TAG_SYS_ERROR;
			logger.error(prefix +" 数据源处理时异常",e);		
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			/**如果是超时异常 记录超时信息*/
		    if(ExceptionUtil.isTimeoutException(e)){
		    	logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);	
		    	initTag = Conts.TAG_SYS_TIMEOUT;
		    } else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			}
		    tags.add(initTag);
			logObj.setTag(StringUtils.join(tags, ";"));
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+e.getMessage());
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
		}finally {
			logger.info("{} 鹏元学历信息log记录!",prefix);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(initTag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}
		return rets;
	}
	
    private String getMockData() throws Exception {
    	URL url = this.getClass().getResource("/pymock.xml");
		File dir = new File(url.toURI());
		return IOUtils.toString(url);
	}
	/**忽略多余的反序列化字段*/
	private void omitFields(XStream stream) {
		String fields = PropertyEngine.get("py_degree_omit");
		if(StringUtils.isNotBlank(fields)){
			for(String item : fields.split(",")){
				stream.omitField(Py_edu_degree.class, item);
			}
		}

		fields = PropertyEngine.get("py_person_omit");
		if(StringUtils.isNotBlank(fields)){
			for(String item : fields.split(",")){
				stream.omitField(Py_edu_personBase.class, item);
			}
		}
		
		fields = PropertyEngine.get("py_college_omit");
		if(StringUtils.isNotBlank(fields)){
			for(String item : fields.split(",")){
				stream.omitField(Py_edu_college.class, item);
			}
		}
	}
	/**学历一致性核查*/
	private String check(String degreeLevel, String topDegree) {
		
		/*11=博士研究生;10=硕士研究生,研究生班;6=本科,第二学士学位,高升本,专升本,第二本科,研究生班,夜大电大函大普通班;
          4=专科,专科(高职);other=不详*/
		String ruleStr = propertyEngine.readById("py_edu_degreelvl_map");
		if(StringUtils.isNotBlank(ruleStr)){
			String[] rules = ruleStr.split(";");
	        for(int i=0;i<rules.length;i++){
	        	String[] rule = rules[i].split("=");	        	
	        	if(degreeLevel.equals(rule[0])){
	        		if(rule[1].indexOf(topDegree) > -1){
	        			return "一致";
	        		}
	        	}else if("other".equals(rule[0])){
	        		if(rule[1].indexOf(topDegree) > -1){
	        			return "其他原因不一致";
	        		}
	        	}
	        }	
		}        
		return "不一致";
	}
	/**照片存储到ns系统 现在是文件*/
	private String savePhoto(String prefix,String photoType,String photoContent,String...labels ) {
    	/**日志存储到文件系统*/
		logger.info("{} 学历照片存储开始",prefix);
/*		String fileId = fileEngine.upload("pyeducation", FileArea.DS,
				FileType.match(photoType), photoContent, labels);
*/		
		String fileId = null;
		try {
			fileId = fileService.upload(photoContent, FileType.match(photoType), FileArea.DS,prefix);
			logger.info("{} 学历照片存储结束",prefix);
		} catch (Exception e) {
			logger.error(prefix +" 文件上传失败", e);
		}
		return fileId;
	}

	private String currentUsedUrl = null;
	private WebServiceSingleQueryOfUnzipSoapBindingStub createStubEdu(String trade_id) 
			throws ServiceException {
		String url = propertyEngine.readById("sys.credit.client.pengyuan.url");
		if(dynEduStub == null || !url.equals(currentUsedUrl)){
			logger.info("create new stub {}",url);
			currentUsedUrl= url;
			dynEduStub = (WebServiceSingleQueryOfUnzipSoapBindingStub) new WebServiceSingleQueryOfUnzipServiceLocator()
	         .getWebServiceSingleQueryOfUnzip(currentUsedUrl);	
	        dynEduStub.setTimeout(Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.httpEdu.timeout")));
		 }
		 return dynEduStub;
	}
	
	
	// 学历编码Map
	// 且学历信息如果客户填写大专及以上（大专、本科、硕士、博士，码值30、40、50、60），需要接外部数据源鹏元查询学历信息进行验证。
	// 鹏元返回数据：博士研究生、硕士研究生、研究生班、本科、第二本科、专升本、第二学士学位、专科、第二专科、专科(高职)、夜大电大函大普通班
	private static Map<String, Integer> degreeMap = new HashMap<String, Integer>();
	static {
		//专科以下
		degreeMap.put("不详", 5);
		degreeMap.put("夜大电大函大普通班", 10);//6分
		//专科
		degreeMap.put("第二专科", 15);//6
		degreeMap.put("专科(高职)", 20);//6 大专
		degreeMap.put("专科", 25);//6 大专
		//本科
		degreeMap.put("专升本", 30);//13
		degreeMap.put("本科", 35);//13
		degreeMap.put("高升本", 40);
		degreeMap.put("第二本科", 45);//14
		degreeMap.put("第二学士学位", 50);//14
		//研究生
		degreeMap.put("研究生班", 55);//14
		degreeMap.put("硕士研究生", 60);//15
		//博士
		degreeMap.put("博士研究生", 65);//17
	}
	
	/**映射关系: 鹏元返回码 <-> 系统码*/
	private static Map<String, String> errorMap = new HashMap<String, String>();
	static {
		/**查得*/
		errorMap.put("1", "PYXL_001");
		/**未查得*/
		errorMap.put("2", "PYXL_002");
		/**其他原因未查得*/
		errorMap.put("3", "PYXL_003");
	}
//	添加拼装学历类别的方法mafei-add
    protected String  checkType(String degree,String studystyle){
		String retstyle=null;
		if((degree.equals("专科")||degree.contains("专科")||degree.equals("高升本")||degree.equals("专升本")||degree.equals("第二本科")||degree.equals("第二学士学位")||degree.equals("第二专科")||degree.equals("本科"))&& (studystyle.equals("全日制")||studystyle.equals("普通全日制"))){
			retstyle="普通";
		}else if((degree.equals("硕士")||degree.equals("硕士研究生")||degree.equals("研究生班")||degree.equals("博士研究生")||degree.equals("博士"))&& (studystyle.equals("全日制")||studystyle.equals("非全日制")||studystyle.equals("普通全日制")||studystyle.equals("在职"))){
			retstyle="研究生";
		}else if(studystyle.equals("业余")||studystyle.equals("夜大学")||studystyle.equals("脱产")||studystyle.equals("函授")){
			retstyle="成人";
		}else if(studystyle.equals("开放教育")||studystyle.equals("电视教育")){
			retstyle="开放教育";
		}else if(studystyle.equals("远程教育")){
			retstyle="远程教育";
		}else if(studystyle.equals("网络教育")){
			retstyle="网络教育";
		}else if(studystyle.equals("")||studystyle.equals("不详")||studystyle.equals("*")){
			retstyle="自考";
		}else{
			retstyle="其他";
		}
		return retstyle;
	}
   /**判断ex异常是否是超时异常：SocketTimeoutException
    * */	
   private boolean isTimeoutException(Exception ex){
	   if(ex == null ) return false;
	   String exeMsg = ex.getMessage();
	   if(exeMsg != null && exeMsg.
			   toLowerCase().indexOf("sockettimeout") > -1){
		   return true;
	   }
	   exeMsg = ex.toString(); 
	   if(exeMsg != null && exeMsg.toLowerCase().
			   indexOf("sockettimeout") > -1){
		   return true;
	   }
	   return false;
   }
	protected String doHttpsPost(String trade_id, String aijin_address, String reqData) {
	return null;
}
	public static void main(String[] args) {
		XStream stream = new XStream(new DomDriver());
		stream.registerConverter(new WsDateConverter("yyyy-MM-dd",
				new String[] { "yyyyMMdd", "yyyy" }));
		stream.registerConverter(new WsIntConverter());
		stream.registerConverter(new WsDoubleConverter());

	}

}
