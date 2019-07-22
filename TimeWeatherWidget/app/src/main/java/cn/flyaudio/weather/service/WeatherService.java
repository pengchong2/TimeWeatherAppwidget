package cn.flyaudio.weather.service;

import cn.flyaudio.Weather.WeatherWidget;
import cn.flyaudio.time.AlarmDetails;
import cn.flyaudio.time.DayAndNightModeUtil;
import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.util.Constant;
import cn.flyaudio.weather.util.DayNightUtil;
import cn.flyaudio.weather.util.GetWeatherUtil;
import cn.flyaudio.weather.util.UtilsTools;
import cn.flyaudio.weather.view.SkinResource;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class WeatherService extends Service implements Runnable, DayNightUtil.ReflashUI2 {
    private final String TAG = "WeatherService";
    private final Boolean DEBUG_FLAG = Constant.DEBUG_FLAG;

    public static final int CompleteDownData = 1;
    private static boolean sThreadRunning = false;
    public static long updateTime = 0;
    private static long _1hours = 3 * 60 * 60 * 1000;
    private static long _1minute = 60 * 1000;
    private int mCityCount;
    private SharedPreferences preference, preference1, preference2,
            preference3, preference4;
    //	public static boolean backups[] = { false, false, false, false };
    public static boolean backup = false;

    private List<FullWeatherInfo> citiesWeatherList = null;
    private ArrayList<String> mCities = null;


    final static String ACTION_TIMEBROADCAST = "android.intent.action.timebroadcast";

    public BroadcastReceiver alarmReceiver;
    public Notification notify;
    private SharedPreferences preference0;
    private MediaPlayer mMediaPlayer_2, mMediaPlayer_1;
    final static String MediaCompleted = "Flyaudio3_TimeService.intent.action.Completed";
    final static String MediaPrepared = "Flyaudio3_TimeService.intent.action.MediaPrepared";
    final static String MediaAlarm = "Flyaudio3_TimeService.intent.action.MediaAlarm";
    private int RingSound = 0;
    public Application application;
    private Bitmap bg;
    private Typeface mTypeface;

    private String strTimeFormat_12_24 = "";
    public static int color = Color.RED;
    public static boolean enableColorTheme = true;
    Drawable whiteBg = null;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " -----onCreate ");
        DayNightUtil.setDayNightModeListener2(this);
        preference = getSharedPreferences("weather", MODE_PRIVATE);
        preference1 = getSharedPreferences("FirstWeather", MODE_PRIVATE);
        preference2 = getSharedPreferences("SecondWeather", MODE_PRIVATE);
        preference3 = getSharedPreferences("ThirdWeather", MODE_PRIVATE);
        preference4 = getSharedPreferences("FouthWeather", MODE_PRIVATE);


        application = ((WeatherWidgetApplication) WeatherWidgetApplication.application);
        Log.d("Flyaudio3_TimeService", "Flyaudio3_TimeService--->onCreate");
        mMediaPlayer_1 = new MediaPlayer();
        mMediaPlayer_2 = new MediaPlayer();
        //need = TimeWidgetApplication.shareResources.getString(((TimeWidgetApplication)application).getResId("need_change_color", "string"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK); // 时间的流逝，以分钟为单位
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED); // 时间被改变，人为设置时间
        intentFilter.addAction("action.flyaudio.updateTimeByGPS");
        if (enableColorTheme) {
            whiteBg = ((WeatherWidgetApplication) application).getShareResources().getDrawable(((WeatherWidgetApplication) application).getResId("white_bg", "drawable"));
            intentFilter.addAction("action.flyaudio.colortheme");
            String c = SystemProperties.get("persist.fly.colortheme", "red");
//			if(c.equals("red"))
//				color = Color.RED;
            try {
                color = Integer.valueOf(c);
            } catch (Exception e) {
                // TODO: handle exception
                color = Color.RED;
            }
        }
        //注册日夜模式广播Action
