package com.wanda.credit.ds.client.dsconfig;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.dsconfig.dao.BatchUpdateSqlSupport;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.dsconfig.db.ExecutingSqlHolder;
import com.wanda.credit.dsconfig.db.SqlOperType;
import com.wanda.credit.dsconfig.enums.ErrorType;
import com.wanda.credit.dsconfig.loader.DsCfgHolder;
import com.wanda.credit.dsconfig.main.ResolveContext;
import com.wanda.credit.dsconfig.model.Error;
import com.wanda.credit.dsconfig.model.action.IAction;
import com.wanda.credit.dsconfig.model.module.CacheModule;
import com.wanda.credit.dsconfig.model.module.DsParam;
import com.wanda.credit.dsconfig.model.module.GathererModule;
import com.wanda.credit.dsconfig.model.module.VerifyModule;
import com.wanda.credit.dsconfig.model.vo.DataSourceCfgInfo;
 
/**
 * @description  默认可配置化数据源处理类
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年12月4日 下午5:36:50 
 *  
 */
@Service
public class DefaultConfigurableDataSourceRequestor extends BaseDataSourceRequestor 
  implements IDataSourceRequestor{
	private final static Logger logger = LoggerFactory.getLogger(DefaultConfigurableDataSourceRequestor.class);
	
	
	@Autowired
	DaoService daoService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource dataSource) {
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = null;

		try {
//			DataSourceCfgVO dsCfg = mockCfg();
			DataSourceCfgInfo dsCfg = DsCfgHolder.getDsCfg(dataSource.getId());
			if (dsCfg == null) {
				String err = String.format("数据源处理异常:未找到id为 [%s] 的数据源配置信息",dataSource.getId());
				logger.error(err);
				throw new Exception(err);
			}
			logger.info("{} context init start",trade_id);
			ResolveContext.init(trade_id,dataSource.getId());
			rets = new HashMap<String, Object>();
			logObj = new DataSourceLogVO();
			logObj.setDs_id(dataSource.getId());
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			logObj.setIncache("0");
			logger.info("{} context init end",trade_id);
			ResolveContext.setDsLog(logObj);
			ResolveContext.setDscfg(dsCfg);
			ResolveContext.setDsParamNames(getParamName(dsCfg.getBasicInfo().getParams()));
			logger.info("{} 收到数据源【{}】处理请求", trade_id, dsCfg.getBasicInfo().getDsName());

			logObj.setReq_url(dsCfg.getBasicInfo().getReqUrl());
			
			/** 初始化变量 */
			Map<String,Object> paramsToSave = dsCfg.getBasicInfo().initParams(dataSource);
			if(MapUtils.isNotEmpty(paramsToSave)){
				/**写入数据源输入参数*/
				logger.debug("{} start save dsparam",trade_id);
/*				ResolveContext.addBatchUpdatedSql(
						BatchUpdateSqlSupport.buildReqParamSavedSqlsFromMap(params, logObj));				
*/				DataSourceLogEngineUtil.writeParamIn(ResolveContext.getTradeId(),paramsToSave,logObj);
				logger.debug("{} end save dsparam",trade_id);

			}
			
			/**执行数据校验动作*/
			VerifyModule verify = ResolveContext.getDscfg().getVerifyModule();
			if (verify != null) {
				logger.info("{} 开始执行数据校验动作",trade_id);
				verify.execute();
				logger.info("{} 完成执行数据校验动作",trade_id);
			}
			
			/**执行变量声明动作*/
			IAction varDefine = dsCfg.getVarDefineAction();
			if (varDefine != null) {
				logger.info("{} 开始执行变量初始化动作",trade_id);
				varDefine.execute();
				logger.info("{} 完成执行变量初始化动作",trade_id);
			}
			
			/**执行缓存查询动作 */
			CacheModule cache = dsCfg.getCacheModule();
			if (cache != null) {
				logger.info("{} 开始执行缓存动作",trade_id);
				cache.execute();
				logger.info("{} 完成执行缓存动作",trade_id);
			}
			/**执行数据采集动作*/
			GathererModule gatherer = dsCfg.getGathererModule();
			if (gatherer != null) {
				logger.info("{} 开始执行数据采集动作",trade_id);
				gatherer.execute();
				logger.info("{} 完成执行数据采集动作",trade_id);
			}
			/**所有流程完毕错误处理*/
			Error error = ResolveContext.getError();
			if (error != null) {
				logger.error("{} {}: 【错误码:{}】 【错误信息:{}】", new Object[] { trade_id,
						error.getType().getDisplayName(), error.getErrCode(),
						error.getErrMsg() });
				if(ErrorType.dshandleTimeout.equals(error.getType())){
					rets = handleTimeout(logObj);
					rets.put(Conts.KEY_RET_CODE,CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT.ret_sub_code);
					return rets;
				}else{
					/**这块代码有点乱 不清晰 担心改动大了 对现有产品有冲击*/
					handleSysError(logObj, error.getLogMsg(),false);
					if(StringUtils.isBlank(error.getErrCode())){
						error.setErrCode(CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.ret_sub_code);
					}
					/**参数校验错误*/
					if(ErrorType.paramvalidErr.equals(error.getType())){
						rets = setRetStatus(getCRSStatusEnum(CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED.name()),
								error.getErrMsg());
					}else{
						/**定义的warn 信息*/
						rets = setRetStatus(getCRSStatusEnum(CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.name()),
								error.getErrMsg());
					}
					
					/**解决老的配置问题*/
					handleErrorCode(error);
					rets.put(Conts.KEY_RET_CODE,error.getErrCode());
					return rets;	
				}				
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
//				logger.debug("{} retdata {}",trade_id,(String)ResolveContext.getRetData());
				rets.put(Conts.KEY_RET_DATA, JSON.parse((String)ResolveContext.getRetData()));
				rets.putAll(setRetStatus(CRSStatusEnum.STATUS_SUCCESS, "采集成功"));
			}

		} catch (Exception ex) {
			logger.error(trade_id + " 数据源处理异常", ex);
			logger.info("{} retdata {}",trade_id,(String)ResolveContext.getRetData());
			if (CommonUtil.isTimeoutException(ex)) {
				rets = handleTimeout(logObj);
				rets.put(Conts.KEY_RET_CODE,CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT.ret_sub_code);
			} else {
				rets = handleSysError(logObj, ex.getMessage(),true);
				rets.put(Conts.KEY_RET_CODE,CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.ret_sub_code);
			}
		} finally {
			String tagstr = StringUtils.join(ResolveContext.getTags(), ";");
			if(ResolveContext.getTags().size()>1){
				//add suffix ';'
				tagstr = tagstr.concat(";");
			}
			logObj.setTag(tagstr);
			logObj.setBiz_code1(StringUtils.join(ResolveContext.getBizcode1(), ";"));
			rets.put(Conts.KEY_RET_TAG,
					ResolveContext.getTags().toArray(new String[0]));
/*			logger.debug("{} start batch Update Sql",trade_id);
			batchUpdateSql();
			logger.debug("{} end start batch Update Sql",trade_id);
*/			
			logger.debug("{} start save dslog",trade_id);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			logger.debug("{} end save dslog",trade_id);
			
			if(logger.isDebugEnabled()){
				logger.debug("{} modeldata>>>{}",
						ResolveContext.getTradeId(), ResolveContext.getModelData());
				logger.debug("{} midatedata>>>{}",
						ResolveContext.getTradeId(),ResolveContext.getMediateData());

			 }
			//标识从数据源配置返回的内容
			rets.put("fromDsConfig","1");
			logger.info("{} 表达式总执行耗时: {}",trade_id,ResolveContext.getExprExeTime());
		}
		return rets;
	}

   private void handleErrorCode(Error error) {
	   if(CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.name().equals(error.getErrCode())){
		   error.setErrCode(CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.ret_sub_code);
	   }else if(CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT.name().equals(error.getErrCode())){
		   error.setErrCode(CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT.ret_sub_code);
	   }else if(CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED.name().equals(error.getErrCode())){
		   error.setErrCode(CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED.ret_sub_code);
	   }
	}

	//	@Transactional
	private void batchUpdateSql2() {
//		javax.sql.DataSource datasource = daoService.getJdbcTemplate().getDataSource();
//		Connection con = datasource.getConnection();
		
		List<ExecutingSqlHolder> sqls = ResolveContext.getBatchUpdatedDBSQLS();
	    if(CollectionUtils.isNotEmpty(sqls)){
	    	for(ExecutingSqlHolder item : sqls){
	    		if(item.getOperType().equals(SqlOperType.update)){
	    		 daoService.getJdbcTemplate().update(item.getSqlText(), item.getParams().toArray());
	    		}
	    	}
	    }	
	}

	private Map<String, Object> handleSysError(DataSourceLogVO logObj,
			String errMsg,boolean forceResetTagWithSysError) {
		Map<String, Object> rets;
		if(forceResetTagWithSysError){
			ResolveContext.resetTag(Conts.TAG_SYS_ERROR);		
		}else if(CollectionUtils.isEmpty(ResolveContext.getTags())){
			ResolveContext.resetTag(Conts.TAG_SYS_ERROR);		
		}
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("数据源处理时异常! 详细信息:" + errMsg);
		rets = setRetStatusWithDefaultErr();
		return rets;
	}

	private Map<String, Object> handleTimeout(DataSourceLogVO logObj) {
		Map<String, Object> rets;
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
		ResolveContext.resetTag(Conts.TAG_SYS_TIMEOUT);
		rets = setRetStatusWithTimeout();
		return rets;
	}
	
	private String getParamName(List<DsParam> params) {
		StringBuffer sb = new StringBuffer();
		int i=0;
		if(params != null){
			for(DsParam item : params){
				if(i>0){
					sb.append(";");
				}
				sb.append(item.getName());
				i++;
			}	
		}		
		return sb.toString();
	}

	private CRSStatusEnum getCRSStatusEnum(String errCode) {
		return CRSStatusEnum.valueOf(errCode);
	}
	
	@Override
	public Map<String, Object> valid(String trade_id, DataSource ds) {
		Map<String,Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_MSG, "数据源参数校验通过!");
		return rets;
	}
	public  Map<String, Object> setRetStatusWithDefaultErr() {
        return setRetStatus(CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION, "数据源处理异常");
	}

	public  Map<String, Object> setRetStatusWithTimeout() {
        return setRetStatus(CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT, "数据源处理超时");
	}
	
	public  Map<String,Object> setRetStatus(CRSStatusEnum status,String errMsg) {
		Map<String,Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS,  status);
		rets.put(Conts.KEY_RET_MSG, errMsg);
		return rets;
	}

	public static void main(String[] args) {	
		ArrayList list = new ArrayList();
		list.add(1);//list.add(2);
		String tagstr;
		tagstr = StringUtils.join(list,";") ;
		if(list.size() >1){
			//add suffix ';'
			tagstr = tagstr.concat(";");
		}
		System.out.println(tagstr);
	  }
}