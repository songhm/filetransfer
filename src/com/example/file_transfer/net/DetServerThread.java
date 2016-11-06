package com.example.file_transfer.net;

import java.io.IOException;
import java.net.ServerSocket;

import android.util.Log;

public class DetServerThread implements Runnable{
	private ServerSocket ss;
	private String TAG = "DecServerThread";
	public DetServerThread(ServerSocket ss) {
		super();
		this.ss = ss;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			try {
				ss.accept();
			} catch (IOException e) {
				Log.v(TAG,"decServer off");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}
