package com.wanda.credit.ds.client.guoztCredit;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
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
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ModelUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Guozt_Roll_check_result;
import com.wanda.credit.ds.dao.iface.IGuoZTRollService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_gzt_jyqlzsCheck")
public class GuoZTSchoolRollRequestor extends BaseGuoZTDataSourcesRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(GuoZTSchoolRollRequestor.class);
	@Autowired
	private IGuoZTRollService rollService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	private static final String STATUS_SUCCESS = "0";//接口调用成功
	private static final String CODE_NOEVAL = "0";//信息有误，无法评估
	private static final String CODE_EVAL_FAIL = "2";//信息无误，评估异常
	private static final String CODE_EVAL_SUSS = "1";//评估成功
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> retdata =  new HashMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(guoztUrl);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		String initTag = Conts.TAG_TST_FAIL;
		try {
			String roll_status = ParamUtil.findValue(ds.getParams_in(), "roll_status").toString(); //学籍状态 
			String name = ParamUtil.findValue(ds.getParams_in(), "name").toString(); //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString(); //身份证号码
			String college = ParamUtil.findValue(ds.getParams_in(), "college").toString(); //学院名称
			String roll_flag = ParamUtil.findValue(ds.getParams_in(), "roll_flag").toString(); //01不忽略报错,02忽略
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logObj.setIncache("0");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不符合规范");
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logger.error("{} {}",prefix,logObj.getState_msg());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
				return rets;
			}
			String degree = ""; //学历层次
			String studyType = ""; //学历类别
			String startYear = ""; //入学年份
			String specialty = ""; //专业
			String degree_desc = ""; //学历层次
			if(ParamUtil.findValue(ds.getParams_in(), "degree")!=null){
				degree = ParamUtil.findValue(ds.getParams_in(), "degree").toString();
			}
			if(ParamUtil.findValue(ds.getParams_in(), "studyType")!=null){
				studyType = ParamUtil.findValue(ds.getParams_in(), "studyType").toString();
			}
			if(ParamUtil.findValue(ds.getParams_in(), "startYear")!=null){
				startYear = ParamUtil.findValue(ds.getParams_in(), "startYear").toString();
				if(!isDateStr(startYear, "yyyy") && !StringUtil.isEmpty(startYear)){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
					rets.put(Conts.KEY_RET_MSG, "传入参数不正确");
					rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
					logObj.setState_msg("传入参数日期格式不正确");
					return rets;
				}
			}
			if(ParamUtil.findValue(ds.getParams_in(), "specialty")!=null){
				specialty = ParamUtil.findValue(ds.getParams_in(), "specialty").toString();
			}
			Map<String, Object> model_param = new HashMap<String, Object>();
			if(StringUtils.isNotEmpty(degree)){
				model_param.put("DEGREE", degree);
			}else{
				model_param.put("DEGREE", "0");
			}			
			model_param = ModelUtils.calculate("M_credit_rollModel", ParamUtil.convertParams(model_param),false);
			if(model_param!=null){				
				if(!"0".equals(extractValueFromResult("DEGREE_DESC",model_param).toString())){
					degree_desc = extractValueFromResult("DEGREE_DESC",model_param).toString();
				}
//				logger.info("{} 查询模型获得数据:{}", new String[] { prefix,extractValueFromResult("DEGREE_DESC",model_param).toString() });
			}
			String params = roll_status+","+name + "," + cardNo+","+college+","+degree_desc+","+studyType+","+startYear+","+specialty+",,"+trade_id;
			String enCardNo = synchExecutorService.encrypt(cardNo);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("roll_status", roll_status);
			reqparam.put("college", college);
			reqparam.put("degree", degree);
			reqparam.put("studyType", studyType);
			reqparam.put("startYear", startYear);
			reqparam.put("specialty", specialty);
			logObj.setIncache("0");
			logger.info("{}国政通就业潜力数据源采集开始......", new String[] { prefix });				
			String respXml = singQurey(params, prefix,guoztUrl);
