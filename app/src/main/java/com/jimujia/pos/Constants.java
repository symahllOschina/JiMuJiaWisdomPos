package com.jimujia.pos;


import com.jimujia.pos.utils.DateFormatUtils;
import com.jimujia.pos.utils.RandomStringGenerator;
import com.jimujia.pos.utils.Utils;

import java.util.Date;

/**
 *  常量帮助类
 */
public class Constants {


    /**
     * 接口请求响应码
     */
    public static final String RETURN_SUCCESS = "00";
    public static final String RETURN_FAILE = "99";

    /**
     * 支付类型选择
     * 1、银联 2、微信 3、支付宝 4 银联二维码
     */
    public static final String PAYTYPE_BANK = "1";
    public static final String PAYTYPE_WX = "2";
    public static final String PAYTYPE_ALI = "3";
    public static final String PAYTYPE_UNIONPAY = "4";





    /**
     * 获取交易方式
     */
    public static String getPayWay(String trade_type){
        String payWay = "未知";
        if(Utils.isNotEmpty(trade_type)){
            if(PAYTYPE_BANK.equals(trade_type)){
                return "银行卡";
            }else if(PAYTYPE_WX.equals(trade_type)){
                return "微信";
            }else if(PAYTYPE_ALI.equals(trade_type)){
                return "支付宝";
            }
        }
        return payWay;
    }

    public static final String SYMBOL = "￥";


}
