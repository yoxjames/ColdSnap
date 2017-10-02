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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.dagger.PlantDetailFragmentModule;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerRelative;
import com.yoxjames.coldsnap.ui.presenter.PlantDetailPresenter;
import com.yoxjames.coldsnap.ui.view.PlantDetailView;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class PlantDetailFragment extends Fragment implements PlantDetailView
{
    private static final String PLANT_UUID_ID = "com.example.yoxjames.coldsnap.ui";

    private View view;
    private EditText plantNameText;
    private EditText plantScientificNameText;
    private TemperaturePickerRelative minimumTempNumberPicker;

    private Button cancelButton;
    private Button saveButton;

    @Inject PlantDetailPresenter plantDetailPresenter;

    public static PlantDetailFragment newFragment(UUID plantUUID)
    {
        Bundle args = new Bundle();
        args.putSerializable(PLANT_UUID_ID, plantUUID);

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
        view = inflater.inflate(R.layout.plant_detail_item, container, false);

        plantNameText = view.findViewById(R.id.name_text);
        plantScientificNameText = view.findViewById(R.id.scientific_name_text);
        minimumTempNumberPicker = view.findViewById(R.id.minimum_temp_picker);

        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> getActivity().finish());

        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> plantDetailPresenter.savePlantInformation());

        minimumTempNumberPicker.setOnLongClickListener(v ->
        {
            showToast(getString(R.string.plant_threshold_expl));
            return false;
        });

        view.setVisibility(View.INVISIBLE); // Make view invisible until it is ready to show.
        return view;
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
        menu.findItem(R.id.menu_item_set_location).setVisible(false);
        menu.findItem(R.id.menu_item_new_plant).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_item_delete_plant:
                plantDetailPresenter.deletePlant();
                return super.onOptionsItemSelected(item);
            case R.id.action_settings:
                Intent intent = new Intent(getContext(), CSPreferencesActivity.class);
                startActivity(intent);
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Nullable
    private UUID getPlantUUID()
    {
        return (UUID) getArguments().getSerializable(PLANT_UUID_ID);
    }

    @Override
    public void showView()
    {
        view.setVisibility(View.VISIBLE);
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
        minimumTempNumberPicker.setTemperatureValue(minTemperature);
    }

    @Override
    public int getMinTemperature()
    {
        return minimumTempNumberPicker.getTemperatureValue();
    }

    @Override
    public void setAddMode()
    {
        saveButton.setText(getString(R.string.add));
    }

    @Override
    public void displayDeleteMessage()
    {
        showToast(getString(R.string.plant_deleted));
    }

    @Override
    public void finishView()
    {
        getActivity().finish();
    }

    @Override
    public void showLoadError()
    {
        showToast(getString(R.string.plant_load_error));
    }

    @Override
    public void showUnableToSaveError()
    {
        showToast(getString(R.string.plant_save_error));
    }

    @Override
    public void showUnableToAddError()
    {
        showToast(getString(R.string.plant_add_error));
    }

    @Override
    public void showUnableToDeleteError()
    {
        showToast(getString(R.string.plant_delete_error));
    }

    @Override
    public void displaySaveMessage(boolean isNewPlant)
    {
        if (isNewPlant)
            showToast(getString(R.string.plant_added));
        else
            showToast(getString(R.string.plant_updated));
    }

    private void showToast(String message)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

}
