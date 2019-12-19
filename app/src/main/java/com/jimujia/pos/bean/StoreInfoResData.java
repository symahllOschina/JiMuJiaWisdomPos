package com.jimujia.pos.bean;

import java.io.Serializable;

/**
 * Time: 2019/11/7
 * Author:Administrator
 * Description: 门店信息返回实体
 */
public class StoreInfoResData implements Serializable {

    /**
     * 门店编号
     */
    private String store_id;
    /**
     * 门店名称
     */
    private String store_name;
    /**
     * 终端名称
     */
    private String terminal_name;

    public StoreInfoResData() {
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

    public String getTerminal_name() {
        return terminal_name;
    }

    public void setTerminal_name(String terminal_name) {
        this.terminal_name = terminal_name;
    }
}
