package com.yoxjames.coldsnap.ui.detail;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PlantDetailViewUpdate
{
    public abstract String getName();
    public abstract String getScientificName();
    public abstract int getTempVal();

    public static Builder builder()
    {
        return new AutoValue_PlantDetailViewUpdate.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder
    {
        abstract Builder name(String name);
        abstract Builder scientificName(String scientificName);
        abstract Builder tempVal(int tempVal);

        abstract PlantDetailViewUpdate build();
    }
}
