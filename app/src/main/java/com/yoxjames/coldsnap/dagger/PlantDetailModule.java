package com.yoxjames.coldsnap.dagger;

import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.reducer.PlantDetailTemperaturePickerReducer;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.ui.detail.PlantDetailMvpView;
import com.yoxjames.coldsnap.ui.detail.PlantDetailPresenter;
import com.yoxjames.coldsnap.ui.detail.PlantDetailPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class PlantDetailModule
{
    private final PlantDetailMvpView view;

    public PlantDetailModule(PlantDetailMvpView view)
    {
        this.view = view;
    }

    @Provides
    static PlantDetailPresenter providePlantDetailPresenter(
        PlantDetailMvpView view,
        PlantService plantService,
        TemperatureValueAdapter temperatureValueAdapter,
        ImageService imageService,
        PlantDetailTemperaturePickerReducer plantDetailTemperaturePickerReducer)
    {
        return new PlantDetailPresenterImpl(view, plantService, temperatureValueAdapter, imageService, plantDetailTemperaturePickerReducer);
    }

    @Provides
    PlantDetailMvpView providePlantDetailMvpView()
    {
        return view;
    }

}
