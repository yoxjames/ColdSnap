package com.yoxjames.coldsnap.ui.feed;

import com.yoxjames.coldsnap.core.ActivityView;

public interface FeedMvpView extends ActivityView
{
    void bindView(FeedViewModel viewModel);
}
