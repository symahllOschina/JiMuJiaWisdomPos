package com.jimujia.pos.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.jimujia.pos.bean.OrderDetailData;
import com.jimujia.pos.bean.PayResultNoticeReqData;
import com.jimujia.pos.bean.PosInitData;
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
 * 订单详情界面
 */
@ContentView(R.layout.activity_order_details)
public class OrderDetailsActivity extends BaseActivity implements OnClickListener {


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


	@ViewInject(R.id.order_details_tvOrderId)
	TextView tvOrderId;
	@ViewInject(R.id.order_details_tvCustomerName)
	TextView tvCustomerName;
	@ViewInject(R.id.order_details_tvCustomerPhone)
	TextView tvCustomerPhone;
	@ViewInject(R.id.order_details_tvReceiptTypeName)
	TextView tvReceiptTypeName;
	@ViewInject(R.id.order_details_tvDecorateAddress)
	TextView tvDecorateAddress;
	@ViewInject(R.id.order_details_tvProductName)
	TextView tvProductName;
	@ViewInject(R.id.order_details_tvOrderMount)
	TextView tvOrderMount;
	@ViewInject(R.id.order_details_tvPaidAmount)
	TextView tvPaidAmount;
	@ViewInject(R.id.order_details_tvLastPaymentTime)
	TextView tvLastPaymentTime;
	@ViewInject(R.id.order_details_tvDebtAmount)
	TextView tvDebtAmount;
	@ViewInject(R.id.order_details_tvDiscountAmount)
	TextView tvDiscountAmount;
	@ViewInject(R.id.order_details_tvOrderTime)
	TextView tvOrderTime;
	
	/** 付款，退款  */
	@ViewInject(R.id.order_details_btPay)
	Button btPay;




	private PosInitData posInitData;
	private OrderDetailData order;//订单对象


	/**
	 * 支付成功通知请求参数对象
	 */
	private PayResultNoticeReqData payResultNoticeReqData;

	/**
	 * 标注onResume()方法执行顺序
	 */
	private int onCreateIndex = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("待收款详情");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");



