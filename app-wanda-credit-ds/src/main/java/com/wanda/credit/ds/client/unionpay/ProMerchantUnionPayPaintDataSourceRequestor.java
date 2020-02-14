package com.wanda.credit.ds.client.unionpay;

import java.net.SocketTimeoutException;
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

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.iface.IUnionPayPaintService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
* @Title: 外部数据源 ds_proMerchantUnionPayPaint
* @Description: 银联商户画像查询
* @author xiaobin.hou@99bill.com  
* @date 2016年4月11日 下午13:38:31 
* @version V1.0
*/
@DataSourceClass(bindingDataSourceId="ds_proMerchantUnionPayPaint")
public class ProMerchantUnionPayPaintDataSourceRequestor extends BaseUnionpayPaintDsRequestor implements
		IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ProMerchantUnionPayPaintDataSourceRequestor.class);
	private String dsId = "ds_proMerchantUnionPayPaint";
	
	@Autowired
	private IUnionPayPaintService iUnionPayPaintService;
	private String url;
	private String account;
	private String privateKey;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		String paintResultId = null;
		//获取日志开关
		boolean doPrint = "1".equals(propertyEngine.readById("sys_log_print_switch"));
		String index = propertyEngine.readById("yl_m_qurey_ids");
		String account_id = propertyEngine.readById("unionpay_per_account");
		String unionpay_url = propertyEngine.readById("unionpay_merchant_url");
		long start = System.currentTimeMillis();
