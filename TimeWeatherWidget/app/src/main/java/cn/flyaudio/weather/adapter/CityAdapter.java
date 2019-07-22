package cn.flyaudio.weather.adapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.flyaudio.Weather.WeatherWidget;
import cn.flyaudio.weather.activity.WeatherDetailsActivity;
import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.service.WeatherService;
import cn.flyaudio.weather.util.Constant;
import cn.flyaudio.weather.util.TimeWeatherUtilsTools;
//import cn.flyaudio.weather.util.UtilsTools;
import cn.flyaudio.weather.view.SkinResource;

/**
 * @author:zengyuke
 * @company:flyaudio
 * @version:1.0
 * @createdDate:2014-5-5下午3:16:08
 */
public class CityAdapter extends BaseAdapter {

	private final String TAG = Constant.TAG;
	private final Boolean DEBUG_FLAG = Constant.DEBUG_FLAG;
	private Context mContext;
	private List<String> mCityList= new ArrayList<String>();;
	private Activity mActivity;
	String supportDayNight = SkinResource.getSkinStringByName("skin_support_day_night_mode");
	private SharedPreferences preference;
//	private DumpActivityReceiver mDumpActivityReceiver;

	public CityAdapter(Activity a, Context context, List<String> citys) {
		mActivity = a;
		mContext = context;
//		mCityList = citys;
		preference = context.getSharedPreferences("weather", 0);
		for (int i = 1; i <= 4; i++) {
			String city = preference.getString(String.valueOf(i), null);
			if (city != null)
				mCityList.add(city);
		}

	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 4;// WeatherDetails.view_count-1
	}

	@Override
	public int getCount() {
		return mCityList.size();
	}

	@Override
	public Object getItem(int position) {
		 return mCityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setCityList(List<String> citys) {
		mCityList = citys;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Button deleteBtn;
		RelativeLayout relativeLayout;
		Log.e("xifei","mCityList---------"+mCityList.size());

		/*mDumpActivityReceiver = new DumpActivityReceiver();
		IntentFilter iFilter2 = new IntentFilter(Constant.ACTION_UPDATE_DATA);
		mContext.registerReceiver(mDumpActivityReceiver, iFilter2);*/

		if (convertView == null) {

			/*if (!TextUtils.isEmpty(supportDayNight) && "support".equals(supportDayNight)
					&& Constant.CURREN_DAYNIGHT_MODE.equals(SystemProperties.get("fly.android.navi.daynightmode", "day"))) {
				convertView = LayoutInflater.from(parent.getContext())
						.inflate(SkinResource.getSkinLayoutIdByName("weather_city_item"), null);
				Log.e("xifei","222222夜");
			}else {
//				Log.e("xifei","222222日(1)");
				convertView = LayoutInflater.from(parent.getContext())
						.inflate(SkinResource.getSkinLayoutIdByName("weather_city_item"), null);
//				Log.e("xifei","222222日(2)---"+convertView);
			}*/
			convertView = LayoutInflater.from(parent.getContext())
					.inflate(SkinResource.getSkinLayoutIdByName("weather_city_item"), null);
			final String currentCity = mCityList.get(position);


			//删除按钮
			deleteBtn = (Button) convertView.findViewById(SkinResource.getSkinResourceId("weather_time_delete", "id"));
			relativeLayout = (RelativeLayout) convertView.findViewById(SkinResource.getSkinResourceId("weather_time_relativelayout", "id"));


			if (WeatherWidgetApplication.isCNLanguage) {
				SharedPreferences shared = mContext.getSharedPreferences(
						"weather", 0);
				if (shared.getString(
						String.valueOf(shared.getInt("current", -1)), "") == currentCity) {

					relativeLayout.setBackgroundResource(SkinResource.getSkinDrawableIdByName("weather_select_o"));
				}
			} else {
				SharedPreferences shared = mContext.getSharedPreferences(
						"weather", 0);
				if (shared.getString(
						String.valueOf(shared.getInt("current", -1)), "") == currentCity) {

					relativeLayout.setBackgroundResource(SkinResource.getSkinDrawableIdByName("weather_select_o"));
				}
				/*if (shared.getString(
						String.valueOf(shared.getInt("current", -1) * 10), "") == currentCity) {
					relativeLayout.setBackgroundResource(SkinResource.getSkinDrawableIdByName("weather_select_o"));
				}*/
			}


			relativeLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					SharedPreferences shared = mContext.getSharedPreferences(
							"weather", 0);

					SharedPreferences.Editor edit = shared.edit();
					int current = 1;
					for (int i = 1; i < 5; i++) {

						if (WeatherWidgetApplication.isCNLanguage) {
							if (shared.getString(String.valueOf(i), "").equals(
									currentCity))
								current = i;
						} else {

							if (shared.getString(String.valueOf(i), "").equals(
									currentCity))
								current = i;
							/*if (shared.getString(String.valueOf(i * 10), "")
									.equals(currentCity))
								current = i;*/
						}
					}
					Log.d(TAG, "[CityAdapter] updateWeatherShared()  add current  == "
							+ current);
					FullWeatherInfo mFullWeatherInfo = TimeWeatherUtilsTools
							.getWeatherInfoFormSharedPreferences(mContext,
									mContext.getSharedPreferences(
											WeatherWidget
													.getCurrentWeather(
															mContext,
															current), 0), current);
					TimeWeatherUtilsTools.sendBoardcast2SystemUI(mContext,
							mFullWeatherInfo, currentCity, shared.getString(String.valueOf(current * 10), ""));
					edit.putInt("current", current);
					edit.commit();

					Intent intent = new Intent(Constant.ACTION_APPWIDGET_UPDATE);
					intent.putExtra("current", current);
					for (int i = 0; i < 4; i++) {
						WeatherService.backup = false;
						if (DEBUG_FLAG)
							Log.d(TAG,
									"[CityAdapter] itemViews.select.setOnClick()  backup"
											+ (i + 1) + " == "
											+ WeatherService.backup);
					}
					mContext.sendBroadcast(intent);
					Intent mIntentHome = new Intent(mContext,
							WeatherDetailsActivity.class);
					mContext.startActivity(mIntentHome);
					mActivity.finish();

				}
			});
			deleteBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("zzz", "itemViews.delete");

