package com.yoxjames.coldsnap.ui.detail;

import com.yoxjames.coldsnap.core.BaseColdsnapActivityPresenter;
import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.PlantImage;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.reducer.PlantDetailReducer;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.ui.plantimage.PlantImageSaveRequest;
import com.yoxjames.coldsnap.ui.plantimage.PlantProfileImageViewModel;

import org.threeten.bp.Instant;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.yoxjames.coldsnap.util.CSUtils.EMPTY_UUID;

public class PlantDetailActivityPresenterImpl extends BaseColdsnapActivityPresenter implements PlantDetailPresenter
{
    private final PlantDetailMvpView view;
    private final PlantService plantService;
    private final TemperatureValueAdapter temperatureValueAdapter;
    private final ImageService imageService;
    private final PlantDetailReducer plantDetailReducer;

    private UUID plantUUID = EMPTY_UUID;

    @Inject
    public PlantDetailActivityPresenterImpl(
        PlantDetailMvpView view,
        PlantService plantService,
        TemperatureValueAdapter temperatureValueAdapter,
        ImageService imageService,
        PlantDetailReducer plantDetailReducer)
    {
        super(view);
        this.view = view;
        this.plantService = plantService;
        this.temperatureValueAdapter = temperatureValueAdapter;
        this.imageService = imageService;
        this.plantDetailReducer = plantDetailReducer;
    }

    @Override
    public void load()
    {
        super.load();

        plantUUID = view.getPlantUUID();

        if (!view.isNewPlant())
            disposables.add(
                Observable.zip(
                    plantService.getPlant(plantUUID),
                    imageService.getPlantImage(plantUUID),
                    plantDetailReducer::reduce)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::bindView));
        else
            view.bindView(PlantDetailViewModel.EMPTY);

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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(i -> view.bindView(PlantProfileImageViewModel.EMPTY)));
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
            .map(request -> Plant.builder()
                .name(request.getName())
                .scientificName(request.getScientificName())
                .minimumTolerance(temperatureValueAdapter.getTemperature(request.getTempVal()))
                .uuid(plantUUID)
                .build())
            .flatMap(plantService::savePlant)
            .subscribe(i -> view.finish()));
    }
}
