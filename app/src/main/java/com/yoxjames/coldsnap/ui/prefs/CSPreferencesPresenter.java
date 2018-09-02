package com.yoxjames.coldsnap.ui.prefs;

import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.prefs.PreferenceModel;
import com.yoxjames.coldsnap.service.preferences.CSPreferencesService;
import com.yoxjames.coldsnap.ui.AbstractBaseColdsnapPresenter;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerViewModel;

import io.reactivex.schedulers.Schedulers;

import static com.yoxjames.coldsnap.model.TemperatureUtil.celsiusToKelvinAbsVal;
import static com.yoxjames.coldsnap.model.TemperatureUtil.fahrenheitToKelvinAbsVal;
import static com.yoxjames.coldsnap.model.TemperatureUtil.kelvinToCelsiusAbsVal;
import static com.yoxjames.coldsnap.model.TemperatureUtil.kelvinToFahrenheitAbsVal;
import static com.yoxjames.coldsnap.model.TemperatureUtil.roundToInt;

public class CSPreferencesPresenter extends AbstractBaseColdsnapPresenter<CSPreferencesView>
{
    private final CSPreferences csPreferences;
    private final TemperatureValueAdapter temperatureValueAdapter;
    private final TemperatureFormatter temperatureFormatter;

    public CSPreferencesPresenter(CSPreferencesView view, CSPreferences csPreferences, TemperatureValueAdapter temperatureValueAdapter, TemperatureFormatter temperatureFormatter)
    {
        super(view);
        this.csPreferences = csPreferences;
        this.temperatureValueAdapter = temperatureValueAdapter;
        this.temperatureFormatter = temperatureFormatter;
    }

    @Override
    public void load()
    {
        super.load();
        disposables.add(csPreferences.getPreferences()
            .map(preferenceModel -> PreferencesViewModel.builder()
                .setThresholdViewModel(getThresholdVM(preferenceModel))
                .setFuzzViewModel(getFuzzVM(preferenceModel))
                .setThresholdSummary(temperatureFormatter.format(preferenceModel.getThreshold()))
                .setFuzzSummary(temperatureFormatter.formatFuzz(preferenceModel.getWeatherDataFuzz()))
                .setTemperatureScale(temperatureFormatter.formatTemperatureScale(preferenceModel.getTemperatureFormat()))
                .setColdAlarmTime(preferenceModel.getColdAlarmTime())
                .build())
            .subscribe(view::bindView));
    }

    @Override
    public void loadMenu()
    {

    }

    public void onEnterThresholdPrefView()
    {
        disposables.add(view.thresholdChanges()
            .observeOn(Schedulers.io())
            .map(temperatureValueAdapter::getTemperature)
            .subscribe(csPreferences::setThreshold));
    }

    public void onEnterFuzzprefView()
    {
        disposables.add(view.fuzzChanges()
            .observeOn(Schedulers.io())
            .map(this::getKelvinsFromValue)
            .map(Double::floatValue)
            .subscribe(csPreferences::setFuzz));
    }

    private TemperaturePickerViewModel getThresholdVM(PreferenceModel preferenceModel)
    {
        return TemperaturePickerViewModel.builder()
            .setValue(temperatureValueAdapter.getValue(preferenceModel.getThreshold()))
            .setFormat(csPreferences.getTemperatureFormat())
            .setMaxValue(100)
            .setMinValue(-100)
            .build();
    }

    private TemperaturePickerViewModel getFuzzVM(PreferenceModel preferenceModel)
    {
        return TemperaturePickerViewModel.builder()
            .setValue(getValueFromKelvins(csPreferences.getWeatherDataFuzz()))
            .setMinValue(0)
            .setMaxValue(10)
            .setFormat(csPreferences.getTemperatureFormat())
            .build();
    }

    private double getKelvinsFromValue(int value)
    {
        if (csPreferences.getTemperatureFormat() == CSPreferencesService.FAHRENHEIT)
            return fahrenheitToKelvinAbsVal(value);
        else if (csPreferences.getTemperatureFormat() == CSPreferencesService.CELSIUS)
            return celsiusToKelvinAbsVal(value);
        else
            throw new IllegalStateException("Invalid temp format");
    }

    private int getValueFromKelvins(double kelvin)
    {
        if (csPreferences.getTemperatureFormat() == CSPreferencesService.FAHRENHEIT)
            return roundToInt(kelvinToFahrenheitAbsVal(kelvin));
        else if (csPreferences.getTemperatureFormat() == CSPreferencesService.CELSIUS)
            return roundToInt(kelvinToCelsiusAbsVal(kelvin));
        else
            throw new IllegalStateException("Invalid temp format");
    }

}
