package com.wanda.credit.ds.client.aijin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.yuanjin.YJ_CreditScoreVO;
import com.wanda.credit.ds.dao.iface.yuanjin.IYuanJinCreditScoreService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description  爰金风险价值评分查询(公安负面信息)
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年8月29日 下午3:18:31 
 *  
 */

@DataSourceClass(bindingDataSourceId="ds_yuanjin_creditScore")
public class CreditScoreDataSourceRequestor extends BaseAijinDataSourceRequestor
  implements IDataSourceRequestor{

	private final  Logger logger = LoggerFactory.getLogger(CreditScoreDataSourceRequestor.class);

	@Autowired
	private IYuanJinCreditScoreService creditScoreService;

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		List<String> tags = new ArrayList<String>();
		String initTag = Conts.TAG_SYS_ERROR;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(aijin_address);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		// 默认交易失败
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		Map<String, Object> params = null;
		try{
			String name = (String)ParamUtil.findValue(ds.getParams_in(), "name");
			String cardNo = (String)ParamUtil.findValue(ds.getParams_in(), "cardNo");
			/*add 身份证规则校验 20160905	Begin*/
			String valiRes = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(valiRes)) {
				logger.info("{} 身份证号码不符合规范： {}" , prefix , valiRes);
				logObj.setIncache("0");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不合法");
        		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
        		rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
                return rets;
			}
			/*add 身份证规则校验 20160905	End*/
			params = CommonUtil.sliceMap(ParamUtil.convertParams(ds.getParams_in()), new String[] { "name", "cardNo"});
			String crptedCardNo = synchExecutorService.encrypt(cardNo);
			/**得到近一年的数据*/
			YJ_CreditScoreVO cachedData = 
					getDataFromCache(trade_id,name,cardNo,crptedCardNo);
			if(cachedData != null){
				logger.info("{} 查询风险评分数据开始了 ",trade_id);
				retdata.putAll(handleCachedDataForRetrn(trade_id,name,cardNo,cachedData));
				retdata.put("found", "1");//查得
				initTag = Conts.TAG_INCACHE_FOUND;
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				rets.put(Conts.KEY_RET_DATA,retdata);
        		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
        		rets.put(Conts.KEY_RET_MSG, "采集成功!");
                return rets;
			}else{
				logObj.setIncache("0");
				String reqData = buildPostData(trade_id,name,cardNo);
				if(StringUtils.isBlank(reqData))throw new  Exception();
	            DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, reqData, new String[]{trade_id}));        