//        intentFilter.addAction(DayAndNightModeUtil.ACTION_DAY_AND_NIGHT_MODE);
        ContentResolver mProvider = WeatherWidgetApplication.getContext().getContentResolver();
        strTimeFormat_12_24 = android.provider.Settings.System.getString(mProvider,
                android.provider.Settings.System.TIME_12_24);
        if (strTimeFormat_12_24 == null) {
            strTimeFormat_12_24 = "";
        }
        Log.d(TAG, "onCreate >>>strTimeFormat_12_24==" + strTimeFormat_12_24);
        registerReceiver(boroadcastReceiver, intentFilter);

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(MediaAlarm);
        registerReceiver(alarmReceiver, iFilter);

        preference0 = getSharedPreferences("time", MODE_PRIVATE);
        if (!preference0.contains("alarmOrNot")) {
            SharedPreferences.Editor editor = preference0.edit();
            editor.putString("alarmOrNot", "no");
            editor.commit();
        }
    }

   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "--onStartCommdd()--intent=" + intent);
        updateUI(color); // 开始服务前先刷新一次UI
        return START_STICKY;
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "--onStartCommdd()--intent=" + intent);
        updateUI(color); // 开始服务前先刷新一次UI
        Log.d(TAG, "[WeatherService] onStart()  sThreadRunning == " + sThreadRunning);

        if (!sThreadRunning) {
            sThreadRunning = true;
            new Thread(this).start();
        }
        return START_STICKY;
    }

  /*  @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        updateUI(color); // 开始服务前先刷新一次UI
        if (DEBUG_FLAG)
            Log.d(TAG, "[WeatherService] onStart()  sThreadRunning == " + sThreadRunning);

        if (!sThreadRunning) {
            sThreadRunning = true;
            new Thread(this).start();
        }
    }*/

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(TAG, " -----onDestroy ");
        Log.d(TAG, "Flyaudio3_TimeService--onDestroy()--");
        unregisterReceiver(boroadcastReceiver);
        this.startService(new Intent(this, WeatherService.class));
