package com.yoxjames.coldsnap.reducer;

import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.ui.cards.graph.GraphCardViewModel;
import com.yoxjames.coldsnap.ui.feed.FeedViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

public class FeedReducer
{
    private final TemperatureCardReducer temperatureCardReducer;
    private final WindspeedCardReducer windspeedCardReducer;

    @Inject
    public FeedReducer(TemperatureCardReducer temperatureCardReducer, WindspeedCardReducer windspeedCardReducer)
    {
        this.temperatureCardReducer = temperatureCardReducer;
        this.windspeedCardReducer = windspeedCardReducer;
    }

    public FeedViewModel reduce(WeatherData weatherData)
    {
        ArrayList<GraphCardViewModel> cardVMList = new ArrayList<>();
        cardVMList.add(temperatureCardReducer.reduce(weatherData));
        cardVMList.add(windspeedCardReducer.reduce(weatherData));
        return FeedViewModel.create(cardVMList);
    }
}
