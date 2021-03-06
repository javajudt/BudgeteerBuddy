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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class LineChartFragment extends ChartFragment {
    public LineChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_line_chart, container, false);

        ArrayList entries = new ArrayList<BarEntry>();
        if (!tryGetEntries(entries)) {
            // Display "no expenses" message if there are no expenses for the year
            view.findViewById(R.id.no_expense_line_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.chart_line).setVisibility(View.GONE);
        } else {
            ArrayList labels = getEntryLabels();

            LineDataSet dataSet = new LineDataSet(entries, "");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            LineData data = new LineData(labels, dataSet);
            LineChart chart = (LineChart) view.findViewById(R.id.chart_line);
            chart.setData(data);

            chart.setDescription("");
        }

        return view;
    }

    private boolean tryGetEntries(@NonNull ArrayList<BarEntry> entries) {
        if (!getDB().hasExpensesForYear(new Date()))
            return false;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        for (int i = 0; i < 12; i++) {
            List<Expense> expenses = getDB().getExpensesForMonth(cal.getTime());

            double totalForMonth = 0;
            for (Expense expense : expenses) {
                totalForMonth += expense.getAmount();
            }
            entries.add(new BarEntry((float) totalForMonth, i));

            cal.add(Calendar.MONTH, 1);
        }

        return true;
    }

    private ArrayList<String> getEntryLabels() {
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
