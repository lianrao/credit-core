package com.wanda.credit.ds.dao.domain;

import com.wanda.credit.base.domain.BaseDomain;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;

/**
 * @author shiwei
 * @version $$Id: AllAuthenBankCard, V 0.1 2017/10/25 15:34 shiwei Exp $$
 */
@Entity
@Table(name = "T_DS_ALL_AUTHENBANKCARD")
@SequenceGenerator(name="SEQ_T_DS_ALL_AUTHENBANKCARD",sequenceName="SEQ_T_DS_ALL_AUTHENBANKCARD",allocationSize=1)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AllAuthenBankCard extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_T_DS_ALL_AUTHENBANKCARD")
    @Column(name = "ID", unique = true, nullable = false)
    private Long id ;
    private String trade_id;
    private String typeno;
    private String name;
    private String cardno;
    private String mobile;
    private String cardid;
    private String CARD_TYPE;
    private String VALID_DATE_YEAR;
    private String VALID_DATE_MONTH;
    private String VALID_CVV2NO;
    private String BANK_ID;
    private String BANK_DESC;
    private String syscode;
    private String sysmsg;
    private String seq;
    private String respdesc;
    private String respcode;
    private String ds_id;
    private String certType;
    private String req_values_md5;

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

    public String getTypeno() {
        return typeno;
    }

    public void setTypeno(String typeno) {
        this.typeno = typeno;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getCARD_TYPE() {
        return CARD_TYPE;
    }

    public void setCARD_TYPE(String CARD_TYPE) {
        this.CARD_TYPE = CARD_TYPE;
    }

    public String getVALID_DATE_YEAR() {
        return VALID_DATE_YEAR;
    }

    public void setVALID_DATE_YEAR(String VALID_DATE_YEAR) {
        this.VALID_DATE_YEAR = VALID_DATE_YEAR;
    }

    public String getVALID_DATE_MONTH() {
        return VALID_DATE_MONTH;
    }

    public void setVALID_DATE_MONTH(String VALID_DATE_MONTH) {
        this.VALID_DATE_MONTH = VALID_DATE_MONTH;
    }

    public String getVALID_CVV2NO() {
        return VALID_CVV2NO;
    }

    public void setVALID_CVV2NO(String VALID_CVV2NO) {
        this.VALID_CVV2NO = VALID_CVV2NO;
    }

    public String getBANK_ID() {
        return BANK_ID;
    }

    public void setBANK_ID(String BANK_ID) {
        this.BANK_ID = BANK_ID;
    }

    public String getBANK_DESC() {
        return BANK_DESC;
    }

    public void setBANK_DESC(String BANK_DESC) {
        this.BANK_DESC = BANK_DESC;
    }

    public String getSyscode() {
        return syscode;
    }

    public void setSyscode(String syscode) {
        this.syscode = syscode;
    }

    public String getSysmsg() {
        return sysmsg;
    }

    public void setSysmsg(String sysmsg) {
        this.sysmsg = sysmsg;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getRespdesc() {
        return respdesc;
    }

    public void setRespdesc(String respdesc) {
        this.respdesc = respdesc;
    }

    public String getRespcode() {
        return respcode;
    }

    public void setRespcode(String respcode) {
        this.respcode = respcode;
    }

    public String getDs_id() {
        return ds_id;
    }

    public void setDs_id(String ds_id) {
        this.ds_id = ds_id;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getReq_values_md5() {
        return req_values_md5;
    }

    public void setReq_values_md5(String req_values_md5) {
        this.req_values_md5 = req_values_md5;
    }
}
