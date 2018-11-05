package com.yoxjames.coldsnap.ui.cards.graph;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GraphConfig
{
    public abstract double getRangeLowerBound();
    public abstract double getRangeUpperBound();
    public abstract double getDomainLowerBound();
    public abstract double getDomainUpperBound();
    public abstract double getRangeStep();
    public abstract double getDomainStep();

    public static Builder builder()
    {
        return new AutoValue_GraphConfig.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder
    {
        public abstract Builder rangeLowerBound(double rangeLowerBound);
        public abstract Builder rangeUpperBound(double rangeUpperBound);
        public abstract Builder domainLowerBound(double domainLowerBound);
        public abstract Builder domainUpperBound(double domainUpperBound);
        public abstract Builder rangeStep(double rangeStep);
        public abstract Builder domainStep(double domainStep);

        public abstract GraphConfig build();
    }
}
