package com.example.file_transfer.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 
 * 连接用户信息类
 *
 */

public class User {
	
	private String Mac;
	private String Ip;
	private String alias;
	private int Distance;
	private boolean isExpand = false;
	private boolean connected = true;
	private Map<String,MyFile> filelist=new LinkedHashMap<String,MyFile>();
	public User(){}
	public User(String mMac,String ip,String malias,int distance)
	{
		this.Mac=mMac;
		this.Ip=ip;
		this.alias = malias;
		this.Distance = distance;
	}
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
	public void setDistance(int distance)
	{
		this.Distance = distance;
	}
	public int getDistance()
	{
		return Distance;
	}
	public int getFileCount()
	{
		return filelist.size();
	}
	public Map<String,MyFile> getFileList()
	{
		return this.filelist;
	}
	public List<MyFile> getFileListInArray()
	{
		List<MyFile> list = new ArrayList<MyFile>();
		if(filelist!=null){
			Iterator<Entry<String, MyFile>> it = filelist.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, MyFile> entry=(Map.Entry<String, MyFile>) it.next();   
				list.add(entry.getValue());
				}
		}
		return list;
	}
	public boolean getIsExpand()
	{
		return this.isExpand;
	}
	public void  setIsExpand(boolean x)
	{
		this.isExpand = x;
	}
	public void setIsConnected(boolean con){
		this.connected = con;
	}
	public  boolean getIsConnected(){
		return this.connected;
	}
	public List<MyFile> getUnDoneFiles() {
		// TODO Auto-generated method stub
		List<MyFile> files = new ArrayList<MyFile>();
		if(filelist!=null){
			Iterator<Entry<String, MyFile>> it = filelist.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, MyFile> entry=(Map.Entry<String, MyFile>) it.next();
				MyFile file = entry.getValue();
				if(!file.getState()) files.add(file);
			}
		}
		return files;
	}
	public boolean haveUnDoneFiles() {
		// TODO Auto-generated method stub
		if(getUnDoneFiles().size()==0) return false;
		return true;
	}
}

