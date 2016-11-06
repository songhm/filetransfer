package com.example.file_transfer.utils;

import com.example.file_transfer.application.MyApplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetUtil {
	
	public static boolean isWifiActive() {
		// TODO Auto-generated method stub
		ConnectivityManager mConnectivity = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if(mConnectivity != null){
			NetworkInfo[] infos = mConnectivity.getAllNetworkInfo();
			
			if(infos != null){
				for(NetworkInfo ni: infos){
					if("WIFI".equals(ni.getTypeName()) && ni.isConnected())
						return true;
				}
			}
		}
		return false;
	}
	//获取本地IP地址
	public static String getlocalip(){  
		WifiManager wifiManager = (WifiManager)MyApplication.getInstance().getSystemService(Context.WIFI_SERVICE);    
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();    
	    int ipAddress = wifiInfo.getIpAddress();   
	    if(ipAddress==0)return null;  
	        Log.v("IP", "ipAddress");
	        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."  
	                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));  
	} 
	
	//获取本机MAC地址
	public static String getLocalMacAddress(){
			WifiManager wifi = (WifiManager)MyApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			return info.getMacAddress();
	}
	//打开网络设置界面           
	public static void openSetting(Activity activity)       
	{           
		if (android.os.Build.VERSION.SDK_INT > 10) {
			// 3.0以上打开设置界面，也可以直接用ACTION_WIFI_SETTINGS打开到wifi界面
			activity.startActivity(new Intent(
					android.provider.Settings.ACTION_SETTINGS));
		} else {
			activity.startActivity(new Intent(
					android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
	}  
}