package com.wanda.credit.ds.dao.impl.yitu;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.yitu.Yitu_auth_photo;
import com.wanda.credit.ds.dao.iface.yitu.IYTAuthPhotoService;
@Service
@Transactional
public class YTAuthPhotoServiceImpl  extends BaseServiceImpl<Yitu_auth_photo> implements IYTAuthPhotoService{
	private final  Logger logger = LoggerFactory.getLogger(YTAuthPhotoServiceImpl.class);

	@Override
	public void batchSave(List<Yitu_auth_photo> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
