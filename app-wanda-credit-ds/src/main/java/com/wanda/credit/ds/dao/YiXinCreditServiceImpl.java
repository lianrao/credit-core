package com.wanda.credit.ds.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.yixin.YXBlackListVO;
import com.wanda.credit.ds.dao.domain.yixin.YXLoanRecordsVO;
import com.wanda.credit.ds.dao.domain.yixin.YXLoanoverdueVO;
import com.wanda.credit.ds.dao.iface.IYiXinCreditService;

@Service
@Transactional
public class YiXinCreditServiceImpl implements IYiXinCreditService {

	@Autowired
	private DaoService daoService;

	@Override
	public List<YXBlackListVO> addBlackList(List<YXBlackListVO> blackListVOs) {
		daoService.create(blackListVOs);		
		return blackListVOs;
	}

	@Override
	public List<YXLoanRecordsVO> addLoan(List<YXLoanRecordsVO> loanVOs) {
		daoService.create(loanVOs);
		return loanVOs;
	}

	@Override
	public YXLoanoverdueVO addLoanOverdue(YXLoanoverdueVO overdueVO) {
		daoService.create(overdueVO);
		return overdueVO;
	}

}