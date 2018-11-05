package com.yoxjames.coldsnap.ui.feed;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.core.BaseColdsnapActivity;
import com.yoxjames.coldsnap.dagger.FeedModule;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityFeed extends BaseColdsnapActivity<FeedPresenterImpl> implements FeedMvpView
{

    @BindView(R.id.rv_feed) FeedView feedView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void inject()
    {
        ((ColdSnapApplication) getApplicationContext())
            .getInjector()
            .feedActivitySubcomponent(new FeedModule(this))
            .inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);

        bnvNavigation.setSelectedItemId(getNavigationId());
    }

    @Override
    protected Toolbar getToolbar()
    {
        return toolbar;
    }

    @Override
    protected int getNavigationId()
    {
        return R.id.menu_feed;
    }

    @Override
    public void bindView(FeedViewModel viewModel)
    {
        feedView.bindView(viewModel);
    }
}
