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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jimujia.pos.BaseActivity;
import com.jimujia.pos.Constants;
import com.jimujia.pos.NitConfig;
import com.jimujia.pos.R;
import com.jimujia.pos.bean.BaseResponseData;
import com.jimujia.pos.bean.PayOrderDetailData;
import com.jimujia.pos.bean.PaymentNoReqData;
import com.jimujia.pos.bean.PaymentNoResData;
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
@ContentView(R.layout.activity_refund)
public class RefundActivity extends BaseActivity implements View.OnClickListener {

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

    @ViewInject(R.id.refund_layoutQrcode)
    RelativeLayout layoutQrcode;
    @ViewInject(R.id.refund_imgScanQrcode)
    ImageView imgScanQrcode;
    @ViewInject(R.id.refund_viewFGX1)
    View iewFGX1;
    @ViewInject(R.id.refund_etRefundQrcode)
    ClearEditText etRefundQrcode;
    @ViewInject(R.id.refund_etRefundAmount)
    ClearEditText etRefundAmount;

    @ViewInject(R.id.refund_btRefund)
    Button btRefund;



    private PosInitData posInitData;
    /**
     * 订单对象
     **/
    private PayOrderDetailData order;
    /**
     *  交易类型
     */
    private String payType;

    /**
     * 退款条码
     */
    private String etRefundQrcodeStr;
    /**
     * 退款金额（输入框退款金额，单位元）
     */
    private String etRefundAmountStr;

    /**
     * 交易流水号
     */
    private String paySerialNum;
    /**
     * 退款请求参数实体
     */
    private RefundResultNoticeReqData refundResultNoticeReqData;

    /**
     * 是否为通知更新订单动作
     */
    private boolean isNotice = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("退款");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");

