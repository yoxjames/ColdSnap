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

package com.yoxjames.coldsnap.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.dagger.PlantListFragmentModule;
import com.yoxjames.coldsnap.ui.presenter.PlantListPresenter;
import com.yoxjames.coldsnap.ui.view.PlantListItemView;
import com.yoxjames.coldsnap.ui.view.PlantListView;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;


public class PlantListFragment extends Fragment implements PlantListView
{
    private static final int ACCESS_LOCATION_PERMISSION_CALLBACK = 1;
    private RecyclerView plantRecyclerView;
    private PlantAdapter adapter;

    @Inject PlantListPresenter plantListPresenter;

    @Override
    public void onAttach(Context context)
    {

        ((ColdSnapApplication) getContext().getApplicationContext())
                .getInjector()
                .plantListFragmentSubcomponent(new PlantListFragmentModule(this))
                .inject(this);

        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_plant_list, container, false);

        plantRecyclerView = view.findViewById(R.id.plant_recycler_view);
        plantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new PlantListFragment.PlantAdapter();
        plantRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void notifyDataChange()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void openPlant(@Nullable UUID plantUUID)
    {
        Intent intent = PlantDetailActivity.newIntent(getActivity(), plantUUID);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        menu.getItem(3).setVisible(false); // Set Delete to invisible
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        plantListPresenter.load();
    }

    @Override
    public void onDestroy()
    {
        plantListPresenter.unload();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_item_new_plant:
                plantListPresenter.newPlant();
                return super.onOptionsItemSelected(item);
            case R.id.action_settings:
                Intent prefIntent = new Intent(getContext(), CSPreferencesActivity.class);
                startActivity(prefIntent);
                return super.onOptionsItemSelected(item);
            case R.id.menu_item_set_location:
                if (hasPermissions())
                    plantListPresenter.resetLocation();
                return super.onOptionsItemSelected(item);
            case R.id.menu_about_coldsnap:
                Intent aboutIntent = new Intent(getContext(), AboutActivity.class);
                startActivity(aboutIntent);
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean hasPermissions()
    {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_LOCATION_PERMISSION_CALLBACK);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if (requestCode == ACCESS_LOCATION_PERMISSION_CALLBACK)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                plantListPresenter.resetLocation();
            }
            else // When they deny the permission
            {
                displayLocationPermissionsError();
            }
        }
    }


    @Override
    public void displayDeviceLocationFailureMessage()
    {
        Toast.makeText(getContext(), R.string.device_location_fail_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayLocationPermissionsError()
    {
        showToast(R.string.location_permission_denied);
    }

    @Override
    public void displayLocationNotAvailableError()
    {
        showToast(R.string.no_location_providers_error);
    }

    private void showToast(int resID)
    {
        Toast.makeText(getContext(), getString(resID), Toast.LENGTH_LONG).show();
    }

    private class PlantHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, PlantListItemView
    {
        private final TextView plantName;
        private final TextView scientificName;
        private final TextView status;
        private final ProgressBar progress;

        private UUID plantUUID;

        PlantHolder(View itemView)
        {
            super(itemView);
            plantName = itemView.findViewById(R.id.plant_name);
            scientificName = itemView.findViewById(R.id.plant_scientific_name);
            status = itemView.findViewById(R.id.plant_status);
            progress = itemView.findViewById(R.id.progress);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            openPlant(plantUUID);
        }

        @Override
        public void showView()
        {
            status.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }

        @Override
        public void setPlantName(String plantName)
        {
            this.plantName.setText(plantName);
        }

        @Override
        public void setPlantScientificName(String scientificName)
        {
            this.scientificName.setText(scientificName);
        }

        @Override
        public void setStatus(String status)
        {
            this.status.setText(status);
        }

        @Override
        public void setUUID(UUID plantUUID)
        {
            this.plantUUID = plantUUID;
        }
    }

    private class PlantAdapter extends RecyclerView.Adapter<PlantHolder>
    {
        @Override
        public PlantHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.plant_list_item, parent, false);
            return new PlantHolder(view);
        }

        @Override
        public void onBindViewHolder(PlantHolder holder, int position)
        {
            plantListPresenter.loadPlantView(holder, position);
        }

        @Override
        public int getItemCount()
        {
            return plantListPresenter.getItemCount();
        }
    }
}
