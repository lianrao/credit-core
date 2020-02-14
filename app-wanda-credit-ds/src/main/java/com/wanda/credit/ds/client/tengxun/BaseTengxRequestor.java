package com.wanda.credit.ds.client.tengxun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.base.util.image.ImageCut;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;

/**
 * @author liunan
 */
public class BaseTengxRequestor extends BaseDataSourceRequestor {

    private Logger logger = LoggerFactory.getLogger(BaseTengxRequestor.class);
    @Autowired
	public IPropertyEngine propertyEngine;
    @Autowired
	private FileEngine fileEngines;
    @SuppressWarnings("rawtypes")
	public  String doPost(String trade_id,String URL, Map<String, String> params,boolean https) throws NoSuchAlgorithmException, KeyManagementException
	{
    	logger.info("{} 腾讯OCR请求开始...",trade_id);
    	List<org.apache.http.NameValuePair> nvps = new ArrayList<org.apache.http.NameValuePair>();
		if(params!=null){	
			Iterator iter = params.entrySet().iterator();
			while(iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				nvps.add(new BasicNameValuePair(key, value));
			}
		}
		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpClient = new DefaultHttpClient();
		InputStream in = null;
		try{
			if(https){
				SSLContext ctx = SSLContext.getInstance("TLS");
				X509TrustManager tm = new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
				};
				ctx.init(null, new TrustManager[] { tm }, null);
				SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("https", 443, ssf));
				ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
				httpClient = new org.apache.http.impl.client.DefaultHttpClient(mgr); 
			}else{
				httpClient = new org.apache.http.impl.client.DefaultHttpClient();
			}
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);  //设定12秒超时，届时会弹出Exception 
			HttpConnectionParams.setSoTimeout(httpParams, 10000);
			org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost(URL);
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
			HttpResponse response = httpClient.execute(httpPost);
			URL = URLDecoder.decode(URL, "UTF-8");
			HttpEntity entity = response.getEntity();
			in = entity.getContent();
			BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String line="";
			while((line=bufferedReader.readLine()) != null){
				result.append(line);
			}
			//in.close();
		} catch (UnsupportedEncodingException e) {
			logger.info("{} 腾讯OCR请求异常01:{}",trade_id,ExceptionUtil.getTrace(e));
		} catch (ClientProtocolException e) {
			logger.info("{} 腾讯OCR请求异常02:{}",trade_id,ExceptionUtil.getTrace(e));
		} catch (IOException e) {
			logger.info("{} 腾讯OCR请求异常03:{}",trade_id,ExceptionUtil.getTrace(e)); 
		}finally{
			try {
				in.close();
				if(httpClient!=null){
					httpClient.close();
				}
			} catch (IOException e) {
				logger.info("{} 腾讯OCR请求异常04:{}",trade_id,ExceptionUtil.getTrace(e));
			}
		}
		logger.info("{} 腾讯OCR请求结束",trade_id);
		return result.toString();
	}
	public  String getSignature(String trade_id,HashMap<String,String> params, String app_key) throws IOException {
        // 先将参数以其参数名的字典序升序进行排序
		logger.info("{} 腾讯OCR获取sign开始...",trade_id);
		
		Map<String, String> sortedParams = new TreeMap<>(params);
        Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();
        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder baseString = new StringBuilder();
        for (Map.Entry<String, String> param : entrys) {
            //sign参数 和 空值参数 不加入算法
            if(param.getValue()!=null && !"".equals(param.getKey().trim()) && !"sign".equals(param.getKey().trim()) && !"".equals(param.getValue().trim())) {
                baseString.append(param.getKey().trim()).append("=").append(URLEncoder.encode(param.getValue().trim(),"UTF-8")).append("&");
            }
        }
        if(baseString.length() > 0 ) {
            baseString.deleteCharAt(baseString.length()-1).append("&app_key="+app_key);
        }
        logger.info("{} 腾讯OCR获取sign结束",trade_id);
        // 使用MD5对待签名串求签
        try {
        	String sign = MD5.uppEncodeByMD5(baseString.toString());
        	return sign;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }
	public String buildRsp(String trade_id,JSONObject json,Map<String,Object> rets,String resource_tag,String side)
    		throws JSONException{
    	logger.info("{} 出参包装开始...",trade_id);
    	if(json == null){
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源请求失败!");
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
			return resource_tag;
		}
		String error_code = json.getString("ret");
		if("0".equals(error_code)){
			int image_x = Integer.valueOf(propertyEngine.readById("tengxun_ocr_x"));
			int image_y = Integer.valueOf(propertyEngine.readById("tengxun_ocr_y"));
			int image_width = Integer.valueOf(propertyEngine.readById("tengxun_ocr_width"));
			int image_height = Integer.valueOf(propertyEngine.readById("tengxun_ocr_height"));
			HashMap<String, Object> retdata = new HashMap<String, Object>();
			HashMap<String, String> result = new HashMap<String, String>();
			JSONObject data = JSONObject.parseObject(json.getString("data"));
			if("front".equals(side)){
				logger.info("{} 正面解析开始...",trade_id);
				if(StringUtil.isEmpty(data.getString("name")) && StringUtil.isEmpty(data.getString("id"))){
					resource_tag = Conts.TAG_SYS_ERROR;
					rets.clear();
					rets.put(
							Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_YIDAO_RECOGNITION_ERRO);
					rets.put(Conts.KEY_RET_MSG,
							"证件识别失败,返回原因:" + json.getString("msg"));
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					return resource_tag;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");  			       
				String birday_str = "";
				try {
					String birday = data.getString("birth");
					Date birday_tmp = sdf.parse(birday);
					birday_str = DateUtil.getSimpleDate(birday_tmp, "yyyy年M月dd日");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		         
				String crop_image = data.getString("frontimage");
				String head_image = "";
				if(!StringUtil.isEmpty(crop_image)){
					String fpath = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, crop_image,trade_id);
					logger.info("{} 图片上传征信存储成功,照片ID为：{}", trade_id, fpath);
					String file_full_path = fileEngines.getFullPathById(fpath);
					try {
						head_image = ImageCut.cut_image(file_full_path, image_x, image_y, image_width, image_height);
					} catch (IOException e) {
						logger.info("{} 图片截取失败：{}", trade_id, e.getMessage());
					}
				}
				result.put("name", data.getString("name"));
				result.put("gender", data.getString("sex"));
				result.put("nation", data.getString("nation"));
				result.put("birthdate", birday_str);
				result.put("address", data.getString("address"));
				result.put("cardNo", data.getString("id"));
				result.put("side", side);
				result.put("head_image", head_image);
				result.put("cropped_image", data.getString("frontimage"));
				logger.info("{} 正面解析完成",trade_id);
			}else{
				logger.info("{} 反面解析开始...",trade_id);
				if(StringUtil.isEmpty(data.getString("authority")) && StringUtil.isEmpty(data.getString("valid_date"))){
					resource_tag = Conts.TAG_SYS_ERROR;
					rets.clear();
					rets.put(
							Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_YIDAO_RECOGNITION_ERRO);
					rets.put(Conts.KEY_RET_MSG,
							"证件识别失败,返回原因:" + json.getString("msg"));
					rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
					return resource_tag;
				}
				result.put("issuedby", data.getString("authority"));
				result.put("validthru", data.getString("valid_date").replace(".", ""));
				result.put("side", "back");
				result.put("cropped_image", data.getString("backimage"));
				logger.info("{} 反面解析完成",trade_id);
			}			
			retdata.put("result", result);
			resource_tag = Conts.TAG_TST_SUCCESS;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "交易成功!");
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		}else{
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.clear();
			rets.put(
					Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_YIDAO_RECOGNITION_ERRO);
			rets.put(Conts.KEY_RET_MSG,
					"证件识别失败,返回原因:" + json.getString("msg"));
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		}
		return resource_tag;
    }
}
