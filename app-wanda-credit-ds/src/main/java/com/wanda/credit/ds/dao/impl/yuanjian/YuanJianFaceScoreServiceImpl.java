package com.wanda.credit.ds.dao.impl.yuanjian;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.yuanjian.YJ_FaceScore_Result;
import com.wanda.credit.ds.dao.iface.yuanjian.IYuanJianFaceScoreService;

@Service
@Transactional
public class YuanJianFaceScoreServiceImpl implements IYuanJianFaceScoreService {
	private final Logger logger = LoggerFactory.getLogger(YuanJianFaceScoreServiceImpl.class);

	@Autowired
	private DaoService daoService;

	@Override
	public void save(YJ_FaceScore_Result vo) {
	   daoService.create(vo);
	}
}
