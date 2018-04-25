/*
 *   Copyright 2015 Benoit LETONDOR
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.ajapplications.budgeteerbuddy.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.ajapplications.budgeteerbuddy.R;
import com.ajapplications.budgeteerbuddy.model.AnalysisChartDateRange;
import com.ajapplications.budgeteerbuddy.model.db.DB;
import com.ajapplications.budgeteerbuddy.view.analysis.BarChartFragment;
import com.ajapplications.budgeteerbuddy.view.analysis.LineChartFragment;
import com.ajapplications.budgeteerbuddy.view.analysis.PieChartFragment;

import me.relex.circleindicator.CircleIndicator;

/**
 * Welcome screen activity
 *
 * @author Benoit LETONDOR
 */
public class ExpenseAnalysisActivity extends DBActivity {
    /**
     * Intent broadcasted by pager fragments to go next
     */
    public final static String PAGER_NEXT_INTENT = "analysis.pager.next";
    /**
     * Intent broadcasted by pager fragments to go previous
     */
    public final static String PAGER_PREVIOUS_INTENT = "analysis.pager.previous";

// ------------------------------------------>

    /**
     * The view pager
     */
    private ViewPager pager;
    /**
     * Broadcast receiver for intent sent by fragments
     */
    private BroadcastReceiver receiver;

// ------------------------------------------>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_analysis);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pager = (ViewPager) findViewById(R.id.expense_analysis_view_pager);
        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new BarChartFragment();
                    case 1:
                        return new PieChartFragment();
                    case 2:
                        return new LineChartFragment();
                }

                return null;
            }

            @Override
            public int getCount() {
                return AnalysisChartDateRange.values().length;
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setOffscreenPageLimit(pager.getAdapter().getCount()); // preload all fragments for transitions smoothness

        // Circle indicator
        ((CircleIndicator) findViewById(R.id.expense_analysis_view_pager_indicator)).setViewPager(pager);

        IntentFilter filter = new IntentFilter();
        filter.addAction(PAGER_NEXT_INTENT);
        filter.addAction(PAGER_PREVIOUS_INTENT);
//        filter.addAction(PAGER_DONE_INTENT);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (PAGER_NEXT_INTENT.equals(intent.getAction()) && pager.getCurrentItem() < pager.getAdapter().getCount() - 1) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                } else if (PAGER_PREVIOUS_INTENT.equals(intent.getAction()) && pager.getCurrentItem() > 0) {
                    pager.setCurrentItem(pager.getCurrentItem() - 1, true);
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_analysis, menu);
//        return true;
//    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if (id == R.id.action_analysis_weekly){
//            //TODO weekly analysis
//        }
//        else if (id == R.id.action_analysis_monthly){
//            //TODO monthly analysis
//        }
//        else if (id == R.id.action_analysis_yearly){
//            //TODO yearly analysis
//        }
        /*else */if( id == android.R.id.home ) // Back button of the actionbar
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        super.onDestroy();
    }

//    @Override
//    public void onBackPressed() {
//        if (pager.getCurrentItem() > 0) {
//            pager.setCurrentItem(pager.getCurrentItem() - 1, true);
//            return;
//        }
//
//        setResult(Activity.RESULT_CANCELED);
//        finish();
//    }

    /**
     * Method that a child (fragment) can call to get the DB connection
     *
     * @return the db connection
     */
    @NonNull
    public DB getDB()
    {
        return db;
    }
}
