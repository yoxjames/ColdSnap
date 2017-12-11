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

package com.yoxjames.coldsnap.db.weather;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.yoxjames.coldsnap.db.ColdsnapDbSchema;

/**
 * Cursor wrapper for obtaining ForecastHourRow objects
 */
public class ForecastDayRowCursorWrapper extends CursorWrapper
{
    /**
     * Constructor for the CursorWrapper
     * @param cursor A cursor to wrap
     */
    public ForecastDayRowCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    /**
     * Gets the next {@link ForecastHourRow}
     *
     * @return A ForecastHourRow
     */
    public ForecastHourRow getForecastDay()
    {
        return new ForecastHourRow.Builder()
                .forecastUUID(getString(getColumnIndex(ColdsnapDbSchema.ForecastHourTable.Cols.UUID)))
                .hourInstant(getLong(getColumnIndex(ColdsnapDbSchema.ForecastHourTable.Cols.HOUR_INSTANCE)))
                .syncInstant(getLong(getColumnIndex(ColdsnapDbSchema.ForecastHourTable.Cols.FETCH_INSTANCE)))
                .tempK(getDouble(getColumnIndex(ColdsnapDbSchema.ForecastHourTable.Cols.TEMP_K)))
                .fuzzK(getDouble(getColumnIndex(ColdsnapDbSchema.ForecastHourTable.Cols.FUZZ_K)))
                .lat(getDouble(getColumnIndex(ColdsnapDbSchema.ForecastHourTable.Cols.LAT)))
                .lon(getDouble(getColumnIndex(ColdsnapDbSchema.ForecastHourTable.Cols.LON)))
                .build();
    }

    /**
     * Factory that will generate ForecastDayWrappers. This exists because Dagger does not currently
     * have assisted injection. Therefore I am using Dagger to inject a Factory which then creates
     * a ForecastDayRowCursorWrapper based on the inputted cursor.
     */
    public interface Factory
    {
        /**
         * Generates the ForecastDayWrapper
         *
         * @param cursor A cursor
         * @return A ForecastDayRowCursorWrapper wrapping the cursor in the parameters.
         */
        ForecastDayRowCursorWrapper create(Cursor cursor);
    }
}
