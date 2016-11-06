package com.example.file_transfer.net;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.PublicKey;
import java.util.Map;
import com.example.file_transfer.utils.IsBreak;
import com.example.file_transfer.utils.FileHelper;
import com.example.file_transfer.utils.RSA;
import android.util.Log;
/**
 * 发送文件服务端
 */
public class NetTcpFileSendThread implements Runnable{
	String TAG="NetTcpSend";
	@SuppressWarnings("unused")
	private String recIp;
	private IsBreak isBreak =new IsBreak();
	public NetTcpFileSendThread(){
		Log.v(TAG,"netTcpSend constru");
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket[] socket=new Socket[5];
		ThreadSend[] sendThread=new ThreadSend[5];
		Map<String, Key> mmap=null;
		try {
			mmap = RSA.generateKeyPair();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Key publicKey=mmap.get("publicKey");
		Key privateKey=mmap.get("privateKey");
		byte[] publicKeyByte=RSA.publicKeyToByte((PublicKey) publicKey);
		Log.v(TAG,"publicKeyByte length: "+publicKeyByte.length);
		for (int i = 0; i < 5; i++){
			try {
				socket[i] = FileHelper.server.accept();
				recIp=socket[i].getInetAddress().getHostAddress();
				
			}
			catch(Exception e){
				Log.v(TAG,"send Exception");
				/*
				 * 捕捉到异常关闭所有的socket
				 */
				for (int j=i;j>=0;j--)
					if (socket[j]!=null){
						try {
							socket[j].getInputStream().close();
							socket[j].getOutputStream().close();
							socket[j].close();
							Log.v(TAG,"close the send socket");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						socket[j] = null;
					}
			}
		
		}
		for (int i=0;i<5;i++)
		{
			/*
			 * 开启发送文件线程
			 */
			sendThread[i]=new ThreadSend(socket[i],privateKey,publicKeyByte,isBreak);
			Thread thread=new Thread(sendThread[i]);
			thread.start();
		}
	}

}
