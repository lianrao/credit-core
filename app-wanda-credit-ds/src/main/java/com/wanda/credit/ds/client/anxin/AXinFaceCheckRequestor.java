/**   
* @Description: 安信身份认证(不带照片)数据源
* @author nan.liu
* @date 2018年2月22日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.anxin;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

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
import com.wanda.credit.base.util.IPUtils;
import com.wanda.credit.base.util.ImgCompress;
import com.wanda.credit.base.util.RotatePhoto;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.anxin.bean.CommonResult;
import com.wanda.credit.ds.client.baiduFace.BaiduFaceCheckDetection;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


@DataSourceClass(bindingDataSourceId="ds_anxin_face")
public class AXinFaceCheckRequestor extends BaseAXinDSRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(AXinFaceCheckRequestor.class);
	protected String CODE_EQUAL = "gajx_001";
	protected String CODE_NOEQUAL = "gajx_002";
	protected String CODE_NOEXIST = "gajx_003";

	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private BaiduFaceCheckDetection baiduService;
	@Autowired
	private FileEngine fileEngines;
	public Map<String, Object> request(String trade_id, DataSource ds) {		
		String anxin_url = propertyEngine.readById("ds_anxin_facecheck_url");
		String anxin_id = propertyEngine.readById("ds_anxin_police_app_id");
		String anxin_key = propertyEngine.readById("ds_anxin_police_app_key");

		int rotate = 0;
		String yuanjin_send_to_baidu = propertyEngine.readById("yuanjin_send_to_baidudsids");
		
		double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_guozt_face_photo_comBase"));//压缩基数
		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_guozt_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
		int photo_limit = Integer.valueOf(propertyEngine.readById("ds_police_auth_limit"));
		
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(anxin_url);
		logObj.setBiz_code3(IPUtils.getLocalIP());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易成功");
		
		try{
			logger.info("{} 开始解析传入的参数" , prefix);
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString().toUpperCase();
			String query_image_content = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //活体照
			logger.info("{} 解析传入的参数成功" , prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			//参数校验 - 身份证号码
			String validate = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(validate)) {
				logger.info("{} 身份证格式校验错误： {}" , prefix , validate);
				logObj.setState_msg("身份证格式校验错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
				return rets;
			}	
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){				
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
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
						rotate = getRotate((double)datas.get("rotation"));
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
			logObj.setIncache("0");
			ds.setId("ds_anxin_face");
			logger.info("{} 安信请求开始...",prefix);		
			CommonResult postResult = PhotoCheck(trade_id,anxin_url,name,cardNo,query_image_content,anxin_id,anxin_key);
			logger.info("{} 安信请求结束",prefix);	
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			Map<String, Object> respResult = new HashMap<String, Object>();
			respResult.put("server_idx", "06");
			resource_tag = buildOutParams(trade_id,postResult,respResult,rets);
		}catch(Exception e){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(e));
			if (ExceptionUtil.isTimeoutException(e)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + e.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		logger.info("{} 身份验证End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;	
	}
	public String buildOutParams(String trade_id,CommonResult result,
			Map<String, Object> respResult,Map<String, Object> rets){
		String resource_tag = Conts.TAG_SYS_ERROR;
		if("03".equals(result.getResult()) || "04".equals(result.getResult()) || "05".equals(result.getResult())){
			logger.info("{} 调用安信错误", trade_id);
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}else if("02".equals(result.getResult()) || "01".equals(result.getResult())){
			logger.info("{} 入参错误",trade_id);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
			rets.put(Conts.KEY_RET_MSG, "参数校验不通过");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		if("00".equals(result.getResult())){
			if("0000000".equals(result.getResult_detail())){
				logger.info("{} 安信人像比对成功", trade_id);
				rets.clear();
				resource_tag = Conts.TAG_FOUND;
				respResult.put("rtn", 0);				
				respResult.put("pair_verify_similarity", result.getSimilarity());
				if("1".equals(result.getAuth_result())){
					respResult.put("pair_verify_result", "0");
				}else{
					respResult.put("pair_verify_result", "1");
				}				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, respResult);
				rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}else if("0301002".equals(result.getResult_detail())){
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 认证不一致",trade_id);
				return resource_tag;
			}else if("0301001".equals(result.getResult_detail())){
				resource_tag = Conts.TAG_NOMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "库中无此号");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安库中无此号",trade_id);
				return resource_tag;
			}else if("0399023".equals(result.getResult_detail())){
				logger.info("{} 公安库中无照片", trade_id);
				resource_tag = Conts.TAG_MATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}else if("0399005".equals(result.getResult_detail())){
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败00", trade_id);
				return resource_tag;
			}else if("0399007".equals(result.getResult_detail())){
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE01_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "用户身份核查成功，特征提取失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 图片质量太低",trade_id);
				return resource_tag;
			}else{
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败", trade_id);
				return resource_tag;
			}
		}
		
		resource_tag = Conts.TAG_UNFOUND;
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
		rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
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
