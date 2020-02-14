package com.wanda.credit.ds.dao.domain.qxb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 前海 黑名单 信息
 **/
@Entity
@Table(name = "T_DS_QXB_Partners_should")
@SequenceGenerator(name = "Seq_T_DS_QXB_Partners_should", sequenceName = "Seq_T_DS_QXB_Partners_should")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Partners_should extends QxbBaseDomain {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long partnerid;
	private String shoud_capi ;
	private String invest_type ;
	private String should_capi_date ;

	public Partners_should() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_T_DS_QXB_Partners_should")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getShoud_capi() {
		return shoud_capi;
	}

	public void setShoud_capi(String shoud_capi) {
		this.shoud_capi = shoud_capi;
	}

	public String getShould_capi_date() {
		return should_capi_date;
	}

	public void setShould_capi_date(String should_capi_date) {
		this.should_capi_date = should_capi_date;
	}

	public long getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(long partnerid) {
		this.partnerid = partnerid;
	}

	public String getInvest_type() {
		return invest_type;
	}

	public void setInvest_type(String invest_type) {
		this.invest_type = invest_type;
	}
	
}
