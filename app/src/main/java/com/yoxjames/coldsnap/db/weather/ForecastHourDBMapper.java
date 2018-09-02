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
        row.setTempK(forecastHour.getTemperature().getKelvin());
        row.setHourInstant(forecastHour.getHour().getEpochSecond());
        row.setFuzzK(0);
        row.setLat(forecastHour.getLat());
        row.setLon(forecastHour.getLon());

        return row;
    }

    public static ForecastHour mapToPOJO(ForecastHourRow forecastHourRow)
    {
        return ForecastHour.create(
                Instant.ofEpochSecond(forecastHourRow.getHourInstant()),
                Temperature.fromKelvin(forecastHourRow.getTempK()),
                UUID.fromString(forecastHourRow.getUuid()),
                forecastHourRow.getLat(),
                forecastHourRow.getLon(),
                Instant.ofEpochSecond(forecastHourRow.getSyncInstant()));
    }
}
