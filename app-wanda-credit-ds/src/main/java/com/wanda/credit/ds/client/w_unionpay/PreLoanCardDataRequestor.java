/**   
* @Description: W项目—贷前银联卡交易数据查询
* @author xiaobin.hou  
* @date 2016年8月5日 上午11:20:38 
* @version V1.0   
*/
package com.wanda.credit.ds.client.w_unionpay;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unionpay.udsp.sdk.SDKConstants;
import com.unionpay.udsp.sdk.SDKUtil;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.w_unionpay.bean.CustomerInfo;
import com.wanda.credit.ds.dao.domain.wUnionpay.UnionPayPreLoanPojo;
import com.wanda.credit.ds.dao.iface.wUnionpay.WUnionPayService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
public class PreLoanCardDataRequestor extends BasicWUninonPayDSRequestor
		implements IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(PreLoanCardDataRequestor.class);
	
	@Autowired
	private WUnionPayService wUnionpayService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	private String productCode;
	private String url;
		
	
	public Map<String, Object> request(String trade_id, DataSource ds) {
	
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		// 组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		
		//请求信息
		UnionPayPreLoanPojo preLoanPojo = new UnionPayPreLoanPojo();
		preLoanPojo.setTrade_id(trade_id);
		preLoanPojo.setOrderId(trade_id);
		//交易日志信息
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(url);
		//默认不获取缓存
		logObj.setIncache("0");
		// 默认交易失败
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));

		try{
			// 获取ds参数
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();//姓名
			String cardNo = (ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString()).toUpperCase();//身份证号
			String cardId = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//卡号
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();//预留手机号
			
			/*String encCardNo = "";
			String encCardId = "";
			String encMobile = "";	
			
			preLoanPojo.setCard(encCardId);		
			preLoanPojo.setIdCard(encCardNo);
			preLoanPojo.setMobile(encMobile);
			preLoanPojo.setName(name);*/
			
			
			//敏感数据加密
			String encCardNo = synchExecutorService.encrypt(cardNo);
			String encCardId = synchExecutorService.encrypt(cardId);
			String encMobile = synchExecutorService.encrypt(mobile);	
			
			preLoanPojo.setCard(encCardId);		
			preLoanPojo.setIdCard(encCardNo);
			preLoanPojo.setMobile(encMobile);
			preLoanPojo.setName(name);
					
			//保存入参数据
			boolean saveOrNo = saveParamIn(name,cardNo,cardId,mobile,trade_id,logObj);
			if (!saveOrNo) {
				logger.info("{} 输入参数保存到数据库失败" , prefix);
			}
			//身份证号码
			String validateRes = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(validateRes)) {
				logger.info("{} 身份证号码校验失败，失败信息为： {}" , prefix , validateRes);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				return rets;
			}
			//手机号码验证
			if (!StringUtil.isPositiveInt(mobile) || mobile.length() != 11) {
				logger.info("{} 手机号码格式错误" , prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());				
				return rets;
			}
			//银行卡卡号验证
			if (!(StringUtil.isPositiveInt(cardId) && (cardId.length() >12 && cardId.length() < 20))) {
				logger.info("{} 手机号码格式错误" , prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_CARDID_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_CARDID_ERROR.getRet_msg());
				return rets;
			}
			
			boolean isCache = wUnionpayService.inCachePreData(name,encCardNo,encCardId,encMobile);
			
			if (isCache) {
				try{
					UnionPayPreLoanPojo  cacheData = wUnionpayService.queryPreData(name,encCardNo,encCardId,encMobile);
					retData.put("match_result", "1");
					retData.put("last_month", cacheData.getLast_month());
					retData.put("txn_months", cacheData.getTxn_months());
					retData.put("amt_12mons", cacheData.getAmt_12mons());
					retData.put("cnt_12mons", cacheData.getCnt_12mons());
					retData.put("cre_3mons_rat", cacheData.getCre_3mons_rat());
					retData.put("deb_3mons_rat", cacheData.getDeb_3mons_rat());
					retData.put("city_3mons", cacheData.getCity_3mons());
					retData.put("prov_3mons", cacheData.getProv_3mons());
					
					logObj.setIncache("1");
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("请求成功");
					
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功");
					rets.put(Conts.KEY_RET_DATA, retData);
					
					return rets;
				}catch(Exception e){
					logger.error("{} 获取缓存数据失败直接连接银联获取数据 {}" , prefix , e.getMessage());
				}
				
			}
			
			//构建请求数据参数
			//构建请求数据参数-构建通用参数
			String reqSeq  = SDKUtil.generateOrderId();
			String reqTime = SDKUtil.generateTxnTime();
			Map<String, String> commonData = initCommonRequestData(trade_id,reqSeq,reqTime);
			commonData.put(SDKConstants.key_pCode, productCode);
			//构建请求数据参数-reqData子域
	        Map<String, String> subData = new HashMap<String, String>();
			String reqData = buildReqData(name, cardNo, cardId, mobile);
			subData.put(SDKConstants.key_customerInfo, reqData);
			
			System.out.println("子域数据为： " + JSONObject.toJSONString(subData));
			//获取请求参数
			Map<String, String> reqDataMap = getReqDataMap(commonData, subData);
			//发送请求	
			long postStartTime = System.currentTimeMillis();
			String data_cb = RequestHelper.doPost(url, null,new HashMap<String,String>(),reqDataMap, null,true);
			logger.info("{} http请求耗时： {}" , prefix , System.currentTimeMillis() - postStartTime);
			//TODO
	        System.out.println("应答报文: " + data_cb);

			// 应答报文处理
			if (data_cb != null && data_cb.length() > 0) {
				Map<String, String> data_map_cb = SDKUtil.convertResultStringToMap(data_cb);
				// 应答报文签名验证
				boolean isok = SDKUtil.validate(data_map_cb,SDKConstants.encoding_UTF_8);
				if (isok) { 
					
					preLoanPojo = buildPreLoanPojo(data_map_cb,reqTime,preLoanPojo);
					preLoanPojo.setTrade_id(trade_id);
					preLoanPojo.setReqSeq(reqSeq);
				
					String respCode = data_map_cb.get(SDKConstants.key_respCode);
					String respMsg = data_map_cb.get(SDKConstants.key_respMsg);
					
					if("00".equals(respCode)){
						//四要素鉴权成功，保存数据
						wUnionpayService.addCardAuthed(name,encCardNo,encCardId,encMobile,cardNo,cardId,mobile);
						//解析应答数据为
						String respData = data_map_cb.get(SDKConstants.key_respData);
						System.out.println("respData=" + respData);

						// 取指标代码示例
						JSONObject respDataJson = JSONObject.parseObject(respData);
						// 指标数据放在respData-cardDataList列表中，一个或多个均用list结构
						JSONArray cardDataList = respDataJson.getJSONArray("cardDataList");
						for (int i = 0; i < cardDataList.size(); i++) {
							// 获取卡片数据
							JSONObject oneCard = cardDataList.getJSONObject(i);
							JSONObject cardData = oneCard.getJSONObject("cardData");
							// 取所需指标,
							String last_month = cardData
									.getString("settle_month"); // 数据年月
							String txn_months = cardData
									.getString("c_no_setl_m_L12m"); // 最近12个月有交易的月份数
							String amt_12mons = cardData
									.getString("c_setl_amt_L12m"); // 最近12个月交易金额
							String cnt_12mons = cardData
									.getString("c_setl_unit_L12m"); // 最近12个月交易笔数
							String cre_3mons_rat = cardData
									.getString("c_cr_incr_amt_L3m_r"); // 最近3个月贷方交易金额增长率
							String deb_3mons_rat = cardData
									.getString("c_dr_incr_amt_L3m_r"); // 最近3个月借方交易金额增长率
							String city_3mons = cardData
									.getString("c_m_trx_city"); // 最近3个月主要交易城市
							String prov_3mons = cardData
									.getString("c_m_trx_prov"); // 最近3个月主要交易省份
							last_month = StringUtil.isEmpty(last_month) ? "":last_month;
							txn_months = StringUtil.isEmpty(txn_months) ? "":txn_months;
							amt_12mons = StringUtil.isEmpty(amt_12mons) ? "":amt_12mons;
							cnt_12mons = StringUtil.isEmpty(cnt_12mons) ? "":cnt_12mons;
							cre_3mons_rat = StringUtil.isEmpty(cre_3mons_rat) ? "":cre_3mons_rat;
							deb_3mons_rat = StringUtil.isEmpty(deb_3mons_rat) ? "":deb_3mons_rat;
							deb_3mons_rat = StringUtil.isEmpty(deb_3mons_rat) ? "":deb_3mons_rat;
							city_3mons = StringUtil.isEmpty(city_3mons) ? "":city_3mons;
							prov_3mons = StringUtil.isEmpty(prov_3mons) ? "":prov_3mons;
							// 处理指标数据--保存数据
							preLoanPojo.setLast_month(last_month);
							preLoanPojo.setTxn_months(txn_months);
							preLoanPojo.setAmt_12mons(amt_12mons);
							preLoanPojo.setCnt_12mons(cnt_12mons);
							preLoanPojo.setCre_3mons_rat(cre_3mons_rat);
							preLoanPojo.setDeb_3mons_rat(deb_3mons_rat);
							preLoanPojo.setCity_3mons(city_3mons);
							preLoanPojo.setProv_3mons(prov_3mons);
							// 处理指标数据--拼装返回报文
							retData.put("match_result", "1");
							retData.put("last_month", last_month);
							retData.put("txn_months", txn_months);
							retData.put("amt_12mons", amt_12mons);
							retData.put("cnt_12mons", cnt_12mons);
							retData.put("cre_3mons_rat", cre_3mons_rat);
							retData.put("deb_3mons_rat", deb_3mons_rat);
							retData.put("city_3mons", city_3mons);
							retData.put("prov_3mons", prov_3mons);
							
							//银行卡鉴权成功
							logObj.setBiz_code2(respCode);
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							logObj.setState_msg("交易成功");
							//返回信息
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
							rets.put(Conts.KEY_RET_MSG, "交易成功");
							rets.put(Conts.KEY_RET_DATA, retData);

						}

						// 请根据应答报文结果进行业务处理
						if (data_map_cb.get(SDKConstants.key_reqSeq).toString().equals(reqSeq)) {
							System.out.println("应答报文签名验证成功。respMsg=" + respMsg);
						} else {
							System.out.println("应答报文签名验证成功。但不是请求报文对应的应答结果");
						}
					}else if("36".equals(respCode)){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "交易成功");
						retData.put("match_result", "0");
						rets.put(Conts.KEY_RET_DATA, retData);
					}else if("37".equals(respCode)){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "交易成功");
						retData.put("match_result", "0");
						rets.put(Conts.KEY_RET_DATA, retData);
					}else{
						logger.info("{} 交易失败，详细信息为: {}" , prefix ,respMsg);
						logObj.setBiz_code2(respCode);
					}
					
				} else {
					logger.info("{} 应答报文签名验证失败" , prefix);
					logObj.setState_msg("交易失败-应答报文验签失败");
				}
			} else {
				logger.info("{} 应答报文为空，可能原因：超时" , prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
			}
			
		}catch(Exception e){
			logger.error("{} 交易处理异常,异常信息为：{}" , prefix , e.getMessage());
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "交易失败");
			
		}finally{
			try{
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			}catch(Exception e){
				logger.info("{} 保存日志信息失败 ,异常信息为：{}" , prefix , e.getMessage());
			}
			
			try {
				if (!"1".equals(logObj.getIncache())) {
					wUnionpayService.addPreLoanData(preLoanPojo);					
				}else{
					logger.info("{} 直接从缓存获取数据，请求流水表不保存数据" , prefix );
				}
			} catch (Exception ex) {
				logger.error("{} 保存贷前银联卡交易数据查询流水表异常： {}" , prefix , ex.getMessage());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "交易失败");
			}
			
		}
		

		return rets;
	}


	/**
	 * @param data_map_cb
	 * @param reqSeq
	 * @param reqTime
	 * @param preLoanPojo
	 * @return
	 * @throws Exception 
	 */
	private UnionPayPreLoanPojo buildPreLoanPojo(
			Map<String, String> data_map_cb, String reqTime,
			UnionPayPreLoanPojo preLoan) throws Exception {
		if (data_map_cb == null) {
			return null;
		}
		DateFormat wDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		preLoan.setReqSeq(data_map_cb.get(SDKConstants.key_reqSeq));
		preLoan.setReqtime(wDateFormat.parse(reqTime));
		String respTime = data_map_cb.get(SDKConstants.key_respTime);
		preLoan.setResptime(wDateFormat.parse(respTime));
		preLoan.setRespCode(data_map_cb.get(SDKConstants.key_respCode));
		preLoan.setRespMsg(data_map_cb.get(SDKConstants.key_respMsg));
		preLoan.setRespSeq(data_map_cb.get(SDKConstants.key_respSeq));

		return preLoan;
	}


	/**
	 * @param name
	 * @param encCardNo
	 * @param encCardId
	 * @param encMobile
	 * @param logObj 
	 * @param trade_id 
	 */
	private boolean saveParamIn(String name, String cardNo, String cardId,
			String mobile, String trade_id, DataSourceLogVO logObj) {
		boolean success = true;
		try{
			Map<String, Object> paramIn = new HashMap<String, Object>();
			paramIn.put("name", name);
			paramIn.put("cardNo", cardNo);
			paramIn.put("cardId", cardId);
			paramIn.put("mobile", mobile);
			
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}catch(Exception e){
			logger.info("{} 输入参数保存到数据库异常 {}" , trade_id , e.getMessage());
			success = false;
		}
	
		return success;
		
	}


	/**
	 * 构建请求参数
	 * @param name
	 * @param cardNo
	 * @param cardId
	 * @param mobile
	 * @return
	 */
	private String buildReqData(String name, String cardNo, String cardId,
			String mobile) {
		List<CustomerInfo> cardInfoList = new ArrayList<CustomerInfo>();
		CustomerInfo cardInfo = new CustomerInfo();
		cardInfo.setCardNo(cardId);
		cardInfo.setCertifId(cardNo);
		cardInfo.setCertifTp("01");
		cardInfo.setCustomerNm(name);
		cardInfo.setPhoneNo(mobile);
		
		cardInfoList.add(cardInfo);
		return JSONObject.toJSONString(cardInfoList);
	}


	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
