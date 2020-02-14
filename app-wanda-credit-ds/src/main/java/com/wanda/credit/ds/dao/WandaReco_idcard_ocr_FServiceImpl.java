package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.WandaReco_idcard_ocr_F;
import com.wanda.credit.ds.dao.iface.IWandaReco_idcard_ocr_FService;

@Service
@Transactional
public class WandaReco_idcard_ocr_FServiceImpl  extends BaseServiceImpl<WandaReco_idcard_ocr_F> implements IWandaReco_idcard_ocr_FService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private final  Logger logger = LoggerFactory.getLogger(FfCreditScoreFlagCheckServiceImpl.class);
}
