package com.wanda.credit.ds.dao.domain.huifa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;
//个人、企业精确查询输入信息
@Entity
@Table(name = "T_DS_HF_INPUT_INFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PreciseQueryInputInfo extends BaseDomain{
	private static final long serialVersionUID = 1L;
	private String id;               //主键id
	private String trade_id;         //产品编号
    private String inputName;        //输入姓名
    private String inputIdCard;      //输入身份证号
    private int inputCurrentPage;    //输入查询页号
    private int inputPageSize;       //输入查询信息条数
    private String inputSourcet;     //输入查询信息类型
    private String success;          //查询成功与否
    private String message;          //返回code
    
	public PreciseQueryInputInfo() {
		super();
	}
	
	
	public PreciseQueryInputInfo(String trade_id, String inputName,
			String inputIdCard, int inputCurrentPage, int inputPageSize,
			String inputSourcet, String success, String message) {
		super();
		this.trade_id = trade_id;
		this.inputName = inputName;
		this.inputIdCard = inputIdCard;
		this.inputCurrentPage = inputCurrentPage;
		this.inputPageSize = inputPageSize;
		this.inputSourcet = inputSourcet;
		this.success = success;
		this.message = message;
	}


	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false, length = 32)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public String getInputIdCard() {
		return inputIdCard;
	}

	public void setInputIdCard(String inputIdCard) {
		this.inputIdCard = inputIdCard;
	}

	public int getInputCurrentPage() {
		return inputCurrentPage;
	}

	public void setInputCurrentPage(int inputCurrentPage) {
		this.inputCurrentPage = inputCurrentPage;
	}

	public int getInputPageSize() {
		return inputPageSize;
	}

	public void setInputPageSize(int inputPageSize) {
		this.inputPageSize = inputPageSize;
	}

	public String getInputSourcet() {
		return inputSourcet;
	}

	public void setInputSourcet(String inputSourcet) {
		this.inputSourcet = inputSourcet;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "PreciseQueryInputInfo [inputName=" + inputName
				+ ", inputIdCard=" + inputIdCard + ", inputCurrentPage="
				+ inputCurrentPage + ", inputPageSize=" + inputPageSize
				+ ", inputSourcet=" + inputSourcet + ", success=" + success
				+ ", message=" + message + "]";
	}
}
