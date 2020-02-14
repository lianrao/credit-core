package com.wanda.credit.ds;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.DataSourceTag;
import com.wanda.credit.api.iface.IDataSourceService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.counter.GlobalCounter;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.template.iface.ITemplateEngine;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.dsconfig.loader.DsCfgHolder;
@Service
public class DataSourceService extends BaseDataSourceService implements IDataSourceService {
	private final  Logger logger = LoggerFactory.getLogger(DataSourceService.class);
	private static WebApplicationContext wac ;
	@Autowired
	public ITemplateEngine templateEngine;
//	@Autowired
//	public IPropertyEngine propertyEngine;
	@Autowired
	public DaoService daoService;
	
	@Override
	public Map<String, Object> fetch(String trade_id, final DataSource ds)
			throws Exception {
		/**检测数据源是否已经登记 开发环境使用 wcs add*/
		if("1".equals(PropertyEngine.get("PrdDsValidSwitch")) && !checkDs(ds.getId())){
			logger.error("{} 请提前登记数据源id  {}",trade_id,ds.getId());
			throw new RuntimeException("请提前登记数据源id "+ ds.getId());
		}
		long start = new Date().getTime();
		DataSource dsNew = new DataSource();
		dsNew = ds;
		final String prefix = trade_id +" "+ header; //流水号标识
		logger.info("{} 数据源适配器收到请求...", prefix);
		final boolean doPrint = "1".equals(propertyEngine.readById("sys_log_print_switch"));
		String hint_dsId = propertyEngine.readById("sys_ds_hint_switch");//特定产品强制走固定数据源id
		String route_dsId = propertyEngine.readById("sys_ds_route_switch");//特定产品按比例路由数据源id
		String special_acct = propertyEngine.readById("sys_ds_special_switch");//特定账户不参加按比例分配
		String ds_id = getDsHint(prefix,ds.getRefProdCode(),
				ds.getId(),hint_dsId,route_dsId,isSpecialAcct(special_acct,ds.getAcct_id()));
//		String ds_id = ds.getId();
		
		//Step1: 是否启用mock
		if(enableMock(ds_id)){
			logger.warn("{} mock开关已启用!{}", prefix, ds_id);
			return getMockResponse(ds_id);
		}
				
		
		//Step2: 该ds_id是否已经熔断
		boolean isSwitchB = false;//是否A已经熔断
		Map<String, Object> rets = new HashMap<String, Object>();
		final String forwardDsId = findABDs(propertyEngine.readById("ds_ab_watch"),ds_id,prefix);
		if(!forwardDsId.equalsIgnoreCase(ds_id)){
			logger.warn("{} AB数据源切换! A数据源ID：{} B数据源ID：{}",prefix,ds.getId(),forwardDsId);
			ds.setId(forwardDsId);
			isSwitchB = true;
		}
		if(isfuseOff(trade_id,ds_id)){
			if(isSwitchB && !GlobalCounter.exists(tail_errorlist, forwardDsId)){
				logger.warn("{} 数据源重定向启动0!  重定向后数据源ID：{}",prefix,ds.getId(),forwardDsId);
				ds_id = forwardDsId;
			}				
		}else{
			if(isSwitchB && !GlobalCounter.exists(tail_errorlist, forwardDsId)){
				logger.warn("{} 数据源重定向启动1!  重定向后数据源ID：{}",prefix,ds_id,forwardDsId);
				ds_id = forwardDsId;
			}
		}
		dsNew.setId(ds_id);
		logger.info("{} 采集任务适配开始...", prefix);
//		String ds_flow_flag = ds.getRefProdCode() + "-" + ds_id;
		String ds_error_flag = ds_id + tail_errorlist;
		boolean isConfiguredDS = false;
		Object wacBean = null;

		//Step3: 数据源配置检查
		try{
			if (null == wac)
				wac = ContextLoader.getCurrentWebApplicationContext();
			//wuchsh add
			if(DsCfgHolder.getDsCfg(ds_id) != null){
			   logger.warn("{}数据源适配走配置通道...", prefix);
			   isConfiguredDS = true;
			   wacBean =  wac.getBean("defaultConfigurableDataSourceRequestor"); 	
			}else{
			   wacBean = wac.getBean(ds_id);			
			}
//			Object wacBean = wac.getBean(ds_id);
			if (null == wacBean
					|| wacBean.getClass().isAssignableFrom(
							BaseDataSourceRequestor.class)) {
				rets.put(Conts.KEY_RET_STATUS, "-99999");
				rets.put(Conts.KEY_RET_MSG, "数据源配置异常，请检查参数!");
				logger.error("{} 数据源配置异常，请检查参数!{}", prefix, ds_id);
				return rets;
			}
		}catch(NoSuchBeanDefinitionException ex){
			rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_SYS_DS_NOT_MATCHED);
			rets.put(Conts.KEY_RET_MSG,"无合适的数据源驱动!");
			logger.warn("{} 无合适的数据源驱动!{}", prefix, ds_id);
			return rets;
		}
		
