package com.ajapplications.budgeteerbuddy.view.analysis;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajapplications.budgeteerbuddy.R;
import com.ajapplications.budgeteerbuddy.model.Category;
import com.ajapplications.budgeteerbuddy.model.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartFragment extends ChartFragment {
    public PieChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        ArrayList entries = new ArrayList<BarEntry>();
        ArrayList labels = new ArrayList<String>();

        if (!tryGetEntriesAndLabels(entries, labels)) {
            // Display "no expenses" message if there are no expenses for the day
            view.findViewById(R.id.no_expense_pie_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.chart_pie).setVisibility(View.GONE);
        } else {
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            PieData data = new PieData(labels, dataSet);
            PieChart chart = (PieChart) view.findViewById(R.id.chart_pie);
            chart.setData(data);
            chart.setDescription("");
        }

        return view;
    }

    private boolean tryGetEntriesAndLabels(@NonNull ArrayList<BarEntry> entries, @NonNull ArrayList<String> labels) {
        if (!getDB().hasExpensesForDay(new Date()))
            return false;

        // Initialize Map for each category
        Map<Category, Double> expensesByCategory = new HashMap();
        for (Category category : Category.values()) {
            expensesByCategory.put(category, new Double(0));
        }

        // Add all expenses for day by category
        List<Expense> expenses = getDB().getExpensesForDay(new Date());
        for (Expense expense : expenses) {
            double totalForCategory = expensesByCategory.get(expense.getCategory());
            totalForCategory += expense.getAmount();
            expensesByCategory.put(expense.getCategory(), totalForCategory);
        }

        // Add the total expense for each category to the pie chart
        int i = 0;
        boolean entriesExist = false;
        for (Category category : Category.values()) {
            float entry = expensesByCategory.get(category).floatValue();
            if (entry > 0) { // Do not show income because it does not make sense in this context
                entriesExist = true;
                entries.add(new BarEntry(expensesByCategory.get(category).floatValue(), i++));
                labels.add(category.toString(getContext()));
            }
        }

        return entriesExist;
    }
}
