package com.wanda.credit.ds.client.juxinli.bean.report;

import java.util.List;

/**
 * 报告数据
 * @author xiaobin.hou
 *
 */
public class ReportData {
	//运营商通话详情
	private List<ContactInfo> contact_list;
	//绑定数据源信息
	private List<DataSource> data_source;
	//行为检测
	private List<BehaviorCheck> behavior_check;
	//联系人名单
	private List<CollectionContact> collection_contact;
	//出行消费
	private List<TripConsume> trip_consume;
	//电商月消费
	private List<EbusinessExpense> ebusiness_expense;
	//申请人信息
	private Person person;
	//主要服务
	private List<MainService> main_service;
	//联系人区域汇总
	private List<ContactRegion> contact_region;
	//信息核对
	private List<ApplicationCheck> application_check;
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
	
	public List<ApplicationCheck> getApplication_check(){
	
		return application_check;
	}
	
	public void setApplication_check(List<ApplicationCheck> application_check){
	
		this.application_check = application_check;
	}
	
	public List<ContactRegion> getContact_region(){
	
		return contact_region;
	}
	
	public void setContact_region(List<ContactRegion> contact_region){
	
		this.contact_region = contact_region;
	}
	
	public List<DataSource> getData_source(){
	
		return data_source;
	}
	
	public void setData_source(List<DataSource> data_source){
	
		this.data_source = data_source;
	}
	
	public List<BehaviorCheck> getBehavior_check(){
	
		return behavior_check;
	}
	
	public void setBehavior_check(List<BehaviorCheck> behavior_check){
	
		this.behavior_check = behavior_check;
	}
	
	public List<CollectionContact> getCollection_contact(){
	
		return collection_contact;
	}
	
	public void setCollection_contact(List<CollectionContact> collection_contact){
	
		this.collection_contact = collection_contact;
	}
	
	public List<DeliverAddress> getDeliver_address(){
	
		return deliver_address;
	}
	
	public void setDeliver_address(List<DeliverAddress> deliver_address){
	
		this.deliver_address = deliver_address;
	}
	
	public List<TripConsume> getTrip_consume(){
	
		return trip_consume;
	}
	
	public void setTrip_consume(List<TripConsume> trip_consume){
	
		this.trip_consume = trip_consume;
	}
	
	public List<EbusinessExpense> getEbusiness_expense(){
	
		return ebusiness_expense;
	}
	
	public void setEbusiness_expense(List<EbusinessExpense> ebusiness_expense){
	
		this.ebusiness_expense = ebusiness_expense;
	}
	
	public List<MainService> getMain_service(){
	
		return main_service;
	}
	
	public void setMain_service(List<MainService> main_service){
	
		this.main_service = main_service;
	}
	
	public List<TripInfo> getTrip_info(){
	
		return trip_info;
	}
	
	public void setTrip_info(List<TripInfo> trip_info){
	
		this.trip_info = trip_info;
	}
	
	public Person getPerson(){
	
		return person;
	}
	
	public void setPerson(Person person){
	
		this.person = person;
	}
	
	public List<CellBehavior> getCell_behavior(){
	
		return cell_behavior;
	}
	
	public void setCell_behavior(List<CellBehavior> cell_behavior){
	
		this.cell_behavior = cell_behavior;
	}
	
	public Report getReport(){
	
		return report;
	}
	
	public void setReport(Report report){
	
		this.report = report;
	}
	
	public List<ContactInfo> getContact_list(){
	
		return contact_list;
	}
	
	public void setContact_list(List<ContactInfo> contact_list){
	
		this.contact_list = contact_list;
	}
	
	@Override
	public String toString(){
	
		return "ReportData [contact_list=" + contact_list + ", data_source=" + data_source + ", behavior_check="
		        + behavior_check + ", collection_contact=" + collection_contact + ", trip_consume=" + trip_consume
		        + ", ebusiness_expense=" + ebusiness_expense + ", person=" + person + ", main_service=" + main_service
		        + ", contact_region=" + contact_region + ", application_check=" + application_check
		        + ", deliver_address=" + deliver_address + ", report=" + report + ", trip_info=" + trip_info
		        + ", cell_behavior=" + cell_behavior + ", _id=" + _id + "]";
	}
	
	// TODO 不需要解析
	// private String _id;
	
	// TODO 废弃字段可以忽略
	// private unionpay_expense unionpay_expense;
}
