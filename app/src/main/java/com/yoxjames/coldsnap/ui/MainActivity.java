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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.jakewharton.rxbinding2.view.RxMenuItem;
import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.dagger.MainActivityModule;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.ui.plantdetail.PlantDetailActivity;
import com.yoxjames.coldsnap.ui.plantlist.PlantListRecyclerView;
import com.yoxjames.coldsnap.ui.plantlist.PlantListRecyclerViewAdapter;
import com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewAdapter;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherBarModelAdapter;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherPreviewBarPresenter;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherPreviewBarView;

import java.security.NoSuchProviderException;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity
{
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private CompositeDisposable disposables;
    private PlantListRecyclerView plantListView;
    private PlantListRecyclerViewAdapter plantListRecyclerViewAdapter;
    @Inject PlantListItemViewAdapter plantListItemViewAdapter;
    @Inject WeatherPreviewBarPresenter weatherPreviewBarPresenter;
    private WeatherPreviewBarView weatherPreviewBarView;
    @Inject MainActivityDataProvider activityPresenter;
    @Inject WeatherBarModelAdapter weatherBarModelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        weatherPreviewBarView = findViewById(R.id.weather_preview_bar_view);

        ((ColdSnapApplication) getApplicationContext())
                .getInjector()
                .mainActivitySubcomponent(new MainActivityModule(weatherPreviewBarView))
                .inject(this);

        if (disposables != null)
            disposables.dispose();

        disposables = new CompositeDisposable();

        disposables.add(weatherPreviewBarView.retryClickEvent()
                .subscribe(ignored ->
                {
                    plantListRecyclerViewAdapter.unload();
                    weatherPreviewBarPresenter.unload();
                    plantListRecyclerViewAdapter.load();
                    weatherPreviewBarPresenter.load();
                }));

        plantListView = findViewById(R.id.plant_list_view);
        plantListView.setNestedScrollingEnabled(false);
        plantListRecyclerViewAdapter = new PlantListRecyclerViewAdapter(plantListItemViewAdapter, this);
        plantListView.setLayoutManager(new LinearLayoutManager(this));
        plantListView.setAdapter(plantListRecyclerViewAdapter);
        plantListRecyclerViewAdapter.load();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onResume()
    {
        plantListRecyclerViewAdapter.load();
        weatherPreviewBarPresenter.load();
        super.onResume();
    }

    @Override
    public void onStop()
    {
        plantListRecyclerViewAdapter.unload();
        weatherPreviewBarPresenter.unload();
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        plantListRecyclerViewAdapter.unload();
        weatherPreviewBarPresenter.unload();
        disposables.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        final MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);

        disposables.add(RxMenuItem.clicks(menu.findItem(R.id.menu_item_new_plant)).subscribe(v -> openPlant(null), Throwable::printStackTrace));

        disposables.add(RxMenuItem.clicks(menu.findItem(R.id.menu_item_set_location))
                .flatMap(ignored -> getLocation(this))
                .map(location -> new SimpleWeatherLocation(location.getLatitude(), location.getLongitude()))
                .subscribe(activityPresenter::pushLocation));

        disposables.add(RxMenuItem.clicks(menu.findItem(R.id.menu_about_coldsnap)).subscribe(v -> startActivity(new Intent(this, AboutActivity.class))));
        disposables.add(RxMenuItem.clicks(menu.findItem(R.id.action_settings)).subscribe(v -> startActivity(new Intent(this, CSPreferencesActivity.class))));

        menu.findItem(R.id.menu_item_delete_plant).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    private void openPlant(@Nullable UUID plantUUID)
    {
        Intent intent = PlantDetailActivity.newIntent(this, plantUUID);
        startActivity(intent);
    }

    public Observable<Location> getLocation(final Context context)
    {
        return Observable.create(emitter ->
        {
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            final LocationListener listener = new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                {
                    emitter.onNext(location);
                    emitter.onComplete();
                }

                @Override public void onStatusChanged(String s, int i, Bundle bundle) { }
                @Override public void onProviderEnabled(String s) { }
                @Override public void onProviderDisabled(String s) { }
            };

            if (locationManager == null)
                emitter.onError(new NullPointerException("Location Manager is null"));
            else
            {
                emitter.setCancellable(() -> locationManager.removeUpdates(listener));

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSION_REQUEST);
                    else
                    {
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null)
                            emitter.onNext(location);
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
                    }
                else
                    emitter.onError(new NoSuchProviderException("No location provider available"));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    disposables.add(getLocation(this)
                            .map(location -> new SimpleWeatherLocation(location.getLatitude(), location.getLongitude()))
                            .subscribe(activityPresenter::pushLocation));

        }
    }
}
