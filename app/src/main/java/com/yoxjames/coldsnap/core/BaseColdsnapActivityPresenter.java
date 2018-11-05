package com.yoxjames.coldsnap.core;

import android.support.annotation.IdRes;

import com.yoxjames.coldsnap.R;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseColdsnapActivityPresenter<V extends ActivityView> implements ActivityPresenter
{
    protected CompositeDisposable disposables = new CompositeDisposable();
    protected CompositeDisposable menuDisposables = new CompositeDisposable();
    protected final V view;

    protected BaseColdsnapActivityPresenter(V view)
    {
        this.view = view;
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void load()
    {
        disposables.dispose();
        disposables = new CompositeDisposable();

        disposables.add(view.onBottomNavigation().subscribe(this::onNavigate));
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void unload()
    {
        disposables.dispose();
        menuDisposables.dispose();
    }

    private void onNavigate(@IdRes int menuId)
    {
        switch (menuId)
        {
            case R.id.menu_feed:
                view.openFeed();
                break;
            case R.id.menu_settings:
                view.openSettings();
                break;
            case R.id.menu_plants:
                view.openPlants();
                break;
        }
    }
}
