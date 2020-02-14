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
@Table(name = "T_DS_QXB_Partners_real")
@SequenceGenerator(name = "SEQ_T_DS_QXB_Partners_real", sequenceName = "SEQ_T_DS_QXB_Partners_real")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Partners_real extends QxbBaseDomain {
	private static final long serialVersionUID = 1L;
	
	private long id;
	
	private long partnerid;
	private String real_capi;
	private String invest_type;
	private String real_capi_date;


	public Partners_real() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_QXB_Partners_real")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReal_capi() {
		return real_capi;
	}

	public void setReal_capi(String real_capi) {
		this.real_capi = real_capi;
	}

	public String getReal_capi_date() {
		return real_capi_date;
	}

	public void setReal_capi_date(String real_capi_date) {
		this.real_capi_date = real_capi_date;
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
