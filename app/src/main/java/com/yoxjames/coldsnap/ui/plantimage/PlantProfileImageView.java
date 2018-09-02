package com.yoxjames.coldsnap.ui.plantimage;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.ui.BaseColdsnapView;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

import static android.text.TextUtils.isEmpty;
import static com.yoxjames.coldsnap.util.CSUtils.EMPTY_UUID;

public class PlantProfileImageView extends ConstraintLayout implements BaseColdsnapView<PlantProfileImageViewModel>
{
    @BindView(R.id.iv_plant_profile) ImageView ivPlantProfile;
    @BindView(R.id.ib_take_photo) ImageButton ibTakePhoto;
    @BindView(R.id.ib_delete_photo) ImageButton ibDeletePhoto;

    private UUID plantUUID = EMPTY_UUID;
    private PlantProfileImageViewModel viewModel = PlantProfileImageViewModel.EMPTY;

    public PlantProfileImageView(Context context)
    {
        super(context);
    }

    public PlantProfileImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PlantProfileImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(PlantProfileImageViewModel viewModel)
    {
        if (!isEmpty(viewModel.getImageURL()))
            Glide.with(this)
                .load(viewModel.getImageURL())
                .into(ivPlantProfile);
        else
            ivPlantProfile.setImageBitmap(null);

        ibTakePhoto.setClickable(viewModel.isTakeImageAvailable());
        plantUUID = viewModel.getPlantUUID();
        this.viewModel = viewModel;
    }

    public Observable<UUID> takeProfilePhoto()
    {
        return RxView.clicks(ibTakePhoto).filter(i -> plantUUID.equals(EMPTY_UUID)).map(i -> plantUUID);
    }

    public Observable<UUID> deleteProfilePhoto()
    {
        return RxView.clicks(ibDeletePhoto).filter(i -> !plantUUID.equals(EMPTY_UUID)).map(i -> plantUUID);
    }

    public void bindImage(String fileName)
    {
        bindView(viewModel.withImage(fileName));
    }
}
