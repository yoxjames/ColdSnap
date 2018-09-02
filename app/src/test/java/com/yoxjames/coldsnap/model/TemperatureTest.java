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
        LegacyTemperature temperature = LegacyTemperature.newTemperatureFromF(32);
        assertEquals(temperature.getDegreesKelvin(), 273.15, .001);
        assertEquals(LegacyTemperature.asFahrenheitDegrees(temperature), 32);
        assertEquals(LegacyTemperature.asCelsiusDegrees(temperature), 0);
    }

    @Test
    public void testAttributesC()
    {
        LegacyTemperature temperature = LegacyTemperature.newTemperatureFromC(0);
        assertEquals(temperature.getDegreesKelvin(), 273.15, .001);
        assertEquals(LegacyTemperature.asFahrenheitDegrees(temperature), 32);
        assertEquals(LegacyTemperature.asCelsiusDegrees(temperature), 0);
    }

    @Test
    public void assertFromKelvin()
    {
        LegacyTemperature temperature = new LegacyTemperature(300.0);
        assertEquals(80, LegacyTemperature.asFahrenheitDegrees(temperature));
        assertEquals(27, LegacyTemperature.asCelsiusDegrees(temperature));
    }

    @Test
    public void testEqualTempsSameScale()
    {
        LegacyTemperature a = LegacyTemperature.newTemperatureFromF(32);
        LegacyTemperature b = LegacyTemperature.newTemperatureFromF(32);

        LegacyTemperature c = LegacyTemperature.newTemperatureFromC(0);
        LegacyTemperature d = LegacyTemperature.newTemperatureFromC(0);

        assertEquals(a.compareTo(b), 0);
        assertEquals(c.compareTo(d), 0);

        assertEquals(b.compareTo(a), 0);
        assertEquals(d.compareTo(c), 0);
    }

    @Test
    public void testSameScaleComparison()
    {
        LegacyTemperature a = LegacyTemperature.newTemperatureFromF(32);
        LegacyTemperature b = LegacyTemperature.newTemperatureFromF(40);

        LegacyTemperature c = LegacyTemperature.newTemperatureFromC(0);
        LegacyTemperature d = LegacyTemperature.newTemperatureFromC(10);

        assertEquals(a.compareTo(b), -1);
        assertEquals(c.compareTo(d), -1);

        assertEquals(b.compareTo(a), 1);
        assertEquals(d.compareTo(c), 1);
    }

    @Test
    public void testDifferentScaleEquality()
    {
        LegacyTemperature a = LegacyTemperature.newTemperatureFromF(32);
        LegacyTemperature b = LegacyTemperature.newTemperatureFromC(0);

        LegacyTemperature c = LegacyTemperature.newTemperatureFromF(99);
        LegacyTemperature d = LegacyTemperature.newTemperatureFromC(37);

        assertEquals(a.compareTo(b), 0);
        assertEquals(b.compareTo(a), 0);

        assertEquals(0, c.compareTo(d));
        assertEquals(d.compareTo(c), 0);
    }

    @Test
    public void testDifferentScaleComparison()
    {
        LegacyTemperature a = LegacyTemperature.newTemperatureFromF(32);
        LegacyTemperature b = LegacyTemperature.newTemperatureFromC(1);

        LegacyTemperature c = LegacyTemperature.newTemperatureFromF(100);
        LegacyTemperature d = LegacyTemperature.newTemperatureFromC(37);

        assertEquals(a.compareTo(b), -1);
        assertEquals(b.compareTo(a), 1);

        assertEquals(c.compareTo(d), 1);
        assertEquals(d.compareTo(c), -1);
    }

    @Test
    public void testAnotherTempScaleComparison()
    {
        LegacyTemperature a = LegacyTemperature.newTemperatureFromC(8);
        LegacyTemperature b = LegacyTemperature.newTemperatureFromF(59);

        assertEquals(a.compareTo(b), -1);
    }

    @Test
    public void testAbsoluteZeroK()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Temperatures below absolute zero are not physically possible");
        new LegacyTemperature(-1.0);
    }

    @Test
    public void testAbsoluteZeroC()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Temperatures below absolute zero are not physically possible");
        LegacyTemperature.newTemperatureFromC(-274);
    }

    @Test
    public void testAbsoluteZeroF()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Temperatures below absolute zero are not physically possible");
        LegacyTemperature.newTemperatureFromF(-460);
    }

    @Test
    public void testExtraCompareTo()
    {
        LegacyTemperature temperatureA = new LegacyTemperature(10, 5);

        assertEquals(LegacyTemperature.LESSER, temperatureA.compareSignificanceTo(new LegacyTemperature(16)));
        assertEquals(LegacyTemperature.GREATER, temperatureA.compareSignificanceTo(new LegacyTemperature(4)));
        assertEquals(LegacyTemperature.MAYBE_LESSER, temperatureA.compareSignificanceTo(new LegacyTemperature(13)));
        assertEquals(LegacyTemperature.MAYBE_GREATER, temperatureA.compareSignificanceTo(new LegacyTemperature(8)));
    }

    @Test
    public void testExtraCompareToDualFuzz()
    {
        LegacyTemperature temperatureA = new LegacyTemperature(10, 5);

        assertEquals(LegacyTemperature.LESSER, temperatureA.compareSignificanceTo(new LegacyTemperature(25, 5)));
        assertEquals(LegacyTemperature.GREATER, temperatureA.compareSignificanceTo(new LegacyTemperature(1, 3)));
        assertEquals(LegacyTemperature.MAYBE_LESSER, temperatureA.compareSignificanceTo(new LegacyTemperature(17,4)));
        assertEquals(LegacyTemperature.MAYBE_GREATER, temperatureA.compareSignificanceTo(new LegacyTemperature(1,5)));
    }
}
