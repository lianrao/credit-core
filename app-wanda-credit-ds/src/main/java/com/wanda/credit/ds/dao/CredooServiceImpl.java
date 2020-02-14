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
import com.wanda.credit.ds.dao.domain.qianhai.CredooVO;
import com.wanda.credit.ds.dao.domain.qianhai.QHResultVO;
import com.wanda.credit.ds.dao.iface.ICredooService;

@SuppressWarnings("unchecked")
@Service
@Transactional
public class CredooServiceImpl  implements ICredooService {
	@Autowired
	private DaoService daoService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	public List<CredooVO> addCredoo(JSONObject credooJsn, String resultId) throws Exception {
		if (credooJsn == null || !credooJsn.containsKey("batchNo")
				|| !credooJsn.containsKey("records"))
			return null;
		
		String batchNo = credooJsn.getString("batchNo");
		List<Map<String, Object>> datas = (List<Map<String, Object>>) credooJsn
				.get("records");
		if(datas == null || datas.size() == 0) return null;
		
		List<CredooVO> retrnls = new ArrayList<CredooVO>();
		CredooVO vo;
		for (Map<String, Object> data : datas) {
		    vo = new CredooVO();
			vo.setBatchNo(batchNo);
			vo.setResultId(resultId);
			vo.setSeqNo((String)data.get("seqNo"));
			vo.setName((String)data.get("name"));
			vo.setIdNo(synchExecutorService.encrypt((String)data.get("idNo")));
			vo.setIdType((String)data.get("idType"));
			vo.setMobileNo(synchExecutorService.encrypt((String)data.get("mobileNo")));
			vo.setCardId(synchExecutorService.encrypt((String)data.get("cardId")));
			vo.setSourceId((String)data.get("sourceId"));
			vo.setDataBuild_time((String)data.get("dataBuildTime"));
			vo.setTrend_score((String)data.get("trendScore"));
			vo.setVirAsset_score((String)data.get("virAssetScore"));
			vo.setAction_score((String)data.get("actionScore"));
			vo.setBseinfo_score((String)data.get("bseInfoScore"));
			vo.setCredoo_score((String)data.get("credooScore"));
			vo.setPayAbility_score((String)data.get("payAbilityScore"));
			vo.setPerform_score((String)data.get("performScore"));
			vo.setFinRequire_score((String)data.get("finRequireScore"));
			vo.setErCode((String)data.get("erCode"));
			vo.setErMsg((String)data.get("erMsg"));
			this.daoService.create(vo);
			retrnls.add(vo);
		}
		return retrnls;
	}

	public QHResultVO addQHResult(JSONObject resultJsn) {
		if (resultJsn == null)
			return null;
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
		vo.setProdType("CREDOO");
		this.daoService.create(vo);
		return vo;
	}
}

