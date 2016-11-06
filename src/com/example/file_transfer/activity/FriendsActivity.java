package com.example.file_transfer.activity;


import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.example.file_transfer.R;
import com.example.file_transfer.adapter.UserExpandableListAdapter;
import com.example.file_transfer.data.MsgConst;
import com.example.file_transfer.data.MyFile;
import com.example.file_transfer.data.User;
import com.example.file_transfer.utils.FileUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class FriendsActivity extends BaseActivity{

	private final static String TAG = "FriendsActivity";
	private Button mback_btn;
	private ExpandableListView mExpandableListView;
	private ExpandableListAdapter adapter;
	private List<User> lists = new ArrayList<User>();
	private TextView nofriend;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend);
		mApplication.addActivity(this);
		//获取控件
		findViews();
		//设置控件监听
		setListener();
		// 设置默认图标为不显示状态       
		mExpandableListView.setGroupIndicator(null);
		refreshList();
		adapter = new UserExpandableListAdapter(this,lists);
		mExpandableListView.setAdapter(adapter);
		for(int i = 0;i<lists.size();i++)
		{
			if(lists.get(i).getIsExpand())  mExpandableListView.expandGroup(i);
			else mExpandableListView.collapseGroup(i);
		}
		if(lists.size()==0) nofriend.setVisibility(View.VISIBLE);
		else nofriend.setVisibility(View.GONE);
	}
	@Override  
    protected void onResume() {  
        super.onResume();
       //刷新界面
       refreshView();
      //将本activity设为当前Activity
      mApplication.setCurrentActivity(this); 
    }  

	@Override
	protected void onDestroy(){
		mApplication.deleteActivity(this);
		super.onDestroy();
	}
	protected void findViews() {
		mback_btn = (Button) findViewById(R.id.friend_back_btn);
		mExpandableListView = (ExpandableListView) findViewById(R.id.friendlist);
		nofriend = (TextView) findViewById(R.id.no_friend);
	}
	protected void setListener() {
		mback_btn.setOnClickListener(this);
		// 设置一级item点击的监听器       
		mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {             
			@Override            
			public boolean onGroupClick(ExpandableListView parent, View v,                    
							int groupPosition, long id) {                
				lists.get(groupPosition).setIsExpand(!(lists.get(groupPosition).getIsExpand()));
				//Log.i(TAG, lists.get(groupPosition).getAlias()+"IsExpand设置为："+lists.get(groupPosition).getIsExpand());
				refreshView();
				return true;            
						}        
		}); 
		// 设置一级item长按的监听器    
		mExpandableListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
						// TODO Auto-generated method stub
						int mposition = (Integer) view.getTag(R.id.group);
						if (mposition != -1) {
							final User user = lists.get(mposition);
							if (user.getIsConnected()) {
 								if(!user.haveUnDoneFiles()) popDialog(0,user);
								else popDialog(1,user);	
							}
						}
						return false;
			}
		});
		// 设置二级item点击的监听器
		mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				// 打开文件
				MyFile file = lists.get(groupPosition).getFileListInArray()
						.get(childPosition);
				if (!file.getDirection() || (file.getState() && !file.getIsCancel() && !file.getIsInterrupted())) {
					String path = file.getDirection() ? file.getReceiveLocalPath() : file.getPath();
					Intent intent = FileUtil.getOpenFileIntent(path);
					startActivity(intent);
					Log.i(TAG, "打开文件:"+ lists.get(groupPosition).getFileListInArray().get(childPosition).getName());
					return true;
				}
				return false;

			}
		});
	}
	
	protected void popDialog(int i,final User user) {
		// TODO Auto-generated method stub
		switch(i){
		case 0:
			new AlertDialog.Builder(FriendsActivity.this)
			.setMessage(
					"确定与\"" + user.getAlias()
							+ "\"断开连接吗?")
			.setTitle(R.string.remind)
			.setPositiveButton(
					R.string.sure,
					new DialogInterface.OnClickListener() {

						public void onClick(
								DialogInterface dialog,
								int which) {
							mNetHelper.disConnect(user.getIp());
							Log.i(TAG, "调用NetHelper.disconnect,与"+user.getIp()+"断开");
							user.setIsConnected(false);
							// 刷新界面
							refreshView();
							dialog.dismiss();
						}
					})
			.setNegativeButton(
					R.string.cancel,
					new DialogInterface.OnClickListener() {

						public void onClick(
								DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
					}).create().show();
			break;
		case 1:
			new AlertDialog.Builder(FriendsActivity.this)
			.setMessage(
					R.string.disconnect_transfering)
			.setTitle(R.string.remind)
			.setPositiveButton(
					R.string.sure,
					new DialogInterface.OnClickListener() {

						public void onClick(
								DialogInterface dialog,
								int which) {
							
							List<MyFile> files = user.getUnDoneFiles();
							Iterator<MyFile> it = files.iterator();
							while(it.hasNext()){
								MyFile file = it.next();
								if(!file.getIsPause()){
									mFileHelper.filePause(user.getIp()+file.getPath(), file.getDirection());
								}
								file.setIsInterrupted(true);
								file.setState(true);
								Log.i(TAG, file.getName()+" 被中止");
							}
							mNetHelper.disConnect(user.getIp());
							Log.i(TAG, "调用NetHelper.disconnect,与"+user.getIp()+"断开");
							user.setIsConnected(false);
							// 刷新界面
							refreshView();
							dialog.dismiss();
						}
					})
			.setNegativeButton(
					R.string.cancel,
					new DialogInterface.OnClickListener() {

						public void onClick(
								DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
					}).create().show();
			break;
		}
	}
	private void refreshList()
	{
		//清空列表
		lists.clear();
		//加载列表
		if(userlist!=null){
			Iterator<Entry<String,User> > it = userlist.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, User> entry=(Entry<String, User>) it.next();   
				lists.add((User)entry.getValue());
			}
		}
		//对列表按距离排序
		Collections.sort(lists, new Comparator<User>(){       
			@Override      
			public int compare(User o1, User o2) { 
				if(o1.getDistance()==0 && o2.getDistance()!=0) return 1;
				else if(o1.getDistance()!=0 && o2.getDistance()==0) return -1;
				else if(o1.getDistance()>o2.getDistance()) return 1;
				else return 0;
				}     
		});
	}
	public void refreshView(){
		refreshList();
		//数据源更新
		((BaseExpandableListAdapter) adapter).notifyDataSetChanged(); 
		for(int i = 0;i<lists.size();i++)
		{
			if(lists.get(i).getIsExpand())  mExpandableListView.expandGroup(i);
			else mExpandableListView.collapseGroup(i);
		 }
		if(lists.size()==0) nofriend.setVisibility(View.VISIBLE);
		else nofriend.setVisibility(View.GONE);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.friend_back_btn:
			Intent intent = new Intent(FriendsActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.in_from_left,R.anim.out_of_right);
			break;
		}
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
			case MsgConst.USEROFF:
			case MsgConst.RECEIVEFILE:
			case MsgConst.FILERECEIVEINFO:
			case MsgConst.FILERECEIVESUCCESS:
			case MsgConst.FILEPAUSE:
			case MsgConst.FILECANCEL:
				refreshView();
				break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK){
			//得到发送文件的路径
			Bundle bundle = data.getExtras();
			String userIp = bundle.getString("userip");
			String filePaths = bundle.getString("filePaths");
			Log.i("TAG","接收fileActivity返回的信息");
			Log.i("TAG","发送给"+userIp+"文件路径为:"+filePaths);
			if(userlist.containsKey(userIp) && userlist.get(userIp).getIsConnected())
			{	
				MyFile file = new MyFile(filePaths,false);
				if(file.getSizeInLong()<20480) makeTextLong(R.string.cannotsend_size);
				else {
					userlist.get(userIp).getFileList().put(filePaths, file);
					mFileHelper.sendFile();
					mFileHelper.getMD5(file.getPath());
					Log.v("debug", "md5 file: " + file.getPath());
					Log.i(TAG, "调用mFileHelper.sendFile");
					mNetHelper
							.sendFile(userIp, filePaths, file.getSizeInLong());
					Log.i(TAG, "调用mNetHelper.sendFile");
					Log.i(TAG, "发送文件路径:" + filePaths);
					Log.i(TAG, "发送文件大小:" + file.getSizeInLong() + "B");
					// 显示文件发送状况
					userlist.get(userIp).setIsExpand(true);
					refreshView();
				}
			}else{
				makeTextLong(R.string.cannotsend_off);
			}			
		}
	}
}
