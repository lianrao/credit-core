package com.wanda.credit.ds.client.dianwei;
/**
 * 点微车辆信息简版-8423
 * */
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dianwei.beans.DianWei_car_check;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_dianwei_carSearch")
public class DWeiCarDescRequestor extends BaseDWeiRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(DWeiCarDescRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 点微车辆信息简版数据源调用开始...", prefix);
		Map<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String dianwei_url = propertyEngine.readById("ds_dianwei_url");//远鉴调用连接
		String channelId = propertyEngine.readById("ds_dianwei_channelId");//channelId
		String enkey = propertyEngine.readById("ds_dianwei_enkey");//enkey

		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String carNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //车牌号
			String carType = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//车牌类型
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			logger.info("{} 点微车辆信息简版数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("carNo", carNo);
			reqparam.put("carType", carType);
			logObj.setReq_url(dianwei_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
				return rets;
			}
			logObj.setIncache("0");
			Map<String, String> predata = new HashMap<String, String>();
			predata.put("productCode", "8423");
		    predata.put("carNo", carNo);
		    predata.put("name", name);
		    predata.put("flapperType", carType);
		    predata.put("subChannelName", "格兰德信息技术有限公司");
		    predata.put("channelOrderId", trade_id);
		   
			String res = getDWeiResp(trade_id,predata,enkey,channelId,dianwei_url);
			if(StringUtil.isEmpty(res)){
				logger.error("{} 驾驶证查询返回异常！", prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "车辆信息查询失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logObj.setState_msg("车辆信息查询返回异常");
				return rets;
			}
			JSONObject car_desc = JSONObject.parseObject(res);
			if("C0".equals(car_desc.getString("code"))){
				JSONObject car_data = car_desc.getJSONObject("data");
				if("0".equals(car_data.getString("respCode"))){
					resource_tag = Conts.TAG_TST_SUCCESS;
					JSONObject car_detail = car_data.getJSONObject("detail");
					retdata.put("result", car_detail.getJSONArray("carDetail"));//性别
				}else if("1".equals(car_data.getString("respCode"))){
					resource_tag = Conts.TAG_UNMATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "姓名不一致");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					return rets;
				}else if("28".equals(car_data.getString("respCode"))){
					resource_tag = Conts.TAG_UNMATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_DWEI_CAR01);
					rets.put(Conts.KEY_RET_MSG, "车牌号未命中");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					return rets;
				}else if("36".equals(car_data.getString("respCode"))){
					resource_tag = Conts.TAG_UNMATCH;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_DWEI_CAR02);
					rets.put(Conts.KEY_RET_MSG, "号牌种类不一致");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					return rets;
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "车辆信息查询失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_msg("驾驶证查询返回异常");
					return rets;
				}
			}else if("C7".equals(car_desc.getString("code"))){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式错误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logObj.setState_msg("驾驶证查询返回异常");
				return rets;
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "车辆信息查询失败");
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