        Intent intent = getIntent();
        posInitData = (PosInitData) intent.getSerializableExtra("posInitData");
        order = (PayOrderDetailData) intent.getSerializableExtra("order");
        payType = intent.getStringExtra("payType");
//        payType = Constants.PAYTYPE_BANK;
        if(Utils.isNotEmpty(payType)){
            if(Constants.PAYTYPE_BANK.equals(payType)){
                layoutQrcode.setVisibility(View.GONE);
                iewFGX1.setVisibility(View.GONE);
            }else{

            }
        }
        etRefundAmount.setText(DecimalUtil.branchToElement(DecimalUtil.StringToPrice(String.valueOf(order.getTrade_amount()))));
        etRefundAmount.setEnabled(false);
        initListener();
    }

    /**
     * 初始化界面控件
     */
    private void initListener(){

        imgBack.setOnClickListener(this);
        imgScanQrcode.setOnClickListener(this);
        btRefund.setOnClickListener(this);
    }

    /**
     * 保存支付订单信息到数据库
     * noticeSuccess:是否通知成功 true:成功，false :失败
     */
    private void saveOrderPayStatus(){
        //抛异常存储
        refundResultNoticeReqData.saveThrows();
        boolean isSave = refundResultNoticeReqData.save();
        if(isSave){
            Log.e(TAG,"保存成功！");
        }else{
            Log.e(TAG,"保存失败！");
        }
        showSaveResultsPromptDialog(isSave);


    }


    /**
     * 设置请求参数
     */
    private void setParameters(final String trade_status){
        RefundResultNoticeReqData refundResultNoticeReqData = ParamsReqUtil.refundNotice(posInitData,order,paySerialNum,trade_status);
        this.refundResultNoticeReqData = refundResultNoticeReqData;
        updateOrderStatus(refundResultNoticeReqData);
    }

    /**
     * 退款结果同步服务器
     */
    private void updateOrderStatus(final RefundResultNoticeReqData reqData){

        showWaitDialog();
        isNotice = true;

        final String url = NitConfig.refundNoticeUrl;
        Log.e(TAG,"退款结果通知接口路径："+url);

        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(reqData);
                    Log.e("退款结果通知请求参数：", content);
                    String content_type = HttpURLConnectionUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content,content_type);
                    Log.e("退款结果通知返回信息：", jsonStr);
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
    private void getRefundNo(){

        showWaitDialog();

        final String url = NitConfig.getRefundNoUrl;
        Log.e(TAG,"获取退款流水号接口路径："+url);
        final RefundNoReqData refundNoReqData = ParamsReqUtil.getRefundNoReqData(posInitData,order);
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(refundNoReqData);
                    Log.e("获取退款流水号请求参数：", content);
                    String content_type = HttpURLConnectionUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content,content_type);
                    Log.e("获取退款流水号返回信息：", jsonStr);
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
                    String getRefundNoJson = (String) msg.obj;
                    getRefundNoJson(getRefundNoJson);
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
                ToastUtil.showText(activity,"退款结果同步成功！",1);
                showRefundResultsPromptDialog();
            }else if(Constants.RETURN_FAILE.equals(baseResponseData.getReturnCode())){
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"退款结果同步失败！",1);
                }
                //保存支付信息（方便后续同步订单支付状态）
                saveOrderPayStatus();

            }else{
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"退款结果同步失败！",1);
                }
                //保存支付信息（方便后续同步订单支付状态）
                saveOrderPayStatus();


            }

        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"退款结果同步失败！",1);
            //保存支付信息（方便后续同步订单支付状态）
            saveOrderPayStatus();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"退款结果同步失败！",1);
            //保存支付信息（方便后续同步订单支付状态）
            saveOrderPayStatus();
        }
    }

    /**
     * 获取退款流水号返回JSON
     */
    private void getRefundNoJson(String jsonStr){

        try{
            Gson gson  =  GsonUtils.getGson();
            BaseResponseData baseResponseData = gson.fromJson(jsonStr, BaseResponseData.class);
            if(Constants.RETURN_SUCCESS.equals(baseResponseData.getReturnCode())){
                Object returnDataObject = baseResponseData.getReturnData();
                if(returnDataObject!=null){
                    String returnDataStr = gson.toJson(returnDataObject);
                    if(Utils.isNotEmpty(returnDataStr)){
                        //获取退款流水号返回对象
                        RefundNoResData refundNoResData = gson.fromJson(returnDataStr, RefundNoResData.class);
                        this.paySerialNum = refundNoResData.getRefund_no();
                        //调起SDK支付
                        refund();
                    }else{
                        ToastUtil.showText(activity,"创建退款流水号失败！",1);
                    }
                }else{
                    ToastUtil.showText(activity,"创建退款流水号失败！",1);
                }
            }else if(Constants.RETURN_FAILE.equals(baseResponseData.getReturnCode())){
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"创建退款流水号失败！",1);
                }
                //保存支付信息（方便后续同步订单支付状态）
                saveOrderPayStatus();
            }else{
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"创建退款流水号失败！",1);
                }
                //保存支付信息（方便后续同步订单支付状态）
                saveOrderPayStatus();
            }

        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"创建退款流水号失败！",1);
            //保存支付信息（方便后续同步订单支付状态）
            saveOrderPayStatus();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"创建退款流水号失败！",1);
            //保存支付信息（方便后续同步订单支付状态）
            saveOrderPayStatus();
        }
    }

    /**
     * 退款成功但通知失败保存订单支付信息到数据库提示
     */
    private void showSaveResultsPromptDialog(final boolean isSave){
        View view = LayoutInflater.from(this).inflate(R.layout.results_prompt_dialog, null);
        //描述信息
        TextView tvMsg = view.findViewById(R.id.results_prompt_tvMsg);
        if(isSave){
            tvMsg.setText("退款结果通知云端失败，订单退款结果导入数据库成功，请重试！");
        }else{
            tvMsg.setText("退款结果通知云端失败，订单退款结果导入数据库失败！请重试！");
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
     * 退款成功并且通知成功提示
     */
    private void showRefundResultsPromptDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.results_prompt_dialog, null);
        //描述信息
        TextView tvMsg = view.findViewById(R.id.results_prompt_tvMsg);
        tvMsg.setText("退款成功且订单记录已成功同步云端！");
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
//                List<RefundResultNoticeReqData> list = LitePal.where("pay_order_id = ?",order.getPay_order_id()).find(RefundResultNoticeReqData.class);
//                if(list.size()>0){
//                    Log.e(TAG,"查询有数据！");
//                    //删除该订单信息
//                    deleteOrder();
//                }

                mDialog.dismiss();
                finish();

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
        List<RefundResultNoticeReqData> list = LitePal.where("pay_order_id = ?",order.getPay_order_id()).find(RefundResultNoticeReqData.class);
        if(list.size()>0){
            Log.e(TAG,"查询有数据！");
            RefundResultNoticeReqData refundResultNoticeReqData = list.get(0);
            updateOrderStatus(refundResultNoticeReqData);
        }else{
            ToastUtil.showText(activity,"数据不存在！",1);
        }
    }

    /**
     * 退款
     */
    private void refund(){

        //元转分
        String total_feeStr = FieldTypeUtil.makeFieldAmount(etRefundAmountStr);
        Log.e("SDK提交带规则金额",total_feeStr);
        Log.e("生成的支付流水号：", paySerialNum);
        if(Constants.PAYTYPE_BANK.equals(payType)){
            //银行卡退款
            FuyouPosServiceUtil.cardRefundReq(activity, paySerialNum,total_feeStr);
        }else{
            //扫码退款
            FuyouPosServiceUtil.scanRefundReq(activity, "",etRefundQrcodeStr,total_feeStr,paySerialNum);
        }


    }

    /**
     * 退款成功返回结果信息
     */
    private void refundSuccessResult(Bundle bundle){
        Log.e(TAG,"SDK退款成功返回："+bundle.toString());
        ToastUtil.showText(activity,"退款成功！",1);
        //交易状态 0、未支付 1、退款成功 2、退款失败 3、结果未知
        String trade_status = "1";
        setParameters(trade_status);

    }
    /**
     * 退款失败返回结果信息
     */
    private void refundFailResult(Bundle bundle){
        Log.e(TAG,"SDK退款失败返回："+bundle.toString());
        //gateway_order_id:通道订单号
        String gateway_order_id = "";
        try{
            ToastUtil.showText(activity,"退款失败！",1);
        }catch (Exception e){
            e.printStackTrace();
            //异常按支付结果未知处理
            refundUnknownResult();
        }

    }

    /**
     * 退款未知
     */
    private void refundUnknownResult(){
        ToastUtil.showText(activity,"退款未知！",1);
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

                            etRefundQrcode.setText(auth_no);

                        }else{

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
         * 交易返回
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
                    // 成功
                    case Activity.RESULT_OK:
                        //获取结果信息
                        refundSuccessResult(bundle);
                        break;
                    // 取消
                    case Activity.RESULT_CANCELED:
                        //获取结果信息
//                        refundFailResult(bundle);
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
//                refundUnknownResult();
            }

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.refund_imgScanQrcode:
                if(Utils.isFastClick()){
                    return;
                }
                etRefundQrcode.setText("");
                FuyouPosServiceUtil.scanReq(activity);
                break;
            case R.id.refund_btRefund:
                if(Utils.isFastClick()){
                    return;
                }
                if(Utils.isNotEmpty(payType)){
                    if(!Constants.PAYTYPE_BANK.equals(payType)){
                        etRefundQrcodeStr = etRefundQrcode.getText().toString().trim();
                        if(Utils.isEmpty(etRefundQrcodeStr)){
                            ToastUtil.showText(activity,"退款条码不能为空！",1);
                            return;
                        }
                    }
                }

               etRefundAmountStr = etRefundAmount.getText().toString().trim();

                if(Utils.isEmpty(etRefundAmountStr)){
                    ToastUtil.showText(activity,"请输入退款金额！",1);
                    return;
                }
                //获取退款流水号
                getRefundNo();
                break;
            default:
                break;

        }
    }
}
