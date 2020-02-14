package com.wanda.credit.ds.client.policeAuthV2;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorNoticeService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.counter.GlobalCounter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RotatePhoto;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.aijin.AiJinFaceDataSourceRequestor;
import com.wanda.credit.ds.client.anxin.AXinFaceCheckRequestor;
import com.wanda.credit.ds.client.baiduFace.BaiduFaceCheckDetection;
import com.wanda.credit.ds.client.policeAuthV2.newThread.MasterNew;
import com.wanda.credit.ds.client.policeAuthV2.newThread.MyResultVo;
import com.wanda.credit.ds.client.policeAuthV2.newThread.Worker;
import com.wanda.credit.ds.client.xinhua.XHuaFacePhotoRequestor;
import com.wanda.credit.ds.client.xyan.XYanFacePhotoSourceRequestor;
import com.wanda.credit.ds.client.yuanjian.YuanJFaceSourceRequestor;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.client.zhengtong.ZTFace251DataSourceRequestor;
import com.wanda.credit.ds.dao.domain.police.Police_Face_Result;
import com.wanda.credit.ds.dao.iface.police.IPoliceFaceService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_policeAuth_photov2")
public class PoliceFacePhotoV2Requestor extends BasePoliceV2Requestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(PoliceFacePhotoV2Requestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorNoticeService executorNoticeService;
	@Autowired
	private IPoliceFaceService policeService;
	@Autowired
	private XYanFacePhotoSourceRequestor xyanFaceService;
	@Autowired
	private AiJinFaceDataSourceRequestor aijinFace;
	@Autowired
	private ZTFace251DataSourceRequestor zhengtFaceService;
	@Autowired
	private AXinFaceCheckRequestor anXinFaceService;
	@Autowired
	private XHuaFacePhotoRequestor xinhuaService;
	
	@Autowired
	private BaiduFaceCheckDetection baiduService;
	
	@Autowired
	private FileEngine fileEngines;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 公安一所静态照片比对请求开始...", prefix);
		Map<String, Object> rets = null;
		Map<String, Object> retdata = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logger.info("{} 公安一所系统变量获取开始...", prefix);

		String request_url = propertyEngine.readById("ds_policev2_request_url");//国政通调用连接
		String auth_url = propertyEngine.readById("ds_police_auth_url");//国政通账号
		String route = propertyEngine.readById("ds_police_auth_route");

		int timeout_limit = Integer.valueOf(propertyEngine.readById("ds_police_timeout_limit"));
		int times_limit = Integer.valueOf(propertyEngine.readById("ds_police_times_limit"));
		
		int rotate = 0;
		String yuanjin_send_to_baidu = propertyEngine.readById("yuanjin_send_to_baidudsids");
		logger.info("{} 公安一所系统变量获取成功", prefix);
		String resource_tag = Conts.TAG_SYS_ERROR;
		String face_switch = "";
		if(ParamUtil.findValue(ds.getParams_in(), "face_switch")!=null){
			face_switch = ParamUtil.findValue(ds.getParams_in(), "face_switch").toString();
		}
		try{	
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); // 身份证号码
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 姓名
			String queryImagePhoto = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 照片数据包

			logger.info("{} 公安一所入参获取成功", prefix);
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			logger.info("{} 公安一所静态照片比对加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(auth_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setIncache("1");
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){				
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			if(!StringUtil.isEmpty(ds.getAcct_id())){
				if(yuanjin_send_to_baidu.contains(ds.getAcct_id())){
					String file_id  = fileEngines.store("ds_rotate_police",FileArea.DS, FileType.JPG, queryImagePhoto,trade_id);
					String file_full_path = fileEngines.getFullPathById(file_id);
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
								queryImagePhoto = image_rorate;
								file_id  = fileEngines.store("ds_nciic_jx",FileArea.DS, FileType.JPG, 
										image_rorate,trade_id);
								file_full_path = fileEngines.getFullPathById(file_id);
								logger.info("{} 旋转后保存图片路径:{}", prefix,file_full_path);
							}			
						}				
					}
				}
			}			
			ds.setId("ds_policeAuth_photov2");
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", prefix);
				
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			logObj.setIncache("0");
			
			if(isInProvince(cardNo,propertyEngine)){
				logger.error("{} 省份已覆盖,调用公安一所", trade_id);
				GlobalCounter.setString(trade_id+"_POLICE", "0",120);
//				ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
//				String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,photo_limit);
//				if(!StringUtil.isEmpty(comperss_rsp)){
//					queryImagePhoto = comperss_rsp;
//				}				
				MasterNew<MyResultVo> master=new MasterNew<MyResultVo>();//master 管理 worker
				//宜信数据源请求
				Worker<MyResultVo> police_worker = new Worker<MyResultVo>(trade_id,request_url,
						auth_url,queryImagePhoto,name,cardNo,master){
					@Override
					public void run() {
						try {			
							logger.info("{} 异步请求公安一所开始...", getTrade_id());
							long start = new Date().getTime();
							Police_Face_Result result = getPoliceResult(propertyEngine,getTrade_id(),getRequest_url(),
									getAuth_url(),getQueryImagePhoto(),getName(),getCardNo());
							long end = new Date().getTime();
							if(result!=null){
								if(!StringUtil.isEmpty(result.getAuthResult()) 
										&& "true".equals(result.getSuccess())){
									JSONObject score = (JSONObject) JSON.parse(result.getAuthResultRetainData());
									String scores = score.getString("rxfs");
									GlobalCounter.setString(getTrade_id()+"_POLICE01", result.getAuthResult()+";"+scores,120);
									GlobalCounter.setString(getTrade_id()+"_POLICE", "1",120);
									logger.info("{} redis设置值完成,数据保存开始...", getTrade_id());
									result.setCardNo(GladDESUtils.encrypt(getCardNo()));
									result.setName(getName());
									result.setTrade_id(getTrade_id());
									result.setTotal_cost(end-start);
									policeService.add(result);
									logger.info("{} 公安一所返回数据保存成功", getTrade_id());
								}
							}									
							logger.info("{} 异步请求公安一所完成", getTrade_id());
							
						} catch (Exception e) {
							try {
								GlobalCounter.setString(getTrade_id()+"_POLICE", "2",120);
							} catch (ServiceException e1) {
								logger.info("{} 异常设置redis值失败", getTrade_id());
							}
							logger.error("{} 系统处理时异常，异常信息:{}", getTrade_id() ,ExceptionUtil.getTrace(e));
						}
					}
				};
				master.addWorker(police_worker);
				master.excute();
				Thread.sleep(timeout_limit);//模拟ds3处理需要时间
				logger.info("{}worker主程序等待结束,循环取结果开始...", prefix);	
				int count1=0;
				boolean police_finish = false;
				while(true){
					if(count1 >= 0)
						logger.info("{} 轮询公安一所主程序启动,轮询序号:{}",prefix,count1);
					if(count1>=times_limit){
						logger.info("{} 轮询公安一所主程序最大容忍次数已到,系统将自动停止！",prefix);
						if("1".equals(GlobalCounter.getString(trade_id+"_POLICE")!=null?GlobalCounter.getString(trade_id+"_POLICE"):"1")){
							police_finish = true;
						}
						break;
					}
					if("2".equals(GlobalCounter.getString(trade_id+"_POLICE")!=null?GlobalCounter.getString(trade_id+"_POLICE"):"1")){
						logger.info("{} 轮询公安一所主程序获取到redis为异常值,轮询结束",prefix,count1);
						break;
					}
					if("1".equals(GlobalCounter.getString(trade_id+"_POLICE")!=null?GlobalCounter.getString(trade_id+"_POLICE"):"1")){
						police_finish = true;
						break;
					}
					Thread.sleep(100);//模拟ds3处理需要时间
					count1++;
				}
				String res = "";
				if(police_finish){
					logger.info("{} 异步调用在规定时间内返回", trade_id);
					res = GlobalCounter.getString(trade_id+"_POLICE01")!=null?GlobalCounter.getString(trade_id+"_POLICE01"):"";
				}
				logObj.setIncache("0");
				if(StringUtil.isEmpty(res)){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "人脸识别失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_UNFOUND});
					logger.error("{} 公安一所未在规定时间内返回信息", trade_id);
					if(!StringUtil.isEmpty(face_switch) && "1".equals(face_switch)){
						rets.clear();
						resource_tag = Conts.TAG_FOUND;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "库中无此号");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{} 公安库中无此 号",trade_id);
					}else if(!StringUtil.isEmpty(face_switch) && "2".equals(face_switch)){
						rets.clear();
						resource_tag = Conts.TAG_FOUND;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{} 公安库中无照片 1",trade_id);
					}
				}else{
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					if(!StringUtil.isEmpty(res)){
						String[] resp = res.split(";");
						String authresult = resp[0];
						DecimalFormat df = new DecimalFormat(".00");
						String score = df.format(Double.valueOf(resp[1])/10);
						logObj.setBiz_code2(res);
						if(!authresult.equals("EXXX")){
							resource_tag = Conts.TAG_UNMATCH;
						}						
						if("00".equals(authresult.substring(0,2))){
							resource_tag = Conts.TAG_FOUND;
							retdata.put("pair_verify_similarity", score);
							retdata.put("pair_verify_result", "0");
							retdata.put("rtn", 0);
							retdata.put("server_idx", "06");
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
							rets.put(Conts.KEY_RET_DATA, retdata);
							rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						}else if("01".equals(authresult.substring(0,2)) 
								|| "02".equals(authresult.substring(0,2))
								|| "0F".equals(authresult.substring(0,2))){
							resource_tag = Conts.TAG_FOUND;
							if("02".equals(authresult.substring(0,2))){
								retdata.put("pair_verify_similarity", score);
							}else{
								retdata.put("pair_verify_similarity", score);
							}		
							retdata.put("pair_verify_result", "1");
							retdata.put("rtn", 0);
							retdata.put("server_idx", "06");
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
							rets.put(Conts.KEY_RET_DATA, retdata);
							rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						}else if(!StringUtil.isEmpty(face_switch) && "1".equals(face_switch)){
							rets.clear();
							resource_tag = Conts.TAG_FOUND;
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
							rets.put(Conts.KEY_RET_MSG, "库中无此号");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							logger.warn("{} 公安库中无此 号",trade_id);
						}else if(!StringUtil.isEmpty(face_switch) && "2".equals(face_switch)){
							rets.clear();
							resource_tag = Conts.TAG_FOUND;
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
							rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							logger.warn("{} 公安库中无照片 1",trade_id);
						}
					}				
				}
			}else{
				logger.error("{} 省份未覆盖,不调用公安一所", trade_id);
			}									
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
//			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
//			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
			if(!"found".equals(resource_tag) && StringUtil.isEmpty(face_switch)){
				long start = System.currentTimeMillis();
				String ds_id = "";
				try {
					ds_id = findABDs(propertyEngine.readById("ds_ab_watch"),getDsid("1",route),trade_id);
					if(!StringUtils.isEmpty(ds_id)){
						route = getDsid("2",ds_id);
					}					
				} catch (ServiceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				rets.clear();
				if(route.equals("xinyan")){
					logger.info("{} 走新颜通道...",trade_id);
					ds.setId("ds_xyan_face");
		        	rets = xyanFaceService.request(trade_id, ds);
		        	ds_id = "ds_xyan_face";
				}else if(route.equals("yuanjin")){
					logger.info("{} 走爰金通道...",trade_id);
					ds.setId("ds_aijin_facePhoto");
					rets = aijinFace.request(trade_id, ds);
					ds_id = "ds_aijin_facePhoto";
				}else if(route.equals("anxin")){
					logger.info("{} 走安信通道...",trade_id);
					ds.setId("ds_anxin_face");
					rets = anXinFaceService.request(trade_id, ds);	
					ds_id = "ds_anxin_face";
				}else if(route.equals("zhengtong")){
					logger.info("{} 走政通通道...",trade_id);
					ds.setId("ds_zhengt_face251");
					rets = zhengtFaceService.request(trade_id, ds);	
					ds_id = "ds_zhengt_face251";
				}else if(route.equals("xinhua")){
					logger.info("{} 走新华汉布直连通道...",trade_id);
					ds.setId("ds_xinhua_face");
					rets = xinhuaService.request(trade_id, ds);	
					ds_id = "ds_xinhua_face";
				}else{					
					logger.info("{} 走新华通道...",trade_id);
					ds.setId("ds_xinhua_face");
					rets = xinhuaService.request(trade_id, ds);
					ds_id = "ds_xinhua_face";
				}
				String ds_error_flag = ds_id + "_errorlist";
				if(isErr(rets,propertyEngine.readById("ds_errors_watch")) || isTimeout(prefix,ds_id,propertyEngine.readById("ds_errors_timeout_watch"),(new Date().getTime() - start))){
					logger.warn("{} police数据源熔断收集器+1：{} {}", prefix, ds_id,ds_error_flag);
					try {
						GlobalCounter.sign(ds_error_flag, Integer.parseInt(propertyEngine
								.readById("ds_error_expire_sec")));
						logger.info("{} police数据源熔断收集器当前统计数：{}", prefix,
								GlobalCounter.getCount(ds_error_flag));

						if(isfuseOff(trade_id,ds_id,propertyEngine.readById("ds_max_error_num")
								,propertyEngine.readById("ds_sms_time_rate"),propertyEngine.readById("ds_ab_watch"),
								propertyEngine.readById("ds_ab_expire_sec"),
								propertyEngine.readById("sys_send_mobiles01"),executorNoticeService)){
							logger.info("{} police数据源:{} 已加入errlist列表", prefix,ds_id);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
			}
		}
		return rets;
	}
	public String getDsid(String flag,String route_dsid){
		String result = "";
		for(String tmp:propertyEngine.readById("ds_police_route_dsids").split(",")){
			String[] tmp1 = tmp.split(":");
			if("1".equals(flag)){
				if(tmp1[0].equals(route_dsid)){
					result = tmp1[1];
				}
			}else{
				if(tmp1[1].equals(route_dsid)){
					result = tmp1[0];
				}
			}
		}
		return result;
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
