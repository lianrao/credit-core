package com.wanda.credit.ds.client.wangshu;

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
 * @version $$Id: YinXingAuthenBankCardDataSourceRequestor, V 0.1 2017/10/25 10:39 shiwei Exp $$
 */
@DataSourceClass(bindingDataSourceId="ds_yinxing_AuthenBankCard")
public class YinXingAuthenBankCardDataSourceRequestor extends BaseYinXingAuthenBankCardDataSourceRequestor
        implements IDataSourceRequestor {
    private final Logger logger = LoggerFactory.getLogger(YinXingAuthenBankCardDataSourceRequestor.class);


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
            logObj.setReq_url(propertyEngine.readById("ds_dmp_yx_url"));
            logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
            String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();   //姓名
            String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString(); //身份证号码
            String cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString(); //银行卡号
            String certType = ParamUtil.findValue(ds.getParams_in(), "certType").toString(); //证件类型
            String phone = null;
            Object phoneObj = ParamUtil.findValue(ds.getParams_in(), "phone");//手机号码
            if (phoneObj != null) {
				phone = phoneObj.toString().trim();
			}

            String bankName = (String) ParamUtil.findValue(ds.getParams_in(),
                    "bankName");
            if(!StringUtil.isEmpty(bankName)){
                logObj.setBiz_code2(bankName);
            }
            reqparam.put("name", name);
            reqparam.put("cardNo", cardNo);
            reqparam.put("cardId", cardId);
            reqparam.put("certType", certType);
            if(!StringUtil.isEmpty(phone)){
                //四要素
                reqparam.put("phone", phone);
                if (!StringUtil.isIntNumeric(phone) && phone.length() != 11) {
                	  logObj.setIncache("1");
                      logObj.setState_msg("手机号码不符合规范");
                      rets.clear();
                      rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
                      rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
                      rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
                      return rets;
				}
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
            String url = buildRequestUrl(trade_id,name,cardNo,cardId, certType, phone);
            Map<String, Object> rspDataMap = doRequest(trade_id,url,false);
            int httpstatus = (Integer)rspDataMap.get("httpstatus");
            JSONObject rspData = JSONObject.parseObject((String) rspDataMap.get("rspstr"));
            if(needRetry(httpstatus,rspData)){
                logger.info("{} token失效 准备重试",trade_id);
                rspDataMap = doRequest(trade_id,url,true);
                rspData = JSONObject.parseObject((String) rspDataMap.get("rspstr"));
            }
            logObj.setBiz_code3(rspData.get("seq") + "");
			logObj.setBiz_code1(rspData.get("code") + "-" +rspData.get("msg"));
            logger.info("{} 请求返回数据 {}",trade_id,rspDataMap.get("rspstr"));
            if(isSuccess(rspData)){
                resource_tag = buildTag(trade_id,rspData.getJSONObject("data"));
                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
                logObj.setState_msg("交易成功");
                Object detailResMsg = rspData.getJSONObject("data").get("detailRespMsg");
                Object detailRespCode = rspData.getJSONObject("data").get("detailRespCode");
                logObj.setBiz_code1(detailResMsg + "-" +detailRespCode);
//                logObj.setBiz_code3(String.valueOf(rspData.getJSONObject("data").get("respCode")));
                retdata.putAll(visitBusiData(trade_id,rspData.getJSONObject("data"), true));
                logger.info("{} 数据转换后结果 {}", trade_id, retdata);
                if ("2003".equals(retdata.get("respCode"))) {
                	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
                    logObj.setState_msg("无法核验");
				}
                //detailRespCode 根据这个值判断接口返回码 如果存在于错误码列表直接返回错误结果
                if(errCode.indexOf("," + String.valueOf(detailRespCode) + ",") != -1){
                    return returnErrorException(trade_id, String.valueOf(detailRespCode));
                }

                try {
                    if("1".equals(propertyEngine.readById("encrypt_switch_142"))){
                        cardNo = CryptUtil.encrypt(cardNo);
                        cardId = CryptUtil.encrypt(cardId);
                        if(StringUtils.isNotEmpty(phone)) {
                            phone = CryptUtil.encrypt(phone);
                        }
                    }
                    long saveStart = System.currentTimeMillis();

                    String req_values = cardNo+"_"+cardId;
                    if(!StringUtil.isEmpty(phone)){
                        req_values = req_values + "_" + phone;
                    }

                    allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, cardNo, cardId, phone,
                            visitBusiData(trade_id,rspData.getJSONObject("data"), false), req_values);
                    logger.info("{} 保存数据耗时为 {}" , prefix ,System.currentTimeMillis() - saveStart);
                }catch (Exception e){
                    logger.error("{} 交易结果落库失败 {}", trade_id, e);
                }
            }else{
                resource_tag = Conts.TAG_TST_FAIL;
                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
                logObj.setBiz_code1(String.valueOf(rspData.get("code")));
                logger.error("{} 厂商返回异常，收到响应信息 {}", trade_id, rspDataMap.get("rspstr"));
                throw new Exception();
            }
            rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
            rets.put(Conts.KEY_RET_DATA, retdata);
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
            rets.put(Conts.KEY_RET_MSG, "采集成功!");
        }catch(Exception ex){
            resource_tag = Conts.TAG_SYS_ERROR;
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
            rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
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
     *
     * @param trade_id
     * @param name
     * @param cardNo
     * @param cardId
     * @param certType 01 身份证
     * @return
     */
    public String buildRequestUrl(String trade_id, String name, String cardNo,
                                  String cardId, String certType, String phone) {
        String url = propertyEngine.readById("ds_dmp_yx_url");
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        sb.append("certNo=").append(cardNo).append("&name=").append(name).append("&cardNo=").append(cardId)
        .append("&certType=").append(certType).append("&personalMandate=1");
        if (!StringUtil.isEmpty(phone)) {
            sb.append("&phoneNo=").append(phone);
        }

        logger.info("{} 请求数据源地址： {}", trade_id, sb.toString());
        return sb.toString();
    }

    /**
     * 错误码列表
     */
    private String errCode = ",4001,4002,4003,4004,4005,4006," +
            "1103,1201,1302,1305,1399," +
            "2208,2301,2302,2306,2308,2309,2324,2327,2329,2341,2342,2405,";

    public Map<String, Object> returnErrorException(String trade_id, String detailRespCode){
        Map<String, Object> rets = new HashMap<String, Object>();

        if("4001".equals(detailRespCode)){
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_INVALID_CARD);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_INVALID_CARD.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }else if("4002".equals(detailRespCode) || "4003".equals(detailRespCode)){
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }else if("4004".equals(detailRespCode)){
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }else if("4005".equals(detailRespCode)){
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_INVALID_NAME);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_INVALID_NAME.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }else if("4006".equals(detailRespCode)){
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_INVALID_PARAM);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_INVALID_PARAM.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }else if("1103".equals(detailRespCode) || "1201".equals(detailRespCode)
                || "1302".equals(detailRespCode) || "1305".equals(detailRespCode)
                || "1399".equals(detailRespCode) || "2208".equals(detailRespCode)
                || "2301".equals(detailRespCode) || "2302".equals(detailRespCode)
                || "2306".equals(detailRespCode) || "2308".equals(detailRespCode)
                || "2309".equals(detailRespCode) || "2324".equals(detailRespCode)
                || "2327".equals(detailRespCode) || "2329".equals(detailRespCode)
                || "2341".equals(detailRespCode) || "2342".equals(detailRespCode)
                || "2405".equals(detailRespCode)){
            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
            rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
            rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
        }

        logger.info("{} 触发错误码，返回系统异常，错误原因：{}", trade_id, rets.get(Conts.KEY_RET_MSG));
        return rets;
    }
}
