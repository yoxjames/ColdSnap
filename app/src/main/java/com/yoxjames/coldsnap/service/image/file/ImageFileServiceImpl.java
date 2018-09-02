package com.yoxjames.coldsnap.service.image.file;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.yoxjames.coldsnap.service.ActionReply;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

public class ImageFileServiceImpl implements ImageFileService
{
    private final Context context;

    public ImageFileServiceImpl(Context context)
    {
        this.context = context;
    }

    @Override
    public Observable<ActionReply> deleteImageFile(String fileName, Context context)
    {
        return Observable.create((ObservableEmitter<ActionReply> e) ->
        {
            File image = new File(fileName);
            Uri photoURI = FileProvider.getUriForFile(context,
                "com.yoxjames.coldsnap.provider",
                image);
            context.getContentResolver().delete(photoURI, null, null);
            e.onNext(ActionReply.genericSuccess());
            e.onComplete();
        })
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> cleanImageFiles(List<String> validFilenames)
    {
        return Observable.create((ObservableEmitter<ActionReply> e) ->
        {
            final File pictureDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (pictureDir != null)
            {
                final File[] existingFiles = pictureDir.listFiles();
                for (final File file : existingFiles)
                {
                    if (!validFilenames.contains(file.getAbsolutePath()))
                    {
                        final Uri fileURI = FileProvider.getUriForFile(context, "com.yoxjames.coldsnap.provider", file);
                        context.getContentResolver().delete(fileURI, null, null);
                    }
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
