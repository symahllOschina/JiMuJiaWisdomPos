package com.jimujia.pos.bean;

import java.io.Serializable;

/**
 * 门店订单详情
 */
public class OrderDetailData implements Serializable {

    /**
     * 最终支付金额 order_amount = discount_amount+paid_amount+debt_amount
     */

    /**
     *积木家给门店分配的唯一编号
     */
    private String store_id;
    /**
     *门店名称
     */
    private String store_name;
    /**
     *客户姓名
     */
    private String customer_name;
    /**
     *收款单号(积木家给客户生成的订单编号,需要保持唯一性)
     */
    private String pay_order_id;
    /**
     *订单金额(单位为分)
     */
    private Integer order_amount;
    /**
     *--待支付金额(单位为分)--
     */
    private Integer debt_amount;
    /**
     *优惠金额(单位为分)
     */
    private Integer discount_amount;
    /**
     *订单创建时间(格式为年月日时分秒 例:2019/12/12 12:30:20）
     */
    private String order_time;
    /**
     *上次支付时间(格式为年月日时分秒 例:2019/12/12 12:30:20）
     */
    private String last_payment_time;
    /**
     *订单状态（0-未支付 1-部分支付 2-支付完成 ）
     */
    private String pay_order_status;
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
    /**
     *已支付金额(单位为分)
     */
    private Integer paid_amount;
    /**
     *是否允许退款 (0-不允许 1-允许) 注:已关闭订单不能支付 已完成订单不能退款
     */
    private String is_refund;
    /**
     *关闭状态 0-正常 1-已关闭
     */
    private String is_closed;

    public OrderDetailData() {
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getPay_order_id() {
        return pay_order_id;
    }

    public void setPay_order_id(String pay_order_id) {
        this.pay_order_id = pay_order_id;
    }

    public Integer getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(Integer order_amount) {
        this.order_amount = order_amount;
    }

    public Integer getDebt_amount() {
        return debt_amount;
    }

    public void setDebt_amount(Integer debt_amount) {
        this.debt_amount = debt_amount;
    }

    public Integer getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Integer discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getLast_payment_time() {
        return last_payment_time;
    }

    public void setLast_payment_time(String last_payment_time) {
        this.last_payment_time = last_payment_time;
    }

    public String getPay_order_status() {
        return pay_order_status;
    }

    public void setPay_order_status(String pay_order_status) {
        this.pay_order_status = pay_order_status;
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

    public Integer getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(Integer paid_amount) {
        this.paid_amount = paid_amount;
    }

    public String getIs_refund() {
        return is_refund;
    }

    public void setIs_refund(String is_refund) {
        this.is_refund = is_refund;
    }

    public String getIs_closed() {
        return is_closed;
    }

    public void setIs_closed(String is_closed) {
        this.is_closed = is_closed;
    }
}
