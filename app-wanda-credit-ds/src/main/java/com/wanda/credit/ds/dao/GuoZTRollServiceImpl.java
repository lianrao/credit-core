package com.wanda.credit.ds.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Guozt_Roll_check_result;
import com.wanda.credit.ds.dao.iface.IGuoZTRollService;

@Service
@Transactional
public class GuoZTRollServiceImpl extends BaseServiceImpl<Guozt_Roll_check_result> implements IGuoZTRollService {
	private final Logger logger = LoggerFactory.getLogger(GuoZTRollServiceImpl.class);

	@Override
	public void batchSave(List<Guozt_Roll_check_result> result) {
		// TODO Auto-generated method stub
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
	public String findScore(String collage){
		String sql ="SELECT NVL(M.LVL,'00') FROM CPDB_MK.T_ETL_IPIN_COLLEGE M WHERE M.COLLEGE=? AND ROWNUM=1 ";
		String result = this.daoService.findOneBySql(sql, new Object[]{collage},String.class);
		return result;
	}
}
