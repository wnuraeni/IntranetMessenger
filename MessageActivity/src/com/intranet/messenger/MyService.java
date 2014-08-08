package com.color.speechbubble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class MyService extends Service{
	private String userid,serverURL;
	private MessageDataSource dataSrc;
	private List<NameValuePair> value = new ArrayList<NameValuePair>(2); 
	
	private SharedPreferences settings;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate(){
		dataSrc = new MessageDataSource(MyService.this);
		dataSrc.open();
		
		PreferenceManager.setDefaultValues(this, "ip_address",Context.MODE_PRIVATE,R.xml.preferences, false);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		serverURL = settings.getString("ip_address", "");
		
		new Timer().schedule(new MyTimerTask(),0,10000);
	}
	
	@Override
	public void onStart(Intent intent, int startId){
        this.userid = intent.getStringExtra("userid");		
	}
	@Override
	public void onDestroy(){
	}

	public class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			new DatabaseSyncTask().execute();
		}
	}
private class DatabaseSyncTask extends AsyncTask<Void, Void, JSONArray>{
		
		@Override
		protected JSONArray doInBackground(Void... params) {
			/*String response="";
			//pake json parser untuk ambil data dari database
			
			DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://192.168.173.1/android_message_server/get_message.php?receiver="+userid);
            try {
              HttpResponse execute = client.execute(httpGet);
              
              InputStream content = execute.getEntity().getContent();
              BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
              String s = "";
              while ((s = buffer.readLine()) != null) {
                response += s;
              }
            }catch(Exception e){
            	  e.printStackTrace();
            }
           
			return response;*/
			
			
			value.add(new BasicNameValuePair("username", null));
			
			JSONArray jArray = Server.makeHttpRequest2("http://"+serverURL+"/android_message_server/get_message.php?receiver="+userid, "GET", value);
							
			return jArray;
		}
		
		@Override
		protected void onPostExecute(JSONArray jArray){
			//ubah data response dari server ke jsonobject
			if(jArray.length()<1){
				
			}else{
			
				try {
					
					Message message = new Message();
					
					System.out.println("*****JARRAY*****"+jArray.length());
					for(int i=0;i<jArray.length();i++){
	
	
					 JSONObject json_data = jArray.getJSONObject(i);
					 Log.i("log_tag","_id"+json_data.getInt("id_message")+
					  "sender, "+json_data.getString("sender")+
					  "receiver, "+json_data.getString("receiver")+
					  "datetime, "+json_data.getString("date_time")+
					  "message, "+json_data.getString("message")+
					  "status, "+json_data.getString("status")
					 );
						
						message = dataSrc.addMessage(json_data.getString("sender"), 
								json_data.getString("receiver"), 
								json_data.getString("date_time"), 
								json_data.getString("message"), 1);
						updateMessage(json_data.getInt("id_message"));
						
						//notify
						NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				        String MyText = "New Message!";
				        Notification mNotification = new Notification(R.drawable.logo, MyText, System.currentTimeMillis() );
				        
				        //The three parameters are: 1. an icon, 2. a title, 3. time when the notification appears
				        String MyNotificationTitle = "Intranet Messenger";
				        String MyNotificationText  = "Message from "+json_data.getString("sendername");
				        
				        Intent intent = new Intent(MyService.this,MessageActivity.class);
						intent.putExtra("friendid", json_data.getString("sender"));
						intent.putExtra("userid", json_data.getString("receiver"));
						
				        Intent MyIntent = new Intent(intent);
				        PendingIntent StartIntent = PendingIntent.getActivity(getApplicationContext(),0,MyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				        mNotification.defaults = Notification.DEFAULT_SOUND;

				        //A PendingIntent will be fired when the notification is clicked. The FLAG_CANCEL_CURRENT flag cancels the pendingintent
				        mNotification.setLatestEventInfo(getApplicationContext(), MyNotificationTitle, MyNotificationText, StartIntent);
				        int NOTIFICATION_ID = 1;
				        notificationManager.notify(NOTIFICATION_ID , mNotification);  
				       
				        MessageActivity.messages.add(message);
				        MessageActivity.adapter.notifyDataSetChanged();
//				        new MessageActivity().getListView().setSelection(MessageActivity.messages.size() - 1);
				        
					}
		
				} catch (JSONException e) {
					e.printStackTrace();
				} 
			}
		}
	}

	public void updateMessage(int id){
		List<NameValuePair> value = new ArrayList<NameValuePair>(2);
		Server.makeHttpRequest("http://"+serverURL+"/android_message_server/update_message.php?msg_id="+id, "GET", value);
	 
	}
}
