package com.ajapplications.budgeteerbuddy.view.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajapplications.budgeteerbuddy.R;
import com.ajapplications.budgeteerbuddy.model.Expense;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


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

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(labels, dataSet);
        BarChart chart = (BarChart) view.findViewById(R.id.chart_bar);
        chart.setData(data);

        chart.animateY(1500);
        chart.setDescription("");

        return view;
    }

    private ArrayList<BarEntry> getEntries() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -6);
        ArrayList entries = new ArrayList();

        // Get expenses for each day in the last week.
        for (int i = 0; i < 7; i++) {
            List<Expense> expenses = getDB().getExpensesForDay(cal.getTime());

            double totalForDay = 0;
            for (Expense expense : expenses) {
                totalForDay += expense.getAmount();
            }
            entries.add(new BarEntry((float) totalForDay, i));

            cal.add(Calendar.DATE, 1);
        }

        return entries;
    }

    private ArrayList<String> getEntryLabels() {
        ArrayList<String> labels = new ArrayList();

        for (int i = 6; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(cal.getTime());

            switch (day) {
                case "Sunday":
                    labels.add(getResources().getString(R.string.graph_label_sunday));
                    break;
                case "Monday":
                    labels.add(getResources().getString(R.string.graph_label_monday));
                    break;
                case "Tuesday":
                    labels.add(getResources().getString(R.string.graph_label_tuesday));
                    break;
                case "Wednesday":
                    labels.add(getResources().getString(R.string.graph_label_wednesday));
                    break;
                case "Thursday":
                    labels.add(getResources().getString(R.string.graph_label_thursday));
                    break;
                case "Friday":
                    labels.add(getResources().getString(R.string.graph_label_friday));
                    break;
                case "Saturday":
                    labels.add(getResources().getString(R.string.graph_label_saturday));
                    break;
            }
        }

        return labels;
    }
}
