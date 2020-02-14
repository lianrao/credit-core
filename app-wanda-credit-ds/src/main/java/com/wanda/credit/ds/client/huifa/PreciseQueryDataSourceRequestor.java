package com.wanda.credit.ds.client.huifa;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.huifa.PreciseQueryInputInfo;
import com.wanda.credit.ds.dao.iface.huifa.inter.IInputInfoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @Title: 外部数据源
 * @Description: 汇法网个人、企业精确查询
 * @author wenpeng.li@99bill.com
 * @date 2015年11月19日 上午11:12:36
 * @version V1.0
 */
@DataSourceClass(bindingDataSourceId="ds_huifaExactQuery")
public class PreciseQueryDataSourceRequestor extends BaseHuifaDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(PreciseQueryDataSourceRequestor.class);
	private String curl;
	private String burl;
	
	@Autowired
	public IPropertyEngine propertyEngine;

	@Autowired
	private IInputInfoService inputInfoService;
	
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String msg=null;
		List<String> tags = new ArrayList<String>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		Map<String, Object> paramIn=new HashMap<String, Object>();
		logObj.setIncache("0");	
		try{
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			rets = new HashMap<String, Object>();
			Map<String, Object> respMap = null;
			Map<String, String> params = new HashMap<String, String>();
			String name = (String)ParamUtil.findValue(ds.getParams_in(), "name");
			String idCard = (String)ParamUtil.findValue(ds.getParams_in(), "idCard");
			String crptedIdCard = synchExecutorService.encrypt(idCard);

			String currentPage = (String)ParamUtil.findValue(ds.getParams_in(), "currentPage");
			String pageSize = (String)ParamUtil.findValue(ds.getParams_in(), "pageSize");
			String sourcet = (String)ParamUtil.findValue(ds.getParams_in(), "sourcet");
			String flag = (String)ParamUtil.findValue(ds.getParams_in(), "flag");
			String acct_id = (String)ParamUtil.findValue(ds.getParams_in(), "acct_id"); //用户id
			String ec_name=URLEncoder.encode(name, "UTF-8");
			boolean flag_c = "c".equalsIgnoreCase(flag);
			String url = flag_c ? propertyEngine.readById("hf_req_prequ_curl") : propertyEngine.readById("hf_req_prequ_burl");
			msg = flag_c ? "个人" : "企业";
			logObj.setReq_url(url);
			params.put("n", ec_name);
			params.put("id", crptedIdCard);
			params.put("pg", currentPage);
			params.put("pz", pageSize);
			params.put("sourcet", sourcet);
			paramIn.putAll(params);
			paramIn.put("n", name);
			paramIn.put("flag", flag);
			if(flag_c && StringUtils.isNotEmpty(CardNoValidator.validate(idCard))){
				logObj.setIncache("0");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不符合规范");
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logger.error("{} {}",prefix,logObj.getState_msg());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				tags.add(Conts.TAG_SYS_ERROR);
				return rets;
			}
			logger.info("{} 开始发送"+msg+"精确查询...", prefix,flag);
			params.put("id", idCard);
			String content = null;
			logObj.setIncache("0");
//			String content = RequestHelper.doGet(url, params, true,getRequestConfig());
			if("single".equals(propertyEngine.readById("hf_req_prequ_type"))){
				content = RequestHelper.doSingleGet(url, params,getRequestConfig(),true);
			}else{
				content = RequestHelper.doCustomedGet(url, params,getRequestConfig(),true);
			}
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			if(content!=null&&!"".equals(content)){
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, content, new String[] { trade_id }));
				respMap = new ObjectMapper().readValue(content, Map.class);
			}
			List<Map<String, Object>> listInfo=null;
			String success = null;
			String message = null;
			if(respMap!=null&&respMap.size()>0){
				success = respMap.get("success")==null?null:(String)respMap.get("success");
				message = respMap.get("message")==null?null:(String)respMap.get("message");
			    listInfo =  respMap.get("models")==null?null:(List<Map<String, Object>>)respMap.get("models");
			}
			PreciseQueryInputInfo  inputInfo = new PreciseQueryInputInfo(trade_id, URLDecoder.decode(ec_name, "UTF-8"), crptedIdCard, 
					"".equals(currentPage)==true?0:Integer.parseInt(currentPage), 
					"".equals(pageSize)==true?0:Integer.parseInt(pageSize), 
					sourcet, success, message);
			inputInfoService.write(inputInfo); 
			//wuchsh add 针对同一个tradeid 请求多次的情况 企业征信报告
			String sql = "SELECT max(ID) as id FROM T_DS_HF_INPUT_INFO WHERE TRADE_ID=?";
			String refid = daoService.getJdbcTemplate().queryForObject(sql, new Object[]{trade_id}, String.class);
			save(success, message, trade_id, refid, listInfo, respMap, rets,logObj,tags,acct_id);
			if(StringUtils.isBlank(logObj.getState_code())){
			   logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			}
		} catch (Exception ex) {
			rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_DS_HUIFA1_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG,msg+"精确查询异常! 详细信息:" + ex.getMessage());
			logger.error(prefix +" "+msg+"精确查询返回异常",ex);
			if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				tags.add(Conts.TAG_SYS_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg(msg+"精确查询异常! 详细信息:" + ex.getMessage());
				tags.add(Conts.TAG_SYS_ERROR);
			}
			
		}finally{
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setTag(StringUtils.join(tags, ";"));
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);	
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn,logObj);
		}
		return rets;
	}

	private RequestConfig getRequestConfig() {
		String timeoutValue = propertyEngine.readById("hf_req_prequ_timeout");
		if(StringUtils.isBlank(timeoutValue))return null;
		else{
			int timeout = Integer.parseInt(timeoutValue);
			return RequestConfig.custom()
					.setSocketTimeout(timeout).setConnectTimeout(timeout).
					setStaleConnectionCheckEnabled(true).build();	
		}
	}
	
	/**
	 * 验证入参方法
	 */
	public Map<String, Object> valid(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
		rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
		try {
			Object flag = ParamUtil.findValue(ds.getParams_in(), "flag");
			if(ds!=null && ds.getParams_in()!=null){
				for(String paramId : paramIds){
					if ("b".equals(flag) && ("name".equals(paramId) || "idCard".equals(paramId))) {
						continue;
					}
					Object param = ParamUtil.findValue(ds.getParams_in(), paramId);
					if (param == null) {
						return rets;
					}
					if (("name".equals(paramId) || "idCard".equals(paramId)) && StringUtils.isBlank((String) param)) {
						return rets;
					}
				}
				Object name = ParamUtil.findValue(ds.getParams_in(), "name");
				Object idCard = ParamUtil.findValue(ds.getParams_in(), "idCard");
				if (StringUtils.isBlank((String) name) && StringUtils.isBlank((String) idCard)) {
					return rets;
				} else if (!"b".equals(flag) && !"c".equals(flag)) {
					return rets;
				}
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "数据源参数校验通过!");
			}
		} catch (Exception ex) {
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}", prefix, ex.getMessage());
			ex.printStackTrace();
		}
		return rets;
	}

	public String getCurl() {
		return curl;
	}

	public void setCurl(String curl) {
		this.curl = curl;
	}

	public String getBurl() {
		return burl;
	}

	public void setBurl(String burl) {
		this.burl = burl;
	}

	public PreciseQueryDataSourceRequestor() {
		super();
	}
	
}
