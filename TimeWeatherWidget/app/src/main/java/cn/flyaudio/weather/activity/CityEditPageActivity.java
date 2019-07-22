package cn.flyaudio.weather.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.flyaudio.Weather.WeatherWidget;
import cn.flyaudio.weather.adapter.CityAdapter;
import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.service.WeatherService;
import cn.flyaudio.weather.util.Constant;
import cn.flyaudio.weather.util.DayNightUtil;
import cn.flyaudio.weather.util.UtilsTools;
import cn.flyaudio.weather.view.SkinResource;


public class CityEditPageActivity extends Activity implements DayNightUtil.ReflashUI {
    private final String TAG = "CityEditPageActivity";
    private final Boolean DEBUG_FLAG = Constant.DEBUG_FLAG;
    private SharedPreferences preference = null;
    private GridView gridview;
    public List<String> mCityList;// 城市名称
    public List<String> mCityEnList;// 城市名称
    public List<String> mCityCodeList;// 城市代码
    private Toast mToast;
    private UpdateUIReceiver mUIReceiver;
    private View mRootView;
    private View rootView;
    String supportDayNight = SkinResource.getSkinStringByName("skin_support_day_night_mode");
    //    private DumpActivityReceiver mDumpActivityReceiver;
    CityAdapter cityAdapter;
    private UpdateItenUIReceiver mItemUIReceiver;


    private class UpdateItenUIReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constant.ACTION_UPITEMUI)) {
                Message msg = mHandler.obtainMessage(1);
                mHandler.sendMessage(msg);
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            cityAdapter.notifyDataSetChanged();
            Log.d(TAG, "[CityEditPageActivity] Notify ItemAdapter Changed...");
        }

        ;
    };

    private void registerIntentFilter() {
        mItemUIReceiver = new UpdateItenUIReceiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Constant.ACTION_UPITEMUI);
        registerReceiver(mUIReceiver, iFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixMyViewError();
    }


    private void initNightView() {
        LayoutInflater inflater = LayoutInflater.from(SkinResource.getSkinContext());
        rootView = inflater.inflate(SkinResource.getSkinLayoutIdByName("cityedit_activity_layout"), null);
        if (rootView == null) {
            rootView = inflater.inflate(SkinResource.getSkinLayoutIdByName("cityedit_activity_layout"), null);
        }
        setContentView(rootView);
    }

    private void initDayView() {
        LayoutInflater inflater = LayoutInflater.from(SkinResource.getSkinContext());
        rootView = inflater.inflate(SkinResource.getSkinLayoutIdByName("cityedit_activity_layout"), null);
        setContentView(rootView);
    }

    private void init() {

        if (rootView != null) {
            mRootView = rootView.findViewById(SkinResource.getSkinResourceId("cityeditpagebg_color", "id"));
            preference = getSharedPreferences("weather", MODE_PRIVATE);

            Button addButton = (Button) rootView.findViewById(SkinResource.getSkinResourceId("addbutton", "id"));
            Button backButton = (Button) rootView.findViewById(SkinResource.getSkinResourceId("backbutton", "id"));
            backButton.setOnClickListener(backClickListener);
            addButton.setOnClickListener(addClickListener);

            mUIReceiver = new UpdateUIReceiver();
            IntentFilter iFilter = new IntentFilter(Constant.ACTION_UPDATE_ADAPTER);
            registerReceiver(mUIReceiver, iFilter);

//            mDumpActivityReceiver = new DumpActivityReceiver();
//            IntentFilter iFilter2 = new IntentFilter(Constant.ACTION_UPDATE_DATA);
//            registerReceiver(mDumpActivityReceiver, iFilter2);
            gridview = (GridView) rootView.findViewById(SkinResource.getSkinResourceId("gridview", "id"));
            gridview.setNumColumns(1);
        }
    }


    /**
     * 换肤(资源重定位)有自定义控件时，需要先加载自定义控件，否则会出现找不到类
     */
    private void fixMyViewError() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int initPreloadViewsID = this.getResources().getIdentifier("main", "layout", this.getPackageName());
        View initMainView = (View) inflater.inflate(initPreloadViewsID, null);
        setContentView(initMainView);
    }

    private void getCityListAndCityCodeListFormShared() {

        for (int i = 0; i < 4; i++) {
            String city = preference.getString(String.valueOf(i + 1), null);
            String cityen = preference.getString(String.valueOf(i * 10 + 10), null);
//            if (DEBUG_FLAG)
            Log.d(TAG,
                    "[CityEditPageActivity] getCityListAndCityCodeListFormShared() city == "
                            + city);
            String code = readSharpPreference(i + 1);

            if (city != null) {
                mCityList.add(city);
                mCityEnList.add(cityen);
                mCityCodeList.add(code);
            }
        }
    }

    private Button.OnClickListener addClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            cancelToast();
            if (mCityList.size() < 4) {
                Intent mIntent = new Intent(CityEditPageActivity.this,
                        AddCityActivity.class);
                startActivity(mIntent);
                CityEditPageActivity.this.finish();
            } else {
                showToast(SkinResource.getSkinStringByName("citynum_limit"));

            }

        }
    };

    private void showToast(String text) {
        mToast = Toast.makeText(CityEditPageActivity.this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    private Button.OnClickListener backClickListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            CityEditPageActivity.this.finish();
//            Intent intent = new Intent(CityEditPageActivity.this,
//                    WeatherDetailsActivity.class);
//            startActivity(intent);
        }
    };


    private String readSharpPreference(int index) {
        String city = null;
        switch (index) {
            case 1:
                city = preference.getString("city1", Constant.DEFAULT_CITYCODE);
                break;
            case 2:
                city = preference.getString("city2", "10000");
                break;
            case 3:
                city = preference.getString("city3", "10000");
                break;
            case 4:
                city = preference.getString("city4", "10000");
                break;
            default:
                break;
        }
        Log.d("CityEditPageActivity", "  city---" + city);
        return city;
    }


    /*class DumpActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {


            if (arg1.getExtras().equals("aaa")) {

                sendBroadcast(new Intent(Constant.ACTION_UPDATE_ADAPTER));
            } else {
                //延时的目的是从服务器请求回来的数据在界面刷新之前
                Log.d("zzz", "UpdateUIReceiver");
                new Thread() {
                    public void run() {
                        try {
                            sleep(3000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }


        }

    }*/


    class UpdateUIReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Log.d("zzz", "UpdateUIReceiver");
            mCityCodeList.clear();
            mCityList.clear();
            mCityEnList.clear();
            for (int i = 1; i < 5; i++) {
                String city = preference.getString(String.valueOf(i), null);
                String cityen = preference.getString(String.valueOf(i * 10), null);
                String code = preference.getString("city" + i, null);
                if (city != null && code != null) {
                    mCityList.add(city);
                    mCityCodeList.add(code);
                    mCityEnList.add(cityen);
                    if (DEBUG_FLAG)
                        Log.d(TAG,
                                "[CityEditPageActivity] UpdateUIReceiver  mCityList.i  == "
                                        + city);
                }
            }

            cityAdapter = new CityAdapter(CityEditPageActivity.this,
                    CityEditPageActivity.this, WeatherWidgetApplication.isCNLanguage ? mCityList : mCityEnList);
            gridview.setAdapter(cityAdapter);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUIReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DayNightUtil.setDayNightModeListener(this);
        //SystemProperties.get("fly.android.navi.daynightmode"
       /* if (!TextUtils.isEmpty(supportDayNight) && "support".equals(supportDayNight)
                && Constant.CURREN_DAYNIGHT_MODE.equals(SystemProperties.get("fly.android.navi.daynightmode", "day"))) {
            initNightView();
        } else {
            initDayView();
        }*/
        initDayView();
        init();

        /*if (WeatherWidgetApplication.getEnableColorTheme()) {
            mRootView.setBackgroundColor(Integer.parseInt(SystemProperties.get(Constant.PROPERTY_COLORTHEME, "-65536")));
        }*/
        mCityList = new ArrayList<String>();
        mCityCodeList = new ArrayList<String>();
        mCityEnList = new ArrayList<String>();
