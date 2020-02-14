package com.wanda.credit.ds.client.yituNew;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImgCompress;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.anxin.AXinFaceCheckRequestor;
import com.wanda.credit.ds.client.policeAuthV2.PoliceFacePhotoV2Requestor;
import com.wanda.credit.ds.client.yuanjian.YuanJFaceSourceRequestor;
import com.wanda.credit.ds.client.zhengtong.ZTFace251DataSourceRequestor;
import com.wanda.credit.ds.dao.domain.YT_Auth_Result;
import com.wanda.credit.ds.dao.iface.IYTAuthService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;

/**
 * author:nan.liu
 * desc:依图获取sdk端采集照片集合(/face/basic/check_image_package)
 * data:2018-12-26
 * version:1.0
 * */
@DataSourceClass(bindingDataSourceId="ds_yitu_getPhotos")
public class YiTuGetPackPhotosRequestor extends BaseYiTuNewSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(YiTuGetPackPhotosRequestor.class);
	private final String MSG_SUCC = "交易成功";
	private final String PHOTO_SIZE_FAIL = "2";

	private String yitu_saas_address;
	private String accessId;
	private String accessKey;

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private FileEngine fileEngines;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	private IYTAuthService ytAuthService;
	@Autowired
	private AXinFaceCheckRequestor anxinFace;
	@Autowired
	private YuanJFaceSourceRequestor yuanjianFace;
	@Autowired
	private ZTFace251DataSourceRequestor zhengt;
	@Autowired
	private PoliceFacePhotoV2Requestor policeV2;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String yitu_url =  propertyEngine.readById("yitu_pack_address01");
		String yitu_sign_key =  propertyEngine.readById("yitu_signKey");
		String yitu_photo_aijin =  propertyEngine.readById("yitu_pack_photo_aijin");//是否请求爰金
		double face_dsId2_size = Double.valueOf(propertyEngine.readById("photo_face_ds_size"));
		//请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());//log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setReq_url(yitu_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);	//初始值-失败
		logObj.setIncache("0");//不缓存
		Map<String,Object> paramIn = new HashMap<String,Object>();
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			rets = new HashMap<String, Object>();
			YT_Auth_Result ytAuth = new YT_Auth_Result();
			ytAuth.setTrade_id(trade_id);
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); // 身份证号码
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 姓名
//			String transType = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 传输类型
			String queryImagePackage = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 照片数据包
			String negativeRate = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();//期望误报率
			String returnImage = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();//是否需要返回解密照片
			enCardNo = synchExecutorService.encrypt(cardNo);
			//记录入参到入参记录表			
			paramIn.put("cardNo", cardNo);
			paramIn.put("name", name);
		
			String fpath = null;
			
			//根据dsid和retCode到返回码对照表获取bizCode-BizName
			DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_yitu_auth", PHOTO_SIZE_FAIL);
			
			ytAuth.setCardno(enCardNo);
			ytAuth.setName(name);
			ytAuth.setPhoto_id(fpath);
			ytAuth.setStatus(STATUS_YITU_NO1);
			Map<String, Object> params = new TreeMap<String, Object>();
			Map<String, String> headers = new TreeMap<String, String>();				

			params.put("query_image_package", queryImagePackage);
			params.put("query_image_package_return_image_list", true);
			params.put("query_image_package_check_same_person", true);
			// end
			headers.put("x-access-id", accessId);
			if("01".equals(yitu_sign_key)){
				headers.put("x-signature", generateSignature(pkNew, accessKey, new ObjectMapper().writeValueAsString(params), userDefinedContent));
			}else{
				headers.put("x-signature", generateSignature(pkNew, yitu_sign_key, new ObjectMapper().writeValueAsString(params), userDefinedContent));
			}				
			logger.info("{} 开始发送照片至外部依图进行人脸识别...", prefix);
			String res = RequestHelper.doPost(yitu_url, null, headers, params,
					ContentType.create("text/plain", Consts.UTF_8),false);
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, res, new String[] { trade_id }));
			JSONObject respMap = JSONObject.fromObject(res);
			int rtn = (Integer) respMap.get("rtn");
			ytAuth.setRtn(respMap.get("rtn").toString());
			ytAuth.setMessage(respMap.get("message").toString());
			ytAuth.setGlobal_request_id(respMap.get("global_request_id").toString());
			//log:交易状态信息,返回码保存				
			logObj.setState_msg(respMap.get("message").toString());
			if(StringUtils.isNotEmpty(respMap.get("rtn").toString())){
				dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_yitu_auth", respMap.get("rtn").toString());
				logObj.setBiz_code1(dataSourceBizCodeVO == null?respMap.get("rtn").toString():dataSourceBizCodeVO.getBizCode());
			}else{
				logObj.setBiz_code1(respMap.get("rtn").toString());
			}				
			if (rtn < 0) {			
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败,原因:" + respMap.get("message").toString());
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.error("{} 外部依图返回识别失败", prefix);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//交易成功
				logObj.setState_msg(MSG_SUCC);
				Map<String, Object> respResult = new HashMap<String, Object>();
				JSONObject verify_result = (JSONObject) respMap.get("query_image_package_result");
				String is_same_person = verify_result.get("is_same_person").toString();
				String is_valid_package = verify_result.get("is_valid_package").toString();
				
				if(!"true".equals(is_same_person)){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败,原因:非同一人");
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.error("{} 外部依图返回识别失败,非同人", prefix);
				}
				if(!"true".equals(is_valid_package)){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败,原因:大礼包解析错误");
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.error("{} 外部依图返回识别失败,大礼包解析错误", prefix);
				}
				
				List<String> query_image = (List<String>) verify_result.get("query_image_contents");
				String base64image = query_image.get(0);
				boolean is_size_flag = true;
				try {
					for(String photostr:query_image){
						if(StringUtils.isNotEmpty(photostr)){
							
							logger.info("{} 图片上传征信存储开始...", prefix);
							String file_id  = fileEngines.store("ds_yuanjian_photo",FileArea.DS, FileType.JPG, photostr,trade_id);
							String file_full_path = fileEngines.getFullPathById(file_id);
							logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix,file_id);
							ImgCompress imgCom = new ImgCompress(trade_id,file_full_path);
							double size = imgCom.getSize()/1024F;							
							
							if(size>face_dsId2_size){
								base64image = photostr;
								is_size_flag = false;
								break;
							}
					    }
					}
//					ytAuthPhotoService.add(result_photo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				logger.error("{} 人脸比对开始...", prefix);
				
				Param query_image_content = new Param();
				query_image_content.setName("query_image_content");
				query_image_content.setId("query_image_content");
				query_image_content.setValue(base64image);

				ds.getParams_in().add(query_image_content);
				if(is_size_flag){
					logger.error("{} 照片过小,走政通通道", prefix);
					ds.setId("ds_zhengt_face251");
					rets = zhengt.request(trade_id, ds);
				}else{
					if("anxin".equals(yitu_photo_aijin)){
						ds.setId("ds_anxin_face");
						rets = anxinFace.request(trade_id, ds);
					}else if("yuanjian".equals(yitu_photo_aijin)){
						ds.setId("ds_yuanjian_face");
						rets = yuanjianFace.request(trade_id, ds);
					}else{
						ds.setId("ds_policeAuth_photov2");
						rets = policeV2.request(trade_id, ds);
					}
				}
				
				if("STATUS_SUCCESS".equals(String.valueOf(rets.get("retstatus")))){
					respResult = (Map<String, Object>) rets.get("retdata");
					//是否返回解密照片
					if ("0".equals(returnImage)) {
						respResult.put("photo_content",query_image.get(0));
					}else{
						respResult.put("photo_content","");
					}
					String pair_verify_result = String.valueOf(respResult.get("pair_verify_result"));
					if("1".equals(pair_verify_result)){
						respResult.put("pair_verify_result","0");
					}else{
						respResult.put("pair_verify_result","1");
					}
					rets.put(Conts.KEY_RET_DATA, respResult);
				}
				logger.info("{} 人脸识别成功!", prefix);
			}
			ytAuthService.add(ytAuth);					
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ex.getMessage());
			
			/**如果是超时异常 记录超时信息*/
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		    if(ExceptionUtil.isTimeoutException(ex)){		    	
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
		    	resource_tag = Conts.TAG_SYS_TIMEOUT;
		    }
		    logObj.setState_msg(ex.getMessage());
		    rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{
			//log入库
			ds.setId("ds_yitu_getPhotos");
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}
		return rets;
	}

	public String getYitu_saas_address() {
		return yitu_saas_address;
	}

	public void setYitu_saas_address(String yitu_saas_address) {
		this.yitu_saas_address = yitu_saas_address;
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

}