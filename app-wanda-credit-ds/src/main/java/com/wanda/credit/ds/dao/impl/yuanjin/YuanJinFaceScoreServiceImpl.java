package com.wanda.credit.ds.dao.impl.yuanjin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.yuanjin.YJ_FaceScoreVO;
import com.wanda.credit.ds.dao.iface.yuanjin.IYuanJinFaceScoreService;

@Service
@Transactional
public class YuanJinFaceScoreServiceImpl implements IYuanJinFaceScoreService {
	private final Logger logger = LoggerFactory.getLogger(YuanJinFaceScoreServiceImpl.class);

	@Autowired
	private DaoService daoService;

	@Override
	public void save(YJ_FaceScoreVO vo) {
	   daoService.create(vo);
	}
}
