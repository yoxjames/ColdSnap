package com.yoxjames.coldsnap.ui.detail;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.ui.BaseColdsnapView;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerView;
import com.yoxjames.coldsnap.ui.plantimage.PlantProfileImageViewModel;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

public class PlantDetailView extends ConstraintLayout implements BaseColdsnapView<PlantDetailViewModel>
{
    @BindView(R.id.et_plant_name) protected EditText etPlantName;
    @BindView(R.id.et_plant_scientific_name) protected EditText etScientificName;
    @BindView(R.id.tpv_cold_temperature) protected TemperaturePickerView tpvColdTemperature;

    private PlantDetailViewModel viewModel = PlantDetailViewModel.EMPTY;

    public PlantDetailView(Context context)
    {
        super(context);
    }

    public PlantDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PlantDetailView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(PlantDetailViewModel vm)
    {
        etPlantName.setText(vm.getName());
        etScientificName.setText(vm.getScientificName());
        tpvColdTemperature.bindView(vm.getTemperaturePickerViewModel());
        viewModel = vm;
    }

    public PlantDetailViewModel getViewModel()
    {
        return PlantDetailViewModel.builder()
            .setName(etPlantName.getText().toString())
            .setScientificName(etScientificName.getText().toString())
            .setTemperaturePickerViewModel(tpvColdTemperature.getViewModel())
            .setPlantProfileImageViewModel(PlantProfileImageViewModel.EMPTY)
            .build();
    }

    public Observable<Object> plantDetailUpdates()
    {
        return Observable.merge(
                RxTextView.afterTextChangeEvents(etPlantName),
                RxTextView.afterTextChangeEvents(etScientificName),
                tpvColdTemperature.valueChanged())
            .debounce(1, TimeUnit.SECONDS)
            .filter(i -> isModified())
            .map(i -> new Object());
    }

    private boolean isModified()
    {
        return (!etPlantName.getText().toString().equals(viewModel.getName()))
            || (!etScientificName.getText().toString().equals(viewModel.getScientificName()))
            || (tpvColdTemperature.getTemperatureValue() != viewModel.getTemperaturePickerViewModel().getValue());
    }
}
