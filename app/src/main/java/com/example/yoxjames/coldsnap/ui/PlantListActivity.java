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

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.yoxjames.coldsnap.R;

public class PlantListActivity extends AppCompatActivity
{
    private FragmentManager fragmentManager;
    private PlantListFragment plantListFragment;
    private WeatherPreviewBarFragment weatherPreviewBarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        plantListFragment = (PlantListFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        weatherPreviewBarFragment = (WeatherPreviewBarFragment) fragmentManager.findFragmentById(R.id.top_bar_container);

        if (weatherPreviewBarFragment == null)
        {
            weatherPreviewBarFragment = new WeatherPreviewBarFragment();
            fragmentManager.beginTransaction().add(R.id.top_bar_container, weatherPreviewBarFragment).commit();
        }

        if (plantListFragment == null)
        {
            plantListFragment = new PlantListFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, plantListFragment).commit();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


}
