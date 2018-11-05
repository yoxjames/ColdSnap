package com.yoxjames.coldsnap.ui.cards.graph;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GraphLine
{
    public abstract double getRangeValue();
    public abstract int getLineColor();

    public static GraphLine create(double rangeValue, int lineColor)
    {
        return new AutoValue_GraphLine(rangeValue, lineColor);
    }
}
