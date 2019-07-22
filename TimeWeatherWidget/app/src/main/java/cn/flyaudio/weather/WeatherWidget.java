package cn.flyaudio.Weather;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.flyaudio.time.AlarmDetails;
import cn.flyaudio.time.DayAndNightModeUtil;
import cn.flyaudio.weather.activity.WeatherDetailsActivity;
import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.service.WeatherService;
import cn.flyaudio.weather.util.Constant;
import cn.flyaudio.weather.util.TimeWeatherUtilsTools;
import cn.flyaudio.weather.util.UtilsTools;
import cn.flyaudio.weather.view.SkinResource;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.os.SystemProperties;

import static android.content.Context.MODE_PRIVATE;

public class WeatherWidget extends AppWidgetProvider {
    private final static String TAG = "WeatherWidget";
    private final static Boolean DEBUG_FLAG = Constant.DEBUG_FLAG;

    public static String now_date;
    public static String sh_date;
    public static SharedPreferences shared;
    public View viewRoots;
    public RemoteViews views;
    private Context context;
    String supportDayNight = SkinResource.getSkinStringByName("skin_support_day_night_mode");


    Intent detailIntent;
    PendingIntent pending;
//	public Application application;
//	public Bitmap bg;
//	Drawable whiteBg= null;
//	int color;


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
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        this.context = context;

        application = ((WeatherWidgetApplication) WeatherWidgetApplication.application);
        Log.d("Flyaudio3_TimeService", "Flyaudio3_TimeService--->onCreate");
        mMediaPlayer_1 = new MediaPlayer();
        mMediaPlayer_2 = new MediaPlayer();
        /*if (DayAndNightModeUtil.isNightMode()) {
            bg = BitmapFactory.decodeResource(((WeatherWidgetApplication) application).getShareResources(),
					((WeatherWidgetApplication) application).getResId("time_widget_bg", "drawable"));
		} else {
			bg = BitmapFactory.decodeResource(((WeatherWidgetApplication) application).getShareResources(),
					((WeatherWidgetApplication) application).getResId("time_widget_bg", "drawable"));
		}*/
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
        intentFilter.addAction(DayAndNightModeUtil.ACTION_DAY_AND_NIGHT_MODE);
        ContentResolver mProvider = WeatherWidgetApplication.getContext().getContentResolver();
        strTimeFormat_12_24 = android.provider.Settings.System.getString(mProvider,
                android.provider.Settings.System.TIME_12_24);
        if (strTimeFormat_12_24 == null) {
            strTimeFormat_12_24 = "";
        }
        Log.d(TAG, "onCreate >>>strTimeFormat_12_24==" + strTimeFormat_12_24);
//		registerReceiver(boroadcastReceiver, intentFilter);

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(MediaAlarm);
//		registerReceiver(alarmReceiver, iFilter);

        preference0 = context.getSharedPreferences("time", MODE_PRIVATE);
        if (!preference0.contains("alarmOrNot")) {
            SharedPreferences.Editor editor = preference0.edit();
            editor.putString("alarmOrNot", "no");
            editor.commit();
        }


        if (DEBUG_FLAG)
            Log.d(TAG, "[WeatherWidget] onReceive() action = " + intent.getAction());

        if (DEBUG_FLAG)
            Log.d(TAG, "[WeatherWidget] onReceive() intent.current = " + intent.getIntExtra("current", -1));

        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            try {
                context.stopService(new Intent(context, WeatherService.class));
            } catch (Exception e) {
                if (DEBUG_FLAG)
                    Log.d(TAG, "[WeatherWidget] onReceive() Exception == " + e.toString());
            }

