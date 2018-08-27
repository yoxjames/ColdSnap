/*
 * Copyright (c) 2017 James Yox
 *
 * This file is part of ColdSnap.
 *
 * ColdSnap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ColdSnap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ColdSnap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yoxjames.coldsnap.ui.weatherpreviewbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.ui.BaseColdsnapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

/**
 * Created by yoxjames on 11/5/17.
 */

public class WeatherPreviewBarView extends RelativeLayout implements BaseColdsnapView<WeatherBarViewModel>
{
    @BindView(R.id.location_text) protected TextView locationText;
    @BindView(R.id.high_field) protected TextView highField;
    @BindView(R.id.low_field) protected TextView lowField;
    @BindView(R.id.last_updated_field) protected TextView lastUpdatedField;
    @BindView(R.id.preview_bar_content) protected LinearLayout content;
    @BindView(R.id.preview_bar_progress) protected ProgressBar progress;
    @BindView(R.id.failed_icon) protected ImageView failedImage;

    public WeatherPreviewBarView(Context context)
    {
        super(context);
    }

    public WeatherPreviewBarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WeatherPreviewBarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public WeatherPreviewBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(WeatherBarViewModel viewModel)
    {
        content.setVisibility((viewModel.isPending()) ?
                View.INVISIBLE :
                (viewModel.errorMessage().equals("")) ?
                        View.VISIBLE :
                        View.INVISIBLE);

        progress.setVisibility((viewModel.isPending()) ? View.VISIBLE : View.INVISIBLE);
        failedImage.setVisibility((viewModel.errorMessage().equals("")) ? View.INVISIBLE : View.VISIBLE);
        locationText.setText(viewModel.errorMessage().equals("") ? viewModel.getLocationText() : getContext().getString(R.string.connection_failure));
        highField.setText(viewModel.getHighTemperatureText());
        lowField.setText(viewModel.getLowTemperatureText());
        lastUpdatedField.setText(viewModel.getLastUpdatedTime());
    }

    public Observable<Object> retryClickEvent()
    {
        return RxView.clicks(failedImage);
    }
}
