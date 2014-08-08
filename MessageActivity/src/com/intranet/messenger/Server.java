package com.color.speechbubble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Server {

	public static int isConnected(String server){
		
			int response = 0;
			
			URL url = null;
			try {
				url = new URL(server);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
				
				HttpURLConnection urlConnection = null;
				try {
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestProperty("User-Agent", "Android");
					urlConnection.setConnectTimeout(2*1000);
					urlConnection.connect();
					response = urlConnection.getResponseCode();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				return response;	
	}
	
	public static JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params){
	
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse execute = null;
		
		if(method == "POST"){
			HttpPost httpPost = new HttpPost(url);
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				execute = client.execute(httpPost);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(method == "GET"){
			HttpGet httpGet = new HttpGet(url);
			try {
				execute = client.execute(httpGet);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		InputStream content = null;
		String response="",s="";
		JSONObject jsondata = null;
		try {
			content = execute.getEntity().getContent();
			
			BufferedReader buffer = new BufferedReader(new InputStreamReader(content));

			while((s=buffer.readLine())!= null){
				response += s;
			}
			buffer.close();
			
			System.out.println(response);

			jsondata = new JSONObject(response);
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsondata;
	}
	
	public static JSONArray makeHttpRequest2(String url, String method, List<NameValuePair> params){
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse execute = null;
		
		if(method == "POST"){
			HttpPost httpPost = new HttpPost(url);
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				execute = client.execute(httpPost);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(method == "GET"){
			HttpGet httpGet = new HttpGet(url);
			try {
				execute = client.execute(httpGet);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		InputStream content = null;
		String response="",s="";
		JSONArray jsonarray = null;
		try {
			content = execute.getEntity().getContent();
			
			BufferedReader buffer = new BufferedReader(new InputStreamReader(content));

			while((s=buffer.readLine())!= null){
				response += s;
			}
			buffer.close();
			
			jsonarray = new JSONArray(response);
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonarray;
	}	
	
	public static String uploadImagetoServer(String url,ArrayList<NameValuePair> value){
	
		InputStream content = null;
 		String response="",s="";
 		JSONObject jsondata = null;
 		
		 HttpClient httpclient = new DefaultHttpClient();
         HttpPost httppost = new HttpPost("http://192.168.173.1/android_message_server/upload_image.php");
        
         try {
				httppost.setEntity(new UrlEncodedFormEntity(value));
				HttpResponse result = httpclient.execute(httppost);
				
				content = result.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));

	 			while((s=buffer.readLine())!= null){
	 				response += s;
	 			}
	 			buffer.close();
	 			
	 			jsondata = new JSONObject(response);
	 			s = jsondata.getString("response");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
  
		return s;
	}
	
	public static Bitmap getImageFromServer(String url){
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url); //url
		HttpResponse response = null;
		
		try {
			response = client.execute(getRequest);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpEntity entity = response.getEntity();
		
		if(entity != null){
			InputStream inputStream = null;
			try {
				inputStream = entity.getContent();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final Bitmap bmp = BitmapFactory.decodeStream(inputStream);
			return bmp;
		}
		else
			return null;
	}
}
