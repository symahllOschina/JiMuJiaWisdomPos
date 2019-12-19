package com.jimujia.pos.bean;

import java.io.Serializable;

/**
 * Time: 2019/10/18
 * Author:Administrator
 * Description: SDK支付返回的公共参数
 */
public class SDKPayResultData extends SDKPayResultBaseData implements Serializable {


    /**
     * orderNumber = 201910162147290776171501878873
     * 银行卡消费返回订单号
     */
    private String orderNumber;

    /**
     * tuikuanNo = 000002000050134664463754285858,
     * 微信扫码支付返回
     */
    private String tuikuanNo;

    /**
     * wxOrderNumber = 201910162149413906171501835645
     * 微信扫码支付返回订单号
     */
    private String wxOrderNumber;

    /**
     * zfbOrderNumber = 201910181342202286171501846985
     * 支付宝扫码支付返回订单号
     */
    private String zfbOrderNumber;



    public SDKPayResultData() {
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTuikuanNo() {
        return tuikuanNo;
    }

    public void setTuikuanNo(String tuikuanNo) {
        this.tuikuanNo = tuikuanNo;
    }

    public String getWxOrderNumber() {
        return wxOrderNumber;
    }

    public void setWxOrderNumber(String wxOrderNumber) {
        this.wxOrderNumber = wxOrderNumber;
    }

    public String getZfbOrderNumber() {
        return zfbOrderNumber;
    }

    public void setZfbOrderNumber(String zfbOrderNumber) {
        this.zfbOrderNumber = zfbOrderNumber;
    }
}