//			logger.info("{}国政通就业潜力数据源返回数据：{}", new String[] { prefix,respXml });
			if (respXml == null || respXml.length() == 0) {
				logger.error("{} 国政通就业潜力数据源查询返回异常！", prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_GUOZT_JYQL_FAIL);
				rets.put(Conts.KEY_RET_MSG, "就业潜力查询失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
				logObj.setState_msg("国政通就业潜力查询返回异常");
				return rets;
			}
			//解析返回报文 
			Document rspDoc = DocumentHelper.parseText(filtRspBody(respXml));
			Node repDocMsg = rspDoc.selectSingleNode("//data/message");
			Node messStat = repDocMsg.selectSingleNode("status");
			Node messStatDesc = repDocMsg.selectSingleNode("value");
			if (STATUS_SUCCESS.equals(messStat.getStringValue())) {
				logger.info("{}国政通就业潜力数据源采集成功,开始数据解析...", new String[] { prefix });	
				initTag = Conts.TAG_TST_SUCCESS;
				Node jyqlzsCheckNode = rspDoc.selectSingleNode("//data/jyqlzsChecks/jyqlzsCheck");
				Guozt_Roll_check_result roll_result = getRollBean(jyqlzsCheckNode);
				roll_result.setTrade_id(trade_id);
				roll_result.setName(name);
				roll_result.setInputzt(roll_status);
				roll_result.setInputYxmc(college);
				String result_code = roll_result.getCode();
				roll_result.setCardno(enCardNo);				
				rollService.add(roll_result);
				rets.clear();
				if(CODE_EVAL_SUSS.equals(result_code)){	
					String collage_level = rollService.findScore(college);
					if("00".equals(collage_level)){
						retdata.put("assAdvise", "");
					}else{
						retdata.put("assAdvise", collage_level);
					}
					retdata.put("name", name);
					retdata.put("cardNo", cardNo);
					retdata.put("college", college);
					retdata.put("degree", degree);
					retdata.put("studyType", studyType);
					retdata.put("startYear", startYear);
					retdata.put("specialty", specialty);
					retdata.put("checkResult", "0");
					rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "采集成功!");
				}else if(CODE_NOEVAL.equals(result_code)){
					if("01".equals(roll_flag)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_GUOZT_JYQL_EVAL01);
						rets.put(Conts.KEY_RET_MSG, "信息有误,无法评估!");
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
					}else{
						retdata.put("name", "");
						retdata.put("cardNo", "");
						retdata.put("college", "");
						retdata.put("degree", "");
						retdata.put("studyType", "");
						retdata.put("startYear", "");
						retdata.put("specialty", "");
						retdata.put("assAdvise", "");	
						retdata.put("checkResult", "1");
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
						rets.put(Conts.KEY_RET_DATA, retdata);
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "采集成功!");
					}					
				}else if(CODE_EVAL_FAIL.equals(result_code)){
					if("01".equals(roll_flag)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_GUOZT_JYQL_EVAL02);
						rets.put(Conts.KEY_RET_MSG, "信息无误,评估异常!");
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
					}else{
						retdata.put("name", "");
						retdata.put("cardNo", "");
						retdata.put("college", "");
						retdata.put("degree", "");
						retdata.put("studyType", "");
						retdata.put("startYear", "");
						retdata.put("specialty", "");
						retdata.put("assAdvise", "");	
						retdata.put("checkResult", "1");
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
						rets.put(Conts.KEY_RET_DATA, retdata);
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "采集成功!");
					}				
				}else{
					initTag = Conts.TAG_TST_FAIL;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_GUOZT_JYQL_FAIL);
					rets.put(Conts.KEY_RET_MSG, "就业潜力查询失败!");
					rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
				}				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			} else {
				logger.info("{} 国政通就业潜力数据源调用失败:{}", new String[] { prefix, messStatDesc.getStringValue() });
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_GUOZT_JYQL_FAIL);
				rets.put(Conts.KEY_RET_MSG, "就业潜力查询失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
				logObj.setState_msg(messStatDesc.getStringValue());
				logObj.setBiz_code1(messStat.getStringValue());
				return rets;
			}			
		} catch (Exception ex) {
			initTag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				initTag = Conts.TAG_SYS_TIMEOUT;
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
		} finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(initTag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}		
		return rets;
	}
	/**
	 * 保存就业潜力信息
	 * @date 2017年1月19日 下午1:49:27
	 * @author nan.liu
	 */
	private Guozt_Roll_check_result getRollBean(Node jyqlzsCheckNode) {
		// TODO Auto-generated method stub
		XStream stream = new XStream(new DomDriver());
		stream.registerConverter(new WsDateConverter("yyyy-MM-dd", new String[] { "yyyyMMdd", "yyyy" }));
		stream.registerConverter(new WsIntConverter());
		stream.registerConverter(new WsDoubleConverter());
		stream.alias("jyqlzsCheck", Guozt_Roll_check_result.class);
		Guozt_Roll_check_result degrees = (Guozt_Roll_check_result) stream.fromXML(jyqlzsCheckNode.asXML());
		return degrees;
	}
}
