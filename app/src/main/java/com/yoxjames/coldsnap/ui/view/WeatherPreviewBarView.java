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

package com.yoxjames.coldsnap.ui.view;

/**
 * View representing the current forecast preview.
 */
public interface WeatherPreviewBarView
{
    /**
     * Refreshes the UI.
     */
    void updateUI();

    /**
     * Sets the location text.
     *
     * @param locationText Location the weather data is for.
     */
    void setLocationText(String locationText);

    /**
     * Sets the current high temperature. This should be correctly formatted for the user.
     *
     * @param highText String representing the high temperature. Needs to be formatted.
     */
    void setHighText(String highText);

    /**
     * Sets the current low temperature. This should be correctly formatted for the user.
     *
     * @param lowText String representing the low temperature. Needs to be formatted.
     */
    void setLowText(String lowText);

    /**
     * Sets the last updated text.
     *
     * @param lastUpdatedText Text suitable for the user that displays when the WeatherData was last
     *                        updated.
     */
    void setLastUpdatedText(String lastUpdatedText);

    /**
     * Sets the view as visible. This should be done once all data is present. This will remove the progress
     * indicator and show the actual view content.
     */
    void setContentVisible();
}
