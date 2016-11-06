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
	private static final int BUFFERLENGTH = 1024; // 缓冲大小
	private static final String TAG = "NetHelper";
	private boolean onWork = false; // 线程工作标识
	private Thread udpThread = null; // 接收UDP数据线程
	private DatagramSocket udpSocket = null; // 用于接收和发送udp数据的socket
	private DatagramPacket udpSendPacket = null; // 用于发送的udp数据包
	private DatagramPacket udpResPacket = null; // 用于接收的udp数据包
	private byte[] resBuffer = new byte[BUFFERLENGTH]; // 接收数据的缓存
	private byte[] sendBuffer = null;
	/*
	 * INITIAL UDPSOCKET&UDPRESPACHET
	 * 启动线程，接受UDP数据
	 */
	public NetHelper() {
		mApplication = MyApplication.getInstance();
		searching = false;
		try {
			if (udpSocket == null) {
				udpSocket = new DatagramSocket(null);
				udpSocket.setReuseAddress(true);
				udpSocket.bind(new InetSocketAddress(MsgConst.PORT));
				Log.i(TAG, "connectSocket()....绑定UDP端口" + MsgConst.PORT + "成功");
			}
			if (udpResPacket == null)
				udpResPacket = new DatagramPacket(resBuffer, BUFFERLENGTH);
			onWork = true; // 设置标识为线程工作
			startThread(); // 启动线程接收udp数据
		} catch (SocketException e) {
			e.printStackTrace();
			disconnectSocket();
			Log.e(TAG, "Nethelper初始化....绑定UDP端口" + MsgConst.PORT + "失败");
		}
	}
	public void startSearch() { // 发送准许被搜索广播。
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
					Log.e(TAG, "startSearch()....广播地址有误");
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
				ipmsgSend.setAdditionalSection(mApplication.getMyself().getAlias() + "\0"); // 附加信息里加入用户名和分组信息

				InetAddress broadcastAddr;
				try {
					broadcastAddr = InetAddress.getByName("255.255.255.255"); // 广播地址
					sendUdpData(ipmsgSend.getProtocolString() + "\0", broadcastAddr,
							MsgConst.PORT); // 发送数据
				} catch (UnknownHostException e) {
					e.printStackTrace();
					Log.e(TAG, "下线广播中。。....广播有误");
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
			int sendPort) { // 发送UDP数据包的方法
		try {
			sendBuffer = sendStr.getBytes("gbk");
			// 构造发送的UDP数据包
			udpSendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
					sendto, sendPort);
			udpSocket.send(udpSendPacket); // 发送udp数据包
			Log.i(TAG, "成功向IP为" + sendto.getHostAddress() + "发送UDP数据："
					+ sendStr);
			udpSendPacket = null;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e(TAG, "sendUdpData(String sendStr, int port)....系统不支持GBK编码");
		} catch (IOException e) { // 发送UDP数据包出错
			e.printStackTrace();
			udpSendPacket = null;
			Log.e(TAG, "sendUdpData(String sendStr, int port)....发送UDP数据包失败");
		}
	}

	@Override
	public void run() {
		while (onWork) {
			try {
				Log.v(TAG,"UDP等待数据中……");
				udpSocket.receive(udpResPacket);
				Log.v(TAG,"UDP接受数据数据成功。");
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
				Log.e(TAG, "UDP数据包接收失败！线程停止");
				break;
			}
			if (udpResPacket.getLength() == 0) {
				Log.i(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
				continue;
			}
			String ipmsgStr = "";
			try {
				ipmsgStr = new String(resBuffer, 0, udpResPacket.getLength(),
						"gbk");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "接收数据时，系统不支持GBK编码");
			}// 截取收到的数据
			Log.i(TAG, "接收到的UDP数据内容为:" + ipmsgStr);
			
			IpMessageProtocol ipmsgPro = new IpMessageProtocol(ipmsgStr); //
			int commandNo = ipmsgPro.getCommandNo();
			System.out.println("UDP接受数据命令号为： "+commandNo);
			switch (commandNo) {
			
			case MsgConst.STARTSEARCH: { // 收到上线数据包，添加用户，并回送IPMSG_ANSENTRY应答。
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
					
						// 下面构造回送报文内容
						IpMessageProtocol ipmsgSend = new IpMessageProtocol();
						ipmsgSend.setSenderName(mApplication.getMyself().getAlias());
						ipmsgSend.setCommandNo(MsgConst.SEARCHBACK); // 回送报文命令
						ipmsgSend.setAdditionalSection(mApplication.getMyself().getMac()+'|'
								+mApplication.getMyself().getIp()+'|'
								+mApplication.getMyself().getAlias()+'|'
								+mApplication.getMyself().getLongtitude()+'|'
								+mApplication.getMyself().getLatitude()+ "\0");
						sendUdpData(ipmsgSend.getProtocolString(),
								udpResPacket.getAddress(), udpResPacket.getPort()); // 发送数据
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
					Log.i(TAG, "user:" + userIp + "停止搜索");
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
				String senderIp = udpResPacket.getAddress().getHostAddress();	//得到发送者IP
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
				String senderIp = udpResPacket.getAddress().getHostAddress();	//得到发送者IP
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
				String senderIp = udpResPacket.getAddress().getHostAddress();	//得到发送者IP
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
				String senderIp = udpResPacket.getAddress().getHostAddress();	//得到发送者IP
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
				String senderIp = udpResPacket.getAddress().getHostAddress();	//得到发送者IP
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
			if (udpResPacket != null) { // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
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

	public void disconnectSocket() { // 停止监听UDP数据
		onWork = false; // 设置线程运行标识为不运行
		stopThread();
	}

	private void stopThread() { // 停止线程
		// TODO Auto-generated method stub
		if (udpThread != null) {
			udpThread.interrupt(); // 若线程堵塞，则中断
		}
		Log.i(TAG, "停止监听UDP数据");
	}

	private void startThread() { // 启动线程
		// TODO Auto-generated method stub
		if (udpThread == null) {
			udpThread = new Thread(this);
			udpThread.start();
			Log.i(TAG, "正在监听UDP数据");
		}
	}
	


}
