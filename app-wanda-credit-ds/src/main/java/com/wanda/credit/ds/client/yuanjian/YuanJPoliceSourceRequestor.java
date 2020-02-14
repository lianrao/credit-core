package com.wanda.credit.ds.client.yuanjian;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.yuanjian.beans.YuanJian_Face;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.domain.yuanjian.YJ_FaceScore_Result;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
import com.wanda.credit.ds.dao.iface.yuanjian.IYuanJianFaceScoreService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_yuanjian_jx")
public class YuanJPoliceSourceRequestor extends BaseYuanJSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(YuanJPoliceSourceRequestor.class);
	@Autowired
	private IYuanJianFaceScoreService yuanjianService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private INciicCheckService nciicCheckService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 远鉴身份认证数据源调用开始...", prefix);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String yuanjian_url = propertyEngine.readById("ds_yuanjian_police_url");//远鉴调用连接
		String member_id = propertyEngine.readById("ds_yuanjian_member_id");//member_id
		String terminal_id = propertyEngine.readById("ds_yuanjian_terminal_id");//terminal_id
		int incache_days = Integer.valueOf(propertyEngine.readById("ds_police_incacheTime"));
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString().toUpperCase();
			String acct_id = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//账户号
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			enCardNo = synchExecutorService.encrypt(cardNo);
			logger.info("{} 远鉴人脸比对数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(yuanjian_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

			retdata.put("server_idx", "07");
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}		
			boolean ds_incache = dsIncache(acct_id,ds.getId());
			if(!nciicCheckService.inCachedCountJuHe(name, enCardNo,incache_days) || ds_incache){
				logObj.setIncache("0");
				String res = doYuanJianPolice(yuanjian_url,member_id,terminal_id,trade_id,
						cardNo,name,time_out);
				if(StringUtil.isEmpty(res)){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "身份认证失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
					logger.error("{} 身份认证失败,返回结果为空", trade_id);
					return rets;
				}
				YuanJian_Face yj_face = com.alibaba.fastjson.JSONObject.parseObject(res,YuanJian_Face.class);
				saveFaceResult(trade_id,name,enCardNo,yj_face);
				if(!StringUtil.isEmpty(yj_face.getErrorCode()) || !"true".equals(yj_face.getSuccess())){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "身份认证失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
					logger.error("{} 远鉴调用失败,返回错误信息为:{}", trade_id,yj_face.getErrorMsg());
					return rets;
				}
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				Nciic_Check_Result nciic = new Nciic_Check_Result();
				nciic.setTrade_id(trade_id);
				nciic.setCardno(enCardNo);
				nciic.setName(name);
				nciic.setSourceid("07");
				if("0".equals(yj_face.getData().getCode())){
					resource_tag = Conts.TAG_MATCH;
					nciic.setCard_check("一致");
					nciic.setName_check("一致");
					nciic.setStatus("00");
					retdata.put("resultGmsfhm", "一致");
					retdata.put("resultXm", "一致");
					retdata.put("xp_content", "");
					retdata.put("xp_id", "");
					rets.clear();
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "采集成功!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("1".equals(yj_face.getData().getCode())){
					resource_tag = Conts.TAG_UNMATCH;
					nciic.setCard_check("一致");
					nciic.setName_check("不一致");
					nciic.setStatus("01");
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("2".equals(yj_face.getData().getCode())){
					resource_tag = Conts.TAG_UNFOUND;
					nciic.setStatus("03");
					nciic.setError_mesg("库中无此号");
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
					rets.put(Conts.KEY_RET_MSG, "库中无此号");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});				
				}else{
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "身份认证失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
					logger.error("{} 远鉴调用失败", trade_id);
				}
				nciicCheckService.add(nciic);
			}else{
				String cardNo_check = "";
				String name_check = "";
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setIncache("1");
				resource_tag = Conts.TAG_INCACHE_MATCH;
				logger.info("{}缓存数据中存在此公安查询数据!", prefix);
				Map<String,Object> getResultMap = nciicCheckService.inCached(name, enCardNo);
				if(getResultMap.get("CARD_CHECK") != null){
					cardNo_check = getResultMap.get("CARD_CHECK").toString();
				}
				if(getResultMap.get("NAME_CHECK") != null){
					name_check  = getResultMap.get("NAME_CHECK").toString();
				}
				if("不一致".equals(cardNo_check) || 
						"不一致".equals(name_check)){
					resource_tag = Conts.TAG_INCACHE_UNMATCH;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
				retdata.put("resultGmsfhm", cardNo_check);
				retdata.put("resultXm", name_check);
				retdata.put("xp_content", "");
				retdata.put("xp_id", "");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "采集成功!");
			}			
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
	public void saveFaceResult(String trade_id,String name,String enCardNo,YuanJian_Face yj_face){
		YJ_FaceScore_Result yuanj_vo = new YJ_FaceScore_Result();
		yuanj_vo.setTrade_id(trade_id);
		yuanj_vo.setCardNo(enCardNo);
		yuanj_vo.setName(name);
		yuanj_vo.setSuccess(yj_face.getSuccess());
		if(!StringUtil.isEmpty(yj_face.getErrorCode())){
			yuanj_vo.setError_code(yj_face.getErrorCode());
			yuanj_vo.setError_msg(yj_face.getErrorMsg());
		}else{
			yuanj_vo.setCode(yj_face.getData().getCode());
			yuanj_vo.setDescription(yj_face.getData().getDesc());
			yuanj_vo.setFee(yj_face.getData().getFee());
			yuanj_vo.setTrade_no(yj_face.getData().getTrade_no());
			yuanj_vo.setTrans_id(yj_face.getData().getTrans_id());
		}
		yuanjianService.save(yuanj_vo);
	}
}
