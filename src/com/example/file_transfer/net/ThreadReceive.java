package com.example.file_transfer.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.security.Key;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import javax.crypto.SecretKey;

import com.example.file_transfer.application.MyApplication;
import com.example.file_transfer.utils.AES;
import com.example.file_transfer.utils.FileHelper;
import com.example.file_transfer.utils.IsBreak;
import com.example.file_transfer.utils.RSA;

import android.util.Log;
/**
 * �����ļ����߳�
 */
public class ThreadReceive implements Runnable{
	String TAG="ThreadRec";
	private MyApplication mApplication;
	private String serverIp; //���ͷ�IP
	public File file;	//���յ��ļ�
	private long start;	//�����ļ�����ʼλ��
	private long length;	//��Ҫ���յĴ�С
	private int NO;	//�����̱߳��
	private String mac;	//���ͷ���MAC��ַ
	private String abspath;	//�ļ�λ�ڷ��ͷ��ľ���·��

	private int bufferSize=4096;
	byte[] buffer=new byte[bufferSize];
	boolean finish;	
	private String selfName = "android";
	private IsBreak isBreak;
	private Socket socket;
	public RandomAccessFile randFile;
	private BufferedOutputStream bos;
	private BufferedInputStream bis;
	long havaRec=0;	//��ǰ�߳̽��յ��ֽ���
	private long sended;	//���շ���Ҫ�����ļ�����ʼλ��
	public ThreadReceive(String serverIp, File file, long start,long length,
			int NO, String mac, String abspath, IsBreak isBreak) {
		super();
		mApplication = MyApplication.getInstance();
		this.serverIp = serverIp;
		this.file = file;
		this.start=start;
		this.length = length;
		this.NO = NO;
		this.mac = mac;
		this.abspath = abspath;
		this.isBreak=isBreak;
		finish=false;
		Log.v("debugREC","·����"+file.getAbsolutePath());
		Log.v(TAG,"start: "+start+", length: "+length);
	}
	@Override
	public void run() {
		Log.v(TAG,"thread: "+NO+" run");
		String no=""+NO;
		//��ȡ���ݿ���Ϣ���õ���ǰ�̷߳����ļ�����ʼ�ص㣬ʵ�ֶϵ�����
		long receivedLength=mApplication.getCreateDB().getLength(abspath,no);
		Log.v(TAG,"thread"+NO+" read db to get length:" +receivedLength);
		//�õ����ͷ���MAC��ַ
		String getmac=mApplication.getCreateDB().getMAC(abspath);
		
		
		
		//���MAC��ַΪ�գ�˵�����ļ�����Ҫ�ϵ�����
		if (getmac==null) sended=start;
		//���MAC��ַ�͵�ǰ���ͷ���MAC��ַһ������ϵ�����
		else if (getmac.equals(mac))
		{
			 sended= receivedLength;
		}
		else sended=start;
		havaRec=sended-start;
		//��ʼ�� ipmsgȻ���͸����ͷ�
		IpMessageProtocol ipmsgPro = new IpMessageProtocol();
		
		ipmsgPro.setCommandNo(IpMessageConst.IPMSG_GETFILEDATA);
		ipmsgPro.setSenderName(selfName);
		
		String additionStr =  NO + "$" + sended + "$" + length + "$"+ start + "$"+abspath+"$";
		ipmsgPro.setAdditionalSection(additionStr);
		
		// TODO Auto-generated method stub
		try {
			socket= new Socket(serverIp,IpMessageConst.PORT) ;
			bos= new BufferedOutputStream(socket.getOutputStream());
			bis= new BufferedInputStream(socket.getInputStream());
			byte[] sendBytes = ipmsgPro.getProtocolString().getBytes("gbk");
			//��ipmsg���͸����ͷ�
			bos.write(sendBytes, 0, sendBytes.length);
			Log.v(TAG,"rec send: "+additionStr);
			bos.flush();
			Log.v("debugREC","send is ok");
			String ipA=socket.getInetAddress().getHostAddress();
			String ip_name_no=ipA+abspath+NO;
			if (!FileHelper.recMap.containsKey(ip_name_no))
			{
				FileHelper.recMap.remove(ip_name_no);
				FileHelper.recMap.put(ip_name_no, this);
			}
				
			Log.v("debugREC","thread:"+NO+" recMap refresh");
			//ȷ���ļ�д�����ʼλ��
			randFile=new RandomAccessFile(file, "rwd");
			Log.v("debugREC","thread: "+NO+" randFile");
			randFile.seek(sended);
			
			int len=0;
			boolean breakInCode=false;
			if (havaRec==0)
			{
				Log.v(TAG,"decode");
				int enlength=bis.read(buffer);
				byte[] publicKeyByte=new byte[enlength];
				for (int j=0;j<enlength;j++)
					publicKeyByte[j]=buffer[j];
				Key publicKey=RSA.byteToPublicKey(publicKeyByte);
				Log.v("debugRecEn","publicKey length: "+publicKeyByte.length);
				SecretKey sk = AES.getSecretKey();
				byte[] skByte=AES.secretKeyToByte(sk);
				Log.v("debugRecEn","sk length: "+skByte.length);
				byte[] ensk=RSA.encrypt(skByte, publicKey);
				Log.v("debugRecEn","ensk length: "+ensk.length);
				bos.write(ensk);
				bos.flush();
				
				byte[] recBuffer=new byte[8192];
				int mtmp=0;
				
				while(mtmp<4096){
					if (bis != null) {
	                    len = bis.read(buffer);
	                    if (len==-1) {breakInCode=true;break;}
	                    Log.v("debugRecEn","thread "+NO+": "+len);
						for (int i=0;i<len;i++){
							recBuffer[mtmp+i]=buffer[i];
						}
					mtmp+=len;	
					}
                }
			if (!breakInCode){
				byte[] b=new byte[4096];
				for (int i=0;i<4096;i++)
					b[i]=recBuffer[i];
				byte[] recE=new byte[4096];
				recE=AES.decrypt(b, sk);

				randFile.write(recE, 0, 4096);
				if (mtmp>4096){
					randFile.write(recBuffer, 4096, mtmp-4096);
				}
				sended += mtmp;	
				havaRec=havaRec+mtmp;
				Log.v("debugRecEn","rec thread: "+NO+" send "+ havaRec+" bytes");
				}
			}
			//д���ļ�
			while((!breakInCode)&&(!finish))
			{
				//Log.v(TAG, "thread:"+NO+" while");
				if ((sended-start)>=length) { //��ǰ�߳��Ѿ���ɹ���
					finish=true;Log.v(TAG,"finish");
					break;
					}
				 if (bis != null) {  
                       len = bis.read(buffer);  
                   }
				
				if (len==-1) {
					if ((sended-start)<length) {
						/* δȫ���������ֽ���
						 * �������ݿ��¼�ɹ����յ��ֽ���
						 * �����Ժ�ϵ�����
						 */
						mApplication.getCreateDB().delete(abspath,mac,""+NO);
						mApplication.getCreateDB().save(abspath,mac,sended,""+NO);
						Log.v(TAG,"thread: "+NO+" len:"+len+" db save");
						finish=true;
						isBreak.setBool(true);
					}
					else{
						/* �ɹ��������ֽ���
						 * �������ݿ��¼�ɹ����յ��ֽ���
						 */
						mApplication.getCreateDB().delete(abspath,mac,""+NO);
						mApplication.getCreateDB().save(abspath,mac,sended,""+NO);
						finish=true;
					}
					break;}
				if (!socket.isConnected()) 
				{
					mApplication.getCreateDB().delete(abspath,mac,""+NO);
					mApplication.getCreateDB().save(abspath,mac,sended,""+NO);
					Log.v(TAG,"send: "+sended+", not connected to the server");
					finish=true;
					}
				randFile.write(buffer, 0, len);
				sended += len;	//�ѽ����ļ���С
				havaRec=havaRec+len;
				
			}
			Log.v(TAG,"Rec thread: "+NO+" ends");
		} 
		catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			Log.v("debugREC","file not found");
			e2.printStackTrace();
		}catch (Exception e) {
			/*
			 * һ�����쳣���������ݿ��¼�ɹ������ֽ���
			 */
			mApplication.getCreateDB().delete(abspath,mac,""+NO);
			mApplication.getCreateDB().save(abspath,mac,sended,""+NO);
			isBreak.setBool(true);
			finish=true;
			
			Log.v(TAG,"thread: "+NO+" exception");
		}
		if (socket!=null){
			/*
			 * �ر�socket
			 */
			try {
				bis.close();
				bos.close();
				socket.close();
				//randFile.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void stopThread()
	{
		finish=true;
		isBreak.setBool(true);
		mApplication.getCreateDB().delete(abspath,mac,""+NO);
		mApplication.getCreateDB().save(abspath,mac,sended,""+NO);
		if (socket!=null){
			/*
			 * �ر�socket
			 */
			try {
				bis.close();
				bos.close();
				socket.close();
				randFile.close();
			Log.v("debugREC","all close");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	public File cancle()
	{
		finish=true;
		isBreak.setBool(true);
		mApplication.getCreateDB().delete(abspath,mac,""+NO);
		if (socket!=null){
			/*
			 * �ر�socket
			 */
			try {
				bis.close();
				bos.close();
				socket.close();
				randFile.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			bis=null;
			bos=null;
			socket=null;
		}
		return file; 
	}

}
