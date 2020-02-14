/**   
* @Description: 信用卡账单数据Service 实现类
* @author xiaobin.hou  
* @date 2016年7月26日 下午2:04:41 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditBase;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditBillInfo;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditDetail;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditInstallment;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditTransDetail;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditTransSum;
import com.wanda.credit.ds.client.juxinli.service.IJXLCreditBillDataService;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardAmtPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardApplyPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardBillInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardInstallmentPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardTransDetailPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardTransSumPojo;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardApplyService;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardBillInfoService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLCreditBillDataServiceImpl implements IJXLCreditBillDataService {
	
	private static final Logger logger = LoggerFactory.getLogger(JXLCreditBillDataServiceImpl.class);

	@Autowired
	private IJXLCreditCardBillInfoService billInfoService;
	@Autowired
	private IJXLCreditCardApplyService applyService;

	
	public boolean addData(List<CreditCardBillInfoPojo> billPojoList,
			String requestId) {
		boolean isSaved = true;
		//判断本地是否有缓存数据
		CreditCardApplyPojo applyInfo = applyService.queryApplyInfoByStatus(requestId, "102");
		if ("1".equals(applyInfo.getLoad_data())) {
			return isSaved;
		}
		//无缓存数据，保存数据
		if (billPojoList != null && billPojoList.size() > 0) {
			try {
				billInfoService.add(billPojoList);
				//更新获取数据标识位
				applyInfo.setLoad_data("1");
				applyInfo.setUpdate_date(new Date());
				applyService.updateApplyInfo(applyInfo);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("保存数据异常");
				isSaved = false;
			}
		
		}		
		return isSaved;
		
	}


	
	public List<CreditDetail> loadCacheData(String requestId) throws Exception {
		
		if (StringUtil.isEmpty(requestId)) {
			return null;
		}
		
		List<String> dsList = billInfoService.queryDataSource(requestId);
		
		List<CreditDetail> detailList = new ArrayList<CreditDetail>();
		
		for (String datasource : dsList) {
			CreditCardBillInfoPojo billInfoPojo = new CreditCardBillInfoPojo();
			billInfoPojo.setRequestId(requestId);
			billInfoPojo.setDatasource(datasource);
			List<CreditCardBillInfoPojo> billPojoList = billInfoService.query(billInfoPojo);
			
			CreditDetail detail = parsePojoToBean(billPojoList);
			
			detailList.add(detail);
		}
		
		
		
		return detailList;
	}



	/**
	 * @param billPojoList
	 * @return
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 */
	private CreditDetail parsePojoToBean(
			List<CreditCardBillInfoPojo> billPojoList) throws Exception {
		
		if (billPojoList == null || billPojoList.size() < 1) {
			return null;
		}
		
		CreditDetail detail = new CreditDetail();
		
		detail.setBank_name(billPojoList.get(0).getBank_name());
		detail.setDatasource(billPojoList.get(0).getDatasource());
		detail.setEmail(billPojoList.get(0).getEmail());

		
		List<CreditBillInfo> billList = new ArrayList<CreditBillInfo>();
		
		for (CreditCardBillInfoPojo billInfoPojo : billPojoList) {
			
			CreditBillInfo bill = new CreditBillInfo();
			
			bill.setCard_number(billInfoPojo.getCard_number());
			bill.setInternaldate(billInfoPojo.getInternaldate());
			bill.setPayment_due_date(billInfoPojo.getPayment_due_date());
			bill.setStatement_cycle(billInfoPojo.getStatement_cycle());
			bill.setStatement_date(billInfoPojo.getStatement_date());
			bill.setUser_name(billInfoPojo.getUser_name());
			bill.setReceived(billInfoPojo.getReceived());
			bill.setFrom(billInfoPojo.getFrom());
			
			List<CreditBase> creditList 	= new ArrayList<CreditBase>();
			List<CreditBase> cashList 		= new ArrayList<CreditBase>();
			List<CreditBase> currentBalList = new ArrayList<CreditBase>();
			List<CreditBase> minPayList 	= new ArrayList<CreditBase>();
			Set<CreditCardAmtPojo> amtPojoSet = billInfoPojo.getAmtSet();
			if (amtPojoSet != null && amtPojoSet.size() > 0) {
				for (CreditCardAmtPojo amtPojo : amtPojoSet) {
					CreditBase amt = new CreditBase();
					BeanUtils.copyProperties(amt, amtPojo);
					if ("CREDIT".equals(amtPojo.getKey_code())) {
						creditList.add(amt);
					}else if("CASH".equals(amtPojo.getKey_code())){
						cashList.add(amt);
					}else if("BALANCE".equals(amtPojo.getKey_code())){
						currentBalList.add(amt);
					}else if("MINIMUM_PAY".equals(amtPojo.getKey_code())){
						minPayList.add(amt);
					}
				}
			}
			
			bill.setCredit_limit(creditList);
			bill.setCash_advance_limit(cashList);
			bill.setCurrent_balance(currentBalList);
			bill.setMinimum_payment_due(minPayList);
			
			Set<CreditCardTransSumPojo> transSumSet = billInfoPojo.getTransSumSet();
			List<CreditTransSum> sumList = new ArrayList<CreditTransSum>();
			if (transSumSet != null && transSumSet.size() > 0) {
				for (CreditCardTransSumPojo sumPojo : transSumSet) {
					CreditTransSum sum = new CreditTransSum();
					BeanUtils.copyProperties(sum, sumPojo);
					sumList.add(sum);
				}
			}
			bill.setTransaction_summary(sumList);
			
			Set<CreditCardTransDetailPojo> detailPojoSet = billInfoPojo.getTransDetailSet();
			List<CreditTransDetail> transDetailList = new ArrayList<CreditTransDetail>();
			if (detailPojoSet != null && detailPojoSet.size() > 0) {
				for (CreditCardTransDetailPojo trasDetailPojo : detailPojoSet) {
					CreditTransDetail tranDetail = new CreditTransDetail();
					BeanUtils.copyProperties(tranDetail, trasDetailPojo);
					transDetailList.add(tranDetail);
				}
			}
			bill.setTransaction_detail(transDetailList);
			
			
			
			Set<CreditCardInstallmentPojo> instalPojoSet = billInfoPojo.getInstallmentSet();
			List<CreditInstallment> instalList = new ArrayList<CreditInstallment>();
			if (instalPojoSet != null && instalPojoSet.size() > 0) {
				for (CreditCardInstallmentPojo instalPojo : instalPojoSet) {
					CreditInstallment install = new CreditInstallment();
					BeanUtils.copyProperties(install, instalPojo);
					instalList.add(install);
				}
			}
			bill.setInstallment_plan_info(instalList);
			
			billList.add(bill);
			
		}
		
			
		detail.setBill_info(billList);

		return detail;
	}

}
