package com.jimujia.pos.sdkutil;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


import com.jimujia.pos.bean.OrderDetailData;
import com.jimujia.pos.bean.PosInitData;
import com.jimujia.pos.bean.StoreInfoResData;
import com.jimujia.pos.utils.DateTimeUtil;
import com.jimujia.pos.utils.DecimalUtil;
import com.jimujia.pos.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**  
 * 打印数据操作类
 */
public class FuyouPrintUtil {

	public static final int PRINT_REQUEST_CODE = 99;


	public static final int ERROR_NONE = 0;  //正常状态，无错误
	public static final int ERROR_PAPERENDED = 240;  //缺纸，不能打印
	public static final int ERROR_HARDERR = 242;     //硬件错误

	/** 统一空格字符串   */
	private static final String twoSpaceStr = "  ";
	private static final String threeSpaceStr = "   ";
	private static final String fiveSpaceStr = "     ";
	private static final String sixSpaceStr = "      ";
	private static final String sevenSpaceStr = "       ";
	private static final String eightSpaceStr = "        ";
	private static final String nineSpaceStr = "         ";

    /** 
     *  商户信息打印(pos签到成功后)
     */
    public static String businessInfoPrintText(PosInitData posInitData,StoreInfoResData storeInfoResData) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        //打印文字
        try {
            jsonArray.put(PrintUtils.setStringContent("商户信息", 2, 3));
            jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
            jsonArray.put(PrintUtils.setStringContent("商户名称：" + posInitData.getMername_pos(), 1, 2));
            jsonArray.put(PrintUtils.setStringContent("门店名称：" + storeInfoResData.getStore_name(), 1, 2));
            jsonArray.put(PrintUtils.setStringContent("终端名称：" + storeInfoResData.getTerminal_name(), 1, 2));
            jsonArray.put(PrintUtils.setStringContent("POS商户号：" + posInitData.getMercId_pos(), 1, 2));
            jsonArray.put(PrintUtils.setStringContent("POS设备号：" + posInitData.getTrmNo_pos(), 1, 2));
            jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
            jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
            jsonArray.put(PrintUtils.setfreeLine("5"));
            jsonObject.put("spos", jsonArray);
            return jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

















}
