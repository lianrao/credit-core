package com.wanda.credit.ds.client.tianchuang;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.client.ppxin.BasePPXDSRequestor;

public class BaseTianChSourceRequestor extends BasePPXDSRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(BaseTianChSourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	public String verifyIdcard(String trade_id,String url,String cardNo, String name)
			throws Exception {
		logger.info("{} 天创信用不良信息http查询开始...",trade_id);
		String tokenId = propertyEngine.readById("ds_tianchuang_tokenId");
		String appId = propertyEngine.readById("ds_tianchuang_appId");
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		Map<String, String> param = new HashMap<String, String>();
		// 所需入参，请参照不同接口文档根据入参不同编写，注意参数的大小写
		param.put("name", name);
		param.put("idcard", cardNo);

		// 生成TokenKey
		// 注意：进行MD5摘要时，不能传入appId参数
		String tokenKey = getTokenKey(url, tokenId, param);
		param.put("tokenKey", tokenKey);

		// 注意：appId参数需要在请求的时候作为入参传入，appId不参与MD5摘要
		param.put("appId", appId);
		// http的post请求
		String res = RequestHelper.doPost(url, param,false,time_out);
		logger.info("{} 天创信用不良信息http查询结束",trade_id);
		// 打印返回结果
		return res;
	}
	public String verifyDriverLicensce(String trade_id,String url,Map<String, String> param)
			throws Exception {
		logger.info("{} 天创信用驾驶证http查询开始...",trade_id);
		String tokenId = propertyEngine.readById("ds_tianchuang_tokenId");
		String appId = propertyEngine.readById("ds_tianchuang_appId");
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		
		// 生成TokenKey
		// 注意：进行MD5摘要时，不能传入appId参数
		String tokenKey = getTokenKey(url, tokenId, param);
		param.put("tokenKey", tokenKey);
		// 注意：appId参数需要在请求的时候作为入参传入，appId不参与MD5摘要
		param.put("appId", appId);
		// http的post请求
		String res = RequestHelper.doPost(url, param,false,time_out);
		logger.info("{} 天创信用驾驶证http查询结束",trade_id);
		// 打印返回结果
		return res;
	}
	public static String getTokenKey(String url, String tokenId, Map<String, String> param) {
		String paramStr = sortParam(param);
		return md5Hex(url + tokenId + paramStr);
	}
	/**
	 * 
	 * 名称: sortParam 
	 * 作者：陈祥
	 * 日期：2017年10月11日 下午2:25:12
	 * 描述: 生成参数字符串，参数key按字典序排列 
	 * 参数： param-生成token需要的参数
	 * 返回值： String
	 * 异常： 
	 *
	 */
	public static String sortParam(Map<String, String> param) {
		if (null == param || 0 == param.size()) {
			return "";
		}
		// 排序键，按照字母先后进行排序
		Iterator<String> iterator = param.keySet().iterator();
		String[] arr = new String[param.size()];
		for (int i = 0; iterator.hasNext(); i++) {
			arr[i] = iterator.next();
		}
		Arrays.sort(arr);
		// 生成进行MD5摘要的字符串
		StringBuffer sb = new StringBuffer();
		for (String key : arr) {
			String value = param.get(key);
			if (StringUtils.isNotBlank(value)) {
				sb.append(key).append("=").append(value).append(",");
			}
		}
		// 检查结果
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		} else {
			return "";
		}
	}

	/**
	 * 
	 * 名称: md5Hex
	 * 作者：陈祥
	 * 日期：2017年10月11日 下午2:47:44
	 * 描述: 对字符串进行md5摘要，然后转化成16进制字符串 
	 *       使用标准的md5摘要算法
	 * 参数： text-需要进行摘要的字符串
	 * 返回值： 进行MD5摘要以及16进制转化后的字符串
	 *
	 */
	public static String md5Hex(String text) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bytes = md5.digest(text.trim().getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				int high = (bytes[i] >> 4) & 0x0f;
				int low = bytes[i] & 0x0f;
				sb.append(high > 9 ? (char) ((high - 10) + 'a') : (char) (high + '0'));
				sb.append(low > 9 ? (char) ((low - 10) + 'a') : (char) (low + '0'));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("系统不支持MD5算法");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("系统不支持指定的编码格式");
			e.printStackTrace();
		}
		return null;
	}
	public String bulidResp(String trade_id,String code,
			Map<String,Object> retdata,Map<String,Object> rets){
		String resource_tag = Conts.TAG_SYS_ERROR;
		logger.info("{} 构建出参开始...", trade_id);
		switch(code){
			case "000":
				retdata.put("respCode", "2000");
				retdata.put("respDesc", "认证一致");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				logger.info("{} 认证一致",trade_id);
				break;
			case "100":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "200":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0103");
				retdata.put("respDetail", "无效卡号");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "201":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0104");
				retdata.put("respDetail", "卡状态不正常");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "202":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0116");
				retdata.put("respDetail", "证件信息验证失败");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "203":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0117");
				retdata.put("respDetail", "手机号验证失败");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "204":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0106");
				retdata.put("respDetail", "姓名校验不通过");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "205":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0118");
				retdata.put("respDetail", "无法识别的卡");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "206":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0107");
				retdata.put("respDetail", "密码输入次数超限");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "300":
				retdata.put("respCode", "2003");
				retdata.put("respDesc", "不支持验证");
				retdata.put("detailRespCode", "0307");
				retdata.put("respDetail", "请开通无卡支付服务");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "301":
				retdata.put("respCode", "2003");
				retdata.put("respDesc", "不支持验证");
				retdata.put("detailRespCode", "0302");
				retdata.put("respDetail", "该银行暂不支持");
				logger.info("{} 认证不一致",trade_id);
				break;
			default :
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				logger.info("{} 认证不一致",trade_id);
				break;
		}
		resource_tag = Conts.TAG_TST_SUCCESS;
		
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_DATA, retdata);
		rets.put(Conts.KEY_RET_MSG, "交易成功");
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return resource_tag;
	}
	public String detailBulidResp(String trade_id,JSONObject data,
			Map<String,Object> retdata,Map<String,Object> rets){
		String resource_tag = Conts.TAG_SYS_ERROR;
		String code = data.getString("result");
		String detailMsg = data.getString("detailMsg");
		logger.info("{} 构建出参开始...", trade_id);
		switch(code){
			case "1":
				retdata.put("respCode", "2000");
				retdata.put("respDesc", "认证一致");
				retdata.put("respDetail", "");
				logger.info("{} 认证一致",trade_id);
				break;
			case "2":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("respDetail", detailMsg);
				logger.info("{} 认证不一致",trade_id);
				break;
			case "3":
				retdata.put("respCode", "2002");
				retdata.put("respDesc", "不支持认证");
				retdata.put("respDetail", detailMsg);
				logger.info("{} 不支持认证:{}",trade_id,detailMsg);
				break;
			case "4":
				retdata.put("respCode", "2002");
				retdata.put("respDesc", "不支持认证");
				retdata.put("respDetail", detailMsg);
				logger.info("{} 不支持认证2",trade_id);
				break;
			default :
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("respDetail", "");
				logger.info("{} 认证不一致",trade_id);
				break;
		}
		resource_tag = Conts.TAG_TST_SUCCESS;
		
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_DATA, retdata);
		rets.put(Conts.KEY_RET_MSG, "交易成功");
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return resource_tag;
	}
}
