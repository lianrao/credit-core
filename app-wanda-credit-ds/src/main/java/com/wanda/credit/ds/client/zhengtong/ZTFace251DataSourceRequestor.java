package com.wanda.credit.ds.client.zhengtong;

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
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.policeAuthV2.PoliceFacePhotoV2Requestor;
import com.wanda.credit.ds.dao.domain.zhengtong.ZT_Face_Result;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 描述: 高清人像对比 版本: 1.0 创建日期: 2019-1-3 创建时间: 17:40:55 PM
 */
// public class ZTFace251DataSourceRequestor {
@DataSourceClass(bindingDataSourceId = "ds_zhengt_face251")
public class ZTFace251DataSourceRequestor extends BaseZTDataSourceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(ZTFace251DataSourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private PoliceFacePhotoV2Requestor polceFaceService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		// <value>cardNo,name,query_image_content</value>
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 政通高清人像比对服务请求开始...", prefix);
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		String zt_url = propertyEngine.readById("ds_zhengt_face_inner_url");// 政通调用连接
		boolean pcb102_send_to_policeV1 = "1".equals(propertyEngine.readById("pcb102_send_to_policeV1"));
		String resource_tag = Conts.TAG_SYS_ERROR;
		String cardNo = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[0])); // 身份证号码
		String name = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[1])); // 姓名
		String query_image_content = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[2])); // 照片数据包
		try {
			
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();
			logger.info("{} 政通高清人像比对服务加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			logObj.setReq_url(zt_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

			if(!isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			if (StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))) {
				logger.warn("{}入参格式不符合要求!", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				return rets;
			} else {
				logObj.setIncache("0");
				Map<String, String> params = new HashMap<String,String>();
				Map<String, String> headers = new HashMap<String,String>();
				params.put("trade_id", trade_id);
				params.put("name", name);
				params.put("cardNo", cardNo);
				params.put("query_image_content", query_image_content);
				logger.info("{} 政通高清人像比对服务请求开始...", prefix);
				 String res = RequestHelper.doPost(zt_url,null,headers,params,null,false, 10000);
				logger.info("{} 政通高清人像比对服务请求结束,返回信息:{}", prefix, res);

				if (StringUtil.isEmpty(res)) {
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "政通人脸识别失败");
					rets.put(Conts.KEY_RET_TAG, new String[] { Conts.TAG_UNFOUND });
					logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
					return rets;
				}
				JSONObject resJson = JSONObject.fromObject(res);
				JSONObject result = JSONObject.fromObject(resJson.getString("result"));
				ZT_Face_Result ztFRes = new ZT_Face_Result();
				if ("0".equals(result.getString("error_no"))) {
					JSONArray resAry = result.getJSONArray("results");
					String arr = com.alibaba.fastjson.JSONObject.toJSONString(JSONObject.fromObject(resAry.get(0)), true);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					ztFRes = com.alibaba.fastjson.JSONObject.parseObject(arr, ZT_Face_Result.class);
				}		
//				ztFRes.setCertseq(enCardNo);//加密证件号
//				ztFRes.setImage(file_id);
				ztFRes.setTrade_id(trade_id);
				ztFRes.setError_info(result.getString("error_info"));
				ztFRes.setError_no(result.getString("error_no"));
//				zTFace251Service.add(ztFRes);
				logObj.setBiz_code1(result.getString("error_no"));
				logObj.setBiz_code2(String.valueOf(ztFRes.getSysSeqNb()));
				logObj.setState_msg(result.getString("error_info"));
				resource_tag = buildOutPutParams(trade_id,ztFRes,rets,res);
			}
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		} finally {
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
	
	public String buildOutPutParams(String trade_id,ZT_Face_Result zt_face,Map<String,Object> rets,String res){
		Map<String,Object> retdata = new HashMap<String, Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		logger.info("{} 构建出参开始...", trade_id);
		retdata.put("server_idx", "06");
		if(res==null){
			logger.info("{} 返回业务数据为空", trade_id);
			resource_tag = Conts.TAG_UNFOUND;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if( !"0".equals(zt_face.getError_no())){							
			logger.info("{} 请求数据源失败:{}", trade_id,zt_face.getError_info());
			resource_tag = Conts.TAG_UNFOUND;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if(StringUtil.isEmpty(zt_face.getRespcd())){
			logger.info("{} 返回业务码为空", trade_id);
			resource_tag = Conts.TAG_UNFOUND;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("1000".equals(zt_face.getRespcd())){
			logger.info("{} 人脸识别成功", trade_id);
			resource_tag = Conts.TAG_FOUND;
			retdata.put("rtn", 0);
			if("1000".equals(zt_face.getRespcd())){
				retdata.put("pair_verify_result", "0");
			}			
			retdata.put("pair_verify_similarity", zt_face.getMpssim());
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("1001".equals(zt_face.getRespcd())){
			logger.info("{} 人脸识别成功", trade_id);
			if("认证不一致(不通过):不能确定是否为同一人".equals(zt_face.getRespinfo()) ||
					"认证不一致(不通过):系统判断为不同人".equals(zt_face.getRespinfo()) ){
				logger.info("{} 人脸识别成功:{}", trade_id,zt_face.getRespinfo());
				resource_tag = Conts.TAG_FOUND;
				retdata.put("rtn", 0);
				retdata.put("pair_verify_result", "1");		
				retdata.put("pair_verify_similarity", zt_face.getMpssim());
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}
			if("认证不一致(不通过):库中无照片".equals(zt_face.getRespinfo())){
				logger.info("{} 政通返回信息01:{}", trade_id,zt_face.getRespinfo());
				rets.clear();
				resource_tag = Conts.TAG_MATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}
			if("认证不一致(不通过):姓名证件号不匹配".equals(zt_face.getRespinfo())){
				logger.info("{} 政通返回信息02:{}", trade_id,zt_face.getRespinfo());
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}
			if("认证不一致(不通过):姓名证件号匹配，请检查图片".equals(zt_face.getRespinfo())){
				logger.info("{} 政通返回信息03:{}", trade_id,zt_face.getRespinfo());
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}
		}
		if("1002".equals(zt_face.getRespcd())){
			logger.info("{} 政通返回信息03:{}", trade_id,zt_face.getRespinfo());
			if("库中无此号".equals(zt_face.getRespinfo())){				
				rets.clear();
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "库中无此号");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}
			if("交易异常:上传相片质量校验不合格，请重新拍摄上传".equals(zt_face.getRespinfo())){
				rets.clear();
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}
		}
		rets.clear();
		resource_tag = Conts.TAG_UNFOUND;
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
		rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return resource_tag;
	}

}
