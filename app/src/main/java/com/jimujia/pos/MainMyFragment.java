package com.jimujia.pos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jimujia.pos.activity.SelectServerActivity;
import com.jimujia.pos.bean.PosInitData;
import com.jimujia.pos.bean.StoreInfoResData;
import com.jimujia.pos.utils.MySerialize;
import com.jimujia.pos.utils.ToastUtil;
import com.jimujia.pos.utils.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("ValidFragment")
@ContentView(R.layout.fragment_my)
public class MainMyFragment extends BaseFragment implements View.OnClickListener{


    @ViewInject(R.id.my_fragment_circleImageView)
    CircleImageView headImg;
    @ViewInject(R.id.my_fragment_myMerName)
    TextView myMerName;
    @ViewInject(R.id.my_fragment_myCashName)
    TextView myCashName;
    @ViewInject(R.id.my_fragment_myCashAccount)
    TextView myCashAccount;

    @ViewInject(R.id.my_fragment_signInLayout)
    RelativeLayout signInLayout;
    @ViewInject(R.id.my_fragment_settLayout)
    RelativeLayout settLayout;

    /**
     * POS初始化信息
     */
    private PosInitData posInitData;
    private StoreInfoResData storeInfoResData;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG,"onViewCreated().........");

        initListener();

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG,"onResume().........");

        try {
            posInitData=(PosInitData) MySerialize.deSerialization(MySerialize.getObject("posInitData", activity));
            storeInfoResData =(StoreInfoResData) MySerialize.deSerialization(MySerialize.getObject("storeInfo", activity));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        initView();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause().........");
    }

    private void initListener(){
        headImg.setOnClickListener(this);
        signInLayout.setOnClickListener(this);
        settLayout.setOnClickListener(this);


    }

    private void initView(){
        myMerName.setText("");
        myCashName.setText("款台名称："+"");
        myCashAccount.setText("款台账号："+"");
        if(posInitData!=null){
            myMerName.setText(posInitData.getMername_pos());
            myCashName.setText("商户号："+posInitData.getMercId_pos());
            myCashAccount.setText("终端号："+posInitData.getTrmNo_pos());
        }
        myMerName.setSelected(true);

    }
    /**
     * 连续点击多次后的事件实现
     */
    int CLICK_NUM = 20;
    //点击时间间隔5秒
    int CLICK_INTERVER_TIME = 5000;
    //上一次的点击时间
    long lastClickTime = 0;
    //记录点击次数
    int clickNum = 0;
    private void continuClick(){

        //点击的间隔时间不能超过5秒
        long currentClickTime = SystemClock.uptimeMillis();
        if (currentClickTime - lastClickTime <= CLICK_INTERVER_TIME || lastClickTime == 0) {
            lastClickTime = currentClickTime;
            clickNum = clickNum + 1;
        } else {
            //超过5秒的间隔
            //重新计数 从1开始
            clickNum = 1;
            lastClickTime = 0;
            return;
        }
        if (clickNum == CLICK_NUM) {
            //重新计数
            clickNum = 0;
            lastClickTime = 0;
            /*实现点击多次后的事件*/
            startActivity(new Intent(activity,SelectServerActivity.class));
        }
    }




    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.my_fragment_circleImageView:
                continuClick();
                break;
            case R.id.my_fragment_signInLayout:
                boolean isPrint = true;
                intent.setClass(activity,SignInActivity.class);
                intent.putExtra("isPrint",isPrint);//签到成功时是否打印
                startActivity(intent);
                break;
            case R.id.my_fragment_settLayout:
                ToastUtil.showText(activity,"敬请期待！",1);
                break;
            default:

                break;
        }
    }


}
