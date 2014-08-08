package com.color.speechbubble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private EditText usernameTxt, phoneTxt;
	private Button registerBtn;
	private String myname,userid,serverURL;
	
	private SharedPreferences settings;
	private SharedPreferences.Editor prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set strict mode
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		//set full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_login);
		
	
		usernameTxt = (EditText) findViewById(R.id.username);
		phoneTxt = (EditText) findViewById(R.id.phone);
		
		registerBtn = (Button) findViewById(R.id.register);
		
		//-------------------Load preferences----------------------
		
		PreferenceManager.setDefaultValues(this, "ip_address",Context.MODE_PRIVATE,R.xml.preferences, false);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
		
		String username = settings.getString("username","");
		String phone = settings.getString("phone","");
		serverURL = settings.getString("ip_address","");
		
		//------------ Check User is registered ------------------------
		if(username.equals("") && phone.equals("")){

			AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).create();
			dialog.setButton("Close", new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.setMessage("Please Register");
			dialog.show();
		}else{
			//-------- Login to server ---------------
			List<NameValuePair> value = new ArrayList<NameValuePair>(2); 
			
			value.add(new BasicNameValuePair("username", username));
			value.add(new BasicNameValuePair("phone", phone));
			
			JSONObject response = Server.makeHttpRequest("http://"+serverURL+"/android_message_server/login.php", "POST", value);
			
			String result="";
			try {
				result = response.getString("response");
				myname = response.getString("username");
				userid = response.getString("userid");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(result.equals("success")){
				//-------- redirect to online user list
				
				finish();
				
				Intent intent2 = new Intent(LoginActivity.this,MyService.class);
				intent2.putExtra("userid", userid);
				startService(intent2);
				
				Intent intent = new Intent(LoginActivity.this,OnlineListActivity.class);
				intent.putExtra("myname", myname);
				intent.putExtra("userid", userid);
				startActivity(intent);
			}else{
				AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).create();
				dialog.setButton("Close", new DialogInterface.OnClickListener() {	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.setMessage("Login Failed");
				dialog.show();
			}
		}
		
		registerBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = usernameTxt.getText().toString();
				String phone = phoneTxt.getText().toString();
				
				//-------Register to server----------------
				List<NameValuePair> value = new ArrayList<NameValuePair>(2); 
				value.add(new BasicNameValuePair("username", username));
				value.add(new BasicNameValuePair("phone", phone));
				value.add(new BasicNameValuePair("status_online", "1"));
				
				JSONObject response =  Server.makeHttpRequest("http://"+serverURL+"/android_message_server/add_user.php","POST",value);
				
				try{
					String user_name = response.getString("username");
					System.out.println(user_name);
					if(user_name.equals("exist")){
		            	  final AlertDialog alert = new AlertDialog.Builder(LoginActivity.this).create();
		            	  alert.setTitle("Warning");
		            	  alert.setMessage("User already exist");
		            	  alert.setButton("Close", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								 usernameTxt.setText("");
								 phoneTxt.setText("");
								 alert.dismiss();
							}
						});
		            	  alert.show();
		            	 
					}else{
						finish();
						
						int userid = response.getInt("userid");
						
						//---------save to preference-----------
						prefs.putString("username", username);
						prefs.putString("phone", phone);
						prefs.commit();
						
						Intent intent2 = new Intent(LoginActivity.this,MyService.class);
						intent2.putExtra("userid", userid);
						startService(intent2);
						
						Intent intent = new Intent(LoginActivity.this,OnlineListActivity.class);
						intent.putExtra("myname",user_name);
						intent.putExtra("userid",String.valueOf(userid));
			            startActivity(intent);
			        }
				
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		});
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
