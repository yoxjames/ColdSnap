package com.yoxjames.coldsnap.service.image;

import android.content.Context;

import com.yoxjames.coldsnap.db.PlantImageDAO;
import com.yoxjames.coldsnap.model.PlantImage;
import com.yoxjames.coldsnap.service.ActionReply;
import com.yoxjames.coldsnap.service.image.file.ImageFileService;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;

public class ImageServiceImpl implements ImageService
{
    private final PlantImageDAO plantImageDAO;
    private final ImageFileService imageFileService;
    private final Context context;

    @Inject
    public ImageServiceImpl(PlantImageDAO plantImageDAO, ImageFileService imageFileService, Context context)
    {
        this.plantImageDAO = plantImageDAO;
        this.imageFileService = imageFileService;
        this.context = context;
    }

    @Override
    public Observable<PlantImage> getPlantImage(UUID plantUUID)
    {
        return plantImageDAO.getPlantImage(plantUUID);
    }

    @Override
    public Observable<ActionReply> savePlantImage(PlantImage plantImage)
    {
        return plantImageDAO.savePlantImage(plantImage);
    }

    @Override
    public Observable<ActionReply> deleteImagesForPlant(UUID plantUUID)
    {
        return getPlantImage(plantUUID)
            .flatMap(pi -> imageFileService.deleteImageFile(pi.getFileName(), context)
                .concatWith(plantImageDAO.deletePlantImage(pi.getPlantUUID())));
    }

    @Override
    public Observable<ActionReply> cleanImagesDirectory()
    {
        return plantImageDAO.getAllPlantImages()
            .flatMap(Observable::fromIterable)
            .map(PlantImage::getFileName)
            .toList()
            .toObservable()
            .flatMap(imageFileService::cleanImageFiles);
    }
}
