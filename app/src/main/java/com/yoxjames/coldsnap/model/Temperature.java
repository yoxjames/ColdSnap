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

package com.yoxjames.coldsnap.model;

import android.support.annotation.NonNull;

import javax.annotation.concurrent.Immutable;

import dagger.internal.Preconditions;

import static java.lang.Math.abs;
import static java.lang.Math.round;

/**
 * This class represents a Temperature. All temperatures are stored as double value containing degrees
 * Kelvin. When comparing two temperatures they must be at least 0.5 Kelvin different in order to be considered
 * significantly different (see {@link Comparable}).
 *
 * Additionally this class also supports a fuzz factor representing how confident with this data the
 * system should be. A fuzz of zero would mean that you are fully confident with this Temperature and only the
 * hardcoded SIGNIFICANCE will be considered when comparing. Adding a fuzz factor adds that on top of SIGNIFICANCE
 * when comparing against other Temperatures.
 *
 * Created by James Yox on 5/20/17.
 */
@Immutable
public class Temperature implements Comparable<Temperature>
{
    public static final double WATER_FREEZING_KELVIN = 273.15;

    /*
     * Actual value representing degrees kelvin
     */
    private final double degreesKelvin;

    /*
     * Represents the fuzz factor during comparison. How "confident" we are that this temperature
     * is accurate. This is always in Kelvin.
     */
    private final double fuzz;

    /*
     * This represents the amount of degrees (in Kelvin) by which we recognize difference. Anything
     * that is not more than this value apart is considered the same in this application.
     */
    private static final double SIGNIFICANCE = 0.5;

    /**
     * Constructs a new Temperature object based on degrees in Celsius. Fuzz will be zero.
     * Cannot construct temperatures below absolute zero.
     *
     * @param degreesC int containing a temperature in celsius
     * @return a Temperature instance representing the temperature inputted.
     */
    public static Temperature newTemperatureFromC(int degreesC)
    {
        return new Temperature(celsiusToKelvin(degreesC));
    }

    /**
     * Creates a new Temperature from degrees C and a fuzz in degrees C
     *
     * @param degreesC The desired temperature in degrees C
     * @param fuzzC The fuzz for the temperature in degrees C
     * @return A new temperature based on inputted degreesC and fuzzC.
     */
    public static Temperature newTemperatureFromC(int degreesC, int fuzzC)
    {
        return new Temperature(celsiusToKelvin(degreesC), celsiusToKelvin(fuzzC));
    }

    /**
     * Creates a new Temperature from degrees C with a fuzz in degrees K.
     *
     * @param degreesC Desired temperature in degrees C
     * @param fuzzK Fuzz in degrees K
     * @return A new temperature from degrees C and inputted fuzz.
     */
    public static Temperature newTemperatureFromC(int degreesC, double fuzzK)
    {
        return new Temperature(celsiusToKelvin(degreesC), fuzzK);
    }

    /**
     * Creates a new Temperature from degrees F with no fuzz.
     *
     * @param degreesF Desired temperature in degrees F
     * @return A new Temperature with no fuzz equal to the inputted degrees F.
     */
    public static Temperature newTemperatureFromF(int degreesF)
    {
        return new Temperature(fahrenheitToKelvin(degreesF));
    }

    /**
     * Creates a new temperature from a F temperature and a fuzz in degrees F.
     *
     * @param degreesF int representing the desired temperature in F
     * @param fuzzF int representing the fuzz value in F.
     * @return A new temperature for the inputted degrees F and fuzz.
     */
    public static Temperature newTemperatureFromF(int degreesF, int fuzzF)
    {
        return new Temperature(fahrenheitToKelvin(degreesF), fahrenheitToKelvin(fuzzF));
    }

    /**
     * Creates a new temperature from a Fahrenheit temperature and a fuzz in K.
     *
     * @param degreesF int representing the desired temperature in F
     * @param fuzzK double representing the fuzz value in K
     * @return A new Temperature for the inputted degrees F and fuzz.
     */
    public static Temperature newTemperatureFromF(int degreesF, double fuzzK)
    {
        return new Temperature(fahrenheitToKelvin(degreesF), fuzzK);
    }

    /**
     * Returns an integer representing the value of the inputted Temperature in degrees F.
     *
     * @param temperature temperature to obtain the degrees F of
     * @return An int representing the inputted temperature in degrees F
     */
    public static int asFahrenheitDegrees(Temperature temperature)
    {
        return (int) round(kelvinToFahrenheit(Preconditions.checkNotNull(temperature).getDegreesKelvin()));
    }

    /**
     * Returns an integer representing the value of the inputted Temperature in degrees C.
     *
     * @param temperature Temperature to obtain the degrees C of
     * @return An int representing the inputted temperature in degrees C
     */
    public static int asCelsiusDegrees(Temperature temperature)
    {
        return (int) round(kelvinToCelsius(Preconditions.checkNotNull(temperature).getDegreesKelvin()));
    }

