package com.jimujia.pos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;


import org.xutils.view.annotation.ContentView;

/**
 * 欢迎界面（主要初始化一些应用数据到保存到本地）
 */
@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}
	
	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();
	}
	
	private void initData(){

		/*
		 * pos初始化数据
		 * 应用初始化（获取POS机商户信息）
		 * 执行签到获取参数
		 */
		boolean isPrint = false;
		Intent intent = new Intent();
		intent.setClass(activity,SignInActivity.class);
		intent.putExtra("isPrint",isPrint);//签到成功时是否打印
		startActivity(intent);
		finish();

	}

	

	






	

}
