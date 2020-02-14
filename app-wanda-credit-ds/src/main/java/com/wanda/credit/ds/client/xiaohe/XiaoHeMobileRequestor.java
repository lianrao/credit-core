package com.wanda.credit.ds.client.xiaohe;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.wanda.credit.base.util.IPUtils;
import com.wanda.credit.base.util.MobileCarrier;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ppxin.BasePPXDSRequestor;
import com.wanda.credit.ds.client.ppxin.bean.JuheMobileBean;
import com.wanda.credit.ds.client.xiaohe.utils.MD5Util;
import com.wanda.credit.ds.client.xiaohe.utils.XmlTool;
import com.wanda.credit.ds.client.xiaohe.utils.httpUtils;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_xiaohe_mobileCheck")
public class XiaoHeMobileRequestor extends BasePPXDSRequestor implements IDataSourceRequestor {
	private Logger logger = LoggerFactory.getLogger(XiaoHeMobileRequestor.class);

	@Autowired
	private IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		String mobile_api = propertyEngine.readById("ds_xiaohe_mobile_api");
		String police_url = propertyEngine.readById("ds_xiaohe_police_url");
		String police_api_key = this.propertyEngine.readById("ds_xiaohe_police_api_key");
	    String police_hashcode = this.propertyEngine.readById("ds_xiaohe_police_hashcode");
		
		String juhe_url = propertyEngine.readById("ds_juhe_localMobile_url");
		String juhe_key = propertyEngine.readById("ds_juhe_localMobile_key");
		
		String cacheTimeStr = propertyEngine.readById("ds_ja_cacheSec");
        String isCache = propertyEngine.readById("ds_ja_isCache");
        String exception = propertyEngine.readById("ds_ja_exce");
        