//		startService(new Intent(getApplicationContext(), WeatherService.class));
    }


    // 用于监听系统时间变化Intent.ACTION_TIME_TICK的BroadcastReceiver，此BroadcastReceiver须为动态注册
    public BroadcastReceiver boroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context acontext, Intent intent) {
            Log.d(TAG, "Flyaudio3_TimeService--监听系统时间变化广播--");
            Log.d(TAG, intent.getAction());
            if (intent.getAction().equals("action.flyaudio.colortheme")) {
                int rgb = intent.getIntExtra("rgb", -1);
                Log.d(TAG, "Flyaudio3_TimeService  onReceive:" + rgb);
                color = rgb;
                updateUI(color);
            } else if (intent.getAction().equals("action.flyaudio.updateTimeByGPS")) {
                Log.d(TAG, " receive action.flyaudio.updateTimeByGPS action .");
                if (SystemProperties.get("persist.fly.usegps", "no").equals("yes")) {
                    Log.d(TAG, "new gps : " + SystemProperties.get("persist.fly.usegps", "no").toString());
                    updateUI(color);
                }
            } else if (intent.getAction().equals("android.intent.action.TIME_SET")) {
                ContentResolver mProvider = WeatherWidgetApplication.getContext().getContentResolver();
                strTimeFormat_12_24 = android.provider.Settings.System.getString(mProvider,
                        android.provider.Settings.System.TIME_12_24);
                if (strTimeFormat_12_24 == null) {
                    strTimeFormat_12_24 = "";
                }
                updateUI(color);
            } else if (intent.getAction().equals(DayAndNightModeUtil.ACTION_DAY_AND_NIGHT_MODE)) {
                String mode = intent.getStringExtra(DayAndNightModeUtil.DAY_AND_NIGHT_MODE_BROADCAST_KEY);
                if (DayAndNightModeUtil.isSupportDayAndNightMode() &&
                        (DayAndNightModeUtil.DAY_NIGHT_MODE_DYA.equals(mode) || DayAndNightModeUtil.DAY_NITHT_MODE_NIGHT.equals(mode))) {
                    updateUI(color);
                }
            } else {
                Log.d(TAG, "receive other action");
                updateUI(color);
            }

        }
    };

    private Bitmap colorChange(Bitmap src, int color) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        if (whiteBg != null) {
            Drawable temp = whiteBg;
            temp.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            cv.drawBitmap(drawableToBitmap(temp), 0, 0, null);
        }
        cv.drawBitmap(src, 0, 0, null);
        //cv.save(Canvas.ALL_SAVE_FLAG);//保存
        cv.save();
        cv.restore();//存储
        return newb;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }


    // 根据当前时间设置小部件相应的数字图片
    private void updateUI(int color) {
        Log.d(TAG, "Flyaudio3_TimeService--->updateUI");
        RemoteViews remoteViews = null;
        if (DayAndNightModeUtil.isNightMode()) {
            remoteViews = new RemoteViews(((WeatherWidgetApplication) application).getSharePackageName(),
                    ((WeatherWidgetApplication) application).getResId("widget_layout", "layout"));
        } else {
            remoteViews = new RemoteViews(((WeatherWidgetApplication) application).getSharePackageName(),
                    ((WeatherWidgetApplication) application).getResId("widget_layout", "layout"));
        }

		/*if (DayAndNightModeUtil.isNightMode()) {
            bg = BitmapFactory.decodeResource(((WeatherWidgetApplication) application).getShareResources(),
					((WeatherWidgetApplication) application).getResId("time_widget_bg", "drawable"));
		} else {
			bg = BitmapFactory.decodeResource(((WeatherWidgetApplication) application).getShareResources(),
					((WeatherWidgetApplication) application).getResId("time_widget_bg", "drawable"));
		}*/
//        Log.e("AAA", "Flyaudio3_TimeService updateUI");
		/*if (enableColorTheme) {
			Bitmap temp = colorChange(bg, color);
			remoteViews.setImageViewBitmap(((WeatherWidgetApplication) application).getResId("time_main", "id"), temp);
		} else {
			remoteViews.setImageViewBitmap(((WeatherWidgetApplication) application).getResId("time_main", "id"), bg);
		}*/

        Date currentDateTime = Calendar.getInstance().getTime();
        SimpleDateFormat AM_PM_hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
        int hournum = Integer.parseInt(AM_PM_hourFormat.format(currentDateTime));
        String amPmStr = null;
        SimpleDateFormat hourFormat = null;
        Log.d(TAG, " updateUI：strTimeFormat_12_24 " + strTimeFormat_12_24);
        if (strTimeFormat_12_24.equals("12")) {
            hourFormat = new SimpleDateFormat("hh", Locale.getDefault());
            if (0 <= hournum && hournum < 12)
                amPmStr = "AM";
            else
                amPmStr = "PM";
            remoteViews.setViewVisibility(((WeatherWidgetApplication) application).getResId("time_ampm_text", "id"), View.VISIBLE);
            remoteViews.setTextViewText(((WeatherWidgetApplication) application).getResId("time_ampm_text", "id"), amPmStr);
        } else {
            hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
            remoteViews.setViewVisibility(((WeatherWidgetApplication) application).getResId("time_ampm_text", "id"), View.GONE);
        }
        SimpleDateFormat minFormat = new SimpleDateFormat("mm", Locale.getDefault());
        SimpleDateFormat secFormat = new SimpleDateFormat("ss", Locale.getDefault());

        Log.d(TAG, "获取到系统当前的时间是 ：strTimeFormat_12_24 " + strTimeFormat_12_24 + "," + hourFormat.format(currentDateTime) + " : " + minFormat.format(currentDateTime) + " : " + secFormat.format(currentDateTime));

        if (getEnableTimeFont()) {
            remoteViews.setImageViewBitmap(((WeatherWidgetApplication) application).getResId("time_hour_imageview", "id"),
                    createTimeBitmap(hourFormat.format(currentDateTime)));
            remoteViews.setImageViewBitmap(((WeatherWidgetApplication) application).getResId("time_min_imageview", "id"), createTimeBitmap(minFormat.format(currentDateTime)));
        } else {
            remoteViews.setTextViewText(((WeatherWidgetApplication) application).getResId("time_hour_text", "id"), hourFormat.format(currentDateTime));
            remoteViews.setTextViewText(((WeatherWidgetApplication) application).getResId("time_min_text", "id"), minFormat.format(currentDateTime));
        }

        if (enableColorTheme) {
            remoteViews.setTextColor(((WeatherWidgetApplication) application).getResId("time_ampm_text", "id"), color);
        }
        preference0 = getSharedPreferences("time", MODE_PRIVATE);
        if (preference0.getString("alarmOrNot", "no").equals("yes")) {
            Bitmap bmp = null;
            if (DayAndNightModeUtil.isNightMode()) {
                bmp = BitmapFactory.decodeResource(((WeatherWidgetApplication) application).getShareResources(),
                        ((WeatherWidgetApplication) application).getResId("time_clockflag", "drawable"));
            } else {
                bmp = BitmapFactory.decodeResource(((WeatherWidgetApplication) application).getShareResources(),
                        ((WeatherWidgetApplication) application).getResId("time_clockflag", "drawable"));
            }
            //remoteViews.setImageViewBitmap(R.id.timelogo, bmp);
            remoteViews.setImageViewBitmap(((WeatherWidgetApplication) application).getResId("timelogo", "id"), bmp);
        } else {
            Bitmap bmp = BitmapFactory.decodeResource(((WeatherWidgetApplication) application).getShareResources(),
                    ((WeatherWidgetApplication) application).getResId("time_clockflag_bg", "drawable"));
            //remoteViews.setImageViewBitmap(R.id.timelogo, bmp);
//            remoteViews.setImageViewBitmap(((WeatherWidgetApplication) application).getResId("timelogo", "id"), bmp);
        }
        if (minFormat.format(currentDateTime).equals("00") && preference0.getString("alarmOrNot", "no").equals("yes")
                && secFormat.format(currentDateTime).equals("00")) {
            if (getResources().getConfiguration().locale.getCountry().equals("CN")) {// 中文版声音
                for (int i = 0; i < 24; i++) {
                    String hour = AM_PM_hourFormat.format(currentDateTime);
                    if (hour.equals(i < 10 ? ("0" + i) : (i + ""))) {
                        RingSound = ((WeatherWidgetApplication) application).getResId("time_b" + i, "raw");
                        break;
                    }
                }
            } else {// 英文版声音
                for (int i = 0; i < 24; i++) {
                    String hour = AM_PM_hourFormat.format(currentDateTime);
                    if (hour.equals(i < 10 ? ("0" + i) : (i + ""))) {
                        RingSound = ((WeatherWidgetApplication) application).getResId("time_e" + i, "raw");
                        break;
                    }
                }
            }

            Intent intent = new Intent(ACTION_TIMEBROADCAST);
            intent.putExtra("TIMEBROADCAST", "play");
            sendBroadcast(intent);
            Log.d(TAG, "Flyaudio3_TimeService--sendBroadcast" + ACTION_TIMEBROADCAST + "---play");
            //sendBroadcast(new Intent(MediaPrepared));// MediaPrepared

            InputStream is1 = ((WeatherWidgetApplication) application).getShareResources().openRawResource(((WeatherWidgetApplication) application).getResId("time_gg_big", "raw"));
            InputStream is2 = ((WeatherWidgetApplication) application).getShareResources().openRawResource(RingSound);

            String tempPath1 = "";
            String tempPath2 = "";
            try {
                File temp1 = File.createTempFile("mediaplayertmp1", "temp1");
                File temp2 = File.createTempFile("mediaplayertmp2", "temp2");
                tempPath1 = temp1.getAbsolutePath();
                tempPath2 = temp2.getAbsolutePath();
                writeTempFile(is1, temp1);
                writeTempFile(is2, temp2);
                Log.d(TAG, "Flyaudio3_TimeService--临时文件1路径：=" + tempPath1);
                Log.d(TAG, "Flyaudio3_TimeService--临时文件2路径：=" + tempPath2);
            } catch (Exception e2) {
                Log.d(TAG, "Flyaudio3_TimeService--创建临时文件出错！!!" + e2);
            }
            mMediaPlayer_1.reset();
            mMediaPlayer_2.reset();
            try {
                mMediaPlayer_1.setDataSource(tempPath1);
                mMediaPlayer_2.setDataSource(tempPath2);
                mMediaPlayer_1.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer_2.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer_1.prepare();
                mMediaPlayer_2.prepare();
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (SecurityException e1) {
                e1.printStackTrace();
            } catch (IllegalStateException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        Log.d(TAG, "开始播放第一段");
                        mMediaPlayer_1.start();
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.d(TAG, "播放第一段异常：" + e.toString());
                    }
                }

            }, 500);

            mMediaPlayer_1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "第一段播放结束");
                    try {
                        Log.d(TAG, "开始播放第二段");
                        mMediaPlayer_2.start();
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.d(TAG, "播放第二段异常：" + e.toString());
                    }

                }
            });
            mMediaPlayer_2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "第二段播放结束");
                    Intent intent = new Intent(ACTION_TIMEBROADCAST);
                    intent.putExtra("TIMEBROADCAST", "stop");
                    Log.d(TAG, "Flyaudio3_TimeService--sendBroadcast" + ACTION_TIMEBROADCAST + "---stop");
                    sendBroadcast(intent);
                }
            });

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());//国内版日期显示格式
//        SimpleDateFormat dateFormat = new SimpleDateFormat(cn.flyaudio.weather.view.SkinResource.getSkinContext().getResources()
//                .getString(cn.flyaudio.weather.view.SkinResource.getSkinResourceId("weather_dateformat", "string")), Locale.getDefault());
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());//俄文版日期显示格式

        if (WeatherWidgetApplication.shareResources.getString(((WeatherWidgetApplication) application).getResId("need_change_time_formate", "string")).equals("yes")) {
            Log.d("QQ", " need_change_time_formate ");
            String format = WeatherWidgetApplication.shareResources.getString(((WeatherWidgetApplication) application).getResId("weather_dateformat", "string"));
//            Log.i("AAA", "format:" + format);

            dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        }

        //remoteViews.setTextViewText(R.id.date_text, dateFormat.format(currentDateTime));
        remoteViews.setTextViewText(((WeatherWidgetApplication) application).getResId("date_text", "id")
                , dateFormat.format(currentDateTime));
        String weekStr = null;
        weekStr = getWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        //remoteViews.setTextViewText(R.id.week_text, weekStr);
        remoteViews.setTextViewText(((WeatherWidgetApplication) application).getResId("week_text", "id")
                , weekStr);
