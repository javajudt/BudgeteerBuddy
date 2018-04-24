package com.ajapplications.budgeteerbuddy.view.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajapplications.budgeteerbuddy.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


/**
 * Fragment that displays a bar chart for weekly expense analysis.
 */
public class BarChartFragment extends ChartFragment {
    public BarChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        ArrayList entries = getEntries();
        ArrayList labels = getEntryLabels();

        BarDataSet dataSet = new BarDataSet(entries, getResources().getString(R.string.graph_label_weekly_expenses));
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(labels, dataSet);
        BarChart chart = (BarChart) view.findViewById(R.id.chart_bar);
        chart.setData(data);

        chart.animateY(1500);
        chart.setDescription("");

        return view;
    }
    
    private ArrayList<BarEntry> getEntries(){
        //TODO pull from db

        ArrayList entries = new ArrayList();

        entries.add(new BarEntry(2f,0));
        entries.add(new BarEntry(4f,1));
        entries.add(new BarEntry(6f,2));
        entries.add(new BarEntry(8f,3));
        entries.add(new BarEntry(7f,4));
        entries.add(new BarEntry(3f,5));
        entries.add(new BarEntry(8f,6));

        return entries;
    }
    
    private ArrayList<String> getEntryLabels(){
        ArrayList labels = new ArrayList();
        
        labels.add(getResources().getString(R.string.graph_label_sunday));
        labels.add(getResources().getString(R.string.graph_label_monday));
        labels.add(getResources().getString(R.string.graph_label_tuesday));
        labels.add(getResources().getString(R.string.graph_label_wednesday));
        labels.add(getResources().getString(R.string.graph_label_thursday));
        labels.add(getResources().getString(R.string.graph_label_friday));
        labels.add(getResources().getString(R.string.graph_label_saturday));

        return labels;
    }
}
