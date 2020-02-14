package com.wanda.credit.ds.client.shangtang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SignatureException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;
/***
 * 静默视频活体检测
 * @author liunan
 *
 */
@DataSourceClass(bindingDataSourceId = "ds_shangtang_liveness")
public class STGladVideoStalessRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(STGladVideoStalessRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 商汤静默视频调用开始...", prefix);
		Map<String, Object> rets = null;
		Map<String, Object> retdata = new HashMap<String, Object>();
		String shangtang_url = propertyEngine.readById("shangtang_liveness_url");
		// 请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setDs_id("ds_shangtang_liveness");// log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setReq_url(shangtang_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("0");// 不缓存
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			rets = new HashMap<String, Object>();
			String vopath = ParamUtil.findValue(
					ds.getParams_in(), paramIds[0]).toString(); // 视频
			logger.info("{} 商汤静默视频入参信息:{}", prefix,vopath);
			String result = httpSTClientPost(vopath,prefix);
			logger.info("{} 商汤静默视频返回信息:{}", prefix,result);
			JSONObject respMap = JSONObject.fromObject(result);
			logObj.setBiz_code1(respMap.getString("request_id"));
			if("1000".equals(respMap.getString("code"))){
				resource_tag = Conts.TAG_TST_SUCCESS;
				if("ok".equals(respMap.getString("liveness_status"))){
					retdata.put("result", "1");
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_MSG, "活体检测通过");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("hack".equals(respMap.getString("liveness_status"))){
					rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL01);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL01.ret_msg);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("short_time".equals(respMap.getString("liveness_status"))){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL02);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL02.ret_msg);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("no_face_detected".equals(respMap.getString("liveness_status"))){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL03);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL03.ret_msg);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("loss_tracking".equals(respMap.getString("liveness_status"))){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL04);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL04.ret_msg);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("face_changed".equals(respMap.getString("liveness_status"))){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL05);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL05.ret_msg);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL06);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL06.ret_msg);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}				
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL07);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_ST_VIDEO_FAIL07.ret_msg);
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			}
		} catch (Exception ex) {
			resource_tag = Conts.TAG_TST_FAIL;
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_SHANGT_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "视频拍摄不符合要求,请重新拍摄!");
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));

			/** 如果是超时异常 记录超时信息 */
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			if (ExceptionUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				resource_tag = Conts.TAG_SYS_TIMEOUT;
			}
			logObj.setState_msg(ex.getMessage());
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		} finally {
			// log入库
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			executorDtoService.writeDsLog(trade_id,logObj,true);
		}
		return rets;
	}

	public String httpSTClientPost(String vopath, String prefix)
			throws ClientProtocolException, IOException, SignatureException {
		logger.info("{} 商汤静默视频活体检测调用开始...", prefix);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(propertyEngine.readById("shangtang_liveness_url"));
		FileBody fileVideo = new FileBody(new File(vopath));
		StringBody return_status = new StringBody("true");
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("video_file", fileVideo);
		entity.addPart("return_status", return_status);
		post.setEntity(entity);
		post.setHeader("Authorization", GenerateString.genHeaderParam(
				propertyEngine.readById("shangtang_liveness_api_key"),
				propertyEngine.readById("shangtang_liveness_api_sec")));// 请将AUTHORIZATION替换为根据API_KEY和API_SECRET得到的签名认证串
		long startTime = System.currentTimeMillis();
		HttpResponse response = httpclient.execute(post);
		long endTime = System.currentTimeMillis();
		logger.info("{} 活体检测请求耗时:{}", prefix, (endTime - startTime) + " ms");
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entitys = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entitys.getContent()));
			String line = reader.readLine();
			return line;
		} else {
			HttpEntity r_entity = response.getEntity();
			String responseString = EntityUtils.toString(r_entity);
			logger.info("{} 错误码是:{} 出错原因:{}", prefix, response.getStatusLine()
					.getStatusCode()
					+ "  "
					+ response.getStatusLine().getReasonPhrase(),
					responseString);
			return responseString;
		}
	}
}
