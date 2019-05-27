/**
 * EchoprintTestActivity.java
 * EchoprintTest
 * 
 * Created by Alex Restrepo on 1/22/12.
 * Copyright (C) 2012 Grand Valley State University (http://masl.cis.gvsu.edu/)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.gvsu.masl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Hashtable;

import edu.gvsu.masl.echoprint.AudioFingerprinter;
import edu.gvsu.masl.echoprint.AudioFingerprinter.AudioFingerprinterListener;

/**
 * EchoprintTestActivity<br>
 * This class demos how to use the AudioFingerprinter class
 * 
 * @author Alex Restrepo (MASL)
 *
 */
public class EchoprintTestActivity extends Activity implements AudioFingerprinterListener 
{	
	boolean recording, resolved;
	public static boolean isWatching = false;
	public static boolean isRecording = false;
	public static int intDelayMillis = 200;

	private static final String TAG = EchoprintTestActivity.class.getSimpleName();

	private static AudioFingerprinter fingerprinter;
	TextView status;
	ImageButton btn;

	/*
	@uthor: Mehul
	 */
	ImageView btn_list,btn_printed;
	static int view=1;
//    ArrayList<Song> songs,song_paths;
//	ListView song_list_view;
//	ArrayAdapter<Song> song_list_adapter;
    //private Context private_context;
    //MyCustomAdapter dataAdapter = null;
	
    @Override       
    public void onCreate(Bundle savedInstanceState)
    {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		if(fingerprinter == null)
			fingerprinter = new AudioFingerprinter(EchoprintTestActivity.this);//


		btn = (ImageButton) findViewById(R.id.recordButton);
        
        status = (TextView) findViewById(R.id.status);
        btn.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
                // Perform action on click
				if(!isWatching) {
					isWatching = true;
					watching();
				} else {
					isWatching = false;
				}

            	/*if(recording) {
					fingerprinter.stop();

				}
            	else
            	{
					if(fingerprinter == null)
            			fingerprinter = new AudioFingerprinter(EchoprintTestActivity.this);

		btn_list=(ImageView)findVi
            		fingerprinter.fingerprint(15);
            	}*/
            }
        });


		/*
			@uthor: Mehul
		 */
		btn_list=(ImageView)findViewById(R.id.buttonAdd);
		btn_list.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//setContentView(R.layout.list_view);
                //view=2;
                Intent intent = new Intent(EchoprintTestActivity.this, EchoprintSongList.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
				//status.append("clicked");
			}
		});

		btn_printed=(ImageView)findViewById(R.id.buttonSearch);
		btn_printed.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//setContentView(R.layout.list_view);
				//view=2;
				Intent intent = new Intent(EchoprintTestActivity.this, EchoprintFingerprintedList.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				//status.append("clicked");
			}
		});
    }

	private static class MyHandler extends Handler {}
	private final MyHandler mHandler = new MyHandler();

	public static class MyRunnable implements Runnable {
		private final WeakReference<Activity> mActivity;

		public MyRunnable(Activity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void run() {

			Activity activity = mActivity.get();

			Calendar rightNow = Calendar.getInstance();
			if (activity != null) {
				if(isRecording) {
					fingerprinter.stop();
					Log.v(TAG, rightNow.getTimeInMillis() + " stopRecording");
					isRecording = false;
					intDelayMillis = 2000;
				} else {

					//if(fingerprinter == null)
					//	fingerprinter = new AudioFingerprinter(activity);//EchoprintTestActivity.this

					fingerprinter.fingerprint(15);
					Log.v(TAG,  rightNow.getTimeInMillis() + " startRecording");
					isRecording = true;
					intDelayMillis = 20000;
				}

				if(isWatching) {
					MyHandler mHandler = new MyHandler();
					mHandler.postDelayed(this, intDelayMillis);
				} else {
					fingerprinter.stop();
					Log.v(TAG,  rightNow.getTimeInMillis() + " stopRecording");
				}
			}
		}
	}

	private void watching() {
		mHandler.postDelayed(mRunnable, intDelayMillis);
	}

	private MyRunnable mRunnable = new MyRunnable(this);

	public void didFinishListening() 
	{					
		//btn.setText("Start");
		//recordMessage.setText("Start");
		
		if(!resolved)
			status.setText("Idle...");
		
		recording = false;
	}
	
	public void didFinishListeningPass()
	{}

	public void willStartListening() 
	{
		status.setText("Listening...");
		//btn.setText("Stop");
		recording = true;
		resolved = false;
	}

	public void willStartListeningPass() 
	{}

	public void didGenerateFingerprintCode(String code) 
	{
		status.setText("Will fetch info for code starting:\n" + code.substring(0, Math.min(50, code.length())));
	}

	public void didFindMatchForCode(final Hashtable<String, String> table,
			String code) 
	{
		resolved = true;
		status.setText("Match: \n" + table);
	}

	public void didNotFindMatchForCode(String code) 
	{
		resolved = true;
		status.setText("No match for code starting with: \n" + code.substring(0, Math.min(50, code.length())));
	}

	public void didFailWithException(Exception e) 
	{
		resolved = true;
		status.setText("Error: " + e);
	}

}