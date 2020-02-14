package com.wanda.credit.ds.client.xiaoan;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
import com.wanda.credit.base.util.RandomUtils;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.xiaoan.bean.XiaoAnResultBean;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_xiaoan_face")
public class XiaoanFaceSourceRequestor extends BaseXiaoAnSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(XiaoanFaceSourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private FileEngine fileEngines;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 小安人脸比对数据源调用开始...", prefix);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String xa_url = propertyEngine.readById("ds_xiaoan_face_url");//远鉴调用连接
		String xiaoan_key = propertyEngine.readById("ds_xiaoan_key");//member_id
		float xiaoan_score = Float.valueOf(propertyEngine.readById("ds_xiaoan_face_score"));//通过阈值
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		String file_id = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String query_image_content = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();

			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>(); 		

			logger.info("{} 小安人脸比对数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(xa_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

			retdata.put("server_idx", "06");
			logger.info("{} 小安静态照片上传开始...", prefix);
			if(StringUtils.isEmpty(file_id)){
				file_id  = fileEngines.store("ds_xiaoan_photo",FileArea.DS, FileType.JPG, query_image_content,trade_id);
			}
			String file_full_path = fileEngines.getFullPathById(file_id);
			logger.info("{} 小安静态照片上传成功", prefix);
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			logObj.setIncache("0");
			String res = xiaoanHttpClient(trade_id,xa_url,xiaoan_key,file_full_path,name,cardNo,time_out);
			if(StringUtil.isEmpty(res)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
				return rets;
			}
			XiaoAnResultBean xa_face = com.alibaba.fastjson.JSONObject.parseObject(res,XiaoAnResultBean.class);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			logObj.setBiz_code1(xa_face.getCode());
			logObj.setBiz_code2(xa_face.getUuid());
			if("4000".equals(xa_face.getCode()) || "4030".equals(xa_face.getCode())){
				logger.info("{} 传入参数格式有误", trade_id);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			if("4031".equals(xa_face.getCode())){
				logger.info("{} 传入照片不符合要求", trade_id);
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			
			if("4100".equals(xa_face.getCode())){
				rets.clear();
				resource_tag = Conts.TAG_NOMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "库中无此号");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安库中无此号",trade_id);
				return rets;
			}	
			if("4101".equals(xa_face.getCode())){
				rets.clear();
				resource_tag = Conts.TAG_MATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}数据源厂商返回4101",trade_id);
				return rets;
			}
			resource_tag = buildOutParams(trade_id,xa_face,retdata,rets,xiaoan_score);
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
	
	public String buildOutParams(String trade_id,XiaoAnResultBean xa_face,
			Map<String,Object> retdata,Map<String,Object> rets,float xiaoan_score){
		String resource_tag = Conts.TAG_SYS_ERROR;
		logger.info("{} 构建出参开始...", trade_id);
		if(xa_face.getPayload()==null){
			logger.info("{} 返回业务数据为空", trade_id);
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源调用失败!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if(StringUtil.isEmpty(xa_face.getPayload().getCheckResult())){
			logger.info("{} 返回业务码为空", trade_id);
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源调用失败!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("1".equals(xa_face.getPayload().getCheckResult())){
			logger.info("{} 人脸识别成功", trade_id);
			float score = Float.valueOf(xa_face.getPayload().getScore())*100;
			resource_tag = Conts.TAG_FOUND;
			retdata.put("rtn", 0);
			retdata.put("pair_verify_similarity", (int)score);
			if(score>=xiaoan_score){
				retdata.put("pair_verify_result", "0");
			}else{
				retdata.put("pair_verify_result", "1");
				if(score>=45){
					retdata.put("pair_verify_similarity", Math.round(RandomUtils.getRandom(0f, 30f)));
				}
			}			
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("2".equals(xa_face.getPayload().getCheckResult())){
			rets.clear();
			resource_tag = Conts.TAG_UNMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
			rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{} 认证不一致",trade_id);
		}
		return resource_tag;
	}
}
