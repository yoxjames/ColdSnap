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

package com.yoxjames.coldsnap.ui.plantlist;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.picasso.Picasso;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.ui.plantdetail.PlantDetailActivity;

import java.io.File;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by yoxjames on 12/9/17.
 */

public class PlantListRecyclerViewAdapter extends RecyclerView.Adapter<PlantListRecyclerViewAdapter.PlantItemHolder>
{
    private final PlantListItemViewAdapter plantListRecyclerViewAdapter;
    private final Context context;
    private CompositeDisposable disposables;
    private int plantViewModelCount;

    @Inject
    public PlantListRecyclerViewAdapter(final PlantListItemViewAdapter plantListRecyclerViewAdapter, final Context context)
    {
        this.plantListRecyclerViewAdapter = plantListRecyclerViewAdapter;
        this.context = context;
        plantViewModelCount = 0;
    }

    public void load()
    {
        if (disposables != null)
            disposables.dispose();
        disposables = new CompositeDisposable();

        disposables.add(plantListRecyclerViewAdapter.getCount()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count ->
                {
                    plantViewModelCount = count.intValue() - 1;
                    PlantListRecyclerViewAdapter.this.notifyDataSetChanged();
                }));
    }

    public void unload()
    {
        disposables.dispose();
    }

    @Override
    public PlantItemHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.plant_list_item, parent, false);
        return new PlantItemHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantItemHolder holder, int position)
    {
        holder.load(position);
    }

    @Override
    public int getItemCount()
    {
        return plantViewModelCount;
    }

    public class PlantItemHolder extends RecyclerView.ViewHolder
    {
        private final TextView plantName;
        private final TextView scientificName;
        private final TextView status;
        private final ProgressBar progress;
        private final View thisView;
        private final ImageView plantProfileImage;
        private UUID plantUUID;

        public PlantItemHolder(final View itemView)
        {
            super(itemView);
            plantName = itemView.findViewById(R.id.plant_name);
            scientificName = itemView.findViewById(R.id.plant_scientific_name);
            status = itemView.findViewById(R.id.plant_status);
            progress = itemView.findViewById(R.id.progress);
            plantProfileImage = itemView.findViewById(R.id.plant_profile_image);
            thisView = itemView;
        }

        public void load(int position)
        {
            disposables.add(plantListRecyclerViewAdapter.getViewModelForListItem(position)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(model ->
                    {
                        plantName.setText(model.getPlantName());
                        scientificName.setText(model.getPlantScientificName());
                        progress.setVisibility((model.getPlantStatus() == PlantListItemViewModel.Status.PENDING ? View.VISIBLE : View.INVISIBLE));
                        status.setVisibility((model.getPlantStatus() == PlantListItemViewModel.Status.PENDING ? View.INVISIBLE : View.VISIBLE));
                        status.setText(statusToText(model.getPlantStatus()));
                        plantUUID = model.getUUID();
                    }));


            disposables.add(RxView.clicks(thisView)
                    .subscribe(ignored -> openPlant(plantUUID)));

            disposables.add(plantListRecyclerViewAdapter.getImageFileName(position)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(fileName ->
                    {
                        if (fileName != null && !fileName.equals(""))
                        {
                            File imageFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
                            Picasso.with(context)
                                    .load(imageFile)
                                    .fit()
                                    .centerCrop()
                                    .into(plantProfileImage);
                        }
                    }));
        }

        private String statusToText(PlantListItemViewModel.Status status)
        {
            switch (status)
            {
                case HAPPY:
                    return "\uD83D\uDE00";
                case SAD:
                    return "\uD83D\uDE1E";
                case NEUTRAL:
                    return "\uD83D\uDE10";
                case DEAD:
                    return "\uD83D\uDC80";
                default:
                    return "...";
            }
        }

        private void openPlant(@Nullable UUID plantUUID)
        {
            Intent intent = PlantDetailActivity.newIntent(context, plantUUID);
            context.startActivity(intent);
        }
    }
}
