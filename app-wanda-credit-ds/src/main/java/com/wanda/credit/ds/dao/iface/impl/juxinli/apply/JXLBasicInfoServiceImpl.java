package com.wanda.credit.ds.dao.iface.impl.juxinli.apply;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLBasicInfoService;
@Service
@Transactional
public class JXLBasicInfoServiceImpl extends BaseServiceImpl<ApplyBasicInfoPojo> implements
		IJXLBasicInfoService {

	public ApplyBasicInfoPojo getValidTokenByRequestId(String requestId) {
		
		String hql = "FROM ApplyBasicInfoPojo b where b.requestId =:requestId and b.success =:success";
		
		ApplyBasicInfoPojo params = new ApplyBasicInfoPojo();
		params.setRequestId(requestId);
		params.setSuccess("true");
		
		ApplyBasicInfoPojo basicInfo = daoService.findOneByHQL(hql, params);
		
		/*开始解密*/
		/*String cellPhone = basicInfo.getCell_phone();
		if (!StringUtil.isEmpty(cellPhone)) {
			basicInfo.setCell_phone(CryptUtil.decrypt(cellPhone));
		}
		String cellPhone2 = basicInfo.getCell_phone2();
		if (!StringUtil.isEmpty(cellPhone2)) {
			basicInfo.setCell_phone2(CryptUtil.decrypt(cellPhone2));
		}
		String homeTel = basicInfo.getHome_tel();
		if (!StringUtil.isEmpty(homeTel)) {
			basicInfo.setHome_tel(CryptUtil.decrypt(homeTel));
		}
		String workTel = basicInfo.getWork_tel();
		if (!StringUtil.isEmpty(workTel)) {
			basicInfo.setWork_tel(CryptUtil.decrypt(workTel));
		}
		String idCardNo = basicInfo.getId_card_no();
		if (!StringUtil.isEmpty(idCardNo)) {
			basicInfo.setId_card_no(CryptUtil.decrypt(idCardNo));
		}
		Set<ApplyContactPojo> contactPojoSet = basicInfo.getContactPojoSet();
		if (contactPojoSet != null && contactPojoSet.size() > 0) {
			Set<ApplyContactPojo> decrySet = new HashSet<ApplyContactPojo>();
			for (ApplyContactPojo contact : contactPojoSet) {
				String contactTel = contact.getContact_tel();
				contact.setContact_tel(CryptUtil.decrypt(contactTel));
				decrySet.add(contact);
			}
			
			basicInfo.setContactPojoSet(decrySet);
			
		}*/
	
		
		return basicInfo;
	}

	public void updateApplyInfo(String requestId,String remark) {
		
		ApplyBasicInfoPojo basicInfo = getValidTokenByRequestId(requestId);
		if (basicInfo != null) {
			basicInfo.setRemark(remark);
			basicInfo.setUpd_time(new Date());
			daoService.update(basicInfo);
		}
		
	}

	/* 
	 * 根据name,id_card_no,cell_phone,remark查询并按crt_time倒叙排列
	 */
	@Override
	public List<ApplyBasicInfoPojo> queryAndOrderByCrt(
			ApplyBasicInfoPojo basicInfo) {
		
		String hql = "FROM ApplyBasicInfoPojo b where b.name =:name and b.id_card_no =:id_card_no and "
				+ "	b.cell_phone =:cell_phone and b.remark =:remark order by b.upd_time desc";
		
		List<ApplyBasicInfoPojo> basicList = daoService.findByHQL(hql, basicInfo);
		
		return basicList;
	}

	public List<ApplyBasicInfoPojo> queryCollectbyPeriod(String name, String cardNo,
			String mobileNo, int periodInt) throws Exception {
		
		Date oldDate = DateUtil.addDays(0-periodInt);
		
		String hql = "FROM ApplyBasicInfoPojo b where b.name =:name and b.id_card_no =:id_card_no and "
				+ "	b.cell_phone =:cell_phone and b.remark =:remark  and b.crt_time >= :oldDate order by b.crt_time desc";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("id_card_no", cardNo);
		params.put("cell_phone", mobileNo);
		params.put("remark", JXLConst.MEBUSI_SUBMIT_All_FINISH);
		params.put("oldDate", oldDate);
		
		List<ApplyBasicInfoPojo> basicList = daoService.findByHQL(hql, params);
		
		return basicList;
		
	}

	public List<ApplyBasicInfoPojo> queryNewMobileCollectInPeriod(String name,
			String cardNo, String mobileNo, int periodInt) throws Exception {
//		Date oldDate = DateUtil.addDays(0-periodInt);
		
		StringBuffer hqlBf = new StringBuffer();
		hqlBf.append("SELECT b FROM ApplyBasicInfoPojo b ,ApplyAccountPojo a")
			.append(" WHERE")
			.append(" b.requestId = a.requestId")
			.append(" AND")
			.append(" b.name =:name ").append(" AND ")
			.append(" b.id_card_no =:idCardNo ").append(" AND ")
			.append(" b.cell_phone =:cellPhone ").append(" AND ")
			.append(" a.account =:cellPhone ").append(" AND ")
			.append(" a.process_code =:processCode ")
//			.append(" AND ")
//			.append(" b.crt_time >=:oldDate ")
			.append(" ORDER BY ")
			.append(" b.upd_time ").append(" DESC ");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("idCardNo", cardNo);
		params.put("cellPhone", mobileNo);
//		params.put("remark", JXLConst.MEBUSI_SUBMIT_All_FINISH);
//		params.put("oldDate", oldDate);
		params.put("processCode", JXLConst.SUCCESS_CODE);
		
		List<ApplyBasicInfoPojo> basicList = daoService.findByHQL(hqlBf.toString(),params);
		
		return basicList;
	}
	
	
	
	
}
