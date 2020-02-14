package com.wanda.credit.ds.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_check;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_status;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYPhoCheckService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYPhoStatusService;
@Service
@Transactional
public class PYPhoCheckService extends BaseServiceImpl<Py_pho_check> implements IPYPhoCheckService{
	@Autowired
	private IPYPhoStatusService pYPhoStatusService;
	@Override
	public boolean inCached(String name,String cardNo,String phone) {
		Py_pho_check phoneCheck = new Py_pho_check();
		phoneCheck.setName(name);
		phoneCheck.setDocumentNo(cardNo);
		phoneCheck.setPhone(phone);
		return this.query(phoneCheck).size()>0;
	}
	@Override
	public Py_pho_check queryPhoneCheck(String name,String cardNo,String phone) {
		Py_pho_check phoneCheck = new Py_pho_check();
		phoneCheck.setName(name);
		phoneCheck.setDocumentNo(cardNo);
		phoneCheck.setPhone(phone);
		List<Py_pho_check> list = this.query(phoneCheck);
		return list.size()>0 ? list.get(0) : null;
	}
	@Override
	public Py_pho_status queryPhoneStatus(String name, String cardNo,String phone) {
		Py_pho_status PyPhoneStatus = new Py_pho_status();
		Py_pho_check phoe = this.queryPhoneCheck(name,cardNo,phone);
		PyPhoneStatus.setTrade_id(phoe.getTrade_id());
		List<Py_pho_status> list = pYPhoStatusService.query(PyPhoneStatus);
		return list.size()>0 ? list.get(0) : null;
	}
}
