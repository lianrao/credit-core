package com.wanda.credit.ds.dao.iface;

import java.util.List;
import java.util.Map;

import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpBranch;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpManagement;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpNational;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpPenaltyInfos;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpReq;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpRsp;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpShareholder;

/**
 * 捷为企业工商信息 保存
 * */
public interface IJieWeiQueryCorpInfoService {
	/**
	 *保存请求信息*/
	public void addCorpReq(JW_CorpReq corpReq);
	
	/**
	 *保存响应信息*/
	public void addCorpRsp(JW_CorpRsp corpRsp);
	
	/**
	 *保存企业分支机构信息*/
	public void addCorpBranch(JW_CorpBranch corpBranch);
	
	/**
	 * 保存 企业主要人员信息*/
	public void addCorpManagement(JW_CorpManagement corpManagement);
	
	/**
	 *保存企业基本信息*/
	public void addCorpNational(JW_CorpNational corpNational);
	
	/**
	 *保存企业行政处罚信息*/
	public void addCorpPenaltyInfos(JW_CorpPenaltyInfos corpPenaltyInfos);
	
	/**
	 * 保存企业股东信息*/
	public void addCorpShareholder(JW_CorpShareholder shareholder);


	/**
	 *根据主键
	 *获取响应信息*/
	public Map<String,Object> getCorpRsp(String id);
	
	/**
	 *根据外键
	 *获取企业分支机构信息*/
	public List<Map<String,Object>> getCorpBranch(String refid);
	
	/**
	 * 根据外键
	 * 获取 企业主要人员信息*/
	public List<Map<String,Object>> getCorpManagement(String refid);
	
	/**
	 *根据外键
	 *获取企业基本信息*/
	public List<Map<String,Object>> getCorpNational(String refid);
	
	/**
	 * 根据外键
	 *获取企业行政处罚信息*/
	public List<Map<String,Object>> getCorpPenaltyInfos(String refid);
	
	/**
	 * 根据外键
	 * 获取企业股东信息*/
	public List<Map<String,Object>> getCorpShareholder(String refid);

	/**
	 * 查询最近30天缓存的企业信息
	 * @param corpName  企业名
	 * @param registerNo 工商注册号或者社会统一代码
	 * @param province  省份代码
	 * @param queryType 查询类型
	 * @param cachedDays 检查的缓存天数
	 * @return 记录的的主键
	 * */
	public String getCachedKey(String corpName,String registerNo,
			String province,Integer queryType, int cachedDays);
}
