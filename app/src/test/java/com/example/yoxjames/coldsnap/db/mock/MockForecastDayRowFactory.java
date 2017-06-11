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

package com.example.yoxjames.coldsnap.db.mock;

import com.example.yoxjames.coldsnap.db.ForecastDayRow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MockForecastDayRowFactory
{
    public static List<ForecastDayRow> getForecastDayRows()
    {
        List<ForecastDayRow> forecastDayRows = new ArrayList<>();
        forecastDayRows.add(new ForecastDayRow.Builder()
                .forecastUUID(UUID.randomUUID().toString())
                .date("JAN 01 2017")
                .zipCode("55555")
                .syncDateTime(new Date().getTime())
                .lowTempK(273)
                .highTempK(300)
                .build());

        forecastDayRows.add(new ForecastDayRow.Builder()
                .forecastUUID(UUID.randomUUID().toString())
                .date("JAN 02 2017")
                .zipCode("55555")
                .syncDateTime(new Date().getTime())
                .lowTempK(283)
                .highTempK(293)
                .build());

        forecastDayRows.add(new ForecastDayRow.Builder()
                .forecastUUID(UUID.randomUUID().toString())
                .date("JAN 03 2017")
                .zipCode("55555")
                .syncDateTime(new Date().getTime())
                .lowTempK(285)
                .highTempK(299)
                .build());

        forecastDayRows.add(new ForecastDayRow.Builder()
                .forecastUUID(UUID.randomUUID().toString())
                .date("JAN 04 2017")
                .zipCode("55555")
                .syncDateTime(new Date().getTime())
                .lowTempK(290)
                .highTempK(299)
                .build());

        return forecastDayRows;
    }
}
