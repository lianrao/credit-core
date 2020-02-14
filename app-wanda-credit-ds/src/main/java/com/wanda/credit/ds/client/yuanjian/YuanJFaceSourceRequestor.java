package com.wanda.credit.ds.client.yuanjian;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import com.wanda.credit.ds.client.policeAuthV2.PoliceFacePhotoV2Requestor;
import com.wanda.credit.ds.client.yituNew.BaseYiTuNewSourceRequestor;
import com.wanda.credit.ds.client.yuanjian.beans.YuanJian_Face;
import com.wanda.credit.ds.dao.domain.yuanjian.YJ_FaceScore_Result;
import com.wanda.credit.ds.dao.iface.yuanjian.IYuanJianFaceScoreService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_yuanjian_face")
public class YuanJFaceSourceRequestor extends BaseYuanJSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(YuanJFaceSourceRequestor.class);
	@Autowired
	private IYuanJianFaceScoreService yuanjianService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private PoliceFacePhotoV2Requestor polceFaceService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 远鉴人脸比对数据源调用开始...", prefix);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String yuanjian_url = propertyEngine.readById("ds_yuanjian_url");//远鉴调用连接
		boolean yuanjian_is_save = "1".equals(propertyEngine.readById("ds_yuanjian_save_flag"));//远鉴调用连接
		String member_id = propertyEngine.readById("ds_yuanjian_member_id");//member_id
		String terminal_id = propertyEngine.readById("ds_yuanjian_terminal_id");//terminal_id
		
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		boolean pcb102_send_to_policeV1 = "1".equals(propertyEngine.readById("pcb102_send_to_policeV1"));
		String enCardNo = "";
		String file_id = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
		String query_image_content = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
		try{	
			
//			if(ParamUtil.findValue(ds.getParams_in(), "file_id")!=null){
//				file_id = ParamUtil.findValue(ds.getParams_in(), "file_id").toString();
//			}
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			enCardNo = GladDESUtils.encrypt(cardNo);
			logger.info("{} 远鉴人脸比对数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(yuanjian_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

			retdata.put("server_idx", "06");
//			logger.info("{} 远鉴静态照片上传开始...", prefix);
//			if(StringUtils.isEmpty(file_id)){
//				file_id  = fileEngines.store("ds_yuanjian_photo",FileArea.DS, FileType.JPG, query_image_content,trade_id);
//			}			
//			String file_full_path = fileEngines.getFullPathById(file_id);
//			logger.info("{} 远鉴静态照片上传成功", prefix);
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
//			ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
//			String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,photo_limit);
//			if(!StringUtil.isEmpty(comperss_rsp)){
//				query_image_content = comperss_rsp;
//			}
			logObj.setIncache("0");
			String res = doYuanJian(yuanjian_url,member_id,terminal_id,trade_id,
					cardNo,name,query_image_content,time_out);
			if(StringUtil.isEmpty(res)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
				return rets;
			}
			YuanJian_Face yj_face = com.alibaba.fastjson.JSONObject.parseObject(res,YuanJian_Face.class);
			
			saveFaceResult(trade_id,name,enCardNo,file_id,yj_face,yuanjian_is_save,logObj);
			if(!StringUtil.isEmpty(yj_face.getErrorCode())){
				if("S1000".equals(yj_face.getErrorCode())){
					logger.info("{} 传入参数格式有误:{}", trade_id,yj_face.getErrorMsg());
					resource_tag = Conts.TAG_SYS_ERROR;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
					rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
				if("010007".equals(yj_face.getErrorCode())){
					logger.info("{} 传入照片不符合要求:{}", trade_id,yj_face.getErrorMsg());
					rets.clear();
					resource_tag = Conts.TAG_UNMATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
				if("010008".equals(yj_face.getErrorCode())){
					logger.info("{} 传入照片不符合要求:{}", trade_id,yj_face.getErrorMsg());
					rets.clear();
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE05_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "照片过小(照片大小不能小于5k)");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 远鉴调用失败,返回错误信息为:{}", trade_id,yj_face.getErrorMsg());
				return rets;
			}
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);			
			resource_tag = buildOutParams(trade_id,yj_face,retdata,rets);
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
//			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
//			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
			if(pcb102_send_to_policeV1){
				if("STATUS_WARN_DS_POLICE_NOTEXISTS".equals(String.valueOf(rets.get("retstatus")))){
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
	public void saveFaceResult(String trade_id,String name,String enCardNo,String file_id,YuanJian_Face yj_face
			,boolean is_save_flag,DataSourceLogVO logs){
		logger.error("{} 数据库插入请求开始...", trade_id);
		YJ_FaceScore_Result yuanj_vo = new YJ_FaceScore_Result();
		yuanj_vo.setTrade_id(trade_id);
		yuanj_vo.setCardNo(enCardNo);
		yuanj_vo.setName(name);
		yuanj_vo.setPhoto_id(file_id);
		yuanj_vo.setSuccess(yj_face.getSuccess());
		if(!StringUtil.isEmpty(yj_face.getErrorCode())){
			yuanj_vo.setError_code(yj_face.getErrorCode());
			yuanj_vo.setError_msg(yj_face.getErrorMsg());
		}else{
			logs.setBiz_code1(String.valueOf(yj_face.getData().getTrade_no()));
			yuanj_vo.setCode(yj_face.getData().getCode());
			yuanj_vo.setDescription(yj_face.getData().getDesc());
			yuanj_vo.setFee(yj_face.getData().getFee());
			yuanj_vo.setLevel1(yj_face.getData().getLevel1());
			yuanj_vo.setLevel2(yj_face.getData().getLevel2());
			yuanj_vo.setScore(yj_face.getData().getScore());
			yuanj_vo.setTrade_no(yj_face.getData().getTrade_no());
			yuanj_vo.setTrans_id(yj_face.getData().getTrans_id());
		}
		if(is_save_flag){
			logger.info("{} 数据库插入开始...", trade_id);
			BaseYiTuNewSourceRequestor.threadYiTuPool.execute(new YuanJBeanStoreRunnable(trade_id,enCardNo,name,yuanj_vo){
				@Override
				public void run() {
					try {
						logger.info("{} 异步数据库插入开始:{}", getTrade_id(),getYuanjian().toString());
						yuanjianService.save(getYuanjian());
						logger.info("{} 异步数据库插入完成", getTrade_id());
					} catch (Exception e) {
						logger.info("{} 异步数据库插入异常:{}", getTrade_id(),e.getMessage());
					}
				}
			});			
			logger.info("{} 数据库插入完毕", trade_id);
		}		
	}
	public String buildOutParams(String trade_id,YuanJian_Face yj_face,Map<String,Object> retdata,Map<String,Object> rets){
		String resource_tag = Conts.TAG_SYS_ERROR;
		logger.info("{} 构建出参开始...", trade_id);
		if(yj_face.getData()==null){
			logger.info("{} 返回业务数据为空", trade_id);
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源调用失败!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if(StringUtil.isEmpty(yj_face.getData().getCode())){
			logger.info("{} 返回业务码为空", trade_id);
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源调用失败!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("0".equals(yj_face.getData().getCode()) || "3".equals(yj_face.getData().getCode())){
			logger.info("{} 人脸识别成功", trade_id);
			resource_tag = Conts.TAG_FOUND;
			retdata.put("rtn", 0);
			if("0".equals(yj_face.getData().getCode())){
				retdata.put("pair_verify_result", "0");
			}else{
				retdata.put("pair_verify_result", "1");
			}			
			retdata.put("pair_verify_similarity", yj_face.getData().getScore());
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		switch(yj_face.getData().getCode()){
			case "2":
				rets.clear();
				resource_tag = Conts.TAG_NOMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "库中无此号");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安库中无此号",trade_id);
				break;
			case "1":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 认证不一致",trade_id);
				break;
			case "4":
				rets.clear();
				resource_tag = Conts.TAG_MATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}国政通数据源厂商返回异常01!",trade_id);
				break;
			case "9":
				rets.clear();
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安库中无此号",trade_id);
				break;
			case "500200":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE01_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "用户身份核查成功，特征提取失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 图片质量太低",trade_id);
				break;
			case "500212":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 图片质量太低",trade_id);
				break;
			case "500210":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE03_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "未检测到人脸");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 未检测到人脸",trade_id);
				break;
			case "500215":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE07_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "出现多张人脸");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 出现多张人脸",trade_id);
				break;
			case "500216":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE01_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "用户身份核查成功，特征提取失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 人脸特征提取失败",trade_id);
				break;
			case "500312":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 图片质量太低",trade_id);
				break;
			case "500310":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE03_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "未检测到人脸");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 未检测到人脸",trade_id);
				break;
			case "500315":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE07_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "出现多张人脸");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 出现多张人脸",trade_id);
				break;
			case "500316":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE01_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "用户身份核查成功，特征提取失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 人脸特征提取失败",trade_id);
				break;
			case "500410":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE03_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "未检测到人脸");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 未检测到人脸",trade_id);
				break;
			case "500412":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 图片质量太低",trade_id);
				break;
			case "500415":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE07_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "出现多张人脸");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 出现多张人脸",trade_id);
				break;
			case "500416":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE01_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "用户身份核查成功，特征提取失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 人脸特征提取失败",trade_id);
				break;
			default :
				rets.clear();
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + yj_face.getData().getDesc().replace("亲，", ""));
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.error("{} 外部返回识别失败:{}", trade_id,yj_face.getData().getDesc());
				break;
		}
		return resource_tag;
	}
}
