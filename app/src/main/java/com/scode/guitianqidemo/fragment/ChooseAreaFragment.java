package com.scode.guitianqidemo.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scode.guitianqidemo.R;
import com.scode.guitianqidemo.db.City;
import com.scode.guitianqidemo.db.County;
import com.scode.guitianqidemo.db.Province;
import com.scode.guitianqidemo.util.HttpUtil;
import com.scode.guitianqidemo.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 知らないのセカイ on 2017/6/24.
 */

public class ChooseAreaFragment extends Fragment {
    private final static int LEVEL_PROVINCE = 0;
    private final static int LEVEL_CITY = 1;
    private final static int LEVEL_COUNTY = 2;


    private Button btn_back;
    private TextView tv_title;
    private ListView lv_content;

    private List<String> contents = new ArrayList<>();//用于储存当前listview的数据
    private ArrayAdapter adapter;//listview 的adapter

    //list<T> 用于存储临时变量
    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;

    private Province selectedProvince;//当前选中的省份
    private City selectedCity;//当前选中的城市

    private int currentLevel;//当前的级别

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        btn_back = (Button) view.findViewById(R.id.back_button);
        tv_title = (TextView) view.findViewById(R.id.title_text);
        lv_content = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, contents);
        lv_content.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince=provinces.get(position);
                    queryCities();

                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cities.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {

                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (currentLevel==LEVEL_CITY){
                queryProvinces();
            }else if(currentLevel==LEVEL_COUNTY){
                queryCities();
            }
            }
        });
        queryProvinces();
    }

    /**
     * 查询省份信息
     */
    private void queryProvinces(){
        tv_title.setText("中国");
        btn_back.setVisibility(View.GONE);
        provinces = DataSupport.findAll(Province.class);
        if (provinces.size()>0){
            contents.clear();
            for (Province province : provinces) {
            contents.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lv_content.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询城市信息
     */
    private void queryCities(){
        tv_title.setText(selectedProvince.getProvinceName());
        btn_back.setVisibility(View.VISIBLE);
        cities = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cities.size() > 0) {
            contents.clear();
            for (City city : cities) {
                contents.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv_content.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode();
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询县的信息
     */
    private void queryCounties(){
        tv_title.setText(selectedCity.getCityName());
        btn_back.setVisibility(View.VISIBLE);
        counties = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (counties.size() > 0) {
            contents.clear();
            for (County county : counties) {
                contents.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lv_content.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }
        else{
            String address="http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            queryFromServer(address,"county");
        }
    }

    /**
     * 当以上数据库中没有满足的信息请求网络并保存到数据库中
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
    showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelProgressDialog();
                        Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                Log.d("response", "onResponse: "+responseText);
                Boolean result = false;
                if (type.equals("province")){
                    result = Utility.handleProvinceResponse(responseText);
                } else if (type.equals("city")) {
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if (type.equals("county")){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cancelProgressDialog();
                            if (type.equals("province")){
                                queryProvinces();
                            } else if (type.equals("city")) {
                                queryCities();
                            } else if (type.equals("county")) {
                                queryCounties();
                            }
                        }
                    });
                }
                else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cancelProgressDialog();
                            Toast.makeText(getActivity(), "获取数据有误", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }
    private void showProgressDialog(){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载中..");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void cancelProgressDialog(){
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
