package cn.flyaudio.weather.objectInfo;

/*
 * Copyright (C) 2014 Francesco Azzola - Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CityResult {

	private String areaid;
	private String cityname_pinyin;
    private String woeid;       //城市代号
    private String country;     //国家
    private String cityName;    //城市名
    private String admin1;      //城市上一级 
    private String admin1_en;   //城市上一级
    public String getAdmin1_en() {
		return admin1_en;
	}

	public void setAdmin1_en(String admin1_en) {
		this.admin1_en = admin1_en;
	}

	private String admin2;      //城市上二级
	private String admin2_en;   //城市上二级
    public String getAdmin2_en() {
		return admin2_en;
	}
	public void setAdmin2_en(String admin2_en) {
		this.admin2_en = admin2_en;
	}
	public String getAdmin1() {
		return admin1;
	}
	public void setAdmin1(String admin1) {
		this.admin1 = admin1;
	}

	
    public String getAreaid() {
		return areaid;
	}
	public CityResult() {}

    public CityResult(String woeid, String cityName,String admin1, String country) {
        this.woeid = woeid;
        this.cityName = cityName;
        this.country = country;
        this.admin1 = admin1;
        this.admin2 = admin2;
        this.areaid = areaid;
        this.cityname_pinyin=cityname_pinyin;
    }

    public String getWoeid() {
        return woeid;
    }
    

    public void setWoeid(String woeid) {
        this.woeid = woeid;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

	public String getCityname_pinyin() {
		return cityname_pinyin;
	}
	public void setCityname_pinyin(String cityname_pinyin) {
		this.cityname_pinyin = cityname_pinyin;
	}
	public String getAdmin2() {
		return admin2;
	}
	public void setAdmin2(String admin2) {
		this.admin2 = admin2;
	}
	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}
    
    @Override
    public String toString() {
        return cityName ;
    }
}
