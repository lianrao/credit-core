package com.wanda.credit.ds.client.juxinli.bean.mobile;

import com.wanda.credit.ds.client.juxinli.bean.ebusi.MobileEBusiDataSource;

public class SubmitCollRes {
	
	private String type;//响应类型
	private String content;//响应信息
	private int process_code;//返回码
	private boolean finish;//所有采集流程是否结束
	private MobileEBusiDataSource next_datasource;//下一个需要采集的数据源
	
	
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
	public int getProcess_code() {
		return process_code;
	}
	public void setProcess_code(int process_code) {
		this.process_code = process_code;
	}
	public boolean getFinish() {
		return finish;
	}
	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	public MobileEBusiDataSource getNext_datasource() {
		return next_datasource;
	}
	public void setNext_datasource(MobileEBusiDataSource next_datasource) {
		this.next_datasource = next_datasource;
	}

	public String toString() {
		return "SubmitCollRes [type=" + type + ", content=" + content
				+ ", process_code=" + process_code + ", finish=" + finish
				+ ", next_datasource=" + next_datasource + "]";
	}
	
	
	

}
