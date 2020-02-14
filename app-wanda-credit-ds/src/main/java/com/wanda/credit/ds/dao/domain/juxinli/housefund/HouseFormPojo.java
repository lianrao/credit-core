/**   
 * @Description: 不同城市公积金申请表单概要信息 
 * @author xiaobin.hou  
 * @date 2016年5月25日 下午2:53:18 
 * @version V1.0   
 */
package com.wanda.credit.ds.dao.domain.juxinli.housefund;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;


/**
 * @author ou.guohao
 *
 */
@Entity
@Table(name = "CPDB_DS.t_ds_jxl_housing_login")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HouseFormPojo extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private String seqId;
	private String regioCode;
	private String website;
	private String name;
	private String loginType;
	private String sortId;
	private String descript;
	private Date create_time;
	private Date update_time;
	private Set<HouseFormDetailPojo> houseFormDetails = new HashSet<HouseFormDetailPojo>();

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "seqid", unique = true, nullable = false, length = 32)
	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}


	public String getRegioCode() {
		return regioCode;
	}

	public void setRegioCode(String regioCode) {
		this.regioCode = regioCode;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="login_type")
	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	@Column(name="sort_id")
	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	@Column(name = "crt_time")
	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	@Column(name = "upd_time")
	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "houseForm")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	public Set<HouseFormDetailPojo> getHouseFormDetails() {
		return houseFormDetails;
	}

	public void setHouseFormDetails(Set<HouseFormDetailPojo> houseFormDetails) {
		this.houseFormDetails = houseFormDetails;
	}

}
