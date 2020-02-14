package com.wanda.credit.ds.client.xyan;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.counter.GlobalCounter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.aijin.AiJinFaceDataSourceRequestor;
import com.wanda.credit.ds.client.aijin.AiJinFaceDirectRequestor;
import com.wanda.credit.ds.client.xiaoan.XiaoanFaceSourceRequestor;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.client.zhengtong.ZTFace251DataSourceRequestor;
import com.wanda.credit.ds.client.zhongsheng.ZhongSFaceDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 新颜人脸识别
 * @author nan.liu
 * @version 1.0
 * @createdate 2019年8月26日
 * 
 */
@DataSourceClass(bindingDataSourceId = "ds_xyan_faceDirect")
public class XYanFacePhotoDirectRequestor extends BaseXYanAuthenBankCardDataSourceRequestor implements	IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(XYanFacePhotoDirectRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private AiJinFaceDataSourceRequestor aijinFace;
	@Autowired
	private AiJinFaceDirectRequestor aijinDirect;
	@Autowired
	private XiaoanFaceSourceRequestor xiaoanFaceService;
	@Autowired
	private ZTFace251DataSourceRequestor zhengtFaceService;
	@Autowired
	private ZhongSFaceDataSourceRequestor zhongsFaceService;
	@Autowired
	private XYanFacePhotoSourceRequestor xyanFaceService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id;
		String  tail_errorlist = ds.getId()+"_errorlist";
		String member_id = propertyEngine.readById("ds_xyan_member_id");
		String aes_pwd = propertyEngine.readById("ds_xyan_photo_aes_pwd");
		String request_url = propertyEngine.readById("ds_xyan_facePhoto_direct_url");
		String route = propertyEngine.readById("ds_xyan_face_route");
		String acct_id_flag = propertyEngine.readById("ds_xyan_face_acct_switch");
		boolean acct_switch = inAcctFlag(acct_id_flag,ds.getAcct_id());
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
				resource_tag = Conts.TAG_TST_FAIL;
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
				resource_tag = Conts.TAG_TST_FAIL;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "校验不通过:传入参数不正确,姓名格式错误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			/** 构建请求参数 */
			JSONObject po = new JSONObject();
			po.put("name", name);// ds入参 必填
			po.put("idCard", cardNo);// ds入参 必填
			po.put("image", query_image_content);

			Map<String, String> Header = new HashMap<String, String>();
			Header.put("mvTrackId", trade_id);
			JSONObject postParam = new JSONObject();
			postParam.put("loginName", member_id);
			postParam.put("pwd", aes_pwd);
			postParam.put("param", po);
			postParam.put("serviceName", "photoComparison");
			
			logger.info("{} 开始请求远程服务器... ", prefix);
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
//			String postString = "{\"MESSAGE\":\"异常情况\",\"guid\":\"31fd1572-db36-4dfa-8a64-3f420a1a3938\",\"mvTrackId\":\"20190829151442310UKCY\",\"detail\":{\"resultCode\":\"9999\",\"resultMsg\":\"服务异常\"},\"RESULT\":\"-1\"}";
			String postString = RequestHelper.doPost(request_url, null, Header, postParam,ContentType.create("application/json", Consts.UTF_8),false);
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
			logObj.setBiz_code1(result_obj.getString("guid"));
			String result_code = result_obj.getString("RESULT");
			if("-1".equals(result_code)){
				logger.info("{} 新颜交易失败:{}", prefix,result_obj.getJSONObject("detail").getString("resultMsg"));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败:"+result_obj.getJSONObject("detail").getString("resultMsg"));
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 新颜人脸熔断收集器+1：{} {}", prefix, ds.getId(),tail_errorlist);
				GlobalCounter.sign(tail_errorlist, Integer.parseInt(propertyEngine.readById("ds_error_expire_sec")));
				logger.info("{} 新颜人脸熔断收集器当前统计数：{}", prefix,GlobalCounter.getCount(tail_errorlist));
			}else{
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				Map<String, Object> respResult = new HashMap<String, Object>();
				respResult.put("server_idx", "06");
				resource_tag = buildOutParams(trade_id,result_obj,respResult,rets);
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
			if("error".equals(resource_tag) && !acct_switch){
				if(route.equals("xiaoan")){
					logger.info("{} 走小安通道...",trade_id);
					ds.setId("ds_xiaoan_face");
		        	rets = xiaoanFaceService.request(trade_id, ds);
				}else if(route.equals("xinyan")){
					logger.info("{} 走新颜通道...",trade_id);
					ds.setId("ds_xyan_face");
		        	rets = xyanFaceService.request(trade_id, ds);
				}else if(route.equals("yuanjin")){
					logger.info("{} 走爰金通道...",trade_id);
					ds.setId("ds_aijin_facePhoto");
					rets = aijinFace.request(trade_id, ds);
					logger.info("{} 走爰金通道返回信息:{}",trade_id,JSONObject.toJSONString(rets));
				}else if(route.equals("zhongsheng")){
					logger.info("{} 走中胜通道...",trade_id);
					ds.setId("ds_zhongsheng_face");
					rets = zhongsFaceService.request(trade_id, ds);	
				}else if(route.equals("zhengtong")){
					logger.info("{} 走政通通道...",trade_id);
					ds.setId("ds_zhengt_face251");
					rets = zhengtFaceService.request(trade_id, ds);	
				}else{
					logger.info("{} 走爰金直连通道...",trade_id);
					ds.setId("ds_aijin_faceDirect");
					rets = aijinDirect.request(trade_id, ds);	
				}
			}
		}
		return rets;
	}
	public String buildOutParams(String trade_id,JSONObject data,Map<String, Object> respResult,Map<String, Object> rets){
		String resource_tag = Conts.TAG_SYS_ERROR;
		String RESULT = data.getString("RESULT");
		JSONObject detail = data.getJSONObject("detail");
		if("1".equals(RESULT)){
			logger.info("{} 人脸识别成功", trade_id);
			resource_tag = Conts.TAG_FOUND;
			respResult.put("rtn", 0);
			if("1001".equals(detail.getString("resultCode"))){
				respResult.put("pair_verify_result", "0");
			}else{
				respResult.put("pair_verify_result", "1");
			}		
			detail.getJSONObject("resultInfo");
			respResult.put("pair_verify_similarity",detail.getJSONObject("resultInfo").get("score"));
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, respResult);
			rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return resource_tag;
		}else if("2".equals(RESULT)){
			resource_tag = Conts.TAG_MATCH;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
			rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{} 数据源厂商返回异常01!",trade_id);
		}else if("3".equals(RESULT)){
			if("3001".equals(detail.getString("resultCode"))){
				resource_tag = Conts.TAG_UNMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
				rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 认证不一致",trade_id);
			}else if("3002".equals(detail.getString("resultCode"))){
				resource_tag = Conts.TAG_NOMATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, "库中无此号");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安库中无此号",trade_id);
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败:" +data.getString("desc"));
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.error("{} 外部返回识别失败:{}", trade_id);
			}
		}else if("4".equals(RESULT)){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_FACE02_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "上传相片质量校验不合格，请重新拍摄上传");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.warn("{} 图片质量太低",trade_id);
		}else{
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "人脸识别失败:" +data.getString("desc"));
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			logger.error("{} 外部返回识别失败:{}", trade_id);
		}
		return resource_tag;
	}
	public boolean inAcctFlag(String acct_id_flag,String acct_id){
		if(StringUtil.isEmpty(acct_id) || StringUtil.isEmpty(acct_id_flag))
			return false;
		for(String tmp:acct_id_flag.split(",")){
			if(tmp.equals(acct_id))
				return true;
		}
		return false;
	}
}
