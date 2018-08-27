package com.yoxjames.coldsnap.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import javax.annotation.Nullable;
import javax.inject.Inject;

public abstract class BaseColdsnapActivity<P extends BaseColdsnapPresenter> extends AppCompatActivity implements MvpView
{
    @Inject protected P presenter;

    @Nullable private Toolbar toolbar;

    protected abstract void inject();
    @Nullable protected abstract Toolbar getToolbar();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        inject();
        toolbar = getToolbar();
        if (toolbar != null)
            setSupportActionBar(toolbar);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        presenter.unload();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        presenter.load();
    }
}
