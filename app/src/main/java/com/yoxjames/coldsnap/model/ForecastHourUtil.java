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

package com.yoxjames.coldsnap.model;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import java.util.List;

/**
 * Created by yoxjames on 10/8/17.
 */

public class ForecastHourUtil
{
    public static class HighLowPair
    {
        final ForecastHour dailyHigh;
        final ForecastHour dailyLow;

        HighLowPair(ForecastHour dailyHigh, ForecastHour dailyLow)
        {
            this.dailyHigh = dailyHigh;
            this.dailyLow = dailyLow;
        }

        public ForecastHour getDailyHigh()
        {
            return dailyHigh;
        }

        public ForecastHour getDailyLow()
        {
            return dailyLow;
        }
    }

    public static HighLowPair getDailyHighLow(List<ForecastHour> forecastHours)
    {
        final Instant threshold = Instant.now().plus(Duration.ofDays(1));

        ForecastHour currentDailyHigh = null;
        ForecastHour currentDailyLow = null;

        for (ForecastHour hour : forecastHours)
            if (hour.getHour().isBefore(threshold))
            {
                if (currentDailyHigh == null || hour.getTemperature().getDegreesKelvin() > currentDailyHigh.getTemperature().getDegreesKelvin())
                    currentDailyHigh = hour;
                if (currentDailyLow == null || hour.getTemperature().getDegreesKelvin() < currentDailyLow.getTemperature().getDegreesKelvin())
                    currentDailyLow = hour;
            }
        return new HighLowPair(currentDailyHigh, currentDailyLow);
    }
}
