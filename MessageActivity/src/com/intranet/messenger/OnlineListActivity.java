package com.color.speechbubble;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

public class OnlineListActivity extends ListActivity {
	
	private List<String> online_users = new ArrayList<String>();
	private List<String> status = new ArrayList<String>();
	private List<String> picture = new ArrayList<String>();
	private List<String> onlineUserId = new ArrayList<String>();
	private List<NameValuePair> value = new ArrayList<NameValuePair>(2); 

	private MySimpleArrayAdapter adapter;
	private ProgressDialog progress;
	private String myname,userid,serverURL;
	
	private SharedPreferences settings;
	
	private SwipeRefreshLayout swipeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
//		handleIntent(getIntent());
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				Intent intent = getIntent();
				startActivity(intent);
				finish();

			}
		});
//		swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
//                android.R.color.holo_green_light, 
//                android.R.color.holo_orange_light, 
//                android.R.color.holo_red_light);
		
		Intent intent = getIntent();
		myname = intent.getStringExtra("myname");
		userid = intent.getStringExtra("userid");
		
		//--------load preferences
		PreferenceManager.setDefaultValues(this, "ip_address",Context.MODE_PRIVATE,R.xml.preferences, false);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		serverURL = settings.getString("ip_address", "");
		
	    adapter = new MySimpleArrayAdapter(this,online_users,status,picture,onlineUserId);
	    setListAdapter(adapter); 
	    getListView().setTextFilterEnabled(true);
	    
	    new GetOnlineUserSyncTask().execute();
	    
	    
		
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id){
		String item = (String) getListAdapter().getItem(position);
		long friendid = getListAdapter().getItemId(position); 

		Intent intent = new Intent(OnlineListActivity.this,MessageActivity.class);
		intent.putExtra("username", item);
		intent.putExtra("friendid", String.valueOf(friendid));
		intent.putExtra("userid", userid);
		intent.putExtra("myname", myname);
		startActivity(intent);
	}
	
	
	private class GetOnlineUserSyncTask extends AsyncTask<Void, Void, JSONArray>{

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			progress = new ProgressDialog(OnlineListActivity.this);
			progress.setMessage("Loading online users...");
			progress.setIndeterminate(false);
            progress.setCancelable(true);
            progress.show();
		}
		@Override
		protected JSONArray doInBackground(Void... params) {
			
			value.add(new BasicNameValuePair("username", null));
			JSONArray jArray = Server.makeHttpRequest2("http://"+serverURL+"/android_message_server/get_users.php?except="+userid, "GET", value);

			return jArray;
		}
		
		@Override
		protected void onPostExecute(JSONArray jArray){
			try {
				
				for(int i=0; i<jArray.length();i++){
					JSONObject data = jArray.getJSONObject(i);
					online_users.add(data.getString("username"));
					status.add(data.getString("status_online"));
					picture.add(data.getString("picture"));
					onlineUserId.add(data.getString("id"));
//					getListView().setSelector(online_users.size()-1);
				}
				adapter.notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			progress.dismiss();
			
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		}
	@Override
	public void onDestroy(){
		super.onDestroy();
		stopService(new Intent(OnlineListActivity.this,MyService.class));
		List<NameValuePair> value = new ArrayList<NameValuePair>(2); 
		value.add(new BasicNameValuePair("username", null));
		Server.makeHttpRequest("http://"+serverURL+"/android_message_server/onlinestatus.php?userid="+userid+"&status="+0, "GET", value);	
	
	}
	
	public void refresh_activity(View v){
		Intent intent = getIntent();
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting, menu);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);   

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() 
        {
            @Override
            public boolean onQueryTextChange(String newText) 
            {
                // this is your adapter that will be filtered
                adapter.getFilter().filter(newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) 
            {
                // this is your adapter that will be filtered
                adapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        
		return super.onCreateOptionsMenu(menu);
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
	
	/*public void onNewIntent(Intent intent){
		setIntent(intent);
		handleIntent(intent);
	}
	public void handleIntent(Intent intent){
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}
	}*/
	
}





