package com.jimujia.pos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jimujia.pos.utils.NetworkUtils;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 定义父Activtiy
 */
public class BaseActivity extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
    protected App myApp;
    @ViewInject(R.id.toolbar)
    protected Toolbar mToolbar;
    @ViewInject(R.id.toolbar_title)
    private TextView mToolbarTitle;
    private ProgressDialog waitingDialog;

    protected BaseActivity activity;
    public Map<String,String> map;


    /**
     * 百度语音回调通知
     */
    protected Handler mainHandler;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.toolbar_layout);
        mainHandler = new Handler() {
            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handle(msg);
            }

        };
        myApp = (App) getApplication();
        activity = this;
        App.getInstance().addActivity(this);
        x.view().inject(this);
        if (mToolbar != null) {
            //将Toolbar显示到界面
            setSupportActionBar(mToolbar);
            if (isShowBacking()) {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }
        if (mToolbarTitle != null) {
            //getTitle()的值是activity的android:lable属性值
            mToolbarTitle.setText(getTitle());
            //设置默认的标题不显示
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }



    }

    protected void handle(Message msg) {
        int what = msg.what;
        /*switch (what) {
            case MainHandlerConstant.PRINT:
                //百度语音回调
                break;
            default:
                break;
        }*/
    }

    private void scrollLog(String message) {
        Spannable colorMessage = new SpannableString(message + "\n");
        colorMessage.setSpan(new ForegroundColorSpan(0xff0000ff), 0, message.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * 是否显示返回按钮
     */
    protected boolean isShowBacking() {

        return true;
    }

    @Override
    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mToolbar != null && mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        } else {
            super.setTitle(title);
        }
    }

    public void showWaitDialog() {
        showWaitDialog("正在加载...");
    }

    public void showWaitDialog(String msg) {
        if (this.isFinishing() || this.isDestroyed()) {
            return;
        }
        if (waitingDialog == null) {
            waitingDialog = new ProgressDialog(this, R.style.Translucent_Theme);
//        waitingDialog.setTitle("我是一个等待Dialog");
            waitingDialog.setMessage(msg);
            waitingDialog.setIndeterminate(true);
            //waitingDialog.setCancelable(false);
            waitingDialog.setCanceledOnTouchOutside(false);
        }
        waitingDialog.show();
    }

    public void hideWaitDialog() {
        if (waitingDialog != null && !(this.isFinishing() || this.isDestroyed())) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
    }





    public void snackError(String msg) {
        snack(msg, Prompt.ERROR);
    }

    private void snack(String msg, Prompt prompt) {
        snack(msg, prompt, null);
    }

    private void snack(String msg, Prompt prompt, SnackbarAction action) {
        final ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content).getRootView();
        TSnackbar snackbar = TSnackbar.make(viewGroup, msg, TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(prompt);
        if (action != null) {
            snackbar.setAction(action.title, action.onClickListener);
        }
        snackbar.show();
    }

    public boolean checkNetwork() {
        if (!NetworkUtils.isAvailable(this)) {
            snackError("网络未连接");
            return false;
        }
        return true;
    }
    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onBackPressed() {
        hideWaitDialog();
        onBack(map);
    }

    private void onBack(Map<String,String> map){
        if(map!=null){
            Set<String> keySet = map.keySet();
            Iterator<String> it = keySet.iterator();
            while(it.hasNext()){
                String key = it.next();
                String value = map.get(key);
                Intent in = new Intent();
                in.putExtra(key,value);
                setResult(RESULT_OK,in);
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑

    }



}
