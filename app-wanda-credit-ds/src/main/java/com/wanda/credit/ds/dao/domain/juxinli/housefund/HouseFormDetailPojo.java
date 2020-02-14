/**   
 * @Description: 不同城市公积金申请表单详细信息 
 * @author xiaobin.hou  
 * @date 2016年5月25日 下午2:53:18 
 * @version V1.0   
 */
package com.wanda.credit.ds.dao.domain.juxinli.housefund;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @author ou.guohao
 *
 */
@Entity
@Table(name = "CPDB_DS.t_ds_jxl_housing_login_detail")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HouseFormDetailPojo extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private String seqId;
	private String parameterName;
	private String parameterCode;
	private String parameterMessage;
	private String parameterErrMessage;
	private String parameterType;
	private String orderby;
	private String status;
	private String grabType;
	private String category;
	private Date create_time;
	private Date update_time;
	private HouseFormPojo houseForm;

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

	@Column(name = "parameter_name")
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	@Column(name = "parameter_code")
	public String getParameterCode() {
		return parameterCode;
	}

	public void setParameterCode(String parameterCode) {
		this.parameterCode = parameterCode;
	}

	@Column(name = "parameter_message")
	public String getParameterMessage() {
		return parameterMessage;
	}

	public void setParameterMessage(String parameterMessage) {
		this.parameterMessage = parameterMessage;
	}

	@Column(name = "parameter_err_message")
	public String getParameterErrMessage() {
		return parameterErrMessage;
	}

	public void setParameterErrMessage(String parameterErrMessage) {
		this.parameterErrMessage = parameterErrMessage;
	}

	@Column(name = "parameter_type")
	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "grab_type")
	public String getGrabType() {
		return grabType;
	}

	public void setGrabType(String grabType) {
		this.grabType = grabType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	@LazyToOne(LazyToOneOption.FALSE)
	@ManyToOne
	@JoinColumn(name = "fk_seqid", nullable = false)
	public HouseFormPojo getHouseForm() {
		return houseForm;
	}

	public void setHouseForm(HouseFormPojo houseForm) {
		this.houseForm = houseForm;
	}

}
