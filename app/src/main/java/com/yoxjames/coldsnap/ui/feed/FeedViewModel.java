package com.yoxjames.coldsnap.ui.feed;

import com.google.auto.value.AutoValue;
import com.yoxjames.coldsnap.ui.cards.graph.GraphCardViewModel;

import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class FeedViewModel
{
    public static final FeedViewModel EMPTY = FeedViewModel.create(Collections.emptyList());

    public abstract List<GraphCardViewModel> getCardViewModels();

    public static FeedViewModel create(List<GraphCardViewModel> cardViewModels) {
        return new AutoValue_FeedViewModel(cardViewModels);
    }
}
