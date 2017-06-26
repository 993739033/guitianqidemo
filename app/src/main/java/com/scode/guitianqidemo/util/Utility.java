package com.scode.guitianqidemo.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.scode.guitianqidemo.db.City;
import com.scode.guitianqidemo.db.County;
import com.scode.guitianqidemo.db.Province;
import com.scode.guitianqidemo.gson.HeWeatherBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析保存存到数据库？
 *
 * Created by 知らないのセカイ on 2017/6/24.
 */

public class Utility {
    /*
     解析省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
        /*
        解析市级数据
         */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCitys = new JSONArray(response);
                for (int i=0;i<allCitys.length();i++) {
                    JSONObject cityObject = allCitys.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /*
    解析县级数据
     */

    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
               JSONArray allCountys = new JSONArray(response);
                for (int i=0;i<allCountys.length();i++) {

                    JSONObject countyObject = allCountys.getJSONObject(i);
                    County county=new County();
                    county.setCityId(cityId);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
        }

        //解析返回的天气数据
    public static HeWeatherBean handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,HeWeatherBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
