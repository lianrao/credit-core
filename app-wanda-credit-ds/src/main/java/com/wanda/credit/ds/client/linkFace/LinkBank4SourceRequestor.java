package com.wanda.credit.ds.client.linkFace;

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
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;

/**
 * 描述: 银行卡三要素验证
 */
@DataSourceClass(bindingDataSourceId = "ds_linkface_bank4")
public class LinkBank4SourceRequestor extends BaseLinkFaceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(LinkBank4SourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
    protected IAllAuthCardService allAuthCardService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} linkface银行卡四要素请求开始...", prefix);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		String link_url = propertyEngine.readById("ds_linkface_url");// 调用连接
		String link_bank4_api = propertyEngine.readById("ds_linkface_bank4_api");// api
		String link_api_id = propertyEngine.readById("ds_linkface_api_id");// id
		String link_api_secret = propertyEngine.readById("ds_linkface_api_secret");// key
		String bank4_url = link_url+link_bank4_api;
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			String name = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[0])); 
			String cardNo = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[1]));
			String cardId = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[2]));
			String phone = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[3]));
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();
			logger.info("{} linkface银行卡四要素服务加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("cardId", cardId);
			reqparam.put("phone", phone);
			logObj.setReq_url(bank4_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

			if (StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))) {
				logger.warn("{}入参格式不符合要求!", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				return rets;
			} else {
				logObj.setIncache("0");
				Map<String, String> params = new HashMap<String,String>();
				Map<String, String> headers = new HashMap<String,String>();
				params.put("api_id", link_api_id);
				params.put("api_secret", link_api_secret);
				params.put("name", name);
				params.put("id_number", cardNo);
				params.put("card_number", cardId);
				params.put("phone_number", phone);
				logger.info("{} linkface银行卡四要素请求开始...", prefix);
				 String res = RequestHelper.doPost(bank4_url,null,headers,params,null,false, 10000);
				logger.info("{} linkface银行卡四要素请求结束,返回信息:{}", prefix, res);

				if (StringUtil.isEmpty(res)) {
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败");
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					logger.error("{} 银行卡鉴权失败,返回结果为空", trade_id);
					return rets;
				}
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				JSONObject resJson = JSONObject.fromObject(res);
				String status = resJson.getString("status");
				logObj.setBiz_code1(resJson.getString("request_id"));
				if("OK".equals(status)){
					logger.info("{} linkface银行卡四要素请求成功", prefix);
					String result = resJson.getString("result");
					retdata.put("name", name);
					retdata.put("cardNo", cardNo);
					retdata.put("cardId", cardId);
					retdata.put("phone", phone);
					resource_tag = bulidResp(trade_id,result,retdata,rets);
					String req_values = cardNo+"_"+cardId+ "_" + phone;
					allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, GladDESUtils.encrypt(cardNo), 
							GladDESUtils.encrypt(cardId), GladDESUtils.encrypt(phone),retdata, req_values);
				}else if("INVALID_ARGUMENT".equals(status)){
					logger.info("{} 传入参数格式有误", trade_id);
					resource_tag = Conts.TAG_SYS_ERROR;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
					rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else{
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败");
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					logger.error("{} 银行卡鉴权失败,返回结果为空", trade_id);
					return rets;
				}
			}
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		} finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}
}
