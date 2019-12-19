package com.jimujia.pos.bean;

import java.io.Serializable;

public class PaginatedData implements Serializable {

    /**
     * 总条数 "total":"1"
     */
    private String total;

    /**
     * 每页条数"page_size": "20",
     */
    private String page_size;

    /**
     * 更多数据"more": "0"
     */
    private String more;

    public PaginatedData() {
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPage_size() {
        return page_size;
    }

    public void setPage_size(String page_size) {
        this.page_size = page_size;
    }

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }
}
