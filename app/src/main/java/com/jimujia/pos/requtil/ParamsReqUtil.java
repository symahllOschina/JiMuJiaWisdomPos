package com.jimujia.pos.requtil;

import android.util.Log;

import com.jimujia.pos.Constants;
import com.jimujia.pos.bean.OrderDetailData;
import com.jimujia.pos.bean.OrderListReqData;
import com.jimujia.pos.bean.PayOrderDetailData;
import com.jimujia.pos.bean.PayOrderListReqData;
import com.jimujia.pos.bean.PayResultNoticeReqData;
import com.jimujia.pos.bean.PaymentNoReqData;
import com.jimujia.pos.bean.PosInitData;
import com.jimujia.pos.bean.RefundNoReqData;
import com.jimujia.pos.bean.RefundResultNoticeReqData;
import com.jimujia.pos.bean.StoreInfoReqData;
import com.jimujia.pos.utils.DateTimeUtil;

/**
 * 参数请求方法封装
 */
public class ParamsReqUtil {

    /**
     * 测试终端号
     */
    private static final String TERMINAL_ID = "PAWOAZLSAD";

    /**
     * 获取门店信息
     */
    public static StoreInfoReqData getStoreInfoReqData(PosInitData posInitData){
        StoreInfoReqData reqData = new StoreInfoReqData();
        reqData.setTerminal_id(posInitData.getTrmNo_pos());
        return reqData;
    }

    /**
     * 获取门店订单信息
     */
    public static OrderListReqData getOrderListReqData(PosInitData posInitData,int page,int pageCount,String startDate,String endDate,String orderId,String customer_name,String customer_phone){
        OrderListReqData reqData = new OrderListReqData();
        reqData.setPage(page+"");
        reqData.setPage_size(pageCount+"");
        reqData.setTerminal_id(posInitData.getTrmNo_pos());
        reqData.setPay_order_id(orderId);
        reqData.setCustomer_name(customer_name);
        reqData.setCustomer_phone(customer_phone);
        reqData.setStart_date(startDate);
        reqData.setEnd_date(endDate);
        reqData.setPay_order_status("");
        return reqData;
    }

    /**
     * 获取支付流水号
     */
    public static PaymentNoReqData getPaymentNoReqData(PosInitData posInitData, OrderDetailData order)
    {
        PaymentNoReqData reqData = new PaymentNoReqData();
        reqData.setTerminal_id(posInitData.getTrmNo_pos());
        reqData.setPay_order_id(order.getPay_order_id());
        return reqData;
    }

    /**
     * 订单支付结果通知
     */
    public static PayResultNoticeReqData paymentNotice(PosInitData posInitData, OrderDetailData order,
                                                       String payType,String total_feeStr,String paySerialNum,
                                                       String gateway_order_id, String refer_no,String trace_no,
                                                       String batch_no,String trade_status)
    {
        PayResultNoticeReqData reqData = new PayResultNoticeReqData();
        reqData.setTerminal_id(posInitData.getTrmNo_pos());
        reqData.setPay_order_id(order.getPay_order_id());
        reqData.setPayment_no(paySerialNum);
        reqData.setGateway_order_id(gateway_order_id);
        reqData.setRefer_no(refer_no);
        reqData.setTrace_no(trace_no);
        reqData.setBatch_no(batch_no);
        reqData.setTrade_amount(total_feeStr);
        //交易时间取系统时间
        String dateTime = DateTimeUtil.getSystemTime("yyyyMMddHHmmss");
        reqData.setTrade_time(dateTime);
        reqData.setTrade_type(payType);
        reqData.setTrade_status(trade_status);
        return reqData;
    }

    /**
     * 获取订单支付信息(可用于退单等)
     */
    public static PayOrderListReqData getPayOrderListReqData(PosInitData posInitData,int page,int pageCount,String type,String orderId,String customer_name,String customer_phone) {
        PayOrderListReqData reqData = new PayOrderListReqData();
        reqData.setPage(page + "");
        reqData.setPage_size(pageCount + "");
        reqData.setTerminal_id(posInitData.getTrmNo_pos());
        reqData.setType(type);
        reqData.setPay_order_id(orderId);
        reqData.setCustomer_name(customer_name);
        reqData.setCustomer_phone(customer_phone);
        return reqData;

    }

    /**
     * 获取退款流水号
     */
    public static RefundNoReqData getRefundNoReqData(PosInitData posInitData, PayOrderDetailData order)
    {
        RefundNoReqData reqData = new RefundNoReqData();
        reqData.setTerminal_id(posInitData.getTrmNo_pos());
        reqData.setPayment_no(order.getPayment_no());
        return reqData;
    }

    /**
     * 订单退款结果通知
     */
    public static RefundResultNoticeReqData refundNotice(PosInitData posInitData, PayOrderDetailData order,
                                                          String paySerialNum,String trade_status)
    {
        RefundResultNoticeReqData reqData = new RefundResultNoticeReqData();
        reqData.setTerminal_id(posInitData.getTrmNo_pos());
        reqData.setOriginal_payment_no(order.getPayment_no());
        reqData.setPay_order_id(order.getPay_order_id());
        //退款单号
        reqData.setRefund_no(paySerialNum);
        reqData.setGateway_order_id("");
        reqData.setTrace_no("");
        reqData.setBatch_no("");
        Log.e("订单金额",String.valueOf(order.getTrade_amount()));
        reqData.setTrade_amount(String.valueOf(order.getTrade_amount()));
        Log.e("通知参数金额",reqData.getTrade_amount());
        //交易时间取系统时间
        String dateTime = DateTimeUtil.getSystemTime("yyyyMMddHHmmss");
        reqData.setTrade_time(dateTime);
        reqData.setTrade_type(order.getTrade_type());
        reqData.setTrade_status(trade_status);



        return reqData;
    }
}
