package com.example.file_transfer.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.example.file_transfer.R;
import com.example.file_transfer.activity.BaseActivity;
import com.example.file_transfer.activity.FriendsActivity;
import com.example.file_transfer.activity.MainActivity;
import com.example.file_transfer.activity.RecordingActivity;
import com.example.file_transfer.data.MsgConst;
import com.example.file_transfer.data.MyFile;
import com.example.file_transfer.data.Myself;
import com.example.file_transfer.data.Record;
import com.example.file_transfer.data.User;
import com.example.file_transfer.net.Detector;
import com.example.file_transfer.net.NetHelper;
import com.example.file_transfer.utils.CreateDB;
import com.example.file_transfer.utils.FileHelper;
import com.example.file_transfer.utils.LocationUtil;
import com.example.file_transfer.utils.NetUtil;
import com.example.file_transfer.utils.sendMD5;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MyApplication extends Application {
	
	private static final String TAG = "MyApplication";
	//定位相关
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	//声明网络辅助类
	private NetHelper mNetHelper;
	//声明文件辅助类
	private FileHelper mFileHelper;
	//声明检测类
	private Detector detector;
	//声明文件辅助类
	private CreateDB mCreateDB;
	//声明创建活动队列
	private LinkedList<BaseActivity> mList = new LinkedList<BaseActivity>();
	//声明当前活动
	private BaseActivity currentActivity;
	//声明初始化用户信息
	private Myself me= new Myself();
	private List<Record> records = new ArrayList<Record>();
	//声明初始化连接用户和搜索用户信息
	private Map<String, User> searchingusers = new HashMap<String,User>();
	private Map<String, User> connectedusers = new HashMap<String,User>();
	private static MyApplication instance;
	public synchronized static MyApplication getInstance() { 
        if (null == instance) { 
            instance = new MyApplication(); 
        } 
        return instance; 
    } 

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        //创建网络辅助类和文件传输辅助类和检测类
        mNetHelper=new NetHelper();
        mFileHelper=new FileHelper();
        detector = new Detector();
        detector.detUser();
        //创建定位对象并注册监听
        mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
    }
    public NetHelper getNetHelper()
    {
    	return mNetHelper;
    }
    public FileHelper getFileHelper()
    {
    	return this.mFileHelper;
    }
    public LocationClient getLocationClient(){
    	return mLocationClient;
    }
    public void setCreateDB(CreateDB db)
    {
    	this.mCreateDB = db;
    }
    public CreateDB getCreateDB()
    {
    	return this.mCreateDB;
    }
    public List<Record> getRecords()
    {
    	return records;
    }
    public Myself getMyself()
    {
    	return me;
    }
    public int getSearchUserCount()
    {
    	return searchingusers.size();
    }
    public Map<String, User> getSearchUsers() {
		return searchingusers;
	}
    public Map<String, User> getConnectedUsers() {
		return connectedusers;
	}
    public int getConnectedUserCount()
    {
    	return connectedusers.size();
    }
    // add Activity  
    public void addActivity(BaseActivity activity) { 
        mList.add(activity); 
    } 
 // delete Activity  
    public void deleteActivity(BaseActivity activity) { 
        mList.remove(activity); 
    } 
    public BaseActivity getCurrentActivity(){
		return currentActivity;
	}
    public void setCurrentActivity(BaseActivity m){
		this.currentActivity = m;
		Log.i(TAG,"当前活动为:"+currentActivity);
	}
    public void sendMessage(Message msg) {
		handler.sendMessage(msg);
	}

	public void sendEmptyMessage(int what) {
		handler.sendEmptyMessage(what);
	}
	private void broadCastOff(){
		Iterator<Entry<String,User> > it = connectedusers.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,User> entry = it.next();
			User user = entry.getValue();
			if(user.getIsConnected()){
				mNetHelper.disConnect(user.getIp());
				Log.i(TAG, "调用NetHelper.disconnect,与"+user.getIp()+"断开");
			}
		}
	}
	public void exit() { 
		broadCastOff();
		mLocationClient.stop();
        try { 
            for (BaseActivity activity : mList) { 
                if (activity != null) 
                    activity.finish(); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
    } 
    public void onLowMemory() { 
        super.onLowMemory();     
        System.gc(); 
    }  

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			//用户进入搜索处理
			case MsgConst.USERIN:
				Bundle bundle = msg.getData();
				String mac = bundle.getString("mac");
				String ip = bundle.getString("ip");
				if(ip.equals(me.getIp()) || searchingusers.containsKey(ip)) break;
				String alias = bundle.getString("alias");
				int distance = (int) LocationUtil.getDistance(me.getLatitude(),me.getLongtitude(),bundle.getDouble("latitude"),bundle.getDouble("longitude"));
				Log.i(TAG,"接收到USEIN");
				Log.i(TAG,"me: latitude("+me.getLatitude()+"),longtitude("+me.getLongtitude()+")");
				Log.i(TAG,"对方: latitude("+bundle.getDouble("latitude")+"),longitude("+bundle.getDouble("longitude")+")");
				Log.i(TAG,"与对方("+ip+")距离为"+distance);
				User u = new User(mac,ip,alias,distance);
				searchingusers.put(u.getIp(), u);
				Message msg1 = Message.obtain();
				msg1.what = msg.what;
				msg1.obj = u;
				if(currentActivity!=null && currentActivity instanceof MainActivity) 
				{	
					Log.i(TAG,"接收到USERIN，并转发给MainActivity处理");
					currentActivity.processMessage(msg1);
				}
				break;
			//用户退出搜索处理
			case MsgConst.USEROUT:
				String ip1 = (String)msg.obj;
				if(searchingusers.containsKey(ip1)){	
					searchingusers.remove(ip1);
					if(currentActivity!=null && currentActivity instanceof MainActivity){
						Log.i(TAG,"接收到USEROUT，并转发给MainActivity处理");
						currentActivity.processMessage(msg);
					}
				}
				break;
			//用户申请连接处理
			case MsgConst.REQUESTCONNECT:
				Log.i(TAG,"接收到REQUESTCONNECT");
				//Log.i(TAG,"当前活动为:"+currentActivity);
				if(currentActivity!=null && currentActivity instanceof MainActivity) 
				{
					currentActivity.processMessage(msg);
					Log.i(TAG,"转发给MainActivity处理");
					}
				break;
			//用户拒绝连接请求处理
			case MsgConst.REJECTCONNECT:
				Log.i(TAG,"接收到REJECTCONNECT");
				//Log.i(TAG,"当前活动为:"+currentActivity);
				if(currentActivity!=null && currentActivity instanceof MainActivity) {
					currentActivity.processMessage(msg);
					Log.i(TAG,"转发给MainActivity处理");
				}
				break;
			//用户同意连接请求处理
			case MsgConst.ACCEPTCONNECT:
				String ip2 = (String)msg.obj;
				connectedusers.put(ip2, searchingusers.get(ip2));
				searchingusers.remove(ip2);
				if(currentActivity!=null && currentActivity instanceof MainActivity) currentActivity.processMessage(msg);
				break;
			//连接用户断开连接处理
			case MsgConst.USEROFF:
				String ip3 = (String)msg.obj;
				Log.i(TAG,"用户"+ip3+"下线");
				List<MyFile> files = connectedusers.get(ip3).getUnDoneFiles();
				Iterator<MyFile> it = files.iterator();
				while(it.hasNext()){
					MyFile file = it.next();
					file.setIsInterrupted(true);
					file.setState(true);
					Log.i(TAG, file.getName()+" 被中止");
				}
				connectedusers.get(ip3).setIsConnected(false);
				//if(currentActivity!=null) Toast.makeText(currentActivity,connectedusers.get(ip3).getAlias()+" 断开连接", Toast.LENGTH_SHORT).show();
				if(currentActivity!=null && currentActivity instanceof FriendsActivity) currentActivity.processMessage(msg);
				break;
			//响应接收文件消息
			case MsgConst.RECEIVEFILE:
				Bundle bundle1 = msg.getData();
				String ip4 = bundle1.getString("ip");
				MyFile file = new MyFile(bundle1.getString("path"),bundle1.getLong("size"),true);
				connectedusers.get(ip4).getFileList().put(file.getPath(), file);
				connectedusers.get(ip4).setIsExpand(true);
				Toast.makeText(currentActivity,connectedusers.get(ip4).getAlias()+" 向您发送文件", Toast.LENGTH_SHORT).show();
				mFileHelper.receiveFile(ip4,file.getPath(),file.getName(),file.getSizeInLong(),connectedusers.get(ip4).getMac());
				Log.i(TAG,"接收到RECEIVEFILE，调用mFileHelper.receiveFile");
				Log.i(TAG,"参数为："+ip4+","+file.getPath()+","+file.getName()+","+file.getSizeInLong()+","+connectedusers.get(ip4).getMac());
				if(currentActivity!=null && currentActivity instanceof FriendsActivity) 
				{
					currentActivity.processMessage(msg);
					Log.i(TAG,"接收到RECEIVEFILE，调用mFileHelper.receiveFile");
				}
				break;
			//响应文件接收进度消息
			case MsgConst.FILERECEIVEINFO:
				Bundle rec_bundle = msg.getData();
				String f_ip = rec_bundle.getString("ip");
				String f_path = rec_bundle.getString("abspath");
				Long currentSize = rec_bundle.getLong("recTotal");
				Long speed = rec_bundle.getLong("speed");
				Log.i(TAG,"接收到FILERECEIVEINFO,更新文件信息");
				Log.i(TAG,"ip:"+f_ip+";path:"+f_path+";speed:"+speed+";currentSize:"+currentSize);
				MyFile setfile = connectedusers.get(f_ip).getFileList().get(f_path);
				if(currentSize > setfile.getCurrentSizeInLong() && currentSize<=setfile.getSizeInLong()){
					setfile.setCurrentSize(currentSize);
					setfile.setRate(speed);
					if(currentActivity!=null && currentActivity instanceof FriendsActivity) 
					{
						currentActivity.processMessage(msg);
						Log.i(TAG,"转发给FriendsAcitvity更新界面");
					}
				}
				if(setfile.getDirection())
				{
					mNetHelper.sendFileInfo(f_ip,f_path,currentSize,speed);
					Log.i(TAG,"转发给发送方");
				}
				break;
			//响应文件完成消息
			case MsgConst.FILERECEIVESUCCESS:
				Bundle suc_bundle = msg.getData();
				String suc_ip = suc_bundle.getString("ip");
				String suc_path = suc_bundle.getString("abspath");
				MyFile suc_file = connectedusers.get(suc_ip).getFileList().get(suc_path);
				if(suc_bundle.containsKey("IsSuc")){
					boolean issuc = suc_bundle.getBoolean("IsSuc");
					Log.v(TAG, "issuc: "+issuc);
					if(issuc){
						suc_file.setState(true);
						suc_file.setDate();
						Log.i(TAG,"接收到FILERECEIVESUCCESS,文件接收成功");
						Log.i(TAG,"ip:"+suc_ip+";path:"+suc_path);
						if(currentActivity!=null && currentActivity instanceof FriendsActivity) 
						{
							currentActivity.processMessage(msg);
							Log.i(TAG,"转发给FriendsAcitvity更新界面");
						}
						//生成记录
						Record record;
						record = new Record(suc_file.getReceiveLocalPath(),suc_file.getSize(),suc_file.getDate(),true,connectedusers.get(suc_ip).getAlias());
						records.add(record);
						//写入数据库
						mCreateDB.save(record.getPath(), record.getSize(), record.getDate(), record.getWho(), record.getDirection()?1:0);
						Log.i(TAG,"已将文件\""+record.getName()+"\"写入数据库");
						if(currentActivity!=null && currentActivity instanceof RecordingActivity) 
						{
							currentActivity.processMessage(msg);
							Log.i(TAG,"转发给RecordingAcitvity更新界面");
						}
					}else{
						Log.i(TAG,"接收到FILERECEIVESUCCESS, 转发给发送方并校验");
						mNetHelper.sendFileSucInfo(suc_ip, suc_path);
					}
				}else{
					suc_file.setState(true);
					suc_file.setDate();
					Log.i(TAG,"接收到FILERECEIVESUCCESS,文件发送成功");
					Log.i(TAG,"ip:"+suc_ip+";path:"+suc_path);
					if(currentActivity!=null && currentActivity instanceof FriendsActivity) 
					{
						currentActivity.processMessage(msg);
						Log.i(TAG,"转发给FriendsAcitvity更新界面");
					}
					//生成记录
					Record record;
					record = new Record(suc_file.getPath(),suc_file.getSize(),suc_file.getDate(),false,connectedusers.get(suc_ip).getAlias());
					records.add(record);
					//写入数据库
					mCreateDB.save(record.getPath(), record.getSize(), record.getDate(), record.getWho(), record.getDirection()?1:0);
					Log.i(TAG,"已将文件\""+record.getName()+"\"写入数据库");
					if(currentActivity!=null && currentActivity instanceof RecordingActivity) 
					{
						currentActivity.processMessage(msg);
						Log.i(TAG,"转发给RecordingAcitvity更新界面");
					}
					Log.i(TAG,"开启线程，发送md5");
					(new Thread(new sendMD5(suc_ip))).start();
				}
				break;
			//响应文件暂停消息
			case MsgConst.FILEPAUSE:
				String [] p_ippath = (String [])msg.obj;
				String p_ip = p_ippath[0];
				String p_path = p_ippath[1];
				MyFile p_file = connectedusers.get(p_ip).getFileList().get(p_path);
				if(!p_file.getIsPause()){
					Log.i(TAG,"接收到FILEPAUSE,文件传输暂停");
					Log.i(TAG,"ip:"+p_ip+";path:"+p_path);
					mFileHelper.filePause(p_ip+p_path,p_file.getDirection());
					Log.i(TAG,"调用mFileHelper.filePause");
					Log.i(TAG,"ip:"+p_ip+"path:"+p_path+"direction:"+p_file.getDirection());
					p_file.setIsPause(true);
					p_file.setIsPaused(true);
				}else{
					p_file.setIsPause(false);
					if(p_file.getDirection()){
						Log.i(TAG,"接收到FILEPAUSE,文件传输继续接收");
						Log.i(TAG,"ip:"+p_ip+";path:"+p_path);
						mFileHelper.receiveFile(p_ip, p_path,p_file.getName(),p_file.getSizeInLong(),connectedusers.get(p_ip).getMac());
						Log.i(TAG,"调用mFileHelper.receiveFile");
					}else{
						Log.i(TAG,"接收到FILEPAUSE,文件传输继续发送");
						Log.i(TAG,"ip:"+p_ip+";path:"+p_path);
						mFileHelper.sendFile();
						Log.i(TAG,"调用mFileHelper.sendFile");
						mNetHelper.filePause(p_ip,p_path);
						Log.i(TAG,"调用NetHelper.filePause");
						Log.i(TAG,"继续用户名:"+connectedusers.get(p_ip).getAlias());
						Log.i(TAG,"继续"+(p_file.getDirection()?"接收":"发送")+"文件路径:"+p_file.getPath());
					}
				}
				if(currentActivity!=null && currentActivity instanceof FriendsActivity) 
				{
					currentActivity.processMessage(msg);
					Log.i(TAG,"转发给FriendsAcitvity更新界面");
				}
				break;
			//响应文件取消消息
			case MsgConst.FILECANCEL:
				String [] c_ippath = (String [])msg.obj;
				String c_ip = c_ippath[0];
				String c_path = c_ippath[1];
				MyFile c_file = connectedusers.get(c_ip).getFileList().get(c_path);
				c_file.setState(true);
				c_file.setIsCancel(true);
				c_file.setIsCanceled(true);
				Log.i(TAG,"接收到FILECANCEL,文件传输取消");
				Log.i(TAG,"ip:"+c_ip+";path:"+c_path);
				mFileHelper.fileCancel(c_ip+c_path,c_file.getDirection());
				Log.i(TAG,"调用mFileHelper.fileCancel");
				Log.i(TAG,"ip:"+c_ip+"path:"+c_path+"direction:"+c_file.getDirection());
				if(currentActivity!=null && currentActivity instanceof FriendsActivity) 
				{
					currentActivity.processMessage(msg);
					Log.i(TAG,"转发给FriendsAcitvity更新界面");
				}
				break;
		    //wifi连接正常消息处理
			case MsgConst.WIFIACTIVE:
				if(currentActivity!=null) Toast.makeText(currentActivity, R.string.wifi_success, Toast.LENGTH_LONG).show();
				me.setIp(NetUtil.getlocalip());
				break;
			}
		}
	};
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			/*StringBuffer sb = new StringBuffer(256);
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			Log.i("BaiduLocationApiDem", sb.toString());*/
			if(location.getLocType()==61 || location.getLocType()==161)
			{
				me.setLatitude(location.getLatitude());
				me.setLongtitude(location.getLongitude());
			}
			
		}


	}
}
