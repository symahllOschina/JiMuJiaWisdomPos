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
import com.jimujia.pos.bean.PayWayBean;

import java.util.List;

/**
 * 支付类型Adapter
 */
public class PayTypeGridViewAdapter extends BaseAdapter {


    private Context context;
    private List<PayWayBean> list;
    private LayoutInflater inflater;

    public PayTypeGridViewAdapter(Context context, List<PayWayBean> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
        ImageView img;
        TextView textView;
    }

    @Override
    public View getView(int position, View subView, ViewGroup parent) {
        ViewHolder vh = null;
        if(subView == null){
            subView = inflater.inflate(R.layout.pay_type_list_item, null);
            vh = new ViewHolder();
            vh.img =  subView.findViewById(R.id.pay_type_item_imgView);
            vh.textView = subView.findViewById(R.id.pay_type_item_textView);
            subView.setTag(vh);
        }else{
            vh = (ViewHolder) subView.getTag();
        }
        vh.img.setImageDrawable(ContextCompat.getDrawable(context,list.get(position).getImg()));
        vh.textView.setText(list.get(position).getText());
        return subView;
    }
}
