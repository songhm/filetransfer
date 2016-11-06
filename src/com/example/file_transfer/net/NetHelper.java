package com.example.file_transfer.net;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.example.file_transfer.application.MyApplication;
import com.example.file_transfer.data.MsgConst;
public class NetHelper implements Runnable {

	private MyApplication mApplication;
	private boolean searching;
	private static final int BUFFERLENGTH = 1024; // �����С
	private static final String TAG = "NetHelper";
	private boolean onWork = false; // �̹߳�����ʶ
	private Thread udpThread = null; // ����UDP�����߳�
	private DatagramSocket udpSocket = null; // ���ڽ��պͷ���udp���ݵ�socket
	private DatagramPacket udpSendPacket = null; // ���ڷ��͵�udp���ݰ�
	private DatagramPacket udpResPacket = null; // ���ڽ��յ�udp���ݰ�
	private byte[] resBuffer = new byte[BUFFERLENGTH]; // �������ݵĻ���
	private byte[] sendBuffer = null;
	/*
	 * INITIAL UDPSOCKET&UDPRESPACHET
	 * �����̣߳�����UDP����
	 */
	public NetHelper() {
		mApplication = MyApplication.getInstance();
		searching = false;
		try {
			if (udpSocket == null) {
				udpSocket = new DatagramSocket(null);
				udpSocket.setReuseAddress(true);
				udpSocket.bind(new InetSocketAddress(MsgConst.PORT));
				Log.i(TAG, "connectSocket()....��UDP�˿�" + MsgConst.PORT + "�ɹ�");
			}
			if (udpResPacket == null)
				udpResPacket = new DatagramPacket(resBuffer, BUFFERLENGTH);
			onWork = true; // ���ñ�ʶΪ�̹߳���
			startThread(); // �����߳̽���udp����
		} catch (SocketException e) {
			e.printStackTrace();
			disconnectSocket();
			Log.e(TAG, "Nethelper��ʼ��....��UDP�˿�" + MsgConst.PORT + "ʧ��");
		}
	}
	public void startSearch() { // ����׼�������㲥��
		new Thread(new Runnable() {
			
			public void run() {
				searching = true;
		    	IpMessageProtocol ipmsgSend = new IpMessageProtocol();
				ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
				ipmsgSend.setCommandNo(MsgConst.STARTSEARCH); 
				ipmsgSend.setAdditionalSection(mApplication.getMyself().getMac()+'|'
						+mApplication.getMyself().getIp()+'|'
						+mApplication.getMyself().getAlias()+'|'
						+mApplication.getMyself().getLongtitude()+'|'
						+mApplication.getMyself().getLatitude()+ "\0");

				InetAddress broadcastAddr;
				try {
					broadcastAddr = InetAddress.getByName("255.255.255.255");
					sendUdpData(ipmsgSend.getProtocolString() + "\0", broadcastAddr,
							MsgConst.PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					Log.e(TAG, "startSearch()....�㲥��ַ����");
				}
			}
		}).start();
	}
	
	public synchronized void StopSearch() {
		new Thread(new Runnable() {
			
			public void run() {
				searching = false;
		    	IpMessageProtocol ipmsgSend = new IpMessageProtocol();
				ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
				ipmsgSend.setCommandNo(MsgConst.STOPSEARCH);
				ipmsgSend.setAdditionalSection(mApplication.getMyself().getAlias() + "\0"); // ������Ϣ������û����ͷ�����Ϣ

				InetAddress broadcastAddr;
				try {
					broadcastAddr = InetAddress.getByName("255.255.255.255"); // �㲥��ַ
					sendUdpData(ipmsgSend.getProtocolString() + "\0", broadcastAddr,
							MsgConst.PORT); // ��������
				} catch (UnknownHostException e) {
					e.printStackTrace();
					Log.e(TAG, "���߹㲥�С���....�㲥����");
				}
			}
		}).start();
	}
	public void requestConnect(final String ip){
		new Thread(new Runnable(){
            @Override
            public void run() {
                IpMessageProtocol ipmsgSend = new IpMessageProtocol();
                ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
                ipmsgSend.setCommandNo(MsgConst.REQUESTCONNECT);
                ipmsgSend.setAdditionalSection(mApplication.getMyself().getAlias() + "\0");
                try {
					InetAddress toTheAddress = InetAddress.getByName(ip);
					sendUdpData(ipmsgSend.getProtocolString(), toTheAddress, MsgConst.PORT);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            }
        }).start();
	}
	public void rejectConnect(final String ip){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				IpMessageProtocol ipmsgSend = new IpMessageProtocol();
                ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
                ipmsgSend.setCommandNo(MsgConst.REJECTCONNECT);
                ipmsgSend.setAdditionalSection(mApplication.getMyself().getAlias()+"\0");
                InetAddress toTheAddress;
				try {
					toTheAddress = InetAddress.getByName(ip);
					sendUdpData(ipmsgSend.getProtocolString(), toTheAddress, MsgConst.PORT);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	public void acceptConnect(final String ip){
		new Thread(new Runnable() {
			
			public void run() {
					IpMessageProtocol ipmsgSend = new IpMessageProtocol();
	                ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
	                ipmsgSend.setCommandNo(MsgConst.ACCEPTCONNECT);
	                ipmsgSend.setAdditionalSection(mApplication.getMyself().getAlias()+"\0");
	                InetAddress toTheAddress;
					try {
						toTheAddress = InetAddress.getByName(ip);
						sendUdpData(ipmsgSend.getProtocolString(), toTheAddress, MsgConst.PORT);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
			}
		}).start();
	}
	public void disConnect(final String ip){
		new Thread(new Runnable() {
			
			public void run() {
					IpMessageProtocol ipmsgSend = new IpMessageProtocol();
	                ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
	                ipmsgSend.setCommandNo(MsgConst.USEROFF);
	                ipmsgSend.setAdditionalSection(mApplication.getMyself().getAlias()+"\0");
	                InetAddress toTheAddress;
					try {
						toTheAddress = InetAddress.getByName(ip);
						sendUdpData(ipmsgSend.getProtocolString(), toTheAddress, MsgConst.PORT);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
			}
		}).start();
	}
	public void sendFile(final String ip,final String path,final long size) {
		new Thread(new Runnable() {
			
			public void run() {
				IpMessageProtocol ipmsgSend = new IpMessageProtocol();
                ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
                ipmsgSend.setCommandNo(MsgConst.RECEIVEFILE);
                ipmsgSend.setAdditionalSection(ip+"|"+path+"|"+Long.toString(size)+"\0");
                InetAddress toTheAddress;
				try {
					toTheAddress = InetAddress.getByName(ip);
					sendUdpData(ipmsgSend.getProtocolString(), toTheAddress, MsgConst.PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public void filePause(final String ip,final String path) {
		new Thread(new Runnable() {
			
			public void run() {
				IpMessageProtocol ipmsgSend = new IpMessageProtocol();
                ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
                ipmsgSend.setCommandNo(MsgConst.FILEPAUSE);
                ipmsgSend.setAdditionalSection(ip+"|"+path+"\0");
                InetAddress toTheAddress;
				try {
					toTheAddress = InetAddress.getByName(ip);
					sendUdpData(ipmsgSend.getProtocolString(), toTheAddress, MsgConst.PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public void fileCancel(final String ip,final String path) {
		new Thread(new Runnable() {
			
			public void run() {
				IpMessageProtocol ipmsgSend = new IpMessageProtocol();
                ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
                ipmsgSend.setCommandNo(MsgConst.FILECANCEL);
                ipmsgSend.setAdditionalSection(ip+"|"+path+"\0");
                InetAddress toTheAddress;
				try {
					toTheAddress = InetAddress.getByName(ip);
					sendUdpData(ipmsgSend.getProtocolString(), toTheAddress, MsgConst.PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public void sendFileInfo(final String ip, final String path, final Long currentSize,
			final Long speed) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			public void run() {
				IpMessageProtocol ipmsgSend = new IpMessageProtocol();
                ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
                ipmsgSend.setCommandNo(MsgConst.FILERECEIVEINFO);
                ipmsgSend.setAdditionalSection(path+"|"+currentSize+"|"+speed+"\0");
                InetAddress toTheAddress;
				try {
					toTheAddress = InetAddress.getByName(ip);
					sendUdpData(ipmsgSend.getProtocolString(), toTheAddress, MsgConst.PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public void sendFileSucInfo(final String ip, final String path) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			public void run() {
				IpMessageProtocol ipmsgSend = new IpMessageProtocol();
                ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
                ipmsgSend.setCommandNo(MsgConst.FILERECEIVESUCCESS);
                ipmsgSend.setAdditionalSection(path+"\0");
                InetAddress toTheAddress;
				try {
					toTheAddress = InetAddress.getByName(ip);
					sendUdpData(ipmsgSend.getProtocolString(), toTheAddress, MsgConst.PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public synchronized void sendUdpData(String sendStr, InetAddress sendto,
			int sendPort) { // ����UDP���ݰ��ķ���
		try {
			sendBuffer = sendStr.getBytes("gbk");
			// ���췢�͵�UDP���ݰ�
			udpSendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
					sendto, sendPort);
			udpSocket.send(udpSendPacket); // ����udp���ݰ�
			Log.i(TAG, "�ɹ���IPΪ" + sendto.getHostAddress() + "����UDP���ݣ�"
					+ sendStr);
			udpSendPacket = null;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e(TAG, "sendUdpData(String sendStr, int port)....ϵͳ��֧��GBK����");
		} catch (IOException e) { // ����UDP���ݰ�����
			e.printStackTrace();
			udpSendPacket = null;
			Log.e(TAG, "sendUdpData(String sendStr, int port)....����UDP���ݰ�ʧ��");
		}
	}

	@Override
	public void run() {
		while (onWork) {
			try {
				Log.v(TAG,"UDP�ȴ������С���");
				udpSocket.receive(udpResPacket);
				Log.v(TAG,"UDP�����������ݳɹ���");
			} catch (IOException e) {
				onWork = false;
				if (udpResPacket != null) {
					udpResPacket = null;
				}
				if (udpSocket != null) {
					udpSocket.close();
					udpSocket = null;
				}
				System.out.println(113);
				udpThread = null;
				Log.e(TAG, "UDP���ݰ�����ʧ�ܣ��߳�ֹͣ");
				break;
			}
			if (udpResPacket.getLength() == 0) {
				Log.i(TAG, "�޷�����UDP���ݻ��߽��յ���UDP����Ϊ��");
				continue;
			}
			String ipmsgStr = "";
			try {
				ipmsgStr = new String(resBuffer, 0, udpResPacket.getLength(),
						"gbk");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "��������ʱ��ϵͳ��֧��GBK����");
			}// ��ȡ�յ�������
			Log.i(TAG, "���յ���UDP��������Ϊ:" + ipmsgStr);
			
			IpMessageProtocol ipmsgPro = new IpMessageProtocol(ipmsgStr); //
			int commandNo = ipmsgPro.getCommandNo();
			System.out.println("UDP�������������Ϊ�� "+commandNo);
			switch (commandNo) {
			
			case MsgConst.STARTSEARCH: { // �յ��������ݰ�������û���������IPMSG_ANSENTRYӦ��
				if(searching){
					String userIp = udpResPacket.getAddress().getHostAddress();
					if (!userIp.equals(mApplication.getMyself().getIp())) {
					
						String additioninfo = ipmsgPro.getAdditionalSection();
						String[] addstr = additioninfo.split("\0");
						String[] userinfo = addstr[0].split("\\|");
					
						Bundle bundle = new Bundle();
						bundle.putString("mac", userinfo[0]);
						bundle.putString("ip", userinfo[1]);
						bundle.putString("alias", userinfo[2]);
						bundle.putDouble("longitude", Double.parseDouble(userinfo[3]));
						bundle.putDouble("latitude", Double.parseDouble(userinfo[4]));
					
						Message msg = new Message();
						msg.what = MsgConst.USERIN;
						msg.setData(bundle);
						mApplication.sendMessage(msg);
					
						// ���湹����ͱ�������
						IpMessageProtocol ipmsgSend = new IpMessageProtocol();
						ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
						ipmsgSend.setCommandNo(MsgConst.SEARCHBACK); // ���ͱ�������
						ipmsgSend.setAdditionalSection(mApplication.getMyself().getMac()+'|'
								+mApplication.getMyself().getIp()+'|'
								+mApplication.getMyself().getAlias()+'|'
								+mApplication.getMyself().getLongtitude()+'|'
								+mApplication.getMyself().getLatitude()+ "\0");
						sendUdpData(ipmsgSend.getProtocolString(),
								udpResPacket.getAddress(), udpResPacket.getPort()); // ��������
					}
			}}
				break;
			case MsgConst.STOPSEARCH: {
				if(!searching){System.out.println(100);break;}
				String userIp = udpResPacket.getAddress().getHostAddress();
				if (!userIp.equals(mApplication.getMyself().getIp())) {
					Message msg = new Message();
					msg.what = MsgConst.USEROUT;
					msg.obj=userIp;
					mApplication.sendMessage(msg);
					Log.i(TAG, "user:" + userIp + "ֹͣ����");
				}
			}
				break;
			case MsgConst.SEARCHBACK:{
				String userIp = udpResPacket.getAddress().getHostAddress();
				if (!userIp.equals(mApplication.getMyself().getIp())) {
					String additioninfo = ipmsgPro.getAdditionalSection();
					String[] addstr = additioninfo.split("\0");
					String[] userinfo = addstr[0].split("\\|");
					Bundle bundle = new Bundle();
					bundle.putString("mac", userinfo[0]);
					bundle.putString("ip", userinfo[1]);
					bundle.putString("alias", userinfo[2]);
					bundle.putDouble("longitude", Double.parseDouble(userinfo[3]));
					bundle.putDouble("latitude", Double.parseDouble(userinfo[4]));
					Message msg = new Message();
					
					msg.what = MsgConst.USERIN;
					msg.setData(bundle);
					mApplication.sendMessage(msg);
				}
			}	
				break;
			case MsgConst.REQUESTCONNECT:{
				String senderIp = udpResPacket.getAddress().getHostAddress();
				Message msg = new Message();
				msg.what = MsgConst.REQUESTCONNECT;
				msg.obj=senderIp;
				mApplication.sendMessage(msg);
			}
				break;
			case MsgConst.REJECTCONNECT:{
				String senderIp = udpResPacket.getAddress().getHostAddress();
				Message msg = new Message();
				msg.what = MsgConst.REJECTCONNECT;
				msg.obj=senderIp;
				mApplication.sendMessage(msg);
			}
				break;
			case MsgConst.ACCEPTCONNECT:{
				String senderIp = udpResPacket.getAddress().getHostAddress();
				Message msg = new Message();
				msg.what = MsgConst.ACCEPTCONNECT;
				msg.obj=senderIp;
				mApplication.sendMessage(msg);
			}
				break;
			case MsgConst.USEROFF:{
				String senderIp = udpResPacket.getAddress().getHostAddress();
				Message msg = new Message();
				msg.what = MsgConst.USEROFF;
				msg.obj=senderIp;
				mApplication.sendMessage(msg);
			}
				break;
			case MsgConst.RECEIVEFILE:{
				String senderIp = udpResPacket.getAddress().getHostAddress();	//�õ�������IP
				String additioninfo = ipmsgPro.getAdditionalSection();
				String[] addistr = additioninfo.split("\0");
				String[] fileinfo = addistr[0].split("\\|");
				
				Bundle bundle = new Bundle();
				bundle.putString("ip", senderIp);
				bundle.putString("path", fileinfo[1]);
				bundle.putLong("size", Long.parseLong(fileinfo[2]));
					
				Message msg = new Message();
				msg.what = (MsgConst.RECEIVEFILE);
				msg.setData(bundle);
				
				mApplication.sendMessage(msg);
				//MyFeiGeBaseActivity.sendMessage(msg);
				}	
				break;
			case MsgConst.FILEPAUSE:{
				String senderIp = udpResPacket.getAddress().getHostAddress();	//�õ�������IP
				String additioninfo = ipmsgPro.getAdditionalSection();
				String[] addistr = additioninfo.split("\0");
				String[] ippath = addistr[0].split("\\|");
				ippath[0] = senderIp;
				Message msg = new Message();
				msg.what = MsgConst.FILEPAUSE;
				msg.obj = ippath;
				mApplication.sendMessage(msg);
				}
				break;
			case MsgConst.FILECANCEL:{
				String senderIp = udpResPacket.getAddress().getHostAddress();	//�õ�������IP
				String additioninfo = ipmsgPro.getAdditionalSection();
				String[] addistr = additioninfo.split("\0");
				String[] ippath = addistr[0].split("\\|");
				ippath[0] = senderIp;
				Message msg = new Message();
				msg.what = MsgConst.FILECANCEL;
				msg.obj = ippath;
				mApplication.sendMessage(msg);
				}
				break;
			case MsgConst.FILERECEIVEINFO:{
				String senderIp = udpResPacket.getAddress().getHostAddress();	//�õ�������IP
				String additioninfo = ipmsgPro.getAdditionalSection();
				String[] addistr = additioninfo.split("\0");
				String[] fileinfo = addistr[0].split("\\|");
				Bundle bundle = new Bundle();
				bundle.putString("ip", senderIp);
				bundle.putString("abspath", fileinfo[0]);
				bundle.putLong("recTotal",Long.parseLong(fileinfo[1]));
				bundle.putLong("speed", Long.parseLong(fileinfo[2]));
				Message msg = new Message();
				msg.what = MsgConst.FILERECEIVEINFO;
				msg.setData(bundle);
				mApplication.sendMessage(msg);
				}
				break;
			case MsgConst.FILERECEIVESUCCESS:{
				String senderIp = udpResPacket.getAddress().getHostAddress();	//�õ�������IP
				String additioninfo = ipmsgPro.getAdditionalSection();
				String[] addistr = additioninfo.split("\0");
				String[] filesuc = addistr[0].split("\\|");
				Bundle bundle = new Bundle();
				bundle.putString("ip", senderIp);
				bundle.putString("abspath", filesuc[0]);
				Message msg = new Message();
				msg.what = MsgConst.FILERECEIVESUCCESS;
				msg.setData(bundle);
				mApplication.sendMessage(msg);
				}
				break;
			}
			if (udpResPacket != null) { // ÿ�ν�����UDP���ݺ����ó��ȡ�������ܻᵼ���´��յ����ݰ����ضϡ�
				udpResPacket.setLength(BUFFERLENGTH);
			}
		}
		System.out.println(120);
		if (udpResPacket != null) {
			udpResPacket = null;
		}

		if (udpSocket != null) {
			udpSocket.close();
			udpSocket = null;
		}

		udpThread = null;

	}

	public void disconnectSocket() { // ֹͣ����UDP����
		onWork = false; // �����߳����б�ʶΪ������
		stopThread();
	}

	private void stopThread() { // ֹͣ�߳�
		// TODO Auto-generated method stub
		if (udpThread != null) {
			udpThread.interrupt(); // ���̶߳��������ж�
		}
		Log.i(TAG, "ֹͣ����UDP����");
	}

	private void startThread() { // �����߳�
		// TODO Auto-generated method stub
		if (udpThread == null) {
			udpThread = new Thread(this);
			udpThread.start();
			Log.i(TAG, "���ڼ���UDP����");
		}
	}
	


}
