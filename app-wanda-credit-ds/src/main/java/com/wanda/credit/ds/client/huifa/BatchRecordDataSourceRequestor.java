package com.wanda.credit.ds.client.huifa;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_huifaBatchRecord")
public class BatchRecordDataSourceRequestor extends BaseDataSourceRequestor implements IDataSourceRequestor{
	private Logger logger = LoggerFactory.getLogger(BatchRecordDataSourceRequestor.class);
	private String url;
	@Autowired
	private DaoService daoService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String,Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setReq_url(url);
		logObj.setIncache("0");
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try {
			rets = new HashMap<String, Object>();
			Map<String,Object> respMap = new HashMap<String, Object>();
			logger.info("{} 汇法网个人、企业信息批量录入获取参数",prefix);
			String type =  ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String keynum = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			String msg = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
			Map<String,Object> params_in = new HashMap<String, Object>();
			params_in.put("type", type);
			params_in.put("keynum", keynum);
			params_in.put("msg",synchExecutorService.encrypt(msg));
			logger.info("{} 汇法网个人、企业信息批量录入保存参数",prefix);
			DataSourceLogEngineUtil.writeParamIn(trade_id, params_in,logObj);
			params_in.put("msg",URLEncoder.encode(msg, "UTF-8"));
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			String content = RequestHelper.sendPostRequest(url, params_in, "UTF-8",true);
			logger.info("{} 汇法网个人、企业信息批量录入返回json信息 ",prefix);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
            if(!"".equals(content)&&content!=null){
            	respMap  = (Map<String, Object>) new ObjectMapper().readValue(content,Map.class);
            }
            String success =null;
            String message=null;
            if(respMap!=null&&respMap.size()>0){
            	if(respMap.containsKey("success")){
            		success = (String) respMap.get("success");
            	}
            	if(respMap.containsKey("message")){
            		message= (String) respMap.get("message");
            	}
            }
            if("s".equals(success)){
            	 String sql="INSERT INTO T_DS_HF_BATCHRECORDINPUTINFO(TRADE_ID,TYPEINFO,KEYNUMINFO,MSGINFO,SUCCESS,MESSAGE,CONTENT) values (?,?,?,?,?,?,?)";
//               daoService.getJdbcTemplate().update(sql, new Object[]{trade_id,type,keynum,URLDecoder.decode(msg, "UTF-8"),success,message,content});
                 daoService.getJdbcTemplate().update(sql, new Object[]{trade_id,type,keynum,null,success,message,null});
                 logger.info("{} 汇法网个人、企业信息批量录入数据保存成功...",prefix);
                 rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_SUCCESS);
                 rets.put(Conts.KEY_RET_DATA,respMap);
                 rets.put(Conts.KEY_RET_MSG,"交易成功!");
                 logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
            }else{
            	rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED);
                rets.put(Conts.KEY_RET_MSG,"交易失败!,汇法网个人、企业信息批量录入数据返回失败"+message);
                logger.warn("{}汇法网批量录入信息返回失败:{}",prefix,"汇法网批量录入信息返回失败"+message);
                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
                logObj.setState_msg("汇法网批量录入信息返回失败"+message);
            }
            DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, JSON.toJSONString(params_in), new String[] { trade_id }));
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, content, new String[] { trade_id }));
		} catch (Exception ex) {
			rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_DS_HUIFA2_EXCEPTION);
            rets.put(Conts.KEY_RET_MSG,"交易失败!汇法网批量录入信息异常,详细信息："+ex.getMessage());
            logger.error(prefix+" 汇法批量录入信息异常:{}",ex);
            logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
            if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("汇法批量录入信息异常! 详细信息:" + ex.getMessage());
			}
		}finally{
			 DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		}
		return rets;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
