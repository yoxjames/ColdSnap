package com.yoxjames.coldsnap.db.image;

import com.yoxjames.coldsnap.model.PlantImage;

import org.threeten.bp.Instant;

import java.util.UUID;


public class PlantImageDBMapper
{
    public static PlantImageRow mapToDB(PlantImage plantImage)
    {
        PlantImageRow row = new PlantImageRow();
        row.setUuid(plantImage.getImageUUID().toString());
        row.setPlantUUID(plantImage.getPlantUUID().toString());
        row.setTitle(plantImage.getTitle());
        row.setImageFilename(plantImage.getFileName());
        row.setImageDate(plantImage.getImageDate().getEpochSecond());

        return row;
    }

    public static PlantImage mapToPOJO(PlantImageRow plantImageRow)
    {
        return PlantImage.builder()
                .setTitle(plantImageRow.getTitle())
                .setImageDate(Instant.ofEpochSecond(plantImageRow.getImageDate()))
                .setFileName(plantImageRow.getImageFilename())
                .setImageUUID(UUID.fromString(plantImageRow.getUuid()))
                .setPlantUUID(UUID.fromString(plantImageRow.getPlantUUID()))
                .build();
    }
}
