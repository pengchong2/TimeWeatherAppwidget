package cn.flyaudio.weather.objectInfo;

/**
 * Created by lan on 16-12-22.
 */

public class WeatherInfo {

    private String weatherPhenomena;
    private String temperature;
    private String windSpeed;
    private String windDirection;
    private String lowTemperature;
    private String hightTemperature;
    private String updateDataDate;
    private String dayOfWeek;
    private String curDate;

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getCurDate() {
        return curDate;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
    }

    public String getUpdateDataDate() {
        return updateDataDate;
    }

    public void setUpdateDataDate(String updateDataDate) {
        this.updateDataDate = updateDataDate;
    }

    public String getWeatherPhenomena() {
        return weatherPhenomena;
    }

    public void setWeatherPhenomena(String weatherPhenomena) {
        this.weatherPhenomena = weatherPhenomena;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getLowTemperature() {
        return lowTemperature;
    }

    public void setLowTemperature(String lowTemperature) {
        this.lowTemperature = lowTemperature;
    }

    public String getHightTemperature() {
        return hightTemperature;
    }

    public void setHightTemperature(String hightTemperature) {
        this.hightTemperature = hightTemperature;
    }

    @Override
    public String toString() {
        return "WeatherInfo{" +
                "weatherPhenomena='" + weatherPhenomena + '\'' +
                ", temperature='" + temperature + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", windDirection='" + windDirection + '\'' +
                ", lowTemperature='" + lowTemperature + '\'' +
                ", hightTemperature='" + hightTemperature + '\'' +
                ", updateDataDate='" + updateDataDate + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", curDate='" + curDate + '\'' +
                '}';
    }
}
