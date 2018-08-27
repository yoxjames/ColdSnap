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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;

public class TemperatureTest
{

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testAttributesF()
    {
        Temperature temperature = Temperature.newTemperatureFromF(32);
        assertEquals(temperature.getDegreesKelvin(), 273.15, .001);
        assertEquals(Temperature.asFahrenheitDegrees(temperature), 32);
        assertEquals(Temperature.asCelsiusDegrees(temperature), 0);
    }

    @Test
    public void testAttributesC()
    {
        Temperature temperature = Temperature.newTemperatureFromC(0);
        assertEquals(temperature.getDegreesKelvin(), 273.15, .001);
        assertEquals(Temperature.asFahrenheitDegrees(temperature), 32);
        assertEquals(Temperature.asCelsiusDegrees(temperature), 0);
    }

    @Test
    public void assertFromKelvin()
    {
        Temperature temperature = new Temperature(300.0);
        assertEquals(80, Temperature.asFahrenheitDegrees(temperature));
        assertEquals(27, Temperature.asCelsiusDegrees(temperature));
    }

    @Test
    public void testEqualTempsSameScale()
    {
        Temperature a = Temperature.newTemperatureFromF(32);
        Temperature b = Temperature.newTemperatureFromF(32);

        Temperature c = Temperature.newTemperatureFromC(0);
        Temperature d = Temperature.newTemperatureFromC(0);

        assertEquals(a.compareTo(b), 0);
        assertEquals(c.compareTo(d), 0);

        assertEquals(b.compareTo(a), 0);
        assertEquals(d.compareTo(c), 0);
    }

    @Test
    public void testSameScaleComparison()
    {
        Temperature a = Temperature.newTemperatureFromF(32);
        Temperature b = Temperature.newTemperatureFromF(40);

        Temperature c = Temperature.newTemperatureFromC(0);
        Temperature d = Temperature.newTemperatureFromC(10);

        assertEquals(a.compareTo(b), -1);
        assertEquals(c.compareTo(d), -1);

        assertEquals(b.compareTo(a), 1);
        assertEquals(d.compareTo(c), 1);
    }

    @Test
    public void testDifferentScaleEquality()
    {
        Temperature a = Temperature.newTemperatureFromF(32);
        Temperature b = Temperature.newTemperatureFromC(0);

        Temperature c = Temperature.newTemperatureFromF(99);
        Temperature d = Temperature.newTemperatureFromC(37);

        assertEquals(a.compareTo(b), 0);
        assertEquals(b.compareTo(a), 0);

        assertEquals(0, c.compareTo(d));
        assertEquals(d.compareTo(c), 0);
    }

    @Test
    public void testDifferentScaleComparison()
    {
        Temperature a = Temperature.newTemperatureFromF(32);
        Temperature b = Temperature.newTemperatureFromC(1);

        Temperature c = Temperature.newTemperatureFromF(100);
        Temperature d = Temperature.newTemperatureFromC(37);

        assertEquals(a.compareTo(b), -1);
        assertEquals(b.compareTo(a), 1);

        assertEquals(c.compareTo(d), 1);
        assertEquals(d.compareTo(c), -1);
    }

    @Test
    public void testAnotherTempScaleComparison()
    {
        Temperature a = Temperature.newTemperatureFromC(8);
        Temperature b = Temperature.newTemperatureFromF(59);

        assertEquals(a.compareTo(b), -1);
    }

    @Test
    public void testAbsoluteZeroK()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Temperatures below absolute zero are not physically possible");
        new Temperature(-1.0);
    }

    @Test
    public void testAbsoluteZeroC()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Temperatures below absolute zero are not physically possible");
        Temperature.newTemperatureFromC(-274);
    }

    @Test
    public void testAbsoluteZeroF()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Temperatures below absolute zero are not physically possible");
        Temperature.newTemperatureFromF(-460);
    }

    @Test
    public void testExtraCompareTo()
    {
        Temperature temperatureA = new Temperature(10, 5);

        assertEquals(Temperature.LESSER, temperatureA.compareSignificanceTo(new Temperature(16)));
        assertEquals(Temperature.GREATER, temperatureA.compareSignificanceTo(new Temperature(4)));
        assertEquals(Temperature.MAYBE_LESSER, temperatureA.compareSignificanceTo(new Temperature(13)));
        assertEquals(Temperature.MAYBE_GREATER, temperatureA.compareSignificanceTo(new Temperature(8)));
    }

    @Test
    public void testExtraCompareToDualFuzz()
    {
        Temperature temperatureA = new Temperature(10, 5);

        assertEquals(Temperature.LESSER, temperatureA.compareSignificanceTo(new Temperature(25, 5)));
        assertEquals(Temperature.GREATER, temperatureA.compareSignificanceTo(new Temperature(1, 3)));
        assertEquals(Temperature.MAYBE_LESSER, temperatureA.compareSignificanceTo(new Temperature(17,4)));
        assertEquals(Temperature.MAYBE_GREATER, temperatureA.compareSignificanceTo(new Temperature(1,5)));
    }
}
