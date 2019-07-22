package cn.flyaudio.time;

import android.os.SystemProperties;

import cn.flyaudio.weather.view.SkinResource;

public class DayAndNightModeUtil {
	
	public static String ACTION_DAY_AND_NIGHT_MODE = "FLY.ANDROID.NAVI.MSG.SENDER";
	public static String DAY_AND_NIGHT_MODE_BROADCAST_KEY = "FLY_DAYNIGHT_MODE";
	public static String DAY_NIGHT_MODE_DYA = "MODE_DAY";
	public static String DAY_NITHT_MODE_NIGHT = "MODE_NIGHT";
	public static final String DAY_NIGHT_MODE_PROPETY_KEY = "fly.android.navi.daynightmode";

	public static String getDayNightMode() {
		return SystemProperties.get(DAY_NIGHT_MODE_PROPETY_KEY,"");
	}
	
	public static boolean isNightMode() {
		return "night".equals(getDayNightMode()) && isSupportDayAndNightMode();
	}
	
	public static boolean isSupportDayAndNightMode() {
		return "support".equals(SkinResource.getSkinStringByName("skin_support_day_night_mode"));
	}
}
