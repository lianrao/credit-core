package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.ffscore.FfCreditScore_Flag;
import com.wanda.credit.ds.dao.iface.IFfCreditScoreInCheckService;
@Service
@Transactional
public class FfCreditScoreFlagCheckServiceImpl  extends BaseServiceImpl<FfCreditScore_Flag> implements IFfCreditScoreInCheckService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private final  Logger logger = LoggerFactory.getLogger(FfCreditScoreFlagCheckServiceImpl.class);
}
