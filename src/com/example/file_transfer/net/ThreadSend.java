package com.example.file_transfer.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.security.Key;
import javax.crypto.SecretKey;

import com.example.file_transfer.utils.AES;
import com.example.file_transfer.utils.FileHelper;
import com.example.file_transfer.utils.IsBreak;
import com.example.file_transfer.utils.RSA;

import android.util.Log;

/**
 * 发送文件的线程
 */
public class ThreadSend implements Runnable {
	String TAG = "ThreadSend";
	Socket socket; // 连接的socket
	boolean finish;
	private byte[] buffer = new byte[4096];
	private int threadNO; // 线程编号
	private Key privateKey;
	private byte[] publicKeyByte;
	private IsBreak isBreak;
	public long haveSend=0;
	public String abspath;

	BufferedOutputStream bos = null;
	BufferedInputStream bis = null;
	RandomAccessFile fdis = null;

	public ThreadSend(Socket socket, Key privateKey, byte[] publicKeyByte,
			IsBreak isBreak) {
		super();
		this.socket = socket;
		// this.abspath = abspath;
		this.privateKey = privateKey;
		this.publicKeyByte = publicKeyByte;
		finish = false;
		this.isBreak = isBreak;
	}

	@Override
	public void run() {
		Log.v(TAG, "send thread run");
		// TODO Auto-generated method stub

		try {
			bos = new BufferedOutputStream(socket.getOutputStream());
			bis = new BufferedInputStream(socket.getInputStream());
			// 从文件接收方接收到消息（ipmsg）
			int mlen = bis.read(buffer);
			String ipmsgStr = new String(buffer, 0, mlen, "gbk");
			Log.v("send", "收到TCP" + ipmsgStr);
			// 初始化ipmsg
			IpMessageProtocol ipmsgPro = new IpMessageProtocol(ipmsgStr);
			String fileNoStr = ipmsgPro.getAdditionalSection();
			String[] fileNoArray = fileNoStr.split("\\$");
			threadNO = Integer.valueOf(fileNoArray[0]);
			long sendbytes = Long.valueOf(fileNoArray[1]);
			long length = Long.valueOf(fileNoArray[2]);
			long start = Long.valueOf(fileNoArray[3]);
			abspath = String.valueOf(fileNoArray[4]);
			String ipA = socket.getInetAddress().getHostAddress();

			String ip_name_no = ipA + abspath + threadNO;
			if (!FileHelper.sendMap.containsKey(ip_name_no))
			{
				FileHelper.sendMap.remove(ip_name_no);
				FileHelper.sendMap.put(ip_name_no, this);
			}
				
			File sendFile = new File(abspath); // 要发送的文件
			// 确定文件发送的初始位置
			fdis = new RandomAccessFile(sendFile, "r");
			fdis.seek(sendbytes);
			Log.v(TAG, "thread:" + threadNO + " start at: " + start
					+ ", sendbytes at: " + sendbytes + ", length:" + length);

			int rlen = 0;
			haveSend = sendbytes - start; // 发送的总字节数
			Log.v(TAG, "thread:" + threadNO + "hava send " + haveSend);

			if (sendbytes == start) {
				Log.v(TAG, "endcode");
				bos.write(publicKeyByte);
				bos.flush();
				int AesSize = bis.read(buffer);

				byte[] Aes = new byte[AesSize];
				for (int i = 0; i < AesSize; i++) {
					Aes[i] = buffer[i];
				}
				byte[] AESKeyByte = RSA.decrypt(Aes, privateKey);
				SecretKey sk = AES.byteToSecretKey(AESKeyByte);
				byte[] tmpBuffer = new byte[8192];
				int mtmp = 0;
				while (mtmp < 4096) {
					if (fdis != null) {
						rlen = fdis.read(buffer);
						for (int i = 0; i < rlen; i++) {
							tmpBuffer[(int) (mtmp + i)] = buffer[i];
						}
						mtmp += rlen;
					}
				}
				byte[] b = new byte[4096];
				for (int i = 0; i < 4096; i++) {
					b[i] = tmpBuffer[i];
				}
				byte[] sendE = new byte[4096];

				sendE = AES.encrypt(b, sk);
				bos.write(sendE, 0, 4096);
				if (mtmp > 4096)
					bos.write(tmpBuffer, 4096, mtmp - 4096);
				haveSend += mtmp;
			}
			Log.v(TAG, "send thread " + threadNO + " send " + haveSend
					+ "bytes");
			// 读取本地文件到buffer，发送buffer
			while (!finish) {
				if (haveSend >= length) { // 成功发送所有字节
					finish = true;
					Log.v(TAG, "send thread " + threadNO + " finish normally");
					break;
				}
				if (fdis != null) {
					rlen = fdis.read(buffer);
				}
				if (rlen == -1) {
					finish = true;
					isBreak.setBool(true);
					Log.v("debug", "thread " + threadNO + " break");
					break;
				}
				bos.write(buffer, 0, rlen);
				bos.flush();
				// Log.v("send",rlen+"bytes");
				haveSend = haveSend + rlen;
				// Log.v("debug","thread: "+threadNO+
				// " send"+haveSend+"bytes in total");

			}
			bos.flush();
			Log.v(TAG, "thread: " + threadNO + " send " + haveSend + "/"
					+ length + " bytes in total");
		} catch (Exception e) {
			finish = true;
			isBreak.setBool(true);
			// TODO Auto-generated catch block
			Log.v("debug", "send thread: " + threadNO + "exception");
			e.printStackTrace();
			stopThread();
		}
		if (socket != null) {
			/*
			 * 关闭socket
			 */
			finish = true;
			try {
				if (bis == null)
					Log.e(TAG, "bis is null");
				bis.close();
				fdis.close();
				bos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Log.v(TAG, "run over");
		}
	}

	public void stopThread() {
		finish = true;
/*		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		if (socket != null) {
			Log.v(TAG, "stopThread start to invoke");
			
			isBreak.setBool(true);
			try {
				if (bis == null)
					Log.e(TAG, "bis is null");
				if (fdis == null)
					Log.e(TAG, "fdis is null");
				if (bos == null)
					Log.e(TAG, "bos is null");
				if (socket == null)
					Log.e(TAG, "socket is null");
				bis.close();
				fdis.close();
				bos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Log.v(TAG, "stopThread invoked");
		}
	}
}
