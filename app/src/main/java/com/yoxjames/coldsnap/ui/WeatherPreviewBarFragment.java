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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.dagger.WeatherPreviewBarViewModule;
import com.yoxjames.coldsnap.ui.presenter.WeatherPreviewBarPresenter;
import com.yoxjames.coldsnap.ui.view.WeatherPreviewBarView;

import javax.inject.Inject;

public class WeatherPreviewBarFragment extends Fragment implements WeatherPreviewBarView
{
    private TextView locationText;
    private TextView highField;
    private TextView lowField;
    private TextView lastUpdatedField;
    @Inject WeatherPreviewBarPresenter weatherPreviewBarPresenter;

    @Override
    public void onAttach(Context context)
    {

        ((ColdSnapApplication) getContext().getApplicationContext())
                .getInjector()
                .weatherPreviewBarSubcomponent(new WeatherPreviewBarViewModule(this))
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.weather_preview_bar, container, false);

        locationText = (TextView) view.findViewById(R.id.location_text);
        highField = (TextView) view.findViewById(R.id.high_field);
        lowField = (TextView) view.findViewById(R.id.low_field);
        lastUpdatedField = (TextView) view.findViewById(R.id.last_updated_field);

        return view;
    }

    @Override
    public void onResume()
    {
        updateUI();
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        weatherPreviewBarPresenter.unload();
        super.onDestroy();
    }

    @Override
    public void onStop()
    {
        weatherPreviewBarPresenter.unload();
        super.onStop();
    }

    @Override
    public void updateUI()
    {
        weatherPreviewBarPresenter.load();
    }

    @Override
    public void setLocationText(String locationText)
    {
        this.locationText.setText(locationText);
    }

    @Override
    public void setHighText(String highText)
    {
        this.highField.setText(highText);
    }

    @Override
    public void setLowText(String lowText)
    {
        this.lowField.setText(lowText);
    }

    @Override
    public void setLastUpdatedText(String lastUpdatedText)
    {
        this.lastUpdatedField.setText(lastUpdatedText);
    }
}
