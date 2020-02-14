package com.wanda.credit.ds.client.juxinli.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.util.DESUtils;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Basic;
import com.wanda.credit.ds.client.juxinli.service.IJXLMobileRawDataService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileMemberPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataAccountPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataCallPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataNetPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataOwnerPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataSmsesPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileTransactionPojo;
import com.wanda.credit.ds.dao.iface.juxinli.mobile.IJXLMobileAccountService;
import com.wanda.credit.ds.dao.iface.juxinli.mobile.IJXLMobileBasicInfoService;
import com.wanda.credit.ds.dao.iface.juxinli.mobile.IJXLMobileCallService;
import com.wanda.credit.ds.dao.iface.juxinli.mobile.IJXLMobileNetService;
import com.wanda.credit.ds.dao.iface.juxinli.mobile.IJXLMobileSmsesService;
@Service
@Transactional
public class JXLMobileRawDataServiceImpl implements IJXLMobileRawDataService {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLMobileRawDataServiceImpl.class);
	@Autowired
	private IJXLMobileBasicInfoService jxlMobileBasicInfoService;
	@Autowired
	private IJXLMobileAccountService mobileAccountService;
	@Autowired
	private IJXLMobileCallService mobileCallService;
	@Autowired
	private IJXLMobileNetService mobileNetService;
	@Autowired
	private IJXLMobileSmsesService mobileSmsesService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	/*public void saveRawData(MobileMemberPojo memberPojo,String requestId) throws Exception {

		if (memberPojo == null) {
			logger.info("需要插入的数据不存在或者为null");
			return;
		}
		if (requestId == null || requestId.length() < 1) {
			logger.info("requestId非法" + requestId);
			return;
		}
		MobileRawDataOwnerPojo rawDataPojo = jxlMobileBasicInfoService.queryByRequestIdAndCode(requestId, JXLConst.MOBILE_RAWDATA_SUCCESS_CODE);
		
		if (rawDataPojo != null) {
			logger.info("{} 该数据已在本地缓存，无须再次获取");
			return;
		}
		
		Date nowTime = new Date();
		MobileRawDataOwnerPojo basicInfo = new MobileRawDataOwnerPojo();
		basicInfo.setRequestId(requestId);
		basicInfo.setCrt_date(nowTime);
		basicInfo.setUpd_date(nowTime);
		basicInfo.setError_code(memberPojo.getError_code() + "");
		basicInfo.setError_msg(memberPojo.getError_msg());
		basicInfo.setUpdate_time(memberPojo.getUpdate_time());
		
		
		List<MobileTransactionPojo> transactions = memberPojo.getTransactions();
		if (transactions == null || transactions.size() < 1) {
			jxlMobileBasicInfoService.add(basicInfo);
			return;
		}	
		
		MobileTransactionPojo transPojo = transactions.get(0);
		basicInfo.setVersion(transPojo.getVersion());
		basicInfo.setDatasource(transPojo.getDatasource());
		basicInfo.setToken(transPojo.getToken());
		
		Basic basic = transPojo.getBasic();
		String cellPhoneEnc = null;
		if (!StringUtil.isEmpty(basic.getCell_phone())) {
			cellPhoneEnc = CryptUtil.encrypt(basic.getCell_phone());
			basicInfo.setCell_phone(cellPhoneEnc);
		}	
//		cellPhoneEnc = "test";
//		basicInfo.setCell_phone(cellPhoneEnc);
		basicInfo.setIdcard(basic.getIdcard());
		basicInfo.setReal_name(basic.getReal_name());
		basicInfo.setReg_time(basic.getReg_time());
		
		Set<MobileRawDataNetPojo> netPojos = transPojo.getNets();
		if (netPojos != null && netPojos.size() > 0) {
			for (MobileRawDataNetPojo netPojo : netPojos) {
				netPojo.setCell_phone(cellPhoneEnc);
				netPojo.setRequestId(requestId);
				netPojo.setCrt_time(nowTime);
				netPojo.setUpd_time(nowTime);
			}
			
			basicInfo.setNetSet(netPojos);
		}
		Set<MobileRawDataCallPojo> callPojos = transPojo.getCalls();
		if (callPojos != null && callPojos.size() > 0) {
			for (MobileRawDataCallPojo callPojo : callPojos) {
				callPojo.setCell_phone(cellPhoneEnc);
				if (!StringUtil.isEmpty(callPojo.getOther_cell_phone())) {
					callPojo.setOther_cell_phone(DESUtils.encode(
							Conts.KEY_DESENC_KEY,
							callPojo.getOther_cell_phone()));
				}
				callPojo.setRequestId(requestId);
				callPojo.setCrt_time(nowTime);
				callPojo.setUpd_time(nowTime);
			}
			
			basicInfo.setCallSet(callPojos);
		}
		Set<MobileRawDataSmsesPojo> smsePojos = transPojo.getSmses();
		if (smsePojos != null && smsePojos.size() > 0) {
			for (MobileRawDataSmsesPojo smsesPojo : smsePojos) {
				smsesPojo.setCell_phone(cellPhoneEnc);
				if (!StringUtil.isEmpty(smsesPojo.getOther_cell_phone())) {
					smsesPojo.setOther_cell_phone(DESUtils.encode(
							Conts.KEY_DESENC_KEY,
							smsesPojo.getOther_cell_phone()));
				}
				smsesPojo.setRequestId(requestId);
				smsesPojo.setCrt_time(nowTime);
				smsesPojo.setUpd_time(nowTime);
			}
			
			basicInfo.setSmseSet(smsePojos);
		}
		Set<MobileRawDataAccountPojo> accountPojos = transPojo.getTransactions();
		if (accountPojos != null && accountPojos.size() > 0) {
			for (MobileRawDataAccountPojo accountPojo : accountPojos) {
				accountPojo.setCell_phone(cellPhoneEnc);
				accountPojo.setRequestId(requestId);
				accountPojo.setCrt_time(nowTime);
				accountPojo.setUpd_time(nowTime);
			}
			
			basicInfo.setAccountSet(accountPojos);
		}
		
		jxlMobileBasicInfoService.add(basicInfo);
	}*/
