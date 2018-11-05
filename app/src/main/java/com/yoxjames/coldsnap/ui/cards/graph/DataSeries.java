package com.yoxjames.coldsnap.ui.cards.graph;

import android.graphics.Color;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;


@AutoValue
public abstract class DataSeries
{
    public static DataSeries EMPTY = DataSeries.builder()
        .data(Collections.emptyList())
        .lineColor(Color.TRANSPARENT)
        .build();

    public abstract List<DataValue> getData();
    public abstract int getLineColor();

    public static Builder builder()
    {
        return new AutoValue_DataSeries.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder
    {
        public abstract Builder data(List<DataValue> data);
        public abstract Builder lineColor(int lineColor);

        public abstract DataSeries build();
    }
}
