package com.jimujia.pos.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 响应头实体
 */
public class BaseResponseData implements Serializable {

    /**
     * 00-接收成功 99-查询失败
     * 如果支付操作中，积木家返回接收失败，该笔支付信息暂存在APP中， 由操作员点击重试按钮后再次发送
     */
    private String returnCode;
    /**
     * 错误提示信息(终端不合法、参数不全等)
     */
    private String returnMsg;
    /**
     * 返回结果集
     */
    private Object returnData;

    public BaseResponseData() {
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public Object getReturnData() {
        return returnData;
    }

    public void setReturnData(Object returnData) {
        this.returnData = returnData;
    }
}
