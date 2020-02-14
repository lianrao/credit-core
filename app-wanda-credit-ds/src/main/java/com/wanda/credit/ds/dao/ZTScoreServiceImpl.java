package com.wanda.credit.ds.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.ZT_Score_Result;
import com.wanda.credit.ds.dao.iface.IZTScoreService;
@Service
@Transactional
public class ZTScoreServiceImpl  extends BaseServiceImpl<ZT_Score_Result> implements IZTScoreService{
	private final  Logger logger = LoggerFactory.getLogger(ZTScoreServiceImpl.class);

	@Override
	public void batchSave(List<ZT_Score_Result> score) {
		try {
			this.add(score);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}

}
