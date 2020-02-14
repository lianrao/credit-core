package com.wanda.credit.ds.client.baiduFace;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.counter.GlobalCounter;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;

/**
 * @author liunan
 */
public class BaseBaiduFaceRequestor extends BaseDataSourceRequestor {

    private Logger logger = LoggerFactory.getLogger(BaseBaiduFaceRequestor.class);
    public final String FACE_TOKEN_ID_REDIS = "baidu_face_redisID";
    @Autowired
	public IPropertyEngine propertyEngine;
    
    /**
     * 获取权限token
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public  String getAuth(String trade_id,boolean isGetNewToken) {
    	if(!isGetNewToken){
			try {
				String token =  GlobalCounter.getString(FACE_TOKEN_ID_REDIS);
				if(!StringUtil.isEmpty(token)){
					return token;
				}
			} catch (ServiceException e) {
				logger.error("{} 从redis获取token失败：{}",trade_id,e.getMessage());
			}
		}
		logger.info("{} 重新获取token获取开始...",trade_id);
        // 官网获取的 API Key 更新为你注册的
        String clientId = propertyEngine.readById("baidu_face_tokenId");
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = propertyEngine.readById("baidu_face_tokenKey");
        String reult = getAuth(clientId, clientSecret);
        try {
			GlobalCounter.setString(FACE_TOKEN_ID_REDIS, reult);
		} catch (ServiceException e) {
			logger.error("{} token存储redis失败：{}",trade_id,e.getMessage());
		}
        return reult;
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public  String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = propertyEngine.readById("baidu_face_token_url");
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
        	Map<String,Object> rspDataMap = RequestHelper.doGetRetFull(getAccessTokenUrl, null, 
					null, true, null, "UTF-8");
            logger.info("获取的token信息:"+rspDataMap.get("res_body_str"));
            if(rspDataMap.get("res_body_str")!=null){
            	JSONObject jsonObject = JSONObject.parseObject(
            			String.valueOf(rspDataMap.get("res_body_str")));
            	return jsonObject.getString("access_token");
            }
            
        } catch (Exception e) {
        	logger.info("获取token失败！"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    /**
	 * 返回byte的数据大小对应的字符串
	 * @param size
	 * @return
	 */
	public static boolean formatStrSize(String str){
		if(str==null || str.length()==0)
			return true;
		long size = str.length();
		if(size>1024*1024){
			return true;
		}
		return false;
	}
}
