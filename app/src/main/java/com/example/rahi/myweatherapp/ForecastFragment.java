package com.example.rahi.myweatherapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rahi.myweatherapp.Adapter.WeatherForecastAdapter;
import com.example.rahi.myweatherapp.Common.Common;
import com.example.rahi.myweatherapp.Model.WeatherForecastResult;
import com.example.rahi.myweatherapp.Retrofit.IOpenWeatherMap;
import com.example.rahi.myweatherapp.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    TextView textView, switchValue;
    TextView txt_city_name, txt_geo_coord;
    RecyclerView recycler_forecast;

    static ForecastFragment instance;

    public static ForecastFragment getInstances() {
        if (instance == null) {
            instance = new ForecastFragment();
        }

        return instance;
    }

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_forecast, container, false);

        txt_city_name = itemView.findViewById(R.id.txt_city_name);
        txt_geo_coord = itemView.findViewById(R.id.txt_geo_coord);

        textView = itemView.findViewById(R.id.swOnOff);
        switchValue = getActivity().findViewById(R.id.switchOnOff);

        String vSwitch = switchValue.getText().toString();
        textView.setText(vSwitch);

        recycler_forecast = itemView.findViewById(R.id.recycler_forecast);
        recycler_forecast.setHasFixedSize(true);
        recycler_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        //        // for get first farenheight value
        //        if (textView.getText().toString().equals("1")) {
        //            getForecastWeatherInformationInFaren();
        //            notifyAll();
        //        } else {
        //            getForecastWeatherInformation();// for celcious value
        //            notifyAll();
        //
        //        }

        getForecastWeatherInformation();

        return itemView;
    }

    // Ctrl + O
    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    //    private void getForecastWeatherInformation() {
    //        compositeDisposable.add(mService.getForcastWeatherByLatLon(
    //                String.valueOf(Common.current_location.getLatitude()),
    //                String.valueOf(Common.current_location.getLongitude()),
    //                Common.APP_ID,
    //                "metric")
    //                .subscribeOn(Schedulers.io())
    //                .observeOn(AndroidSchedulers.mainThread())
    //                .subscribe(new Consumer<WeatherForecastResult>() {
    //                    @Override
    //                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
    //                        displayForecastWeather(weatherForecastResult);
    //                    }
    //                }, new Consumer<Throwable>() {
    //                    @Override
    //                    public void accept(Throwable throwable) throws Exception {
    //                        // Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
    //                        Log.d("ERROR", "" + throwable.getMessage());
    //                    }
    //                })
    //        );
    //    }
    //
    //    private void getForecastWeatherInformationInFaren() {
    //        compositeDisposable.add(mService.getForcastWeatherByLatLon(
    //                String.valueOf(Common.current_location.getLatitude()),
    //                String.valueOf(Common.current_location.getLongitude()),
    //                Common.APP_ID,
    //                "imperial")
    //                .subscribeOn(Schedulers.io())
    //                .observeOn(AndroidSchedulers.mainThread())
    //                .subscribe(new Consumer<WeatherForecastResult>() {
    //                    @Override
    //                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
    //                        displayForecastWeather(weatherForecastResult);
    //                    }
    //                }, new Consumer<Throwable>() {
    //                    @Override
    //                    public void accept(Throwable throwable) throws Exception {
    //                        // Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
    //                        Log.d("ERROR", "" + throwable.getMessage());
    //                    }
    //                })
    //        );
    //    }

    private void getForecastWeatherInformation() {
        compositeDisposable.add(mService.getForcastWeatherByLatLon(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForecastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        // Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR", "" + throwable.getMessage());
                    }
                })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {
        txt_city_name.setText(new StringBuilder(weatherForecastResult.city.name));
        txt_geo_coord.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(getContext(), weatherForecastResult);
        recycler_forecast.setAdapter(adapter);
    }

}
