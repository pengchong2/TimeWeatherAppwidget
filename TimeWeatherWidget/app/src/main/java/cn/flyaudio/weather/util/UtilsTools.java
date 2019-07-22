package cn.flyaudio.weather.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.flyaudio.weather.adapter.LastFewDaysWeatherViewAdapter;
import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.view.SkinResource;

/**
 * @author:
 * @company:flyaudio
 * @version:2.0
 * @createdDate:20160531
 */
public class UtilsTools {
    private final static String TAG = Constant.TAG;
    private final static Boolean DEBUG_FLAG = Constant.DEBUG_FLAG;

    public static boolean isConnect(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (DEBUG_FLAG)
                Log.d(TAG, "[Utils] isConnect() == "
                        + connectivityManager.getActiveNetworkInfo()
                        .isAvailable());
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        } catch (Exception e) {
            return false;
        }
    }

    public static void setConvertViewWithShared(Context context, View v,
                                                String city) {
        SharedPreferences wShared = context.getSharedPreferences("weather", 0);
        int index = 0;

        FullWeatherInfo mForecastyweathers = null;
        for (int i = 1; i < 5; i++)
            if (city.equals(wShared.getString(String.valueOf(i), null)))
                index = i;

        switch (index) {
            case 1:
                mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                        context.getSharedPreferences("FirstWeather", 0), 1);
                break;
            case 2:
                mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                        context.getSharedPreferences("SecondWeather", 0), 2);
                break;
            case 3:
                mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                        context.getSharedPreferences("ThirdWeather", 0), 3);
                break;
            case 4:
                mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                        context.getSharedPreferences("FouthWeather", 0), 4);
                break;
        }

        setDataView(SkinResource.getSkinContext(), v, mForecastyweathers);
    }

    /**
     * @author moxiyong
     */
    public static void sendBoardcast2SystemUI(Context context, FullWeatherInfo info,
                                              String city, String cityPinYin) {
        Intent intent = new Intent("cn.flyaudio.weather.WEATHERINFO");
        intent.putExtra("condition_text", info.getCondition_text());
        intent.putExtra("condition_code", info.getCondition_code());
        intent.putExtra("condition_temp", info.getCondition_temp());
        intent.putExtra("condition_date", info.getCondition_date());
        intent.putExtra("city_name", city);
        intent.putExtra("city_pinyin", cityPinYin);
        if (DEBUG_FLAG)
            Log.d(TAG, "[UtilsTools] sendBoardcast2SystemUI()" + " " + info.getCondition_text()
                    + "$" + info.getCondition_code() + "$" + info.getCondition_temp()
                    + "$" + info.getCondition_date() + "$" + city + "$" + cityPinYin);
        context.sendBroadcast(intent);
    }

    public static FullWeatherInfo getWeatherInfoFormSharedPreferences(
            Context context, SharedPreferences shared, int index) {
        FullWeatherInfo mForecastyweathers = new FullWeatherInfo();

        mForecastyweathers.setCondition_text(shared.getString("condition_text",
                ""));
        mForecastyweathers.setCondition_code(shared.getString("condition_code",
                "-1"));
        mForecastyweathers.setCondition_temp(shared.getString("condition_temp",
                ""));
        mForecastyweathers.setCondition_date(shared.getString("condition_date",
                ""));
//        mForecastyweathers.setHumidity(shared.getString("humidity", "--"));
//        mForecastyweathers.setVisibility(shared.getString("visibility", "--"));

        mForecastyweathers.setWinddirection(shared.getString("direction", "-1"));
        mForecastyweathers.setWindspeed(shared.getString("speed", ""));
//        mForecastyweathers.setSunrise(shared.getString("sunrise", "--"));
//        mForecastyweathers.setSunset(shared.getString("sunset", "--"));
        mForecastyweathers.setDataflag(shared.getBoolean("dataflag", false));
        mForecastyweathers.setCity(shared.getString("city",
                getCityNameWithIndex(context, index)));
        mForecastyweathers.setCityPinyin(shared.getString("city",
                getCityPinyinNameWithIndex(context, index)));

//        mForecastyweathers.setFeelslike(shared.getString("feelslike", "--"));
        // mForecastyweathers.setCityPinyin(shared.getString("city_en", ""));

        for (int i = 0; i < 5; i++) {
            mForecastyweathers.yweathers[i].setCode(shared.getString("code"
                    + (i + 1), "-1"));
            mForecastyweathers.yweathers[i].setText(shared.getString("text"
                    + (i + 1), ""));
            mForecastyweathers.yweathers[i].setDay(shared.getString("day"
                    + (i + 1), ""));
            mForecastyweathers.yweathers[i].setDate(shared.getString("date"
                    + (i + 1), ""));
            mForecastyweathers.yweathers[i].setLow(shared.getString("low"
                    + (i + 1), ""));
            mForecastyweathers.yweathers[i].setHigh(shared.getString("high"
                    + (i + 1), ""));
        }

        return mForecastyweathers;
    }

    private static void setDataView(Context context, View v,
                                    FullWeatherInfo mForecastyweathers) {
        if (WeatherWidgetApplication.isCNLanguage) {
            setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("weathercity", "id")),
                    mForecastyweathers.getCity());
        } else {
            String cityName = mForecastyweathers.getCityPinyin();
            setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("weathercity", "id")),
                    WeatherWidgetApplication.toUpperCaseFirstOne(cityName));
        }
