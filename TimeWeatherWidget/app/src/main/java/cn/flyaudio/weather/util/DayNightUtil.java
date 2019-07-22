package cn.flyaudio.weather.util;

import android.util.Log;

/**
 * Created by necorchen on 17-11-23.
 */

public class DayNightUtil {

    public static ReflashUI reflashListener;
    public static ReflashUI2 reflashListener2;

    public void setDayNightMode(String m){
        if(reflashListener!=null){
            reflashListener.showUI(m);
        }
        if(reflashListener2!=null){
            reflashListener2.showUI2(m);
        }
        Log.e("abc","----"+(reflashListener2!=null));
    }



    public static void setDayNightModeListener(ReflashUI listener){
        reflashListener=listener;
    }
    public static void setDayNightModeListener2(ReflashUI2 listener2){
        reflashListener2=listener2;
    }

    public interface ReflashUI{
        void showUI(String mode);
    }

    public interface ReflashUI2{
        void showUI2(String mode);
    }

}
