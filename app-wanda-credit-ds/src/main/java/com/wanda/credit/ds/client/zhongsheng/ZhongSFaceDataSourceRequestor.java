package com.wanda.credit.ds.client.zhongsheng;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import cn.net.wnd.commons.JimAES;
@DataSourceClass(bindingDataSourceId="ds_zhongsheng_face")
public class ZhongSFaceDataSourceRequestor extends BaseZhongsRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZhongSFaceDataSourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}中胜人像比对数据源请求开始...", prefix);
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String zhongs_face_url = propertyEngine.readById("ds_zhongsheng_face_url");//爰金调用连接
		String zhongs_face_account = propertyEngine.readById("ds_zhongsheng_face_account");//中胜账号
		String zhongs_face_pwd = propertyEngine.readById("ds_zhongsheng_face_pwd");//中胜秘钥
		int zhongs_face_score = Integer.valueOf(propertyEngine.readById("ds_zhongsheng_face_score"));//压缩基数
//		double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_yuanjin_face_photo_comBase"));//压缩基数
//		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_yuanjin_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); //身份证号码
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();   //姓名 			
			String query_image_content = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //活体照
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			logger.info("{} 中胜人像比对数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(zhongs_face_url);
			logObj.setIncache("0");
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
//			logger.info("{}中胜人像比对图片上传开始...", prefix);
//			String file_id  = fileEngines.store("ds_nciic_jx",FileArea.DS, FileType.JPG, query_image_content,trade_id);
//			String file_full_path = fileEngines.getFullPathById(file_id);
//			logger.info("{}中胜人像比对图片上传成功", prefix);
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
//			ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
//			String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,100*1024);
//			if(!StringUtil.isEmpty(comperss_rsp)){
//				query_image_content = comperss_rsp;
//			}
			String encrypt = "1";
			String actionName = "livingcp";
			String api = "s";
			
			String params = "";
			
			com.alibaba.fastjson.JSONObject queryInfo  = new JSONObject();
			queryInfo.put("name", name);
			queryInfo.put("idCard", cardNo);
			queryInfo.put("imgB64AType", "1");
			queryInfo.put("imgB64A", query_image_content);
			
			com.alibaba.fastjson.JSONObject baseInfo = new JSONObject();
			baseInfo.put("account", zhongs_face_account);
			baseInfo.put("order", qryBatchNo(zhongs_face_account));
			baseInfo.put("sign", MD5encrypt16(zhongs_face_account + zhongs_face_pwd + queryInfo + baseInfo.getString("order")));
			
			com.alibaba.fastjson.JSONObject jObject = new JSONObject();
			jObject.put("baseInfo", baseInfo);
			jObject.put("queryInfo", queryInfo);
			
			try {
				params = "actionName=" + actionName + "&encrypt=" + encrypt + "&api=" + api + "&params="
						+ JimAES.getInstance().encodeDataAes(URLEncoder.encode(jObject.toJSONString(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String res = post(zhongs_face_url, params);
			if(StringUtil.isEmpty(res)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
				return rets;
			}
			JSONObject json = JSON.parseObject(res);
			if(StringUtil.isEmpty(json.getString("resultInfo"))){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
				return rets;
			}
			logger.info("{}中胜人像比对请求结束,返回结果:{}", prefix,json.getString("resultInfo"));
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			Map<String, Object> respResult = new HashMap<String, Object>();
			respResult.put("server_idx", "06");
			JSONObject result = JSON.parseObject(json.getString("resultInfo"));
			resource_tag = buildOutParams(trade_id,result,respResult,rets,zhongs_face_score,logObj);
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
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
	public String buildOutParams(String trade_id,JSONObject result,
			Map<String, Object> respResult,Map<String, Object> rets,int score,
			DataSourceLogVO log){
		String resource_tag = Conts.TAG_SYS_ERROR;
		String statcode = result.getString("statcode");
		String sessionId = result.getString("sessionId");
		log.setBiz_code1(statcode);
		log.setBiz_code2(sessionId);
		if("2017".equals(statcode) || "2019".equals(statcode) || "998".equals(statcode)
				|| "2022".equals(statcode) || "9100".equals(statcode)
				|| "9110".equals(statcode) || "9200".equals(statcode)
				|| "9202".equals(statcode) || "9304".equals(statcode)){
			logger.info("{} 调用中胜错误:{}", trade_id,statcode);
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("1903".equals(statcode) 
				|| "1904".equals(statcode)|| "1906".equals(statcode)
				|| "1910".equals(statcode)|| "2018".equals(statcode)){
			logger.info("{} 传入参数格式有误:{}", trade_id,statcode);
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
			rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("1706".equals(statcode) || "1905".equals(statcode)){
			resource_tag = Conts.TAG_UNFOUND;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
			rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
			logger.error("{} 外部返回识别失败", trade_id);
			return resource_tag;
		}
		if("1700".equals(statcode)){
			logger.error("{} 外部返回识别成功", trade_id);
			log.setBiz_code3(result.getString("state"));
			double state = Double.valueOf(result.getString("state"));
			int similarity = (int)state;
			rets.clear();
			resource_tag = Conts.TAG_FOUND;
			respResult.put("rtn", 0);				
			respResult.put("pair_verify_similarity", (int)state);
			if(similarity>=score){
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
		if("1701".equals(statcode)){
			rets.clear();
			resource_tag = Conts.TAG_UNMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
			rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{} 认证不一致",trade_id);
			return resource_tag;
		}
		if("1703".equals(statcode)){
			rets.clear();
			logger.info("{} 公安库中无照片", trade_id);
			resource_tag = Conts.TAG_MATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
			rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("1704".equals(statcode)){
			rets.clear();
			resource_tag = Conts.TAG_UNMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
			rets.put(Conts.KEY_RET_MSG, "库中无此号");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{}公安库中无此号",trade_id);
			return resource_tag;
		}
		rets.clear();
		resource_tag = Conts.TAG_UNFOUND;
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
		rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
		rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
		logger.error("{} 外部返回识别失败:{}", trade_id,statcode);
		return resource_tag;
	}
	public int getRotate(double rotation){
		if(rotation>=0){
			if(rotation-90>=-10 && (rotation-90<=10)){
				return -90;
			}
			if(rotation-180>=-10 && (rotation-180<=10)){
				return -180;
			}
		}else{
			if(rotation+90>=-10 && (rotation+90<=10)){
				return 90;
			}
			if(rotation+180>=-10 && (rotation+180<=10)){
				return 180;
			}
		}
		return 0;
	}
}