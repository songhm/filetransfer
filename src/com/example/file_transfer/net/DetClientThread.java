package com.example.file_transfer.net;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

import com.example.file_transfer.application.MyApplication;
import com.example.file_transfer.data.User;

public class DetClientThread implements Runnable{
	private MyApplication mApplication;
	private String TAG = "DetClientThread";
	public DetClientThread(MyApplication mApplication)
	{
		this.mApplication=mApplication;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			Map<String,User> connecteduserlist = mApplication.getConnectedUsers();
			Iterator<Entry<String,User> > it = connecteduserlist.entrySet().iterator();
			while(it.hasNext()){
				Entry<String,User> entry = it.next();
				User user = entry.getValue();
				if(user.getIsConnected()){
					Thread th=new Thread(new SingleClientThread(user.getIp(),mApplication));
					th.start();
				}
				
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Log.v(TAG, "exception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	

}
