package cn.flyaudio.weather.webserviceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.flyaudio.weather.objectInfo.FullWeatherInfo;
import cn.flyaudio.weather.util.WebAccessTools;

public class GetWeatherByJsonParser {

	private final static String TAG = "GetWeatherByJsonParser";

	private String Url;
	private JSONObject jsonObject;
	private String weatherAPIAddress = "http://webapi.weather.com.cn/data/?";
	private static final String APPID = "6bb5cbc973392a3d";//秘钥计算之用
	private static final String AVAILABLE_APPID = "6bb5cb";//appid前六位http请求时用这个
	private static final String PRIVATE_Key = "485655_SmartWeatherAPI_504eb0f";//秘钥计算之用
	
    public GetWeatherByJsonParser(/*Context context,*/String citynum){
	try {
		this.Url = this.getFullAddress(citynum, "forecast3d",
				getDate());
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (GeneralSecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   	
    	Log.d("zzz","url"+this.Url);
    }
    
	/**
	 * 捕获发送时间
	 * 
	 * @return String
	 */
	private String getDate() {
		Calendar c = Calendar.getInstance();
		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH) + 1);
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));
		// String secs = String.valueOf(c.get(Calendar.SECOND));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + month + day + hour + mins);

		return sbBuffer.toString();
	}
    
    
	/**
	 * 取得请求地址
	 * 
	 * @param weatherAPIAddress
	 * @param areaId
	 * @param weatherType
	 * @param date
	 * @return fullAddress
	 * @throws UnsupportedEncodingException
	 * @throws GeneralSecurityException
	 */
	private String getFullAddress(String areaId, String weatherType, String date)
			throws UnsupportedEncodingException, GeneralSecurityException {

		String baseString = weatherAPIAddress + "areaid=" + areaId + "&type="
				+ weatherType + "&date=" + date + "&appid=" + APPID;
		String keyString = PRIVATE_Key;
		String token = computeSignature(baseString, keyString);
		String fullAddress = weatherAPIAddress + "areaid=" + areaId + "&type="
				+ weatherType + "&date=" + date + "&appid=" + AVAILABLE_APPID
				+ "&key=" + token;
		return fullAddress;
	}
    
	/*public static String getSystemDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		Date date = new Date(System.currentTimeMillis());
		return format.format(date);
	}*/
    
    /** 
     * 计算签名 
     * @param baseString 明文 
     * @param keyString 私钥 
     * @return 秘钥 
     * @throws GeneralSecurityException 
     * @throws UnsupportedEncodingException 
     */  
    private String computeSignature(String baseString, String keyString)  
            throws GeneralSecurityException, UnsupportedEncodingException {  
      
        SecretKey secretKey = null;  
      
        byte[] keyBytes = keyString.getBytes();  
        secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");  
      
        Mac mac = Mac.getInstance("HmacSHA1");  
      
        mac.init(secretKey);  
      
        byte[] text = baseString.getBytes();  
      
        return new String(Base64.encodeBase64(mac.doFinal(text))).trim();  
    }  
    
	public FullWeatherInfo getWeatherinfo(int cityIndex) {
		return getWeatherBean( getJsonText(), cityIndex);
	}

	private StringBuilder getJsonText() {
		BufferedReader bufferedReader2;
		StringBuilder builder = new StringBuilder();
		try {
			WebAccessTools webTools = new WebAccessTools();
			bufferedReader2 = new BufferedReader(new InputStreamReader(webTools.getWebContent(Url)));
			for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
				builder.append(s);
			}
		} catch (Exception e) {
		}
		return builder;
	}

	private FullWeatherInfo getWeatherBean(StringBuilder builder, int cityIndex) {
//		WeatherInfo weather = new WeatherInfo(context);
		FullWeatherInfo latestWeathers = null;
		try {
			Log.d(TAG, "weather info from jsondata:" + builder.toString());
			jsonObject = new JSONObject(builder.toString()).getJSONObject("c");
		} catch (JSONException e1) {
			
		}
		try {			
			jsonObject = new JSONObject(builder.toString()).getJSONObject("f");
			
			latestWeathers = new FullWeatherInfo();
			latestWeathers.setCondition_date(dateFormatTransform(jsonObject.getString("f0"), "yyyyMMddhhmm", "yyyy-MM-dd HH:mm"));

			Log.d("zzz","date:"+jsonObject.getString("f0"));
			
			for (int i = 0; i < 3; i++) {
				
				jsonObject = new JSONObject(builder.toString()).getJSONObject("c");
				jsonObject = new JSONObject(builder.toString()).getJSONObject("f");
				JSONArray list = jsonObject.getJSONArray("f1");
				jsonObject = new JSONObject(list.getString(i));
				
				latestWeathers.yweathers[i].setLow( jsonObject.getString("fd") );
				latestWeathers.yweathers[i].setHigh( jsonObject.getString("fc").equals("")?jsonObject.getString("fd"):jsonObject.getString("fc"));
				
				latestWeathers.yweathers[i].setCode((jsonObject.getString("fa").equals("")?
								(jsonObject.getString("fb")):(jsonObject.getString("fa"))));
				if(i==0){
					latestWeathers.setCondition_temp(jsonObject.getString("fc").equals("")?jsonObject.getString("fd"):jsonObject.getString("fc"));
					latestWeathers.setWinddirection(jsonObject.getString("fe").equals("")?jsonObject.getString("ff"):jsonObject.getString("fe"));
					latestWeathers.setWindspeed(jsonObject.getString("fg").equals("")?jsonObject.getString("fh"):jsonObject.getString("fg"));
				}
				
				Log.d("zzz","预报第"+(i+1)+"天数据 ");
				Log.d("zzz","白天天气 fa ="+jsonObject.getString("fa"));
				Log.d("zzz","晚上天气 fb ="+jsonObject.getString("fb"));
				Log.d("zzz","白天温度 fc ="+jsonObject.getString("fc"));
				Log.d("zzz","晚上天气 fd ="+jsonObject.getString("fd"));
				Log.d("zzz","白天风向 fe ="+jsonObject.getString("fe"));
				Log.d("zzz","晚上风向 ff ="+jsonObject.getString("ff"));
				Log.d("zzz","白天风力 fg ="+jsonObject.getString("fg"));
				Log.d("zzz","晚上风力 fh ="+jsonObject.getString("fh"));
				Log.d("zzz","日出日落 fi="+jsonObject.getString("fi"));
			}
			
			latestWeathers.setDataflag(true);
			String fi = jsonObject.getString("fi");
			latestWeathers.setCondition_code(latestWeathers.yweathers[0].getCode());
			if(fi.contains("|")){
				latestWeathers.setSunrise( fi.split("[\\|]")[0]);
				latestWeathers.setSunset( fi.split("[\\|]")[1]);
				Log.d("zzz","日出日落 fi ="+jsonObject.getString("fi").split("|")[1]);
			}
			latestWeathers.setDwinddirection(jsonObject.getString("ff"));
		} catch (Exception e) {
			
		}
		return latestWeathers;
	}
	/*
	private String getTempRange(JSONObject jsonObject, int cityIndex) {
		Log.d(TAG, "cityIndex----->" + cityIndex);
		if (jsonObject == null) return "";
		
		try {
			String dayTemp = jsonObject.getString("fc");
			
			if(dayTemp == null || dayTemp.isEmpty()) {
				SharedPreferences shared = getSharedPreferencesByIndex(cityIndex);
				List<String> dates = new ArrayList<String>();
				dates.add(shared.getString("date1", ""));
				dates.add(shared.getString("date2", ""));
				dates.add(shared.getString("date3", ""));
				int todayIndex = getTodayWeather(dates);
				String tempToday = "temp" + todayIndex;
				String todayTemp = shared.getString(tempToday, "");  //xml已经保存的当前温度
				if(!todayTemp.isEmpty()) {
					String todayDayTemp = todayTemp.substring(0, todayTemp.indexOf("°"));  //取到白天温度
					return todayDayTemp + "°C～" + jsonObject.getString("fd") + "°C";
				} else {
					return "0" + "°C～" + jsonObject.getString("fd") + "°C";
				}
			}
			
			return jsonObject.getString("fc") + "°C～" + jsonObject.getString("fd") + "°C";
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d(TAG, "json解析异常!");
		}
		
		return "";
	}
	*/
	/**
	 * 从xml中获取当前日期的下标
	 * @param dates
	 * @return
	 */
	/*private int getTodayWeather(List<String> dates) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		Date date = new Date(System.currentTimeMillis());
		String dateToday = format.format(date);
		int todayIndex = 1;
		for(String tmpDate : dates) {
			if(dateToday.equals(tmpDate)) {
				return todayIndex;
			}
			todayIndex ++;
		}
		return -1;
	}*/
	
