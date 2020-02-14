package com.wanda.credit.ds.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Nciic_Mult_Check_Result;
import com.wanda.credit.ds.dao.iface.INciicMultCheckService;
@Service
@Transactional
public class NciicMultCheckServiceImpl  extends BaseServiceImpl<Nciic_Mult_Check_Result> implements INciicMultCheckService{
	private final  Logger logger = LoggerFactory.getLogger(NciicMultCheckServiceImpl.class);
	private  String STATUS_CHECK_EQUAL = "00";
	private  String STATUS_CHECK_NOEQUAL = "01";
	@Override
	public Nciic_Mult_Check_Result inCached(String name, String cardNo) {
		Nciic_Mult_Check_Result nciicCheck = new Nciic_Mult_Check_Result();
		nciicCheck.setName(name);
		nciicCheck.setCardno(cardNo);
		List<Nciic_Mult_Check_Result> nciic = this.query(nciicCheck);
		for(Nciic_Mult_Check_Result cr : nciic){
			Nciic_Mult_Check_Result ncr = cr;
			if(ncr.getName().equals(name) && ncr.getCardno().equals(cardNo) && (ncr.getStatus().equals(STATUS_CHECK_EQUAL) || ncr.getStatus().equals(STATUS_CHECK_NOEQUAL))){
				return cr;
			}
		}
	    return null;
	}

	@Override
	public void batchSave(List<Nciic_Mult_Check_Result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
