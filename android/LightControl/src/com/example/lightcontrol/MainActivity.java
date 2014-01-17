package com.example.lightcontrol;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;

public class MainActivity extends Activity  implements SeekBar.OnSeekBarChangeListener {

    private SeekBar light1;
    private SeekBar light2;
    private SeekBar light3;
    private SeekBar light4;
    protected int lights[] = new int[5];

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        light1 = (SeekBar) findViewById(R.id.seekBar1);
    	light1.setOnSeekBarChangeListener(this);

        light2 = (SeekBar) findViewById(R.id.seekBar2);
    	light2.setOnSeekBarChangeListener(this);
    	
        light3 = (SeekBar) findViewById(R.id.seekBar3);
    	light3.setOnSeekBarChangeListener(this);

    	light4 = (SeekBar) findViewById(R.id.seekBar4);
    	light4.setOnSeekBarChangeListener(this);
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        //  Notify that the progress level has changed.
    	long val = 255 - progress;
    	val = Math.round(((Math.log(255) - Math.log(val)) / Math.log(255)) * 255);
    	if (val > 255) val = 255;
		Log.w("prog / val", progress + " " + val);

    	if (seekBar == light1)
    		this.lights[1] = (int) val;

    	if (seekBar == light2)
    		this.lights[2] = (int) val;

    	if (seekBar == light3)
    		this.lights[3] = (int) val;

    	if (seekBar == light4)
    		this.lights[4] = (int) val;
    }
    
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // placeholder
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    	new UpdateSettingsTask().execute(
    			Integer.toString(this.lights[1]),
    			Integer.toString(this.lights[2]),
    			Integer.toString(this.lights[3]),
    			Integer.toString(this.lights[4])
    	);
    }    
    
}


class UpdateSettingsTask extends AsyncTask<String, Void, Void> {

//    private Exception exception;

	@Override
	protected Void doInBackground(String... params) {
		Log.w("before", params[0]);


	    URL url;
	    HttpURLConnection connection = null;  
	    try {
	      url = new URL("http://192.168.1.33/light");
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", 
	           "application/x-www-form-urlencoded");

	      connection.setUseCaches (false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);

	      //Send request
	      DataOutputStream wr = new DataOutputStream (
	                  connection.getOutputStream ());
	      wr.writeBytes ("light_1=" + params[0] + "&light_2=" + params[1] + "&light_3=" + params[2] + "&light_4=" + params[3]);
	      wr.flush ();
	      wr.close ();

	      //Get Response	
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append('\r');
	      }
	      rd.close();
	      Log.w("myApp", "response: " + response.toString());
	    } catch (Exception e) {

	      e.printStackTrace();
	      return null;

	    } finally {

	      if(connection != null) {
	        connection.disconnect(); 
	      }
	    }
		return null;
	}

}