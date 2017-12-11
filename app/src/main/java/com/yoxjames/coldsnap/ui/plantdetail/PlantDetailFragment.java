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

package com.yoxjames.coldsnap.ui.plantdetail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxMenuItem;
import com.jakewharton.rxbinding2.view.RxView;
import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.dagger.PlantDetailFragmentModule;
import com.yoxjames.coldsnap.ui.AboutActivity;
import com.yoxjames.coldsnap.ui.CSPreferencesActivity;
import com.yoxjames.coldsnap.ui.UIReply;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerRelative;
import com.yoxjames.coldsnap.ui.plantimage.PlantImageViewAdapter;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class PlantDetailFragment extends Fragment implements PlantDetailView
{
    private static final String PLANT_UUID = "com.yoxjames.coldsnap.ui.PLANT_UUID";
    private static final int REQUEST_PHOTO = 2;

    private View view;
    private EditText plantNameText;
    private EditText plantScientificNameText;
    private RelativeLayout plantProfileImageView;

    private TemperaturePickerRelative minimumTempNumberPicker;
    private UUID plantUUID;

    private Button cancelButton;
    private Button saveButton;
    private File imageFile;

    private CompositeDisposable disposables;

    @Inject PlantImageViewAdapter plantImageViewAdapter;
    @Inject PlantDetailViewAdapter plantDetailViewAdapter;

    public static PlantDetailFragment newFragment(UUID plantUUID)
    {
        Bundle args = new Bundle();
        args.putSerializable(PLANT_UUID, plantUUID);

        PlantDetailFragment fragment = new PlantDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context)
    {

        ((ColdSnapApplication) getContext().getApplicationContext())
                .getInjector()
                .plantDetailFragmentSubcomponent(new PlantDetailFragmentModule(this, plantUUID))
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void bindViewModel(PlantDetailViewModel viewModel)
    {
        view.setVisibility(View.VISIBLE);
        plantNameText.setText(viewModel.getName());
        plantScientificNameText.setText(viewModel.getScientificName());
        minimumTempNumberPicker.setTemperatureValue(viewModel.getMinimumTemperature());
        plantUUID = viewModel.getPlantUUID();
        saveButton.setText(viewModel.isNewPlant() ? getString(R.string.add) : getString(R.string.save));

        final ObservableTransformer<PlantDetailSaveRequest, UIReply> saveTransformer =
                (getPlantUUID() != null ? plantDetailViewAdapter.savePlantDetail() : plantDetailViewAdapter.addPlant());

        disposables.add(RxView.clicks(saveButton)
                .map(ignored -> new PlantDetailSaveRequest(getPlantUUID(),
                        plantNameText.getText().toString(),
                        plantScientificNameText.getText().toString(),
                        minimumTempNumberPicker.getTemperatureValue(),
                        plantImageViewAdapter.getImageSaveRequest(),
                        getPlantUUID()   == null))
                .compose(saveTransformer)
                .subscribe(i -> getActivity().finish(), Throwable::printStackTrace, () -> getActivity().finish()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.plant_detail_item, container, false);

        if (disposables != null)
            disposables.dispose();
        disposables = new CompositeDisposable();

        plantNameText = view.findViewById(R.id.name_text);
        plantScientificNameText = view.findViewById(R.id.scientific_name_text);
        minimumTempNumberPicker = view.findViewById(R.id.minimum_temp_picker);
        cancelButton = view.findViewById(R.id.cancel_button);
        saveButton = view.findViewById(R.id.save_button);
        plantProfileImageView = view.findViewById(R.id.imageViewContainer);
        plantImageViewAdapter.inflateView(inflater, plantProfileImageView);

        disposables.add(RxView.clicks(cancelButton).subscribe(v -> getActivity().finish()));
        disposables.add(RxView.longClicks(minimumTempNumberPicker).subscribe(v -> showToast(getString(R.string.plant_threshold_expl))));
        disposables.add(plantDetailViewAdapter.getViewModel(getPlantUUID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindViewModel));

        plantImageViewAdapter.load(getPlantUUID());


        disposables.add(
                plantImageViewAdapter.takePhoto()
                        .subscribe(ignored ->
                        {
                            final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            String fileName = "IMG_" + UUID.randomUUID().toString() + ".jpg";
                            imageFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

                            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", imageFile);
                            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            captureImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivityForResult(captureImage, REQUEST_PHOTO);
                        }));

        disposables.add(plantImageViewAdapter.viewPhoto()
                .subscribe(file ->
                {
                    final Intent viewImageIntent = new Intent(Intent.ACTION_VIEW);
                    Uri imageURI = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
                    viewImageIntent.setDataAndType(imageURI, "image/*");
                    viewImageIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    viewImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Intent chooser = Intent.createChooser(viewImageIntent, "Choose");

                    startActivity(chooser);
                }));

        // Make view invisible until it is ready to show.
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_PHOTO)
            plantImageViewAdapter.setImage(imageFile);
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
    }

    @Override
    public void onDestroy()
    {
        disposables.dispose();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.menu_item_set_location).setVisible(false);
        menu.findItem(R.id.menu_item_new_plant).setVisible(false);

        disposables.add(RxMenuItem.clicks(menu.findItem(R.id.action_settings))
                .subscribe(ignored -> startActivity(new Intent(getContext(), CSPreferencesActivity.class))));

        if (getPlantUUID() != null)
            disposables.add(RxMenuItem.clicks(menu.findItem(R.id.menu_item_delete_plant))
                    .map(i -> new PlantDetailDeleteRequest(getPlantUUID()))
                    .compose(plantDetailViewAdapter.deletePlant())
                    .subscribe(ignored -> getActivity().finish()));
        else
            disposables.add(RxMenuItem.clicks(menu.findItem(R.id.menu_item_delete_plant))
                    .subscribe(ignored -> getActivity().finish()));

        disposables.add(RxMenuItem.clicks(menu.findItem(R.id.menu_about_coldsnap))
                .subscribe(ignored -> startActivity(new Intent(getContext(), AboutActivity.class))));
    }

    @Nullable
    private UUID getPlantUUID()
    {
        return (UUID) getArguments().getSerializable(PLANT_UUID);
    }

    private void showToast(String message)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
