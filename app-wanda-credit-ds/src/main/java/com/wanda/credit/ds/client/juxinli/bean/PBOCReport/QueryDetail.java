/**   
* @Description: 查询明细 
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:18:28 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

/**
 * @author xiaobin.hou
 *
 */
public class QueryDetail {
	
	private String query_time;// 查询日期
	private String query_reason;// 查询原因
	private String query_operator;// 查询操作员	
	public String getQuery_time() {
		return query_time;
	}
	public void setQuery_time(String query_time) {
		this.query_time = query_time;
	}
	public String getQuery_reason() {
		return query_reason;
	}
	public void setQuery_reason(String query_reason) {
		this.query_reason = query_reason;
	}
	public String getQuery_operator() {
		return query_operator;
	}
	public void setQuery_operator(String query_operator) {
		this.query_operator = query_operator;
	}
	@Override
	public String toString() {
		return "QueryDetail [query_time=" + query_time + ", query_reason="
				+ query_reason + ", query_operator=" + query_operator + "]";
	}
	
	

}
