package com.wanda.credit.ds.client.juxinli.bean.reportNew.po;

import java.util.List;

import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ApplicationCheck;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.BehaviorCheck;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.CellBehavior;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.CollectionContact;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ContactInfo;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ContactRegion;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.DeliverAddress;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.EbusinessExpense;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.MainService;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.Report;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.TripInfo;

/**
 * @author xiaobin.hou 报告数据
 */
public class ReportData {
	private Report report;

	private List<ApplicationCheck> application_check;

	private UserInfoCheck user_info_check;

	private List<BehaviorCheck> behavior_check;

	private List<CellBehavior> cell_behavior;

	private List<ContactRegion> contact_region;

	private List<ContactInfo> contact_list;

	private List<MainService> main_service;

	private List<DeliverAddress> deliver_address;

	private List<EbusinessExpense> ebusiness_expense;

	private List<CollectionContact> collection_contact;

	private List<TripInfo> trip_info;

	public List<ApplicationCheck> getApplication_check() {

		return application_check;
	}

	public void setApplication_check(List<ApplicationCheck> application_check) {

		this.application_check = application_check;
	}

	public List<ContactRegion> getContact_region() {

		return contact_region;
	}

	public void setContact_region(List<ContactRegion> contact_region) {

		this.contact_region = contact_region;
	}

	public List<BehaviorCheck> getBehavior_check() {

		return behavior_check;
	}

	public void setBehavior_check(List<BehaviorCheck> behavior_check) {

		this.behavior_check = behavior_check;
	}

	public List<CollectionContact> getCollection_contact() {

		return collection_contact;
	}

	public void setCollection_contact(List<CollectionContact> collection_contact) {

		this.collection_contact = collection_contact;
	}

	public List<DeliverAddress> getDeliver_address() {

		return deliver_address;
	}

	public void setDeliver_address(List<DeliverAddress> deliver_address) {

		this.deliver_address = deliver_address;
	}

	public List<EbusinessExpense> getEbusiness_expense() {

		return ebusiness_expense;
	}

	public void setEbusiness_expense(List<EbusinessExpense> ebusiness_expense) {

		this.ebusiness_expense = ebusiness_expense;
	}

	public List<MainService> getMain_service() {

		return main_service;
	}

	public void setMain_service(List<MainService> main_service) {

		this.main_service = main_service;
	}

	public List<TripInfo> getTrip_info() {

		return trip_info;
	}

	public void setTrip_info(List<TripInfo> trip_info) {

		this.trip_info = trip_info;
	}

	public List<CellBehavior> getCell_behavior() {

		return cell_behavior;
	}

	public void setCell_behavior(List<CellBehavior> cell_behavior) {

		this.cell_behavior = cell_behavior;
	}

	public Report getReport() {

		return report;
	}

	public void setReport(Report report) {

		this.report = report;
	}

	public List<ContactInfo> getContact_list() {

		return contact_list;
	}

	public void setContact_list(List<ContactInfo> contact_list) {

		this.contact_list = contact_list;
	}

	public UserInfoCheck getUser_info_check() {
		return user_info_check;
	}

	public void setUser_info_check(UserInfoCheck user_info_check) {
		this.user_info_check = user_info_check;
	}

	@Override
	public String toString() {
		return "ReportData [report=" + report + ", application_check="
				+ application_check + ", user_info_check=" + user_info_check
				+ ", behavior_check=" + behavior_check + ", cell_behavior="
				+ cell_behavior + ", contact_region=" + contact_region
				+ ", contact_list=" + contact_list + ", main_service="
				+ main_service + ", deliver_address=" + deliver_address
				+ ", ebusiness_expense=" + ebusiness_expense
				+ ", collection_contact=" + collection_contact + ", trip_info="
				+ trip_info + "]";
	}

}
