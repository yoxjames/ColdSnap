package com.yoxjames.coldsnap.db.weather;

import com.yoxjames.coldsnap.model.ForecastHour;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.Windspeed;

import org.threeten.bp.Instant;

import java.util.UUID;

public class ForecastHourDBMapper
{
    public static ForecastHourRow mapToDB(WeatherData weatherData, ForecastHour forecastHour)
    {
        ForecastHourRow row = new ForecastHourRow();
        row.setUuid(forecastHour.getUuid().toString());
        row.setSyncInstant(weatherData.getSyncInstant().getEpochSecond());
        row.setTempK(forecastHour.getTemperature().getKelvin());
        row.setWindSpeedMps(forecastHour.getWindspeed().getMetersPerSecond());
        row.setHourInstant(forecastHour.getHour().getEpochSecond());
        row.setFuzzK(0);
        row.setLat(weatherData.getWeatherLocation().getLat());
        row.setLon(weatherData.getWeatherLocation().getLon());

        return row;
    }

    public static ForecastHour mapToPOJO(ForecastHourRow forecastHourRow)
    {
        return ForecastHour.builder()
            .hour(Instant.ofEpochSecond(forecastHourRow.getHourInstant()))
            .temperature(Temperature.fromKelvin(forecastHourRow.getTempK()))
            .windspeed(Windspeed.fromMetersPerSecond(forecastHourRow.getWindSpeedMps()))
            .uuid(UUID.fromString(forecastHourRow.getUuid()))
            .build();
    }
}
