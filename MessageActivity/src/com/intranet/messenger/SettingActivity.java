package com.color.speechbubble;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SettingActivity extends Activity {

	private Button mynameBtn;
	private EditText mynameTxt;
	private ImageView imageView;
	
	private static int GALLERY=1,CAMERA = 2 ,CROP = 3;
	private String filename,myname,userid,serverURL;
	private Uri imageUri;
	private Bitmap bitmap;
	private MessageDataSource datasrc;
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		Intent intent = getIntent();
		
		//--------load preferences
		PreferenceManager.setDefaultValues(this, "ip_address",Context.MODE_PRIVATE,R.xml.preferences, false);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		serverURL = settings.getString("ip_address", "");
				
		datasrc = new MessageDataSource(this);
		datasrc.open();
		
		myname = intent.getStringExtra("myname");
		userid = intent.getStringExtra("userid");
		imageView = (ImageView) findViewById(R.id.profilePicture);
		mynameBtn = (Button) findViewById(R.id.mynameBtn);
		mynameTxt = (EditText) findViewById(R.id.myname);
		mynameTxt.setText(myname);
		
		mynameBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new UpdateUsername().execute();
			}
		});
		
		//get user profile picture
		//if empty set to default pic if not download from url
		new ImageDownloaderTask(imageView).execute("http://"+serverURL+"/android_message_server/images/"+userid+".jpg");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	public void changePicture(View view){
		
		String[] items = {"Gallery","Camera"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose Image From");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case 0:
					//gallery
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					startActivityForResult(intent,GALLERY);
					break;
				case 1:
					//camera
					Intent intent2 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent2,CAMERA);
					break;
					
				}
			}
		});
		builder.show();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		
		InputStream stream = null;
		if(resultCode == RESULT_CANCELED)
		{
			finish();
		}
		if(requestCode ==  GALLERY){
			imageUri = intent.getData();
			try {
				if(bitmap != null){
					bitmap.recycle();
				}
				stream = getContentResolver().openInputStream(intent.getData());
				bitmap = BitmapFactory.decodeStream(stream);
				imageView.setImageBitmap(bitmap);
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			cropIntent.setDataAndType(imageUri, "image/*");
			cropIntent.putExtra("crop", true);
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY",1);
			cropIntent.putExtra("outputX",256);
			cropIntent.putExtra("outputY",256);
			cropIntent.putExtra("return-data",true);
			startActivityForResult(cropIntent,CROP);
			

		}
		else if(requestCode == CAMERA){
			Bundle extras = intent.getExtras();
			bitmap = (Bitmap) extras.get("data");
			imageView.setImageBitmap(bitmap);
			try{
				//set picture file name
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy-hh-mm-ss");
				String date = dateFormat.format(new Date());
				filename = Environment.getExternalStorageDirectory()+File.separator+"IMG_"+date+".png";
				imageUri = Uri.fromFile(new File(filename));
				//save picture to defined location
				FileOutputStream out = new FileOutputStream(filename);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				//notify mount sd card - force show on gallery
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ filename)));
				out.flush();
				out.close();
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		else if (requestCode == CROP){
			Bundle extras = intent.getExtras();
			 if (extras != null) {
				//set image to layout 
				Bitmap photo = (Bitmap) extras.get("data");
            	imageView.setImageBitmap(photo);
            	bitmap = photo;
    			Toast.makeText(SettingActivity.this, "start uploading picture", Toast.LENGTH_LONG).show();
    			new SendPicture().execute();
			 }
		}
	}
	
	class SendPicture extends AsyncTask<Void, Void, String>{

		ProgressDialog progress;
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			progress = ProgressDialog.show(SettingActivity.this, "Loading", "Uploading file to server....");
			progress.show();
		}
		@Override
		protected String doInBackground(Void... params) {         
			/*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr,1);
            ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
 
            nameValuePairs.add(new BasicNameValuePair("picture",image_str));
            nameValuePairs.add(new BasicNameValuePair("userid",userid));
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.173.1/android_message_server/upload_image.php");
            try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
            
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr,1);
            ArrayList<NameValuePair> value = new  ArrayList<NameValuePair>();
 
            value.add(new BasicNameValuePair("picture",image_str));
            value.add(new BasicNameValuePair("userid",userid));
 
            String response = Server.uploadImagetoServer("http://"+serverURL+"/android_message_server/upload_image.php",value);
            	
			return response;
		}
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			if(result.equals("true")){
				progress.dismiss();
			}
			else{
				progress.dismiss();
				Toast.makeText(SettingActivity.this, "failed upload picture", Toast.LENGTH_LONG).show();
			}
					
		}
		
	}
	class UpdateUsername extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {         
			ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username",mynameTxt.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("userid",userid));
            
            Server.makeHttpRequest("http://"+serverURL+"/android_message_server/update_name.php", "POST", nameValuePairs);
            
            /*HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.173.1/android_message_server/update_name.php");
            try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       */
			return "success";
		}
		@Override
		protected void onPostExecute(String result){
			datasrc.updateUserData(mynameTxt.getText().toString(), Long.parseLong(userid));
					
		}
	}
	static class ImageDownloaderTask extends AsyncTask<String,Void,Bitmap>{
		private final WeakReference imageViewRef;
		public ImageDownloaderTask(ImageView imageView){
			imageViewRef = new WeakReference(imageView);
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bmp = Server.getImageFromServer(params[0]);
			
			/*DefaultHttpClient client = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(params[0]); //url
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
				return null;*/
			return bmp;
		}
		@Override
		protected void onPostExecute(Bitmap bitmap){
			ImageView imageView = (ImageView) imageViewRef.get();
			if(imageViewRef != null){
				if(bitmap!=null){
					imageView.setImageBitmap(bitmap);
				}else{
					imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.nopic));
				}
					
			}
		}
	}
}
