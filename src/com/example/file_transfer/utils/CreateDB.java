package com.example.file_transfer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.file_transfer.data.Record;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CreateDB {
	private DBOpenHelper dbOpenHelper;
	
	public CreateDB(Context context){
		this.dbOpenHelper = new DBOpenHelper(context);
	}
	
	public void save(String filepath, String clean){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into send(filepath, clean) values(?,?)",
				new Object[]{filepath,clean});
	}
	public void save(String filepath, String mac, long sended, String ThreadNO){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into receive(filepath, mac, length, ThreadNO) values(?,?,?,?)",
				new Object[]{filepath,mac,sended,ThreadNO});
	}
	public void save(String path, String size, String date,String who,int direction){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into record(path, size, date, who, direction) values(?,?,?,?,?)",
				new Object[]{path,size,date,who,direction});
	}
	public void delete(String path, String size, String date,String who,int direction){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from record where path=? and size=? and date=? and who=? and direction=?",
				new Object[]{path,size,date,who,direction});
	}
	public void delete(String filepath, String mac, String ThreadNO){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from receive where filepath=? and mac=? and ThreadNO=?", new Object[]{filepath,mac,ThreadNO});
	}
	
	
	public String getClean(File uploadFile){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select clean from send where filepath=?", 
				new String[]{uploadFile.getAbsolutePath()});
		if(cursor.moveToFirst()){
			return cursor.getString(0);
		}
		return null;
	}
	public String getMAC(String filepath){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mac from receive where filepath=?", 
				new String[]{filepath});
		if(cursor.moveToFirst()){
			return cursor.getString(0);
		}
		return null;
	}
	public long getLength(String filepath, String NO){
		Log.v("debug","call getLength");
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select length from receive where filepath=? and ThreadNO=?", 
				new String[]{filepath,NO});
		Log.v("debug","call getLength rawQ");
		if(cursor.moveToFirst()){
			Log.v("debug","call getLength in db");
			return cursor.getInt(0);
		}
		Log.v("debug","call getLength in db");
		return 0;
	}
	public List<Record> getRecords() {
		String sql = "select * from record";
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		List<Record> recordList = new ArrayList<Record>();
		Record record = null;
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			String path = cursor.getString(cursor.getColumnIndex("path"));
			String size = cursor.getString(cursor.getColumnIndex("size"));
			String date = cursor.getString(cursor.getColumnIndex("date"));
			Boolean direction = cursor.getInt(cursor.getColumnIndex("direction"))==0?false:true;
			String who = cursor.getString(cursor.getColumnIndex("who"));
			record = new Record(path,size,date,direction,who);
			recordList.add(record);
		}
		return recordList;
	}
	public  void saveRecords(List<Record> list) {
		Iterator<Record> it = list.iterator();
        while(it.hasNext()){
            Record record = it.next();
            String path = record.getPath();
			String size = record.getSize();
			String date = record.getDate();
			String who = record.getWho();
			int direction = record.getDirection()?1:0;
			save(path, size, date, who, direction);
        }
	}
}
