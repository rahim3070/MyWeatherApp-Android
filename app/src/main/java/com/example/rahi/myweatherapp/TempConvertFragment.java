package com.example.rahi.myweatherapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rahi.myweatherapp.Common.Common;
import com.example.rahi.myweatherapp.Model.WeatherResult;
import com.example.rahi.myweatherapp.Retrofit.IOpenWeatherMap;
import com.example.rahi.myweatherapp.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TempConvertFragment extends Fragment {
    TextView textView, switchValue;
    TextView txt_city_name, txt_temperature, ans1, ans2, answer1, answer2;
    //Button convert;
    Switch convert;

    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static TempConvertFragment instance;

    public static TempConvertFragment getInstances() {
        if (instance == null) {
            instance = new TempConvertFragment();
        }

        return instance;
    }

    public TempConvertFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_temp_convert, container, false);

        txt_city_name = itemView.findViewById(R.id.txt_city_name);
        txt_temperature = itemView.findViewById(R.id.txt_temperature);
        convert = itemView.findViewById(R.id.convert);

        weather_panel = itemView.findViewById(R.id.weather_panel);
        loading = itemView.findViewById(R.id.loading);

        textView = itemView.findViewById(R.id.swOnOff);
        switchValue = getActivity().findViewById(R.id.switchOnOff);

        String vSwitch = switchValue.getText().toString();
        textView.setText(vSwitch);

        ans1 = itemView.findViewById(R.id.ans1);
        ans2 = itemView.findViewById(R.id.ans2);
        answer1 = itemView.findViewById(R.id.answer1);
        answer2 = itemView.findViewById(R.id.answer2);

        getWeatherInformation();

        //        convert.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
        //            }
        //        });

        convert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {

                    ans1.setText("Fahrenheit");
                    ans2.setText("Kelvin");

                    float number = Float.valueOf(txt_temperature.getText().toString());

                    float vFar = ((number * 9 / 5) + 32);
                    answer1.setText(Float.valueOf(vFar).toString());

                    float vKel = (number + (float) 273.15);
                    answer2.setText(Float.valueOf(vKel).toString());
                } else {
                    ans1.setText("");
                    ans2.setText("");
                    answer1.setText("");
                    answer2.setText("");
                }
            }
        });

        return itemView;
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mService.getWeatherByLatLon(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        // Load Information
                        txt_city_name.setText(weatherResult.getName());
                        // txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
                        // .append("°C").toString());
                        txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).toString());

                        // Display Information Panel
                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
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

    //    private fun convertFunc() {
    //        val temperatureTxt = findViewById < View > (R.id.temperature) as EditText
    //        val temperature = temperatureTxt.text.toString().trim()
    //        val tempNo = Integer.parseInt(temperature)
    //        val format = typeSpin.selectedItem.toString().trim()
    //        val answer1 = findViewById < View > (R.id.answer1) as TextView
    //        val answer2 = findViewById < View > (R.id.answer2) as TextView
    //        val ans1 = findViewById < View > (R.id.ans1) as TextView
    //        val ans2 = findViewById < View > (R.id.ans2) as TextView
    //
    //        if (!temperature.isEmpty()) {
    //            if (format == "Celsius") {
    //                ans1.text = "Fahrenheit"
    //                ans2.text = "Kelvin"
    //                answer1.text = ((tempNo * 9 / 5) + 32).toString()
    //                answer2.text = (tempNo + 273.15).toString()
    //            } else if (format == "Kelvin") {
    //                ans1.text = "Fahrenheit"
    //                ans2.text = "Celsius"
    //                answer1.text = ((tempNo - 273.15) * 9 / 5 + 32).toString()
    //                answer2.text = (tempNo - 273.15).toString()
    //            } else if (format == "Fahrenheit") {
    //                ans1.text = "Celsius"
    //                ans2.text = "Kelvin"
    //                answer1.text = ((tempNo - 52) * 5 / 9).toString()
    //                answer2.text = ((tempNo - 32) * 5 / 9 + 273.15).toString()
    //            }
    //        }
    //
    //    }
}
