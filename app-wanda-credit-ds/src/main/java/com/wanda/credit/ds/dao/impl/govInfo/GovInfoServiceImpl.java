package com.wanda.credit.ds.dao.impl.govInfo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.domain.BaseDomain;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.govInfo.Gov_basicinfo_result;
import com.wanda.credit.ds.dao.iface.govInfo.IGovInfoService;
@Service
@Transactional
public class GovInfoServiceImpl  extends BaseServiceImpl<Gov_basicinfo_result> implements IGovInfoService{
	private final  Logger logger = LoggerFactory.getLogger(GovInfoServiceImpl.class);
	@Override
	public <T extends BaseDomain> void batchInfoSave(List<T> result) {
		try {
			this.daoService.create(result);
		} catch (Exception e) {
			logger.error("保存数据失败");
		}
	}
}
