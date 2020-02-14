package com.wanda.credit.ds.dao.iface;

import java.util.List;

import com.wanda.credit.ds.dao.domain.yixin.YXBlackListVO;
import com.wanda.credit.ds.dao.domain.yixin.YXLoanRecordsVO;
import com.wanda.credit.ds.dao.domain.yixin.YXLoanoverdueVO;

public interface IYiXinCreditService{	
	/**
	 * 新增借款记录信息*/
	public List<YXLoanRecordsVO> addLoan(List<YXLoanRecordsVO> loanVOs);
	
	/**
	 * 新增借款逾期信息*/
	public YXLoanoverdueVO addLoanOverdue(YXLoanoverdueVO overdueVO);
	
	/**
	 * 新增黑名单信息*/
	public List<YXBlackListVO> addBlackList(List<YXBlackListVO> blackListVOs);

}
