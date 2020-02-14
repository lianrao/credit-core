package com.wanda.credit.ds.client.yinlian;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.yinlian.sdk.AcpService;
import com.wanda.credit.ds.client.yinlian.sdk.CertUtil;
import com.wanda.credit.ds.client.yinlian.sdk.SDKConfig;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author shiwei
 * @version $$Id: BaseAnhuizxDSRequestor, V 0.1 2017/11/15 14:58 shiwei Exp $$
 */
public abstract class BaseYinlianDSRequestor extends BaseDataSourceRequestor
    implements IDataSourceRequestor{

    private final Logger logger = LoggerFactory.getLogger(BaseYinlianDSRequestor.class);

    @Autowired
    protected PropertyEngine propertyEngine;

    @Autowired
    protected IAllAuthCardService allAuthCardService;

    //默认配置的是UTF-8
    public static String encoding = "UTF-8";

    // 商户发送交易时间 格式:YYYYMMDDhhmmss
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    protected Map<String, String> getResponseByHttp(String prefix, Map<String, Object> dataMap, String trade_id){
        try {
            Map<String, String> retMap = new HashMap<>();

            String name = String.valueOf(dataMap.get("name"));
            String cardId = String.valueOf(dataMap.get("cardId"));
            String cardNo = String.valueOf(dataMap.get("cardNo"));
            String phone = "";
            if(dataMap.containsKey("phoneNo")){
                phone = String.valueOf(dataMap.get("phoneNo"));
            }

            String merId = propertyEngine.readById("ds_yl_merId");//商户号
            String orderId = trade_id;
            String txnTime = this.getCurrentTime();//YYYYMMddHHmmss

            Map<String, String> data = new HashMap<String, String>();

            /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
            data.put("version", SDKConfig.getConfig().getVersion());                 //版本号
            data.put("encoding", this.encoding);               //字符集编码 可以使用UTF-8,GBK两种方式
            data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
            data.put("txnType", "72");                             //交易类型 00-默认
            data.put("txnSubType", "12");                          //交易子类型 银行卡认证
            data.put("bizType", "000803");                         //业务类型 代收

            //B136:银行卡二要素（卡号、姓名 ）
            //B144：银行卡二要素（卡号、证件类型、证件号）
            //B132：银行卡二要素（卡号、手机号）
            //B152：银行卡三要素（卡号、身份证号、姓名）
            //B156：银行卡四要素（卡号、手机号、身份证号、姓名）
            if(StringUtil.isEmpty(phone)){
                data.put("productId", "B152");
            }else {
                data.put("productId", "B156");
            }

            /***商户接入参数***/
            data.put("merId", merId);                  			   //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
            data.put("accessType", "0");                           //接入类型，商户接入固定填0，不需修改

            /***要调通交易以下字段必须修改***/
            data.put("orderId", orderId);                 //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
            data.put("txnTime", txnTime);                 //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间

            Map<String,String> customerInfoMap = new HashMap<String,String>();
            customerInfoMap.put("customerNm", name);					//姓名
            if(!StringUtil.isEmpty(phone)) {
                customerInfoMap.put("phoneNo", phone);                    //手机号
            }
            customerInfoMap.put("certifTp", "01");						//证件类型
            customerInfoMap.put("certifId", cardNo);		//证件号码

            String accNo = cardId;//银行卡号 交易账号。请求时使用加密公钥对交易账号加密，并做Base64编码后上送；应答时如需返回，则使用签名私钥进行解密。

            ////////////如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo，pin和phoneNo，cvn2，expired加密（如果这些上送的话），对敏感信息加密使用：
//            String accNo1 = AcpService.encryptData(accNo, this.encoding);  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号

            data.put("accNo", accNo);

            /*当需要加密敏感信息如CVN2、有效期、密码及其他账户信息如卡号、手机号时，
		        填写加密公钥证书的Serial Number*/
            data.put("encryptCertId",AcpService.getEncryptCertId());       //加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下
            //敏感信息加密方法-如果商户号开通了【商户对敏感信息加密】的权限那么需要
//            String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap, null, this.encoding);
            String customerInfoStr = AcpService.getCustomerInfo(customerInfoMap, null, this.encoding);

            data.put("customerInfo", customerInfoStr);

            Map<String, String> reqData = AcpService.sign(data,this.encoding);			//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
            String url = SDKConfig.getConfig().getBackRequestUrl();								   //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
            Map<String, String> rspData = AcpService.post(reqData,url,this.encoding);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

            if(!rspData.isEmpty()){
                if(AcpService.validate(rspData, this.encoding)){
                    logger.info("{} 验证签名成功", prefix);
                    logger.info("{} 应答码：{}", prefix, rspData.get("respCode"));
                    retMap.put("respCode", rspData.get("respCode"));
                    retMap.put("respMsg", rspData.get("respMsg"));
                    if(("00").equals(rspData.get("respCode"))){//如果查询交易成功
                        logger.info("{} 原始应答码：{}", prefix, rspData.get("origRespCode"));
                        retMap.put("origRespCode", rspData.get("origRespCode"));
                    }
                }else{
                    logger.info("{} 验证签名失败", prefix);
                    retMap.put("errMsg", "验证签名失败");
                }
            }else{
                //未返回正确的http状态
                logger.info("{} 未获取到返回报文或返回http状态码非200", prefix);
                retMap.put("errMsg", "未获取到返回报文或返回http状态码非200");
            }

            return retMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{} http请求异常 银总联 api接口 {}", prefix, e);
            return null;
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

    /**
     * 加载配置文件
     */
    public static Properties systemProps;

    public static void init(){
        if(systemProps == null){
            systemProps = readProperties("/yinlian/acp_sdk.properties");
            //加载配置文件
            SDKConfig.getConfig().loadProperties(systemProps);
            //初始化证书
            CertUtil.init();
        }
    }

    private static Properties readProperties(String fileName) {
        InputStream in = null;
        Properties props = null;
        try {
            props = new Properties();
            in =BaseYinlianDSRequestor.class.getResourceAsStream(fileName);
            props.load(new InputStreamReader(in, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    protected Map<? extends String, ? extends Object> visitBusiData(
            String trade_id, Map<String, String> data, boolean isOut) {
        Map<String,Object> ret = new HashMap<String,Object>();

        String code = data.get("respCode");
        String message = data.get("respMsg");
        String respCode = "";
        String resMsg = message;
        String detailRespCode = "";

        if("00".equals(code)){
            respCode = "2000";
            resMsg = "认证一致";
        }else if("61".equals(code)){
            respCode = "2001";
            resMsg = "认证不一致";
            detailRespCode = "0103";
        }else if("63".equals(code)){
            respCode = "2001";
            resMsg = "认证不一致";
            detailRespCode = "0104";
        }else if("66".equals(code)){
            respCode = "2001";
            resMsg = "认证不一致";
            detailRespCode = "0106";
        }else if("68".equals(code)){
            respCode = "2003";
            resMsg = "不支持验证";
            detailRespCode = "0302";
        }else if("77".equals(code)){
            respCode = "2003";
            resMsg = "不支持验证";
            detailRespCode = "0306";
        }else if("01".equals(code)){
            respCode = "warn_130";
        }else {
            respCode = "err_015";
        }

        ret.put("respCode", respCode);
        ret.put("respDesc", resMsg);
        ret.put("detailRespCode", detailRespCode);

        return ret;
    }

    protected boolean isSuccess(Map<String, String> map) {
        if(map == null || map.containsKey("errMsg")){
            return false;
        }
        return true;
    }
}
