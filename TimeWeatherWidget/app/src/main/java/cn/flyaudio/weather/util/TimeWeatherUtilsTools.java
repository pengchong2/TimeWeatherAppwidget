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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.view.SkinResource;

/**
 * @author:
 * @company:flyaudio
 * @version:2.0
 * @createdDate:20160531
 */
public class TimeWeatherUtilsTools {
    private final static String TAG = Constant.TAG;
    private final static Boolean DEBUG_FLAG = Constant.DEBUG_FLAG;
    private static int i = 0;
    private static int i2 = 0;


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
                Log.e("mForecastyweathers1", "---" + mForecastyweathers.getCity()
                        + "  --Low()" + mForecastyweathers.yweathers[0].getLow());
                break;
            case 2:
                mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                        context.getSharedPreferences("SecondWeather", 0), 2);
                Log.e("mForecastyweathers2", "---" + mForecastyweathers.getCity()
                        + "  --Low()" + mForecastyweathers.yweathers[0].getLow());
//                if (TextUtils.isEmpty(mForecastyweathers.yweathers[0].getLow())
//                        || mForecastyweathers.yweathers[0].getLow().equals(" ")) {
                    Log.e("mForecastyweathers2", "--数据为空的-");
                    //解决界面刷新和网络请求不同步问题
/*
                    while (TextUtils.isEmpty(mForecastyweathers.yweathers[0].getLow())
                            || TextUtils.isEmpty(mForecastyweathers.getCondition_temp())
                            ||TextUtils.isEmpty(mForecastyweathers.getCondition_code())
                            ||mForecastyweathers.yweathers[0].getLow().equals(" ")
                            ||mForecastyweathers.getCondition_temp().equals(" ")||
                    mForecastyweathers.getCondition_code().equals(" ")
                            ) {

                            mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                                    context.getSharedPreferences("SecondWeather", 0), 2);
                            i++;

                        if (i >= 20000) {
                            break;
                        }
                    }*/
                    Log.e("mForecastyweathers2", "--数据为空的-=="+i);

                    //发广播
