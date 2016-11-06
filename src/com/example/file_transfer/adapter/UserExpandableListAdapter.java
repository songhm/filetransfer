package com.example.file_transfer.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.file_transfer.R;
import com.example.file_transfer.activity.FileActivity;
import com.example.file_transfer.activity.FriendsActivity;
import com.example.file_transfer.application.MyApplication;
import com.example.file_transfer.data.MyFile;
import com.example.file_transfer.data.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 
 * 扩展listview适配器
 *
 */

public class UserExpandableListAdapter extends BaseExpandableListAdapter {
	
	private final static String TAG = "UserExpandableListAdapter";
	private MyApplication mApplication; 
	private Activity activity;	//父activity
	protected Resources res;
	private LayoutInflater mChildInflater;	//用于加载分组的布局xml
	private LayoutInflater mGroupInflater;	//用于加载对应分组用户的布局xml
	List<User> data = new ArrayList<User>();
	
	public UserExpandableListAdapter(Activity c,List<User> data){
		mApplication = MyApplication.getInstance();
		mChildInflater = LayoutInflater.from(c);
		mGroupInflater = LayoutInflater.from(c);
		this.data = data;
		activity = c;
		res = c.getResources();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return data.get(groupPosition).getFileListInArray().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) { //返回item索引
		// TODO Auto-generated method stub
		return childPosition;
	}

	//分组视图
	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View myView = null;

		if(data == null || data.size() == 0 || data.get(groupPosition).getFileListInArray() == null || data.get(groupPosition).getFileListInArray().size() == 0){
			return myView;
		}
		final User user = (User)getGroup(groupPosition);
		
