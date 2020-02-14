package com.wanda.credit.ds.client.aijin;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.aijin.beans.YuanJin_CarCheck;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_aijin_carCheck")
public class AiJinCarCheckSourceRequestor extends BaseAijinDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(AiJinCarCheckSourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 爱金车辆核查数据源请求开始...", prefix);
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String yuanjin_face_url = propertyEngine.readById("ds_yuanjin_face_url");//爰金调用连接
		String yuanjin_face_account = propertyEngine.readById("ds_yuanjin_car_account");//爰金调用连接
		String yuanjin_face_acode = propertyEngine.readById("ds_yuanjin_car_acode");//服务代码
		String yuanjin_face_key = propertyEngine.readById("ds_yuanjin_car_key");//服务秘钥

		String enCardNo = "";

		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); //姓名
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			enCardNo = GladDESUtils.encrypt(cardNo);
			logger.info("{} 爱金车辆核查数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(yuanjin_face_url);
			logObj.setIncache("0");
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

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
			String param = AESUtil.encrypt(yuanjin_face_key, "name="+name+"&idnumber="+cardNo+"&clientOrderNumber="+trade_id);
	        String sign = AESUtil.md5(yuanjin_face_acode + param + yuanjin_face_account + AESUtil.md5(yuanjin_face_key));//生成签名

	        String post_data = null;
	        try {
	            post_data = "acode=" + yuanjin_face_acode + "&param=" + URLEncoder.encode(param, "UTF-8") + "&account="
	                    + yuanjin_face_account + "&sign=" + sign;
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        logger.info("{} 爱金车辆核查请求开始...", prefix);
	        String res = postHtml(yuanjin_face_url, post_data);
	        logger.info("{} 爱金车辆核查请求结束,返回信息:{}", prefix,res);
	        if(StringUtil.isEmpty(res)){
				logger.error("{} 驾驶证查询返回异常！", prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "驾驶证查询失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logObj.setState_msg("驾驶证查询返回异常");
				return rets;
			}
	        YuanJin_CarCheck carCheck = com.alibaba.fastjson.JSONObject.parseObject(res,YuanJin_CarCheck.class);
	        logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
	        if("100".equals(carCheck.getResponseCode())){
	        	if("1".equals(carCheck.getResult())){
	        		resource_tag = Conts.TAG_TST_SUCCESS;
	        		retdata.put("result", carCheck.getResultData());
	        	}else if("2".equals(carCheck.getResult())){
	        		resource_tag = Conts.TAG_TST_SUCCESS;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});					
					return rets;
	        	}else if("3".equals(carCheck.getResult())){
	        		resource_tag = Conts.TAG_TST_SUCCESS;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_DWEI_CAR01);
					rets.put(Conts.KEY_RET_MSG, "身份证号未命中");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
	        	}else{
	        		logger.error("{} 驾驶证查询返回异常01", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_DRIVERLICENSE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "驾驶证信息核查失败!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_msg("驾驶证查询返回异常");
					return rets;
	        	}
	        }else{
	        	logger.error("{} 驾驶证查询返回异常01", prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_DRIVERLICENSE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "驾驶证信息核查失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logObj.setState_msg("驾驶证查询返回异常");
				return rets;
	        }
	        rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "交易成功");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
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
}