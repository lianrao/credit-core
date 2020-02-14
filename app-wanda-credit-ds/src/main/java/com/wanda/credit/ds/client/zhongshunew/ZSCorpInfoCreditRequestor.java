package com.wanda.credit.ds.client.zhongshunew;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.zhongshunew.ZS_Order;
import com.wanda.credit.ds.dao.iface.IZSNewOrderService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;

/**新中数新接口：组织结构查询 
 * add by liunan*/
@DataSourceClass(bindingDataSourceId="ds_zsCorpCredit_query")
public class ZSCorpInfoCreditRequestor extends BaseZS_NEW_DataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZSCorpInfoCreditRequestor.class);
	
	@Autowired
	public IZSNewOrderService newOrderService;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}新中数请求开始...",  prefix);

		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, String> reqparam = new HashMap<String, String>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setIncache("0");//不缓存
		logObj.setDs_id(ds.getId());
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		String resource_tag = Conts.TAG_SYS_ERROR;
		String resource_ds_tag = Conts.TAG_SYS_ERROR;
		
		try{
			//id,creditcode,regno,name,orgcode,mask,version,enttype
			//新中数企业ID,统一信用代码,企业注册号,企业名称,组织机构代码,查询掩码,个人标识版本号_返回个人标识码时生效 ,企业类型:1-企业 2-个体
			rets = new HashMap<String, Object>();
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			
			String[] infos = propertyEngine.readByIds("ds_zs_new_orgs", 
					"ds_zs_new_url", "ds_zs_new_UID", "ds_zs_new_SECURITY_KEY",
					"ds_zs_new_encode_version", "ds_zs_new_encode_paramsIds",
					"ds_zs_new_notparamids");
			String paramsStr = "?";
			for (String paramId : paramIds) {
				Object paramValue = ParamUtil.findValue(ds.getParams_in(), paramId);
                if(!StringUtil.isEmpty(paramValue)
                		&& !Arrays.asList(infos[6]).contains(paramId)) //排除非数据源参数 如acct_id
                	paramsStr = paramsStr + paramId + "=" + URLEncoder.encode(paramValue.toString(),"utf-8") + "&";
               
                reqparam.put(paramId, String.valueOf(paramValue));
			}
			if(!StringUtil.areNotEmpty(infos)){
				rets.put(Conts.KEY_RET_MSG, "模型参数有空值，请检查！");
				return rets;
			}
			//id、creditcode、regno、name、orgcode中至少有一个不能为空,当同时传入这五个参数的时候，匹配优先级依次是：id,creditcode,regno,name,orgcode
			boolean flag = false;
			for (String paramId : paramIds) {
				if(paramsStr.contains(paramId + "=")){
					flag = true;
				}
			}
			if(!flag){
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_ENCODE_FAILED);
				rets.put(Conts.KEY_RET_MSG, "主要业务参数全部为空，请检查!");
				return rets;
			}
			paramsStr = paramsStr.substring(0, paramsStr.length()-1);
			
			logObj.setReq_url(infos[1] + infos[0]);
			
			ZS_Order order = new ZS_Order();
			order.setTRADE_ID(trade_id);
			order.setZS_API(infos[0]);
			order.setCODE("-1");
			order.setMSG("尚未请求数据源");
			
			String url = infos[1] + infos[0] + paramsStr;
			logger.info("{} 新中数开始请求-----", prefix);
