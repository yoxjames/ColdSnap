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

package com.example.yoxjames.coldsnap.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoxjames.coldsnap.ColdSnapApplication;
import com.example.yoxjames.coldsnap.R;
import com.example.yoxjames.coldsnap.dagger.PlantListFragmentModule;
import com.example.yoxjames.coldsnap.model.SimpleWeatherLocation;
import com.example.yoxjames.coldsnap.model.SimpleWeatherLocationNotFoundException;
import com.example.yoxjames.coldsnap.ui.presenter.PlantListPresenter;
import com.example.yoxjames.coldsnap.ui.view.PlantListItemView;
import com.example.yoxjames.coldsnap.ui.view.PlantListView;

import java.util.UUID;

import javax.inject.Inject;

import dagger.internal.Preconditions;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Function;


public class PlantListFragment extends Fragment implements PlantListView
{
    private static final int ACCESS_LOCATION_PERMISSION_CALLBACK = 1;
    private RecyclerView plantRecyclerView;
    private PlantAdapter adapter;
    private Single<Location> locationObservable;

    @Inject PlantListPresenter plantListPresenter;

    @Override
    public void onAttach(Context context)
    {

        ((ColdSnapApplication) getContext().getApplicationContext())
                .getInjector()
                .plantListFragmentSubcomponent(new PlantListFragmentModule(this))
                .inject(this);
        locationObservable = Single.create(new SingleOnSubscribe<Location>()
        {
            final LocationManager locationManager = (LocationManager) getContext().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            @Override
            public void subscribe(@NonNull final SingleEmitter<Location> emitter) throws Exception
            {
                final LocationListener listener = new LocationListener()
                {
                    @Override
                    public void onLocationChanged(Location location)
                    {
                        emitter.onSuccess(location);
                    }

                    @Override public void onStatusChanged(String s, int i, Bundle bundle) { }
                    @Override public void onProviderEnabled(String s) { }
                    @Override public void onProviderDisabled(String s) { }
                };

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION }, ACCESS_LOCATION_PERMISSION_CALLBACK);
                        emitter.onError(new SimpleWeatherLocationNotFoundException("Location permission not granted"));
                    }
                    else
                    {
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null)
                            emitter.onSuccess(location);
                        else
                            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
                    }
                }
                else
                    emitter.onError(new SimpleWeatherLocationNotFoundException("No acceptable location providers available"));
            }
        });

        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_plant_list, container, false);

        plantRecyclerView = (RecyclerView) view.findViewById(R.id.plant_recycler_view);
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
    public void openPlant(UUID plantUUID, boolean isNewPlant)
    {
        Intent intent = PlantDetailActivity.newIntent(getActivity(), Preconditions.checkNotNull(plantUUID), isNewPlant);
        startActivity(intent);
    }

    @Override
    public Single<SimpleWeatherLocation> provideLocationObservable()
    {
        return locationObservable.map(new Function<Location, SimpleWeatherLocation>()
        {
            @Override
            public SimpleWeatherLocation apply(@NonNull Location location) throws Exception
            {
                return new SimpleWeatherLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case ACCESS_LOCATION_PERMISSION_CALLBACK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    plantListPresenter.resetLocation();
        }
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
        super.onDestroy();
        plantListPresenter.unload();
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
                plantListPresenter.resetLocation();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void displayDeviceLocationFailureMessage()
    {
        Toast.makeText(getContext(), R.string.device_location_fail_message, Toast.LENGTH_LONG).show();
    }

    private class PlantHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, PlantListItemView
    {
        private final TextView plantName;
        private final TextView scientificName;
        private final TextView status;
        private UUID plantUUID;

        PlantHolder(View itemView)
        {
            super(itemView);
            plantName = (TextView) itemView.findViewById(R.id.plant_name);
            scientificName = (TextView) itemView.findViewById(R.id.plant_scientific_name);
            status = (TextView) itemView.findViewById(R.id.plant_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            openPlant(plantUUID, false);
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
