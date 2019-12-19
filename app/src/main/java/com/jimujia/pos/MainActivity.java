package com.jimujia.pos;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jimujia.pos.bean.PosInitData;

import org.litepal.LitePal;
import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout homeLayout,myLayout;//首页,我的
    private ImageView homeImg,myImg;
    private TextView homeText,myText;

    int tabIndex = 0;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    MainHomeFragment homeFragment;
    MainMyFragment myFragment;

    private static final int REQUEST_PERMISSION = 0;//动态权限注册请求码



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().addActivity(activity);
        registerPermission();
        initView();
        initListener();
        initFragments();
        //创建LitePal数据库
        LitePal.getDatabase();

    }

    /** 初始化控件 */
    private void initView(){
        homeLayout = findViewById(R.id.main_tab_homeLayout);
        myLayout = findViewById(R.id.main_tab_myLayout);
        homeImg = findViewById(R.id.main_tab_homeImg);
        myImg = findViewById(R.id.main_tab_myImg);
        homeText = findViewById(R.id.main_tab_homeText);
        myText = findViewById(R.id.main_tab_myText);
    }

    private void initListener(){
        //注册tab点击时间
        homeLayout.setOnClickListener(this);
        myLayout.setOnClickListener(this);
    }


    private void initFragments() {
        homeFragment = new MainHomeFragment();
        myFragment = new MainMyFragment();
        fragmentList.add(homeFragment);
        fragmentList.add(myFragment);
        for (Fragment fragment : fragmentList) {
            addFragment(fragment);
        }
        //初始化加载项
        int tabIndex = getIntent().getIntExtra("tabIndex", 0);
        changeHomeTab(tabIndex);
        homeImg.setImageDrawable(getResources().getDrawable(R.drawable.main_home_checd_icon));
        homeText.setTextColor(getResources().getColor(R.color.blue_2496F9));
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.main_content_frame, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void hideFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().hide(fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void changeHomeTab(int index) {
//        setTitle(titleList.get(index));
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i == index) {
                showFragment(fragmentList.get(i));
            } else {
                hideFragment(fragmentList.get(i));
            }
        }
    }

    /**
     * 初始化所有tab
     */
    private void resetImg(){
        homeImg.setImageDrawable(getResources().getDrawable(R.drawable.main_home_nochecd_icon));
        homeText.setTextColor(getResources().getColor(R.color.gray_7B8196));
        myImg.setImageDrawable(getResources().getDrawable(R.drawable.main_my_nochecd_icon));
        myText.setTextColor(getResources().getColor(R.color.gray_7B8196));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_tab_homeLayout:
                tabIndex = 0;
                //先初始化所有Tab
                resetImg();
                homeImg.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.main_home_checd_icon));
                homeText.setTextColor(ContextCompat.getColor(activity,R.color.blue_2496F9));
                break;
            case R.id.main_tab_myLayout:
                tabIndex = 1;
                //先初始化所有Tab
                resetImg();
                myImg.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.main_my_checd_icon));
                myText.setTextColor(ContextCompat.getColor(activity,R.color.blue_2496F9));
                break;
            default:

                break;
        }
        changeHomeTab(tabIndex);

    }






    /**
     * Android6.0动态注册权限
     */
    private void registerPermission(){
        /**
         *默认安装禁止SD卡的读写权限，以下方式打开权限
         */
        try {
            PackageManager pkgManager = getPackageManager();

            // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
            boolean sdCardWritePermission =
                    pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

            // read phone state用于获取 imei 设备信息
            boolean phoneSatePermission =
                    pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

            //相机权限
            boolean cameraPermission = pkgManager.checkPermission(Manifest.permission.CAMERA,getPackageName()) == PackageManager.PERMISSION_GRANTED;
            //sd卡读取权限
            boolean sdCardReadPermission = pkgManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,getPackageName()) == PackageManager.PERMISSION_GRANTED;

            if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission || !cameraPermission || !sdCardReadPermission) {
                requestPermission();
            }else{

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void requestPermission() {
        try {
            ActivityCompat.requestPermissions(this, new String[]
                            {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            },
                    REQUEST_PERMISSION);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode){
                case REQUEST_PERMISSION:
                    if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                    } else {
//                        ToastUtil.showText(context,"权限拒绝！",1);
//                        //没有权限
//                        new AlertDialog.Builder(activity)
//                                .setTitle("提示")
//                                .setMessage("应用必须打开[允许安装未知来源应用，SD卡读写，相机拍照]权限，请去设置中开启权限")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        JumpPermissionManagement.goToSettings(activity);
//                                        dialog.dismiss();
//
//                                    }
//                                }).show();

                    }
                    break;
                default:

                    break;
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**  退出应用提示窗口 */
    private void showColseAPPDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.close_app_dialog, null);
        TextView btok = (TextView) view.findViewById(R.id.close_dialog_tvOk);
        TextView btCancel = (TextView) view.findViewById(R.id.close_dialog_tvCancel);
        final Dialog myDialog = new Dialog(this,R.style.dialog);
        Window dialogWindow = myDialog.getWindow();
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        myDialog.setContentView(view);
        btok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //关闭应用
                finish();
                myDialog.dismiss();

            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    //拦截/屏蔽返回键、MENU键实现代码
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            showColseAPPDialog();
        }
        else if(keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
            //监控/拦截菜单键
            return true;
        }
        else if (keyCode == KeyEvent. KEYCODE_HOME) {
            //(屏蔽HOME键3)

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 打开应用签到失败提示框
     */
    private void showSignInFailDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.update_hint_dialog, null);
        //标题
        TextView tvTitle = view.findViewById(R.id.update_hint_tvTitle);
        tvTitle.setText("签到失败");
        //版本号：
        TextView tvVersion = view.findViewById(R.id.update_hint_tvVersion);
        tvVersion.setText("");
        //描述信息
        TextView tvMsg = view.findViewById(R.id.update_hint_tvMsg);
        tvMsg.setText(getString(R.string.signIn_fail_msg));
        //操作按钮
        final Button btUpdate = (Button) view.findViewById(R.id.update_hint_btUpdate);
        final Dialog mDialog = new Dialog(this,R.style.dialog);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        mDialog.setContentView(view);
        btUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();

            }
        });
        //点击屏幕和物理返回键dialog不消失
        mDialog.setCancelable(false);
        mDialog.show();
    }
}
