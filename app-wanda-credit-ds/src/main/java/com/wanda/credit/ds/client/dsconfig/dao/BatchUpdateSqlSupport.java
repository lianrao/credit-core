package com.wanda.credit.ds.client.dsconfig.dao;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.ds.client.dsconfig.DefaultConfigurableDataSourceRequestor;
import com.wanda.credit.dsconfig.db.ExecutingSqlHolder;
import com.wanda.credit.dsconfig.db.SqlOperType;
import com.wanda.credit.dsconfig.main.ResolveContext;

/**
 * @description
 * @author wuchsh
 * @version 1.0
 * @createdate 2017年3月27日 下午5:29:31
 * 
 */
@Service
public class BatchUpdateSqlSupport {
	private final static Logger logger = LoggerFactory.getLogger(BatchUpdateSqlSupport.class);

	private static PropertyEngine propertyEngine; 
	
	private static IExecutorSecurityService synchExecutorService;

	public static PropertyEngine getPropertyEngine() {
		return propertyEngine;
	}

	@Autowired
	public static void setPropertyEngine(PropertyEngine propertyEngine) {
		BatchUpdateSqlSupport.propertyEngine = propertyEngine;
	}

	public static IExecutorSecurityService getExecutorSecurityService() {
		return synchExecutorService;
	}

	@Autowired
	public static void setExecutorSecurityService(IExecutorSecurityService 
			synchExecutorService) {
		BatchUpdateSqlSupport.synchExecutorService = synchExecutorService;
	}

	public static List<ExecutingSqlHolder> buildReqParamSavedSqlsFromMap(Map<String, Object> paramsmap,DataSourceLogVO log) {
		List<ExecutingSqlHolder> sqls = new ArrayList<ExecutingSqlHolder>();
		if(MapUtils.isNotEmpty(paramsmap)){
			String encryptWords = propertyEngine.readById("sys_encrypt_keywords");
			ExecutingSqlHolder item ;
			for(Map.Entry<String, Object> entry: paramsmap.entrySet()){
				if(entry.getValue() == null)continue;
				item = new ExecutingSqlHolder();
				item.setOperType(SqlOperType.update);
				item.setSqlText(reqSQL);
				sqls.add(item);
				item.getParams().add(ResolveContext.getTradeId());
				item.getParams().add(entry.getKey());
				String value = 
				entry.getValue() != null? entry.getValue().toString() : null;
				if (!StringUtil.isEmpty(value)
						&& ArrayUtils.contains(encryptWords.split(","),entry.getKey())) {
					try {
						value = synchExecutorService.encrypt(value);
					} catch (Exception e) {
						logger.error("目标值 {} 加密异常,具体原因：{}", value,
								e);
					}
				}
				item.getParams().add(value);
				item.getParams().add(log.getId());
			}
		}
		return sqls;
	}

	public static ExecutingSqlHolder buildDsLogSavedSqlFromLogObj(DataSourceLogVO log) {
		ExecutingSqlHolder item = new ExecutingSqlHolder();
		item.setOperType(SqlOperType.update);
		item.setSqlText(logSQL);

		if(StringUtils.isBlank(log.getTrade_id())){
			log.setTrade_id(ResolveContext.getTradeId());
		}
		/**统计交易耗时*/
		if(log.getReq_time() != null && log.getRsp_time() != null 
				&& log.getTotal_cost() == null){
			log.setTotal_cost(log.getRsp_time().getTime() 
					- log.getReq_time().getTime());
		}
		
		/**设置交易状态码*/
		if(StringUtils.isBlank(log.getState_msg())){
		    log.setState_msg((String)DataSourceLogEngineUtil.ERRMAP.get(log.getState_code()));	
		}else{
			/**如果state_msg 的字节长度  >4000 截取 前1000个字符*/
			try {
				if(log.getState_msg().getBytes("utf-8").length > 4000){
				   log.setState_msg(log.getState_msg().substring(0, 1000)+"...");
				}
			} catch (UnsupportedEncodingException e) {
				logger.error(ResolveContext.getTradeId() +" 数据源日志处理异常 ",e);
			}	
		}	
		Object[] paramarr = new Object[]{
				log.getId(),
				log.getTrade_id(),log.getDs_id(),log.getState_code(),log.getState_msg(),
				log.getReq_url(),log.getIncache(),log.getBiz_code1(),log.getBiz_code2(),
				log.getBiz_code3(),log.getField_id(),log.getReq_time(),log.getRsp_time(),
				log.getTotal_cost(),log.getTag()
		};
		item.getParams().clear();
		item.getParams().addAll(Arrays.asList(paramarr));
		return item;
	}
	

	/**通用数据源请求数据insert SQL*/
	private static String reqSQL = "INSERT INTO CPDB_DS.T_DS_DATASOURCE_REQ "
			+ " (ID,TRADE_ID,REQ_NAME,REQ_VALUE,REFID) "
			+ " VALUES (SYS_GUID(),?,?,?,?)";

	/**通用数据源响应数据insert SQL*/
	private static String logSQL = "INSERT INTO CPDB_DS.T_DS_DATASOURCE_LOG (ID, TRADE_ID, DS_ID, STATE_CODE, STATE_MSG,"
			+ " REQ_URL, INCACHE, BIZ_CODE1, BIZ_CODE2, "
			+ " BIZ_CODE3, FIELD_ID, REQ_TIME, RSP_TIME, "
			+ " TOTAL_COST,TAG)"
			+ " VALUES (?, ?, ?, ?,?, "
			+ " ?, ?, ?, ?, "
			+ " ?, ?, ?, ?, "
			+ " ?,?)";
	
	
}
