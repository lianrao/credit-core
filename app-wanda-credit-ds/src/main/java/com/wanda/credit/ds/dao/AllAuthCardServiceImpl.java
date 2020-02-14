package com.wanda.credit.ds.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.dao.domain.AllAuthenBankCard;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;

@Service
public class AllAuthCardServiceImpl implements IAllAuthCardService {
	private final  Logger logger = LoggerFactory.getLogger(AllAuthCardServiceImpl.class);
	@Autowired
	DaoService daoService;

	private AllAuthenBankCard buildPersisObjFromAuth(Map jsndata) {
        AllAuthenBankCard obj = new AllAuthenBankCard();
        obj.setCARD_TYPE(getCardType(String.valueOf(jsndata.get("dcType"))));
        obj.setSyscode(String.valueOf(jsndata.get("respCode")));
        obj.setSysmsg(String.valueOf(jsndata.get("respDesc")));
        if(jsndata.containsKey("detailRespCode")) {
            obj.setRespcode(String.valueOf(jsndata.get("detailRespCode")));
        }
        obj.setRespdesc(String.valueOf(jsndata.get("detailRespMsg")));
        obj.setCertType(String.valueOf(jsndata.get("certType")));
		return obj;
	}

	@Override
	public void saveAuthCard(String dsId, String tradeId,String name, String cardNo, String cardId,
			String phone, Map object, String req_values) {
        AllAuthenBankCard perobj = buildPersisObjFromAuth(object);
        perobj.setName(name);
//        perobj.setCardid(CryptUtil.encrypt(cardId));
//        perobj.setCardno(CryptUtil.encrypt(cardNo));
        perobj.setCardid(cardId);
        perobj.setCardno(cardNo);
        if(!StringUtil.isEmpty(phone)){
//            perobj.setMobile(CryptUtil.encrypt(phone));
            perobj.setMobile(phone);
            perobj.setTypeno("01");
        }else {
            perobj.setTypeno("00");
        }
        perobj.setTrade_id(tradeId);
        perobj.setSeq(String.valueOf(object.get("seq")));
        perobj.setDs_id(dsId);
        perobj.setReq_values_md5(MD5.uppEncodeByMD5(req_values));

        try{
            //只保存一致不一致结果
            if("2000".equals(perobj.getSyscode()) || "2001".equals(perobj.getSyscode())) {
                daoService.create(perobj);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info("{} 保存数据库失败 {}", tradeId, e);
        }
	}

    private String getCardType(String dcType){
        if("0".equals(dcType)){
            return "101";
        }else if("1".equals(dcType)){
            return "102";
        }else {
            return "";
        }
    }

	@Override
	public void savaJuHeAuthCard(String dsId, String tradeId, String name,
			String cardNo, String cardId, String phone, JSONObject rspData, String req_values) {
		try{
			AllAuthenBankCard perobj = new AllAuthenBankCard();
			perobj.setDs_id(dsId);
			perobj.setTrade_id(tradeId);
			perobj.setName(name);
			perobj.setCardid(cardId);
			perobj.setCardno(cardNo);
			perobj.setTypeno("00");//三要素
            perobj.setReq_values_md5(MD5.uppEncodeByMD5(req_values));
			if (!StringUtil.isEmpty(phone)) {
				perobj.setMobile(phone);
				perobj.setTypeno("01");//四要素
			}
//			JSONObject jsonObj = rspData.getJSONObject("data");
//			if (jsonObj != null) {
//				Object resObj = jsonObj.get("res");
			if (rspData != null) {
				Object resObj = rspData.get("res");
				perobj.setSeq(rspData.getString("jobid"));
				if(resObj == null){
					perobj.setRespdesc(rspData.getString("message"));
					return;
				}
				perobj.setRespdesc(rspData.getString("message"));
				String res = resObj.toString();
				if("1".equals(res)){
					perobj.setSyscode("2000");
					perobj.setSysmsg("验证一致");
				}else if("2".equals(res)){
					perobj.setSyscode("2001");
					perobj.setSysmsg("验证不一致");
				}
				daoService.create(perobj);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("{} 保存数据库错误 {}" ,tradeId, e.getMessage());
		}
		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public AllAuthenBankCard savaSuanHuaAuthCard(String dsId, String tradeId, String name,
			String cardNo, String cardId, String phone, JSONObject rspData, String req_values) {
		AllAuthenBankCard perobj = new AllAuthenBankCard();
		try{
			perobj.setDs_id(dsId);
			perobj.setTrade_id(tradeId);
			perobj.setName(name);
			perobj.setCardid(cardId);
			perobj.setCardno(cardNo);
			perobj.setTypeno("00");//三要素
            perobj.setReq_values_md5(MD5.uppEncodeByMD5(req_values));
			if (!StringUtil.isEmpty(phone)) {
				perobj.setMobile(phone);
				perobj.setTypeno("01");//四要素
			}
			if (rspData != null) {
				perobj.setSeq(rspData.getString("sorgseq"));
				perobj.setRespdesc(rspData.getString("errors"));
				perobj.setRespcode(rspData.getString("success"));
				if("true".equals(rspData.getString("success"))){//调用成功
					List<Map> cardInfoList = (List<Map>) ((Map)rspData.get("data")).get("cardInfoList");
					//rspData.get("cardInfoList")
					for(Map cardInfo: cardInfoList){
						SuanHuaCodeMsgMapEnum codemsg = 
								SuanHuaCodeMsgMapEnum.match(String.valueOf(cardInfo.get("status")));
						perobj.setCardid(String.valueOf(cardInfo.get("cardNum")));
						perobj.setSyscode(codemsg.sysCode);
						perobj.setSysmsg(codemsg.msg);
						daoService.create(perobj);
					}
				} else {
					
					daoService.create(perobj);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("{} 保存数据库错误 {}" ,tradeId, e.getMessage());
		}
		return perobj;
	}
	enum SuanHuaCodeMsgMapEnum {
		code1("1", "2000", "验证一致"),code2("2", "2001", "验证不一致、库无"),code3("3", "9999", "系统错误"),
		code4("31", "9998", "请求参数错误"),code5("5", "9997", "银行卡不支持");
		private SuanHuaCodeMsgMapEnum(String code, String sysCode, String msg) {
			this.code = code;
			this.sysCode = sysCode;
			this.msg = msg;
		}
		public String code;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String sysCode;
		public String getSysCode() {
			return sysCode;
		}
		public void setSysCode(String sysCode) {
			this.sysCode = sysCode;
		}
		public String msg;
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public static SuanHuaCodeMsgMapEnum match(String code){
			for(SuanHuaCodeMsgMapEnum em : SuanHuaCodeMsgMapEnum.values()){
				if(em.getCode().equals(code))
					return em;
			}
			return null;
		}
	}	

	@Override
	public void saveKQCNP(String dsId, String tradeId, String name,
			String cardNo, String cardId, String phone,String respCode, String detailCode, String req_values) {
		try{
			AllAuthenBankCard authInfo = new AllAuthenBankCard();
			authInfo.setDs_id(dsId);
			authInfo.setTrade_id(tradeId);
			authInfo.setTypeno("01");//四要素
			authInfo.setName(name);
			authInfo.setCardno(cardNo);
			authInfo.setCardid(cardId);
			authInfo.setMobile(phone);
			authInfo.setSyscode(respCode);
			authInfo.setRespcode(detailCode);
            authInfo.setReq_values_md5(MD5.uppEncodeByMD5(req_values));
			if ("2000".equals(respCode)) {
				authInfo.setSysmsg("认证一致");
			}else if("2001".equals(respCode)){
				authInfo.setSysmsg("认证不一致");
			}else if("2003".equals(respCode)){
				authInfo.setSysmsg("认证失败");
			}
			daoService.create(authInfo);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("{} 保存快钱卡鉴权数据异常 {}" , tradeId , e.getMessage());
		}
	}

    @Override
    public void saveJXAuthCard(String dsId, String tradeId,String name, String cardNo, String cardId,
                             String phone, Map object, String req_values) {
        try{
            AllAuthenBankCard perobj = new AllAuthenBankCard();
            perobj.setDs_id(dsId);
            perobj.setTrade_id(tradeId);
            perobj.setName(name);
            perobj.setCardid(cardId);
            perobj.setCardno(cardNo);
            perobj.setTypeno("00");//三要素
            perobj.setReq_values_md5(MD5.uppEncodeByMD5(req_values));

            if (!StringUtil.isEmpty(phone)) {
                perobj.setMobile(phone);
                perobj.setTypeno("01");//四要素
            }
            if (object != null) {
                perobj.setSeq(String.valueOf(object.get("orderid")));
                perobj.setSyscode(String.valueOf(object.get("respCode")));
                perobj.setSysmsg(String.valueOf(object.get("respDesc")));
                perobj.setRespcode(String.valueOf(object.get("detailRespCode")));
                perobj.setRespdesc(String.valueOf(object.get("detailRespMsg")));
                daoService.create(perobj);
            }

        }catch(Exception e){
            e.printStackTrace();
            logger.error("{} 保存数据库错误 {}", tradeId, e.getMessage());
        }
    }
}