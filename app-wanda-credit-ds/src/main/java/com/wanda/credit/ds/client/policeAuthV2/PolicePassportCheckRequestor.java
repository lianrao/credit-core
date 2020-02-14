package com.wanda.credit.ds.client.policeAuthV2;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.policeAuthV2.beans.AuthenticationApplicationDataCert;
import com.wanda.credit.ds.client.policeAuthV2.beans.IdAuthData;
import com.wanda.credit.ds.client.policeAuthV2.data.DataSignatureLocal;
import com.wanda.credit.ds.client.policeAuthV2.data.EncryptedReservedLocal;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

@DataSourceClass(bindingDataSourceId="ds_police_cert")
public class PolicePassportCheckRequestor extends BasePoliceV2Requestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(PolicePassportCheckRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 公安一所普通护照认证请求开始...", prefix);
		Map<String, Object> rets = null;
		Map<String, Object> retdata = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String request_url = propertyEngine.readById("ds_police_passport_request_url");
		String auth_url = propertyEngine.readById("ds_police_passport_auth_url");
		
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); //身份证号码
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //姓名
			String nation = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //国家
			String idType = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString(); //类型
			String mode = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString(); //认证模式
			logObj.setDs_id(ds.getId());
			logObj.setIncache("0");
			rets = new HashMap<String, Object>();	 		
			logger.info("{} 公安一所普通护照加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			logObj.setReq_url(auth_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			
			String result = getCertAutoResult(propertyEngine,trade_id,
					request_url,auth_url,name,cardNo,idType,nation,Integer.valueOf(mode));
			com.alibaba.fastjson.JSONObject result_json = JSON.parseObject(result);
			String authFirst = result_json.getJSONObject("bizPackage").getString("authResult").substring(0,1);
			if("0".equals(authFirst)){
				resource_tag = Conts.TAG_MATCH;
				retdata.put("final_auth_result", "0");//一致
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_MSG, "交易成功");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}else if("7".equals(authFirst)){
				resource_tag = Conts.TAG_UNMATCH;
				retdata.put("final_auth_result", "1");//不一致
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_MSG, "交易成功");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "认证失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally {			
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}
	//获取请求结果
	public String getCertAutoResult(IPropertyEngine propertyEngine,String trade_id,
			String request_url,String auth_url,String name,String cardNo,String idType,String nation,int mode) throws Exception {
		 EncryptedReservedLocal erd = new EncryptedReservedLocal();
		 BASE64Encoder be = new BASE64Encoder();
		int timeout = Integer.valueOf(propertyEngine.readById("ds_police_auth_timeout"));
		String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    String customerNumber = propertyEngine.readById("ds_polcieV2_signAuth_id");
        //应用名称
        String appName = "Alipay";
        //时间戳
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        //活体控件版本
        String liveDetectionControlVersion = "1230";

        IdAuthData idAuth = new IdAuthData();
        idAuth.setIdNumber(cardNo);
        idAuth.setIdType(idType);
        idAuth.setNation(nation);
        idAuth.setName(name);
        idAuth.setBirthDate("");
        idAuth.setExpiryDate("");
        idAuth.setSex("");
        idAuth.setRandNum(trade_id);

        AuthenticationApplicationDataCert.BizPackageBean bizPackageBean = new AuthenticationApplicationDataCert.BizPackageBean();
        bizPackageBean.setCustNum(customerNumber);
        bizPackageBean.setAppName(appName);
        bizPackageBean.setTimeStamp(timeStamp);
        bizPackageBean.setLiveDetectCtrlVer(liveDetectionControlVersion);
        bizPackageBean.setBizSerialNum(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        bizPackageBean.setIdAuthData(be.encode(erd.encryptEnvelope(JSON.toJSONString(idAuth).getBytes(),trade_id,signUrl)));
        bizPackageBean.setAuthMode(mode);
        JSONObject applyObj = JSONObject.fromObject(bizPackageBean);

        //身份认证申请签名
        DataSignatureLocal ds = new DataSignatureLocal();

        //吉大正元签名
        String signature = ds.signature(trade_id,signUrl,customerNumber,applyObj.toString().getBytes());

        logger.info("{} 公安一所2.0认证申请包签名结果:{}",trade_id,signature);
        AuthenticationApplicationDataCert appData = new AuthenticationApplicationDataCert();
        appData.setBizPackage(bizPackageBean);
        appData.setSign(signature);
        JSONObject obj = JSONObject.fromObject(appData);


        //身份认证申请包
        String ApplyData = obj.toString();

        //Http接受返回的数据
        logger.info("{} 公安一所2.0认证请求数据:{}",trade_id,ApplyData);
        Map<String, String> headers = new HashMap<String, String>();
        //Http接受返回的数据
        String json = RequestHelper.doPost(request_url, null, headers, obj,null,false,timeout);
        logger.info("{} 公安一所2.0认证请求应答数据:{}",trade_id,json);

        JSONObject jsonobject=JSONObject.fromObject(json);
        AuthenticationApplicationDataCert data= (AuthenticationApplicationDataCert) JSONObject.toBean(jsonobject,AuthenticationApplicationDataCert.class);
        AuthenticationApplicationDataCert.BizPackageBean adb = data.getBizPackage();

        
        //获取业务流水号
        String BusinessSerialNumber = adb.getBizSerialNum();
        idAuth.setRandNum("");
        logger.info("{} 公安一所2.0交易流水号:{}",trade_id,BusinessSerialNumber);
        adb.setCustNum(customerNumber);
        adb.setAppName(appName);
        adb.setTimeStamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        adb.setLiveDetectCtrlVer(liveDetectionControlVersion);
        adb.setIdAuthData(be.encode(erd.encryptEnvelope(JSON.toJSONString(idAuth).getBytes(),trade_id,signUrl)));
        adb.setBizSerialNum(BusinessSerialNumber);
        //认证模式
        adb.setAuthMode(mode);


        AuthenticationApplicationDataCert add = new AuthenticationApplicationDataCert();
        JSONObject objAdd = JSONObject.fromObject(adb);
        add.setBizPackage(adb);

        //身份认证请求包签名(吉大正元)
        String signature2 = ds.signature(trade_id,signUrl,customerNumber,objAdd.toString().getBytes());
        add.setSign(signature2);
        //身份认证包
        JSONObject obj2 = JSONObject.fromObject(add);
        logger.info("{} 公安一所2.0认证2请求数据:{}",trade_id,obj2.toString());
        //Http接受返回的数据
        String json2 = RequestHelper.doPost(auth_url, null, headers, obj2,null,false,timeout);
        logger.info("{} 公安一所2.0认证2应答数据:{}",trade_id,json2);
        return json2;
    }
}
