package com.yoxjames.coldsnap.ui.plantlist;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.Temperature.TemperatureComparison;
import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.ui.AbstractBaseColdsnapPresenter;
import com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.PlantStatus;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherBarViewModel;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.yoxjames.coldsnap.model.Temperature.GREATER;
import static com.yoxjames.coldsnap.model.Temperature.LESSER;
import static com.yoxjames.coldsnap.model.Temperature.MAYBE_GREATER;
import static com.yoxjames.coldsnap.model.Temperature.MAYBE_LESSER;
import static com.yoxjames.coldsnap.model.Temperature.TRUE_EQUAL;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.DEAD;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.ERROR;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.HAPPY;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.NEUTRAL;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.SAD;

public class PlantListPresenterImpl extends AbstractBaseColdsnapPresenter implements PlantListPresenter
{
    private final PlantListMvpView view;
    private final PlantService plantService;
    private final ImageService imageService;
    private final WeatherService weatherService;
    private final WeatherLocationService weatherLocationService;
    private final TemperatureFormatter temperatureFormatter;

    public PlantListPresenterImpl(
        PlantListMvpView view,
        PlantService plantService,
        ImageService imageService,
        WeatherService weatherService,
        WeatherLocationService weatherLocationService,
        TemperatureFormatter temperatureFormatter)
    {
        super(view);
        this.view = view;
        this.plantService = plantService;
        this.imageService = imageService;
        this.weatherService = weatherService;
        this.weatherLocationService = weatherLocationService;
        this.temperatureFormatter = temperatureFormatter;
    }

    @Override
    public void load()
    {
        super.load();

        // Load plant list view
        disposables.add(plantService.getPlants()
            .concatMap(p -> Observable.just(p)
                .zipWith(weatherService.getWeatherData(), (plant, weatherData) ->
                    PlantListItemViewModel.builder()
                        .setUUID(plant.getUuid())
                        .setPlantName(plant.getName())
                        .setPlantScientificName(plant.getScientificName())
                        .setPlantStatus(getPlantStatus(plant, weatherData))
                        .build()))
            .concatMap(vm -> imageService.getPlantImage(vm.getUUID())
                .map(plantImage -> vm.toBuilder()
                    .setImageFileName(plantImage.getFileName())
                    .build())
                .single(vm)
                .toObservable())
            .toList()
            .map(PlantListViewModel::create)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::bindView));

        // Weather preview bar load
        disposables.add(weatherService.getWeatherData()
            .map(weatherData -> WeatherBarViewModel.create(
                weatherData.getWeatherLocation().getPlaceString(),
                temperatureFormatter.format(weatherData.getTodayHigh().getTemperature()),
                temperatureFormatter.format(weatherData.getTodayLow().getTemperature()),
                localizeDateTime(weatherData.getSyncInstant()),
                false,
                ""))
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
        menuDisposables.add(view.settingsRequests().subscribe(i -> view.openSettings()));
        menuDisposables.add(view.aboutRequests().subscribe(i -> view.openAbout()));
    }

    @PlantStatus
    private int getPlantStatus(Plant plant, WeatherData weatherData)
    {
        @TemperatureComparison int temperatureComparison =
            plant.getMinimumTolerance().compareSignificanceTo(weatherData.getTodayLow().getTemperature());

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

    private String localizeDateTime(Instant instant)
    {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter localFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

        return localDateTime.format(localFormatter);
    }
}
