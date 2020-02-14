package com.wanda.credit.ds.client.zhengtong;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.SignatureUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.ZT_Ident_Result;
import com.wanda.credit.ds.dao.iface.IZTIdentService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_zhengtong_rz")
public class ZTIdentDataSourceRequestor extends BaseZTDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZTIdentDataSourceRequestor.class);
	private final  String CHANNEL_NO = "01";
	private final  String CHANNEL_NO2 = "02";
	@Autowired
	private IZTIdentService ztIdentService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		String zhengtong_url =  propertyEngine.readById("zhengtong_address01");
		//请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO();		
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setReq_url(zhengtong_url);
		logObj.setDs_id("ds_zhengtong_rz");//log:供应商id
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);	//初始值-失败
		logObj.setIncache("0");//不缓存
		try{
			rets = new HashMap<String, Object>();
			String photoFront = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();	//身份证正面照
			String photoNegative = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();	//身份证反面照
			String fpathFront = null;
			String fpathNegative = null;
			ZT_Ident_Result ident_result = new ZT_Ident_Result();
			if(StringUtils.isNotEmpty(photoFront) && StringUtils.isNotEmpty(photoNegative)){
				logger.info("{} 图片上传征信存储开始...", prefix);
				fpathFront = fileService.upload(photoFront, FileType.JPG, FileArea.DS,trade_id);
				fpathNegative = fileService.upload(photoNegative, FileType.JPG, FileArea.DS,trade_id);
				ident_result.setCerfront(fpathFront);
				ident_result.setCertnegative(fpathNegative);
				logger.info("{} 图片上传征信存储成功,正面照片ID为：{},反面照片ID为：{}",new String[]{ prefix,fpathFront,fpathNegative});
			}
	    	logger.info("{} 身份信息真伪认证开始...",prefix);
	    	
	    	ident_result.setTrade_id(trade_id);
	    	Map<String, String>  map = new TreeMap<String, String>();
	    	//照片比对接口查询	
			map.put("photoFront",photoFront );//机构帐号
			map.put("photoNegative", photoNegative);//机构号
			//请求解析参数
			JSONObject jsonPhoto = buildRequestData(map,CHANNEL_NO,trade_id);
			//log:交易状态信息
			logObj.setState_msg(jsonPhoto.get("error_info").toString());
			DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_zhengtong_pf", jsonPhoto.get("error_no").toString());
			//log 返回码
			logObj.setBiz_code1(dataSourceBizCodeVO == null?jsonPhoto.get("error_no").toString():dataSourceBizCodeVO.getBizCode());
			if(jsonPhoto.get("error_no").equals("0")){
				if(jsonPhoto.get("results")!=null){
					JSONArray jsonArray = jsonPhoto.getJSONArray("results");
					JSONObject jsonObject1 = jsonArray.getJSONObject(0);
					String sysseqnb = jsonObject1.getString("sysseqnb");
					ident_result.setSysseqnb(sysseqnb);
					//保存数据 todo
					logger.info("{} 身份信息真伪认证流水号获得成功,流水号为：{}",prefix,sysseqnb);
					logger.info("{} 身份信息真伪认证分数查询开始...",prefix);

					int icount = 0;
					if(StringUtils.isNotEmpty(sysseqnb)){
						while(true){
							if(icount > 0)
								logger.info("{} 轮询身份信息真伪认证启动,轮询序号:{}",prefix,icount);
							if(icount==times){
								logger.info("{} 轮询身份信息真伪认证最大容忍次数已到,系统将自动停止！",prefix);
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
								rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
								//数据保存
								ident_result.setIdent_status(STATUS_IDENT_NO2);
								ident_result.setStatus(STATUS_FAIL_ERROR);
								ident_result.setTrade_id(trade_id);																
								break;
							}								
							map.clear();
							map.put("sysseqnb", sysseqnb);//发送请求后返回的流水号
							//发送请求
							JSONObject jsonScore = buildRequestData(map,CHANNEL_NO2,trade_id);
							//log:交易状态信息
							logObj.setState_msg(jsonScore.get("error_info").toString());
							dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_zhengtong_pf", jsonScore.get("error_no").toString());
							logObj.setBiz_code1(dataSourceBizCodeVO == null?jsonScore.get("error_no").toString():dataSourceBizCodeVO.getBizCode());
							if(jsonScore.get("error_no").equals("0")){
								if(jsonScore.get("results")!=null){
									JSONArray scoreArray = jsonScore.getJSONArray("results");
									JSONObject jsonScore1 = scoreArray.getJSONObject(0);
									String status = jsonScore1.getString("status");//处理状态，00为成功，01为正在处理，03位处理失败
									String mpssimScore = jsonScore1.getString("mpssim");//公安比对分值
									String localsimScore = jsonScore1.getString("localsim");//本地比对分值
									String respcd = jsonScore1.getString("respcd");//请求应答码
									double score =   Double.parseDouble(mpssimScore); 
									
									ident_result.setCard_no(synchExecutorService.encrypt(jsonScore1.getString("certseq")));
									ident_result.setName(jsonScore1.getString("name"));
									ident_result.setRespinfo(jsonScore1.getString("respinfo"));
									ident_result.setRespcd(respcd);
									ident_result.setStatus(status);
									ident_result.setLocal_score(localsimScore);
									ident_result.setMp_score(mpssimScore);
									ident_result.setIdent_status(STATUS_IDENT_NO2);
									if(status.equals("00")){//处理成功，结束轮询
										//log:交易状态信息
										logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//交易成功
										if(StringUtils.isNotEmpty(respcd)){
											dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_zhengtong_pf", respcd);
											logObj.setBiz_code1(dataSourceBizCodeVO == null?respcd:dataSourceBizCodeVO.getBizCode());
										}
										if(respcd.equals("0000") || score>=45){
											ident_result.setIdent_status(STATUS_IDENT_NO1);
										}
										retdata.put("final_mpssim_score", mpssimScore);
										retdata.put("final_local_score", localsimScore);
										retdata.put("server_idx", "02");
										rets.clear();
										rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
										rets.put(Conts.KEY_RET_DATA,retdata);
										rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
										break;
									}else if(status.equals("01")){//正在处理,继续轮询
										icount++;
									}else{//处理失败，结束轮询
										rets.clear();
										rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
										rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
										logger.info("{} 身份信息真伪认证查询失败,失败原因：{}",prefix,jsonScore1.getString("respinfo"));	
										ident_result.setIdent_status(STATUS_IDENT_NO2);
										break;
									}
									
								}else{
									rets.clear();
									rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
									rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
									logger.info("{} 身份信息真伪认证查询失败",prefix);
									ident_result.setStatus(STATUS_FAIL_NULL);
									break;
								}
							}else {//查询失败，结束轮询
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
								rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
								logger.info("{} 身份信息真伪认证查询失败，失败原因：{}",prefix,jsonScore.get("error_info"));
								ident_result.setStatus(STATUS_FAIL_ERROR);
								break;
							}									
							Thread.sleep(interval);									
						}
					}
					ztIdentService.add(ident_result);
				}else{
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败！");
					logger.info("{} 身份信息真伪认证流水号获得失败！",prefix);
				}			
			}else{
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
				logger.info("{} 身份信息真伪认证失败，失败原因：{}",prefix,jsonPhoto.get("error_info"));
			}

		}catch(Exception ex){
			ex.printStackTrace();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			
			/**如果是超时异常 记录超时信息*/
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		    if(ExceptionUtil.isTimeoutException(ex)){		    	
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);		    	
		    }
		    logObj.setState_msg(ex.getMessage());	
		}finally{
			//log入库
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		}
		return rets;
	}

	/**
	 * 构建请求体
	 * @param contxt 
	 *  一定要含有 cardId cardNo phone usernm
	 *  四个key 的值
	 * */
	private JSONObject buildRequestData(Map<String, String> contxt,String flag,String trade_id) throws Exception{
		JSONObject jsonPhoto = null;
		String photoApi = "";
		String zhengtong_url =  propertyEngine.readById("zhengtong_address01");
		Map<String,String> map = new HashMap<String,String>();
		if(flag.equals("01")){
			map.clear();
			//照片比对接口查询	
			map.put("ptyacct",accessId );//机构帐号
			map.put("ptycd", ptycd);//机构号
			map.put("sourcechnl", sourceChannel);//来源渠道，pc端传0
			map.put("placeid", placeId);//业务发生地
			map.put("biztyp", biztyp);//对照接口文档查看
			map.put("biztypdesc", biztypDesc);//服务描述
			String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			map.put("timestamp", timestamp);//时间

			String sign = SignatureUtil.signature( map,encrykey);
			map.put("sign", sign);//防篡改密钥
			map.put("funcNo", ZHENGTONG_IDENT_API);
			map.put("cerfront", contxt.get("photoFront"));//身份证正面照
			map.put("certnegative", contxt.get("photoNegative"));//身份证反面照			
		}else{
			map.clear();
			map.put("sysseqnb", contxt.get("sysseqnb"));//发送请求后返回的流水号
			map.put("ptyacct", accessId);//券商帐号
			String timestamp1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			map.put("timestamp", timestamp1);
			String sign1 = SignatureUtil.signature( map,encrykey);
			map.put("sign", sign1);//防篡改密钥
			map.put("funcNo", ZHENGTONG_PHOTO_RESULT);//单笔请求业务BUS功能号	
		}
		photoApi = RequestHelper.keyPost(zhengtong_url, map,2);
		DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, photoApi, new String[] { trade_id }));
		jsonPhoto = JSONObject.fromObject(photoApi);
			
		return jsonPhoto;
	}
}
