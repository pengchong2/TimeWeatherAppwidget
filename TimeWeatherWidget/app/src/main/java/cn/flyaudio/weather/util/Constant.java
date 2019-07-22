/**
 * 
 */
package cn.flyaudio.weather.util;


/**
 * @author:zengyuke
 * @company:flyaudio
 * @version:1.0
 * @createdDate:2014-5-5下午3:18:46
 */
public class Constant {
	public static final String TAG ="weather";
	public static final Boolean DEBUG_FLAG = false;
	
	//For yahoo weather .........................begin
	// Action for updateUI 
	public static final String  ACTION_UPDATEUI="cn.flyaudio.action.UPDATEUI_";
	public static final String  ACTION_STOP_FRESH="cn.flyaudio.action.STOP_FRESH";
	public static final String  ACTION_START_FRESH="cn.flyaudio.action.START_FRESH";
	public static final String  ACTION_UPDATEUI_VIEWFLOW="cn.flyaudio.action.UPDATEUI_VIEWFLOW";
	public static final String  ACTION_REQUEST_WEATHER="cn.flyaudio.weater.RequestWeather";
//	public static final String  REQUEST_URL="http://120.77.72.35/FlyWeatherServer/getWeatherInfo.action";
    public static final String  REQUEST_URL="http://api.flyaudio.name:8080/FlyWeatherServer/getWeatherInfo.action";
	
	
	// Action UPDATE_ADAPTER
	public static final String  ACTION_UPDATE_ADAPTER="cn.flyaudio.ACTION.UPDATE_ADAPTER";
	
	//Action ACTION_APPWIDGET_UPDATE
	public static final String  ACTION_APPWIDGET_UPDATE="android.appwidget.action.APPWIDGET_UPDATE";
	
	//
	public static final String  ACTION_UPDATE_ALL="com.android.flyaudio.weather.UPDATE_ALL";

	// default city code  "广州--2161838"
	public static final String  DEFAULT_CITYCODE="101280101";
	
	// Action for  LaunchHome
	public static final String  ACTION_HOME="android.intent.action.HOME";
	
	public static final String PROPERTY_COLORTHEME ="persist.fly.colortheme";

	//日夜模式
	public static final String NIGHT_MODE="MODE_NIGHT";
	public static  String CURREN_DAYNIGHT_MODE="night";
	public static  String DAY_MODE="MODE_DAY";

	public static final String  ACTION_UPDATE_DATA="cn.flyaudio.ACTION.UPDATE_DATA";
	public static final String  ACTION_UPITEMUI="cn.flyaudio.action.UPDATEUI_ITEM";
}
