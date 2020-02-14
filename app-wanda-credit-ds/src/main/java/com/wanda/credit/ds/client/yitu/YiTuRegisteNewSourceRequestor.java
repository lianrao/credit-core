package com.wanda.credit.ds.client.yitu;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.ds.dao.domain.YT_Regist_Result;
import com.wanda.credit.ds.dao.iface.IYTRegistService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.api.dto.DataSource;
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
@DataSourceClass(bindingDataSourceId="ds_yitu_djNew")
public class YiTuRegisteNewSourceRequestor extends BaseYiTuDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(YiTuRegisteNewSourceRequestor.class);
	private String registImgApi;
	private String yitu_saas_address; 
	private String accessId; 
	private String accessKey; 
	
	private final String INTERFACE_TYPE = "CS101";
	private final String REGIST_STATUS_SUCCESS = "0";
	private final String REGIST_STATUS_FAIL = "1";
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IYTRegistService ytRegistService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String yitu_url =  propertyEngine.readById("yitu_address01");
		//请求交易结果日志表
		Map<String,Object> paramIn = new HashMap<String,Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id("ds_yitu_djNew");//log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setReq_url(yitu_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);	//初始值-失败
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{
			rets = new HashMap<String, Object>();
	 		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String photo = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();	//照片(based64)
			String ptype = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();   //照片类型
			String transType = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();   //照片类型
			String retFlag = ParamUtil.findValue(ds.getParams_in(), paramIds[5]).toString();   //返回类型
			enCardNo = synchExecutorService.encrypt(cardNo);
			int lengthStr = cardNo.length();
			//记录入参到入参记录表			
			paramIn.put("cardNo", cardNo);
			paramIn.put("name", name);
			paramIn.put("ptype", ptype);
			paramIn.put("transType", transType);
			String sendCard = MD5.uppEncodeByMD5(cardNo+transType);
			String photoContent = "";
			boolean inCached  = ytRegistService.inCached(name, enCardNo,REGIST_STATUS_SUCCESS,transType);
			if(!inCached){
				logObj.setIncache("0");//不缓存
				logger.info("{} 从nas存储获取活体照片开始...", prefix);
				photoContent = fileService.download(photo,trade_id);
				logger.info("{} 从nas存储获取活体照片成功！", prefix);	
				YT_Regist_Result police_regist = new YT_Regist_Result();
				police_regist.setTrade_id(trade_id);
				police_regist.setCard_no(enCardNo);
				police_regist.setName(name);
				police_regist.setTrans_type(transType);
				police_regist.setInterface_type(INTERFACE_TYPE);
				logger.info("{} 图片登记UseId为 : {}", prefix,sendCard);
				Map<String, Object> params = new TreeMap<String, Object>();
				Map<String, String> headers = new TreeMap<String, String>();
				params.put("user_id", sendCard); //加密身份证号码,避免被泄露
				params.put("database_image_content", photoContent);
				params.put("database_image_type", (int)Double.parseDouble(ptype));
				if(lengthStr == 15){
					if (Integer.parseInt(cardNo.substring(14, 15)) % 2 != 0)
						params.put("gender", 1);
					else
						params.put("gender", 0);
				    params.put("born_year",Integer.parseInt("19"+cardNo.substring(6, 8)));
				}else{
					 logger.info("{} 身份要素提取开始...", prefix);
					if (Integer.parseInt(cardNo.substring(16, 17)) % 2 != 0)
						params.put("gender", 1);
					else
						params.put("gender", 0);
				    params.put("born_year",Integer.parseInt(cardNo.substring(6, 10)));
				}				
			    params.put("name", name);
			    params.put("citizen_id", cardNo);
			    params.put("nation", 1);
			    headers.put("x-access-id", accessId);
				headers.put("x-signature", generateSignature(pk,accessKey,new ObjectMapper().writeValueAsString(params),userDefinedContent));
			    logger.info("{} 开始发送照片至外部依图进行登记...", prefix);
				String res = RequestHelper.doPost(yitu_url + registImgApi, null, headers,
						params, ContentType.create("text/plain", Consts.UTF_8),false);
				Map<String, Object> respMap = new ObjectMapper().readValue(res, Map.class);// 转成map
				//log:交易状态信息,返回码保存
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, res, new String[] { trade_id }));
				logObj.setState_msg(respMap.get("message").toString());
				if(StringUtils.isNotEmpty(respMap.get("rtn").toString())){
					if("0".equals(respMap.get("rtn").toString()))
						logObj.setBiz_code1("YTBD_001");
					if("1".equals(respMap.get("rtn").toString()))
						logObj.setBiz_code1("YTBD_002");
				}else{
					logObj.setBiz_code1(respMap.get("rtn").toString());
				}
				if(respMap.get("global_request_id") != null){
					police_regist.setGlobal_request_id(respMap.get("global_request_id").toString());
				}
				police_regist.setRtn(respMap.get("rtn").toString());
				if(((Integer)respMap.get("rtn"))!=0){
					rets.clear();
					if(retFlag.equals(REGIST_STATUS_SUCCESS)){						
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_DATA,respMap);
						rets.put(Conts.KEY_RET_MSG, "照片登记失败!");							
					}else{
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU1_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "照片登记失败,返回原因:"+ respMap.get("message").toString());
					}	
					resource_tag = Conts.TAG_TST_FAIL;
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					police_regist.setStatus(REGIST_STATUS_FAIL);
					logger.error("{} 外部依图返回上传失败", prefix);
				}else{			
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//登记成功
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA,respMap);
					rets.put(Conts.KEY_RET_MSG, "照片登记成功!");
					resource_tag = Conts.TAG_TST_SUCCESS;
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					police_regist.setStatus(REGIST_STATUS_SUCCESS);
					logger.info("{} 上传成功!",prefix);
				}
				ytRegistService.add(police_regist);
			}else{
				logObj.setIncache("1");//缓存
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//登记成功
				DataSourceBizCodeVO dataSourceBizCodeVO = DataSourceLogEngineUtil.fetchBizCodeByRetCode("ds_yitu_auth", "0");
				logObj.setBiz_code1(dataSourceBizCodeVO == null?"0":dataSourceBizCodeVO.getBizCode());
				Map<String, Object> respMap1 = new HashMap<String, Object>();
				respMap1.put("message", "OK");
				respMap1.put("rtn", 0);
				
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA,respMap1);
				rets.put(Conts.KEY_RET_MSG, "照片登记成功!");
				resource_tag = Conts.TAG_TST_SUCCESS;
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}缓存存在此照片登记数据!", new String[] { prefix});
			}
			
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
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

	public String getRegistImgApi() {
		return registImgApi;
	}

	public void setRegistImgApi(String registImgApi) {
		this.registImgApi = registImgApi;
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
