/**   
* @Description: 数据源-启信宝-根据人名关键字或者企业注册号获取企业列表 
* @author xiaobin.hou  
* @date 2019年4月3日 下午2:03:47 
* @version V1.0   
*/
package com.wanda.credit.ds.client.qixinbao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.JsonFilter;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.qixinbao.bean.EnterListData;
import com.wanda.credit.ds.client.qixinbao.bean.EnterListItem;
import com.wanda.credit.ds.client.qixinbao.bean.EnterListRes;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author nan.liu 
 *
 */
@DataSourceClass(bindingDataSourceId="ds_qxb_enternameList")
public class QXBNameSearchListDSRequestor extends BaseQXBDataSourceRequestor
		implements IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(QXBNameSearchListDSRequestor.class);
	
	private final static String RES_CODE = "res_code";
	private final static String RES_MSG = "res_msg";
	private final static String RETDATA_TOTAL = "total";
	private final static String RETDATA_NUM = "num";
	private final static String RETDATA_DETAIL = "detail";
	@Autowired
	public IPropertyEngine propertyEngine;
	
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;

		// 记录交易开始时间
		long startTime = System.currentTimeMillis();
		logger.info("{} 启信宝-模糊搜索匹配按名称 BEGIN：{}", prefix, DateUtil.getCurrTime());
		// 组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		// 交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		// 计费标签
		Set<String> tags = new HashSet<String>();

		try{
			//获取请求参数
			String keyWord = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String skip = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			String matchType = null;//匹配类型（可选）
			String region = null;//地区编码（可选）
			reqparam.put("keyWord", keyWord);
			reqparam.put("skip", skip);
			if(ParamUtil.findValue(ds.getParams_in(), "matchType")!=null){
				matchType = ParamUtil.findValue(ds.getParams_in(), "matchType").toString();
				reqparam.put("matchType", matchType);
			}
			if(ParamUtil.findValue(ds.getParams_in(), "region")!=null){
				region = ParamUtil.findValue(ds.getParams_in(), "region").toString();
				reqparam.put("region", region);
			}
			//校验请求参数
			if (!StringUtil.isPositiveInt(skip)) {
				logger.info("{} 参数skip不合法 输入值为 {}" , skip);
				retData.clear();
				retData.put(RES_CODE, "0005");
				retData.put(RES_MSG, "参数skip不合法，请输入为非负整数");
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_SUCCESS.getRet_msg());
				rets.put(Conts.KEY_RET_DATA, retData);
				return rets;
			}
			int keywordLen = keyWord.length();
			if (keywordLen > 100 || keywordLen <= 0) {
				retData.clear();
				retData.put(RES_CODE, "0004");
				retData.put(RES_MSG, "关键字至少一个字最长不超过100个字");
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_SUCCESS.getRet_msg());
				rets.put(Conts.KEY_RET_DATA, retData);
				return rets;
			}
			
			//获取配置参数
			String reqUrl = propertyEngine.readById("qxb_enterList_name_url");
			String appKey = propertyEngine.readById("qxb_enterList_appkey");
		
			logObj.setReq_url(reqUrl);
			//请求启信宝获取数据
			Map<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("keyword", keyWord);
			reqParams.put("appkey", appKey);
			reqParams.put("skip", skip);
			if(!StringUtil.isEmpty(region)){
				reqParams.put("region", region);
			}
			if(!StringUtil.isEmpty(matchType)){
				reqParams.put("matchType", matchType);
			}
			String httpGetRes = RequestHelper.doGet(reqUrl, reqParams, true);
			
			if (StringUtil.isEmpty(httpGetRes)) {
				logger.info("{} http请求启信宝返回内容为空：{}" , prefix , httpGetRes);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				return rets;
			}
			
			//数据处理
			EnterListRes resObj = JSONObject.parseObject(httpGetRes, EnterListRes.class);
			
			String status = resObj.getStatus();
			if (StringUtil.isEmpty(status) || !StringUtil.isNumeric(status)) {
				logger.info("{} 数据源返回信息中status异常：{}" , prefix , status);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
				return rets;
			}
			
			int statusInt = Integer.parseInt(status);
			
			if (200 != statusInt) {
				logger.info("{} 数据源返回内容为：{}" , prefix , httpGetRes);
			}
			
			//记录日志信息表
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			logObj.setState_msg("交易成功");
			logObj.setBiz_code3(status);
			
			switch (statusInt) {
			case 200://查询成功
				retData.clear();
				retData.put(RES_CODE, "0000");
				retData.put(RES_MSG, "查询成功");
				//解析返回数据输出
				retData = parse2Out(resObj,retData,prefix);
				//标签处理
				tags.clear();
				tags.add(Conts.TAG_FOUND);
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_SUCCESS.getRet_msg());
				rets.put(Conts.KEY_RET_DATA, retData);
				break;
			case 201://查询无结果
				//标签处理
				tags.clear();
				tags.add(Conts.TAG_UNFOUND);
				
				retData.clear();
				retData.put(RES_CODE, "0001");
				retData.put(RES_MSG, "查询无结果");
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_SUCCESS.getRet_msg());
				rets.put(Conts.KEY_RET_DATA, retData);
				break;
			case 205://超过最大查询数量
				logger.info("{} 数据源返回超过最大查询量" , prefix);
				
				//产品表示该结果返回失败
				retData.clear();
				retData.put(RES_CODE, "0002");
				retData.put(RES_MSG, "超过最大查询数量");
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_SUCCESS.getRet_msg());
				rets.put(Conts.KEY_RET_DATA, retData);
				
				break;
			case 206://跳过数量超过最大数
				retData.clear();
				retData.put(RES_CODE, "0003");
				retData.put(RES_MSG, "跳过条数超过当前查询到的最大数");	
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_SUCCESS.getRet_msg());
				rets.put(Conts.KEY_RET_DATA, retData);
				break;
			case 207://查询错误，请联系技术人员
				logger.info("{} 数据源返回信息为：{}" , prefix , httpGetRes);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());
			break;
			case 209://接口查询异常，请联系技术人员
				logger.info("{} 数据源返回信息为：{}" , prefix , httpGetRes);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());				
			break;
			default://其他异常返回码
				break;
			}
			
		}catch(Exception e){
			logger.error("{} 模糊搜索匹配企业信息异常：{}" , prefix , e.getMessage());
		}finally{
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[tags.size()]));
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(StringUtils.join(tags, ";"));
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		
		logger.info("{}  启信宝-模糊搜索匹配企业信息 END,耗时：{}" ,prefix , System.currentTimeMillis() - startTime);
		
		return rets;
	}

	/**
	 * 解析数据源返回的数据并输出
	 * @param resObj
	 * @param retData
	 * @param prefix
	 * @return
	 */
	private TreeMap<String, Object> parse2Out(EnterListRes resObj,
			TreeMap<String, Object> retData, String prefix) {
		
		String dataStr = resObj.getData();
		EnterListData data = JSONObject.parseObject(dataStr, EnterListData.class);
		List<EnterListItem> items = data.getItems();
		retData.put(RETDATA_TOTAL, data.getTotal());
		retData.put(RETDATA_NUM, data.getNum());
		
		ValueFilter filter = new ValueFilter() {
	        @Override
	        public Object process(Object obj, String s, Object v) {
	            if (v == null)
	                return "";
	            return v;
	        }
	    };
	    String data_json = JSONObject.toJSONString(items, filter,
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteNullStringAsEmpty);
	    JSONArray json = JSONObject.parseArray(data_json);
	    JSONArray arr = new JSONArray();
	    for(Object tmp:json){
	    	JSONObject tmp1 = (JSONObject) tmp;
	    	arr.add(JsonFilter.getJsonKeys(tmp1, "id"));
	    }
		retData.put(RETDATA_DETAIL, arr);
		return retData;
	}
}
