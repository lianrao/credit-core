/**   
* @Description: 4.2报告Service 
* @author xiaobin.hou  
* @date 2016年9月26日 下午6:51:53 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.po.CheckBlackInfo;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.po.CheckSearchInfo;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.po.ReportData;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.po.UserInfoCheck;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ApplicationCheck;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.Behavior;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.BehaviorCheck;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.CellBehavior;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.CheckPoints;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.CollectionContact;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ContactDetails;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ContactInfo;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ContactRegion;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.CourtBlacklist;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.DeliverAddress;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.EbusinessExpense;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.FinancialBlacklist;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.MainService;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.Receiver;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.Report;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ServiceDetail;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.TripInfo;
import com.wanda.credit.ds.client.juxinli.service.IJXLNewReportDataService;
import com.wanda.credit.ds.dao.domain.juxinli.report.ApplyCheck2Pojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.BehaviorCheckPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.CellBehaviorPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.CollectionContactPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.ContactDetailsPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.ContactInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.ContactRegionPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.DeliverAddressPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.EbusinessExpensePojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.MainServicePojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.PersonPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.ReceiverPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.ServiceDetailPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.TripInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.UserInfoCheckPojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLRepApplyCheck2Service;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLRepUserInfoCheckService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportBehaviorCheckService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportCellBehaviorService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportCollectionContactService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportContactInfoService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportContactRegionService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportDeliverAddressService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportEbusinessExpenseService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportMainServiceService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportPersonService;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportTripInfoService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLNewReportDataServiceImpl implements IJXLNewReportDataService {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLNewReportDataServiceImpl.class);
	private static Map<String, String> encKeyMap = new HashMap<String, String>();
	static {
		encKeyMap.put("id_card", "application_check_身份证号");
		encKeyMap.put("cell_phone", "application_check_手机号");
		encKeyMap.put("home_phone", "application_check_家庭电话");
		encKeyMap.put("contact", "application_check_联系人信息");
		
	}
	
	private final String SEPARATOR = ",";
	
	@Autowired
	private IJXLRepApplyCheck2Service applyCheck2Service;
	@Autowired
	private IJXLRepUserInfoCheckService userInfoCheckService;
	@Autowired
	private IJXLReportBehaviorCheckService behaviorCheckService;
	@Autowired
	private IJXLReportCellBehaviorService cellBehaviorService;
	@Autowired
	private IJXLReportCollectionContactService collectionContactService;
	@Autowired
	private IJXLReportContactInfoService contactInfoService;
	@Autowired
	private IJXLReportContactRegionService contactRegionService;
	@Autowired
	private IJXLReportDeliverAddressService deliverAddressService;
	@Autowired
	private IJXLReportEbusinessExpenseService ebusiExpenseService;
	@Autowired
	private IJXLReportMainServiceService mainSerService;
	@Autowired
	private IJXLReportPersonService personService;
	@Autowired
	private IJXLReportTripInfoService tripInfoService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	@Override
	public void addNewReport(ReportData reportData, String requestId,
			String trade_id)  {
		logger.info("{} 保存4.2版本报告数据" , trade_id);
		Date nowTime = new Date();
		//判断本地是否保存了报告数据
		try{
			PersonPojo person = personService.queryByRequestId(requestId);
			if (person != null) {
				logger.info("{} 本地已缓存该序列号对应的数据，序列号为：{}",trade_id,requestId);
				return;
			}
			// 用户信息核查
			List<ApplicationCheck> applyInfoCheck = reportData
					.getApplication_check();
			
			if (applyInfoCheck != null && applyInfoCheck.size() > 0) {
				List<ApplyCheck2Pojo> Check2PojoList = new ArrayList<ApplyCheck2Pojo>();
				for (ApplicationCheck appChcek : applyInfoCheck) {
					ApplyCheck2Pojo check2Pojo = changeToAppCheck(appChcek,requestId);
					Check2PojoList.add(check2Pojo);
				}
				applyCheck2Service.add(Check2PojoList);
			}
			// 用户行为核查
			List<BehaviorCheck> behavior_check = reportData.getBehavior_check();
			if (behavior_check != null && behavior_check.size() > 0) {
				List<BehaviorCheckPojo> behaviorPojoList = new ArrayList<BehaviorCheckPojo>();
				for (BehaviorCheck behaviorCheck : behavior_check) {
					BehaviorCheckPojo behaviorPojo = change2BehaviorCheckPojo(behaviorCheck,requestId,nowTime);
					behaviorPojoList.add(behaviorPojo);
				}
				behaviorCheckService.add(behaviorPojoList);
				
			}
			// 用户通话行为汇总（按月）包括呼入，呼出，流量等汇总
			List<CellBehavior> cell_behavior = reportData.getCell_behavior();
			if(cell_behavior != null && cell_behavior.size() > 0){
				List<CellBehaviorPojo> cellBePojoList = new ArrayList<CellBehaviorPojo>();
				for (CellBehavior cellBehavior : cell_behavior) {
					String phoneNum = cellBehavior.getPhone_num();
					String encPhoneNum = "";
					if (!StringUtil.isEmpty(phoneNum)) {
						encPhoneNum = synchExecutorService.encrypt(phoneNum);
					}
					List<Behavior> behaviorList = cellBehavior.getBehavior();			
					for (Behavior behavior : behaviorList) {
						CellBehaviorPojo pojo = change2CellBehaviorPojo(behavior,encPhoneNum,requestId,nowTime);
						cellBePojoList.add(pojo);
					}
				}
				cellBehaviorService.add(cellBePojoList);
			}
			

			// 与常用联系人联系状况分析
			List<CollectionContact> collection_contact = reportData
					.getCollection_contact();
			if (collection_contact != null && collection_contact.size() > 0) {
				List<CollectionContactPojo> collContactPojoList = new ArrayList<CollectionContactPojo>();
				for (CollectionContact collContact : collection_contact) {		
					CollectionContactPojo contactPojo = change2CollContactPojo(collContact,requestId,nowTime);
					collContactPojoList.add(contactPojo);
				}
				collectionContactService.add(collContactPojoList);
			}
			// 通讯行为汇总（按联系人）
			List<ContactInfo> contact_list = reportData.getContact_list();
			if (contact_list != null && contact_list.size() > 0) {
				List<ContactInfoPojo> infoPojoList = new ArrayList<ContactInfoPojo>();
				for (ContactInfo contactInfo : contact_list) {
					ContactInfoPojo infoPojo = change2ContactInfoPojo(contactInfo,requestId,nowTime);
					infoPojoList.add(infoPojo);
				}
				contactInfoService.add(infoPojoList);
			}
			// 通讯行为汇总（按区域）
			List<ContactRegion> contact_region = reportData.getContact_region();
			if (contact_region != null && contact_region.size() > 0) {
				List<ContactRegionPojo> regionPojoList = new ArrayList<ContactRegionPojo>();
				for (ContactRegion region : contact_region) {
					
					ContactRegionPojo regionPojo = change2ContactRegionPojo(region,requestId,nowTime);
					regionPojoList.add(regionPojo);
				}
				contactRegionService.add(regionPojoList);
			}
			
			// 电商快递收货地址信息汇总
			List<DeliverAddress> deliverAddrList = reportData.getDeliver_address();
			if (deliverAddrList != null && deliverAddrList.size() > 0) {
				List<DeliverAddressPojo> deliverAddrPojoList = new ArrayList<DeliverAddressPojo>();
				for (DeliverAddress deliverAddr : deliverAddrList) {
					
					DeliverAddressPojo deliverAddrPojo = change2DeliverAddrPojo(deliverAddr,requestId,nowTime);
					
					deliverAddrPojoList.add(deliverAddrPojo);
				}
				deliverAddressService.add(deliverAddrPojoList);
			}
		
			// 电商月消费金额汇总
			List<EbusinessExpense> ebusiness_expense = reportData
					.getEbusiness_expense();
			if (ebusiness_expense != null && ebusiness_expense.size() > 0) {
				List<EbusinessExpensePojo> ebusiExpPojoList = new ArrayList<EbusinessExpensePojo>();
				for (EbusinessExpense ebusiExp : ebusiness_expense) {
					EbusinessExpensePojo ebusiExpPojo = new EbusinessExpensePojo();
					BeanUtils.copyProperties(ebusiExpPojo, ebusiExp);
					ebusiExpPojo.setRequestId(requestId);
					ebusiExpPojo.setCrt_time(nowTime);
					ebusiExpPojo.setUpd_time(nowTime);
					
					ebusiExpPojoList.add(ebusiExpPojo);
				}
				ebusiExpenseService.add(ebusiExpPojoList);
			}
			
			// 常用电话服务汇总
			List<MainService> main_service = reportData.getMain_service();
			if (main_service != null && main_service.size() > 0) {
				List<MainServicePojo> mainPojoList = new ArrayList<MainServicePojo>();
				for (MainService main : main_service) {
					
					MainServicePojo mainPojo = change2MainServicePojo(main,requestId,nowTime);
				
					mainPojoList.add(mainPojo);
				}
				mainSerService.add(mainPojoList);
			}

			// 行程信息
			List<TripInfo> trip_info = reportData.getTrip_info();
			if (trip_info != null && trip_info.size() > 0) {
				List<TripInfoPojo> tripPojoList = new ArrayList<TripInfoPojo>();
				for (TripInfo tripInfo : trip_info) {
					TripInfoPojo tripPojo = change2TripInfoPojo(tripInfo,requestId,nowTime);
					tripPojoList.add(tripPojo);
				}
				tripInfoService.add(tripPojoList);
			}
			
			UserInfoCheck userInfoCheck = reportData.getUser_info_check();
			if (userInfoCheck != null ) {
				UserInfoCheckPojo userInfoPojo = change2UserInfoCheckPojo(userInfoCheck,requestId,nowTime);
				userInfoCheckService.add(userInfoPojo);
			}
			// 数据验真报告信息
			Report report = reportData.getReport();
			PersonPojo personPojo = change2PersonPojo(report,applyInfoCheck,requestId,nowTime);
			personService.add(personPojo);
		}catch(Exception e){
			logger.error("{} 4.2报告数据保存数据库异常：{}", trade_id , e.getMessage());
		}
			
		
	}
	

	@Override
	public ReportData loadCacheData(String requestId) throws Exception {
		
		ReportData reportData = new ReportData();
		
		ApplyCheck2Pojo checkPojo = new ApplyCheck2Pojo();
		checkPojo.setRequestId(requestId);
		List<ApplyCheck2Pojo> checkPojoList = applyCheck2Service.query(checkPojo);
		List<ApplicationCheck> checkList = new ArrayList<ApplicationCheck>();
		if (checkPojoList != null && checkPojoList.size() >0) {
			for (ApplyCheck2Pojo applyCheck2Pojo : checkPojoList) {
				ApplicationCheck check = changeApplyCheck2Bean(applyCheck2Pojo);
				checkList.add(check);
			}
		}
		reportData.setApplication_check(checkList);
		
		BehaviorCheckPojo behaviorPojo = new BehaviorCheckPojo();
		behaviorPojo.setRequestId(requestId);
		List<BehaviorCheckPojo> behaviorPojoList = behaviorCheckService.query(behaviorPojo);
		List<BehaviorCheck> behaviorCheckList = new ArrayList<BehaviorCheck>();
		if (behaviorPojoList != null && behaviorPojoList.size() > 0) {
			for (BehaviorCheckPojo behaviorCheckPojo : behaviorPojoList) {
				BehaviorCheck behaviorCheck = changeBehaviorPojo2Bean(behaviorCheckPojo);
				behaviorCheckList.add(behaviorCheck);
			}	
		}
		reportData.setBehavior_check(behaviorCheckList);
		
		//通话行为分析
		List<String> phoneNumList = cellBehaviorService.queryUniquePhoneNum(requestId);
		List<CellBehavior> cellBehaList = new ArrayList<CellBehavior>();
		if (phoneNumList != null && phoneNumList.size() > 0) {
			for (String phoneNum : phoneNumList) {
				String decPhoneNum = synchExecutorService.decrypt(phoneNum);
				List<CellBehaviorPojo> cellBehaviorPojoList = cellBehaviorService.queryByPhoneNumAndRequestId(requestId, phoneNum);
				if (cellBehaviorPojoList != null && cellBehaviorPojoList.size() > 0) {
					CellBehavior cellBehavior = changeCellBehaviorPojoList2Bean(cellBehaviorPojoList,decPhoneNum);
					cellBehaList.add(cellBehavior);
				}
			}
		}
		reportData.setCell_behavior(cellBehaList);
		
		CollectionContactPojo collContactPojo = new CollectionContactPojo();
		collContactPojo.setRequestId(requestId);
		List<CollectionContactPojo> collContactPojoList = collectionContactService.query(collContactPojo);
		List<CollectionContact> collContactList = new ArrayList<CollectionContact>();
		if (collContactPojoList != null && collContactPojoList.size() > 0) {
			for (CollectionContactPojo contactPojo : collContactPojoList) {
				CollectionContact collContact = changeCollContactPojo2Bean(contactPojo);
				collContactList.add(collContact);
			}
			
		}
		reportData.setCollection_contact(collContactList);
		
		ContactInfoPojo contInfoPojo = new ContactInfoPojo();
		contInfoPojo.setRequestId(requestId);
		List<ContactInfoPojo> contactInfoPojoList = contactInfoService.query(contInfoPojo);
		List<ContactInfo> contactInfoList = changeContactInfoPojo2Bean(contactInfoPojoList);
		reportData.setContact_list(contactInfoList);
		
		ContactRegionPojo regionPojo = new ContactRegionPojo();
		regionPojo.setRequestId(requestId);
		List<ContactRegionPojo> regionPojoList = contactRegionService.query(regionPojo);
		List<ContactRegion> contactRegionList = changeRegionPojoList2Bean(regionPojoList);
		reportData.setContact_region(contactRegionList);
		
		DeliverAddressPojo addrPojo = new DeliverAddressPojo();
		addrPojo.setRequestId(requestId);
		List<DeliverAddressPojo> deliverAddrPojoList = deliverAddressService.query(addrPojo);
		List<DeliverAddress> deliAddrList = changeAddrPojoList2Bean(deliverAddrPojoList);
		reportData.setDeliver_address(deliAddrList);
		
		EbusinessExpensePojo ebusiExpPojo = new EbusinessExpensePojo();
		ebusiExpPojo.setRequestId(requestId);
		List<EbusinessExpensePojo> ebusiExpPojoList = ebusiExpenseService.query(ebusiExpPojo);
		List<EbusinessExpense> ebusiExpList = changeEbusiExpPojoList2Bean(ebusiExpPojoList);
		reportData.setEbusiness_expense(ebusiExpList);
		
		MainServicePojo mainSerPojo = new MainServicePojo();
		mainSerPojo.setRequestId(requestId);
		List<MainServicePojo> mainSerPojoList = mainSerService.query(mainSerPojo);
		List<MainService> manSerList = changeMainPojoList2Bean(mainSerPojoList);
		reportData.setMain_service(manSerList);
		
		UserInfoCheckPojo userInfoCheckPojo = new UserInfoCheckPojo();
		userInfoCheckPojo.setRequestId(requestId);
		List<UserInfoCheckPojo> userCheckPojoList = userInfoCheckService.query(userInfoCheckPojo);
		UserInfoCheck userCheck = changeUserCheckPojoList2Bean(userCheckPojoList);
		reportData.setUser_info_check(userCheck);
		
		TripInfoPojo tripPojo = new TripInfoPojo();
		tripPojo.setRequestId(requestId);
		List<TripInfoPojo> tripPojoList = tripInfoService.query(tripPojo);
		List<TripInfo> tripList = changeTripPojoList2Bean(tripPojoList);
		reportData.setTrip_info(tripList);
		
		
		
		return reportData;
		
	}

	/**
	 * @param userCheckPojoList
	 * @return
	 * @throws Exception 
	 */
	private UserInfoCheck changeUserCheckPojoList2Bean(
			List<UserInfoCheckPojo> userCheckPojoList) throws Exception {
		UserInfoCheck userCheck = new UserInfoCheck();
		if (userCheckPojoList != null && userCheckPojoList.size() > 0) {
			UserInfoCheckPojo userCheckPojo = userCheckPojoList.get(0);
			
			CheckBlackInfo blackInfo = new CheckBlackInfo();
			blackInfo.setContacts_class1_blacklist_cnt(userCheckPojo.getContacts_class1_blacklist_cnt());
			blackInfo.setContacts_class1_cnt(userCheckPojo.getContacts_class1_cnt());
			blackInfo.setContacts_class2_blacklist_cnt(userCheckPojo.getContacts_class2_blacklist_cnt());
			blackInfo.setContacts_router_cnt(userCheckPojo.getContacts_router_cnt());
			blackInfo.setContacts_router_ratio(userCheckPojo.getContacts_router_ratio());
			blackInfo.setPhone_gray_score(userCheckPojo.getPhone_gray_score());
			userCheck.setCheck_black_info(blackInfo);
			
			CheckSearchInfo searchInfo = new CheckSearchInfo();	
			String arisedOpenWeb = userCheckPojo.getArised_open_web();
			if (!StringUtil.isEmpty(arisedOpenWeb)) {
				List<String> openWebList = String2List(arisedOpenWeb);
				searchInfo.setArised_open_web(openWebList);
			}
			
			String idCardNames = userCheckPojo.getIdcard_with_other_names();
			if (!StringUtil.isEmpty(idCardNames)) {
				List<String> idCardNameList = String2List(idCardNames);
				searchInfo.setIdcard_with_other_names(idCardNameList);
			}
			
			String idCardPhones = userCheckPojo.getIdcard_with_other_phones();
			if (!StringUtil.isEmpty(idCardPhones)) {
				List<String> idCardPhoneList = String2DecList(idCardPhones);
				searchInfo.setIdcard_with_other_phones(idCardPhoneList);
			}
			
			String phoneIdCards = userCheckPojo.getPhone_with_other_idcards();
			if (!StringUtil.isEmpty(phoneIdCards)) {
				List<String> phoneIdCardList = String2DecList(phoneIdCards);
				searchInfo.setPhone_with_other_idcards(phoneIdCardList);
			}
			
			String phoneNames = userCheckPojo.getPhone_with_other_names();
			if (!StringUtil.isEmpty(phoneNames)) {
				List<String> phoneNameList = String2List(phoneNames);
				searchInfo.setPhone_with_other_names(phoneNameList);
			}
			
			searchInfo.setRegister_org_cnt(userCheckPojo.getRegister_org_cnt());
			
			String regOrgTypes = userCheckPojo.getRegister_org_type();
			if (!StringUtil.isEmpty(regOrgTypes)) {
				List<String> regOrgTypeList = String2List(regOrgTypes);
				searchInfo.setRegister_org_type(regOrgTypeList);
			}
			
			searchInfo.setSearched_org_cnt(userCheckPojo.getSearched_org_cnt());
			
			String searOrgTypes = userCheckPojo.getSearched_org_type(); 
			if (!StringUtil.isEmpty(searOrgTypes)) {
				List<String> searOrgTypeList = String2List(searOrgTypes);
				searchInfo.setSearched_org_type(searOrgTypeList);
			}
			
			userCheck.setCheck_search_info(searchInfo);
		}
		
		return userCheck;
	}


	/**
	 * 将字符串转化成List<String>
	 * @param arisedOpenWeb
	 * @return
	 */
	private List<String> String2List(String str) {
		
		List<String> retList = new ArrayList<String>();
		
		if (!StringUtil.isEmpty(str)) {
			String[] split = str.split(SEPARATOR);
			retList = Arrays.asList(split);
		}
		
		return retList;
	}
	
	/**
	 * 将字符串内容解密转化成List<String>
	 * @param arisedOpenWeb
	 * @return
	 * @throws Exception 
	 */
	private List<String> String2DecList(String str) throws Exception {
		
		List<String> retList = new ArrayList<String>();
		
		if (!StringUtil.isEmpty(str)) {
			String[] split = str.split(SEPARATOR);
			for (int i = 0; i < split.length; i++) {
				retList.add(synchExecutorService.decrypt(split[i]));
			}
		}
		
		return retList;
	}


	/**
	 * @param tripPojoList
	 * @return
	 */
	private List<TripInfo> changeTripPojoList2Bean(
			List<TripInfoPojo> tripPojoList) {
		
		List<TripInfo> tripList = new ArrayList<TripInfo>();
		
		if (tripPojoList != null && tripPojoList.size() > 0) {
			for (TripInfoPojo tripPojo : tripPojoList) {
				TripInfo trip = new TripInfo();
				trip.setTrip_dest(tripPojo.getTrip_dest());
				trip.setTrip_end_time(tripPojo.getTrip_end_time());
				trip.setTrip_leave(tripPojo.getTrip_leave());
				trip.setTrip_start_time(tripPojo.getTrip_start_time());
				trip.setTrip_type(tripPojo.getTrip_type());
				
				tripList.add(trip);
			}
		}
		return tripList;
	}


	/**
	 * @param mainSerPojoList
	 * @return
	 */
	private List<MainService> changeMainPojoList2Bean(
			List<MainServicePojo> mainSerPojoList) {
		
		List<MainService> mainList = new ArrayList<MainService>();
		if (mainSerPojoList != null & mainSerPojoList.size() > 0) {
			for (MainServicePojo mainPojo : mainSerPojoList) {
				MainService main = new MainService();
				main.setCompany_name(mainPojo.getCompany_name());
				main.setCompany_type(mainPojo.getCompany_type());
				main.setTotal_service_cnt(mainPojo.getTotal_service_cnt());
				
				Set<ServiceDetailPojo> serDetailPojoSet = mainPojo.getService_details();
				if (serDetailPojoSet != null && serDetailPojoSet.size() > 0) {
					List<ServiceDetail> detailList = new ArrayList<ServiceDetail>();
					for (ServiceDetailPojo detailPojo : serDetailPojoSet) {
						ServiceDetail detail = new ServiceDetail();
						detail.setInteract_cnt(detailPojo.getInteract_cnt());
						detail.setInteract_mth(detailPojo.getInteract_mth());
						detailList.add(detail);
					}
					main.setService_details(detailList);
				}
				mainList.add(main);
			}
		}
		return mainList;
	}


	/**
	 * @param ebusiExpPojoList
	 * @return
	 */
	private List<EbusinessExpense> changeEbusiExpPojoList2Bean(
			List<EbusinessExpensePojo> ebusiExpPojoList) {
		
		List<EbusinessExpense> ebusiExpList = new ArrayList<EbusinessExpense>();
		for (EbusinessExpensePojo ebusiExpPojo : ebusiExpPojoList) {
			EbusinessExpense ebusiExp = new EbusinessExpense();
			ebusiExp.setAll_amount(ebusiExpPojo.getAll_amount());
			ebusiExp.setAll_count(ebusiExpPojo.getAll_count());
			ebusiExp.setCategory(ebusiExpPojo.getCategory());
			ebusiExp.setTrans_mth(ebusiExpPojo.getTrans_mth());
			ebusiExpList.add(ebusiExp);
		}
		
		
		return ebusiExpList;
	}


	/**
	 * @param deliverAddrPojoList
	 * @return
	 * @throws Exception 
	 */
	private List<DeliverAddress> changeAddrPojoList2Bean(
			List<DeliverAddressPojo> deliverAddrPojoList) throws Exception {		
		List<DeliverAddress> deliAddrList = new ArrayList<DeliverAddress>();
		if (deliverAddrPojoList != null && deliverAddrPojoList.size() > 0) {
			for (DeliverAddressPojo addrPojo : deliverAddrPojoList) {
				DeliverAddress addr = new DeliverAddress();
				addr.setAddress(addrPojo.getAddr());
				addr.setBegin_date(addrPojo.getBegin_date());
				addr.setEnd_date(addrPojo.getEnd_date());
				addr.setLat(addrPojo.getLat());
				addr.setLng(addrPojo.getLng());
				addr.setPredict_addr_type(addrPojo.getPredict_addr_type());
				addr.setTotal_amount(addrPojo.getTotal_amount());
				addr.setTotal_count(addrPojo.getTotal_count());
				
				Set<ReceiverPojo> receiverPojoSet = addrPojo.getReceivers();
				if (receiverPojoSet != null && receiverPojoSet.size() > 0) {
					List<Receiver> receiverList = new ArrayList<Receiver>();
					for (ReceiverPojo receiverPojo : receiverPojoSet) {
						Receiver receiver = new Receiver();
						receiver.setAmount(receiverPojo.getAmount());
						receiver.setCount(receiverPojo.getCount());
						receiver.setName(receiverPojo.getReveiver());
						String phones = receiverPojo.getPhonelist();
						List<String> phoneList = String2DecList(phones);
						receiver.setPhone_num_list(phoneList);
						
						receiverList.add(receiver);
						
					}
					addr.setReceiver(receiverList);
				}
				deliAddrList.add(addr);
			}
		}
		
		return deliAddrList;
	}


	/**
	 * @param regionPojoList
	 * @return
	 * @throws Exception 
	 */
	private List<ContactRegion> changeRegionPojoList2Bean(
			List<ContactRegionPojo> regionPojoList) throws Exception {
		List<ContactRegion> regionList = new ArrayList<ContactRegion>();
		if (regionPojoList != null && regionPojoList.size() > 0) {
			for (ContactRegionPojo regionPojo : regionPojoList) {
				ContactRegion region = new ContactRegion();
				BeanUtils.copyProperties(region, regionPojo);
				regionList.add(region);
			}
		}
		return regionList;
	}


	/**
	 * @param contactInfoPojoList
	 * @return
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 */
	private List<ContactInfo> changeContactInfoPojo2Bean(
			List<ContactInfoPojo> contactInfoPojoList) throws Exception {
		
		List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();

		if (contactInfoPojoList != null && contactInfoPojoList.size() > 0) {
			for (ContactInfoPojo infoPojo : contactInfoPojoList) {
				ContactInfo info = new ContactInfo();
				BeanUtils.copyProperties(info, infoPojo);
				String encPhoneNum = infoPojo.getPhone_num();
				if (!StringUtil.isEmpty(encPhoneNum)) {
					info.setPhone_num(synchExecutorService.decrypt(encPhoneNum));
				}
				contactInfoList.add(info);
			}
		}
		return contactInfoList;
	}


	/**
	 * @param contactPojo
	 * @return
	 * @throws Exception 
	 */
	private CollectionContact changeCollContactPojo2Bean(
			CollectionContactPojo contactPojo) throws Exception {
		
		CollectionContact contact = new CollectionContact();
		
		contact.setBegin_date(contactPojo.getBegin_date());
		contact.setContact_name(contactPojo.getContact_name());
		contact.setEnd_date(contactPojo.getEnd_date());
		contact.setTotal_amount(contactPojo.getTotal_amount());
		contact.setTotal_count(contactPojo.getTotal_count());
		
		Set<ContactDetailsPojo> detailPojoSet = contactPojo.getContact_details();
		if (detailPojoSet != null && detailPojoSet.size() > 0) {
			List<ContactDetails> detailList = new ArrayList<ContactDetails>();
			for (ContactDetailsPojo detailPojo : detailPojoSet) {
				ContactDetails detail = new ContactDetails();
				detail.setCall_cnt(detailPojo.getCall_cnt());
				detail.setCall_in_cnt(detailPojo.getCall_in_cnt());
				detail.setCall_len(detailPojo.getCall_len());
				detail.setCall_out_cnt(detailPojo.getCall_out_cnt());
				detail.setPhone_num("");
				String encPhoneNum = detailPojo.getPhone_num();
				if (!StringUtil.isEmpty(encPhoneNum)) {
					detail.setPhone_num(synchExecutorService.decrypt(encPhoneNum));
				}				
				detail.setPhone_num_loc(detailPojo.getPhone_num_loc());
				detail.setSms_cnt(detailPojo.getSms_cnt());
				detail.setTrans_end(detailPojo.getTrans_end());
				detail.setTrans_start(detailPojo.getTrans_start());
				
				detailList.add(detail);
			}
			
			contact.setContact_details(detailList);
		}
		
		return contact;
	}


	/**
	 * @param cellBehaviorPojoList
	 * @return
	 */
	private CellBehavior changeCellBehaviorPojoList2Bean(
			List<CellBehaviorPojo> cellBehaviorPojoList,String decPhoneNum) {
		CellBehavior cellBeha = new CellBehavior();

		List<Behavior> behaList = new ArrayList<Behavior>();
		for (CellBehaviorPojo cellBehaPojo : cellBehaviorPojoList) {
			Behavior beha = new Behavior();
			beha.setCall_cnt(cellBehaPojo.getCall_cnt());
			beha.setCall_in_cnt(cellBehaPojo.getCall_in_cnt());
			beha.setCall_in_time(cellBehaPojo.getCall_in_time());
			beha.setCall_out_cnt(cellBehaPojo.getCall_out_cnt());
			beha.setCall_out_time(cellBehaPojo.getCall_out_time());
			beha.setCell_loc(cellBehaPojo.getCell_loc());
			beha.setCell_mth(cellBehaPojo.getCell_mth());
			beha.setCell_operator(cellBehaPojo.getCell_operator());
			beha.setCell_operator_zh(cellBehaPojo.getCell_operator_zh());
			beha.setCell_phone_num(decPhoneNum);
			beha.setNet_flow(cellBehaPojo.getNet_flow());
			beha.setSms_cnt(cellBehaPojo.getSms_cnt());
			beha.setTotal_amount(cellBehaPojo.getTotal_amt());
			
			behaList.add(beha);
		}
		cellBeha.setPhone_num(decPhoneNum);
		cellBeha.setBehavior(behaList);
		
		return cellBeha;
	}


	

	/**
	 * @param behaviorCheckPojo
	 * @return
	 */
	private BehaviorCheck changeBehaviorPojo2Bean(
			BehaviorCheckPojo behaviorCheckPojo) {
		
		BehaviorCheck check = new BehaviorCheck();
		check.setCheck_point(behaviorCheckPojo.getCheck_point_en());
		check.setCheck_point_cn(behaviorCheckPojo.getCheck_point());
		check.setEvidence(behaviorCheckPojo.getEvidence());
		check.setResult(behaviorCheckPojo.getResult());
		check.setScore(behaviorCheckPojo.getScore());
		
		return check;
	}


	/**
	 * @param applyCheck2Pojo
	 * @return
	 * @throws Exception 
	 */
	private ApplicationCheck changeApplyCheck2Bean(
			ApplyCheck2Pojo applyCheck2Pojo) throws Exception {
		ApplicationCheck check = new ApplicationCheck();
		CheckPoints point = new CheckPoints();
		
		check.setApp_point(applyCheck2Pojo.getApp_point());
		point.setAge(applyCheck2Pojo.getAge());
		point.setCheck_addr(applyCheck2Pojo.getCheck_addr());
		point.setCheck_ebusiness(applyCheck2Pojo.getCheck_ebusiness());
		point.setCheck_idcard(applyCheck2Pojo.getCheck_idcard());
		point.setCheck_mobile(applyCheck2Pojo.getCheck_mobile());
		point.setCheck_name(applyCheck2Pojo.getCheck_name());
		point.setCheck_xiaohao(applyCheck2Pojo.getCheck_xiaohao());
		point.setCity(applyCheck2Pojo.getCity());
		point.setContact_name(applyCheck2Pojo.getContact_name());
		
		CourtBlacklist courtBlackList = new CourtBlacklist();
		courtBlackList.setArised("False");
		FinancialBlacklist financialBlackList = new FinancialBlacklist();
		financialBlackList.setArised("False");
		
		String courtBlack = applyCheck2Pojo.getCourt_black();
		if (!StringUtil.isEmpty(courtBlack)) {
			courtBlackList.setArised("True");
			String[] split = courtBlack.split(SEPARATOR);
			courtBlackList.setBlack_type(Arrays.asList(split));
		}		
		point.setCourt_blacklist(courtBlackList);
		String financialBlack = applyCheck2Pojo.getFinancial_black();
		if (!StringUtil.isEmpty(financialBlack)) {
			financialBlackList.setArised("True");
			String[] split = financialBlack.split(SEPARATOR);
			financialBlackList.setBlack_type(Arrays.asList(split));
		}	
		point.setFinancial_blacklist(financialBlackList);
		point.setGender(applyCheck2Pojo.getGender());
		String keyVal = applyCheck2Pojo.getKey_value();
		point.setKey_value(keyVal);
		if (encKeyMap.containsKey(applyCheck2Pojo.getApp_point())) {
			if (!StringUtil.isEmpty(keyVal)) {
				point.setKey_value(synchExecutorService.decrypt(keyVal));
			}
		}
		
		point.setProvince(applyCheck2Pojo.getProvince());
		point.setReg_time(applyCheck2Pojo.getReg_time());
		point.setRegion(applyCheck2Pojo.getRegion());
		point.setRelationship(applyCheck2Pojo.getRelationship());
		point.setReliability(applyCheck2Pojo.getReliability());
		point.setWebsite(applyCheck2Pojo.getWebsite());
		
		check.setCheck_points(point);
		
		return check;
	}


	/**
	 * @param report
	 * @param applyInfoCheck 
	 * @param requestId
	 * @param nowTime
	 * @return
	 * @throws Exception 
	 */
	private PersonPojo change2PersonPojo(Report report, List<ApplicationCheck> checkList, String requestId,
			Date nowTime) throws Exception {
		String rptId = report.getRpt_id();
		String token = report.getToken();
		String updTime = report.getUpdate_time();
		String version = report.getVersion();
		PersonPojo personPojo = new PersonPojo();
		
		if (checkList != null && checkList.size() > 0 ) {
			for (ApplicationCheck check : checkList) {
				if ("user_name".equals(check.getApp_point())) {
					CheckPoints point = check.getCheck_points();
					personPojo.setReal_name(point.getKey_value());
				}else if("id_card".equals(check.getApp_point())){
					CheckPoints point = check.getCheck_points();
					personPojo.setAge(point.getAge());
					personPojo.setCity(point.getCity());
					personPojo.setGender(point.getGender());
					String idCard = point.getKey_value();
					personPojo.setId_card_num(idCard);
					if (!StringUtil.isEmpty(idCard)) {
						personPojo.setId_card_num(synchExecutorService.encrypt(idCard));
					}
					
					personPojo.setProvince(point.getProvince());
					personPojo.setRegion(point.getRegion());
				}
			}
		}

//		personPojo.setRegion(region);
//		personPojo.setSign(sign);
//		personPojo.setState(state);
//		personPojo.setStatus(status);
		personPojo.setSuccess("true");
		personPojo.setToken(token);
		personPojo.setVersion(version);
		
		personPojo.setRequestId(requestId);
		personPojo.setCrt_time(nowTime);
		personPojo.setUpd_time(nowTime);
		
		return personPojo;
	}

	/**
	 * @param userInfoCheck
	 * @param requestId
	 * @param nowTime
	 * @return
	 * @throws Exception 
	 */
	private UserInfoCheckPojo change2UserInfoCheckPojo(
			UserInfoCheck userInfoCheck, String requestId, Date nowTime) throws Exception {
		UserInfoCheckPojo userInfoPojo = new UserInfoCheckPojo();
		userInfoPojo.setRequestId(requestId);
		CheckBlackInfo blackInfo = userInfoCheck.getCheck_black_info();
		userInfoPojo.setContacts_class1_blacklist_cnt(blackInfo.getContacts_class1_blacklist_cnt());
		userInfoPojo.setContacts_class1_cnt(blackInfo.getContacts_class1_cnt());
		userInfoPojo.setContacts_class2_blacklist_cnt(blackInfo.getContacts_class2_blacklist_cnt());
		userInfoPojo.setContacts_router_cnt(blackInfo.getContacts_router_cnt());
		userInfoPojo.setContacts_router_ratio(blackInfo.getContacts_router_ratio());
		userInfoPojo.setPhone_gray_score(blackInfo.getPhone_gray_score());
		
		CheckSearchInfo searchInfo = userInfoCheck.getCheck_search_info();
		List<String> arised_open_web = searchInfo.getArised_open_web();
		userInfoPojo.setArised_open_web(list2String(arised_open_web));
		List<String> idcard_with_other_names = searchInfo.getIdcard_with_other_names();
		userInfoPojo.setIdcard_with_other_names(list2String(idcard_with_other_names));
		//敏感字段需要加密
		List<String> idcard_with_other_phones = searchInfo.getIdcard_with_other_phones();
		userInfoPojo.setIdcard_with_other_phones(list2EncString(idcard_with_other_phones));
		//敏感字段需要加密
		List<String> phone_with_other_idcards = searchInfo.getPhone_with_other_idcards();
		userInfoPojo.setPhone_with_other_idcards(list2EncString(phone_with_other_idcards));
		List<String> phone_with_other_names = searchInfo.getPhone_with_other_names();
		userInfoPojo.setPhone_with_other_names(list2String(phone_with_other_names));
		String register_org_cnt = searchInfo.getRegister_org_cnt();
		userInfoPojo.setRegister_org_cnt(register_org_cnt);
		List<String> register_org_type = searchInfo.getRegister_org_type();
		userInfoPojo.setRegister_org_type(list2String(register_org_type));
		String searched_org_cnt = searchInfo.getSearched_org_cnt();
		userInfoPojo.setSearched_org_cnt(searched_org_cnt);
		List<String> searched_org_type = searchInfo.getSearched_org_type();
		userInfoPojo.setSearched_org_type(list2String(searched_org_type));
		
		return userInfoPojo;
	}
	
	/**
	 * @param idcard_with_other_phones
	 * @return
	 * @throws Exception 
	 */
	private String list2EncString(List<String> strList) throws Exception {
		if (strList != null && strList.size() > 0) {
			List<String> encStrList = new ArrayList<String>();
			for (String val : strList) {
				if (!StringUtil.isEmpty(val)) {
					encStrList.add(synchExecutorService.encrypt(val));
				}
			}
			String join = StringUtils.join(encStrList, SEPARATOR);
			return join;
		}else{
			return "";
		}
	}


	private String list2String(List<String> strList){
		
		if (strList != null && strList.size() > 0) {
			String join = StringUtils.join(strList, SEPARATOR);
			return join;
		}else{
			return "";
		}
		
		
	}

	/**
	 * @param tripInfo
	 * @param requestId
	 * @param nowTime
	 * @return
	 */
	private TripInfoPojo change2TripInfoPojo(TripInfo tripInfo,
			String requestId, Date nowTime) {
		
		TripInfoPojo tripPojo = new TripInfoPojo();

		tripPojo.setRequestId(requestId);
		tripPojo.setCrt_time(nowTime);
		tripPojo.setUpd_time(nowTime);
		
		tripPojo.setTrip_dest(tripInfo.getTrip_dest());
		tripPojo.setTrip_end_time(tripInfo.getTrip_end_time());
		tripPojo.setTrip_leave(tripInfo.getTrip_leave());
		tripPojo.setTrip_start_time(tripInfo.getTrip_start_time());
		tripPojo.setTrip_type(tripInfo.getTrip_type());
		
		return tripPojo;
	}

	/**
	 * @param main
	 * @param requestId
	 * @param nowTime
	 * @return
	 */
	private MainServicePojo change2MainServicePojo(MainService main,
			String requestId, Date nowTime) throws Exception {
		MainServicePojo mainPojo = new MainServicePojo();
		
		mainPojo.setRequestId(requestId);
		mainPojo.setCrt_time(nowTime);
		mainPojo.setUpd_time(nowTime);
		
		mainPojo.setCompany_name(main.getCompany_name());
		mainPojo.setCompany_type(main.getCompany_type());
		mainPojo.setTotal_service_cnt(main.getTotal_service_cnt());
		
		
		List<ServiceDetail> serviceDetails = main.getService_details();
		if (serviceDetails != null && serviceDetails.size() > 0) {
			Set<ServiceDetailPojo> detailPojoSet = new HashSet<ServiceDetailPojo>();
			for (ServiceDetail detail : serviceDetails) {
				ServiceDetailPojo detailPojo = new ServiceDetailPojo();
				detailPojo.setRequestId(requestId);
				detailPojo.setCrt_time(nowTime);
				detailPojo.setUpd_time(nowTime);
				detailPojo.setInteract_cnt(detail.getInteract_cnt());
				detailPojo.setInteract_mth(detail.getInteract_mth());
				
				detailPojoSet.add(detailPojo);
			}
			mainPojo.setService_details(detailPojoSet);
		}
		
		return mainPojo;
	}

	/**
	 * @param deliverAddr
	 * @param requestId
	 * @param nowTime
	 * @return
	 */
	private DeliverAddressPojo change2DeliverAddrPojo(
			DeliverAddress deliverAddr, String requestId, Date nowTime) throws Exception{
		
		DeliverAddressPojo deliverAddrPojo = new DeliverAddressPojo();
		deliverAddrPojo.setRequestId(requestId);
		deliverAddrPojo.setCrt_time(nowTime);
		deliverAddrPojo.setUpd_time(nowTime);
		
		deliverAddrPojo.setAddr(deliverAddr.getAddress());
		deliverAddrPojo.setBegin_date(deliverAddr.getBegin_date());
		deliverAddrPojo.setEnd_date(deliverAddr.getEnd_date());
		deliverAddrPojo.setLat(deliverAddr.getLat());
		deliverAddrPojo.setLng(deliverAddr.getLng());
		deliverAddrPojo.setPredict_addr_type(deliverAddr.getPredict_addr_type());
		deliverAddrPojo.setTotal_amount(deliverAddr.getTotal_amount());
		deliverAddrPojo.setTotal_count(deliverAddr.getTotal_count());
		List<Receiver> receiverList = deliverAddr.getReceiver();
		if (receiverList != null && receiverList.size() > 0) {
			Set<ReceiverPojo> receiverPojoSet = new HashSet<ReceiverPojo>();
			for (Receiver receiver : receiverList) {
				ReceiverPojo receiverPojo = new ReceiverPojo();
				receiverPojo.setRequestId(requestId);
				receiverPojo.setCrt_time(nowTime);
				receiverPojo.setUpd_time(nowTime);
				receiverPojo.setReveiver(receiver.getName());
				receiverPojo.setAmount(receiver.getAmount());
				receiverPojo.setCount(receiver.getCount());
				List<String> phoneNumList = receiver.getPhone_num_list();
				receiverPojo.setPhonelist(list2EncString(phoneNumList));
				receiverPojoSet.add(receiverPojo);
			}
			deliverAddrPojo.setReceivers(receiverPojoSet);
			
		}
		return deliverAddrPojo;
	}

	/**
	 * @param region
	 * @param requestId
	 * @param nowTime
	 * @return
	 */
	private ContactRegionPojo change2ContactRegionPojo(ContactRegion region,
			String requestId, Date nowTime) throws Exception{
		ContactRegionPojo regionPojo = new ContactRegionPojo();
		BeanUtils.copyProperties(regionPojo, region);
		regionPojo.setRequestId(requestId);
		regionPojo.setCrt_time(nowTime);
		regionPojo.setUpd_time(nowTime);
		
		return regionPojo;
	}

	/**
	 * @param contactInfo
	 * @param requestId
	 * @param nowTime
	 * @return
	 */
	private ContactInfoPojo change2ContactInfoPojo(ContactInfo contactInfo,
			String requestId, Date nowTime) throws Exception{
		
		ContactInfoPojo infoPojo = new ContactInfoPojo();
		BeanUtils.copyProperties(infoPojo, contactInfo);
		String phoneNum = contactInfo.getPhone_num();
		if (!StringUtil.isEmpty(phoneNum)) {
			infoPojo.setPhone_num(synchExecutorService.encrypt(phoneNum));
		}
		infoPojo.setRequestId(requestId);
		infoPojo.setCrt_time(nowTime);
		infoPojo.setUpd_time(nowTime);
		
		return infoPojo;
	}

	/**
	 * @param collContact
	 * @param requestId
	 * @param nowTime
	 * @return
	 * @throws InvocationTargetException 
	 * @throws  
	 */
	private CollectionContactPojo change2CollContactPojo(
			CollectionContact collectionContact, String requestId, Date nowTime) throws Exception {
		
		CollectionContactPojo contactPojo = new CollectionContactPojo();
		
		contactPojo.setCrt_time(nowTime);
		contactPojo.setUpd_time(nowTime);					
		contactPojo.setBegin_date(collectionContact.getBegin_date());
		contactPojo.setContact_name(collectionContact.getContact_name());
		contactPojo.setEnd_date(collectionContact.getEnd_date());
		contactPojo.setRequestId(requestId);
		contactPojo.setTotal_amount(collectionContact.getTotal_amount());
		contactPojo.setTotal_count(collectionContact.getTotal_count());
		//联系明细
		List<ContactDetails> contact_details = collectionContact.getContact_details();
		if (contact_details != null && contact_details.size() > 0) {
			Set<ContactDetailsPojo> detailPojoSet = new HashSet<ContactDetailsPojo>();
			for (ContactDetails detail : contact_details) {
				ContactDetailsPojo detailPojo = new ContactDetailsPojo();
				detailPojo.setRequestId(requestId);
				detailPojo.setCrt_time(nowTime);
				detailPojo.setUpd_time(nowTime);
				BeanUtils.copyProperties(detailPojo, detail);
				String phoneNum = detail.getPhone_num();
				if (!StringUtil.isEmpty(phoneNum)) {
					detailPojo.setPhone_num(synchExecutorService.encrypt(phoneNum));
				}
				detailPojoSet.add(detailPojo);
			}
			contactPojo.setContact_details(detailPojoSet);
		}
		
		return contactPojo;
	}

	/**
	 * @param behavior
	 * @param requestId
	 * @param nowTime
	 * @return
	 */
	private CellBehaviorPojo change2CellBehaviorPojo(Behavior behavior,String encPhoneNum,
			String requestId, Date nowTime) {
		CellBehaviorPojo pojo = new CellBehaviorPojo();
		pojo.setRequestId(requestId);
		pojo.setCrt_time(nowTime);
		pojo.setUpd_time(nowTime);
		
		pojo.setSms_cnt(behavior.getSms_cnt());
		pojo.setCell_operator(behavior.getCell_operator());
		pojo.setCell_operator_zh(behavior.getCell_operator_zh());
		pojo.setPhone_num(encPhoneNum);
		pojo.setCell_loc(behavior.getCell_loc());
		pojo.setCell_mth(behavior.getCell_mth());
		pojo.setNet_flow(behavior.getNet_flow());
		pojo.setTotal_amt(behavior.getTotal_amount());
		pojo.setCall_cnt(behavior.getCall_cnt());
		pojo.setCall_out_cnt(behavior.getCall_out_cnt());
		pojo.setCall_out_time(behavior.getCall_out_time());
		pojo.setCall_in_cnt(behavior.getCall_in_cnt());
		pojo.setCall_in_time(behavior.getCall_in_time());
		
		return pojo;
	}

	/**
	 * @param behaviorCheck
	 * @return
	 */
	private BehaviorCheckPojo change2BehaviorCheckPojo(
			BehaviorCheck behaviorCheck,String requestId,Date nowTime) {
		BehaviorCheckPojo behaviorPojo = new BehaviorCheckPojo();
		behaviorPojo.setRequestId(requestId);
		behaviorPojo.setCheck_point(behaviorCheck.getCheck_point_cn());
		behaviorPojo.setCheck_point_en(behaviorCheck.getCheck_point());
		behaviorPojo.setEvidence(behaviorCheck.getEvidence());
		behaviorPojo.setResult(behaviorCheck.getResult());
		behaviorPojo.setScore(behaviorCheck.getScore());
		behaviorPojo.setCrt_time(nowTime);
		behaviorPojo.setUpd_time(nowTime);
		return behaviorPojo;
	}

	/**
	 * @param appChcek
	 * @return
	 * @throws Exception 
	 */
	private ApplyCheck2Pojo changeToAppCheck(ApplicationCheck appChcek,String requestId) throws Exception {
		ApplyCheck2Pojo check2Pojo = new ApplyCheck2Pojo();
		String appPoint = appChcek.getApp_point();
		CheckPoints checkPoint = appChcek.getCheck_points();
		
		check2Pojo.setApp_point(appPoint);
		check2Pojo.setAge(checkPoint.getAge());
		check2Pojo.setGender(checkPoint.getGender());
		check2Pojo.setCheck_addr(checkPoint.getCheck_addr());
		check2Pojo.setCheck_ebusiness(checkPoint.getCheck_ebusiness());
		check2Pojo.setCheck_idcard(checkPoint.getCheck_idcard());
		check2Pojo.setCheck_mobile(checkPoint.getCheck_mobile());
		check2Pojo.setCheck_name(checkPoint.getCheck_name());
		check2Pojo.setCheck_xiaohao(checkPoint.getCheck_xiaohao());
		check2Pojo.setCity(checkPoint.getCity());
		check2Pojo.setContact_name(checkPoint.getContact_name());
		CourtBlacklist courtBlack = checkPoint.getCourt_blacklist();
		
		if (courtBlack != null ) {
			List<String> blackTypeList = courtBlack.getBlack_type();
			check2Pojo.setCourt_black(list2String(blackTypeList));
		}
		FinancialBlacklist financialBlack = checkPoint.getFinancial_blacklist();
		if (financialBlack != null) {
			List<String> blackTypeList = financialBlack.getBlack_type();
			check2Pojo.setFinancial_black(list2String(blackTypeList));
			
		}
		String keyVal = checkPoint.getKey_value();
		if (encKeyMap.containsKey(appPoint)) {
			if (!StringUtil.isEmpty(keyVal)) {
				check2Pojo.setKey_value(synchExecutorService.encrypt(keyVal));
			}
		}else{
			check2Pojo.setKey_value(keyVal);
		}		
		check2Pojo.setProvince(checkPoint.getProvince());
		check2Pojo.setReg_time(checkPoint.getReg_time());
		check2Pojo.setRegion(checkPoint.getRegion());
		check2Pojo.setRelationship(checkPoint.getRelationship());
		check2Pojo.setReliability(checkPoint.getReliability());
		check2Pojo.setRequestId(requestId);
		check2Pojo.setWebsite(checkPoint.getWebsite());
		
		
		return check2Pojo;
	}

	

}
