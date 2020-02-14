package com.wanda.credit.ds.client.aijin;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
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
import com.wanda.credit.ds.client.aijin.beans.YuanJin_Face;
import com.wanda.credit.ds.client.policeAuthV2.PoliceFacePhotoV2Requestor;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.yuanjin.YJ_FaceScoreVO;
import com.wanda.credit.ds.dao.iface.yuanjin.IYuanJinFaceScoreService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_aijin_faceDirect")
public class AiJinFaceDirectRequestor extends BaseAijinDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(AiJinFaceDirectRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IYuanJinFaceScoreService yuanjinService;
	@Autowired
	private PoliceFacePhotoV2Requestor polceFaceService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}爱金人像比对数据源请求开始...", prefix);
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间

		String yuanjin_face_url = propertyEngine.readById("ds_yuanjin_face_url");//爰金调用连接
		String yuanjin_face_account = propertyEngine.readById("ds_yuanjin_facedirect_account");//爰金账号
		String yuanjin_face_acode = propertyEngine.readById("ds_yuanjin_facedirect_acode");//服务代码
		String yuanjin_face_key = propertyEngine.readById("ds_yuanjin_facedirect_key");//服务密码
		
		boolean pcb102_send_to_policeV1 = "1".equals(propertyEngine.readById("pcb102_send_to_policeV1"));//爰金调用连接
		String enCardNo = "";

		String resource_tag = Conts.TAG_SYS_ERROR;
		String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); //身份证号码
		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();   //姓名 			
		String query_image_content = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //活体照
		try{	
			
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			enCardNo = GladDESUtils.encrypt(cardNo);
			logger.info("{}爱金人像比对数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(yuanjin_face_url);
			logObj.setIncache("0");
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
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
			
			String param = "name=" + name + "&idnumber=" + cardNo + "&photo=" + query_image_content;
	        String sign = md5(yuanjin_face_acode + param + yuanjin_face_account + md5(yuanjin_face_key));//生成签名 (MD5大写)
	       
	        String post_data = "acode=" + yuanjin_face_acode + "&param=" + URLEncoder.encode(param, "UTF-8") + "&account=" 
					+ yuanjin_face_account + "&sign=" + sign;
			String json = postHtml(yuanjin_face_url, post_data);
			if(StringUtil.isEmpty(json)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
				return rets;
			}
			logger.info("{}爱金人像比对请求结束,返回结果:{}", prefix,json);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			Map<String, Object> respResult = new HashMap<String, Object>();
			respResult.put("server_idx", "06");
			YuanJin_Face result = com.alibaba.fastjson.JSONObject.parseObject(json,YuanJin_Face.class);
			saveFaceResult(trade_id,name,enCardNo,"",result);
			resource_tag = buildOutParams(trade_id,result,respResult,rets);
		}catch(Exception ex){
			ex.printStackTrace();
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
			ds.setId("ds_aijin_faceDirect");
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
			if(pcb102_send_to_policeV1){
				if("STATUS_WARN_DS_POLICE_NOTEXISTS".equals(String.valueOf(rets.get("retstatus")))){
					logger.info("{} 库无时走公安一所..." ,prefix);
					DataSource ds_live = new DataSource();
					List<Param> params_in = new ArrayList<Param>();
					Param pname = new Param();
					pname.setId("name");
					pname.setValue(name);
					
					Param pcardNo = new Param();
					pcardNo.setId("cardNo");
					pcardNo.setValue(cardNo);
					
					Param pquery_image_content = new Param();
					pquery_image_content.setId("query_image_content");
					pquery_image_content.setValue(query_image_content);
					
					Param switchs = new Param();
					switchs.setId("face_switch");
					switchs.setValue("1");
					
					params_in.add(pname);
					params_in.add(pcardNo);
					params_in.add(pquery_image_content);
					params_in.add(switchs);
					ds_live.setParams_in(params_in);
					ds_live.setId("ds_policeAuth_photov2");
					rets = polceFaceService.request(trade_id, ds_live);
				}else if("STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS".equals(String.valueOf(rets.get("retstatus")))){
					logger.info("{} 无照片时走公安一所..." ,prefix);
					DataSource ds_live = new DataSource();
					List<Param> params_in = new ArrayList<Param>();
					Param pname = new Param();
					pname.setId("name");
					pname.setValue(name);
					
					Param pcardNo = new Param();
					pcardNo.setId("cardNo");
					pcardNo.setValue(cardNo);
					
					Param pquery_image_content = new Param();
					pquery_image_content.setId("query_image_content");
					pquery_image_content.setValue(query_image_content);
					
					Param switchs = new Param();
					switchs.setId("face_switch");
					switchs.setValue("2");
					
					params_in.add(pname);
					params_in.add(pcardNo);
					params_in.add(pquery_image_content);
					params_in.add(switchs);
					ds_live.setParams_in(params_in);
					ds_live.setId("ds_policeAuth_photov2");
					rets = polceFaceService.request(trade_id, ds_live);
				}
			}
			
		}
		return rets;
	}
	public void saveFaceResult(String trade_id,String name,String cardNo,String file_id,YuanJin_Face result){
		YJ_FaceScoreVO yu_face = new YJ_FaceScoreVO();
		yu_face.setTrade_id(trade_id);
		yu_face.setCardNo(cardNo);
		yu_face.setName(name);
		yu_face.setFile_image(file_id);
		yu_face.setResult(result.getResult());
		yu_face.setResponseCode(result.getResponseCode());
		yu_face.setFaceResultText(result.getResultText());
		yu_face.setResponseText(result.getResponseText());
		if(result.getFaceCheckResult()!=null && "1".equals(result.getResult())){
			if(!StringUtil.isEmpty(result.getFaceCheckResult().getScore())){
				yu_face.setScore(result.getFaceCheckResult().getScore());
			}
		}
				
		yuanjinService.save(yu_face);
	}
	public String buildOutParams(String trade_id,YuanJin_Face result,
			Map<String, Object> respResult,Map<String, Object> rets){
		String resource_tag = Conts.TAG_SYS_ERROR;
		if("999".equals(result.getResponseCode()) || "200".equals(result.getResponseCode())
				|| "210".equals(result.getResponseCode()) || "220".equals(result.getResponseCode())
				|| "230".equals(result.getResponseCode()) || "240".equals(result.getResponseCode())
				|| "260".equals(result.getResponseCode()) || "270".equals(result.getResponseCode())
				|| "280".equals(result.getResponseCode()) || "430".equals(result.getResponseCode())){
			logger.info("{} 调用爰金错误:{}", trade_id,result.getResponseText());
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("440".equals(result.getResponseCode())){
			rets.clear();
			resource_tag = Conts.TAG_UNFOUND;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
			rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
			logger.error("{} 外部返回识别失败:{}", trade_id,result.getResponseText());
			return resource_tag;
		}
		if("420".equals(result.getResponseCode())){
			rets.clear();
			resource_tag = Conts.TAG_UNFOUND;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
			rets.put(Conts.KEY_RET_MSG, "传入参数有误");
			rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
			logger.error("{} 外部返回识别失败参数有误:{}", trade_id,result.getResponseText());
			return resource_tag;
		}
		if(!StringUtil.isEmpty(result.getResult())){
			if("2".equals(result.getResult())){
				rets.clear();
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败:{}", trade_id,result.getResultText());
				return resource_tag;
			}
		}
		if("100".equals(result.getResponseCode())){
			if("2".equals(result.getFaceCheckResult().getCitizenResult())){
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 认证不一致",trade_id);
				return resource_tag;
			}
			if("3".equals(result.getFaceCheckResult().getCitizenResult())){
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "库中无此号");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安库中无此号",trade_id);
				return resource_tag;
			}
			if("5".equals(result.getFaceCheckResult().getFaceResult())){
				logger.info("{} 公安库中无照片", trade_id);
				resource_tag = Conts.TAG_MATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}
			if("1".equals(result.getFaceCheckResult().getFaceResult()) || "2".equals(result.getFaceCheckResult().getFaceResult()) 
					|| "3".equals(result.getFaceCheckResult().getFaceResult())){
				logger.info("{}爱金人像比对成功", trade_id);
				rets.clear();
				resource_tag = Conts.TAG_FOUND;
				respResult.put("rtn", 0);				
				respResult.put("pair_verify_similarity", result.getFaceCheckResult().getScore());
				if("1".equals(result.getFaceCheckResult().getFaceResult())){
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
		}
		rets.clear();
		resource_tag = Conts.TAG_UNFOUND;
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
		rets.put(Conts.KEY_RET_MSG, "人脸识别失败:"+result.getResponseText());
		rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
		logger.error("{} 外部返回识别失败", trade_id);
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