package com.wanda.credit.ds.dao.domain;

import java.sql.Timestamp;

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
 * @author wj
 */
@Entity
@Table(name = "T_DS_ZS_CorpBasicAuthen")
@SequenceGenerator(name="SEQ_T_DS_ZS_CorpBasicAuthen",sequenceName="SEQ_T_DS_ZS_CorpBasicAuthen",allocationSize=1)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ZS_CorpBasicAuthen extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_T_DS_ZS_CorpBasicAuthen")
    @Column(name = "ID", unique = true, nullable = false)
//    id,trade_id,entname,entmark,cardno,name, to_char(substr(content,1,1000)),create_date,update_date
    private Long id ;
    private String trade_id;
    private String name;
    private String cardno;
    private String entname;
    private String entmark;
    private String content;
    private Timestamp create_date;
    private Timestamp update_date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

	public String getEntname() {
		return entname;
	}

	public void setEntname(String entname) {
		this.entname = entname;
	}

	public String getEntmark() {
		return entmark;
	}

	public void setEntmark(String entmark) {
		this.entmark = entmark;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Timestamp create_date) {
		this.create_date = create_date;
	}

	public Timestamp getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(Timestamp update_date) {
		this.update_date = update_date;
	}

}
