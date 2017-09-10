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

package com.yoxjames.coldsnap.db.mock;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import com.yoxjames.coldsnap.db.ColdsnapDbSchema;
import com.yoxjames.coldsnap.db.ForecastDayCursorWrapper;
import com.yoxjames.coldsnap.db.ForecastDayRow;

import java.util.List;

public class FakeForecastDayCursorWrapper extends ForecastDayCursorWrapper
{
    public FakeForecastDayCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    private enum COLS
    {
        UUID,
        DATE,
        ZIPCODE,
        FETCH_DATE,
        LOW_TEMP_K,
        HIGH_TEMP_K
    }

    private int position;
    private List<ForecastDayRow> forecastDayRows;

    public void injectDatabase(List<ForecastDayRow> forecastDayRows)
    {
        this.forecastDayRows = forecastDayRows;
    }

    @Override
    public int getCount()
    {
        return 0;
    }

    @Override
    public int getPosition()
    {
        return 0;
    }

    @Override
    public boolean move(int offset)
    {
        return false;
    }

    @Override
    public boolean moveToPosition(int position)
    {
        return false;
    }

    @Override
    public boolean moveToFirst()
    {
        position = 0;
        return false;
    }

    @Override
    public boolean moveToLast()
    {
        return false;
    }

    @Override
    public boolean moveToNext()
    {
        position += 1;
        return false;
    }

    @Override
    public boolean moveToPrevious()
    {
        return false;
    }

    @Override
    public boolean isFirst()
    {
        return false;
    }

    @Override
    public boolean isLast()
    {
        return false;
    }

    @Override
    public boolean isBeforeFirst()
    {
        return false;
    }

    @Override
    public boolean isAfterLast()
    {
        return position >= forecastDayRows.size();
    }

    @Override
    public int getColumnIndex(String columnName)
    {
        switch (columnName)
        {
            case ColdsnapDbSchema.ForecastDayTable.Cols.UUID:
                return COLS.UUID.ordinal();
            case ColdsnapDbSchema.ForecastDayTable.Cols.DATE:
                return COLS.DATE.ordinal();
            case ColdsnapDbSchema.ForecastDayTable.Cols.ZIPCODE:
                return COLS.ZIPCODE.ordinal();
            case ColdsnapDbSchema.ForecastDayTable.Cols.FETCH_DATE:
                return COLS.FETCH_DATE.ordinal();
            case ColdsnapDbSchema.ForecastDayTable.Cols.LOW_TEMP_K:
                return COLS.LOW_TEMP_K.ordinal();
            case ColdsnapDbSchema.ForecastDayTable.Cols.HIGH_TEMP_K:
                return COLS.HIGH_TEMP_K.ordinal();
            default:
                throw new IllegalArgumentException("Invalid Column");
        }
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException
    {
        return 0;
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        return null;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[0];
    }

    @Override
    public int getColumnCount()
    {
        return 0;
    }

    @Override
    public byte[] getBlob(int columnIndex)
    {
        return new byte[0];
    }

    @Override
    public String getString(int columnIndex)
    {
        if (columnIndex == COLS.UUID.ordinal())
            return forecastDayRows.get(position).getForecastUUID();
        else if (columnIndex == COLS.DATE.ordinal())
            return forecastDayRows.get(position).getDate();
        else if (columnIndex == COLS.ZIPCODE.ordinal())
            return forecastDayRows.get(position).getZipCode();
        else
            throw new IllegalArgumentException("Invalid column index");
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer)
    {

    }

    @Override
    public short getShort(int columnIndex)
    {
        return 0;
    }

    @Override
    public int getInt(int columnIndex)
    {
        return 0;
    }

    @Override
    public long getLong(int columnIndex)
    {
        if (columnIndex == COLS.FETCH_DATE.ordinal())
            return forecastDayRows.get(position).getSyncDateTime();
        else
            throw new IllegalArgumentException("Invalid column index");
    }

    @Override
    public float getFloat(int columnIndex)
    {
        return 0;
    }

    @Override
    public double getDouble(int columnIndex)
    {
        if (columnIndex == COLS.LOW_TEMP_K.ordinal())
            return forecastDayRows.get(position).getLowTempK();
        else if (columnIndex == COLS.HIGH_TEMP_K.ordinal())
            return forecastDayRows.get(position).getHighTempK();
        else
            throw new IllegalArgumentException("Invalid column index");
    }

    @Override
    public int getType(int columnIndex)
    {
        return 0;
    }

    @Override
    public boolean isNull(int columnIndex)
    {
        return false;
    }

    @Override
    public void deactivate()
    {

    }

    @Override
    public boolean requery()
    {
        return false;
    }

    @Override
    public void close()
    {

    }

    @Override
    public boolean isClosed()
    {
        return false;
    }

    @Override
    public void registerContentObserver(ContentObserver observer)
    {

    }

    @Override
    public void unregisterContentObserver(ContentObserver observer)
    {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer)
    {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer)
    {

    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri)
    {

    }

    @Override
    public Uri getNotificationUri()
    {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls()
    {
        return false;
    }

    @Override
    public void setExtras(Bundle extras)
    {

    }

    @Override
    public Bundle getExtras()
    {
        return null;
    }

    @Override
    public Bundle respond(Bundle extras)
    {
        return null;
    }
}