    /**
     * Takes an amount in K and returns that amount in C. Both values do not represent the actual temperatures but rather
     * degrees as a quantity.
     *
     * For instance, this function answers the question, if I heat something 5 degrees K how many degrees C have I heated
     * it.
     * @param kelvinValue The amount in C
     * @return An integer for the amount in K
     */
    public static int asCelsiusValue(double kelvinValue)
    {
        return (int) round(kelvinValue);
    }

    /**
     * Takes an amount in C and returns that amount in K. Both values do not represent the actual temperatures but rather
     * degrees as a quantity.
     *
     * For instance, this function answers the question, if I heat something 5 degrees C how many degrees K have I heated
     * it.
     * @param celsiusValue The amount in C
     * @return An integer for the amount in K
     */
    public static double asKelvinValueC(int celsiusValue)
    {
        return celsiusValue;
    }

    /**
     * Takes an amount in K and returns that amount in F. Both values do not represent the actual temperatures but rather
     * degrees as a quantity.
     *
     * For instance, this function answers the question, if I heat something 5 degrees K how many degrees F have I heated
     * it.
     * @param kelvinValue The amount in K
     * @return An integer for the amount in F.
     */
    public static int asFahrenheitValue(double kelvinValue)
    {
        return (int) round(kelvinValue / 0.556);
    }

    /**
     * Takes an amount in F and returns that amount in K. Both values do not represent the actual temperatures but rather
     * degrees as a quantity.
     *
     * For instance, this function answers the question, if I heat something 5 degrees F how many degrees K have I heated
     * it.
     *
     * @param fahrenheitValue The value in degrees F
     * @return A double value of the value in degrees K
     */
    public static double asKelvinValueF(int fahrenheitValue)
    {
        return fahrenheitValue * 0.556;
    }

    /**
     * Constructor for Temperature using degrees kelvin and a fuzz value. Both degrees kelvin and fuzz
     * are in Kelvin.
     *
     * @param degreesKelvin The temperature in degrees Kelvin
     * @param fuzz The "fuzziness" of the temperature in degrees Kelvin.
     */
    public Temperature(double degreesKelvin, double fuzz)
    {
        if (degreesKelvin < 0.0)
            throw new IllegalArgumentException("Temperatures below absolute zero are not physically possible");
        if (fuzz < 0.0)
            throw new IllegalArgumentException("Negative fuzz values are not allowed");

        this.degreesKelvin = degreesKelvin;
        this.fuzz = fuzz;
    }

    /**
     * Constructor that creates a Temperature using degrees kelvin. Fuzz will be 0.0 K.
     *
     * @param degreesKelvin The temperature amount in Kelvin
     */
    public Temperature(double degreesKelvin)
    {
        this(degreesKelvin, 0.0);
    }

    /**
     * Returns the temperature amount in degrees Kelvin
     * @return A double representing the temperature in degrees Kelvin
     */
    public double getDegreesKelvin()
    {
        return degreesKelvin;
    }

    /**
     * Compares two temperatures. Temperatures are only considered "different" if they differ by the "fuzz" amount and
     * the SIGNIFICANCE (see above). This allows a measure of confidence to be included when comparing two Temperature types.
     *
     * @param o The other Temperature that we are comparing to.
     * @return -1 if this temperature is less than o.
     *          0 If this temperature is equal to o.
     *          1 If this temperature is greater than o.
     */
    @Override
    public int compareTo(@NonNull Temperature o)
    {
        Preconditions.checkNotNull(o);

        if (abs(this.degreesKelvin - o.degreesKelvin) < SIGNIFICANCE + fuzz + o.getFuzz())
            return 0;
        else if (this.degreesKelvin < o.degreesKelvin)
            return -1;
        else
            return 1;
    }

    /**
     * Returns the "fuzziness" of the temperature. A temperature is only considered significantly
     * different from another temperature if the difference is greater than the fuzz amount.
     *
     * @return a double representing the fuzz in degrees Kelvin
     */
    public double getFuzz()
    {
        return fuzz;
    }

    /* Celsius Conversions */
    private static double celsiusToKelvin(double celsius)
    {
        return celsius + 273.15;
    }

    private static double kelvinToCelsius(double kelvin)
    {
        return kelvin - 273.15;
    }

    /* Fahrenheit Conversions */
    private static double fahrenheitToKelvin(double fahrenheit)
    {
        return (fahrenheit + 459.67) * (5.0 / 9.0);
    }

    private static double kelvinToFahrenheit(double kelvin)
    {
        return (9.0/5.0) * (kelvin - 273.15) + 32;
    }
}
