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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.core.view.BaseColdsnapView;
import com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.PlantStatus;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.DEAD;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.ERROR;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.HAPPY;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.NEUTRAL;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.PENDING;
import static com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewModel.SAD;
import static com.yoxjames.coldsnap.util.CSUtils.EMPTY_UUID;

/**
 * Created by yoxjames on 12/9/17.
 */

public class PlantListRecyclerViewAdapter extends RecyclerView.Adapter<PlantListRecyclerViewAdapter.PlantItemHolder>
{
    private PlantListViewModel viewModel = PlantListViewModel.EMPTY;

    private final Subject<UUID> subject = PublishSubject.create();

    @Override
    public PlantItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.view_plant_list_item, parent, false);
        return new PlantItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantItemHolder holder, int position)
    {
        holder.bindView(viewModel.getPlantItemViewModels().get(position));
    }

    @Override
    public int getItemCount()
    {
        return viewModel.getPlantItemViewModels().size();
    }

    public void bindAdapter(PlantListViewModel vm)
    {
        viewModel = vm;
        notifyDataSetChanged();
    }

    public Observable<UUID> onPlantClicked()
    {
        return subject;
    }

    public class PlantItemHolder extends RecyclerView.ViewHolder implements BaseColdsnapView<PlantListItemViewModel>
    {
        @BindView(R.id.tv_name) TextView plantName;
        @BindView(R.id.tv_scientific_name) TextView scientificName;
        @BindView(R.id.tv_status) TextView status;
        @BindView(R.id.pb_loading) ProgressBar progress;
        @BindView(R.id.iv_profile) ImageView plantProfileImage;

        private UUID plantUUID = EMPTY_UUID;

        PlantItemHolder(final View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(PlantListItemViewModel vm)
        {
            plantName.setText(vm.getPlantName());
            scientificName.setText(vm.getPlantScientificName());
            progress.setVisibility((vm.getPlantStatus() == PENDING ? View.VISIBLE : View.INVISIBLE));
            status.setVisibility((vm.getPlantStatus() == PENDING ? View.INVISIBLE : View.VISIBLE));
            status.setText(statusToText(vm.getPlantStatus()));
            plantUUID = vm.getUuid();
            if (!vm.getImageFileName().equals(""))
                Glide.with(itemView).load(vm.getImageFileName()).into(plantProfileImage);
            else
                plantProfileImage.setImageBitmap(null);
        }

        @OnClick(R.id.plant_card)
        protected void onClickItem()
        {
            if (!plantUUID.equals(EMPTY_UUID))
                subject.onNext(plantUUID);
        }

        private String statusToText(@PlantStatus int status)
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
                case PENDING:
                    return "...";
                case ERROR:
                    return "ERR";
                default:
                    throw new IllegalStateException("Invalid Plant Status");
            }
        }
    }
}
