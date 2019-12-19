package com.jimujia.pos.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 查询门店订单信息返回
 */
public class PayOrderListResData implements Serializable {

    private List<PayOrderDetailData> payment_list;

    private PaginatedData paginated;

    public PayOrderListResData() {
    }

    public List<PayOrderDetailData> getPayment_list() {
        return payment_list;
    }

    public void setPayment_list(List<PayOrderDetailData> payment_list) {
        this.payment_list = payment_list;
    }

    public PaginatedData getPaginated() {
        return paginated;
    }

    public void setPaginated(PaginatedData paginated) {
        this.paginated = paginated;
    }
}
