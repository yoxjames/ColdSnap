package com.yoxjames.coldsnap.db;

import com.yoxjames.coldsnap.db.weather.ForecastHourDBMapper;
import com.yoxjames.coldsnap.db.weather.ForecastHourRowDAO;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;

import org.threeten.bp.Instant;

import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class WeatherDataDAOImpl implements WeatherDataDAO
{
    private final ForecastHourRowDAO forecastHourRowDAO;

    @Inject
    public WeatherDataDAOImpl(ForecastHourRowDAO forecastHourRowDAO)
    {
        this.forecastHourRowDAO = forecastHourRowDAO;
    }

    @Override
    public Completable saveWeatherData(WeatherData weatherData)
    {
        return Completable.create(e ->
        {
            forecastHourRowDAO.addForecastHours(
                weatherData.getForecastHours().stream()
                    .map(forecastHour -> ForecastHourDBMapper.mapToDB(weatherData, forecastHour))
                    .collect(Collectors.toList()));
            e.onComplete();
        })
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<WeatherData> getWeatherData(SimpleWeatherLocation weatherLocation) {
        return Observable.fromCallable(() -> forecastHourRowDAO.getForecastHours(weatherLocation.getLat(), weatherLocation.getLon()))
                .filter(list -> !list.isEmpty())
                .map(forecastHours -> WeatherData.builder()
                    .syncInstant(Instant.ofEpochSecond(forecastHours.get(0).getSyncInstant()))
                    .forecastHours(forecastHours.stream()
                        .map(ForecastHourDBMapper::mapToPOJO)
                        .collect(Collectors.toList()))
                    .weatherLocation(WeatherLocation.builder()
                        .setPlaceString("")
                        .setLat(forecastHours.get(0).getLat())
                        .setLon(forecastHours.get(0).getLon())
                        .build())
                .build())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable deleteWeatherData()
    {
        return Completable.create(e ->
        {
            forecastHourRowDAO.clearForecastHourRows();
            e.onComplete();
        })
            .subscribeOn(Schedulers.io());
    }
}
