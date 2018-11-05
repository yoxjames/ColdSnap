package com.yoxjames.coldsnap.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jakewharton.rxbinding2.support.design.widget.RxBottomNavigationView;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.ui.feed.ActivityFeed;
import com.yoxjames.coldsnap.ui.plantlist.PlantListActivity;
import com.yoxjames.coldsnap.ui.prefs.SettingsActivity;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;

public abstract class BaseColdsnapActivity<P extends ActivityPresenter> extends AppCompatActivity implements ActivityView
{
    @BindView(R.id.bnv_navigation) protected BottomNavigationView bnvNavigation;

    @Inject protected P presenter;

    @Nullable private Toolbar toolbar;

    protected abstract void inject();
    @Nullable protected abstract Toolbar getToolbar();
    @IdRes protected abstract int getNavigationId();

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

    @Override
    public Observable<Integer> onBottomNavigation()
    {
        return RxBottomNavigationView.itemSelections(bnvNavigation)
            .map(MenuItem::getItemId)
            .filter(i -> i != this.getNavigationId());
    }

    @Override
    public void openSettings()
    {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void openPlants()
    {
        startActivity(new Intent(this, PlantListActivity.class));
    }

    @Override
    public void openFeed()
    {
        startActivity(new Intent(this, ActivityFeed.class));
    }
}
