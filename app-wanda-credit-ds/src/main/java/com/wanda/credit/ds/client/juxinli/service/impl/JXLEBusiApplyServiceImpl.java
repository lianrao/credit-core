package com.wanda.credit.ds.client.juxinli.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.EBusiWebsite;
import com.wanda.credit.ds.client.juxinli.bean.mobile.Contact;
import com.wanda.credit.ds.client.juxinli.bean.mobile.GetTokenReq;
import com.wanda.credit.ds.client.juxinli.bean.mobile.UserBasicInfo;
import com.wanda.credit.ds.client.juxinli.service.IJXLEBusiApplyService;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyContactPojo;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyWebsitPojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLBasicInfoService;

@Service
@Transactional
public class JXLEBusiApplyServiceImpl implements IJXLEBusiApplyService {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLEBusiApplyServiceImpl.class);
	
	@Autowired
	private IJXLBasicInfoService jxlBasicInfoService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	public void saveApplyData(GetTokenReq tokenReq,String requestId,String token) throws Exception{
		logger.info("{} 聚信立提交申请表单将请求和返回数据转化成Domain对象 BEGIN");
		ApplyBasicInfoPojo basicInfoPojo = parseToDomain(tokenReq,requestId,token);
		logger.info("{} 聚信立提交申请表单将请求和返回数据转化成Domain对象  END,准备将数据保存到数据库");
		if(basicInfoPojo != null){
			jxlBasicInfoService.add(basicInfoPojo);
		}
		
		logger.info("{} 聚信立提交申请表单将请求和返回参数保存到数据库成功 END");
		
	}

	private ApplyBasicInfoPojo parseToDomain(GetTokenReq tokenReq,String requestId,String token) {
		
		if (tokenReq == null) {
			logger.info("{} 聚信立提交申请表单将请求和返回数据转化成Domain_传入参数对象为null");
			return null;
		}
		
		
		ApplyBasicInfoPojo basicInfoPojo = new ApplyBasicInfoPojo();
		
		UserBasicInfo basic_info = tokenReq.getBasic_info();
		basicInfoPojo.setRequestId(requestId);
		basicInfoPojo.setCrt_time(new Date());
		basicInfoPojo.setUpd_time(new Date());
		
		try{
			//对手机号码加密
			if (!StringUtil.isEmpty(basic_info.getCell_phone_num())) {
				basicInfoPojo.setCell_phone(synchExecutorService.encrypt(basic_info.getCell_phone_num()));
			}		
			if (!StringUtil.isEmpty(basic_info.getCell_phone_num2())) {
				basicInfoPojo.setCell_phone2(synchExecutorService.encrypt(basic_info.getCell_phone_num2()));
			}
			if (!StringUtil.isEmpty(basic_info.getHome_tel())) {
				basicInfoPojo.setHome_tel(synchExecutorService.encrypt(basic_info.getHome_tel()));
			}
			if (!StringUtil.isEmpty(basic_info.getId_card_num())) {
				basicInfoPojo.setId_card_no(synchExecutorService.encrypt(basic_info.getId_card_num()));
			}
			if (!StringUtil.isEmpty(basic_info.getWork_tel())) {
				basicInfoPojo.setWork_tel(synchExecutorService.encrypt(basic_info.getWork_tel()));
			}
//			basicInfoPojo.setWork_tel(basic_info.getWork_tel());
			/*basicInfoPojo.setHome_tel(basic_info.getHome_tel());
			basicInfoPojo.setId_card_no(basic_info.getId_card_num());*/
			
			basicInfoPojo.setHome_addr(basic_info.getHome_addr());
			basicInfoPojo.setName(basic_info.getName());	
			basicInfoPojo.setSkip_mobile(tokenReq.isSkip_mobile() + "");		
			basicInfoPojo.setToken(token);

			basicInfoPojo.setWork_addr(basic_info.getWork_addr());
			//设置success和remark的默认值
			basicInfoPojo.setSuccess("false");
			basicInfoPojo.setRemark("failed");
			if (requestId != null && requestId.length() >1) {
				//requestId为null或者长度长度不符合规范
				//TODO
				basicInfoPojo.setSuccess("true");
				//TODO
				basicInfoPojo.setRemark("ready");
			}
			
			Set<ApplyContactPojo> contactPojoSet = new HashSet<ApplyContactPojo>();
			List<Contact> contacts = tokenReq.getContacts();
			if (contacts != null && contacts.size() > 0) {
				for (Contact contact : contacts) {
					ApplyContactPojo contactPojo = new ApplyContactPojo();
					contactPojo.setRequestId(requestId);
					contactPojo.setCrt_time(new Date());
					contactPojo.setUpd_time(new Date());
					contactPojo.setContact_name(contact.getContact_name());
					if (!StringUtil.isEmpty(contact.getContact_tel())) {
						contactPojo.setContact_tel(synchExecutorService.encrypt(contact.getContact_tel()));
					}
//					contactPojo.setContact_tel(contact.getContact_tel());
					contactPojo.setContact_type(contact.getContact_type());
					contactPojoSet.add(contactPojo);
				}
				basicInfoPojo.setContactPojoSet(contactPojoSet);
			}
			
			Set<ApplyWebsitPojo> websitPojoSet = new HashSet<ApplyWebsitPojo>();
			List<EBusiWebsite> selected_website = tokenReq.getSelected_website();
			if (selected_website != null && selected_website.size() > 0) {
				for (EBusiWebsite eBusiWebsite : selected_website) {
					ApplyWebsitPojo websitePojo = new ApplyWebsitPojo();
					websitePojo.setRequestId(requestId);
					websitePojo.setCrt_time(new Date());
					websitePojo.setUpd_time(new Date());
					
					websitePojo.setCategory(eBusiWebsite.getCategory());
					websitePojo.setName(eBusiWebsite.getName());
					
					websitPojoSet.add(websitePojo);				
				}
				basicInfoPojo.setWebsitPojoSet(websitPojoSet);
			}
		}catch(Exception e){
			logger.error("保存数据异常{}" , e.getMessage());
		}
		
		
		
		
		return basicInfoPojo;
	}

}
