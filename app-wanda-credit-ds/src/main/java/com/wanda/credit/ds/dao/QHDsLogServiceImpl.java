package com.wanda.credit.ds.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.qianhai.QHDsLogVO;
import com.wanda.credit.ds.dao.iface.IQHLogService;

@SuppressWarnings("unchecked")
@Service
public class QHDsLogServiceImpl implements IQHLogService {
	private final Logger logger = LoggerFactory.getLogger(QHDsLogServiceImpl.class);
	
	private static final String queryLogOnCurrMonthSQL = ""
			+ " SELECT COUNT(1) AS COUNT FROM CPDB_DS.T_DS_QH_DSLOG WHERE DS_ID = ? AND CARDNO=? "
			+ " AND TO_CHAR(CREATE_DATE,'MM') = TO_CHAR(SYSDATE,'MM')";


	@Autowired
	private DaoService daoService;

	@Override
	public int queryLogOnCurrMonth(String ds_id, String encryCardNo)
			throws Exception {
		List<Map<String, Object>> result = 
				this.daoService.getJdbcTemplate().queryForList(queryLogOnCurrMonthSQL,ds_id,encryCardNo);
		if(CollectionUtils.isNotEmpty(result)){
			Object count = result.get(0).get("count");
			if(count != null){
				return Integer.valueOf(count.toString());
			}
		}
		return 0;
	}

	@Override
	@Transactional
	public void addNewLog(String trade_id, String ds_id, String name,
			String encryCardNo) throws Exception {
		QHDsLogVO vo = new  QHDsLogVO();
		vo.setCardNo(encryCardNo);vo.setName(name);
		vo.setTrade_id(trade_id);vo.setDs_id(ds_id);
		this.daoService.create(vo);
	}
}
