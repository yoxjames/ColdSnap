package com.yoxjames.coldsnap.ui;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import io.reactivex.disposables.CompositeDisposable;

public abstract class AbstractBaseColdsnapPresenter<V extends MvpView> implements BaseColdsnapPresenter
{
    protected CompositeDisposable disposables = new CompositeDisposable();
    protected CompositeDisposable menuDisposables = new CompositeDisposable();
    protected final V view;

    protected AbstractBaseColdsnapPresenter(V view)
    {
        this.view = view;
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void load()
    {
        disposables.dispose();
        disposables = new CompositeDisposable();
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void unload()
    {
        disposables.dispose();
        menuDisposables.dispose();
    }
}
