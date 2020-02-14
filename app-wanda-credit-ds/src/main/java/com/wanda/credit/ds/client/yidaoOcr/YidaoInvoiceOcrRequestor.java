package com.wanda.credit.ds.client.yidaoOcr;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImgCompress;
import com.wanda.credit.base.util.JsonFilter;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


@DataSourceClass(bindingDataSourceId = "ds_yidao_invoice_ocr")
public class YidaoInvoiceOcrRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(YidaoInvoiceOcrRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private FileEngine fileEngines;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String yidao_invoice_url = propertyEngine.readById("yidao_cloud_invoiceocr_url");
		
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		
		double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_yidao_face_photo_comBase"));//压缩基数
		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_yidao_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
		int photo_limit = Integer.valueOf(propertyEngine.readById("ds_yidaoocr_auth_limit"));
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
			String image = ParamUtil.findValue(ds.getParams_in(), "image").toString();
			
			String fpath1 = null;
			if (StringUtils.isNotEmpty(image)) {
				logger.info("{} 图片上传征信存储开始...", prefix);
				fpath1  = fileEngines.store("ds_yidao_photo",FileArea.DS, FileType.JPG, image,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix, fpath1);
				String file_full_path = fileEngines.getFullPathById(fpath1);
				ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
				String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,photo_limit);
				if(!StringUtil.isEmpty(comperss_rsp)){
					image = comperss_rsp;
				}
			}
			logger.info("{} 易道发票ocr请求开始...", prefix);
			String result = RequestHelper.doPost(yidao_invoice_url, 
					paramOptions(image), false,time_out);
			logger.info("{} 易道发票ocr请求完成:{}", prefix,result);
			JSONObject json = JSONObject.parseObject(result);
			logObj.setBiz_code1(json.getString("request_id"));
			String error_code = json.getString("error_code");
			if("0".equals(error_code)){
				resource_tag = Conts.TAG_TST_SUCCESS;
				JSONArray result_json = json.getJSONArray("result");
				JSONObject result_tmp = result_json.getJSONObject(0);
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, JsonFilter.getJsonKeys(result_tmp, "image,score,quad,error_msg,error_code,page_no"));
				rets.put(Conts.KEY_RET_MSG, "交易成功!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}else{
				logger.info("{} 易道发票ocr识别异常", prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_YIDAO_PICTURE_ERRO);
				rets.put(Conts.KEY_RET_MSG,
						"识别失败,返回原因:" + json.getString("description"));
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
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

	public Map<String, String> paramOptions(String image) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("app_key", propertyEngine.readById("yidao_ocr_app_key"));
		params.put("app_secret", propertyEngine.readById("yidao_ocr_app_secret"));
		params.put("image_base64", image);

		return params;
	}
}
