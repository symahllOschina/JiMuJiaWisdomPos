package com.jimujia.pos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jimujia.pos.BaseActivity;
import com.jimujia.pos.Constants;
import com.jimujia.pos.NitConfig;
import com.jimujia.pos.R;
import com.jimujia.pos.adapter.OrderListAdapter;
import com.jimujia.pos.adapter.PayOrderListAdapter;
import com.jimujia.pos.bean.BaseResponseData;
import com.jimujia.pos.bean.OrderDetailData;
import com.jimujia.pos.bean.OrderListReqData;
import com.jimujia.pos.bean.OrderListResData;
import com.jimujia.pos.bean.PaginatedData;
import com.jimujia.pos.bean.PayOrderDetailData;
import com.jimujia.pos.bean.PayOrderListReqData;
import com.jimujia.pos.bean.PayOrderListResData;
import com.jimujia.pos.bean.PosInitData;
import com.jimujia.pos.requtil.ParamsReqUtil;
import com.jimujia.pos.utils.DateTimeUtil;
import com.jimujia.pos.utils.FastJsonUtil;
import com.jimujia.pos.utils.GsonUtils;
import com.jimujia.pos.utils.HttpURLConnectionUtil;
import com.jimujia.pos.utils.NetworkUtils;
import com.jimujia.pos.utils.ToastUtil;
import com.jimujia.pos.utils.Utils;
import com.jimujia.pos.view.ClearEditText;
import com.jimujia.pos.view.FuzzyQueryPopupWindow;
import com.jimujia.pos.view.XListView;

import org.json.JSONException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 交易明细Activity
 */
