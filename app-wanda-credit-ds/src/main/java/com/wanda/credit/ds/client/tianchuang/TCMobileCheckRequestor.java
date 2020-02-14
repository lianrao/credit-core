package com.wanda.credit.ds.client.tianchuang;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.MobileCarrier;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ppxin.bean.JuheMobileBean;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_tianchuang_mobile")
public class TCMobileCheckRequestor extends BaseTianChSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(TCMobileCheckRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
    protected IAllAuthCardService allAuthCardService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}天创信用数据源请求开始...", prefix);
		String url = propertyEngine.readById("ds_tianchuang_mobile3_url");
		String juhe_url = propertyEngine.readById("ds_juhe_localMobile_url");
		String juhe_key = propertyEngine.readById("ds_juhe_localMobile_key");
		
		String cacheTimeStr = propertyEngine.readById("ds_ja_cacheSec");
        String isCache = propertyEngine.readById("ds_ja_isCache");
        String exception = propertyEngine.readById("ds_ja_exce");
		Map<String, Object> rets = null;
		TreeMap<String, Object> retdata = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[0])); // 身份证号码
			String cardNo = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[1])); // 姓名
			String mobile = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[2])); // 手机号	
			
			Long cacheTime = 0L;
	        logger.info("{} 缓存开关状态：{}", prefix, isCache);
	        logger.info("{} 缓存时间：{}", prefix, cacheTimeStr);
	        logger.info("{} 异常模拟开关状态：{}", prefix, exception);
	        boolean inCache = false;
	        try{
	            if(StringUtil.isEmpty(cacheTimeStr)){
	                cacheTime = 60*60*24*30L; //30天默认
	            }else {
	                cacheTime = Long.parseLong(cacheTimeStr);
	            }
	        }catch (Exception e){
	            logger.error("{} 解析cacheTime失败，使用默认值", prefix);
	            cacheTime = 60*60*24*30L;
	        }
	        
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setTrade_id(trade_id);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("mobile", mobile);
			
			String encardNo = GladDESUtils.encrypt(cardNo);
			String enMobile = GladDESUtils.encrypt(mobile);
			rets = new HashMap<String, Object>();
			retdata = new TreeMap<String, Object>();
			//参数校验 - 身份证号码
			String validate = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(validate)) {
				logObj.setIncache("1");
				logger.info("{} 身份证格式校验错误： {}" , prefix , validate);
				logObj.setState_msg("身份证格式校验错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
				return rets;
			}
			if(!(mobile.length() == 11 && StringUtil.isPositiveInt(mobile))){
				logger.info("{} 手机号码格式错误" , prefix);
				logObj.setState_msg("手机号码格式错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
				return rets;
			}
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
				return rets;
			}
			logObj.setIncache("0");
			
			String carrier = mobile.substring(0,3);
			if("1".equals(isCache)){
                logger.info("{} 开启缓存规则", prefix);
                GeoMobileCheck cacheData = isIncache(name, encardNo, enMobile, cacheTime, prefix);
                if(cacheData != null){
                    logger.info("{} 匹配到缓存数据{}", prefix , cacheData.getCheckResult());
                    String cacheAttr = cacheData.getAttribute();
                    if(StringUtils.isNotEmpty(cacheAttr)){
                        retdata.put(ATTRIBUTE, cacheAttr);
                        if(cacheAttr.contains("移动")){
                        	retdata.put(ATTRIBUTE_EN, CHINA_MOBILE);
                        }else if(cacheAttr.contains("联通")){
                        	retdata.put(ATTRIBUTE_EN, CHINA_UNICOM);
                        }else if(cacheAttr.contains("电信")){
                        	retdata.put(ATTRIBUTE_EN, CHINA_TELECOM);
                        }else {
                        	retdata.put(ATTRIBUTE_EN, CHINA_OTHERS);
                        }
                    }else {
                    	String attr = MobileCarrier.getIspGroupByCID(mobile);
						if (MobileCarrier.CHINA_MOBILE.equals(attr)) {
							retdata.put(ATTRIBUTE_EN, CHINA_MOBILE);
							retdata.put(ATTRIBUTE,"移动");
						}else if(MobileCarrier.CHINA_UNICOM.equals(attr)){
							retdata.put(ATTRIBUTE_EN, CHINA_UNICOM);
							retdata.put(ATTRIBUTE,"联通");
						}else if(MobileCarrier.CHINA_TELECOM.equals(attr)){
							retdata.put(ATTRIBUTE_EN, CHINA_TELECOM);
							retdata.put(ATTRIBUTE,"电信");
						}else{
							retdata.put(ATTRIBUTE,"" );
							retdata.put(ATTRIBUTE_EN, "");	
						}
                    }
                 
                    String cacheCheckRes = cacheData.getCheckResult();
                    resource_tag = bulidAllCacheTag(retdata.get(ATTRIBUTE_EN).toString(), cacheCheckRes);
                    retdata.put(PROVICE, cacheData.getProvince()==null?"":cacheData.getProvince());
                    retdata.put(CITY, cacheData.getCity()==null?"":cacheData.getCity());
                    retdata.put(CHECK_RESULT, cacheCheckRes);
                    rets.put(Conts.KEY_RET_DATA, retdata);
                    rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
                    rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
                    rets.put(Conts.KEY_RET_MSG, "请求成功");
                    logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
                    logObj.setState_msg("交易成功");
                    logObj.setIncache("1");
                    logObj.setBiz_code2(cacheData.getTrade_id());
                    return rets;
                }
            }
			if (!inCache) {
				Map<String, String> param = new HashMap<String,String>();
				param.put("name", name);
				param.put("idcard", cardNo);
				param.put("mobile", mobile);
				String res = verifyDriverLicensce(trade_id,url,param);
				if(StringUtil.isEmpty(res)){
					logger.error("{} 天创信用手机三要素查询返回异常！", prefix);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源厂商返回异常!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{} 手机三要素返回异常! ",prefix);
					return rets;
				}
				logger.error("{} 天创信用手机三要素查询结束:{}", prefix,res);
				JSONObject json = (JSONObject) JSONObject.parse(res);
				if("0".equals(json.getString("status"))){
					JSONObject data = json.getJSONObject("data");
					if("1".equals(data.getString("result"))){
						retdata.put(CHECK_RESULT, "0");
					}else if("2".equals(data.getString("result"))){
						retdata.put(CHECK_RESULT, "1");
					}else{
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "数据源厂商返回异常!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{} 手机三要素返回异常! ",prefix);
						return rets;
					}
					logger.info("{} 本地查询归属地" , prefix);	
					MobileLocation location = new MobileLocation();
					
					logger.info("{} 调用聚合归属地查询接口" , prefix);
					String pexfix = juhe_url+"phone="+mobile+"&key="+juhe_key;
					String juhe_rsp = getMsg(pexfix);
					logger.info("{} 调用聚合归属地查询返回信息:{}" , prefix,juhe_rsp);
					if(!StringUtil.isEmpty(juhe_rsp)){
						JuheMobileBean juhe_json = JSONObject.parseObject(juhe_rsp, JuheMobileBean.class);
						if(juhe_json.getError_code()==0){
							location.setProvince(juhe_json.getResult().getProvince());
			                location.setCity(juhe_json.getResult().getCity());
			                location.setIsp(juhe_json.getResult().getCompany());
						}else{
							logger.info("{} 调用聚合归属地查询失败" , prefix);
						}
					}
					retdata = parseLocatin(location, retdata);
					//构建返回标签
					resource_tag = buildTags(retdata.get(ATTRIBUTE_EN).toString());
					//解析结果用于保存
					parseToSave(trade_id,name,encardNo,enMobile,location,retdata);
					//拼装返回信息
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功");
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					//记录日志信息
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
				}else if("2".equals(json.getString("status"))){
					logger.info("{} 天创信用银行卡三要素传入参数有误", prefix);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
					resource_tag = Conts.TAG_SYS_ERROR;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
					rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else{
					logger.info("{} 天创信用三要素验证失败1", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源厂商返回异常!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{} 手机三要素返回异常! ",prefix);
					return rets;
				}
				
			}
			
			
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
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
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		return rets;
	}
	private String buildTags(String attributeEn) {
		String resource_tag = Conts.TAG_TST_FAIL;
		if (CHINA_MOBILE.equals(attributeEn)) {
			resource_tag = Conts.TAG_CHECK_CMCC_FOUND1;
		}else if(CHINA_UNICOM.equals(attributeEn)){
			resource_tag = Conts.TAG_CHECK_CUCC_FOUND1;
		}else if(CHINA_TELECOM.equals(attributeEn)){
			resource_tag = Conts.TAG_CHECK_CTCC_FOUND1;
		}else {
			resource_tag = Conts.TAG_CHECK_FOUND1_OTHERS;
		}
		return resource_tag;
	}
	//判断是否虚拟号
	public boolean getInventedCode(String invented_code,String carrier){
		for(String tmp:invented_code.split(",")){
			if(carrier.equals(tmp))
				return true;
		}
		return false;
	}
}
