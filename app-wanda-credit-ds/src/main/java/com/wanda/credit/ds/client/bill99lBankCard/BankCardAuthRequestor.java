package com.wanda.credit.ds.client.bill99lBankCard;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.dao.domain.bill99.MasBankCardAuth;
import com.wanda.credit.ds.dao.iface.bill99.IMasBankCardAuthService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description  快钱cnp银行卡鉴权
 * @author xiaocl 
 * @version 1.0
 * @createdate 2016年8月4日 下午2:29:32 
 *  
 */
@DataSourceClass(bindingDataSourceId="ds_mas_cardAuth")
public class BankCardAuthRequestor extends BaseMasDataSourcesRequestor implements IDataSourceRequestor{
	private final  Logger logger = LoggerFactory.getLogger(BankCardAuthRequestor.class);
	private final String idType = "0";
	@Autowired
	private IMasBankCardAuthService bankCardAuthService;

	private Map<String, String> detaiCodeTransMap = null;
	private Map<String, String> respCodeTransMap = null;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 银行卡鉴权交易开始",prefix);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "交易失败");
		MasBankCardAuth mbcAuth = new MasBankCardAuth();
		DataSourceLogVO logObj = new DataSourceLogVO();
		String dsId = "ds_mas_cardAuth";
		String tag = Conts.TAG_TST_FAIL;
		//计费标签
		Set<String> tags = new HashSet<String>();
		tags.add(tag);
//		retdata.put("server_idx", "kq_cnp");
	
