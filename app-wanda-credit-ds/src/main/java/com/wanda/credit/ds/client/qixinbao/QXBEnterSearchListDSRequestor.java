/**   
* @Description: 数据源-启信宝-根据企业名称关键字或者企业注册号获取企业列表 
* @author xiaobin.hou  
* @date 2016年12月19日 下午2:03:47 
* @version V1.0   
*/
package com.wanda.credit.ds.client.qixinbao;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.wanda.credit.base.util.JSONUtil;
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
import com.wanda.credit.ds.dao.domain.qxb.CorpListPojo;
import com.wanda.credit.ds.dao.iface.qixinbao.IQXBCorpListService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou 1.2
 *
 */
@DataSourceClass(bindingDataSourceId="ds_qxb_enterpriseList")
public class QXBEnterSearchListDSRequestor extends BaseQXBDataSourceRequestor
		implements IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(QXBEnterSearchListDSRequestor.class);
	
	private final static String RES_CODE = "res_code";
	private final static String RES_MSG = "res_msg";
	private final static String RETDATA_TOTAL = "total";
	private final static String RETDATA_NUM = "num";
	private final static String RETDATA_DETAIL = "detail";
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IQXBCorpListService corpListService;
	
	
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;

		// 记录交易开始时间
		long startTime = System.currentTimeMillis();
		logger.info("{} 启信宝-模糊搜索匹配企业信息 BEGIN：{}", prefix, DateUtil.getCurrTime());
		// 组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		// 交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		// 计费标签
		Set<String> tags = new HashSet<String>();
		//需求只保存found和unfound标签，其他不保存
//		tags.add(Conts.TAG_SYS_ERROR);
		
		try{
			//获取请求参数
			String keyWord = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String skip = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			//保存请求参数
			saveParamIn(keyWord,skip,trade_id,logObj);
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
			String reqUrl = propertyEngine.readById("qxb_enterList_url");
			String appKey = propertyEngine.readById("qxb_enterList_appkey");
			//TODO 测试
//			reqUrl = "http://api.qixin007.com/APITestService/v2/enterprise/searchListPaging";
//			appKey = "ada44bd0070711e6b8a865678b483fde";
			//TODO 生产
//			reqUrl = "http://api.qixin007.com/APIService/v2/enterprise/searchListPaging";
//			appKey = "4530d97af19e424293dc68d8734de17e";
		
			logObj.setReq_url(reqUrl);
			
			//请求启信宝获取数据
			Map<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("keyword", keyWord);
			reqParams.put("appkey", appKey);
			reqParams.put("skip", skip);
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
				//保存企业列表
				saveCorpList(resObj,keyWord,trade_id);
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
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
		}
		
		logger.info("{}  启信宝-模糊搜索匹配企业信息 END,耗时：{}" ,prefix , System.currentTimeMillis() - startTime);
		
		return rets;
	}
	
	/**
	 * @param resObj
	 * @param keyWord
	 * @param trade_id
	 * @return
	 */
	private boolean saveCorpList(EnterListRes resObj,
			String keyWord, String trade_id) {
		boolean isSuccess = false;
		try{
			String dataStr = resObj.getData();
			EnterListData data = JSONObject.parseObject(dataStr, EnterListData.class);
			
			List<EnterListItem> items = data.getItems();
			if (items != null && items.size() > 0) {
				List<CorpListPojo> corpPojoList = new ArrayList<CorpListPojo>();
				for (EnterListItem item : items) {
					CorpListPojo corpListPojo = new CorpListPojo();
					corpListPojo.setTrade_id(trade_id);
					corpListPojo.setItem_id(item.getId());
					corpListPojo.setKeyword(keyWord);
					corpListPojo.setName(item.getName());
					corpListPojo.setOper_name(item.getOper_name());
					corpListPojo.setReg_no(item.getReg_no());
					corpListPojo.setStart_date(item.getStart_date());
					corpListPojo.setCredit_no(item.getCredit_no());
					
					corpPojoList.add(corpListPojo);
				}
				
				corpListService.add(corpPojoList);
			}
			
		}catch(Exception e){
			logger.error("{} 保存企业列表异常：{}" , e.getMessage());
		}
		return isSuccess;
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

	/**
	 * 保存请求参数	
	 * @param param0
	 * @param trade_id
	 * @param prefix 
	 * @param logObj
	 * @return
	 */
	private boolean saveParamIn(String keyWord,String skip,
			String tradeId, DataSourceLogVO logObj) {
		boolean isSave = true;
		try {
			Map<String, Object> paramIn = new HashMap<String, Object>();
			paramIn.put("keyWord", keyWord);
			paramIn.put("skip", skip);
			DataSourceLogEngineUtil.writeParamIn(tradeId, paramIn, logObj);
		} catch (Exception e) {
			logger.info("{}保存入参信息异常{}", tradeId, e.getMessage());
			isSave = false;
		}

		return isSave;
	}
}
