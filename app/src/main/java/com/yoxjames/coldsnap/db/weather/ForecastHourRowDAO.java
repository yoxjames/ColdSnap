package com.yoxjames.coldsnap.db.weather;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ForecastHourRowDAO
{
    @Insert
    void addForecastHours(List<ForecastHourRow> rows);

    @Query("DELETE FROM forecast_hour")
    void clearForecastHourRows();

    @Query("SELECT * FROM forecast_hour WHERE lat = :lat AND lon = :lon")
    List<ForecastHourRow> getForecastHours(double lat, double lon);
}
