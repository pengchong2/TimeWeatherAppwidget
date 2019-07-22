package cn.flyaudio.weather.util;

import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import cn.flyaudio.Weather.WeatherWidget;
import cn.flyaudio.weather.service.WeatherService;
import cn.flyaudio.weather.view.SkinResource;

/**
 * Created by necorchen on 17-11-23.
 */

public class DayNightModeReciver extends BroadcastReceiver {

    private String TAG = "DayNightModeReciver";
    String supportDayNight = SkinResource.getSkinStringByName("skin_support_day_night_mode");
    UiModeManager mUiModeManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "DayNightModeReciver,----------------");
        String mode = intent.getStringExtra("FLY_DAYNIGHT_MODE");
        Log.d(TAG, "DayNightModeReciver,  mode==="+mode);
//        Constant.CURREN_DAYNIGHT_MODE = mode;
//        SystemProperties.set("fly.android.navi.daynightmode", mode);
      /*  if (!TextUtils.isEmpty(supportDayNight) && "support".equals(supportDayNight) && !TextUtils.isEmpty(mode)) {
            if (mode.equals(Constant.DAY_MODE)||mode.equals(Constant.NIGHT_MODE)) {
                //防止服务被杀死,接收不到广播
                context.startService(new Intent(context, WeatherService.class));
                new DayNightUtil().setDayNightMode(mode);

            }
        }*/

        mUiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (!TextUtils.isEmpty(supportDayNight) && "support".equals(supportDayNight) && !TextUtils.isEmpty(mode)) {
            if (mode.equals(Constant.NIGHT_MODE)) {
//                mUiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
                context.sendBroadcast(new Intent(Constant.ACTION_APPWIDGET_UPDATE));
//                context.startService(new Intent(context, WeatherService.class));
            }else if (mode.equals(Constant.DAY_MODE)){
//                context.startService(new Intent(context, WeatherService.class));
//                mUiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
                context.sendBroadcast(new Intent(Constant.ACTION_APPWIDGET_UPDATE));
            }
            Log.d("=====", "--"+ mUiModeManager.getNightMode());

        }

    }
}
