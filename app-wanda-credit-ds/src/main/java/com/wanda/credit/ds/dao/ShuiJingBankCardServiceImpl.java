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
import com.wanda.credit.ds.dao.iface.IShuiJingBankCardService;

/**
* @Title: 返回处理
* @Description: 水晶球银行卡号风险数据保存
* @author wenpeng.li@99bill.com  
* @date 2015年9月21日 下午09:42:31 
* @version V1.0
*/
@Service
@Transactional
public class ShuiJingBankCardServiceImpl implements IShuiJingBankCardService{
	private final Logger logger = LoggerFactory
	.getLogger(ShuiJingBankCardServiceImpl.class);
	@Autowired
	private DaoService daoService;
	public void save(String trade_id,String inputBankCode,String result) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		try {
			JSONObject s = JSONObject.fromObject(result);
			JSONObject data=new JSONObject();
			if(s.get("data")!=null){
			  data = (JSONObject) s.get("data");
			}
			JdbcTemplate jdbcTemplate = daoService.getJdbcTemplate();
			String sql="INSERT INTO T_DS_SJQ_BANKCARDRISK_RESULT(RESPCD,MSG,CARD_NO,RISK_LEVEL,HIT_BADCARDHOLDER,HIT_FRAUDTRANS,HIT_CASERELATED,HIT_OFFLINEBLACK,HIT_ONLINEBLACK,HIT_OTHERBLACK,INPUT_BANKCARD,TRADE_ID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
			Object[] paramIn = { s.get("respCd"), s.get("msg"),  data.get("cardNo"), data.get("riskLevel"),
					 data.get("hitBadCardHolder"), data.get("hitFraudTrans"),  data.get("hitCaseRelated"),
					 data.get("hitOfflineBlack"), data.get("hitOnlineBlack"), data.get("hitOtherBlack"),inputBankCode,trade_id};
			jdbcTemplate.update(sql, paramIn);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("{} 外部水晶球个人银行卡号欺诈数据插入时异常：{}", prefix, ex.getMessage());
		}
	}
}
