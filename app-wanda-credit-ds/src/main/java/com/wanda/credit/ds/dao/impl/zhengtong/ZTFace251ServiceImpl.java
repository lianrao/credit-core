package com.wanda.credit.ds.dao.impl.zhengtong;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.zhengtong.ZT_Face_Result;
import com.wanda.credit.ds.dao.iface.zhengtong.IZTFace251Service;
@Service
@Transactional
public class ZTFace251ServiceImpl  extends BaseServiceImpl<ZT_Face_Result> implements IZTFace251Service{
	private static final long serialVersionUID = 1L;
	private final  Logger logger = LoggerFactory.getLogger(ZTFace251ServiceImpl.class);
	@Override
	public void save(List<ZT_Face_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
