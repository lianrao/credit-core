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

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
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
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.aijin.beans.YuanJin_Face_New;
import com.wanda.credit.ds.client.baiduFace.BaiduFaceCheckDetection;
import com.wanda.credit.ds.client.policeAuthV2.PoliceFacePhotoV2Requestor;
import com.wanda.credit.ds.dao.domain.yuanjin.YJ_FaceScoreVO;
import com.wanda.credit.ds.dao.iface.yuanjin.IYuanJinFaceScoreService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_aijin_facePhoto")
public class AiJinFaceDataSourceRequestor extends BaseAijinDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(AiJinFaceDataSourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IYuanJinFaceScoreService yuanjinService;
	@Autowired
	private FileEngine fileEngines;
	@Autowired
	private BaiduFaceCheckDetection baiduService;
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
		boolean yuanjin_send_to_baidu = "1".equals(propertyEngine.readById("yuanjin_send_to_baidu"));//爰金调用连接
		String yuanjin_face_url = propertyEngine.readById("ds_yuanjin_face_url");//爰金调用连接
		String yuanjin_face_account = propertyEngine.readById("ds_yuanjin_face_account");//爰金调用连接
		String yuanjin_face_acode = propertyEngine.readById("ds_yuanjin_face_acode");//服务代码
		String yuanjin_face_key = propertyEngine.readById("ds_yuanjin_face_key");//服务代码
		double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_yuanjin_face_photo_comBase"));//压缩基数
		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_yuanjin_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
		
		boolean pcb102_send_to_policeV1 = "1".equals(propertyEngine.readById("pcb102_send_to_policeV1"));//爰金调用连接
		String enCardNo = "";
		int rotate = 0;
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
			logger.info("{}爱金人像比对图片上传开始...", prefix);
			String file_id  = fileEngines.store("ds_nciic_jx",FileArea.DS, FileType.JPG, query_image_content,trade_id);
			String file_full_path = fileEngines.getFullPathById(file_id);
			logger.info("{}爱金人像比对图片上传成功", prefix);
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
			if(yuanjin_send_to_baidu){
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
						}			
					}				
				}
			}
			ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
			String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,100*1024);
			if(!StringUtil.isEmpty(comperss_rsp)){
				query_image_content = comperss_rsp;
			}
			Integer imagetype = 0;
			String param = "name=" + name + "&idnumber=" + cardNo + "&image=" + query_image_content+ "&imagetype=" + imagetype;
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
			YuanJin_Face_New result = com.alibaba.fastjson.JSONObject.parseObject(json,YuanJin_Face_New.class);
			saveFaceResult(trade_id,name,enCardNo,file_id,result);
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
			ds.setId("ds_aijin_facePhoto");
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
	public void saveFaceResult(String trade_id,String name,String cardNo,String file_id,YuanJin_Face_New result){
		YJ_FaceScoreVO yu_face = new YJ_FaceScoreVO();
		yu_face.setTrade_id(trade_id);
		yu_face.setCardNo(cardNo);
		yu_face.setName(name);
		yu_face.setFile_image(file_id);
		yu_face.setResult(result.getResult());
		yu_face.setResponseCode(result.getResponseCode());
		yu_face.setFaceResultText(result.getResultText());
		yu_face.setResponseText(result.getResponseText());
		if(!StringUtil.isEmpty(result.getScore())){
			yu_face.setScore(result.getScore());
		}		
		yuanjinService.save(yu_face);
	}
	public String buildOutParams(String trade_id,YuanJin_Face_New result,
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
		if(!StringUtil.isEmpty(result.getResult())){
			if("7".equals(result.getResult())){
				rets.clear();
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败:{}", trade_id,result.getResultText());
				return resource_tag;
			}
			if("5".equals(result.getResult())){
				rets.clear();
				logger.info("{} 公安库中无照片", trade_id);
				resource_tag = Conts.TAG_MATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return resource_tag;
			}
			if("6".equals(result.getResult())){
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "库中无此号");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安库中无此号",trade_id);
				return resource_tag;
			}
			if("4".equals(result.getResult())){
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 认证不一致",trade_id);
				return resource_tag;
			}
		}
		if("100".equals(result.getResponseCode())  
				&& !StringUtil.isEmpty(result.getScore())){
			if("1".equals(result.getResult()) || "2".equals(result.getResult()) 
					|| "3".equals(result.getResult())){
				logger.info("{}爱金人像比对成功", trade_id);
				rets.clear();
				resource_tag = Conts.TAG_FOUND;
				respResult.put("rtn", 0);				
				respResult.put("pair_verify_similarity", result.getScore());
				if("1".equals(result.getResult())){
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
		logger.error("{} 外部返回识别失败:{}", trade_id);
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