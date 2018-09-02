package com.yoxjames.coldsnap.ui.plantlist;

import com.yoxjames.coldsnap.reducer.PlantListItemReducer;
import com.yoxjames.coldsnap.reducer.WeatherBarReducer;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.ui.AbstractBaseColdsnapPresenter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PlantListPresenterImpl extends AbstractBaseColdsnapPresenter implements PlantListPresenter
{
    private final PlantListMvpView view;
    private final PlantService plantService;
    private final ImageService imageService;
    private final WeatherService weatherService;
    private final WeatherLocationService weatherLocationService;
    private final PlantListItemReducer plantListItemReducer;
    private final WeatherBarReducer weatherBarReducer;

    public PlantListPresenterImpl(
        PlantListMvpView view,
        PlantService plantService,
        ImageService imageService,
        WeatherService weatherService,
        WeatherLocationService weatherLocationService,
        PlantListItemReducer plantListItemReducer,
        WeatherBarReducer weatherBarReducer)
    {
        super(view);
        this.view = view;
        this.plantService = plantService;
        this.imageService = imageService;
        this.weatherService = weatherService;
        this.weatherLocationService = weatherLocationService;
        this.plantListItemReducer = plantListItemReducer;
        this.weatherBarReducer = weatherBarReducer;
    }

    @Override
    public void load()
    {
        super.load();

        // Load plant list view
        disposables.add(plantService.getPlants()
            .concatMap(p -> Observable.zip(Observable.just(p), weatherService.getWeatherData(), imageService.getPlantImage(p.getUuid()), plantListItemReducer::reduce)
                .singleOrError()
                .onErrorResumeNext(Observable.zip(Observable.just(p), weatherService.getWeatherData(), plantListItemReducer::reduce).singleOrError())
                .onErrorResumeNext(Observable.zip(Observable.just(p), imageService.getPlantImage(p.getUuid()), plantListItemReducer::reduce).singleOrError())
                .onErrorResumeNext(Observable.just(p).map(plantListItemReducer::reduce).singleOrError())
                .toObservable())
            .firstOrError()
            .toObservable()
            .toList()
            .map(PlantListViewModel::create)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::bindView, Throwable::printStackTrace));

        // Weather preview bar load
        disposables.add(weatherService.getWeatherData()
            .map(weatherBarReducer::reduce)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::bindView, Throwable::printStackTrace));

        // When clicking on a plant in the list view open the detail view
        disposables.add(view.plantItemClicks()
            .subscribe(view::openPlant));

        disposables.add(view.locationChange()
            .observeOn(Schedulers.io())
            .doOnNext(loc -> weatherLocationService.saveWeatherLocation(loc).blockingAwait())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(simpleWeatherLocation -> load(), Throwable::printStackTrace));

        disposables.add(imageService.cleanImagesDirectory()
            .subscribe());

        disposables.add(view.retryConnection()
            .subscribe(i -> load()));
    }

    @Override
    public void loadMenu()
    {
        menuDisposables.add(view.locationClicks().subscribe(i -> view.requestLocation()));
        menuDisposables.add(view.newPlantRequests().subscribe(i -> view.newPlant()));
        menuDisposables.add(view.settingsRequests().subscribe(i -> view.openSettings()));
        menuDisposables.add(view.aboutRequests().subscribe(i -> view.openAbout()));
    }
}
