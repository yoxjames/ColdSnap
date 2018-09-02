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

package com.yoxjames.coldsnap.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.yoxjames.coldsnap.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yoxjames on 9/30/17.
 */

public class AboutActivity extends AppCompatActivity
{
    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.cold_snap_item) protected RelativeLayout coldSnap;
    @BindView(R.id.licence_item) protected RelativeLayout licence;
    @BindView(R.id.view_source_item) protected RelativeLayout viewSource;
    @BindView(R.id.help_item) protected RelativeLayout help;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // Licence
        licence.setOnClickListener(view ->
        {
            Uri url = Uri.parse("file:///android_asset/licence.html");
            Intent webIntent = DummyWebActivity.newDummyWebIntent(AboutActivity.this, url);
            startActivity(webIntent);
        });

        // View Source
        viewSource.setOnClickListener(view ->
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yoxjames/ColdSnap?files=1"));
            startActivity(browserIntent);
        });

        // Help
        help.setOnClickListener(view ->
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yoxjames/ColdSnap/wiki"));
            startActivity(browserIntent);
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}
