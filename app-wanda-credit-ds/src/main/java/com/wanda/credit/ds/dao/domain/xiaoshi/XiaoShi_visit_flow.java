package com.wanda.credit.ds.dao.domain.xiaoshi;

import java.util.Date;

/**
 * 人流统计传输接口
 */
public class XiaoShi_visit_flow {

	private long id;
	private String trade_id;
	 private String devSn ;
	 private String  pepoleCount;
	 private Date timestamp  ;
	 private String  VIPCount  ;
	 private String  ordinaryCount ;
	 private String  maleCount     ;
	 private String  ageCount1     ;
	 private String  ageCount2    ;
	 private String  ageCount3    ;
	 private String  ageCount4    ;
	 private String  ageCount5    ;
	 private String  reserve1    ;
	 private String  reserve2    ;
	 private String  reserve3    ;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getDevSn() {
		return devSn;
	}

	public void setDevSn(String devSn) {
		this.devSn = devSn;
	}

	public String getPepoleCount() {
		return pepoleCount;
	}

	public void setPepoleCount(String pepoleCount) {
		this.pepoleCount = pepoleCount;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getVIPCount() {
		return VIPCount;
	}

	public void setVIPCount(String vIPCount) {
		VIPCount = vIPCount;
	}

	public String getOrdinaryCount() {
		return ordinaryCount;
	}

	public void setOrdinaryCount(String ordinaryCount) {
		this.ordinaryCount = ordinaryCount;
	}

	public String getMaleCount() {
		return maleCount;
	}

	public void setMaleCount(String maleCount) {
		this.maleCount = maleCount;
	}

	public String getAgeCount1() {
		return ageCount1;
	}

	public void setAgeCount1(String ageCount1) {
		this.ageCount1 = ageCount1;
	}

	public String getAgeCount2() {
		return ageCount2;
	}

	public void setAgeCount2(String ageCount2) {
		this.ageCount2 = ageCount2;
	}

	public String getAgeCount3() {
		return ageCount3;
	}

	public void setAgeCount3(String ageCount3) {
		this.ageCount3 = ageCount3;
	}

	public String getAgeCount4() {
		return ageCount4;
	}

	public void setAgeCount4(String ageCount4) {
		this.ageCount4 = ageCount4;
	}

	public String getAgeCount5() {
		return ageCount5;
	}

	public void setAgeCount5(String ageCount5) {
		this.ageCount5 = ageCount5;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

}
