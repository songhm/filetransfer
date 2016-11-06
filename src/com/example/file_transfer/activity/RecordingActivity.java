package com.example.file_transfer.activity;




import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.file_transfer.R;
import com.example.file_transfer.adapter.MyListViewAdapter;
import com.example.file_transfer.data.MsgConst;
import com.example.file_transfer.data.Record;
import com.example.file_transfer.utils.FileUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class RecordingActivity extends BaseActivity{
	
	private final static String TAG = "RecordingActivity";
	private MyListViewAdapter adapter;
	private List<Record> lists = new ArrayList<Record>();
	private Button back_Btn;
	private ListView mrecords;
	private TextView norecord;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication.addActivity(this);
		setContentView(R.layout.activity_recording);
		//获取控件
		findViews();
		//设置控件监听
		setListener();
		//获取数据源
		lists.addAll(mApplication.getRecords());
		adapter = new MyListViewAdapter(this,lists);
		mrecords.setAdapter(adapter);
		if(lists.size()==0) norecord.setVisibility(View.VISIBLE);
		else norecord.setVisibility(View.GONE);
	}
	public void refreshView(){
		lists.clear();
		lists.addAll(mApplication.getRecords());
		adapter.notifyDataSetChanged();
		if(lists.size()==0) norecord.setVisibility(View.VISIBLE);
		else norecord.setVisibility(View.GONE);
	}
	@Override  
    protected void onResume() {  
        super.onResume(); 
      //刷新界面
      refreshView();
      Log.i(TAG,"刷新界面");
      //将本activity设为当前Activity
      mApplication.setCurrentActivity(this); 
    }  

	@Override
	protected void onDestroy(){
		mApplication.deleteActivity(this);
		super.onDestroy();
	}
	protected void findViews() {
		// TODO Auto-generated method stub
		back_Btn = (Button) findViewById(R.id.settings_back_btn);
		mrecords = (ListView) findViewById(R.id.record_list);
		norecord = (TextView) findViewById(R.id.no_recording);
	}
	protected void setListener() {
		// TODO Auto-generated method stub
		back_Btn.setOnClickListener(this);
		//单击记录查看文件
		mrecords.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//打开文件
				Record record = lists.get(position);
				String path = record.getPath();
				Intent intent = FileUtil.getOpenFileIntent(path);
				startActivity(intent);
				Log.i(TAG, "打开文件:"+lists.get(position).getName());
			}
		});
		mrecords.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final Record record = lists.get(position);
				new AlertDialog.Builder(RecordingActivity.this)
				.setMessage(R.string.deleterecord)
				.setTitle(R.string.remind)
				.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								////从内存中删除该记录
								String path = record.getPath();
								String size = record.getSize();
								String date = record.getDate();
								String who = record.getWho();
								int direction = record.getDirection()?1:0;
								Iterator<Record> mit = mApplication.getRecords().iterator();
						        while(mit.hasNext()){
						            Record mrecord = mit.next();
						            String mpath = mrecord.getPath();
									String msize = mrecord.getSize();
									String mdate = mrecord.getDate();
									String mwho = mrecord.getWho();
									int mdirection = mrecord.getDirection()?1:0;
									if(path==mpath && msize==size && mdate == date && mwho==who && mdirection ==direction)
										mit.remove();
						        }
						        //从数据库中删除该记录
								mApplication.getCreateDB().delete(path, size, date, who, direction);
								Log.i(TAG, "已从数据库中删除一条记录");
								Log.i(TAG, "记录内容为    "+"path:"+path+"size:"+size+"date:"+date+"who:"+who+"direction:"+direction);
								//刷新界面
								refreshView();
								dialog.dismiss();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();
				return true;
			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.settings_back_btn:
			Intent intent = new Intent(RecordingActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.in_from_right,R.anim.out_of_left);
			break;
			
		}
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case MsgConst.FILERECEIVESUCCESS:
			refreshView();
			break;
	}
		}
}
