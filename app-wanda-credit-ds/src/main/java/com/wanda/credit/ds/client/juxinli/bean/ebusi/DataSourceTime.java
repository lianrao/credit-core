package com.wanda.credit.ds.client.juxinli.bean.ebusi;

import java.io.Serializable;

public class DataSourceTime implements Serializable {
    private static final long serialVersionUID = 1L;
	
	private String year;
	private String month;
	private String dayOfMonth;
	private String hourOfDay;
	private String minute;
	private String second;
	
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	public String getHourOfDay() {
		return hourOfDay;
	}
	public void setHourOfDay(String hourOfDay) {
		this.hourOfDay = hourOfDay;
	}
	public String getMinute() {
		return minute;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}

	
	public String toString() {
		return "DataSourceTime [year=" + year + ", month=" + month
				+ ", dayOfMonth=" + dayOfMonth + ", hourOfDay=" + hourOfDay
				+ ", minute=" + minute + ", second=" + second + "]";
	}
	
	

}
