package com.jimujia.pos.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Time: 2019/10/16
 * Author:Administrator
 * Description: 支付结果通知请求参数实体
 */
public class PayResultNoticeReqData extends LitePalSupport {


    /**
     * 本地保存数据库自增长表id
     */
    private int id;

    /**
     * 门店只能终端唯一标识(需要积木家系统维护终端与门店对应关系)
     */
    private String terminal_id;

    /**
     * 积木家给客户生成的支付订单编号
     */
    private String pay_order_id;

    /**
     * 亿联付款流水单号(由APP端生成)
     */
    private String payment_no;

    /**
     * 通道订单号(由微信、支付宝或者银联生成)支付通道返回)
     */
    private String gateway_order_id;

    /**
     * 参考号 退款参数
     */
    private String refer_no;

    /**
     * 凭证号(支付通道返回)
     */
    private String trace_no;


    /**
     * 批次号(支付通道返回)
     */
    private String batch_no;


    /**
     * 交易金额(单位为分: 如10000)
     */
    private String trade_amount;


    /**
     * 交易时间(年月日时分秒 例: 2019/09/12 12:23:20）
     */
    private String trade_time;

    /**
     * 支付方式(1:银联 2:微信 3:支付宝)
     */
    private String trade_type;


    /**
     * 交易状态（0:未支付 1:支付成功 2:支付失败 3:结果未知）
     */
    private String trade_status;

    public PayResultNoticeReqData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
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

    public String getTrade_amount() {
        return trade_amount;
    }

    public void setTrade_amount(String trade_amount) {
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
}
