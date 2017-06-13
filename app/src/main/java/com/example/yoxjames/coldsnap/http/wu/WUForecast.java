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

package com.example.yoxjames.coldsnap.http.wu;

import com.example.yoxjames.coldsnap.model.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the "Forecast" data as returned from Wunderground. This will be used to
 * construct the {@link WeatherData} business object.
 */
class WUForecast
{
    private final List<Day> days;

    static WUForecast parseJSON(JSONObject forecast) throws JSONException
    {
        final List<Day> forecastDays = new ArrayList<>();
        final JSONArray jsonDays = forecast
                .getJSONObject("forecast")
                .getJSONObject("simpleforecast")
                .getJSONArray("forecastday");

        /* Loop through each day in forecastday and send that json object to factory in ForecastDays */
        for (int i = 0; i < jsonDays.length(); i++)
        {
            forecastDays.add(getForecastDay(jsonDays.getJSONObject(i)));
        }

        return new WUForecast(forecastDays);
    }

    /**
     * Static method to construct a ForecastDayOld from a piece of JSON returned from the Wunderground Weather
     * API. This is a private method designed to make parsing Wunderground data easier.
     *
     * @param dayJSON The JSONObject starting with "date"
     * @return a ForecastDayOld represented by the inputted JSONObject
     * @throws JSONException If the inputted JSON is invalid.
     */
    private static Day getForecastDay(JSONObject dayJSON) throws JSONException
    {
        final String dateString =
                dayJSON.getJSONObject("date").getString("monthname_short") + " " +
                        dayJSON.getJSONObject("date").getInt("day") + " " +
                        dayJSON.getJSONObject("date").getInt("year");
        final int lowTempF = dayJSON.getJSONObject("low").getInt("fahrenheit");
        final int highTempF = dayJSON.getJSONObject("high").getInt("fahrenheit");

        return new Day(highTempF, lowTempF, dateString);
    }

    private WUForecast(List<Day> days)
    {
        this.days = days;
    }

    List<Day> getDays()
    {
        return new ArrayList<>(days);
    }

    /**
     * Class representing the JSON array of Days returned from the Wunderground Forecast service.
     */
    static class Day
    {
        private final int highTempF;

        private final int lowTempF;

        private final String dateString;


        Day(int highTempF, int lowTempF, String dateString)
        {
            this.highTempF = highTempF;
            this.lowTempF = lowTempF;
            this.dateString = dateString;
        }

        /**
         * Gets the high temp in F. We are using F because it is more precise (as an integer) than C.
         *
         * @return An integer representing the day's high in F.
         */
        int getHighTempF()
        {
            return highTempF;
        }

        /**
         * Gets the low temp in F. We are using F because it is more precise (as an integer) than C.
         *
         * @return An integer representing the day's low in F.
         */
        int getLowTempF()
        {
            return lowTempF;
        }

        /**
         * Gets a string representing the day in question.
         *
         * @return A human readable string representing the day.
         */
        String getDateString()
        {
            return dateString;
        }
    }
}
