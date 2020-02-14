package com.wanda.credit.ds.client.bairong;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
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
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author shiwei
 * @version $$Id: BairongCmccMobileCheckDSRequestor, V 0.1 2017/8/22 11:34 shiwei Exp $$
 */
@DataSourceClass(bindingDataSourceId="ds_bairong_cmccCheck")
public class BairongCmccMobileCheckDSRequestor extends BaseBairongDSRequestor implements IDataSourceRequestor {
    private Logger logger = LoggerFactory.getLogger(BairongCmccMobileCheckDSRequestor.class);

    @Autowired
    private IPropertyEngine propertyEngine;

    private String testFlag;

    public final static String TAG_CHECK_CMCC_FOUND1 = "check_yd_found1";

    public final static String TAG_CHECK_UNFOUND = "check_unfound";

    public final static String TAG_CHECK_CMCC_INCACHE_FOUND = "check_yd_incache_found1";

    private String url;
    private String inCache;
    private Long cacheTime;

    @Autowired
    private IMobileSrcLocService mobileSrcLocService;

    public Map<String, Object> request(String trade_id, DataSource ds) {

        url = propertyEngine.readById("ds_br_cmcc_checkUrl");
        if(StringUtils.isEmpty(url)){
            url = "http://10.213.128.91/gateway8000/data/dmp_br/MobileThree";
        }
        inCache = propertyEngine.readById("ds_br_cmcc_iscache");
        if(StringUtils.isEmpty(inCache)){
            inCache = "true";
        }

        try{
            String cacheTimeStr = propertyEngine.readById("ds_br_cmcc_cacheSec");
            if(StringUtils.isEmpty(cacheTimeStr)){
                cacheTime = 60*60*24*30L;//30天
            }else {
                cacheTime = Long.parseLong(cacheTimeStr);
            }
        }catch (Exception e){
            cacheTime = 60*60*24*30L;//30天
        }

        final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
        long start = System.currentTimeMillis();
        logger.info("{} 手机三要素验证-百融-移动Begin {}" , prefix ,start);

        //初始化对象
        Map<String, Object> rets = initRets();
        TreeMap<String, Object> retData = new TreeMap<String, Object>();

        //计费标签
        Set<String> tags = new HashSet<String>();
        tags.add(Conts.TAG_SYS_ERROR);
        //交易日志信息数据
        DataSourceLogVO logObj = new DataSourceLogVO();
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

            if ("true".equalsIgnoreCase(inCache)) {
                GeoMobileCheck geoMobileCheck = getGeoMobileCheck(name, enccardNo, encMobile);

                if(geoMobileCheck != null){
                    long difTime = (new Date().getTime() - geoMobileCheck.getUpdate_time().getTime()) / 1000;
                    if(cacheTime > difTime && geoMobileCheck.getCheckResult() != null){
                        logger.info("{} 取得记录 {}", prefix, JSON.toJSONString(geoMobileCheck));
                        //解析返回的结果
                        retData.put(CHECK_RESULT, geoMobileCheck.getCheckResult());
                        MobileLocation location = new MobileLocation();

                        location.setProvince(geoMobileCheck.getProvince()==null?"":geoMobileCheck.getProvince());
                        location.setCity(geoMobileCheck.getCity()==null?"":geoMobileCheck.getCity());
                        location.setIsp(geoMobileCheck.getAttribute()==null?"":geoMobileCheck.getAttribute());

                        retData = parseLocatin(location, retData);
                        //构建返回标签
                        tags.clear();
                        tags.add(TAG_CHECK_CMCC_INCACHE_FOUND);

                        //拼装返回信息
                        rets.clear();
                        rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
                        rets.put(Conts.KEY_RET_MSG, "请求成功");
                        rets.put(Conts.KEY_RET_DATA, retData);
                        //记录日志信息
                        logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
                        logObj.setState_msg("交易成功");

                        return rets;
                    }else {
                        logger.info("{} 没取到数据", prefix);
                    }
                }
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
                logger.info("{} 请求百融-移动三要素异常返回状态码为 {}" , prefix , resStauts);
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

            //号码归属地-读表
            MobileLocation location = new MobileLocation();
            location.setIsp("移动");
            MobileSrcLocVo mobileSrcLocVo = mobileSrcLocService.findByMobileNo(mobile);
            if(mobileSrcLocVo != null){
                location = new MobileLocation();
                location.setProvince(mobileSrcLocVo.getProvince());
                location.setCity(mobileSrcLocVo.getCity());
            }
            retData = parseLocatin(location, retData);
            logger.info("{} 解析号码归属地成功" , prefix);

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
                    retData = parseResult(product.getResult(), retData);
                    //构建返回标签
                    tags.clear();
                    if(msgResObj.getData().getFlag().getFlag_mobilethree() == 1){
                        tags.add(TAG_CHECK_CMCC_FOUND1);
                    }else {
                        tags.add(TAG_CHECK_UNFOUND);
                    }

                    //解析结果用于保存 -查无不存
                    if(!"0".equals(product.getResult())) {
                        parseToSave(trade_id, name, enccardNo, encMobile, location, retData);
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

            logger.error("{} 手机号码核查交易处理异常：{}" , prefix , e.getMessage());

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
        }finally{

            rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[tags.size()]));
            //保存日志信息
            logObj.setTag(StringUtils.join(tags, ";"));
            long dsLogStart = System.currentTimeMillis();
            DataSourceLogEngineUtil.writeLog(trade_id,logObj);
            logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
        }
        logger.info("{} 手机三维验证End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
        return rets;
    }

    public void setTestFlag(String testFlag) {
        this.testFlag = testFlag;
    }

    /**
     *
     * @param result
     * @param retData
     * @return
     */
    private TreeMap<String, Object> parseResult(String result,
                                                 TreeMap<String, Object> retData) {
        Map<String, String> resMap = getCheckResMap();
        if (resMap.containsKey(result)) {
            retData.put(CHECK_RESULT, resMap.get(result));
        }else{
            retData.put(CHECK_RESULT, result);
        }
        return retData;
    }
}
