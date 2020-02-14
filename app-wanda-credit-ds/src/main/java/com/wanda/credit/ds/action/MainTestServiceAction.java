package com.wanda.credit.ds.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.MD5;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.ds.DataSourceService;

/**
 * @Title: 征信数据源主服务接口
 * @Package com.wanda.credit.ds.action
 * @Description: 征信数据源主服务接口
 * @author chenglin.xiao
 * @date 2016年6月17日 下午12:16:36
 * @version V1.0
 */
@Controller
@RequestMapping(value="/inner/test/dataService")
public class MainTestServiceAction {
    private final Logger logger = LoggerFactory.getLogger(MainTestServiceAction.class);
    @Autowired
    private DataSourceService dataSourceService;
    @Autowired
	public DaoService daoService;
    @RequestMapping(value = "fetch", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> service(final HttpServletResponse response,
                                      final HttpServletRequest request,
                                      @RequestHeader(name = "sign", required = false) String sign,
                                      @RequestHeader(name = "trade_id", required = false) String trade_id,
                                      @RequestHeader(name = "acct_id", required = false) String acct_id,
                                      @RequestBody final DataSource ds)
            throws Exception {
    	Map<String, Object> rets = new HashMap<String, Object>();
    	if(StringUtil.isEmpty(trade_id) || StringUtil.isEmpty(sign) || StringUtil.isEmpty(acct_id)){
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
			rets.put(Conts.KEY_RET_MSG, "交易号或签名不能为空");
			logger.warn("交易号为空");
			return rets;
		}
        String prefix = trade_id +" "+ Conts.KEY_SYS_AGENT_HEADER; //流水号标识
        /**检测数据源是否已经登记 开发环境使用 wcs add*/
		if(!checkDs(ds.getId())){
			logger.error("{} 请提前登记数据源id {}",trade_id,ds.getId());
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
			rets.put(Conts.KEY_RET_MSG, "请提前登记数据源id");
			return rets;
		}		
		if(checkRequest(trade_id)){
			logger.error("{} 请求交易号重复",trade_id);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
			rets.put(Conts.KEY_RET_MSG, "请求交易号重复");
			return rets;
		}
        logger.info("{} 收到HTTP请求!",prefix);
        request.setCharacterEncoding("utf-8");
        logger.info("{} 入参信息为:{}",prefix,JSON.toJSONString(ds));
        String before_sign = ds.getId()+PropertyEngine.get("ds_test_outer_private_key")+trade_id+acct_id;
        logger.info("{} 加签前结果为:{}",prefix,before_sign);
        String localSign = MD5.uppEncodeByMD5(before_sign);
        logger.info("{} 账号信息为:{}",prefix,acct_id);
        logger.info("{} 传入sign:{};本地加签结果:{}",prefix,sign,localSign);
        if(!localSign.equals(sign)){
        	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
			rets.put(Conts.KEY_RET_MSG, "签名有误");
			logger.warn("{} 签名不正确",prefix);
			return rets;
        }
        if(!StringUtil.isEmpty(trade_id) && ds!=null){
            rets =  dataSourceService.fetch(trade_id,ds);
        }
        logger.info("{} HTTP请求处理完成!",prefix);
        return rets;
    }
    
    private boolean checkDs(String dsid) {
		Integer count = daoService.getJdbcTemplate().queryForObject(
				"select count(1) count from cpdb_mk.t_etl_datasource_idname where ds_id = ?", 
				new Object[]{dsid}, Integer.class);
		return count!=null && count > 0;
	}
    private boolean checkRequest(String trade_id) {
		Integer count = daoService.getJdbcTemplate().queryForObject(
				"select sum(cnt) cn1 from ( "
				+ " select count(1) cnt from cpdb_ds.t_ds_datasource_log_new d where trade_id=? "
				+ " union "
				+ " select count(1) cnt from cpdb_ds.t_ds_datasource_log_noid d where trade_id=?) ", 
				new Object[]{trade_id,trade_id}, Integer.class);
		return count!=null && count > 0;
	}
}
