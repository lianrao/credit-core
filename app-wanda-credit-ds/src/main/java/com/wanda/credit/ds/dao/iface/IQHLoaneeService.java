package com.wanda.credit.ds.dao.iface;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.wanda.credit.ds.dao.domain.qianhai.LoaneeVO;

public interface IQHLoaneeService{	
	public List<LoaneeVO> addLoanee(JSONObject loaneeJsn,String trade_id,Map<String,Object> ctx) throws Exception;
}
