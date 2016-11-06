package com.example.file_transfer.activity;

import java.util.Map;

import com.example.file_transfer.R;
import com.example.file_transfer.application.MyApplication;
import com.example.file_transfer.data.Myself;
import com.example.file_transfer.data.User;
import com.example.file_transfer.net.NetHelper;
import com.example.file_transfer.utils.FileHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

public abstract class BaseActivity extends Activity implements OnClickListener{

	protected MyApplication mApplication;
	protected NetHelper mNetHelper;
	protected FileHelper mFileHelper;
	protected Myself me;
	protected Map<String,User> userlist;
	protected Map<String,User> searchusers;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mApplication = (MyApplication) getApplication();
		userlist = mApplication.getConnectedUsers();
		searchusers = mApplication.getSearchUsers();
		//获取自己
		me = mApplication.getMyself();
		//获取网络辅助类
		mNetHelper = mApplication.getNetHelper();
		//获取文件辅助类
		mFileHelper = mApplication.getFileHelper();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	} 
	public void makeTextShort(int text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void makeTextLong(int cannotsendSize) {
		Toast.makeText(this, cannotsendSize, Toast.LENGTH_LONG).show();
	}
	public abstract void processMessage(Message msg);
	protected abstract void findViews();
	protected abstract void setListener();
	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 || keyCode == KeyEvent.KEYCODE_HOME) {
			showDialog(0);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			return new AlertDialog.Builder(BaseActivity.this)
					.setMessage(R.string.isexit)
					.setTitle(R.string.remind)
					.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									mApplication.exit();
									dialog.dismiss();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();

		}
		return null;

	}
}
