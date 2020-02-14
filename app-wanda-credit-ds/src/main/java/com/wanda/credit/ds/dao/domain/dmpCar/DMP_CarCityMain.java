package com.wanda.credit.ds.dao.domain.dmpCar;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_DMP_WEIZHANGCITYQY",schema="CPDB_DS")
@SequenceGenerator(name = "Seq_T_DS_DMP_WEIZHANGCITYQY", sequenceName = "Seq_T_DS_DMP_WEIZHANGCITYQY")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("dmpCarmain")
public class DMP_CarCityMain extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	 private long id;
	 private String trade_id ;
	 private String province;
	 private String code;
	 private String msg;
	 private String seq;
	 private String error_code;
	private List<DMP_CarDetail> dataList;

	public DMP_CarCityMain() {
		super();
	}

	public DMP_CarCityMain(long id, String trade_id, String province, String code, String msg, String seq,
			 List<DMP_CarDetail> dataList) {
		super();
		this.id = id;
		this.trade_id = trade_id;
		this.province = province;
		this.code = code;
		this.msg = msg;
		this.seq = seq;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Seq_T_DS_DMP_WEIZHANGCITYQY")
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

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dmpCarmain", cascade = CascadeType.ALL)
	public List<DMP_CarDetail> getDataList() {
		return dataList;
	}

	public void setDataList(List<DMP_CarDetail> dataList) {
		this.dataList = dataList;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
}
