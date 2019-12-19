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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jimujia.pos.BaseInputAmountActivity;
import com.jimujia.pos.Constants;
import com.jimujia.pos.NitConfig;
import com.jimujia.pos.R;
import com.jimujia.pos.adapter.PayTypeGridViewAdapter;
import com.jimujia.pos.bean.BaseResponseData;
import com.jimujia.pos.bean.OrderDetailData;
import com.jimujia.pos.bean.PayResultNoticeReqData;
import com.jimujia.pos.bean.PayWayBean;
import com.jimujia.pos.bean.PaymentNoReqData;
import com.jimujia.pos.bean.PaymentNoResData;
import com.jimujia.pos.bean.PosInitData;
import com.jimujia.pos.bean.StoreInfoResData;
import com.jimujia.pos.requtil.ParamsReqUtil;
import com.jimujia.pos.sdkutil.FieldTypeUtil;
import com.jimujia.pos.sdkutil.FuyouPosServiceUtil;
import com.jimujia.pos.sdkutil.FuyouPrintUtil;
import com.jimujia.pos.utils.DecimalUtil;
import com.jimujia.pos.utils.FastJsonUtil;
import com.jimujia.pos.utils.GsonUtils;
import com.jimujia.pos.utils.HttpURLConnectionUtil;
import com.jimujia.pos.utils.NetworkUtils;
import com.jimujia.pos.utils.ToastUtil;
import com.jimujia.pos.utils.Utils;

import org.json.JSONException;
import org.litepal.LitePal;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Time: 2019/10/16
 * Author:Administrator
 * Description: 支付界面
 */
@ContentView(R.layout.activity_payment)
public class PaymentActivity extends BaseInputAmountActivity implements View.OnClickListener {

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

    @ViewInject(R.id.payment_tvSumMoney)
    TextView tvSumMoney;

    @ViewInject(R.id.pay_type_mGridView)
    GridView mGridView;
    private List<PayWayBean> list = new ArrayList<>();
    private BaseAdapter mAdapter;

    /**
     * 支付金额（输入框输入的合法金额）
     */
    private String totalFee;
    /**
     * 支付方式
     */
    private String payType;
    /**
     * 交易流水号
     */
    private String paySerialNum;

    private PosInitData posInitData;
    private OrderDetailData order;

    /**
     * 支付成功通知请求参数对象
     */
    private PayResultNoticeReqData payResultNoticeReqData;

    /**
     *  待支付金额（分转元后的金额）
     */
    private String debtAmount = "";

    /**
     * 是否为通知更新订单动作
     */
    private boolean isNotice = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("付款");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");



        Intent intent = getIntent();
        posInitData = (PosInitData) intent.getSerializableExtra("posInitData");
        order = (OrderDetailData) intent.getSerializableExtra("order");
        //待支付金额
        Integer debtAmountStr = order.getDebt_amount();
        if(Utils.isNotEmpty(String.valueOf(debtAmountStr))){
            debtAmount = DecimalUtil.branchToElement(String.valueOf(debtAmountStr));
        }
        tvSumMoney.setText(getString(R.string.debt_amount)+"：￥"+debtAmount);

        initData();

