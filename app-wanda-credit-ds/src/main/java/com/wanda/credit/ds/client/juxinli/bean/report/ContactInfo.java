package com.wanda.credit.ds.client.juxinli.bean.report;

/**
 * 联系详情
 * @author xiaobin.hou
 *
 */
public class ContactInfo {
	
	/**
	 * 中午联系次数
	 */
	private String contact_noon;
	
	/**
	 * 号码归属地
	 */
	private String phone_num_loc;
	
	/**
	 * 最近三月联系次数
	 */
	private String contact_3m;
	
	/**
	 * 最近一月联系次数
	 */
	private String contact_1m;
	
	/**
	 * 最近一周联系次数
	 */
	private String contact_1w;
	
	/**
	 * 关系推测（未实现）
	 */
	private String p_relation;
	
	/**
	 * 号码
	 */
	private String phone_num;
	
	/**
	 * 号码被标注的名称
	 */
	private String contact_name;
	
	/**
	 * 呼入次数
	 */
	private String call_in_cnt;
	
	/**
	 * 呼出次数
	 */
	private String call_out_cnt;
	
	/**
	 * 呼出时间(秒)
	 */
	private String call_out_len;
	
	/**
	 * 节假日联系次数
	 */
	private String contact_holiday;
	
	/**
	 * 被标注的名称的类型
	 */
	private String needs_type;
	
	/**
	 * 周中联系次数
	 */
	private String contact_weekday;
	
	/**
	 * 下午联系次数
	 */
	private String contact_afternoon;
	
	/**
	 * 总通话时长(秒)
	 */
	private String call_len;
	
	/**
	 * 凌晨联系次数
	 */
	private String contact_early_morning;
	
	/**
	 * 晚上联系次数
	 */
	private String contact_night;
	
	/**
	 * 废弃，但是由于结构已经定下，无法修改
	 */
	private String contact_3m_plus;
	
	/**
	 * 呼出次数
	 */
	private String call_cnt;
	
	/**
	 * 呼入时间(秒)
	 */
	private String call_in_len;
	
	/**
	 * 是否全天联系过
	 */
	private String contact_all_day;
	
	/**
	 * 上午联系次数
	 */
	private String contact_morning;
	
	/**
	 * 周末联系次数
	 */
	private String contact_weekend;
	

	

	public String getContact_noon() {
		return contact_noon;
	}

	public void setContact_noon(String contact_noon) {
		this.contact_noon = contact_noon;
	}

	public String getPhone_num_loc() {
		return phone_num_loc;
	}

	public void setPhone_num_loc(String phone_num_loc) {
		this.phone_num_loc = phone_num_loc;
	}

	public String getContact_3m() {
		return contact_3m;
	}

	public void setContact_3m(String contact_3m) {
		this.contact_3m = contact_3m;
	}

	public String getContact_1m() {
		return contact_1m;
	}

	public void setContact_1m(String contact_1m) {
		this.contact_1m = contact_1m;
	}

	public String getContact_1w() {
		return contact_1w;
	}

	public void setContact_1w(String contact_1w) {
		this.contact_1w = contact_1w;
	}

	public String getP_relation() {
		return p_relation;
	}

	public void setP_relation(String p_relation) {
		this.p_relation = p_relation;
	}

	public String getPhone_num() {
		return phone_num;
	}

	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}

	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}

	public String getCall_in_cnt() {
		return call_in_cnt;
	}

	public void setCall_in_cnt(String call_in_cnt) {
		this.call_in_cnt = call_in_cnt;
	}

	public String getCall_out_cnt() {
		return call_out_cnt;
	}

	public void setCall_out_cnt(String call_out_cnt) {
		this.call_out_cnt = call_out_cnt;
	}

	public String getCall_out_len() {
		return call_out_len;
	}

	public void setCall_out_len(String call_out_len) {
		this.call_out_len = call_out_len;
	}

	public String getContact_holiday() {
		return contact_holiday;
	}

	public void setContact_holiday(String contact_holiday) {
		this.contact_holiday = contact_holiday;
	}

	public String getNeeds_type() {
		return needs_type;
	}

	public void setNeeds_type(String needs_type) {
		this.needs_type = needs_type;
	}

	public String getContact_weekday() {
		return contact_weekday;
	}

	public void setContact_weekday(String contact_weekday) {
		this.contact_weekday = contact_weekday;
	}

	public String getContact_afternoon() {
		return contact_afternoon;
	}

	public void setContact_afternoon(String contact_afternoon) {
		this.contact_afternoon = contact_afternoon;
	}

	public String getCall_len() {
		return call_len;
	}

	public void setCall_len(String call_len) {
		this.call_len = call_len;
	}

	public String getContact_early_morning() {
		return contact_early_morning;
	}

	public void setContact_early_morning(String contact_early_morning) {
		this.contact_early_morning = contact_early_morning;
	}

	public String getContact_night() {
		return contact_night;
	}

	public void setContact_night(String contact_night) {
		this.contact_night = contact_night;
	}


	@Override
	public String toString() {
		return "ContactInfo [contact_noon=" + contact_noon + ", phone_num_loc="
				+ phone_num_loc + ", contact_3m=" + contact_3m
				+ ", contact_1m=" + contact_1m + ", contact_1w=" + contact_1w
				+ ", p_relation=" + p_relation + ", phone_num=" + phone_num
				+ ", contact_name=" + contact_name + ", call_in_cnt="
				+ call_in_cnt + ", call_out_cnt=" + call_out_cnt
				+ ", call_out_len=" + call_out_len + ", contact_holiday="
				+ contact_holiday + ", needs_type=" + needs_type
				+ ", contact_weekday=" + contact_weekday
				+ ", contact_afternoon=" + contact_afternoon + ", call_len="
				+ call_len + ", contact_early_morning=" + contact_early_morning
				+ ", contact_night=" + contact_night + ", contact_3m_plus="
				+ contact_3m_plus + ", call_cnt=" + call_cnt + ", call_in_len="
				+ call_in_len + ", contact_all_day=" + contact_all_day
				+ ", contact_morning=" + contact_morning + ", contact_weekend="
				+ contact_weekend + "]";
	}

	public String getContact_3m_plus() {
		return contact_3m_plus;
	}

	public void setContact_3m_plus(String contact_3m_plus) {
		this.contact_3m_plus = contact_3m_plus;
	}

	public String getCall_cnt() {
		return call_cnt;
	}

	public void setCall_cnt(String call_cnt) {
		this.call_cnt = call_cnt;
	}

	public String getCall_in_len() {
		return call_in_len;
	}

	public void setCall_in_len(String call_in_len) {
		this.call_in_len = call_in_len;
	}

	public String getContact_all_day() {
		return contact_all_day;
	}

	public void setContact_all_day(String contact_all_day) {
		this.contact_all_day = contact_all_day;
	}

	public String getContact_morning() {
		return contact_morning;
	}

	public void setContact_morning(String contact_morning) {
		this.contact_morning = contact_morning;
	}

	public String getContact_weekend() {
		return contact_weekend;
	}

	public void setContact_weekend(String contact_weekend) {
		this.contact_weekend = contact_weekend;
	}
	
	
}
