package com.wanda.credit.ds.dao.impl.police;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.police.Police_Face_Result;
import com.wanda.credit.ds.dao.iface.police.IPoliceFaceService;
@Service
@Transactional
public class PoliceFaceServiceImpl  extends BaseServiceImpl<Police_Face_Result> implements IPoliceFaceService{
	private final  Logger logger = LoggerFactory.getLogger(PoliceFaceServiceImpl.class);
	@Override
	public void batchSave(List<Police_Face_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
