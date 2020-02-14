package com.wanda.credit.ds.client.jixin;

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
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_jixin_AuthenBankCard2")
public class JiXinAuthenBankCard2DataSourceRequestor extends BaseDataSourceRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(JiXinAuthenBankCard2DataSourceRequestor.class);

	@Autowired
	protected PropertyEngine propertyEngine;

    @Autowired
    protected IAllAuthCardService allAuthCardService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
        String url = propertyEngine.readById("ds_jx_bankcard_url");
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
        logObj.setIncache("0");
		try{			
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
	 		String transCode =  "105"; //交易码
			String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();  //姓名 
			String cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString(); //银行卡号

            String bankName = (String) ParamUtil.findValue(ds.getParams_in(),
                    "bankName");
            if(!StringUtil.isEmpty(bankName)){
                logObj.setBiz_code2(bankName);
            }

			reqparam.put("name", name);
			reqparam.put("cardId", cardId);


			CommonBean reqdata = new CommonBean();
			setupReqData(trade_id,reqdata,transCode);
			setBusiReqData(trade_id,reqdata,name,"",cardId,"");
			byte[] sendPacket = TransUtil.packet(reqdata, propertyEngine.readById("ds_jx_bankcard_key"));
			
            int readTimeout = 5000;
            int connTimeout = 5000;
            try{
                readTimeout = Integer.parseInt(propertyEngine.readById("req_read_timeout"));
                connTimeout = Integer.parseInt(propertyEngine.readById("req_conn_timeout"));
            }catch (Exception e){
                logger.error(" 读取propertyEngine属性（http time out）失败 {}", prefix, e);
            }
            logger.info("{} 开始请求远程服务器... ", prefix);
            logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			String response = CommonUtil.post(url, sendPacket, readTimeout, connTimeout);
            logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logger.info("{} 厂商返回响应信息: {}",prefix,response);

            if(!StringUtils.isEmpty(response)){
            	JSONObject rspDataJsn = JSONObject.parseObject(response);
                logger.info("{} 厂商返回响应码等信息 {} {}",new Object[]{prefix,rspDataJsn.get("returncode"),rspDataJsn.get("errtext")});
    			logObj.setBiz_code1(rspDataJsn.get("returncode") + "-" + rspDataJsn.get("errtext"));
                logObj.setBiz_code3(String.valueOf(rspDataJsn.get("orderid")));

                retdata.put("name", name);
                retdata.put("cardId", cardId);
                resource_tag = visitBusiData(trade_id,rspDataJsn, transCode, retdata,rets);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			}else{
				resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logger.error("{} 厂商返回异常",trade_id);

				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败!");		
                return rets;
			}		
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error(prefix+" 数据源处理时异常：{}",ex);
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
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
	
	private void setBusiReqData(String trade_id, CommonBean reqdata, String name, String cardNo, String cardId,
			String phone) {
		/*  idtype 证件类型 是 String(2) 暂只支持身份证 01 
			idcard 证件号码 是 String(30)   
			username 姓名 是 String(50) 乔布斯 
			bankcard */
		reqdata.setUsername(name);
		if(StringUtils.isNotBlank(phone)){
			reqdata.setMobile(phone);
		}
		if(StringUtils.isNotBlank(cardId)){
			reqdata.setBankcard(cardId);
		}
		if(StringUtils.isNotBlank(cardNo)){
			reqdata.setIdtype("01");
			reqdata.setIdcard(cardNo);
		}		
	} 

	private void setupReqData(String trade_id, CommonBean reqdata, String transCode) {
		reqdata.setTranscode(transCode);
		reqdata.setVersion("0100");
		reqdata.setOrdersn(trade_id);
		reqdata.setDsorderid(trade_id);
		//使用相应的商户号即可
        reqdata.setMerchno(propertyEngine.readById("ds_jx_bankcard_no"));
//		reqdata.setMerchno("0000000000000411");
 	}

	private String visitBusiData(String trade_id, JSONObject rspDataJsn,
			String transCode, Map<String,Object> retrnMap,Map<String,Object> rets) {
		String resource_tag = Conts.TAG_SYS_ERROR;
        String respCode = String.valueOf(rspDataJsn.get("platformCode"));

		if("001000000".equals(respCode)){
			resource_tag = Conts.TAG_TST_SUCCESS;
			retrnMap.put("respCode", "2000");
			retrnMap.put("respDesc", "认证一致");
		}else if("001010034".equals(respCode)){
			resource_tag = Conts.TAG_TST_SUCCESS;
			retrnMap.put("respCode", "2001");
			retrnMap.put("respDesc", "认证不一致");
        }else if("001010035".equals(respCode)){
        	resource_tag = Conts.TAG_TST_SUCCESS;
			retrnMap.put("respCode", "2001");
			retrnMap.put("respDesc", "认证不一致");
        }else if("001010036".equals(respCode)){
        	resource_tag = Conts.TAG_TST_SUCCESS;
			retrnMap.put("respCode", "2001");
			retrnMap.put("respDesc", "认证不一致");
        }else if("001012002".equals(respCode)){
        	resource_tag = Conts.TAG_TST_SUCCESS;
			retrnMap.put("respCode", "2001");
			retrnMap.put("respDesc", "认证不一致");
        }else if("001050003".equals(respCode)){
			retrnMap.put("respCode", "2001");
			retrnMap.put("respDesc", "认证不一致");
        }else if("001050011".equals(respCode)){
			retrnMap.put("respCode", "2003");
			retrnMap.put("respDesc", "不支持验证");
        }else if("001050012".equals(respCode)){
			retrnMap.put("respCode", "2003");
			retrnMap.put("respDesc", "不支持验证");
        }else if("001050013".equals(respCode)){
			retrnMap.put("respCode", "2003");
			retrnMap.put("respDesc", "不支持验证");
        }else if("001050014".equals(respCode)){
			retrnMap.put("respCode", "2003");
			retrnMap.put("respDesc", "不支持验证");
        }else if("001050037".equals(respCode)){
			retrnMap.put("respCode", "2001");
			retrnMap.put("respDesc", "认证不一致");
        }else if("001050041".equals(respCode)){
        	retrnMap.put("respCode", "2003");
			retrnMap.put("respDesc", "不支持验证");
        }else if("001050043".equals(respCode)){
        	retrnMap.put("respCode", "2003");
			retrnMap.put("respDesc", "不支持验证");
        }else if("001052001".equals(respCode)){
        	rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
			rets.put(Conts.KEY_RET_MSG, "传入参数格式有误:卡号错误");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{}公安数据源厂商返回异常! ",trade_id);
			return resource_tag;
        }else if("001050060".equals(respCode)){
        	rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_INVALID_PARAM);
			rets.put(Conts.KEY_RET_MSG, "您输入的多个参数无效，请核对后重新输入!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{}公安数据源厂商返回异常! ",trade_id);
			return resource_tag;
        }else{
        	rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:"+rspDataJsn.get("errtext"));
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{}数据源厂商返回异常! ",trade_id);
			return resource_tag;
        }
		
		
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_DATA, retrnMap);
		rets.put(Conts.KEY_RET_MSG, "交易成功");
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return resource_tag;
	}
}
