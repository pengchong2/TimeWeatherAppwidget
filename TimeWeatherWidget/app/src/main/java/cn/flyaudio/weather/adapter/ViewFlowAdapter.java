package cn.flyaudio.weather.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.util.Constant;
import cn.flyaudio.weather.util.UtilsTools;
import cn.flyaudio.weather.view.SkinResource;


/**
 * @author:zengyuke
 * @company:flyaudio
 * @version:1.0
 * @createdDate:2014-5-5下午3:16:19
 */
public class ViewFlowAdapter extends BaseAdapter {

    private final String TAG = Constant.TAG;
    private final Boolean DEBUG_FLAG = Constant.DEBUG_FLAG;
    private ArrayList<String> mCityList = new ArrayList<String>();
    private LayoutInflater mInflater;
    private SharedPreferences preference;
    private Context mContext;
    private Activity mActivity;
    private Animation mAnim;
    private Button mFreshButton1;

    public ViewFlowAdapter(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        mInflater = (LayoutInflater) SkinResource.getSkinContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preference = context.getSharedPreferences("weather", 0);
        for (int i = 1; i <= 4; i++) {
            String city = preference.getString(String.valueOf(i), null);
            if (city != null)
                mCityList.add(city);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 4;// WeatherDetails.view_count-1
    }

    @Override
    public int getCount() {
        return mCityList.size();
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
        if (convertView == null) {

            //SystemProperties.get("fly.android.navi.daynightmode", "day")

            convertView = mInflater.inflate(SkinResource.getSkinLayoutIdByName("viewflow_layout"), null);
            //更新于20161223，新接口没有日出日落所以隐藏切换按钮
            //convertView.findViewById(SkinResource.getSkinResourceId("city_button", "id")).setOnClickListener(clickListener);
            convertView.findViewById(SkinResource.getSkinResourceId("city_button", "id")).setVisibility(View.GONE);
            mAnim = AnimationUtils.loadAnimation(SkinResource.getSkinContext(), SkinResource.getSkinResourceId("weather_loading", "anim"));
            mAnim.setInterpolator(new LinearInterpolator());

            UtilsTools.setConvertViewWithShared(mContext, convertView, mCityList.get(position));

        }

        return convertView;
    }



    public String getTitle(int position) {
        return mCityList.get(position);
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            View m = (View) view.getParent().getParent().getParent();
            Animation mAnim_out = AnimationUtils.loadAnimation(SkinResource.getSkinContext(), SkinResource.getSkinResourceId("more_detail_weather_out", "anim"));
            mAnim_out.setInterpolator(new LinearInterpolator());
            Animation mAnim_in = AnimationUtils.loadAnimation(SkinResource.getSkinContext(), SkinResource.getSkinResourceId("more_detail_weather_in", "anim"));
            mAnim_in.setInterpolator(new LinearInterpolator());
            if (m.findViewById(SkinResource.getSkinResourceId("lv_last_few_days_weather", "id")).getVisibility() == View.VISIBLE) {
                m.findViewById(SkinResource.getSkinResourceId("moretodaydetail", "id")).setVisibility(View.VISIBLE);
                m.findViewById(SkinResource.getSkinResourceId("lv_last_few_days_weather", "id")).startAnimation(mAnim_out);
                m.findViewById(SkinResource.getSkinResourceId("moretodaydetail", "id")).startAnimation(mAnim_in);
                m.findViewById(SkinResource.getSkinResourceId("lv_last_few_days_weather", "id")).setVisibility(View.GONE);
                view.setBackgroundResource(SkinResource.getSkinDrawableIdByName("moredetailbutton"));
            } else {
                m.findViewById(SkinResource.getSkinResourceId("lv_last_few_days_weather", "id")).setVisibility(View.VISIBLE);
                m.findViewById(SkinResource.getSkinResourceId("moretodaydetail", "id")).startAnimation(mAnim_out);
                m.findViewById(SkinResource.getSkinResourceId("lv_last_few_days_weather", "id")).startAnimation(mAnim_in);
                m.findViewById(SkinResource.getSkinResourceId("moretodaydetail", "id")).setVisibility(View.GONE);
                view.setBackgroundResource(SkinResource.getSkinDrawableIdByName("weather_style"));
            }

        }
    };


}