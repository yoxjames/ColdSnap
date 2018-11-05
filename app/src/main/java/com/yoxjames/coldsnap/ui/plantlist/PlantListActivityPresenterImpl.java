package com.yoxjames.coldsnap.ui.plantlist;

import com.yoxjames.coldsnap.core.BaseColdsnapActivityPresenter;
import com.yoxjames.coldsnap.reducer.PlantListItemReducer;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.weather.WeatherService;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PlantListActivityPresenterImpl extends BaseColdsnapActivityPresenter implements PlantListPresenter
{
    private final PlantListMvpView view;
    private final PlantService plantService;
    private final ImageService imageService;
    private final WeatherService weatherService;
    private final WeatherLocationService weatherLocationService;
    private final PlantListItemReducer plantListItemReducer;

    public PlantListActivityPresenterImpl(
        PlantListMvpView view,
        PlantService plantService,
        ImageService imageService,
        WeatherService weatherService,
        WeatherLocationService weatherLocationService,
        PlantListItemReducer plantListItemReducer)
    {
        super(view);
        this.view = view;
        this.plantService = plantService;
        this.imageService = imageService;
        this.weatherService = weatherService;
        this.weatherLocationService = weatherLocationService;
        this.plantListItemReducer = plantListItemReducer;
    }

    @Override
    public void load()
    {
        super.load();

        // Load plant list view
        disposables.add(plantService.getPlants()
            .concatMap(p -> Observable.zip(
                Observable.just(p),
                weatherService.getWeatherData(),
                imageService.getPlantImage(p.getUuid()),
                plantListItemReducer::reduce))
            .toList()
            .map(PlantListViewModel::create)
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
    }

    @Override
    public void loadMenu()
    {
        menuDisposables.add(view.locationClicks().subscribe(i -> view.requestLocation()));
        menuDisposables.add(view.newPlantRequests().subscribe(i -> view.newPlant()));
    }
}
