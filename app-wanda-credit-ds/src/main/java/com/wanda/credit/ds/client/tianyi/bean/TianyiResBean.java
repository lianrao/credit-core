package com.wanda.credit.ds.client.tianyi.bean;


/**
 * @author shiwei
 * @version $$Id: TianyiResBean, V 0.1 2017/8/23 17:36 shiwei Exp $$
 */
public class TianyiResBean {
    private String code;
    private String msg;
    private ResData data;
    private String seq;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public ResData getData() {
        return data;
    }
    public void setData(ResData data) {
        this.data = data;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
