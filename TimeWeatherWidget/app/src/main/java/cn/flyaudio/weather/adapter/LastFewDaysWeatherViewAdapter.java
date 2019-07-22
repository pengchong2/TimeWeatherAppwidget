package cn.flyaudio.weather.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.util.Constant;
import cn.flyaudio.weather.util.TimeWeatherUtilsTools;
import cn.flyaudio.weather.util.UtilsTools;
import cn.flyaudio.weather.view.SkinResource;

public class LastFewDaysWeatherViewAdapter extends BaseAdapter {

    private final static int VIEW_SIZE = 3;
    private FullWeatherInfo fullWeatherInfo;
    private Context context;

    public LastFewDaysWeatherViewAdapter(Context context, FullWeatherInfo info) {
        this.fullWeatherInfo = info;
        this.context = context;
    }

    @Override
    public int getCount() {
        return VIEW_SIZE;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String mWeek = UtilsTools.getWeekName(context, fullWeatherInfo.yweathers[position].getDay(), position);
        String mDate = UtilsTools.getTimeShort(fullWeatherInfo.yweathers[position].getDate(), position, context);
        String mLowTemperature = fullWeatherInfo.yweathers[position].getLow();
        String mHightTemperature = fullWeatherInfo.yweathers[position].getHigh();
        String mWertherCondition = UtilsTools.getSmartWeatherByNum(context, fullWeatherInfo.yweathers[position].getCode());
        Bitmap mWeatherIcon = UtilsTools.getWeatherIcon(context, fullWeatherInfo.yweathers[position].getCode());

        if (mLowTemperature.equals(null) || mLowTemperature.equals("")) {
            mLowTemperature = SkinResource.getSkinStringByName("no_temp_data");
        }
        if (mHightTemperature.equals(null) || mHightTemperature.equals("")) {
            mHightTemperature = SkinResource.getSkinStringByName("no_temp_data");
        }

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);

            convertView = inflater.inflate(SkinResource.getSkinLayoutIdByName("last_few_days_weather_item"), parent, false);


            TextView txtDate = (TextView) convertView.findViewById(SkinResource.getSkinResourceId("txt_date", "id"));
            TextView txtWeek = (TextView) convertView.findViewById(SkinResource.getSkinResourceId("txt_week", "id"));
            TextView txtLowTemperature = (TextView) convertView.findViewById(SkinResource.getSkinResourceId("txt_low_temperature", "id"));
            TextView txtHightTemperature = (TextView) convertView.findViewById(SkinResource.getSkinResourceId("txt_hight_temperature", "id"));
            TextView txtWeatherCondition = (TextView) convertView.findViewById(SkinResource.getSkinResourceId("txt_weather_condition", "id"));
            ImageView imgWeatherConditionIcon = (ImageView) convertView.findViewById(SkinResource.getSkinResourceId("img_weather_smart_icon", "id"));
            //风向风速
			TextView windDirection = (TextView) convertView.findViewById(SkinResource.getSkinResourceId("weather_time_detail_windDirection", "id"));
			TextView windSpeed = (TextView) convertView.findViewById(SkinResource.getSkinResourceId("weather_time_detail_windSpeed", "id"));


            txtWeatherCondition.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    SkinResource.getSkinContext().getResources().getDimension(SkinResource.getSkinResourceId("normal_text_size", "dimen")));
            if (WeatherWidgetApplication.isCNLanguage == true) {
                if (mWertherCondition.length() > 4) {
                    txtWeatherCondition.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            SkinResource.getSkinContext().getResources().getDimension(SkinResource.getSkinResourceId("small_text_size", "dimen")));
                }
            } else {
                if (mWertherCondition.length() > 9) {
                    txtWeatherCondition.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            SkinResource.getSkinContext().getResources().getDimension(SkinResource.getSkinResourceId("small_text_size", "dimen")));
                }
            }

            txtDate.setText(mDate);
            txtWeek.setText(mWeek);
            txtHightTemperature.setText(mHightTemperature);
            txtLowTemperature.setText(mLowTemperature);
            txtWeatherCondition.setText(mWertherCondition);
            imgWeatherConditionIcon.setImageBitmap(mWeatherIcon);

			windSpeed.setText(TimeWeatherUtilsTools.getSmartWindSpeed(SkinResource.getSkinContext(), fullWeatherInfo.getWindspeed()));
			windDirection.setText(TimeWeatherUtilsTools.getSmartWindDirection(SkinResource.getSkinContext(), fullWeatherInfo.getWinddirection()));




        }



        return convertView;
    }


    private static class ViewHolder {
        TextView txtDate;
        TextView txtWeek;
        TextView txtLowTemperature;
        TextView txtHightTemperature;
        TextView txtWeatherCondition;
        ImageView imgWeatherConditionIcon;
        TextView windDirection;
        TextView windSpeed;
    }
}
