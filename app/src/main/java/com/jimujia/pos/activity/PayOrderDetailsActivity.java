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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jimujia.pos.BaseActivity;
import com.jimujia.pos.Constants;
import com.jimujia.pos.NitConfig;
import com.jimujia.pos.R;
import com.jimujia.pos.bean.BaseResponseData;
import com.jimujia.pos.bean.PayOrderDetailData;
import com.jimujia.pos.bean.PayResultNoticeReqData;
import com.jimujia.pos.bean.PosInitData;
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

import org.json.JSONException;
import org.litepal.LitePal;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.List;

/**
 * Time: 2019/10/21
 * Author:Administrator
 * Description: 支付订单详情
 */
@ContentView(R.layout.activity_pay_order_details)
public class PayOrderDetailsActivity extends BaseActivity implements View.OnClickListener {

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

    @ViewInject(R.id.pay_order_details_tvOrderId)
    TextView tvOrderId;
    @ViewInject(R.id.pay_order_details_tvGatewayOrderId)
    TextView tvGatewayOrderId;
    @ViewInject(R.id.pay_order_details_tvReferNo)
    TextView tvReferNo;
    @ViewInject(R.id.pay_order_details_tvTraceNo)
    TextView tvTraceNo;
    @ViewInject(R.id.pay_order_details_tvBatchNo)
    TextView tvBatchNo;
    @ViewInject(R.id.pay_order_details_tvOrderMount)
    TextView tvOrderMount;
    @ViewInject(R.id.pay_order_details_tvOrderTime)
    TextView tvOrderTime;
    @ViewInject(R.id.pay_order_details_tvOrderPayType)
    TextView tvOrderPayType;
    @ViewInject(R.id.pay_order_details_tvOrderPayStatus)
    TextView tvOrderPayStatus;
    @ViewInject(R.id.pay_order_details_tvRefundHintMsg)
    TextView tvRefundHintMsg;

    /** 退款  */
    @ViewInject(R.id.pay_order_details_btRefund)
    Button btRefund;

    private PosInitData posInitData;
    private PayOrderDetailData order;//订单对象



    /**
     * 退款通知请求参数实体
     */
    private RefundResultNoticeReqData refundResultNoticeReqData;

    /**
     * 标注onResume()方法执行顺序
     */
    private int onCreateIndex = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("交易详情");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");

        Intent intent = getIntent();
        posInitData = (PosInitData) intent.getSerializableExtra("posInitData");
        order = (PayOrderDetailData) intent.getSerializableExtra("order");




        initListener();


