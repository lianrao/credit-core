package com.wanda.credit.ds.client.yixin;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.yixin.YXLoanRecordsVO;
import com.wanda.credit.ds.dao.domain.yixin.YXLoanoverdueVO;
import com.wanda.credit.ds.dao.iface.IYiXinCreditService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @title yixin贷款信息数据源
 * @date 2016-06-13
 * @author chsh.wu
 * */
@SuppressWarnings("unchecked")
@DataSourceClass(bindingDataSourceId="ds_yixin_loanrecords")
public class YiXinLoanDataSourceRequestor extends BaseYiXinDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(YiXinLoanDataSourceRequestor.class);

	@Autowired
	private IYiXinCreditService yxCreditService;
	
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	public IPropertyEngine propertyEngine;
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		String yixin_url = propertyEngine.readById("sys.credit.client.yixin.loanrecords.url");
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> context = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(yixin_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		
		String name = (String)ParamUtil.findValue(ds.getParams_in(), "name"); // 姓名
		String idType = (String)ParamUtil.findValue(ds.getParams_in(), "idType");// 证件类型
		if(idType == null) idType = DEFAULT_IDTYPE;//默认是身份证类型
		String idNo = (String)ParamUtil.findValue(ds.getParams_in(), "idNo");// 证件号码
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			String cryptIdNo = synchExecutorService.encrypt(idNo);
			context = ParamUtil.convertParams(ds.getParams_in());
			context.put("idType", idType);
			context.put("queryReason", DEFAULT_QUERYREASON);
			context.put("cryptIdNo", cryptIdNo);
			params = CommonUtil.sliceMapIfNotBlank(context, 
					new String[] { "name", "idType", "idNo"});
			List<YXLoanRecordsVO> loanList = fetchFromCache(name, idNo);
			if (loanList == null || loanList.size() == 0) {
				logObj.setIncache("0");
				context.put(TRADE_ID,trade_id);
				 JSONObject rspJsnData = executeClient(context,yixin_url);
				 /**交易成功*/
				 if(isSuccessful(rspJsnData)){
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					resource_tag = Conts.TAG_TST_SUCCESS;
					if (SUCC_CODE.equals(rspJsnData.getString("errorcode"))) {
						JSONObject dataJsnObj = rspJsnData.getJSONObject("data");
						/** 存储贷款记录 */
						JSONArray loanRecords = dataJsnObj.getJSONArray("loanRecords");
						/** 存储逾期记录 */
						JSONObject overdue = dataJsnObj.getJSONObject("overdue");
						if (loanRecords != null && loanRecords.size() > 0 ) {
							doSaveOperation(loanRecords,overdue, context);
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
							rets.put(Conts.KEY_RET_MSG, "采集成功!");
							rets.put(Conts.KEY_RET_DATA, rspJsnData.get("data"));
						} else {
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YX_LOANRECORD_NOTFOUND);//
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_YX_LOANRECORD_NOTFOUND.ret_msg);//
							logger.warn("{} 没有查询到借款信息", prefix);
						}
					} else if (SUCC_CODE2.equals(rspJsnData.getString("errorcode"))) {
						/** 查询无结果 */
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YX_LOANRECORD_NOTFOUND);//
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_YX_LOANRECORD_NOTFOUND.ret_msg);//
						logger.warn("{} 没有查询到借款信息", prefix);
					}
				 }else{ 
					 /**交易失败*/
//					String errorMsg = (String)rspJsnData.get("message");
					 resource_tag = Conts.TAG_TST_FAIL;
					String errorMsg=errorCode.get(rspJsnData.getString("errorcode"));
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YX_LOANRECORD_EXCEPTION);//
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_YX_LOANRECORD_EXCEPTION.ret_msg);//
					logger.warn("{} 借款信息查询失败 {}", prefix,errorMsg);
					logObj.setState_msg(errorMsg);
				 }
			} else {
				logger.info("{} 读取到缓存的借款信息", prefix);
				logObj.setIncache("1");
                buildRtrnDataFromCache(loanList);
			}
		} catch (Exception e) {
			/** 如果是超时异常 记录超时信息 */
			if (CommonUtil.isTimeoutException(e)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			}
			logObj.setState_msg("借款信息查询数据源处理时异常! 详细信息:" + e.getMessage());
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "借款信息查询数据源处理时异常! 详细信息:" + e.getMessage());
			logger.error(prefix + " 借款信息查询数据源处理时异常", e);
		} finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, params, logObj);
		}
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return rets;
	}


	/**目前不读缓存*/
	private void buildRtrnDataFromCache(List<YXLoanRecordsVO> blackList) {
	}


	/**数据库存储操作*/
	private void doSaveOperation(JSONArray datas,JSONObject overdue,
			Map<String, Object> context) {
		
		String trade_id = (String) context.get(TRADE_ID);
		String name = (String) context.get("name");
		String idType = (String) context.get("idType");
		String cryptIdNo = (String) context.get("cryptIdNo");
		String queryReason = (String) context.get("queryReason");
		/**存储贷款记录*/
		if(datas != null && datas.size() >0){
			List<YXLoanRecordsVO> vos = new ArrayList<YXLoanRecordsVO>();
			for(JSONObject data : datas.toArray(new JSONObject[0])){
				YXLoanRecordsVO vo = JSONObject.toJavaObject(data, YXLoanRecordsVO.class);
				vo.setTrade_id(trade_id);
				vo.setName(name);
				vo.setIdType(idType);
				vo.setIdNo(cryptIdNo);
				vo.setQueryReason(queryReason);
				vos.add(vo);
			}
			if(vos.size() >0 ){
				yxCreditService.addLoan(vos);
			}

		}
		/**存储逾期记录*/
		if(overdue != null){
			YXLoanoverdueVO vo = JSONObject.toJavaObject(overdue, YXLoanoverdueVO.class);
			if(overdue.get("180overdueTimes") != null){
				vo.setOverdueTimes180(overdue.get("180overdueTimes").toString());							
			}
			if(overdue.get("90overdueTimes") != null){
				vo.setOverdueTimes90(overdue.get("90overdueTimes").toString());							
			}
			vo.setTrade_id(trade_id);
			vo.setIdNo(cryptIdNo);
			vo.setIdType(idType);
			vo.setName(name);
			vo.setQueryReason(queryReason);
			yxCreditService.addLoanOverdue(vo);
		}
	
	}


	/**目前不读缓存*/
	private List<YXLoanRecordsVO> fetchFromCache(String name, String cardNo) {
		return null;
	}

	@Override
	protected JSONObject buildRequestBody(Map<String, Object> ctx) {
		Map<String, Object> reqparams = CommonUtil.sliceMapIfNotBlank(ctx, 
				new String[] { "name", "idType", "idNo","queryReason"});
		return new JSONObject(reqparams);
	}
	
	public static void main(String[] args) {/**/}
	
}
