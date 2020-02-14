package com.wanda.credit.ds.client.juxinli.service;

import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyAccountPojo;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyNextDataSourcePojo;

public interface IJXLEBusiMobileSubmitService {

	/**
	 * 保存提交采集请求的账户数据并更新当前采集流程状态
	 * 
	 * @param accountPojo
	 * @param requestId
	 * @param remark
	 * @throws Exception
	 */
	public void saveSubmitAccount(ApplyAccountPojo accountPojo, String requestId,
			String remark)  throws Exception;
	
	/**
	 * 判断同一个requestId对应的数据源[weibsite]是否有成功提交过
	 * @param requestId
	 * @param website
	 * @return 重复提交返回true 否则返回false
	 */
	public boolean isRepeatSubmit(String requestId,String website);

	/**
	 * 更新T_DS_JXL_ORIG_RESP_RESULT表中的success标识
	 * 初始为FALSE 跳过为skip 提交采集成功为true
	 * @param requestId
	 * @param website
	 */
	public void updateNextDSByReqIdAndWebSite(String requestId, String website ,String isSuc);
	/**
	 * 获取下一个需要提交采集请求的数据源
	 * @param requestId
	 */
	public ApplyNextDataSourcePojo queryNextDs(String requestId);
	

}
