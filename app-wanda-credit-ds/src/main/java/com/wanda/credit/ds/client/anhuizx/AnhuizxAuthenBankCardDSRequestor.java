package com.wanda.credit.ds.client.anhuizx;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author shiwei
 * @version $$Id: AnhuizxAuthenBankCardDSRequestor, V 0.1 2017/11/15 14:59 shiwei Exp $$
 */
@DataSourceClass(bindingDataSourceId="ds_anhuizx_AuthenBankCard")
public class AnhuizxAuthenBankCardDSRequestor extends BaseAnhuizxDSRequestor
        implements IDataSourceRequestor {

    private final Logger logger = LoggerFactory.getLogger(AnhuizxAuthenBankCardDSRequestor.class);

    @Override
    public Map<String, Object> request(String trade_id, DataSource ds) {
        final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
        TreeMap<String, Object> retdata = new TreeMap<String, Object>();
        Map<String, Object> rets = new HashMap<String, Object>();
        DataSourceLogVO logObj = new DataSourceLogVO();
        Map<String, Object> reqparam = new HashMap<String, Object>();
        String resource_tag = Conts.TAG_SYS_ERROR;
        logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
        try{
            logObj.setDs_id(ds.getId());
            logObj.setReq_url(propertyEngine.readById("ds_ahzx_bankcard_url"));
            logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
            String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();   //姓名
            String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString(); //身份证号码
            String cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString(); //银行卡号
            String phone = null;
            Object phoneObj = ParamUtil.findValue(ds.getParams_in(), "phone");//手机号码
            if (phoneObj != null) {
                phone = phoneObj.toString().trim();
            }
            reqparam.put("custName", name);
            reqparam.put("idNo", cardNo);
            reqparam.put("bankCardno", cardId);
            reqparam.put("idType", "01");
            if(!StringUtil.isEmpty(phone)){
                //四要素
                reqparam.put("phoneNo", phone);
                if (!StringUtil.isIntNumeric(phone) && phone.length() != 11) {
                    logObj.setIncache("1");
                    logObj.setState_msg("手机号码不符合规范");
                    rets.clear();
                    rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
                    rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
                    rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
                    return rets;
                }
            }else{
                reqparam.put("phoneNo", "");
            }

            if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
                logObj.setIncache("1");
                logObj.setState_msg("身份证号码不符合规范");
                rets.clear();
                rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
                rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
                rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
                return rets;
            }

            logObj.setIncache("0");
            logger.info("{} 开始请求数据源", prefix);
            String result = getResponseByHttp(prefix, reqparam);
            logger.info("{} 请求结果{}", prefix, result);

            JSONObject rspData = JSONObject.parseObject(result);
            if(isSuccess(rspData)){

                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
                logObj.setBiz_code1(String.valueOf(rspData.get("code")));
                logObj.setBiz_code2(String.valueOf(rspData.get("msg")));
                logObj.setBiz_code3(String.valueOf(rspData.get("transNo")));
                retdata.putAll(visitBusiData(trade_id, rspData, true));
                logger.info("{} 数据转换后结果 {}", trade_id, retdata);
                //计费标签
                resource_tag = buildTag(trade_id, retdata);
                //detailRespCode 根据这个值判断接口返回码 如果存在于错误码列表直接返回错误结果
                if(errCode.indexOf("," + String.valueOf(rspData.get("code")) + ",") != -1){
                    return returnErrorException(trade_id, String.valueOf(rspData.get("code")));
                }

                String req_values = cardNo+"_"+cardId;
                if(!StringUtil.isEmpty(phone)){
                    req_values = req_values + "_" + phone;
                }

                try {
                    if("1".equals(propertyEngine.readById("encrypt_switch_142"))){
                        cardNo = CryptUtil.encrypt(cardNo);
                        cardId = CryptUtil.encrypt(cardId);
                        if(StringUtils.isNotEmpty(phone)) {
                            phone = CryptUtil.encrypt(phone);
                        }
                    }

                    allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, cardNo, cardId, phone,
                            visitBusiData(trade_id , rspData, false), req_values);
                }catch (Exception e){
                    logger.error("{} 交易结果落库失败 {}", trade_id, e);
                }
            }else{
                resource_tag = Conts.TAG_TST_FAIL;
                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
                logObj.setBiz_code1(String.valueOf(rspData.get("code")));
                logObj.setState_msg(result);
                logger.error("{} 厂商返回异常，收到响应信息 {}", trade_id, result);
                throw new Exception();
            }

            rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
            rets.put(Conts.KEY_RET_DATA, retdata);
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
            rets.put(Conts.KEY_RET_MSG, "采集成功!");
        }catch (Exception ex){
            resource_tag = Conts.TAG_SYS_ERROR;
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
            rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
            logger.error(prefix+" 数据源处理时异常：{}",ex);
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
     * 错误码列表
     */
    private String errCode = ",31177,41177,32097,42097,32170,32181," +
            "42170,42181,32171,32099,42171,42099,30101,31103,32173,32174,32175,32176," +
            "32178,32179,32180,32088,32093,32094,32096,32098,320A0,320A6,40101,41103," +
            "42173,42174,42175,42176,42178,42179,42180,42088,42093,42094,42096,42098,420A0,420A0,32172,42172,32090,42090";

    public Map<String, Object> returnErrorException(String trade_id, String detailRespCode){
        Map<String, Object> rets = new HashMap<String, Object>();

        if("31177".equals(detailRespCode) || "41177".equals(detailRespCode)){
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }else if("32097".equals(detailRespCode) || "42097".equals(detailRespCode)){
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_INVALID_NAME);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_INVALID_NAME.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }else if("32170".equals(detailRespCode) || "32181".equals(detailRespCode)
                || "42170".equals(detailRespCode) || "42181".equals(detailRespCode)){
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_INVALID_PARAM);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_INVALID_PARAM.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }else {
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }

        logger.info("{} 触发错误码，返回系统异常，错误原因：{}", trade_id, rets.get(Conts.KEY_RET_MSG));
        return rets;
    }
}
