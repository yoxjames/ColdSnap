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

package com.yoxjames.coldsnap.db.image;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.yoxjames.coldsnap.db.ColdsnapDbSchema.ImageTable;

/**
 * Created by yoxjames on 10/14/17.
 */

public class PlantImageRowCursorWrapper extends CursorWrapper
{
    public PlantImageRowCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public PlantImageRow getPlantImageRow()
    {
        final long id = getLong(getColumnIndex(ImageTable.Cols.ID));
        final String uuid = getString(getColumnIndex(ImageTable.Cols.UUID));
        final String plantUUID = getString(getColumnIndex(ImageTable.Cols.PLANT_UUID));
        final String title = getString(getColumnIndex(ImageTable.Cols.TITLE));
        final String imageFilename = getString(getColumnIndex(ImageTable.Cols.IMAGE_FILENAME));
        final long imageDate = getLong(getColumnIndex(ImageTable.Cols.IMAGE_DATE));

        return new PlantImageRow.Builder()
                .id(id)
                .uuid(uuid)
                .plantUUID(plantUUID)
                .title(title)
                .imageFilename(imageFilename)
                .imageDate(imageDate)
                .build();
    }

    public interface Factory
    {
        PlantImageRowCursorWrapper create(Cursor cursor);
    }
}
