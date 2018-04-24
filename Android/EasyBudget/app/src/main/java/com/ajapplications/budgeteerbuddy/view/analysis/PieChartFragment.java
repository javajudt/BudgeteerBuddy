package com.ajapplications.budgeteerbuddy.view.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajapplications.budgeteerbuddy.R;
import com.ajapplications.budgeteerbuddy.model.Category;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PieChartFragment extends ChartFragment {
    public PieChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        ArrayList entries = getEntries();
        ArrayList labels = getEntryLabels();

        PieDataSet dataSet = new PieDataSet(entries,"");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(labels, dataSet);
        PieChart chart = (PieChart) view.findViewById(R.id.chart_pie);
        chart.setData(data);

        chart.setDescription(getResources().getString(R.string.graph_label_monthly_expenses));

        return view;
    }

    private ArrayList<BarEntry> getEntries() {
        //TODO pull from db

        ArrayList entries = new ArrayList();

        entries.add(new BarEntry(2f,0));
        entries.add (new BarEntry(4f,1));
        entries.add(new BarEntry(6f,2));
        entries.add(new BarEntry(8f,3));
        entries.add(new BarEntry(7f,4));
        entries.add(new BarEntry(3f,5));
        entries.add(new BarEntry(2f,6));
        entries.add(new BarEntry(3f,7));

        return entries;
    }

    private ArrayList<String> getEntryLabels() {
        ArrayList labels = new ArrayList();

        for (Category category : Category.values()) {
            labels.add(category.toString(getContext()));
        }

        return labels;
    }
}
