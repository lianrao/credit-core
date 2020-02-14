package com.wanda.credit.ds.client.guozt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>批量返回
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="queryBatchReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "queryBatchReturn"
})
@XmlRootElement(name = "queryBatchResponse")
public class QueryBatchResponse {

    @XmlElement(required = true)
    protected String queryBatchReturn;

    /**
     * queryBatchReturn
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryBatchReturn() {
        return queryBatchReturn;
    }

    /**
     * queryBatchReturn
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryBatchReturn(String value) {
        this.queryBatchReturn = value;
    }

}
