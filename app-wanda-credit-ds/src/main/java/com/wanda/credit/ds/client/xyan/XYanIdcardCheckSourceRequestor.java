package com.wanda.credit.ds.client.xyan;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.xyan.utils.HttpUtil;
import com.wanda.credit.ds.client.xyan.utils.RsaCodingUtil;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.ExceptionUtil;
/**
 * @description 公安身份核查
 * @author nan.liu 
 * @version 1.0
 * @createdate 2017年03月17日
 *  
 */
@DataSourceClass(bindingDataSourceId="ds_xyan_police")
public class XYanIdcardCheckSourceRequestor  extends BaseXYanAuthenBankCardDataSourceRequestor
              implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(XYanIdcardCheckSourceRequestor.class);
	private final  String CHANNEL_NO = "01";
	private final  String DIRECT_CHANNEL_NO = "03";
	private final  String POLICE_PHOTO_NO = "POLICE_PHOTO_NOTEXISTS";
	private final  String POLICE_STATUS_SUSSES = "一致";
	private final  String POLICE_STATUS_FAIL = "不一致";
	private final  String STATUS_CHECK_EQUAL = "00";
	private final  String STATUS_CHECK_NO = "01";
	private final  String STATUS_CHECK_NULL = "02";
	private final  String SOURCE_ID = "07";
	
	private final  String CODE_EQUAL = "gajx_001";
	private final  String CODE_NOEQUAL = "gajx_002";
	private final  String CODE_NOEXIST = "gajx_003";
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private INciicCheckService nciicCheckService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	private IExecutorFileService fileService;
	
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id;
		
		String member_id = propertyEngine.readById("ds_xyan_member_id2");
		String terminal_id = propertyEngine.readById("ds_xyan_termid2");
		String request_url = propertyEngine.readById("ds_xyan_police_url");
		String pfxpwd = propertyEngine.readById("ds_xyan_pfxpwd2");
		String pfxname =  propertyEngine.readById("ds_xyan_pfxname2");
		String is_photo =  propertyEngine.readById("ds_xyan_isPhoto");
		int incache_days = Integer.valueOf(propertyEngine.readById("ds_police_incacheTime"));//公安数据缓存时间(天)
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		/**设置编目*/
		Set<String> tags = new HashSet<String>();
		String initTag = Conts.TAG_SYS_ERROR;
		logger.info("{} 公安核查交易开始...",prefix);
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setIncache("0");
		logObj.setTrade_id(trade_id);
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(request_url);
 		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));

		Map<String, Object> rets = new HashMap<String, Object>();;
 		Map<String,Object> paramForLog = new HashMap<String,Object>();
		try{			
			/**姓名-必填*/
	 		String name = (String)ParamUtil.findValue(ds.getParams_in(), "name");
            /**身份证号码-必填*/  
	 		String cardNo = (String)ParamUtil.findValue(ds.getParams_in(), "cardNo");  	
	 		String channelNo = (String)ParamUtil.findValue(ds.getParams_in(), "channelNo"); //是否忽略无照片
	 		if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不符合规范");
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logger.error("{} {}",prefix,logObj.getState_msg());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				return rets;
			}	
	 		String cardNo_check = null;
			String name_check = null;
			String image = null;
			String fileId = null;
			retdata.put("server_idx", "07");
	 		/**敏感数据加密*/
	 		String cardNo_ency = synchExecutorService.encrypt(cardNo);
			
	 		/**请求参数记录到日志*/
	 		paramForLog.put("name", name);
	 		paramForLog.put("cardNo", cardNo);
	 		boolean  incacheData = true;
			if(!DIRECT_CHANNEL_NO.equals(channelNo)){
				incacheData = nciicCheckService.inCachedCount(name, cardNo_ency,incache_days);
			}
			if(!incacheData){
				logObj.setIncache("0");
				/**构建请求参数*/
				JSONObject po=new JSONObject();
				po.put("member_id", member_id);//配置参数
				po.put("terminal_id", terminal_id);//配置参数			
				po.put("id_card", cardNo);//ds入参 必填 
				po.put("id_holder", name);//ds入参 必填
				po.put("is_photo", is_photo);//ds入参 必填
				po.put("industry_type", "A1");//配置参数
				po.put("trans_id",  trade_id);
				po.put("trade_date", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				String base64str = com.wanda.credit.ds.client.xyan.utils.SecurityUtil.Base64Encode(po.toString());
				/** rsa加密  **/
				String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str, cer_file_base_path+pfxname, pfxpwd);//加密数据
				
				Map<String,String> HeadPostParam = new HashMap<String,String>();   
		        HeadPostParam.put("member_id", member_id);
		        HeadPostParam.put("terminal_id", terminal_id);
		        HeadPostParam.put("data_type", "json");
		        HeadPostParam.put("data_content",data_content);
		        StringBuilder postParms = new StringBuilder();
				int PostItemTotal = HeadPostParam.keySet().size();
				int itemp=0;
				for (String key : HeadPostParam.keySet()){
					postParms.append(key + "="+HeadPostParam.get(key));
					itemp++;
					if(itemp<PostItemTotal){
						postParms.append("&");
					}
				}
				logger.info("{} 开始请求远程服务器... ",new String[]{prefix});
		        String postString  =HttpUtil.RequestForm(request_url, HeadPostParam);
				logger.info("{} 请求成功!",prefix);
				if(!StringUtils.isNotEmpty(postString)){
					logger.info("{} 远程请求返回数据为空! ",new String[]{prefix});
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回异常!");	
					rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
					return rets;
				}
				JSONObject result_obj =JSONObject.parseObject(postString);
				JSONObject data_obj = null;
				if(result_obj.getBoolean("success")&&(data_obj=result_obj.getJSONObject("data"))!=null && !"9".equals(data_obj.getString("code"))){
					Nciic_Check_Result nciic_check = new Nciic_Check_Result();
					nciic_check.setTrade_id(trade_id);
					nciic_check.setCardno(cardNo_ency);
					nciic_check.setName(name);
					nciic_check.setSourceid(SOURCE_ID);
					nciic_check.setStatus(STATUS_CHECK_NULL);
					if("0".equals(data_obj.getString("code"))){
						logObj.setBiz_code1(CODE_EQUAL);
						initTag = Conts.TAG_MATCH;
						image = data_obj.getString("photo");
						cardNo_check = POLICE_STATUS_SUSSES;
						name_check = POLICE_STATUS_SUSSES;
						retdata.put("resultGmsfhm", cardNo_check);
						retdata.put("resultXm", name_check);
						if(StringUtils.isNotEmpty(image)){
							logger.info("{} 照片上传征信存储开始...", prefix);
							String fpath = fileService.upload(image, FileType.JPG, FileArea.DS,trade_id);
							logger.info("{}照片上传存储成功,照片id为：{}", new String[] { prefix,fpath});
							nciic_check.setImage_file(fpath);
							fileId = fpath;
							retdata.put("xp_content", image);
							retdata.put("xp_id", fileId);
							rets.clear();
							rets.put(Conts.KEY_RET_DATA, retdata);
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
							rets.put(Conts.KEY_RET_MSG, "采集成功!");
						}else{
							logger.warn("{}新颜数据源厂商返回申请人户籍照片不存在", new String[] { prefix});
							if(channelNo.equals(CHANNEL_NO)){//判断是否走无照片输出通道
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
								rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
							}else{
								retdata.put("xp_content", "");
								retdata.put("xp_id", "");	
								rets.clear();
								rets.put(Conts.KEY_RET_DATA, retdata);
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
								rets.put(Conts.KEY_RET_MSG, "采集成功!");
							}
							nciic_check.setError_mesg(POLICE_PHOTO_NO);
						}	
						nciic_check.setStatus(STATUS_CHECK_EQUAL);
						nciic_check.setCard_check(cardNo_check);
						nciic_check.setName_check(name_check);
						nciicCheckService.add(nciic_check);
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
						return rets;						
					}else if("1".equals(data_obj.getString("code"))){
						logObj.setBiz_code1(CODE_NOEQUAL);
						initTag = Conts.TAG_UNMATCH;
						cardNo_check = POLICE_STATUS_SUSSES;
						name_check = POLICE_STATUS_FAIL;
						nciic_check.setCard_check(cardNo_check);
						nciic_check.setName_check(name_check);
						nciic_check.setStatus(STATUS_CHECK_NO);
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
						nciicCheckService.add(nciic_check);
						return rets;
					}else if("2".equals(data_obj.getString("code"))){
						logObj.setBiz_code1(CODE_NOEXIST);
						initTag = Conts.TAG_UNFOUND;
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "公安库中无此号");
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
						return rets;
					}else{
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+data_obj.getString("code"));
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
						return rets;
					}
				}else{
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
					logObj.setBiz_code1(result_obj.getString("errorCode"));
					logObj.setState_msg(result_obj.getString("errorMsg"));
					logger.error(prefix+" 公安核查调用失败 :{}",result_obj.getString("errorMsg"));
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+result_obj.getString("errorMsg"));
					rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
					return rets;
				}
			}else{
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setBiz_code1(CODE_EQUAL);
				initTag = Conts.TAG_INCACHE_MATCH;
				logger.info("{}缓存数据中存在此公安查询数据,开始新颜渠道查询！", new String[] { prefix});
				Map<String,Object> getResultMap = nciicCheckService.inCached(name, cardNo_ency);
				String photoId = "";
				if(getResultMap.get("CARD_CHECK") != null){
					cardNo_check = getResultMap.get("CARD_CHECK").toString();
				}
				if(getResultMap.get("NAME_CHECK") != null){
					name_check  = getResultMap.get("NAME_CHECK").toString();
				}
				if(getResultMap.get("IMAGE_FILE") != null){
					photoId = getResultMap.get("IMAGE_FILE").toString();
				}
				if("不一致".equals(cardNo_check) || 
						"不一致".equals(name_check)){
					logObj.setBiz_code1(CODE_NOEQUAL);
					initTag = Conts.TAG_INCACHE_UNMATCH;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
					return rets;
				}				
				if(StringUtils.isNotEmpty(photoId)){
					logger.info("{} 从征信存储根据图片ID获取照片开始...", prefix);
					String photoImages = fileService.download(photoId,trade_id);// 根据ID从征信存储区下载照片
					image = photoImages;
					logger.info("{} 从征信存储根据图片ID获取照片成功,照片id为 : {}", prefix,photoId);					
				}else{
					if(!channelNo.equals(CHANNEL_NO)){
						image="";
						photoId="";
					}					
				}					
				fileId = photoId;
				if(channelNo.equals(CHANNEL_NO)){
					if(image==null || "".equals(image)){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
						rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
						return rets;
					}	
				}
			}
			retdata.put("resultGmsfhm", cardNo_check);
			retdata.put("resultXm", name_check);
			retdata.put("xp_content", image);
			retdata.put("xp_id", fileId);
			rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception ex){
			initTag = Conts.TAG_SYS_ERROR;
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				initTag = Conts.TAG_SYS_TIMEOUT;
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error(prefix+" 数据源处理时异常,详细信息:", ExceptionUtil.getTrace(ex));
		}finally{
			tags.add(initTag);
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setTag(StringUtils.join(tags, ";"));
			/**记录请求*/
	 		if(MapUtils.isNotEmpty(paramForLog)){
	 			DataSourceLogEngineUtil.writeParamIn(trade_id, paramForLog,logObj);
	 		}
		    DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		}	
		return rets;
	}
}
