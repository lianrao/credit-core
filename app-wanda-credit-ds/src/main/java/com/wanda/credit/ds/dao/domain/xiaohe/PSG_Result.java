package com.wanda.credit.ds.dao.domain.xiaohe;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

//查询成功与否
@Entity
@Table(name = "T_DS_JZ_RESULT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("result")
public class PSG_Result extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String commplace;// 常住地
	private String sex;// 性别
	private int age;// 年龄
	private String issuing;// 发证地
	private String period;// 分析周期例如本次分析12个月
	private Psg_AnalyseReport analyseReport;

	public PSG_Result() {
		super();
	}

	public PSG_Result(String id, String commplace, String sex, int age, String issuing, String period) {
		super();
		this.id = id;
		this.commplace = commplace;
		this.sex = sex;
		this.age = age;
		this.issuing = issuing;
		this.period = period;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
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

	public String getCommplace() {
		return commplace;
	}

	public void setCommplace(String commplace) {
		this.commplace = commplace;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getIssuing() {
		return issuing;
	}

	public void setIssuing(String issuing) {
		this.issuing = issuing;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID")
	public Psg_AnalyseReport getAnalyseReport() {
		return analyseReport;
	}

	public void setAnalyseReport(Psg_AnalyseReport analyseReport) {
		this.analyseReport = analyseReport;
	}

	@Override
	public String toString() {
		return "PSG_Result [commplace=" + commplace + ", sex=" + sex + ", age=" + age + ", issuing=" + issuing
				+ ", period=" + period + "]";
	}
}
