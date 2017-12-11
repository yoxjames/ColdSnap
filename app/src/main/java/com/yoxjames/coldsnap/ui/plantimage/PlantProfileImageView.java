/*
 * Copyright (c) 2017 James Yox
 *
 * This file is part of ColdSnap.
 *
 * ColdSnap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ColdSnap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ColdSnap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yoxjames.coldsnap.ui.plantimage;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.picasso.Picasso;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.ui.plantdetail.PlantDetailSaveRequest;

import org.threeten.bp.Instant;

import java.io.File;
import java.util.UUID;

import io.reactivex.Observable;

/**
 * Created by yoxjames on 11/18/17.
 */

public class PlantProfileImageView extends RelativeLayout implements  PlantMainImageView
{
    private ImageView imageView;
    private ImageButton cameraButton;
    private File imageFile;
    private UUID uuid;
    private Instant photoInstant;

    public PlantProfileImageView(Context context)
    {
        super(context);
    }

    public PlantProfileImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PlantProfileImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate()
    {
        super.onFinishInflate();
        imageView = findViewById(R.id.plantImage);
        cameraButton = findViewById(R.id.cameraButton);
    }

    @Override
    public void bindViewModel(PlantMainImageViewModel viewModel)
    {
        cameraButton.setVisibility(viewModel.isHasImage() ? View.INVISIBLE : View.VISIBLE);

        if (viewModel.getFileName() != null && !viewModel.getFileName().equals(""))
        {
            imageFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), viewModel.getFileName());
            setImage(imageFile);
        }

        uuid = viewModel.getUUID();
    }

    @Override
    public PlantMainImageViewModel getViewModel()
    {
        return new PlantMainImageViewModel(imageFile.getName(), false, true, null, "not implemented", uuid);
    }

    @Override
    public PlantDetailSaveRequest.ProfileImage getImageSaveRequest()
    {
        if (uuid != null && imageFile != null)
            return new PlantDetailSaveRequest.ProfileImage(uuid, imageFile.getName(), Instant.now()); // TODO: Instant is wrong
        else
            return new PlantDetailSaveRequest.ProfileImage(null, null, null);
    }

    @Override
    public Observable<Object> takePhoto()
    {
        return RxView.clicks(cameraButton);
    }

    @Override
    public Observable<File> viewPhoto()
    {
        return RxView.clicks(imageView).filter(ignored -> cameraButton.getVisibility() == View.INVISIBLE).map(ignored -> imageFile);
    }

    @Override
    public void setImage(File imageFile)
    {
        Picasso.with(getContext())
                .load(imageFile)
                .resize(imageView.getWidth(), imageView.getHeight())
                .centerCrop()
                .into(imageView);
    }
}
