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
 * �����ļ��ͻ���
 */
public class NetTcpFileReceiveThread implements Runnable {

	private final static String TAG = "NetTcpRec";
	private MyApplication mApplication;
	private IsBreak isBreak = new IsBreak(); // �Ƿ�ĳһ�����߳��޷���������
	private String senderIp; // ���ͷ�IP��ַ
	private String mac;
	private String savePath; // �ļ�����·��
	private String fileName;
	private String abspath;
	private long fileSize;
	private ThreadReceive[] receiveThread = new ThreadReceive[5]; // �����ļ�������߳�

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
		Log.i(TAG, "����·��Ϊ��" + savePath);
		// �жϽ����ļ����ļ����Ƿ���ڣ��������ڣ��򴴽�
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

		long blockSize = fileSize / 5; // ÿ���߳����ٴ�����ֽ���
		long remain = fileSize - blockSize * 5; // ʣ����ֽ���
		File receiveFile = new File(savePath + fileName);
		long start = 0;
		
		for (int j = 0; j < 5; j++) {
			if (remain != 0) {
				// ��������ֽ����η���������߳�
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

		boolean finished = false; // �Ƿ������̶߳���������

		long beforeRec = 0;
		while (!finished) {
			try {
				Thread.sleep(1000); // 1sˢ��һ��
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finished = true;
			long recTotal = 0; // �Ѿ��������յ��ֽ���

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
			for (int k = 0; k < 5; k++) { // �ļ����ճɹ��󣬽����ļ������ݿ���Ϣɾ��
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
			// �����ļ����ܳɹ���msg
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
