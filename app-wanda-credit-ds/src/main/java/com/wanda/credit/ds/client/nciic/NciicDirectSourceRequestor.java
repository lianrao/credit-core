package com.wanda.credit.ds.client.nciic;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.sql.Timestamp;

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
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
@DataSourceClass(bindingDataSourceId="ds_nciic_jxDirect")
public class NciicDirectSourceRequestor extends BaseNciicDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(NciicDirectSourceRequestor.class);
	private final  String SOURCE_ID = "01";
	private final  String POLICE_PHOTO_NO = "POLICE_PHOTO_NOTEXISTS";
	@Autowired
	private INciicCheckService nciicCheckService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}公安数据源查询开始......", new String[] { prefix});
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{
			Nciic_Check_Result nciic_check = new Nciic_Check_Result();
			rets = new HashMap<String, Object>();
	 		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			enCardNo = synchExecutorService.encrypt(cardNo);
			nciic_check.setTrade_id(trade_id);
			nciic_check.setCardno(enCardNo);
			nciic_check.setName(name);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			logObj.setDs_id("ds_nciic_jx");
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

			logObj.setIncache("0");
			logger.info("{}公安简项数据源采集开始......", new String[] { prefix});
			String reqXML  = buildRequestBody(NCIIC_CODE_FSD,NCIIC_CODE_YWLX,cardNo,name);		
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, reqXML, new String[] { trade_id }));
			String respXML = executeClient(license,reqXML);
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXML, new String[] { trade_id }));
			logger.info("{}远程服务器返回消息成功！", new String[] { prefix });
			
			//解析返回报文
			Document rspDoc = DocumentHelper.parseText(filtRspBody(respXML));			
			Node errorCode = rspDoc.selectSingleNode("//*/ErrorCode");
			Node errorMsg = rspDoc.selectSingleNode("//*/ErrorMsg");
			if(errorCode!=null){
				logObj.setState_msg("远程数据源返回失败!");
				logObj.setBiz_code1(errorCode.getStringValue());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安数据源厂商返回异常! 代码:{},错误消息:{}",
						new String[] { prefix,errorCode.getStringValue(),errorMsg.getStringValue()});
				return rets;
			}else{
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				nciic_check.setSourceid(SOURCE_ID);
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
						nciic_check.setStatus(STATUS_CHECK_NULL);
						nciic_check.setError_mesg(errorMesg.getStringValue().toString());
						nciicCheckService.add(nciic_check);
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
					nciic_check.setCard_check(cardNo_check);
					nciic_check.setName_check(name_check);
					if(CHECK_EQUAL.equals(name_check)){							
						logObj.setBiz_code1(CODE_EQUAL);
						resource_tag = Conts.TAG_MATCH;
						Node imageCheck = rspDoc.selectSingleNode("//*/xp");
						image = imageCheck.getStringValue();
						if(StringUtils.isNotEmpty(image)){
							logger.info("{} 照片上传征信存储开始...", prefix);
							String fpath = fileService.upload(image, FileType.JPG, FileArea.DS,trade_id);
							logger.info("{}照片上传存储成功,照片id为：{}", new String[] { prefix,fpath});
							nciic_check.setImage_file(fpath);
							fileId = fpath;
						}							
						nciic_check.setStatus(STATUS_CHECK_EQUAL);
					}else{
						logObj.setBiz_code1(CODE_NOEQUAL);
						resource_tag = Conts.TAG_UNMATCH;
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						nciic_check.setStatus(STATUS_CHECK_NO);
						nciicCheckService.add(nciic_check);
						return rets;
					}
					if(image==null || "".equals(image)){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						nciic_check.setError_mesg(POLICE_PHOTO_NO);
						nciicCheckService.add(nciic_check);
						return rets;
					}												
					nciicCheckService.add(nciic_check);
					logger.info("{}公安数据源采集成功", new String[] { prefix});
				}
			}
			
			retdata.put("resultGmsfhm", cardNo_check);
			retdata.put("resultXm", name_check);
			retdata.put("xp_content", image);
			retdata.put("xp_id", fileId);
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());		
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
