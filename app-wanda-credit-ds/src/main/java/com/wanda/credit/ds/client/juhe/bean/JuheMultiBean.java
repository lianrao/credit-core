package com.wanda.credit.ds.client.juhe.bean;

import java.util.List;

public class JuheMultiBean {
	private List<RegisteredInfo> registered_logs;
	private List<ApplyInfo> apply_logs;
	private List<LoanInfo> loan_logs;
	private List<RejectedInfo> rejected_logs;
	private List<OverdueInfo> overdue_logs;
	
	public List<RegisteredInfo> getRegistered_logs() {
		return registered_logs;
	}
	public void setRegistered_logs(List<RegisteredInfo> registered_logs) {
		this.registered_logs = registered_logs;
	}
	public List<ApplyInfo> getApply_logs() {
		return apply_logs;
	}
	public void setApply_logs(List<ApplyInfo> apply_logs) {
		this.apply_logs = apply_logs;
	}
	public List<LoanInfo> getLoan_logs() {
		return loan_logs;
	}
	public void setLoan_logs(List<LoanInfo> loan_logs) {
		this.loan_logs = loan_logs;
	}
	public List<RejectedInfo> getRejected_logs() {
		return rejected_logs;
	}
	public void setRejected_logs(List<RejectedInfo> rejected_logs) {
		this.rejected_logs = rejected_logs;
	}
	public List<OverdueInfo> getOverdue_logs() {
		return overdue_logs;
	}
	public void setOverdue_logs(List<OverdueInfo> overdue_logs) {
		this.overdue_logs = overdue_logs;
	}
	
}
