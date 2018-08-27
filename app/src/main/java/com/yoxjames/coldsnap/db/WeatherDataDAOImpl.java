package com.yoxjames.coldsnap.db;

import com.yoxjames.coldsnap.db.weather.ForecastHourDBMapper;
import com.yoxjames.coldsnap.db.weather.ForecastHourRow;
import com.yoxjames.coldsnap.db.weather.ForecastHourRowDAO;
import com.yoxjames.coldsnap.model.ForecastHour;
import com.yoxjames.coldsnap.model.ForecastHourUtil;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;

import java.util.ArrayList;
import java.util.List;

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
            List<ForecastHourRow> forecastHourRowList = new ArrayList<>();

            for (ForecastHour forecastHour : weatherData.getForecastHours())
                forecastHourRowList.add(ForecastHourDBMapper.mapToDB(weatherData.getSyncInstant(), forecastHour));

            forecastHourRowDAO.addForecastHours(forecastHourRowList);
            e.onComplete();
        })
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<WeatherData> getWeatherData(SimpleWeatherLocation weatherLocation) {
        return Observable.fromCallable(() -> forecastHourRowDAO.getForecastHours(weatherLocation.getLat(), weatherLocation.getLon()))
                .concatMap(Observable::fromIterable)
                .map(ForecastHourDBMapper::mapToPOJO)
                .toList()
                .filter(list -> !list.isEmpty())
                .map(forecastHours -> WeatherData.builder()
                    .syncInstant(forecastHours.get(0).getSyncTime())
                    .forecastHours(forecastHours)
                    .todayHigh(ForecastHourUtil.getDailyHigh(forecastHours))
                    .todayLow(ForecastHourUtil.getDailyLow(forecastHours))
                    .weatherLocation(WeatherLocation.builder()
                        .setPlaceString("")
                        .setLat(forecastHours.get(0).getLat())
                        .setLon(forecastHours.get(0).getLon())
                        .build())
                .build())
                .toObservable()
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
