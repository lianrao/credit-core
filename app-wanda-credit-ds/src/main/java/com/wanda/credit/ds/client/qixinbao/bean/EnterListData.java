/**   
* @Description: 企业名称模糊查询返回data节点 
* @author xiaobin.hou  
* @date 2016年12月19日 下午5:15:20 
* @version V1.0   
*/
package com.wanda.credit.ds.client.qixinbao.bean;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class EnterListData {
	
	private int total;
	private int num;
	private List<EnterListItem> items;
	
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public List<EnterListItem> getItems() {
		return items;
	}
	public void setItems(List<EnterListItem> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "EnterListData [total=" + total + ", num=" + num + ", items="
				+ items + "]";
	}
	
	

}
