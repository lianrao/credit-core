package com.wanda.credit.ds.dao.domain.zhongshu;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wanda.credit.base.domain.BaseDomain;
/**
 * 车辆违章查询信息类
 * @author shenziqiang
 *
 */
@Entity
@Table(name = "T_DS_DMPCXY_CARIEG")
@SequenceGenerator(name = "SEQ_T_DS_DMPCXY_CARIEG", sequenceName = "SEQ_T_DS_DMPCXY_CARIEG")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ZS_CarIllegal_Result extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private long id;								
	private String trade_id;						
	private String carnumber;					
	private String cartype;						
	private String carcode;						
	private String cardrivenumber;				
	private String provinceid;					
	private String cityid;							
	private String carowner;						
	private String seq;								
	private String aTime;							
	private String location;						
	private String reason;							
	private String acount;							
	private String status;							
	private String department;						
	private String degree;							
	private String code;						
	private String archive;							
	private String telephone;						
	private String excutelocation;					
	private String excutedepartment;				
	private String category;						
	private String latefine;						
	private String punishmentaccording;				
	private String illegalentry;					
	private String locationid;						
	private String locationName;				
	private String dataSourceID;					
	private String recordType;						
	private String poundage;						
	private String cooperPoundge;					
	private String canProcess;						
	private String secondaryUniqueCode;				
	private String uniqueCode;						
	private String degreePoundage;					
	private String canprocessMsg;					
	private String other;							
	private String canUsePackage;					
	private String proxyType;						
	private String proxyRule;
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_DMPCXY_CARIEG")
	@Column(name = "ID", unique = true, nullable = false)
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
	public String getCarnumber() {
		return carnumber;
	}
	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}
	public String getCartype() {
		return cartype;
	}
	public void setCartype(String cartype) {
		this.cartype = cartype;
	}
	public String getCarcode() {
		return carcode;
	}
	public void setCarcode(String carcode) {
		this.carcode = carcode;
	}
	public String getCardrivenumber() {
		return cardrivenumber;
	}
	public void setCardrivenumber(String cardrivenumber) {
		this.cardrivenumber = cardrivenumber;
	}
	public String getProvinceid() {
		return provinceid;
	}
	public void setProvinceid(String provinceid) {
		this.provinceid = provinceid;
	}
	public String getCityid() {
		return cityid;
	}
	public void setCityid(String cityid) {
		this.cityid = cityid;
	}
	public String getCarowner() {
		return carowner;
	}
	public void setCarowner(String carowner) {
		this.carowner = carowner;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getaTime() {
		return aTime;
	}
	public void setaTime(String aTime) {
		this.aTime = aTime;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getAcount() {
		return acount;
	}
	public void setAcount(String acount) {
		this.acount = acount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getArchive() {
		return archive;
	}
	public void setArchive(String archive) {
		this.archive = archive;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getExcutelocation() {
		return excutelocation;
	}
	public void setExcutelocation(String excutelocation) {
		this.excutelocation = excutelocation;
	}
	public String getExcutedepartment() {
		return excutedepartment;
	}
	public void setExcutedepartment(String excutedepartment) {
		this.excutedepartment = excutedepartment;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLatefine() {
		return latefine;
	}
	public void setLatefine(String latefine) {
		this.latefine = latefine;
	}
	public String getPunishmentaccording() {
		return punishmentaccording;
	}
	public void setPunishmentaccording(String punishmentaccording) {
		this.punishmentaccording = punishmentaccording;
	}
	public String getIllegalentry() {
		return illegalentry;
	}
	public void setIllegalentry(String illegalentry) {
		this.illegalentry = illegalentry;
	}
	public String getLocationid() {
		return locationid;
	}
	public void setLocationid(String locationid) {
		this.locationid = locationid;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getDataSourceID() {
		return dataSourceID;
	}
	public void setDataSourceID(String dataSourceID) {
		this.dataSourceID = dataSourceID;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getPoundage() {
		return poundage;
	}
	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}
	public String getCooperPoundge() {
		return cooperPoundge;
	}
	public void setCooperPoundge(String cooperPoundge) {
		this.cooperPoundge = cooperPoundge;
	}
	public String getCanProcess() {
		return canProcess;
	}
	public void setCanProcess(String canProcess) {
		this.canProcess = canProcess;
	}
	public String getSecondaryUniqueCode() {
		return secondaryUniqueCode;
	}
	public void setSecondaryUniqueCode(String secondaryUniqueCode) {
		this.secondaryUniqueCode = secondaryUniqueCode;
	}
	public String getUniqueCode() {
		return uniqueCode;
	}
	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
	public String getDegreePoundage() {
		return degreePoundage;
	}
	public void setDegreePoundage(String degreePoundage) {
		this.degreePoundage = degreePoundage;
	}
	public String getCanprocessMsg() {
		return canprocessMsg;
	}
	public void setCanprocessMsg(String canprocessMsg) {
		this.canprocessMsg = canprocessMsg;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public String getCanUsePackage() {
		return canUsePackage;
	}
	public void setCanUsePackage(String canUsePackage) {
		this.canUsePackage = canUsePackage;
	}
	public String getProxyType() {
		return proxyType;
	}
	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
	}
	public String getProxyRule() {
		return proxyRule;
	}
	public void setProxyRule(String proxyRule) {
		this.proxyRule = proxyRule;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
