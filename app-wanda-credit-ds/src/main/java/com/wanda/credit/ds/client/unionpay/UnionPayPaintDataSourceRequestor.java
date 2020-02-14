package com.wanda.credit.ds.client.unionpay;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.IUnionPayPaintService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
* @Title: 外部数据源
* @Description: 银联用户画像查询
* @author wenpeng.li@99bill.com  
* @date 2015年9月21日 下午09:38:31 
* @version V1.0
*/
@DataSourceClass(bindingDataSourceId="ds_unionPayPaint")
public class UnionPayPaintDataSourceRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor { 
	private final  Logger logger = LoggerFactory.getLogger(UnionPayPaintDataSourceRequestor.class);
	@Autowired
	private IUnionPayPaintService iUnionPayPaintService;
	private String url;
	private String account;
	private String privateKey;
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets =null;
		StringBuffer paramsUrl =null;
		Map<String, String> params = new TreeMap<String, String>();
		Set<String> keySet = null;
		Iterator<String> iter = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setDs_id(ds.id);
		logObj.setReq_url(url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setIncache("0");
		String resource_tag = Conts.TAG_YL_01;//不计费
		try{
			rets = new HashMap<String, Object>();
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //持卡人姓名
			String card = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();	//持卡卡号
			paramsUrl =new StringBuffer();
		    params.put("account", account);
		 	params.put("card", card);
		 	params.put("orderId", trade_id);
		 	params.put("name", name);
		 	params.put("index", "all");
		 	keySet = params.keySet();
		 	iter = keySet.iterator();
		      while (iter.hasNext()) {
		          String key = iter.next();
		          paramsUrl.append(key);
		          paramsUrl.append(params.get(key));
		      }
		    paramsUrl.append(privateKey);
		    params.put("sign", new MD5().get16MD5ofStr(paramsUrl.toString()).toUpperCase());
		    logger.info("{} 银联用户画像查询开始...",prefix);
		    String resp = RequestHelper.doGet(url, params, false);
		    
		    logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		    
		    Map<String,Object> respMap = new ObjectMapper().readValue(resp, Map.class);// 转成map
		    //modify by wangjing//个人/企业标识：0-个人，1-企业
		    rets.clear();
		    rets.put(Conts.KEY_RET_DATA, respMap);
		    rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		    params.put("bc_flag", "0");
		    iUnionPayPaintService.save(trade_id, resp, params);
		    //ds日志：数据源响应信息记录
		    DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, resp, new String[] { trade_id }));
		    
		    logObj.setBiz_code1(String.valueOf(respMap.get("resCode")));
		    if("0000".equals(respMap.get("resCode"))){
		    	logObj.setBiz_code2(String.valueOf(respMap.get("statCode")));
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
		    	if("1001".equals(respMap.get("statCode")))
		    		resource_tag = Conts.TAG_YL_01;//计费
				rets.put(Conts.KEY_RET_MSG, "银联用户画像查询成功!");
				logger.info("{} 银联用户画像查询成功!", prefix);
		    }else if("2006".equals(respMap.get("statCode"))){
		    	logger.warn("{} 银联用户画像无返回信息:{}",prefix,respMap.get("resMsg"));
				rets.put(Conts.KEY_RET_MSG, "银联用户画像查询成功，但无卡信息!");
				logger.info("{} 银联用户画像查询成功，但无卡信息!", prefix);
		    }else{
		    	logger.error("{} 银联用户画像返回失败! 详细信息:{}",prefix,respMap.get("resMsg"));
		    	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PAINT_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "银联用户画像返回失败! 详细信息:"+respMap.get("resMsg"));
		    }
		}catch(Exception ex){
//			ex.printStackTrace();
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error(prefix + " 数据源处理时异常：：", ex);
		} finally {
			logObj.setTag(resource_tag);
			//ds日志：数据源入参调用概要信息记录
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
//			Map<String, Object> reqParams =(Map)params;
			//ds日志：数据源入参信息记录
			DataSourceLogEngineUtil.writeParamIn(trade_id, (Map)params, logObj);
		}
		return rets;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
}
