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

package com.example.yoxjames.coldsnap.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.yoxjames.coldsnap.db.ColdsnapDbSchema.ForecastDayTable;

/**
 * Cursor wrapper for obtaining ForecastDayRow objects
 */
public class ForecastDayCursorWrapper extends CursorWrapper
{
    /**
     * Constructor for the CursorWrapper
     * @param cursor A cursor to wrap
     */
    public ForecastDayCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    /**
     * Gets the next {@link ForecastDayRow}
     *
     * @return A ForecastDayRow
     */
    ForecastDayRow getForecastDay()
    {
        return new ForecastDayRow.Builder()
                .forecastUUID(getString(getColumnIndex(ForecastDayTable.Cols.UUID)))
                .date(getString(getColumnIndex(ForecastDayTable.Cols.DATE)))
                .zipCode(getString(getColumnIndex(ForecastDayTable.Cols.ZIPCODE)))
                .syncDateTime(getLong(getColumnIndex(ForecastDayTable.Cols.FETCH_DATE)))
                .lowTempK(getDouble(getColumnIndex(ForecastDayTable.Cols.LOW_TEMP_K)))
                .highTempK(getDouble(getColumnIndex(ForecastDayTable.Cols.HIGH_TEMP_K)))
                .build();
    }

    /**
     * Factory that will generate ForecastDayWrappers. This exists because Dagger does not currently
     * have assisted injection. Therefore I am using Dagger to inject a Factory which then creates
     * a ForecastDayCursorWrapper based on the inputted cursor.
     */
    public interface Factory
    {
        /**
         * Generates the ForecastDayWrapper
         *
         * @param cursor A cursor
         * @return A ForecastDayCursorWrapper wrapping the cursor in the parameters.
         */
        ForecastDayCursorWrapper create(Cursor cursor);
    }
}
