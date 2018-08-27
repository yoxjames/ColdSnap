package com.yoxjames.coldsnap.ui.plantimage;

import com.google.auto.value.AutoValue;

import java.util.UUID;

import static com.yoxjames.coldsnap.util.CSUtils.EMPTY_UUID;

@AutoValue
public abstract class PlantProfileImageViewModel
{
    public static PlantProfileImageViewModel EMPTY = builder()
        .setImageURL("")
        .setTakeImageAvailable(true)
        .setPlantUUID(EMPTY_UUID)
        .build();

    public abstract String getImageURL();
    public abstract boolean isTakeImageAvailable();
    public abstract UUID getPlantUUID();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_PlantProfileImageViewModel.Builder();
    }

    public PlantProfileImageViewModel withImage(String fileName)
    {
        return toBuilder().setImageURL(fileName).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setImageURL(String url);
        public abstract Builder setTakeImageAvailable(boolean available);
        public abstract Builder setPlantUUID(UUID uuid);

        public abstract PlantProfileImageViewModel build();
    }

}
