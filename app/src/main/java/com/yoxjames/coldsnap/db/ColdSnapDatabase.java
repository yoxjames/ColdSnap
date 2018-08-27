package com.yoxjames.coldsnap.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.yoxjames.coldsnap.db.image.PlantImageRow;
import com.yoxjames.coldsnap.db.image.PlantImageRowDAO;
import com.yoxjames.coldsnap.db.plant.PlantRow;
import com.yoxjames.coldsnap.db.plant.PlantRowDAO;
import com.yoxjames.coldsnap.db.weather.ForecastHourRow;
import com.yoxjames.coldsnap.db.weather.ForecastHourRowDAO;

@Database(
        entities = { PlantRow.class, ForecastHourRow.class, PlantImageRow.class },
        version = 2,
        exportSchema = false) // TODO: Maybe we should export the schema?
public abstract class ColdSnapDatabase extends RoomDatabase
{
    public abstract PlantRowDAO plantRowDAO();
    public abstract PlantImageRowDAO plantImageRowDAO();
    public abstract ForecastHourRowDAO forecastHourRowDAO();
}
