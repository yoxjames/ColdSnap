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

package com.yoxjames.coldsnap.ui.plantlist;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by yoxjames on 11/4/17.
 */

public class PlantListRecyclerView extends RecyclerView
{

    public PlantListRecyclerView(Context context)
    {
        super(context);
    }

    public PlantListRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PlantListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
}
