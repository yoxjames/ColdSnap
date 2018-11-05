package com.yoxjames.coldsnap.reducer;

import android.graphics.Color;

import com.yoxjames.coldsnap.model.ForecastHour;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.Windspeed;
import com.yoxjames.coldsnap.ui.cards.graph.DataSeries;
import com.yoxjames.coldsnap.ui.cards.graph.DataValue;
import com.yoxjames.coldsnap.ui.cards.graph.GraphCardViewModel;
import com.yoxjames.coldsnap.ui.cards.graph.GraphConfig;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class WindspeedCardReducer
{
    @Inject
    public WindspeedCardReducer() { }

    public GraphCardViewModel reduce(WeatherData weatherData)
    {
        final List<Double> windspeeds = weatherData.getForecastHours().stream()
            .map(ForecastHour::getWindspeed)
            .map(Windspeed::getMetersPerSecond)
            .collect(Collectors.toList());

        final double rangeLowerBound = windspeeds.stream().mapToDouble(i -> i).min().orElse(0) - 0.2;
        final double rangeUpperBound = windspeeds.stream().mapToDouble(i -> i).max().orElse(100) + 0.2;
        final long thisMidnightEpochSeconds = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond() - 60;
        final long maxTimeRange = weatherData.getForecastHours().get(weatherData.getForecastHours().size() - 1).getHour().getEpochSecond();

        final GraphConfig graphConfig = GraphConfig.builder()
            .rangeLowerBound(rangeLowerBound)
            .rangeUpperBound(rangeUpperBound)
            .domainLowerBound(thisMidnightEpochSeconds)
            .domainUpperBound(maxTimeRange)
            .domainStep(86400)
            .rangeStep(1)
            .build();

        return GraphCardViewModel.builder()
            .title("Windspeed")
            .rangeLines(Collections.emptyList())
            .graphConfig(graphConfig)
            .dataSeries(Collections.singletonList(getWindspeedSeries(weatherData)))
            .rangeLines(Collections.emptyList())
            .XFormatter(this::localizeDateTime)
            .build();
    }

    private DataSeries getWindspeedSeries(WeatherData weatherData)
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
        return DataValue.create(forecastHour.getHour().getEpochSecond(), forecastHour.getWindspeed().getMetersPerSecond());
    }

    private String localizeDateTime(double value)
    {
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond((long) value), ZoneId.systemDefault());
        final DateTimeFormatter localFormatter = DateTimeFormatter.ofPattern("MM/dd");

        return localDateTime.format(localFormatter);
    }
}
