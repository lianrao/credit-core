package com.wanda.credit.ds.client.xyan.vo;

public class RetDataVo {
/*{"success":true,"data":{"code":"0","desc":"亲，认证成功","trans_id":"20161130142450719RRLZ","trade_no":"201611301419070000014636",
	"org_code":null,"org_desc":null,"fee":"Y"},"errorCode":null,"errorMsg":null}*/
	private String code;
	private String desc;
	private String trans_id;
	private String trade_no;
	private String org_code;
	private String org_desc;
	private String fee;
	
	private String bank_id;//银行编码
	private String bank_description;//银行简称
	public String getBank_id() {
		return bank_id;
	}
	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}
	public String getBank_description() {
		return bank_description;
	}
	public void setBank_description(String bank_description) {
		this.bank_description = bank_description;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getTrans_id() {
		return trans_id;
	}
	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	public String getOrg_code() {
		return org_code;
	}
	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}
	public String getOrg_desc() {
		return org_desc;
	}
	public void setOrg_desc(String org_desc) {
		this.org_desc = org_desc;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	
	
}
