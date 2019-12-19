package com.jimujia.pos.bean;

/**
 * Time: 2019/11/9
 * Author:Administrator
 * Description: 生成支付流水号返回
 */
public class PaymentNoResData {

    /**
     * 收款单号生成的支付流水号
     */
    private String payment_no;


    public PaymentNoResData() {
    }

    public String getPayment_no() {
        return payment_no;
    }

    public void setPayment_no(String payment_no) {
        this.payment_no = payment_no;
    }
}
