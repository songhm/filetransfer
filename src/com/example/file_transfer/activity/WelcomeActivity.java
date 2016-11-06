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
		//����db����������
		CreateDB db = new CreateDB(this);
		mApplication.setCreateDB(db);
		//��ȡ�û�������Ϣ
		SetMyInfo();
		//�����ݿ��ȡ������ʷ��¼
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
		//������λ
		InitLocation();
		mApplication.getLocationClient().start();
		
	}
	private void getRecordFromDB() {
		// TODO Auto-generated method stub
		mApplication.getRecords().addAll(mApplication.getCreateDB().getRecords());
	}
	private void SetMyInfo() {
		// TODO Auto-generated method stub
		//��ȡ�Լ�
		me = mApplication.getMyself();
		//����mac��ַ
		me.setMac(NetUtil.getLocalMacAddress());
		SharedPreferences preference = this.getSharedPreferences("myself", Context.MODE_PRIVATE);
		//��ȡ����
		String alias = preference.contains("Alias")?preference.getString("Alias", ""):android.os.Build.MANUFACTURER+" "+android.os.Build.MODEL;
		//��ȡ����·��
		String filepath = preference.contains("FilePath")?preference.getString("FilePath", ""):getSDPath()+"/WindRec/";
		//��ȡ�Ƿ��ļ�����Ҫ����
		Boolean needrequest = preference.contains("NeedRequest")?preference.getBoolean("NeedRequest",false):false;
		me.setAlias(alias);
		me.setReceiveFilePath(filepath);
		me.setIsNeedRequest(needrequest);
	}
	private String getSDPath(){ 
	       File sdDir = null; 
	       boolean sdCardExist = Environment.getExternalStorageState()   
	                           .equals(android.os.Environment.MEDIA_MOUNTED);   //�ж�sd���Ƿ���� 
	       if   (sdCardExist)   
	       {                               
	         sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼ 
	      }   
	       return sdDir.toString(); 
	       
	}
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
		option.setCoorType("gcj02");//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		int span=5000;
		option.setScanSpan(span);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(false);
		mApplication.getLocationClient().setLocOption(option);
	}
}
