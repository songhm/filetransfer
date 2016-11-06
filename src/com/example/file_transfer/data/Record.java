package com.example.file_transfer.data;

import com.example.file_transfer.activity.FileActivity;

public class Record {
	private String type;
	private String path;
	private String size;
	private String date;
	private String who;
	private boolean direction;  //false ±Ì æ∑¢ÀÕ
	public Record(String mpath,String msize,String mdate,boolean mdirection,String mwho)
	{
		this.path=mpath;
		this.size =msize;
		this.date = mdate;
		this.direction=mdirection;
		this.who=mwho;
	}
	public String getType()
	{
		type=FileActivity.getFileType(getName());
		return this.type;
	}
	public String getPath()
	{
		return this.path;
	}
	public String getName()
	{
		return path.substring(path.lastIndexOf("/")+1);
	}
	public String getSize()
	{
		return this.size;
	}
	public String getDate()
	{
		return this.date;
	}
	public boolean getDirection()
	{
		return this.direction;
	}
	public String getWho()
	{
		return this.who;
	}
}
