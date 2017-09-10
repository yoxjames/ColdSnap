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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.dagger.PlantDetailActivityModule;
import com.yoxjames.coldsnap.ui.presenter.PlantDetailPagerPresenter;
import com.yoxjames.coldsnap.ui.view.PlantDetailPagerView;

import java.util.UUID;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class PlantDetailActivity extends AppCompatActivity implements PlantDetailPagerView
{
    private static final String PLANT_UUID_ID = "com.example.yoxjames.coldsnap.ui";

    private FragmentManager fragmentManager;
    private UUID plantUUID;

    @Inject PlantDetailPagerPresenter presenter;
    private ViewPager viewPager;

    public static Intent newIntent(Context packageContext, @Nullable UUID plantUUID)
    {
        final Intent intent = new Intent(packageContext, PlantDetailActivity.class);
        intent.putExtra(PLANT_UUID_ID, plantUUID);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ((ColdSnapApplication) getApplicationContext())
                .getInjector()
                .plantDetailActivitySubcomponent(new PlantDetailActivityModule(this))
                .inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_detail_pager);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        viewPager = (ViewPager) findViewById(R.id.activity_plant_detail_pager);
        plantUUID = (UUID) getIntent().getSerializableExtra(PLANT_UUID_ID);

        presenter.load();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void startUI()
    {
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager)
        {
            @Override
            public Fragment getItem(int position)
            {
                return PlantDetailFragment.newFragment(presenter.getPlant(position));
            }

            @Override
            public int getCount()
            {
                if (plantUUID == null)
                    return 1;
                return presenter.getPlantsSize();
            }
        });

        if (getStartedWithUUID() != null)
            for (int i = 0; i < presenter.getPlantsSize(); i++)
            {
                if (presenter.getPlant(i).equals(plantUUID))
                {
                    viewPager.setCurrentItem(i);
                        break;
                }
            }
        else
            viewPager.setCurrentItem(0);
    }

    @Override
    public UUID getStartedWithUUID()
    {
        return plantUUID;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        presenter.unload();
    }
}
