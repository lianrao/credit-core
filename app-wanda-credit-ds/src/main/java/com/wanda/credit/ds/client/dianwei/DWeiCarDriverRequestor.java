package com.wanda.credit.ds.client.dianwei;
/**
 * 点微驾驶证-8303
 * */
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dianwei.beans.DianWei_car_check;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_dianwei_driver")
public class DWeiCarDriverRequestor extends BaseDWeiRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(DWeiCarDriverRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 点微驾驶证数据源调用开始...", prefix);
		Map<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String dianwei_url = propertyEngine.readById("ds_dianwei_url");//远鉴调用连接
		String channelId = propertyEngine.readById("ds_dianwei_channelId");//channelId
		String enkey = propertyEngine.readById("ds_dianwei_enkey");//enkey
		String version = propertyEngine.readById("ds_dianwei_remote_version");//enkey

		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码

			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			logger.info("{} 点微驾驶证数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);		
			logObj.setReq_url(dianwei_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}		
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
				return rets;
			}
			logObj.setIncache("0");
			Map<String, String> predata = new HashMap<String, String>();
			predata.put("productCode", "8303");
		    predata.put("cid", cardNo);
		    predata.put("name", name);
		    predata.put("subChannelName", "格兰德信息技术有限公司");
		    predata.put("channelOrderId", trade_id);
		    predata.put("version", version);
			String res = getDWeiResp(trade_id,predata,enkey,channelId,dianwei_url);
			if(StringUtil.isEmpty(res)){
				logger.error("{} 驾驶证查询返回异常！", prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "驾驶证查询失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logObj.setState_msg("驾驶证查询返回异常");
				return rets;
			}
			DianWei_car_check car_check = com.alibaba.fastjson.JSONObject.parseObject(res,DianWei_car_check.class);
			if("C0".equals(car_check.getCode())){
				if("0".equals(car_check.getData().getRespCode())){
					resource_tag = Conts.TAG_TST_SUCCESS;
					String allowDrive = "";
					String status1 = "";
					String gender1 = "";
					String allowDriveCar = car_check.getData().getDetail().getAllowDriveCar();
					String status = car_check.getData().getDetail().getDriveCardStatus();
					String gender = car_check.getData().getDetail().getGender();
					if(!StringUtil.isEmpty(allowDriveCar)){
						String[] allow = allowDriveCar.split("/");
						if(allow.length>1){
							allowDrive = allow[0];
						}
					}
					if(!StringUtil.isEmpty(status)){
						String[] status2 = status.split("/");
						if(status2.length>1){
							status1 = status2[0];
						}
					}
					if(!StringUtil.isEmpty(gender)){
						String[] gender2 = gender.split("/");
						if(gender2.length>1){
							gender1 = gender2[0];
						}
					}
					retdata.put("allowDriveCar", allowDrive);//准驾车型
					retdata.put("firstIssueDate", car_check.getData().getDetail().getFirstIssueDate());//初次领证日期
					retdata.put("expiryDate", car_check.getData().getDetail().getValidDate());//驾驶证有效期止
					retdata.put("startDate", car_check.getData().getDetail().getDriveValidStartDate());//驾驶证有效期开始
					retdata.put("status", status1);//驾驶证状态
					retdata.put("driveIssueDate", car_check.getData().getDetail().getDriveIssueDate());//驾驶证发证日期
					retdata.put("driveLicenseType", car_check.getData().getDetail().getDriveLicenseType());//驾驶证种类
					retdata.put("gender", gender1);//性别
				}else if("1".equals(car_check.getData().getRespCode())){
					resource_tag = Conts.TAG_UNMATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					return rets;
				}else if("25".equals(car_check.getData().getRespCode())){
					resource_tag = Conts.TAG_UNMATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_DWEI_CAR01);
					rets.put(Conts.KEY_RET_MSG, "身份证号未命中");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					return rets;
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "驾驶证查询失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_msg("驾驶证查询返回异常");
					return rets;
				}
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "驾驶证查询失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logObj.setState_msg("驾驶证查询返回异常");
				return rets;
			}
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
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

			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}
}