//			String res = "{\"code\":200,\"msg\":\"OK\",\"orgs\":[{\"ZCRQ\":\"2014-10-13\",\"ZFRQ\":\"2044-10-12\",\"JYFW\":\"房屋租售代理；房地产营销策划；经济信息咨询、企业管理咨询、投资咨询；市场调查；家政服务；婚姻介绍；婚庆礼仪服务；电脑图文设计（依法须经批准的项目，经相关部门批准后方可开展经营活动）＊＊＊\",\"JYDZ\":\"\",\"ZGRS\":\"0\",\"BZRQ\":\"2014-10-13\",\"BZJG\":\"大连市中山区市场监督管理局\",\"EMAIL\":\"\",\"URL\":\"\",\"REGNO\":\"210202000059754\",\"ID\":\"6cce8d3fcaa73ba0dc4e2d6b3df88950\",\"BGRQ\":\"2018-05-15\",\"JJHYDM\":\"房地产中介服务\",\"JGDM\":\"311486319\",\"CREDITCODE\":\"912102023114863198\",\"JJLXDM\":\"有限责任公司(自然人投资或控股)\",\"DHHM\":\"13664264157\",\"ZGMC\":\"\",\"JGLX\":\"企业\",\"JYZT\":\"在营（开业）\",\"FDDBR\":\"孟凡一\",\"XZQH\":\"辽宁省大连市中山区\",\"ZCZJ\":\"10.000000\",\"JGMC\":\"大连诚利房地产代理有限公司\",\"JGDZ\":\"辽宁省大连市中山区和阳街41号4单元1层2号\",\"JHBZ\":\"\",\"UPDATEDATE\":\"\",\"ZYBZ\":\"\"}]}";
			String res = callApi(url, prepareHeaders(infos[2], infos[3], trade_id), trade_id);
			logger.info("{} 新中数结束请求-----", prefix);
			
	        JSONObject json = JSONObject.fromObject(res);
        	String code = json.getString("code");
        	if("200".equals(code)){
        		//保存企业详情到数据库
    	        Map<String ,String> tagMap = saveIntoDB(json, order, ds.getParams_in());
    	        resource_tag = tagMap.get("tag");
    	        resource_ds_tag = tagMap.get("ds_tag");
    	        Map<String ,Object> result = new HashMap<String ,Object>();
    	        result.put("result", json.getJSONArray("orgs"));
    	        rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
    	        rets.put(Conts.KEY_RET_DATA, result);
    			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
    			rets.put(Conts.KEY_RET_MSG, "企业详情采集成功!");
    			
    			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
    			return rets;
        	}else if("400".equals(code)){
        		logger.info("{} 传入参数不正确:{}",prefix,json.getString("msg"));
        		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "校验不通过:传入参数不正确");
				return rets;
        	}else if("404".equals(code)){
        		logger.info("{} 查询不到:{}",prefix,json.getString("msg"));
        		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_B_NOTFOUND_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "查无记录");
				return rets;
        	}else{
        		logger.info("{} 组织机构查询失败:{}",prefix,json.getString("msg"));
        		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "组织机构查询失败");
				return rets;
        	}
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			resource_ds_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error(prefix+" 数据源处理时异常：{}",ex);
			
			/**如果是超时异常 记录超时信息*/
		    if(ExceptionUtil.isTimeoutException(ex)){	
		    	resource_tag = Conts.TAG_SYS_TIMEOUT;
		    	resource_ds_tag = Conts.TAG_SYS_TIMEOUT;
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);		    	
		    }
		    logObj.setState_msg(ex.getMessage());
		    rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{
			//log入库
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_ds_tag);
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, JSONObject.fromObject(reqparam), logObj);
		}
		return rets;
	}
	
	private Map<String ,String> saveIntoDB(JSONObject json, ZS_Order order, List<Param> params) throws ServiceException{
		Map<String ,String> tagMap = new HashMap<String, String>();
		String tag = "";
		String ds_tag = "";
		logger.info("{} 开始组织数据bean对象=====", order.getTRADE_ID());
		order.setAcct_id(String.valueOf(ParamUtil.findValue(params, "acct_id")));
		
		order.setENTID(String.valueOf(ParamUtil.findValue(params, "id")));
		order.setCREDITCODE(String.valueOf(ParamUtil.findValue(params, "creditcode")));
		order.setREGNO(String.valueOf(ParamUtil.findValue(params, "regno")));
		order.setORGCODE(String.valueOf(ParamUtil.findValue(params, "orgcode")));
		order.setENTNAME(String.valueOf(ParamUtil.findValue(params, "name")));
		
		order.setMASK(String.valueOf(ParamUtil.findValue(params, "mask")));
		order.setVERSION(String.valueOf(ParamUtil.findValue(params, "version")));
		order.setENTTYPE(String.valueOf(ParamUtil.findValue(params, "enttype")));

		order.setCODE(json.getString("code"));
        order.setMSG(json.getString("msg"));

		if(StringUtil.isEmpty(json) || !"200".equals(json.getString("code"))){
			tag = Conts.TAG_UNFOUND;
			ds_tag = Conts.TAG_UNFOUND;
		} else {
    		logger.info("{} 新中数企业照面信息数据库查询开始...", order.getTRADE_ID());
    		//String months = propertyEngine.readById("ds_zs_incache_month");
    		//按acct_id查询
    		Map<String,Object> getResultMap = newOrderService.inCached(order);//, months);
    		if("1".equals(getResultMap.get("STAT"))){
    			tag = Conts.TAG_FOUND_OLDRECORDS;
    		}else{
    			tag = Conts.TAG_FOUND_NEWRECORDS;
    		}
    		//去除acct_id因素
    		Map<String,Object> getResultDsMap = newOrderService.inCachedDs(order);//, months);
    		if("1".equals(getResultDsMap.get("STAT"))){
    			ds_tag = Conts.TAG_FOUND_OLDRECORDS;
    		}else{
    			ds_tag = Conts.TAG_FOUND_NEWRECORDS;
    		}
    		logger.info("{} 新中数组织机构数据库查询结束!", order.getTRADE_ID());
        }

		tagMap.put("tag", tag);
		tagMap.put("ds_tag", ds_tag);
		
		logger.info("{} 开始保存数据=====", order.getTRADE_ID());
		try {
			newOrderService.add(order);
		} catch (Exception e) {
			logger.info("{} 报存数据失败=====", order.getTRADE_ID());
			logger.error("保存数据失败：", e);
		}
		
		logger.info("{} 保存数据结束=====", order.getTRADE_ID());
		return tagMap;
	}
}
