/**   
* @Description: 征信数据 
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:42:43 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

/**
 * @author xiaobin.hou
 *
 */
public class CreditTransaction {
	
	private String token;// 系统用户id
	private String version;// 接口版本
	private String data_source;// 数据源
	private String update_time;// 获取数据时间
	private String report_time;// 报告时间
	private CreditRecord credit_record;// 信贷记录
	private String request_time;// 查询时间
	private String queried_number;// 被查询者证件号码
	private String queried_papers;// 被查询者证件类型
	private String query_marriage;// 被查询者婚姻状况
	private String queried_name;// 被查询者姓名
	private RecordQuery query;// 查询记录
	private String report_id;// 报告编号	
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getData_source() {
		return data_source;
	}
	public void setData_source(String data_source) {
		this.data_source = data_source;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getReport_time() {
		return report_time;
	}
	public void setReport_time(String report_time) {
		this.report_time = report_time;
	}
	public CreditRecord getCredit_record() {
		return credit_record;
	}
	public void setCredit_record(CreditRecord credit_record) {
		this.credit_record = credit_record;
	}
	public String getRequest_time() {
		return request_time;
	}
	public void setRequest_time(String request_time) {
		this.request_time = request_time;
	}
	public String getQueried_number() {
		return queried_number;
	}
	public void setQueried_number(String queried_number) {
		this.queried_number = queried_number;
	}
	public String getQueried_papers() {
		return queried_papers;
	}
	public void setQueried_papers(String queried_papers) {
		this.queried_papers = queried_papers;
	}
	public String getQuery_marriage() {
		return query_marriage;
	}
	public void setQuery_marriage(String query_marriage) {
		this.query_marriage = query_marriage;
	}
	public String getQueried_name() {
		return queried_name;
	}
	public void setQueried_name(String queried_name) {
		this.queried_name = queried_name;
	}
	public RecordQuery getQuery() {
		return query;
	}
	public void setQuery(RecordQuery query) {
		this.query = query;
	}
	public String getReport_id() {
		return report_id;
	}
	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}

}
