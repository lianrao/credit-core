package com.wanda.credit.ds.client.aijin;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.JSONUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;
@DataSourceClass(bindingDataSourceId="ds_aijin_jx")
public class AiJinDataSourceRequestor extends BaseAijinDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(AiJinDataSourceRequestor.class);
	private final  String POLICE_STATUS_SUSSES = "一致";
	private final  String POLICE_STATUS_FAIL = "不一致";
	private final  String STATUS_CHECK_EQUAL = "00";
	private final  String STATUS_CHECK_NO = "01";
	private final  String STATUS_CHECK_NULL = "02";
	private final  String SOURCE_ID = "02";
	@Autowired
	private INciicCheckService nciicCheckService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		int incache_days = Integer.valueOf(propertyEngine.readById("ds_police_incacheTime"));//公安数据(天)
		String yuanjin_url = propertyEngine.readById("ds_yuanjin_police_check_url");//爰金调用连接
		String yuanjin_account = propertyEngine.readById("ds_yuanjin_police_account");
		String yuanjin_privateKey = propertyEngine.readById("ds_yuanjin_police_privateKey");
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String flag = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //是否带照片,01带照片,02不带照片
			String acct_id = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();//账户号

			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			enCardNo = GladDESUtils.encrypt(cardNo);
			logger.info("{} 爱金简项数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(yuanjin_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			
			String cardNo_check = null;
			String name_check = null;
			retdata.put("server_idx", "04");
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				boolean ds_incache = dsIncache(acct_id,ds.getId());
				if(!nciicCheckService.inCachedCount(name, enCardNo,incache_days) || ds_incache){
					logObj.setIncache("0");
					if("03".equals(flag)){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_LOCAL_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "申请人本地未查得");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						return rets;
					}					
					logger.info("{} 爱金简项数据源采集开始......", prefix);
					logger.info("{} 爱金简项调用连接:{}", prefix,yuanjin_url);
					String sign = md5(md5(cardNo + yuanjin_account) + yuanjin_privateKey);
					
					String post_data = "idNumber=" + cardNo+ "&name=" +name+ "&account=" + yuanjin_account + "&sign=" + sign;
					String json = postHtml(yuanjin_url, post_data);

					logger.info("{}远程服务器返回消息成功:{}", prefix,json);
					if(json != null){
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						JSONObject jsondata = JSONObject.fromObject(json);
						String responseCode = jsondata.get("ResponseCode").toString();	
						String responseText = jsondata.get("ResponseText").toString();
						logObj.setBiz_code1(responseCode);
						logObj.setState_msg(responseText);
						logger.info("{} 爰金返回信息码:{},返回信息:{}", prefix,responseCode,responseText);
						Nciic_Check_Result nciic_check = new Nciic_Check_Result();
						nciic_check.setTrade_id(trade_id);
						nciic_check.setCardno(enCardNo);
						nciic_check.setName(name);
						nciic_check.setSourceid(SOURCE_ID);
						nciic_check.setStatus(STATUS_CHECK_NULL);
						if(responseCode.equals("100")){
							JSONObject result = (JSONObject) JSONUtil.getJsonValueByKey(jsondata, "Identifier", false);
							String resultCheck = result.get("Result").toString();
							if(resultCheck.equals(POLICE_STATUS_SUSSES)){
								
								logObj.setBiz_code1(CODE_EQUAL);
								resource_tag = Conts.TAG_MATCH;
								cardNo_check = POLICE_STATUS_SUSSES;
								name_check = POLICE_STATUS_SUSSES;
								nciic_check.setCard_check(cardNo_check);
								nciic_check.setName_check(name_check);
								nciic_check.setStatus(STATUS_CHECK_EQUAL);
								retdata.put("resultGmsfhm", cardNo_check);
								retdata.put("resultXm", name_check);
								retdata.put("xp_content", "");
								
								nciicCheckService.add(nciic_check);
								rets.put(Conts.KEY_RET_DATA, retdata);
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
								rets.put(Conts.KEY_RET_MSG, "采集成功!");
								rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
								return rets;
							}
							if(resultCheck.equals(POLICE_STATUS_FAIL)){
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
							}
							if(resultCheck.indexOf("库中无此号") >= 0){
								logObj.setBiz_code1(CODE_NOEXIST);
								resource_tag = Conts.TAG_UNFOUND;
								nciic_check.setStatus(STATUS_CHECK_NULL);
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
								rets.put(Conts.KEY_RET_MSG, "申请人身份证号码校验不存在");
								rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
								nciic_check.setError_mesg(resultCheck);
								nciicCheckService.add(nciic_check);
								return rets;
							}
						}else{
							if(responseCode.length()<200){
								logObj.setBiz_code1(responseCode);
							}						
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回异常");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							logger.warn("{}爱金公安数据源厂商返回异常! 代码:{},错误消息:{}",prefix,responseCode,responseText);
							return rets;
						}
						
					}				
				}else{
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setIncache("1");
					logObj.setBiz_code1(CODE_EQUAL);
					resource_tag = Conts.TAG_INCACHE_MATCH;
					logger.info("{} 查询公安数据开始了", prefix);
					Map<String,Object> getResultMap = nciicCheckService.inCached(name, enCardNo);
					if(getResultMap.get("CARD_CHECK") != null){
						cardNo_check = getResultMap.get("CARD_CHECK").toString();
					}
					if(getResultMap.get("NAME_CHECK") != null){
						name_check  = getResultMap.get("NAME_CHECK").toString();
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
				}
			}		
			retdata.put("resultGmsfhm", cardNo_check);
			retdata.put("resultXm", name_check);
			retdata.put("xp_content", "");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
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
