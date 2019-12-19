package com.jimujia.pos.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.jimujia.pos.R;
import com.jimujia.pos.bean.OrderDetailData;
import com.jimujia.pos.utils.DecimalUtil;
import com.jimujia.pos.utils.Utils;

import java.util.List;

/**
 *  门店列表Adapter
 */
public class OrderListAdapter extends BaseAdapter{


    private Context context;
    private List<OrderDetailData> list;
    private LayoutInflater inflater;

    public OrderListAdapter(Context context, List<OrderDetailData> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{

        /**
         * 收款类型
         */
        TextView tvReceiptTypeName;
        /**
         * 收款状态、订单状态
         */
        TextView tvPayOrderStatus;
        TextView tvCustomerName;
        TextView tvCustomerPhone;
        /**
         * 待支付金额
         */
        TextView tvDebtAmount;
        /**
         * 客户地址
         */
        TextView tvDecorateAddress;
        TextView tvProductName;
        TextView tvOrderTime;
    }

    @Override
    public View getView(int position, View subView, ViewGroup parent) {
        OrderDetailData order = list.get(position);
        ViewHolder vh = null;
        if(subView == null){
            subView = inflater.inflate(R.layout.order_list_item,null);
            vh = new ViewHolder();
            vh.tvReceiptTypeName = subView.findViewById(R.id.order_list_item_tvReceiptTypeName);
            vh.tvPayOrderStatus = subView.findViewById(R.id.order_list_item_tvPayOrderStatus);
            vh.tvCustomerName = subView.findViewById(R.id.order_list_item_tvCustomerName);
            vh.tvCustomerPhone = subView.findViewById(R.id.order_list_item_tvCustomerPhone);
            vh.tvDebtAmount = subView.findViewById(R.id.order_list_item_tvDebtAmount);
            vh.tvDecorateAddress = subView.findViewById(R.id.order_list_item_tvDecorateAddress);
            vh.tvProductName  = subView.findViewById(R.id.order_list_item_tvProductName);
            vh.tvOrderTime = subView.findViewById(R.id.order_list_item_tvOrderTime);
            subView.setTag(vh);
        }else{
            vh = (ViewHolder) subView.getTag();

        }
        //收款类型
        String receiptTypeNameStr = order.getReceipt_type_name();
        String receiptTypeName = "";
        if(Utils.isNotEmpty(receiptTypeNameStr)){
            receiptTypeName = receiptTypeNameStr;
        }
        vh.tvReceiptTypeName.setText(receiptTypeName);
        //订单状态（0-未支付 1-部分支付 2-支付完成 ）
        String payOrderStatusStr= order.getPay_order_status();
        String payOrderStatus = context.getString(R.string.unknown);
        if(Utils.isNotEmpty(payOrderStatusStr)){
            if("0".equals(payOrderStatusStr)){
                payOrderStatus = context.getString(R.string.waiting_payment);
            }else if("1".equals(payOrderStatusStr)){
                payOrderStatus = context.getString(R.string.part_payment);
            }else if("2".equals(payOrderStatusStr)){
                payOrderStatus = context.getString(R.string.completed_ayment);
            }
        }
        vh.tvPayOrderStatus.setText(payOrderStatus);
        //客户名称
        String customerName = order.getCustomer_name();
        vh.tvCustomerName.setText(customerName);
        //客户电话
        String customerPhone = order.getCustomer_phone();
        vh.tvCustomerPhone.setText(customerPhone);
        //待支付金额
        Integer debtAmountStr = order.getDebt_amount();
        String debtAmount = "";
        if(Utils.isNotEmpty(String.valueOf(debtAmountStr))){
            debtAmount = DecimalUtil.branchToElement(String.valueOf(debtAmountStr));
        }
        vh.tvDebtAmount.setText("￥"+debtAmount);
        //客户地址地址
        String decorateAddress = order.getDecorate_address();
        vh.tvDecorateAddress.setText(decorateAddress);
        //套餐名称
        String productName = order.getProduct_name();
        String productNameStr = "";
        if(Utils.isNotEmpty(productName)){
            productNameStr = productName;
        }
        vh.tvProductName.setText(productNameStr);
        //订单创建时间
        String orderTimeStr = order.getOrder_time();
        vh.tvOrderTime.setText(orderTimeStr);

        return subView;
    }
}
