package com.scode.guitianqidemo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.scode.guitianqidemo.gson.HeWeatherBean;
import com.scode.guitianqidemo.util.HttpUtil;
import com.scode.guitianqidemo.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity,titleUpdateTime,degreeText,weatherInfoText,aqiText,pm25Text,comfortText,carWashText,sportText;
    private LinearLayout forecastLayout;
    private ImageView bingPicImage;

    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;//存储当前的city id

    public DrawerLayout drawerLayout;
    private Button btn_home;

    private static String lastUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();

    }
    //初始化view
    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity= (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        bingPicImage = (ImageView) findViewById(R.id.bing_pic_img);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btn_home = (Button) findViewById(R.id.title_home);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sp.getString("weather", null);//获取是否已经保存过的weather信息
        String bingPic = sp.getString("bing_pic", null);
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImage);
        }else{
            loadImage();
        }

        if (weatherString != null) {
            HeWeatherBean weather = Utility.handleWeatherResponse(weatherString);//转换为bean
            mWeatherId = weather.getBasic().getId();
            //如果已经存在就进行展示
            showWeatherInfo(weather);
        }
        else{
            mWeatherId = getIntent().getStringExtra("weather_id");
//            String weatherId="CN101010100";
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                loadImage();
            }
        });


    }
    //获取必应的每日一图
    private void loadImage() {
        String requestUrl="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取每日一图失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String url=response.body().string();

                if (!TextUtils.isEmpty(url)&!url.equals(lastUrl)) {
                    lastUrl=url;
                    SharedPreferences.Editor spEdit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    spEdit.putString("bing_pic", url);
                    spEdit.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(url).into(bingPicImage);
                        }
                    });
                }else if (TextUtils.isEmpty(url)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WeatherActivity.this, "获取的数据无效", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }

    public void requestWeather(String weatherId) {
        mWeatherId=weatherId;
        //采用和风v5接口
        String weatherUrl="https://free-api.heweather.com/v5/weather?city="
                +weatherId+"&key=5e097c6bf99b4ece88749ff2a77168f4";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final HeWeatherBean weatherBean=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //存储数据并展示数据
                        if (weatherBean != null && weatherBean.getStatus().equals("ok")) {
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weatherBean);
                        }else{
                            Toast.makeText(WeatherActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
        //传入信息bean进行数据的展示
    private void showWeatherInfo(HeWeatherBean weather) {
        String cityName=weather.getBasic().getCity();
        String updateTime=weather.getBasic().getUpdate().getLoc().split(" ")[1];
        String degree=weather.getNow().getTmp()+"℃";
        String weatherInfo=weather.getNow().getCond().getTxt();
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (HeWeatherBean.ForecastBean forecast : weather.getDaily_forecast()) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_texts);

            dateText.setText(forecast.getDate());
            infoText.setText(forecast.getCond().getTxt_d());
            maxText.setText(forecast.getTmp().getMax());
            minText.setText(forecast.getTmp().getMin());

            forecastLayout.addView(view);
        }
        if(weather.getAqi()!=null){
            aqiText.setText(weather.getAqi().getCity().getAqi());
            pm25Text.setText(weather.getAqi().getCity().getPm25());
        }

        String comfort="舒适度:"+weather.getSuggestion().getComf().getTxt();
        String carWash = "洗车指数:" + weather.getSuggestion().getCw().getTxt();
        String sport="运动建议:"+weather.getSuggestion().getSport().getTxt();
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

}
