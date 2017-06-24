package com.scode.guitianqidemo.gson;

import java.util.List;

/**
 * 和风天气接口Bean
 *
 * Created by 知らないのセカイ on 2017/6/24.
 */

public class HeWeatherBean {
    private String status;
    private BasicBean basic;
    private AqiBean aqi;
    private NowBean now;
    private SuggestionBean suggestion;
    private List<ForecastBean>  daily_forecast;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BasicBean getBasic() {
        return basic;
    }

    public void setBasic(BasicBean basic) {
        this.basic = basic;
    }

    public AqiBean getAqi() {
        return aqi;
    }

    public void setAqi(AqiBean aqi) {
        this.aqi = aqi;
    }

    public NowBean getNow() {
        return now;
    }

    public void setNow(NowBean now) {
        this.now = now;
    }

    public SuggestionBean getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(SuggestionBean suggestion) {
        this.suggestion = suggestion;
    }

    public List<ForecastBean> getDaily_forecast() {
        return daily_forecast;
    }

    public void setDaily_forecast(List<ForecastBean> daily_forecast) {
        this.daily_forecast = daily_forecast;
    }

    public static class ForecastBean{
        private String date;
        private Daily_CondBean cond;
        private TmpBean tmp;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Daily_CondBean getCond() {
            return cond;
        }

        public void setCond(Daily_CondBean cond) {
            this.cond = cond;
        }

        public TmpBean getTmp() {
            return tmp;
        }

        public void setTmp(TmpBean tmp) {
            this.tmp = tmp;
        }

        public static class TmpBean {
            private String max;
            private String min;

            public String getMax() {
                return max;
            }

            public void setMax(String max) {
                this.max = max;
            }

            public String getMin() {
                return min;
            }

            public void setMin(String min) {
                this.min = min;
            }
        }
        public static class Daily_CondBean{
            private String txt_d;

            public String getTxt_d() {
                return txt_d;
            }

            public void setTxt_d(String txt_d) {
                this.txt_d = txt_d;
            }
        }

    }
    //天气建议
    public static class SuggestionBean{
        private ComfBean comf;
        private CwBean cw;
        private SportBean sport;
        public static class SportBean{
            private String txt;

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }
        public static class CwBean{
            private String txt;

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }
        //天气建议
        public static class ComfBean{
            private String txt;

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }
    }

    //现在的天气状况
    public static class NowBean{
        private String tmp;
        private CondBean cond;

        public String getTmp() {
            return tmp;
        }

        public void setTmp(String tmp) {
            this.tmp = tmp;
        }

        public CondBean getCond() {
            return cond;
        }

        public void setCond(CondBean cond) {
            this.cond = cond;
        }

        public static class CondBean{
            private String txt;

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }
    }

    //城市的天气状况
    public static class AqiBean{
        private CityBean city;

        public CityBean getCity() {
            return city;
        }

        public void setCity(CityBean city) {
            this.city = city;
        }

        public static class CityBean{
            private String aqi;
            private String pm25;

            public String getAqi() {
                return aqi;
            }

            public void setAqi(String aqi) {
                this.aqi = aqi;
            }

            public String getPm25() {
                return pm25;
            }

            public void setPm25(String pm25) {
                this.pm25 = pm25;
            }
        }
    }
    public static class BasicBean{
        //城市
        private String city;
        //城市id
        private String id;
        //更新时间的集合
        private UpdateBean update;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public UpdateBean getUpdate() {
            return update;
        }

        public void setUpdate(UpdateBean update) {
            this.update = update;
        }

        public static class UpdateBean{
            //表示天气更新的时间
          private  String loc;

            public String getLoc() {
                return loc;
            }

            public void setLoc(String loc) {
                this.loc = loc;
            }
        }

    }

}
