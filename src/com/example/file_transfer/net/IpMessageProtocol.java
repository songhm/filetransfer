package com.example.file_transfer.net;
/**
 * IPMSGЭ�������
 * IPMSGЭ���ʽ��
 * Ver(1): PacketNo:SenderName:SenderHost:CommandNo:AdditionalSection
 * ÿ���ֱַ��ӦΪ���汾�ţ�������1��:���ݰ����:��������:����:��������
 * ���У�
 * ���ݰ���ţ�һ����ȡ������������������ݣ�����Ψһ������ÿ�����ݰ���
 * SenderNameָ���Ƿ����ߵ��ǳ�(ʵ�����Ǽ������¼��)
 * ����������ָ���Ƿ���������������������������
 * ���ָ���Ƿɸ�Э���ж����һϵ�������������ģ�
 * �������ݣ�ָ���Ƕ�Ӧ��ͬ�ľ��������Ҫ�ṩ�����ݡ���Ϊ���߱���ʱ��������Ϣ�������û����ͷ��������м���"\0"�ָ�
 * 
 * ���磺
 * hello:1:Hello 
 * ��ʾ hello�û������� Hello ������Ϣ��32��ӦΪIPMSG_SEND_MSG������������Ҫ��Դ���еĺ궨�壩��
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
	
	// ����Э���ַ�����ʼ��
	public IpMessageProtocol(String protocolString){
		String[] args = protocolString.split(":");	// ��:�ָ�Э�鴮
		senderName = args[0];
		commandNo = Integer.parseInt(args[1]);
		if(args.length >= 3){	//�Ƿ��и�������
			additionalSection = args[2];
		}else{
			additionalSection = "";
		}
		for(int i = 6; i < args.length; i++){	//��������������:�����
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
	
	//�õ�Э�鴮
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
