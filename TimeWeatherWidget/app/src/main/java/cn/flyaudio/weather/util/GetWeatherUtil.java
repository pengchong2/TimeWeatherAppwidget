package cn.flyaudio.weather.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.objectInfo.WeatherInfo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by lan on 16-12-22.
 */

public class GetWeatherUtil {

    private final static String TAG = "GetWeatherUtil";

    private final static String RETURN_CODE = "return_code";//服务区返回码
    private final static String RETURN_MSG = "return_msg";//返回的信息
    private final static String RETURN_HIG = "highestTemperature";//返回当天最高温度
    private final static String RETURN_LOW = "lowestTemperature";//返回当天最低温度
    private final static String CURRENT_TEMP = "temperature";//当前温度
    private final static String CURRENT_DATA_DATE2 = "dateTimeOfCurWeather";//当前时间段
    private final static String WIND_DIRECTION2 = "windDirection";//风向
    private final static String WIND_SPEED2 = "windSpeed";//风速
    private final static String WEATHER_PHENOMENA2 = "weatherPhenomena";//天气现象编码
    private final static String WEATHER_FORECAST = "mWeatherForecast";//天气现象编码

    private final static String APP_KEY = "flyaudioWeather";

    private static GetWeatherUtil mWeatherUtil = null;
    public static OkHttpClient okHttpClient = null;
    private String weatherUrl = null;

    private List<WeatherInfo> weatherDataList = null;
    private List<String> temperatureList = null;

    GetWeather_SQL getWeatherSql = new GetWeather_SQL();