        initListener();

    }

    private void initData(){
        PayWayBean bankBean = new PayWayBean();
        bankBean.setImg(R.drawable.bank_pay_icon);
        bankBean.setText("刷卡");
        PayWayBean wxBean = new PayWayBean();
        wxBean.setImg(R.drawable.wx_pay_icon);
        wxBean.setText("微信");
        PayWayBean aliBean = new PayWayBean();
        aliBean.setImg(R.drawable.ali_pay_icon);
        aliBean.setText("支付宝");
        list.add(bankBean);
        list.add(wxBean);
        list.add(aliBean);

        mAdapter = new PayTypeGridViewAdapter(activity,list);
        mGridView.setAdapter(mAdapter);
    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        mGridView.setOnItemClickListener(payTypeCheckedLinstener);
    }



    /**
     * 保存支付订单信息到数据库
     * noticeSuccess:是否通知成功 true:成功，false :失败
     */
    private void saveOrderPayStatus(){
        //抛异常存储
        payResultNoticeReqData.saveThrows();
        boolean isSave = payResultNoticeReqData.save();
        if(isSave){
            Log.e(TAG,"保存成功！");
        }else{
            Log.e(TAG,"保存失败！");
        }

        showSaveResultsPromptDialog(isSave);


    }

    /**
     * 支付第一步（判断输入金额是否合法）
     */
    private void payMethodOne(){

        try {
            String totalFeeStr = pending.toString();
            Log.e("输入框金额值：", totalFeeStr);
            if(Utils.isEmpty(totalFeeStr)){
                ToastUtil.showText(activity,"请输入有效金额！",1);
                return;
            }
            totalFee =  DecimalUtil.StringToPrice(totalFeeStr);
            Log.e("金额值转换后：", totalFee);
            //金额是否合法
            int isCorrect = DecimalUtil.isEqual(totalFee);
            if(isCorrect != 1){
                ToastUtil.showText(activity,"请输入有效金额！",1);
                return;
            }
            //金额是否有效（不能大于待支付金额）
            int isEffective = DecimalUtil.isEffective(totalFee,debtAmount);
            if(isEffective == 1){
                ToastUtil.showText(activity,"不能大于待支付金额！",1);
                return;
            }
            payMethodTwo();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"金额错误！",1);
        }
    }

    /**
     * 支付第二步，获取支付流水号
     */
    private void payMethodTwo(){
        PaymentNoReqData reqData = ParamsReqUtil.getPaymentNoReqData(posInitData,order);
        getPaymentNo(reqData);

    }

    /**
     * 支付第三步（调用支付SDK）
     */
    private void payMethodThree(){
        //元转分
        String total_feeStr = FieldTypeUtil.makeFieldAmount(totalFee);
        Log.e("SDK提交带规则金额",total_feeStr);
        Log.e("生成的支付流水号：", paySerialNum);
        //是否开启前置摄像头
        boolean isFrontCamera = false;
        FuyouPosServiceUtil.payReq(activity, payType, total_feeStr,paySerialNum,isFrontCamera);

    }

    /**
     * 设置请求参数
     */
    private void setParameters(final String gateway_order_id,final String refer_no,final String trace_no,final String batch_no,final String trade_status){
        //元转分
        String total_feeStr = DecimalUtil.elementToBranch(totalFee);
        PayResultNoticeReqData payResultNoticeReqData = ParamsReqUtil.paymentNotice(posInitData,order,payType,total_feeStr,paySerialNum,gateway_order_id,refer_no,trace_no,batch_no,trade_status);
        this.payResultNoticeReqData = payResultNoticeReqData;
        updateOrderStatus(payResultNoticeReqData);
    }


    /**
     *  支付第四步（更新服务器订单状态）
     */
    private void updateOrderStatus(final PayResultNoticeReqData payResultNoticeReqData){

        showWaitDialog();
        isNotice = true;

        final String url = NitConfig.paymentNoticeUrl;
        Log.e(TAG,"支付结果通知接口路径："+url);

        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(payResultNoticeReqData);
                    Log.e("支付结果通知请求参数：", content);
                    String content_type = HttpURLConnectionUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content,content_type);
                    Log.e("支付结果通知返回信息：", jsonStr);
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

    /**
     *  支付第二步获取支付流水号
     */
    private void getPaymentNo(final PaymentNoReqData paymentNoReqData){

        showWaitDialog();

        final String url = NitConfig.getPaymentNoUrl;
        Log.e(TAG,"获取支付流水号接口路径："+url);

        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(paymentNoReqData);
                    Log.e("获取支付流水号请求参数：", content);
                    String content_type = HttpURLConnectionUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content,content_type);
                    Log.e("获取支付流水号返回信息：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_TWO;
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
            switch (msg.what){
                case NetworkUtils.MSG_WHAT_ONE:
                    String noticeResultJson = (String) msg.obj;
                    noticeResultJson(noticeResultJson);
                    hideWaitDialog();
                    break;
                case NetworkUtils.MSG_WHAT_TWO:
                    String getPaymentNoJson = (String) msg.obj;
                    getPaymentNoJson(getPaymentNoJson);
                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(isNotice){
                        //保存支付信息（方便后续同步订单支付状态）
                        saveOrderPayStatus();
                    }
                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(isNotice){
                        //保存支付信息（方便后续同步订单支付状态）
                        saveOrderPayStatus();
                    }
                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(isNotice){
                        //保存支付信息（方便后续同步订单支付状态）
                        saveOrderPayStatus();
                    }
                    hideWaitDialog();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 通知服务器更新订单返回JSON
     */
    private void noticeResultJson(String jsonStr){

        try{
            Gson gson  =  GsonUtils.getGson();
            BaseResponseData baseResponseData = gson.fromJson(jsonStr, BaseResponseData.class);
            if(Constants.RETURN_SUCCESS.equals(baseResponseData.getReturnCode())){
                ToastUtil.showText(activity,"支付结果同步成功！",1);
                showResultsPromptDialog();
            }else if(Constants.RETURN_FAILE.equals(baseResponseData.getReturnCode())){
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"支付结果同步失败！",1);
                }
                //保存支付信息（方便后续同步订单支付状态）
                saveOrderPayStatus();
            }else{
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"支付结果同步失败！",1);
                }
                //保存支付信息（方便后续同步订单支付状态）
                saveOrderPayStatus();
            }

        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"支付结果同步失败！",1);
            //保存支付信息（方便后续同步订单支付状态）
            saveOrderPayStatus();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"支付结果同步失败！",1);
            //保存支付信息（方便后续同步订单支付状态）
            saveOrderPayStatus();
        }
    }

    /**
     * 获取支付流水号返回JSON
     */
    private void getPaymentNoJson(String jsonStr){

        try{
            Gson gson  =  GsonUtils.getGson();
            BaseResponseData baseResponseData = gson.fromJson(jsonStr, BaseResponseData.class);
            if(Constants.RETURN_SUCCESS.equals(baseResponseData.getReturnCode())){
                Object returnDataObject = baseResponseData.getReturnData();
                if(returnDataObject!=null){
                    String returnDataStr = gson.toJson(returnDataObject);
                    if(Utils.isNotEmpty(returnDataStr)){
                        //获取支付流水号返回对象
                        PaymentNoResData paymentNoResData = gson.fromJson(returnDataStr, PaymentNoResData.class);
                        this.paySerialNum = paymentNoResData.getPayment_no();
                        //调起SDK支付
                        payMethodThree();
                    }else{
                        ToastUtil.showText(activity,"创建支付流水号失败！",1);
                    }
                }else{
                    ToastUtil.showText(activity,"创建支付流水号失败！",1);
                }
            }else if(Constants.RETURN_FAILE.equals(baseResponseData.getReturnCode())){
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"创建支付流水号失败！",1);
                }
                //保存支付信息（方便后续同步订单支付状态）
                saveOrderPayStatus();
            }else{
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"创建支付流水号失败！",1);
                }
                //保存支付信息（方便后续同步订单支付状态）
                saveOrderPayStatus();
            }

        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"创建支付流水号失败！",1);
            //保存支付信息（方便后续同步订单支付状态）
            saveOrderPayStatus();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"创建支付流水号失败！",1);
            //保存支付信息（方便后续同步订单支付状态）
            saveOrderPayStatus();
        }
    }


    /**
     * 支付成功并且通知成功提示
     */
    private void showResultsPromptDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.results_prompt_dialog, null);
        //描述信息
        TextView tvMsg = view.findViewById(R.id.results_prompt_tvMsg);
        tvMsg.setText("支付成功且订单记录已成功同步云端！");
        //操作按钮
        final Button btCancel = (Button) view.findViewById(R.id.results_prompt_btCancel);
        final Button btSuccess = (Button) view.findViewById(R.id.results_prompt_btSuccess);
        final Dialog mDialog = new Dialog(this,R.style.dialog);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        mDialog.setContentView(view);
        btCancel.setVisibility(View.GONE);
        btSuccess.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();

            }
        });
        //点击屏幕和物理返回键dialog不消失
        mDialog.setCancelable(false);
        mDialog.show();
    }

    /**
     * 支付成功但通知失败保存订单支付信息到数据库提示
     */
    private void showSaveResultsPromptDialog(final boolean isSave){
        View view = LayoutInflater.from(this).inflate(R.layout.results_prompt_dialog, null);
        //描述信息
        TextView tvMsg = view.findViewById(R.id.results_prompt_tvMsg);
        if(isSave){
            tvMsg.setText("支付结果通知云端失败，订单支付结果导入数据库成功，请重试！");
        }else{
            tvMsg.setText("支付结果通知云端失败，订单支付结果导入数据库失败！请重试！");
        }

        //操作按钮
        final Button btCancel = (Button) view.findViewById(R.id.results_prompt_btCancel);
        final Button btSuccess = (Button) view.findViewById(R.id.results_prompt_btSuccess);
        btSuccess.setText("重试");
        final Dialog mDialog = new Dialog(this,R.style.dialog);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        mDialog.setContentView(view);
        btCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
            }
        });
        btSuccess.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if(isSave){
                    //如果通知失败，但保存订单成功，只需重新通知
                    retryNoticeSynch();
                }else{
                    saveOrderPayStatus();
                }
            }
        });
        //点击屏幕和物理返回键dialog不消失
        mDialog.setCancelable(false);
        mDialog.show();
    }

    /**
     * 取出保存的支付信息，重新通知
     */
    private void retryNoticeSynch(){
        List<PayResultNoticeReqData> list = LitePal.where("pay_order_id = ?",order.getPay_order_id()).find(PayResultNoticeReqData.class);
        if(list.size()>0){
            Log.e(TAG,"查询有数据！");
            PayResultNoticeReqData payResultNoticeReqData = list.get(0);
            updateOrderStatus(payResultNoticeReqData);
        }else{
            ToastUtil.showText(activity,"数据不存在！",1);
        }
    }

    /**
     * 支付成功返回结果信息
     */
    private void paymentSuccessResult(Bundle bundle){
        try{
            Log.e(TAG,"SDK支付成功返回："+bundle.toString());
            //gateway_order_id:通道订单号
            String gateway_order_id = "";
            if(Constants.PAYTYPE_BANK.equals(payType)){
                gateway_order_id = bundle.getString("orderNumber");

            }else if(Constants.PAYTYPE_WX.equals(payType)){
                gateway_order_id = bundle.getString("wxOrderNumber");
            }else if(Constants.PAYTYPE_ALI.equals(payType)){
                gateway_order_id = bundle.getString("zfbOrderNumber");
            }
            //trace_no:凭证号(支付通道返回)
            String trace_no = bundle.getString("traceNo");
            //batch_no：批次号(支付通道返回)
            String batch_no = bundle.getString("batchNo");
            //refer_no：参考号
            String refer_no  = bundle.getString("referenceNo");
            //trade_status:交易状态（0:未支付 1:支付成功 2:支付失败 3:结果未知）
            String trade_status = "1";

            //通知服务器更新订单信息
            setParameters(gateway_order_id,refer_no,trace_no,batch_no,trade_status);
        }catch (Exception e){
            e.printStackTrace();
            //异常按支付结果未知处理
            ToastUtil.showText(activity,"支付结果返回错误！通知失败",1);
//            paymentUnknownResult();
        }



    }
    /**
     * 支付失败返回结果信息
     */
    private void paymentFailResult(Bundle bundle){

        //gateway_order_id:通道订单号
        String gateway_order_id = "";
        try{
            Log.e(TAG,"SDK支付失败返回："+bundle.toString());
            gateway_order_id = bundle.getString("orderNumber");
            //trace_no:凭证号(支付通道返回)
            String trace_no = "";
            //batch_no：批次号(支付通道返回)
            String batch_no = "";
            //refer_no：参考号
            String refer_no  = "";
            //trade_status:交易状态（0:未支付 1:支付成功 2:支付失败 3:结果未知）
            String trade_status = "2";

            //通知服务器更新订单信息
            setParameters(gateway_order_id,refer_no,trace_no,batch_no,trade_status);
        }catch (Exception e){
            e.printStackTrace();
            //异常按支付结果未知处理
            paymentUnknownResult();
        }

    }

    /**
     * 支付未知
     */
    private void paymentUnknownResult(){
        //gateway_order_id:通道订单号
        String gateway_order_id = "";
        //trace_no:凭证号(支付通道返回)
        String trace_no = "";
        //batch_no：批次号(支付通道返回)
        String batch_no = "";
        //refer_no：参考号
        String refer_no  = "";
        //trade_status:交易状态（0:未支付 1:支付成功 2:支付失败 3:结果未知）
        String trade_status = "3";
        //通知服务器更新订单信息
        setParameters(gateway_order_id,refer_no,trace_no,batch_no,trade_status);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        /**
         * 富友POS扫码返回
         */
        if (requestCode == FuyouPosServiceUtil.SCAN_REQUEST_CODE) {
            if(bundle != null){
                Log.e(TAG,resultCode+"");
                String reason = "扫码取消";
                String traceNo = "";
                String batchNo = "";
                String ordernumber = "";
                String reason_str = (String) bundle.get("reason");
                String traceNo_str = (String)bundle.getString("traceNo");
                String batchNo_str = (String)bundle.getString("batchNo");
                String ordernumber_str = (String)bundle.getString("ordernumber");

                switch (resultCode) {
                    case Activity.RESULT_OK:
                        String scanCodeStr = bundle.getString("return_txt");//扫码返回数据
                        Log.e("获取扫描结果：", scanCodeStr);
                        //如果扫描的二维码为空则不执行支付请求
                        if(Utils.isNotEmpty(scanCodeStr)){
                            //auth_no	授权码（及扫描二维码值）
                            String auth_no = scanCodeStr;



                        }else{
                            //清空StringBuilder，EditText恢复初始值
                            //清空EditText
                            pending.delete( 0, pending.length() );
                            if(pending.length()<=0){
                                etSumMoney.setText("0.00");
                            }

                            ToastUtil.showText(activity,"扫描结果为空！",1);
                        }


                        break;
                    // 扫码取消
                    case Activity.RESULT_CANCELED:

                        if (Utils.isNotEmpty(reason_str)) {
                            Log.e("reason", reason_str);
                            reason = reason_str;
                        }
                        ToastUtil.showText(activity,reason,1);
                        Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);
                        break;
                    default:
                        break;

                }
            }else{
                ToastUtil.showText(activity,"扫码失败！",1);
            }

        }

        /**
         * 打印返回
         */
        if(requestCode == FuyouPosServiceUtil.PRINT_REQUEST_CODE){
            if(bundle!=null){
                Log.e(TAG,resultCode+"");
                String reason = "打印取消";
                String traceNo = "";
                String batchNo = "";
                String ordernumber = "";

                String reason_str = (String) bundle.get("reason");
                String traceNo_str = (String)bundle.getString("traceNo");
                String batchNo_str = (String)bundle.getString("batchNo");
                String ordernumber_str = (String)bundle.getString("ordernumber");
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //打印完成关闭界面


                        break;
                    case Activity.RESULT_CANCELED:
                        if (Utils.isNotEmpty(reason_str)) {
                            reason = reason_str;
                            Log.e("reason", reason);
                            if(FuyouPrintUtil.ERROR_PAPERENDED == Integer.valueOf(reason)){
                                //缺纸，不能打印
                                ToastUtil.showText(activity,"打印机缺纸，打印中断！",1);
                            }else {
                                ToastUtil.showText(activity,"打印机出现故障错误码为："+reason,1);
                            }
                        }else{
                            ToastUtil.showText(activity,reason,1);
                        }
                        finish();

                        Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);
                        break;
                    default:
                        break;

                }
            }else{
                ToastUtil.showText(activity,"打印返回数据为空！",1);
            }

        }
        /**
         * 支付返回
         */
        if (requestCode == FuyouPosServiceUtil.PAY_REQUEST_CODE) {
            if(bundle != null){
                Log.e(TAG,resultCode+"");
                String reason = "支付取消";
                String traceNo = "";
                String batchNo = "";
                String ordernumber = "";

                String reason_str = (String) bundle.get("reason");
                String traceNo_str = (String)bundle.getString("traceNo");
                String batchNo_str = (String)bundle.getString("batchNo");
                String ordernumber_str = (String)bundle.getString("ordernumber");
                switch (resultCode) {
                    // 支付成功
                    case Activity.RESULT_OK:
                        //获取支付结果信息
                        paymentSuccessResult(bundle);
                        break;
                    // 支付取消
                    case Activity.RESULT_CANCELED:
                        //获取支付结果信息
//                        paymentFailResult(bundle);
                        if (Utils.isNotEmpty(reason_str)) {
                            reason = reason_str;
                            Log.e("reason", reason);
                        }
                        ToastUtil.showText(activity,reason,1);
                        finish();

                        Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);

                        break;
                    default:
                        break;
                }
            }else{
                ToastUtil.showText(activity,"支付返回数据为空！",1);
                //支付未知
//                paymentUnknownResult();
            }

        }
    }


    /**
     * GridView的Item事件回调
     */
    private AdapterView.OnItemClickListener payTypeCheckedLinstener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(Utils.isFastClick()){
                return;
            }
            if(position == 0&&list.get(position).getText().equals("刷卡")){
                payType = Constants.PAYTYPE_BANK;
                payMethodOne();
            }else if(position == 1&&list.get(position).getText().equals("微信")){
                payType = Constants.PAYTYPE_WX;
                payMethodOne();
            }else if(position == 2&&list.get(position).getText().equals("支付宝")){
                payType = Constants.PAYTYPE_ALI;
                payMethodOne();
            }else{
                payType = "";
                ToastUtil.showText(activity,"请选择付款类型",1);
            }

        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        super.onClick(v);
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            default:
                break;

        }
    }
}