//		url = "https://warcraft-test.unionpaysmart.com/quota/merchant";
//		account = "T2010005";
		//标签
		Set<String> tagSet = new HashSet<String>();
		tagSet.add(Conts.TAG_SYS_ERROR);
		//数据源请求响应数据对象
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(dsId);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(unionpay_url);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "交易失败");
		Map<String, String> params = null;
		try{
			
			String mid = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();    //商户MID
			String regNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();    //营业执照号
			String currentTime = DateUtil.getSimpleDate(new Date(), "yyyyMMddHHmmssSSS");
		    params = new TreeMap<String, String>();
		    params.put(ACCOUNT_ID_FLAG, account_id);
		 	params.put(ORDER_ID_FLAG, currentTime);
//		 	params.put(INDEX_FLAG, index);
		 	params.put(MID_FLAG, mid);
		 	params.put(REGNO_FLAG, regNo);
		 	
		 	/**记录请求状态信息 BEGIN*/
		 	Map<String,Object> paramIn = new HashMap<String,Object>();
		 	paramIn.put(MID_FLAG, mid);
		 	paramIn.put(INDEX_FLAG, index);
		 	paramIn.put(REGNO_FLAG, regNo);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn,logObj);
			/**记录请求状态信息 END*/
			//缓存开关
			paintResultId = iUnionPayPaintService.isExistCurentMonthRecord(trade_id, mid);
			if(!StringUtil.isEmpty(paintResultId)){
				logger.info("{} 本地存在一个月内的该mid的缓存数据，开始从缓存中获取数据" , prefix);
				logObj.setIncache("1");
				TreeMap<String, Object> paintData = iUnionPayPaintService.queryLastResult(trade_id, paintResultId);
				
				if(paintData != null && paintData.size() > 0){	
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg((String) DataSourceLogEngineUtil.ERRMAP.get(DataSourceLogEngineUtil.TRADE_STATE_SUCC));
					Map<String, Object> retData = new HashMap<String, Object>();
					retData.put("final_result", "1");
					retData.put("final_data", paintData);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "银联商户画像查询成功!");
					rets.put(Conts.KEY_RET_DATA, retData);
					
					tagSet.clear();
					tagSet.add(Conts.TAG_INCACHE_TST_SUCCESS);
				}else{
					logObj.setIncache("0");
					logger.info("{} 缓存中获取商户画像数据返回为null，直接连接银联获取数据" , prefix);
					paintResultId = null;
				}
			}
			
			if(StringUtil.isEmpty(paintResultId)){
				logger.info("{} 连接银联智慧获取商户画像数据,orderId:{}" , prefix,currentTime);
			 	logger.info("{} 获取参数成功，开始加签..." ,prefix);
			 	//加密加签名
			 	Map<String, String> reqMap = new HashMap<String, String>();
			 	reqMap.put(MID_FLAG, mid);
			 	reqMap.put(REGNO_FLAG, regNo);
			 	reqMap.put(ORDER_ID_FLAG, currentTime);
			 	
			 	Map<String, String> httpReqMap = buildReqMap(prefix, account_id, reqMap);

	            long httpStart = System.currentTimeMillis();
	            String respResult = RequestHelper.doPost(unionpay_url, null, new HashMap<String, String>(), httpReqMap, null, false);
	            logger.info("{} 银联商户画像http请求时间为  {}" , prefix, (System.currentTimeMillis() - httpStart));
	            logger.info("请求返回数据：{}", respResult);
	            
	            Map<String, String> decMap = decResInfo(prefix, respResult);
	            if (!(decMap.containsKey(PARSE_CODE) && "000".equals(decMap.get(PARSE_CODE)))) {
					logger.info("{} 解密银联返回报文失败 {}" , prefix , decMap);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
					logObj.setState_msg(decMap.get(PARSE_MSG));
					
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			    	rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
			    	
					return rets;
				}
	            
			    String resp = decMap.get(PARSE_MSG);
		    	 //保存商户画像数据
			    params.put("bc_flag", "1");//个人/企业标识：0-个人，1-企业
			    iUnionPayPaintService.saveMerchant(trade_id, resp, params);
			    if (doPrint) {
					logger.info("{} 银联商户画像查询结束,银联返回解密结果为 {}",prefix, resp );
				}
			    //标签
				tagSet.clear();
				tagSet.add(Conts.TAG_TST_FAIL);
		    	//将返回报文转化成json格式
		    	JSONObject respJsonObj = JSONObject.parseObject(resp);
		    	String resCode = respJsonObj.getString(KEY_CODE);
				String resMsg = respJsonObj.getString(KEY_MESSAGE);
				String statCode = respJsonObj.getString(KEY_STATUS);
				String smartId = respJsonObj.getString(KEY_SMARTID);
				String orderId = respJsonObj.getString(KEY_ORDERID);
				logObj.setBiz_code1(resCode + "-" + statCode + "-" + resMsg );
				logObj.setBiz_code2(smartId);
				logObj.setBiz_code3(orderId);
					
		    	//获取code对应的值	200-正常返回
		    	if ("200".equals(resCode)) {						
					logObj.setState_msg(resMsg);
					logger.info("{} 银联商户画像查询返回响应码为code= {}",prefix,resCode);
					if ("2000".equals(statCode)) {
						paintResultId = iUnionPayPaintService.isExistCurentMonthRecord(trade_id, mid);
						//查询成功
						if (!StringUtil.isEmpty(paintResultId) && respJsonObj.containsKey("data")) {
							TreeMap<String, Object> paintData = iUnionPayPaintService.queryLastResult(trade_id,paintResultId);
							Map<String, Object> retData = new HashMap<String, Object>();
							if (paintData != null && paintData.size() > 0) {										
								retData.put("final_result", "1");
								retData.put("final_data", paintData);
							}else{
								retData.put("final_result", "0");
							}									
							rets.clear();
						    rets.put(Conts.KEY_RET_DATA, retData);	
						}else{
							logger.info("{} 银联商户画像数据返回异常，返回数据信息为" + resp ,prefix);
							Map<String, Object> retData = new HashMap<String, Object>();
							retData.put("final_result", "0");
							rets.clear();
						    rets.put(Conts.KEY_RET_DATA, retData);	
						}
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "银联商户画像查询成功!");
						logger.info("{} 银联商户画像查询成功!", prefix);
						//标签
						tagSet.clear();
						tagSet.add(Conts.TAG_TST_SUCCESS);
						//交易日志
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setState_msg("交易处理成功");
					}else{
						//没有查到结果
						rets.clear();
					    rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_UNIONPAY_MERPAINT_MID_ERROR);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_UNIONPAY_MERPAINT_MID_ERROR.getRet_msg());
					}
				}else{
					//没有查到结果
					rets.clear();
				    rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_UNIONPAY_MERPAINT_MID_ERROR);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_UNIONPAY_MERPAINT_MID_ERROR.getRet_msg());
				}
		    }
		}catch(Exception ex){
			ex.printStackTrace();
			if((ex instanceof ConnectTimeoutException) || (ex instanceof SocketTimeoutException)){
				logger.error("{} 连接银联智慧画像获取商户画像数据请求超时" + ex.getMessage());
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
			}else{
				logger.error("{} 银联商户画像交易处理时异常：{}",prefix,ex.getMessage());
				
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());
			}
		}finally{
			rets.put(Conts.KEY_RET_TAG,tagSet.toArray(new String[tagSet.size()]));
			/**记录响应状态信息*/
			logObj.setTag(StringUtils.join(tagSet, ";"));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		}
		logger.info("{} 银联商户画像查询结束，交易时间为 " + (System.currentTimeMillis() - start) ,prefix);
		return rets;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	
}
