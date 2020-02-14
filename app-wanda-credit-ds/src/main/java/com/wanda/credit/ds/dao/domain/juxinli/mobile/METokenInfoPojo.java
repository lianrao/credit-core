package com.wanda.credit.ds.dao.domain.juxinli.mobile;

import com.wanda.credit.base.domain.BaseDomain;

public class METokenInfoPojo extends BaseDomain{
	
	private static final long serialVersionUID = 1914506200352157716L;
	private String id;//主键ID         
	private String tradeId;//交易序列号
	private String name;//用户姓名			
	private String cardNo;//用户身份证              
	private String mobileNo;//用户手机号码            
	private String homeAddr;//家庭住址            
	private String homeTel;//家庭电话             
	private String workAddr;//工作地址          
	private String workTel;//工作电话             
	private String mobileNo2;//备用电话           
	private String skipMobile;//是否跳过手机运营商资源获取          
	private String dsSize;//此次申请的数据源个数			
	private String token;//聚信立返回的令牌                
	private String requestId;//此次请求对应的请求ID           
	private String nextDSId;//下一个采集的数据源编号           
	private String nextDSName;//下一个采集的数据源名称         
	private String nextDSWebsite;//下一个采集的数据源英文名称      
	private String nextDSCategory;//下一个采集的数据源分类英文名称     
	private String nextDSCategoryName;//下一个采集的数据源分类中文名称
	private String nextDSStatus;//下一个数据源的状态      
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getHomeAddr() {
		return homeAddr;
	}
	public void setHomeAddr(String homeAddr) {
		this.homeAddr = homeAddr;
	}
	public String getHomeTel() {
		return homeTel;
	}
	public void setHomeTel(String homeTel) {
		this.homeTel = homeTel;
	}
	public String getWorkAddr() {
		return workAddr;
	}
	public void setWorkAddr(String workAddr) {
		this.workAddr = workAddr;
	}
	public String getWorkTel() {
		return workTel;
	}
	public void setWorkTel(String workTel) {
		this.workTel = workTel;
	}
	public String getMobileNo2() {
		return mobileNo2;
	}
	public void setMobileNo2(String mobileNo2) {
		this.mobileNo2 = mobileNo2;
	}
	public String getSkipMobile() {
		return skipMobile;
	}
	public void setSkipMobile(String skipMobile) {
		this.skipMobile = skipMobile;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getNextDSId() {
		return nextDSId;
	}
	public void setNextDSId(String nextDSId) {
		this.nextDSId = nextDSId;
	}
	public String getNextDSName() {
		return nextDSName;
	}
	public void setNextDSName(String nextDSName) {
		this.nextDSName = nextDSName;
	}
	public String getNextDSWebsite() {
		return nextDSWebsite;
	}
	public void setNextDSWebsite(String nextDSWebsite) {
		this.nextDSWebsite = nextDSWebsite;
	}
	public String getNextDSCategory() {
		return nextDSCategory;
	}
	public void setNextDSCategory(String nextDSCategory) {
		this.nextDSCategory = nextDSCategory;
	}
	public String getNextDSCategoryName() {
		return nextDSCategoryName;
	}
	public void setNextDSCategoryName(String nextDSCategoryName) {
		this.nextDSCategoryName = nextDSCategoryName;
	}
	public String getNextDSStatus() {
		return nextDSStatus;
	}
	public void setNextDSStatus(String nextDSStatus) {
		this.nextDSStatus = nextDSStatus;
	}
	public String getDsSize() {
		return dsSize;
	}
	public void setDsSize(String dsSize) {
		this.dsSize = dsSize;
	}
	
	


}
