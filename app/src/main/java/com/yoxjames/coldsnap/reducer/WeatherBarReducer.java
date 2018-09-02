package com.yoxjames.coldsnap.reducer;

import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherBarViewModel;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

public class WeatherBarReducer
{
    private final TemperatureFormatter temperatureFormatter;

    public WeatherBarReducer(TemperatureFormatter temperatureFormatter)
    {
        this.temperatureFormatter = temperatureFormatter;
    }

    public WeatherBarViewModel reduce(WeatherData weatherData)
    {
        return WeatherBarViewModel.create(
            weatherData.getWeatherLocation().getPlaceString(),
            temperatureFormatter.format(weatherData.getTodayHigh().getTemperature()),
            temperatureFormatter.format(weatherData.getTodayLow().getTemperature()),
            localizeDateTime(weatherData.getSyncInstant()),
            false,
            "");
    }


    private String localizeDateTime(Instant instant)
    {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter localFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

        return localDateTime.format(localFormatter);
    }
}