		final MyFile file = (MyFile)getChild(groupPosition,childPosition);
		if(file.getState()) 
		{	
			myView = mChildInflater.inflate(R.layout.child_done_layout, null);
			TextView mfile_name= (TextView) myView.findViewById(R.id.file_name);
			TextView mfile_size= (TextView) myView.findViewById(R.id.file_size);
			TextView mfile_date= (TextView) myView.findViewById(R.id.file_date);
			TextView mfile_state= (TextView) myView.findViewById(R.id.file_state);
			//显示文件名
			mfile_name.setText(file.getName());
			//显示文件大小
			mfile_size.setText(file.getSize());
			if(file.getIsInterrupted()){
				//显示传输状态
				mfile_state.setText(R.string.filefail);
			}else if(file.getIsCancel())
			{
				//显示传输状态
				if(file.getIsCanceled() && file.getDirection() ) mfile_state.setText("对方取消发送");
				else if(!file.getIsCanceled() && file.getDirection()) mfile_state.setText("已取消接收");
				else if(file.getIsCanceled() && !file.getDirection()) mfile_state.setText("对方取消接收");
				else mfile_state.setText("已取消发送");
			}else{
				//显示日期
				mfile_date.setText(file.getDate());
				//显示传输状态
				if(file.getDirection()) mfile_state.setText("接收成功");
				else mfile_state.setText("发送成功");
			}
			
		}else{
			myView = mChildInflater.inflate(R.layout.child_doing_layout, null);
			TextView mfile_name= (TextView) myView.findViewById(R.id.file_name);
			TextView mfile_rate= (TextView) myView.findViewById(R.id.file_rate);
			TextView mfile_currentsize= (TextView) myView.findViewById(R.id.file_currentsize);
			Button mfile_cancel= (Button) myView.findViewById(R.id.file_cancel);
			Button mfile_pause= (Button) myView.findViewById(R.id.file_pause);
			TextView mfile_state= (TextView) myView.findViewById(R.id.file_state);
			mfile_name.setText(file.getName());
			if(file.getIsPause()) file.setRate(0);
			mfile_rate.setText(file.getRate()+"/S");
			mfile_currentsize.setText(file.getCurrentSize()+"/"+file.getSize());
			if(file.getIsPause()) mfile_pause.setText("继续");
			else mfile_pause.setText("暂停");
			mfile_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//设置取消时间
					file.setDate();
					//设置文件完成
					file.setState(true);
					file.setIsCancel(true);
					file.setIsCanceled(false);
					//调用文件传输辅助类取消数据传输
					mApplication.getFileHelper().fileCancel(user.getIp()+file.getPath(),file.getDirection());
					//调用网络辅助类发udp消息通知对方
					mApplication.getNetHelper().fileCancel(user.getIp(),file.getPath());
					Log.i(TAG,"调用mNetHelper.fileCancel");
					Log.i(TAG,"调用mFileHelper.fileCancel");
					Log.i(TAG,"取消用户名:"+user.getAlias());
					Log.i(TAG,"取消"+(file.getDirection()?"接收":"发送")+"文件路径:"+file.getPath());
					((FriendsActivity)(mApplication.getCurrentActivity())).refreshView();
				}
			});
			mfile_pause.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(file.getIsPause()){
						if(file.getDirection()){
							mApplication.getNetHelper().filePause(user.getIp(),file.getPath());
							Log.i(TAG,"调用NetHelper.filePause,让发送方继续发送");
							Log.i(TAG,"继续用户名:"+user.getAlias());
							Log.i(TAG,"继续"+(file.getDirection()?"接收":"发送")+"文件路径:"+file.getPath());
						}else{
							file.setIsPause(false);
							mApplication.getFileHelper().sendFile();
							Log.i(TAG,"调用FileHelper.sendFile");
							mApplication.getNetHelper().filePause(user.getIp(),file.getPath());
							Log.i(TAG,"调用NetHelper.filePause");
							Log.i(TAG,"继续用户名:"+user.getAlias());
							Log.i(TAG,"继续"+(file.getDirection()?"接收":"发送")+"文件路径:"+file.getPath());
						}
					}else{
						//调用文件传输辅助类暂停数据传输
						mApplication.getFileHelper().filePause(user.getIp()+file.getPath(),file.getDirection());
						Log.i(TAG,"调用mFileHelper.filePause");
						Log.i(TAG,"暂停用户名:"+user.getAlias());
						Log.i(TAG,"暂停"+(file.getDirection()?"接收":"发送")+"文件路径:"+file.getPath());
						file.setIsPause(true);
						file.setIsPaused(false);
						//调用网络辅助类发udp消息通知对方
						mApplication.getNetHelper().filePause(user.getIp(),file.getPath());
						Log.i(TAG,"调用mNetHelper.filePause");
						
						
					}
					//刷新界面
					((FriendsActivity)(mApplication.getCurrentActivity())).refreshView();
				}
			});
			
			if(file.getIsPause())
			{
				if(file.getIsPaused() && file.getDirection() ) mfile_state.setText("对方暂停发送");
				else if(!file.getIsPaused() && file.getDirection()) mfile_state.setText("已暂停接收");
				else if(file.getIsPaused() && !file.getDirection()) mfile_state.setText("对方暂停接收");
				else mfile_state.setText("已暂停发送");
			}else{
				
				if(file.getDirection()) mfile_state.setText("正在接收");
				else mfile_state.setText("正在发送");
			}
			ProgressBar progressbar = (ProgressBar) myView.findViewById(R.id.file_progress);
			progressbar.setMax(100);
			progressbar.setProgress(file.getProgress());
		}
		myView.setTag(R.id.group,-1);
		return myView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return data.get(groupPosition).getFileListInArray().size();
	}

	@Override
	public Object getGroup(int groupPosition) { 
		// TODO Auto-generated method stub
		return data.get(groupPosition);
	}

	@Override
	public int getGroupCount() { //返回分组数
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) { //返回分组索引
		// TODO Auto-generated method stub
		return groupPosition;
	}

	//组视图
	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View myView = mGroupInflater.inflate(R.layout.group_layout, null);
		final User user=data.get(groupPosition);
		if(data == null || data.size() == 0){
			return myView;
		}
		TextView muser_name= (TextView) myView.findViewById(R.id.user_name);
		TextView muser_ip= (TextView) myView.findViewById(R.id.user_ip);
		Button muser_send= (Button) myView.findViewById(R.id.user_send);
		TextView muser_distance= (TextView) myView.findViewById(R.id.user_distance);
		muser_name.setText(user.getAlias());
		muser_ip.setText(user.getIp());
		if(user.getIsConnected()){
			muser_send.setText("发送");
			muser_send.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(activity,FileActivity.class);
					intent.putExtra("userip", user.getIp());
					activity.startActivityForResult(intent,1);
				}
			});
			muser_distance.setText(user.getDistance()==0?"未知":user.getDistance()+"m");
		}else{
			muser_send.setText("删除");
			muser_send.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mApplication.getConnectedUsers().remove(user.getIp());
					((FriendsActivity)(mApplication.getCurrentActivity())).refreshView();
				}
			});
			muser_distance.setText("已断开连接");
		}
		myView.setTag(R.id.group,groupPosition);
		return myView;
	}

	@Override
	public boolean hasStableIds() { //行是否具有唯一id
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) { //行是否可选
		// TODO Auto-generated method stub
		return true;
	}

}