//        sendBroadcast(new Intent(Constant.ACTION_UPDATE_ADAPTER));//更新adapter,在添加城市后刷新界面
        getCityListAndCityCodeListFormShared();
        gridview.setAdapter(new CityAdapter(CityEditPageActivity.this,
                CityEditPageActivity.this, WeatherWidgetApplication.isCNLanguage ? mCityList : mCityEnList));

        registerIntentFilter();
    }

    @Override
    public void showUI(String mode) {
        Log.d("necor-ota", "Dea showUI ==");
        if (mode.equals(Constant.NIGHT_MODE)) {
            initNightView();
        } else {
            initDayView();
        }
        init();

        /*if (WeatherWidgetApplication.getEnableColorTheme()) {
            mRootView.setBackgroundColor(Integer.parseInt(SystemProperties.get(Constant.PROPERTY_COLORTHEME, "-65536")));
        }*/
        mCityList = new ArrayList<String>();
        mCityCodeList = new ArrayList<String>();
        mCityEnList = new ArrayList<String>();
        getCityListAndCityCodeListFormShared();
        gridview.setAdapter(new CityAdapter(CityEditPageActivity.this,
                CityEditPageActivity.this, WeatherWidgetApplication.isCNLanguage ? mCityList : mCityEnList));

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {//按键的按下事件
                Intent mIntent = new Intent(CityEditPageActivity.this,
                        WeatherDetailsActivity.class);
                startActivity(mIntent);
                CityEditPageActivity.this.finish();
//               return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}