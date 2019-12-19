package com.jimujia.pos.bean;

/**
 * Time: 2019/11/7
 * Author:Administrator
 * Description: 门店信息请求参数实体
 */
public class StoreInfoReqData {

    /**
     * 门店智能终端唯一标识(需要积木家系统维护终端与门店对应关系)
     */
    private String terminal_id;

    public StoreInfoReqData() {
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }
}
