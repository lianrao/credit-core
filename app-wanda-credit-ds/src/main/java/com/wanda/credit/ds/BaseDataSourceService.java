package com.wanda.credit.ds;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorNoticeService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.counter.GlobalCounter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.*;
import com.wanda.credit.common.agent.ds.DataSourceDispatch;
import com.wanda.credit.common.template.iface.IPropertyEngine;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BaseDataSourceService {
	private final  Logger logger = LoggerFactory.getLogger(BaseDataSourceService.class);
	private final String ds_error_warn_sms = "数据源区间异常次数达到指定阈值,";
	private final String ds_error_on_sms = "数据源动态调整已启用,";
	public final int templateId = 19714437;
	private static Map<String,Object> implObjectMap = null;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorNoticeService executorNoticeService;
	@Autowired
	public DataSourceDispatch dsDispatch;
	public final String  tail_errorlist="_errorlist";
	public final String  tail_send_time="_sendtime";
	public final String header =Conts.KEY_SYS_AGENT_HEADER;
	
	public boolean isfuseOff(final String trade_id,String ds_ids) throws ServiceException{
		final String prefix = trade_id +" "+ header; //流水号标识
		final String ds_id = ds_ids;
//		final String ds_name = ds.getName();
		String max_error_num_strs = propertyEngine.readById("ds_max_error_num");//数据源error熔断阈值
		int ds_sms_time_rate_mini = Integer.parseInt(propertyEngine.readById("ds_sms_time_rate"));//sms频率，单位分
		final long ds_sms_time_rate_ms=ds_sms_time_rate_mini*60*1000;//单位毫秒
		final String mobiles = propertyEngine.readById("sys_send_mobiles01");
		String ds_error_flag = ds_id + tail_errorlist;
		final int ds_error_count_value=GlobalCounter.getCount(ds_error_flag);
		//熔断阈值定制化
		int  max_error_num=getDsErrorConfigMax(max_error_num_strs, max_error_num_strs.indexOf(ds_id)>-1?ds_id:"*");
		logger.info("{} 监控情况：KEY:{},COUNT:{},MAX_LIMIT:{}", new Object[]{prefix,ds_error_flag,ds_error_count_value,max_error_num});
		if(ds_error_count_value>=max_error_num){
			logger.warn("{} 熔断阈值触达,自动关闭开启!!",prefix);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Date send_time=new Date();
						Long now=send_time.getTime();
						String last_send_time_key = ds_id+tail_send_time;
						String last_send_time_value=GlobalCounter.getString(last_send_time_key);
						boolean is_send=false;
						if(last_send_time_value!=null){
							long timsmap_redis=Long.parseLong(last_send_time_value);
							logger.info("{} 当前时间戳:{},近一次发送的时间戳:{},间隔周期(ms):{}", new Object[]{prefix,now,timsmap_redis,ds_sms_time_rate_ms});
							if((now-timsmap_redis)>=ds_sms_time_rate_ms){
								is_send=true;
							}
						}else{
							is_send=true;
						}
						if(is_send){
							executorNoticeService.sendSmsMsg(mobiles,ds_error_warn_sms +"接口ID:"+ds_id
									 +",当前异常汇总:"+ds_error_count_value,"预警提示");
							logger.info("{} 告警短信发送完毕.",prefix);
							GlobalCounter.setString(last_send_time_key, now+"");
						}else{
							logger.error("{} 短信发送时间间隔小于设定时限，不发送短信！",prefix);
						}
					} catch (Exception e) {
						logger.error("{}短信发送时发生异常：{}", prefix,ExceptionUtil.getTrace(e));
					}
				}
			}).start();
			if(!GlobalCounter.exists(tail_errorlist, ds_id)){
				String ds_ab_watch = propertyEngine.readById("ds_ab_watch");
				String ds_ab_expire_sec = propertyEngine.readById("ds_ab_expire_sec");
				GlobalCounter.regist(tail_errorlist, ds_id, Integer.parseInt(ds_ab_expire_sec));
				String forwardDsId = findABDs(ds_ab_watch,ds_id,prefix);
				if(!forwardDsId.equalsIgnoreCase(ds_id)){
					executorNoticeService.sendSmsMsg(mobiles, ds_error_on_sms +"当前已熔断:"+ds_id
							+",流量切换至:"+forwardDsId
							+",预计恢复时间："+new SimpleDateFormat(DateUtil.DATETIMESHOWFORMAT).format(new Date(System.currentTimeMillis()+(Integer.parseInt(ds_ab_expire_sec)*1000)))
							,"切流提示");
					logger.warn("{} 数据源重定向策略准备开始...{}",prefix,ds_ab_watch);
				}
			}
			return true;
		}
		return false;
	}
	/**从配置参数字符串（如dsid1:300,dsid2:50,*:10）取得对应ds的熔断阈值，默认10*/
	protected static int getDsErrorConfigMax(String numstring ,String dsid){
		//numstring="dsid1:300,dsid2:50,*:10";
		String[] arr= numstring.split(",");
		for (int i = 0; i < arr.length; i++) {
			String item=arr[i];
			if(item.indexOf(dsid)>-1)
				return Integer.parseInt(item.substring(item.indexOf(":")+1));
		}
		return 10;
	}

	/**
	 * 判断交易返回是否成功(包含预警)
	 * @param result
	 * @return
     */
	public static boolean isSuccess(Map<String, Object> result) {
		if (result == null)
			return false;
		CRSStatusEnum retstatus = CRSStatusEnum.valueOf(result.get(
				Conts.KEY_RET_STATUS).toString());
		return CRSStatusEnum.STATUS_SUCCESS.equals(retstatus);
	}
	/**
	 * 判断交易是否需要捕获异常
	 * @param result
	 * @return ret
	 */
	public boolean isErr(Map<String, Object> result){
		String ds_errors_watch = propertyEngine.readById("ds_errors_watch");
		Object obj_retstatus=result.get(Conts.KEY_RET_STATUS);
		if(obj_retstatus!=null&&obj_retstatus instanceof CRSStatusEnum){
			String status_code =((CRSStatusEnum)obj_retstatus).getRet_sub_code();
			if(StringUtil.areNotEmpty(status_code,ds_errors_watch)
					&&StringUtil.isStrInStrs(status_code, ds_errors_watch.split(","))){ 
				return true;
			}
		}
		return false;
	}
	public boolean isTimeout(String trade_id,String ds_id,long cost){
		String ds_errors_watch = propertyEngine.readById("ds_errors_timeout_watch");
		if(StringUtils.isEmpty(ds_id)||StringUtils.isEmpty(ds_errors_watch))
			return false;
		logger.info("{} 数据源超时计数开始:{}",trade_id,ds_errors_watch);
		for(String err:ds_errors_watch.split(",")){
			String[] errs = err.split(":");
			if(errs[0].equals(ds_id)){
				long err_cost = Integer.valueOf(errs[1]);
				if(cost>=err_cost){
					logger.info("{} 数据源:{} 超时计数+1",trade_id,ds_id);
					return true;
				}					
			}
		}		
		return false;
	}
	/**
	 * 反射查找数据源requestor实现
	 * @param ds_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends BaseDataSourceRequestor> T matchDataSourceRequestor(String ds_id) {
		if (implObjectMap == null) {
			ApplicationContext ac = SpringContextUtils.getContext();
			implObjectMap = ac.getBeansWithAnnotation(DataSourceClass.class);
		}
		for(Object obj :  implObjectMap.values()){
			if (obj != null) {
				DataSourceClass dataSourceClass = obj.getClass().getAnnotation(
						DataSourceClass.class);
				if (dataSourceClass.bindingDataSourceId().equalsIgnoreCase(ds_id)) {
					return (T) obj;
				}
			}
		}
		return null;
	}
	
	private final String start_suffix  ="mock_resp_";
	private final String all_suffix  ="*";
	/**
	 * 是否启用mock
	 * @param ds_id
	 * @return
	 */
	public boolean enableMock(String ds_id){
		String mock_exprs = propertyEngine.readById("sys.credit.client.mock.expr");
		for(String expr : mock_exprs.split(",")){
			if(expr.equalsIgnoreCase(ds_id) || expr.equals(all_suffix)){
				 return true;
			}
		}
		return false;
	}
	/**
	 * mock报文
	 * @param ds_id
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public Map<String, Object> getMockResponse(String ds_id) throws Exception {
		String respBody = SystemPropertiesUtil.getSysProperty(start_suffix + ds_id);
		if(!StringUtil.isEmpty(respBody)){
			return JSONObject.parseObject(respBody, Map.class);
		}else
			throw new ServiceException("mock报文尚未配置!");
	}
	/**数据源适配器强制路由
	 * @throws Exception */
	protected String getDsHint(String prefix,String prod_id,
			String dsid,String hint_ds_switch,String route_dsIds,boolean special_acct) throws Exception{
		String result_ds = dsid;
		if(!StringUtil.isEmpty(prod_id) 
				&& !StringUtil.isEmpty(dsid)
				&& !StringUtil.isEmpty(hint_ds_switch)){
			boolean is_hint_ds = false;
			String[] ds_switch = hint_ds_switch.split(",");
			for (String ds_prods:ds_switch) {
				String[] ds_prod = ds_prods.split(":");
				if(prod_id.equals(ds_prod[0])){
					result_ds = ds_prod[1];
					logger.info("{} 数据源适配器强制路由到:{}", prefix,result_ds);
					is_hint_ds = true;
					break;
				}
			}
//			logger.info("{} 变量获取值:{}", prefix,route_dsIds);
			if(!is_hint_ds && !StringUtil.isEmpty(route_dsIds) && !special_acct){//不是强制路由,不是特殊账户,按比例分配
				for(String route_dsId:route_dsIds.split(",")){
//					logger.info("{} 变量获取值:{}", prefix,route_dsId);
					String[] route_switch = route_dsId.split("%");
//					logger.info("{} 变量获取值route_switch:{}", prefix,route_switch);
					if(route_switch.length>=2){
						if(isContainStr(route_switch[1],dsid)){
							if("all".equals(route_switch[0]) ){//适合所有产品
								result_ds = dsDispatch.dispatch("all", route_switch[1]);
								GlobalCounter.sign("all-"+result_ds,300);
								logger.info("{} 流量监控for all:+1", prefix);
								logger.info("{} 数据源适配器按比例路由到:{}", prefix,result_ds);
								break;
							}else if(prod_id.equals(route_switch[0])){
								result_ds = dsDispatch.dispatch(prod_id, route_switch[1]);
								GlobalCounter.sign(prod_id+"-"+result_ds,300);
								logger.info("{} 流量监控for {}:+1", prefix,prod_id);
								logger.info("{} 数据源适配器按比例路由到:{}", prefix,result_ds);
								break;
							}							
						}
					}					
				}				
			}
		}		
		return result_ds;
	}
	public boolean isContainStr(String dsRatio,String ds_id){
		if(StringUtil.isEmpty(dsRatio) || 
				StringUtil.isEmpty(ds_id))
			return false;
		String[] p = dsRatio.split("=");
		for(String ds:p[0].split(":")){
			if(ds.equals(ds_id)){
				return true;
			}
		}
		return false;
	}
	public boolean isSpecialAcct(String special_acct,String ds_id){
		if(StringUtil.isEmpty(special_acct) || 
				StringUtil.isEmpty(ds_id))
			return false;
		for(String ds:special_acct.split(",")){
			if(ds.equals(ds_id)){
				return true;
			}
		}
		return false;
	}
	/** 从AB配置中查找最佳DS（如果熔断） 
	 * @throws ServiceException */
	public String findABDs(String ds_ab_watch, String dsid,String trade_id)
			throws ServiceException {
		String next_dsid = dsid;
		logger.info("{} AB方案切换匹配开始:{}",trade_id,ds_ab_watch);
		if (!StringUtil.isEmpty(ds_ab_watch) && GlobalCounter.exists(tail_errorlist, dsid)) {
			logger.info("{} 数据源:{}需要进行AB切换",trade_id,dsid);
			String[] abs = ds_ab_watch.split(",");
			for (String ab : abs) {
				if(!next_dsid.equals(dsid)){
					break;
				}
				String[] confs = ab.split(":");
				for (int i = 0; i < confs.length; i++) {
					if(!next_dsid.equals(dsid)){
						break;
					}
					if (dsid.equalsIgnoreCase(confs[i])) {
						for(String conf:confs){
							if(!conf.equals(dsid) && !GlobalCounter.exists(tail_errorlist, conf)){
								next_dsid = conf;
								break;
							}
						}
					}
				}
			}
		}
		logger.warn("{} 数据源重定向策略返回数据{}-->{}",trade_id,dsid,next_dsid);
		return next_dsid;
	}
}
