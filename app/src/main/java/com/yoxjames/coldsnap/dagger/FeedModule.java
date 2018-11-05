package com.yoxjames.coldsnap.dagger;

import com.yoxjames.coldsnap.reducer.FeedReducer;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.ui.feed.FeedMvpView;
import com.yoxjames.coldsnap.ui.feed.FeedPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class FeedModule
{
    private final FeedMvpView view;

    public FeedModule(FeedMvpView view)
    {
        this.view = view;
    }

    @Provides
    public FeedPresenterImpl provideFeedPresenter(FeedMvpView view, WeatherService weatherService, FeedReducer feedReducer)
    {
        return new FeedPresenterImpl(view, weatherService, feedReducer);
    }

    @Provides
    public FeedMvpView provideFeedMvpView()
    {
        return view;
    }
}
