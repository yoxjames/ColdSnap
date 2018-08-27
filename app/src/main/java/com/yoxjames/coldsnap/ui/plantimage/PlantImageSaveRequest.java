package com.yoxjames.coldsnap.ui.plantimage;

import com.google.auto.value.AutoValue;

import org.threeten.bp.Instant;

import java.util.UUID;

@AutoValue
public abstract class PlantImageSaveRequest
{
    public abstract String getFileName();
    public abstract UUID getPlantUUID();
    public abstract Instant getPhotoTime();
    public abstract boolean isProfilePicture();

    public static Builder builder()
    {
        return new AutoValue_PlantImageSaveRequest.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder
    {
        public abstract Builder setFileName(String fileName);
        public abstract Builder setPlantUUID(UUID plantUUID);
        public abstract Builder setPhotoTime(Instant photoTime);
        public abstract Builder setProfilePicture(boolean profilePicture);

        public abstract PlantImageSaveRequest build();
    }
}
