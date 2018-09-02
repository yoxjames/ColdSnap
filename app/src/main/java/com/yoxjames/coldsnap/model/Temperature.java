package com.yoxjames.coldsnap.model;

import com.google.auto.value.AutoValue;

import static com.yoxjames.coldsnap.model.TemperatureUtil.celsiusToKelvin;
import static com.yoxjames.coldsnap.model.TemperatureUtil.fahrenheitToKelvin;
import static com.yoxjames.coldsnap.model.TemperatureUtil.kelvinToCelsius;
import static com.yoxjames.coldsnap.model.TemperatureUtil.kelvinToFahrenheit;

@AutoValue
public abstract class Temperature
{
    public abstract double getKelvin();

    public static Temperature fromKelvin(double kelvin)
    {
        return new AutoValue_Temperature(kelvin);
    }

    public static Temperature fromCelsius(double celsius)
    {
        return fromKelvin(celsiusToKelvin(celsius));
    }

    public static Temperature fromFahrenheit(double fahrenheit)
    {
        return fromKelvin(fahrenheitToKelvin(fahrenheit));
    }

    @Override
    public String toString()
    {
        return "Temperature{"
            + "kelvin=" + getKelvin() + "K" + ", "
            + "celsius=" + (kelvinToCelsius(getKelvin())) + "°C" + ", "
            + "fahrenheit=" + (kelvinToFahrenheit(getKelvin())) + "°F" + ", "
            + "}";
    }
}
