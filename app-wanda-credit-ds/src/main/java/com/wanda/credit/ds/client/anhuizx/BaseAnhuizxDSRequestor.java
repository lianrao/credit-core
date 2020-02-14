package com.wanda.credit.ds.client.anhuizx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.Conts;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.anhuizx.service.AhzxAES;
import com.wanda.credit.ds.client.anhuizx.service.HttpClientUtil;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author shiwei
 * @version $$Id: BaseAnhuizxDSRequestor, V 0.1 2017/11/15 14:58 shiwei Exp $$
 */
public abstract class BaseAnhuizxDSRequestor extends BaseDataSourceRequestor
    implements IDataSourceRequestor{

    private final Logger logger = LoggerFactory.getLogger(BaseAnhuizxDSRequestor.class);

    @Autowired
    protected PropertyEngine propertyEngine;

    @Autowired
    protected IAllAuthCardService allAuthCardService;

    protected String getResponseByHttp(String prefix, Map<String, Object> dataMap){
        try {
            String url = propertyEngine.readById("ds_ahzx_bankcard_url");  //请求url，安徽征信提供

            String userName = propertyEngine.readById("ds_ahzx_userName");  //用户名称，安徽征信提供
            String HEX_AES_128_PASSWORD = propertyEngine.readById("ds_ahzx_password");  //用户对应密码，安徽征信提供

            AhzxAES AhzxAES = new AhzxAES(Hex.decodeHex(HEX_AES_128_PASSWORD.toCharArray()));

            Map<String, String> params = new HashMap<String, String>();

            //根据接口文档传递相应参数
            //系统参数
            params.put("interfaceNo", propertyEngine.readById("ds_ahzx_interfaceNo"));  ////接口号，安徽征信提供
            params.put("unitId", propertyEngine.readById("ds_ahzx_unitId"));   //接入机构号，安徽征信提供
            params.put("customerId", propertyEngine.readById("ds_ahzx_customerId"));   //客户id，安徽征信提供
            params.put("productId", propertyEngine.readById("ds_ahzx_productId"));  //产品id，安徽征信提供
            params.put("loginName", userName);  //客户名称，安徽征信提供
            params.put("orderId", propertyEngine.readById("ds_ahzx_orderId")); //产品订单编号，安徽征信提供
            params.put("channel", propertyEngine.readById("ds_ahzx_channel"));   //渠道编号，安徽征信提供
            params.put("department", propertyEngine.readById("ds_ahzx_department"));//部门编号，安徽征信提供
            params.put("tradeDate", getDateTimeNow("yyyy-MM-dd"));
            //业务参数

            params.put("idType", String.valueOf(dataMap.get("idType")));       //证件类型(01 身份证)
            params.put("idNo", String.valueOf(dataMap.get("idNo")));      //身份证号
            params.put("custName", String.valueOf(dataMap.get("custName")));          //姓名
            params.put("phoneNo", String.valueOf(dataMap.get("phoneNo")));           //电话
            params.put("bankCardno", String.valueOf(dataMap.get("bankCardno")));        //银行卡号

            String paramJson = JSON.toJSONString(params);

            // 2.1 AES 128
            byte[] encryptBytes = AhzxAES.encrypt(paramJson);
            // 2.2 BASE64
            String base64String = new sun.misc.BASE64Encoder().encode(encryptBytes);
//            logger.info("{} base64结果：{}", prefix, base64String);
            String response=null;

            Map<String, String> heads = new HashMap<String, String>();
            heads.put("afemarkId", userName);  //客户名称需要加入到请求头
            response = HttpClientUtil.post(url, base64String, heads, "application/json");

            logger.info("{} 请求结果response={}", prefix, response);

            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            byte[] encryptAESResponseByte = decoder.decodeBuffer(response);
            byte[] responseByte = AhzxAES.decrypt(encryptAESResponseByte);
            String result = new String(responseByte,"utf-8");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{} http请求异常 安徽征信 api接口 {}", prefix, e);
            return "";
        }
    }

    /**
     * 获取当前时间字符串
     * @param mould "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    private String getDateTimeNow(String mould){
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(mould);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    protected boolean isSuccess(JSONObject rspData) {
        if(getRespDetailMap().containsKey(rspData.get("code"))){
            return true;
        }
        return false;
    }

    protected String buildTag(String trade_id, TreeMap<String, Object> rspData) {
        Object res = rspData.get("respCode");
        String resstr = null;
        if(res != null ){
            resstr = res.toString();
            if("2000".equals(resstr) || "2001".equals(resstr)){
                return Conts.TAG_TST_SUCCESS;
            }
        }
        return Conts.TAG_TST_FAIL;
    }

    //{"retDate":"2017-02-09 15:08:32","reqSerialNo":"123871483274234",
    // "code":"000021","transNo":"2017020900000058","msg":"四要素验证未通过，要素不一致"}
    protected Map<? extends String, ? extends Object> visitBusiData(
            String trade_id, JSONObject data, boolean isOut) {
        Map<String,Object> ret = new HashMap<String,Object>();

        String code = String.valueOf(data.get("code"));
        String message = String.valueOf(data.get("msg"));
        String respCode = code;
        String resMsg = message;
        String detailRespCode = "";
        String detailRespMsg = "";

        Map<String, String> respCodeMap = getRespCodeMap();
        Map<String, String> respDetailMap = getRespDetailMap();

        if(respCodeMap.containsKey(code)){
            respCode = respCodeMap.get(code);
        }

        if("2000".equals(respCode)){
            resMsg = "认证一致";
        }else if("2001".equals(respCode)){
            resMsg = "认证不一致";
        }else if("2003".equals(respCode)){
            resMsg = "不支持验证";
        }else {
            resMsg = "系统异常";
        }

        ret.put("respCode", respCode);
        ret.put("respDesc", resMsg);

        if(respDetailMap.containsKey(code)){
            detailRespCode = respDetailMap.get(code);
            detailRespMsg = message;
            ret.put("detailRespCode", detailRespCode);
        }

        if (!isOut){
            ret.put("detailRespMsg", detailRespMsg);
        }

        return ret;
    }

    public Map<String, String> getRespDetailMap(){
        Map<String, String> respDetailMap = new HashMap<>();

        respDetailMap.put("30100", "");
        respDetailMap.put("30101", "0206");
        respDetailMap.put("31103", "0206");
        respDetailMap.put("31104", "0102");
        respDetailMap.put("31105", "0106");
        respDetailMap.put("31112", "0108");
        respDetailMap.put("31114", "0103");
        respDetailMap.put("31121", "0120");
        respDetailMap.put("31134", "0109");
        respDetailMap.put("31140", "0302");
        respDetailMap.put("31141", "0113");
        respDetailMap.put("31143", "0114");
        respDetailMap.put("31154", "0112");
        respDetailMap.put("31155", "0303");
        respDetailMap.put("31157", "0304");
        respDetailMap.put("31161", "");
        respDetailMap.put("31162", "0304");
        respDetailMap.put("31175", "0303");
        respDetailMap.put("31177", "");
        respDetailMap.put("311C1", "0102");
        respDetailMap.put("32170", "");
        respDetailMap.put("32171", "0203");
        respDetailMap.put("32172", "0209");
        respDetailMap.put("32173", "0206");
        respDetailMap.put("32174", "0206");
        respDetailMap.put("32175", "0206");
        respDetailMap.put("32176", "0206");
        respDetailMap.put("32178", "0206");
        respDetailMap.put("32179", "0206");
        respDetailMap.put("32180", "0206");
        respDetailMap.put("32181", "");
        respDetailMap.put("31182", "0302");
        respDetailMap.put("32088", "0206");
        respDetailMap.put("32092", "0101");
        respDetailMap.put("32093", "0206");
        respDetailMap.put("32094", "0206");
        respDetailMap.put("32096", "0206");
        respDetailMap.put("32097", "");
        respDetailMap.put("32098", "0206");
        respDetailMap.put("32099", "0203");
        respDetailMap.put("320A0", "0206");
        respDetailMap.put("320A6", "0206");
        respDetailMap.put("32090", "0206");

        respDetailMap.put("40100", "");
        respDetailMap.put("40101", "0206");
        respDetailMap.put("41103", "0206");
        respDetailMap.put("41104", "0102");
        respDetailMap.put("41105", "0106");
        respDetailMap.put("41112", "0108");
        respDetailMap.put("41114", "0103");
        respDetailMap.put("41121", "0120");
        respDetailMap.put("41134", "0109");
        respDetailMap.put("41140", "0302");
        respDetailMap.put("41141", "0113");
        respDetailMap.put("41143", "0114");
        respDetailMap.put("41154", "0112");
        respDetailMap.put("41155", "0303");
        respDetailMap.put("41157", "0304");
        respDetailMap.put("41161", "");
        respDetailMap.put("41162", "0304");
        respDetailMap.put("41175", "0303");
        respDetailMap.put("41177", "");
        respDetailMap.put("411C1", "0102");
        respDetailMap.put("42170", "");
        respDetailMap.put("42171", "0203");
        respDetailMap.put("42172", "0209");
        respDetailMap.put("42173", "0206");
        respDetailMap.put("42174", "0206");
        respDetailMap.put("42175", "0206");
        respDetailMap.put("42176", "0206");
        respDetailMap.put("42178", "0206");
        respDetailMap.put("42179", "0206");
        respDetailMap.put("42180", "0206");
        respDetailMap.put("42181", "");
        respDetailMap.put("41182", "0302");
        respDetailMap.put("42088", "0206");
        respDetailMap.put("42092", "0101");
        respDetailMap.put("42093", "0206");
        respDetailMap.put("42094", "0206");
        respDetailMap.put("42096", "0206");
        respDetailMap.put("42097", "");
        respDetailMap.put("42098", "0206");
        respDetailMap.put("42099", "0203");
        respDetailMap.put("420A0", "0206");
        respDetailMap.put("420A6", "0206");
        respDetailMap.put("42090", "0206");

        return respDetailMap;
    }

    public Map<String, String> getRespCodeMap(){
        Map<String, String> respCodeMap = new HashMap<>();

        respCodeMap.put("30100", "2000");
        respCodeMap.put("30101", "err_015");
        respCodeMap.put("31103", "err_015");
        respCodeMap.put("31104", "2001");
        respCodeMap.put("31105", "2001");
        respCodeMap.put("31112", "2001");
        respCodeMap.put("31114", "2001");
        respCodeMap.put("31121", "2001");
        respCodeMap.put("31134", "2001");
        respCodeMap.put("31140", "2003");
        respCodeMap.put("31141", "2001");
        respCodeMap.put("31143", "2001");
        respCodeMap.put("31154", "2001");
        respCodeMap.put("31155", "2003");
        respCodeMap.put("31157", "2003");
        respCodeMap.put("31161", "2000");
        respCodeMap.put("31162", "2003");
        respCodeMap.put("31175", "2003");
        respCodeMap.put("31177", "warn_111");
        respCodeMap.put("311C1", "2001");
        respCodeMap.put("32170", "warn_304");
        respCodeMap.put("32171", "err_015");
        respCodeMap.put("32172", "err_015");
        respCodeMap.put("32173", "err_015");
        respCodeMap.put("32174", "err_015");
        respCodeMap.put("32175", "err_015");
        respCodeMap.put("32176", "err_015");
        respCodeMap.put("32178", "err_015");
        respCodeMap.put("32179", "err_015");
        respCodeMap.put("32180", "err_015");
        respCodeMap.put("32181", "warn_304");
        respCodeMap.put("31182", "2003");
        respCodeMap.put("32088", "err_015");
        respCodeMap.put("32092", "2001");
        respCodeMap.put("32093", "err_015");
        respCodeMap.put("32094", "err_015");
        respCodeMap.put("32096", "err_015");
        respCodeMap.put("32097", "warn_303");
        respCodeMap.put("32098", "err_015");
        respCodeMap.put("32099", "err_015");
        respCodeMap.put("320A0", "err_015");
        respCodeMap.put("320A6", "err_015");
        respCodeMap.put("32090", "err_015");

        respCodeMap.put("40100", "2000");
        respCodeMap.put("40101", "err_015");
        respCodeMap.put("41103", "err_015");
        respCodeMap.put("41104", "2001");
        respCodeMap.put("41105", "2001");
        respCodeMap.put("41112", "2001");
        respCodeMap.put("41114", "2001");
        respCodeMap.put("41121", "2001");
        respCodeMap.put("41134", "2001");
        respCodeMap.put("41140", "2003");
        respCodeMap.put("41141", "2001");
        respCodeMap.put("41143", "2001");
        respCodeMap.put("41154", "2001");
        respCodeMap.put("41155", "2003");
        respCodeMap.put("41157", "2003");
        respCodeMap.put("41161", "2000");
        respCodeMap.put("41162", "2003");
        respCodeMap.put("41175", "2003");
        respCodeMap.put("41177", "warn_111");
        respCodeMap.put("411C1", "2001");
        respCodeMap.put("42170", "warn_304");
        respCodeMap.put("42171", "err_015");
        respCodeMap.put("42172", "err_015");
        respCodeMap.put("42173", "err_015");
        respCodeMap.put("42174", "err_015");
        respCodeMap.put("42175", "err_015");
        respCodeMap.put("42176", "err_015");
        respCodeMap.put("42178", "err_015");
        respCodeMap.put("42179", "err_015");
        respCodeMap.put("42180", "err_015");
        respCodeMap.put("42181", "warn_304");
        respCodeMap.put("41182", "2003");
        respCodeMap.put("42088", "err_015");
        respCodeMap.put("42092", "2001");
        respCodeMap.put("42093", "err_015");
        respCodeMap.put("42094", "err_015");
        respCodeMap.put("42096", "err_015");
        respCodeMap.put("42097", "warn_303");
        respCodeMap.put("42098", "err_015");
        respCodeMap.put("42099", "err_015");
        respCodeMap.put("420A0", "err_015");
        respCodeMap.put("420A6", "err_015");
        respCodeMap.put("42090", "err_015");

        return respCodeMap;
    }
}