//        Log.e("AAA", "format:" + weekStr);
//        Log.e("AAA", "format:" + Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        Intent detailIntent1 = new Intent(this, AlarmDetails.class);
        PendingIntent pending1 = PendingIntent.getActivity(this, 0, detailIntent1, 0);
        //remoteViews.setOnClickPendingIntent(R.id.time_main, pending1);
        remoteViews.setOnClickPendingIntent(((WeatherWidgetApplication) application).getResId("time_main", "id"),
                pending1);

        // 将AppWidgetProvider的子类包装成ComponentName对象
        ComponentName componentName = new ComponentName(this, WeatherWidget.class);
        // 调用AppWidgetManager将remoteViews添加到ComponentName中
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        if (appWidgetManager == null) {
            // appWidgetManager= TimeApplication.appWidgetManager;
            Log.d(TAG, "appWidgetManager == null");
            WeatherService.this.sendBroadcast(new Intent("test"));
            return;
        }
        appWidgetManager.updateAppWidget(componentName, remoteViews);
        remoteViews = null;
        sendBroadcast(new Intent(Constant.ACTION_APPWIDGET_UPDATE));

    }

    public String getWeek(int week) {
        String weekStr = null;
        switch (week) {
            case 1:
                weekStr = SkinResource.getSkinStringByName("sunday");
                break;
            case 2:
                weekStr = SkinResource.getSkinStringByName("monday");
                break;
            case 3:
                weekStr = SkinResource.getSkinStringByName("tuesday");
                break;
            case 4:
                weekStr = SkinResource.getSkinStringByName("wednesday");
                break;
            case 5:
                weekStr = SkinResource.getSkinStringByName("thursday");
                break;
            case 6:
                weekStr = SkinResource.getSkinStringByName("friday");
                break;
            case 7:
                weekStr = SkinResource.getSkinStringByName("saturday");
                break;
        }
        return weekStr;
    }

    private void writeTempFile(InputStream is, File temp) throws FileNotFoundException, IOException {
        FileOutputStream out = new FileOutputStream(temp);
        BufferedOutputStream bis = new BufferedOutputStream(out);
        byte buf[] = new byte[128];
        do {
            int numread = is.read(buf);
            if (numread <= 0)
                break;
            bis.write(buf, 0, numread);
        } while (true);
        Log.d(TAG, "writeTempFile");
    }

    private MediaPlayer create(Context context, int resid) {
        InputStream stream = ((WeatherWidgetApplication) application).getShareResources().openRawResource(resid);
        Log.d(TAG, "stream : " + stream);
        if (stream != null)
            return create(context, stream);
        else
            return null;
    }

    private MediaPlayer create(Context context, InputStream stream) {
        MediaPlayer mediaplayer = null;
        try {
            File temp = File.createTempFile("mediaplayertmp", "temp");
            String tempPath = temp.getAbsolutePath();
            FileOutputStream out = new FileOutputStream(temp);
            // 用BufferdOutputStream速度快
            BufferedOutputStream bis = new BufferedOutputStream(out);
            byte buf[] = new byte[128];
            do {
                int numread = stream.read(buf);
                if (numread <= 0)
                    break;
                bis.write(buf, 0, numread);
            } while (true);
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(tempPath);
            mp.prepare();
            mediaplayer = mp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaplayer;
    }

    /**
     * 释放上一次MediaPlayer资源
     */
    private void releaseMediaPlayer() {
        if (mMediaPlayer_2 != null) {
            if (mMediaPlayer_2.isPlaying()) {
                mMediaPlayer_2.stop();
            }
            mMediaPlayer_2.release();
            mMediaPlayer_2 = null;
        }
    }

    /**
     * 获取是否使用图片改变时间字体
     *
     * @return
     */
    private boolean getEnableTimeFont() {
        if (((WeatherWidgetApplication) application).getFlyProperty() != null) {
            return ((WeatherWidgetApplication) application).getFlyProperty().getBoolenValue("enableTimeFont");
        }
        return false;
    }

    private Bitmap createTimeBitmap(String time) {
        Bitmap bitmap = null;
        Canvas canvas = null;
        Paint paint = null;
        if (mTypeface == null) {
            mTypeface = ((WeatherWidgetApplication) application).getTypeface();
        }
        if (mTypeface != null) {
            bitmap = Bitmap.createBitmap(SkinResource.getIntegerFromSkin("widget_time_bitmap_width"),
                    SkinResource.getIntegerFromSkin("widget_time_bitmap_height"), Bitmap.Config.ARGB_4444);
            canvas = new Canvas(bitmap);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setAlpha(SkinResource.getIntegerFromSkin("widget_time_bitmap_alpha"));
            paint.setSubpixelText(true);
            paint.setTypeface(mTypeface);
            paint.setStyle(Paint.Style.FILL);
            if (DayAndNightModeUtil.isNightMode()) {
                paint.setColor(SkinResource.getColorFromSkin("widget_time_bitmap_night_textcolor"));
            } else {
                paint.setColor(SkinResource.getColorFromSkin("widget_time_bitmap_textcolor"));
            }
            paint.setTextSize(SkinResource.getDimenFromSkin("widget_time_bitmap_textsize"));
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(time, SkinResource.getIntegerFromSkin("widget_time_text_beginx"),
                    SkinResource.getIntegerFromSkin("widget_time_text_beginy"), paint);
        }
        return bitmap;
    }


    @Override
    public void run() {

        FullWeatherInfo[] mFullWeatherInfos = new FullWeatherInfo[4];
        mCities = new ArrayList<String>();
        mCityCount = 0;
        String city1 = preference.getString("city1", null);
        if (!TextUtils.isEmpty(city1)) {
            mCityCount++;
            mCities.add(city1);
        }

        String city2 = preference.getString("city2", null);
        if (!TextUtils.isEmpty(city2)) {
            mCityCount++;
            mCities.add(city2);
        }

        String city3 = preference.getString("city3", null);
        if (!TextUtils.isEmpty(city3)) {
            mCityCount++;
            mCities.add(city3);
        }

        String city4 = preference.getString("city4", null);
        if (!TextUtils.isEmpty(city4)) {
            mCityCount++;
            mCities.add(city4);
        }

        Log.d(TAG, "mCityCount = " + mCityCount + ",mCities.size = " + mCities.size());
        //get all city weather info
        if (mCityCount != 0 && !backup) {
            citiesWeatherList = GetWeatherUtil.Instance().getCitiesWeatherInfo(mCities);

            if (null != citiesWeatherList) {
                int listSize = citiesWeatherList.size();
                Log.d(TAG, "WeatherData listSize = " + listSize);
                if (listSize == 0) {
                    sThreadRunning = false;
                    return;
                }

                switch (listSize) {
                    case 1:
                        if (!TextUtils.isEmpty(city1)) {
                            mFullWeatherInfos[0] = citiesWeatherList.get(0);
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference1, citiesWeatherList.get(0));
                            break;
                        }
                        if (!TextUtils.isEmpty(city2)) {
                            mFullWeatherInfos[1] = citiesWeatherList.get(0);
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference2, citiesWeatherList.get(0));
                            break;
                        }
                        if (!TextUtils.isEmpty(city3)) {
                            mFullWeatherInfos[2] = citiesWeatherList.get(0);
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference3, citiesWeatherList.get(0));
                            break;
                        }
                        if (!TextUtils.isEmpty(city4)) {
                            mFullWeatherInfos[3] = citiesWeatherList.get(0);
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference4, citiesWeatherList.get(0));
                            break;
                        }
                        break;
                    case 2:
                        if (TextUtils.isEmpty(city1)) {
                            if (TextUtils.isEmpty(city2)) {
                                mFullWeatherInfos[2] = citiesWeatherList.get(0);
                                mFullWeatherInfos[3] = citiesWeatherList.get(1);
                                UtilsTools.saveForecastfweathers(WeatherService.this, preference3, citiesWeatherList.get(0));
                                UtilsTools.saveForecastfweathers(WeatherService.this, preference4, citiesWeatherList.get(1));
                                break;
                            } else {
                                mFullWeatherInfos[1] = citiesWeatherList.get(0);
                                UtilsTools.saveForecastfweathers(WeatherService.this, preference2, citiesWeatherList.get(0));
                                if (TextUtils.isEmpty(city3)) {
                                    mFullWeatherInfos[3] = citiesWeatherList.get(1);
                                    UtilsTools.saveForecastfweathers(WeatherService.this, preference4, citiesWeatherList.get(1));
                                    break;
                                }
                                mFullWeatherInfos[2] = citiesWeatherList.get(1);
                                UtilsTools.saveForecastfweathers(WeatherService.this, preference3, citiesWeatherList.get(1));
                                break;
                            }
                        } else {
                            mFullWeatherInfos[0] = citiesWeatherList.get(0);
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference1, citiesWeatherList.get(0));
                            if (!TextUtils.isEmpty(city2)) {
                                mFullWeatherInfos[1] = citiesWeatherList.get(1);
                                UtilsTools.saveForecastfweathers(WeatherService.this, preference2, citiesWeatherList.get(1));
                                Log.e("AAA", "保存了");
                                break;
                            }
                            if (!TextUtils.isEmpty(city3)) {
                                mFullWeatherInfos[2] = citiesWeatherList.get(1);
                                UtilsTools.saveForecastfweathers(WeatherService.this, preference3, citiesWeatherList.get(1));
                                break;
                            }
                            if (!TextUtils.isEmpty(city4)) {
                                mFullWeatherInfos[3] = citiesWeatherList.get(1);
                                UtilsTools.saveForecastfweathers(WeatherService.this, preference4, citiesWeatherList.get(1));
                                break;
                            }
                        }
                        break;
                    case 3:
                        if (TextUtils.isEmpty(city1)) {
                            mFullWeatherInfos[1] = citiesWeatherList.get(0);
                            mFullWeatherInfos[2] = citiesWeatherList.get(1);
                            mFullWeatherInfos[3] = citiesWeatherList.get(2);
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference2, citiesWeatherList.get(0));
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference3, citiesWeatherList.get(1));
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference4, citiesWeatherList.get(2));
                            break;
                        }
                        if (TextUtils.isEmpty(city2)) {
                            mFullWeatherInfos[0] = citiesWeatherList.get(0);
                            mFullWeatherInfos[2] = citiesWeatherList.get(1);
                            mFullWeatherInfos[3] = citiesWeatherList.get(2);
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference1, citiesWeatherList.get(0));
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference3, citiesWeatherList.get(1));
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference4, citiesWeatherList.get(2));
                            break;
                        }

                        if (TextUtils.isEmpty(city3)) {
                            mFullWeatherInfos[0] = citiesWeatherList.get(0);
                            mFullWeatherInfos[1] = citiesWeatherList.get(1);
                            mFullWeatherInfos[3] = citiesWeatherList.get(2);
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference1, citiesWeatherList.get(0));
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference2, citiesWeatherList.get(1));
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference4, citiesWeatherList.get(2));
                            break;
                        }
                        if (TextUtils.isEmpty(city4)) {
                            mFullWeatherInfos[0] = citiesWeatherList.get(0);
                            mFullWeatherInfos[1] = citiesWeatherList.get(1);
                            mFullWeatherInfos[2] = citiesWeatherList.get(2);
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference1, citiesWeatherList.get(0));
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference2, citiesWeatherList.get(1));
                            UtilsTools.saveForecastfweathers(WeatherService.this, preference3, citiesWeatherList.get(2));
                            break;
                        }

                        break;
                    case 4:
                        mFullWeatherInfos[0] = citiesWeatherList.get(0);
                        mFullWeatherInfos[1] = citiesWeatherList.get(1);
                        mFullWeatherInfos[2] = citiesWeatherList.get(2);
                        mFullWeatherInfos[3] = citiesWeatherList.get(3);
                        UtilsTools.saveForecastfweathers(WeatherService.this, preference1, citiesWeatherList.get(0));
                        UtilsTools.saveForecastfweathers(WeatherService.this, preference2, citiesWeatherList.get(1));
                        UtilsTools.saveForecastfweathers(WeatherService.this, preference3, citiesWeatherList.get(2));
                        UtilsTools.saveForecastfweathers(WeatherService.this, preference4, citiesWeatherList.get(3));
                        break;
                    default:
                        break;

                }

                backup = true;
                notifyForUpdateUI(0);
                notifyForUpdateItemUI();

            }
        }

		/*FullWeatherInfo[] weatherInfos = new FullWeatherInfo[4];
		if (!preference.getString("city1", "无").equals("无") && !backups[0]) {
			mCityCount = 1;

				//GetWeatherByJsonParser mGetWeatherByJsonParser = new GetWeatherByJsonParser(preference.getString("city1", "无"));
				//FullWeatherInfo mFullWeatherInfo = mGetWeatherByJsonParser.getWeatherinfo(1);
			    FullWeatherInfo mFullWeatherInfo = GetWeatherUtil.Instance().getCitiesWeatherInfo(preference.getString("city1", "无"));
				if (mFullWeatherInfo != null && mFullWeatherInfo.getDataflag()!=null) {
					UtilsTools.saveForecastfweathers(WeatherService.this,preference1, mFullWeatherInfo);
					backups[0] = true;
					notifyForUpdateUI(1);
					weatherInfos[0] = mFullWeatherInfo;
			}
		}*/

        updateTime = getUpdateTime();
        Intent updateIntent = new Intent(Constant.ACTION_UPDATE_ALL);
        updateIntent.setClass(this, WeatherService.class);
        PendingIntent pending = PendingIntent.getService(this, 0, updateIntent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Time time = new Time();
        long nowMillis = System.currentTimeMillis();
        time.set(nowMillis + updateTime);
        long updateTimes = time.toMillis(true);
        alarm.set(AlarmManager.RTC_WAKEUP, updateTimes, pending);
        sThreadRunning = false;
//		stopSelf();
        Log.d(TAG, "run()====   sThreadRunning ==  " + sThreadRunning);
        if (DEBUG_FLAG)
            Log.d(TAG, "[WeatherService] run()  request next updateTime at"
                    + updateTime);
        // 2015-07-06 moxiyong
        int current = preference.getInt("current", 1);
        String city = preference.getString(String.valueOf(current), "");
        String cityPinYin = preference.getString(String.valueOf(current * 10),
                "");
        current--;
        Log.d(TAG, "run()====weatherInfos[current]==" + mFullWeatherInfos[current]);
        /*Log.e("AAAAA2","cityname"+mFullWeatherInfos[current].getCity()+"  --Low()"+mFullWeatherInfos[current].yweathers[0].getLow()
                +"   --High()"+mFullWeatherInfos[current].yweathers[0].getHigh()
                +"   --Condition_temp()"+mFullWeatherInfos[current].getCondition_temp());*/
        if (mFullWeatherInfos[current] != null)
            UtilsTools.sendBoardcast2SystemUI(WeatherService.this, mFullWeatherInfos[current], city, cityPinYin);
    }


    private void notifyForUpdateUI(int current) {
        this.sendBroadcast(new Intent(Constant.ACTION_APPWIDGET_UPDATE));
        Intent intent = new Intent(Constant.ACTION_UPDATEUI_VIEWFLOW);
        intent.putExtra("current", current);
        this.sendBroadcast(intent);
    }

    private void notifyForUpdateItemUI() {
        this.sendBroadcast(new Intent(Constant.ACTION_UPITEMUI));
       /* Intent intent = new Intent(Constant.ACTION_UPDATEUI_VIEWFLOW);
        intent.putExtra("current", current);
        this.sendBroadcast(intent);*/
    }

    int j = 0;

    private long getUpdateTime() {

        long systemMillis = System.currentTimeMillis();
        if (UtilsTools.isConnect(WeatherService.this)) {
            for (int i = 0; i < mCityCount; i++) {
                if (!backup) {
                    Log.d(TAG, "[WeatherService] getUpdateTime()  12s");
                    return 12000;
                }
            }
        }

        ((WeatherWidgetApplication) getApplicationContext()).setServiceRunning(false);
        sendBroadcast(new Intent(Constant.ACTION_STOP_FRESH));
        Log.d(TAG, "[WeatherService] getUpdateTime()  sendBroadcast Constant.ACTION_STOP_FRESH");
        for (int i = 0; i < 4; i++) {
            backup = false;
            if (DEBUG_FLAG)
                Log.d(TAG, "[WeatherService] getUpdateTime()  backup" + (i + 1) + " = " + backup);
        }
        if (DEBUG_FLAG)
            Log.d(TAG, "[WeatherService] " + "() _1hours - ((systemMillis) % (_1hours))="
                    + (_1hours - ((systemMillis) % (_1hours))));
        // udatetime in next 1hour_1minute 10:01 11:01
        return _1hours - ((systemMillis) % (_1hours)) + _1minute;
    }

    public static boolean isServiceRunning() {
        return updateTime == 12000;
    }

    @Override
    public void showUI2(String mode) {
        Log.d(TAG, "---showUI");
        if (mode.equals(Constant.NIGHT_MODE)) {
            Log.d(TAG, TAG + "---夜间模式");
//			notifyForUpdateUI(0);
            sendBroadcast(new Intent(Constant.ACTION_APPWIDGET_UPDATE));
        } else {
            Log.d(TAG, TAG + "---日间模式");
            sendBroadcast(new Intent(Constant.ACTION_APPWIDGET_UPDATE));
//			notifyForUpdateUI(0);
        }

    }
}