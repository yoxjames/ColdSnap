package com.yoxjames.coldsnap.db.plant;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.Temperature;

import java.util.UUID;

public class PlantDBMapper
{
    public static PlantRow mapToDB(Plant plant)
    {
        PlantRow row = new PlantRow();
        row.setUuid(plant.getUuid().toString());
        row.setName(plant.getName());
        row.setScientificName(plant.getScientificName());
        row.setColdThresholdK(plant.getMinimumTolerance().getKelvin());

        return row;
    }

    public static Plant mapToPOJO(PlantRow plantRow)
    {
        return Plant.builder()
            .name(plantRow.getName())
            .scientificName(plantRow.getScientificName())
            .minimumTolerance(Temperature.fromKelvin(plantRow.getColdThresholdK()))
            .uuid(UUID.fromString(plantRow.getUuid()))
            .build();
    }
}