        String invented_code = propertyEngine.readById("ds_mobile_invented_code");
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
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retData = new TreeMap<String, Object>();	
		Map<String, Object> reqparam = new HashMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(mobile_api);
		logObj.setBiz_code3(IPUtils.getLocalIP());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		
		try{
			logger.info("{} 开始解析传入的参数" , prefix);
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString().toUpperCase();
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//账户号

			logger.info("{} 解析传入的参数成功" , prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("mobile", mobile);
			//加密敏感信息
			String encardNo = GladDESUtils.encrypt(cardNo);
			String enMobile = GladDESUtils.encrypt(mobile);
			//参数校验 - 身份证号码和手机号
			String validate = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(validate)) {
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
			String carrier = mobile.substring(0,3);
			if("1".equals(isCache)){
                logger.info("{} 开启缓存规则", prefix);
                GeoMobileCheck cacheData = isIncache(name, encardNo, enMobile, cacheTime, prefix);
                if(cacheData != null){
                    logger.info("{} 匹配到缓存数据{}", prefix , cacheData.getCheckResult());
                    String cacheAttr = cacheData.getAttribute();
                    if(StringUtils.isNotEmpty(cacheAttr)){
                        retData.put(ATTRIBUTE, cacheAttr);
                        if(cacheAttr.contains("移动")){
                            retData.put(ATTRIBUTE_EN, CHINA_MOBILE);
                        }else if(cacheAttr.contains("联通")){
                            retData.put(ATTRIBUTE_EN, CHINA_UNICOM);
                        }else if(cacheAttr.contains("电信")){
                            retData.put(ATTRIBUTE_EN, CHINA_TELECOM);
                        }else {
                            retData.put(ATTRIBUTE_EN, CHINA_OTHERS);
                        }
                    }else {
                    	String attr = MobileCarrier.getIspGroupByCID(mobile);
						if (MobileCarrier.CHINA_MOBILE.equals(attr)) {
							retData.put(ATTRIBUTE_EN, CHINA_MOBILE);
							retData.put(ATTRIBUTE,"移动");
						}else if(MobileCarrier.CHINA_UNICOM.equals(attr)){
							retData.put(ATTRIBUTE_EN, CHINA_UNICOM);
							retData.put(ATTRIBUTE,"联通");
						}else if(MobileCarrier.CHINA_TELECOM.equals(attr)){
							retData.put(ATTRIBUTE_EN, CHINA_TELECOM);
							retData.put(ATTRIBUTE,"电信");
						}else{
							retData.put(ATTRIBUTE,"" );
							retData.put(ATTRIBUTE_EN, "");	
						}
                    }
                 
                    String cacheCheckRes = cacheData.getCheckResult();
                    resource_tag = bulidAllCacheTag(retData.get(ATTRIBUTE_EN).toString(), cacheCheckRes);
                    retData.put(PROVICE, cacheData.getProvince()==null?"":cacheData.getProvince());
                    retData.put(CITY, cacheData.getCity()==null?"":cacheData.getCity());
                    retData.put(CHECK_RESULT, cacheCheckRes);
                    rets.put(Conts.KEY_RET_DATA, retData);
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
            	logger.info("{} 小河实时三要素验证开始...", prefix);
            	Date now = new Date(); 
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//可以方便地修改日期格式
				String sdate = dateFormat.format(now);
				
            	Map<String, String> paramMap = new HashMap<String, String>();
        		paramMap.put("Hashcode", police_hashcode);
        		paramMap.put("passname", name);
        		paramMap.put("pid", cardNo);
        		paramMap.put("mobile", mobile);

        		String sign=paramMap.get("Hashcode")+paramMap.get("mobile")+paramMap.get("passname")
        		+paramMap.get("pid")+police_api_key+sdate;
				sign=MD5Util.MD5(sign);
				paramMap.put("sign",sign);//姓名
            	String httpresult=httpUtils.httpsGet(trade_id,"https", police_url, "443",mobile_api, paramMap);
            	logger.info("{} 小河实时三要素验证调用成功:{}", prefix,httpresult);
            	if(StringUtils.isEmpty(httpresult)){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源调用失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{} 公安数据源厂商返回异常! ",prefix);
					return rets;
				}
            	JSONObject json=XmlTool.documentToJSONObject(httpresult);
				JSONObject police = (JSONObject) json.getJSONArray("ErrorRes").get(0);
				String code = police.getString("Err_code");
				logObj.setBiz_code1(code);
				if("200".equals(code)){
					retData.put(CHECK_RESULT, "0");
				}else if("404".equals(code)){
					retData.put(CHECK_RESULT, "1");
				}else if("503".equals(code)){
					retData.put(CHECK_RESULT, "8");
					if(getInventedCode(invented_code,carrier)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_NOT_SUPPORT_VNO);
						rets.put(Conts.KEY_RET_MSG, "暂不支持虚拟运营商");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{}运营商查询失败,不支持虚拟号 ",prefix);
						return rets;
					}
				}else if("501".equals(code)){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
					rets.put(Conts.KEY_RET_MSG, "您输入的手机号码无效，请核对后重新输入");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{}运营商查询失败 ",prefix);
					return rets;
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源厂商返回异常!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{}公安数据源厂商返回异常! ",prefix);
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
				retData = parseLocatin(location, retData);
				//构建返回标签
				resource_tag = buildTags(code,retData.get(ATTRIBUTE_EN).toString());
				//解析结果用于保存
				parseToSave(trade_id,name,encardNo,enMobile,location,retData);
				//拼装返回信息
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "请求成功");
				rets.put(Conts.KEY_RET_DATA, retData);
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				//记录日志信息
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setState_msg("交易成功");
            }
			
		}catch(Exception e){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(e));
			if (ExceptionUtil.isTimeoutException(e)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + e.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		logger.info("{} 身份验证End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	}
	private String buildTags(String queryStatus, String attributeEn) {
		String resource_tag = Conts.TAG_TST_FAIL;
		if("200".equals(queryStatus) || "404".equals(queryStatus)){
			if (CHINA_MOBILE.equals(attributeEn)) {
				resource_tag = Conts.TAG_CHECK_CMCC_FOUND1;
			}else if(CHINA_UNICOM.equals(attributeEn)){
				resource_tag = Conts.TAG_CHECK_CUCC_FOUND1;
			}else if(CHINA_TELECOM.equals(attributeEn)){
				resource_tag = Conts.TAG_CHECK_CTCC_FOUND1;
			}else {
				resource_tag = Conts.TAG_CHECK_FOUND1_OTHERS;
			}
		}else{
			resource_tag = Conts.TAG_TST_FAIL;
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
