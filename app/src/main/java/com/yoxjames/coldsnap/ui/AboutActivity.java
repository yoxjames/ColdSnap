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

/**
 * Created by yoxjames on 9/30/17.
 */

public class AboutActivity extends AppCompatActivity
{
    private RelativeLayout coldSnap;
    private RelativeLayout licence;
    private RelativeLayout viewSource;
    private RelativeLayout help;
    private RelativeLayout reportBug;
    private RelativeLayout suggestions;
    private RelativeLayout changelog;

    private RelativeLayout wunderground;
    private RelativeLayout icons8;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coldSnap = (RelativeLayout) findViewById(R.id.cold_snap_item);
        licence = (RelativeLayout) findViewById(R.id.licence_item);
        viewSource = (RelativeLayout) findViewById(R.id.view_source_item);
        help = (RelativeLayout) findViewById(R.id.help_item);
        reportBug = (RelativeLayout) findViewById(R.id.report_bug_item);
        suggestions = (RelativeLayout) findViewById(R.id.suggestions_item);
        changelog = (RelativeLayout) findViewById(R.id.changelog_item);

        wunderground = (RelativeLayout) findViewById(R.id.wunderground_item);
        icons8 = (RelativeLayout) findViewById(R.id.icons8_item);

        // ColdSnap

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

        // Report Bug
        // TODO: Something for this

        // Suggestions
        // TODO: Something for this

        // Changelog
        // TODO: Create this

        // Wunderground
        wunderground.setOnClickListener(view ->
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wunderground.com"));
            startActivity(browserIntent);
        });

        // Icons8
        icons8.setOnClickListener(view ->
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.icons8.com"));
            startActivity(browserIntent);
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}
