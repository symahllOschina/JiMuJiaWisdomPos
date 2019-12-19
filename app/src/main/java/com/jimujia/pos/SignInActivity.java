package com.jimujia.pos;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jimujia.pos.bean.BaseResponseData;
import com.jimujia.pos.bean.PaginatedData;
import com.jimujia.pos.bean.PayOrderDetailData;
import com.jimujia.pos.bean.PayOrderListReqData;
import com.jimujia.pos.bean.PayOrderListResData;
import com.jimujia.pos.bean.PayResultNoticeReqData;
import com.jimujia.pos.bean.PosInitData;
import com.jimujia.pos.bean.StoreInfoReqData;
import com.jimujia.pos.bean.StoreInfoResData;
import com.jimujia.pos.requtil.ParamsReqUtil;
import com.jimujia.pos.sdkutil.FuyouPosServiceUtil;
import com.jimujia.pos.sdkutil.FuyouPrintUtil;
import com.jimujia.pos.utils.FastJsonUtil;
import com.jimujia.pos.utils.GsonUtils;
import com.jimujia.pos.utils.HttpURLConnectionUtil;
import com.jimujia.pos.utils.MySerialize;
import com.jimujia.pos.utils.NetworkUtils;
import com.jimujia.pos.utils.SharedPreferencesUtil;
import com.jimujia.pos.utils.ToastUtil;
import com.jimujia.pos.utils.Utils;

import org.json.JSONException;
import org.xutils.view.annotation.ContentView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 签到界面
 */
@ContentView(R.layout.activity_sign_in)
public class SignInActivity extends BaseActivity {





    /**
     * 第一次从welcome界面和点击签到功能进入
     * 区分不同入口，控制是否需获取POS机设备参数以及是否签到成功打印小票
     */
    private boolean isPrint = false;

