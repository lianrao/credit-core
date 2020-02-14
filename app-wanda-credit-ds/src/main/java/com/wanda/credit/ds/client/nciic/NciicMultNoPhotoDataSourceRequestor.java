package com.wanda.credit.ds.client.nciic;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Nciic_Mult_Check_Result;
import com.wanda.credit.ds.dao.iface.INciicMultCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
@DataSourceClass(bindingDataSourceId="ds_nciic_multNoPhotojx")
public class NciicMultNoPhotoDataSourceRequestor extends BaseNciicDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(NciicMultNoPhotoDataSourceRequestor.class);
	private final  String POLICE_PHOTO_NO = "POLICE_PHOTO_NOTEXISTS";
	@Autowired
	private INciicMultCheckService nciicMultCheckService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{
			rets = new HashMap<String, Object>();
			Nciic_Mult_Check_Result nciic_multcheck = new Nciic_Mult_Check_Result();
	 		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			enCardNo = synchExecutorService.encrypt(cardNo);
			nciic_multcheck.setTrade_id(trade_id);
			nciic_multcheck.setCardno(enCardNo);
			nciic_multcheck.setName(name);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			logObj.setDs_id("ds_nciic_multNoPhotojx");
			logObj.setReq_url(nciic_address);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
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
			retdata.put("server_idx", "01");
			Nciic_Mult_Check_Result inCached  = nciicMultCheckService.inCached(name, enCardNo);
			if(inCached == null){
				logObj.setIncache("0");
				logger.info("{}公安多项数据源采集开始......", new String[] { prefix});
				String reqXML  = buildRequestBody(NCIIC_CODE_FSD,NCIIC_CODE_YWLX,cardNo,name);	
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, reqXML, new String[] { trade_id }));
				String respXML = executeMultClient(licenseMult,reqXML);
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXML, new String[] { trade_id }));
				logger.info("{}远程服务器返回消息成功!", new String[] { prefix });
				//解析返回报文
				Document rspDoc = DocumentHelper.parseText(filtRspBody(respXML));			
				Node errorCode = rspDoc.selectSingleNode("//*/ErrorCode");
				Node errorMsg = rspDoc.selectSingleNode("//*/ErrorMsg");
				if(errorCode!=null){
					logObj.setState_msg("远程数据源返回失败!");
					logObj.setBiz_code1(errorCode.getStringValue());
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{}公安多项数据源厂商返回异常! 代码:{},错误消息:{}",
							new String[] { prefix,errorCode.getStringValue(),errorMsg.getStringValue()});
					return rets;
				}else{	
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					Node errorMesg = rspDoc.selectSingleNode("//*/errormesage");
					if(errorMesg!=null){
						logger.warn("{} 公安数据源请求失败,错误原因:{}", new String[]{prefix,errorMesg.getStringValue()});
						if(errorMesg.getStringValue().indexOf("库中无此号")>=0){
							logObj.setBiz_code1(CODE_NOEXIST);
							resource_tag = Conts.TAG_UNFOUND;
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
							rets.put(Conts.KEY_RET_MSG, "申请人身份证号码校验不存在!");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							nciic_multcheck.setStatus(STATUS_CHECK_NULL);
							nciic_multcheck.setError_mesg(errorMesg.getStringValue().toString());
							nciicMultCheckService.add(nciic_multcheck);
							return rets;
						}else{
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, errorMesg.getStringValue());
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							return rets;
						}						
					}else{						
						Node nameCheck = rspDoc.selectSingleNode("//*/result_xm");
						Node cardCheck = rspDoc.selectSingleNode("//*/result_gmsfhm");						
						cardNo_check = cardCheck.getStringValue();
						name_check = nameCheck.getStringValue();						
						nciic_multcheck.setCard_check(cardNo_check);
						nciic_multcheck.setName_check(name_check);
						if(CHECK_EQUAL.equals(name_check)){
							logObj.setBiz_code1(CODE_EQUAL);
							resource_tag = Conts.TAG_MATCH;
							Node sexCheck = rspDoc.selectSingleNode("//*/xb");
							Node birdayCheck = rspDoc.selectSingleNode("//*/csrq");
							Node educationCheck = rspDoc.selectSingleNode("//*/whcd");
							Node addrCheck = rspDoc.selectSingleNode("//*/zz");
							Node cityCheck = rspDoc.selectSingleNode("//*/ssssxq");
							Node wsdnCheck = rspDoc.selectSingleNode("//*/DN");
							Node wsdesCheck = rspDoc.selectSingleNode("//*/DES");
							Node imageCheck = rspDoc.selectSingleNode("//*/xp");
							image = imageCheck.getStringValue();
							if(StringUtils.isNotEmpty(sexCheck.getStringValue())){
								retdata.put("xb", sexCheck.getStringValue());
								nciic_multcheck.setSex(sexCheck.getStringValue());
							}else{
								retdata.put("xb", "");
							}
							if(StringUtils.isNotEmpty(birdayCheck.getStringValue())){
								retdata.put("csrq", birdayCheck.getStringValue());
								nciic_multcheck.setBirth_day(birdayCheck.getStringValue());
							}else{
								retdata.put("csrq", "");
							}
							if(StringUtils.isNotEmpty(educationCheck.getStringValue())){
								retdata.put("whcd", educationCheck.getStringValue());
								nciic_multcheck.setEducation(educationCheck.getStringValue());
							}else{
								retdata.put("whcd", "");
							}
							if(StringUtils.isNotEmpty(addrCheck.getStringValue())){
								retdata.put("zz", addrCheck.getStringValue());
								nciic_multcheck.setAddress(addrCheck.getStringValue());
							}else{
								retdata.put("zz", "");
							}
							if(StringUtils.isNotEmpty(cityCheck.getStringValue())){
								retdata.put("city", cityCheck.getStringValue());
								nciic_multcheck.setCity(cityCheck.getStringValue());
							}else{
								retdata.put("city", "");
							}
							if(StringUtils.isNotEmpty(wsdnCheck.getStringValue())){
								nciic_multcheck.setWsdn(wsdnCheck.getStringValue());
							}
							if(StringUtils.isNotEmpty(wsdesCheck.getStringValue())){
								nciic_multcheck.setWsdes(wsdesCheck.getStringValue());
							}
							if(StringUtils.isNotEmpty(imageCheck.getStringValue())){
								logger.info("{} 照片上传征信存储开始...", prefix);
								String fpath = fileService.upload(image, FileType.JPG, FileArea.DS,trade_id);
								logger.info("{}照片上传存储成功,照片id为：{}", new String[] { prefix,fpath});
								nciic_multcheck.setImage_file(fpath);
								fileId = fpath;
							}else{
								image = "";
								nciic_multcheck.setError_mesg(POLICE_PHOTO_NO);
							}				
							nciic_multcheck.setStatus(STATUS_CHECK_EQUAL);
						}else{
							logObj.setBiz_code1(CODE_NOEQUAL);
							resource_tag = Conts.TAG_UNMATCH;
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
							rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							nciic_multcheck.setStatus(STATUS_CHECK_NO);
							nciicMultCheckService.add(nciic_multcheck);
							return rets;
						}												
						nciicMultCheckService.add(nciic_multcheck);
						logger.info("{}公安多项数据源采集成功", new String[] { prefix});
					}
				}
			}else{
				logObj.setIncache("1");
				logObj.setBiz_code1(CODE_EQUAL);
				resource_tag = Conts.TAG_INCACHE_MATCH;
				logger.info("{}缓存数据中存在此公安多项查询数据!", new String[] { prefix});
				cardNo_check = inCached.getCard_check();
				name_check  = inCached.getName_check();
				String photoId = inCached.getImage_file();
				String status = inCached.getStatus();
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				if(status.equals(STATUS_CHECK_EQUAL)){
					if(StringUtils.isNotEmpty(inCached.getSex())){
						retdata.put("xb", inCached.getSex().toString());
					}else{
						retdata.put("xb", "");
					}
					if(StringUtils.isNotEmpty(inCached.getBirth_day())){
						retdata.put("csrq", inCached.getBirth_day().toString());
					}else{
						retdata.put("csrq", "");
					}
					if(StringUtils.isNotEmpty(inCached.getEducation())){
						retdata.put("whcd", inCached.getEducation().toString());
					}else{
						retdata.put("whcd", "");
					}
					if(StringUtils.isNotEmpty(inCached.getAddress())){
						retdata.put("zz", inCached.getAddress().toString());
					}else{
						retdata.put("zz", "");
					}
					if(StringUtils.isNotEmpty(inCached.getCity())){
						retdata.put("city", inCached.getCity().toString());
					}else{
						retdata.put("city", "");
					}
				}
				if(status.equals(STATUS_CHECK_NULL)){
					logObj.setBiz_code1(CODE_NOEXIST);
					String errMsg = inCached.getError_mesg();
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
					rets.put(Conts.KEY_RET_MSG, errMsg);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
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
					image = photoImages;
					logger.info("{} 从征信存储根据图片ID获取照片成功,照片id为 : {}", prefix,photoId);					
				}else{
					photoId = "";
					image = "";
				}	
				fileId = photoId;			
			}			
			retdata.put("PersonId", cardNo);
			retdata.put("PersonName", name);
			retdata.put("resultGmsfhm", cardNo_check);
			retdata.put("resultXm", name_check);
			retdata.put("xp", image);
			retdata.put("xp_id", fileId);
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ex.getMessage());
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
	
	/**
	 * 构建公安请求信息
	 * @param queryType
	 * @param reportIds
	 * @param name
	 * @param cardNo
	 * @return
	 */
	private String buildRequestBody(String fsd,String ywlx,String cardNo,String name){
		StringBuffer conditionXML = new StringBuffer();
		conditionXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <ROWS><INFO><SBM>******</SBM></INFO><ROW><GMSFHM>公民身份号码</GMSFHM><XM>姓名</XM></ROW>");
		conditionXML.append("<ROW FSD=\""+fsd+"\" YWLX=\""+ywlx+"\" >");
		conditionXML.append("<GMSFHM>");
		conditionXML.append(cardNo);
		conditionXML.append("</GMSFHM>");
		conditionXML.append("<XM>");
		conditionXML.append(name);
		conditionXML.append("</XM>");
		conditionXML.append("</ROW></ROWS>");
		return conditionXML.toString();
	}
}