//	            String rspData = postHtml(aijin_address, reqData);
	            String rspData = doHttpsPost(trade_id,aijin_address,reqData);
	            DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, rspData, new String[]{trade_id}));
	            if(StringUtils.isNotBlank(rspData)){
	            	JSONObject rspJsnObj = JSONObject.parseObject(rspData);
	            	/**接口调用成功*/
	            	if(isCallSucss(trade_id,rspJsnObj)){
	            		if(existCreditScore(trade_id,rspJsnObj)){
	            		   logger.info("{} 开始保存本地数据",trade_id);
	            		   doSaveOper(trade_id,name,cardNo,crptedCardNo,rspJsnObj);
	            		   logger.info("{} 完成保存本地数据 ",trade_id);
	            		   Map<String,Object> retrnMap = 
	            				   handleRspObjForRetrn(trade_id,name,cardNo,rspJsnObj);
	            		   if(MapUtils.isNotEmpty(retrnMap)){
	            			   retdata.putAll(retrnMap);
	            		   }
	            		   retdata.put("found", "1");//查得
	            		   initTag = Conts.TAG_FOUND;
	            		}else{
	            			logger.warn("{} 未查得风险评分信息",trade_id);
	            			retdata.put("found", "0");//未查得
	            			initTag = Conts.TAG_UNFOUND;
	            		}          
	        			rets.put(Conts.KEY_RET_DATA,retdata);
	        			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
		        		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		        		rets.put(Conts.KEY_RET_MSG, "采集成功!");
	           			logger.info("{} yuanj风险评分数据源采集成功", new String[] { prefix });
	            	}else{
	            		logger.error("{} yuanj风险评分查询失败:{}",trade_id,rspJsnObj.get("ResponseText"));
	    				logObj.setState_msg((String)rspJsnObj.get("ResponseText"));
	    				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
	    				rets.put(Conts.KEY_RET_MSG, "风险评分数据源处理时异常!");
	    				return rets;
	            	}
	            }else{
	            	logger.error("{} yuanj返回数据格式不正确:{} ",trade_id,rspData);
    				logObj.setState_msg("数据源返回数据格式不正确:"+rspData);
	            	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
	    			rets.put(Conts.KEY_RET_MSG, "风险评分数据源处理时异常!" );
	    			return rets;
	            }
			}			
		}catch(Exception ex){
			/** 如果是超时异常 记录超时信息 */
			initTag = Conts.TAG_SYS_ERROR;
			if (CommonUtil.isTimeoutException(ex)) {
				initTag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("风险评分数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "风险评分数据源处理时异常!");
			logger.error(prefix + " 风险评分数据源处理时异常", ex);			
		} finally {
			tags.add(initTag);
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setTag(StringUtils.join(tags, ";"));
			logObj.setBiz_code1(logObj.getTag());
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			if (MapUtils.isNotEmpty(params)) {
				DataSourceLogEngineUtil.writeParamIn(trade_id, params, logObj);
			}
		}
		return rets;
	}


	/**把缓存数据转化为输出格式*/
	private Map<? extends String, ? extends Object> handleCachedDataForRetrn(
			String trade_id,String name,String cardNo, YJ_CreditScoreVO cachedData) {
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		if(cachedData != null){
			retMap.put("cardNo", cardNo);
			retMap.put("name", name);
			retMap.put("score", cachedData.getCreditScores());
			retMap.put("riskSort", obtainCreditScoresName(trade_id, 
					cachedData.getCreditScores()));	
		}
		return retMap;
	}

	/**查询12个月的缓存数据*/
	private YJ_CreditScoreVO getDataFromCache(String trade_id, String name,String cardNo,
		String crptedCardNo) {
		/**最近12个月的缓存*/
		YJ_CreditScoreVO cachedData = 
				creditScoreService.queryCached(name,cardNo,crptedCardNo,12);
	    return cachedData;
    }

	/**爰金返回数据转化成输出*/
	private Map<String, Object> handleRspObjForRetrn(String trade_id,String name,String cardNo,
			JSONObject rspJsnObj) {
		HashMap<String, Object> retMap = new HashMap<String, Object>();
//		{"CreditScores":{"Flag":0,"Score":644}
		JSONObject creditScores = rspJsnObj.getJSONObject("CreditScores");
		if(creditScores == null){
			logger.error("{} element 'CreditScores' can not be null",trade_id);
		}else if(!creditScores.containsKey("Score")){
			logger.error("{} don't find Property 'Score'",trade_id);
		}else{			
			String score_str = convertObjToStr(creditScores.get("Score"));
			retMap.put("cardNo", cardNo);
			retMap.put("name", name);
			retMap.put("score", score_str);	
			retMap.put("riskSort", obtainCreditScoresName(trade_id, 
					score_str));
			
		}
		return retMap;
	}

	private String convertObjToStr(Object object) {
		if(object == null)return null;
		else return object.toString();
	}

	/**数据保存(之前必须做数据校验)*/
	private YJ_CreditScoreVO doSaveOper(String trade_id,String name,
			String cardNo,String crptedCardNo,JSONObject rspJsnObj) {
//		ResponseCode":100,"ResponseText":"接口调用成功","Result":1,"ResultText
		YJ_CreditScoreVO vo = new YJ_CreditScoreVO();
		vo.setTrade_id(trade_id);
		vo.setName(name);vo.setCardNo(crptedCardNo);
		JSONObject creditScores = rspJsnObj.getJSONObject("CreditScores");
		vo.setCreditScores(convertObjToStr(creditScores.get("Score")));		
		vo.setResponseCode(convertObjToStr(rspJsnObj.get("ResponseCode")));
		vo.setResponseText(rspJsnObj.getString("ResponseText"));
		vo.setResult(convertObjToStr(rspJsnObj.get("Result")));
		vo.setResultText(rspJsnObj.getString("ResultText"));
		vo.setCreditScoresName(obtainCreditScoresName(trade_id,vo.getCreditScores()));
		creditScoreService.save(vo);
		return vo;
	}
	
	 private String obtainCreditScoresName(String trade_id, String creditScores) {
		if(StringUtils.isNotBlank(creditScores)){
			String head = creditScores.substring(0, 1);
			//1=犯罪嫌疑人;2=吸毒人员;3=具有前科;4=涉案人员;5=失信人员;6=无
			String map = propertyEngine.readById("yj_creSco_rskSrt_map");
			if(StringUtils.isBlank(map)){
				logger.error("{} 属性[yj_creSco_rskSrt_map]没有配置",trade_id);
				return null;
			}
			/**前后加;补齐*/
			map = ";"+map+";";
			int index = map.indexOf(";"+head+"=");			
			if(index >-1){
			  int start = index + 3;
			  int end = map.indexOf(";", start);
			  return map.substring(start, end);
			}
			
		}
		return null;
	}

	/**返回数据是否存在风险信用分*/
	private boolean existCreditScore(String trade_id, JSONObject rspJsnObj) {
		if(rspJsnObj != null && "1".equals(convertObjToStr(rspJsnObj.get("Result")))){
			return true;	
		}
		return false;
	}
    /**远程接口调用是否成功*/
	private boolean isCallSucss(String trade_id, JSONObject rspJsnObj) {
		if(rspJsnObj != null && "100".equals(convertObjToStr(rspJsnObj.get("ResponseCode")))){
		   return true;
		}
		return false;
	}

	private String buildPostData(String trade_id, String name, String cardNo) {
		String acode = "100700";//代码（固定不变）
        String param = "idNumber=" + cardNo + "&Name=" + name;
        String sign = md5(acode + param + account + md5(privateKey));//生成签名       
        String post_data = null;
		try {
			post_data = "acode=" + acode + "&param=" + URLEncoder.encode(param, "UTF-8") + "&account=" 
					+ account + "&sign=" + sign;
		} catch (UnsupportedEncodingException ex) {
			logger.error(trade_id+" ajin构建请求参数时异常 ",ex);			
		}
		return post_data;
	}

	public static void main(String[] args) {}
}
