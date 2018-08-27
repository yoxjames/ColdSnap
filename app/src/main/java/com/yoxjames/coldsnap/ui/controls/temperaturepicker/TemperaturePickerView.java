package com.yoxjames.coldsnap.ui.controls.temperaturepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.yoxjames.coldsnap.ui.BaseColdsnapView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.Observable;

import static com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerViewModel.CELSIUS;
import static com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerViewModel.FAHRENHEIT;

public class TemperaturePickerView extends NumberPicker
    implements BaseColdsnapView<TemperaturePickerViewModel>, NumberPicker.Formatter
{
    private TemperaturePickerViewModel viewModel = TemperaturePickerViewModel.EMPTY;
    private int offset = 0;

    public TemperaturePickerView(Context context)
    {
        super(context);
    }

    public TemperaturePickerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TemperaturePickerView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public TemperaturePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onFinishInflate()
    {
        super.onFinishInflate();
        reflectionFormatHack();
    }

    @Override
    public void bindView(TemperaturePickerViewModel vm)
    {
        this.viewModel = vm;

        // Take the absolute value of the min value if it's negative and that will act as the offset.
        // This is needed because NumberPickers cannot handle negatives. Therefore the offset will be used to calculate
        // a value to display to the user in the formatter.
        if (vm.getMinValue() < 0)
            offset = Math.abs(vm.getMinValue());

        setMaxValue(vm.getMaxValue() + offset);
        setMinValue(vm.getMinValue() + offset);
        setValue(vm.getValue() + offset);
        setFormatter(this);
        setWrapSelectorWheel(false);
    }

    public TemperaturePickerViewModel getViewModel()
    {
        return viewModel.withValue(getTemperatureValue());
    }

    @Override
    public String format(int value)
    {
        int formattedValue = value - offset;
        switch (viewModel.getFormat())
        {
            case CELSIUS:
                return formattedValue + "°C";
            case FAHRENHEIT:
                return formattedValue + "°F";
            default:
                throw new IllegalStateException("Invalid temperature format. This should never happen");
        }
    }

    public Observable<Integer> valueChanged()
    {
        return Observable.create(e -> setOnValueChangedListener((v, oldValue, newValue) ->
        {
            e.onNext(newValue - offset);
            onValueChange(oldValue, newValue);
        }));
    }

    public int getTemperatureValue()
    {
        return getValue() - offset;
    }

    private void reflectionFormatHack()
    {
        // Disgusting hack to get around an android bug: https://issuetracker.google.com/issues/36952035
        try
        {
            Method changeValueByOne = getClass().getSuperclass().getDeclaredMethod("changeValueByOne", boolean.class);
            changeValueByOne.setAccessible(true);
            changeValueByOne.invoke(this, true);
        }
        catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            // I dont really care if this doesn't work. It just means the NumberPicker will format poorly
        }
    }

    private void onValueChange(int oldValue, int newValue)
    {
        if (Math.abs(oldValue - newValue) > 1)
        {
            // Minus 1 because somehow the reflection hack increments the counter? Idk this thing is weird....
            setValue(newValue + offset - 1);
            reflectionFormatHack();
        }
    }
}
