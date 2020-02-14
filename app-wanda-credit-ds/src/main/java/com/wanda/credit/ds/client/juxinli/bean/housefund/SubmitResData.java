/**   
* @Description: 聚信立_公积金_提交采集请求返回结果中的Data节点 
* @author xiaobin.hou  
* @date 2016年5月28日 下午5:45:48 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

/**
 * @author xiaobin.hou
 *
 */
public class SubmitResData {
	
	private String token;
	private String type;
	private String content;
	private int process_code;
	private String taskId;
	private boolean finish;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public boolean isFinish() {
		return finish;
	}
	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	public int getProcess_code() {
		return process_code;
	}
	public void setProcess_code(int process_code) {
		this.process_code = process_code;
	}
	
	public String toString() {
		return "SubmitResData [token=" + token + ", type=" + type
				+ ", content=" + content + ", process_code=" + process_code
				+ ", taskId=" + taskId + ", finish=" + finish + "]";
	}
	
	

}
