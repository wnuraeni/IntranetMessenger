package com.color.speechbubble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class MessageDataSource {

	//Database fields
	private SQLiteDatabase database;
	private MessageSQLiteHelper dbHelper;
	private String[] allColumns = {
			MessageSQLiteHelper.COLUMN_ID,
			MessageSQLiteHelper.COLUMN_SENDER,
			MessageSQLiteHelper.COLUMN_RECEIVER,
			MessageSQLiteHelper.COLUMN_DATETIME,
			MessageSQLiteHelper.COLUMN_MESSAGE,
			MessageSQLiteHelper.COLUMN_STATUS};
	private String[] allColumns_user = {
			MessageSQLiteHelper.COLUMN_ID_USER,
			MessageSQLiteHelper.COLUMN_USERNAME,
			MessageSQLiteHelper.COLUMN_USER_ID,
			};
	public MessageDataSource(Context context){
		dbHelper = new MessageSQLiteHelper(context);
	}
	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
	}
	public void close(){
		dbHelper.close();
	}
	public Cursor getUserData(){
		Cursor cursor = database.query(MessageSQLiteHelper.TABLE_USER, allColumns_user, null, null, null, null, null);
		return cursor;
	}
	public void updateUserData(String username, long id){
		ContentValues values = new ContentValues();
		values.put(MessageSQLiteHelper.COLUMN_USERNAME, username);
		database.update(MessageSQLiteHelper.TABLE_USER, values, MessageSQLiteHelper.COLUMN_USER_ID + " = ? ",new String[]{String.valueOf(id)});		
	
	}
	public void addUserData(String username, int userid){
		ContentValues values = new ContentValues();
		values.put(MessageSQLiteHelper.COLUMN_USERNAME, username);
		values.put(MessageSQLiteHelper.COLUMN_USER_ID, userid);
		long insertId =  database.insert(MessageSQLiteHelper.TABLE_USER, null, values);
	}
	public Message addMessage(String sender, String receiver, String datetime, String message, int status){
		Message newMessage = new Message();
		try{
		//insert new message
		ContentValues values = new ContentValues();
		values.put(MessageSQLiteHelper.COLUMN_MESSAGE, message);
		values.put(MessageSQLiteHelper.COLUMN_SENDER, sender);
		values.put(MessageSQLiteHelper.COLUMN_RECEIVER, receiver);
		values.put(MessageSQLiteHelper.COLUMN_DATETIME, datetime);
		values.put(MessageSQLiteHelper.COLUMN_STATUS, status);
		long insertId = database.insert(MessageSQLiteHelper.TABLE_MESSAGES, null, values);
		//get new message
		Cursor cursor = database.query(MessageSQLiteHelper.TABLE_MESSAGES, allColumns, MessageSQLiteHelper.COLUMN_ID+" = "+insertId, null, null, null, null);
		cursor.moveToFirst();
		newMessage = cursorToMessage(cursor);
		cursor.close();
		return newMessage;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return newMessage;
		
		
	}
	public Message getMessage(int id){
		Cursor cursor = database.query(MessageSQLiteHelper.TABLE_MESSAGES, allColumns, MessageSQLiteHelper.COLUMN_ID+" = "+id, null, null, null, null);
		if(cursor != null)
			cursor.moveToFirst();
		Message message = cursorToMessage(cursor);
		cursor.close();
		return message;
		
	}
	public void deleteMessage(Message message){
		long id = message.getId();
		System.out.println("Message deleted with id:"+id);
		database.delete(MessageSQLiteHelper.TABLE_MESSAGES, MessageSQLiteHelper.COLUMN_ID+ " = "+id, null);
	}
	public int updateMessage(Message message){
		ContentValues values = new ContentValues();
		values.put(MessageSQLiteHelper.COLUMN_STATUS, "1");
		return database.update(MessageSQLiteHelper.TABLE_MESSAGES, values, MessageSQLiteHelper.COLUMN_ID + " = ? ",new String[]{String.valueOf(message.getId())});		
	}
	
	public ArrayList<Message> getAllMessages(String sender,String receiver){
		ArrayList<Message> messages = new ArrayList<Message>();
		
		Cursor cursor = database.rawQuery(
				"SELECT * FROM messages WHERE (sender = ? OR sender = ?) AND (receiver = ?  OR receiver = ?)",
				new String[] {sender, receiver, receiver, sender});
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Message message = cursorToMessage(cursor);
			messages.add(message);
			cursor.moveToNext();
		}
		cursor.close();
		return messages;
	}
	
	private Message cursorToMessage(Cursor cursor){
		Message message = new Message();
		message.setId(cursor.getLong(cursor.getColumnIndex("_id")));
		message.setMessage(cursor.getString(cursor.getColumnIndex("message")));
		message.setSender(cursor.getString(cursor.getColumnIndex("sender")));
		message.setReceiver(cursor.getString(cursor.getColumnIndex("receiver")));
		//set statusmessage
		//set ismine
		//set dll ke AwesomeAdapter
		
		return message;
	}
}
