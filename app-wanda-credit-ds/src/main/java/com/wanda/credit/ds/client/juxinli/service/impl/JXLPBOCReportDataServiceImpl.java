/**   
* @Description: 征信报告数据ServiceImpl
* @author xiaobin.hou  
* @date 2016年7月11日 下午7:01:40 
* @version V1.0   
*/
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

import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.CreditNoOverdueAccount;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.CreditOverdueAccount;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.CreditRecord;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.CreditTransaction;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.GuaranteeDetail;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.LoanNoOverdueAccount;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.LoanOverdueAccount;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.QueryDetail;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordCreditLoanInfo;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordGuarantee;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordHousingLoanInfo;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordOtherLoanInfo;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordQuery;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.Summary;
import com.wanda.credit.ds.client.juxinli.service.IJXLPBOCReportDataService;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataGuaranteePojo;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataQueryPojo;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataRecordPojo;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataResPojo;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataSummaryPojo;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCDataGuaranteeService;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCDataQueryService;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCDataRecordService;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCDataResService;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCDataSummaryService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLPBOCReportDataServiceImpl implements IJXLPBOCReportDataService {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLPBOCReportDataServiceImpl.class);	
	private final static String FLAG_0 = "0";
	private final static String FLAG_1 = "1";
	private final static String FLAG_2 = "2";
	private final static String FLAG_3 = "3";

	@Autowired
	private IJXLPBOCDataGuaranteeService guaranteeService;
	@Autowired
	private IJXLPBOCDataQueryService queryService;
	@Autowired
	private IJXLPBOCDataRecordService recordService;
	@Autowired
	private IJXLPBOCDataSummaryService summaryService;
	@Autowired
	private IJXLPBOCDataResService resService;

	public void addReportData(CreditTransaction tran,String requestId,String prefix,PBOCDataResPojo resPojo)  {
		
		if (tran == null) {
			return;
		}
		
		Date nowTime = new Date();
		
		try{
			if(resPojo != null){
				resPojo.setGet_data_time(tran.getUpdate_time());
				resPojo.setQuery_marriage(tran.getQuery_marriage());
				resPojo.setQuery_name(tran.getQueried_name());
				resPojo.setQuery_number(tran.getQueried_number());
				resPojo.setQuery_papers(tran.getQueried_papers());
				resPojo.setReport_id(tran.getReport_id());
				resPojo.setReport_time(tran.getReport_time());
				resPojo.setRequest_time(tran.getRequest_time());
				resPojo.setVersion(tran.getVersion());
				
			}else{
				resPojo = new PBOCDataResPojo();
			}
			
			resPojo.setRequestId(requestId);
			
			CreditRecord credit_record = tran.getCredit_record();						
			Set<PBOCDataGuaranteePojo> guaranteePojoSet = getPBOCGuaranteePojoSet(
					credit_record, requestId, nowTime);
			resPojo.setGuaranteeSet(guaranteePojoSet);
			logger.info("{} 保存担保信息正常" , prefix);
			
			Set<PBOCDataRecordPojo> recordPojoSet = getPBOCRecordPojoSet(
					credit_record, requestId, nowTime);
			resPojo.setRecordSet(recordPojoSet);
			logger.info("{} 保存信贷信息成功" , prefix);
			
			Set<PBOCDataSummaryPojo> summaryPojoSet = getPBOCSummaryPojoSet(
					credit_record, requestId, nowTime);			
			resPojo.setSummarySet(summaryPojoSet);
			logger.info("{} 保存汇总信息成功" , prefix);
			
			RecordQuery query = tran.getQuery();
			
			Set<PBOCDataQueryPojo> queryPojoSet = getQueryPojoSet(query,requestId,nowTime);
			resPojo.setQuerySet(queryPojoSet);
			logger.info("{} 保存查询记录信息成功" , prefix);
			
			
			resService.merge(resPojo);
		
		}catch(Exception e){
			logger.error("{} 保存报告数据异常 {}" , prefix , e.getMessage());
		}
	}

	/**
	 * @param query
	 * @param requestId
	 * @param nowTime
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	private Set<PBOCDataQueryPojo> getQueryPojoSet(RecordQuery query,
			String requestId, Date nowTime) throws Exception {
		if (query == null) {
			return null;
		}
		Set<PBOCDataQueryPojo> queryPojoSet = new HashSet<PBOCDataQueryPojo>();
		List<QueryDetail> institution_query_details = query.getInstitution_query_details();
		if (institution_query_details != null && institution_query_details.size() > 0) {
			for (QueryDetail queryDetail : institution_query_details) {
				PBOCDataQueryPojo queryPojo = new PBOCDataQueryPojo();
				BeanUtils.copyProperties(queryPojo, queryDetail);
				queryPojo.setType(FLAG_0);
				queryPojo.setCreate_date(nowTime);
				queryPojo.setUpdate_date(nowTime);
				queryPojoSet.add(queryPojo);
			}
		}
		
		List<QueryDetail> personal_query_details = query.getPersonal_query_details();
		if (personal_query_details != null && personal_query_details.size() > 0) {
			for (QueryDetail queryDetail : personal_query_details) {
				PBOCDataQueryPojo queryPojo = new PBOCDataQueryPojo();
				BeanUtils.copyProperties(queryPojo, queryDetail);
				queryPojo.setType(FLAG_1);
				queryPojo.setCreate_date(nowTime);
				queryPojo.setUpdate_date(nowTime);
				queryPojoSet.add(queryPojo);
			}
		}
		
		return queryPojoSet;
	}

	/**
	 * @param credit_record
	 * @param requestId
	 * @param nowTime
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	private Set<PBOCDataSummaryPojo> getPBOCSummaryPojoSet(
			CreditRecord credit_record, String requestId, Date nowTime) throws Exception {
		if (credit_record == null) {
			return null;
		}
		List<Summary> summarys = credit_record.getSummarys();	
		
		if (summarys != null && summarys.size() > 0) {
			Set<PBOCDataSummaryPojo> summaryPojoSet = new HashSet<PBOCDataSummaryPojo>();
			for (Summary summary : summarys) {
				PBOCDataSummaryPojo pbocSummary = new PBOCDataSummaryPojo();
				BeanUtils.copyProperties(pbocSummary,summary);
				pbocSummary.setCreate_date(nowTime);
				pbocSummary.setUpdate_date(nowTime);
				summaryPojoSet.add(pbocSummary);
			}
			return summaryPojoSet;
		}else{
			return null;
		}
		
	}

	/**
	 * @param credit_record
	 * @param requestId
	 * @param nowTime
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	private Set<PBOCDataRecordPojo> getPBOCRecordPojoSet(
			CreditRecord credit_record, String requestId, Date nowTime) throws Exception {
		
		if (credit_record == null) {
			return null;
		}
		
		Set<PBOCDataRecordPojo> recordPojoSet = new HashSet<PBOCDataRecordPojo>();
		RecordCreditLoanInfo credit_info = credit_record.getCredit_info();
		if (credit_info != null) {
			List<CreditNoOverdueAccount> creditNoOverdueList = credit_info.getCredit_no_overdue_account();
			
			if (creditNoOverdueList != null && creditNoOverdueList.size() > 0) {
				for (CreditNoOverdueAccount creditNoOverdueAccount : creditNoOverdueList) {
					PBOCDataRecordPojo recordPojo = new PBOCDataRecordPojo();
					BeanUtils.copyProperties(recordPojo, creditNoOverdueAccount);
					recordPojo.setCredit_record_type(FLAG_2);
					recordPojo.setIs_overdue(FLAG_0);
//					recordPojo.setRequestId(requestId);
					recordPojo.setCreate_date(nowTime);
					recordPojo.setUpdate_date(nowTime);
					recordPojoSet.add(recordPojo);
				}
			}
			
			List<CreditOverdueAccount> creditOverdueList = credit_info.getCredit_overdue_account();
			if (creditOverdueList != null && creditOverdueList.size() > 0) {
				for (CreditOverdueAccount creditOverdueAccount : creditOverdueList) {
					PBOCDataRecordPojo recordPojo = new PBOCDataRecordPojo();
					BeanUtils.copyProperties(recordPojo, creditOverdueAccount);
					recordPojo.setCredit_record_type(FLAG_2);
					recordPojo.setIs_overdue(FLAG_1);
//					recordPojo.setRequestId(requestId);
					recordPojo.setCreate_date(nowTime);
					recordPojo.setUpdate_date(nowTime);
					recordPojoSet.add(recordPojo);
				}
			}
			
		}		
		
		RecordHousingLoanInfo housing_loan_info = credit_record.getHousing_loan_info();
		
		if (housing_loan_info != null) {
			List<LoanNoOverdueAccount> housingNoOverdueList = housing_loan_info.getLoan_no_overdue_account();
			if (housingNoOverdueList != null && housingNoOverdueList.size() > 0) {
				for (LoanNoOverdueAccount loanNoOverdueAccount : housingNoOverdueList) {
					PBOCDataRecordPojo recordPojo = new PBOCDataRecordPojo();
					BeanUtils.copyProperties(recordPojo, loanNoOverdueAccount);
					recordPojo.setCredit_record_type(FLAG_1);
					recordPojo.setIs_overdue(FLAG_0);
//					recordPojo.setRequestId(requestId);
					recordPojo.setCreate_date(nowTime);
					recordPojo.setUpdate_date(nowTime);
					recordPojoSet.add(recordPojo);
				}
			}
			
			List<LoanOverdueAccount> housingOverdueList = housing_loan_info.getLoan_overdue_account();
			
			if (housingOverdueList != null && housingOverdueList.size() > 0) {
				for (LoanOverdueAccount loanOverdueAccount : housingOverdueList) {
					PBOCDataRecordPojo recordPojo = new PBOCDataRecordPojo();
					BeanUtils.copyProperties(recordPojo, loanOverdueAccount);
					recordPojo.setCredit_record_type(FLAG_1);
					recordPojo.setIs_overdue(FLAG_1);
//					recordPojo.setRequestId(requestId);
					recordPojo.setCreate_date(nowTime);
					recordPojo.setUpdate_date(nowTime);
					recordPojoSet.add(recordPojo);
				}
			}
			
		}
		
		
		RecordOtherLoanInfo loan_info = credit_record.getLoan_info();
		if (loan_info != null) {
			List<LoanNoOverdueAccount> otherNoOverdueList = loan_info.getLoan_no_overdue_account();
			if (otherNoOverdueList != null && otherNoOverdueList.size() > 0) {
				for (LoanNoOverdueAccount loanNoOverdueAccount : otherNoOverdueList) {
					PBOCDataRecordPojo recordPojo = new PBOCDataRecordPojo();
					BeanUtils.copyProperties(recordPojo, loanNoOverdueAccount);
					recordPojo.setCredit_record_type(FLAG_3);
					recordPojo.setIs_overdue(FLAG_0);
//					recordPojo.setRequestId(requestId);
					recordPojo.setCreate_date(nowTime);
					recordPojo.setUpdate_date(nowTime);
					recordPojoSet.add(recordPojo);
				}
			}
			
			List<LoanOverdueAccount> otherOverdueList = loan_info.getLoan_overdue_account();
			if (otherOverdueList != null && otherOverdueList.size() > 0) {
				for (LoanOverdueAccount loanOverdueAccount : otherOverdueList) {
					PBOCDataRecordPojo recordPojo = new PBOCDataRecordPojo();
					BeanUtils.copyProperties(recordPojo, loanOverdueAccount);
					recordPojo.setCredit_record_type(FLAG_3);
					recordPojo.setIs_overdue(FLAG_1);
//					recordPojo.setRequestId(requestId);
					recordPojo.setCreate_date(nowTime);
					recordPojo.setUpdate_date(nowTime);
					recordPojoSet.add(recordPojo);
				}
			}
			
		}
		
		return recordPojoSet;
	}

	/**
	 * @param guarantee
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	private Set<PBOCDataGuaranteePojo> getPBOCGuaranteePojoSet(
			CreditRecord credit_record,String requestId,Date nowTime) throws Exception {
		if (credit_record == null) {
			return null;
		}
		RecordGuarantee guarantee = credit_record.getGuarantee();
		if (guarantee == null) {
			return null;
		}
		List<GuaranteeDetail> guaranteeDetailList = guarantee.getGuarantee_detail();
		if (guaranteeDetailList != null && guaranteeDetailList.size() > 0) {
			Set<PBOCDataGuaranteePojo> guaranteePojoSet = new HashSet<PBOCDataGuaranteePojo>();
			for (GuaranteeDetail guaranteeDetail : guaranteeDetailList) {
				PBOCDataGuaranteePojo guaranteePojo = new PBOCDataGuaranteePojo();
				BeanUtils.copyProperties(guaranteePojo, guaranteeDetail);
//				guaranteePojo.setRequestId(requestId);
				guaranteePojo.setCreate_date(nowTime);
				guaranteePojo.setUpdate_date(nowTime);
				guaranteePojoSet.add(guaranteePojo);
			}
			return guaranteePojoSet;
		}else{
			return null;
		}
	}


}
