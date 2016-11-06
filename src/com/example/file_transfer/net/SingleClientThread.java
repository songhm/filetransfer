package com.example.file_transfer.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.file_transfer.application.MyApplication;
import com.example.file_transfer.data.MsgConst;

import android.os.Message;
import android.util.Log;

public class SingleClientThread implements Runnable{

	private String TAG = "SingleClientThread";
	private String ip;
	private MyApplication mApplication;
	
	public SingleClientThread(String ip, MyApplication mApplication) {
		super();
		this.ip = ip;
		this.mApplication = mApplication;
	}

	@Override
	public void run() {
		try {
			new Socket(ip, IpMessageConst.DEC_PORT);
		} catch (UnknownHostException e) {
			Message m = new Message();
			m.what=MsgConst.USEROFF;
			m.obj=ip;
			mApplication.sendMessage(m);
			Log.v(TAG, "Unknow EXo");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			Message m = new Message();
			m.what=MsgConst.USEROFF;
			m.obj=ip;
			mApplication.sendMessage(m);
			Log.v(TAG, "IO tfBOYS");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}
	

}
