package com.example.file_transfer.net;
/**
 * IPMSG协议抽象类
 * IPMSG协议格式：
 * Ver(1): PacketNo:SenderName:SenderHost:CommandNo:AdditionalSection
 * 每部分分别对应为：版本号（现在是1）:数据包编号:发送主机:命令:附加数据
 * 其中：
 * 数据包编号，一般是取毫秒数。利用这个数据，可以唯一的区别每个数据包；
 * SenderName指的是发送者的昵称(实际上是计算机登录名)
 * 发送主机，指的是发送主机的主机名；（主机名）
 * 命令，指的是飞鸽协议中定义的一系列命令，具体见下文；
 * 附加数据，指的是对应不同的具体命令，需要提供的数据。当为上线报文时，附加信息内容是用户名和分组名，中间用"\0"分隔
 * 
 * 例如：
 * hello:1:Hello 
 * 表示 hello用户发送了 Hello 这条消息（32对应为IPMSG_SEND_MSG这个命令，具体需要看源码中的宏定义）。
 * 
 * @author what the luck
 * 
 * v1.0 2015/04/16
 */
public class IpMessageProtocol {
	private String senderName;
	private int commandNo;
	private String additionalSection;
	public IpMessageProtocol(){
	}
	
	// 根据协议字符串初始化
	public IpMessageProtocol(String protocolString){
		String[] args = protocolString.split(":");	// 以:分割协议串
		senderName = args[0];
		commandNo = Integer.parseInt(args[1]);
		if(args.length >= 3){	//是否有附加数据
			additionalSection = args[2];
		}else{
			additionalSection = "";
		}
		for(int i = 6; i < args.length; i++){	//处理附加数据中有:的情况
			additionalSection += (":" + args[i]);
		}
		
	}
	
	public IpMessageProtocol(String senderName,int commandNo,String additionalSection) {
		super();
		this.senderName = senderName;
		this.commandNo = commandNo;
		this.additionalSection = additionalSection;
	}

	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public int getCommandNo() {
		return commandNo;
	}
	public void setCommandNo(int commandNo) {
		this.commandNo = commandNo;
	}
	public String getAdditionalSection() {
		return additionalSection;
	}
	public void setAdditionalSection(String additionalSection) {
		this.additionalSection = additionalSection;
	}
	
	//得到协议串
	public String getProtocolString(){
		StringBuffer sb = new StringBuffer();
		sb.append(senderName);
		sb.append(":");
		sb.append(commandNo);
		sb.append(":");
		sb.append(additionalSection);
		return sb.toString();
	}
}
