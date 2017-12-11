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

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.ui.plantdetail.PlantDetailPresenter;
import com.yoxjames.coldsnap.ui.plantdetail.PlantDetailSaveRequest;

import java.io.File;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by yoxjames on 11/18/17.
 */

public class PlantImageViewAdapter
{
    private final PlantDetailPresenter presenter;
    private PlantMainImageView view;
    private CompositeDisposable disposables;
    private Observable<PlantMainImageViewModel> plantVM;
    private File file;

    @Inject
    public PlantImageViewAdapter(PlantDetailPresenter presenter)
    {
        this.presenter = presenter;
    }

    public PlantMainImageView inflateView(LayoutInflater inflater, ViewGroup parent)
    {
        inflater.inflate(R.layout.plant_main_image, parent);

        view = parent.findViewById(R.id.imageView);
        return view;
    }

    public void load(UUID plantUUID)
    {

        if (disposables != null)
            disposables.dispose();
        disposables = new CompositeDisposable();

        if (plantUUID != null)
        {
            plantVM = presenter.imageVM(plantUUID);
            disposables.add(plantVM.subscribe(view::bindViewModel));
        }
    }

    public Observable<Object> takePhoto()
    {
        return view.takePhoto();
    }

    public Observable<File> viewPhoto()
    {
        return view.viewPhoto();
    }

    public ObservableTransformer<Object, PlantMainImageViewModel> savePhoto()
    {
        return ignored -> ignored.map(i -> view.getViewModel());
    }

    public void setImage(File imageFile)
    {
        file = imageFile;
        view.bindViewModel(new PlantMainImageViewModel(imageFile.getName(), false, true, null, "not implemented"));
    }

    public PlantDetailSaveRequest.ProfileImage getImageSaveRequest()
    {
        return view.getImageSaveRequest();
    }

    public void unload()
    {
        disposables.dispose();
    }
}
