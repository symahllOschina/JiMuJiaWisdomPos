package com.jimujia.pos.bean;

import java.io.Serializable;

/**
 * 门店订单详情
 */
public class PayOrderDetailData implements Serializable {


    /**
     *  收款单号 对应订单支付结果中的pay_order_id.
     */
    private String pay_order_id;

    /**
     *  支付订单号.由APP端生成
     */
    private String payment_no;
    /**
     * 通道订单号
     */
    private String gateway_order_id;
    /**
     * 参考号
     */
    private String refer_no;
    /**
     * 凭证号
     */
    private String trace_no;
    /**
     * 批次号
     */
    private String batch_no;
    /**
     * 交易金额(单位为分)
     */
    private Integer trade_amount;
    /**
     * 交易时间
     */
    private String trade_time;
    /**
     * 支付方式 1、银联 2、微信 3、支付宝
     */
    private String trade_type;

    /**
     * 交易状态 0、未支付 1、支付成功 2、支付失败 3、结果未知 (注:交易状态如果未知,则需要手动去查询状态)
     */
    private String trade_status;

    /**
     * 0-收款 1-退款
     */
    private String type;

    /**
     * 是否允许退款 (0-不允许 1-允许) --收款流水状态
     */
    private Integer is_refund;


    /**
     * 是否已经退款(0-未退款 1-已退款) 收款流水状态
     */
    private Integer already_refund;


    /**
     * 原支付订单号
     */
    private String original_payment_no;

    /**
     *客户姓名
     */
    private String customer_name;

    /**
     *客户联系电话
     */
    private String customer_phone;

    /**
     *收款类型(名称:如 硬装订金、硬装首付款等)
     */
    private String receipt_type_name;
    /**
     *装修地址
     */
    private String decorate_address;
    /**
     *套餐名称
     */
    private String product_name;




    public PayOrderDetailData() {
    }


    public String getPay_order_id() {
        return pay_order_id;
    }

    public void setPay_order_id(String pay_order_id) {
        this.pay_order_id = pay_order_id;
    }

    public String getPayment_no() {
        return payment_no;
    }

    public void setPayment_no(String payment_no) {
        this.payment_no = payment_no;
    }

    public String getGateway_order_id() {
        return gateway_order_id;
    }

    public void setGateway_order_id(String gateway_order_id) {
        this.gateway_order_id = gateway_order_id;
    }

    public String getRefer_no() {
        return refer_no;
    }

    public void setRefer_no(String refer_no) {
        this.refer_no = refer_no;
    }

    public String getTrace_no() {
        return trace_no;
    }

    public void setTrace_no(String trace_no) {
        this.trace_no = trace_no;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public Integer getTrade_amount() {
        return trade_amount;
    }

    public void setTrade_amount(Integer trade_amount) {
        this.trade_amount = trade_amount;
    }

    public String getTrade_time() {
        return trade_time;
    }

    public void setTrade_time(String trade_time) {
        this.trade_time = trade_time;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getTrade_status() {
        return trade_status;
    }

    public void setTrade_status(String trade_status) {
        this.trade_status = trade_status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIs_refund() {
        return is_refund;
    }

    public void setIs_refund(Integer is_refund) {
        this.is_refund = is_refund;
    }

    public Integer getAlready_refund() {
        return already_refund;
    }

    public void setAlready_refund(Integer already_refund) {
        this.already_refund = already_refund;
    }

    public String getOriginal_payment_no() {
        return original_payment_no;
    }

    public void setOriginal_payment_no(String original_payment_no) {
        this.original_payment_no = original_payment_no;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getReceipt_type_name() {
        return receipt_type_name;
    }

    public void setReceipt_type_name(String receipt_type_name) {
        this.receipt_type_name = receipt_type_name;
    }

    public String getDecorate_address() {
        return decorate_address;
    }

    public void setDecorate_address(String decorate_address) {
        this.decorate_address = decorate_address;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}
