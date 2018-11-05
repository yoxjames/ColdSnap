package com.yoxjames.coldsnap.ui.feed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.yoxjames.coldsnap.core.view.BaseColdsnapView;

public class FeedView extends RecyclerView implements BaseColdsnapView<FeedViewModel>
{
    private final FeedViewAdapter adapter = new FeedViewAdapter();

    public FeedView(@NonNull Context context)
    {
        super(context);
    }

    public FeedView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FeedView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void onFinishInflate()
    {
        super.onFinishInflate();
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void bindView(FeedViewModel viewModel)
    {
        adapter.bindView(viewModel);
    }
}
