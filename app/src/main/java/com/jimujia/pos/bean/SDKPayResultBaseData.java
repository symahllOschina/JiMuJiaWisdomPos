package com.jimujia.pos.bean;

import java.io.Serializable;

/**
 * Time: 2019/10/18
 * Author:Administrator
 * Description: SDK支付返回的公共参数
 */
public class SDKPayResultBaseData implements Serializable {


    /**
     * 金额 000000000001
     */
    private String amount;

    /**
     * 消费返回银行卡号6226200102484210
     * 微信扫码支付返回订单号（微信返回订单号）
     * 支付宝扫码支付返回订单号（支付宝返回订单号）
     */
    private String cardNo;

    /**
     * 终端号 61715018
     */
    private String terminalId;

    /**
     * 凭证号（流水号） 000049
     */
    private String traceNo;

    /**
     * 批次号 000002
     */
    private String batchNo;

    /**
     * 商户号 818791070130037
     */
    private String merchantId;

    /**
     * 参考号 162147192220
     */
    private String referenceNo;

    /**
     * 日期 1016
     */
    private String date;

    /**
     * 时间 214738
     */
    private String time;

    /**
     * 卡类型 （如：借记卡，信用卡）
     */
    private String type;

    /**
     * 发卡行 民生银行
     */
    private String issue;

    /**
     * 版本号 1.0 .7
     */
    private String version;

    /**
     * 商户名称 西安市长安区金钥匙房屋中介所
     */
    private String merchantName;

    /**
     * 交易类型 消费 微信消费 支付宝消费 消费撤销 微信退款 支付宝退款
     */
    private String transactionType;

    public SDKPayResultBaseData() {
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}
