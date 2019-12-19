package com.jimujia.pos.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 查询门店订单信息返回
 */
public class OrderListResData implements Serializable {

    private List<OrderDetailData> order_list;

    private PaginatedData paginated;

    public OrderListResData() {
    }

    public List<OrderDetailData> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<OrderDetailData> order_list) {
        this.order_list = order_list;
    }

    public PaginatedData getPaginated() {
        return paginated;
    }

    public void setPaginated(PaginatedData paginated) {
        this.paginated = paginated;
    }
}
