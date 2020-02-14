package com.wanda.credit.ds.client.jixin;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_jixin_face")
public class JiXinFaceDataSourceRequestor extends BaseDataSourceRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(JiXinFaceDataSourceRequestor.class);

	@Autowired
	protected PropertyEngine propertyEngine;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 吉信人脸识别调用开始...", prefix);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
        String url = propertyEngine.readById("ds_jx_face_url");
        String jixin_face_key = propertyEngine.readById("ds_jx_face_key");
        String jixin_face_merchno = propertyEngine.readById("ds_jx_face_merchno");
        int readTimeout = Integer.parseInt(propertyEngine.readById("req_read_timeout"));
        int connTimeout = Integer.parseInt(propertyEngine.readById("req_conn_timeout"));
//        double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_yuanjan_face_photo_comBase"));//压缩基数
//		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_yuanjan_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
//		int photo_limit = Integer.valueOf(propertyEngine.readById("ds_yuanjian_auth_limit"));
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
        logObj.setIncache("0");
		try{			
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String query_image_content = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); 
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			
			retdata.put("server_idx", "06");
//			logger.info("{} 吉信照片上传开始...", prefix);
//			String file_id  = fileEngines.store("ds_yuanjian_photo",FileArea.DS, FileType.JPG, query_image_content,trade_id);
//			String file_full_path = fileEngines.getFullPathById(file_id);
//			logger.info("{} 吉信照片上传成功", prefix);
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
//			ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
//			String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,photo_limit);
//			if(!StringUtil.isEmpty(comperss_rsp)){
//				query_image_content = comperss_rsp;
//			}
			logObj.setIncache("0");
			String request_sn = UUID.randomUUID().toString().replaceAll("-", "");
			CommonBean trans = new CommonBean();
	        trans.setTranscode("121");
	        trans.setVersion("0100");
	        trans.setOrdersn(request_sn);
	        trans.setMerchno(jixin_face_merchno);
	        trans.setDsorderid(trade_id);
	        trans.setUsername(name);
	        trans.setIdcard(cardNo);
	        trans.setIdtype("01");
	        trans.setImageData(query_image_content);
            logger.info("{} 开始请求远程服务器... ", prefix);
            //加签生成请求参数
            byte[] requert = TransUtil.packet(trans, jixin_face_key);
            String res = CommonUtil.post(url, requert,readTimeout,connTimeout);
			logger.info("{} 厂商返回响应信息: {}",prefix,res);
			if(StringUtil.isEmpty(res)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
				logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
				return rets;
			}			
			FaceJiXinBean jixin_face = com.alibaba.fastjson.JSONObject.parseObject(res,FaceJiXinBean.class);
            logger.info("{} 厂商返回响应码等信息 {} {}",prefix,jixin_face.getPlatformCode(),jixin_face.getPlatformDesc());
            request_sn = jixin_face.getOrdersn();
            logObj.setBiz_code1(jixin_face.getReturncode() + "-" + jixin_face.getErrtext());
            logObj.setBiz_code2(jixin_face.getOrderid());
            logObj.setBiz_code3(request_sn);
            if("001050060".equals(jixin_face.getPlatformCode())){
				logger.info("{} 传入参数格式有误", trade_id);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
            if("001030050".equals(jixin_face.getPlatformCode()) || "001050040".equals(jixin_face.getPlatformCode())
            		|| "001050061".equals(jixin_face.getPlatformCode()) || "001050062".equals(jixin_face.getPlatformCode())
            		|| "001052003".equals(jixin_face.getPlatformCode()) || "001052011".equals(jixin_face.getPlatformCode())
            		|| "001052012".equals(jixin_face.getPlatformCode()) || "001052013".equals(jixin_face.getPlatformCode())
            		|| "001052031".equals(jixin_face.getPlatformCode()) || "001052032".equals(jixin_face.getPlatformCode())
            		|| "001052033".equals(jixin_face.getPlatformCode()) || "001052034".equals(jixin_face.getPlatformCode())
            		|| "001052999".equals(jixin_face.getPlatformCode()) || "001058000".equals(jixin_face.getPlatformCode())
            		|| "001058002".equals(jixin_face.getPlatformCode()) || "001059000".equals(jixin_face.getPlatformCode())
            		|| "001059001".equals(jixin_face.getPlatformCode()) || "001063000".equals(jixin_face.getPlatformCode())
            		|| "001009999".equals(jixin_face.getPlatformCode())){
            	rets.clear();
    			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
    			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
    			rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
    			logger.error("{} 吉信调用失败,返回错误信息为:{}", trade_id,jixin_face.getPlatformDesc());
    			return rets;
            }           
            logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);			
			resource_tag = buildOutParams(trade_id,jixin_face,retdata,rets);		
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
	public String buildOutParams(String trade_id,FaceJiXinBean jixin_face,Map<String,Object> retdata,Map<String,Object> rets){
		String resource_tag = Conts.TAG_SYS_ERROR;
		logger.info("{} 构建出参开始...", trade_id);
		switch(jixin_face.getPlatformCode()){
			case "001050340":
				logger.info("{} 照片过小(照片大小不能小于5k)", trade_id);
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE05_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "照片过小(照片大小不能小于5k)");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				break;
			case "001000000":
				logger.info("{} 人脸识别成功", trade_id);
				resource_tag = Conts.TAG_FOUND;
				retdata.put("rtn", 0);
				retdata.put("pair_verify_result", "0");			
				retdata.put("pair_verify_similarity", (int)(Float.valueOf(jixin_face.getConfidence())*100));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				break;
			case "001010336":
				logger.info("{} 人脸识别成功", trade_id);
				resource_tag = Conts.TAG_FOUND;
				retdata.put("rtn", 0);
				retdata.put("pair_verify_result", "1");			
				retdata.put("pair_verify_similarity", (int)(Float.valueOf(jixin_face.getConfidence())*100));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				break;
			case "001050042":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "库中无此号");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安库中无此号",trade_id);
				break;
			case "001010034":
				rets.clear();
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 认证不一致",trade_id);
				break;
			case "001050337":
				rets.clear();
				resource_tag = Conts.TAG_MATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}国政通数据源厂商返回异常01!",trade_id);
				break;
			case "001050338":
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
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.error("{} 外部返回识别失败", trade_id);
				break;
		}
		return resource_tag;
	}
}
