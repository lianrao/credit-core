package com.wanda.credit.ds.client.zhengtong;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.thinkive.base.util.Base64;
import com.thinkive.base.util.security.AES;
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
import com.wanda.credit.ds.dao.domain.ZT_Score_Result;
import com.wanda.credit.ds.dao.iface.IZTScoreService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_zhengtong_pfNew")
public class ZTScoreDataSourceRequestor extends BaseZTDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZTScoreDataSourceRequestor.class);
	private final  String CHANNEL_NO = "01";
	private final  String CHANNEL_NO2 = "02";
	private  String channelNo;
	@Autowired
	private IZTScoreService ztScoreService;
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
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);	//初始值-失败
		logObj.setIncache("0");//不缓存
		Map<String,Object> paramIn = new HashMap<String,Object>();
		Map<String,Object> reqParamIn = new HashMap<String,Object>();
		String enCardNo = "";
		try{			
			rets = new HashMap<String, Object>();
	 		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String photo = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();	//照片id			
			enCardNo = synchExecutorService.encrypt(cardNo);
			//记录入参到入参记录表
			reqParamIn.put("cardNo", cardNo);
			reqParamIn.put("name", name);
			if(channelNo.equals(CHANNEL_NO)){
				reqParamIn.put("photo", photo);
			}	
			paramIn.put("cardNo", cardNo);
			paramIn.put("photo", photo);			
			paramIn.put("name", name);			
			
			String photoContent = photo;
			String fpath = null;
			String fail_desc;
			if(channelNo.equals(CHANNEL_NO)){
				logger.info("{} 政通高清比对走01通道！", prefix);
				if(StringUtils.isNotEmpty(photo)){
					logger.info("{} 图片上传征信存储开始...", prefix);
					fpath = fileService.upload(photoContent, FileType.JPG, FileArea.DS,trade_id);
					logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix,fpath);
				}
			}else{	
				logger.info("{} 政通高清比对走其他通道！", prefix);
				logObj.setDs_id("ds_zhengtong_pfNew");//log:供应商id
				if(StringUtils.isNotEmpty(photo)){
					photoContent = photo;
				}else{
					logger.info("{} 政通拿不到照片，依图未返回高清照片！",prefix);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
					return rets;
				}
			}						
			ZT_Score_Result score_result = new ZT_Score_Result();
			score_result.setTrade_id(trade_id);
			score_result.setName(name);
			score_result.setCard_no(enCardNo);
			score_result.setImage_file(fpath);
			
	    	logger.info("{} 照片比对开始...",prefix);
	    	paramIn.put("photoContent", photoContent);
			JSONObject jsonPhoto = buildRequestData(paramIn,CHANNEL_NO,trade_id);
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
					
					logger.info("{} 照片比对流水号获得成功,流水号为：{}",prefix,sysseqnb);
					logger.info("{} 照片比对分数查询开始...",prefix);						
					int icount = 0;
					if(StringUtils.isNotEmpty(sysseqnb)){
						while(true){
							if(icount > 0)
								logger.info("{} 轮询查询比对分数启动,轮询序号:{}",prefix,icount);
							if(icount==times){
								logger.info("{} 轮询查询比对分数最大容忍次数已到,系统将自动停止！",prefix);
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
								rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
								//数据保存
								fail_desc = "轮询查询比对分数最大容忍次数已到,系统将自动停止;接口号为："+ZHENGTONG_PHOTO_RESULT;
								score_result.setIdent_status(STATUS_IDENT_NO2);
								score_result.setStatus(STATUS_FAIL_ERROR);								
								score_result.setSysseqnb(sysseqnb);
								score_result.setFail_desc(fail_desc);							
								break;
							}
							paramIn.clear();
							paramIn.put("sysseqnb", sysseqnb);
							JSONObject jsonScore = buildRequestData(paramIn,CHANNEL_NO2,trade_id);
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
									if(mpssimScore.length()>=20){
										mpssimScore = mpssimScore.substring(0,20);
									}
									if(localsimScore.length()>=20){
										localsimScore = localsimScore.substring(0,20);
									}
									String respcd = jsonScore1.getString("respcd");//请求应答码
									double score =   Double.parseDouble(mpssimScore); 
									score_result.setIdent_status(STATUS_IDENT_NO2);
									score_result.setRespcd(respcd);
									score_result.setRespinfo(jsonScore1.getString("respinfo").toString());
									score_result.setTrade_id(trade_id);
									score_result.setSysseqnb(sysseqnb);
									score_result.setMp_score(mpssimScore);
									score_result.setLocal_score(localsimScore);
									score_result.setStatus(status);
									
									if(status.equals("00")){//处理成功，结束轮询
										logger.info("{} 照片比对分数查询成功！",prefix);
										//log:交易状态信息
										logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//交易成功
										if(StringUtils.isNotEmpty(respcd)){
											dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_zhengtong_pf", respcd);
											logObj.setBiz_code1(dataSourceBizCodeVO == null?respcd:dataSourceBizCodeVO.getBizCode());
										}
										
										
										if(respcd.equals("1000") || score>=45){
											score_result.setIdent_status(STATUS_IDENT_NO1);
										}
										retdata.put("server_idx", "02");
										retdata.put("rtn", 0);										
										retdata.put("pair_verify_similarity", mpssimScore);
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
										logger.info("{} 照片比对分数查询失败,失败原因：{}",prefix,jsonScore1.getString("respinfo"));
										score_result.setFail_desc(jsonScore1.getString("respinfo").toString());
										break;
									}									
								}else{
									rets.clear();
									rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
									rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
									logger.info("{} 照片比对分数查询失败",prefix);
									fail_desc="照片比对分数查询失败,返回结果为空,查询接口号为:"+ZHENGTONG_PHOTO_RESULT+";查询流水号："+sysseqnb;
									score_result.setStatus(STATUS_FAIL_NULL);
									score_result.setFail_desc(fail_desc);
									break;
								}
							}else {//查询失败，结束轮询
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
								rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
								logger.info("{} 照片比对分数查询失败，失败原因：{}",prefix,jsonScore.get("error_info"));
								score_result.setStatus(STATUS_FAIL_ERROR);
								score_result.setFail_desc(jsonScore.get("error_info").toString());
								break;
							}									
							Thread.sleep(interval);									
						}
					}
					ztScoreService.add(score_result);
				}else{
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败！");
					logger.info("{} 照片比对流水号获得失败！",prefix);
				}			
			}else{
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZHENGTONG_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
				logger.info("{} 照片比对失败，失败原因：{}",prefix,jsonPhoto.get("error_info"));
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
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqParamIn, logObj);
		}
		return rets;
	}
	public String getChannelNo() {
		return channelNo;
	}
	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}

	/**
	 * 构建请求体
	 * @param contxt 
	 *  一定要含有 cardId cardNo phone usernm
	 *  四个key 的值
	 * */
	private JSONObject buildRequestData(Map<String, Object> contxt,String flag,String trade_id) throws Exception{
		String zhengtong_url =  propertyEngine.readById("zhengtong_address01");
		JSONObject jsonPhoto = null;
		String photoApi = "";
		Map<String,String> map = new HashMap<String,String>();
			if(flag.equals("01")){
				map.clear();
				AES aes = new AES(encrykey);
				map.put("certseq", aes.encrypt(contxt.get("cardNo").toString(), "utf-8"));//证件号码		
				map.put("ptyacct",accessId );//机构帐号
				map.put("ptycd", ptycd);//机构号
				map.put("sourcechnl", sourceChannel);//来源渠道，pc端传0
				map.put("placeid", placeId);//业务发生地
				map.put("biztyp", biztyp);//对照接口文档查看
				map.put("biztypdesc", biztypDesc);//服务描述
				String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				map.put("timestamp", timestamp);//时间
		
				String sign = SignatureUtil.signature( map,encrykey);
				map.put("certseq", Base64.encodeBytes((URLEncoder.encode(aes.encrypt(contxt.get("cardNo").toString(), "utf-8"), "utf-8")).getBytes(), Base64.DONT_BREAK_LINES));//证件号码
				map.put("sign", sign);//防篡改密钥
				map.put("usernm", contxt.get("name").toString());//用户姓名
				map.put("funcNo", ZHENGTONG_PHOTO_API);
				map.put("videopic", contxt.get("photoContent").toString());//高清头像
				map.put("actionpic", "");//活体照片				
			}else{
				map.clear();
				map.put("sysseqnb", contxt.get("sysseqnb").toString());//发送请求后返回的流水号
				map.put("ptyacct", accessId);//机构帐号
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
