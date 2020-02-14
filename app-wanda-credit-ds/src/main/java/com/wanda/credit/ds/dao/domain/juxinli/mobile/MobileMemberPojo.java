package com.wanda.credit.ds.dao.domain.juxinli.mobile;

import java.util.List;

public class MobileMemberPojo {
	
	
	private String status;
	
	private String update_time;
	
	private List<MobileTransactionPojo> transactions;
	
	private int error_code;
	
	private String error_msg;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public List<MobileTransactionPojo> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<MobileTransactionPojo> transactions) {
		this.transactions = transactions;
	}

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	
	

}
