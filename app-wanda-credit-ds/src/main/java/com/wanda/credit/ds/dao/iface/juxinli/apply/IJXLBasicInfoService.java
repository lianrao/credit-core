package com.wanda.credit.ds.dao.iface.juxinli.apply;


import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;

public interface IJXLBasicInfoService extends IBaseService<ApplyBasicInfoPojo> {

	public ApplyBasicInfoPojo getValidTokenByRequestId(String requestId);

	/**
	 * 根据RequestId更新BasicInfo
	 * @param requestId
	 * @param basicInfoPojo
	 */
	public void updateApplyInfo(String requestId,String remark);
	
	
	/**
	 * 根据name,id_card_no,cell_phone,remark查询并按crt_time倒叙排列
	 * @param basicInfo
	 * @return
	 */
	public List<ApplyBasicInfoPojo> queryAndOrderByCrt(ApplyBasicInfoPojo basicInfo);

	/**
	 * 根据身份证号，姓名，手机号查询某个周期内是否有成功交易
	 * @param periodInt 
	 * @param mobileNo 
	 * @param cardNo 
	 * @param name 
	 * 
	 */
	public List<ApplyBasicInfoPojo> queryCollectbyPeriod(String name, String cardNo, String mobileNo, int periodInt) throws Exception;
	
	/**
	 * 根据身份证号码，姓名，手机号查询某个周期内最近一次提交手机采集请求的原始数据
	 * @param name
	 * @param cardNo
	 * @param mobileNo
	 * @param periodInt
	 * @return
	 */
	public List<ApplyBasicInfoPojo>  queryNewMobileCollectInPeriod(String name, String cardNo, String mobileNo, int periodInt) throws Exception;
}
