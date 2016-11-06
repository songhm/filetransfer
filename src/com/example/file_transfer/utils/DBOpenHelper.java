package com.example.file_transfer.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "upload.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE send (_id integer primary key autoincrement, filepath varchar(100), clean varchar(10))");
		db.execSQL("CREATE TABLE receive (_id integer primary key autoincrement, filepath varchar(100), mac varchar(50), length integer, ThreadNO varchar(3))");
		db.execSQL("CREATE TABLE record (_id integer primary key autoincrement, path varchar(100), size varchar(20), date varchar(20), who varchar(50), direction integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS uploadlog");
		onCreate(db);		
	}

}
