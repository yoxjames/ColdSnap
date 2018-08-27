package com.yoxjames.coldsnap.prefs;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface CSRxPreferences
{
    Observable<PreferenceModel> getPreferences();
    Completable savePreferences(PreferenceModel preferenceModel);
}