/*
	public MobileRawDataOwnerPojo loadRawData(String requestId) throws Exception {
		
		if (requestId == null || requestId.length() < 1) {
			logger.info("{} 聚信立获取运营商原始数据根据requestId获取缓存数据requestId不符合规范" + requestId);
			return null;
		}
		
		MobileRawDataOwnerPojo rawDataPojo = jxlMobileBasicInfoService.queryByRequestIdAndCode(requestId, JXLConst.MOBILE_RAWDATA_SUCCESS_CODE);
		
		if (rawDataPojo == null) {
			logger.info("{} 本地没有运营商原始数据的缓存数据或者之前获取数据失败重新获取");
			return null;
		}else{
			return rawDataPojo;
		}
		
		
		
	}
	*/
	public MobileRawDataOwnerPojo loadRawData(String requestId) throws Exception {
		
		if (requestId == null || requestId.length() < 1) {
			logger.info("{} 聚信立获取运营商原始数据根据requestId获取缓存数据requestId不符合规范" + requestId);
			return null;
		}
		
		MobileRawDataOwnerPojo rawDataPojo = jxlMobileBasicInfoService.queryByRequestIdAndCode(requestId, JXLConst.MOBILE_RAWDATA_SUCCESS_CODE);
		if (rawDataPojo == null) {
			return null;
		}
		MobileRawDataOwnerPojo mobileRawData = new MobileRawDataOwnerPojo();
		BeanUtils.copyProperties(mobileRawData, rawDataPojo);
		//获取账单数据
		MobileRawDataAccountPojo accountPojo = new MobileRawDataAccountPojo();
		accountPojo.setRequestId(requestId);
		List<MobileRawDataAccountPojo> accountPojoList = mobileAccountService.query(accountPojo);
		if (accountPojoList != null && accountPojoList.size() > 0) {
			Set<MobileRawDataAccountPojo> accountPojoSet = new HashSet<MobileRawDataAccountPojo>(accountPojoList);
			mobileRawData.setAccountSet(accountPojoSet);
		}
		//获取通话详单
		MobileRawDataCallPojo callPojo = new MobileRawDataCallPojo();
		callPojo.setRequestId(requestId);
		List<MobileRawDataCallPojo> callPojoList = mobileCallService.query(callPojo);
		
		if (callPojoList != null && callPojoList.size() > 0) {
			Set<MobileRawDataCallPojo> callPojoSet = new HashSet<MobileRawDataCallPojo>(callPojoList);
			mobileRawData.setCallSet(callPojoSet);
		}
		//流量信息
		MobileRawDataNetPojo netPojo = new MobileRawDataNetPojo();
		netPojo.setRequestId(requestId);
		List<MobileRawDataNetPojo> netPojoList = mobileNetService.query(netPojo);
		if (netPojoList != null && netPojoList.size() > 0) {
			Set<MobileRawDataNetPojo> netPojoSet = new HashSet<MobileRawDataNetPojo>(netPojoList);
			mobileRawData.setNetSet(netPojoSet);
		}
		//短信详情
		MobileRawDataSmsesPojo smsesPojo = new MobileRawDataSmsesPojo();
		smsesPojo.setRequestId(requestId);
		List<MobileRawDataSmsesPojo> smsPojoList = mobileSmsesService.query(smsesPojo);
		if (smsPojoList != null && smsPojoList.size() > 0) {
			Set<MobileRawDataSmsesPojo> smsPojoSet = new HashSet<MobileRawDataSmsesPojo>(smsPojoList);
			mobileRawData.setSmseSet(smsPojoSet);
		}


		return mobileRawData;

	}
	
	public void saveRawData(MobileMemberPojo memberPojo,String requestId) throws Exception {

		if (memberPojo == null) {
			logger.info("需要插入的数据不存在或者为null");
			return;
		}
		if (requestId == null || requestId.length() < 1) {
			logger.info("requestId非法" + requestId);
			return;
		}
		MobileRawDataOwnerPojo rawDataPojo = jxlMobileBasicInfoService.queryByRequestIdAndCode(requestId, JXLConst.MOBILE_RAWDATA_SUCCESS_CODE);
		
		if (rawDataPojo != null) {
			logger.info("{} 该数据已在本地缓存");
			return;
		}
		
		Date nowTime = new Date();
		MobileRawDataOwnerPojo basicInfo = new MobileRawDataOwnerPojo();
		basicInfo.setRequestId(requestId);
		basicInfo.setCrt_date(nowTime);
		basicInfo.setUpd_date(nowTime);
		basicInfo.setError_code(memberPojo.getError_code() + "");
		basicInfo.setError_msg(memberPojo.getError_msg());
		
		
		
		List<MobileTransactionPojo> transactions = memberPojo.getTransactions();
		if (transactions == null || transactions.size() < 1) {
			jxlMobileBasicInfoService.add(basicInfo);
			return;
		}	
		
		MobileTransactionPojo transPojo = transactions.get(0);
		basicInfo.setVersion(transPojo.getVersion());
		basicInfo.setDatasource(transPojo.getDatasource());
		basicInfo.setToken(transPojo.getToken());
		
		Basic basic = transPojo.getBasic();
		String cellPhoneEnc = null;
		if (!StringUtil.isEmpty(basic.getCell_phone())) {
			cellPhoneEnc = synchExecutorService.encrypt(basic.getCell_phone());
			basicInfo.setCell_phone(cellPhoneEnc);
		}	
//		cellPhoneEnc = "test";
//		basicInfo.setCell_phone(cellPhoneEnc);
		basicInfo.setIdcard(basic.getIdcard());
		basicInfo.setReal_name(basic.getReal_name());
		basicInfo.setReg_time(basic.getReg_time());
		basicInfo.setUpdate_time(basic.getUpdate_time());
		
		jxlMobileBasicInfoService.add(basicInfo);
		
//		Set<MobileRawDataNetPojo> netPojos = transPojo.getNets();
		List<MobileRawDataNetPojo> netPojos = transPojo.getNets();
		if (netPojos != null && netPojos.size() > 0) {
			for (MobileRawDataNetPojo netPojo : netPojos) {
				netPojo.setCell_phone(cellPhoneEnc);
				netPojo.setRequestId(requestId);
				netPojo.setCrt_time(nowTime);
				netPojo.setUpd_time(nowTime);
				
				netPojo.setFk_seqId(basicInfo.getSeqId());
				
			}
			
//			basicInfo.setNetSet(netPojos);
			mobileNetService.add(netPojos);
		}
//		Set<MobileRawDataCallPojo> callPojos = transPojo.getCalls();
		List<MobileRawDataCallPojo> callPojos = transPojo.getCalls();
		if (callPojos != null && callPojos.size() > 0) {
			for (MobileRawDataCallPojo callPojo : callPojos) {
				callPojo.setCell_phone(cellPhoneEnc);
				if (!StringUtil.isEmpty(callPojo.getOther_cell_phone())) {
					callPojo.setOther_cell_phone(DESUtils.encode(
							Conts.KEY_DESENC_KEY,
							callPojo.getOther_cell_phone()));
				}
				callPojo.setRequestId(requestId);
				callPojo.setCrt_time(nowTime);
				callPojo.setUpd_time(nowTime);
				callPojo.setFk_seqId(basicInfo.getSeqId());
			}
			
//			basicInfo.setCallSet(callPojos);
			mobileCallService.add(callPojos);
		}
//		Set<MobileRawDataSmsesPojo> smsePojos = transPojo.getSmses();
		List<MobileRawDataSmsesPojo> smsePojos = transPojo.getSmses();
		if (smsePojos != null && smsePojos.size() > 0) {
			for (MobileRawDataSmsesPojo smsesPojo : smsePojos) {
				smsesPojo.setCell_phone(cellPhoneEnc);
				if (!StringUtil.isEmpty(smsesPojo.getOther_cell_phone())) {
					smsesPojo.setOther_cell_phone(DESUtils.encode(
							Conts.KEY_DESENC_KEY,
							smsesPojo.getOther_cell_phone()));
				}
				smsesPojo.setRequestId(requestId);
				smsesPojo.setCrt_time(nowTime);
				smsesPojo.setUpd_time(nowTime);
				smsesPojo.setFk_seqId(basicInfo.getSeqId());
			}
			
//			basicInfo.setSmseSet(smsePojos);
			mobileSmsesService.add(smsePojos);
		}
//		Set<MobileRawDataAccountPojo> accountPojos = transPojo.getTransactions();
		List<MobileRawDataAccountPojo> accountPojos = transPojo.getTransactions();
		if (accountPojos != null && accountPojos.size() > 0) {
			for (MobileRawDataAccountPojo accountPojo : accountPojos) {
				accountPojo.setCell_phone(cellPhoneEnc);
				accountPojo.setRequestId(requestId);
				accountPojo.setCrt_time(nowTime);
				accountPojo.setUpd_time(nowTime);
				accountPojo.setFk_seqId(basicInfo.getSeqId());
			}
			
//			basicInfo.setAccountSet(accountPojos);
			mobileAccountService.add(accountPojos);
		}
		
		
	}

	

}
