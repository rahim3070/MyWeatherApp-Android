package com.example.rahi.myweatherapp.Retrofit;

import com.example.rahi.myweatherapp.Model.WeatherForecastResult;
import com.example.rahi.myweatherapp.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLon(@Query("lat") String lat,
                                                 @Query("lon") String lon,
                                                 @Query("appid") String appid,
                                                 @Query("units") String unit);

    @GET("weather")
    Observable<WeatherResult> getWeatherByCityName(@Query("q") String cityName,
                                                 @Query("appid") String appid,
                                                 @Query("units") String unit);

    @GET("forecast")
    Observable<WeatherForecastResult> getForcastWeatherByLatLon(@Query("lat") String lat,
                                                                @Query("lon") String lon,
                                                                @Query("appid") String appid,
                                                                @Query("units") String unit);
}
