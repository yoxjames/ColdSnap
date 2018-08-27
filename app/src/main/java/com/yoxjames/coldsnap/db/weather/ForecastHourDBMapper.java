package com.yoxjames.coldsnap.db.weather;

import com.yoxjames.coldsnap.model.ForecastHour;
import com.yoxjames.coldsnap.model.Temperature;

import org.threeten.bp.Instant;

import java.util.UUID;

public class ForecastHourDBMapper
{
    public static ForecastHourRow mapToDB(Instant syncInstant, ForecastHour forecastHour)
    {
        ForecastHourRow row = new ForecastHourRow();
        row.setUuid(forecastHour.getUuid().toString());
        row.setSyncInstant(syncInstant.getEpochSecond());
        row.setTempK(forecastHour.getTemperature().getDegreesKelvin());
        row.setHourInstant(forecastHour.getHour().getEpochSecond());
        row.setFuzzK(forecastHour.getTemperature().getFuzz());
        row.setLat(forecastHour.getLat());
        row.setLon(forecastHour.getLon());

        return row;
    }

    public static ForecastHour mapToPOJO(ForecastHourRow forecastHourRow)
    {
        return ForecastHour.create(
                Instant.ofEpochSecond(forecastHourRow.getHourInstant()),
                new Temperature(forecastHourRow.getTempK(), forecastHourRow.getFuzzK()),
                UUID.fromString(forecastHourRow.getUuid()),
                forecastHourRow.getLat(),
                forecastHourRow.getLon(),
                Instant.ofEpochSecond(forecastHourRow.getSyncInstant()));
    }
}
