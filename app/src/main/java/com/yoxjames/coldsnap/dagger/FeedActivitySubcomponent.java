package com.yoxjames.coldsnap.dagger;

import com.yoxjames.coldsnap.ui.feed.ActivityFeed;

import dagger.Subcomponent;

@Subcomponent(modules = { FeedModule.class })
@ActivityScope
public interface FeedActivitySubcomponent
{
    void inject(ActivityFeed activityFeed);
}
