package com.wanda.credit.ds.client.bairong.bean;

/**
 * @author shiwei
 * @version $$Id: ResData, V 0.1 2017/8/22 15:17 shiwei Exp $$
 */
public class ResData {
    private String swift_number;
    private Product product;
    private CheckFlag flag;

    public String getSwift_number() {
        return swift_number;
    }

    public void setSwift_number(String swift_number) {
        this.swift_number = swift_number;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public CheckFlag getFlag() {
        return flag;
    }

    public void setFlag(CheckFlag flag) {
        this.flag = flag;
    }
}
