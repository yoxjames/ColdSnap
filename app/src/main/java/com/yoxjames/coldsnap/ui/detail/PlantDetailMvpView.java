package com.yoxjames.coldsnap.ui.detail;

import com.yoxjames.coldsnap.ui.MvpView;

import java.util.UUID;

import io.reactivex.Observable;

public interface PlantDetailMvpView extends MvpView
{
    Observable<PlantDetailViewUpdate> plantDetailSaves();
    Observable<UUID> takeProfileImageRequests();
    Observable<String> imageSaveRequests();
    Observable<UUID> imageDeleteRequests();
    Observable<Object> plantDeleteRequests();
    Observable<Object> uiStateModifications();

    void bindView(PlantDetailViewModel vm);
    void takePhoto(String fileName);
    void deletePhoto(String fileName);
    void enableSave();
    void finish();

    UUID getPlantUUID();
    boolean isNewPlant();
}
