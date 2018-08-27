package com.yoxjames.coldsnap.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jakewharton.rxbinding2.view.RxMenuItem;
import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.dagger.PlantDetailModule;
import com.yoxjames.coldsnap.ui.BaseColdsnapActivity;
import com.yoxjames.coldsnap.ui.plantimage.PlantProfileImageView;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class PlantDetailActivity extends BaseColdsnapActivity<PlantDetailPresenter> implements PlantDetailMvpView
{
    private static final String PLANT_UUID = "PLANT_UUID";
    private static final String IS_NEW_PLANT = "IS_NEW_PLANT";

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @BindView(R.id.pdv_plant_detail) PlantDetailView plantDetailView;
    @BindView(R.id.cl_plant_detail) CoordinatorLayout clPlantDetail;
    @BindView(R.id.ppiv_plant_profile_view) PlantProfileImageView ppivPlantProfileImageView;
    @BindView(R.id.toolbar) protected Toolbar toolbar;

    private final Subject<String> plantImageSaveRequests = PublishSubject.create();

    private String photoFileName = "";
    private boolean isSaveEnabled = false;

    public static Intent newIntent(Context context, @Nullable UUID plantUUID, boolean isNewPlant)
    {
        return new Intent(context, PlantDetailActivity.class)
            .putExtra(PLANT_UUID, plantUUID)
            .putExtra(IS_NEW_PLANT, isNewPlant);
    }

    @Override
    protected void inject()
    {
        ((ColdSnapApplication) getApplicationContext())
            .getInjector()
            .plantDetailSubcomponent(new PlantDetailModule(this))
            .inject(this);
    }

    @Nullable
    @Override
    protected Toolbar getToolbar()
    {
        return toolbar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_plant_detail);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        if (isNewPlant())
            inflater.inflate(R.menu.menu_detail_new_plant, menu);
        else
            inflater.inflate(R.menu.menu_detail, menu);

        presenter.loadMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
            NavUtils.navigateUpFromSameTask(this);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isNewPlant()
    {
        return getIntent().getBooleanExtra(IS_NEW_PLANT, true);
    }

    @Override
    public Observable<PlantDetailViewUpdate> plantDetailSaves()
    {
        return RxMenuItem.clicks(toolbar.getMenu().findItem(R.id.menu_item_save_plant))
            .map(i -> plantDetailView.getViewModel())
            .map(vm -> PlantDetailViewUpdate.builder()
                .name(vm.getName())
                .scientificName(vm.getScientificName())
                .tempVal(vm.getTemperaturePickerViewModel().getValue())
                .build());
    }

    @Override
    public Observable<UUID> takeProfileImageRequests()
    {
        return ppivPlantProfileImageView.takeProfilePhoto();
    }

    @Override
    public Observable<String> imageSaveRequests()
    {
        return plantImageSaveRequests;
    }

    @Override
    public Observable<UUID> imageDeleteRequests()
    {
        return ppivPlantProfileImageView.deleteProfilePhoto();
    }

    @Override
    public Observable<Object> plantDeleteRequests()
    {
        if (toolbar.getMenu().findItem(R.id.menu_item_delete_plant) != null)
            return RxMenuItem.clicks(toolbar.getMenu().findItem(R.id.menu_item_delete_plant));
        else
            return Observable.empty();
    }

    @Override
    public Observable<Object> uiStateModifications()
    {
        return plantDetailView.plantDetailUpdates().mergeWith(plantImageSaveRequests);
    }

    @Override
    public UUID getPlantUUID()
    {
        UUID intentUUID = (UUID) getIntent().getSerializableExtra(PLANT_UUID);

        return (intentUUID != null) ? intentUUID : UUID.randomUUID();
    }

    @Override
    public void bindView(PlantDetailViewModel vm)
    {
        plantDetailView.bindView(vm);
        ppivPlantProfileImageView.bindView(vm.getPlantProfileImageViewModel());
    }

    @Override
    public void takePhoto(String fileName)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try {
                photoFile = createImageFile(fileName);
            }
            catch (IOException ex)
            {
                // TODO: Handle Error
            }

            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,
                    "com.yoxjames.coldsnap.provider",
                    photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void deletePhoto(String fileName)
    {
        File image = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        Uri photoURI = FileProvider.getUriForFile(this,
            "com.yoxjames.coldsnap.provider",
            image);
        this.getContentResolver().delete(photoURI, null, null);
    }

    @Override
    public void enableSave()
    {
        isSaveEnabled = true;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        toolbar.getMenu().findItem(R.id.menu_item_save_plant).setEnabled(isSaveEnabled);
        return true;
    }

    private File createImageFile(String fileName) throws IOException
    {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        photoFileName = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && !photoFileName.equals(""))
        {
            ppivPlantProfileImageView.bindImage(photoFileName);

            // Publish the fileName to save into the DB
            plantImageSaveRequests.onNext(photoFileName);
        }
    }
}
