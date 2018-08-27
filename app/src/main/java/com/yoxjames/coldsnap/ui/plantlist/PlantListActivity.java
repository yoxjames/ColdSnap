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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.jakewharton.rxbinding2.view.RxMenuItem;
import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.dagger.PlantListModule;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.ui.AboutActivity;
import com.yoxjames.coldsnap.ui.BaseColdsnapActivity;
import com.yoxjames.coldsnap.ui.detail.PlantDetailActivity;
import com.yoxjames.coldsnap.ui.prefs.CSPreferencesActivity;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherBarViewModel;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherPreviewBarView;

import java.security.NoSuchProviderException;
import java.util.UUID;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class PlantListActivity extends BaseColdsnapActivity<PlantListPresenter> implements PlantListMvpView
{
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.plant_list_view) protected PlantListRecyclerView plantListView;
    @BindView(R.id.weather_preview_bar_view) protected WeatherPreviewBarView weatherPreviewBarView;

    private final Subject<SimpleWeatherLocation> weatherLocationSubject = PublishSubject.create();

    private PlantListRecyclerViewAdapter plantListRecyclerViewAdapter;

    @Override
    protected void inject()
    {
        ((ColdSnapApplication) getApplicationContext())
            .getInjector()
            .mainActivitySubcomponent(new PlantListModule(this))
            .inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_plant_list);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);

        plantListView.setNestedScrollingEnabled(false);
        plantListView.setLayoutManager(new LinearLayoutManager(this));
        plantListRecyclerViewAdapter = new PlantListRecyclerViewAdapter();
        plantListView.setAdapter(plantListRecyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        presenter.loadMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Nullable
    @Override
    protected Toolbar getToolbar()
    {
        return toolbar;
    }

    @Override
    public void newPlant()
    {
        Intent intent = PlantDetailActivity.newIntent(this, null, true);
        startActivity(intent);
    }

    @Override
    public void requestLocation()
    {
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        final LocationListener listener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                weatherLocationSubject.onNext(SimpleWeatherLocation.create(location.getLatitude(), location.getLongitude()));
            }

            @Override public void onStatusChanged(String s, int i, Bundle bundle) { }
            @Override public void onProviderEnabled(String s) { }
            @Override public void onProviderDisabled(String s) { }
        };

        if (locationManager == null)
            weatherLocationSubject.onError(new NullPointerException("Location Manager is null"));
        else
        {

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSION_REQUEST);
                else
                {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null)
                        weatherLocationSubject.onNext(SimpleWeatherLocation.create(location.getLatitude(), location.getLongitude()));
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
                }
            else
                weatherLocationSubject.onError(new NoSuchProviderException("No location provider available"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    requestLocation();
        }
    }

    @Override
    public Observable<Object> retryConnection()
    {
        return weatherPreviewBarView.retryClickEvent();
    }

    @Override
    public void bindView(PlantListViewModel vm)
    {
        plantListView.bindView(vm);
    }

    @Override
    public void bindView(WeatherBarViewModel vm)
    {
        weatherPreviewBarView.bindView(vm);
    }

    @Override
    public Observable<UUID> plantItemClicks()
    {
        return plantListRecyclerViewAdapter.onPlantClicked();
    }

    @Override
    public Observable<SimpleWeatherLocation> locationChange()
    {
        return weatherLocationSubject;
    }

    @Override
    public Observable<Object> newPlantRequests()
    {
        return RxMenuItem.clicks(toolbar.getMenu().findItem(R.id.menu_item_new_plant));
    }

    @Override
    public Observable<Object> locationClicks()
    {
        return RxMenuItem.clicks(toolbar.getMenu().findItem(R.id.menu_item_set_location));
    }

    @Override
    public void openPlant(UUID plantUUID)
    {
        startActivity(PlantDetailActivity.newIntent(this, plantUUID, false));
    }

    @Override
    public void openSettings()
    {
        startActivity(new Intent(this, CSPreferencesActivity.class));
    }

    @Override
    public void openAbout()
    {
        startActivity(new Intent(this, AboutActivity.class));
    }

    @Override
    public Observable<Object> settingsRequests()
    {
        return RxMenuItem.clicks(toolbar.getMenu().findItem(R.id.menu_item_settings));
    }

    @Override
    public Observable<Object> aboutRequests()
    {
        return RxMenuItem.clicks(toolbar.getMenu().findItem(R.id.menu_about_coldsnap));
    }
}
