package com.wanda.credit.ds.client.yitu;

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
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.YT_Auth_Result;
import com.wanda.credit.ds.dao.iface.IYTAuthService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_yitu_authnew")
public class YiTuAuthNewDataSourceRequestor extends BaseYiTuDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(YiTuAuthNewDataSourceRequestor.class);
	private final String MSG_SUCC = "交易成功";
	private String verifImgApi;
	private String yitu_saas_address;
	private String accessId;
	private String accessKey;
	private String true_negative_rate;

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	private IYTAuthService ytAuthService;
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String yitu_url =  propertyEngine.readById("yitu_address01");
		//请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id("ds_yitu_authnew");//log:供应商id
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
			String photo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 照片ID
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 照片ID
			String transType = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString(); // 传输类型
			enCardNo = synchExecutorService.encrypt(cardNo);
			//记录入参到入参记录表			
			paramIn.put("cardNo", cardNo);
//			paramIn.put("photo", photo);
			paramIn.put("name", name);
			paramIn.put("transType", transType);
			String sendCard = MD5.uppEncodeByMD5(cardNo+transType);
			String fpath = null;
			ytAuth.setCardno(enCardNo);
			ytAuth.setName(name);
			ytAuth.setStatus(STATUS_YITU_NO2);
			ytAuth.setTrans_type(transType);
			Map<String, Object> respMap = null;
			logger.info("{} 人脸识别UseId为 : {}", prefix,sendCard);
			Map<String, Object> params = new TreeMap<String, Object>();
			Map<String, String> headers = new TreeMap<String, String>();
			params.put("user_id", sendCard); // 加密身份证号码,避免被泄露
			params.put("query_image_package", photo);
			params.put("query_image_package_return_image_list", true);
			params.put("true_negative_rate", true_negative_rate);// 99.9 这个参数是误报率,按千分之一误报率部署
			// end
			headers.put("x-access-id", accessId);
			headers.put("x-signature", generateSignature(pk, accessKey, new ObjectMapper().writeValueAsString(params), userDefinedContent));
			logger.info("{} 开始发送照片至外部依图进行人脸识别...", prefix);
			String res = "";
			res = RequestHelper.doPost(yitu_url + verifImgApi, null, headers, params,
					ContentType.create("text/plain", Consts.UTF_8),false);			
			logger.info("{} 外部依图返回消息成功！", prefix);
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, res, new String[] { trade_id }));
			respMap = new ObjectMapper().readValue(res, Map.class);// 转成map
			int rtn = (Integer) respMap.get("rtn");
			ytAuth.setRtn(respMap.get("rtn").toString());
			ytAuth.setMessage(respMap.get("message").toString());
			//log:交易状态信息,返回码保存
			logObj.setState_msg(respMap.get("message").toString());
			if(StringUtils.isNotEmpty(respMap.get("rtn").toString())){
				if("0".equals(respMap.get("rtn").toString()))
					logObj.setBiz_code1("YTBD_001");
				if("1".equals(respMap.get("rtn").toString()))
					logObj.setBiz_code1("YTBD_002");
			}else{
				logObj.setBiz_code1(respMap.get("rtn").toString());
			}	
			if (rtn < 0) {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + respMap.get("message").toString());
				resource_tag = Conts.TAG_UNFOUND;
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.error("{} 外部依图返回识别失败", prefix);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//交易成功
				logObj.setState_msg(MSG_SUCC);
				respMap.put("server_idx", "01");
				respMap.put("rtn", 0);
				ytAuth.setPair_result(respMap.get("pair_verify_result").toString());
				String verify_similarity = respMap.get("pair_verify_similarity").toString();
				if(respMap.get("global_request_id") != null){
					ytAuth.setGlobal_request_id(respMap.get("global_request_id").toString());
				}
				Map<String, Object> query_image_result=(Map<String, Object>) respMap.get("query_image_package_result");
				List<String> query_image = (List<String>) query_image_result.get("query_image_contents");
				if(StringUtils.isNotEmpty(query_image.get(0))){
					logger.info("{} 图片上传征信存储开始...", prefix);
					fpath = fileService.upload(query_image.get(0), FileType.JPG, FileArea.DS,trade_id);
					logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix,fpath);
					respMap.put("photo_content", query_image.get(0));
			    }else{
			    	respMap.put("photo_content", "");
			    	logger.warn("{} 依图未返回照片信息！", prefix);
			    }
				ytAuth.setPhoto_id(fpath);
				int similer_len = verify_similarity.length();
				if(similer_len>=20){
					ytAuth.setPair_similarity(verify_similarity.substring(0,20));
				}else{
					ytAuth.setPair_similarity(verify_similarity);
				}
				
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, respMap);
				rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
				resource_tag = Conts.TAG_FOUND;
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.info("{} 人脸识别成功!", prefix);
			}
			ytAuthService.add(ytAuth);
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			
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
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}
		return rets;
	}

	public String getVerifImgApi() {
		return verifImgApi;
	}

	public void setVerifImgApi(String verifImgApi) {
		this.verifImgApi = verifImgApi;
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

	public String getTrue_negative_rate() {
		return true_negative_rate;
	}

	public void setTrue_negative_rate(String true_negative_rate) {
		this.true_negative_rate = true_negative_rate;
	}

}