@ContentView(R.layout.activity_pay_order_list)
public class PayOrderListActivity extends BaseActivity implements View.OnClickListener,XListView.IXListViewListener,
                                                    AdapterView.OnItemClickListener,FuzzyQueryPopupWindow.OnScreeningClickListener {
    @ViewInject(R.id.menu_title_imageView)
    ImageView imgBack;
    @ViewInject(R.id.menu_title_layout)
    LinearLayout titleLayout;
    @ViewInject(R.id.menu_title_tvTitle)
    TextView tvTitle;
    @ViewInject(R.id.menu_title_imgTitleImg)
    ImageView imgTitleImg;
    @ViewInject(R.id.menu_title_layoutOption)
    LinearLayout layoutOption;
    @ViewInject(R.id.menu_title_imgOption)
    ImageView imgOption;
    @ViewInject(R.id.menu_title_tvOption)
    TextView tvOption;


    @ViewInject(R.id.pay_order_list_mXListView)
    XListView mListView;


    /**
     * 下拉刷新，上拉加载参数初始化
     */
    private int refreshCount = 1;
    private int pageNum = 1;//默认加载第一页
    private static final int pageNumCount = 20;//默认一页加载xx条数据（死值不变）
    //总条数
    private int orderListTotalCount = 0;
    //每次上拉获取的条数
    private int getMoerNum = 0;
    private static final int REFRESH = 100;
    private static final int LOADMORE = 200;
    private static final int NOLOADMORE = 300;
    private String loadMore = "0";//loadMore为1表示刷新操作  2为加载更多操作，

    //交易列表
    private List<PayOrderDetailData> list = new ArrayList<PayOrderDetailData>();
    private BaseAdapter mAdapter;


    private PosInitData posInitData;
    /**
     * 交易类型
     * tradeType :  0-收款 1-退款
     */
    private String tradeType = "";

    private FuzzyQueryPopupWindow popupWindow;
    private float alpha = 1;
    private static final int WINDOW_BG_ALPHA = 0x1001;

    String startDate,endDate,orderId = "",customerName = "",customerPhone = "";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("收款流水");
        imgTitleImg.setVisibility(View.GONE);
        imgOption.setVisibility(View.VISIBLE);
        tvOption.setVisibility(View.VISIBLE);
        tvOption.setText("筛选");



        Intent intent = getIntent();
        posInitData = (PosInitData) intent.getSerializableExtra("posInitData");
        tradeType = (String) intent.getSerializableExtra("tradeType");
        if("1".equals(tradeType)){
            tvTitle.setText("退款流水");
        }

        initView();
        initListener();

        mAdapter = new PayOrderListAdapter(activity,list);
        mListView.setAdapter(mAdapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
        startDate = DateTimeUtil.getAMonthDateStr(-8,"yyyyMMdd");
        endDate = DateTimeUtil.getSystemTime("yyyyMMdd");
        Log.e(TAG,"起始日期："+startDate);
        Log.e(TAG,"结束日期："+endDate);
        refreshCount = 1;
        loadMore = "1";
        pageNum = 1;
        getOrderList(pageNum,pageNumCount);
    }

    private void initView(){

        /**
         * ListView初始化
         */
        mListView.setPullLoadEnable(true);//是否可以上拉加载更多,默认可以上拉
        mListView.setPullRefreshEnable(true);//是否可以下拉刷新,默认可以下拉


    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        layoutOption.setOnClickListener(this);
        //注册刷新和加载更多接口
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
    }

    /**
     * 获取交易明细
     **/
    private void getOrderList(final int pageNum,final int pageCount){
        if(refreshCount == 1){

            showWaitDialog();
        }
        final String url = NitConfig.getPaymentsUrl;
        Log.e(TAG,"获取支付订单接口路径："+url);
        final PayOrderListReqData reqData = ParamsReqUtil.getPayOrderListReqData(posInitData,pageNum,pageCount,tradeType,orderId,customerName,customerPhone);
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(reqData);
                    Log.e("获取支付订单请求参数：", content);
                    String content_type = HttpURLConnectionUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content,content_type);
                    Log.e("获取支付订单返回信息：", jsonStr);
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
                case WINDOW_BG_ALPHA:
                    backgroundAlpha((float)msg.obj);
                    break;
                case REFRESH:
                    mAdapter.notifyDataSetChanged();
                    //更新完毕
                    onLoad();
                    break;
                case LOADMORE:
                    mListView.setPullLoadEnable(true);//是否可以上拉加载更多
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成
                    onLoad();
                    break;
                case NOLOADMORE:
                    mListView.setPullLoadEnable(false);//是否可以上拉加载更多
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成-->>已没有更多
                    onLoad();
                    break;
                case NetworkUtils.MSG_WHAT_ONE:
                    String orderListJsonStr=(String) msg.obj;
                    orderListJsonStr(orderListJsonStr);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                default:
                    break;
            }
        }
    };

    private void orderListJsonStr(String jsonStr){
        try{
            Gson gson  =  GsonUtils.getGson();
            BaseResponseData baseResponseData = gson.fromJson(jsonStr, BaseResponseData.class);
            if(Constants.RETURN_SUCCESS.equals(baseResponseData.getReturnCode())){
                Object returnDataObject = baseResponseData.getReturnData();
                if(returnDataObject!=null){
                    String returnDataStr = gson.toJson(returnDataObject);
                    if(Utils.isNotEmpty(returnDataStr)){
                        PayOrderListResData payOrderListResData = gson.fromJson(returnDataStr, PayOrderListResData.class);
                        //分页信息
                        PaginatedData paginated = payOrderListResData.getPaginated();
                        orderListTotalCount = Integer.parseInt(paginated.getTotal());
                        Log.e(TAG,"总条数："+orderListTotalCount);
                        //获取的list
                        List<PayOrderDetailData> orderList = new ArrayList<PayOrderDetailData>();
                        orderList = payOrderListResData.getPayment_list();
                        getMoerNum = orderList.size();
                        if(pageNum == 1){
                            list.clear();
                        }
                        list.addAll(orderList);
                        Log.e("查询数据：", list.size()+""+"条");
                        //关闭上拉或下拉View，刷新Adapter
                        if("0".equals(loadMore)){
                            if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
                                Message msg1 = new Message();
                                msg1.what = LOADMORE;
                                handler.sendEmptyMessageDelayed(LOADMORE, 0);
                            }else{
                                Message msg1 = new Message();
                                msg1.what = NOLOADMORE;
                                handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                            }
                        }else if("1".equals(loadMore)){
                            if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
                                Message msg1 = new Message();
                                msg1.what = REFRESH;
                                handler.sendEmptyMessageDelayed(REFRESH, 0);
                            }else{
                                Message msg1 = new Message();
                                msg1.what = NOLOADMORE;
                                handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                            }
                        }else if("2".equals(loadMore)){
                            Message msg1 = new Message();
                            msg1.what = LOADMORE;
                            handler.sendEmptyMessageDelayed(LOADMORE, 2000);
                        }
                    }else{
                        Message msg1 = new Message();
                        msg1.what = NOLOADMORE;
                        handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                        ToastUtil.showText(activity,"查询失败！",1);
                    }

                }else{
                    Message msg1 = new Message();
                    msg1.what = NOLOADMORE;
                    handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                    ToastUtil.showText(activity,"查询失败！",1);
                }
            }else if(Constants.RETURN_FAILE.equals(baseResponseData.getReturnCode())){
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"查询失败！",1);
                }
                Message msg1 = new Message();
                msg1.what = NOLOADMORE;
                handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
            }else{
                if(Utils.isNotEmpty(baseResponseData.getReturnMsg())){
                    ToastUtil.showText(activity,baseResponseData.getReturnMsg(),1);
                }else{
                    ToastUtil.showText(activity,"查询失败！",1);
                }
                Message msg1 = new Message();
                msg1.what = NOLOADMORE;
                handler.sendEmptyMessageDelayed(NOLOADMORE, 0);

            }

        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Message msg1 = new Message();
            msg1.what = NOLOADMORE;
            handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
            ToastUtil.showText(activity,"查询失败！",1);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Message msg1 = new Message();
            msg1.what = NOLOADMORE;
            handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
            ToastUtil.showText(activity,"查询失败！",1);
        }
    }


    /**
     * 背景渐变暗
     */
    private void setWindowBackground(View v){
        popupWindow = new FuzzyQueryPopupWindow(this,orderId,customerName,customerPhone);
        popupWindow.showWindow();
        popupWindow.setOnScreeningClickListener(this);
        popupWindow.showAsDropDown(v, 0,0);
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        popupWindow.setOnDismissListener(new PayOrderListActivity.poponDismissListener());
        backgroundAlpha(1f);
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(alpha > 0.5f){
                    try {
                        //4是根据弹出动画时间和减少的透明度计算
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = handler.obtainMessage();
                    msg.what = WINDOW_BG_ALPHA;
                    //每次减少0.01，精度越高，变暗的效果越流畅
                    alpha-=0.01f;
                    msg.obj = alpha ;
                    handler.sendMessage(msg);
                }
            }

        }).start();
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void screeningListener(String orderId, String customerName, String customerPhone) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        refreshCount=1;
        loadMore = "1";
        pageNum = 1;
        getOrderList(pageNum,pageNumCount);
    }


    /**
     * 返回或者点击空白位置的时候将背景透明度改回来
     */
    class poponDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            new Thread(new Runnable(){
                @Override
                public void run() {
                    //此处while的条件alpha不能<= 否则会出现黑屏
                    while(alpha<1f){
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("HeadPortrait","alpha:"+alpha);

                        Message msg = handler.obtainMessage();
                        msg.what = WINDOW_BG_ALPHA;
                        alpha+=0.01f;
                        msg.obj =alpha ;
                        handler.sendMessage(msg);
                    }
                }

            }).start();
        }

    }



    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.menu_title_layoutOption:
                if(Utils.isFastClick()){
                    return;
                }
                if (popupWindow != null&&popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    return;
                } else {
                    //背景渐变暗
                    setWindowBackground(v);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //这里因为添加了头部，所以数据Item的索引值发生变化，即对应item应为：position-1；
        PayOrderDetailData order = list.get(position-1);
        Intent intent = new Intent();
        intent.setClass(activity,PayOrderDetailsActivity.class);
        intent.putExtra("posInitData",posInitData);
        intent.putExtra("order",order);
        startActivity(intent);


    }

    @Override
    public void onRefresh() {
        refreshCount++;
        loadMore = "1";
        pageNum = 1;
        getOrderList(pageNum,pageNumCount);
    }

    @Override
    public void onLoadMore() {

        refreshCount++;
        loadMore = "2";
        if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
            //已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
            pageNum = pageNum + 1;
            getOrderList(pageNum,pageNumCount);
        }else{
            //没有数据执行两秒关闭view
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = NOLOADMORE;
                    handler.sendMessage(msg);
                }
            }, 1000);

        }
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(new Date().toLocaleString());
    }
}
