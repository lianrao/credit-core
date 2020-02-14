package com.wanda.credit.ds.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.client.qianhai.QHDataSourceUtils;
import com.wanda.credit.ds.dao.domain.qianhai.LoaneeVO;
import com.wanda.credit.ds.dao.iface.IQHLoaneeService;

@SuppressWarnings("unchecked")
@Service
@Transactional
public class QHLoaneeServiceImpl implements IQHLoaneeService {
	private final Logger logger = LoggerFactory
			.getLogger(QHLoaneeServiceImpl.class);

	@Autowired
	private DaoService daoService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@Override
	public List<LoaneeVO> addLoanee(JSONObject loaneeJsn, String trade_id
			,Map<String,Object> ctx) throws Exception {
		if (loaneeJsn == null || !loaneeJsn.containsKey("records"))
			return null;
		
		List<Map<String, Object>> datas = (List<Map<String, Object>>) loaneeJsn
				.get("records");
		
		if(datas == null || datas.size() == 0) return null;

		String batchNo = QHDataSourceUtils.getValFromJsnObj(loaneeJsn, "batchNo");
		List<LoaneeVO> retrnls = new ArrayList<LoaneeVO>();
		LoaneeVO vo;
		for (Map<String, Object> data : datas) {			
			if("E000000".equals(data.get("erCode"))){
				vo = new LoaneeVO();
				vo.setBatchNo(batchNo);
				vo.setTrade_id(trade_id);
				vo.setIdNo(synchExecutorService.encrypt((String) data.get("idNo")));
				vo.setIdType((String) data.get("idType"));
				vo.setName((String) data.get("name"));
				vo.setAmount((String) data.get("amount"));
				vo.setBnkAmount((String) data.get("bnkAmount"));
				vo.setCnssAmount((String) data.get("cnssAmount"));
				vo.setP2pAmount((String) data.get("p2pAmount"));
				vo.setQueryAmt((String) data.get("queryAmt"));
				vo.setQueryAmtM3((String) data.get("queryAmtM3"));
				vo.setQueryAmtM6((String) data.get("queryAmtM6"));
				vo.setBusiDate((String) data.get("busiDate"));
				vo.setIndustry((String) data.get("industry"));
				vo.setReasonCode((String) data.get("reasonCode"));
				vo.setSeqNo((String) data.get("seqNo"));
				vo.setErcode((String) data.get("erCode"));
				vo.setErmsg((String) data.get("erMsg"));
				retrnls.add(vo);
			 }else if("E000996".equals(data.get("erCode"))){
				//unfound
				ctx.put(Conts.KEY_RET_TAG, Conts.TAG_UNFOUND);
				logger.warn("{} 未查询到qh常贷客信息",trade_id);
				return null;
			}else{
				//other error
				ctx.put(Conts.KEY_RET_TAG, Conts.TAG_SYS_ERROR);
				ctx.put("erMsg", data.get("erMsg") );
				logger.error("{} qh常带客信息查询失败 {}",trade_id,data.get("erMsg"));
				return null;
			}		
	     }
		if(CollectionUtils.isNotEmpty(retrnls)){
			  this.daoService.create(retrnls);
			 }
		return retrnls;
	}

}