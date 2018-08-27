package com.yoxjames.coldsnap.ui.detail;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.PlantImage;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.reducer.PlantDetailTemperaturePickerReducer;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.ui.AbstractBaseColdsnapPresenter;
import com.yoxjames.coldsnap.ui.plantimage.PlantImageSaveRequest;
import com.yoxjames.coldsnap.ui.plantimage.PlantProfileImageViewModel;

import org.threeten.bp.Instant;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.yoxjames.coldsnap.util.CSUtils.EMPTY_UUID;

public class PlantDetailPresenterImpl extends AbstractBaseColdsnapPresenter implements PlantDetailPresenter
{
    private final PlantDetailMvpView view;
    private final PlantService plantService;
    private final TemperatureValueAdapter temperatureValueAdapter;
    private final ImageService imageService;
    private final PlantDetailTemperaturePickerReducer plantDetailTemperaturePickerReducer;

    private UUID plantUUID = EMPTY_UUID;

    @Inject
    public PlantDetailPresenterImpl(
        PlantDetailMvpView view,
        PlantService plantService,
        TemperatureValueAdapter temperatureValueAdapter,
        ImageService imageService,
        PlantDetailTemperaturePickerReducer plantDetailTemperaturePickerReducer)
    {
        super(view);
        this.view = view;
        this.plantService = plantService;
        this.temperatureValueAdapter = temperatureValueAdapter;
        this.imageService = imageService;
        this.plantDetailTemperaturePickerReducer = plantDetailTemperaturePickerReducer;
    }

    @Override
    public void load()
    {
        super.load();

        plantUUID = view.getPlantUUID();

        Observable<Plant> cachedPlant = plantService.getPlant(plantUUID).cache();

        if (!view.isNewPlant())
            disposables.add(
                cachedPlant
                .map(this::mapToVM)
                .flatMap(vm -> imageService.getPlantImage(plantUUID)
                    .map(plantImage -> composeImageToVM(vm, plantImage))
                    .first(vm)
                    .toObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::bindView));
        else
        {
            view.bindView(PlantDetailViewModel.EMPTY
                .toBuilder()
                .setTemperaturePickerViewModel(plantDetailTemperaturePickerReducer.reduce())
                .build());
        }

        disposables.add(view.takeProfileImageRequests()
            .observeOn(Schedulers.io())
            .map(UUID::toString)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::takePhoto));

        disposables.add(view.imageSaveRequests()
            .observeOn(Schedulers.io())
            .map(fileName -> PlantImageSaveRequest.builder()
                .setFileName(fileName)
                .setPhotoTime(Instant.now())
                .setProfilePicture(true)
                .setPlantUUID(plantUUID)
                .build())
            .map(PlantImage::fromPlantImageSaveRequest)
            .flatMap(imageService::savePlantImage)
            .subscribe());

        disposables.add(view.uiStateModifications()
            .subscribe(i -> view.enableSave()));

        disposables.add(view.imageDeleteRequests()
            .flatMap(imageService::deleteImagesForPlant)
            .subscribe());
    }

    @Override
    public void loadMenu()
    {
        menuDisposables.add(view.plantDeleteRequests()
            .map(i -> plantUUID)
            .observeOn(Schedulers.io())
            .flatMap(plantUUID -> Observable.concat(
                plantService.deletePlant(plantUUID),
                imageService.deleteImagesForPlant(plantUUID)))
            .subscribe(actionReply -> view.finish()));

        menuDisposables.add(view.plantDetailSaves()
            .observeOn(Schedulers.io())
            .map(request ->
                Plant.create(request.getName(), request.getScientificName(), temperatureValueAdapter.getTemperature(request.getTempVal()), plantUUID))
            .flatMap(plantService::savePlant)
            .subscribe(i -> view.finish()));
    }

    private PlantDetailViewModel mapToVM(Plant plant)
    {
        return PlantDetailViewModel.builder()
            .setName(plant.getName())
            .setScientificName(plant.getScientificName())
            .setTemperaturePickerViewModel(plantDetailTemperaturePickerReducer.reduce(plant))
            .setPlantProfileImageViewModel(PlantProfileImageViewModel.EMPTY)
            .build();
    }

    private PlantDetailViewModel composeImageToVM(PlantDetailViewModel vm, PlantImage plantImage)
    {
        return vm.withPlantProfileImageViewModel(PlantProfileImageViewModel.builder()
            .setImageURL(plantImage.getFileName())
            .setPlantUUID(plantImage.getPlantUUID())
            .setTakeImageAvailable(true)
            .build());
    }
}
