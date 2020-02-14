package com.wanda.credit.ds.client.dsconfig.services;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.dsconfig.main.ResolveContext;
import com.wanda.credit.dsconfig.support.DsCfgUtil;

/**
 * @description
 * @author wuchsh
 * @version 1.0
 * @createdate 2017年8月31日 上午9:53:02
 * 
 */

@Service
public class JiAoMobileNameCheckService {
	private final static Logger logger = LoggerFactory.getLogger(JiAoMobileNameCheckService.class);

	@Autowired
	private DaoService daoService;

	public String readCache() {
		List<Map<String, Object>> ls = daoService.getJdbcTemplate().queryForList(
				"select content from cpdb_ds.T_DS_DMP_MOBILENAMECHK " + "where name = ? and mobile = ?  "
						+ " and create_date >= (SYSDATE - 30) "
						+ " order by create_date desc",
				new Object[] { ResolveContext.getModelData("name"),
						CryptUtil.encrypt((String) ResolveContext.getModelData("mobile")) });
		String content = CollectionUtils.isNotEmpty(ls) ? (String) ls.get(0).get("content") : null;
		if(StringUtils.isBlank(content))return "succ";
		JSONObject rspData = (JSONObject) DsCfgUtil.convert2JsonObj(content);
		logger.info("{} start to onCacheCondition {}",
				ResolveContext.getTradeId(),onCacheCondition((JSONObject) rspData));
		if (onCacheCondition((JSONObject) rspData)) {
			ResolveContext.setModelData("rawRspData", content);
			ResolveContext.setRetData(content);
			ResolveContext.setModelData("rspData", rspData);
			String tag = resolveTag(rspData);
			ResolveContext.resetTag(tag);
			ResolveContext.getDsLog().setIncache("1");
			ResolveContext.exit();
		}else{
			logger.warn("{} 查询到了数据记录，但不满足缓存条件 {}",ResolveContext.getTradeId(),content);
		}
		
        return "succ";
	}

	/** 只有code 0 or 1 or 99 时才查询缓存 */
	private boolean onCacheCondition(JSONObject rspData) {
		if(rspData.getString("ISPNUM")==null)
			return false;
		JSONArray rsl = rspData.getJSONArray("RSL");
		if (CollectionUtils.isNotEmpty(rsl)) {
			JSONObject rs = ((JSONObject) rsl.get(0)).getJSONObject("RS");
			if (rs != null) {
				Object code = rs.get("code");
				if ("0".equals(code) || "1".equals(code)|| "99".equals(code))
					return true;
			}
		}
		return false;
	}

	private String resolveTag(JSONObject rspData) {
		if(rspData.getString("ISPNUM")==null)
			return "error";
		String yyinshang = rspData.getJSONObject("ISPNUM").getString("isp");
		if ("移动".equals(yyinshang))
			return "check_yd_incache_found";
		if ("联通".equals(yyinshang))
			return "check_lt_incache_found";
		if ("电信".equals(yyinshang))
			return "check_dx_incache_found";
		else {
			logger.error("{} 不能识别的运营商信息 {}", ResolveContext.getTradeId(), rspData);
		}
		return null;
	}
}
