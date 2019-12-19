package com.jimujia.pos.bean;
/**
 * 查询门店订单信息请求实体
 */
public class OrderListReqData {

    /**
     * 分页下标
     */
    private String page;

    /**
     * 分页列表数
     */
    private String page_size;

    /**
     * 终端id
     */
    private String terminal_id;
    /**
     * 收款单号。积木家给客户生成的订单编号(需支持模糊查询)
     */
    private String pay_order_id;
    /**
     * 客户姓名(需支持模糊查询)
     */
    private String customer_name;
    /**
     * 客户联系电话(需支持模糊查询)
     */
    private String customer_phone;
    /**
     * 起始日期(yyyyMMdd)
     */
    private String start_date;
    /**
     * 截止日期(yyyyMMdd)   积木家后台默认 <= yyyyMMdd 23:59:59.999
     */
    private String end_date;
    /**
     * 订单状态 0-未支付 1-部分支付 2-支付完成 (不输入默认返回支付和部分支付的订单)
     * 需支持一次传入一个或多个状态，多个状态用逗号分隔.
     */
    private String pay_order_status;

    public OrderListReqData() {
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

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getPay_order_status() {
        return pay_order_status;
    }

    public void setPay_order_status(String pay_order_status) {
        this.pay_order_status = pay_order_status;
    }
}
