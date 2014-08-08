package com.color.speechbubble;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MySimpleArrayAdapter extends ArrayAdapter<String>{

	private final Context context;
	private List<String> values = new ArrayList<String>();
	private List<String> status = new ArrayList<String>();
	private List<String> picture = new ArrayList<String>();
	private List<String> userId = new ArrayList<String>();
	private HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	 
	public MySimpleArrayAdapter(Context context,List<String> values,List<String> status, List<String> picture,List<String> userId) {
		super(context, R.layout.customlist,values);
		this.context = context;
		this.values = values;
		this.status = status;
		this.picture = picture;
		this.userId = userId;	
	}

	@Override
    public long getItemId(int position) {
      String item = getItem(position);
      return mIdMap.get(item);
    }
	
	public View getView(int position, View convertView, ViewGroup parent){
		for (int i = 0; i < values.size(); ++i) {
	        mIdMap.put(values.get(i), Integer.parseInt(userId.get(i)));
	    }
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.customlist, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		TextView statusView = (TextView) rowView.findViewById(R.id.status);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		textView.setText(values.get(position));
		
		if (status.get(position).equals("1")){
//			rowView.setBackgroundColor(Color.GRAY);
			statusView.setText("Online");
		}else{
			statusView.setText("Offline");
		}
		if(imageView != null){
			new ImageDownloaderTask(imageView).execute("http://192.168.173.1/android_message_server/images/"+picture.get(position));
		}
		return rowView;
	}
	static class ImageDownloaderTask extends AsyncTask<String,Void,Bitmap>{
		private final WeakReference imageViewRef;
		public ImageDownloaderTask(ImageView imageView){
			imageViewRef = new WeakReference(imageView);
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			DefaultHttpClient client = new DefaultHttpClient();
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
				return null;
			
		}
		@Override
		protected void onPostExecute(Bitmap bitmap){
			ImageView imageView = (ImageView) imageViewRef.get();
			if(imageViewRef != null){
				if(bitmap!=null){
					imageView.setImageBitmap(bitmap);
				}else{
					imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.ic_launcher));
				}
					
			}
		}
	}
}
