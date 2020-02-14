package com.wanda.credit.ds.client.zhongjin;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

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
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 新颜人脸识别
 * @author nan.liu
 * @version 1.0
 * @createdate 2019年8月26日
 * 
 */
@DataSourceClass(bindingDataSourceId = "ds_zhongjin_faceDirect")
public class ZJinFacePhotoDirectRequestor extends BaseZJinSourceRequestor implements	IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(ZJinFacePhotoDirectRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id;

		String zhongjin_account = propertyEngine.readById("ds_zhongjin_account");
		String zhongjin_pwd = propertyEngine.readById("ds_zhongjin_pwd");
		String request_url = propertyEngine.readById("ds_zhongjin_facePhoto_url");
		String face_prod = propertyEngine.readById("ds_zhongjin_face_prod");

		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		logger.info("{} 人脸识别交易开始...", prefix);
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setIncache("0");
		logObj.setTrade_id(trade_id);
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(request_url);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易成功");

		Map<String, Object> rets = new HashMap<String, Object>();

		Map<String, Object> reqparam = new HashMap<String, Object>();
		try {
			/** 姓名-必填 */
			String name = (String) ParamUtil.findValue(ds.getParams_in(),"name");
			/** 身份证号码-选填 */
			String cardNo = (String) ParamUtil.findValue(ds.getParams_in(),"cardNo");
			String query_image_content = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //活体照
			/** 请求参数记录到日志 */
			reqparam.put("cardNo", cardNo);
			reqparam.put("name", name);

			if (!StringUtil.isEmpty(cardNo) && StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不符合规范");
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logger.warn("{} 身份证号码不符合规范", prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "校验不通过:传入参数不正确,姓名格式错误");
				return rets;
			}
			/** 构建请求参数 */
			JSONObject meta = new JSONObject();
			meta.put("account", zhongjin_account);// 配置参数
			meta.put("password", zhongjin_pwd);// 配置参数
			meta.put("service_code", face_prod);// ds入参 必填
			
			JSONObject params = new JSONObject();
			params.put("name", name);// ds入参 必填
			params.put("request_sn", trade_id);
			params.put("id_no",cardNo);
			params.put("photo", query_image_content);

			JSONObject request = new JSONObject();
			request.put("meta", meta);
			request.put("params", params);
			logger.info("{} 开始请求远程服务器... ", prefix);
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			String postString = RequestHelper.sendPostRequest(request_url, JSONObject.toJSONString(request), "application/json;charset=utf-8");
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logger.info("{} 请求返回:{}", prefix,postString);
			if(StringUtils.isEmpty(postString)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源调用失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安数据源厂商返回异常! ",prefix);
				return rets;
			}
			JSONObject result_obj = JSONObject.parseObject(postString);
			JSONObject res_meta = result_obj.getJSONObject("meta");
			if("200".equals(res_meta.getString("result_code"))){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				JSONObject data = result_obj.getJSONObject("data");
				String res_code = data.getString("res_code");
				if("200".equals(res_code) || "400".equals(res_code)){
					Map<String, Object> respResult = new HashMap<String, Object>();
					respResult.put("server_idx", "06");
					resource_tag = buildOutParams(trade_id,data,respResult,rets);
				}else{
					logger.info("{} 交易失败:{}", prefix,result_obj.getString("errorMsg"));
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
					return rets;
				}				
			}else{
				logger.info("{} 交易失败:{}", prefix,result_obj.getString("errorMsg"));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				return rets;
			}           
		} catch (Exception e) {
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
		} finally {
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		return rets;
	}
	public String buildOutParams(String trade_id,JSONObject data,Map<String, Object> respResult,Map<String, Object> rets){
		String resource_tag = Conts.TAG_SYS_ERROR;
		String citizen_result = data.getString("citizen_result");
		String face_result = data.getString("face_result");
		String res_code = data.getString("res_code");
		String error_message = data.getString("error_message");
		if("400".equals(res_code) && error_message.contains("相片质量校验不合格")){
			resource_tag = Conts.TAG_UNMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
			rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNMATCH});
			logger.error("{} 照片质量不合格", trade_id);
			return resource_tag;
		}
		if("2001".equals(citizen_result)){
			resource_tag = Conts.TAG_UNMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
			rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{} 认证不一致",trade_id);
			return resource_tag;
		}
		if("2002".equals(citizen_result)){
			resource_tag = Conts.TAG_NOMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
			rets.put(Conts.KEY_RET_MSG, "库中无此号");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{}公安库中无此号",trade_id);
			return resource_tag;
		}
		if("2000".equals(citizen_result) && "2004".equals(face_result)){
			resource_tag = Conts.TAG_MATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
			rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("2003".equals(face_result)){
			resource_tag = Conts.TAG_UNMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
			rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNMATCH});
			logger.error("{} 外部返回识别失败", trade_id);
			return resource_tag;
		}
		if("2000".equals(citizen_result) && ("2000".equals(face_result) || "2001".equals(face_result) || "2002".equals(face_result))){
			resource_tag = Conts.TAG_FOUND;
			respResult.put("rtn", 0);				
			respResult.put("pair_verify_similarity", data.getString("photo_score"));
			if("2000".equals(data.getString("face_result"))){
				respResult.put("pair_verify_result", "0");
			}else{
				respResult.put("pair_verify_result", "1");
			}				
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, respResult);
			rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		resource_tag = Conts.TAG_UNFOUND;
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
		rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
		rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
		logger.error("{} 外部返回识别失败 end", trade_id);
		return resource_tag;
	}
}
