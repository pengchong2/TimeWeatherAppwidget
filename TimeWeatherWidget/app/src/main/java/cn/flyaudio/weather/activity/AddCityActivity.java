package cn.flyaudio.weather.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AutoCompleteTextView.OnDismissListener;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.flyaudio.weather.adapter.CityAdapter;
import cn.flyaudio.weather.data.WeatherWidgetApplication;
import cn.flyaudio.weather.objectInfo.CityResult;
import cn.flyaudio.weather.service.WeatherService;
import cn.flyaudio.weather.util.Constant;
import cn.flyaudio.weather.util.DayNightUtil;
import cn.flyaudio.weather.util.Smart_GetCity_SQL;
import cn.flyaudio.weather.util.UtilsTools;
import cn.flyaudio.weather.view.SkinResource;

public class AddCityActivity extends Activity implements DayNightUtil.ReflashUI {
    private GridView grid;
    private SharedPreferences preference = null;
    public List<String> mCityList; // 城市名称
    public List<String> mCityCodeList; // 城市代码Woeid
    private Boolean searching = false;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView slideText;
    private List<CityResult> citylist = new ArrayList<CityResult>();
    private List<String> mstringArray;
    private Boolean isVisibility = false;
    private String[] citynamecopy;
    int grid_page_size;
    int current_grid_page_size = 0;
    ArrayAdapter<String> adapter;
    private Toast mToast;
    private View mRootView;
    private View cityView;
    // add for skin
    private View rootView;
    private Button btnSearch;//搜索
    private Button btnNextPage;//下一页
    private Button btnPrePage;//上一页
    String supportDayNight = SkinResource.getSkinStringByName("skin_support_day_night_mode");
//    private DumpActivityReceiver mUIReceiver;

