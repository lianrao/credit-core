package com.wanda.credit.ds.client.policeAuthV2;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.policeAuthV2.beans.AuthenticationApplicationData;
import com.wanda.credit.ds.client.policeAuthV2.beans.AuthenticationData;
import com.wanda.credit.ds.client.policeAuthV2.beans.ReservedDataEntity;
import com.wanda.credit.ds.client.policeAuthV2.data.DataSignatureLocal;
import com.wanda.credit.ds.client.policeAuthV2.data.EncryptedReservedLocal;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.police.Police_Face_Result;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;
@DataSourceClass(bindingDataSourceId="ds_policeAuth_four")
public class PoliceFourCheckRequestor extends BasePoliceV2Requestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(PoliceFourCheckRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 公安一所四项认证请求开始...", prefix);
		Map<String, Object> rets = null;
		Map<String, Object> retdata = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String request_url = propertyEngine.readById("ds_policev2_request_url");//国政通调用连接
		String auth_url = propertyEngine.readById("ds_police_auth_url");//国政通账号
		
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); // 身份证号码
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 姓名			
			String start_date = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //有效期开始
			String end_date = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString(); //有效期截止,长期填8个0

			logObj.setDs_id(ds.getId());
			logObj.setIncache("0");
			rets = new HashMap<String, Object>();	 		
			logger.info("{} 公安一所四项认证加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("start_date", start_date);
			reqparam.put("end_date", end_date);
			logObj.setReq_url(auth_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
				rets.put(Conts.KEY_RET_MSG, "姓名格式错误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			if(!isValidDate(start_date) || !isValidDate(end_date)){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_PENYUAN_PARAMETER_FAILED);
				rets.put(Conts.KEY_RET_MSG, "输入的有效日期格式有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			Police_Face_Result result_json = getSignPoliceResult(trade_id,request_url,auth_url,name,cardNo,start_date,end_date);
			if("0XXX".equals(result_json.getAuthResult())){
				resource_tag = Conts.TAG_MATCH;
				retdata.put("final_auth_result", "0");//一致
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_MSG, "交易成功");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}else if("5XXX".equals(result_json.getAuthResult())){
				resource_tag = Conts.TAG_UNMATCH;
				retdata.put("final_auth_result", "1");//不一致
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_MSG, "交易成功");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "公安四项认证失败");
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
	public Police_Face_Result getSignPoliceResult(String trade_id,
			String request_url,String auth_url,String name,String cardNo,String start_date,String end_date) throws Exception{
		logger.info("{} 公安一所2.0认证开始...",trade_id);
		int timeout = Integer.valueOf(propertyEngine.readById("ds_police_auth_timeout"));
		String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
        String customerNumber = propertyEngine.readById("ds_polcieV2_signAuth_id");
        //应用名称
        String appName = "Alipay";
        //时间戳
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
//        String timeStamp = "201809131559000000";
        //读卡控件版本
        String cardReaderVersion = "1230";
        //活体控件版本
        String liveDetectionControlVersion = "1230";
        //认证码控件版本
        String authCodeControlVersion = "1230";
        //认证模式
        String authMode = "0x10";

        AuthenticationApplicationData.BizPackageBean bizPackageBean = new AuthenticationApplicationData.BizPackageBean();
        bizPackageBean.setCustomerNumber(customerNumber);
        bizPackageBean.setAppName(appName);
        bizPackageBean.setTimeStamp(timeStamp);
        bizPackageBean.setCardReaderVersion(cardReaderVersion);
        bizPackageBean.setLiveDetectionControlVersion(liveDetectionControlVersion);
        bizPackageBean.setAuthCodeControlVersion(authCodeControlVersion);
        bizPackageBean.setAuthMode(authMode);
        JSONObject applyObj = JSONObject.fromObject(bizPackageBean);

        //身份认证申请签名
        DataSignatureLocal ds = new DataSignatureLocal();
//        DataSignature ds = new DataSignature();

        //吉大正元签名
        long start_request = new Date().getTime();
        String signature = ds.signature(trade_id,signUrl,customerNumber,applyObj.toString().getBytes());
        long end_request = new Date().getTime();
        logger.info("{} 公安一所2.0认证申请包签名结果:{}",trade_id,signature);

        AuthenticationApplicationData appData = new AuthenticationApplicationData();
        appData.setBizPackage(bizPackageBean);
        appData.setSign(signature);
        JSONObject obj = JSONObject.fromObject(appData);

        //身份认证申请包
        String ApplyData = obj.toString();
        logger.info("{} 公安一所2.0认证请求数据:{}",trade_id,ApplyData);
        Map<String, String> headers = new HashMap<String, String>();
        //Http接受返回的数据
        String json = RequestHelper.doPost(request_url, null, headers, obj,null,false,timeout);
        logger.info("{} 公安一所2.0认证请求应答数据:{}",trade_id,json);

        /**
         * 组装身份认证请求包
         */
        JSONObject jsonobject=JSONObject.fromObject(json);
        AuthenticationData data= (AuthenticationData) JSONObject.toBean(jsonobject,AuthenticationData.class);
        AuthenticationData.BizPackageBean adb = data.getBizPackage();

        //获取业务流水号
        String BusinessSerialNumber = adb.getBusinessSerialNumber();

        adb.setCustomNumber(customerNumber);
        adb.setAppName(appName);
        adb.setTimeStamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

        //模拟时间戳
        adb.setTimeStamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        adb.setBusinessSerialNumber(BusinessSerialNumber);

        //认证模式
        adb.setAuthMode(authMode);
        //照片
//        adb.setPhotoData(picture);
        //认证码数据
        adb.setAuthCode(null);
        //ID验证数据
        adb.setIdcardAuthData(null);
        //认证保留数据(填写真实的信息)
        ReservedDataEntity.SFXXBean rs = new ReservedDataEntity.SFXXBean();
        rs.setxM(name);
        rs.setgMSFZHM(cardNo);
        rs.setyXQQSRQ(start_date);//有效期开始
        rs.setyXQJZRQ(end_date);//有效期截止
        
        
        ReservedDataEntity.WZXXBean rw = new ReservedDataEntity.WZXXBean();
        rw.setBusinessType("test");
        rw.setDealDate(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        rw.setVenderName("yingxininfo");

        ReservedDataEntity.ZPBean rz = new ReservedDataEntity.ZPBean();

        ReservedDataEntity re = new ReservedDataEntity();
        re.setsFXX(rs);
        re.setwZXX(rw);
        re.setzP(rz);
        JSONObject jo = JSONObject.fromObject(re);

        EncryptedReservedLocal erd = new EncryptedReservedLocal();
//        EncryptedReservedData erd = new EncryptedReservedData();
        BASE64Encoder be = new BASE64Encoder();
        //加密保留数据(吉大正元)
        String authApplyRetainData=be.encode(erd.encryptEnvelope(jo.toString().getBytes(),trade_id,signUrl));

        adb.setAuthApplyRetainData(authApplyRetainData);

        AuthenticationData add = new AuthenticationData();
        JSONObject objAdd = JSONObject.fromObject(adb);
        add.setBizPackage(adb);

        //身份认证请求包签名(吉大正元)
        long start_auth = new Date().getTime();
        String signature2 = ds.signature(trade_id,signUrl,customerNumber,objAdd.toString().getBytes());
        long end_auth = new Date().getTime();
        add.setSign(signature2);
        //身份认证包
        JSONObject obj2 = JSONObject.fromObject(add);
        //Http接受返回的数据
        String json2 = RequestHelper.doPost(auth_url, null, headers, obj2,null,false,timeout);
        logger.info("{} 公安一所2.0认证2应答数据:{}",trade_id,json2);
        com.alibaba.fastjson.JSONObject json_result = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(json2);
		Police_Face_Result result = com.alibaba.fastjson.JSONObject.parseObject(json_result.getString("bizPackage"),
				Police_Face_Result.class);
		result.setRequest_cost(end_request-start_request);
		result.setAuth_cost(end_auth-start_auth);
        return result;
	}
	public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        if("00000000".equals(str))
        	return convertSuccess;
        SimpleDateFormat format = new SimpleDateFormat("yyyymmdd");
        try {
       	// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
           format.setLenient(false);
           format.parse(str);
        } catch (ParseException e) {
           e.printStackTrace();
           convertSuccess=false;
       } 
       return convertSuccess;
	}
}