//		setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("weathercity_pinyin","id")),
//				mForecastyweathers.getCityPinyin());
        setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("date", "id")),
                getSystemDate(SkinResource.getSkinContext().getString(SkinResource.getSkinResourceId("weather_timeformat", "string"))));

        if (mForecastyweathers.getDataflag()) {
            v.findViewById(SkinResource.getSkinResourceId("curTemp_parent", "id")).setVisibility(View.VISIBLE);
            v.findViewById(SkinResource.getSkinResourceId("layout_low_hight_temperature", "id")).setVisibility(View.VISIBLE);

            setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("curTemp", "id")),
                    mForecastyweathers.getCondition_temp());
            if (isDay(mForecastyweathers.getSunrise(), mForecastyweathers.getSunset())) {
                setTextView(
                        (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_cur_weather_condition", "id")),
                        getSmartWeatherByNum(context, mForecastyweathers.getCondition_code()));
            } else {
                setTextView(
                        (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_cur_weather_condition", "id")),
                        getSmartWeatherByNum(context, mForecastyweathers.getCondition_code()));
            }

            TextView curLowTemperature = (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_cur_low_temperature", "id"));
            TextView curHightTemperature = (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_cur_hight_temperature", "id"));
            curLowTemperature.setText(mForecastyweathers.yweathers[0].getLow());
            curHightTemperature.setText(mForecastyweathers.yweathers[0].getHigh());

//			setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("weather_lowhigh","id")),
//					mForecastyweathers.yweathers[0].getLow() + " ~ "	+ mForecastyweathers.yweathers[0].getHigh());
        }
        setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("datafrom", "id")),
                SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("weather_num_100", "string")));

        setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("weatherdate", "id")),
                getPubDate(context, mForecastyweathers.getCondition_date()));

        ImageView imgBg = (ImageView) v.findViewById(SkinResource.getSkinResourceId("set", "id"));

//        imgBg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
//                parseWeatherBgBycode(mForecastyweathers.getCondition_code(),
//                        mForecastyweathers.getSunrise(), mForecastyweathers.getSunset())));

        //图标和背景分离时修改：set改为img_weather_icon（新增的图标id）
        ImageView img = (ImageView) v.findViewById(SkinResource.getSkinResourceId("img_weather_icon", "id"));

        img.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                parseSmartBgBycode(mForecastyweathers.getCondition_code(),
                        mForecastyweathers.getSunrise(), mForecastyweathers.getSunset())));


