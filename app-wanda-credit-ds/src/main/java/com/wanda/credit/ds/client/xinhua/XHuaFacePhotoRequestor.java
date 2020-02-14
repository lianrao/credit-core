package com.wanda.credit.ds.client.xinhua;

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
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImgCompress;
import com.wanda.credit.base.util.RotatePhoto;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.baiduFace.BaiduFaceCheckDetection;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 新华人脸识别
 * @author nan.liu
 * @version 1.0
 * @createdate 2019年11月28日
 * 
 */
@DataSourceClass(bindingDataSourceId = "ds_xinhua_face")
public class XHuaFacePhotoRequestor extends BaseXHuaSourceRequestor implements	IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(XHuaFacePhotoRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private BaiduFaceCheckDetection baiduService;
	@Autowired
	private FileEngine fileEngines;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id;

		String xinhua_key = propertyEngine.readById("ds_xinhua_accountkey");
		String xinhua_username = propertyEngine.readById("ds_xinhua_username");
		String request_url = propertyEngine.readById("ds_xinhua_facePhoto_url");
		String face_prod = propertyEngine.readById("ds_xinhua_face_prod");
		int rotate = 0;
		String yuanjin_send_to_baidu = propertyEngine.readById("yuanjin_send_to_baidudsids");
		
		double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_guozt_face_photo_comBase"));//压缩基数
		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_guozt_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
		int photo_limit = Integer.valueOf(propertyEngine.readById("ds_police_auth_limit"));
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
			if(!StringUtil.isEmpty(ds.getAcct_id())){
				if(yuanjin_send_to_baidu.contains(ds.getAcct_id())){
					String file_id  = fileEngines.store("ds_rotate_police",FileArea.DS, FileType.JPG, query_image_content,trade_id);
					String file_full_path = fileEngines.getFullPathById(file_id);
					logger.info("{} 图片保存file_id:{}", prefix,file_full_path);
					ds.setId("ds_baidu_faceCheck");
					Map<String, Object> ret_map = baiduService.request(trade_id, ds);
					logger.info("{} 调用百度人脸检测返回信息:{}", prefix,JSONObject.toJSONString(ret_map));
					if("STATUS_SUCCESS".equals(String.valueOf(ret_map.get("retstatus")))){
						logger.info("{} 调用百度人脸检测成功", prefix);
						Map<String, Object> datas = (Map<String, Object>)ret_map.get("retdata");
						rotate = RotatePhoto.getRotate((double)datas.get("rotation"));
						logger.info("{} 需要旋转角度:{}", prefix,rotate);
						if(rotate!=0){
							RotatePhoto rotates = new RotatePhoto();
							String image_rorate = rotates.rotatePhonePhoto(file_full_path,rotate);
							if(!StringUtil.isEmpty(image_rorate)){
								logger.info("{} 旋转后保存开始...", prefix);
								query_image_content = image_rorate;
								file_id  = fileEngines.store("ds_nciic_jx",FileArea.DS, FileType.JPG, 
										image_rorate,trade_id);
								file_full_path = fileEngines.getFullPathById(file_id);
								logger.info("{} 旋转后保存图片路径:{}", prefix,file_full_path);
								ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
								String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,photo_limit);
								if(!StringUtil.isEmpty(comperss_rsp)){
									query_image_content = comperss_rsp;
								}
							}			
						}				
					}
				}
			}
			Map<String, String> params = new TreeMap<String, String>();
			params.put("name", name);// ds入参 必填
			params.put("identityCard",cardNo);
			params.put("photo", query_image_content);
			
			logger.info("{} 开始请求远程服务器... ", prefix);
			ds.setId("ds_xinhua_face");
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			String res = Service(trade_id,xinhua_key,face_prod,request_url,xinhua_username,params);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logger.info("{} 请求返回:{}", prefix,res);
			if(StringUtils.isEmpty(res)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源调用失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安数据源厂商返回异常! ",prefix);
				return rets;
			}
			JSONObject result_obj = JSONObject.parseObject(res);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			logObj.setBiz_code1(result_obj.getString("serialno"));
			if("0".equals(result_obj.getString("code"))){				
				JSONObject data = result_obj.getJSONObject("data");
				Map<String, Object> respResult = new HashMap<String, Object>();
				respResult.put("server_idx", "06");
				resource_tag = buildOutParams(trade_id,data,respResult,rets);				
			}else if("1002".equals(result_obj.getString("code"))){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "校验不通过:同一身份证请求过于频繁!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}数据源厂商返回失败:{}",prefix,result_obj.getString("msg"));
			}else{
				logger.info("{} 交易失败:{}", prefix,result_obj.getString("msg"));
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
		String result = data.getString("result");
		String score = data.getString("verificationScore");
		String message = data.getString("message");
		if("7".equals(result)){
			resource_tag = Conts.TAG_UNFOUND;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
			rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
			logger.error("{} 照片质量不合格:{}", trade_id,message);
			return resource_tag;
		}
		if("11".equals(result)){
			resource_tag = Conts.TAG_UNMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
			rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNMATCH});
			logger.error("{} 照片质量不合格:{}", trade_id,message);
			return resource_tag;
		}
		if("5".equals(result)){
			resource_tag = Conts.TAG_UNMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
			rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{} 认证不一致",trade_id);
			return resource_tag;
		}
		if("6".equals(result)){
			resource_tag = Conts.TAG_NOMATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
			rets.put(Conts.KEY_RET_MSG, "库中无此号");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{}公安库中无此号",trade_id);
			return resource_tag;
		}
		if("10".equals(result)){
			resource_tag = Conts.TAG_MATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
			rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("1".equals(result) || "2".equals(result) || "3".equals(result)){
			resource_tag = Conts.TAG_FOUND;
			respResult.put("rtn", 0);				
			respResult.put("pair_verify_similarity", score);
			if("1".equals(result)){
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
