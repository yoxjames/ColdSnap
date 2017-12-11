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

package com.yoxjames.coldsnap.db.plant;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.yoxjames.coldsnap.db.ColdsnapDbSchema.PlantTable;
import com.yoxjames.coldsnap.model.Plant;

/**
 * Cursor wrapper for obtaining Plant objects. PlantRow is not used since the plant table
 * pretty perfectly coincides with a Plant POJO.
 */
public class PlantRowCursorWrapper extends CursorWrapper
{
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public PlantRowCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    /**
     * Gets the next {@link Plant}
     * @return A Plant
     */
    public PlantRow getPlant()
    {
        final long id = getLong(getColumnIndex(PlantTable.Cols.ID));
        final String uuid = getString(getColumnIndex(PlantTable.Cols.UUID));
        final String name = getString(getColumnIndex(PlantTable.Cols.NAME));
        final String scientificName = getString(getColumnIndex(PlantTable.Cols.SCIENTIFIC_NAME));
        final double coldThresholdAmt = getDouble(getColumnIndex(PlantTable.Cols.COLD_THRESHOLD_DEGREES));
        final String mainImageUUID = getString(getColumnIndex(PlantTable.Cols.MAIN_IMAGE_UUID));

        return new PlantRow.Builder()
                .id(id)
                .uuid(uuid)
                .name(name)
                .scientificName(scientificName)
                .coldThresholdK(coldThresholdAmt)
                .mainImageUUID(mainImageUUID)
                .build();
    }

    /**
     * This is used to get around Dagger's lack of assisted injection. The idea is to inject
     * the factory then issue the create method with the cursor to generate the PlantRowCursorWrapper
     *
     * This lets us inject an object then pass something at runtime (the Cursor) to generate
     * the object in question.
     */
    public interface Factory
    {
        /**
         * Generates the PlantRowCursorWrapper
         *
         * @param cursor The cursor to wrap
         * @return A PlantRowCursorWrapper using the cursor inputted.
         */
        PlantRowCursorWrapper create(Cursor cursor);
    }
}