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

package com.yoxjames.coldsnap.db;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yoxjames.coldsnap.db.mock.FakeForecastDayCursorWrapper;
import com.yoxjames.coldsnap.db.mock.MockForecastDayRowFactory;
import com.yoxjames.coldsnap.mocks.WeatherDataMockFactory;
import com.yoxjames.coldsnap.mocks.WeatherLocationMockFactory;
import com.yoxjames.coldsnap.model.ForecastDay;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.ui.CSPreferencesFragment;
import com.yoxjames.coldsnap.utils.RxColdSnap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.inject.Provider;
import dagger.Lazy;
import io.reactivex.observers.TestObserver;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherDataDAOSqliteImplTest
{
    private @Mock SQLiteDatabase database;
    private @Mock Provider<Lazy<ContentValues>> lazyProvider;
    private @Mock SharedPreferences sharedPreferences;
    private @Mock Provider<Lazy<ForecastDayCursorWrapper.Factory>> cursorWrapperFactory;
    private @Mock ForecastDayCursorWrapper cursorWrapper;
    private FakeForecastDayCursorWrapper fakeCursor;
    private static List<ForecastDayRow> fakeDatabase;

    private ForecastDay forecastDay = new ForecastDay("junit",
            Temperature.newTemperatureFromF(75),
            Temperature.newTemperatureFromF(32),
            new Date(),
            UUID.randomUUID());

    @BeforeClass
    public static void setupTests()
    {
        RxColdSnap.forceSynchronous();
    }
    @Before
    public void setup()
    {

        fakeDatabase = MockForecastDayRowFactory.getForecastDayRows();
        when(sharedPreferences.getString(CSPreferencesFragment.LOCATION_STRING, "Location")).thenReturn("Kansas City, MO");


        // This allows us to inject a mock CursorWrapper. The things we do for tests....
        when(cursorWrapperFactory.get()).thenReturn(new Lazy<ForecastDayCursorWrapper.Factory>()
        {
            @Override
            public ForecastDayCursorWrapper.Factory get()
            {
                return new ForecastDayCursorWrapper.Factory()
                {
                    @Override
                    public ForecastDayCursorWrapper create(Cursor cursor)
                    {
                        FakeForecastDayCursorWrapper fakeCursor = new FakeForecastDayCursorWrapper(cursor);
                        fakeCursor.injectDatabase(fakeDatabase);
                        return fakeCursor;
                    }
                };
            }
        });
    }

    @Test
    public void testSaveWeatherData()
    {

        when(lazyProvider.get()).thenReturn(new Lazy<ContentValues>()
                                            {
                                                @Override
                                                public ContentValues get()
                                                {
                                                    return mock(ContentValues.class);
                                                }
                                            });
        when(sharedPreferences.getFloat(CSPreferencesFragment.WEATHER_DATA_FUZZ,0f)).thenReturn(0f);
        List <ForecastDay> forecastDays = new ArrayList<>();
        forecastDays.add(forecastDay);
        forecastDays.add(forecastDay);

        WeatherLocation weatherLocation = new WeatherLocation("55555", "JNUITVILLE, TEST", 0f, 0f);
        WeatherData weatherData = new WeatherData(forecastDays, new Date(), weatherLocation);


        WeatherDataDAOSQLiteImpl weatherDataDAOSQLite = new WeatherDataDAOSQLiteImpl(lazyProvider, cursorWrapperFactory, sharedPreferences);

        TestObserver testObserver = weatherDataDAOSQLite.saveWeatherData(database, weatherData).test();
        testObserver.assertComplete();
        verify(database, times(2)).insert(eq(ColdsnapDbSchema.ForecastDayTable.NAME), isNull(String.class), any(ContentValues.class));
    }

    @Test
    public void getWeatherDataTest() throws WeatherDataNotFoundException
    {
        WeatherDataDAOSQLiteImpl weatherDataDAOSQLite = new WeatherDataDAOSQLiteImpl(lazyProvider, cursorWrapperFactory, sharedPreferences);

        TestObserver<WeatherData> weatherDataTestObserver = weatherDataDAOSQLite.getWeatherData(database, WeatherLocationMockFactory.kansasCity()).test();

        WeatherData weatherData = weatherDataTestObserver.values().get(0);

        assertEquals(weatherData.getWeatherLocation().getZipCode(), WeatherLocationMockFactory.kansasCity().getZipCode());
        assertEquals(weatherData.getTodayLow().compareTo(new Temperature(273.0)), 0);
        assertEquals(weatherData.getTodayHigh().compareTo(new Temperature(300.0)), 0);

        assertEquals(WeatherLocationMockFactory.kansasCity().getPlaceString(), weatherData.getWeatherLocation().getPlaceString());

        assertEquals(weatherData.getForecastDays().get(0).getLowTemperature().compareTo(new Temperature(273.0)), 0);
        assertEquals(weatherData.getForecastDays().get(0).getHighTemperature().compareTo(new Temperature(300.0)), 0);

        assertEquals(weatherData.getForecastDays().get(1).getLowTemperature().compareTo(new Temperature(283.0)), 0);
        assertEquals(weatherData.getForecastDays().get(1).getHighTemperature().compareTo(new Temperature(293.0)), 0);

        assertEquals(weatherData.getForecastDays().get(2).getLowTemperature().compareTo(new Temperature(285.0)), 0);
        assertEquals(weatherData.getForecastDays().get(2).getHighTemperature().compareTo(new Temperature(299.0)), 0);

        assertEquals(weatherData.getForecastDays().get(3).getLowTemperature().compareTo(new Temperature(290.0)), 0);
        assertEquals(weatherData.getForecastDays().get(3).getHighTemperature().compareTo(new Temperature(299.0)), 0);
    }

    public void getWeatherDataNotFoundTest() throws WeatherDataNotFoundException
    {
        when(cursorWrapperFactory.get()).thenReturn(new Lazy<ForecastDayCursorWrapper.Factory>()
        {
            @Override
            public ForecastDayCursorWrapper.Factory get()
            {
                return new ForecastDayCursorWrapper.Factory()
                {
                    @Override
                    public ForecastDayCursorWrapper create(Cursor cursor)
                    {
                        FakeForecastDayCursorWrapper fakeCursor = new FakeForecastDayCursorWrapper(cursor);
                        fakeCursor.injectDatabase(new ArrayList<ForecastDayRow>()); // Empty DB
                        return fakeCursor;
                    }
                };
            }
        });
        WeatherDataDAOSQLiteImpl weatherDataDAOSQLite = new WeatherDataDAOSQLiteImpl(lazyProvider, cursorWrapperFactory, sharedPreferences);

        TestObserver testObserver = weatherDataDAOSQLite.getWeatherData(database, WeatherLocationMockFactory.kansasCity()).test();
        testObserver.assertError(WeatherDataNotFoundException.class);
    }
}
