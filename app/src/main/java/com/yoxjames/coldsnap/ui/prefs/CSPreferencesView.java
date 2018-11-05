package com.yoxjames.coldsnap.ui.prefs;

import com.yoxjames.coldsnap.core.MvpView;

import io.reactivex.Observable;

public interface CSPreferencesView extends MvpView
{
    void bindView(PreferencesViewModel viewModel);
    Observable<Integer> thresholdChanges();
    Observable<Integer> fuzzChanges();
}
