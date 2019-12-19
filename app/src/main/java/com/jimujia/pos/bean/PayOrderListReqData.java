package com.jimujia.pos.bean;

/**
 * Time: 2019/10/18
 * Author:Administrator
 * Description: 支付订单列表请求参数
 */
public class PayOrderListReqData {


    /**
     * 分页下标 页码(默认：1)
     */
    private String page;

    /**
     * 分页列表数 ,每页记录数(默认：10)
     */
    private String page_size;

    /**
     *终端编号.门店智能终端唯一标识（需要积木家系统维护终端与门店对应关系）
     */
    private String terminal_id;

    /**
     * 类型 0-收款 1-退款
     */
    private String type;


    /**
     * (收款)订单号.积木家给客户生成的订单编号 (需支持模糊查询)
     */
    private String pay_order_id;


    /**
     * 客户姓名
     */
    private String customer_name;


    /**
     * 客户手机号
     */
    private String customer_phone;



    public PayOrderListReqData() {
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPage_size() {
        return page_size;
    }

    public void setPage_size(String page_size) {
        this.page_size = page_size;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
