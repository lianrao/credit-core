package com.wanda.credit.ds.client.yidaoOcr;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
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
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


@DataSourceClass(bindingDataSourceId = "ds_yidao_cloud_invoice")
public class YidaoInvoiceRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(YidaoInvoiceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String yidao_invoice_url = propertyEngine.readById("yidao_cloud_invoice_url");
		
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		// 请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setDs_id(ds.getId());// log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("0");// 不缓存
		logObj.setReq_url(yidao_invoice_url);
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			rets = new HashMap<String, Object>();
			String invoice_type = ParamUtil.findValue(ds.getParams_in(), "invoice_type").toString();
			String invoice_no = ParamUtil.findValue(ds.getParams_in(), "invoice_no").toString();
			String invoice_code = ParamUtil.findValue(ds.getParams_in(), "invoice_code").toString();
			String check_code_last6 = "";
			if(ParamUtil.findValue(ds.getParams_in(), "check_code_last6")!=null){
				check_code_last6 = ParamUtil.findValue(ds.getParams_in(), "check_code_last6").toString();
			}
			String total = "";
			if(ParamUtil.findValue(ds.getParams_in(), "total")!=null){
				total = ParamUtil.findValue(ds.getParams_in(), "total").toString();
			}
			String create_date = ParamUtil.findValue(ds.getParams_in(), "create_date").toString();
			// 记录入参到入参记录表
			reqparam.put("invoice_type", invoice_type);
			reqparam.put("invoice_no", invoice_no);
			reqparam.put("invoice_code", invoice_code);
			reqparam.put("check_code_last6", check_code_last6);
			reqparam.put("total", total);
			reqparam.put("create_date", create_date);
			
			logger.info("{} 易道发票验真请求开始...", prefix);
			String result = RequestHelper.doPost(yidao_invoice_url, 
					paramOptions(invoice_type,invoice_no,invoice_code,
							check_code_last6,total,create_date), false,time_out);
			logger.info("{} 易道发票验真请求完成:{}", prefix,result);
			JSONObject json = JSONObject.parseObject(result);
			logObj.setBiz_code1(json.getString("request_id"));
			String error_code = json.getString("error_code");
			if("0".equals(error_code)){
				JSONObject result_json = json.getJSONObject("result");
				String result_code = result_json.getString("result_code");
				if("1".equals(result_code)){
					resource_tag = Conts.TAG_TST_SUCCESS;
					logger.info("{} 易道发票验真成功", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA, result_json.get("data"));
					rets.put(Conts.KEY_RET_MSG, "交易成功!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("6".equals(result_code)){
					logger.warn("{} 认证不一致",trade_id);
					resource_tag = Conts.TAG_UNMATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "发票不一致");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});				
				}else if("2".equals(result_code)){
					logger.warn("{} 超过该张票当天查验次数",trade_id);
					resource_tag = Conts.TAG_UNMATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU3_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "超过该张票当天查验次数");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("9".equals(result_code)){
					logger.warn("{} 所查发票不存在",trade_id);
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JINGZHONG_IHCACHE_NULL);
					rets.put(Conts.KEY_RET_MSG, "所查发票不存在");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("105".equals(result_code)){
					logger.warn("{} 查询发票不规范",trade_id);
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU4_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "查询发票不规范");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else{
					logger.warn("{} 查询失败",trade_id);
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "查询失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}
			}else if("3203".equals(error_code)){
				logger.warn("{} 超过该张票当天查验次数",trade_id);
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU3_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "超过该张票当天查验次数");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}else if("3204".equals(error_code)){
				logger.warn("{} 超过该张票当天查验次数",trade_id);
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED);
				rets.put(Conts.KEY_RET_MSG, "发票代码格式错误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}else{
				logger.warn("{} 查询失败",trade_id);
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "查询失败:"+json.getString("description"));
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}
			
		} catch (Exception ex) {
			resource_tag = Conts.TAG_TST_FAIL;
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "查询失败");
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));

			/** 如果是超时异常 记录超时信息 */
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			if (ExceptionUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				resource_tag = Conts.TAG_SYS_TIMEOUT;
			}
			logObj.setState_msg(ex.getMessage());
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		} finally {
			// log入库
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}

	public Map<String, String> paramOptions(String invoice_type,String invoice_no,String invoice_code,
			String check_code_last6,String total,String create_date) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("app_key", propertyEngine.readById("yidao_ocr_app_key"));
		params.put("app_secret", propertyEngine.readById("yidao_ocr_app_secret"));
		params.put("invoice_type", invoice_type);
	    params.put("invoice_no", invoice_no);
	    params.put("invoice_code", invoice_code);
	    params.put("check_code_last6", check_code_last6);
	    params.put("total", total);
	    params.put("create_date", create_date);

		return params;
	}
}
