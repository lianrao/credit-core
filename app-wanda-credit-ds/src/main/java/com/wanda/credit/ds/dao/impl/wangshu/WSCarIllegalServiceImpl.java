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
import com.wanda.credit.ds.dao.iface.wangshu.IWSCarIllegalService;
@Service
@Transactional
public class WSCarIllegalServiceImpl  extends BaseServiceImpl<ZS_CarIllegal_Result> implements IWSCarIllegalService{
	private final  Logger logger = LoggerFactory.getLogger(WSCarIllegalServiceImpl.class);

	@Override
	public void batchSave(List<ZS_CarIllegal_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
