package com.wanda.credit.ds.dao;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_only_check;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYPhoOnlyCheckService;
@Service
@Transactional
public class PYPhoOnlyCheckService extends BaseServiceImpl<Py_pho_only_check> implements IPYPhoOnlyCheckService {
	@Override
	public boolean inCached(String name,String cardNo,String phone) {
		Py_pho_only_check phoneOnlyCheck = new Py_pho_only_check();
		phoneOnlyCheck.setName(name);
		phoneOnlyCheck.setDocumentNo(cardNo);
		phoneOnlyCheck.setPhone(phone);
		return this.query(phoneOnlyCheck).size()>0;
	}
	@Override
	public Py_pho_only_check queryPhoneCheck(String name,String cardNo,String phone) {
		Py_pho_only_check phoneCheck = new Py_pho_only_check();
		phoneCheck.setName(name);
		phoneCheck.setDocumentNo(cardNo);
		phoneCheck.setPhone(phone);
		List<Py_pho_only_check> list = this.query(phoneCheck);
		return list.size()>0 ? list.get(0) : null;
	}
}
