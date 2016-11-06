package com.example.file_transfer.data;
/**
 * 
 * 用户个人信息类
 *
 */
public class Myself {
	
	private String Mac;
	private String Ip;
	private String alias;
	private String FilePath;
	private Boolean needRequest;
	private double longtitude=0;
	private double latitude=0;
	public void setMac(String mMac)
	{
		this.Mac = mMac;
	}
	public String getMac()
	{
		return Mac;
	}
	public void setAlias(String malias)
	{
		this.alias = malias;
	}
	public String getAlias()
	{
		return alias;
	}
	public void setIp(String ip)
	{
		this.Ip = ip;
	}
	public String getIp()
	{
		return Ip;
	}
	public void setReceiveFilePath(String filepath)
	{
		this.FilePath = filepath; 
	}
	public String getReceviceFilePath()
	{
		return FilePath;
	}
	public void setIsNeedRequest(Boolean req)
	{
		this.needRequest = req;
	}
	public Boolean getIsNeedRequest()
	{
		return this.needRequest;
	}
	public void setLongtitude(Double lng){
		this.longtitude=lng;
	}
	public Double getLongtitude()
	{
		return this.longtitude;
	}
	public void setLatitude(Double lat){
		this.latitude=lat;
	}
	public Double getLatitude()
	{
		return this.latitude;
	}
}
