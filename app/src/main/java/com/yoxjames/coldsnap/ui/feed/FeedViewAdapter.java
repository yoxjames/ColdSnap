package com.yoxjames.coldsnap.ui.feed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.core.view.BaseColdsnapView;
import com.yoxjames.coldsnap.ui.cards.temperature.TemperatureCardView;

public class FeedViewAdapter extends RecyclerView.Adapter<FeedCardViewHolder> implements BaseColdsnapView<FeedViewModel>
{
    private FeedViewModel viewModel = FeedViewModel.EMPTY;

    @NonNull
    @Override
    public FeedCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new FeedCardViewHolder((TemperatureCardView) LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.view_temperature_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FeedCardViewHolder feedCardViewHolder, int i)
    {
        feedCardViewHolder.bindView(viewModel.getCardViewModels().get(i));
    }

    @Override
    public int getItemCount()
    {
        return viewModel.getCardViewModels().size();
    }

    @Override
    public void bindView(FeedViewModel viewModel)
    {
        this.viewModel = viewModel;
        notifyDataSetChanged();
    }
}
