package cn.flyaudio.weather.data;

import java.io.InputStream;

import cn.flyaudio.Weather.Flog;
import cn.flyaudio.weather.service.WeatherService;
import cn.flyaudio.weather.util.Constant;
import cn.flyaudio.weather.view.SkinResource;
import cn.flyaudio.weather.xml.FlyProperty;
import cn.flyaudio.weather.xml.PropertyXMLParaser;
import cn.flyaudio.weather.xml.XMLTool;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * @company:flyaudio
 * @version:1.0
 */
public class WeatherWidgetApplication extends Application {

	private boolean isServiceRunning = true;
	public static Boolean isCNLanguage = true;
	private static Context mContext;
	private static FlyProperty mFlyProperty;
	private static final String SKIN_SMALL="cn.flyaudio.weather.skin";
	private static final String SKIN_BIG="com.flyaudio.flyaudioskinproj";

	public static Context shareContext = null;
	public static Resources shareResources = null;
	String sharePackageName = null;
	private final static String RESPACKAGENAME = "com.flyaudio.flyaudioskinproj";
	public static Context application;
	public boolean flag = false;

	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		//韩国现代天气的皮肤包包名cn.flyaudio.weather.skin
		//G9所有皮肤合为一个皮肤的皮肤包名com.flyaudio.flyaudioskinproj
		SkinResource.initSkinResource(mContext, SKIN_BIG);
		initFlyProperty();
		getlanguage();
		setDefaultWeather();
		super.onCreate();

//		Toast.makeText(this,"xifei-----",Toast.LENGTH_SHORT).show();

		application = getApplicationContext();
		try {
			shareContext = WeatherWidgetApplication.this.createPackageContext(SKIN_BIG, Context.CONTEXT_IGNORE_SECURITY);
			Log.d("Flyaudio3_TimeService", "TimeWidgetApplication-->shareContext = "+shareContext.getPackageName());
			shareResources = shareContext.getResources();
			sharePackageName = shareContext.getPackageName();
			flag = true;

		} catch (PackageManager.NameNotFoundException e) {
			Log.d("Flyaudio3_TimeService", "cn.flyaudio.timeres资源包出错或未找到，将读取本地资源。。。。。错误信息：" + e.toString());
			shareContext = this;
			shareResources = getResources();
			sharePackageName = getPackageName();
			flag = false;
		}
		if(shareContext==null){
			Log.d("Flyaudio3_TimeService", "cn.flyaudio.timeres资源包出错或未找到，将读取本地资源。。。。。错误信息：");
			shareContext = this;
			shareResources = getResources();
			sharePackageName = getPackageName();
			flag = false;
		}
//		SkinResource.initSkinResource(this, RESPACKAGENAME);
//		initFlyProperty();
		WeatherService.enableColorTheme =getEnableColorTheme();

		Log.d("Flyaudio3_TimeService", "TimeWidgetApplication------>onCreate");

	}




	public int getResId(String name, String type) {
		if (shareContext!=null && flag ) {
			int id = shareResources.getIdentifier(name, type, sharePackageName);
			if (id != 0) {
				return id;
			} else {
				return getResources().getIdentifier(name, type, getPackageName());
			}
		} else {
			return getResources().getIdentifier(name, type, getPackageName());
		}
	}

	public Context getShareContext() {
		return shareContext;
	}

	public void setShareContext(Context shareContext) {
		this.shareContext = shareContext;
	}

	public Resources getShareResources() {
		return shareResources;
	}

	public void setShareResources(Resources shareResources) {
		this.shareResources = shareResources;
	}

	public String getSharePackageName() {
		return sharePackageName;
	}

	public void setSharePackageName(String sharePackageName) {
		this.sharePackageName = sharePackageName;
	}

//	private FlyProperty mFlyProperty;

	public FlyProperty getFlyProperty() {
		return mFlyProperty;
	}


	private InputStream getSkinRawInputStream(String name) {
		int rawId = getResId(name, "raw");
		if (rawId == 0)
			return null;
		try {
			if (shareContext!=null && flag ) {
				return shareResources.openRawResource(rawId);
			} else {
				return getResources().openRawResource(rawId);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public Typeface getTypeface(){
		Typeface typeface = null;
		String path = "";
		if (getFlyProperty() != null) {
			path = getFlyProperty().getStringValue("fontPath");
		}
		try {
			if (shareContext!=null && flag ) {
				typeface = Typeface.createFromAsset(shareResources.getAssets(), path);
			}else {
				typeface = Typeface.createFromAsset(getAssets(), path);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("TimeService", "getTypeface Exception = " + e.toString());
		}
		return typeface;
	}




	public static Context getContext(){
		return mContext;
	}
	public static void getlanguage() {

		Resources resources = mContext.getResources();
		Configuration config = resources.getConfiguration();
		String mlang = config.locale.toString();
		Log.d("zzz", "mlang = " + mlang);

		if (!mlang.endsWith("zh_CN")) {
			isCNLanguage = false;
		} else {
			isCNLanguage = true;
		}
	}

	private void setDefaultWeather() {
		SharedPreferences shared = mContext.getSharedPreferences("weather", 0);
		SharedPreferences.Editor editor = shared.edit();
		int currentCityNum = shared.getInt("current", 1);
		String currentCityName = "city" + String.valueOf(currentCityNum);
		String city = shared.getString(currentCityName, "");

		if (city.equals("") || city == null) {
			editor.putString("city1", Constant.DEFAULT_CITYCODE);
			editor.putString("1", SkinResource.getSkinStringByName("guangzhou"));
			editor.putString("10",
					SkinResource.getSkinStringByName("guangzhou_pinyin"));
			editor.putInt("current", 1);
			editor.commit();
		}
		Flog.d("setDefaultWeather=====data=="
				+ shared.getString(currentCityName, "") + ":"
				+ shared.getString("1", ""));
	}

	public boolean isServiceRunning() {
		return isServiceRunning;
	}

	public void setServiceRunning(boolean isServiceRunning) {
		this.isServiceRunning = isServiceRunning;
	}

	private void initFlyProperty() {
		InputStream inputStream = SkinResource
				.getSkinRawInputStream("properties");
		if (inputStream != null) {
			PropertyXMLParaser propertyXMLParaser = new PropertyXMLParaser();
			propertyXMLParaser = (PropertyXMLParaser) XMLTool.parse(
					inputStream, propertyXMLParaser);
			mFlyProperty = propertyXMLParaser.getProperties();
		}
	}

	// 皮肤是否支持改变颜色
	public static boolean getEnableColorTheme() {
		Flog.d("getEnableColorTheme="+mFlyProperty.getBoolenValue("enableColorTheme"));
		return mFlyProperty.getBoolenValue("enableColorTheme");
	}
	
	//首字母转大写
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