		//Step4: 数据源适配
		logger.warn("{} 数据源适配开始...", prefix);
		IDataSourceRequestor requestor;
		if(!isConfiguredDS){
			requestor = (IDataSourceRequestor) matchDataSourceRequestor(ds_id);
			if(requestor==null){
				rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_SYS_DS_NOT_MATCHED);
				rets.put(Conts.KEY_RET_MSG,"无合适的数据源驱动!");
				logger.warn("{} 无合适的数据源驱动!{}", prefix, ds_id);
				return rets;
			}	
		}else{
			requestor = (IDataSourceRequestor) wacBean;
		}
		
		logger.info("{}采集任务适配成功!", prefix);
		
		//Step5: 数据源参数校验
		logger.info("{}数据源参数验证开始...", prefix);
		rets = requestor.valid(trade_id, dsNew);
		if (!isSuccess(rets)){
			rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
			rets.put(Conts.KEY_RET_MSG,"校验不通过:传入参数不正确");
			logger.warn("{} 校验不通过:传入参数不正确!{}", prefix, ds_id);
			return rets;
		}
		logger.info("{} 验证通过", prefix);
		
		//Step6: 数据源采集启动
		logger.info("{} 数据采集任务开始...", prefix);
		long reqStart = new Date().getTime();
		rets = requestor.request(trade_id, ds);
		if(ds.getLimit_out()!=null)
			templateEngine.filterParams(rets, ds.getLimit_out());
		//add by wangjing
		logger.info("{} 数据采集任务完毕,耗时:{}", prefix ,new Date().getTime() - reqStart +" ms");
		
		//Step7: redis数据源流量监控
		
		
		//Step8: redis数据源熔断监控 
		if(isErr(rets) || isTimeout(prefix,ds_id,(new Date().getTime() - start))){
			logger.warn("{} 数据源熔断收集器+1：{} {}", prefix, ds_id,ds_error_flag);
			GlobalCounter.sign(ds_error_flag, Integer.parseInt(propertyEngine
					.readById("ds_error_expire_sec")));
			logger.info("{} 数据源熔断收集器当前统计数：{}", prefix,
					GlobalCounter.getCount(ds_error_flag));
		}
		
		//Step9: redis数据源标签注册
		if (rets != null
				&& rets.containsKey(Conts.KEY_RET_TAG)
				&& "1".equals(propertyEngine.readById("sys_tag_switch"))) {
			String status_code=((CRSStatusEnum)rets.get(Conts.KEY_RET_STATUS)).getRet_sub_code();
			logger.info("{} 标签注册启动", prefix);
			rets.put(Conts.KEY_RET_TAG, new DataSourceTag(ds.getId(),
					(String[])rets.get(Conts.KEY_RET_TAG),status_code));
			logger.info("{} 注册成功!", prefix);
		}
		logger.info("{} 标签注册完毕", prefix);
		
		if (doPrint) {
			logger.info("{} 数据采集任务完毕,返回消息:{}", prefix, JSONObject.toJSONString(rets,true));
		}
		logger.info("{} 总计耗时:{}", prefix, new Date().getTime() - start +" ms");
		return rets;
	}

	private boolean checkDs(String dsid) {
		Integer count = daoService.getJdbcTemplate().queryForObject(
				"select count(1) count from cpdb_mk.t_etl_datasource_idname where ds_id = ?", 
				new Object[]{dsid}, Integer.class);
		return count!=null && count > 0;
	}

	public ITemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	public void setTemplateEngine(ITemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public IPropertyEngine getPropertyEngine() {
		return propertyEngine;
	}

	public void setPropertyEngine(IPropertyEngine propertyEngine) {
		this.propertyEngine = propertyEngine;
	}
	
}
