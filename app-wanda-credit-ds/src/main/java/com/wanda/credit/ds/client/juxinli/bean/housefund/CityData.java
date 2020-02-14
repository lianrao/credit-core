/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年5月25日 上午9:36:47 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

/**
 * @author xiaobin.hou
 *
 */
public class CityData {
	
	private String id;
	private String name;
	private String code;
	private String fullcode;
	private String level;
	private String category;
	private String status;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFullcode() {
		return fullcode;
	}
	public void setFullcode(String fullcode) {
		this.fullcode = fullcode;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "CityData [id=" + id + ", name=" + name + ", code=" + code
				+ ", fullcode=" + fullcode + ", level=" + level + ", category="
				+ category + ", status=" + status + "]";
	}
	
	

}
