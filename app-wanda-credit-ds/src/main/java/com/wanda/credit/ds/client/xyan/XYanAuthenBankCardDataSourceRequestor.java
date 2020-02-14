package com.wanda.credit.ds.client.xyan;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.xyan.utils.HttpUtil;
import com.wanda.credit.ds.client.xyan.utils.RsaCodingUtil;
import com.wanda.credit.ds.client.xyan.vo.RetDataVo;
import com.wanda.credit.ds.dao.domain.XYanAuthenBankCard;
import com.wanda.credit.ds.dao.iface.IXYanAuthenBankCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 新颜银行卡鉴权(2,3,4要素)
 * @author lijiong.tang
 * @version 1.0
 * @createdate 2016年11月29日
 * 
 */
@DataSourceClass(bindingDataSourceId = "ds_xyan_AuthenBankCard")
public class XYanAuthenBankCardDataSourceRequestor extends
		BaseXYanAuthenBankCardDataSourceRequestor implements
		IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(XYanAuthenBankCardDataSourceRequestor.class);

	/*
	 * @Autowired private IAuthenBankCardService service;
	 */

	@Autowired
	public IPropertyEngine propertyEngine;

	@Autowired
	public IXYanAuthenBankCardService xYanAuthenBankCardService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
        final String prefix = trade_id;

        String member_id = propertyEngine.readById("ds_xyan_member_id");
        String terminal_id = propertyEngine.readById("ds_xyan_termid");
        String request_url = propertyEngine.readById("ds_xyan_req_url");
        String pfxpwd = propertyEngine.readById("ds_xyan_pfxpwd");
        String pfxname = propertyEngine.readById("ds_xyan_pfxname");
        // String cername = propertyEngine.readById("ds_xyan_cername");

        TreeMap<String, Object> retdata = new TreeMap<String, Object>();
        /** 设置编目 */
        // retdata.put("server_idx", "yw_authen4");
        Set<String> tags = new HashSet<String>();
        String initTag = Conts.TAG_SYS_ERROR;
        logger.info("{} 银行卡鉴权交易开始", prefix);
        CRSStatusEnum retStatus = CRSStatusEnum.STATUS_FAILED;
        String retMsg = "银行卡鉴权失败";
        DataSourceLogVO logObj = new DataSourceLogVO();
        logObj.setIncache("0");
        logObj.setTrade_id(trade_id);
        logObj.setDs_id(ds.getId());
        logObj.setReq_url(request_url);
        logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
        logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
        logObj.setState_msg("交易失败");

        Map<String, Object> rets = new HashMap<String, Object>();

        Map<String, Object> paramForLog = new HashMap<String, Object>();
        try {
//            /** 姓名-必填 */
//            String name = (String) ParamUtil.findValue(ds.getParams_in(),
//                    "name");
//            /** 銀行卡號-必填 */
//            String cardid = (String) ParamUtil.findValue(ds.getParams_in(),
//                    "cardid");
//            /** 校验类型：2/3/4要输-必填 */
//            String typeno = (String) ParamUtil.findValue(ds.getParams_in(),
//                    "typeno");
//            /** 身份证号码-选填 */
//            String cardno = (String) ParamUtil.findValue(ds.getParams_in(),
//                    "cardno");
//            /** 手机号码-选填 */
//            String mobile = (String) ParamUtil.findValue(ds.getParams_in(),
//                    "mobile");
//            /** 借贷标识 借记卡： 101；信用卡： 102-选填 */
//            String card_type = (String) ParamUtil.findValue(ds.getParams_in(),
//                    "card_type");
//            /** 卡有限期年，如： 17 -选填 */
//            String valid_date_year = (String) ParamUtil.findValue(
//                    ds.getParams_in(), "valid_date_year");
//            /** 卡有限期月，如： 9 -选填 */
//            String valid_date_month = (String) ParamUtil.findValue(
//                    ds.getParams_in(), "valid_date_month");
//            /** CVV2码 信用卡背面3位数字检验码，如： 123-选填 */
//            String valid_cvv2no = (String) ParamUtil.findValue(
//                    ds.getParams_in(), "valid_cvv2no");
//
//            String bankName = (String) ParamUtil.findValue(ds.getParams_in(),
//                    "bankName");
//            if(!StringUtil.isEmpty(bankName)){
//                logObj.setBiz_code2(bankName);
//            }
//
//            // 保存请求参数
//            /** 敏感数据加密 */
//            String cardid_ency = synchExecutorService.encrypt(cardid);
//            String cardno_ency = null;
//            String mobile_ency = null;
//
//            /** 构建请求参数 */
//            JSONObject po = new JSONObject();
//            po.put("member_id", member_id);// 配置参数
//            po.put("terminal_id", terminal_id);// 配置参数
//            po.put("verify_element", typeno);// ds入参 必填 校验类型 2、3、4要素
//            po.put("id_holder", name);// ds入参 必填
//            po.put("acc_no", cardid);// ds入参 必填
//
//            /** 请求参数记录到日志 */
//            paramForLog.put("typeno", typeno);
//            paramForLog.put("name", name);
//            paramForLog.put("cardid", cardid_ency);
//
//            if (!StringUtil.isEmpty(cardno)) {
//                po.put("id_card", cardno);// ds入参 不必填
//                cardno_ency = synchExecutorService.encrypt(cardno);
//                paramForLog.put("cardno", cardno_ency);
//            } else {
//                cardno_ency = "";
//            }
//            if (!StringUtil.isEmpty(mobile)) {
//                po.put("mobile", mobile);// ds入参 不必填
//                mobile_ency = synchExecutorService.encrypt(mobile);
//                paramForLog.put("mobile", mobile);
//            } else {
//                mobile_ency = "";
//            }
//            if (!StringUtil.isEmpty(card_type)) {
//                po.put("card_type", card_type);// ds入参 不必填
//                paramForLog.put("card_type", card_type);
//            }
//
//            if (!StringUtil.isEmpty(valid_date_year)) {
//                po.put("valid_date_year", valid_date_year);// ds入参 不必填
//                paramForLog.put("valid_date_year", valid_date_year);
//            }
//
//            if (!StringUtil.isEmpty(valid_date_month)) {
//                po.put("valid_date_month", valid_date_month);// ds入参 不必填
//                paramForLog.put("valid_date_month", valid_date_month);
//            }
//
//            if (!StringUtil.isEmpty(valid_cvv2no)) {
//                po.put("valid_no", valid_cvv2no);// ds入参 不必填
//                paramForLog.put("valid_cvv2no", valid_cvv2no);
//            }
//            if (!StringUtil.isStrInStrs(typeno, "123", "1234")) {
//                logger.info("{} 入参typeno错误，传入值为：{}", prefix, typeno);
//                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
//                logObj.setState_msg("typeno错误");
//                logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
//
//                rets.clear();
//                rets.put(Conts.KEY_RET_CODE,
//                        CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
//                rets.put(Conts.KEY_RET_MSG, "传入参数不正确：typeno应为123或者1234");
//                return rets;
//            }
//
//            if (!StringUtil.isEmpty(cardno)
//                    && StringUtils.isNotEmpty(CardNoValidator.validate(cardno))) {
//                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
//                logObj.setState_msg("身份证号码不符合规范");
//                logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
//                logger.error("{}  {}", prefix, logObj.getState_msg());
//                rets.clear();
//                rets.put(Conts.KEY_RET_STATUS,
//                        CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
//                rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
//                return rets;
//            }
//
//            po.put("trans_id", trade_id);
//            po.put("trade_date",
//                    new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//            // 20170515 houxiabin add 新颜银行卡认证 2.0.2 Begin
//            po.put("product_type", "0");
//            po.put("industry_type", "C1");
//            // 20170515 houxiabin add 新颜银行卡认证 2.0.2 End
//            String base64str = com.wanda.credit.ds.client.xyan.utils.SecurityUtil
//                    .Base64Encode(po.toString());
//            /** rsa加密 **/
//            String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str,
//                    cer_file_base_path + pfxname, pfxpwd);// 加密数据
//
//            Map<String, String> HeadPostParam = new HashMap<String, String>();
//            HeadPostParam.put("member_id", member_id);
//            HeadPostParam.put("terminal_id", terminal_id);
//            HeadPostParam.put("data_type", "json");
//            HeadPostParam.put("data_content", data_content);
//            // StringBuilder postParms = new StringBuilder();
//            // int PostItemTotal = HeadPostParam.keySet().size();
//            // int itemp=0;
//            // for (String key : HeadPostParam.keySet()){
//            // postParms.append(key + "="+HeadPostParam.get(key));
//            // itemp++;
//            // if(itemp<PostItemTotal){
//            // postParms.append("&");
//            // }
//            // }
//            // DataSourceLogEngineUtil.writeLog2LogSys(new
//            // LoggingEvent(trade_id,request_url,new String[]{trade_id}));
//            logger.info("{} 开始请求远程服务器... ", new String[] { prefix });
//            logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
//            String postString = HttpUtil
//                    .RequestForm(request_url, HeadPostParam);
//            logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
//            logger.info("{} 请求返回.{}", new String[] { prefix, postString });
//
//            // DataSourceLogEngineUtil.writeLog2LogSys(new
//            // LoggingEvent(trade_id,postString,new String[]{trade_id}));
//            // {"success":true,"data":{"code":"0","desc":"亲，认证成功","trans_id":"20161130142450719RRLZ","trade_no":"201611301419070000014636",
//            // "org_code":null,"org_desc":null,"fee":"Y"},"errorCode":null,"errorMsg":null}
//            JSONObject result_obj = JSONObject.parseObject(postString);
//            logObj.setBiz_code1(result_obj.get("errorCode") + "-" + result_obj.get("errorMsg"));
//            JSONObject data_obj = null;
//
//            retdata.putAll(visitBusiData(trade_id,result_obj, true));
//            logger.info("{} 转换之后结果：{}", prefix, retdata);
//
//            String respCode = String.valueOf(retdata.get("respCode"));
//            //一致/不一致
//            if("2000".equals(respCode) || "2001".equals(respCode)){
//                data_obj = result_obj.getJSONObject("data");
//
//                RetDataVo retDataVo = JSONObject.toJavaObject(data_obj,
//                        RetDataVo.class);
//
//                logObj.setBiz_code1(retDataVo.getOrg_code() + "-" + retDataVo.getOrg_desc());
//                logObj.setBiz_code3(retDataVo.getTrade_no());
//                retStatus = CRSStatusEnum.STATUS_SUCCESS;
//                retMsg = "采集成功";
//                XYanAuthenBankCard record_new = new XYanAuthenBankCard();
//                record_new.setCard_type(card_type);
//                record_new.setCardid(cardid_ency);
//                record_new.setCardno(cardno_ency);
//                record_new.setMobile(mobile_ency);
//                record_new.setName(name);
//                record_new.setRet_code(retDataVo.getCode());
//                record_new.setRet_desc(retDataVo.getDesc());
//                record_new.setRet_fee(retDataVo.getFee());
//                record_new.setRet_trade_no(retDataVo.getTrade_no());
//                record_new.setValid_cvv2no(valid_cvv2no);
//                record_new.setValid_date_month(valid_date_month);
//                record_new.setValid_date_year(valid_date_year);
//                record_new.setTrade_id(trade_id);
//                record_new.setCard_type(card_type);
//                record_new.setTypeno(typeno);
//                record_new.setBank_id(retDataVo.getBank_id());
//                record_new.setBank_desc(retDataVo.getBank_description());
//                record_new.setOrg_code(retDataVo.getOrg_code());
//                record_new.setOrg_desc(retDataVo.getOrg_desc());
//                try {
//                    xYanAuthenBankCardService.add(record_new);
//
//                    String req_values = cardno+"_"+cardid;
//                    if(!StringUtil.isEmpty(mobile)){
//                        req_values = req_values + "_" + mobile;
//                    }
//
//                    //落库数据总表
//                    if("1".equals(propertyEngine.readById("encrypt_switch_142"))){
//                        allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, cardno_ency, cardid_ency, mobile_ency,
//                                visitBusiData(trade_id,result_obj, false), req_values);
//                    }else {
//                        allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, cardno, cardid, mobile,
//                                visitBusiData(trade_id,result_obj, false), req_values);
//                    }
//                } catch (Exception e) {
//                    logger.error("{} 卡鉴权信息保存失败 {}", prefix,
//                            e.getMessage());
//                }
//                logger.info("{} 数据入库成功. ", prefix);
//
//                retdata.put("org_code", retDataVo.getOrg_code());
//                retdata.put("org_desc", retDataVo.getOrg_desc());
//                retdata.put("bank_id", retDataVo.getBank_id());
//                retdata.put("bank_desc",
//                        retDataVo.getBank_description());
//                retdata.put("name", name);
//                retdata.put("cardId", cardid);
//                if (!StringUtil.isEmpty(cardno))
//                    retdata.put("cardNo", cardno);
//                if (!StringUtil.isEmpty(mobile))
//                    retdata.put("phone", mobile);
//
////                logObj.setBiz_code1(retDataVo.getCode());
////                logObj.setBiz_code2(retDataVo.getOrg_code());
////                logObj.setBiz_code3(retDataVo.getFee());
//                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
//                logObj.setState_msg("交易成功");
//
//                // 收费才打标签SUCCESS，不收费打标签FAIL
//                initTag = Conts.TAG_TST_SUCCESS;
//                logObj.setTag(initTag);
//            }else if("2003".equals(respCode)){
//                retStatus = CRSStatusEnum.STATUS_SUCCESS;
//                retMsg = "采集成功";
//
//                data_obj = result_obj.getJSONObject("data");
//
//                RetDataVo retDataVo = JSONObject.toJavaObject(data_obj,
//                        RetDataVo.class);
//
//                retdata.put("name", name);
//                retdata.put("cardId", cardid);
//                if (!StringUtil.isEmpty(cardno))
//                    retdata.put("cardNo", cardno);
//                if (!StringUtil.isEmpty(mobile))
//                    retdata.put("phone", mobile);
//
//                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
//                logObj.setState_msg("交易成功");
//
//                // 收费才打标签SUCCESS，不收费打标签FAIL
//                initTag = Conts.TAG_TST_FAIL;
//                logObj.setTag(initTag);
//            }else {
//                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
//                logObj.setState_msg("数据源系统异常");
//
//                if("warn_130".equals(respCode)){
//                    retStatus = CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION;
//                    retMsg = retStatus.getRet_msg();
//                }else if("warn_304".equals(respCode)){
//                    retStatus = CRSStatusEnum.STATUS_FAILED_INVALID_PARAM;
//                    retMsg = retStatus.getRet_msg();
//                }else if("err_015".equals(respCode)){
//                    retStatus = CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION;
//                    retMsg = retStatus.getRet_msg();
//                }else {
//                    retStatus = CRSStatusEnum.STATUS_FAILED;
//                    retMsg = retStatus.getRet_msg();
//                }
//            }
//
//            rets.put(Conts.KEY_RET_STATUS, retStatus);
//            rets.put(Conts.KEY_RET_DATA, retdata);
//            rets.put(Conts.KEY_RET_MSG, retMsg);
        } catch (Exception ex) {
            initTag = Conts.TAG_SYS_ERROR;
            logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
            if (CommonUtil.isTimeoutException(ex)) {
                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
                initTag = Conts.TAG_SYS_TIMEOUT;
            } else {
                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
                logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
            }
            rets.put(Conts.KEY_RET_STATUS,
                    CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
            rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
            logger.error(prefix + " 数据源处理时异常", ex);
        } finally {
            tags.add(initTag);
            rets.put(Conts.KEY_RET_TAG, tags.toArray(new String[0]));
            logObj.setTag(StringUtils.join(tags, ";"));
            /** 记录请求 */
            if (MapUtils.isNotEmpty(paramForLog)) {
                DataSourceLogEngineUtil.writeParamIn(trade_id, paramForLog,
                        logObj);
            }
            DataSourceLogEngineUtil.writeLog(trade_id, logObj);
        }
        return rets;
	}

}