//        ListView lvLastFewDaysWeather = (ListView) v.findViewById(SkinResource.getSkinResourceId("lv_last_few_days_weather", "id"));

        GridView lvLastFewDaysWeather = (GridView) v.findViewById(SkinResource.getSkinResourceId("lv_last_few_days_weather", "id"));

        LastFewDaysWeatherViewAdapter adapter = new LastFewDaysWeatherViewAdapter(context, mForecastyweathers);
        lvLastFewDaysWeather.setAdapter(adapter);
        lvLastFewDaysWeather.setEnabled(false);

        TextView txtWindSpeed = (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_wind_speed", "id"));
        TextView txtWindDirection = (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_wind_direction", "id"));
        TextView txtSunrise = (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_sunrise", "id"));
        TextView txtSunset = (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_sunset", "id"));
        TextView viewWindSpeed = (TextView) v.findViewById(SkinResource.getSkinResourceId("weather_time_view_windSpeed", "id"));
        TextView viewWindDirection = (TextView) v.findViewById(SkinResource.getSkinResourceId("weather_time_view_windDirection", "id"));

        txtSunrise.setText(getNotNullValue(mForecastyweathers.getSunrise()));
        txtSunset.setText(getNotNullValue(mForecastyweathers.getSunset()));
        txtWindSpeed.setText(getSmartWindSpeed(SkinResource.getSkinContext(), mForecastyweathers.getWindspeed()));
        txtWindDirection.setText(getSmartWindDirection(SkinResource.getSkinContext(), mForecastyweathers.getWinddirection()));
        viewWindSpeed.setText(getSmartWindSpeed(SkinResource.getSkinContext(), mForecastyweathers.getWindspeed()));
        viewWindDirection.setText(getSmartWindDirection(SkinResource.getSkinContext(), mForecastyweathers.getWinddirection()));

    }

    private static String getPubDate(Context context, String date) {
        return SkinResource.getSkinContext().getString(SkinResource.getSkinResourceId("updatetime", "string"), date, "");
    }

    private static void setTextView(TextView textView, String text) {
        textView.setText(text);
    }

    private static String getSystemDate(String dateformat) {
        SimpleDateFormat format = new SimpleDateFormat(dateformat);
        Date date = new Date(System.currentTimeMillis());
        return format.format(date).toUpperCase();
    }

    public static Bitmap getWeatherIcon(Context context, String pos) {

        Bitmap bm = BitmapFactory.decodeResource(
                context.getResources(),
                UtilsTools.parseSmartIcon(pos));
        int width = bm.getWidth();
        int height = bm.getHeight();
        int newWidth1 = 110;
        int newHeight1 = 110;
        float scaleWidth = ((float) newWidth1) / width;
        float scaleHeight = ((float) newHeight1) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        return newbm;

    }

    private static String getCityNameWithIndex(Context context, int index) {
        SharedPreferences shared = context.getSharedPreferences("weather", 0);
        String city = shared.getString(String.valueOf(index), "");
        /*
		 * if (city.length() >= 7 && city.contains("-")) city =
		 * city.split("-")[1];
		 */
        return city;
    }

    private static String getCityPinyinNameWithIndex(Context context, int index) {
        SharedPreferences shared = context.getSharedPreferences("weather", 0);
        String city = shared.getString(String.valueOf(index * 10), "");
        return city;
    }

    /* 保存天气信息 */
    public static void saveForecastfweathers(Context context,
                                             SharedPreferences p, FullWeatherInfo mForecastyweathers) {
        SharedPreferences.Editor editor = p.edit();
        editor.putString("condition_text",
                mForecastyweathers.getCondition_text());
        editor.putString("condition_code",
                mForecastyweathers.getCondition_code());
        editor.putString("condition_temp",
                mForecastyweathers.getCondition_temp());
        editor.putString("condition_date",
                mForecastyweathers.getCondition_date());
        editor.putString("humidity", mForecastyweathers.getHumidity());
        editor.putString("visibility", mForecastyweathers.getVisibility());
        editor.putString("direction", mForecastyweathers.getWinddirection());
        editor.putString("speed", mForecastyweathers.getWindspeed());
        editor.putString("feelslike", mForecastyweathers.getFeelslike());
        editor.putString("sunrise", mForecastyweathers.getSunrise());
        editor.putString("sunset", mForecastyweathers.getSunset());
        editor.putBoolean("dataflag", mForecastyweathers.getDataflag());
        int index = 0;
        for (int i = 0; i < 5; i++) {
            index = i + 1;
            editor.putString("code" + index,
                    mForecastyweathers.yweathers[i].getCode());
            editor.putString("text" + index,
                    mForecastyweathers.yweathers[i].getText());
            editor.putString("day" + index,
                    mForecastyweathers.yweathers[i].getDay());
            editor.putString("date" + index,
                    mForecastyweathers.yweathers[i].getDate());
            editor.putString("low" + index,
                    mForecastyweathers.yweathers[i].getLow());
            editor.putString("high" + index,
                    mForecastyweathers.yweathers[i].getHigh());
        }
        editor.commit();
    }

    private static String getNotNullValue(String value) {
        if (value != null && value != "") {
            return value;
        }
        return "--";
    }

    private static String getSmartWindSpeed(Context context, String value) {
        int sunnyNum = SkinResource.getSkinResourceId("nullstring", "string");
        if (value == null || value.equals("") || value.equals("-1"))
            return "";
        int codeValue = Integer.parseInt(value);
        switch (codeValue) {
            case 0:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed0", "string");
                break;
            case 1:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed1", "string");
                break;
            case 2:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed2", "string");
                break;
            case 3:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed3", "string");
                break;
            case 4:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed4", "string");
                break;
            case 5:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed5", "string");
                break;
            case 6:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed6", "string");
                break;
            case 7:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed7", "string");
                break;
            case 8:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed8", "string");
                break;
            case 9:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_speed9", "string");
                break;
            default:
                return "";
        }
        return SkinResource.getSkinContext().getResources().getString(sunnyNum);
    }

    private static String getSmartWindDirection(Context context, String value) {
        int sunnyNum = SkinResource.getSkinResourceId("nullstring", "string");
        if (value == null || value.equals("") || value.equals("-1"))
            return "";
        int codeValue = Integer.parseInt(value);
        switch (codeValue) {
            case 0:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct0", "string");
                break;
            case 1:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct1", "string");
                break;
            case 2:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct2", "string");
                break;
            case 3:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct3", "string");
                break;
            case 4:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct4", "string");
                break;
            case 5:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct5", "string");
                break;
            case 6:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct6", "string");
                break;
            case 7:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct7", "string");
                break;
            case 8:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct8", "string");
                break;
            case 9:
                sunnyNum = SkinResource.getSkinResourceId("smart_weather_wind_direct9", "string");
                break;
            default:
                return "";
        }
        return SkinResource.getSkinContext().getResources().getString(sunnyNum);
    }

    /* 获取星期 字符串 */
    public static String getWeekName(Context context, String date, int index) {
        String weekName;
        String systemweek = new SimpleDateFormat("yyyy-MM-dd").format(new Date(
                System.currentTimeMillis()));
        if (DEBUG_FLAG)
            Log.d(TAG, "[Utils] getWeekName() == " + systemweek);
        // String s = "2008-08-08";
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            Date date2 = df.parse(systemweek);
            if (DEBUG_FLAG)
                Log.d(TAG, "[Utils] getWeekName() date2== " + date2);
            if (date.equals(date2.toString().split(" ")[0]))
                return SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Today", "string"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date.equals(SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Monday", "string")))) {
            return SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Monday", "string"));
        } else if (date.equals(SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Tuesday", "string")))) {
            return SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Tuesday", "string"));
        } else if (date.equals(SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Wednesday", "string")))) {
            return SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Wednesday", "string"));
        } else if (date.equals(SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Thursday", "string")))) {
            return SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Thursday", "string"));
        } else if (date.equals(SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Friday", "string")))) {
            return SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Friday", "string"));
        } else if (date.equals(SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Saturday", "string")))) {
            return SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Saturday", "string"));
        } else if (date.equals(SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Sunday", "string")))) {
            return SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("Sunday", "string"));
        }
        SimpleDateFormat format = new SimpleDateFormat(
                SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("weather_weekformat", "string")));
        weekName = format.format(new Date(System.currentTimeMillis() + index
                * (24 * 60 * 60 * 1000)));
        return weekName.toUpperCase();
    }

    /* 网络是否连接 */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static String getTimeShort(String strDate, int index, Context context) {
        Date date;
        String dateString = "";
        SimpleDateFormat formatter = new SimpleDateFormat(SkinResource.getSkinContext().getResources().getString(SkinResource.getSkinResourceId("weather_dateformat", "string")));
        try {
            if (strDate != "") {
                date = new Date(strDate);
                if (date == null)
                    return "";
            } else {
                date = new Date(System.currentTimeMillis() + index
                        * (24 * 60 * 60 * 1000));
            }
            dateString = formatter.format(date);
        } catch (Exception e) {
        }
        return dateString;
    }

    private static boolean isDay(String sunrise, String sunset) {
        Date date = new Date(System.currentTimeMillis());
        int hour = date.getHours();
        int minutes = date.getMinutes();
        Boolean isDay = true;
        if (sunrise == "--" && sunset == "--") {
            return isDay;
        } else if (sunrise == "--") {
            isDay = false;
            return isDay;
        }

        try {
            int hour_sunrise = Integer.parseInt(sunrise.substring(0,
                    sunrise.indexOf(":")));
            int minutes_sunrise = Integer.parseInt(sunrise.substring(
                    sunrise.indexOf(":") + 1, sunrise.indexOf(" ")));
            int hour_sunset = Integer.parseInt(sunset.substring(0,
                    sunrise.indexOf(":"))) + 12;
            int minutes_sunset = Integer.parseInt(sunset.substring(
                    sunrise.indexOf(":") + 1, sunrise.indexOf(" ")));

            if ((hour_sunrise < hour || (hour_sunrise == hour && minutes_sunrise <= minutes))
                    && ((hour_sunset > hour || (hour_sunset == hour && minutes_sunset >= minutes)))) {
                isDay = true;
            } else {
                isDay = false;
            }
        } catch (Exception e) {
        }
        return isDay;
    }

    /* 获取天气类型 解析代码 */
    public static String getSmartWeatherByNum(Context context, String strIcon) {
        int sunnyNum = SkinResource.getSkinResourceId("weather_num_100", "string");
        if (strIcon == null)
            return "";
        if ("00".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_00", "string");// 晴天
        if ("01".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_01", "string");// 多云
        if ("02".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_02", "string");// 阴天
        if ("03".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_03", "string");// 阵雨
        if ("04".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_04", "string");// 雷阵雨
        if ("05".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_05", "string");// 雷阵雨伴有冰雹
        if ("06".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_06", "string");// 雨夹雪
        if ("07".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_07", "string");// 小雨
        if ("08".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_08", "string");// 中雨
        if ("09".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_09", "string");// 大雨
        if ("10".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_10", "string");// 暴雨
        if ("11".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_11", "string");// 大暴雨
        if ("12".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_12", "string");// 特大暴雨
        if ("13".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_13", "string");// 阵雪
        if ("14".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_14", "string");// 小雪
        if ("15".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_15", "string");// 中雪
        if ("16".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_16", "string");// 大雪
        if ("17".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_17", "string");// 暴雪
        if ("18".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_18", "string");// 雾
        if ("19".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_19", "string");// 冻雨
        if ("20".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_20", "string");// 沙尘暴
        if ("21".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_21", "string");// 小雨-中雨
        if ("22".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_22", "string");// 中雨-大雨
        if ("23".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_23", "string");// 大雨-暴雨
        if ("24".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_24", "string");// 暴雨-大暴雨
        if ("25".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_25", "string");// 大暴雨-特大暴雨
        if ("26".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_26", "string");// 小雪-中雪
        if ("27".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_27", "string");// 中雪-大雪
        if ("28".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_28", "string");// 大雪-暴雪
        if ("29".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_29", "string");// 浮尘
        if ("30".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_30", "string");// 扬沙
        if ("31".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_31", "string");// 强沙尘暴
        if ("53".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_53", "string");// 霾
        if ("99".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_99", "string");// 无
        //（20161223更新，下面为新增天气，因没有来得及更新所有皮肤包里的数据所以使用类似String代替）
        if ("32".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_18", "string");// 浓雾
        if ("49".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_18", "string");// 强浓雾
        if ("54".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_53", "string");// 中度霾
        if ("55".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_53", "string");// 重度霾
        if ("56".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_53", "string");// 严重霾
        if ("57".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_18", "string");// 大雾
        if ("58".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_18", "string");// 特强浓雾
        if ("301".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_07", "string");// 雨
        if ("302".equals(strIcon))
            sunnyNum = SkinResource.getSkinResourceId("weather_num_14", "string");// 雪
        return SkinResource.getSkinContext().getResources().getString(sunnyNum);
    }

    /* 获取天气小图片 解析天气代码 **/
    private static int parseSmartIcon(String strIcon) {
        if (strIcon == null)
            return SkinResource.getSkinDrawableIdByName("weather_sunny_d");
        if ("00".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_sunny_d");// 晴天
        if ("01".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_cloudy_d");// 多云
        if ("02".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_overcast");// 阴天
        if ("03".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_shower_d");// 阵雨
        if ("04".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_thundershower");// 雷阵雨
        if ("05".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_thundershower_with_hail");// 冰雹
        if ("06".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_sleet");// 雨夹雪
        if ("07".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_rain");// 小雨
        if ("08".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_moderate_rain");// 中雨
        if ("09".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_rain");// 大雨
        if ("10".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_storm");// 暴雨
        if ("11".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_storm");// 大暴雨
        if ("12".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_severe_storm");// 特大暴雨
        if ("13".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_snow_flurry_d");// 阵雪
        if ("14".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_snow");// 小雪
        if ("15".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_moderate_snow");// 中雪
        if ("16".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_snow");// 大雪
        if ("17".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_snow_storm");// 暴雪
        if ("18".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 雾
        if ("19".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_ice_rain");// 冻雨
        if ("20".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_duststorm");// 沙尘暴
        if ("21".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_to_moderate_rain");// 小雨-中雨
        if ("22".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_moderate_to_heavy_rain");// 中雨-大雨severe_thunderstorms_d
        if ("23".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_rain_to_storm");// 大雨-暴雨
        if ("24".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_storm_to_heavy_storm");// 暴雨-大暴雨
        if ("25".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_to_severe_storm");// 大暴雨-特大暴雨
        if ("26".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_to_moderate_snow");// 小雪-中雪
        if ("27".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_moderate_to_heavy_snow");// 中雪-大雪
        if ("28".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_snow_to_snowstorm");// 大雪-暴雪weather_heavy_snow_d
        if ("29".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_dust");// 浮尘
        if ("30".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_sand");// 扬沙
        if ("31".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_sandstorm");// 强沙尘暴
        if ("53".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_haze");// 霾
        if ("99".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_na");//默认
        //（20161223更新，下面为新增天气，因没有来得及设计新图所以使用类似图代替）
        if ("32".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 浓雾
        if ("49".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 强浓雾
        if ("54".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_haze");// 中度霾
        if ("55".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_haze");// 重度霾
        if ("56".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_haze");// 严重霾
        if ("57".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 大雾
        if ("58".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 特强浓雾
        if ("301".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_rain");// 雨
        if ("302".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_snow");// 雪
        return SkinResource.getSkinDrawableIdByName("weather_na");
    }

    // 20140308 换肤进度到这里。
    public static int parseSmartBgBycode(String strIcon, String sunrise,
                                         String sunset) {
        if (strIcon == null)
            return SkinResource.getSkinDrawableIdByName("weather_sunny_d");
        if ("00".equals(strIcon)) {
            if (isDay(sunrise, sunset)) {
                return SkinResource.getSkinDrawableIdByName("weather_sunny_d");// 晴天(白天)
            }
            return SkinResource.getSkinDrawableIdByName("weather_sunny_n");// 晴天(夜晚)
        }
        if ("01".equals(strIcon)) {
            if (isDay(sunrise, sunset)) {
                return SkinResource.getSkinDrawableIdByName("weather_cloudy_d");// 多云(白天)
            }
            return SkinResource.getSkinDrawableIdByName("weather_cloudy_n");// 多云(夜晚)
        }
        if ("02".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_overcast");// 阴天
        if ("03".equals(strIcon)) {
            if (isDay(sunrise, sunset)) {
                return SkinResource.getSkinDrawableIdByName("weather_shower_d");// 阵雨(白天)
            }
            return SkinResource.getSkinDrawableIdByName("weather_shower_n");// 阵雨(夜晚)
        }
        if ("04".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_thundershower");// 雷阵雨
        if ("05".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_thundershower_with_hail");// 冰雹
        if ("06".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_sleet");// 雨夹雪
        if ("07".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_rain");// 小雨
        if ("08".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_moderate_rain");// 中雨
        if ("09".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_rain");// 大雨
        if ("10".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_storm");// 暴雨
        if ("11".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_storm");// 大暴雨
        if ("12".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_severe_storm");// 特大暴雨
        if ("13".equals(strIcon)) {
            if (isDay(sunrise, sunset)) {
                return SkinResource.getSkinDrawableIdByName("weather_snow_flurry_d");// 阵雪（白天）
            }
            return SkinResource.getSkinDrawableIdByName("weather_snow_flurry_n");// 阵雪（黑夜）
        }
        if ("14".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_snow");// 小雪
        if ("15".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_moderate_snow");// 中雪
        if ("16".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_snow");// 大雪
        if ("17".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_snow_storm");// 暴雪
        if ("18".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 雾
        if ("19".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_ice_rain");// 冻雨
        if ("20".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_duststorm");// 沙尘暴
        if ("21".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_to_moderate_rain");// 小雨-中雨
        if ("22".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_moderate_to_heavy_rain");// 中雨-大雨
        if ("23".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_rain_to_storm");// 大雨-暴雨
        if ("24".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_storm_to_heavy_storm");// 暴雨-大暴雨
        if ("25".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_to_severe_storm");// 大暴雨-特大暴雨
        if ("26".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_to_moderate_snow");// 小雪-中雪
        if ("27".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_moderate_to_heavy_snow");// 中雪-大雪
        if ("28".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_heavy_snow_to_snowstorm");// 大雪-暴雪
        if ("29".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_dust");// 浮尘
        if ("30".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_sand");// 扬沙
        if ("31".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_sandstorm");// 强沙尘暴
        if ("53".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_haze");// 霾
        if ("99".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_sunny_d");//默认
        // （20161223更新，下面为新增天气，因没有来得及设计新图所以使用类似图代替）
        if ("32".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 浓雾
        if ("49".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 强浓雾
        if ("54".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_haze");// 中度霾
        if ("55".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_haze");// 重度霾
        if ("56".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_haze");// 严重霾
        if ("57".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 大雾
        if ("58".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_foggy");// 特强浓雾
        if ("301".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_rain");// 雨
        if ("302".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_light_snow");// 雪
        return SkinResource.getSkinDrawableIdByName("weather_sunny_d");
    }

    //天气背景
    /*public static int parseWeatherBgBycode(String strIcon, String sunrise,
                                           String sunset) {
        if (strIcon == null)
            return SkinResource.getSkinDrawableIdByName("weather_bg_sunny_d");
        if ("00".equals(strIcon)) {
            if (isDay(sunrise, sunset)) {
                return SkinResource.getSkinDrawableIdByName("weather_bg_sunny_d");// 晴天(白天)
            }
            return SkinResource.getSkinDrawableIdByName("weather_bg_sunny_n");// 晴天(夜晚)
        }
        if ("01".equals(strIcon)) {
            if (isDay(sunrise, sunset)) {
                return SkinResource.getSkinDrawableIdByName("weather_bg_cloudy_d");// 多云(白天)
            }
            return SkinResource.getSkinDrawableIdByName("weather_bg_cloudy_n");// 多云(夜晚)
        }
        if ("02".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_overcast");// 阴天
        if ("03".equals(strIcon)) {
            if (isDay(sunrise, sunset)) {
                return SkinResource.getSkinDrawableIdByName("weather_bg_shower_d");// 阵雨(白天)
            }
            return SkinResource.getSkinDrawableIdByName("weather_bg_shower_n");// 阵雨(夜晚)
        }
        if ("04".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_thundershower");// 雷阵雨
        if ("05".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_thundershower_with_hail");// 冰雹
        if ("06".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_sleet");// 雨夹雪
        if ("07".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_light_rain");// 小雨
        if ("08".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_moderate_rain");// 中雨
        if ("09".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_heavy_rain");// 大雨
        if ("10".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_storm");// 暴雨
        if ("11".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_heavy_storm");// 大暴雨
        if ("12".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_severe_storm");// 特大暴雨
        if ("13".equals(strIcon)) {
            if (isDay(sunrise, sunset)) {
                return SkinResource.getSkinDrawableIdByName("weather_bg_snow_flurry_d");// 阵雪（白天）
            }
            return SkinResource.getSkinDrawableIdByName("weather_bg_snow_flurry_n");// 阵雪（黑夜）
        }
        if ("14".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_light_snow");// 小雪
        if ("15".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_moderate_snow");// 中雪
        if ("16".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_heavy_snow");// 大雪
        if ("17".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_snow_storm");// 暴雪
        if ("18".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_foggy");// 雾
        if ("19".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_ice_rain");// 冻雨
        if ("20".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_duststorm");// 沙尘暴
        if ("21".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_light_to_moderate_rain");// 小雨-中雨
        if ("22".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_moderate_to_heavy_rain");// 中雨-大雨
        if ("23".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_heavy_rain_to_storm");// 大雨-暴雨
        if ("24".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_storm_to_heavy_storm");// 暴雨-大暴雨
        if ("25".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_heavy_to_severe_storm");// 大暴雨-特大暴雨
        if ("26".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_light_to_moderate_snow");// 小雪-中雪
        if ("27".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_moderate_to_heavy_snow");// 中雪-大雪
        if ("28".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_heavy_snow_to_snowstorm");// 大雪-暴雪
        if ("29".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_dust");// 浮尘
        if ("30".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_sand");// 扬沙
        if ("31".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_sandstorm");// 强沙尘暴
        if ("53".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_haze");// 霾
        if ("99".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_sunny_d");//默认
        //（20161223更新，下面为新增天气，因没有来得及设计新图所以使用类似图代替）
        if ("32".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_foggy");// 浓雾
        if ("49".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_foggy");// 强浓雾
        if ("54".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_haze");// 中度霾
        if ("55".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_haze");// 重度霾
        if ("56".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_haze");// 严重霾
        if ("57".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_foggy");// 大雾
        if ("58".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_foggy");// 特强浓雾
        if ("301".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_light_rain");// 雨
        if ("302".equals(strIcon))
            return SkinResource.getSkinDrawableIdByName("weather_bg_light_snow");// 雪
        return SkinResource.getSkinDrawableIdByName("weather_bg_sunny_d");
    }*/

    public static Bitmap getBitmap(int color, BitmapDrawable bitmapDrawable1) {
        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
        Bitmap mybaseBitmap = Bitmap.createBitmap(bitmap1.getWidth(),
                bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas1 = new Canvas(mybaseBitmap);
        Paint paint1 = new Paint();
        paint1.setColor(color);
        canvas1.drawRect(0, 0, bitmap1.getWidth(), bitmap1.getHeight(), paint1);
        canvas1.drawBitmap(mybaseBitmap, 0, 0, paint1);

        Bitmap baseBitmap = Bitmap.createBitmap(bitmap1.getWidth(),
                bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(baseBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(mybaseBitmap, 0, 0, paint);
        // 显示图片交集下面那个图片
        paint.setXfermode(new PorterDuffXfermode(
                android.graphics.PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmap1, 0, 0, paint);
        paint.setXfermode(null);
        //canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.save();
        canvas.restore();
        return baseBitmap;
    }

}
