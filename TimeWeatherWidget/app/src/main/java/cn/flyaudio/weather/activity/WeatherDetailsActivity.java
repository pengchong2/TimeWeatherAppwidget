package cn.flyaudio.weather.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.flyaudio.weather.adapter.ViewFlowAdapter;
import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.service.WeatherService;
import cn.flyaudio.weather.util.Constant;
import cn.flyaudio.weather.util.DayNightUtil;
import cn.flyaudio.weather.view.SkinResource;
import cn.flyaudio.weather.view.ViewFlow;


public class WeatherDetailsActivity extends Activity implements DayNightUtil.ReflashUI {
    private final String TAG = "WeatherDetailsActivity";
    private final Boolean DEBUG_FLAG = Constant.DEBUG_FLAG;
    private ViewFlow mViewFlow;
    //	private ViewPager mViewFlow;
    private Button mFreshButton1, mBackButton, mChooseCityButton, mFreshButton;
    private ViewFlowAdapter mViewFlowAdapter;
    private UpdateUIReceiver mUIReceiver;
    private Animation mAnim;
    private View weatherdetailview;
    public static int widthPixels = 0;
    private final int NOTIFYDATACHANGED = 0;
    private Toast mToast;
    private View rootView;//add for skin
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            mViewFlowAdapter.notifyDataSetChanged();
            if (DEBUG_FLAG)
                Log.d(TAG,
                        "[WeatherDetailsActivity] Notify mViewFlowAdapter Changed...");
        }

        ;
    };

    private boolean isgetData = false;
    private RelativeLayout relativeLayout;
    private Animation loadingAnim;
    private ImageView img_loading;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixMyViewError();
        Log.e(TAG, "----" + SystemProperties.get("fly.android.navi.daynightmode", "day"));

        initDayView();
        init();


        WeatherWidgetApplication.getlanguage();

        if (DEBUG_FLAG)
            Log.d(TAG, "[WeatherDetailsActivity][onResume]");

        if (((WeatherWidgetApplication) getApplicationContext()).isServiceRunning()) {
            if (isNetworkAvailable(this)) {
                //mFreshButton1.setVisibility(View.VISIBLE);
                //mFreshButton1.startAnimation(mAnim);
            } else {
                mFreshButton1.setVisibility(View.INVISIBLE);
                Toast.makeText(this,
                        SkinResource.getSkinStringByName("neworkconnect"),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // send Message ......NOTIFYDATACHANGED.....
        Message msg = mHandler.obtainMessage(NOTIFYDATACHANGED);
        mHandler.sendMessage(msg);
    }

    private void init() {
        if (rootView != null) {
            weatherdetailview = rootView.findViewById(SkinResource.getSkinResourceId("weatherdetail", "id"));
            mViewFlow = (ViewFlow) rootView.findViewById(SkinResource.getSkinResourceId("viewflow", "id"));
            mAnim = AnimationUtils.loadAnimation(SkinResource.getSkinContext(), SkinResource.getSkinResourceId("weather_loading", "anim"));
            mAnim.setInterpolator(new LinearInterpolator());

            mFreshButton1 = (Button) rootView.findViewById(SkinResource.getSkinResourceId("freshbutton", "id"));
            //	mFreshButton1.setOnClickListener(freshListener);

            mFreshButton = (Button) rootView.findViewById(SkinResource.getSkinResourceId("fresh_button", "id"));
            mFreshButton.setOnClickListener(freshListener);

            mBackButton = (Button) rootView.findViewById(SkinResource.getSkinResourceId("back_button1", "id"));
            mBackButton.setOnClickListener(backListener);

            mChooseCityButton = (Button) rootView.findViewById(SkinResource.getSkinResourceId("choosecity_button", "id"));
            mChooseCityButton.setOnClickListener(choosecityListener);

            initAnimation();
            relativeLayout = (RelativeLayout) rootView.findViewById(SkinResource.getSkinResourceId("weather_time_loading", "id"));
            img_loading = (ImageView) rootView.findViewById(SkinResource.getSkinResourceId("weather_time_loading_img", "id"));
            mViewFlow.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            startLoadingAnim();

            SharedPreferences shared = getSharedPreferences("weather", 0);
            mViewFlowAdapter = new ViewFlowAdapter(WeatherDetailsActivity.this, this);
            Log.d(TAG, "aaaa---" + mViewFlowAdapter.isEmpty());
            int currentpage = shared.getInt("current", 0) == 0 ? 0 : shared.getInt(
                    "current", 0) - 1;
            if (DEBUG_FLAG)
                Log.d(TAG, "[WeatherDetailsActivity]   currentpage---->"
                        + currentpage);

            for (int i = 0; i < currentpage; i++) {
                if (shared.getString(String.valueOf(i + 1), "").equals("")) {
                    currentpage = currentpage - 1;
                }
            }

            mViewFlow.setAdapter(mViewFlowAdapter, currentpage);
            registerIntentFilter();

			if (mViewFlowAdapter.getCount()==1) {
                mHandler2.sendEmptyMessage(0);
            } else {
                mHandler2.sendEmptyMessageDelayed(0, 1000);
            }


        }

    }

    Handler mHandler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mViewFlow.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                    weatherdetailview.setBackgroundResource(SkinResource.getSkinDrawableIdByName("weather_all_bg_2"));
                    break;
                default:
                    break;
            }
        }

    };


    private void initAnimation() {
        loadingAnim = new RotateAnimation(0, 360, (float) 24, (float) 24);
        loadingAnim.setDuration(1000);
        loadingAnim.setRepeatCount(-1);//动画的重复次数
    }

    private void startLoadingAnim() {
        img_loading.startAnimation(loadingAnim);
    }

    private void stopLoadingAnim() {
        img_loading.clearAnimation();
    }

    private void registerIntentFilter() {
        mUIReceiver = new UpdateUIReceiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Constant.ACTION_UPDATEUI_VIEWFLOW);
        iFilter.addAction(Constant.ACTION_UPDATEUI);
        iFilter.addAction(Constant.ACTION_START_FRESH);
        iFilter.addAction(Constant.ACTION_STOP_FRESH);
        iFilter.addAction(Constant.ACTION_REQUEST_WEATHER);
        registerReceiver(mUIReceiver, iFilter);
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

    private void initDayView() {
        LayoutInflater inflater = LayoutInflater.from(SkinResource.getSkinContext());
        rootView = inflater.inflate(SkinResource.getSkinLayoutIdByName("details_activity_layout"), null);
        setContentView(rootView);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void showUI(String mode) {
        Log.d(TAG, "Dea showUI ==" + mode);
        initDayView();
        init();

    }

    private class UpdateUIReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constant.ACTION_UPDATEUI_VIEWFLOW)) {
                Message msg = mHandler.obtainMessage(NOTIFYDATACHANGED);
                mHandler.sendMessage(msg);
            } else if (intent.getAction().equals(Constant.ACTION_STOP_FRESH)) {
                mFreshButton1.setVisibility(View.INVISIBLE);
                mFreshButton1.clearAnimation();
            } else if (intent.getAction().equals(Constant.ACTION_START_FRESH)) {
                mFreshButton1.startAnimation(mAnim);
                mFreshButton1.setVisibility(View.VISIBLE);

            }
        }
    }

    private OnClickListener backListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntentHome = new Intent(Constant.ACTION_HOME);
            mIntentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(mIntentHome);
            } catch (Exception e) {
            } finally {
                finish();
            }
        }
    };

    private OnClickListener choosecityListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent i = new Intent(WeatherDetailsActivity.this, CityEditPageActivity.class);
            WeatherDetailsActivity.this.startActivity(i);
            WeatherDetailsActivity.this.finish();
        }
    };

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    private OnClickListener freshListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            cancelToast();
            if (isNetworkAvailable(WeatherDetailsActivity.this)) {
                // TODO Auto-generated method stub
                Log.e(TAG, "----WeatherService.isServiceRunning()--1111" + WeatherService.isServiceRunning());
                if (!WeatherService.isServiceRunning()) {
                    Intent intent = new Intent(WeatherDetailsActivity.this,
                            WeatherService.class);
                    startService(intent);
                    Log.e(TAG, "----WeatherService.isServiceRunning()--22222" + WeatherService.isServiceRunning());
                }
                showToast(SkinResource.getSkinStringByName("showToast"));
                mFreshButton1.setVisibility(View.VISIBLE);
                mFreshButton1.startAnimation(mAnim);

            } else
                showToast(SkinResource.getSkinStringByName("neworkconnect"));
        }
    };

    private void showToast(String text) {
        mToast = Toast.makeText(WeatherDetailsActivity.this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mUIReceiver);
        if (DEBUG_FLAG) {
            Log.d(TAG, "[WeatherDetailsActivity][onDestroy]");
        }
    }

}