        updateViewData();



    }

    @Override
    protected void onResume() {
        super.onResume();
        queryDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "释放资源成功");
    }


    /**
     * 初始化界面控件
     */
    private void initListener(){

        imgBack.setOnClickListener(this);
        btRefund.setOnClickListener(this);
    }


    /**
     * 查询数据库（是否有未同步数据）
     */
    private void queryDatabase(){
        List<RefundResultNoticeReqData> list = LitePal.where("pay_order_id = ?",order.getPay_order_id()).find(RefundResultNoticeReqData.class);
        if(list.size()>0){
            Log.e(TAG,"查询有数据！");
            this.refundResultNoticeReqData = list.get(0);
//            refundResultNoticeReqData.setTrade_amount(String.valueOf(order.getTrade_amount()));
//            refundResultNoticeReqData.save();
            showResultsPromptDialog();
        }else{
            Log.e(TAG,"查询没数据！");
//            ToastUtil.showText(activity,"数据不存在！",1);
            if(onCreateIndex > 1){
                finish();
            }
        }
        onCreateIndex ++;
    }


    /**
     *删除数据
     */
    private void deleteOrder(){
        List<RefundResultNoticeReqData> list = LitePal.where("pay_order_id = ?",order.getPay_order_id()).find(RefundResultNoticeReqData.class);
        if(list.size()>0){
            Log.e(TAG,"查询有数据！");
            LitePal.deleteAll(RefundResultNoticeReqData.class,"pay_order_id = ?",order.getPay_order_id());
        }

    }


    /** 界面数据初始化 */
    private void updateViewData(){


        try {
            tvOrderId.setText("");
            tvGatewayOrderId.setText("");
            tvReferNo.setText("");
            tvTraceNo.setText("");
            tvBatchNo.setText("");
            tvOrderMount.setText("");
            tvOrderTime.setText("");
            tvOrderPayType.setText("");
            tvOrderPayStatus.setText("");
            /**
             * 按钮状态初始化
             */
            updateButton("","","");

            if(order!=null){
                //收款单号
                String payOrderId = order.getPayment_no();
                tvOrderId.setText(payOrderId);
                //通道订单号
                String gatewayOrderIdStr = order.getGateway_order_id();
                String gatewayOrderId = "";
                if(Utils.isNotEmpty(gatewayOrderIdStr)){
                    gatewayOrderId = gatewayOrderIdStr;
                }
                tvGatewayOrderId.setText(gatewayOrderId);
                //交易参考号
                String referNoStr = order.getRefer_no();
                String referNo = "";
                if(Utils.isNotEmpty(referNoStr)){
                    referNo = referNoStr;
                }
                tvReferNo.setText(referNo);
                //凭证号
                String traceNoStr = order.getTrace_no();
                String traceNo = "";
                if(Utils.isNotEmpty(traceNoStr)){
                    traceNo = traceNoStr;
                }
                tvTraceNo.setText(traceNo);
                //批次号
                String batchNoStr = order.getBatch_no();
                String batchNo = "";
                if(Utils.isNotEmpty(batchNoStr)){
                    batchNo = batchNoStr;
                }
                tvBatchNo.setText(batchNo);
                //交易金额
                Integer tradeAmountStr = order.getTrade_amount();
                String tradeAmount = "";
                if(Utils.isNotEmpty(String.valueOf(tradeAmountStr))){
                    String goodsPriceStr = DecimalUtil.StringToPrice(String.valueOf(tradeAmountStr));
                    tradeAmount = DecimalUtil.branchToElement(goodsPriceStr);

                }
                tvOrderMount.setText(String.format(getResources().getString(R.string.trade_amount), tradeAmount));
                //支付时间
                String payTimeStr = order.getTrade_time();
                String payTime = "";
                if(Utils.isNotEmpty(payTimeStr)){
//            payTime = DateTimeUtil.stampToFormatDate(Long.parseLong(payTimeStr), "yyyy-MM-dd HH:mm:ss");
                    payTime = payTimeStr;
                }
                tvOrderTime.setText(payTime);
                //支付方式
                String payTypeStr = order.getTrade_type();
                tvOrderPayType.setText(Constants.getPayWay(payTypeStr));
                //交易类型 0-收款 1-退款
                String typeStr = order.getType();
                //支付状态 0、未支付 1、支付成功 2、支付失败 3、结果未知 (注:交易状态如果未知,则需要手动去查询状态)
                String tradeStatusStr = order.getTrade_status();

                String tradeStatus = getResources().getString(R.string.unknown);
                if(Utils.isNotEmpty(typeStr)){
                    if("0".equals(typeStr)){
                        if(Utils.isNotEmpty(tradeStatusStr)){
                            if("0".equals(tradeStatusStr)){
                                tradeStatus = "未支付";
                                tvOrderPayStatus.setTextColor(ContextCompat.getColor(this,R.color.grey_666666));
                            }else if("1".equals(tradeStatusStr)){
                                tradeStatus = getResources().getString(R.string.payment_success);
                                tvOrderPayStatus.setTextColor(ContextCompat.getColor(this,R.color.green_34d390));
                            }else if("2".equals(tradeStatusStr)){
                                tradeStatus = getResources().getString(R.string.payment_fail);
                                tvOrderPayStatus.setTextColor(ContextCompat.getColor(this,R.color.red_FF4400));
                            }
                        }
                    }else if("1".equals(typeStr)){
                        if(Utils.isNotEmpty(tradeStatusStr)){
                            tradeStatus = getResources().getString(R.string.refund_success);
                            tvOrderPayStatus.setTextColor(ContextCompat.getColor(this,R.color.green_34d390));
                        }
                    }
                }
                tvOrderPayStatus.setText(tradeStatus);
                //是否允许退款 (0-不允许 1-允许) --收款流水状态
                String isRefundStr = String.valueOf(order.getIs_refund());
                //是否已经退款(0-未退款 1-已退款) 收款流水状态
                String alreadyRefundStr = String.valueOf(order.getAlready_refund());
                updateButton(typeStr,isRefundStr,alreadyRefundStr);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 操作按钮状态
     * payType 交易类型 0-收款 1-退款
     * isRefund 是否允许退款 (0-不允许 1-允许) --收款流水状态
     * alreadyRefund 是否已经退款(0-未退款 1-已退款) 收款流水状态
     */
    private void updateButton(String payType,String isRefund,String alreadyRefund){
        if(Utils.isNotEmpty(payType)){
            if("0".equals(payType)){
                if(Utils.isNotEmpty(isRefund)){
                    if("0".equals(isRefund)){
                        btRefund.setClickable(false);
                        btRefund.setText("未开启退款开关");
                        btRefund.setTextColor(ContextCompat.getColor(activity,R.color.grey_666666));
                        btRefund.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
                    }else if("1".equals(isRefund)){

                        tvRefundHintMsg.setVisibility(View.VISIBLE);
                        btRefund.setClickable(true);
                        btRefund.setText("退款");
                        btRefund.setTextColor(ContextCompat.getColor(activity,R.color.white_ffffff));
                        btRefund.setBackgroundColor(ContextCompat.getColor(activity,R.color.blue_2496F9));

                    }else{
                        btRefund.setClickable(false);
                        btRefund.setText("退款");
                        btRefund.setTextColor(ContextCompat.getColor(activity,R.color.white_ffffff));
                        btRefund.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
                    }
                }else{
                    btRefund.setClickable(false);
                    btRefund.setText("退款");
                    btRefund.setTextColor(ContextCompat.getColor(activity,R.color.white_ffffff));
                    btRefund.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
                }
            }else if("1".equals(payType)){
                if(Utils.isNotEmpty(alreadyRefund)){
                    if("0".equals(alreadyRefund)){
                        btRefund.setClickable(false);
                        btRefund.setText("未退款");
                        btRefund.setTextColor(ContextCompat.getColor(activity,R.color.grey_666666));
                        btRefund.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
                    }else if("1".equals(alreadyRefund)){
                        btRefund.setClickable(false);
                        btRefund.setText("已退款");
                        btRefund.setTextColor(ContextCompat.getColor(activity,R.color.grey_666666));
                        btRefund.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
                    }else{
                        btRefund.setClickable(false);
                        btRefund.setText("退款");
                        btRefund.setTextColor(ContextCompat.getColor(activity,R.color.white_ffffff));
                        btRefund.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
                    }

                }else{
                    btRefund.setClickable(false);
                    btRefund.setText("退款");
                    btRefund.setTextColor(ContextCompat.getColor(activity,R.color.white_ffffff));
                    btRefund.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
                }
            }

        }else{
            btRefund.setClickable(false);
            btRefund.setText("退款");
            btRefund.setTextColor(ContextCompat.getColor(activity,R.color.white_ffffff));
            btRefund.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
        }

    }



    /**
     * 退款结果同步服务器
     */
    private void updateOrderStatus(final RefundResultNoticeReqData reqData){

        showWaitDialog();

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
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    //继续执行更新订单状态步骤
                    queryDatabase();
                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    //继续执行更新订单状态步骤
                    queryDatabase();
                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    //继续执行更新订单状态步骤
                    queryDatabase();
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
                //继续执行更新订单状态步骤
                queryDatabase();

            }else{
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"退款结果同步失败！",1);
                }
                //继续执行更新订单状态步骤
                queryDatabase();


            }

        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"退款结果同步失败！",1);
            //继续执行更新订单状态步骤
            queryDatabase();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"退款结果同步失败！",1);
            //继续执行更新订单状态步骤
            queryDatabase();
        }
    }


    /**
     * 支付成功但通知失败保存订单支付信息到数据库提示
     */
    private void showResultsPromptDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.results_prompt_dialog, null);
        //描述信息
        TextView tvMsg = view.findViewById(R.id.results_prompt_tvMsg);
        tvMsg.setText("本地保存的有未同步云端的订单信息，请先进行订单同步！");
        //操作按钮
        final Button btCancel = (Button) view.findViewById(R.id.results_prompt_btCancel);
        final Button btSuccess = (Button) view.findViewById(R.id.results_prompt_btSuccess);

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
                //如果通知失败，但保存订单成功，只需重新通知
                updateOrderStatus(refundResultNoticeReqData);

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


                //删除该订单信息
                deleteOrder();


                mDialog.dismiss();
                finish();

            }
        });
        //点击屏幕和物理返回键dialog不消失
        mDialog.setCancelable(false);
        mDialog.show();
    }



    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.pay_order_details_btRefund:
                if(Utils.isFastClick()){
                    return;
                }
                String payType = order.getTrade_type();
                intent = new Intent();
                intent.setClass(activity,RefundActivity.class);
                intent.putExtra("posInitData",posInitData);
                intent.putExtra("order",order);
                intent.putExtra("payType",payType);
                startActivity(intent);

                break;
            default:
                break;

        }
    }
}
