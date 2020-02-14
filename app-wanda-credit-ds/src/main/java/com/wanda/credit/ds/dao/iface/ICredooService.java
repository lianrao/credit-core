package com.wanda.credit.ds.dao.iface;

import java.util.List;

import net.sf.json.JSONObject;

import com.wanda.credit.ds.dao.domain.qianhai.CredooVO;
import com.wanda.credit.ds.dao.domain.qianhai.QHResultVO;

public interface ICredooService {
	public List<CredooVO> addCredoo(JSONObject credooJsn,String resultId)throws Exception;
	public QHResultVO addQHResult(JSONObject resultJsn);
}
