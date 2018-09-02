package com.yoxjames.coldsnap.model;

public class TemperatureUtil
{
    public static double kelvinToFahrenheit(double kelvin)
    {
        return (9.0/5.0) * (kelvin - 273.15) + 32;
    }

    public static double kelvinToCelsius(double kelvin)
    {
        return kelvin - 273.15;
    }

    public static double celsiusToKelvin(double celsius)
    {
        return celsius + 273.15;
    }

    public static double fahrenheitToKelvin(double fahrenheit)
    {
        return (fahrenheit + 459.67) * (5.0 / 9.0);
    }

    public static double kelvinToCelsiusAbsVal(double kelvin)
    {
        return kelvin;
    }

    public static double kelvinToFahrenheitAbsVal(double kelvin)
    {
        return kelvin * (9.0 / 5.0);
    }

    public static double fahrenheitToKelvinAbsVal(double fahrenheit)
    {
        return fahrenheit * (5.0 / 9.0);
    }

    public static double celsiusToKelvinAbsVal(double celsius)
    {
        return celsius;
    }

    public static int roundToInt(double doubleVal)
    {
        return (int) Math.round(doubleVal);
    }
}
