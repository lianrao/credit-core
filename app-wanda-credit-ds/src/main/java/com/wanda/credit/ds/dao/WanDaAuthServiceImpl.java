package com.wanda.credit.ds.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.WanDa_Auth_Result;
import com.wanda.credit.ds.dao.iface.IWanDaAuthService;
@Service
@Transactional
public class WanDaAuthServiceImpl  extends BaseServiceImpl<WanDa_Auth_Result> implements IWanDaAuthService{
	private final  Logger logger = LoggerFactory.getLogger(WanDaAuthServiceImpl.class);

	@Override
	public void batchSave(List<WanDa_Auth_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}

}
