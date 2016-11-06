package com.example.file_transfer.net;

import java.io.IOException;
import java.net.ServerSocket;
import com.example.file_transfer.application.MyApplication;

public class Detector {
	private ServerSocket dSocket;
	private MyApplication mApplication;
	public Detector()
	{
		mApplication = MyApplication.getInstance();
		try {
			dSocket=new ServerSocket(IpMessageConst.DEC_PORT);
			Thread th=new Thread(new DetServerThread(dSocket));
			th.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void detUser()
	{
		Thread th = new Thread(new DetClientThread(mApplication));
		th.start();
	}
}
