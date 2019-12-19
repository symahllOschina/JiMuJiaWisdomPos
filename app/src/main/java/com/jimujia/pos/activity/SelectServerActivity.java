package com.jimujia.pos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jimujia.pos.BaseActivity;
import com.jimujia.pos.Constants;
import com.jimujia.pos.NitConfig;
import com.jimujia.pos.R;
import com.jimujia.pos.SharedPreConstants;
import com.jimujia.pos.bean.BaseResponseData;
import com.jimujia.pos.bean.PayOrderDetailData;
import com.jimujia.pos.bean.PosInitData;
import com.jimujia.pos.bean.RefundNoReqData;
import com.jimujia.pos.bean.RefundNoResData;
import com.jimujia.pos.bean.RefundResultNoticeReqData;
import com.jimujia.pos.requtil.ParamsReqUtil;
import com.jimujia.pos.sdkutil.FieldTypeUtil;
import com.jimujia.pos.sdkutil.FuyouPosServiceUtil;
import com.jimujia.pos.sdkutil.FuyouPrintUtil;
import com.jimujia.pos.utils.DecimalUtil;
import com.jimujia.pos.utils.FastJsonUtil;
import com.jimujia.pos.utils.GsonUtils;
import com.jimujia.pos.utils.HttpURLConnectionUtil;
import com.jimujia.pos.utils.NetworkUtils;
import com.jimujia.pos.utils.SharedPreferencesUtil;
import com.jimujia.pos.utils.ToastUtil;
import com.jimujia.pos.utils.Utils;
import com.jimujia.pos.view.ClearEditText;

import org.json.JSONException;
import org.litepal.LitePal;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.List;

/**
 * Time: 2019/10/21
 * Author:Administrator
 * Description: 退款
 */
@ContentView(R.layout.select_server_activity)
public class SelectServerActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.menu_title_imageView)
    ImageView imgBack;
    @ViewInject(R.id.menu_title_layout)
    LinearLayout titleLayout;
    @ViewInject(R.id.menu_title_tvTitle)
    TextView tvTitle;
    @ViewInject(R.id.menu_title_imgTitleImg)
    ImageView imgTitleImg;
    @ViewInject(R.id.menu_title_tvOption)
    TextView tvOption;

    @ViewInject(R.id.select_server_rgSelectServer)
    private RadioGroup rgSelectServer;
    @ViewInject(R.id.select_server_rbTest)
    private RadioButton rbTest;
    @ViewInject(R.id.select_server_rbProduction)
    private RadioButton rbProduction;


    /**
     * 服务器选择值   默认true，表示生产环境
     */
    private boolean isServer = true;
    private SharedPreferencesUtil sharedPreferencesUtil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("选择服务器");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");
        initView();
        initListener();
    }

    /**
     * 初始化界面控件
     */
    private void initListener(){

        imgBack.setOnClickListener(this);
        rgSelectServer.setOnCheckedChangeListener(selectServerListener);

    }

    private void initView(){
        sharedPreferencesUtil = new SharedPreferencesUtil(activity,SharedPreConstants.SELECT_SERVER_NAME);
        if(sharedPreferencesUtil.contain(SharedPreConstants.SELECT_SERVER_KEY_NAME)){
            isServer = (boolean) sharedPreferencesUtil.getSharedPreference(SharedPreConstants.SELECT_SERVER_KEY_NAME,false);
            if(isServer){
                rbTest.setChecked(false);
                rbProduction.setChecked(true);
            }else{
                rbTest.setChecked(true);
                rbProduction.setChecked(false);
            }
        }else{
            //默认选择生产
            rbTest.setChecked(false);
            rbProduction.setChecked(true);
        }


    }

    private RadioGroup.OnCheckedChangeListener selectServerListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId == R.id.select_server_rbProduction){
                isServer = true;
            }else{
                isServer = false;
            }
            sharedPreferencesUtil.put(SharedPreConstants.SELECT_SERVER_KEY_NAME,isServer);
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.menu_title_imageView:
                finish();
                break;

            default:
                break;

        }
    }
}
