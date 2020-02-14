package com.wanda.credit.ds.client.xyan;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.xyan.utils.HttpUtil;
import com.wanda.credit.ds.client.xyan.utils.RsaCodingUtil;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 新颜银行卡鉴权(2,3,4要素)
 * @author nan.liu
 * @version 1.0
 * @createdate 2019年8月29日
 * 
 */
@DataSourceClass(bindingDataSourceId = "ds_xyan_bank4")
public class XYanAuthenBankCard4Requestor extends
		BaseXYanAuthenBankCardDataSourceRequestor implements
		IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(XYanAuthenBankCard4Requestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
    protected IAllAuthCardService allAuthCardService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id;

		String member_id = propertyEngine.readById("ds_xyan_member_id");
		String terminal_id = propertyEngine.readById("ds_xyan_termid");
		String request_url = propertyEngine.readById("ds_xyan_req_url_new");
		String pfxpwd = propertyEngine.readById("ds_xyan_pfxpwd");
		String pfxname = propertyEngine.readById("ds_xyan_pfxname");

		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		logger.info("{} 银行卡鉴权交易开始", prefix);
		String resource_tag = Conts.TAG_SYS_ERROR;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setIncache("0");
		logObj.setTrade_id(trade_id);
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(request_url);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易成功");

		Map<String, Object> rets = new HashMap<String, Object>();

		Map<String, Object> paramForLog = new HashMap<String, Object>();
		try {
			/** 姓名-必填 */
			String name = (String) ParamUtil.findValue(ds.getParams_in(),"name");
			/** 銀行卡號-必填 */
			String cardId = (String) ParamUtil.findValue(ds.getParams_in(),"cardId");
			/** 身份证号码-选填 */
			String cardNo = (String) ParamUtil.findValue(ds.getParams_in(),"cardNo");
			/** 手机号码-选填 */
			String phone = (String) ParamUtil.findValue(ds.getParams_in(),"phone");

			/** 构建请求参数 */
			JSONObject po = new JSONObject();
			po.put("member_id", member_id);// 配置参数
			po.put("terminal_id", terminal_id);// 配置参数
			po.put("verify_element", "1234");// ds入参 必填 校验类型 2、3、4要素
			po.put("id_holder", name);// ds入参 必填
			po.put("acc_no", cardId);// ds入参 必填
			po.put("id_card", cardNo);// ds入参 不必填
			po.put("mobile", phone);// ds入参 不必填

			paramForLog.put("name", name);
			paramForLog.put("cardId", cardId);
			paramForLog.put("cardNo", cardNo);
			paramForLog.put("phone", phone);

			if (!StringUtil.isEmpty(cardNo)
					&& StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不符合规范");
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logger.error("{}  {}", prefix, logObj.getState_msg());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				return rets;
			}

			po.put("trans_id", trade_id);
			po.put("trade_date",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			po.put("product_type", "0");
			po.put("industry_type", "C1");

			String base64str = com.wanda.credit.ds.client.xyan.utils.SecurityUtil
					.Base64Encode(po.toString());
			/** rsa加密 **/
			String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str,
					cer_file_base_path + pfxname, pfxpwd);// 加密数据

			Map<String, String> HeadPostParam = new HashMap<String, String>();
			HeadPostParam.put("member_id", member_id);
			HeadPostParam.put("terminal_id", terminal_id);
			HeadPostParam.put("data_type", "json");
			HeadPostParam.put("data_content", data_content);
			
			logger.info("{} 开始请求远程服务器... ", prefix);
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			String postString = HttpUtil.RequestForm(request_url, HeadPostParam);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logger.info("{} 请求返回:{}", prefix, postString);
			JSONObject result_obj = JSONObject.parseObject(postString);
            logObj.setBiz_code1(result_obj.get("errorCode") + "-" + result_obj.get("errorMsg"));
            retdata.putAll(visitBusiData(trade_id,result_obj));
            if(result_obj.getBoolean("success")){
            	JSONObject data = result_obj.getJSONObject("data");
            	String respCode = data.getString("code");
            	if("0".equals(respCode)){
    				resource_tag = Conts.TAG_TST_SUCCESS;
    				logger.info("{} 认证一致",trade_id);
    				String req_values = cardNo+"_"+cardId+ "_" + phone;
					allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, GladDESUtils.encrypt(cardNo), 
							GladDESUtils.encrypt(cardId), GladDESUtils.encrypt(phone),
							retdata, req_values);
    			}else if("1".equals(respCode)){
    				resource_tag = Conts.TAG_TST_SUCCESS;
    			}else if("3".equals(respCode)){
    				if("0022".equals(data.getString("org_code"))){
    					rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:请求频繁,验证次数超限");
    					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
    					rets.put(Conts.KEY_RET_TAG, new String[] { Conts.TAG_UNFOUND });
    					logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
    					return rets;
    				}
    			}
            	retdata.put("name", name);
                retdata.put("cardNo", cardNo);
                retdata.put("cardId", cardId);
                retdata.put("phone", phone);
                
    			rets.put(Conts.KEY_RET_DATA, retdata);
    			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
    			rets.put(Conts.KEY_RET_MSG, "采集成功!");
    			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
            }else{
				logger.info("{} 新颜交易失败:{}", prefix,result_obj.getString("errorMsg"));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "查询失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
            
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				resource_tag = Conts.TAG_SYS_TIMEOUT;
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常:{}",prefix,ExceptionUtil.getTrace(ex));
		} finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, paramForLog, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}

}
