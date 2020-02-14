package com.wanda.credit.ds.client.juxinli;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.JXLEbusiDataSourcesRes;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.MobileEBusiDataSource;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 获取支持的数据源列表
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlEbusiDataSources")
public class JXLEBusiDataSourcesRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {

	private final static Logger logger = LoggerFactory.getLogger(JXLEBusiDataSourcesRequestor.class);
	
	private String httpsGetUrl;
	private String orgAccount;
	private int timeOut;
	
	public Map<String, Object> valid(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		try{
			rets = new HashMap<String, Object>();
			if(ds!=null){
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "数据源参数校验通过!");
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
				return rets;
			}
			
		}catch(Exception ex){
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ex.getMessage());
			ex.printStackTrace();
		}
		
		return rets;
	}
	
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		long startTime = System.currentTimeMillis();
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 准备连接聚信立获取电商的数据源信息,trade_id=" + trade_id , prefix);
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(httpsGetUrl);
		logObj.setIncache("0");
		String resource_tag = Conts.TAG_SYS_ERROR;
		//拼装获取聚信立获取支持的电商数据的URL为
		StringBuffer urlBf = new StringBuffer();
		if(httpsGetUrl == null || "".equals(httpsGetUrl)){
			logger.error("{} 聚信立获取电商的数据源信息,配置参数httpsGetUrl为" + httpsGetUrl  ,prefix);
			
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "获取支持的服务失败");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			return rets;
		}else{
			if(httpsGetUrl.endsWith("/")){
				urlBf.append(httpsGetUrl).append(orgAccount).append("/datasources");
			}else{
				urlBf.append(httpsGetUrl).append("/").append(orgAccount).append("/datasources");
			}
			logger.info("{} 聚信立获取电商数据源信息请求URL为" + urlBf.toString()  ,prefix);
		}
		
		try{
			long postStartTime = System.currentTimeMillis();
			//调用聚信立接口
			JsonObject eBusiDataSources = getEBusiDataSources(urlBf.toString(), timeOut * 1000,prefix);
			long PostTime = (System.currentTimeMillis() - postStartTime);
			logger.info("{} https请求聚信立耗时为（ms）" + PostTime  ,prefix);
			if(eBusiDataSources != null){
				logger.info("{} 聚信立获取电商数据源返回信息为" + eBusiDataSources.toString() , prefix);
				JsonElement successElement = eBusiDataSources.get(JXLConst.FLAG_SUCCESS);
				if(successElement != null){
					logger.info("{} 聚信立获取电商数据源Json数据源中success节点内容为" + successElement.getAsString()  ,prefix);
					
					logObj.setBiz_code1(successElement.getAsString());
					
					if("true".equals(successElement.getAsString())){

						logger.info("{} 连接聚信立获取支持的数据源列表成功，将返回结果转成对象 BEGIN "  ,prefix);
						
						Gson gson = new Gson();
						JXLEbusiDataSourcesRes dataSources = gson.fromJson(eBusiDataSources, JXLEbusiDataSourcesRes.class);

						logger.info("{} 连接聚信立获取支持的数据源列表成功，将返回结果转成对象 SUCCESS "  ,prefix);
						
						if(dataSources != null){
							List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
							//获取返回数据中data对应的内容
							logger.info("{} 解析data数据节点，将数据节点解析并拼装成返回数据 BEGIN"  ,prefix);
							List<MobileEBusiDataSource> datas = dataSources.getData();
							if (datas != null && datas.size() > 0) {
								for (MobileEBusiDataSource dataSource : datas) {
									//获取当前数据源的status状态：0-开发 1-上线  2-下线
									if (JXLConst.JUXINLI_DSONLINE_STATUS.equals(dataSource.getStatus())) {
										Map<String, String> dataMap = new HashMap<String, String>();
										dataMap.put(JXLConst.WEBSITE_EN_NAME,dataSource.getWebsite());
										dataMap.put(JXLConst.WEBSITE_CN_NAME,dataSource.getName());
										dataMap.put(JXLConst.CATEGORY_EN_NAME,dataSource.getCategory());
										dataMap.put(JXLConst.CATEGORY_CN_NAME,dataSource.getCategory_name());
										dataList.add(dataMap);
									} else{
										StringBuffer bf = new StringBuffer();										
										bf.append("datasource_status=").append(dataSource.getStatus())
											.append(JXLConst.WEBSITE_EN_NAME).append("=").append(dataSource.getWebsite())
											.append(JXLConst.CATEGORY_EN_NAME).append("=").append(dataSource.getCategory());
										logger.info("{} 聚信立返回数据中未上线的数据源为:" + bf.toString()  ,prefix);
									}
								}

							}
							retdata.put("dataSources",JSONObject.toJSON(dataList));
							logger.info("{} 解析data数据节点，将数据节点解析并拼装成返回数据 SUCCESS"  ,prefix);
							resource_tag = Conts.TAG_TST_SUCCESS;
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							logObj.setState_msg("交易成功");
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_SUCCESS);
							rets.put(Conts.KEY_RET_MSG, "获取支持的服务成功");							
							rets.put(Conts.KEY_RET_DATA, retdata);
							
						}else{	
							logger.error("{} 连接聚信立获取支持的数据源列表，聚信立返回数据但data节点的数据为null"  ,prefix);
							throw new Exception("E_000004");
						}
					}else{	
						logger.error("{} 连接聚信立获取支持的数据源列表，返回数据中success节点对应内容不为true"  ,prefix);
						throw new Exception("E_000003");
					}
				}else{			
					logger.error("{} 连接聚信立获取支持的数据源列表，返回数据中success节点对应内容为null"  ,prefix);
					throw new Exception("E_000002");
				}
			}else{			
				logger.error("{} 连接聚信立获取支持的数据源列表，返回数据为null"  ,prefix);
				throw new Exception("E_000001");
			}
		}catch (Exception e) {
			
			if((e instanceof ConnectTimeoutException) || (e instanceof SocketTimeoutException)){
				logger.error("{} 连接聚信立获取支持的数据源请求超时" + e.getMessage());
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
			}else{
				logger.error("{} 连接聚信立获取支持的数据源返回失败! 详细信息:{}",e.getMessage() ,prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("交易失败");
			}
			String message = e.getMessage();
			if("E_000001".equals(message)){
				logger.error("{} 聚信立获取电商数据源信息https请求结果为null"  ,prefix);
			}else if("E_000002".equals(message)){
				logger.error("{} 聚信立获取电商数据源返回JSON数据中没有success节点"  ,prefix);
			}else if("E_000003".equals(message)){
				logger.error("{} 聚信立获取电商数据源Json数据源中success节点内容不为true"  ,prefix);
			}else if("E_000004".equals(message)){
				logger.error("{} 聚信立获取电商数据源将Json转化成对象返回为null"  ,prefix);
			}else{
				logger.error("{} 聚信立获取电商数据异常  {}",prefix,ExceptionUtil.getTrace(e));
			}
			
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "获取支持的服务失败");
		}
		
		String retCode = "";
		if (rets.containsKey(Conts.KEY_RET_STATUS)) {
			CRSStatusEnum retstatus = CRSStatusEnum.valueOf(rets.get(Conts.KEY_RET_STATUS).toString());
			retCode = retstatus.getRet_sub_code();
		}
		
		saveTradeInfo(trade_id, JXLConst.TF_GET_ALL_DS, retCode, null, null, null, null);
		
		logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		/**记录响应状态信息*/
		try{
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		}catch(Exception e){
			logger.error("{} 日志表数据保存异常 {}" , prefix , ExceptionUtil.getTrace(e));
		}
		
		long tradeTime = (System.currentTimeMillis() - startTime);
		logger.info("{} 获取支持的数据源列表总共耗时时间为（ms）" + tradeTime  ,prefix);
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return rets;
	}

	public String getHttpsGetUrl() {
		return httpsGetUrl;
	}

	public void setHttpsGetUrl(String httpsGetUrl) {
		this.httpsGetUrl = httpsGetUrl;
	}

	public String getOrgAccount() {
		return orgAccount;
	}

	public void setOrgAccount(String orgAccount) {
		this.orgAccount = orgAccount;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	} 

}
