package com.yoxjames.coldsnap.ui.cards.temperature;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.core.view.BaseColdsnapView;
import com.yoxjames.coldsnap.ui.cards.FeedCardView;
import com.yoxjames.coldsnap.ui.cards.graph.DataSeries;
import com.yoxjames.coldsnap.ui.cards.graph.DataValue;
import com.yoxjames.coldsnap.ui.cards.graph.GraphCardViewModel;
import com.yoxjames.coldsnap.ui.cards.graph.GraphConfig;
import com.yoxjames.coldsnap.ui.cards.graph.GraphLine;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TemperatureCardView extends CardView implements BaseColdsnapView<GraphCardViewModel>, FeedCardView
{
    @BindView(R.id.xyp_temperature) XYPlot xypTemperature;

    public TemperatureCardView(@NonNull Context context)
    {
        super(context);
    }

    public TemperatureCardView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TemperatureCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        PixelUtils.init(getContext());
    }

    @Override
    public void bindView(final GraphCardViewModel vm)
    {
        xypTemperature.clear();
        addDataSeries(vm);
        addRangeLines(vm);
        addFormatters(vm);
        axisConfig(vm);
    }

    private void axisConfig(GraphCardViewModel vm)
    {
        final GraphConfig gc = vm.getGraphConfig();
        xypTemperature.setRangeLowerBoundary(gc.getRangeLowerBound(), BoundaryMode.FIXED);
        xypTemperature.setRangeUpperBoundary(gc.getRangeUpperBound(), BoundaryMode.FIXED);
        xypTemperature.setDomainLowerBoundary(gc.getDomainLowerBound(), BoundaryMode.FIXED);
        xypTemperature.setRangeStep(StepMode.INCREMENT_BY_VAL, gc.getRangeStep());
        xypTemperature.setDomainStep(StepMode.INCREMENT_BY_VAL, gc.getDomainStep());
        xypTemperature.setTitle(vm.getTitle());
    }

    private void addFormatters(GraphCardViewModel vm)
    {
        xypTemperature.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(createFormatter(vm.getXFormatter()));
        xypTemperature.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(createFormatter(vm.getYFormatter()));
    }

    private Format createFormatter(@Nullable Function<Double, String> inputFunction)
    {
        return new Format()
        {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
            {
                final double i = Math.round(((Number) obj).intValue());
                if (inputFunction != null)
                    return toAppendTo.append(inputFunction.apply(i));
                else
                    return toAppendTo.append(i);
            }

            @Override @Nullable
            public Object parseObject(String source, ParsePosition pos)
            {
                return null;
            }
        };
    }

    private void addRangeLines(GraphCardViewModel vm)
    {
        vm.getRangeLines().forEach(graphLine -> drawLine(vm, graphLine));
    }

    private void addDataSeries(GraphCardViewModel vm)
    {
        vm.getDataSeries().forEach(dataSeries -> xypTemperature.addSeries(
            mapDataSeriesToGraphSeriesImpl(dataSeries),
            mapDataSeriesToLineAndPointFormatter(dataSeries)));
    }

    private SimpleXYSeries mapDataSeriesToGraphSeriesImpl(DataSeries dataSeries)
    {
        return new SimpleXYSeries(
            dataSeries.getData().stream()
                .map(DataValue::getXValue)
                .collect(Collectors.toList()),
            dataSeries.getData().stream()
                .map(DataValue::getYValue)
                .collect(Collectors.toList()),
            "Title");
    }

    private LineAndPointFormatter mapDataSeriesToLineAndPointFormatter(DataSeries dataSeries)
    {
        final LineAndPointFormatter lineAndPointFormatter = new LineAndPointFormatter(dataSeries.getLineColor(), null, null, null);
        lineAndPointFormatter.setInterpolationParams(
            new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        return lineAndPointFormatter;
    }

    protected void drawLine(GraphCardViewModel vm, GraphLine graphLine)
    {
        final GraphConfig gc = vm.getGraphConfig();
        final double mostFutureTime = gc.getDomainUpperBound();

        final List<Double> xVals = new ArrayList<>();
        final List<Double> yVals = new ArrayList<>();

        xVals.add(gc.getDomainLowerBound());
        xVals.add(mostFutureTime);

        yVals.add(graphLine.getRangeValue());
        yVals.add(graphLine.getRangeValue());

        xypTemperature.addSeries(
            new SimpleXYSeries(xVals, yVals, getContext().getString(R.string.cold_threshold)),
            new LineAndPointFormatter(graphLine.getLineColor(), null, null, null));
    }
}
