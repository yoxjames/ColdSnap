package com.yoxjames.coldsnap.ui.feed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.yoxjames.coldsnap.core.view.BaseColdsnapView;
import com.yoxjames.coldsnap.ui.cards.graph.GraphCardViewModel;
import com.yoxjames.coldsnap.ui.cards.temperature.TemperatureCardView;

public class FeedCardViewHolder extends RecyclerView.ViewHolder implements BaseColdsnapView<GraphCardViewModel>
{
    private final TemperatureCardView temperatureCardView;

    public FeedCardViewHolder(@NonNull TemperatureCardView temperatureCardView)
    {
        super(temperatureCardView);
        this.temperatureCardView = temperatureCardView;
    }

    @Override
    public void bindView(GraphCardViewModel viewModel)
    {
        temperatureCardView.bindView(viewModel);
    }
}
