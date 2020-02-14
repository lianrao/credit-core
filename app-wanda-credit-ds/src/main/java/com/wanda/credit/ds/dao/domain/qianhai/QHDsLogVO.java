package com.wanda.credit.ds.dao.domain.qianhai;

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
 * 前海 黑名单 信息
 **/
@Entity
@Table(name = "T_DS_QH_DSLOG")
@SequenceGenerator(name = "Seq_T_DS_QH_DSLOG", sequenceName = "Seq_T_DS_QH_DSLOG")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class QHDsLogVO extends BaseDomain {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String trade_id;
	private String cardNo;//身份证号
	private String ds_id;
	private String name;

	public QHDsLogVO() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_T_DS_QH_DSLOG")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getDs_id() {
		return ds_id;
	}

	public void setDs_id(String ds_id) {
		this.ds_id = ds_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(long id) {
		this.id = id;
	}

}
