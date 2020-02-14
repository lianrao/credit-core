package com.wanda.credit.ds.dao.domain.jiewei;

import javax.persistence.MappedSuperclass;

import com.wanda.credit.base.domain.BaseDomain;

@MappedSuperclass
public class SubReportBaseInfo extends BaseDomain {
	private Integer subReportType;
	private Integer treatResult;
	private String treatErrorCode;
	private String errorMessage;

	public Integer getSubReportType() {
		return subReportType;
	}

	public void setSubReportType(Integer subReportType) {
		this.subReportType = subReportType;
	}

	public Integer getTreatResult() {
		return treatResult;
	}

	public void setTreatResult(Integer treatResult) {
		this.treatResult = treatResult;
	}

	public String getTreatErrorCode() {
		return treatErrorCode;
	}

	public void setTreatErrorCode(String treatErrorCode) {
		this.treatErrorCode = treatErrorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
