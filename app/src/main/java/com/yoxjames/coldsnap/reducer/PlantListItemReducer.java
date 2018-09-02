package com.yoxjames.coldsnap.reducer;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.PlantImage;
import com.yoxjames.coldsnap.model.TemperatureComparator;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel;

import javax.inject.Inject;

import static com.yoxjames.coldsnap.model.TemperatureComparator.*;
import static com.yoxjames.coldsnap.model.TemperatureComparator.GREATER;
import static com.yoxjames.coldsnap.model.TemperatureComparator.LESSER;
import static com.yoxjames.coldsnap.model.TemperatureComparator.MAYBE_GREATER;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.DEAD;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.ERROR;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.HAPPY;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.NEUTRAL;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.SAD;

public class PlantListItemReducer
{
    private final TemperatureComparator temperatureComparator;

    @Inject
    public PlantListItemReducer(TemperatureComparator temperatureComparator)
    {
        this.temperatureComparator = temperatureComparator;
    }

    public PlantListItemViewModel reduce(Plant plant)
    {
        return PlantListItemViewModel.builder()
            .setPlantName(plant.getName())
            .setPlantScientificName(plant.getScientificName())
            .setUUID(plant.getUuid())
            .setImageFileName("")
            .build();
    }

    public PlantListItemViewModel reduce(Plant plant, WeatherData weatherData)
    {
        return reduce(plant).toBuilder().setPlantStatus(getPlantStatus(plant, weatherData)).build();
    }

    public PlantListItemViewModel reduce(Plant plant, WeatherData weatherData, PlantImage plantImage)
    {
        return reduce(plant, weatherData).toBuilder().setImageFileName(plantImage.getFileName()).build();
    }

    public PlantListItemViewModel reduce(Plant plant, PlantImage plantImage)
    {
        return reduce(plant).toBuilder().setImageFileName(plantImage.getFileName()).build();
    }

    private int getPlantStatus(Plant plant, WeatherData weatherData)
    {
        @TemperatureComparison int temperatureComparison = temperatureComparator.compare(
            plant.getMinimumTolerance(),
            weatherData.getTodayLow().getTemperature());

        switch (temperatureComparison)
        {
            case GREATER:
                return DEAD;
            case LESSER:
                return HAPPY;
            case MAYBE_GREATER:
                return SAD;
            case MAYBE_LESSER:
                return NEUTRAL;
            case TRUE_EQUAL:
                return NEUTRAL;
        }

        return ERROR;
    }


}