//                Intent intent =new Intent(Constant.ACTION_UPDATE_DATA);
//                intent.putExtra("aaa","aaa");
//                context.sendBroadcast(intent);
//                }
                break;
            case 3:
                mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                        context.getSharedPreferences("ThirdWeather", 0), 3);
                Log.e("mForecastyweathers3", "---" + mForecastyweathers.getCity()
                        + "  --Low()" + mForecastyweathers.yweathers[0].getLow());

                /*while (TextUtils.isEmpty(mForecastyweathers.yweathers[0].getLow())
                        || TextUtils.isEmpty(mForecastyweathers.getCondition_temp())
                        ||TextUtils.isEmpty(mForecastyweathers.getCondition_code())
                        ||mForecastyweathers.yweathers[0].getLow().equals(" ")
                        ||mForecastyweathers.getCondition_temp().equals(" ")||
                        mForecastyweathers.getCondition_code().equals(" ")
                        ) {

                    mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                            context.getSharedPreferences("ThirdWeather", 0), 3);
                    i2++;

                    if (i>=20000||i2 >= 20000) {
                        i=0;
                        break;
                    }
                }*/
                Log.e("mForecastyweathers3", "--数据为空的-=="+i2);
                break;
            case 4:
                mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                        context.getSharedPreferences("FouthWeather", 0), 4);
                Log.e("mForecastyweathers4", "---" + mForecastyweathers.getCity()
                        + "  --Low()" + mForecastyweathers.yweathers[0].getLow());
                int i3 = 0;
               /* while (TextUtils.isEmpty(mForecastyweathers.yweathers[0].getLow())
                        || TextUtils.isEmpty(mForecastyweathers.getCondition_temp())
                        ||TextUtils.isEmpty(mForecastyweathers.getCondition_code())
                        ||mForecastyweathers.yweathers[0].getLow().equals(" ")
                        ||mForecastyweathers.getCondition_temp().equals(" ")||
                        mForecastyweathers.getCondition_code().equals(" ")
                        ) {

                    mForecastyweathers = getWeatherInfoFormSharedPreferences(context,
                            context.getSharedPreferences("FouthWeather", 0), 4);
                    i3++;

                    if (i2>=20000||i3 >= 20000) {
                        i2=0;
                        break;
                    }
                }*/
                Log.e("mForecastyweathers4", "--数据为空的-=="+i3);
                break;
                default:
                    break;
        }

        setDataView(SkinResource.getSkinContext(), v, mForecastyweathers);
    }

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
        mForecastyweathers.setHumidity(shared.getString("humidity", "--"));
        mForecastyweathers.setVisibility(shared.getString("visibility", "--"));

        mForecastyweathers
                .setWinddirection(shared.getString("direction", "-1"));
        mForecastyweathers.setWindspeed(shared.getString("speed", ""));
        mForecastyweathers.setSunrise(shared.getString("sunrise", "--"));
        mForecastyweathers.setSunset(shared.getString("sunset", "--"));
        mForecastyweathers.setDataflag(shared.getBoolean("dataflag", false));
        mForecastyweathers.setCity(shared.getString("city",
                getCityNameWithIndex(context, index)));
        mForecastyweathers.setCityPinyin(shared.getString("city",
                getCityPinyinNameWithIndex(context, index)));

        mForecastyweathers.setFeelslike(shared.getString("feelslike", "--"));
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
       /* Log.e("AAAAA", "cityname" + mForecastyweathers.getCity() + "--Low()" + mForecastyweathers.yweathers[0].getLow()
                + "--High()" + mForecastyweathers.yweathers[0].getHigh()
                + "--Condition_temp()" + mForecastyweathers.getCondition_temp());*/

       if (mForecastyweathers!=null) {

        if (WeatherWidgetApplication.isCNLanguage) {
            setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("weather_time_cityname", "id")),
                    mForecastyweathers.getCity());
        } else {
            String cityName = mForecastyweathers.getCityPinyin();
            setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("weather_time_cityname", "id")),
                    WeatherWidgetApplication.toUpperCaseFirstOne(cityName));
        }

        if (mForecastyweathers.getDataflag()) {


            setTextView((TextView) v.findViewById(SkinResource.getSkinResourceId("weather_time_temp", "id")),
                    mForecastyweathers.getCondition_temp());
           /* if (isDay(mForecastyweathers.getSunrise(), mForecastyweathers.getSunset())) {
                setTextView(
                        (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_cur_weather_condition", "id")),
                        getSmartWeatherByNum(context, mForecastyweathers.getCondition_code()));
            } else {
                setTextView(
                        (TextView) v.findViewById(SkinResource.getSkinResourceId("txt_cur_weather_condition", "id")),
                        getSmartWeatherByNum(context, mForecastyweathers.getCondition_code()));
            }*/

            TextView curLowTemperature = (TextView) v.findViewById(SkinResource.getSkinResourceId("weather_time_low_temperature", "id"));
            TextView curHightTemperature = (TextView) v.findViewById(SkinResource.getSkinResourceId("weather_time_hight_temperature", "id"));
            curLowTemperature.setText(mForecastyweathers.yweathers[0].getLow());
            curHightTemperature.setText(mForecastyweathers.yweathers[0].getHigh());

        }

        //图标和背景分离时修改：set改为img_weather_icon（新增的图标id）
        ImageView img = (ImageView) v.findViewById(SkinResource.getSkinResourceId("weather_time_weather_icon", "id"));

        img.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                parseSmartBgBycode(mForecastyweathers.getCondition_code(),
                        mForecastyweathers.getSunrise(), mForecastyweathers.getSunset())));

        TextView txtWindSpeed = (TextView) v.findViewById(SkinResource.getSkinResourceId("weather_time_windSpeed", "id"));
        TextView txtWindDirection = (TextView) v.findViewById(SkinResource.getSkinResourceId("weather_time_windDirection", "id"));

        if (mForecastyweathers.getWinddirection().equals("") || mForecastyweathers.getWinddirection().isEmpty()
                ) {
            Log.e("AAA0", "刷新数据了-" + mForecastyweathers.getWinddirection().isEmpty());
            context.sendBroadcast(new Intent(Constant.ACTION_UPDATE_DATA));
            Log.e("AAA", "刷新数据了");
        }
        Log.e("AAA2", "刷新数据了--" + mForecastyweathers.getWinddirection() + "=");
        txtWindSpeed.setText(getSmartWindSpeed(SkinResource.getSkinContext(), mForecastyweathers.getWindspeed()));
        txtWindDirection.setText(getSmartWindDirection(SkinResource.getSkinContext(), mForecastyweathers.getWinddirection()));

       }
    }


    private static void setTextView(TextView textView, String text) {
        textView.setText(text);
    }


    public static Bitmap getWeatherIcon(Context context, String pos) {

        Bitmap bm = BitmapFactory.decodeResource(
                context.getResources(),
                TimeWeatherUtilsTools.parseSmartIcon(pos));
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


    public static String getSmartWindSpeed(Context context, String value) {
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

    public static String getSmartWindDirection(Context context, String value) {
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

}
