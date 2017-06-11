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

import com.example.yoxjames.coldsnap.ui.view.PlantListItemView;

/**
 * Presenter for the main plant list Fragment.
 */
public interface PlantListPresenter
{
    /**
     * Loads and reloads all the information of the view (the list of plants).
     */
    void load();

    /**
     * Loads the Plant into the list at position.
     *
     * @param view The view representing the GUI object at position position.
     * @param position The position in the Plant List of the plant.
     */
    void loadPlantView(PlantListItemView view, int position);

    /**
     * Gets the total count of Plants in the list.
     *
     * @return An integer representing the size of all plants in the system.
     */
    int getItemCount();

    /**
     * Called when the user wishes to create a new Plant. Should cache a new plant and launch
     * the user into a detail view where they can edit this new Plant.
     */
    void newPlant();
}
