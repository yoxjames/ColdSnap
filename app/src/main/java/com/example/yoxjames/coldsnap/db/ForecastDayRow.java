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

import com.example.yoxjames.coldsnap.model.ForecastDay;
import com.example.yoxjames.coldsnap.model.WeatherData;

/**
 * This class is a POJO that literally represents a forecast_day row. This differs from the business
 * object {@link ForecastDay}. ForecastDayRow objects can be used to construct a {@link ForecastDay} but there is some
 * additional data on ForecastDayRow that is used to make {@link WeatherData} objects.
 *
 * This separation is to differentiation between the objects stored on the database and the actual business objects.
 * It's possible that a business object may be stored on many tables or many business objects stored on one table.
 */
public class ForecastDayRow
{
    private final String forecastUUID;
    private final String date;
    private final String zipCode;
    private final long syncDateTime;
    private final double lowTempK;
    private final double highTempK;

    /*
     * Private constructor for the Builder pattern.
     */
    private ForecastDayRow(Builder builder)
    {
        this.forecastUUID = builder.forecastUUID;
        this.date = builder.date;
        this.zipCode = builder.zipCode;
        this.syncDateTime = builder.syncDateTime;
        this.lowTempK = builder.lowTempK;
        this.highTempK = builder.highTempK;
    }

    /**
     * Gets the forecast UUID.
     *
     * @return The Forecast UUID
     */
    public String getForecastUUID()
    {
        return forecastUUID;
    }

    public String getDate()
    {
        return date;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public long getSyncDateTime()
    {
        return syncDateTime;
    }

    public double getLowTempK()
    {
        return lowTempK;
    }

    public double getHighTempK()
    {
        return highTempK;
    }

    /**
     * Fluid Builder used to construct ForecastDayRows. Usage is self explanatory.
     */
    public static class Builder
    {
        private String forecastUUID;
        private String date;
        private String zipCode;
        private long syncDateTime;
        private double lowTempK;
        private double highTempK;

        public Builder forecastUUID(String forecastUUID)
        {
            this.forecastUUID = forecastUUID;
            return this;
        }

        public Builder date(String date)
        {
            this.date = date;
            return this;
        }

        public Builder zipCode(String zipCode)
        {
            this.zipCode = zipCode;
            return this;
        }

        public Builder syncDateTime(long syncDateTime)
        {
            this.syncDateTime = syncDateTime;
            return this;
        }

        public Builder highTempK(double highTempK)
        {
            this.highTempK = highTempK;
            return this;
        }

        public Builder lowTempK(double lowTempK)
        {
            this.lowTempK = lowTempK;
            return this;
        }

        /**
         * Constructs the forecastDayRow. Please note that there is no enforced business logic
         * for this POJO. All validation will be done on {@link ForecastDay} and {@link WeatherData}
         *
         * @return A new ForecastDayRow object
         */
        public ForecastDayRow build()
        {
            return new ForecastDayRow(this);
        }
    }
}
