package com.wanda.credit.ds.dao.iface;

import java.util.List;

import net.sf.json.JSONObject;

import com.wanda.credit.ds.dao.domain.qianhai.BlackListVO;
import com.wanda.credit.ds.dao.domain.qianhai.QHResultVO;

public interface IBlackListService{	
	public List<BlackListVO> addOneBlackList(JSONObject blackListJsn,String resultId)throws Exception;
	public QHResultVO addQHResult(JSONObject resultJsn);
}
