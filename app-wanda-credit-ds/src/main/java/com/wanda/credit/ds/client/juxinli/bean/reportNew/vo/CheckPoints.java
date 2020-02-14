package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;
/**
 * 用户申请表检测之数据检查点
 * @author xiaobin.hou
 *
 */
public class CheckPoints {
	
	private String key_value;

	private String gender;

	private String age;

	private String province;

	private String city;

	private String region;

	private String website;

	private String reliability;

	private String reg_time;

	private String check_name;

	private String check_idcard;

	private String check_ebusiness;

	private String check_addr;

	private String relationship;

	private String contact_name;

	private String check_xiaohao;

	private String check_mobile;

	private CourtBlacklist court_blacklist;

	private FinancialBlacklist financial_blacklist;

	public String getKey_value() {
		return key_value;
	}

	public void setKey_value(String key_value) {
		this.key_value = key_value;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getReliability() {
		return reliability;
	}

	public void setReliability(String reliability) {
		this.reliability = reliability;
	}

	public String getReg_time() {
		return reg_time;
	}

	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}

	public String getCheck_name() {
		return check_name;
	}

	public void setCheck_name(String check_name) {
		this.check_name = check_name;
	}

	public String getCheck_idcard() {
		return check_idcard;
	}

	public void setCheck_idcard(String check_idcard) {
		this.check_idcard = check_idcard;
	}

	public String getCheck_ebusiness() {
		return check_ebusiness;
	}

	public void setCheck_ebusiness(String check_ebusiness) {
		this.check_ebusiness = check_ebusiness;
	}

	public String getCheck_addr() {
		return check_addr;
	}

	public void setCheck_addr(String check_addr) {
		this.check_addr = check_addr;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}

	public String getCheck_xiaohao() {
		return check_xiaohao;
	}

	public void setCheck_xiaohao(String check_xiaohao) {
		this.check_xiaohao = check_xiaohao;
	}

	public String getCheck_mobile() {
		return check_mobile;
	}

	public void setCheck_mobile(String check_mobile) {
		this.check_mobile = check_mobile;
	}

	public CourtBlacklist getCourt_blacklist() {
		return court_blacklist;
	}

	public void setCourt_blacklist(CourtBlacklist court_blacklist) {
		this.court_blacklist = court_blacklist;
	}

	public FinancialBlacklist getFinancial_blacklist() {
		return financial_blacklist;
	}

	public void setFinancial_blacklist(FinancialBlacklist financial_blacklist) {
		this.financial_blacklist = financial_blacklist;
	}

	@Override
	public String toString() {
		return "checkPoints [key_value=" + key_value + ", gender=" + gender + ", age=" + age + ", province=" + province
				+ ", city=" + city + ", region=" + region + ", website=" + website + ", reliability=" + reliability
				+ ", reg_time=" + reg_time + ", check_name=" + check_name + ", check_idcard=" + check_idcard
				+ ", check_ebusiness=" + check_ebusiness + ", check_addr=" + check_addr + ", relationship="
				+ relationship + ", contact_name=" + contact_name + ", check_xiaohao=" + check_xiaohao
				+ ", check_mobile=" + check_mobile + ", court_blacklist=" + court_blacklist + ", financial_blacklist="
				+ financial_blacklist + "]";
	}

}
