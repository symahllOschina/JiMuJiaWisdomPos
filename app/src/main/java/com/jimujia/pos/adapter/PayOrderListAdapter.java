package com.jimujia.pos.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jimujia.pos.Constants;
import com.jimujia.pos.R;
import com.jimujia.pos.bean.PayOrderDetailData;
import com.jimujia.pos.utils.DecimalUtil;
import com.jimujia.pos.utils.Utils;

import java.util.List;

/**
 *  首页列表Adapter
 */
public class PayOrderListAdapter extends BaseAdapter{


    private Context context;
    private List<PayOrderDetailData> list;
    private LayoutInflater inflater;

    public PayOrderListAdapter(Context context, List<PayOrderDetailData> list) {
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
        TextView tvTradeAmount;
        /**
         * 客户地址
         */
        TextView tvDecorateAddress;
        TextView tvProductName;
        TextView tvOrderTime;
        ImageView imgTradeType;
    }

    @Override
    public View getView(int position, View subView, ViewGroup parent) {
        PayOrderDetailData order = list.get(position);
        ViewHolder vh = null;
        if(subView == null){
            subView = inflater.inflate(R.layout.pay_order_list_item,null);
            vh = new ViewHolder();
            vh.tvReceiptTypeName = subView.findViewById(R.id.pay_order_list_item_tvReceiptTypeName);
            vh.tvPayOrderStatus = subView.findViewById(R.id.pay_order_list_item_tvPayOrderStatus);
            vh.tvCustomerName = subView.findViewById(R.id.pay_order_list_item_tvCustomerName);
            vh.tvCustomerPhone = subView.findViewById(R.id.pay_order_list_item_tvCustomerPhone);
            vh.tvTradeAmount = subView.findViewById(R.id.pay_order_list_item_tvTradeAmount);
            vh.tvDecorateAddress = subView.findViewById(R.id.pay_order_list_item_tvDecorateAddress);
            vh.tvProductName  = subView.findViewById(R.id.pay_order_list_item_tvProductName);
            vh.tvOrderTime = subView.findViewById(R.id.pay_order_list_item_tvOrderTime);
            vh.imgTradeType = subView.findViewById(R.id.pay_order_list_item_imgTradeType);
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
        //交易类型 0-收款 1-退款
        String typeStr = order.getType();
        //支付状态 0、未支付 1、支付成功 2、支付失败 3、结果未知 (注:交易状态如果未知,则需要手动去查询状态)
        String tradeStatusStr = order.getTrade_status();

        String tradeStatus = context.getResources().getString(R.string.unknown);
        if(Utils.isNotEmpty(typeStr)){
            if("0".equals(typeStr)){
                if(Utils.isNotEmpty(tradeStatusStr)){
                    if("0".equals(tradeStatusStr)){
                        tradeStatus = "未支付";
                        vh.tvPayOrderStatus.setTextColor(ContextCompat.getColor(context,R.color.grey_666666));
                    }else if("1".equals(tradeStatusStr)){
                        tradeStatus = context.getResources().getString(R.string.payment_success);
                        vh.tvPayOrderStatus.setTextColor(ContextCompat.getColor(context,R.color.green_34d390));
                    }else if("2".equals(tradeStatusStr)){
                        tradeStatus = context.getResources().getString(R.string.payment_fail);
                        vh.tvPayOrderStatus.setTextColor(ContextCompat.getColor(context,R.color.red_FF4400));
                    }
                }
            }else if("1".equals(typeStr)){

                tradeStatus = context.getResources().getString(R.string.refund_success);
                vh.tvPayOrderStatus.setTextColor(ContextCompat.getColor(context,R.color.green_34d390));

            }
        }
        vh.tvPayOrderStatus.setText(tradeStatus);
        //客户名称
        String customerName = order.getCustomer_name();
        vh.tvCustomerName.setText(customerName);
        //客户电话
        String customerPhone = order.getCustomer_phone();
        vh.tvCustomerPhone.setText(customerPhone);
        //交易金额
        Integer tradeAmountStr = order.getTrade_amount();
        Log.e("金额",String.valueOf(tradeAmountStr));
        String tradeAmount = "";
        if(Utils.isNotEmpty(String.valueOf(tradeAmountStr))){
            String goodsPriceStr = DecimalUtil.StringToPrice(String.valueOf(tradeAmountStr));
            tradeAmount = DecimalUtil.branchToElement(goodsPriceStr);

        }
        vh.tvTradeAmount.setText(String.format(context.getResources().getString(R.string.trade_amount), tradeAmount));
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
        //支付时间
        String payTimeStr = order.getTrade_time();
        String payTime = "";
        if(Utils.isNotEmpty(payTimeStr)){
//            payTime = DateTimeUtil.stampToFormatDate(Long.parseLong(payTimeStr), "yyyy-MM-dd HH:mm:ss");
            payTime = payTimeStr;
        }
        vh.tvOrderTime.setText(payTime);

        //交易类型
        String tradeTypeStr = order.getTrade_type();
        if(Utils.isNotEmpty(tradeTypeStr)){
            if(tradeTypeStr.equals(Constants.PAYTYPE_BANK)){
                vh.imgTradeType.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.bank_log_icon));
            }else if(tradeTypeStr.equals(Constants.PAYTYPE_WX)){
                vh.imgTradeType.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.wxin_log_icon));
            }else if(tradeTypeStr.equals(Constants.PAYTYPE_ALI)){
                vh.imgTradeType.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ali_log_icon));
            }else{
                vh.imgTradeType.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.unknown_icon));
            }
        }else{
            vh.imgTradeType.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.unknown_icon));
        }


        return subView;
    }
}
