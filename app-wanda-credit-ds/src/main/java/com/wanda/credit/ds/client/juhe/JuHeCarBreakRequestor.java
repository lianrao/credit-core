package com.wanda.credit.ds.client.juhe;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

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
import com.wanda.credit.base.util.JSONUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ModelUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.dmpCar.DMP_carBreak;
import com.wanda.credit.ds.dao.iface.dmp.ICarBreakMain;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 256	车辆违章直连
 * @author liunan
 */
@DataSourceClass(bindingDataSourceId="ds_juhe_carillegal")
public class JuHeCarBreakRequestor extends BaseJuheDSRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(JuHeCarBreakRequestor.class);

	@Autowired
	ICarBreakMain carBreakService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 聚合车辆违章直连查询开始...",prefix);
		String url = propertyEngine.readById("ds_juhe_carquery_url");
		String car_source_switch = propertyEngine.readById("ds_juhe_carquery_carsource");//是否新能源汽车类型
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
		logObj.setState_msg("交易成功");
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
			reqparam.put("hpzl", carType);
			reqparam.put("key", propertyEngine.readById("ds_juhe_carquery_key"));
			reqparam.put("trade_id", trade_id);
			if(engineno.length()<6 || classno.length()<6){
				logObj.setIncache("1");
				logger.warn("{} 入参格式不符合要求", prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "校验不通过:传入参数不正确");
				return rets;
			}
			Map<String, Object> model_param = new HashMap<String, Object>();
			model_param.put("HPHM", hphm);
//			logger.error("{}模型入参信息:{}",prefix,JSONObject.toJSONString(model_param,true));
			model_param = ModelUtils.calculate("M_dmpCredit_car", ParamUtil.convertParams(model_param),false);
//			logger.error("{}模型返回信息:{}",prefix,JSONObject.toJSONString(model_param,true));
			int date_num = 0;
			if(model_param!=null){
				date_num = (int)Double.parseDouble(extractValueFromResult("NUM",model_param).toString());
			}
			String car_source="0";
			if(car_source_switch.contains(carType)){
				car_source="1";
				date_num=1;
			}
			List<Map<String, Object>> carList = carBreakService.queryCarCity(hphm,engineno, classno, date_num,car_source);
			logger.info("{}聚合支持城市查询信息: {}",prefix,JSONObject.toJSONString(carList));
			if(carList.size()>0){
				respJsn = carCityQuery(url,mapObjToMapStr(reqparam),carList,prefix);
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
					rets.put(Conts.KEY_RET_MSG, "查询失败:"+messStatDesc);	
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
	private String carCityQuery(String url,Map<String,String> param,List<Map<String, Object>> carList,String prefix) throws Exception{
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
			param.put("city", city_code);
			param.put("engineno", engineNo);
			param.put("classno", classNo);
			Map<String,Object> rspMap = RequestHelper.doGetRetFull(url, param, 
					new HashMap<String, String>(), true, null, "UTF-8");
			respJsn = String.valueOf(rspMap.get("res_body_str"));
			JSONObject rspData = JSONObject.parseObject(respJsn);
			String messStat = rspData.getString("error_code");
			if("0".equals(messStat) ||"203606".equals(messStat)){
				break;
			}
		}		
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
	public boolean checkPlateNumberFormat(String content) {
        String pattern = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{1}(([A-HJ-Z]{1}[A-HJ-NP-Z0-9]{5})|([A-HJ-Z]{1}(([DF]{1}[A-HJ-NP-Z0-9]{1}[0-9]{4})|([0-9]{5}[DF]{1})))|([A-HJ-Z]{1}[A-D0-9]{1}[0-9]{3}警)))|([0-9]{6}使)|((([沪粤川云桂鄂陕蒙藏黑辽渝]{1}A)|鲁B|闽D|蒙E|蒙H)[0-9]{4}领)|(WJ[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼·•]{1}[0-9]{4}[TDSHBXJ0-9]{1})|([VKHBSLJNGCE]{1}[A-DJ-PR-TVY]{1}[0-9]{5})";
        return Pattern.matches(pattern, content);
    }
}
