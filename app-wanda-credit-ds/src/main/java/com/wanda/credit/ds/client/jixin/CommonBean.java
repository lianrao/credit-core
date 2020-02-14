package com.wanda.credit.ds.client.jixin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CommonBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5016944159848569429L;

	// 交易类型
	private String transcode;

	// 商户号
	private String merchno;

	// 商户订单号
	private String dsorderid;

	// 注册号或统一信用代码
	private String regno;

	// 公司名称
	private String compayname;

	// 企业法人
	private String frname;

	// 备注
	private String remark;

	// 签名
	private String sign;

	// 版本号
	private String version;

	// 流水号
	private String ordersn;

	// 返回码
	private String returncode;

	// 返回信息
	private String errtext;

	// 订单号
	private String orderid;

	// 证件号码
	private String idcard;
	// 证件类型
	private String idtype;
	// 银行卡号
	private String bankcard;
	// 手机号
	private String mobile;
	// 用户名
	private String username;

	// 人脸识别图1
	private String face1;

	// 人脸识别图2
	private String face2;

	// 相识度
	private String score;

	// 建议值
	private String threshold;
	// 建议最高
	private String thhigh;
	// 建议最低
	private String thlow;
	// 业务发生地
	private String businessplace;
	// 业务类型
	private String businesstype;

	// 头像 base64
	private String headimg;

	private String bankid;

	private String bankname;

	private String bankcode;

	private String bankcodename;

	private String image;

	private String tradeNo;
	private String bankAccountType;	
	private String imageData;

	/**
	 * 校验码
	 */
	private String cvv2;

	/**
	 * 有效期:MMyyyy
	 */
	private String validDate;

	public String getTranscode() {
		return transcode;
	}

	public void setTranscode(String transcode) {
		this.transcode = transcode;
	}

	public String getMerchno() {
		return merchno;
	}

	public void setMerchno(String merchno) {
		this.merchno = merchno;
	}

	public String getDsorderid() {
		return dsorderid;
	}

	public void setDsorderid(String dsorderid) {
		this.dsorderid = dsorderid;
	}

	public String getRegno() {
		return regno;
	}

	public void setRegno(String regno) {
		this.regno = regno;
	}

	public String getCompayname() {
		return compayname;
	}

	public void setCompayname(String compayname) {
		this.compayname = compayname;
	}

	public String getFrname() {
		return frname;
	}

	public void setFrname(String frname) {
		this.frname = frname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getOrdersn() {
		return ordersn;
	}

	public void setOrdersn(String ordersn) {
		this.ordersn = ordersn;
	}

	public String getReturncode() {
		return returncode;
	}

	public void setReturncode(String returncode) {
		this.returncode = returncode;
	}

	public String getErrtext() {
		return errtext;
	}

	public void setErrtext(String errtext) {
		this.errtext = errtext;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getIdtype() {
		return idtype;
	}

	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}

	public String getBankcard() {
		return bankcard;
	}

	public void setBankcard(String bankcard) {
		this.bankcard = bankcard;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFace1() {
		return face1;
	}

	public void setFace1(String face1) {
		this.face1 = face1;
	}

	public String getFace2() {
		return face2;
	}

	public void setFace2(String face2) {
		this.face2 = face2;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getThhigh() {
		return thhigh;
	}

	public void setThhigh(String thhigh) {
		this.thhigh = thhigh;
	}

	public String getThlow() {
		return thlow;
	}

	public void setThlow(String thlow) {
		this.thlow = thlow;
	}

	public String getBusinessplace() {
		return businessplace;
	}

	public void setBusinessplace(String businessplace) {
		this.businessplace = businessplace;
	}

	public String getBusinesstype() {
		return businesstype;
	}

	public void setBusinesstype(String businesstype) {
		this.businesstype = businesstype;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public String getBankid() {
		return bankid;
	}

	public void setBankid(String bankid) {
		this.bankid = bankid;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	public String getBankcodename() {
		return bankcodename;
	}

	public void setBankcodename(String bankcodename) {
		this.bankcodename = bankcodename;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getBankAccountType() {
		return bankAccountType;
	}

	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}
	public String getCvv2() {
		return cvv2;
	}
	
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}
	public String getValidDate() {
		return validDate;
	}
	
	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

	public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

}
