package com.yoxjames.coldsnap.reducer;

import com.yoxjames.coldsnap.model.ForecastHourUtil;
import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.PlantImage;
import com.yoxjames.coldsnap.model.TemperatureComparator;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel;

import javax.inject.Inject;

import static com.yoxjames.coldsnap.model.TemperatureComparator.GREATER;
import static com.yoxjames.coldsnap.model.TemperatureComparator.LESSER;
import static com.yoxjames.coldsnap.model.TemperatureComparator.MAYBE_GREATER;
import static com.yoxjames.coldsnap.model.TemperatureComparator.MAYBE_LESSER;
import static com.yoxjames.coldsnap.model.TemperatureComparator.TRUE_EQUAL;
import static com.yoxjames.coldsnap.model.TemperatureComparator.TemperatureComparison;
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

    public PlantListItemViewModel reduce(Plant plant, WeatherData weatherData, PlantImage plantImage)
    {
        return PlantListItemViewModel.builder()
            .plantName(plant.getName())
            .plantScientificName(plant.getScientificName())
            .uuid(plant.getUuid())
            .imageFileName(plantImage.getFileName())
            .plantStatus(getPlantStatus(plant, weatherData))
            .build();
    }

    private int getPlantStatus(Plant plant, WeatherData weatherData)
    {
        @TemperatureComparison int temperatureComparison = temperatureComparator.compare(
            plant.getMinimumTolerance(),
            ForecastHourUtil.getDailyLow(weatherData.getForecastHours()));

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
