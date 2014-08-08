package com.color.speechbubble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MessageActivity extends ListActivity {
	/** Called when the activity is first created. */

	public static ArrayList<Message> messages;
	public static AwesomeAdapter adapter;
	private EditText text;
	private static Random rand = new Random();	
	private static String sender;
	private MessageDataSource dataSrc;
	private String username,myname,userid,friendid,serverURL;
	
	private SharedPreferences settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		text = (EditText) this.findViewById(R.id.text);
		
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		myname = intent.getStringExtra("myname");
		userid = intent.getStringExtra("userid");
		friendid = intent.getStringExtra("friendid");

		this.setTitle(username);
		
		
		PreferenceManager.setDefaultValues(this, "ip_address",Context.MODE_PRIVATE,R.xml.preferences, false);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		serverURL = settings.getString("ip_address", "");
		
		dataSrc = new MessageDataSource(getBaseContext());
		dataSrc.open();
		
		messages = dataSrc.getAllMessages(userid,friendid);	
		adapter = new AwesomeAdapter(getBaseContext(), messages,userid);
		setListAdapter(adapter);
	}
	public void sendMessage(View v)
	{
		String newMessage = text.getText().toString().trim(); 
		if(newMessage.length() > 0)
		{
			
		      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
	  		  String currentDate = dateFormat.format(new Date());
	          
	  		  List<NameValuePair> value = new ArrayList<NameValuePair>(2);
	          value.add(new BasicNameValuePair("sender", userid));
	          value.add(new BasicNameValuePair("receiver", friendid));
	          value.add(new BasicNameValuePair("date_time", currentDate));
	          value.add(new BasicNameValuePair("message", text.getText().toString()));
	          value.add(new BasicNameValuePair("status", "0"));
	          
	          Server.makeHttpRequest("http://"+serverURL+"/android_message_server/add_message.php","POST",value);
	          
	          addNewMessage();
		}
	}	

	void addNewMessage()
	{
		Message message = new Message();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String currentDate = dateFormat.format(new Date());
		
		message = dataSrc.addMessage(userid, friendid, currentDate, text.getText().toString(), 0);
		messages.add(message);
		
		adapter.notifyDataSetChanged();
		getListView().setSelection(messages.size()-1);
		text.setText("");
	}
	
	@Override
	public void onPause(){
		super.onPause();	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_settings:
			Intent settingIntent = new Intent(this,AppPreference.class);
			startActivity(settingIntent);
			return true;
		case R.id.profile_settings:
			Intent intent = new Intent(this,SettingActivity.class);
			intent.putExtra("myname", myname);
			intent.putExtra("userid", userid);
			startActivity(intent);
			return true;
		case R.id.about_us:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}