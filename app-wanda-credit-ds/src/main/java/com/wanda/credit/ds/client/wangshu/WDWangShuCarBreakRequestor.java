package com.wanda.credit.ds.client.wangshu;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import oracle.net.aso.r;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.JSONUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ModelUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.dmpCar.DMP_carBreak;
import com.wanda.credit.ds.dao.iface.dmp.ICarBreakMain;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_wangshu_carillegal")
public class WDWangShuCarBreakRequestor extends BaseWDWangShuDataSourceRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(WDWangShuCarBreakRequestor.class);
	@Autowired
	WDWangShuTokenService tokenService;
	@Autowired
	ICarBreakMain carBreakService;
	@Autowired
	WDWangShuCarCityRequestor supportCityService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} dmp车辆违章查询开始...",prefix);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
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
			Map<String, Object> model_param = new HashMap<String, Object>();
			model_param.put("HPHM", hphm);
//			logger.error("{}模型入参信息:{}",prefix,JSONObject.toJSONString(model_param,true));
			model_param = ModelUtils.calculate("M_dmpCredit_car", ParamUtil.convertParams(model_param),false);
//			logger.error("{}模型返回信息:{}",prefix,JSONObject.toJSONString(model_param,true));
			int date_num = 0;
			if(model_param!=null){
				date_num = (int)Double.parseDouble(extractValueFromResult("NUM",model_param).toString());
			}
			List<Map<String, Object>> carList = carBreakService.queryCarCity(hphm,engineno, classno, date_num,"1");
			logger.info("{}dmp支持城市查询信息: {}",prefix,JSONObject.toJSONString(carList));
			if(carList.size()>0){
				respJsn = carCityQuery("&hphm="+hphm,carList,prefix);
			}else{
				resource_tag = Conts.TAG_TST_FAIL;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源返回异常!");	
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
//			respJsn = "{\"code\":2001,\"msg\":\"查询成功\",\"data\":{\"province\":\"SH\",\"city\":\"SH\",\"hphm\":\"沪J91167\",\"hpzl\":\"02\",\"lists\":[{\"date\":\"2015-08-17 08:08:00\",\"area\":\"浦建路/环龙路（东向西，公交专用车道）\",\"act\":\"违反规定使用专用车道的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"50\",\"handled\":\"1\"},{\"date\":\"2015-08-14 08:05:00\",\"area\":\"浦建路/环龙路（东向西，公交专用车道）\",\"act\":\"违反规定使用专用车道的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"50\",\"handled\":\"1\"},{\"date\":\"2015-04-06 07:16:00\",\"area\":\"潍坊路/崂山路（东向西，公交专用车道）\",\"act\":\"违反规定使用专用车道的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"50\",\"handled\":\"1\"},{\"date\":\"2012-08-01 10:36:00\",\"area\":\"梅川路进真北路西约50米\",\"act\":\"在禁止停放和临时停放机动车的地点停车，驾驶人不在现场或虽在现场但拒绝立即驶离，妨碍其他车辆行人通行的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"200\",\"handled\":\"1\"},{\"date\":\"2011-04-20 21:14:00\",\"area\":\"永吉路81弄\",\"act\":\"机动车违反规定停放、临时停车，妨碍其它车辆、行人通行，驾驶人不在现场的\",\"code\":\"\",\"fen\":\"0\",\"money\":\"200\",\"handled\":\"1\"}]},\"seq\":\"39d5c50089c94460a049fb9eab1b7bf9\"}";
				        
	        if(StringUtils.isBlank(respJsn)){
	        	resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_msg("网数车辆违章查询失败：返回结果内容为空");
				logger.error("{} 网数车辆违章查询失败：返回结果内容为空!",prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源返回异常!");	
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
//				logger.error("{}请求dmp返回信息: {}",prefix,respJsn);
				logger.info("{} 网数车辆违章查询成功,开始解析...",prefix);
				net.sf.json.JSONObject rsponseNew = (net.sf.json.JSONObject) net.sf.json.JSONObject.fromObject(respJsn);
				String messStat = rsponseNew.get("code").toString();
				String messStatDesc = rsponseNew.getString("msg");
				logObj.setBiz_code2(messStat);
				logObj.setState_msg(messStatDesc);
				if("2001".equals(messStat)){
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
					
				}else if("225605".equals(messStat) || "225603".equals(messStat) || "225604".equals(messStat)){	
					synchronized (this){
						logger.info("{} 网数车辆支持城市重新查询开始...",prefix);
						DataSource ds1 = new DataSource();
						ds1.setId("ds_wangshu_carCity");
						ds1.setName("车辆支持城市查询");
						Map<String, Object> mapCity = supportCityService.request(trade_id, ds);
						if("STATUS_SUCCESS".equals(mapCity.get("retstatus").toString())){
							logger.info("{} 网数车辆支持城市数据源调用成功!",prefix);
							carBreakService.saveCarCity(trade_id);
						}
						logger.info("{} 网数重新查询车辆违章信息!",prefix);
						respJsn = carCityQuery("&hphm="+hphm,carList,prefix);
						net.sf.json.JSONObject rsponseNew01 = (net.sf.json.JSONObject) net.sf.json.JSONObject.fromObject(respJsn);
						String messStat01 = rsponseNew01.get("code").toString();
						String messStatDesc01 = rsponseNew01.getString("msg");
						logObj.setBiz_code2(messStat01);
						logObj.setState_msg(messStatDesc01);
						if("2001".equals(messStat01)){
							logObj.setState_code("00");
							logObj.setState_msg("交易成功");
							treatResult = "1";
							rets = rspCarMap(messStatDesc01,hphm,engineno,carType,classno,
									trade_id,rsponseNew01);
						}else{
							resource_tag = Conts.TAG_TST_FAIL;
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, "数据源返回异常!");	
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						}
					}
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
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex);
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
	private Map<String,Object> doRequest(String params, String prefix,boolean forceRefreshToken) throws Exception {
		String url = propertyEngine.readById("wdwangshu_carurl");
		url = url+"sweizhang_query?"+params;
		Map<String,Object> header = new HashMap<String,Object>();
		if(forceRefreshToken){
			logger.info("{} 强制刷新token",prefix);
			tokenService.setToken(tokenService.getNewToken());
			logger.info("{} 强制刷新token结束",prefix);
		}else if(tokenService.getToken() == null){
			logger.info("{} 发起token请求",prefix);
			tokenService.setToken(tokenService.getNewToken());
			logger.info("{} 发起token请求结束",prefix);
		}
		header.put("X-Access-Token",tokenService.getToken());
		logger.info("{} 发起网数身份验证请求",prefix);
		Map<String,Object> rspMap = doGetForHttpAndHttps(url,prefix,header,null);		
		logger.info("{} 发起网数身份验证请求结束",prefix);
		return rspMap;
	}
	private String paramConn(String engineNo,String classa){
		String retult = "";
		if(!StringUtil.isEmpty(engineNo)){
			retult = retult+"&engineno="+engineNo;
		}
		if(!StringUtil.isEmpty(classa)){
			retult = retult+"&classno="+classa;
		}
		return retult;
	}
	/**
	 * 根据车牌号规则查询违章信息
	 * */
	private String carCityQuery(String param,List<Map<String, Object>> carList,String prefix) throws Exception{
//		logger.error("{}sql返回信息: {}",prefix,JSONObject.toJSONString(carList,true));
		String respJsn = "";
		String engineNo = "";
		String classNo = "";
		String city_code = "";
		for(Map<String, Object> map:carList){
			engineNo = map.get("ENGINE").toString();
			classNo = map.get("CLASSA").toString();
			city_code = map.get("CITY_CODE").toString();
			if("|".equals(engineNo)){//不需要发动机号
				engineNo = "";
			}
			if("|".equals(classNo)){//不需要车架号
				classNo = "";
			}
			if(":".equals(engineNo) || ":".equals(classNo)){
				continue;
			}
			logger.info("{} 请求城市code为:{}",prefix,city_code);
			String params = "city="+city_code+param+paramConn(engineNo,classNo);//请求参数拼装
			Map<String,Object> rspMap = doRequest(params,prefix,false);
			/**check token valid*/
			int httpstatus = (Integer)rspMap.get("httpstatus");
			respJsn = (String)rspMap.get("rspstr");
			JSONObject rsponse = (JSONObject) JSONObject.parse(respJsn);
			if(!rsponse.containsKey("code")){
				logger.error("{} 数据源返回异常,接收到不合法的数据格式:{}",prefix,respJsn);
			    throw new RuntimeException("数据源返回不合法的数据格式");
			}
			String rspcode = rsponse.get("code").toString();
	        if(httpstatus == 401  || "401".equals(rspcode) || "1101".equals(rspcode)){
	        	logger.error("{} 厂商返回token失效：{},重新发起请求",prefix,rsponse.get("msg"));
	        	rspMap = doRequest(params,prefix,true);
	        	respJsn = (String)rspMap.get("rspstr");
	        }
	        net.sf.json.JSONObject rsponseNew = (net.sf.json.JSONObject) net.sf.json.JSONObject.fromObject(respJsn);
			String messStat = rsponseNew.get("code").toString();
			if("2001".equals(messStat)){
				break;
			}
		}		
        return respJsn;
	}
	private Map<String,Object> rspCarMap(String messStatDesc,String hphm,String engineno,String carType,String classno,
			String trade_id,net.sf.json.JSONObject rsponseNew) throws ServiceException{
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		String treatResult = "1";
		String resource_tag = "";
		net.sf.json.JSONObject rsponseData = (net.sf.json.JSONObject) net.sf.json.JSONObject.fromObject(rsponseNew.getString("data"));
		List<DMP_carBreak> cardetail = new ArrayList<DMP_carBreak>();
		JSONArray cardetailJson = (JSONArray) JSONUtil.getJsonValueByKey(rsponseData,
				"lists", true);
		JSONArray carJsonArray = new JSONArray();
		if (cardetailJson != null && cardetailJson.size() > 0) {
			resource_tag = Conts.TAG_FOUND;
			for(Object objs : cardetailJson){
				DMP_carBreak cardetails = (DMP_carBreak) JSONUtil.convertJson2Object(
						(net.sf.json.JSONObject) objs, DMP_carBreak.class);
				if (cardetails == null)
					continue;
				cardetails.setTrade_id(trade_id);
				cardetails.setSeq(rsponseNew.get("seq")!=null?rsponseNew.getString("seq"):"");
				cardetails.setMsg(messStatDesc);
				cardetails.setProvince(rsponseData.get("province")!=null?rsponseData.getString("province"):"");
				cardetails.setHphm(hphm);
				cardetails.setEngineno(engineno);
				cardetails.setCity(rsponseData.get("city")!=null?rsponseData.getString("city"):"");
				cardetails.setHpzl(rsponseData.get("hpzl")!=null?rsponseData.getString("hpzl"):"");
				cardetail.add(cardetails);
				net.sf.json.JSONObject newJsonObj = new net.sf.json.JSONObject();
				newJsonObj.put("location", cardetails.getArea());
				newJsonObj.put("reason", cardetails.getAct());
				newJsonObj.put("penalty", cardetails.getMoney());
				newJsonObj.put("status", cardetails.getHandled());
				newJsonObj.put("penaltyPoint", cardetails.getFen());
				newJsonObj.put("code", cardetails.getCode());
				newJsonObj.put("time", cardetails.getDate());
				carJsonArray.add(newJsonObj);
			}
			carBreakService.add(cardetail);
		}else{
			treatResult = "2";
			resource_tag = Conts.TAG_UNFOUND;
		}
		logger.info("{} 网数车辆违章解析成功!",trade_id);
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
}
