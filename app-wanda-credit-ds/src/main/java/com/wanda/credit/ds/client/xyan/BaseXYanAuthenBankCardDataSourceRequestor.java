package com.wanda.credit.ds.client.xyan;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.ds.BaseDataSourceRequestor;

/**
 * @description  
 * @author lijiong.tang 
 * @version 1.0
 * @createdate 2016年11月29日 上午9:47:32 
 *  
 */
public class BaseXYanAuthenBankCardDataSourceRequestor 
       extends BaseDataSourceRequestor{
	protected static final String cer_file_base_path="/depends/ds/xyan/";

    private final Logger logger = LoggerFactory
            .getLogger(BaseXYanAuthenBankCardDataSourceRequestor.class);

    protected Map<? extends String, ? extends Object> visitBusiData(
            String trade_id, JSONObject data) {
        Map<String,Object> ret = new HashMap<String,Object>();

        JSONObject data_obj = null;

        if (data.containsKey("success")
                && data.getBoolean("success")) {
            data_obj = data.getJSONObject("data");
            if (data_obj != null && data_obj.get("code") != null) {
                String code = data_obj.getString("code");
                String msg = data_obj.getString("desc");
                String orgCode = data_obj.getString("org_code");
                String orgMsg = data_obj.getString("org_desc");
                logger.info("{} 新颜返回code信息为 {}", trade_id, code);

                if("0".equals(code)){
                    ret.put("respCode", "2000");
                    ret.put("respDesc", "认证一致");
                }else if("1".equals(code)){
                    switch (String.valueOf(orgCode)){
                        case "null":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "");
                            break;
                        case "0007":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0102");
                            break;
                        case "0009":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0103");
                            break;
                        case "0004":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0104");
                            break;
                        case "0012":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0104");
                            break;
                        case "0001":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0106");
                            break;
                        case "0002":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0106");
                            break;
                        case "0003":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0106");
                            break;
                        case "0025":
                            ret.put("respCode", "2003");
                            ret.put("respDesc", "不支持验证");
                            ret.put("detailRespCode", "0302");
                            break;
                        case "0020":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0112");
                            break;
                        case "0024":
                            ret.put("respCode", "2003");
                            ret.put("respDesc", "不支持验证");
                            ret.put("detailRespCode", "0307");
                            break;
                        case "0016":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0115");
                            break;
                        case "0014":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0116");
                            break;
                        case "0015":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0117");
                            break;
                        case "0013":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0118");
                            break;
                        default:
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "");
                            break;
                    }
                }else if("2".equals(code)){
                    ret.put("respCode", "err_015");
                    ret.put("respDesc", msg);
                }else if("3".equals(code)){
                    switch (String.valueOf(orgCode)){
                        case "0022":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0101");
                            break;
                        case "0008":
                            ret.put("respCode", "2001");
                            ret.put("respDesc", "认证不一致");
                            ret.put("detailRespCode", "0101");
                            break;
                        case "0023":
                            ret.put("respCode", "2003");
                            ret.put("respDesc", "不支持验证");
                            ret.put("detailRespCode", "0302");
                            break;
                        case "0026":
                            ret.put("respCode", "2003");
                            ret.put("respDesc", "不支持验证");
                            ret.put("detailRespCode", "0303");
                            break;
                        case "0017":
                            ret.put("respCode", "warn_130");
                            ret.put("respDesc", msg);
                            ret.put("detailRespCode", "");
                            break;
                        case "null":
                            ret.put("respCode", "warn_130");
                            ret.put("respDesc", msg);
                            ret.put("detailRespCode", "");
                            break;
                        default:
                            ret.put("respCode", "warn_130");
                            ret.put("respDesc", msg);
                            ret.put("detailRespCode", "");
                            break;
                    }
                }else if("9".equals(code)){
                    ret.put("respCode", "err_015");
                    ret.put("respDesc", msg);
                }else {

                }
            }
        }else {
            Object errorMsgObj = data.get("errorMsg");
            Object errorCodeObj = data.get("errorCode");

            if ("S1016".equals(errorCodeObj) || "S1015".equals(errorCodeObj)
                    || "S2004".equals(errorCodeObj) || "S2005".equals(errorCodeObj)
                    || "S2000".equals(errorCodeObj) || "S2002".equals(errorCodeObj)) {
                // 构建输出
                ret.put("respCode", "2003");
                ret.put("respDesc", "不支持验证");
                if("S1016".equals(errorCodeObj) || "S1015".equals(errorCodeObj)){
                    ret.put("detailRespCode", "0302");
                }else if("S2004".equals(errorCodeObj) || "S2005".equals(errorCodeObj)){
                    ret.put("detailRespCode", "0301");
                }else if("S2000".equals(errorCodeObj) || "S2002".equals(errorCodeObj)){
                    ret.put("detailRespCode", "0308");
                }
            }else if("S1000".equals(errorCodeObj)){
                ret.put("respCode", "warn_304");
                ret.put("respDesc", errorMsgObj);
            }else if("S1001".equals(errorCodeObj)){
                ret.put("respCode", "err_015");
                ret.put("respDesc", errorMsgObj);
                ret.put("detailRespCode", "0203");
            }else if("S1002".equals(errorCodeObj) || "S1004".equals(errorCodeObj)
                    || "S1005".equals(errorCodeObj) || "S1006".equals(errorCodeObj)
                    || "S1007".equals(errorCodeObj) || "S1010".equals(errorCodeObj)
                    || "S1011".equals(errorCodeObj) || "S1012".equals(errorCodeObj)
                    || "S1013".equals(errorCodeObj) || "S1014".equals(errorCodeObj)){
                ret.put("respCode", "err_015");
                ret.put("respDesc", errorMsgObj);
                ret.put("detailRespCode", "0206");
            }else if("S2001".equals(errorCodeObj) || "S2003".equals(errorCodeObj)
                    || "S2006".equals(errorCodeObj)){
                ret.put("respCode", "err_015");
                ret.put("respDesc", errorMsgObj);
                ret.put("detailRespCode", "0209");
            }else if("S0001".equals(errorCodeObj)){
                ret.put("respCode", "err_015");
                ret.put("respDesc", errorMsgObj);
                ret.put("detailRespCode", "0214");
            }else {
                ret.put("respCode", "err_015");
                ret.put("respDesc", errorMsgObj);
            }
        }

        return ret;
    }
}
