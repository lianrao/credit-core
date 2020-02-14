package com.wanda.credit.ds.dao;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.iface.IShuiJingMobileService;
/**
* @Title: 返回处理
* @Description: 水晶球手机号风险数据保存
* @author wenpeng.li@99bill.com  
* @date 2015年9月21日 下午09:43:12
* @version V1.0
*/
@Service
@Transactional
public class ShuiJingMobileServiceImpl implements IShuiJingMobileService{
	private final Logger logger = LoggerFactory
	.getLogger(ShuiJingMobileServiceImpl.class);
	@Autowired
	private DaoService daoService;
	public void save(String trade_id,String result,String inpuMobileCode) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		try {
			JSONObject s = JSONObject.fromObject(result);
			JSONObject data=new JSONObject();
			if(s.get("data")!=null){
				  data = (JSONObject) s.get("data");
				}
			JdbcTemplate jdbcTemplate = daoService.getJdbcTemplate();
			String sql="INSERT INTO T_DS_SJQ_MOBILERISK_RESULT(TRADE_ID,RESPCD,MSG,MOBILE,RISK_LEVEL,HIT_BADCARDHOLDER,HIT_ONLINEBLACK,HIT_OTHERBLACK,INPUT_MOBILECODE) VALUES(?,?,?,?,?,?,?,?,?)";
		    Object[] paramIn={trade_id, s.get("respCd"), s.get("msg"),data.get("mobile"),data.get("riskLevel"),data.get("hitBadCardHolder"),data.get("hitOnlineBlack"),data.get("hitOtherBlack"),inpuMobileCode};
			jdbcTemplate.update(sql, paramIn);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("{} 外部水晶球个人手机号风险数据插入时异常：{}", prefix, ex.getMessage());
		}
	}

}
