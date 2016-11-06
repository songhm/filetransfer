package com.example.file_transfer.adapter;

import java.util.List;

import com.example.file_transfer.R;
import com.example.file_transfer.data.Record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyListViewAdapter extends BaseAdapter{

	private Context context;
	private List<Record> lists;
	private LayoutInflater layoutInflater;
	public MyListViewAdapter(Context context,List<Record> lists){
		this.context = context;
		this.lists = lists;
		layoutInflater = LayoutInflater.from(this.context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView;
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.record_item,null);
			listItemView = new ListItemView();
			listItemView.mFileName = (TextView) convertView.findViewById(R.id.file_name);
			listItemView.mFileSize = (TextView) convertView.findViewById(R.id.file_size);
			listItemView.mFileWho = (TextView) convertView.findViewById(R.id.file_who);
			convertView.setTag(listItemView);
		}else{
			listItemView = (ListItemView) convertView.getTag();
		}
		Record record = lists.get(position);
		listItemView.mFileName.setText(record.getName());
		listItemView.mFileSize.setText(record.getSize()+"     "+record.getDate());
		if(record.getDirection()){
			listItemView.mFileWho.setText("接收自"+record.getWho());
		}else{
			listItemView.mFileWho.setText("发送给"+record.getWho());
		}
		return convertView;
	}
	class ListItemView{
		TextView mFileName;
		TextView mFileSize;
		TextView mFileWho;
	}
}
