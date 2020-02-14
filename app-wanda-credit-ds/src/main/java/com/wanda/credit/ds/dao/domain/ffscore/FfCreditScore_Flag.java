package com.wanda.credit.ds.dao.domain.ffscore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_WS_CREDITFLAG")
@SequenceGenerator(name="ID_SEQ_T_DS_WS_CREDITFLAG",sequenceName="SEQ_T_DS_WS_CREDITFLAG",allocationSize=1)  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FfCreditScore_Flag extends BaseDomain{
	/**
	 */
	private static final long serialVersionUID = 1L;
/*	hotel_level 酒店会员等级
	worth_level 网数整合的会员等级
	hotel_cnt_1Y近一年酒店消费次数
	li_amt寿险购买金额
	pi_amt财险购买金额*/
	/**
	 * 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="ID_SEQ_T_DS_WS_CREDITFLAG")  
	@Column(name = "ID", unique = true, nullable = false)
	 private Long id ;
	
	 private String mobile;
	 private String trade_id;

	 private String chineseName;

	 private String idNo;

	 private String sex;

	 private String age;

	 private String birthplace;

	 private String drivingLicenseNo;

	 private String province;

	 private String city;

	 private String profession;

	 private String professional_title;

	 private String working_state;

	 private String working_experience;

	 private String working_company_type;

	 private String industry_type;

	 private String education_state;

	 private String highest_education;

	 private String highest_degree;

	 private String graduate_school;

	 private String mastered_language;

	 private String marital_state;

	 private String children_num;

	 private String children_age;

	 private String children_sex;

	 private String children_edu_state;

	 private String father_age;

	 private String mother_age;

	 private String spouse_chname;

	 private String spouse_age;

	 private String spouse_edu_state;

	 private String spouse_idNo;

	 private String personal_monthly_salary;

	 private String family_monthly_salary;

	 private String family_income_source;

	 private String family_main_income_source;

	 private String investment_in_stock;

	 private String investment_in_finance;

	 private String ffan_level;

	 private String mall_level;
	 /**
	  * 酒店会员等级
	  */
	 private String hotel_level;

	 private String cinema_level;

	 private String kuaiqian_level;

	 private String workday_bill_num;

	 private String weekend_bill_num;

	 private String workday_bill_amount;

	 private String weekend_bill_amount;

	 private String buy_times_aplus;

	 private String buy_amount_aplus;

	 private String buy_avgamount_aplus;

	 private String buy_times_a;

	 private String buy_amount_a;

	 private String buy_avgamount_a;

	 private String buy_times_b;

	 private String buy_amount_b;

	 private String buy_avgamount_b;

	 private String buy_times_c;

	 private String buy_amount_c;

	 private String buy_avgamount_c;

	 private String buy_times_d;

	 private String buy_amount_d;

	 private String buy_avgamount_d;

	 private String buy_times_e;

	 private String buy_amount_e;

	 private String buy_avgamount_e;

	 private String buy_times_s;

	 private String buy_amount_s;

	 private String buy_avgamount_s;

	 private String first_order_time;

	 private String last_order_time;

	 private String workday_arrival_num;

	 private String weekend_arrival_num;

	 private String workday_departure_num;

	 private String weekend_departure_num;

	 private String holiday_arrival_num;

	 private String nonholiday_arrival_num;

	 private String hotel_num;

	 private String city_num;

	 private String arrival_alone_num;

	 private String arrival_multiperson_num;

	 private String arrival_child_num;

	 private String arrival_multiroom_num;

	 private String cancel_num;
	 private String major;
	 private String graduate_year;
	 private String enrollment_year;

	 private String prefer_brand;

	 private String prefer_category;
	 /**
	  * 网数整合的会员 
	  */
	 private String worth_level;

	 private String is_high_worth;
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getBirthplace() {
		return birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public String getDrivingLicenseNo() {
		return drivingLicenseNo;
	}

	public void setDrivingLicenseNo(String drivingLicenseNo) {
		this.drivingLicenseNo = drivingLicenseNo;
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

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getProfessional_title() {
		return professional_title;
	}

	public void setProfessional_title(String professional_title) {
		this.professional_title = professional_title;
	}

	public String getWorking_state() {
		return working_state;
	}

	public void setWorking_state(String working_state) {
		this.working_state = working_state;
	}

	public String getWorking_experience() {
		return working_experience;
	}

	public void setWorking_experience(String working_experience) {
		this.working_experience = working_experience;
	}

	public String getWorking_company_type() {
		return working_company_type;
	}

	public void setWorking_company_type(String working_company_type) {
		this.working_company_type = working_company_type;
	}

	public String getIndustry_type() {
		return industry_type;
	}

	public void setIndustry_type(String industry_type) {
		this.industry_type = industry_type;
	}

	public String getEducation_state() {
		return education_state;
	}

	public void setEducation_state(String education_state) {
		this.education_state = education_state;
	}

	public String getHighest_education() {
		return highest_education;
	}

	public void setHighest_education(String highest_education) {
		this.highest_education = highest_education;
	}

	public String getHighest_degree() {
		return highest_degree;
	}

	public void setHighest_degree(String highest_degree) {
		this.highest_degree = highest_degree;
	}

	public String getGraduate_school() {
		return graduate_school;
	}

	public void setGraduate_school(String graduate_school) {
		this.graduate_school = graduate_school;
	}

	public String getMastered_language() {
		return mastered_language;
	}

	public void setMastered_language(String mastered_language) {
		this.mastered_language = mastered_language;
	}

	public String getMarital_state() {
		return marital_state;
	}

	public void setMarital_state(String marital_state) {
		this.marital_state = marital_state;
	}

	public String getChildren_num() {
		return children_num;
	}

	public void setChildren_num(String children_num) {
		this.children_num = children_num;
	}

	public String getChildren_age() {
		return children_age;
	}

	public void setChildren_age(String children_age) {
		this.children_age = children_age;
	}

	public String getChildren_sex() {
		return children_sex;
	}

	public void setChildren_sex(String children_sex) {
		this.children_sex = children_sex;
	}

	public String getChildren_edu_state() {
		return children_edu_state;
	}

	public void setChildren_edu_state(String children_edu_state) {
		this.children_edu_state = children_edu_state;
	}

	public String getFather_age() {
		return father_age;
	}

	public void setFather_age(String father_age) {
		this.father_age = father_age;
	}

	public String getMother_age() {
		return mother_age;
	}

	public void setMother_age(String mother_age) {
		this.mother_age = mother_age;
	}

	public String getSpouse_chname() {
		return spouse_chname;
	}

	public void setSpouse_chname(String spouse_chname) {
		this.spouse_chname = spouse_chname;
	}

	public String getSpouse_age() {
		return spouse_age;
	}

	public void setSpouse_age(String spouse_age) {
		this.spouse_age = spouse_age;
	}

	public String getSpouse_edu_state() {
		return spouse_edu_state;
	}

	public void setSpouse_edu_state(String spouse_edu_state) {
		this.spouse_edu_state = spouse_edu_state;
	}

	public String getSpouse_idNo() {
		return spouse_idNo;
	}

	public void setSpouse_idNo(String spouse_idNo) {
		this.spouse_idNo = spouse_idNo;
	}

	public String getPersonal_monthly_salary() {
		return personal_monthly_salary;
	}

	public void setPersonal_monthly_salary(String personal_monthly_salary) {
		this.personal_monthly_salary = personal_monthly_salary;
	}

	public String getFamily_monthly_salary() {
		return family_monthly_salary;
	}

	public void setFamily_monthly_salary(String family_monthly_salary) {
		this.family_monthly_salary = family_monthly_salary;
	}

	public String getFamily_income_source() {
		return family_income_source;
	}

	public void setFamily_income_source(String family_income_source) {
		this.family_income_source = family_income_source;
	}

	public String getFamily_main_income_source() {
		return family_main_income_source;
	}

	public void setFamily_main_income_source(String family_main_income_source) {
		this.family_main_income_source = family_main_income_source;
	}

	public String getInvestment_in_stock() {
		return investment_in_stock;
	}

	public void setInvestment_in_stock(String investment_in_stock) {
		this.investment_in_stock = investment_in_stock;
	}

	public String getInvestment_in_finance() {
		return investment_in_finance;
	}

	public void setInvestment_in_finance(String investment_in_finance) {
		this.investment_in_finance = investment_in_finance;
	}

	public String getFfan_level() {
		return ffan_level;
	}

	public void setFfan_level(String ffan_level) {
		this.ffan_level = ffan_level;
	}

	public String getMall_level() {
		return mall_level;
	}

	public void setMall_level(String mall_level) {
		this.mall_level = mall_level;
	}

	public String getHotel_level() {
		return hotel_level;
	}

	public void setHotel_level(String hotel_level) {
		this.hotel_level = hotel_level;
	}

	public String getCinema_level() {
		return cinema_level;
	}

	public void setCinema_level(String cinema_level) {
		this.cinema_level = cinema_level;
	}

	public String getKuaiqian_level() {
		return kuaiqian_level;
	}

	public void setKuaiqian_level(String kuaiqian_level) {
		this.kuaiqian_level = kuaiqian_level;
	}

	public String getWorkday_bill_num() {
		return workday_bill_num;
	}

	public void setWorkday_bill_num(String workday_bill_num) {
		this.workday_bill_num = workday_bill_num;
	}

	public String getWeekend_bill_num() {
		return weekend_bill_num;
	}

	public void setWeekend_bill_num(String weekend_bill_num) {
		this.weekend_bill_num = weekend_bill_num;
	}

	public String getWorkday_bill_amount() {
		return workday_bill_amount;
	}

	public void setWorkday_bill_amount(String workday_bill_amount) {
		this.workday_bill_amount = workday_bill_amount;
	}

	public String getWeekend_bill_amount() {
		return weekend_bill_amount;
	}

	public void setWeekend_bill_amount(String weekend_bill_amount) {
		this.weekend_bill_amount = weekend_bill_amount;
	}

	public String getBuy_times_aplus() {
		return buy_times_aplus;
	}

	public void setBuy_times_aplus(String buy_times_aplus) {
		this.buy_times_aplus = buy_times_aplus;
	}

	public String getBuy_amount_aplus() {
		return buy_amount_aplus;
	}

	public void setBuy_amount_aplus(String buy_amount_aplus) {
		this.buy_amount_aplus = buy_amount_aplus;
	}

	public String getBuy_avgamount_aplus() {
		return buy_avgamount_aplus;
	}

	public void setBuy_avgamount_aplus(String buy_avgamount_aplus) {
		this.buy_avgamount_aplus = buy_avgamount_aplus;
	}

	public String getBuy_times_a() {
		return buy_times_a;
	}

	public void setBuy_times_a(String buy_times_a) {
		this.buy_times_a = buy_times_a;
	}

	public String getBuy_amount_a() {
		return buy_amount_a;
	}

	public void setBuy_amount_a(String buy_amount_a) {
		this.buy_amount_a = buy_amount_a;
	}

	public String getBuy_avgamount_a() {
		return buy_avgamount_a;
	}

	public void setBuy_avgamount_a(String buy_avgamount_a) {
		this.buy_avgamount_a = buy_avgamount_a;
	}

	public String getBuy_times_b() {
		return buy_times_b;
	}

	public void setBuy_times_b(String buy_times_b) {
		this.buy_times_b = buy_times_b;
	}

	public String getBuy_amount_b() {
		return buy_amount_b;
	}

	public void setBuy_amount_b(String buy_amount_b) {
		this.buy_amount_b = buy_amount_b;
	}

	public String getBuy_avgamount_b() {
		return buy_avgamount_b;
	}

	public void setBuy_avgamount_b(String buy_avgamount_b) {
		this.buy_avgamount_b = buy_avgamount_b;
	}

	public String getBuy_times_c() {
		return buy_times_c;
	}

	public void setBuy_times_c(String buy_times_c) {
		this.buy_times_c = buy_times_c;
	}

	public String getBuy_amount_c() {
		return buy_amount_c;
	}

	public void setBuy_amount_c(String buy_amount_c) {
		this.buy_amount_c = buy_amount_c;
	}

	public String getBuy_avgamount_c() {
		return buy_avgamount_c;
	}

	public void setBuy_avgamount_c(String buy_avgamount_c) {
		this.buy_avgamount_c = buy_avgamount_c;
	}

	public String getBuy_times_d() {
		return buy_times_d;
	}

	public void setBuy_times_d(String buy_times_d) {
		this.buy_times_d = buy_times_d;
	}

	public String getBuy_amount_d() {
		return buy_amount_d;
	}

	public void setBuy_amount_d(String buy_amount_d) {
		this.buy_amount_d = buy_amount_d;
	}

	public String getBuy_avgamount_d() {
		return buy_avgamount_d;
	}

	public void setBuy_avgamount_d(String buy_avgamount_d) {
		this.buy_avgamount_d = buy_avgamount_d;
	}

	public String getBuy_times_e() {
		return buy_times_e;
	}

	public void setBuy_times_e(String buy_times_e) {
		this.buy_times_e = buy_times_e;
	}

	public String getBuy_amount_e() {
		return buy_amount_e;
	}

	public void setBuy_amount_e(String buy_amount_e) {
		this.buy_amount_e = buy_amount_e;
	}

	public String getBuy_avgamount_e() {
		return buy_avgamount_e;
	}

	public void setBuy_avgamount_e(String buy_avgamount_e) {
		this.buy_avgamount_e = buy_avgamount_e;
	}

	public String getBuy_times_s() {
		return buy_times_s;
	}

	public void setBuy_times_s(String buy_times_s) {
		this.buy_times_s = buy_times_s;
	}

	public String getBuy_amount_s() {
		return buy_amount_s;
	}

	public void setBuy_amount_s(String buy_amount_s) {
		this.buy_amount_s = buy_amount_s;
	}

	public String getBuy_avgamount_s() {
		return buy_avgamount_s;
	}

	public void setBuy_avgamount_s(String buy_avgamount_s) {
		this.buy_avgamount_s = buy_avgamount_s;
	}

	public String getFirst_order_time() {
		return first_order_time;
	}

	public void setFirst_order_time(String first_order_time) {
		this.first_order_time = first_order_time;
	}

	public String getLast_order_time() {
		return last_order_time;
	}

	public void setLast_order_time(String last_order_time) {
		this.last_order_time = last_order_time;
	}

	public String getWorkday_arrival_num() {
		return workday_arrival_num;
	}

	public void setWorkday_arrival_num(String workday_arrival_num) {
		this.workday_arrival_num = workday_arrival_num;
	}

	public String getWeekend_arrival_num() {
		return weekend_arrival_num;
	}

	public void setWeekend_arrival_num(String weekend_arrival_num) {
		this.weekend_arrival_num = weekend_arrival_num;
	}

	public String getWorkday_departure_num() {
		return workday_departure_num;
	}

	public void setWorkday_departure_num(String workday_departure_num) {
		this.workday_departure_num = workday_departure_num;
	}

	public String getWeekend_departure_num() {
		return weekend_departure_num;
	}

	public void setWeekend_departure_num(String weekend_departure_num) {
		this.weekend_departure_num = weekend_departure_num;
	}

	public String getHoliday_arrival_num() {
		return holiday_arrival_num;
	}

	public void setHoliday_arrival_num(String holiday_arrival_num) {
		this.holiday_arrival_num = holiday_arrival_num;
	}

	public String getNonholiday_arrival_num() {
		return nonholiday_arrival_num;
	}

	public void setNonholiday_arrival_num(String nonholiday_arrival_num) {
		this.nonholiday_arrival_num = nonholiday_arrival_num;
	}

	public String getHotel_num() {
		return hotel_num;
	}

	public void setHotel_num(String hotel_num) {
		this.hotel_num = hotel_num;
	}

	public String getCity_num() {
		return city_num;
	}

	public void setCity_num(String city_num) {
		this.city_num = city_num;
	}

	public String getArrival_alone_num() {
		return arrival_alone_num;
	}

	public void setArrival_alone_num(String arrival_alone_num) {
		this.arrival_alone_num = arrival_alone_num;
	}

	public String getArrival_multiperson_num() {
		return arrival_multiperson_num;
	}

	public void setArrival_multiperson_num(String arrival_multiperson_num) {
		this.arrival_multiperson_num = arrival_multiperson_num;
	}

	public String getArrival_child_num() {
		return arrival_child_num;
	}

	public void setArrival_child_num(String arrival_child_num) {
		this.arrival_child_num = arrival_child_num;
	}

	public String getArrival_multiroom_num() {
		return arrival_multiroom_num;
	}

	public void setArrival_multiroom_num(String arrival_multiroom_num) {
		this.arrival_multiroom_num = arrival_multiroom_num;
	}

	public String getCancel_num() {
		return cancel_num;
	}

	public void setCancel_num(String cancel_num) {
		this.cancel_num = cancel_num;
	}

	public String getPrefer_brand() {
		return prefer_brand;
	}

	public void setPrefer_brand(String prefer_brand) {
		this.prefer_brand = prefer_brand;
	}

	public String getPrefer_category() {
		return prefer_category;
	}

	public void setPrefer_category(String prefer_category) {
		this.prefer_category = prefer_category;
	}

	public String getWorth_level() {
		return worth_level;
	}

	public void setWorth_level(String worth_level) {
		this.worth_level = worth_level;
	}

	public String getIs_high_worth() {
		return is_high_worth;
	}

	public void setIs_high_worth(String is_high_worth) {
		this.is_high_worth = is_high_worth;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getGraduate_year() {
		return graduate_year;
	}

	public void setGraduate_year(String graduate_year) {
		this.graduate_year = graduate_year;
	}

	public String getEnrollment_year() {
		return enrollment_year;
	}

	public void setEnrollment_year(String enrollment_year) {
		this.enrollment_year = enrollment_year;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	
}
