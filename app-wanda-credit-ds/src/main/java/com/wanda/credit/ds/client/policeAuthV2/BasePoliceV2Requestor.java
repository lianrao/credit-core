package com.wanda.credit.ds.client.policeAuthV2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.api.iface.IExecutorNoticeService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.counter.GlobalCounter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.policeAuthV2.beans.AuthenticationApplicationData;
import com.wanda.credit.ds.client.policeAuthV2.beans.AuthenticationData;
import com.wanda.credit.ds.client.policeAuthV2.beans.ReservedDataEntity;
import com.wanda.credit.ds.client.policeAuthV2.data.DataSignatureLocal;
import com.wanda.credit.ds.client.policeAuthV2.data.EncryptedReservedLocal;
import com.wanda.credit.ds.dao.domain.police.Police_Face_Result;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

public class BasePoliceV2Requestor extends BaseDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(BasePoliceV2Requestor.class);
	private final String ds_error_warn_sms = "数据源区间异常次数达到指定阈值,";
	//获取请求结果
	public Police_Face_Result getPoliceResult(IPropertyEngine propertyEngine,String trade_id,
			String request_url,String auth_url,String picture,String name,String cardNo) throws Exception{
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
        String authMode = "0x42";

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
        adb.setPhotoData(picture);
        //认证码数据
        adb.setAuthCode(null);
        //ID验证数据
        adb.setIdcardAuthData(null);
        //认证保留数据(填写真实的信息)
        ReservedDataEntity.SFXXBean rs = new ReservedDataEntity.SFXXBean();
        rs.setxM(name);
        rs.setgMSFZHM(cardNo);

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
	public boolean isInProvince(String cardNo,IPropertyEngine propertyEngine){
		String[] provinces = propertyEngine.readById("ds_police_in_provinces").split(",");
		String sub_card2 = cardNo.substring(0, 2);
		String sub_card4 = cardNo.substring(0, 4);
		for(String province:provinces){
			if(province.length()==2){
				if(sub_card2.equals(province))
					return true;
			}else if(province.length()==4){
				if(sub_card4.equals(province))
					return true;
			}			
		}
		return false;
	}
	/**
	 * 判断交易是否需要捕获异常
	 * @param result
	 * @return ret
	 */
	public boolean isErr(Map<String, Object> result,String ds_errors_watch){
		Object obj_retstatus=result.get(Conts.KEY_RET_STATUS);
		if(obj_retstatus!=null&&obj_retstatus instanceof CRSStatusEnum){
			String status_code =((CRSStatusEnum)obj_retstatus).getRet_sub_code();
			if(StringUtil.areNotEmpty(status_code,ds_errors_watch)
					&&StringUtil.isStrInStrs(status_code, ds_errors_watch.split(","))){ 
				return true;
			}
		}
		return false;
	}
	public boolean isTimeout(String trade_id,String ds_id,String timeout_watch,long cost){
		String ds_errors_watch = timeout_watch;
		if(StringUtils.isEmpty(ds_id)||StringUtils.isEmpty(ds_errors_watch))
			return false;
		logger.info("{} police数据源超时计数开始:{}",trade_id,ds_errors_watch);
		for(String err:ds_errors_watch.split(",")){
			String[] errs = err.split(":");
			if(errs[0].equals(ds_id)){
				long err_cost = Integer.valueOf(errs[1]);
				if(cost>=err_cost){
					logger.info("{} police数据源:{} 超时计数+1",trade_id,ds_id);
					return true;
				}					
			}
		}		
		return false;
	}
	public final String  tail_errorlist="_errorlist";
	
	public boolean isfuseOff(String trade_id,String ds_ids,String ds_max_error_num
			,String ds_sms_time_rate,String ds_ab_watch,
			String ds_ab_expire_sec,String mobiles,IExecutorNoticeService executorNoticeService) throws ServiceException{
		final String prefix = trade_id; //流水号标识
		final String ds_id = ds_ids;
		String max_error_num_strs = ds_max_error_num;//数据源error熔断阈值
		
		String ds_error_flag = ds_id + tail_errorlist;
		final int ds_error_count_value=GlobalCounter.getCount(ds_error_flag);
		//熔断阈值定制化
		int  max_error_num=getDsErrorConfigMax(max_error_num_strs, max_error_num_strs.indexOf(ds_id)>-1?ds_id:"*");
		logger.info("{} police监控情况：KEY:{},COUNT:{},MAX_LIMIT:{}", new Object[]{prefix,ds_error_flag,ds_error_count_value,max_error_num});
		if(ds_error_count_value>=max_error_num){
			logger.warn("{} police熔断阈值触达,自动关闭开启!!",prefix);
			if(!GlobalCounter.exists(tail_errorlist, ds_id)){
				GlobalCounter.regist(tail_errorlist, ds_id, Integer.parseInt(ds_ab_expire_sec));
				executorNoticeService.sendSmsMsg(mobiles,ds_error_warn_sms +"接口ID:"+ds_id
						 +",当前异常汇总:"+ds_error_count_value,"预警提示");
				logger.info("{} 告警短信发送完毕.",prefix);
			}
			return true;
		}
		return false;
	}
	/**从配置参数字符串（如dsid1:300,dsid2:50,*:10）取得对应ds的熔断阈值，默认10*/
	protected static int getDsErrorConfigMax(String numstring ,String dsid){
		//numstring="dsid1:300,dsid2:50,*:10";
		String[] arr= numstring.split(",");
		for (int i = 0; i < arr.length; i++) {
			String item=arr[i];
			if(item.indexOf(dsid)>-1)
				return Integer.parseInt(item.substring(item.indexOf(":")+1));
		}
		return 10;
	}
	/** 从AB配置中查找最佳DS（如果熔断） 
	 * @throws ServiceException */
	public String findABDs(String ds_ab_watch, String dsid,String trade_id)
			throws ServiceException {
		String next_dsid = dsid;
		logger.info("{} AB方案police切换匹配开始:{}",trade_id,ds_ab_watch);
		if (!StringUtil.isEmpty(ds_ab_watch) && GlobalCounter.exists(tail_errorlist, dsid)) {
			logger.info("{} police数据源:{}需要进行AB切换",trade_id,dsid);
			String[] abs = ds_ab_watch.split(",");
			for (String ab : abs) {
				if(!next_dsid.equals(dsid)){
					break;
				}
				String[] confs = ab.split(":");
				for (int i = 0; i < confs.length; i++) {
					if("ds_policeAuth_photov2".equals(confs[i])){
						continue;
					}
					if(!next_dsid.equals(dsid)){
						break;
					}
					if (dsid.equalsIgnoreCase(confs[i])) {
						for(String conf:confs){
							if("ds_policeAuth_photov2".equals(conf)){
								continue;
							}
							if(!conf.equals(dsid) && !GlobalCounter.exists(tail_errorlist, conf)){
								next_dsid = conf;
								break;
							}
						}
					}
				}
			}
		}
		logger.warn("{} police数据源重定向策略返回数据{}-->{}",trade_id,dsid,next_dsid);
		return next_dsid;
	}
}