            if (UtilsTools.isConnect(context)) {

                if (DEBUG_FLAG)
                    Log.d(TAG, "[WeatherWidget] Utils.isConnect()");
                context.startService(new Intent(context, WeatherService.class));

                if (DEBUG_FLAG)
                    Log.d(TAG, "[WeatherWidget] sendBroadcast ACTION_START_FRESH ");
                context.sendBroadcast(new Intent(Constant.ACTION_START_FRESH));
            } else {
                context.sendBroadcast(new Intent(Constant.ACTION_STOP_FRESH));
                if (DEBUG_FLAG)
                    Log.d(TAG, "[WeatherWidget] sendBroadcast ACTION_STOP_FRESH ");
            }
        } else if (intent.getAction().equals(
                "android.intent.action.BOOT_COMPLETED")
                || intent.getAction().equals("cn.flyaudio.action.ACCON")) {// 时间判断
            shared = context.getSharedPreferences(
                    getCurrentWeather(context, intent.getIntExtra("current", -1)), 0);

            AppWidgetManager appwidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentname = new ComponentName(context, WeatherWidget.class);
            appwidgetManager.updateAppWidget(componentname, updateAppWidget(context, shared));

        } else if (intent.getAction().equals(
                "android.appwidget.action.APPWIDGET_UPDATE")) {
            Log.e(TAG, "---aaaaa---");
            shared = context.getSharedPreferences(
                    getCurrentWeather(context, intent.getIntExtra("current", -1)), 0);

            AppWidgetManager appwidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentname = new ComponentName(context, WeatherWidget.class);
            appwidgetManager.updateAppWidget(componentname, updateAppWidget(context, shared));

        } else if (intent.getAction().equals("android.intent.action.TIME_SET")) {
            shared = context.getSharedPreferences(
                    getCurrentWeather(context, intent.getIntExtra("current", -1)), 0);

            AppWidgetManager appwidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentname = new ComponentName(context, WeatherWidget.class);
            appwidgetManager.updateAppWidget(componentname, updateAppWidget(context, shared));

        } else if (intent.getAction().equals("action.flyaudio.colortheme")) {
            int rgb = intent.getIntExtra("rgb", -1);
            Log.d("rgb", "weather widget rgb=" + rgb);
            SharedPreferences shared = context.getSharedPreferences("weather", 0);
            SharedPreferences.Editor editor = shared.edit();
            editor.putInt("weather_widget_color", rgb);
            editor.commit();

            shared = context.getSharedPreferences(
                    getCurrentWeather(context, intent.getIntExtra("current", -1)), 0);

            AppWidgetManager appwidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentname = new ComponentName(context, WeatherWidget.class);
            appwidgetManager.updateAppWidget(componentname, updateAppWidget(context, shared));

        } else if (intent.getAction().equals("android.intent.action.LOCALE_CHANGED")) {
            WeatherWidgetApplication.getlanguage();
            shared = context.getSharedPreferences(
                    getCurrentWeather(context, intent.getIntExtra("current", -1)), 0);
            AppWidgetManager appwidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentname = new ComponentName(context, WeatherWidget.class);
            appwidgetManager.updateAppWidget(componentname, updateAppWidget(context, shared));
        }


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        SharedPreferences shared = context.getSharedPreferences("weather", 0);
        SharedPreferences.Editor editor = shared.edit();

        SharedPreferences shared2 = context.getSharedPreferences("FirstWeather", 0);
        SharedPreferences shared3 = context.getSharedPreferences("SecondWeather", 0);
        SharedPreferences shared4 = context.getSharedPreferences("ThirdWeather", 0);
        SharedPreferences shared5 = context.getSharedPreferences("FouthWeather", 0);

        int currentCityNum = shared.getInt("current", 1);
        String currentCityName = "city" + String.valueOf(currentCityNum);
        String city = shared.getString(currentCityName, "");

		if (!city.equals("") || city != null) {
            int currentpage = shared.getInt("current", 0) == 0 ? 0 : shared.getInt("current", 0) - 1;
            if (currentpage==1) {
                saveForecastfweathers2(shared3);
            }else if (currentpage==2) {
                saveForecastfweathers2(shared4);
            }else if (currentpage==3) {
                saveForecastfweathers2(shared5);
            }else {
                saveForecastfweathers2(shared2);
            }
            Log.e("aaaaa--","bbbbbbb---"+currentpage);

		}
        if (city.equals("")) {
            editor.putString("city1", Constant.DEFAULT_CITYCODE);
            editor.putString("1", SkinResource.getSkinContext().getString(SkinResource.getSkinResourceId("guangzhou", "string")));
            editor.putString("10", SkinResource.getSkinContext().getString(SkinResource.getSkinResourceId("guangzhou_pinyin", "string")));
            editor.putInt("current", 1);
            editor.commit();
        }
        context.startService(new Intent(context, WeatherService.class));

    }

    /* 保存天气信息 */
    public static void saveForecastfweathers2(SharedPreferences p) {
        Log.e("aaaaa--","bbbbbbb");
        SharedPreferences.Editor editor = p.edit();
        editor.putString("condition_text", "");
        editor.putString("condition_code", "");
        editor.putString("condition_temp", "");
        editor.putString("condition_date", "");
        editor.putString("humidity", "--");
        editor.putString("visibility", "--");
        editor.putString("direction", "-1");
        editor.putString("speed", "");
        editor.putString("feelslike", "--");
        editor.putString("sunrise", "--");
        editor.putString("sunset", "--");
        editor.putBoolean("dataflag", false);
        int index = 0;
        for (int i = 0; i < 5; i++) {
            index = i + 1;
            editor.putString("code" + index, "-1");
            editor.putString("text" + index, "");
            editor.putString("day" + index, "");
            editor.putString("date" + index, "");
            editor.putString("low" + index, "");
            editor.putString("high" + index, "");
        }
        editor.commit();
    }

    private RemoteViews updateAppWidget(Context context, SharedPreferences shared) {

        int passtime = getPastTime(context, shared);
        if (passtime > 5 || passtime < 0)
            passtime = 0;

        views = new RemoteViews(SkinResource.getSkinContext().getPackageName(),
                SkinResource.getSkinLayoutIdByName("widget_layout"));

        if (WeatherWidgetApplication.getEnableColorTheme()) {
            int bgcolor = Integer.parseInt(SystemProperties.get(Constant.PROPERTY_COLORTHEME, "-65536"));
            BitmapDrawable mask = (BitmapDrawable) SkinResource.getSkinContext().getResources().getDrawable(SkinResource.getSkinDrawableIdByName("menu_weather"));
            views.setBitmap(SkinResource.getSkinResourceId("weatherwidgetcolormask", "id"), "setImageBitmap", UtilsTools.getBitmap(bgcolor, mask));
        }

        Intent detailIntent2 = new Intent(context, AlarmDetails.class);
        PendingIntent pending2 = PendingIntent.getActivity(context, 0,
                detailIntent2, 0);
        views.setOnClickPendingIntent(SkinResource.getSkinResourceId("weather_time_widget_rl2", "id"), pending2);

        Intent detailIntent3 = new Intent(context, WeatherDetailsActivity.class);
        PendingIntent pending3 = PendingIntent.getActivity(context, 0,
                detailIntent3, 1);
        views.setOnClickPendingIntent(SkinResource.getSkinResourceId("weather_time_widget_rl", "id"), pending3);

        //摄氏度
//		String degreeCelsius = SkinResource.getSkinStringByName("degree");
        //低温到高温之间的符号
        String lowToHightSymbol = SkinResource.getSkinStringByName("low_to_hight_symbol");
        //没数据时默认显示的温度
        String noTempData = SkinResource.getSkinStringByName("no_temp_data");

        if (shared.getBoolean("dataflag", false)) {
            views.setTextViewText(SkinResource.getSkinResourceId("txt_widget_low_temperature", "id"), shared.getString("low1", noTempData));
            views.setTextViewText(SkinResource.getSkinResourceId("txt_widget_hight_temperature", "id"), shared.getString("high1", noTempData));

            views.setTextViewText(SkinResource.getSkinResourceId("curt_temp_text", "id"),
                    shared.getString("condition_temp", noTempData));
            //图标和背景分离时修改：weatherwidget改为img_widget_weather_icon（新增的图标id）
            views.setInt(SkinResource.getSkinResourceId("img_widget_weather_icon", "id"), "setBackgroundResource",
                    UtilsTools.parseSmartBgBycode(shared.getString(
                            "condition_code", "-1"), shared.getString("", shared.getString("sunrise", noTempData)), shared.getString("sunset", noTempData)));
            views.setTextViewText(
                    SkinResource.getSkinResourceId("condition_text", "id"),
                    UtilsTools.getSmartWeatherByNum(context, shared.getString("condition_code", "-1")));

        } else {
            String noConditionData = SkinResource.getSkinStringByName("no_weather_data");
            views.setTextViewText(SkinResource.getSkinResourceId("curt_temp_text", "id"), noTempData);
            views.setTextViewText(SkinResource.getSkinResourceId("txt_widget_low_temperature", "id"), noTempData);
            views.setTextViewText(SkinResource.getSkinResourceId("txt_widget_hight_temperature", "id"), noTempData);
            views.setInt(SkinResource.getSkinResourceId("img_widget_weather_icon", "id"), "setBackgroundResource",
                    SkinResource.getSkinDrawableIdByName("weather_widget_default_icon"));
            views.setTextViewText(SkinResource.getSkinResourceId("condition_text", "id"), noConditionData);
        }

        SharedPreferences sharedCity = context.getSharedPreferences("weather", 0);
        int currentCityNum = sharedCity.getInt("current", 1);
        String city = WeatherWidgetApplication.isCNLanguage ? sharedCity.getString(String.valueOf(currentCityNum), "") :
                sharedCity.getString(String.valueOf(currentCityNum * 10), "");

        if (city.length() >= 7 && city.contains(","))
            city = city.split(",")[1];
        if (WeatherWidgetApplication.isCNLanguage) {
            views.setTextViewText(SkinResource.getSkinResourceId("city_text", "id"), city);
        } else {
            String cityName = WeatherWidgetApplication.toUpperCaseFirstOne(city);
            Flog.d("WidgetcityName cityName=" + cityName);
            views.setTextViewText(SkinResource.getSkinResourceId("city_text", "id"), cityName);
        }


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
            views.setViewVisibility(((WeatherWidgetApplication) application).getResId("time_ampm_text", "id"), View.VISIBLE);
            views.setTextViewText(((WeatherWidgetApplication) application).getResId("time_ampm_text", "id"), amPmStr);
        } else {
            hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
            views.setViewVisibility(((WeatherWidgetApplication) application).getResId("time_ampm_text", "id"), View.GONE);
        }
        SimpleDateFormat minFormat = new SimpleDateFormat("mm", Locale.getDefault());
        SimpleDateFormat secFormat = new SimpleDateFormat("ss", Locale.getDefault());

        Log.d(TAG, "获取到系统当前的时间是 ：strTimeFormat_12_24 " + strTimeFormat_12_24 + "," + hourFormat.format(currentDateTime) + " : " + minFormat.format(currentDateTime) + " : " + secFormat.format(currentDateTime));

        if (getEnableTimeFont()) {
            views.setImageViewBitmap(((WeatherWidgetApplication) application).getResId("time_hour_imageview", "id"),
                    createTimeBitmap(hourFormat.format(currentDateTime)));
            views.setImageViewBitmap(((WeatherWidgetApplication) application).getResId("time_min_imageview", "id"), createTimeBitmap(minFormat.format(currentDateTime)));
        } else {
            views.setTextViewText(((WeatherWidgetApplication) application).getResId("time_hour_text", "id"), hourFormat.format(currentDateTime));
            views.setTextViewText(((WeatherWidgetApplication) application).getResId("time_min_text", "id"), minFormat.format(currentDateTime));
        }

        if (enableColorTheme) {
            views.setTextColor(((WeatherWidgetApplication) application).getResId("time_ampm_text", "id"), color);
        }
        preference0 = context.getSharedPreferences("time", MODE_PRIVATE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());//国内版日期显示格式
