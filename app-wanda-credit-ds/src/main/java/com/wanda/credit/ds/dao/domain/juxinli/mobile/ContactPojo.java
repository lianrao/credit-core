package com.wanda.credit.ds.dao.domain.juxinli.mobile;

import com.wanda.credit.base.domain.BaseDomain;
/**
 * POJO类 常用联系人
 * @author xiaobin.hou
 *
 */
public class ContactPojo extends BaseDomain {
	private static final long serialVersionUID = -7897710712670938210L;
	
	private String id;  	
	private String tradeId;	
	private String requestId;
	private String contactTel;
	private String contactName;
	private String relationType;
	
	
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
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getContactTel() {
		return contactTel;
	}
	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String toString() {
		return "ContactPojo [id=" + id + ", tradeId=" + tradeId
				+ ", requestId=" + requestId + ", contactTel=" + contactTel
				+ ", contactName=" + contactName + ", relationType="
				+ relationType + "]";
	} 
	
	
	

}