    /**
     * POS初始化信息
     * isPrint：签到成功是否打印小票
     */
    private PosInitData posInitData;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        isPrint = intent.getBooleanExtra("isPrint",isPrint);
        SharedPreferencesUtil posInitSP = new SharedPreferencesUtil(activity, "posInit");
        SharedPreferencesUtil storeInfoSP = new SharedPreferencesUtil(activity, "storeInfo");
        if(isPrint){
            appDataInstance();
        }else{
            if(posInitSP.contain("posMercId")){
                if(storeInfoSP.contain("storeId")){
                    toMainActivity();
                    Log.e("登录初始化信息：", "Key存在");
                    Log.e("商户号：", (String) posInitSP.getSharedPreference("posMercId",""));
                    Log.e("终端号：", (String) posInitSP.getSharedPreference("posTrmNo",""));
                    Log.e("商户姓名：", (String) posInitSP.getSharedPreference("posMername",""));

                    Log.e("门店ID：", (String) storeInfoSP.getSharedPreference("storeId",""));
                    Log.e("门店名称：", (String) storeInfoSP.getSharedPreference("storeName",""));
                    Log.e("终端名称：", (String) storeInfoSP.getSharedPreference("terminalName",""));
                    finish();
                }else{
                    Log.e("登录初始化信息：", "Key不存在");
                    appDataInstance();
                }

            }else{
                Log.e("登录初始化信息：", "Key不存在");
                appDataInstance();
            }
        }





    }






    /** 获取POS机本身的相关信息，获取应用初始化数据 */
    private void appDataInstance(){
        try {

            FuyouPosServiceUtil.signInReq(activity);

        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Newland_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }

    }


    /**
     *  获取门店信息
     */
    private void getStoreInfo(){

        showWaitDialog();

        final String url = NitConfig.getStoreInfoUrl;
        Log.e(TAG,"获取门店信息接口路径："+url);
        final StoreInfoReqData reqData = ParamsReqUtil.getStoreInfoReqData(posInitData);
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(reqData);
                    Log.e("获取门店信息请求参数：", content);
                    String content_type = HttpURLConnectionUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content,content_type);
                    Log.e("获取门店信息返回信息：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_ONE;
                    String text = jsonStr;
                    sendMessage(msg,text);
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
                }
            }
        }.start();
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
            StoreInfoResData storeInfoResData = new StoreInfoResData();
            switch (msg.what){
                case NetworkUtils.MSG_WHAT_ONE:
                    String storeInfoResultJson = (String) msg.obj;
                    storeInfoResultJson(storeInfoResultJson);
                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    storeInfoResData.setStore_id("");
                    storeInfoResData.setStore_name("");
                    storeInfoResData.setTerminal_name("");
                    saveStoreInfo(storeInfoResData);
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    storeInfoResData.setStore_id("");
                    storeInfoResData.setStore_name("");
                    storeInfoResData.setTerminal_name("");
                    saveStoreInfo(storeInfoResData);
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    storeInfoResData.setStore_id("");
                    storeInfoResData.setStore_name("");
                    storeInfoResData.setTerminal_name("");
                    saveStoreInfo(storeInfoResData);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 门店信息
     */
    private void storeInfoResultJson(String jsonStr){
        //门店信息
        StoreInfoResData storeInfoResData = new StoreInfoResData();
        storeInfoResData.setStore_id("");
        storeInfoResData.setStore_name("");
        storeInfoResData.setTerminal_name("");
        try{
            Gson gson  =  GsonUtils.getGson();
            BaseResponseData baseResponseData = gson.fromJson(jsonStr, BaseResponseData.class);
            if(Constants.RETURN_SUCCESS.equals(baseResponseData.getReturnCode())){
                Object returnDataObject = baseResponseData.getReturnData();
                if(returnDataObject!=null){
                    String returnDataStr = gson.toJson(returnDataObject);
                    if(Utils.isNotEmpty(returnDataStr)){
                        //门店信息
                       storeInfoResData = gson.fromJson(returnDataStr, StoreInfoResData.class);
                    }else{
                        ToastUtil.showText(activity,"获取门店信息失败！",1);
                    }
                }else{
                    ToastUtil.showText(activity,"获取门店信息失败！",1);
                }
            }else if(Constants.RETURN_FAILE.equals(baseResponseData.getReturnCode())){
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"获取门店信息失败！",1);
                }

            }else{
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"获取门店信息失败！",1);
                }

            }

        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"获取门店信息失败！",1);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"获取门店信息失败！",1);
        }

        saveStoreInfo(storeInfoResData);

    }

    /**
     * 保存门店信息
     */
    private void saveStoreInfo(StoreInfoResData storeInfoResData){
        /**
         * 下面是调用帮助类将一个对象以序列化的方式保存
         * 方便我们在其他界面调用，类似于Intent携带数据
         */
        try {
            MySerialize.saveObject("storeInfo",activity,MySerialize.serialize(storeInfoResData));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"保存门店信息失败！",Toast.LENGTH_LONG,2);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"保存门店信息失败！",Toast.LENGTH_LONG,2);
        }
        Log.e("保存门店信息：","成功！");
        //将POS初始化信息保存在本地
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "storeInfo");
        sharedPreferencesUtil.put("storeId", storeInfoResData.getStore_id());
        sharedPreferencesUtil.put("storeName", storeInfoResData.getStore_name());
        sharedPreferencesUtil.put("terminalName", storeInfoResData.getTerminal_name());


        if(isPrint){

            //打印
            String printTextStr = FuyouPrintUtil.businessInfoPrintText(posInitData,storeInfoResData);
            FuyouPosServiceUtil.printTextReq(activity,printTextStr);


        }else{

            toMainActivity();
        }

        finish();
    }



    /**
     * 富友界面访问成功返回
     */
    private void fuyouSignResult(Bundle bundle){

        String merchantIdStr = bundle.getString("merchantId");//商户号
        String terminalIdStr = bundle.getString("terminalId");//终端号
        String merchantNameStr = bundle.getString("merchantName");//商户名
        Log.e("SDK签到返回：", "----------");
        Log.e("商户号：", merchantIdStr);
        Log.e("终端号：", terminalIdStr);
        Log.e("商户姓名：", merchantNameStr);

        posInitData = new PosInitData();
        posInitData.setMercId_pos(merchantIdStr);
        posInitData.setTrmNo_pos(terminalIdStr);
        posInitData.setMername_pos(merchantNameStr);

        //将需要的参数传入支付请求公共类保存在本地
        saveDataLocal();
    }

    private void saveDataLocal(){


        /**
         * 下面是调用帮助类将一个对象以序列化的方式保存
         * 方便我们在其他界面调用，类似于Intent携带数据
         */
        try {
            MySerialize.saveObject("posInitData",activity,MySerialize.serialize(posInitData));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"保存POS机参数失败！",Toast.LENGTH_LONG,2);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"保存POS机参数失败！",Toast.LENGTH_LONG,2);
        }
        Log.e("保存POS机初始化信息：","成功！");
        //将POS初始化信息保存在本地
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "posInit");
        sharedPreferencesUtil.put("posMercId", posInitData.getMercId_pos());
        sharedPreferencesUtil.put("posTrmNo", posInitData.getTrmNo_pos());
        sharedPreferencesUtil.put("posMername", posInitData.getMername_pos());

        /**
         * 获取门店信息
         */
        getStoreInfo();


    }

    private void toMainActivity(){
        Intent intent = new Intent();
        intent.setClass(activity,MainActivity.class);
        startActivity(intent);
        //跳转动画效果
        overridePendingTransition(R.anim.in_from, R.anim.to_out);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            Bundle bundle = data.getExtras();
            Log.e(TAG,bundle.toString());
            /**
             *  签到返回
             */
            if (requestCode == FuyouPosServiceUtil.SIGN_REQUEST_CODE) {
                if(bundle != null){
                    switch (resultCode) {
                        // 请求成功
                        case Activity.RESULT_OK:

                            fuyouSignResult(bundle);

                            break;
                        // 请求取消
                        case Activity.RESULT_CANCELED:
                            String reason = bundle.getString("reason");
                            if (reason != null) {
                                ToastUtil.showText(activity,"POS机初始化参数失败！",Toast.LENGTH_LONG,2);
                            }
                            break;
                        default:
                            break;
                    }
                }else{
                    ToastUtil.showText(activity,"POS机初始化参数失败！",Toast.LENGTH_LONG,2);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.showText(activity,"POS机初始化参数失败！",Toast.LENGTH_LONG,2);
        }
    }



}
