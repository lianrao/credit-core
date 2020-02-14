/**   
 * @Description: 商汤身份核验 - 图片上传
 * @author xiaobin.hou  
 * @date 2016年11月10日 上午11:26:23 
 * @version V1.0   
 */
package com.wanda.credit.ds.dao.domain.phjr;

import java.util.Date;

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
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_MK.T_ETL_ST_PHOTOAUTH")
@SequenceGenerator(name = "SEQ_T_ETL_ST_PHOTOAUTH", sequenceName = "CPDB_MK.SEQ_T_ETL_ST_PHOTOAUTH", schema = "CPDB_MK")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class STFileAuth extends BaseDomain {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_ETL_ST_PHOTOAUTH")
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String mh_busno;
	private String st_id1;
	private String st_id2;
	private String auth_result;
	private String st_rquestid;
	private Date create_date;
	private Date update_date;
	
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
	public String getMh_busno() {
		return mh_busno;
	}
	public void setMh_busno(String mh_busno) {
		this.mh_busno = mh_busno;
	}
	public String getSt_id1() {
		return st_id1;
	}
	public void setSt_id1(String st_id1) {
		this.st_id1 = st_id1;
	}
	public String getSt_id2() {
		return st_id2;
	}
	public void setSt_id2(String st_id2) {
		this.st_id2 = st_id2;
	}
	public String getAuth_result() {
		return auth_result;
	}
	public void setAuth_result(String auth_result) {
		this.auth_result = auth_result;
	}
	public String getSt_rquestid() {
		return st_rquestid;
	}
	public void setSt_rquestid(String st_rquestid) {
		this.st_rquestid = st_rquestid;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
	

	

}
