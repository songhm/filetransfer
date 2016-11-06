package com.example.file_transfer.utils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.example.file_transfer.net.IpMessageConst;
import com.example.file_transfer.net.NetTcpFileReceiveThread;
import com.example.file_transfer.net.NetTcpFileSendThread;
import com.example.file_transfer.net.ThreadReceive;
import com.example.file_transfer.net.ThreadSend;

public class FileHelper {
	public static ServerSocket server;
	public static ServerSocket checkServer;
	public static Map<String, ThreadSend> sendMap = new HashMap<String, ThreadSend>();
	public static Map<String, ThreadReceive> recMap = new HashMap<String, ThreadReceive>();
    public static Map<String, String> md5Map = new HashMap<String, String>();
	String TAG="FileHelper";
	public FileHelper() {
		try {
			server = new ServerSocket(IpMessageConst.PORT);
			checkServer=new ServerSocket(IpMessageConst.CHECK_PORT);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("debug", "server is not open");
		}
	}

	public void sendFile() {
		NetTcpFileSendThread sendFileHelper = new NetTcpFileSendThread();
		Thread th = new Thread(sendFileHelper);
		th.start();
	}

	public void receiveFile(String senderIp, String abspath, String fileName,
			Long fileSize, String mac) {
		NetTcpFileReceiveThread receiveFileHelper = new NetTcpFileReceiveThread(
				senderIp, abspath, fileName, fileSize, mac);
		Thread th = new Thread(receiveFileHelper);
		th.start();
	}

	void breakSendFile(String ip_FileName) {

		for (int i = 0; i < 5; i++) {
			ThreadSend myth = sendMap.get(ip_FileName + i);
			myth.stopThread();

		}

	}

	void breakRecFile(String ip_FileName) {
		for (int i = 0; i < 5; i++) {
			ThreadReceive myth = recMap.get(ip_FileName + i);
			myth.stopThread();
			recMap.remove(ip_FileName+i);
		}
	}

	public void filePause(String ip_FileName, boolean flag) {
		if (flag){
			breakRecFile(ip_FileName);
			Log.v(TAG, "rec pause invoke");
		}
		else
			breakSendFile(ip_FileName);
	}

	public void fileCancel(String ip_name, boolean flag) {
		if (flag) {
			File f = null;
			for (int i = 0; i < 5; i++) {
				ThreadReceive myth = recMap.get(ip_name + i);
				
				f = myth.cancle();
				recMap.remove(ip_name+i);
			}
			f.delete();
		}
		else{
			breakSendFile(ip_name);
		}
	}
	public void getMD5(String filepath)
	{
		Thread th=new Thread(new getMD5Thread(filepath));
		th.start();
	}

}
