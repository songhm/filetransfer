package com.example.file_transfer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.example.file_transfer.net.IpMessageConst;

import android.util.Log;

public class sendMD5 implements Runnable {

	private String RecIp;

	public sendMD5(String RecIp) {
		this.RecIp = RecIp;
		Log.v("debug", "sendMD5 con");
	}

	@Override
	public void run() {
		Socket s = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			Log.v("debug", "sendMD run");
			byte[] buffer = new byte[1024];
			s = new Socket(RecIp, IpMessageConst.CHECK_PORT);
			Log.v("debug", "socket is ok");
			bos = new BufferedOutputStream(s.getOutputStream());
			bis = new BufferedInputStream(s.getInputStream());
			int len = bis.read(buffer);
			Log.v("debug", "len: " + len);
			String filepath = new String(buffer, 0, len, "gbk");
			Log.v("debug", "sendMD path: " + filepath);
			String sendStr = null;
			boolean haveSendMD = false;
			while (!haveSendMD) {

				if (FileHelper.md5Map.containsKey(filepath)) {
					sendStr = FileHelper.md5Map.get(filepath);
					Log.v("debug", "send File: " + filepath);
					Log.v("debug", "send md5: " + sendStr);
					byte[] b = sendStr.getBytes("gbk");
					bos.write(b);
					bos.flush();
					haveSendMD = true;
				}

			}

		} catch (IOException e) {
			Log.v("debug", "exc");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (s != null) {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub

	}

}
