package com.yoxjames.coldsnap.model;

import com.yoxjames.coldsnap.prefs.CSPreferences;

import javax.inject.Inject;

public class TemperatureComparatorImpl implements TemperatureComparator
{
    private static final double SIGNIFICANCE = 0.5;

    private final CSPreferences csPreferences;

    @Inject
    public TemperatureComparatorImpl(CSPreferences csPreferences)
    {
        this.csPreferences = csPreferences;
    }

    @Override
    @TemperatureComparison
    public int compare(Temperature thisTemp, Temperature otherTemp)
    {
        final double thisTempKelvin = thisTemp.getKelvin();
        final double otherTempKelvin = otherTemp.getKelvin();

        final double difference = Math.abs(thisTempKelvin - otherTempKelvin);

        if (difference <= SIGNIFICANCE)
            return TRUE_EQUAL;
        else if (thisTempKelvin > otherTempKelvin)
            return (difference < (SIGNIFICANCE + csPreferences.getWeatherDataFuzz())) ? MAYBE_GREATER : GREATER;
        else
            return (difference < (SIGNIFICANCE + csPreferences.getWeatherDataFuzz())) ? MAYBE_LESSER : LESSER;
    }
}
