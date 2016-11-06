package com.example.file_transfer.net;

import java.io.File;
import com.example.file_transfer.application.MyApplication;
import com.example.file_transfer.data.MsgConst;
import com.example.file_transfer.utils.IsBreak;
import com.example.file_transfer.utils.recMD5;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * 接收文件客户端
 */
public class NetTcpFileReceiveThread implements Runnable {

	private final static String TAG = "NetTcpRec";
	private MyApplication mApplication;
	private IsBreak isBreak = new IsBreak(); // 是否某一接受线程无法正常继续
	private String senderIp; // 发送方IP地址
	private String mac;
	private String savePath; // 文件保存路径
	private String fileName;
	private String abspath;
	private long fileSize;
	private ThreadReceive[] receiveThread = new ThreadReceive[5]; // 接受文件的五个线程

	public NetTcpFileReceiveThread(String senderIp, String abspath,
			String fileName, long fileSize, String mac) {
		mApplication = MyApplication.getInstance();

		this.senderIp = senderIp;
		this.abspath = abspath;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.mac = mac;
		Log.v("debugREC", "fileName: " + fileName);
		savePath = mApplication.getMyself().getReceviceFilePath();
		Log.i(TAG, "接受路径为：" + savePath);
		// 判断接收文件的文件夹是否存在，若不存在，则创建
		File fileDir = new File(savePath);
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
	}

	@Override
	public void run() {
		
		Log.v(TAG, "netTcpFileRec run");
		
		// TODO Auto-generated method stub
		Log.v(TAG, "file total bytes:" + fileSize);

		long blockSize = fileSize / 5; // 每个线程至少传输的字节数
		long remain = fileSize - blockSize * 5; // 剩余的字节数
		File receiveFile = new File(savePath + fileName);
		long start = 0;
		
		for (int j = 0; j < 5; j++) {
			if (remain != 0) {
				// 将多余的字节依次分配给接受线程
				remain--;
				receiveThread[j] = new ThreadReceive(senderIp, receiveFile,
						start, blockSize + 1, j, mac, abspath, isBreak);
				Thread thread = new Thread(receiveThread[j]);
				thread.start();
				start += blockSize + 1;
			} else {
				receiveThread[j] = new ThreadReceive(senderIp, receiveFile,
						start, blockSize, j, mac, abspath, isBreak);
				Thread thread = new Thread(receiveThread[j]);
				thread.start();
				start += blockSize;
			}
		}

		boolean finished = false; // 是否所有线程都结束工作

		long beforeRec = 0;
		while (!finished) {
			try {
				Thread.sleep(1000); // 1s刷新一次
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finished = true;
			long recTotal = 0; // 已经正常接收的字结束

			long speed = 0;
			for (int k = 0; k < 5; k++) {
				if (receiveThread[k].finish == false)
					finished = false;
				recTotal += receiveThread[k].havaRec;
				/*
				 * if (beforeRec == 0) beforeRec = recTotal; else { speed =
				 * recTotal - beforeRec; long s=speed/1024;
				 * Log.v(TAG,"speed: "+s); beforeRec = recTotal; }
				 */
			}
			if (beforeRec == 0)
				beforeRec = recTotal;
			else {
				speed = recTotal - beforeRec;
				long s = speed / 1024;
				Log.v(TAG, "speed: " + s);
				beforeRec = recTotal;
			}
			Bundle bundle = new Bundle();
			bundle.putLong("recTotal", recTotal);
			bundle.putLong("speed", speed);
			bundle.putString("ip", senderIp);
			bundle.putString("abspath", abspath);
			Log.v(TAG, "recTotal: " + recTotal + ", speed: " + speed / 1024
					+ "K/s, " + "sIp: " + senderIp);
			Message msg = new Message();
			msg.what = MsgConst.FILERECEIVEINFO;
			msg.setData(bundle);
			mApplication.sendMessage(msg);

		}
		boolean breakDown = isBreak.getBool();
		if (!breakDown) {
			for (int k = 0; k < 5; k++) { // 文件接收成功后，将此文件的数据库信息删除
				mApplication.getCreateDB().delete(abspath, mac, "" + k);
				Log.v(TAG, "thread:" + k + " finish, db delete");
			}
			
			
			Bundle bundle = new Bundle();
			bundle.putString("ip", senderIp);
			bundle.putString("abspath", abspath);
			bundle.putBoolean("IsSuc", false);
			Message msgSendSuc = new Message();
			msgSendSuc.what = MsgConst.FILERECEIVESUCCESS;
			msgSendSuc.setData(bundle);
			mApplication.sendMessage(msgSendSuc);
			
			recMD5 rec_md5 = new recMD5(senderIp, receiveFile,abspath);
			Thread rec_md5_th = new Thread(rec_md5);
			rec_md5_th.start();
			while (!rec_md5.finish) {
				
			}
			Log.v("debug","rec MD5");
			// 发送文件接受成功的msg
			if (rec_md5.isOK) {
				Bundle mbundle = new Bundle();
				mbundle.putString("ip", senderIp);
				mbundle.putString("abspath", abspath);
				mbundle.putBoolean("IsSuc", true);
				Message mmsgSendSuc = new Message();
				mmsgSendSuc.what = MsgConst.FILERECEIVESUCCESS;
				mmsgSendSuc.setData(mbundle);
				mApplication.sendMessage(mmsgSendSuc);
				Log.v(TAG, "rec success");
			}
		}

	}

}