    /**
     * 自1970年1月1日 0点0分0秒以来的秒数
     * 毫秒级，需要转换成秒(10位数字)
     */
    public static String getCurrentMinTime() {
        String time = "";
        try {
            long l = System.currentTimeMillis();

            long mtime = (long) (l / 1000); //mtime 为秒

            time = String.valueOf(mtime);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public String getMD5Nonce(String stamp) {
        if (TextUtils.isEmpty(stamp)) {
//            log.error("getMD5Nonce---stamp="+stamp);
            return null;
        }
        StringBuffer string1 = new StringBuffer();
        string1.append(APP_KEY);

        string1.append("||");
        string1.append(stamp);
        String signature = "";

        String stringA = string1.toString();
//        log.info("getMD5Nonce---stringA="+stringA);
        try {

            MessageDigest crypt = MessageDigest.getInstance("MD5");
            crypt.reset();
            crypt.update(stringA.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return signature;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }

    public static GetWeatherUtil Instance() {
        if (mWeatherUtil == null) {
            synchronized (GetWeatherUtil.class) {
                mWeatherUtil = new GetWeatherUtil();
            }
        }
        return mWeatherUtil;
    }

    public List<FullWeatherInfo> getCitiesWeatherInfo(ArrayList<String> citiesNum) {
        return getAllWeatherContent(citiesNum);
    }

    private List<FullWeatherInfo> getAllWeatherContent(ArrayList<String> citiesNum) {

        String returnCode = null;
        String returnMsg = null;
        String cityId = null;
        String weatherMsg = null;
        String weatherForecast = null;
        JSONObject returnData;

        SimpleDateFormat sdf = null;
        String preDay = null;
        String curDay = null;
        String windSpeed = null;
        String windDirection = null;
        String weatherPhenomena = null;
        String temperature = null;
        String dateTimeOfCurWeather = null;
        String strTimeFormat_12_24 = null;
        String hourOfDay;
        int curHourOfDay = 0;
        int dayFlag = 0;
        String highestTemperature = null;
        String lowestTemperature = null;
        ArrayList<String> cityNumToCityId = null;
        //获取当前事件戳
        String mtime = getCurrentMinTime();
        //进行MD5加密
        String md5String = getMD5Nonce(mtime);

        int citiesCount = citiesNum.size();
        Log.d(TAG, "-------citiesNum.toString()=--------" + citiesNum.toString() );
        if (citiesCount == 0) return null;
        cityNumToCityId = getWeatherSql.queryCitiseId(citiesNum);
        //添加城市id
        StringBuffer sb = new StringBuffer();
        sb.append("area=");
        for (int i = 0; i < citiesCount - 1; i++) {
            sb.append(cityNumToCityId.get(i));
            sb.append("|");
        }
        sb.append(cityNumToCityId.get(citiesCount - 1));
        sb.append("&");
        sb.append("stamp=" + mtime);
        sb.append("&");
        sb.append("nonce=" + md5String);
        Log.d(TAG, "-------stamp=--------" + mtime + "  -----nonce=------" + md5String);
        Log.d(TAG, "-------StringBuffer=--------" + sb.toString());
//        RequestBody body = RequestBody.create(MediaType.parse("charset=utf-8"),"x");
        RequestBody body = RequestBody.create(MediaType.parse("charset=utf-8"), sb.toString());
        Request request = new Request.Builder().url(Constant.REQUEST_URL).post(body).build();
        FullWeatherInfo latestWeathers = null;
        List<FullWeatherInfo> mFullWeatherInfoList = new ArrayList<FullWeatherInfo>();
        StringBuilder mCitiesNum = new StringBuilder();

        try {
            Response response = getOkHttpClient().newCall(request).execute();
//            Log.d(TAG,"---response--data = " + response.body().string());
            returnData = new JSONObject(new String(response.body().string()));
            Log.d(TAG, "----returnData------" + returnData);
            returnCode = returnData.getString(RETURN_CODE);
            if (returnCode.equals("SUCCESS")) {
                returnMsg = returnData.getString(RETURN_MSG);
                Log.d(TAG, "----returnMsg------" + returnMsg);
                JSONArray array = new JSONArray(returnMsg);
                cityId = array.getString(0);
                Log.d(TAG, "----cityId------" + cityId + "  citiesCount=" + citiesCount);
                JSONObject object = new JSONObject(cityId);

                for (int x = 0; x < citiesCount; x++) {
//                Log.d(TAG, "----cityNumToCityId------" + cityNumToCityId.get(x) +"  object=  "+object);
                    weatherMsg = object.getString(cityNumToCityId.get(x));
                    Log.d(TAG, "----weatherMsg------" + weatherMsg);
                    JSONArray array2 = new JSONArray(weatherMsg);

                    dayFlag = 0;
                    latestWeathers = new FullWeatherInfo();

                    for (int j = 0; j < array2.length(); j++) {
                        JSONObject object2 = array2.getJSONObject(j);
                        weatherForecast = object2.getString(WEATHER_FORECAST);
                        Log.d(TAG, "----weatherForecast------" + weatherForecast);
                        JSONObject object3 = new JSONObject(weatherForecast);

                        weatherPhenomena = object3.getString(WEATHER_PHENOMENA2);
                        windDirection = object3.getString(WIND_DIRECTION2);
                        temperature = object3.getString(CURRENT_TEMP);
                        windSpeed = object3.getString(WIND_SPEED2);
                        highestTemperature = object3.getString(RETURN_HIG);
                        lowestTemperature = object3.getString(RETURN_LOW);
                        dateTimeOfCurWeather = object3.getString(CURRENT_DATA_DATE2);

                        Log.d(TAG, "----weatherPhenomena------" + weatherPhenomena);
                        Log.d(TAG,"----highestTemperature------" + highestTemperature);
                        Log.d(TAG, "----temperature------" + temperature);

                        //截取年月日
                        curDay = dateTimeOfCurWeather.substring(0, 8);
                        hourOfDay = dateTimeOfCurWeather.substring(8, 10);
                        Log.d(TAG, "curDay =" + curDay + "  hourOfDay= " + hourOfDay+ "  preDay= " + preDay);
                        Log.d(TAG, "dayFlag =" + dayFlag );
                        //当前获取的日期和上一次的不同时说明当前获取的是下一天的天气，
                        if (!curDay.equals(preDay) && dayFlag != 0) {
                            dayFlag++;
                            preDay = curDay;
                            Log.d(TAG, "进来了  " + dayFlag );
                        }
                        Log.d(TAG, "dayFlag --" + dayFlag);
                        switch (dayFlag) {
                            case 0:
                                dayFlag++;
                                preDay = curDay;
                                Calendar calendar = Calendar.getInstance();
                                //获取当前的时间(24小时制)
                                curHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                                Log.d(TAG, "dayFlag -00-" + dayFlag);
                            case 1:
                                //获取最接近当前时间的天气情况
//                                if (curHourOfDay <= Integer.valueOf(hourOfDay)) {
//                                curHourOfDay = 24;//只使用时间最接近一次数据（每天最后一个数据是23时）
                                latestWeathers.setWindspeed(windSpeed);
                                latestWeathers.setCondition_temp(temperature);
                                latestWeathers.setWinddirection(windDirection);
                                Log.d(TAG, "lowestTemperature 222=" + lowestTemperature);
                                Log.d(TAG, "getWeatherSql.queryWeatherPhenomena(weatherPhenomena) ="
                                        + getWeatherSql.queryWeatherPhenomena(weatherPhenomena));
                                //将天气现象id改为对应的编码
                                latestWeathers.yweathers[0].setCode(getWeatherSql.queryWeatherPhenomena(weatherPhenomena));
                                latestWeathers.yweathers[0].setLow(lowestTemperature);
                                latestWeathers.yweathers[0].setHigh(highestTemperature);

//                                }

                                break;
                            case 2:
                                if ("14".equals(hourOfDay))//以下午14点的天气现象作为明天的预报天气现象
                                    latestWeathers.yweathers[1].setCode(getWeatherSql.queryWeatherPhenomena(weatherPhenomena));
                                latestWeathers.yweathers[1].setLow(lowestTemperature);
                                latestWeathers.yweathers[1].setHigh(highestTemperature);
                                Log.d(TAG, "lowestTemperature 22=" + lowestTemperature);
                                Log.d(TAG, "getWeatherSql.queryWeatherPhenomena(weatherPhenomena) 22="
                                        + getWeatherSql.queryWeatherPhenomena(weatherPhenomena));
                                break;
                            case 3:
                                if ("14".equals(hourOfDay))//以下午14点的天气现象作为后天的预报天气现象
                                    latestWeathers.yweathers[2].setCode(getWeatherSql.queryWeatherPhenomena(weatherPhenomena));
                                latestWeathers.yweathers[2].setLow(lowestTemperature);
                                latestWeathers.yweathers[2].setHigh(highestTemperature);
                                Log.d(TAG, "lowestTemperature 33=" + lowestTemperature);
                                Log.d(TAG, "getWeatherSql.queryWeatherPhenomena(weatherPhenomena) 33="
                                        + getWeatherSql.queryWeatherPhenomena(weatherPhenomena));
                                break;
                            default:
                                break;
                        }
                    }

                    ContentResolver contentResolver = WeatherWidgetApplication.getContext().getContentResolver();
                    strTimeFormat_12_24 = android.provider.Settings.System.getString(contentResolver,
                            android.provider.Settings.System.TIME_12_24);
                    if ("12".equals(strTimeFormat_12_24)) {
                        //24小时制
                        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    } else {
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    }

                    Date curDate = new Date(System.currentTimeMillis());
                    String curDateTime = sdf.format(curDate);
                    Log.d(TAG, "curDateTime= " + curDateTime);
                    latestWeathers.setCondition_date(curDateTime);
                    latestWeathers.setCondition_code(latestWeathers.yweathers[0].getCode());
                    latestWeathers.setDataflag(true);

                    mFullWeatherInfoList.add(latestWeathers);
                    Log.d(TAG, "mFullWeatherInfoList= " + mFullWeatherInfoList.size());

                }


                //发广播
//                Intent intent =new Intent(Constant.ACTION_UPDATE_DATA);
//                intent.putExtra("aaa","aaa");
//                WeatherWidgetApplication.getContext().sendBroadcast(intent);

            }

            return mFullWeatherInfoList;
        } catch (Exception e) {

        }
        return null;
    }

    private String dateFormatTransform(String dateStr, String fromFormatStr, String toFormatStr) {
        if (TextUtils.isEmpty(dateStr)) return dateStr;

        SimpleDateFormat fromFormat = new SimpleDateFormat(fromFormatStr);
        SimpleDateFormat toFormat = new SimpleDateFormat(toFormatStr);

        Date date = null;
        try {
            date = fromFormat.parse(dateStr);
            return toFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }
}
