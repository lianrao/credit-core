package com.wanda.credit.ds.client.juxinli.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.client.juxinli.service.IJXLHouseRawDataDealService;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataBasicPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataDetailPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataLoanPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataOverduePojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataPaymentPojo;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFundBasicService;

@Service
@Transactional
public class JXLHouseRawDataServiceDealImpl implements IJXLHouseRawDataDealService {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLHouseRawDataServiceDealImpl.class);
	
	@Autowired
	private IJXLHouseFundBasicService jxlHouseFundBasicService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	
	@Override
	public void addHouseRawData(HouseRawDataBasicPojo rawDataPojo,
			String requestId) throws Exception {
		
		if (inCache(requestId)) {
			logger.info("{} 本地已缓存数据不用重复保存" , requestId);
			
			return;
		}
		
		Date nowTime = new Date();
			
		rawDataPojo.setRequestId(requestId);
		rawDataPojo.setCrt_time(nowTime);
		rawDataPojo.setUpd_time(nowTime);
		if (!StringUtil.isEmpty(rawDataPojo.getId_card()) && !rawDataPojo.getId_card().contains("*")) {
			rawDataPojo.setId_card(synchExecutorService.encrypt(rawDataPojo.getId_card()));
		}
		if (!StringUtil.isEmpty(rawDataPojo.getFund_num()) && !rawDataPojo.getFund_num().contains("*")) {
			rawDataPojo.setFund_num(synchExecutorService.encrypt(rawDataPojo.getFund_num()));
		}
		if (!StringUtil.isEmpty(rawDataPojo.getOpen_bank_account()) && !rawDataPojo.getOpen_bank_account().contains("*")) {
			rawDataPojo.setOpen_bank_account(synchExecutorService.encrypt(rawDataPojo.getOpen_bank_account()));
		}
		
		Set<HouseRawDataDetailPojo> detailSet = rawDataPojo.getDetails();
		
		if (detailSet != null && detailSet.size() >0 ) {
			for (HouseRawDataDetailPojo detail : detailSet) {
				detail.setCrt_time(nowTime);
				detail.setUpd_time(nowTime);
				detail.setRequestId(requestId);
			}
		}
		
		Set<HouseRawDataLoanPojo> loanSet = rawDataPojo.getLoan_info();
		
		if (loanSet != null && loanSet.size() > 0) {
			for (HouseRawDataLoanPojo loan : loanSet) {
				loan.setCrt_time(nowTime);
				loan.setUpd_time(nowTime);
				loan.setRequestId(requestId);
				if (!StringUtil.isEmpty(loan.getBank_account()) && !loan.getBank_account().contains("*")) {
					loan.setBank_account(synchExecutorService.encrypt(loan.getBank_account()));
				}
				if (!StringUtil.isEmpty(loan.getLoan_idcard()) && !loan.getLoan_idcard().contains("*")) {
					loan.setLoan_idcard(synchExecutorService.encrypt(loan.getLoan_idcard()));
				}
				
				Set<HouseRawDataPaymentPojo> paymentSet = loan.getPayment_details();
				
				if (paymentSet != null && paymentSet.size() > 0) {
					for (HouseRawDataPaymentPojo paymentPojo : paymentSet) {
						paymentPojo.setRequestId(requestId);
						paymentPojo.setCrt_time(nowTime);
						paymentPojo.setUpd_time(nowTime);
					}
				}
				
				Set<HouseRawDataOverduePojo> overdueSet = loan.getOverdue_details();
				
				if (overdueSet != null && overdueSet.size() > 0) {
					for (HouseRawDataOverduePojo overduePojo : overdueSet) {
						overduePojo.setRequestId(requestId);
						overduePojo.setCrt_time(nowTime);
						overduePojo.setUpd_time(nowTime);
					}
				}
			}
		}
		
		jxlHouseFundBasicService.add(rawDataPojo);
		
	}



	public HouseRawDataBasicPojo queryRawData(String requestId) {
		
		HouseRawDataBasicPojo basic = new HouseRawDataBasicPojo();
		basic.setRequestId(requestId);
		List<HouseRawDataBasicPojo> rawDataPojoList = jxlHouseFundBasicService.query(basic);
		
		if (rawDataPojoList != null && rawDataPojoList.size() >0 ) {
			
			return rawDataPojoList.get(0);
		}else{
			return null;
		}
		
	}



	
	@Override
	public boolean inCache(String requestId) {
		HouseRawDataBasicPojo basic = new HouseRawDataBasicPojo();
		basic.setRequestId(requestId);
		List<HouseRawDataBasicPojo> basicList = jxlHouseFundBasicService.query(basic);
		return basicList.size() > 0;
	}
	
	

}
