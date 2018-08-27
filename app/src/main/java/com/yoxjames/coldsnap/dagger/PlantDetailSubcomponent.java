package com.yoxjames.coldsnap.dagger;

import com.yoxjames.coldsnap.ui.detail.PlantDetailActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = { PlantDetailModule.class })
public interface PlantDetailSubcomponent
{
    void inject(PlantDetailActivity activity);
}
