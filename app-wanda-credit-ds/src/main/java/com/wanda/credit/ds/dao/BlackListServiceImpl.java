package com.wanda.credit.ds.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.client.qianhai.QHDataSourceUtils;
import com.wanda.credit.ds.dao.domain.qianhai.BlackListVO;
import com.wanda.credit.ds.dao.domain.qianhai.QHResultVO;
import com.wanda.credit.ds.dao.iface.IBlackListService;

@SuppressWarnings("unchecked")
@Service
@Transactional
public class BlackListServiceImpl implements IBlackListService {

	@Autowired
	private DaoService daoService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	/*
	 * @Override public BlackListVO inCached(String name,String idType, String
	 * idNo) { BlackListVO vo = new BlackListVO(); vo.setName(name);
	 * vo.setIdType(idType); vo.setIdNo(idNo); List<BlackListVO> result =
	 * this.query(vo); if(result != null && result.size()>0){ return
	 * result.get(0); } return null; }
	 */

	/**
	 * @throws Exception 
	 * @blacklistJsn 结构: {batchNo:'001',record:[{idNo:'zhsan',idType:'330...'}]}
	 * */
	public List<BlackListVO> addOneBlackList(JSONObject blackListJsn,
			String resultId) throws Exception {
		if (blackListJsn == null || !blackListJsn.containsKey("batchNo")
				|| !blackListJsn.containsKey("records"))
			return null;
		
		String batchNo = blackListJsn.getString("batchNo");
		List<Map<String, Object>> datas = (List<Map<String, Object>>) blackListJsn
				.get("records");
		
		if(datas == null || datas.size() == 0) return null;
		
		List<BlackListVO> retrnls = new ArrayList<BlackListVO>();
		BlackListVO vo;
		for (Map<String, Object> data : datas) {
			vo = new BlackListVO();
			vo.setBatchNo(batchNo);
			vo.setResultId(resultId);
			vo.setIdNo(synchExecutorService.encrypt((String) data.get("idNo")));
			vo.setIdType((String) data.get("idType"));
			vo.setName((String) data.get("name"));
			vo.setSeqNo((String) data.get("seqNo"));
			vo.setState((String) data.get("state"));
			vo.setDataStatus((String) data.get("dataStatus"));
			vo.setDataBuild_time((String) data.get("dataBuildTime"));
			vo.setGradeQuery((String) data.get("gradeQuery"));
			vo.setMoneyBound((String) data.get("moneyBound"));
			vo.setSourceId((String) data.get("sourceId"));
			vo.setReserved_filed1((String) data.get("reservedFiled1"));
			vo.setReserved_filed2((String) data.get("reservedFiled2"));
			vo.setReserved_filed3((String) data.get("reservedFiled3"));
			vo.setReserved_filed4((String) data.get("reservedFiled4"));
			vo.setReserved_filed5((String) data.get("reservedFiled5"));
			vo.setErcode((String) data.get("erCode"));
			vo.setErmsg((String) data.get("erMsg"));
			this.daoService.create(vo);
			retrnls.add(vo);
		}

		return retrnls;
	}

	public QHResultVO addQHResult(JSONObject resultJsn) {
		if(resultJsn == null) return null;
		
		QHResultVO vo = new QHResultVO();
		vo.setAuthCode(QHDataSourceUtils
				.getValFromJsnObj(resultJsn, "authCode"));
		vo.setAuthDate(QHDataSourceUtils
				.getValFromJsnObj(resultJsn, "authDate"));
		vo.setChnlId(QHDataSourceUtils.getValFromJsnObj(resultJsn, "chnlId"));
		vo.setErCode(QHDataSourceUtils.getValFromJsnObj(resultJsn, "rtCode"));
		vo.setErMsg(QHDataSourceUtils.getValFromJsnObj(resultJsn, "rtMsg"));
		vo.setOrgCode(QHDataSourceUtils.getValFromJsnObj(resultJsn, "orgCode"));
		vo.setTrade_id(QHDataSourceUtils
				.getValFromJsnObj(resultJsn, "trade_id"));
		vo.setTransDate(QHDataSourceUtils.getValFromJsnObj(resultJsn,
				"transDate"));
		vo.setTransNo(QHDataSourceUtils.getValFromJsnObj(resultJsn, "transNo"));
		vo.setProdType("BLACKLIST");
		this.daoService.create(vo);
		return vo;
	}
}
