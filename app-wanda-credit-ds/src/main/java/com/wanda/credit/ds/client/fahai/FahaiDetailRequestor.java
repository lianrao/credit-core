/**   
* @Description: 手机三维验证 集奥数据源
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.fahai;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author nan.liu
 *
 */
@DataSourceClass(bindingDataSourceId="ds_fahai_riskmge_detail_new")
public class FahaiDetailRequestor extends BaseDataSourceRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(FahaiDetailRequestor.class);	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IJiAoMobileCheckService fahaiService;
	public Map<String, Object> request(String trade_id, DataSource ds) {
		String url = propertyEngine.readById("ds_fahai_detail_url").trim();
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 法海-法人负面详情查询Begin {}" , prefix ,start);	
		String authcode = propertyEngine.readById("ds_fahai_detail_authcode");
		String regEx = propertyEngine.readById("ds_fahai_detail_auth_regex");
		boolean regEx_flag = "1".equals(propertyEngine.readById("ds_fahai_detail_auth_regex_flag"));
		int date_num = Integer.valueOf(propertyEngine.readById("ds_fahai_detail_cachedate"));
		String resource_tag = Conts.TAG_SYS_ERROR;
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();
		Map<String, Object> req_param = new HashMap<String,Object>();
		//计费标签
		Set<String> tags = new HashSet<String>();
		tags.add(Conts.TAG_SYS_ERROR);
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(url);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);		
		try{
			logger.info("{} 开始解析传入的参数" , prefix);
			String dataType = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String entryId = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			logger.info("{} 解析传入的参数成功" , prefix);
			req_param.put("dataType", dataType);
			req_param.put("entryId", entryId);
			String params = "authCode="+authcode+"&id="+dataType+":"+entryId;
			String content = fahaiService.findFahaiDetail(trade_id, ds.getId(), dataType, entryId, date_num);
			if(!StringUtil.isEmpty(content)){
				logger.info("{} 查询数据开始了",trade_id);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setIncache("1");
				resource_tag = Conts.TAG_INCACHE_FOUND;
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_DATA, JSONObject.parse(content));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "采集成功!");
			}else{
				if(!isIdReg(regEx,entryId) && regEx_flag){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
					rets.put(Conts.KEY_RET_MSG, "传入参数不正确");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
				logger.info("{} 缓存未查询到数据,开始远程调用",trade_id);
				Map<String, Object> rspMsg = RequestHelper.doGetRetFull(url+"?"+params, null, null, false,null,
		                "UTF-8");
				String res = String.valueOf(rspMsg.get("res_body_str"));
				logger.error("{} 调用成功,返回信息:{}",trade_id,res);
				if (StringUtil.isEmpty(res) || "null".equals(res)) {
					logger.error("{} 返回信息为空",trade_id);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
					return rets;
				}
				JSONObject json = JSONObject.parseObject(res);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				if(!"s".equals(json.getString("code"))){//调用失败
					resource_tag = Conts.TAG_TST_FAIL;
					if(json.getString("msg").contains("您没有该维度查询权限")){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
						rets.put(Conts.KEY_RET_MSG, "传入参数不正确");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						return rets;
					}
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else{
					JSONObject result = new JSONObject();
					result.put("totalCount", json.get("totalCount"));
					result.put(dataType, json.get(dataType));
					if((int)json.get("totalCount")>0){
						resource_tag = Conts.TAG_FOUND;
						fahaiService.saveFahaiDetail(trade_id, ds.getId(), dataType, entryId, JSONObject.toJSONString(result));
					}else{
						resource_tag = Conts.TAG_UNFOUND;
					}					
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					rets.put(Conts.KEY_RET_DATA, result);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "采集成功!");
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("{} 法海负面交易处理异常：{}" , prefix , e.getMessage());
			resource_tag = Conts.TAG_TST_FAIL;
			if (e instanceof ConnectTimeoutException) {				
				logger.error("{} 连接远程数据源超时" , prefix);				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				//设置标签
				resource_tag = Conts.TAG_SYS_TIMEOUT;
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
		}finally{			
			rets.put(Conts.KEY_RET_TAG,new String[]{resource_tag});
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			long dsLogStart = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, req_param, logObj);
			logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
		}
		logger.info("{} 法海负面详情End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;	
	}
	public static boolean isIdReg(String regEx,String param) {
		return Pattern.matches(regEx, param);
	}
}
