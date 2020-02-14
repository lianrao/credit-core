package com.wanda.credit.ds.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Guozt_Black_Car_Result;
import com.wanda.credit.ds.dao.iface.IGuoZTBlackCarService;

@Service
@Transactional
public class GuoZTBlackCarServiceImpl  extends BaseServiceImpl<Guozt_Black_Car_Result> implements IGuoZTBlackCarService{
	private final  Logger logger = LoggerFactory.getLogger(GuoZTBlackCarServiceImpl.class);

	@Override
	public void batchSave(List<Guozt_Black_Car_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void updateToken(String tokenStr,String trade_id) {
		try {
			String sql = "UPDATE CPDB_DS.T_DS_GUOZT_CARTOKEN T SET T.TOKEN=?,T.UPDATE_TIME=SYSDATE WHERE T.TRADE_ID=?  AND T.STATUS='01'  ";
			this.daoService.getJdbcTemplate().update(sql,tokenStr,trade_id);		
		}catch (Exception e) {
			logger.error("更新数据失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String getTokenSql(String trade_id) {
		String sql = "SELECT T.TOKEN  FROM CPDB_DS.T_DS_GUOZT_CARTOKEN T WHERE T.TRADE_ID=? AND T.STATUS='01' AND ROWNUM=1";
		String resultMap = this.daoService.findOneBySql(sql,new Object[]{trade_id},String.class);
		return resultMap;
	}
}
