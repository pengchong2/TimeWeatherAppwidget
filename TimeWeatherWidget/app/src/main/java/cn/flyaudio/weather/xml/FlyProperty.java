package cn.flyaudio.weather.xml;


import java.util.HashMap;

public class FlyProperty {
	
	private HashMap<String, String> mPropertiesMap = new HashMap<String, String>();
	
	public FlyProperty(HashMap<String, String> map){
		mPropertiesMap = map;
	}
	
	public String getStringValue(String key){
		if(!mPropertiesMap.containsKey(key))
			return "";
		return mPropertiesMap.get(key);
	}
	
	public boolean getBoolenValue(String key){
		if(!mPropertiesMap.containsKey(key))
			return false;
		
		String value = mPropertiesMap.get(key);
		if(("true").equals(value))
			return true;
		if(("false").equals(value))
			return false;
		return false;
	}
}
