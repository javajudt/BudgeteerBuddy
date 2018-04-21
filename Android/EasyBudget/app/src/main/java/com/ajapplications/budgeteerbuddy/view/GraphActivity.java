package com.ajapplications.budgeteerbuddy.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ajapplications.budgeteerbuddy.R;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
