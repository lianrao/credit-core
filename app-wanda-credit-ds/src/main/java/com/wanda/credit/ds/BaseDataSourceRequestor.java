package com.wanda.credit.ds;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorDtoService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.util.ParamUtil;


public class BaseDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(BaseDataSourceRequestor.class);
	@Autowired
	public DaoService daoService;
	@Resource(name = "executorDtoService")
	public IExecutorDtoService executorDtoService;
	public String[] paramIds;
	/**可为空的参数 */
	public String[] nullableIds;
	public Map<String, Object> valid(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		try{
			rets = new HashMap<String, Object>();
			if(ds!=null && ds.getParams_in()!=null){
				for(String paramId : paramIds){
					if(nullableIds !=null && ArrayUtils.contains(nullableIds, paramId))continue;
					if(StringUtil.isEmpty(ParamUtil.findValue(ds.getParams_in(),paramId))){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
						rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
						return rets;
					}
				}
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "数据源参数校验通过!");
		}catch(Exception ex){
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ex.getMessage());
			ex.printStackTrace();
		}
		return rets;
	}
	/**判断账户是否走缓存,大于0走缓存*/
	public boolean dsIncache(String acct_id,String dsid){
		Integer count = daoService.getJdbcTemplate().queryForObject(
				" select count(1) cnt from cpdb_mk.t_mk_ds_incache d where d.acct_id=? and d.ds_id=? and d.status='0' ", 
				new Object[]{acct_id,dsid}, Integer.class);
		return count!=null && count > 0;
	}
	
	public String[] getParamIds() {
		return paramIds;
	}

	public void setParamIds(String[] paramIds) {
		this.paramIds = paramIds;
	}
	public String[] getNullableIds() {
		return nullableIds;
	}
	public void setNullableIds(String[] nullableIds) {
		this.nullableIds = nullableIds;
	}
		
}
