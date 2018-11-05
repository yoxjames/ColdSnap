package com.yoxjames.coldsnap.reducer;

import android.graphics.Color;

import com.yoxjames.coldsnap.model.ForecastHour;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.ui.cards.graph.DataSeries;
import com.yoxjames.coldsnap.ui.cards.graph.DataValue;
import com.yoxjames.coldsnap.ui.cards.graph.GraphCardViewModel;
import com.yoxjames.coldsnap.ui.cards.graph.GraphConfig;
import com.yoxjames.coldsnap.ui.cards.graph.GraphLine;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class TemperatureCardReducer
{
    private final TemperatureValueAdapter temperatureValueAdapter;
    private final TemperatureFormatter temperatureFormatter;
    private final CSPreferences csPreferences;

    @Inject
    public TemperatureCardReducer(
        TemperatureValueAdapter temperatureValueAdapter,
        TemperatureFormatter temperatureFormatter,
        CSPreferences csPreferences)
    {
        this.temperatureValueAdapter = temperatureValueAdapter;
        this.temperatureFormatter = temperatureFormatter;
        this.csPreferences = csPreferences;
    }

    public GraphCardViewModel reduce(WeatherData weatherData)
    {
        final List<Integer> temperatures = weatherData.getForecastHours().stream()
            .map(ForecastHour::getTemperature)
            .map(temperatureValueAdapter::getValue)
            .collect(Collectors.toList());

        final double rangeLowerBound = temperatures.stream().mapToDouble(i -> i).min().orElse(0) - 10;
        final double rangeUpperBound = temperatures.stream().mapToDouble(i -> i).max().orElse(100) + 10;
        final long thisMidnightEpochSeconds = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond() - 60;
        final long maxTimeRange = weatherData.getForecastHours().get(weatherData.getForecastHours().size() - 1).getHour().getEpochSecond();

        List<GraphLine> graphLines = new ArrayList<>();
        graphLines.add(GraphLine.create((double) temperatureValueAdapter.getValue(Temperature.fromCelsius(0)), Color.BLUE));
        graphLines.add(GraphLine.create((double) temperatureValueAdapter.getValue(csPreferences.getThreshold()), Color.GREEN));

        final GraphConfig graphConfig = GraphConfig.builder()
            .rangeLowerBound(rangeLowerBound)
            .rangeUpperBound(rangeUpperBound)
            .domainLowerBound(thisMidnightEpochSeconds)
            .domainUpperBound(maxTimeRange)
            .domainStep(86400)
            .rangeStep(10)
            .build();

        return GraphCardViewModel.builder()
            .title("Temperature")
            .rangeLines(Collections.emptyList())
            .graphConfig(graphConfig)
            .dataSeries(Collections.singletonList(getTemperatureSeries(weatherData)))
            .rangeLines(graphLines)
            .YFormatter(this::formatTemperature)
            .XFormatter(this::localizeDateTime)
            .build();
    }

    private DataSeries getTemperatureSeries(WeatherData weatherData)
    {
        return DataSeries.builder()
            .data(weatherData.getForecastHours().stream()
                .map(this::mapToDataValue)
                .collect(Collectors.toList()))
            .lineColor(Color.RED)
            .build();
    }

    private DataValue mapToDataValue(ForecastHour forecastHour)
    {
        return DataValue.create(forecastHour.getHour().getEpochSecond(), temperatureValueAdapter.getValue(forecastHour.getTemperature()));
    }

    private String formatTemperature(double value)
    {
        return temperatureFormatter.format(temperatureValueAdapter.getTemperature((int) value));
    }

    private String localizeDateTime(double value)
    {
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond((long) value), ZoneId.systemDefault());
        final DateTimeFormatter localFormatter = DateTimeFormatter.ofPattern("MM/dd");

        return localDateTime.format(localFormatter);
    }
}
