package cn.flyaudio.weather.objectInfo;



/**
 * @author:zengyuke
 * @company:flyaudio
 * @version:1.0
 * @createdDate:2014-5-5下午3:18:07
 */
public class WeatherInfo_OneDay {

	private String day;    //星期
	private String date;   //日期
	private String low;    //最高温
	private String high;   //最低温
    private String text;   //天气类型
    private String code;   //天气代号
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	
}
