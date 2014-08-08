package com.color.speechbubble;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashActivity extends Activity {

	SharedPreferences settings;
	String serverURL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.splash);
			
		//load preference
		PreferenceManager.setDefaultValues(this,R.xml.preferences, true);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		serverURL = settings.getString("ip_address", "");
		
//		Toast.makeText(getApplicationContext(), serverURL, Toast.LENGTH_LONG).show();
		
		if(serverURL==""){
			final AlertDialog alert = new AlertDialog.Builder(SplashActivity.this).create();
			alert.setMessage("Please set server IP Address in Settings");
			alert.setButton("Close", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				alert.dismiss();
				}
			});
			alert.show();
		}else{
			new Timer().schedule(new MyTimerTask(),3* 1000);
			
		}
	}

	class MyTimerTask extends TimerTask{
		@Override
		public void run(){
			new ConnectToServer().execute("http://"+serverURL);
		}
	}
	class ConnectToServer extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String...urls){
			int responseCode = 0;
			
			responseCode = Server.isConnected(urls[0]);
			
			return Integer.toString(responseCode);
		}
		
		@Override
		protected void onPostExecute(String response){
		if(response.equals("200")){
				finish();
				
				Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
				startActivity(intent);
			}else{
				final AlertDialog alert = new AlertDialog.Builder(SplashActivity.this).create();
				alert.setMessage("No Connection! Please turn on your WiFi");
				alert.setButton("Close", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					finish();
					alert.dismiss();
					}
				});
				alert.show();
			}
		}
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
			finish();
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
