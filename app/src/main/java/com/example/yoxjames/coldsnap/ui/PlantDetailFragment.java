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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.yoxjames.coldsnap.ColdSnapApplication;
import com.example.yoxjames.coldsnap.R;
import com.example.yoxjames.coldsnap.dagger.PlantDetailFragmentModule;
import com.example.yoxjames.coldsnap.ui.presenter.PlantDetailPresenter;
import com.example.yoxjames.coldsnap.ui.view.PlantDetailView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import javax.inject.Inject;

public class PlantDetailFragment extends Fragment implements PlantDetailView
{
    private static final String PLANT_UUID_ID = "com.example.yoxjames.coldsnap.ui";
    private static final String NEW_PLANT_IND = "com.example.yoxjames.coldsnap.ui.new_plant_ind";

    private EditText plantNameText;
    private EditText plantScientificNameText;
    private NumberPicker minimumTempNumberPicker;

    private Button cancelButton;
    private Button saveButton;

    @Inject PlantDetailPresenter plantDetailPresenter;

    public static PlantDetailFragment newFragment(UUID plantUUID, boolean newPlantInd)
    {
        Bundle args = new Bundle();
        args.putSerializable(PLANT_UUID_ID, plantUUID);
        args.putBoolean(NEW_PLANT_IND, newPlantInd);

        PlantDetailFragment fragment = new PlantDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context)
    {

        ((ColdSnapApplication) getContext().getApplicationContext())
                .getInjector()
                .plantDetailFragmentSubcomponent(new PlantDetailFragmentModule(this))
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.plant_detail_item, container, false);

        plantNameText = (EditText) view.findViewById(R.id.name_text);
        plantScientificNameText = (EditText) view.findViewById(R.id.scientific_name_text);
        minimumTempNumberPicker = (NumberPicker) view.findViewById(R.id.minimum_temp_picker);

        reflectionFormatHack(minimumTempNumberPicker);

        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().finish();
            }
        });

        saveButton = (Button) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                plantDetailPresenter.savePlantInformation(getPlantUUID());
                getActivity().finish();
            }
        });

        return view;
    }

    private void reflectionFormatHack(NumberPicker numberPicker)
    {
        // Disgusting hack to get around an android bug: https://issuetracker.google.com/issues/36952035
        try
        {
            Method method = numberPicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(numberPicker, true);
        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
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
        plantDetailPresenter.load(getPlantUUID());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false); // Remove set location in detail view
        menu.getItem(2).setVisible(false); // Remove add plant in detail view
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_item_delete_plant:
                plantDetailPresenter.deletePlant(getPlantUUID());
                getActivity().finish();
                return super.onOptionsItemSelected(item);
            case R.id.action_settings:
                Intent intent = new Intent(getContext(), CSPreferencesActivity.class);
                startActivity(intent);
                return super.onOptionsItemSelected(item);
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    private UUID getPlantUUID()
    {
        return (UUID) getArguments().getSerializable(PLANT_UUID_ID);
    }

    @Override
    public boolean isNewPlantInd()
    {
        return getArguments().getBoolean(NEW_PLANT_IND);
    }

    @Override
    public void setPlantName(String name)
    {
        plantNameText.setText(name);
    }

    @Override
    public String getPlantName()
    {
        return plantNameText.getText().toString();
    }

    @Override
    public void setPlantScientificName(String scientificName)
    {
        plantScientificNameText.setText(scientificName);
    }

    @Override
    public String getPlantScientificName()
    {
        return plantScientificNameText.getText().toString();
    }

    @Override
    public void setMinTemperature(int minTemperature)
    {
        minimumTempNumberPicker.setValue(minTemperature);
    }

    @Override
    public int getMinTemperature()
    {
        return minimumTempNumberPicker.getValue();
    }

    @Override
    public void setMaxBound(int maxTemperature)
    {
        minimumTempNumberPicker.setMaxValue(maxTemperature);
    }

    @Override
    public void setMinBound(int minTemperature)
    {
        minimumTempNumberPicker.setMinValue(minTemperature);
    }

    @Override
    public void setAddMode()
    {
        saveButton.setText(getString(R.string.add));
    }

    @Override
    public void displayDeleteMessage()
    {
        Toast.makeText(getActivity(), getString(R.string.plant_deleted), Toast.LENGTH_LONG).show();
    }

    @Override
    public void displaySaveMessage(boolean isNewPlant)
    {
        if (isNewPlant)
            Toast.makeText(getActivity(), getString(R.string.plant_added), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity(), getString(R.string.plant_updated), Toast.LENGTH_LONG).show();
    }

    @Override
    public void setMinimumTemperatureFormatter(NumberPicker.Formatter formatter)
    {
        minimumTempNumberPicker.setFormatter(formatter);
    }

    @Override
    public void setMinimumTemperatureValueChangeListener(NumberPicker.OnValueChangeListener listener)
    {
        minimumTempNumberPicker.setOnValueChangedListener(listener);
    }
}
