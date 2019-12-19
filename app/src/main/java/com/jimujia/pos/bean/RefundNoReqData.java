package com.jimujia.pos.bean;

/**
 * Time: 2019/11/9
 * Author:Administrator
 * Description: 生成退款流水号请求参数
 */
public class RefundNoReqData {

    /**
     * 门店智能终端唯一标识(需要积木家系统维护终端与门店对应关系)
     */
    private String terminal_id;


    /**
     * 支付流水号
     */
    private String payment_no;


    public RefundNoReqData() {
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }

    public String getPayment_no() {
        return payment_no;
    }

    public void setPayment_no(String payment_no) {
        this.payment_no = payment_no;
    }
}
