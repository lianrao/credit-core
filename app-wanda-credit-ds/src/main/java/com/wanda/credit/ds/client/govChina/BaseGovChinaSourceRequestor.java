package com.wanda.credit.ds.client.govChina;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bankcomm.gbicc.util.base64.BASE64Decoder;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.govInfo.Gov_basicinfo_result;
import com.wanda.credit.ds.dao.domain.govInfo.Gov_creditcode_result;
import com.wanda.credit.ds.dao.domain.govInfo.Gov_pubpenalty_result;
import com.wanda.credit.ds.dao.domain.govInfo.Gov_pubpermission_result;
import com.wanda.credit.ds.dao.iface.govInfo.IGovInfoService;

public class BaseGovChinaSourceRequestor extends BaseDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(BaseGovChinaSourceRequestor.class);
	private static final int timeout = Integer.parseInt(DynamicConfigLoader
			.get("sys.credit.client.http.timeout"));
	@Autowired
	IGovInfoService govService;
	/**
	 * 获取国家信息中心url
	 * @param queryType
	 * @param propertyEngine
	 * @return
	 */
	protected String getUrl(String queryType,IPropertyEngine propertyEngine){
		String url = propertyEngine.readById("ds_gov_china_url");
		//查询类型:1-BasicInfo,2-PubPermission,3-Pubpenalty,4-CreditCode,5-Record
		if("1".equals(queryType)){
			url = url + propertyEngine.readById("ds_gov_china_basicInfo");
		}else if("2".equals(queryType)){
			url = url + propertyEngine.readById("ds_gov_china_pubPermission");
		}else if("3".equals(queryType)){
			url = url + propertyEngine.readById("ds_gov_china_pubpenalty");
		}else if("4".equals(queryType)){
			url = url + propertyEngine.readById("ds_gov_china_CreditCode");
		}else if("5".equals(queryType)){
			url = url + propertyEngine.readById("ds_gov_china_record");
		}
		return url;
	}
	/**
	 * 判断查询类型是否在1-5内
	 * @param queryType
	 * @param propertyEngine
	 * @return
	 */
	protected boolean isQueryType(String queryType,IPropertyEngine propertyEngine){
		String types = propertyEngine.readById("ds_gov_china_types");
		for(String type:types.split(",")){
			if(type.equals(queryType)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 网络请求获取公司信息
	 * @param url
	 * @param header
	 * @param params
	 * @return
	 */
	public  String httpGet(String prefix,String url, Map<String, String> header, Map<String, String> params) {
		/************start************/
		// 注释掉之前直接获取httpClient对象的方式
        // HttpClient httpClient = HttpClients.createDefault();
		// 通过SSl相关代码进行一系列的设置
		HttpClient httpClient = null;
		KeyStore keyStore = null;
		FileInputStream instream = null;
		SSLContext sslcontext = null;
		SSLConnectionSocketFactory sslsf = null;
		// 定义文件名称

		try {
			// 获取keyStore实例化对象
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			// 定义输入流
			instream = new FileInputStream(new File(this.getClass().getClassLoader().getResource("/depends/ds/ovoca/ovoca").getPath()));
			// 输入密码并加载
			keyStore.load(instream, "changeit".toCharArray());
			// 创建sslcontext对象
			sslcontext = SSLContexts.custom()
					.loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
					.build();
			// 创建sslsf对象
			sslsf = new SSLConnectionSocketFactory(sslcontext,
					new String[] { "TLSv1" }, null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		} catch (KeyManagementException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (CertificateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
     	// 若ssl套接字连接工厂创建成功
	   	if (sslsf != null) {
	   		System.out.println(sslsf);
	   		// 定义httpClient对象
	   		httpClient = HttpClients.custom()
	   				.setSSLSocketFactory(sslsf).build();
	   	}
	   	/**************end****************/
        StringBuilder sbUrl = new StringBuilder(url+"?");
        for (String s : params.keySet()) {
            sbUrl.append(s).append("=").append(params.get(s)).append("&");
        }
        if (!params.isEmpty()) {
            sbUrl.delete(sbUrl.length() - 1, sbUrl.length());
        }
        String requestUrl = sbUrl.toString().replaceAll("\"", "%22").replaceAll(" ", "%20");
        HttpGet httpGet = new HttpGet(requestUrl);
        RequestConfig requestConfig = RequestConfig.custom().
        		setConnectTimeout(timeout).
        		setConnectionRequestTimeout(timeout).
        		setSocketTimeout(timeout).build();
        httpGet.setConfig(requestConfig);
        if(header!=null){
        	for (Map.Entry<String, String> entry : header.entrySet()) {				
        		httpGet.setHeader(entry.getKey(), entry.getValue());
			}
        }
        HttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (SocketTimeoutException ste) {
        	logger.error("{} 读取超时：{}",prefix,ste);
            //OverTimeMonitorUtil.overTimeBaiDu(null,requestUrl,"第三方接口",null);
        } catch (Exception e) {
        	logger.error("{} 发送错误:{}",prefix,ExceptionUtil.getTrace(e));
        }
        return null;
    }
	/**
	 * 返回信息进行AES解密
	 * @param prefix
	 * @param context
	 * @param key
	 * @return
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public  String decrypt(String prefix,String context,String key) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, 
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException{
		if(key ==null){
			logger.error("{} key为空",prefix);
		}
		if(key.length()!=16){
			logger.error("{} key长度不是16位",prefix);
		}
		byte[] raw = key.getBytes("utf-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return  new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(context))); 
	}
	/**
	 * 保存信息中心返回数据
	 * @param prefix
	 * @param rspStr
	 * @param queryType
	 */
	protected void saveInfo(String trade_id,String rspStr,String queryType) {
		logger.info("{} 返回信息保存开始...",trade_id);
		JSONObject json = JSONObject.parseObject(rspStr);
		logger.info("{} 响应结果", json);// extr
			if(json.get("results")!=null){
				JSONArray results = JSONObject.parseArray(
						String.valueOf(json.get("results")));
				//查询类型:1-BasicInfo,2-PubPermission,3-Pubpenalty,4-CreditCode,5-Record
				if("1".equals(queryType)){
					logger.info("{} 查询信息为基本信息",trade_id);
					List<Gov_basicinfo_result> basic_list = new ArrayList<Gov_basicinfo_result>();
					for(Object obj:results){
						JSONObject basic_info = (JSONObject) obj;
						Gov_basicinfo_result tmp = JSONObject.parseObject(basic_info.toJSONString(),
								Gov_basicinfo_result.class);
						tmp.setTrade_id(trade_id);
						tmp.setQuerytype(queryType);
						tmp.setStatus(json.getString("status"));
						tmp.setMessage(json.getString("message"));
						basic_list.add(tmp);
					}
					govService.batchInfoSave(basic_list);
				}else if("2".equals(queryType)){
					logger.info("{} 查询信息为行政许可信息",trade_id);
					List<Gov_pubpermission_result> pubpermission_list = new ArrayList<Gov_pubpermission_result>();
					for(Object obj:results){
						JSONObject pubpermission_info = (JSONObject) obj;
						Gov_pubpermission_result tmp = JSONObject.parseObject(pubpermission_info.toJSONString(),
								Gov_pubpermission_result.class);
						tmp.setTrade_id(trade_id);
						tmp.setQuerytype(queryType);
						tmp.setStatus(json.getString("status"));
						tmp.setMessage(json.getString("message"));
						pubpermission_list.add(tmp);
					}
					govService.batchInfoSave(pubpermission_list);
				}else if("3".equals(queryType)){
					logger.info("{} 查询信息为行政处罚信息",trade_id);
					List<Gov_pubpenalty_result> pubpenalty_list = new ArrayList<Gov_pubpenalty_result>();
					for(Object obj:results){
						JSONObject pubpenalty_info = (JSONObject) obj;
						Gov_pubpenalty_result tmp = JSONObject.parseObject(
								pubpenalty_info.toJSONString(), Gov_pubpenalty_result.class);
						tmp.setTrade_id(trade_id);
						tmp.setQuerytype(queryType);
						tmp.setStatus(json.getString("status"));
						tmp.setMessage(json.getString("message"));
						pubpenalty_list.add(tmp);
					}
					govService.batchInfoSave(pubpenalty_list);
				}else if("4".equals(queryType)){
					logger.info("{} 查询信息为社会统一信用代码信息",trade_id);
					List<Gov_creditcode_result> creditcode_list = new ArrayList<Gov_creditcode_result>(); 
					for(Object obj:results){
						JSONObject creditcode_info = (JSONObject) obj;
						Gov_creditcode_result tmp = JSONObject.parseObject(
								creditcode_info.toJSONString(), Gov_creditcode_result.class);
						tmp.setTrade_id(trade_id);
						tmp.setQuerytype(queryType);
						tmp.setRsp_status(json.getString("status"));
						tmp.setMessage(json.getString("message"));
						creditcode_list.add(tmp);
					}
					govService.batchInfoSave(creditcode_list);
				}
			}
	}
}
