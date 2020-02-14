package com.wanda.credit.ds.client.bairong.bean;

/**
 * @author shiwei
 * @version $$Id: Product, V 0.1 2017/8/22 15:19 shiwei Exp $$
 */
public class Product {
    private String serialNo;
    private String result;
    private String msg;
    private int costTime;
    private String value;
    private String status;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
