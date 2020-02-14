package com.wanda.credit.ds.client.xyan;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import com.wanda.credit.ds.client.xyan.utils.HttpUtil;
import com.wanda.credit.ds.client.xyan.utils.RsaCodingUtil;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 新颜人脸识别
 * @author nan.liu
 * @version 1.0
 * @createdate 2019年8月26日
 * 
 */
@DataSourceClass(bindingDataSourceId = "ds_xyan_face")
public class XYanFacePhotoSourceRequestor extends BaseXYanAuthenBankCardDataSourceRequestor implements	IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(XYanFacePhotoSourceRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private BaiduFaceCheckDetection baiduService;
	@Autowired
	private FileEngine fileEngines;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id;

		String member_id = propertyEngine.readById("ds_xyan_member_id");
		String terminal_id = propertyEngine.readById("ds_xyan_termid");
		String request_url = propertyEngine.readById("ds_xyan_facePhoto_url");
		String pfxpwd = propertyEngine.readById("ds_xyan_pfxpwd");
		String pfxname = propertyEngine.readById("ds_xyan_pfxname");
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
			JSONObject po = new JSONObject();
			po.put("member_id", member_id);// 配置参数
			po.put("terminal_id", terminal_id);// 配置参数
			po.put("id_holder", name);// ds入参 必填
			po.put("id_card", cardNo);// ds入参 必填
			po.put("trans_id", StringUtil.getRandomNo());
			po.put("trade_date",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			po.put("industry_type", "A1");
			// 20170515 houxiabin add 新颜银行卡认证 2.0.2 End
			String base64str = com.wanda.credit.ds.client.xyan.utils.SecurityUtil
					.Base64Encode(po.toString());
			/** rsa加密 **/
			String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str,
					cer_file_base_path + pfxname, pfxpwd);// 加密数据
			Map<String, String> Header = new HashMap<String, String>();
			Map<String, String> HeadPostParam = new HashMap<String, String>();
			HeadPostParam.put("member_id", member_id);
			HeadPostParam.put("terminal_id", terminal_id);
			HeadPostParam.put("data_type", "json");
			HeadPostParam.put("data_content", data_content);
			HeadPostParam.put("photo", query_image_content);
			
			logger.info("{} 开始请求远程服务器... ", prefix);
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			String postString = HttpUtil.RequestForm(request_url, HeadPostParam);
//			String postString = RequestHelper.doPost(request_url, HeadPostParam, Header, null,
//					ContentType.create("application/json", Consts.UTF_8),false);
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
			if(result_obj.getBoolean("success")){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				JSONObject data = result_obj.getJSONObject("data");
				logObj.setBiz_code1(data.getString("trade_no"));
				logObj.setBiz_code2(data.getString("trans_id"));
				Map<String, Object> respResult = new HashMap<String, Object>();
				respResult.put("server_idx", "06");
				resource_tag = buildOutParams(trade_id,data,respResult,rets);
			}else{
				logger.info("{} 新颜交易失败:{}", prefix,result_obj.getString("errorMsg"));
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
		String code = data.getString("code");
		if("0".equals(code) || "3".equals(code)){
			logger.info("{} 人脸识别成功", trade_id);
			resource_tag = Conts.TAG_FOUND;
			respResult.put("rtn", 0);
			if("0".equals(code)){
				respResult.put("pair_verify_result", "0");
			}else{
				respResult.put("pair_verify_result", "1");
			}			
			respResult.put("pair_verify_similarity",data.getString("score"));
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, respResult);
			rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}
		switch(code){
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
				logger.warn("{} 数据源厂商返回异常01!",trade_id);
				break;
			case "9":
				rets.clear();
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 请求异常",trade_id);
				break;
			case "6":
				rets.clear();
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败:"+data.getString("desc"));
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.warn("{} 认证异常",trade_id);
				break;
			case "5":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE01_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "用户身份核查成功，特征提取失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 图片质量太低",trade_id);
				break;
			default :
				rets.clear();
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败:" +data.getString("desc"));
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.error("{} 外部返回识别失败:{}", trade_id);
				break;
		}
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
