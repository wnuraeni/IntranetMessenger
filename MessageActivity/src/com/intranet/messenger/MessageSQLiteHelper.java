package com.color.speechbubble;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MessageSQLiteHelper extends SQLiteOpenHelper{

	public static final String TABLE_MESSAGES="messages";
	public static final String COLUMN_ID="_id";
	public static final String COLUMN_SENDER="sender";
	public static final String COLUMN_RECEIVER="receiver";
	public static final String COLUMN_DATETIME="datetime";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_STATUS = "status";
	
	public static final String TABLE_USER="user";
	public static final String COLUMN_ID_USER = "_id";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_USER_ID = "userid";
	
	private static final String DATABASE_NAME="Database.db";
	private static final int DATABASE_VERSION=1;
	
	//Database creation for message sql statement             
	private static final String DATABASE_CREATE="create table if not exists "
			+TABLE_MESSAGES+"("+
			COLUMN_ID + " integer primary key autoincrement, "+ 
			COLUMN_SENDER + " text not null, " +
			COLUMN_RECEIVER + " text not null, "+
			COLUMN_DATETIME + " text not null, "+
			COLUMN_MESSAGE + " text not null, "+
			COLUMN_STATUS + " numeric not null "
					+");";
	
	//Database user
	private static final String DATABASE_CREATE2="create table if not exists "
			+TABLE_USER+"("+
			COLUMN_ID_USER + " integer primary key autoincrement, "+ 
			COLUMN_USERNAME + " text not null, " +
			COLUMN_USER_ID + " numeric not null );";
	
	public MessageSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		db.execSQL(DATABASE_CREATE2);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MessageSQLiteHelper.class.getName(),"Upgrading database from version"
				+oldVersion+ " to "+newVersion+"which will destroy old data");
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_MESSAGES);
		onCreate(db);
		
	}

}
