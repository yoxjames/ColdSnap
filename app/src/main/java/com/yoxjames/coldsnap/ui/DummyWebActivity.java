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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.yoxjames.coldsnap.R;

/**
 * Created by yoxjames on 10/1/17.
 */

public class DummyWebActivity extends AppCompatActivity
{
    private static final String HTML_CONTENT = "com.yoxjames.coldsnap.html_content";
    private WebView view;


    public static Intent newDummyWebIntent(Context context, Uri htmlContent)
    {
        final Intent intent = new Intent(context, DummyWebActivity.class);
        intent.putExtra(HTML_CONTENT, htmlContent);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_web_activity);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view = (WebView) findViewById(R.id.webview);

        Uri webContent = getIntent().getParcelableExtra(HTML_CONTENT);

        view.loadUrl(webContent.toString());
        view.setHorizontalScrollBarEnabled(false);
    }
}
