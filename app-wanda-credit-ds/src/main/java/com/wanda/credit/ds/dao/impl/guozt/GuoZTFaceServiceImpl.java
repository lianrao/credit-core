package com.wanda.credit.ds.dao.impl.guozt;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.guozt.GuoZT_Face_Result;
import com.wanda.credit.ds.dao.iface.guozt.IGuoZTFaceService;
@Service
@Transactional
public class GuoZTFaceServiceImpl  extends BaseServiceImpl<GuoZT_Face_Result> implements IGuoZTFaceService{
	private final  Logger logger = LoggerFactory.getLogger(GuoZTFaceServiceImpl.class);
	@Override
	public void batchSave(List<GuoZT_Face_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
