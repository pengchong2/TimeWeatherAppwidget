package cn.flyaudio.time;

import android.app.Activity;
import android.app.Application;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.service.WeatherService;
import cn.flyaudio.weather.view.SkinResource;

public class AlarmDetails extends Activity {

	TextView alarmText = null;
	Button checkbox = null;
	Button backBtn = null;
	Button timeset = null;
	Button gps =null;
	private SharedPreferences preference = null;
	public Application application;
	String useGPS = "no";
	View v;
	UiModeManager mUiModeManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
		application = ((WeatherWidgetApplication) WeatherWidgetApplication.application);
		LayoutInflater li = LayoutInflater.from(((WeatherWidgetApplication) application).getShareContext());
	/*	if (DayAndNightModeUtil.isNightMode()) {
			v = li.inflate(((WeatherWidgetApplication) application).getShareResources().getLayout(((WeatherWidgetApplication) application).getResId("time_alarm_details", "layout")),null);
		} else {
			v = li.inflate(((WeatherWidgetApplication) application).getShareResources().getLayout(((WeatherWidgetApplication) application).getResId("time_alarm_details", "layout")),null);
		}*/
		v = li.inflate(((WeatherWidgetApplication) application).getShareResources()
				.getLayout(((WeatherWidgetApplication) application).getResId("time_alarm_details", "layout")),null);
		setContentView(v);
		if(WeatherService.enableColorTheme)
		{
			Log.i("AAA", "AlarmDetails :"+WeatherService.color);
			v.setBackgroundColor(WeatherService.color);
		}
		initViews();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (WeatherService.enableColorTheme) {
			String c = SystemProperties.get("persist.fly.colortheme","red");
			if(c.equals("red"))
				WeatherService.color = Color.RED;
			try {
				WeatherService.color = Integer.valueOf(c);
			} catch (Exception e) {
				// TODO: handle exception
				WeatherService.color = Color.RED;
			}
			v.setBackgroundColor(WeatherService.color);
			Log.i("AlarmDetails", "onResume enableColorTheme AlarmDetails :"+WeatherService.color);
		}
	}
	
	protected void onDestroy() {
		super.onDestroy();
		alarmText = null;
		checkbox = null;
		backBtn = null;
		timeset = null;
	}
	
	private void initViews(){
		//setContentView(findViewById(((WeatherWidgetApplication) application).getResId("time_alarm_details", "layout")));
		alarmText = (TextView) findViewById(((WeatherWidgetApplication) application).getResId("alarmText", "id"));
		checkbox = (Button) findViewById(((WeatherWidgetApplication) application).getResId("box1", "id"));

		backBtn = (Button) findViewById(((WeatherWidgetApplication) application).getResId("back1", "id"));
		timeset = (Button) findViewById(((WeatherWidgetApplication) application).getResId("box2", "id"));



		preference = getSharedPreferences("time", MODE_PRIVATE);
		if (preference.getString("alarmOrNot", "no").equals("yes")) {
			int id = 0;
			if (mUiModeManager.getNightMode()==2) {
				id = ((WeatherWidgetApplication) application).getResId("time_set_clock_on", "drawable");
			} else {
				id = ((WeatherWidgetApplication) application).getResId("time_set_clock_on", "drawable");
			}
			Log.d("WeatherService", "time_set_clock_on.id-->"+id);
			checkbox.setBackgroundResource(id);
			//checkbox.setVisibility(View.INVISIBLE);
			alarmText.setText(((WeatherWidgetApplication) application).getResId("alarmopen", "string"));
		} else {
			if (mUiModeManager.getNightMode()==2) {
				checkbox.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_off", "drawable"));
			} else {
				checkbox.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_off", "drawable"));
			}
			//checkbox.setVisibility(View.INVISIBLE);
			alarmText.setText(((WeatherWidgetApplication) application).getResId("alarmclose", "string"));
		}
		checkbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (preference.getString("alarmOrNot", "no").equals("yes")) {
					if (mUiModeManager.getNightMode()==2) {
						checkbox.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_off", "drawable"));
					} else {
						checkbox.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_off", "drawable"));
					}
					alarmText.setText(((WeatherWidgetApplication) application).getResId("alarmclose", "string"));
					SharedPreferences.Editor editor = preference.edit();
					editor.putString("alarmOrNot", "no");
					editor.commit();
					Log.d("WeatherService", "close");
					Intent myIntent = new Intent(AlarmDetails.this, WeatherService.class);
					AlarmDetails.this.startService(myIntent);
					sendBroadcast(new Intent("Intent.ACTION_TIME_CHANGED"));
				} else {
					if (mUiModeManager.getNightMode()==2) {
						checkbox.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_on", "drawable"));
					} else {
						checkbox.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_on", "drawable"));
					}
					alarmText.setText(((WeatherWidgetApplication) application).getResId("alarmopen", "string"));
					SharedPreferences.Editor editor = preference.edit();
					editor.putString("alarmOrNot", "yes");
					editor.commit();
					Log.d("WeatherService", "open");
					Intent myIntent = new Intent(AlarmDetails.this, WeatherService.class);
					AlarmDetails.this.startService(myIntent);
					sendBroadcast(new Intent("Intent.ACTION_TIME_CHANGED"));
				}
			}
		});

		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		timeset.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d("tiemset", "settime");
				Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
				startActivity(intent);
			}
		});

//				IntentFilter filter = new IntentFilter("action.flyaudio.colortheme");
//				registerReceiver(mRGBReceiver, filter);

		useGPS = SystemProperties.get("persist.fly.usegps","no");
		gps = (Button) findViewById(((WeatherWidgetApplication) application).getResId("box3", "id"));
//		gps = (Button) v.findViewById(SkinResource.getSkinResourceId("box3", "id"));
		if (useGPS.equals("yes")) {
			int id = 0;
//			if (isNightMode) {
			if (mUiModeManager.getNightMode()==2) {
				id = ((WeatherWidgetApplication) application).getResId("time_set_clock_on", "drawable");
			} else {
				id = ((WeatherWidgetApplication) application).getResId("time_set_clock_on", "drawable");
			}
			gps.setBackgroundResource(id);
		} else {
			if (mUiModeManager.getNightMode()==2) {
				gps.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_off", "drawable"));
			} else {
//				Log.e("AAA","-----"+((WeatherWidgetApplication) application).getResId("time_set_clock_off", "drawable"));
				gps.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_off", "drawable"));
			}
		}
		gps.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (useGPS.equals("yes")) {
					if (mUiModeManager.getNightMode()==2) {
						gps.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_off", "drawable"));
					} else {
						gps.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_off", "drawable"));
					}
					useGPS = "no";
					SystemProperties.set("persist.fly.usegps", "no");
				} else {
					if (mUiModeManager.getNightMode()==2) {
						gps.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_on", "drawable"));
					} else {
						gps.setBackgroundResource(((WeatherWidgetApplication) application).getResId("time_set_clock_on", "drawable"));
					}
					useGPS = "yes";
					SystemProperties.set("persist.fly.usegps", "yes");
				}
			}
		});
	}
}
