package com.wanda.credit.ds.client.wangshu;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.Conts;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shiwei
 * @version $$Id: BaseYinXingAuthenBankCardDataSourceRequestor, V 0.1 2017/10/25 13:54 shiwei Exp $$
 */
public abstract class BaseYinXingAuthenBankCardDataSourceRequestor  extends BaseWDWangShuDataSourceRequestor
        implements IDataSourceRequestor {
    private final Logger logger = LoggerFactory.getLogger(BaseYinXingAuthenBankCardDataSourceRequestor.class);

    @Autowired
    protected  WDWangShuTokenService tokenService;

    @Autowired
    protected IAllAuthCardService allAuthCardService;

    protected Map<String,Object> doRequest(String trade_id, String url,boolean forceRefresh) throws Exception {
        Map<String,Object> header = new HashMap<String,Object>();
        if(forceRefresh){
            logger.info("{} 强制刷新token",trade_id);
            tokenService.setToken(tokenService.getNewToken());
            logger.info("{} 强制刷新token结束",trade_id);
        }else if(tokenService.getToken() == null){
            logger.info("{} 发起token请求",trade_id);
            tokenService.setToken(tokenService.getNewToken());
            logger.info("{} 发起token请求结束",trade_id);

        }
//        String token = PropertyEngine.get("tmp_tokenid");
		String token = tokenService.getToken();
        header.put("X-Access-Token",token);
        logger.info("{} tokenid {}",trade_id,token);
        logger.info("{} start request",trade_id);
        Map<String,Object> rspMap = doGetForHttpAndHttps(url,trade_id,header,null);
        logger.info("{} end request",trade_id);
        return rspMap;
    }

    protected boolean needRetry(int httpstatus, JSONObject rsponse) {
        if(httpstatus == 401){
            return true;
        }
        return false;
    }

    protected boolean isSuccess(JSONObject rspData) {
        if("2001".equals(rspData.getString("code"))){
            return true;
        }
        return false;
    }

    protected String buildTag(String trade_id, JSONObject rspData) {
        Object res = rspData.get("respCode");
        String resstr = null;
        if(res != null ){
            resstr = res.toString();
            if("00".equals(resstr) || "01".equals(resstr)){
                return Conts.TAG_TST_SUCCESS;
            }
        }
        return Conts.TAG_TST_FAIL;
    }

    protected Map<? extends String, ? extends Object> visitBusiData(
            String trade_id, JSONObject data, boolean isOut) {
        Map<String,Object> ret = new HashMap<String,Object>();
        Object resObj = data.get("respCode");
        if(resObj == null){
            logger.error("{} respCode 字段值非法  ,",trade_id,data.toJSONString());
            return ret;
        }
        String message = data.getString("respMsg");
        String res = resObj.toString();
        String respCode = res; String resMsg = message;
        if("00".equals(res)){
            respCode = "2000" ;
            resMsg = "认证一致" ;
        }else if("01".equals(res)){
            respCode = "2001";
            resMsg = "认证不一致";
        }else if("02".equals(res)){
            respCode = "2003";
            resMsg = "不支持验证";
        }else if("03".equals(res)){
            respCode = "-9991";
            resMsg = "验证要素格式有误";
        }else if("04".equals(res)){
            respCode = "-9999";
            resMsg = "系统异常";
        }
        ret.put("respCode", respCode);
        ret.put("respDesc", resMsg);

        String detailRespCode = data.getString("detailRespCode");
        String detailRespMsg = data.getString("detailRespMsg");

        //转换detailRespCode 为万达标准
        Map<String, String> respDetailMap = getRespDetailMap();
        if(respDetailMap.containsKey(detailRespCode)) {
            ret.put("detailRespCode", respDetailMap.get(detailRespCode));
        }

        if (!isOut){
            ret.put("certType", data.getString("certType"));
            ret.put("dcType", data.getString("dcType"));
            ret.put("detailRespMsg", detailRespMsg);
        }
        return ret;
    }

    public Map<String, String> getRespDetailMap(){
        Map<String, String> respDetailMap = new HashMap<>();

        respDetailMap.put("0000", "");
        respDetailMap.put("2310", "0101");
        respDetailMap.put("2311", "0102");
        respDetailMap.put("2314", "0103");
        respDetailMap.put("2315", "0301");
        respDetailMap.put("2316", "0104");
        respDetailMap.put("2317", "");
        respDetailMap.put("2318", "0105");
        respDetailMap.put("2319", "0106");
        respDetailMap.put("2320", "0107");
        respDetailMap.put("2321", "0302");
        respDetailMap.put("2325", "0303");
        respDetailMap.put("2326", "0108");
        respDetailMap.put("2330", "0109");
        respDetailMap.put("2334", "0304");
        respDetailMap.put("2343", "0305");
        respDetailMap.put("2344", "0110");
        respDetailMap.put("2345", "0306");
        respDetailMap.put("2400", "0111");
        respDetailMap.put("2401", "0112");
        respDetailMap.put("2402", "0113");
        respDetailMap.put("2403", "0114");
        respDetailMap.put("2404", "0114");
        respDetailMap.put("5000", "0308");

        respDetailMap.put("4001", "");
        respDetailMap.put("4002", "");
        respDetailMap.put("4003", "");
        respDetailMap.put("4004", "");
        respDetailMap.put("4005", "");
        respDetailMap.put("4006", "");
        respDetailMap.put("1103", "0201");
        respDetailMap.put("1201", "0202");
        respDetailMap.put("1302", "0203");
        respDetailMap.put("1305", "0204");
        respDetailMap.put("1399", "0205");
        respDetailMap.put("2208", "0206");
        respDetailMap.put("2301", "0207");
        respDetailMap.put("2302", "0208");
        respDetailMap.put("2306", "0209");
        respDetailMap.put("2308", "0210");
        respDetailMap.put("2309", "0211");
        respDetailMap.put("2324", "0212");
        respDetailMap.put("2327", "0213");
        respDetailMap.put("2329", "0214");
        respDetailMap.put("2341", "0215");
        respDetailMap.put("2342", "0216");
        respDetailMap.put("2405", "0217");

        return respDetailMap;
    }
}