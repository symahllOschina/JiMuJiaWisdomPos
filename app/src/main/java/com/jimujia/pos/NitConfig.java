package com.jimujia.pos;


import android.content.Context;

import com.jimujia.pos.utils.SharedPreferencesUtil;

/**
 * 服务地址管理类
 */
public class NitConfig {

	/**  打包前必看：
	 * 1，替换正式域名前缀(包括更新版本地址前缀) true:正式环境,false:测试环境
	 */
	/**
	 * 本地配置文件保存标识判断是否输入管理密码
	 */
	private static SharedPreferencesUtil sharedPreferencesUtil;
	public static boolean init(Context context){

		sharedPreferencesUtil = new SharedPreferencesUtil(context,SharedPreConstants.SELECT_SERVER_NAME);
		if(sharedPreferencesUtil.contain(SharedPreConstants.SELECT_SERVER_KEY_NAME)){
			boolean isServer = (boolean) sharedPreferencesUtil.getSharedPreference(SharedPreConstants.SELECT_SERVER_KEY_NAME,false);
			return isServer;
		}else{
			//默认选择生产
			return true;
		}

	}

	/**
	 * 测试环境
	 **/
	public static final String basePathTest =  				"http://api-cbs.jimujia.miaowudz.com";

	/**
	 * 生产环境
	 **/
	public static final String basePath =          			"http://api-cbs.work.jimujiazx.com";




	/**
	 * 查询门店信息
	 * http://api-e-payment.com/store/receipt/getBranchOrders
	 */
	public static final String getStoreInfoTestUrl = 	basePathTest +   "/api_app/order/order/storeInfo";
	public static final String getStoreInfoUrl = 			basePath +   "/api_app/order/order/storeInfo";


	/**
	 * 查询门店订单信息
	 * http://api-e-payment.com/store/receipt/getBranchOrders
	 */
	public static final String getBranchOrdersTestUrl = 	basePathTest +   "/api_app/order/order/getBranchOrders";
	public static final String getBranchOrdersUrl = 			basePath +   "/api_app/order/order/getBranchOrders";

	/**
	 * 获取支付流水号
	 */
	public static final String getPaymentNoTestUrl = 	basePathTest +   "/api_app/order/order/getPaymentNo";
	public static final String getPaymentNoUrl = 			basePath +   "/api_app/order/order/getPaymentNo";

	/**
	 * 订单支付结果通知
	 */
	public static final String paymentNoticeTestUrl = 	basePathTest +   "/api_app/notice/notice/paymentNotice";
	public static final String paymentNoticeUrl = 			basePath +   "/api_app/notice/notice/paymentNotice";

	/**
	 * 获取订单支付信息(可用于退单等)
	 */
	public static final String getPaymentsTestUrl = 	basePathTest +   "/api_app/order/order/getPayments";
	public static final String getPaymentsUrl = 			basePath +   "/api_app/order/order/getPayments";

	/**
	 * 获取退款流水号
	 */
	public static final String getRefundNoTestUrl = 	basePathTest +   "/api_app/order/order/getRefundNo";
	public static final String getRefundNoUrl = 			basePath +   "/api_app/order/order/getRefundNo";

	/**
	 * 订单退款结果通知
	 */
	public static final String refundNoticeTestUrl = 	basePathTest +   "/api_app/notice/notice/refundNotice";
	public static final String refundNoticeUrl = 		basePath +   "/api_app/notice/notice/refundNotice";

}
