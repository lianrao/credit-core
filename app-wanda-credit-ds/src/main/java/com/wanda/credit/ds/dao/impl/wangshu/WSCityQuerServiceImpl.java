package com.wanda.credit.ds.dao.impl.wangshu;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_bank_result;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_CarIllegal_Result;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_CityQuery_Result;
import com.wanda.credit.ds.dao.iface.wangshu.IWSCarIllegalService;
import com.wanda.credit.ds.dao.iface.wangshu.IWSCityQueryResultService;
@Service
@Transactional
public class WSCityQuerServiceImpl  extends BaseServiceImpl<ZS_CityQuery_Result> implements IWSCityQueryResultService{
	private final  Logger logger = LoggerFactory.getLogger(WSCityQuerServiceImpl.class);

	@Override
	public void batchSave(List<ZS_CityQuery_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