/*	
	private SharedPreferences getSharedPreferencesByIndex(int cityIndex) {
		SharedPreferences shared = null;
		switch(cityIndex) {
		case 1:
			shared = mContext.getSharedPreferences("FirstWeather", Context.MODE_PRIVATE);
			break;
		case 2:
			shared = mContext.getSharedPreferences("SecondWeather", Context.MODE_PRIVATE);
			break;
		case 3:
			shared = mContext.getSharedPreferences("ThirdWeather", Context.MODE_PRIVATE);
			break;
		case 4:
			shared = mContext.getSharedPreferences("FouthWeather", Context.MODE_PRIVATE);
			break;
		default:
			break;
		}
		return shared;
	}
	*/
	/*public String dateFormat(String dateStr) {
		if (dateStr.equals("")) return dateStr;
		
		int year = Integer.parseInt(dateStr.split("年")[0]);
		int month = Integer.parseInt(dateStr.split("年")[1].split("月")[0]);
		int day = Integer.parseInt(dateStr.split("年")[1].split("月")[1].split("日")[0]);
		
		SimpleDateFormat formatAfter = new SimpleDateFormat("yyyy年MM月dd日");
		Date date = null;
		
		try {
			date = new Date();
			date.setYear(year-1900);
			date.setMonth(month-1);
			date.setDate(day);
			return formatAfter.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateStr;
		}
	}*/
	
	private String dateFormatTransform(String dateStr, String fromFormatStr, String toFormatStr) {
		if (dateStr.equals("")) return dateStr;
		
		SimpleDateFormat fromFormat = new SimpleDateFormat(fromFormatStr);
		SimpleDateFormat toFormat = new SimpleDateFormat(toFormatStr);
		
		Date date = null;
		try {
			date = fromFormat.parse(dateStr);
			return toFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateStr;
		}
	}
}