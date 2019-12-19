package com.jimujia.pos.bean;

/**
 * Time: 2019/11/9
 * Author:Administrator
 * Description: 生成支付流水号请求参数
 */
public class PaymentNoReqData {

    /**
     * 门店智能终端唯一标识(需要积木家系统维护终端与门店对应关系)
     */
    private String terminal_id;


    /**
     * 收款单号
     */
    private String pay_order_id;


    public PaymentNoReqData() {
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
}
