package cn.flyaudio.weather.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cn.flyaudio.weather.view.SkinResource;

/**
 * Created by xifei on 17-3-7.
 */
public class GetWeather_SQL {

    private final static String TAG = "---GetWeather_SQL---";
    private Context context;
    private SQLiteDatabase database;

    public GetWeather_SQL(){
        super();
    }

    public void initDatabase() {

        Log.d(Constant.TAG,TAG + "initDatabase");
        //数据库路径
        String dirPath = "/data/data/cn.flyaudio.Weather/databases";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        //创建或打开数据库
        File dbfile = new File(dir, "weatherData.db");
        try {
            if (!dbfile.exists()) {
                dbfile.createNewFile();
            }
//			InputStream is = SkinResource.getSkinContext().getResources().openRawResource(SkinResource.getSkinResourceId("weather_db_weather","raw"));

            InputStream is = SkinResource.getLocalContext().getResources().openRawResource(
                    SkinResource.getIdentifier("weather_db_weather","raw", SkinResource.getLocalContext()));
            FileOutputStream fos = new FileOutputStream(dbfile);
            byte[] buffere = new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Constant.TAG,TAG + "importInitDatabase IOException=" + e.toString());
        }
    }


    public String queryCityId(String cityNum){

        initDatabase();
        String path = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + "/cn.flyaudio.Weather/databases/weatherData.db";
        System.out.println("path : " + path);
        database = SQLiteDatabase.openOrCreateDatabase(path, null);

        String cityId =null;
        String getCityIdSQL="select city_id from cities_table where city_num="+cityNum;
        Cursor cursor = database.rawQuery(getCityIdSQL, null);
        System.out.println("cursor count : " + cursor.getCount());
        while (cursor.moveToNext()) {
            cityId=cursor.getString(0);
        }
        if (cursor!=null){
            cursor.close();
        }
        return cityId;
    }

    public ArrayList<String> queryCitiseId(ArrayList<String> citiesNum){

        initDatabase();
        String path = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + "/cn.flyaudio.Weather/databases/weatherData.db";
        System.out.println("path : " + path);
        database = SQLiteDatabase.openOrCreateDatabase(path, null);

        String cityId =null;
        ArrayList<String> citiesId = new ArrayList<String>();
        for (String cityNum :citiesNum){

        String getCityIdSQL="select city_id from cities_table where city_num="+cityNum;
        Cursor cursor = database.rawQuery(getCityIdSQL, null);
        System.out.println("cursor count : " + cursor.getCount());
        while (cursor.moveToNext()) {
            cityId=cursor.getString(0);
        }
            citiesId.add(cityId);

        if (cursor!=null){
            cursor.close();
           }

        }

        return citiesId;
    }

    //查询天气现象编码
    public String queryWeatherPhenomena(String phenomenaId){

        initDatabase();
        String path = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + "/cn.flyaudio.Weather/databases/weatherData.db";
        System.out.println("path : " + path);
        database = SQLiteDatabase.openOrCreateDatabase(path, null);

        String phenomena =null;
        String getPhenomenaSQL="select phenomena_num from weather_phenomena where phenomena_id="+phenomenaId;
        Cursor cursor = database.rawQuery(getPhenomenaSQL, null);
        System.out.println("cursor count : " + cursor.getCount());
        while (cursor.moveToNext()) {
            phenomena=cursor.getString(0);
        }
        if (cursor!=null){
            cursor.close();
        }
        return phenomena;
    }


}
