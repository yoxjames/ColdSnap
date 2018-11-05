package com.yoxjames.coldsnap.ui.feed;

import com.yoxjames.coldsnap.core.BaseColdsnapActivityPresenter;
import com.yoxjames.coldsnap.reducer.FeedReducer;
import com.yoxjames.coldsnap.service.weather.WeatherService;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class FeedPresenterImpl extends BaseColdsnapActivityPresenter<FeedMvpView>
{
    private final WeatherService weatherService;
    private final FeedReducer feedReducer;

    @Inject
    public FeedPresenterImpl(FeedMvpView view, WeatherService weatherService, FeedReducer feedReducer)
    {
        super(view);
        this.weatherService = weatherService;
        this.feedReducer = feedReducer;
    }

    @Override
    public void load()
    {
        super.load();
        disposables.add(weatherService.getWeatherData()
            .map(feedReducer::reduce)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::bindView));
    }

    @Override
    public void loadMenu()
    {

    }

    @Override
    public void unload()
    {
        super.unload();
    }
}
