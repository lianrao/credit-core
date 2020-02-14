/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年8月15日 下午4:14:21 
* @version V1.0   
*/
package com.wanda.credit.ds.client.pengyuan.bean;


/**
 * @author xiaobin.hou
 *
 */
public class Py_osta_PersonBase{
	

	private String name;
	private String documentNo;
	private String age;
	private String gender;
	private String originalAddress;
	private String verifyResult;
	private String birthday;
	private String cardNo;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getOriginalAddress() {
		return originalAddress;
	}
	public void setOriginalAddress(String originalAddress) {
		this.originalAddress = originalAddress;
	}
	public String getVerifyResult() {
		return verifyResult;
	}
	public void setVerifyResult(String verifyResult) {
		this.verifyResult = verifyResult;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	
	@Override
	public String toString() {
		return "Py_osta_PersonBase [name=" + name + ", documentNo="
				+ documentNo + ", age=" + age + ", gender=" + gender
				+ ", originalAddress=" + originalAddress + ", verifyResult="
				+ verifyResult + ", birthday=" + birthday + ", cardNo="
				+ cardNo + "]";
	}
	
	
	
	
	
	

}
