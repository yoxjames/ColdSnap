package com.yoxjames.coldsnap.core;

import io.reactivex.Observable;

public interface ActivityView extends MvpView
{
    Observable<Integer> onBottomNavigation();

    void openSettings();
    void openPlants();
    void openFeed();
}
