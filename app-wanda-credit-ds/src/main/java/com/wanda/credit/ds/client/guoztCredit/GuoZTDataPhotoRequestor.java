package com.wanda.credit.ds.client.guoztCredit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
@DataSourceClass(bindingDataSourceId="ds_guozt_jx")
public class GuoZTDataPhotoRequestor extends BaseGuoZTDataSourcesRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(GuoZTDataPhotoRequestor.class);
	private final  String CHANNEL_NO = "01";
	private final  String POLICE_PHOTO_NO = "POLICE_PHOTO_NOTEXISTS";
	private final  String POLICE_STATUS_CODESUSS = "3";
	private final  String POLICE_STATUS_SUSSES = "一致";
	private final  String POLICE_STATUS_FAIL = "不一致";
	private final  String STATUS_CHECK_EQUAL = "00";
	private final  String STATUS_CHECK_NO = "01";
	private final  String STATUS_CHECK_NULL = "02";
	private final  String SOURCE_ID = "03";
	@Autowired
	private INciicCheckService nciicCheckService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		String police_url = propertyEngine.readById("ds_guozt_police_url");
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String respXml = "";
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			int incache_days = Integer.valueOf(propertyEngine.readById("ds_police_incacheTime"));//公安数据(天)
			
			rets = new HashMap<String, Object>();
	 		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String flag = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //是否带照片,01带照片,02不带照片
			String acct_id = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();//账户号
			String params = name+","+cardNo;
			String enCardNo = synchExecutorService.encrypt(cardNo);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(guoztUrl);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setDs_id(ds.getId());
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", new String[] { prefix });
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			String cardNo_check = null;
			String name_check = null;
			String image = null;
			String fileId = null;
			retdata.put("server_idx", "03");
			boolean ds_incache = dsIncache(acct_id,ds.getId());
			if(!nciicCheckService.inCachedCount(name, enCardNo,incache_days) || ds_incache){
				logObj.setIncache("0");
				if ("1".equals(DynamicConfigLoader.get("ds_guozt_request_method"))) {//1走最新client调用方式
					respXml = httpClientQuery(params,prefix,police_url);
				}else {
					respXml = singQurey(params,prefix,police_url);
				}
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXml, new String[] { trade_id }));
				if("".equals(respXml)){
					logger.error("{} 国账通查询失败！",prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回异常!");	
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else{
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					Nciic_Check_Result nciic_check = new Nciic_Check_Result();
					nciic_check.setTrade_id(trade_id);
					nciic_check.setCardno(enCardNo);
					nciic_check.setName(name);
					nciic_check.setSourceid(SOURCE_ID);
					nciic_check.setStatus(STATUS_CHECK_NULL);
					//解析返回报文
					Document rspDoc = DocumentHelper.parseText(filtRspBody(respXml));
					Node repDocMsg = rspDoc.selectSingleNode("//data/message");
					Element repDocMsgEle = (Element) repDocMsg;
					Element messStat = repDocMsgEle.element("status");
					Element messStatDesc = repDocMsgEle.element("value");
					logObj.setState_msg(messStatDesc.getStringValue());
					logObj.setBiz_code2(messStat.getStringValue());
					Node policeInfos = rspDoc.selectSingleNode("//data/policeCheckInfos");
					if(policeInfos!=null && Integer.parseInt(messStat.getStringValue())>=0){		
						Element policeInfosEle = (Element) policeInfos;
						Document policeDoc = policeInfosEle.getDocument();
						Node status = policeDoc.selectSingleNode("//*/status");					
						String policeDocMsgStatus = status.getStringValue();						
						Node compStatus = policeDoc.selectSingleNode("//*/compStatus");
						Node compResult = policeDoc.selectSingleNode("//*/compResult");
						String compStat = compStatus.getStringValue();
						String compRes = compResult.getStringValue();
						logObj.setBiz_code3(policeDocMsgStatus);
						//查询成功
						if("0".equals(policeDocMsgStatus)){	
							//开始异步插入照片准备
							List<Map<String,String>> file_list = new ArrayList<Map<String,String>>();
							Map<String,String> fileMap = new HashMap<String,String>();
							fileMap.put("table_name", "CPDB_DS.T_DS_NCIIC_RESULT");
							fileMap.put("table_col", "image_file");
							file_list.add(fileMap);
							
							if(POLICE_STATUS_CODESUSS.equals(compStat)){
								logObj.setBiz_code1(CODE_EQUAL);
								resource_tag = Conts.TAG_MATCH;
								Node policeAddr = policeDoc.selectSingleNode("//*/policeadd");
								Node birthday = policeDoc.selectSingleNode("//*/birthday2");
								Node sex = policeDoc.selectSingleNode("//*/sex2");
								Node imageCheck = policeDoc.selectSingleNode("//*/checkPhoto");
								image = imageCheck.getStringValue();
								cardNo_check = POLICE_STATUS_SUSSES;
								name_check = POLICE_STATUS_SUSSES;
								retdata.put("resultGmsfhm", cardNo_check);
								retdata.put("resultXm", name_check);
								if(StringUtils.isNotEmpty(image)){
									logger.info("{}照片异步上传征信存储开始...", new String[] { prefix});
									fileService.uploadAsync(image,trade_id, FileType.JPG, FileArea.DS, file_list);
									logger.info("{}照片异步上传征信存储结束!", new String[] { prefix});	
									
									retdata.put("xp_content", image);
									retdata.put("xp_id", fileId);
									rets.clear();
									rets.put(Conts.KEY_RET_DATA, retdata);
									rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
									rets.put(Conts.KEY_RET_MSG, "采集成功!");
								}else{
									logger.warn("{}国政通数据源厂商返回申请人户籍照片不存在", new String[] { prefix});
									if(flag.equals(CHANNEL_NO)){//判断是否走无照片输出通道
										rets.clear();
										rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
										rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
									}else{
										retdata.put("xp_content", "");
										retdata.put("xp_id", "");	
										rets.clear();
										rets.put(Conts.KEY_RET_DATA, retdata);
										rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
										rets.put(Conts.KEY_RET_MSG, "采集成功!");
									}
									nciic_check.setError_mesg(POLICE_PHOTO_NO);
								}	
								nciic_check.setStatus(STATUS_CHECK_EQUAL);
								nciic_check.setCard_check(cardNo_check);
								nciic_check.setName_check(name_check);
								nciic_check.setBirth_day(birthday.getStringValue());
								nciic_check.setSex(sex.getStringValue());
								nciic_check.setPolice_addr(policeAddr.getStringValue());
								nciicCheckService.add(nciic_check);
								rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
								return rets;
							}else{
								if(compRes.indexOf("不一致")>=0){
									logObj.setBiz_code1(CODE_NOEQUAL);
									resource_tag = Conts.TAG_UNMATCH;
									cardNo_check = POLICE_STATUS_SUSSES;
									name_check = POLICE_STATUS_FAIL;
									nciic_check.setCard_check(cardNo_check);
									nciic_check.setName_check(name_check);
									nciic_check.setStatus(STATUS_CHECK_NO);
									rets.clear();
									rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
									rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
									rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
									nciicCheckService.add(nciic_check);
									return rets;
								}else{
									if(compRes.indexOf("库中无此号")>=0){
										logObj.setBiz_code1(CODE_NOEXIST);
										resource_tag = Conts.TAG_UNFOUND;
										nciic_check.setStatus(STATUS_CHECK_NULL);
										nciic_check.setError_mesg(compRes);
										rets.clear();
										rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
										rets.put(Conts.KEY_RET_MSG, compRes);
										rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
										nciicCheckService.add(nciic_check);
										return rets;
									}else{
										logObj.setBiz_code1(CODE_NOCHECK);
										rets.clear();
										rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
										rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+compRes);
										rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
										return rets;
									}
								
								}
							}					
						}
						//未查询到数据
						if("1".equals(policeDocMsgStatus)){
							logger.info("{} 国政通返回未查询到数据!", prefix);
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS.getRet_msg());
						}
						//查询失败
						if("2".equals(policeDocMsgStatus)){
							logger.info("{} 国政通返回查询失败!", prefix);
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION.getRet_msg());
						}
						
					}else{
						logger.warn("{}国政通数据源厂商返回异常! 代码:{},错误消息:{}",
								new String[] { prefix,messStat.getStringValue(),messStatDesc.getStringValue()});
						rets.clear();				
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						return rets;
					}
				}
			}else{
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setBiz_code1(CODE_EQUAL);
				resource_tag = Conts.TAG_INCACHE_MATCH;
				logger.info("{}公安查询数据,开始国政通渠道查询！", new String[] { prefix});
				Map<String,Object> getResultMap = nciicCheckService.inCached(name, enCardNo);
				String photoId = "";
				if(getResultMap.get("CARD_CHECK") != null){
					cardNo_check = getResultMap.get("CARD_CHECK").toString();
				}
				if(getResultMap.get("NAME_CHECK") != null){
					name_check  = getResultMap.get("NAME_CHECK").toString();
				}
				if(getResultMap.get("IMAGE_FILE") != null){
					photoId = getResultMap.get("IMAGE_FILE").toString();
				}
				if("不一致".equals(cardNo_check) || 
						"不一致".equals(name_check)){
					logObj.setBiz_code1(CODE_NOEQUAL);
					resource_tag = Conts.TAG_INCACHE_UNMATCH;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}				
				if(StringUtils.isNotEmpty(photoId)){
					logger.info("{} 从征信存储根据图片ID获取照片开始...", prefix);
					String photoImages = fileService.download(photoId,trade_id);// 根据ID从征信存储区下载照片
					if(StringUtil.isEmpty(photoImages)){
						logger.warn("{} 从征信存储根据图片ID获取照片失败", prefix);
					}else{
						image = photoImages;
						logger.info("{} 从征信存储根据图片ID获取照片成功,照片id为 : {}", prefix,photoId);
					}					
				}else{
					if(!flag.equals(CHANNEL_NO)){
						image="";
						photoId="";
					}					
				}					
				fileId = photoId;
				if(flag.equals(CHANNEL_NO)){
					if(image==null || "".equals(image)){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						return rets;
					}	
				}				
			}
			
			retdata.put("resultGmsfhm", cardNo_check);
			retdata.put("resultXm", name_check);
			retdata.put("xp_content", image);
			retdata.put("xp_id", fileId);
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
}
