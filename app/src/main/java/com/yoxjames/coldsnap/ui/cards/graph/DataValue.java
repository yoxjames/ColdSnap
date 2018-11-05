package com.yoxjames.coldsnap.ui.cards.graph;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DataValue
{
    public abstract double getXValue();
    public abstract double getYValue();

    public static DataValue create(double xValue, double yValue)
    {
        return new AutoValue_DataValue(xValue, yValue);
    }
}