		Intent intent = getIntent();
		posInitData = (PosInitData) intent.getSerializableExtra("posInitData");
		order = (OrderDetailData) intent.getSerializableExtra("order");




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
		btPay.setOnClickListener(this);
	}



	/**
	 * 查询数据库（是否有未同步数据）
	 */
	private void queryDatabase(){
		List<PayResultNoticeReqData> list = LitePal.where("pay_order_id = ?",order.getPay_order_id()).find(PayResultNoticeReqData.class);
		if(list.size()>0){
			Log.e(TAG,"查询有数据！");
			this.payResultNoticeReqData = list.get(0);
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
		List<PayResultNoticeReqData> list = LitePal.where("pay_order_id = ?",order.getPay_order_id()).find(PayResultNoticeReqData.class);
		if(list.size()>0){
			Log.e(TAG,"查询有数据！");
			LitePal.deleteAll(PayResultNoticeReqData.class,"pay_order_id = ?",order.getPay_order_id());
		}

	}


	

	/** 界面数据初始化 */
    private void updateViewData(){
    	
    	try {
			tvOrderId.setText("");
			tvOrderTime.setText("");
			tvReceiptTypeName.setText("");
			tvOrderMount.setText("");
			tvDiscountAmount.setText("");
			tvPaidAmount.setText("");
			tvDebtAmount.setText("");
			tvLastPaymentTime.setText("");


			tvProductName.setText("");
			tvCustomerName.setText("");
			tvCustomerPhone.setText("");
			tvDecorateAddress.setText("");
			/**
			 * 按钮状态初始化
			 */
			updateButton("","");
			if(order!=null){
				//收款单号
				String payOrderId = order.getPay_order_id();
				tvOrderId.setText(payOrderId);
				//订单创建时间
				String orderTimeStr = order.getOrder_time();
				tvOrderTime.setText(orderTimeStr);
				//收款说明
				String receiptTypeName = order.getReceipt_type_name();
				tvReceiptTypeName.setText(receiptTypeName);
				//订单金额
				Integer orderMountStr = order.getOrder_amount();
				String orderMount = "";
				if(Utils.isNotEmpty(String.valueOf(orderMountStr))){
					orderMount = DecimalUtil.branchToElement(String.valueOf(orderMountStr));
					tvOrderMount.setText(Constants.SYMBOL+orderMount);
				}

				//优惠金额
				Integer orderDiscountAmountStr = order.getDiscount_amount();
				String orderDiscountAmount = "";
				if(Utils.isNotEmpty(String.valueOf(orderDiscountAmountStr))){
					orderDiscountAmount = DecimalUtil.branchToElement(String.valueOf(orderDiscountAmountStr));
					tvDiscountAmount.setText(Constants.SYMBOL+orderDiscountAmount);
				}

				//已支付金额
				Integer paidAmountStr = order.getPaid_amount();
				String paidAmount = "";
				if(Utils.isNotEmpty(String.valueOf(paidAmountStr))){
					paidAmount = DecimalUtil.branchToElement(String.valueOf(paidAmountStr));
					tvPaidAmount.setText(Constants.SYMBOL+paidAmount);
				}
				//待支付金额
				Integer debtAmountStr = order.getDebt_amount();
				String debtAmount = "";
				if(Utils.isNotEmpty(String.valueOf(debtAmountStr))){
					debtAmount = DecimalUtil.branchToElement(String.valueOf(debtAmountStr));
					tvDebtAmount.setText(Constants.SYMBOL+debtAmount);
				}
				//上次支付时间
				String lastPaymentTimeStr = order.getLast_payment_time();
				tvLastPaymentTime.setText(lastPaymentTimeStr);

				//套餐名称
				String productName = order.getProduct_name();
				String productNameStr = "";
				if(Utils.isNotEmpty(productName)){
					productNameStr = productName;
				}
				tvProductName.setText(productNameStr);
				//客户名称
				String customerName = order.getCustomer_name();
				tvCustomerName.setText(customerName);
				//客户电话
				String customerPhone = order.getCustomer_phone();
				tvCustomerPhone.setText(customerPhone);
				//装修地址
				String decorateAddress = order.getDecorate_address();
				tvDecorateAddress.setText(decorateAddress);
				//订单状态 0-正常 1-已关闭
				String isClosedStr = order.getIs_closed();
                //是否允许退款 (0-不允许 1-允许) 注:已关闭订单不能支付 已完成订单不能退款
                String isRefundStr = order.getIs_refund();
				updateButton(isClosedStr,isRefundStr);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/**
	 * 操作按钮状态
	 * 已关闭订单不能支付 已完成订单不能退款
	 */
	private void updateButton(String isClosed,String isRefund){
    	if(Utils.isNotEmpty(isClosed)){
			if("0".equals(isClosed)){
				btPay.setClickable(true);
				btPay.setText("发起收款");
				btPay.setTextColor(ContextCompat.getColor(activity,R.color.white_ffffff));
				btPay.setBackgroundColor(ContextCompat.getColor(activity,R.color.blue_2496F9));
			}else if("1".equals(isClosed)){
				btPay.setClickable(false);
				btPay.setText("订单已关闭");
				btPay.setTextColor(ContextCompat.getColor(activity,R.color.white_ffffff));
				btPay.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
			}
		}else{
			btPay.setClickable(false);
			btPay.setText("发起收款");
			btPay.setTextColor(ContextCompat.getColor(activity,R.color.white_ffffff));
			btPay.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
		}
	}

	/**
	 *  （更新服务器订单状态）
	 */
	private void updateOrderStatus(final PayResultNoticeReqData payResultNoticeReqData){

		showWaitDialog();

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
				ToastUtil.showText(activity,"支付结果同步成功！",1);
				showPayResultsPromptDialog();

			}else if(Constants.RETURN_FAILE.equals(baseResponseData.getReturnCode())){
				if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
					ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
				}else{
					ToastUtil.showText(activity,"支付结果同步失败！",1);
				}
                //继续执行更新订单状态步骤
                queryDatabase();
			}else{
				if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
					ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
				}else{
					ToastUtil.showText(activity,"支付结果同步失败！",1);
				}
                //继续执行更新订单状态步骤
                queryDatabase();

			}

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ToastUtil.showText(activity,"支付结果同步失败！",1);
            //继续执行更新订单状态步骤
            queryDatabase();

		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ToastUtil.showText(activity,"支付结果同步失败！",1);
            //继续执行更新订单状态步骤
            queryDatabase();
		}
	}

	/**
	 * 支付成功通知
	 */
	private void showPayResultsPromptDialog(){
		View view = LayoutInflater.from(this).inflate(R.layout.results_prompt_dialog, null);
		//描述信息
		TextView tvMsg = view.findViewById(R.id.results_prompt_tvMsg);
		tvMsg.setText("订单记录已成功同步云端！");
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
				updateOrderStatus(payResultNoticeReqData);

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
		case R.id.order_details_btPay:
			if(Utils.isFastClick()){
				return;
			}
			intent = new Intent();
			intent.setClass(activity,PaymentActivity.class);
			intent.putExtra("posInitData",posInitData);
			intent.putExtra("order",order);
			startActivity(intent);

			break;

			default:
				break;
			
		}
	}
}
