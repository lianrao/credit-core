package com.wanda.credit.ds.dao.domain.juxinli.report;

import java.util.List;

import com.wanda.credit.ds.client.juxinli.bean.report.CellBehavior;
import com.wanda.credit.ds.client.juxinli.bean.report.DeliverAddress;
import com.wanda.credit.ds.client.juxinli.bean.report.TripInfo;

/**
 * 报告数据
 * @author xiaobin.hou
 *
 */
public class ReportDataPojo {
	//运营商通话详情
	private List<ContactInfoPojo> contact_list;
	//绑定数据源信息
	private List<DataSourcePojo> data_source;
	//行为检测
	private List<BehaviorCheckPojo> behavior_check;
	//联系人名单
	private List<CollectionContactPojo> collection_contact;
	//出行消费
	private List<TripConsumePojo> trip_consume;
	//电商月消费
	private List<EbusinessExpensePojo> ebusiness_expense;
	//申请人信息
	private PersonPojo person;
	//主要服务
	private List<MainServicePojo> main_service;
	//联系人区域汇总
	private List<ContactRegionPojo> contact_region;
	//信息核对
	private List<ApplicationCheckPojo> application_check;
	//电商地址分析
	private List<DeliverAddress> deliver_address;
	
	private Report report;
	//出行分析
	private List<TripInfo> trip_info;
	//运营商数据整理
	private List<CellBehavior> cell_behavior;
	
	private String _id;
	
	public String get_id(){
	
		return _id;
	}
	
	public void set_id(String _id){
	
		this._id = _id;
	}
	
	public List<ApplicationCheckPojo> getApplication_check(){
	
		return application_check;
	}
	
	public void setApplication_check(List<ApplicationCheckPojo> application_check){
	
		this.application_check = application_check;
	}
	
	public List<ContactRegionPojo> getContact_region(){
	
		return contact_region;
	}
	
	public void setContact_region(List<ContactRegionPojo> contact_region){
	
		this.contact_region = contact_region;
	}
	
	public List<DataSourcePojo> getData_source(){
	
		return data_source;
	}
	
	public void setData_source(List<DataSourcePojo> data_source){
	
		this.data_source = data_source;
	}
	
	public List<BehaviorCheckPojo> getBehavior_check(){
	
		return behavior_check;
	}
	
	public void setBehavior_check(List<BehaviorCheckPojo> behavior_check){
	
		this.behavior_check = behavior_check;
	}
	
	
	
	public List<TripConsumePojo> getTrip_consume(){
	
		return trip_consume;
	}
	
	public void setTrip_consume(List<TripConsumePojo> trip_consume){
	
		this.trip_consume = trip_consume;
	}
	
	public List<EbusinessExpensePojo> getEbusiness_expense(){
	
		return ebusiness_expense;
	}
	
	public void setEbusiness_expense(List<EbusinessExpensePojo> ebusiness_expense){
	
		this.ebusiness_expense = ebusiness_expense;
	}
	
	public List<MainServicePojo> getMain_service(){
	
		return main_service;
	}
	
	public void setMain_service(List<MainServicePojo> main_service){
	
		this.main_service = main_service;
	}	
	public PersonPojo getPerson(){
	
		return person;
	}
	
	public void setPerson(PersonPojo person){
	
		this.person = person;
	}
	

	public Report getReport(){
	
		return report;
	}
	
	public void setReport(Report report){
	
		this.report = report;
	}
	
	public List<ContactInfoPojo> getContact_list(){
	
		return contact_list;
	}
	
	public void setContact_list(List<ContactInfoPojo> contact_list){
	
		this.contact_list = contact_list;
	}

	public List<DeliverAddress> getDeliver_address() {
		return deliver_address;
	}

	public void setDeliver_address(List<DeliverAddress> deliver_address) {
		this.deliver_address = deliver_address;
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

	public List<CollectionContactPojo> getCollection_contact() {
		return collection_contact;
	}

	public void setCollection_contact(List<CollectionContactPojo> collection_contact) {
		this.collection_contact = collection_contact;
	}
	
	// TODO 不需要解析
	// private String _id;
	
	// TODO 废弃字段可以忽略
	// private unionpay_expense unionpay_expense;
}