		try {
			String requstUrl = propertyEngine.readById("MAS_REQ_URL1");
			logger.info("{} 请求URL为 {}" , prefix ,requstUrl );
			logObj.setDs_id(dsId);
			logObj.setTrade_id(trade_id);
			logObj.setReq_url(requstUrl);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setIncache("0");
			
			/**鉴权要素1：持卡人姓名*/
	 		String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();
	 		/**鉴权要素2：持卡人身份证号码*/
	 		String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString();
	 		/**鉴权要素3：持卡人手机号码*/
	 		String phone = ParamUtil.findValue(ds.getParams_in(), "phone").toString();
	 		/**鉴权要素4：持卡人卡号*/
	 		String cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString();
	 		/**鉴权要素5：CVV2*/
	 		String cvv2 = ParamUtil.findValue(ds.getParams_in(), "cvv2").toString();
	 		/**鉴权要素6：有效期*/
	 		String expiredDate = ParamUtil.findValue(ds.getParams_in(), "expiredDate").toString();

            String bankName = (String) ParamUtil.findValue(ds.getParams_in(),
                    "bankName");
            if(!StringUtil.isEmpty(bankName)){
                logObj.setBiz_code3(bankName);
            }
	 		String reqXML = buildReqXML(trade_id, name, cardNo, phone, cardId,cvv2,expiredDate);
			logger.info("{} 构建请求报文:{}", prefix, reqXML);
			logger.info("{} 开始发送请求...", prefix);
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
			HashMap hm = sendPost(requstUrl,
					reqXML,
					new String[] { "indAuthContent", "ErrorMsgContent" });
			logger.info("{} 返回数据为 {}" , prefix , hm);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			
			String isEnc = propertyEngine.readById("encrypt_switch_142");
			String encCardNo = cardNo;
			String encCardId = cardId;
			String encPhone = phone;
			if ("1".equals(isEnc)) {
				encCardNo = CryptUtil.encrypt(cardNo);
				encCardId = CryptUtil.encrypt(cardId);
				encPhone = CryptUtil.encrypt(phone);
			}
			mbcAuth.setCardId(encCardId);
			mbcAuth.setPhone(encPhone);
			mbcAuth.setName(StringUtil.isEmpty(name) ? null : name);
			mbcAuth.setCardno(encCardNo);
			mbcAuth.setCvv2(StringUtil.isEmpty(cvv2) ? null : cvv2);
			mbcAuth.setExpireddate(StringUtil.isEmpty(expiredDate) ? null : expiredDate);
			mbcAuth.setTrade_id(trade_id);
			if(hm!=null){
				String responseCode = hm.get("responseCode")!=null ? hm.get("responseCode").toString() : null;
				logger.info("{} 返回码为 {}" , prefix , responseCode);
				String responseMes = hm.get("responseTextMessage")!=null ? hm.get("responseTextMessage").toString() : null;
				mbcAuth.setCustomerId(hm.get("customerId")!=null ? hm.get("customerId").toString() : null);
				mbcAuth.setExternalRefNumber(hm.get("externalRefNumber")!=null ? hm.get("externalRefNumber").toString() : null);
				mbcAuth.setMerchantId(hm.get("merchantId")!=null ? hm.get("merchantId").toString() : null);
				mbcAuth.setResponseCode(responseCode);
				mbcAuth.setResponseTextMessage(responseMes);
				mbcAuth.setStorablePan(hm.get("storablePan")!=null ? hm.get("storablePan").toString() : null);
				mbcAuth.setTerminalId(hm.get("terminalId")!=null ? hm.get("terminalId").toString() : null);
				mbcAuth.setToken(hm.get("token")!=null ? hm.get("token").toString() : null);
				mbcAuth.setVersion(hm.get("version")!=null ? hm.get("version").toString() : null);
				bankCardAuthService.save(mbcAuth);

                String req_values = cardNo+"_"+cardId;
                if(!StringUtil.isEmpty(phone)){
                    req_values = req_values + "_" + phone;
                }
				
				if ("00".equals(responseCode) || "51".equals(responseCode)) {
					//认证一直
					retdata.put("respCode", "2000");
					retdata.put("respDesc", "认证一致");
					retdata.put("respDetail", "");
					tag = Conts.TAG_TST_SUCCESS;

					allAuthCardService.saveKQCNP(dsId, trade_id, name, encCardNo, encCardId,
							encPhone, "2000", "", req_values);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "交易成功");
					rets.put(Conts.KEY_RET_DATA, retdata);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					return rets;
				}else if("E6".equals(responseCode)){
					//银行卡号无效
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_INVALID_CARD);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_INVALID_CARD.getRet_msg());
					return rets;
				}else if ("AP,HY,I6,KM".contains(responseCode)){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
					return rets;
				}else{
					logger.info("{} 开始映射详细码" , prefix);
					String detailCode = getDetailCodeTransMap(responseCode);
					logger.info("{} 详细码为 {}" , prefix , detailCode);
					if (StringUtil.isEmpty(detailCode)) {
						//没有映射到匹配码
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
						return rets;
					}
					//映射到详细返回码
					String respCode = getRespCodeTransMap(detailCode);
					logger.info("{} 返回码为 {}" , prefix , detailCode);
					if (StringUtil.isEmpty(respCode)) {
						//没有映射到一致不一致返回码
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
						return rets;
					}
					
					retdata.put("respCode", respCode);
					retdata.put("respDetail", detailCode);
					
					if ("2000".equals(respCode)) {
						retdata.put("respDesc", "认证一致");
						retdata.put("respDetail", "");
						tag = Conts.TAG_TST_SUCCESS;
						allAuthCardService.saveKQCNP(dsId, trade_id, name, encCardNo, encCardId,
								encPhone, respCode, detailCode, req_values);
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setState_msg("交易成功");
					}else if("2001".equals(respCode)){
						retdata.put("respDesc", "认证不一致");
						tag = Conts.TAG_TST_SUCCESS;
						allAuthCardService.saveKQCNP(dsId, trade_id, name, encCardNo, encCardId,
								encPhone, respCode, detailCode, req_values);
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setState_msg("交易成功");
					}else if("2003".equals(respCode)){
						retdata.put("respDesc", "认证失致");//TODO
//						saveRespInfo(hm,trade_id,dsId,name,cardNo,cardId,phone,"2003");
					}
				    retdata.put("name", name);
					retdata.put("cardNo", cardNo);
					retdata.put("cardId", cardId);
					retdata.put("phone", phone);
					logger.info("{} 收到远程响应: {}", prefix ,JSONObject.toJSONString(hm, true));
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "交易成功");
					rets.put(Conts.KEY_RET_DATA, retdata);
				}
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
				return rets;
			}
			
		} catch (Exception e) {
			logger.error("{} 数据源处理异常 {}" , prefix , e.getMessage());
			if (ExceptionUtil.isTimeoutException(e)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			}
			rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			e.printStackTrace();
		} finally{
			tags.clear();
			tags.add(tag);
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[tags.size()]));
			logObj.setTag(tag);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, ParamUtil.convertParams(ds.getParams_in()), logObj);
		}
		
		return rets;
	}

	/**
	 * 构建请求报文
	 * @param externalRefNumber 外部追踪序号
	 * @param name   持卡人姓名
	 * @param cardNo 持卡人身份证号码
	 * @param mobile 持卡人手机号码
	 * @param cardId 持卡人卡号
	 * @return
	 */
	public String buildReqXML2(String externalRefNumber,String name,String cardNo,String phone,String cardId,String cvv2,String expiredDate){
	    String reqXml = "";
	    reqXml += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	    reqXml += "<MasMessage xmlns=\"http://www.99bill.com/mas_cnp_merchant_interface\">";
	    reqXml += "<version>1.0</version>";
	    reqXml += "<indAuthContent>";
	    reqXml += "<merchantId>" + propertyEngine.readById("MAS_MERCHANTID") + "</merchantId>";
	    reqXml += "<terminalId>" + propertyEngine.readById("MAS_TERMINALID") + "</terminalId>";
	    reqXml += "<customerId>" + propertyEngine.readById("MAS_CUSTOMERID") + "</customerId>";
        reqXml += "<externalRefNumber>" + externalRefNumber + "</externalRefNumber>";
        if(!StringUtils.isEmpty(cardId))
        	reqXml += "<pan>" + cardId + "</pan>";
        if(!StringUtils.isEmpty(name))
        	reqXml += "<cardHolderName>" + name + "</cardHolderName>";
        if(!StringUtils.isEmpty(cardNo)){
        	 reqXml += "<idType>" + idType + "</idType>";
             reqXml += "<cardHolderId>" + cardNo + "</cardHolderId>";
        }
        if(!StringUtils.isEmpty(cvv2)){
        	reqXml += "<cvv2>" + cvv2 + "</cvv2>";
        }
        if(!StringUtils.isEmpty(expiredDate)){
        	reqXml += "<expiredDate>" + expiredDate + "</expiredDate>";
        }
        if(!StringUtils.isEmpty(phone))
        	reqXml += "<phoneNO>" + phone + "</phoneNO>";
        reqXml += "</indAuthContent>";
        reqXml += "</MasMessage>";
		return reqXml;
	}

	/**
	 * 构建请求报文
	 * @param externalRefNumber 外部追踪序号
	 * @param name   持卡人姓名
	 * @param cardNo 持卡人身份证号码
	 * @param mobile 持卡人手机号码
	 * @param cardId 持卡人卡号
	 * @return
	 */
	public String buildReqXML(String externalRefNumber,String name,String cardNo,String phone,String cardId,String cvv2,String expiredDate){
	    StringBuffer reqXmlBf = new StringBuffer();
	    
	    reqXmlBf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
	    reqXmlBf.append("<MasMessage xmlns=\"http://www.99bill.com/mas_cnp_merchant_interface\">");
	    reqXmlBf.append("<version>1.0</version>");
	    reqXmlBf.append("<indAuthContent>");
	    reqXmlBf.append("<merchantId>").append(propertyEngine.readById("MAS_MERCHANTID")).append("</merchantId>");
	    reqXmlBf.append("<terminalId>").append(propertyEngine.readById("MAS_TERMINALID")).append("</terminalId>");
	    reqXmlBf.append("<customerId>").append(propertyEngine.readById("MAS_CUSTOMERID")).append("</customerId>");
	    reqXmlBf.append("<externalRefNumber>").append(externalRefNumber).append("</externalRefNumber>");
        if(!StringUtils.isEmpty(cardId))
        	reqXmlBf.append("<pan>").append(cardId).append("</pan>");
        if(!StringUtils.isEmpty(name))
        	reqXmlBf.append("<cardHolderName>").append(name).append("</cardHolderName>");
        if(!StringUtils.isEmpty(cardNo)){
        	 reqXmlBf.append("<idType>").append(idType).append("</idType>");
             reqXmlBf.append("<cardHolderId>").append(cardNo).append("</cardHolderId>");
        }
        if(!StringUtils.isEmpty(cvv2)){
        	reqXmlBf.append("<cvv2>").append(cvv2).append("</cvv2>");
        }
        if(!StringUtils.isEmpty(expiredDate)){
        	reqXmlBf.append("<expiredDate>").append(expiredDate).append("</expiredDate>");
        }
        if(!StringUtils.isEmpty(phone))
        	reqXmlBf.append("<phoneNO>").append(phone).append("</phoneNO>");
        reqXmlBf.append("</indAuthContent>");
        reqXmlBf.append("</MasMessage>");
		return reqXmlBf.toString();
	}
	
	/**
	 * 获取快钱码和返回详细码映射关系
	 * @param kqRspCode	快钱返回码
	 * @return	返回详细码
	 */
	private String getDetailCodeTransMap(String kqRspCode){
		
		String detailCode = null;
		if (StringUtil.isEmpty(kqRspCode)) {
			return detailCode;
		}
		
		if (detaiCodeTransMap == null) {
			detaiCodeTransMap = new HashMap<String, String>();
			//2001	0102	风险受限（黑名单）
			detaiCodeTransMap.put("05","0102");//不予承兑
			detaiCodeTransMap.put("80","0102");//交易拒绝
			detaiCodeTransMap.put("59","0102");//有作弊嫌疑
			detaiCodeTransMap.put("57","0102");//不允许持卡人进行的交易
			detaiCodeTransMap.put("93","0102");//交易违法、不能完成
			detaiCodeTransMap.put("78","0102");//止付卡
			detaiCodeTransMap.put("34","0102");//有作弊嫌疑
			detaiCodeTransMap.put("07","0102");//特定条件下没收卡
			detaiCodeTransMap.put("21","0102");//不做任何处理
			detaiCodeTransMap.put("67","0102");//强行受理（要求在自动柜员机上没收此卡）
			detaiCodeTransMap.put("R5","0102");//交易不予承兑，请换卡重试
			detaiCodeTransMap.put("43","0102");//被窃卡
			detaiCodeTransMap.put("37","0102");//风险卡，请联系快钱公司
			//2001	0103	无效卡号
			detaiCodeTransMap.put("14","0103");//	无效卡号（无此号），请换卡重试
			detaiCodeTransMap.put("56","0103");//	无此卡记录
			detaiCodeTransMap.put("BC","0103");//	无效卡
			detaiCodeTransMap.put("SK","0103");//	无效卡校验/Invalid Card Verification Value (CVV)
			detaiCodeTransMap.put("EK","0103");//	卡号无效
			detaiCodeTransMap.put("L9","0103");//	BIN.无效卡号
			detaiCodeTransMap.put("K9","0103");//	卡标志无效
			//2003	0301	交易失败，发卡银行不支持该商户
			detaiCodeTransMap.put("40","0301");//	请求的功能尚不支持
			//2001	0104	卡状态不正常 
			detaiCodeTransMap.put("WF","0104");//	您的卡号存在异常	
			detaiCodeTransMap.put("30","0104");//	卡片故障，请换卡重试			
			detaiCodeTransMap.put("KG","0104");//	卡状态、户口无效或不存在，拒绝交易对照
			//2001	0105	输入的密码、有效期或CVN2有误，交易失败 
			detaiCodeTransMap.put("55","0105");//	密码错误，请重新输入	
			detaiCodeTransMap.put("HU","0105");//	有效期不符			
			detaiCodeTransMap.put("I4","0105");//	请提供正确的卡有效期，卡有效期是在卡号下面的4位数字			
			detaiCodeTransMap.put("BB","0105");//	CVV错误次数超限			
			detaiCodeTransMap.put("FX","0105");//	取款密码错误			
			detaiCodeTransMap.put("I2","0105");//	请提供正确的验证码（CVV2），验证码在卡背面签名栏后的三位数字串			
			detaiCodeTransMap.put("Q2","0105");//	有效期错，请核实重输或联系发卡行
			//2001	0106	户名、证件信息或手机号等验证失败
			detaiCodeTransMap.put("Y1","0106");//	身份认证失败	
			detaiCodeTransMap.put("CD","0106");//	卡状态异常或户名证件号不符				
			detaiCodeTransMap.put("B8","0106");//	获取持卡人帐户信息失败				
			detaiCodeTransMap.put("W6","0106");//	手机号、身份证号码、姓名与开户时登记的不一致				
			detaiCodeTransMap.put("WC","0106");//	手机号、姓名与开户时登记的不一致				
			detaiCodeTransMap.put("GL","0106");//	用户输入信息错误
			//2001	0107	密码输入次数超限	
			detaiCodeTransMap.put("75","0107");//	密码错误次数超限，请换卡重试
			//2003	0302	您的银行卡暂不支持该业务
			detaiCodeTransMap.put("CA","0302");//	发卡方不支持的交易	
			detaiCodeTransMap.put("Z8","0302");//	不支持该卡种
			//2003	0304	受限制的卡或卡不在白名单中，无法进行交易
			detaiCodeTransMap.put("36","0304");//	受限制的卡	
			//2001	0110	您的银行卡未预留手机号
			detaiCodeTransMap.put("CC","0110");//	此卡未在银行预留绑定手机号，请联系发卡行	
			//2003	0306	您的手机未开通无卡支付服务
			detaiCodeTransMap.put("LG","0306");//	该银行卡未开通银联在线支付业务	
			//2001	0112	卡状态不正常(过期卡)
			detaiCodeTransMap.put("54","0112");//	卡片已过期，请换卡后交易
			detaiCodeTransMap.put("33","0112");//	过期的卡
			//2001	0113	卡状态不正常(挂失卡)	
			detaiCodeTransMap.put("41","0113");//	挂失卡	
			//2001	0115	户名验证失败
			detaiCodeTransMap.put("HX","0115");//	姓名不符	
			detaiCodeTransMap.put("I1","0115");//	请提供正确的持卡人姓名				
			detaiCodeTransMap.put("W4","0115");//	姓名与开户时登记的不一致
			//2001	0116	证件信息验证失败
			detaiCodeTransMap.put("I3","0116");//	请提供正确的证件号码，必须与申请银行卡时的证件号码一致
			detaiCodeTransMap.put("HZ","0116");//	证件号不符				
			detaiCodeTransMap.put("W2","0116");//	身份证号码与开户时登记的不一致	
			//2001	0117	手机号验证失败
			detaiCodeTransMap.put("HW","0117");//	手机号码不符	
			detaiCodeTransMap.put("IA","0117");//	请提供正确的手机号				
			detaiCodeTransMap.put("W0","0117");//	手机号与开户时登记的不一致
			//2001	0118	银行卡号验证失败
			detaiCodeTransMap.put("01","0118");//	请联系发卡行，或核对卡信息后重新输入	
			detaiCodeTransMap.put("20","0118");//	卡信息提供有误
			//2001	0119	卡已锁
			detaiCodeTransMap.put("NH","0119");//	卡已锁
			//2001	0120	卡未初始化	
			detaiCodeTransMap.put("Y5","0120");//	卡未初始化	
		}
		
		detailCode = detaiCodeTransMap.get(kqRspCode);
		
		return detailCode;
	}
	
	private String getRespCodeTransMap(String detailCode){
		String respCode = null;
		if (StringUtil.isEmpty(detailCode)) {
			return respCode;
		}
		if (respCodeTransMap == null) {
			respCodeTransMap = new HashMap<String, String>();
			respCodeTransMap.put("0102","2001");//	风险受限（黑名单）
			respCodeTransMap.put("0103","2001");//	无效卡号
			respCodeTransMap.put("0301","2003");//	交易失败，发卡银行不支持该商户
			respCodeTransMap.put("0104","2001");//	卡状态不正常 
			respCodeTransMap.put("0105","2001");//	输入的密码、有效期或CVN2有误，交易失败 
			respCodeTransMap.put("0106","2001");//	户名、证件信息或手机号等验证失败
			respCodeTransMap.put("0107","2001");//	密码输入次数超限	
			respCodeTransMap.put("0302","2003");//	您的银行卡暂不支持该业务
			respCodeTransMap.put("0304","2003");//	受限制的卡或卡不在白名单中，无法进行交易
			respCodeTransMap.put("0110","2001");//	您的银行卡未预留手机号	
			respCodeTransMap.put("0306","2003");//	您的手机未开通无卡支付服务
			respCodeTransMap.put("0112","2001");//	卡状态不正常(过期卡)
			respCodeTransMap.put("0113","2001");//	卡状态不正常(挂失卡)		
			respCodeTransMap.put("0115","2001");//	户名验证失败
			respCodeTransMap.put("0116","2001");//	证件信息验证失败	
			respCodeTransMap.put("0117","2001");//	手机号验证失败
			respCodeTransMap.put("0118","2001");//	银行卡号验证失败
			respCodeTransMap.put("0119","2001");//	卡已锁
			respCodeTransMap.put("0120","2001");//	卡未初始化	
		}
		respCode = respCodeTransMap.get(detailCode);
		return respCode;
	}

	
}
