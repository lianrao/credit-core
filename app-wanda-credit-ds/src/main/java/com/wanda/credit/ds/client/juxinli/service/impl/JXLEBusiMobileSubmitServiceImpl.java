package com.wanda.credit.ds.client.juxinli.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.ds.client.juxinli.service.IJXLEBusiMobileSubmitService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyAccountPojo;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyNextDataSourcePojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLAccountService;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLBasicInfoService;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLNextDatasourceService;

@Service
@Transactional
public class JXLEBusiMobileSubmitServiceImpl implements
		IJXLEBusiMobileSubmitService {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLEBusiMobileSubmitServiceImpl.class);
	
	@Autowired
	private IJXLAccountService jxlAccountService;
	@Autowired
	private IJXLBasicInfoService jxlBasicInfoService;
	@Autowired
	private IJXLNextDatasourceService jxlNextDatasourceService;

	public void saveSubmitAccount(ApplyAccountPojo accountPojo,
			String requestId, String remark) throws Exception {
		accountPojo.setCrt_time(new Date());
		accountPojo.setUpd_time(new Date());
		jxlAccountService.add(accountPojo);
		jxlBasicInfoService.updateApplyInfo(requestId,remark);
		
	}

	public boolean isRepeatSubmit(String requestId, String website) {
		
		ApplyBasicInfoPojo basicInfoPojo = new ApplyBasicInfoPojo();
		basicInfoPojo.setRequestId(requestId);
		basicInfoPojo.setRemark(JXLConst.MEBUSI_SUBMIT_All_FINISH);
		List<ApplyBasicInfoPojo> basicPojoList = jxlBasicInfoService.query(basicInfoPojo);
		if (basicPojoList != null && basicPojoList.size() > 0) {
			logger.info("{} 当前requestId [" + requestId + "] 已完成所有的采集请求");
			return true;
		}
		
		ApplyAccountPojo accountPojo = new ApplyAccountPojo();
		accountPojo.setRequestId(requestId);
		accountPojo.setWebsite(website);
		accountPojo.setProcess_code(JXLConst.MEBUSI_SUBMIT_SUCCESSCODE);
		
		List<ApplyAccountPojo> accountList = jxlAccountService.query(accountPojo);
		if (accountList != null && accountList.size() > 0) {
			return true;
		}
		
		return false;
	}

	public void updateNextDSByReqIdAndWebSite(String requestId, String website,String isSuc) {
		
		ApplyNextDataSourcePojo  nextDSPojo = new ApplyNextDataSourcePojo();
		
		nextDSPojo.setRequestId(requestId);
		nextDSPojo.setWebsite(website);
		
		List<ApplyNextDataSourcePojo> nextDSPojoList = jxlNextDatasourceService.query(nextDSPojo);
	
		if (nextDSPojoList != null && nextDSPojoList.size() > 0) {
			ApplyNextDataSourcePojo updateDSPojo = nextDSPojoList.get(0);
			updateDSPojo.setUpd_time(new Date());
			updateDSPojo.setSuccess(isSuc);
			
			jxlNextDatasourceService.updateNextDS(updateDSPojo);
		}
	}

	public ApplyNextDataSourcePojo queryNextDs(String requestId) {
		
		ApplyNextDataSourcePojo nextDsPojo = new ApplyNextDataSourcePojo();
		nextDsPojo.setRequestId(requestId);
		nextDsPojo.setSuccess(JXLConst.MEBUSI_SUBMIT_ING);
		List<ApplyNextDataSourcePojo> nextDS = jxlNextDatasourceService.query(nextDsPojo);
		if(nextDS != null && nextDS.size() > 0){
			return nextDS.get(0);
		}
		return null;
	}

}
