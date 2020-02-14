package com.wanda.credit.ds.client.bairong;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.bairong.bean.BaiRongResBean;
import com.wanda.credit.ds.client.bairong.bean.Product;
import com.wanda.credit.ds.client.bairong.service.IMobileSrcLocService;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.dao.domain.bairong.MobileSrcLocVo;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author shiwei
 * @version $$Id: BairongCmccMobileStatusDSRequestor, V 0.1 2017/9/7 9:29 shiwei Exp $$
 */
@DataSourceClass(bindingDataSourceId="ds_bairong_cmccStatus")
public class BairongCmccMobileStatusDSRequestor extends BaseBairongDSRequestor implements IDataSourceRequestor {
    private String testFlag;

    private Logger logger = LoggerFactory.getLogger(BairongCmccMobileStatusDSRequestor.class);

    @Autowired
    private IPropertyEngine propertyEngine;

    @Autowired
    private IMobileSrcLocService mobileSrcLocService;

    public final static String TAG_STATUS_CMCC_FOUND1 = "state_yd_found";

    public final static String TAG_STATUS_UNFOUND = "state_unfound";

    private String url;

    public Map<String, Object> request(String trade_id, DataSource ds) {

        url = propertyEngine.readById("ds_br_cmcc_statusUrl");
//        url = "http://10.213.128.91/gateway8000/data/dmp_br/TelStatusCMCC_f";
        if(StringUtils.isEmpty(url)){
            url = "http://10.214.96.74/gateway8000/data/dmp_br/TelStatusCMCC_f";
        }

        final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
        long start = System.currentTimeMillis();
        logger.info("{} 手机号码在网状态查询-网数-百融-移动Begin {}" , prefix ,start);

        //组织返回对象
        Map<String, Object> rets = new HashMap<String, Object>();
        TreeMap<String, Object> retData = new TreeMap<String, Object>();
        rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
        rets.put(Conts.KEY_RET_MSG, "交易失败");
        //计费标签
        Set<String> tags = new HashSet<String>();
        tags.add(Conts.TAG_SYS_ERROR);
        //交易日志信息数据
        DataSourceLogVO logObj = new DataSourceLogVO();
        logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
        logObj.setDs_id(ds.getId());
        logObj.setReq_url(url);
        logObj.setIncache("0");
        logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
        logObj.setState_msg("交易失败");

        try{
            logger.info("{} 开始解析传入的参数" , prefix);
            String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
            String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString().toUpperCase();
            String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
            logger.info("{} 解析传入的参数成功" , prefix);

            //加密敏感信息
            String enccardNo = CryptUtil.encrypt(cardNo);
            String encMobile = CryptUtil.encrypt(mobile);
            //保存请求参数
            saveParamIn(name,cardNo,mobile,null,null,trade_id,logObj);

            //参数校验 - 身份证号码和手机号
            String validate = CardNoValidator.validate(cardNo);
            if (!StringUtil.isEmpty(validate)) {
                logger.info("{} 身份证格式校验错误： {}" , prefix , validate);
                logObj.setState_msg("身份证格式校验错误");
                rets.clear();
                rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
                rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
                return rets;
            }
            if(!(mobile.length() == 11 && StringUtil.isPositiveInt(mobile))){
                logger.info("{} 手机号码格式错误" , prefix);
                logObj.setState_msg("手机号码格式错误");
                rets.clear();
                rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
                rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
                return rets;
            }

            Map<String, String> params =  new HashMap<String, String>();
            params.put("cell",mobile);
            params.put("id", cardNo);
            params.put("name", name);
            long postStart = System.currentTimeMillis();
            logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
            Map<String, Object> httpRespMap = doRequest(url, params, prefix, false ,doPrint(testFlag));
            logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
            logger.info("{} http请求耗时(ms)为：{}",prefix , System.currentTimeMillis() - postStart);
            logger.info("{} http请求结果为：{}",prefix , httpRespMap);

            Object resStauts = httpRespMap.get(RequestHelper.HTTP_RES_CODE);
            if (!"200".equals(resStauts + "")) {
                logger.info("{} 请求百融-移动手机在线状态异常返回状态码为 {}" , prefix , resStauts);
                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
                logObj.setState_msg("请求失败返回状态码为 ：" + resStauts);
                return rets;
            }

            String postResult = httpRespMap.get(RequestHelper.HTTP_RES_BODYSTR).toString();
            if (StringUtil.isEmpty(postResult)) {
                logger.info("{} http请求返回结果为空" , prefix);
                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
                logObj.setState_msg("请求超时");
                return rets;
            }

            BaiRongResBean msgResObj = JSONObject.parseObject(postResult, BaiRongResBean.class);

            if (StringUtil.isEmpty(msgResObj)) {
                logObj.setState_msg("返回报文解析成对象错误");
                return rets;
            }

            //接口调用返回码
            String resCode = msgResObj.getCode();
            String resMsg = msgResObj.getMsg();
            String seq = msgResObj.getSeq();
            logObj.setBiz_code2(resCode);
            logObj.setState_msg(resMsg);
            logObj.setBiz_code3(seq);
            if (!"2001".equals(resCode)) {
                logger.info("{} 调用远程接口失败返回错误码为 {}" , prefix , resCode);
                rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
                rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
                return rets;
            }

            if (StringUtil.isEmpty(msgResObj.getData())) {
                logger.info("{} 返回报文信息中对应data节点为空" , prefix );
                rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
                rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
                return rets;
            }

            Product product = msgResObj.getData().getProduct();

            if(product != null){
                if("1001".equals(product.getResult()) || "1002".equals(product.getResult())
                        || "2001".equals(product.getResult()) || "2002".equals(product.getResult())
                        || "2005".equals(product.getResult()) || "9999".equals(product.getResult())){
                    logger.info("{} 调用远程接口验证结果码返回为 {}" , prefix , product.getResult());
                    rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
                    rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
                    return rets;
                }else {
                    //解析返回的结果
                    retData = parseResult(product.getResult(), retData, product.getStatus());
                    //构建返回标签
                    tags.clear();
                    if(msgResObj.getData().getFlag().getFlag_telstatuscmcc_f() == 1){
                        tags.add(TAG_STATUS_CMCC_FOUND1);
                    }else {
                        tags.add(TAG_STATUS_UNFOUND);
                    }

                    //解析结果用于保存 -查无不存
                    if(!"0".equals(product.getResult())) {
                        parseToSave(trade_id, name, enccardNo, encMobile, null, retData);
                    }
                    //拼装返回信息
                    rets.clear();
                    rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
                    rets.put(Conts.KEY_RET_MSG, "请求成功");
                    rets.put(Conts.KEY_RET_DATA, retData);
                    //记录日志信息
                    logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
                    logObj.setState_msg("交易成功");
                }
            }else {
                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
                rets.clear();
                rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
                rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
                return rets;
            }


        }catch (Exception e){
            logger.error("{} 手机号码在线状态交易处理异常：{}" , prefix , e.getMessage());

            //设置标签
            tags.clear();
            tags.add(Conts.TAG_TST_FAIL);

            if (e instanceof ConnectTimeoutException) {

                logger.error("{} 连接远程数据源超时" , prefix);

                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
                logObj.setState_msg("请求超时");
                //设置标签
                tags.clear();
                tags.add(Conts.TAG_SYS_TIMEOUT);
            }
            e.printStackTrace();
        }
        finally {
            rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[tags.size()]));
            //保存日志信息
            logObj.setTag(StringUtils.join(tags, ";"));
            long dsLogStart = System.currentTimeMillis();
            DataSourceLogEngineUtil.writeLog(trade_id,logObj);
            logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
        }
        logger.info("{} 手机在线状态查询End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
        return rets;
    }

    private TreeMap<String, Object> parseResult(String result,
                                                TreeMap<String, Object> retData, String value) {
        Map<String, String> resMap = getStatusResMap();

        if("0".equals(result)){
            retData.put(MOBILE_STATUS, "5");
            return retData;
        }

        if (resMap.containsKey(value)) {
            retData.put(MOBILE_STATUS, resMap.get(value));
        }else{
            retData.put(MOBILE_STATUS, value);
        }
        return retData;
    }

    public void setTestFlag(String testFlag) {
        this.testFlag = testFlag;
    }
}
