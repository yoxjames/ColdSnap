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

package com.example.yoxjames.coldsnap.ui.presenter;

import java.util.UUID;

/**
 * Presenter for the PlantList in the main fragment.
 */
public interface PlantDetailPagerPresenter
{
    /**
     * Obtains the amount of plants saved (not cached) in the system.
     * @return An integer representing the total amount of plants.
     */
    int getPlantsSize();

    /**
     * Gets a UUID for each plant at position plantIndex
     * @param plantIndex Index in the list view for each pl
     *                   ant.
     * @return A UUID that can be used to obtain a Plant object.
     */
    UUID getPlant(int plantIndex);
}
