package com.ajapplications.budgeteerbuddy.view.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajapplications.budgeteerbuddy.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class LineChartFragment extends ChartFragment {
    public LineChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_line_chart, container, false);

        ArrayList entries = getEntries();
        ArrayList labels = getEntryLabels();

        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.graph_label_yearly_expenses));
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        LineData data = new LineData(labels, dataSet);
        LineChart chart = (LineChart) view.findViewById(R.id.chart_line);
        chart.setData(data);

        chart.setDescription("");

        return view;
    }

    private ArrayList<BarEntry> getEntries(){
        //TODO pull from db

        ArrayList entries = new ArrayList();

        entries.add(new Entry(4f, 0));
        entries.add(new Entry(5f,1));
        entries.add(new Entry(6f,2));
        entries.add(new Entry(2f,3));
        entries.add(new Entry(18f,4));
        entries.add(new Entry(9f,5));
        entries.add(new Entry(4f, 6));
        entries.add(new Entry(5f,7));
        entries.add(new Entry(6f,8));
        entries.add(new Entry(2f,9));
        entries.add(new Entry(18f,10));
        entries.add(new Entry(9f, 11));

        return entries;
    }

    private ArrayList<String> getEntryLabels(){
        ArrayList labels = new ArrayList();

        labels.add(getResources().getString(R.string.graph_label_january));
        labels.add(getResources().getString(R.string.graph_label_february));
        labels.add(getResources().getString(R.string.graph_label_march));
        labels.add(getResources().getString(R.string.graph_label_april));
        labels.add(getResources().getString(R.string.graph_label_may));
        labels.add(getResources().getString(R.string.graph_label_june));
        labels.add(getResources().getString(R.string.graph_label_july));
        labels.add(getResources().getString(R.string.graph_label_august));
        labels.add(getResources().getString(R.string.graph_label_september));
        labels.add(getResources().getString(R.string.graph_label_october));
        labels.add(getResources().getString(R.string.graph_label_november));
        labels.add(getResources().getString(R.string.graph_label_december));
        return labels;
    }
}
