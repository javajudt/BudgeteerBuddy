/*
   Copyright (c) 2018 Jordan Judt and Alexis Layne.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ajapplications.budgeteerbuddy.view.analysis;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Date;
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

        ArrayList entries = new ArrayList<BarEntry>();
        ArrayList labels = new ArrayList<String>();

        if (!tryGetEntriesAndLabels(entries, labels)) {
            // Display "no expenses" message if there are no expenses for the week
            view.findViewById(R.id.no_expense_bar_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.chart_bar).setVisibility(View.GONE);
        } else {

            BarDataSet dataSet = new BarDataSet(entries, "");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            BarData data = new BarData(labels, dataSet);
            BarChart chart = (BarChart) view.findViewById(R.id.chart_bar);
            chart.setData(data);

            chart.animateY(1500);
            chart.setDescription("");
        }

        return view;
    }

    private boolean tryGetEntriesAndLabels(@NonNull ArrayList<BarEntry> entries, @NonNull ArrayList<String> labels) {
        if (!getDB().hasExpensesForLastSevenDays(new Date()))
            return false;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -6);

        // Get expenses for each day in the last week.
        for (int i = 0; i < 7; i++) {
            List<Expense> expenses = getDB().getExpensesForDay(cal.getTime());

            double totalForDay = 0;
            for (Expense expense : expenses) {
                totalForDay += expense.getAmount();
            }
            entries.add(new BarEntry((float) totalForDay, i));

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

            cal.add(Calendar.DATE, 1);
        }

        return true;
    }
}