					if (getCityCount() > 1) {
						updateWeatherShared(currentCity);
						mContext.sendBroadcast(new Intent(
								Constant.ACTION_UPDATE_ADAPTER));
					}
				}
			});

			TimeWeatherUtilsTools.setConvertViewWithShared(mContext, convertView, mCityList.get(position));
		}
		
		return convertView;
	}

/*	class DumpActivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {


			if (arg1.getExtras().equals("aaa")) {
				Log.d("mForecastyweathers1", "UpdateUIReceiver");

			} else {
				//延时的目的是从服务器请求回来的数据在界面刷新之前
				Log.d("mForecastyweathers2", "UpdateUIReceiver");
				new Thread() {
					public void run() {
						try {
							sleep(2000);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}


		}

	}*/


	protected void updateWeatherShared(String currentcity) {

		if (DEBUG_FLAG)
			Log.d(TAG, "[CityAdapter] updateWeatherShared()  delete city  == "
					+ currentcity);
		SharedPreferences shared = mContext.getSharedPreferences("weather", 0);
		SharedPreferences.Editor edit = shared.edit();
		int index = 1;

		// for get delete index
		for (int i = 1; i <= 4; i++) {

			/*String city = WeatherWidgetApplication.isCNLanguage ? shared
					.getString(String.valueOf(i), null) : shared.getString(
					String.valueOf(i * 10), null);*/
			String city = WeatherWidgetApplication.isCNLanguage ? shared
					.getString(String.valueOf(i), null) : shared.getString(
					String.valueOf(i), null);

			if (city != null && city.equals(currentcity)) {
				index = i;
			}
		}

		// remove info city
		removeCity(edit, String.valueOf(index));
		removeCity(edit, String.valueOf(index * 10));
		removeCity(edit, "city" + index);
		clearSharedWithIndex(index);

		int current = shared.getInt("current", 1);

		if (current == index) {
			for (int i = 0; i < 4; i++) {
				String city = shared.getString(String.valueOf(i + 1), null);
				if (city != null) {
					current = i + 1;
					break;
				}
			}
			Log.d(TAG, "[CityAdapter] updateWeatherShared()  delete current  == "
					+ current);
			FullWeatherInfo mFullWeatherInfo = TimeWeatherUtilsTools
					.getWeatherInfoFormSharedPreferences(mContext,
							mContext.getSharedPreferences(
									WeatherWidget
											.getCurrentWeather(
													mContext,
													current), 0), current);
			TimeWeatherUtilsTools.sendBoardcast2SystemUI(mContext,
					mFullWeatherInfo, shared.getString(String.valueOf(current), ""), shared.getString(String.valueOf(current * 10), ""));
			edit.putInt("current", current);
			edit.commit();
			mContext.sendBroadcast(new Intent(Constant.ACTION_APPWIDGET_UPDATE));
		}
	}

	private void removeCity(SharedPreferences.Editor edit, String key) {
		edit.remove(key);
		edit.commit();
	}

	public int getCityCount() {
		SharedPreferences shared = mContext.getSharedPreferences("weather", 0);
		int count = 0;
		for (int i = 0; i < 4; i++) {
			String city = shared.getString(String.valueOf(i + 1), null);
			if (city != null)
				count++;
		}
		Log.d("zzz", "count" + count);
		return count;
	}

	protected void clearSharedWithIndex(int index) {
		if (DEBUG_FLAG)
			Log.d(TAG, "[CityAdapter] clearSharedWithIndex()  index  == "
					+ index);
		switch (index) {
		case 1:
			ClearShread(mContext.getSharedPreferences("FirstWeather", 0).edit());
			break;
		case 2:
			ClearShread(mContext.getSharedPreferences("SecondWeather", 0).edit());
			break;
		case 3:
			ClearShread(mContext.getSharedPreferences("ThirdWeather", 0).edit());
			break;
		case 4:
			ClearShread(mContext.getSharedPreferences("FouthWeather", 0).edit());
			break;
		default:
			break;
		}
	}

	public void ClearShread(SharedPreferences.Editor edit) {
		edit.clear();
		edit.commit();
	}


}