//        SimpleDateFormat dateFormat = new SimpleDateFormat(cn.flyaudio.weather.view.SkinResource.getSkinContext().getResources()
//                .getString(cn.flyaudio.weather.view.SkinResource.getSkinResourceId("weather_dateformat", "string")), Locale.getDefault());
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());//俄文版日期显示格式

        if (WeatherWidgetApplication.shareResources.getString(((WeatherWidgetApplication) application).getResId("need_change_time_formate", "string")).equals("yes")) {
            Log.d("QQ", " need_change_time_formate ");
            String format = WeatherWidgetApplication.shareResources.getString(((WeatherWidgetApplication) application).getResId("weather_dateformat", "string"));
            Log.i("AAA", "format:" + format);

            dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        }

        //remoteViews.setTextViewText(R.id.date_text, dateFormat.format(currentDateTime));
        views.setTextViewText(((WeatherWidgetApplication) application).getResId("date_text", "id")
                , dateFormat.format(currentDateTime));
        String weekStr = null;
        weekStr = getWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        //remoteViews.setTextViewText(R.id.week_text, weekStr);
        views.setTextViewText(((WeatherWidgetApplication) application).getResId("week_text", "id")
                , weekStr);
        Log.e("AAA", "format:" + weekStr);
        Log.e("AAA", "format:" + Calendar.getInstance().get(Calendar.DAY_OF_WEEK));


        return views;
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


    public static String getCurrentWeather(Context c, int current) {
        if (current <= 0) {
            SharedPreferences shared = c.getSharedPreferences("weather", 0);
            current = shared.getInt("current", 1);
            if (DEBUG_FLAG)
                Log.d(TAG, "[WeatherWidget] getCurrentWeather()  current="
                        + current);
        }
        switch (current) {
            case 1: {
                return "FirstWeather";
            }
            case 2: {
                return "SecondWeather";
            }
            case 3: {
                return "ThirdWeather";
            }
            case 4: {
                return "FouthWeather";
            }
            default:
                return "FirstWeather";
        }
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
//		android.os.Process.killProcess(android.os.Process.myPid());
//		System.exit(0);


        Log.d(TAG, "TimeWidget--->onDeleted");
        Intent intent = new Intent(context, WeatherService.class);
        context.stopService(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private int getPastTime(Context context, SharedPreferences shared) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        now_date = sDateFormat.format(new java.util.Date());
        // 20140221 diff同步机器时间显示
        if (shared != null) {
            if (DEBUG_FLAG)
                Log.d(TAG, "[WeatherWidget] getPastTime() shared!=null");

            sh_date = shared.getString("date_y", "无");
            if (sh_date.equals("无")) {
                return 0;
            }
        } else {
            if (DEBUG_FLAG)
                Log.d(TAG, "[WeatherWidget] getPastTime()  shared=null");

            return 0;
        }
        java.util.Date before = null;
        try {
            before = df.parse(now_date);
        } catch (ParseException e) {
            System.out.println("error--before = df.parse(now_date)");
            e.printStackTrace();
        }
        java.util.Date after = null;
        try {
            after = df.parse(sh_date);
        } catch (ParseException e) {
            System.out.println("error--after = df.parse(sh_date)");
            e.printStackTrace();
        }
        long l = before.getTime() - after.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        if (DEBUG_FLAG)
            Log.d(TAG, "[WeatherWidget] day == " + day);
        return (int) day;
    }
}
