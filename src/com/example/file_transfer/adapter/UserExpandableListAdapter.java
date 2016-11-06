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
 * ��չlistview������
 *
 */

public class UserExpandableListAdapter extends BaseExpandableListAdapter {
	
	private final static String TAG = "UserExpandableListAdapter";
	private MyApplication mApplication; 
	private Activity activity;	//��activity
	protected Resources res;
	private LayoutInflater mChildInflater;	//���ڼ��ط���Ĳ���xml
	private LayoutInflater mGroupInflater;	//���ڼ��ض�Ӧ�����û��Ĳ���xml
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
	public long getChildId(int groupPosition, int childPosition) { //����item����
		// TODO Auto-generated method stub
		return childPosition;
	}

	//������ͼ
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
			//��ʾ�ļ���
			mfile_name.setText(file.getName());
			//��ʾ�ļ���С
			mfile_size.setText(file.getSize());
			if(file.getIsInterrupted()){
				//��ʾ����״̬
				mfile_state.setText(R.string.filefail);
			}else if(file.getIsCancel())
			{
				//��ʾ����״̬
				if(file.getIsCanceled() && file.getDirection() ) mfile_state.setText("�Է�ȡ������");
				else if(!file.getIsCanceled() && file.getDirection()) mfile_state.setText("��ȡ������");
				else if(file.getIsCanceled() && !file.getDirection()) mfile_state.setText("�Է�ȡ������");
				else mfile_state.setText("��ȡ������");
			}else{
				//��ʾ����
				mfile_date.setText(file.getDate());
				//��ʾ����״̬
				if(file.getDirection()) mfile_state.setText("���ճɹ�");
				else mfile_state.setText("���ͳɹ�");
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
			if(file.getIsPause()) mfile_pause.setText("����");
			else mfile_pause.setText("��ͣ");
			mfile_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//����ȡ��ʱ��
					file.setDate();
					//�����ļ����
					file.setState(true);
					file.setIsCancel(true);
					file.setIsCanceled(false);
					//�����ļ����丨����ȡ�����ݴ���
					mApplication.getFileHelper().fileCancel(user.getIp()+file.getPath(),file.getDirection());
					//�������縨���෢udp��Ϣ֪ͨ�Է�
					mApplication.getNetHelper().fileCancel(user.getIp(),file.getPath());
					Log.i(TAG,"����mNetHelper.fileCancel");
					Log.i(TAG,"����mFileHelper.fileCancel");
					Log.i(TAG,"ȡ���û���:"+user.getAlias());
					Log.i(TAG,"ȡ��"+(file.getDirection()?"����":"����")+"�ļ�·��:"+file.getPath());
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
							Log.i(TAG,"����NetHelper.filePause,�÷��ͷ���������");
							Log.i(TAG,"�����û���:"+user.getAlias());
							Log.i(TAG,"����"+(file.getDirection()?"����":"����")+"�ļ�·��:"+file.getPath());
						}else{
							file.setIsPause(false);
							mApplication.getFileHelper().sendFile();
							Log.i(TAG,"����FileHelper.sendFile");
							mApplication.getNetHelper().filePause(user.getIp(),file.getPath());
							Log.i(TAG,"����NetHelper.filePause");
							Log.i(TAG,"�����û���:"+user.getAlias());
							Log.i(TAG,"����"+(file.getDirection()?"����":"����")+"�ļ�·��:"+file.getPath());
						}
					}else{
						//�����ļ����丨������ͣ���ݴ���
						mApplication.getFileHelper().filePause(user.getIp()+file.getPath(),file.getDirection());
						Log.i(TAG,"����mFileHelper.filePause");
						Log.i(TAG,"��ͣ�û���:"+user.getAlias());
						Log.i(TAG,"��ͣ"+(file.getDirection()?"����":"����")+"�ļ�·��:"+file.getPath());
						file.setIsPause(true);
						file.setIsPaused(false);
						//�������縨���෢udp��Ϣ֪ͨ�Է�
						mApplication.getNetHelper().filePause(user.getIp(),file.getPath());
						Log.i(TAG,"����mNetHelper.filePause");
						
						
					}
					//ˢ�½���
					((FriendsActivity)(mApplication.getCurrentActivity())).refreshView();
				}
			});
			
			if(file.getIsPause())
			{
				if(file.getIsPaused() && file.getDirection() ) mfile_state.setText("�Է���ͣ����");
				else if(!file.getIsPaused() && file.getDirection()) mfile_state.setText("����ͣ����");
				else if(file.getIsPaused() && !file.getDirection()) mfile_state.setText("�Է���ͣ����");
				else mfile_state.setText("����ͣ����");
			}else{
				
				if(file.getDirection()) mfile_state.setText("���ڽ���");
				else mfile_state.setText("���ڷ���");
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
	public int getGroupCount() { //���ط�����
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) { //���ط�������
		// TODO Auto-generated method stub
		return groupPosition;
	}

	//����ͼ
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
			muser_send.setText("����");
			muser_send.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(activity,FileActivity.class);
					intent.putExtra("userip", user.getIp());
					activity.startActivityForResult(intent,1);
				}
			});
			muser_distance.setText(user.getDistance()==0?"δ֪":user.getDistance()+"m");
		}else{
			muser_send.setText("ɾ��");
			muser_send.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mApplication.getConnectedUsers().remove(user.getIp());
					((FriendsActivity)(mApplication.getCurrentActivity())).refreshView();
				}
			});
			muser_distance.setText("�ѶϿ�����");
		}
		myView.setTag(R.id.group,groupPosition);
		return myView;
	}

	@Override
	public boolean hasStableIds() { //���Ƿ����Ψһid
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) { //���Ƿ��ѡ
		// TODO Auto-generated method stub
		return true;
	}

}
