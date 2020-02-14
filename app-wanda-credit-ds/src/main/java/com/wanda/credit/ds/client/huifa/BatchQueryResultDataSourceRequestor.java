package com.wanda.credit.ds.client.huifa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


@DataSourceClass(bindingDataSourceId="ds_huifaBatchQueryResult")
public class BatchQueryResultDataSourceRequestor extends BaseHuifaDataSourceRequestor implements IDataSourceRequestor{
	private Logger logger = LoggerFactory.getLogger(BatchQueryResultDataSourceRequestor.class);
	private String url;
	public String getUrl(){
		return url;
	}
	public void setUrl(String url){
		this.url = url;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String,Object> rets = null;
		List<String> tags =  new ArrayList<String>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setReq_url(url);
		logObj.setIncache("0");
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try {
			rets = new HashMap<String, Object>();
			Map<String,Object> respMap = new HashMap<String, Object>();
			String type = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String keynum = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			String pg = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
			String pz = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();
			String acct_id = (String)ParamUtil.findValue(ds.getParams_in(), "acct_id"); //用户id
			Map<String,String> params_in = new HashMap<String, String>();
			params_in.put("type", type);
			params_in.put("keynum", keynum);
			params_in.put("pg",pg);
			params_in.put("pz",pz);
			Map<String, Object> paramIn=new HashMap<String, Object>();
			paramIn.putAll(params_in);
			logger.info("{} 批量查询把请求参数写入数据库 ",prefix);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn,logObj);
			logger.info("{} 批量查询远程请求 ",prefix);
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			String content = RequestHelper.doGet(url, params_in, true);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
            if(!"".equals(content)&&content!=null){
            	respMap  = (Map<String, Object>) new ObjectMapper().readValue(content,Map.class);
            }
            String success=null;
            String message=null;
            List<Map<String, Object>> listInfo=null;
           if(respMap!=null&&respMap.size()>0){
        	   if(respMap.containsKey("success")){
        		   success = (String) respMap.get("success");
        	   }
        	   if(respMap.containsKey("message")){
           	       message = (String) respMap.get("message");
        	   }
        	    listInfo =  respMap.get("models")==null?null:(List<Map<String, Object>>)respMap.get("models");
           }
           logger.info("{} 批量查询返回字符串结果 ",prefix);
           String ref="SELECT ID FROM T_DS_HF_BATCHRECORDINPUTINFO WHERE KEYNUMINFO=? AND ROWNUM<2 ORDER BY CREATE_TIME ASC";
           String _ref="";
           try {
  			 _ref = daoService.getJdbcTemplate().queryForObject(ref,new String[]{keynum},String.class );
  		  } catch (Exception e) {
  				rets.clear();
  				rets.put(Conts.KEY_RET_DATA, respMap);
  				rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED);
  				rets.put(Conts.KEY_RET_MSG, "查询失败! 失败描述:"+ message);
  		  }
           logger.info("{} 批量查询数据库结果 ",prefix);
           String sql = "INSERT INTO T_DS_HF_BATCHQUERYRESULTINFO(REFID,TRADE_ID,INPUT_TYPE,INPUT_KEYNUM,INPUT_PG,INPUT_PZ,SUCCESS,MESSAGE,CONTENT) VALUES(?,?,?,?,?,?,?,?,?)";
//         daoService.getJdbcTemplate().update(sql, new Object[]{_ref,trade_id,type,keynum,pg,pz,success,message,content});
           daoService.getJdbcTemplate().update(sql, new Object[]{_ref,trade_id,type,keynum,pg,pz,success,message,null});
           String _sql = "SELECT ID FROM T_DS_HF_BATCHQUERYRESULTINFO WHERE TRADE_ID=?";
		   String refid = daoService.getJdbcTemplate().queryForObject(_sql, new Object[]{trade_id}, String.class);
		   save(success, message, trade_id, refid, listInfo, respMap, rets,logObj,tags,acct_id);
		   logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
		   logger.info("{} 批量查询 写入日志文件 ",prefix);
		   DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, JSON.toJSONString(paramIn), new String[] { trade_id }));
		   DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, content, new String[] { trade_id }));
		 }catch (Exception ex) {
			rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_DS_HUIFA3_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG,"批量查询异常! 详细信息:" + ex.getMessage());
			logger.error(prefix+" 批量查询返回异常：{}", ex);	
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("批量查询返回异常! 详细信息:" + ex.getMessage());
			}
		 }
		DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		return rets;
	}
		
	public BatchQueryResultDataSourceRequestor() {
		super();
	}
}