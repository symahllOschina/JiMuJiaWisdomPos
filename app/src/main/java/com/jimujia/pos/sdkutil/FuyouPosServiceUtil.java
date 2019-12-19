package com.jimujia.pos.sdkutil;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jimujia.pos.Constants;

/**
 * 调用富友POS内置服务接口，支付，查询业务类
 */
public class FuyouPosServiceUtil {



    /**
     * 签到请求码
     */
    public static final int SIGN_REQUEST_CODE = 110;
    /**
     * 支付请求码
     */
    public static final int PAY_REQUEST_CODE = 1;
    /**
     * 扫码请求码
     */
    public static final int SCAN_REQUEST_CODE = 11;
    /**
     * 打印请求码
     */
    public static final int PRINT_REQUEST_CODE = 99;


    /**
     * 签到
     */
    public static void signInReq(Activity activity){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "签到");
            intent.putExtras(bundle);
            Log.e("签到Bundle的值：",bundle.toString());
            activity.startActivityForResult(intent, SIGN_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 扫码
     */
    public static void scanReq(Activity activity){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.NewSetScanCodeActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("flag", "true");
            intent.putExtras(bundle);
            Log.e("扫码Bundle的值：",bundle.toString());
            activity.startActivityForResult(intent, SCAN_REQUEST_CODE);


        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 消费，微信，支付宝，银联二维码支付
     *
     */
    public static void payReq(Activity activity, String payType, String total_fee, String order_noStr,boolean isFrontCamera){
        //String payType = "";//银行卡，微信，支付宝，银联二维码分别顺序对应：040,010,020,030
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            if(Constants.PAYTYPE_BANK.equals(payType)){
                bundle.putString("transName", "消费");
            }else if(Constants.PAYTYPE_WX.equals(payType)){
                bundle.putString("transName", "微信消费");
            }else if(Constants.PAYTYPE_ALI.equals(payType)){
                bundle.putString("transName", "支付宝消费");
            }else if(Constants.PAYTYPE_UNIONPAY.equals(payType)){
                bundle.putString("transName", "银联二维码消费");
            }
            bundle.putString("amount", total_fee);
            bundle.putString("orderNumber", order_noStr);
            if(!Constants.PAYTYPE_BANK.equals(payType)){
                //为true时调用打印；为false时不调用打印
                bundle.putString("isPrintTicket", "true");
                //是否打开前置摄像头(传true时，打开前置。传false不打开前置)
                if (isFrontCamera) {
                    bundle.putString("isFrontCamera", "true");
                }else{
                    bundle.putString("isFrontCamera", "false");

                }
            }
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            Log.e("支付SDK参数值：",bundle.toString());
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 扫码交易凭证号退款 不支持部分退（按富友技术的说法，这个方法叫做交易撤销，只能撤销全部金额，并且只支持当天交易）
     */
    public static void scanCancelReq(Activity activity,String payType,String total_fee,String oldTrace,String order_noStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            if(Constants.PAYTYPE_WX.equals(payType)){
                bundle.putString("transName", "微信退款");
            }else if(Constants.PAYTYPE_ALI.equals(payType)){
                bundle.putString("transName", "支付宝退款");
            }else if(Constants.PAYTYPE_UNIONPAY.equals(payType)){
                bundle.putString("transName", "银联二维码退款");
            }
            bundle.putString("amount", total_fee);
            bundle.putString("oldTrace", oldTrace);
            bundle.putString("orderNumber", order_noStr);
            bundle.putString("isPrintTicket", "true");//为true时调用打印；为false时不调用打印
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            Log.e("退款SDK参数值：",bundle.toString());
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 扫码交易扫退款码退款或交易参考号退款二选一 支持部分退
     * transName:扫码退款外调
     * referNo:交易参考号
     * scanVoidQrcode:退款码
     * isPrintTicket:"true"
     * amount:退款金额
     * orderNumber:订单号
     * version:版本号
     * isFrontCamera:是否打开前置摄像头
     */
    public static void scanRefundReq(Activity activity,String referNo,String refundQrcode,String total_fee,String order_noStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "扫码退款外调");
            bundle.putString("referNo", referNo);
            bundle.putString("scanVoidQrcode", refundQrcode);
            bundle.putString("amount", total_fee);
            bundle.putString("orderNumber", order_noStr);
            bundle.putString("isPrintTicket", "true");//为true时调用打印；为false时不调用打印
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            Log.e("退款SDK参数值：",bundle.toString());
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 银行卡消费撤销
     * 20191022测试SDK不支持部分退
     */
    public static void cardCancelReq(Activity activity,String oldTrace,String order_noStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "消费撤销");
            bundle.putString("amount", "");
            bundle.putString("oldTrace", oldTrace);//凭证号
            bundle.putString("isManagePwd", "false");//撤销时不显示输入密码(false-不显示输入密码，true-显示输入密码)
            bundle.putString("orderNumber", order_noStr);
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 银行卡收款部分退款时调用
     * 支持部分退
     */
    public static void cardRefundReq(Activity activity,String order_noStr,String amount){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "退货");
            bundle.putString("amount", amount);
            bundle.putString("orderNumber", order_noStr);
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 扫码查询（不支持补打印）
     */
    public static void scanQueryReq(Activity activity,String orderIdStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "订单号查询");
//            bundle.putString("orderNumber", orderIdStr);//订单号	商户定义的订单号
            bundle.putString("orderNumber", "FU2018081410091602561271243221");//订单号	商户定义的订单号
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("NotFoundException：", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 扫码查询（支持补打印：但仅支持正向交易）
     * 该接口支持银行卡、微信、支付宝消费交易联机查询，适用于当前查询终端本地没有结果时调用此接口查询服务端，补打凭条。撤销交易该接口暂不支持查询。
     */
    public static void scanQueryOrderReq(Activity activity,String payType,String oldTrace,String orderIdStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            if(payType.equals("WX")){
                bundle.putString("transName", "微信失败查询");
            }else if(payType.equals("ALI")){
                bundle.putString("transName", "支付宝失败查询");
            }else if(payType.equals("UNIONPAY")){
                bundle.putString("transName", "银联二维码消费失败交易查询");
            }else if(payType.equals("BANK")){
                bundle.putString("transName", "消费失败查询");
            }
            bundle.putString("oldTrace", oldTrace);
            bundle.putString("orderNumber", orderIdStr);//订单号	商户定义的订单号
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("NotFoundException：", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 预授权，预授权撤销，预授权完成，预授权完成撤销
     */
    public static void authReq(Activity activity,String type){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            if(type.equals("1")){
                bundle.putString("transName", "预授权");
            }else if(type.equals("2")){
                bundle.putString("transName", "预授权撤销");
            }else if(type.equals("3")){
                bundle.putString("transName", "预授权完成（请求）");
            }else if(type.equals("4")){
                bundle.putString("transName", "预授权完成撤销");
            }
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 结算（清空POS流水）
     */
    public static void settleReq(Activity activity){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "微信银行卡结算");
            bundle.putString("isPrintSettleTicket", "false");
            bundle.putString("version", "1.0.7");
            Log.e("调结算Bundle的值：",bundle.toString());
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    public static void printTextReq(Activity activity,String printTextStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.CustomPrinterActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("data", printTextStr);
            bundle.putString("isPrintTicket", "true");
            Log.e("调打印Bundle的值：",bundle.toString());
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PRINT_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }


}
