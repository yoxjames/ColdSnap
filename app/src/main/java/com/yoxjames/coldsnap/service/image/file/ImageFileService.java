package com.yoxjames.coldsnap.service.image.file;

import android.content.Context;

import com.yoxjames.coldsnap.service.ActionReply;

import java.util.List;

import io.reactivex.Observable;

public interface ImageFileService
{
    Observable<ActionReply> deleteImageFile(String fileName, Context context);
    Observable<ActionReply> cleanImageFiles(List<String> validFilenames);
}
