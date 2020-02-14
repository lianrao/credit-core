/**   
* @Description: 商汤上传Base64图片
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.shangTangPic;


import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.client.shangTangPic.util.PicSignUtil;
import com.wanda.credit.ds.dao.domain.phjr.STFileUpload;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_stPic_base64Up")
public class PicBase64UploadDsRequestor extends BaseShangTangPicDSRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(PicBase64UploadDsRequestor.class);
	
	
	
	public Map<String, Object> request(String tradeId, DataSource ds) {
		final String prefix = tradeId + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 商汤图片比对上传Base64图片Begin {}", prefix, start);
		String url = propertyEngine.readById("st_uploadBase64_url");
		if (StringUtil.isEmpty(url)) {
			url = "https://v2-auth-api.visioncloudapi.com/resource/image/base64";
		}
		int timeOut = 10000;
		String timeOutStr = propertyEngine.readById("st_pic_timeOut");
		if (!StringUtil.isEmpty(timeOutStr) && StringUtil.isNumeric(timeOutStr)) {
			timeOut = Integer.parseInt(timeOutStr);
		}
		//初始化对象
		Map<String, Object> rets = initRets();
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		//计费标签
		Set<String> tags = new HashSet<String>();
		tags.add(Conts.TAG_SYS_ERROR);
		//交易日志信息数据
		DataSourceLogVO logObj = buildLogObj(ds.getId(),url);
		STFileUpload fileInfo = null;
		try{
			String apiKey = propertyEngine.readById("st_pic_apikey");
			String apiSecret = propertyEngine.readById("st_pic_apisecret");
			if (StringUtil.isEmpty(apiKey)) {
				apiKey = "06deeb41bdb94e1ba3b7ee9d019f0396";
			}
			if (StringUtil.isEmpty(apiSecret)) {
				apiSecret = "336ce6b0f9ae41d5bd24a06bd3b3fb21";
			}

			//获取入参
			logger.info("{} 开始解析传入的参数" , prefix);
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			String base64Pic = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
			String type = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();
			String busiNo = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();	
			logger.info("{} 解析传入的参数成功" , prefix);
			//保存入参
			Map<String, Object> paramIn = new HashMap<String, Object>();
			paramIn.put("name", name);
			paramIn.put("cardNo", cardNo);
			paramIn.put("type", type);
			paramIn.put("busiNo", busiNo);
			saveParamIn(paramIn, tradeId, logObj);
			String fileId = fileService.upload(base64Pic,FileType.JPG,FileArea.DS, tradeId);
			logger.info("{} 保存入参成功", prefix);
			//TODO是否校验图片大小			
			logger.info("{} 请求URL为  {}" , prefix ,url);
			logger.info("{} 请求URL信息地址为  {}" , prefix , url);
			String nonce = PicSignUtil.getNonce();
			long timeStamp = System.currentTimeMillis();
			String authorization = PicSignUtil.genAuthorization(apiKey, apiSecret, timeStamp + "", nonce);
			
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(HEADER_AUTHOR, authorization);
			long postStart = System.currentTimeMillis();
			Map<String, Object> bodyMap = new HashMap<String, Object>();
			bodyMap.put(BODY_DATA, base64Pic);
			
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(timeOut)
					.setSocketTimeout(timeOut)
					.setConnectTimeout(timeOut)
					.build();
			Map<String,Object> resMap = RequestHelper.doPostRetFull(url, null, headers, bodyMap, null, requestConfig, true);
			long postCost = System.currentTimeMillis() - postStart;
			logger.info("{} 请求商汤接口耗时时间为 {} ms" , prefix ,postCost);
			if (postCost >= timeOut) {
				//http请求超过10S
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求商汤超时");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;
			}
			
			Object statuObj = resMap.get(RequestHelper.HTTP_RES_CODE);
			if (!"200".equals(statuObj + "")) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("http请求商汤返回状态码为" + statuObj);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;
			}
			Object retBodyObj = resMap.get(RequestHelper.HTTP_RES_BODYSTR);
			if (StringUtil.isEmpty(retBodyObj)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("商汤返回内容为空");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;
			}
			String postResult = retBodyObj.toString();
			logger.info("{} http请求返回结果为 {}" , prefix , postResult);
			
			JSONObject resJsonObj = JSONObject.parseObject(postResult);
			int resCode = resJsonObj.getIntValue(RES_CODE);
			String resMsg = "上传成功";
			String resRequestId = resJsonObj.getString(RES_REQUEST_ID);
			
			if (1000 == resCode) {
				String stId = resJsonObj.getString("id");
				fileInfo = buildFileUpload(tradeId,busiNo,name,cardNo,fileId,stId,resRequestId);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				retData.put("image_id", stId);
				
				tags.clear();
				tags.add(Conts.TAG_TST_SUCCESS);
			}else{
				fileInfo = buildFileUpload(tradeId,busiNo,name,cardNo,fileId,null,resRequestId);
				resMsg = resJsonObj.getString(RES_MESSAGE);
				tags.clear();
				tags.add(Conts.TAG_TST_FAIL);
			}
			
			retData.put(RES_CODE, resCode);
			retData.put(RES_MESSAGE, resMsg);
			retData.put(RES_REQUEST_ID, resRequestId);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_SUCCESS.getRet_msg());
			rets.put(Conts.KEY_RET_DATA, retData);
			
			logObj.setBiz_code1(resCode + "");
			logObj.setState_msg(resMsg);
			
		}catch(Exception e){
			logger.error("{} 处理异常 {}" , prefix ,e.getMessage());
		}finally{
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[tags.size()]));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(StringUtils.join(tags, ";"));
			long dsLogStart = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeLog(tradeId,logObj);
			if (fileInfo != null) {
				try {
					uploadService.add(fileInfo);
				} catch (Exception addExe) {
					addExe.printStackTrace();
					logger.error("{} 保存数据异常 {}" , prefix ,addExe.getMessage());
				}
			}
			logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
		}

		return rets;
	}



	private STFileUpload buildFileUpload(String tradeId, String busiNo,
			String name, String cardNo, String fileId, String stId,String stReqId) {
		STFileUpload fileUpload = new STFileUpload();
		Date nowTime = new Date();
		fileUpload.setCreate_date(nowTime);
		fileUpload.setUpdate_date(nowTime);
		fileUpload.setTrade_id(tradeId);
		fileUpload.setMh_busno(busiNo);
		if (StringUtils.isNotEmpty(name)) {
			fileUpload.setName(name);
		}
		if (StringUtils.isNotEmpty(cardNo)) {
			fileUpload.setCardno(CryptUtil.encrypt(cardNo));
		}
		fileUpload.setSwift_id(fileId);
		fileUpload.setSt_id(stId);
		fileUpload.setSt_rquestid(stReqId);
		return fileUpload;
	}

	

	

}
