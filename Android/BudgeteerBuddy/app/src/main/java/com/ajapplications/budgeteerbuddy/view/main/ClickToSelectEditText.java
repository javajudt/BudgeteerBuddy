/*
   Copyright (c) 2018 Jordan Judt and Alexis Layne.

   Original project "EasyBudget" Copyright (c) Benoit LETONDOR

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   "ClickToSelectEditText" Source: https://gist.github.com/rodrigohenriques/77398a81b5d01ac71c3b
 */

package com.ajapplications.budgeteerbuddy.view.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.View;

import com.ajapplications.budgeteerbuddy.R;
import com.ajapplications.budgeteerbuddy.model.Category;

public class ClickToSelectEditText extends AppCompatEditText {

    Category[] mItems;
    String[] mListableItems;
    CharSequence mHint;

    OnItemSelectedListener onItemSelectedListener;

    public ClickToSelectEditText(Context context) {
        super(context);

        mHint = getHint();
    }

    public ClickToSelectEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHint = getHint();
    }

    public ClickToSelectEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mHint = getHint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setFocusable(false);
        setClickable(true);
    }

    public void setItems(Category[] items) {
        this.mItems = items;
        this.mListableItems = new String[items.length];

        int i = 0;

        for (Category item : mItems) {
            mListableItems[i++] = item.toString();
        }

        configureOnClickListener();
    }

    private void configureOnClickListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(mHint);
                builder.setItems(mListableItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                        setText(mListableItems[selectedIndex]);

                        if (onItemSelectedListener != null) {
                            onItemSelectedListener.onItemSelectedListener(mItems[selectedIndex], selectedIndex);
                        }
                    }
                });
                builder.setPositiveButton(R.string.cancel, null);
                builder.create().show();
            }
        });
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelectedListener(Category item, int selectedIndex);
    }
}