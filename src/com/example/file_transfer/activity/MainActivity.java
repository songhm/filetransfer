package com.example.file_transfer.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.example.file_transfer.R;
import com.example.file_transfer.data.MsgConst;
import com.example.file_transfer.data.User;
import com.example.file_transfer.utils.NetUtil;
import com.example.file_transfer.view.MyDialog;
import com.example.file_transfer.view.SearchDevicesView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	//搜索半径 
	private static final int SearchRadius = 30;
	private Map<String,RelativeLayout> userLayouts=new HashMap<String,RelativeLayout>();
	//主页面UI
	private SlidingMenu myMenu;
	private RelativeLayout mainlayout;
	private LinearLayout myselfLayout;
	private LinearLayout mSet;
	private TextView mMyName;
	private TextView mMyIp;
	private SearchDevicesView search_device_view;
	private ImageView mBottom1;
	private ImageView mBottom2;
	private ImageView mBottom3;
	//侧滑菜单UI
	private LinearLayout mNickname;
	private LinearLayout mFileposition;
	private LinearLayout mRequest;
	private LinearLayout mWifispot;
	private LinearLayout mRateus;
	private LinearLayout mFeedBack;
	private LinearLayout mUpdate;
	private LinearLayout mAboutus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//加载侧滑菜单
		setSlidingMenu();
		mApplication.addActivity(this);
		//将本activity设为当前Activity
	    mApplication.setCurrentActivity(this); 
		//获取控件
		findViews();
		//设置控件监听
		setListener();
		search_device_view.setWillNotDraw(false);
	}
	private void setSlidingMenu() {
		// TODO Auto-generated method stub
		// configure the SlidingMenu
		myMenu = new SlidingMenu(this);
		myMenu.setMode(SlidingMenu.LEFT);
		myMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		myMenu.setShadowWidthRes(R.dimen.shadow_width);
		myMenu.setShadowDrawable(R.drawable.shadow);
		myMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		myMenu.setFadeDegree(0.35f);
		/**
		 * SLIDING_WINDOW will include the Title/ActionBar in the content
		 * section of the SlidingMenu, while SLIDING_CONTENT does not.
		 */
		myMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		myMenu.setMenu(R.layout.leftmenu);
	}
	@Override  
    protected void onResume() {  
        super.onResume(); 
      //将本activity设为当前Activity
      mApplication.setCurrentActivity(this);
    }  

	@Override
	protected void onDestroy(){
		mApplication.deleteActivity(this);
		Log.i(TAG, "活动被销毁");
		super.onDestroy();
	}
	
	@SuppressLint("InflateParams")
	protected void findViews() {
		// TODO Auto-generated method stub
		//获取主界面控件
		mainlayout= (RelativeLayout) findViewById(R.id.MainLayout);
		mSet = (LinearLayout) findViewById(R.id.main_search_set);
		myselfLayout = (LinearLayout) findViewById(R.id.main_search_myself);
	    mMyName = (TextView) findViewById(R.id.main_search_name);
		mMyIp = (TextView)findViewById(R.id.main_search_ip);
		search_device_view = (SearchDevicesView) findViewById(R.id.search_device_view);
		mBottom1 = (ImageView) findViewById(R.id.main_bottom1);
		mBottom2 = (ImageView) findViewById(R.id.main_bottom2);
		mBottom3 = (ImageView) findViewById(R.id.main_bottom3);
		//获取侧滑菜单控件
		mNickname= (LinearLayout) findViewById(R.id.nickname_layout);
		mFileposition= (LinearLayout) findViewById(R.id.fileposition_layout);
		mRequest= (LinearLayout) findViewById(R.id.request_layout);
		mWifispot= (LinearLayout) findViewById(R.id.wifispot_layout);
		mRateus= (LinearLayout) findViewById(R.id.rateus_layout);
		mFeedBack= (LinearLayout) findViewById(R.id.feedback_layout);
		mUpdate= (LinearLayout) findViewById(R.id.update_layout);
		mAboutus= (LinearLayout) findViewById(R.id.aboutus_layout);
	}
	protected void setListener() {
		//设置主界面控件监听
		mSet.setOnClickListener(this);
		mBottom1.setOnClickListener(this);
		mBottom2.setOnClickListener(this);
		mBottom3.setOnClickListener(this);   
		//设置侧滑菜单界面监听
		mNickname.setOnClickListener(this);
		mFileposition.setOnClickListener(this);
		mRequest.setOnClickListener(this);
		mWifispot.setOnClickListener(this);
		mRateus.setOnClickListener(this);
		mFeedBack.setOnClickListener(this);
		mUpdate.setOnClickListener(this);
		mAboutus.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.main_bottom1:
			Intent intent = new Intent(MainActivity.this,RecordingActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.in_from_left,R.anim.out_of_right);
			break;
		case R.id.main_bottom2:
			if(!search_device_view.isSearching()){
				if(!NetUtil.isWifiActive()){	
				      Toast.makeText(this, R.string.wifi_fail, Toast.LENGTH_SHORT).show();
				      NetUtil.openSetting(this);
				      break;
				}else
				{ 	me.setIp(NetUtil.getlocalip());
					search_device_view.setSearching(true);
					double lng = me.getLongtitude();
					double lat = me.getLatitude();
					//网络辅助类搜索用户开启
					mNetHelper.startSearch();
					Log.i(TAG,"调用mNetHelper.startSearch");
					Log.i(TAG,"参数为："+me.getMac()+","+me.getIp()+","+me.getAlias()+","+lng+","+lat);
					mSet.setVisibility(View.GONE);
					showMyInfo(true);
				}
			}else{
				//网络辅助类搜索用户关闭
				mNetHelper.StopSearch();
				Log.i(TAG,"调用mNetHelper.stopSearch");
				showMyInfo(false);
				mSet.setVisibility(View.VISIBLE);
				//清空搜索用户列表
				searchusers.clear();
				search_device_view.setSearching(false);
				//删除所有点
				Iterator<Entry<String, RelativeLayout>> iter = userLayouts.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String,RelativeLayout> entry = (Entry<String,RelativeLayout>) iter.next();
					RelativeLayout val = entry.getValue();
					mainlayout.removeView(val);
					}
				userLayouts.clear();
			}
			break;
		case R.id.main_bottom3:
			Intent intent1 = new Intent(MainActivity.this,FriendsActivity.class);
			startActivity(intent1);
			finish();
			overridePendingTransition(R.anim.in_from_right,R.anim.out_of_left);
			break;
		case R.id.main_search_set:
			//显示菜单
			myMenu.showMenu();
			break;
		case R.id.nickname_layout:
			showTheDialog(0);
			Log.i(TAG, "显示编辑框");
			break;
		case R.id.fileposition_layout:
			break;
		case R.id.request_layout:
			break;
		case R.id.wifispot_layout:
			break;
		case R.id.rateus_layout:
			break;
		case R.id.feedback_layout:
			break;
		case R.id.update_layout:
			break;
		case R.id.aboutus_layout:
			break;
		}
	}
	private void showTheDialog(int i) {
		// TODO Auto-generated method stub
		switch(i){
		case 0:
			final EditText inputName = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle(R.string.inputname)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(inputName)
			.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String name = inputName.getText().toString();
					if(!name.equals("")){
						SharedPreferences preference = MainActivity.this.getSharedPreferences("myself", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = preference.edit();   
						editor.putString("Alias", name);   
						editor.putString("FilePath", me.getReceviceFilePath());
						editor.putBoolean("NeedRequest", me.getIsNeedRequest());
						editor.commit();
						me.setAlias(name);
						makeTextShort(R.string.revise_suc);
						dialog.dismiss();
					}else{
						makeTextShort(R.string.revise_fail);
					}
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}	
			})
			.create()
			.show();
		break;
		}
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		//搜索到用户时处理
		case MsgConst.USERIN:
			User u = (User)msg.obj;
			Log.i(TAG,"接收到USERIN,增加用户"+u.getIp());
			addUserPoint(u);
			break;
		//搜索到的用户下线
		case MsgConst.USEROUT:
			String str = (String) msg.obj;
			Log.i(TAG,"接收到USEROUT,删除用户"+str);
			deleteUserPoint(str);
			break;
		//连接请求消息处理
		case MsgConst.REQUESTCONNECT:
			String u1 = (String)msg.obj;
			showRequestDialog(searchusers.get(u1).getAlias(),searchusers.get(u1).getIp());
			Log.i(TAG,"接收到REQUESTCONNECT,"+searchusers.get(u1).getIp()+"请求连接");
			break;
		//连接拒绝消息处理
		case MsgConst.REJECTCONNECT:
			Log.i(TAG,"接收到REJECTCONNECT,"+(String)msg.obj+"拒绝连接请求");
			RelativeLayout point = userLayouts.get((String)msg.obj);
			TextView mpointname=(TextView)point.findViewById(R.id.point_name);
			TextView mpointip=(TextView)point.findViewById(R.id.point_ip);
			ImageView mpoint=(ImageView)point.findViewById(R.id.img);
			mpointname.setVisibility(View.INVISIBLE);
			mpointip.setText(R.string.rejectconnect);
			mpoint.setImageResource(R.drawable.red);
			break;
		//连接接受消息处理
		case MsgConst.ACCEPTCONNECT:
			final String str1 =(String)msg.obj;
			Log.i(TAG,"接收到ACCEPTCONNECT,"+(String)msg.obj+"同意连接请求");
			RelativeLayout point1 = userLayouts.get(str1);
			TextView mpointname1=(TextView)point1.findViewById(R.id.point_name);
			TextView mpointip1=(TextView)point1.findViewById(R.id.point_ip);
			ImageView mpoint1=(ImageView)point1.findViewById(R.id.img);
			mpointname1.setVisibility(View.INVISIBLE);
			mpointip1.setText(R.string.connect_suc);
			mpoint1.setImageResource(R.drawable.green);
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = handler.obtainMessage(MsgConst.EXEUSERMOVE,str1);
				handler.sendMessage(msg);
				}
				};
			new Timer().schedule(task, 500);
			break;
		}
}
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MsgConst.EXEUSERMOVE:
				String str2=(String)msg.obj;  
		        moveUserPoint(str2);  
				break;
			}
		}
	};
	private void showMyInfo(boolean sign){
		if(sign) 
		{
			myselfLayout.setVisibility(View.VISIBLE);
			mMyName.setText(me.getAlias());
			Log.i(TAG, "设备名称为: "+me.getAlias());
			mMyIp.setText("IP:"+me.getIp());
		}else{
			myselfLayout.setVisibility(View.GONE);
		}
	}
	private void showRequestDialog(String name,final String ip) {
		// TODO Auto-generated method stub
		new MyDialog.Builder(MainActivity.this) 
		.setTitle(R.string.requestconnect)
		.setName(name)
		.setIp(ip)
		.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub	
				mNetHelper.acceptConnect(ip);
				userlist.put(ip, searchusers.get(ip));
				searchusers.remove(ip);
				RelativeLayout point1 = userLayouts.get(ip);
				TextView mpointname1=(TextView)point1.findViewById(R.id.point_name);
				TextView mpointip1=(TextView)point1.findViewById(R.id.point_ip);
				ImageView mpoint1=(ImageView)point1.findViewById(R.id.img);
				mpointname1.setVisibility(View.INVISIBLE);
				mpointip1.setText(R.string.connect_suc);
				mpoint1.setImageResource(R.drawable.green);
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Message msg = handler.obtainMessage(MsgConst.EXEUSERMOVE,ip);
					handler.sendMessage(msg);
					}
				};
				new Timer().schedule(task, 400);
				dialog.dismiss();
			}
		})
		.setNegativeButton(R.string.refuse, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mNetHelper.rejectConnect(ip);
				dialog.dismiss();
			}
		})
		.create().show();
	}
	@SuppressLint("InflateParams")
	private void addUserPoint(User user)
	{
		//解析用户信息
		String name=user.getAlias();
		final String ip=user.getIp();
		int distance = user.getDistance();
		//获取位置点布局并加入布局map
		RelativeLayout pointlayout=(RelativeLayout)this.getLayoutInflater().inflate(R.layout.point,null);
		userLayouts.put(ip,pointlayout);
		//将点布局加入到主布局
		mainlayout.addView(pointlayout);
		//获取点布局中的控件
		final TextView mpointname=(TextView)pointlayout.findViewById(R.id.point_name);
		final TextView mpointip=(TextView)pointlayout.findViewById(R.id.point_ip);
		final ImageView mpoint=(ImageView)pointlayout.findViewById(R.id.img);
		//如果已连接无法点击请求
		if(userlist.containsKey(ip) && userlist.get(ip).getIsConnected()){
			mpointname.setVisibility(View.INVISIBLE);
			mpointip.setText(R.string.connected);
			mpoint.setImageResource(R.drawable.green);
		}else{
			//显示名字
			mpointname.setText(name);
			//显示IP地址
			mpointip.setText(ip);
			//设置点布局监听
			pointlayout.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mpointname.setVisibility(View.INVISIBLE);
					mpointip.setText(R.string.requestconnecting);
					mpoint.setImageResource(R.drawable.yellow);
					mNetHelper.requestConnect(ip);
					Log.i(TAG,"调用mNetHelper.requestConnect");
					Log.i(TAG,"参数为："+ip);
				}
			});
		}
		//设置点布局显示位置
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(pointlayout.getLayoutParams());  
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		int x=mainlayout.getWidth()/2;
		int y=mainlayout.getHeight()/2;
		int r=(int)(x*distance/(double)SearchRadius);
		int left=(int)(x-r+Math.random()*r*2);
		int top=Math.random()<0.5?(int)(y-Math.sqrt(r*r-(left-x)*(left-x))):(int)(y+Math.sqrt(r*r-(left-x)*(left-x)));        
		int t1h = getViewHeight(mpointname);         
		int t2h = getViewHeight(mpointip);
		int vw = getViewWidth(mpoint);
		int vh = getViewHeight(mpoint);
		lp.setMargins(left-vw/2,top-t1h-t2h-vh/2, 0, 0); 
		pointlayout.setLayoutParams(lp);
		//设置进入动画
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
		pointlayout.startAnimation(animation);
		
	}
	private void deleteUserPoint(String str)
	{
		RelativeLayout pointlayout=userLayouts.get(str);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
		pointlayout.startAnimation(animation);
		mainlayout.removeView(pointlayout);
		userLayouts.remove(str);
	}
	private void moveUserPoint(String str)
	{
		RelativeLayout pointlayout=userLayouts.get(str);
		if(pointlayout==null) Log.v("pointlayout1","null");
		AnimationSet animation = (AnimationSet)AnimationUtils.loadAnimation(this, R.anim.zoom_out);
		int[] location = new int[2]; 
		//获取控件绝对位置
		mBottom3.getLocationOnScreen(location); 
		int x = location[0];
		int y = location[1];
		//Log.v("ax", ""+x);
		//Log.v("ay", ""+y);
		TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.ABSOLUTE,x,Animation.RELATIVE_TO_SELF,0,Animation.ABSOLUTE,y);
	    translateAnimation.setDuration(1500);
		animation.addAnimation(translateAnimation);
		//if(pointlayout==null) Log.v("pointlayout2","null");
		pointlayout.startAnimation(animation);
		//if(pointlayout==null) Log.v("pointlayout3","null");
		mainlayout.removeView(pointlayout);
		userLayouts.remove(str);
	}
	private int getViewHeight(View view)
	{
		view.measure(0, 0);
		return view.getMeasuredHeight();
	}
	private int getViewWidth(View view)
	{
		view.measure(0, 0);
		return view.getMeasuredWidth();
	}
}
