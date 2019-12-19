package com.jimujia.pos.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jimujia.pos.R;
import com.jimujia.pos.utils.Utils;


/**
 * 门店列表模糊查询条件筛选
 */
public class FuzzyQueryPopupWindow extends PopupWindow{

    private Context context;
    private String orderId;
    private String customerName;
    private String customerPhone;



    public FuzzyQueryPopupWindow(Context context,String orderId,String customerName,String customerPhone) {
        super(context);
        this.context = context;
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
    }

    private OnScreeningClickListener onScreeningClickListener;

    public void setOnScreeningClickListener(OnScreeningClickListener onScreeningClickListener){
        this.onScreeningClickListener = onScreeningClickListener;
    }

    public interface OnScreeningClickListener{
        public void screeningListener(String orderId,String customerName,String customerPhone);
    }

    public void showWindow() {
        View view = LayoutInflater.from(context).inflate(R.layout.fuzzy_query_layout,null);
        final ClearEditText etOrderId = view.findViewById(R.id.fuzzy_query_etOrderId);
        final ClearEditText etCustomerName = view.findViewById(R.id.fuzzy_query_etCustomerName);
        final ClearEditText etCustomerPhone = view.findViewById(R.id.fuzzy_query_etCustomerPhone);
        if(Utils.isNotEmpty(orderId)){
            etOrderId.setText(orderId);
        }
        if(Utils.isNotEmpty(customerName)){
            etCustomerName.setText(customerName);
        }
        if(Utils.isNotEmpty(customerPhone)){
            etCustomerPhone.setText(customerPhone);
        }
        Button btCancel = view.findViewById(R.id.fuzzy_query_btCancel);
        Button btOk = view.findViewById(R.id.fuzzy_query_btOk);
        btCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        // 当月（不包含当天）
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderId = etOrderId.getText().toString().trim();
                customerName = etCustomerName.getText().toString().trim();
                customerPhone = etCustomerPhone.getText().toString().trim();
                onScreeningClickListener.screeningListener(orderId,customerName,customerPhone);
                // 销毁弹出框
                dismiss();
            }
        });
        // 设置SelectPicPopupWindow的View
        this.setContentView(view);
        // 设置SelectPicPopupWindow弹出窗口的宽
        this.setWidth(LinearLayout.LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗口的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗口可点击  点击空白处时，隐藏掉pop窗口,为true时可弹出软键盘
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗口动画效果
        this.setAnimationStyle(R.style.AnimTop);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗口的背景
        this.setBackgroundDrawable(dw);

    }


}
