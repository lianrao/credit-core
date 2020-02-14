package com.wanda.credit.ds.dao.impl.xiaoshi;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.xiaoshi.XiaoShi_visit_flow;
import com.wanda.credit.ds.dao.iface.xiaoshi.IXiaoshiVisitFlowService;
@Service
@Transactional
public class XiaoShiVisitFlowImpl   implements IXiaoshiVisitFlowService{
	private final  Logger logger = LoggerFactory.getLogger(XiaoShiVisitFlowImpl.class);
	@Autowired
	private DaoService daoService;
	@Override
	public void batchSave(XiaoShi_visit_flow result) {
		try {
			String sql = " insert into t_ds_head_visit_flow(id,devsn,trade_id,pepolecount,timestamp,vipcount, "
					+" ordinarycount,malecount,agecount1,agecount2,agecount3,agecount4,agecount5,reserve1,reserve2,reserve3) "
					+" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			this.daoService.getJdbcTemplate().update(sql,
					result.getId(),
					result.getDevSn(),
					result.getTrade_id(),
					result.getPepoleCount(),
					result.getTimestamp(),
					result.getVIPCount(),
					result.getOrdinaryCount(),
					result.getMaleCount(),
					result.getAgeCount1(),
					result.getAgeCount2(),
					result.getAgeCount3(),
					result.getAgeCount4(),
					result.getAgeCount5(),
					result.getReserve1(),
					result.getReserve2(),
					result.getReserve3());
		} catch (Exception e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
