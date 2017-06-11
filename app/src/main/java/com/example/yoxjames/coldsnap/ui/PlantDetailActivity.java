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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.yoxjames.coldsnap.ColdSnapApplication;
import com.example.yoxjames.coldsnap.R;
import com.example.yoxjames.coldsnap.dagger.PlantDetailActivityModule;
import com.example.yoxjames.coldsnap.ui.presenter.PlantDetailPagerPresenter;

import java.util.UUID;

import javax.inject.Inject;

public class PlantDetailActivity extends AppCompatActivity
{
    private static final String PLANT_UUID_ID = "com.example.yoxjames.coldsnap.ui";
    private static final String NEW_PLANT_IND = "com.example.yoxjames.coldsnap.ui.new_plant_ind";

    private FragmentManager fragmentManager;

    @Inject PlantDetailPagerPresenter presenter;
    private ViewPager viewPager;

    public static Intent newIntent(Context packageContext, UUID plantUUID, boolean newPlantInd)
    {
        final Intent intent = new Intent(packageContext, PlantDetailActivity.class);
        intent.putExtra(PLANT_UUID_ID, plantUUID);
        intent.putExtra(NEW_PLANT_IND, newPlantInd);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ((ColdSnapApplication) getApplicationContext())
                .getInjector()
                .plantDetailActivitySubcomponent(new PlantDetailActivityModule())
                .inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_detail_pager);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        //plantDetailFragment = (PlantDetailFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        viewPager = (ViewPager) findViewById(R.id.activity_plant_detail_pager);
        final boolean newPlantInd = getIntent().getBooleanExtra(NEW_PLANT_IND, false);
        final UUID plantUUID = (UUID) getIntent().getSerializableExtra(PLANT_UUID_ID);

        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager)
        {
            @Override
            public Fragment getItem(int position)
            {
                if (!newPlantInd)
                {
                    UUID plantUUID = presenter.getPlant(position);
                    boolean newPlantInd = getIntent().getBooleanExtra(NEW_PLANT_IND, false);
                    return PlantDetailFragment.newFragment(plantUUID, newPlantInd);
                }
                else
                {
                    return PlantDetailFragment.newFragment(plantUUID, true);
                }
            }

            @Override
            public int getCount()
            {
                if (newPlantInd)
                    return 1;
                return presenter.getPlantsSize();
            }
        });

        for (int i = 0; i < presenter.getPlantsSize(); i++)
        {
            if (presenter.getPlant(i).equals(plantUUID))
            {
                viewPager.setCurrentItem(i);
                break;
            }
        }

        /*
        if (plantDetailFragment == null)
        {
            UUID plantUUID = (UUID) getIntent().getSerializableExtra(PLANT_UUID_ID);
            boolean newPlantInd = getIntent().getBooleanExtra(NEW_PLANT_IND, false);
            plantDetailFragment = PlantDetailFragment.newFragment(plantUUID, newPlantInd);
            fragmentManager.beginTransaction().add(R.id.fragment_container, plantDetailFragment).commit();
        }
        */
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}
