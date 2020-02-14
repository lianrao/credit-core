package com.wanda.credit.ds.client.anxin.bean;

public class CommonResult {
	  private String result;
	  private String result_detail;
	  private String similarity;
	  private boolean fee;
	  private boolean authResult;
	  private String auth_result;
	  private String result_badinfo;

	  public String getResult_badinfo()
	  {
	    return this.result_badinfo;
	  }

	  public void setResult_badinfo(String result_badinfo) {
	    this.result_badinfo = result_badinfo;
	  }

	  public boolean isAuthResult() {
	    return this.authResult;
	  }

	  public void setAuthResult(boolean authResult) {
	    this.authResult = authResult;
	  }

	  public boolean isFee() {
	    return this.fee;
	  }

	  public void setFee(boolean fee) {
	    this.fee = fee;
	  }

	  public String getSimilarity() {
	    return this.similarity;
	  }

	  public void setSimilarity(String similarity) {
	    this.similarity = similarity;
	  }

	  public void setResult(String result) {
	    this.result = result;
	  }

	  public void setResult_detail(String result_detail) {
	    this.result_detail = result_detail;
	  }

	  public String getResult() {
	    return this.result;
	  }

	  public String getResult_detail() {
	    return this.result_detail;
	  }

	public String getAuth_result() {
		return auth_result;
	}

	public void setAuth_result(String auth_result) {
		this.auth_result = auth_result;
	}
}