    @Override
    protected void onResume() {
        super.onResume();

        final String[] cityname = {SkinResource.getSkinStringByName("city1"),
                SkinResource.getSkinStringByName("city2"), SkinResource.getSkinStringByName("city3"),
                SkinResource.getSkinStringByName("city4"), SkinResource.getSkinStringByName("city5"),
                SkinResource.getSkinStringByName("city6"), SkinResource.getSkinStringByName("city7"),
                SkinResource.getSkinStringByName("city8"), SkinResource.getSkinStringByName("city9"),
                SkinResource.getSkinStringByName("city10"), SkinResource.getSkinStringByName("city11"),
                SkinResource.getSkinStringByName("city12"), SkinResource.getSkinStringByName("city13"),
                SkinResource.getSkinStringByName("city14"), SkinResource.getSkinStringByName("city15"),
                SkinResource.getSkinStringByName("city16"), SkinResource.getSkinStringByName("city17"),
                SkinResource.getSkinStringByName("city18"), SkinResource.getSkinStringByName("city19"),
                SkinResource.getSkinStringByName("city20"), SkinResource.getSkinStringByName("city21"),
                SkinResource.getSkinStringByName("city22"), SkinResource.getSkinStringByName("city23"),
                SkinResource.getSkinStringByName("city24"), SkinResource.getSkinStringByName("city25"),
                SkinResource.getSkinStringByName("city26"), SkinResource.getSkinStringByName("city27"),
                SkinResource.getSkinStringByName("city28"), SkinResource.getSkinStringByName("city29"),
                SkinResource.getSkinStringByName("city30"), SkinResource.getSkinStringByName("city31"),
                SkinResource.getSkinStringByName("city32"), SkinResource.getSkinStringByName("city33"),
                SkinResource.getSkinStringByName("city34"), SkinResource.getSkinStringByName("city35"),
                SkinResource.getSkinStringByName("city36")};

        citynamecopy = cityname.clone();
        mstringArray = new ArrayList<String>();

        grid_page_size = (int) Math.ceil(citynamecopy.length / (12 * 1.0));

        getcurrentData(current_grid_page_size);
//        fixMyViewError();


//        if (WeatherWidgetApplication.getEnableColorTheme()) {
//            mRootView.setBackgroundColor(Integer.parseInt(SystemProperties.get(Constant.PROPERTY_COLORTHEME, "-65536")));
//        }

        DayNightUtil.setDayNightModeListener(this);
        //SystemProperties.get("fly.android.navi.daynightmode", "day")

        Log.e("xifei", "-------" + supportDayNight);
      /*  if (!TextUtils.isEmpty(supportDayNight) && "support".equals(supportDayNight)
                && Constant.CURREN_DAYNIGHT_MODE.equals(SystemProperties.get("fly.android.navi.daynightmode", "day"))) {
            initNightView();
        } else {
            initDayView();
        }*/
        initDayView();

        init();
       /* if (!TextUtils.isEmpty(supportDayNight) && "support".equals(supportDayNight)
                && Constant.CURREN_DAYNIGHT_MODE.equals(SystemProperties.get("fly.android.navi.daynightmode", "day"))) {
            adapter = new ArrayAdapter<String>(SkinResource.getSkinContext(), SkinResource.getSkinLayoutIdByName("gridview_cityitem_night"),
                    mstringArray);
        } else {
            adapter = new ArrayAdapter<String>(SkinResource.getSkinContext(), SkinResource.getSkinLayoutIdByName("gridview_cityitem"),
                    mstringArray);
        }*/
        adapter = new ArrayAdapter<String>(SkinResource.getSkinContext(), SkinResource.getSkinLayoutIdByName("gridview_cityitem"),
                mstringArray);

        grid = (GridView) rootView.findViewById(SkinResource.getSkinResourceId("commoncity", "id"));
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                autoCompleteTextView.setText(cityname[position
                        + current_grid_page_size * 12]);
            }
        });

    }

    /**
     * 换肤(资源重定位)有自定义控件时，需要先加载自定义控件，否则会出现找不到类
     */
    private void fixMyViewError() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int initPreloadViewsID = this.getResources().getIdentifier("main", "layout", this.getPackageName());
        View initMainView = (View) inflater.inflate(initPreloadViewsID, null);
        setContentView(initMainView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixMyViewError();

        //SystemProperties.get("fly.android.navi.daynightmode", "day")
       /* if (!TextUtils.isEmpty(supportDayNight) && "support".equals(supportDayNight)
                && Constant.CURREN_DAYNIGHT_MODE.equals(SystemProperties.get("fly.android.navi.daynightmode", "day"))) {
            initNightView();
        } else {
            initDayView();
        }*/
        initDayView();
    }

    private void init() {
        if (rootView != null) {
            btnSearch = (Button) rootView.findViewById(SkinResource.getSkinResourceId("btn_search_city", "id"));
            btnNextPage = (Button) rootView.findViewById(SkinResource.getSkinResourceId("next_button", "id"));
            btnPrePage = (Button) rootView.findViewById(SkinResource.getSkinResourceId("previous_button", "id"));

            mRootView = rootView.findViewById(SkinResource.getSkinResourceId("addcity_bg_color", "id"));
            preference = getSharedPreferences("weather", MODE_PRIVATE);
            mCityList = new ArrayList<String>();
            mCityCodeList = new ArrayList<String>();
            Button backButton = (Button) rootView.findViewById(SkinResource.getSkinResourceId("back", "id"));// return Button
            slideText = (TextView) rootView.findViewById(SkinResource.getSkinResourceId("choose_city_slider_page_num", "id"));
            slideText.setText((current_grid_page_size + 1) + "/" + grid_page_size);
            backButton.setOnClickListener(backClickListener);
            getCityListAndCityCodeListFormShared();
            cityView = rootView.findViewById(SkinResource.getSkinResourceId("citylist", "id"));
            autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(SkinResource.getSkinResourceId("autoComplete_city", "id"));


            CityAdapter mCityAdapter = new CityAdapter(AddCityActivity.this, null);
            autoCompleteTextView.setAdapter(mCityAdapter);

            btnNextPage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    next_button_OnclickListener();
                }
            });
            btnPrePage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    previous_button_OnclickListener();
                }
            });
            btnSearch.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    searchCityOnclick();
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //API版本大于17
                autoCompleteTextView.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        // TODO Auto-generated method stub
                        Log.d("zzz", " AutoCompleteTextView.OnDismissListener()");
                        isVisibility = false;
                        cityView.setVisibility(View.VISIBLE);
                    }
                });
            }

            autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    boolean different = true;
                    CityResult result = citylist.get(position);

                    for (int i = 0; i < mCityList.size(); i++) {
                        if (mCityList.get(i).equals((result.getCityName().equals(result.getAdmin2()) ? result.getCityName() :
                                (result.getAdmin2() + "," + result.getCityName())))) {
                            different = false;
                            break;
                        }
                    }

                    if (different == true) {
                        mCityList.add(result.getCityName());
                        mCityCodeList.add(result.getAreaid());

                        int current = preference.getInt("current", 1);
                        SharedPreferences.Editor edit = preference.edit();
                        edit.putInt("current", current);
                        edit.commit();
                        for (int j = 0; j < 4; j++) {
                            if (preference.getString(String.valueOf(j + 1), null) == null) {
                                if (!WeatherWidgetApplication.isCNLanguage) {
                                    autoCompleteTextView.setText(result.getCityname_pinyin());
                                }
                                AddCityActivity.this.startService(new Intent(
                                        AddCityActivity.this, WeatherService.class));
                                writeSharpPreference(j + 1, (result.getCityname_pinyin().equals(result.getAdmin2_en()) ? result.getCityname_pinyin() :
                                                (result.getAdmin2_en() + "," + result.getCityname_pinyin())),
                                        (result.getCityName().equals(result.getAdmin2()) ? result.getCityName() :
                                                (result.getAdmin2() + "," + result.getCityName())),
                                        result.getAreaid());

                                break;
                            }
                        }

//                        mUIReceiver = new DumpActivityReceiver();
//                        IntentFilter iFilter = new IntentFilter(Constant.ACTION_UPDATE_DATA);
//                        registerReceiver(mUIReceiver, iFilter);

//                        AddCityActivity.this.startService(new Intent(
//                                AddCityActivity.this, WeatherService.class));

                        Intent mIntent = new Intent(AddCityActivity.this,
                                CityEditPageActivity.class);
                        startActivity(mIntent);
                        AddCityActivity.this.finish();


                    } else {
                        //当API版本低于17时,监听当添加的城市已经存在的时候,显示城市列表
                        if (cityView.getVisibility() == View.INVISIBLE || autoCompleteTextView.getVisibility() == View.VISIBLE) {
                            isVisibility = false;
                            cityView.setVisibility(View.VISIBLE);
                        }
                        autoCompleteTextView.setText("");
                        Toast.makeText(AddCityActivity.this,
                                SkinResource.getSkinStringByName("had_existed"),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }


    private void initNightView() {
        LayoutInflater inflater = LayoutInflater.from(SkinResource.getSkinContext());
        rootView = inflater.inflate(SkinResource.getSkinLayoutIdByName("addcity_activity_layout_night"), null);
        if (rootView == null) {
            rootView = inflater.inflate(SkinResource.getSkinLayoutIdByName("addcity_activity_layout"), null);
        }
        setContentView(rootView);
    }

    private void initDayView() {
        LayoutInflater inflater = LayoutInflater.from(SkinResource.getSkinContext());
        rootView = inflater.inflate(SkinResource.getSkinLayoutIdByName("addcity_activity_layout"), null);
        setContentView(rootView);
    }


    @Override
    public void showUI(String mode) {
        Log.d("necor-ota", "Dea showUI ==");
       /* if (mode.equals(Constant.NIGHT_MODE)) {
            initNightView();
        } else {
            initDayView();
        }*/
        initDayView();


        final String[] cityname = {SkinResource.getSkinStringByName("city1"),
                SkinResource.getSkinStringByName("city2"), SkinResource.getSkinStringByName("city3"),
                SkinResource.getSkinStringByName("city4"), SkinResource.getSkinStringByName("city5"),
                SkinResource.getSkinStringByName("city6"), SkinResource.getSkinStringByName("city7"),
                SkinResource.getSkinStringByName("city8"), SkinResource.getSkinStringByName("city9"),
                SkinResource.getSkinStringByName("city10"), SkinResource.getSkinStringByName("city11"),
                SkinResource.getSkinStringByName("city12"), SkinResource.getSkinStringByName("city13"),
                SkinResource.getSkinStringByName("city14"), SkinResource.getSkinStringByName("city15"),
                SkinResource.getSkinStringByName("city16"), SkinResource.getSkinStringByName("city17"),
                SkinResource.getSkinStringByName("city18"), SkinResource.getSkinStringByName("city19"),
                SkinResource.getSkinStringByName("city20"), SkinResource.getSkinStringByName("city21"),
                SkinResource.getSkinStringByName("city22"), SkinResource.getSkinStringByName("city23"),
                SkinResource.getSkinStringByName("city24"), SkinResource.getSkinStringByName("city25"),
                SkinResource.getSkinStringByName("city26"), SkinResource.getSkinStringByName("city27"),
                SkinResource.getSkinStringByName("city28"), SkinResource.getSkinStringByName("city29"),
                SkinResource.getSkinStringByName("city30"), SkinResource.getSkinStringByName("city31"),
                SkinResource.getSkinStringByName("city32"), SkinResource.getSkinStringByName("city33"),
                SkinResource.getSkinStringByName("city34"), SkinResource.getSkinStringByName("city35"),
                SkinResource.getSkinStringByName("city36")};

        citynamecopy = cityname.clone();
        mstringArray = new ArrayList<String>();

        grid_page_size = (int) Math.ceil(citynamecopy.length / (12 * 1.0));

        getcurrentData(current_grid_page_size);
//        fixMyViewError();


        init();

        if (!TextUtils.isEmpty(supportDayNight) && "support".equals(supportDayNight)
                && Constant.CURREN_DAYNIGHT_MODE.equals(SystemProperties.get("fly.android.navi.daynightmode", "day"))) {
            adapter = new ArrayAdapter<String>(SkinResource.getSkinContext(), SkinResource.getSkinLayoutIdByName("gridview_cityitem_night"),
                    mstringArray);
        } else {
            adapter = new ArrayAdapter<String>(SkinResource.getSkinContext(), SkinResource.getSkinLayoutIdByName("gridview_cityitem"),
                    mstringArray);
        }
//        adapter = new ArrayAdapter<String>(SkinResource.getSkinContext(), SkinResource.getSkinLayoutIdByName("gridview_cityitem"),
//                mstringArray);

        grid = (GridView) rootView.findViewById(SkinResource.getSkinResourceId("commoncity", "id"));
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                autoCompleteTextView.setText(cityname[position
                        + current_grid_page_size * 12]);
            }
        });


    }


    private OnClickListener backClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(AddCityActivity.this,
                    CityEditPageActivity.class);
            startActivity(intent);
            AddCityActivity.this.finish();
        }
    };

    private void searchCityOnclick() {
        cancelToast();

        if (!UtilsTools.isNetworkAvailable(AddCityActivity.this)) {
            showToast(SkinResource.getSkinStringByName("neworkconnect"));
            return;
        }
        autoCompleteTextView.setText(autoCompleteTextView.getEditableText()
                .toString());
        searching = true;
    }

    private void previous_button_OnclickListener() {
        if (current_grid_page_size - 1 >= 0) {
            current_grid_page_size--;
            getcurrentData(current_grid_page_size);
        }
        adapter.notifyDataSetChanged();
        slideText.setText((current_grid_page_size + 1) + "/" + grid_page_size);
    }

    private void next_button_OnclickListener() {
        if (current_grid_page_size + 1 < grid_page_size) {
            current_grid_page_size++;
            getcurrentData(current_grid_page_size);
        }
        adapter.notifyDataSetChanged();
        slideText.setText((current_grid_page_size + 1) + "/" + grid_page_size);

    }

    public void getcurrentData(int page) {
        int start = page * 12;
        int end = start + 12;
        mstringArray.clear();
        while (start < citynamecopy.length && start < end) {
            mstringArray.add(citynamecopy[start]);
            start++;
        }

    }

    private void getCityListAndCityCodeListFormShared() {
        for (int i = 0; i < 4; i++) {
            String city = preference.getString(String.valueOf(i + 1), null);
            String code = readSharpPreference(i + 1);
            if (city != null) {
                mCityList.add(city);
                mCityCodeList.add(code);
                Log.e("xifei---city--", city);
                Log.e("xifei---code--", code);
            }
        }
    }


    private String readSharpPreference(int index) {
        String city = null;
        switch (index) {
            case 1:
                city = preference.getString("city1", Constant.DEFAULT_CITYCODE);
                break;
            case 2:
                city = preference.getString("city2", "10000");
                break;
            case 3:
                city = preference.getString("city3", "10000");
                break;
            case 4:
                city = preference.getString("city4", "10000");
                break;
            default:
                break;
        }
        return city;
    }

    private void showToast(String text) {
        mToast = Toast.makeText(AddCityActivity.this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    private void writeSharpPreference(int index, String cityname_pinyin, String cityname, String citynum
    ) {
        SharedPreferences.Editor editor = preference.edit();
        switch (index) {
            case 1:
                editor.putString("1", cityname);
                editor.putString("10", cityname_pinyin);
                editor.putString("city1", citynum);
                break;
            case 2:
                editor.putString("2", cityname);
                editor.putString("20", cityname_pinyin);
                editor.putString("city2", citynum);
                break;
            case 3:
                editor.putString("3", cityname);
                editor.putString("30", cityname_pinyin);
                editor.putString("city3", citynum);
                break;
            case 4:
                editor.putString("4", cityname);
                editor.putString("40", cityname_pinyin);
                editor.putString("city4", citynum);
                break;
            default:
                break;
        }
        editor.commit();
    }

    private class CityAdapter extends ArrayAdapter<CityResult> implements
            Filterable {
        private Context context;

        public CityAdapter(Context context, List<CityResult> citylist) {
            super(SkinResource.getSkinContext(), SkinResource.getSkinLayoutIdByName("cityresult_layout"), citylist);
            this.context = context;
        }

        @Override
        public int getCount() {
            if (citylist != null) {
                return citylist.size();
            }
            return 0;
        }

        @Override
        public CityResult getItem(int position) {

            if (citylist != null) {
                return citylist.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            if (citylist != null) {
                return citylist.get(position).hashCode();
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) SkinResource.getSkinContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

               /* if (!TextUtils.isEmpty(supportDayNight) && "support".equals(supportDayNight)
                        && Constant.CURREN_DAYNIGHT_MODE.equals(SystemProperties.get("fly.android.navi.daynightmode", "day"))) {
                    view = layoutInflater.inflate(SkinResource.getSkinLayoutIdByName("cityresult_layout_night"),
                            parent, false);
                    Log.e("xifei", "3333333夜");
                } else {
                    view = layoutInflater.inflate(SkinResource.getSkinLayoutIdByName("cityresult_layout"),
                            parent, false);
                    Log.e("xifei", "3333333日");
                }*/

                view = layoutInflater.inflate(SkinResource.getSkinLayoutIdByName("cityresult_layout"),
                        parent, false);
            }
            if (citylist != null) {
                String cityName;
                String adminName;
                String admin2Name;
                String countryName;

                TextView tvTextView = (TextView) view
                        .findViewById(SkinResource.getSkinResourceId("cityitem", "id"));

                if (WeatherWidgetApplication.isCNLanguage) {
                    cityName = citylist.get(position).getCityName();
                    admin2Name = citylist.get(position).getAdmin2();
                    adminName = citylist.get(position).getAdmin1();
                } else {
                    cityName = citylist.get(position).getCityname_pinyin();
                    admin2Name = citylist.get(position).getAdmin2_en();
                    adminName = WeatherWidgetApplication.toUpperCaseFirstOne(citylist.get(position).getAdmin1_en());
                }

                countryName = citylist.get(position).getCountry();

                tvTextView.setText((adminName != null ? (adminName + ",") : "") +
                        (admin2Name != null ? (admin2Name + ",") : "") +
                        (cityName != null ? (cityName + "") : ""));
            }
            return view;
        }

        @Override
        public Filter getFilter() {
            Filter cityFilter = new Filter() {
                @Override
                protected void publishResults(CharSequence contain,
                                              FilterResults results) {
                    if (isVisibility) {
                        cityView.setVisibility(View.INVISIBLE);
                    }
                    citylist = (List<CityResult>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence contait) {
                    FilterResults results = new FilterResults();

                    if (contait == null || contait.length() < 1) {
                        return results;
                    }
                    if (searching == true) {
                        List<CityResult> citylist;
                        citylist = new Smart_GetCity_SQL(getApplicationContext()).query(contait.toString());
                        results.values = citylist;
                        results.count = citylist.size();
                        if (results.count > 0) {
                            isVisibility = true;
                        } else {
                            searching = false;
                            //showToast(getResources().getString(R.string.nullsearch));
                        }
                        searching = false;
                    }
                    return results;
                }
            };
            return cityFilter;
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {//按键的按下事件
                Intent mIntent = new Intent(AddCityActivity.this,
                        CityEditPageActivity.class);
                startActivity(mIntent);
                AddCityActivity.this.finish();
//               return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}