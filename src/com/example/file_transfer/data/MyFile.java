package com.example.file_transfer.data;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;

import com.example.file_transfer.activity.FileActivity;
import com.example.file_transfer.application.MyApplication;

/**
 * 
 * 文件信息类
 *
 */
public class MyFile{
	
	private MyApplication mApplication;
	private String type;
	private String path;
	private long size;
	private String date;
	private boolean state=false; //fasle 表示未完成
	private boolean isinterrupted = false;
	private boolean ispause=false;
	private boolean ispaused=false;
	private boolean iscancel=false;
	private boolean iscanceled=false;
	private boolean direction;  //false 表示发送
	private long currentsize=0;
	private long rate=0;
	private File file=null;
	public MyFile(){}
	public MyFile(String mpath,boolean mdirection)
	{
		mApplication = MyApplication.getInstance();
		this.path=mpath;
		this.direction=mdirection;
		this.file = new File(mpath);
		this.size=file.length();
	}
	public MyFile(String mpath,long msize,boolean mdirection)
	{
		mApplication = MyApplication.getInstance();
		this.path=mpath;
		this.size=msize;
		this.direction=mdirection;
	}
	public String getType()
	{
		type=FileActivity.getFileType(getName());
		return this.type;
	}
	public void setPath(String mpath)
	{
		this.path = mpath;
	}
	public String getPath()
	{
		return this.path;
	}
	public String getReceiveLocalPath()
	{
		return mApplication.getMyself().getReceviceFilePath()+getName();
	}
	public String getName()
	{
		return path.substring(path.lastIndexOf("/")+1);
	}
	public long getSizeInLong()
	{
		return this.size;
	}
	public String getSize()
	{
		String s[] ={"B","KB","MB","GB"};
		double current = this.size;
		int cnt = 0;
		while(current>=1)
		{
			current = current/1024;
			cnt++;
		}
		if(current == 0) cnt++;
		DecimalFormat   df = new DecimalFormat("#####0.00");     
		return df.format(current*1024)+s[cnt-1];
	}
	public String getDate()
	{
		return this.date;
	}
	public void setDate()
	{
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);  
        int day = c.get(Calendar.DAY_OF_MONTH);  
        int hour = c.get(Calendar.HOUR_OF_DAY);  
        int minute = c.get(Calendar.MINUTE);
        this.date = year+"-"+month+"-"+day+" "+hour+":"+((minute/10)==0?("0"+minute):minute);
	}
	public void setState(boolean mstate)
	{
		this.state = mstate;
	}
	public boolean getState()
	{
		return this.state;
	}
	public void setIsInterrupted(boolean mInterrupted)
	{
		this.isinterrupted = mInterrupted;
	}
	public boolean getIsInterrupted()
	{
		return this.isinterrupted;
	}
	public void setIsPause(boolean mpause)
	{
		this.ispause = mpause;
	}
	public boolean getIsPause()
	{
		return this.ispause;
	}
	public void setIsCancel(boolean mcancel)
	{
		this.iscancel = mcancel;
	}
	public boolean getIsCancel()
	{
		return this.iscancel;
	}
	public void setIsPaused(boolean mpaused)
	{
		this.ispaused = mpaused;
	}
	public boolean getIsPaused()
	{
		return this.ispaused;
	}
	public void setIsCanceled(boolean mcanceled)
	{
		this.iscanceled = mcanceled;
	}
	public boolean getIsCanceled()
	{
		return this.iscanceled;
	}
	public void setDirection(boolean mdirection)
	{
		this.direction = mdirection;
	}
	public boolean getDirection()
	{
		return this.direction;
	}
	public void setCurrentSize(long mcurrentsize)
	{
		this.currentsize = mcurrentsize;
	}
	public Long getCurrentSizeInLong()
	{
   
		return currentsize;
	}
	public String getCurrentSize()
	{
		String s[] ={"B","KB","MB","GB"};
		double current = this.currentsize;
		int cnt = 0;
		while(current>=1)
		{
			current = current/1024;
			cnt++;
		}
		if(current == 0) cnt++;
		DecimalFormat   df = new DecimalFormat("#####0.00");     
		return df.format(current*1024)+s[cnt-1];
	}
	public void setRate(long rate)
	{
		this.rate=rate;
	}
	public String getRate()
	{
		String s[] ={"B","KB","MB","GB"};
		double current = this.rate;
		//Log.v("current",current+"");
		int cnt = 0;
		while(current>=1)
		{
			current = current/1024;
			cnt++;
		}
		if(current == 0) cnt++;
		DecimalFormat   df = new DecimalFormat("#####0.00");  
		//Log.v("cnt",cnt+"");
		return df.format(current*1024)+s[cnt-1];
	}
	public int getProgress()
	{
		return (int)(((double)currentsize/size)*100);
	}
}

