package com.wanda.credit.ds.client.guoztFace;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.guozt.GuoZT_Face_Result;
import com.wanda.credit.ds.dao.iface.guozt.IGuoZTFaceService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;
@DataSourceClass(bindingDataSourceId="ds_guozt_facePack")
public class GuoZTFacePackSourceRequestor extends BaseGuoZTSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(GuoZTFacePackSourceRequestor.class);

	@Autowired
	private IGuoZTFaceService guoztService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private FileEngine fileEngines;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 国政通活体检测请求开始...", prefix);
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String guozt_url = propertyEngine.readById("ds_guozt_face_pack_url");//国政通调用连接
		String guozt_id = propertyEngine.readById("ds_guozt_face_pack_id");//国政通账号
		String guozt_key = propertyEngine.readById("ds_guozt_face_pack_key");//国政通秘钥
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); // 身份证号码
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 姓名
			String queryImagePackage = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 照片数据包
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	

			enCardNo = GladDESUtils.encrypt(cardNo);
			logger.info("{} 国政通活体检测加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(guozt_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			
			Map<String, Object> respResult = new HashMap<String, Object>();
			respResult.put("server_idx", "05");
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				logObj.setIncache("0");
	            String strTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
	            // 入参设定
	            JSONObject jo = new JSONObject();
	            jo.put("accessId", guozt_id);
	            String strAccessKey = MD5.ecodeByMD5(guozt_id + guozt_key + strTimestamp);
	            jo.put("accessKey", strAccessKey);
	            jo.put("userId", cardNo);
	            jo.put("userName", java.net.URLEncoder.encode(name, "UTF-8"));
	            jo.put("dataPackage", queryImagePackage);
	            jo.put("timeStamp", strTimestamp);
	            jo.put("sign", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
	            logger.info("{} 国政通活体检测请求开始...", prefix);
	            String res = RequestHelper.doPost(guozt_url, null,new HashMap<String, String>(), jo,null,false);
//	            String res = "{\"result\":\"0\",\"message\":\"%E6%AF%94%E5%AF%B9%E6%9C%8D%E5%8A%A1%E5%A4%84%E7%90%86%E6%88%90%E5%8A%9F\",\"transaction_id\":\"b6fc60e87d07883c8b98700ae3e47f28\",\"user_check_result\":\"3\",\"verify_result\":\"1\",\"verify_similarity\":\"19.37030702829361\",\"package_image\":\"1111\"}";
	            logger.info("{} 国政通活体检测请求结束.", prefix);
	            if(StringUtil.isEmpty(res)){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
					logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
					return rets;
				}
	            logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
	            GuoZT_Face_Result result = com.alibaba.fastjson.JSONObject.parseObject(res, GuoZT_Face_Result.class);
	            String image_file = result.getPackage_image();
	            logger.info("{} 国政通上传照片开始...", prefix);
	            String file_id  = fileEngines.store("ds_guozt_face",FileArea.DS, FileType.JPG, image_file,trade_id);
	            logger.info("{} 国政通上传照片结束", prefix);
	            result.setMessage(java.net.URLDecoder.decode(result.getMessage(), "UTF-8"));
	            result.setPackage_image(file_id);
	            result.setCardNo(enCardNo);
	            result.setName(name);
	            result.setTrade_id(trade_id);
	            guoztService.add(result);
	            //是否返回解密照片
	            respResult.put("photo_content",image_file);	
				if("7".equals(result.getResult())){
	            	resource_tag = Conts.TAG_FOUND;
	            	rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU__RISK_USER);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败:"+result.getMessage());
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.error("{} 外部返回识别失败,服务异常:{}", trade_id,res);
					return rets;
	            }
				resource_tag = buildOutPutParams(trade_id,respResult,result,rets);
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
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
}
