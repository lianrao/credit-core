/**   
* @Description: 启信宝根据企业名称关键字和注册号查询企业信息列表 
* @author xiaobin.hou  
* @date 2016年12月21日 上午9:30:22 
* @version V1.0   
*/
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

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "T_DS_QXB_CORP_LIST",schema="CPDB_DS")
@SequenceGenerator(name="SEQ_T_DS_QXB_CORP_LIST",sequenceName="SEQ_T_DS_QXB_CORP_LIST")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CorpListPojo extends BaseDomain {
	
	private static final long serialVersionUID = -1255548077696445733L;
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_T_DS_QXB_CORP_LIST")  
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String keyword;
	private String name;
	private String item_id;
	private String start_date;
	private String oper_name;
	private String reg_no;
	private String credit_no;
	
	
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
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getOper_name() {
		return oper_name;
	}
	public void setOper_name(String oper_name) {
		this.oper_name = oper_name;
	}
	public String getReg_no() {
		return reg_no;
	}
	public void setReg_no(String reg_no) {
		this.reg_no = reg_no;
	}
	public String getCredit_no() {
		return credit_no;
	}
	public void setCredit_no(String credit_no) {
		this.credit_no = credit_no;
	}
	
	

}
