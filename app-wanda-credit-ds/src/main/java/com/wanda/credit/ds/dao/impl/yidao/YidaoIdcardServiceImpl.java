package com.wanda.credit.ds.dao.impl.yidao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_Idcard_result;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoIdcardService;
@Service
@Transactional
public class YidaoIdcardServiceImpl  extends BaseServiceImpl<Yidao_Idcard_result> implements IYidaoIdcardService{
	private final  Logger logger = LoggerFactory.getLogger(YidaoIdcardServiceImpl.class);

	@Override
	public void batchSave(List<Yidao_Idcard_result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
