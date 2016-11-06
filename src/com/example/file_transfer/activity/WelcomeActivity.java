package com.example.file_transfer.activity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.file_transfer.R;
import com.example.file_transfer.application.MyApplication;
import com.example.file_transfer.data.Myself;
import com.example.file_transfer.utils.CreateDB;
import com.example.file_transfer.utils.NetUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
public class WelcomeActivity extends Activity{
	
	private MyApplication mApplication;
	private Myself me;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		mApplication = (MyApplication) getApplication();
		//创建db辅助并共享
		CreateDB db = new CreateDB(this);
		mApplication.setCreateDB(db);
		//读取用户个人信息
		SetMyInfo();
		//从数据库读取发送历史记录
		getRecordFromDB();
		final Intent localIntent = new Intent(WelcomeActivity.this,MainActivity.class);
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				startActivity(localIntent);
			    finish();
			}
		};
		timer.schedule(task, 2000);
		//开启定位
		InitLocation();
		mApplication.getLocationClient().start();
		
	}
	private void getRecordFromDB() {
		// TODO Auto-generated method stub
		mApplication.getRecords().addAll(mApplication.getCreateDB().getRecords());
	}
	private void SetMyInfo() {
		// TODO Auto-generated method stub
		//获取自己
		me = mApplication.getMyself();
		//设置mac地址
		me.setMac(NetUtil.getLocalMacAddress());
		SharedPreferences preference = this.getSharedPreferences("myself", Context.MODE_PRIVATE);
		//获取名称
		String alias = preference.contains("Alias")?preference.getString("Alias", ""):android.os.Build.MANUFACTURER+" "+android.os.Build.MODEL;
		//获取接收路径
		String filepath = preference.contains("FilePath")?preference.getString("FilePath", ""):getSDPath()+"/WindRec/";
		//获取是否文件接收要请求
		Boolean needrequest = preference.contains("NeedRequest")?preference.getBoolean("NeedRequest",false):false;
		me.setAlias(alias);
		me.setReceiveFilePath(filepath);
		me.setIsNeedRequest(needrequest);
	}
	private String getSDPath(){ 
	       File sdDir = null; 
	       boolean sdCardExist = Environment.getExternalStorageState()   
	                           .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
	       if   (sdCardExist)   
	       {                               
	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
	      }   
	       return sdDir.toString(); 
	       
	}
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
		int span=5000;
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(false);
		mApplication.getLocationClient().setLocOption(option);
	}
}
