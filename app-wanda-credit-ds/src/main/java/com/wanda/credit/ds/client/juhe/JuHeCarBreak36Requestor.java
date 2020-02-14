package com.wanda.credit.ds.client.juhe;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.dmpCar.DMP_carBreak;
import com.wanda.credit.ds.dao.iface.dmp.ICarBreakMain;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/*
 * 聚合车辆违章-非直连 36
 * */
@DataSourceClass(bindingDataSourceId="ds_juhe_carillegal36")
public class JuHeCarBreak36Requestor extends BaseJuheDSRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(JuHeCarBreak36Requestor.class);

	@Autowired
	ICarBreakMain carBreakService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 聚合车辆违章查询开始...",prefix);
		String url = propertyEngine.readById("ds_juhe_carquerywz_url");
		
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String respJsn = "";
		String treatResult = "3";//是否查得
		String resource_tag = Conts.TAG_UNFOUND_OTHERS;
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setDs_id(ds.getId());
		logObj.setState_code("01");
		logObj.setState_msg("交易失败");
		logObj.setIncache("0");
		logObj.setReq_url(url);
		try{
			String hphm = ParamUtil.findValue(ds.getParams_in(), "carNumber").toString();//车牌号
			String engineno = "";//发动机号
			if(ParamUtil.findValue(ds.getParams_in(), "carEngine")!=null){
				engineno = ParamUtil.findValue(ds.getParams_in(), "carEngine").toString();
			}
			String classno = "";//车架号
			if(ParamUtil.findValue(ds.getParams_in(), "carCode")!=null){
				classno = ParamUtil.findValue(ds.getParams_in(), "carCode").toString();
			}
			String carType = ParamUtil.findValue(ds.getParams_in(), "carType").toString();// 车辆类型（大型车 小型车）
			reqparam.put("hphm", hphm);
			reqparam.put("engineno", engineno);
			reqparam.put("classno", classno);
			reqparam.put("hpzl", carType);
			reqparam.put("key", propertyEngine.readById("ds_juhe_carquerywz_key"));
			reqparam.put("trade_id", trade_id);
			logger.info("{} 车辆违章支持城市查询开始...");
			Map<String,Object> city_map = RequestHelper.doGetRetFull(propertyEngine.readById("ds_juhe_carquerywz_city_url"), mapObjToMapStr(reqparam), 
					new HashMap<String, String>(), true, null, "UTF-8");
			String city_str = String.valueOf(city_map.get("res_body_str"));
			logger.info("{}聚合支持城市查询信息: {}",prefix,city_str);
			JSONObject city_json = JSONObject.parseObject(city_str);
//			respJsn = "{\"code\":2001,\"msg\":\"查询成功\",\"data\":{\"province\":\"SH\",\"city\":\"SH\",\"hphm\":\"沪J91167\",\"hpzl\":\"02\",\"lists\":[{\"date\":\"2015-08-17 08:08:00\",\"area\":\"浦建路/环龙路（东向西，公交专用车道）\",\"act\":\"违反规定使用专用车道的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"50\",\"handled\":\"1\"},{\"date\":\"2015-08-14 08:05:00\",\"area\":\"浦建路/环龙路（东向西，公交专用车道）\",\"act\":\"违反规定使用专用车道的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"50\",\"handled\":\"1\"},{\"date\":\"2015-04-06 07:16:00\",\"area\":\"潍坊路/崂山路（东向西，公交专用车道）\",\"act\":\"违反规定使用专用车道的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"50\",\"handled\":\"1\"},{\"date\":\"2012-08-01 10:36:00\",\"area\":\"梅川路进真北路西约50米\",\"act\":\"在禁止停放和临时停放机动车的地点停车，驾驶人不在现场或虽在现场但拒绝立即驶离，妨碍其他车辆行人通行的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"200\",\"handled\":\"1\"},{\"date\":\"2011-04-20 21:14:00\",\"area\":\"永吉路81弄\",\"act\":\"机动车违反规定停放、临时停车，妨碍其它车辆、行人通行，驾驶人不在现场的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"200\",\"handled\":\"1\"}]},\"seq\":\"39d5c50089c94460a049fb9eab1b7bf9\"}";
		    if("0".equals(city_json.getString("error_code"))){
		    	JSONObject city_sub = city_json.getJSONObject("result");
		    	respJsn = carCityQuery(url,city_sub,mapObjToMapStr(reqparam),prefix,engineno,classno);
		    }
	        if(StringUtils.isBlank(respJsn) || "1".equals(respJsn) || "2".equals(respJsn)){
	        	resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_msg("聚合车辆违章查询失败：返回结果内容为空");
				logger.error("{} 聚合车辆违章查询失败：返回结果内容为空!",prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数不正确!");	
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				logger.info("{} 聚合车辆违章查询成功,返回信息:{}",prefix,respJsn);
				JSONObject rsponseNew = JSONObject.parseObject(respJsn);
				String messStat = rsponseNew.get("error_code").toString();
				String messStatDesc = rsponseNew.getString("reason");
				logObj.setBiz_code2(messStat);
				logObj.setState_msg(messStatDesc);
				if("0".equals(messStat)){
					logObj.setState_code("00");
					logObj.setState_msg("交易成功");
					treatResult = "1";
					rets = rspCarMap(messStatDesc,hphm,engineno,carType,classno,
							trade_id,rsponseNew);
					TreeMap trmap =  (TreeMap) rets.get("retdata");
					JSONArray jnArray = (JSONArray) trmap.get("carIllegals");
					
					if (jnArray.size()==0) {
						resource_tag = Conts.TAG_UNFOUND;
					}else {
						resource_tag = Conts.TAG_FOUND;
					}					
				}else if("225606".equals(messStat)){
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_NORECORD01);
					rets.put(Conts.KEY_RET_MSG, "车辆信息错误,请输入正确车辆信息");	
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else if("225607".equals(messStat)){
					resource_tag = Conts.TAG_TST_FAIL;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_NORECORD02);
					rets.put(Conts.KEY_RET_MSG, messStatDesc);	
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else{
					resource_tag = Conts.TAG_TST_FAIL;					
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源返回异常!");	
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
			}
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
			logger.error(prefix+" 数据源处理时异常：{}",ExceptionUtil.getTrace(ex));
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
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
	/**
	 * 根据车牌号规则查询违章信息
	 * */
	private String carCityQuery(String url,JSONObject carJson,Map<String,String> param,String prefix,
			String ENGINE,String CLASSNO) throws Exception{
//		logger.error("{}sql返回信息: {}",prefix,JSONObject.toJSONString(carList,true));
		String respJsn = "";
		String engineNo = "";
		String classNo = "";

		if("1".equals(carJson.getString("engine"))){//不需要发动机号
			int num = carJson.getIntValue("engineno");
			if(num==0){
				engineNo = ENGINE;
			}else{
				if(ENGINE.length()-num<0){
					return "1";
				}else{
					engineNo = ENGINE.substring(ENGINE.length()-num);
				}			
			}
		}
		if("1".equals(carJson.getString("classa"))){//不需要车架号
			int num = carJson.getIntValue("classno");
			if(num==0){
				classNo = CLASSNO;
			}else{
				if(CLASSNO.length()- num<0){
					return "2";
				}else{
					classNo = CLASSNO.substring(CLASSNO.length()- num);
				}			
			}
		}
		param.put("engineno", engineNo);
		param.put("classno", classNo);
		Map<String,Object> rspMap = RequestHelper.doGetRetFull(url, param, 
				new HashMap<String, String>(), true, null, "UTF-8");
		respJsn = String.valueOf(rspMap.get("res_body_str"));
        return respJsn;
	}
	private Map<String,Object> rspCarMap(String messStatDesc,String hphm,String engineno,String carType,String classno,
			String trade_id,JSONObject rsponseNew) throws ServiceException{
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		String treatResult = "1";
		String resource_tag = "";
		JSONObject rsponseData = JSONObject.parseObject(rsponseNew.getString("result"));
		List<DMP_carBreak> cardetail = new ArrayList<DMP_carBreak>();
		JSONArray cardetailJson = JSONObject.parseArray(rsponseData.getString("lists"));
		JSONArray carJsonArray = new JSONArray();
		if (cardetailJson != null && cardetailJson.size() > 0) {
			resource_tag = Conts.TAG_FOUND;
			for(Object objs : cardetailJson){
				DMP_carBreak cardetails =JSONObject.parseObject(JSONObject.toJSONString(objs),DMP_carBreak.class); 
				if (cardetails == null)
					continue;
				cardetails.setTrade_id(trade_id);
				cardetails.setMsg(messStatDesc);
				cardetails.setProvince(rsponseData.get("province")!=null?rsponseData.getString("province"):"");
				cardetails.setHphm(hphm);
				cardetails.setEngineno(engineno);
				cardetails.setCity(rsponseData.get("city")!=null?rsponseData.getString("city"):"");
				cardetails.setHpzl(rsponseData.get("hpzl")!=null?rsponseData.getString("hpzl"):"");
				cardetail.add(cardetails);
				JSONObject newJsonObj = new JSONObject();
				newJsonObj.put("location", cardetails.getArea());
				newJsonObj.put("reason", cardetails.getAct());
				newJsonObj.put("penalty", cardetails.getMoney());
				newJsonObj.put("status", cardetails.getHandled());
				newJsonObj.put("penaltyPoint", cardetails.getFen());
				newJsonObj.put("code", cardetails.getCode());
				newJsonObj.put("time", cardetails.getDate());
				newJsonObj.put("wzcity", cardetails.getWzcity());
				newJsonObj.put("archiveno", cardetails.getArchiveno());
				carJsonArray.add(newJsonObj);
			}
			carBreakService.add(cardetail);
		}else{
			treatResult = "2";
			resource_tag = Conts.TAG_UNFOUND;
		}
		logger.info("{} 聚合车辆违章解析成功!",trade_id);
		retdata.put("carIllegals", carJsonArray);
		retdata.put("status", treatResult);
		retdata.put("carNumber", hphm);
		retdata.put("carType", carType);
		retdata.put("carCode", classno);
		retdata.put("carEngine", engineno);
		rets.put(Conts.KEY_RET_DATA, retdata);
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_MSG, "车辆违章数据采集成功!");
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return rets;
	}
	@SuppressWarnings("unchecked")
	public Object extractValueFromResult(String key,
			Map<String, Object> params_out) {
		Object retdataObj = params_out.get(Conts.KEY_RET_DATA);
		if(retdataObj!=null){
			if(retdataObj instanceof Map){
				return ((Map<String, Object>) params_out.get(Conts.KEY_RET_DATA))
						.get(key);
			}
		}
		return null;
	}
}
