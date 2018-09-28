package com.example.rahi.myweatherapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rahi.myweatherapp.Common.Common;
import com.example.rahi.myweatherapp.Model.WeatherResult;
import com.example.rahi.myweatherapp.Retrofit.IOpenWeatherMap;
import com.example.rahi.myweatherapp.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment {

    private List<String> lstCities;
    private MaterialSearchBar searchBar;

    TextView textView, switchValue;
    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_temperature, txt_unit, txt_description, txt_date_time, txt_day_name, txt_wind, txt_mintemp, txt_minunit, txt_maxtemp, txt_maxunit, txt_geo_coord;
    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static CityFragment instance;

    public static CityFragment getInstances() {
        if (instance == null) {
            instance = new CityFragment();
        }

        return instance;
    }

    public CityFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_city, container, false);

        img_weather = itemView.findViewById(R.id.img_weather);
        txt_city_name = itemView.findViewById(R.id.txt_city_name);
        txt_humidity = itemView.findViewById(R.id.txt_humidity);
        txt_sunrise = itemView.findViewById(R.id.txt_sunrise);
        txt_sunset = itemView.findViewById(R.id.txt_sunset);
        txt_mintemp = itemView.findViewById(R.id.txt_mintemp);
        txt_minunit = itemView.findViewById(R.id.txt_minunit);
        txt_maxtemp = itemView.findViewById(R.id.txt_maxtemp);
        txt_maxunit = itemView.findViewById(R.id.txt_maxunit);
        txt_pressure = itemView.findViewById(R.id.txt_pressure);
        txt_temperature = itemView.findViewById(R.id.txt_temperature);
        txt_unit = itemView.findViewById(R.id.txt_unit);
        txt_description = itemView.findViewById(R.id.txt_description);
        txt_date_time = itemView.findViewById(R.id.txt_date_time);
        txt_day_name = itemView.findViewById(R.id.txt_day_name);
        //txt_wind = itemView.findViewById(R.id.txt_wind);
        //txt_geo_coord = itemView.findViewById(R.id.txt_geo_coord);

        weather_panel = itemView.findViewById(R.id.weather_panel);
        loading = itemView.findViewById(R.id.loading);

        textView = itemView.findViewById(R.id.swOnOff);
        switchValue = getActivity().findViewById(R.id.switchOnOff);

        String vSwitch = switchValue.getText().toString();
        textView.setText(vSwitch);

        searchBar = itemView.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);

        // Asynctask class to load City List
        new LoadCities().execute();

        return itemView;
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {
        @Override
        protected List<String> doInBackgroundSimple() {
            lstCities = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);

                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);

                String readed;
                while ((readed = in.readLine()) != null)
                    builder.append(readed);

                lstCities = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return lstCities;
        }

        // Ctrl + O
        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for (String search : listCity) {
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }

                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());

                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);
            loading.setVisibility(View.GONE);
        }
    }

    private void getWeatherInformation(String cityName) {
        compositeDisposable.add(mService.getWeatherByCityName(
                cityName,
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        // Load Image
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_weather);

                        // Load Information
                        String vSw = textView.getText().toString();

                        if (vSw == "1") {
                            txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).toString());
                            float number = Float.valueOf(txt_temperature.getText().toString());
                            float vFar = ((number * 9 / 5) + 32);

                            //txt_temperature.setText(new StringBuilder(String.valueOf(Float.valueOf(vFar).toString())).toString());
                            txt_temperature.setText(new StringBuilder(String.valueOf(new DecimalFormat("##.##").format(Float.valueOf(vFar)))).toString());
                            txt_unit.setText("°F");
                            // txt_temperature.setText(new StringBuilder(String.valueOf(new DecimalFormat("##.##").format(converter.convertTempToFarenheiht(weatherResult.getMain().getTemp())))).append("°F").toString());

                            txt_mintemp.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp_min())).toString());
                            float minnumber = Float.valueOf(txt_mintemp.getText().toString());
                            float vMinFar = ((minnumber * 9 / 5) + 32);

                            //txt_mintemp.setText(new StringBuilder(String.valueOf(Float.valueOf(vMinFar).toString())).toString());
                            txt_mintemp.setText(new StringBuilder(String.valueOf(new DecimalFormat("##.##").format(Float.valueOf(vMinFar)))).toString());
                            txt_minunit.setText("°F");

                            txt_maxtemp.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp_max())).toString());
                            float maxnumber = Float.valueOf(txt_maxtemp.getText().toString());
                            float vMaxFar = ((maxnumber * 9 / 5) + 32);

                            //txt_maxtemp.setText(new StringBuilder(String.valueOf(Float.valueOf(vMaxFar).toString())).toString());
                            txt_maxtemp.setText(new StringBuilder(String.valueOf(new DecimalFormat("##.##").format(Float.valueOf(vMaxFar)))).toString());
                            txt_maxunit.setText("°F");
                        } else {
                            txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).toString());
                            txt_unit.setText("°C");

                            txt_mintemp.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp_min())).toString());
                            txt_minunit.setText("°C");

                            txt_maxtemp.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp_max())).toString());
                            txt_maxunit.setText("°C");
                        }

                        txt_city_name.setText(weatherResult.getName());
                        txt_description.setText(new StringBuilder("Weather in ").append(weatherResult.getName().toString()));
                        txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        txt_day_name.setText(Common.convertUnixToDay(weatherResult.getDt()));
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txt_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));

                        // txt_geo_coord.setText(new StringBuilder(weatherResult.getCoord().toString()).toString());

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
}
