package com.jimujia.pos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jimujia.pos.activity.OrderListActivity;
import com.jimujia.pos.activity.PayOrderListActivity;
import com.jimujia.pos.bean.PosInitData;
import com.jimujia.pos.bean.StoreInfoResData;
import com.jimujia.pos.utils.MySerialize;
import com.jimujia.pos.utils.NetworkUtils;
import com.jimujia.pos.utils.SharedPreferencesUtil;
import com.jimujia.pos.utils.ToastUtil;
import com.jimujia.pos.utils.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;

@ContentView(R.layout.fragment_home)
public class MainHomeFragment extends BaseFragment implements View.OnClickListener{


    @ViewInject(R.id.home_tvTitle)
    TextView tvTitle;
    @ViewInject(R.id.home_tvStoreName)
    TextView tvStoreName;
    @ViewInject(R.id.home_tvTerminalName)
    TextView tvTerminalName;


    @ViewInject(R.id.home_menu_paymentLayout)
    RelativeLayout paymentLayout;
    @ViewInject(R.id.home_menu_paymentRecordLayout)
    RelativeLayout paymentRecordLayout;
    @ViewInject(R.id.home_menu_refundRocordLayout)
    RelativeLayout refundRocordLayout;


    /**
     * POS初始化信息
     */
    private PosInitData posInitData;
    private StoreInfoResData storeInfoResData;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG,"onViewCreated().........");

        tvTitle.setText(R.string.app_name);


        initListener();



    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume().........");
        try {
            posInitData =(PosInitData) MySerialize.deSerialization(MySerialize.getObject("posInitData", activity));
            storeInfoResData =(StoreInfoResData) MySerialize.deSerialization(MySerialize.getObject("storeInfo", activity));

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //从内存中取对象
            getLocalData();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //从内存中取对象
            getLocalData();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //从内存中取对象
            getLocalData();
        }
        Log.e("主界面onResume()：", "----------");
        Log.e("商户号：", posInitData.getMercId_pos());
        Log.e("终端号：", posInitData.getTrmNo_pos());
        Log.e("商户姓名：", posInitData.getMername_pos());

        setStoreName();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause().........");
    }

    private void initListener(){
        paymentLayout.setOnClickListener(this);
        paymentRecordLayout.setOnClickListener(this);
        refundRocordLayout.setOnClickListener(this);
    }


    /**
     * 从内存中取对象
     */
    private void getLocalData(){
        posInitData = new PosInitData();
        SharedPreferencesUtil posInitSP = new SharedPreferencesUtil(activity, "posInit");
        if(posInitSP.contain("posMercId")){

            posInitData.setMercId_pos((String)posInitSP.getSharedPreference("posMercId",""));
            posInitData.setTrmNo_pos((String) posInitSP.getSharedPreference("posTrmNo",""));
            posInitData.setMername_pos((String) posInitSP.getSharedPreference("posMername",""));
        }else{
            posInitData.setMercId_pos("");
            posInitData.setTrmNo_pos("");
            posInitData.setMername_pos("");
        }
        Log.e("主界面getPosInitData()：", "----------");
        Log.e("商户号：", posInitData.getMercId_pos());
        Log.e("终端号：", posInitData.getTrmNo_pos());
        Log.e("商户姓名：", posInitData.getMername_pos());
        storeInfoResData = new StoreInfoResData();
        SharedPreferencesUtil storeInfoSP = new SharedPreferencesUtil(activity, "storeInfo");
        if(storeInfoSP.contain("storeId")){

            storeInfoResData.setStore_id((String)storeInfoSP.getSharedPreference("storeId",""));
            storeInfoResData.setStore_name((String) storeInfoSP.getSharedPreference("storeName",""));
            storeInfoResData.setTerminal_name((String) storeInfoSP.getSharedPreference("terminalName",""));
        }else{
            storeInfoResData.setStore_id("");
            storeInfoResData.setStore_name("");
            storeInfoResData.setTerminal_name("");
        }
        Log.e("门店编号：", storeInfoResData.getStore_id());
        Log.e("门店名称：", storeInfoResData.getStore_name());
        Log.e("终端名称：", storeInfoResData.getTerminal_name());
    }

    private void setStoreName(){
        tvStoreName.setText("");
        tvTerminalName.setText("");
        String storeName = storeInfoResData.getStore_name();
        String terminalName = storeInfoResData.getTerminal_name();
        if(Utils.isNotEmpty(storeName)){
            tvStoreName.setText(storeName);
        }
        if(Utils.isNotEmpty(terminalName)){
            tvTerminalName.setText("—"+terminalName);
        }


    }


    private void sendMessage(int what,String text){
        Message msg = new Message();
        msg.what = what;
        msg.obj = text;
        handler.sendMessage(msg);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String errorJsonText = "";
            switch (msg.what){
                case NetworkUtils.MSG_WHAT_ONE:
                    String jsonStr=(String) msg.obj;

                    break;
                case NetworkUtils.MSG_WHAT_TWO:
                    String scanPayJsonStr=(String) msg.obj;

                    break;
                case NetworkUtils.MSG_WHAT_THREE:
                    String saveTestJsonStr=(String) msg.obj;

                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        //交易类型 tradeType：0-收款 1-退款
        String tradeType = "";
        switch (v.getId()){
            case R.id.home_menu_paymentLayout://门店列表订单
                if(Utils.isFastClick()){
                    return;
                }
                intent.setClass(activity,OrderListActivity.class);
                intent.putExtra("posInitData",posInitData);
                startActivity(intent);
                break;
            case R.id.home_menu_paymentRecordLayout://交易订单
                if(Utils.isFastClick()){
                    return;
                }
                tradeType = "0";
                intent.setClass(activity,PayOrderListActivity.class);
                intent.putExtra("posInitData",posInitData);
                intent.putExtra("tradeType",tradeType);
                startActivity(intent);
                break;
            case R.id.home_menu_refundRocordLayout:
                if(Utils.isFastClick()){
                    return;
                }
                tradeType = "1";
                intent.setClass(activity,PayOrderListActivity.class);
                intent.putExtra("posInitData",posInitData);
                intent.putExtra("tradeType",tradeType);
                startActivity(intent);

                break;
            default:

                break;
        }
    }

}
