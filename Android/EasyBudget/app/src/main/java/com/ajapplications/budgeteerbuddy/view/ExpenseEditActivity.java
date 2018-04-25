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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.ajapplications.budgeteerbuddy.helper.CurrencyHelper;
import com.ajapplications.budgeteerbuddy.helper.UIHelper;
import com.ajapplications.budgeteerbuddy.model.Category;
import com.ajapplications.budgeteerbuddy.model.Expense;
import com.ajapplications.budgeteerbuddy.R;
import com.ajapplications.budgeteerbuddy.view.main.ClickToSelectEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activity to add a new expense
 *
 * @author Benoit LETONDOR
 */
public class ExpenseEditActivity extends DBActivity {
    /**
     * Save floating action button
     */
    private FloatingActionButton fab;
    /**
     * Edit text that contains the category
     */
    private ClickToSelectEditText categoryEditText;
    /**
     * Edit text that contains the description
     */
    private EditText memoEditText;
    /**
     * Edit text that contains the amount
     */
    private EditText amountEditText;
    /**
     * Button for date selection
     */
    private Button dateButton;
    /**
     * Textview that displays the type of expense
     */
    private TextView expenseType;
    /**
     * Expense that is being edited (will be null if it's a new one)
     */
    private Expense expense;
    /**
     * The date of the expense
     */
    private Date date;
    /**
     * Is the new expense a revenue
     */
    private boolean isRevenue = false;
    /**
     * Selected category for the expense
     */
    private Category category;
    /**
     * Switch for expense or income
     */
    private SwitchCompat expenseTypeSwitch;

// -------------------------------------->

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_edit);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        date = new Date(getIntent().getLongExtra("date", 0));

        if (isEdit()) {
            expense = getIntent().getParcelableExtra("expense");
            isRevenue = expense.isRevenue();
            date = expense.getDate();
            category = expense.getCategory();

            setTitle(isRevenue ? R.string.title_activity_edit_income : R.string.title_activity_edit_expense);
        }

        setUpButtons();
        setUpInputs();
        setUpDateButton();

        setResult(RESULT_CANCELED);

        if (UIHelper.willAnimateActivityEnter(this)) {
            UIHelper.animateActivityEnter(this, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    UIHelper.showFAB(fab);
                }
            });
        } else
            UIHelper.showFAB(fab);
    }

// ----------------------------------->

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) // Back button of the actionbar
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

// ----------------------------------->

    /**
     * Is the current action an edit (vs a creation of a new one)
     *
     * @return
     */
    private boolean isEdit() {
        return getIntent().hasExtra("expense");
    }

    /**
     * Validate user inputs
     *
     * @return true if user inputs are ok, false otherwise
     */
    private boolean validateInputs() {
        boolean ok = true;

        String category = categoryEditText.getText().toString();
        if (category.trim().isEmpty()){
            categoryEditText.setError(getResources().getString(R.string.no_category_error));
            ok = false;
        }

        String amount = amountEditText.getText().toString();
        if (amount.trim().isEmpty()) {
            amountEditText.setError(getResources().getString(R.string.no_amount_error));
            ok = false;
        } else {
            try {
                double value = Double.parseDouble(amount);
                if (value <= 0) {
                    amountEditText.setError(getResources().getString(R.string.negative_amount_error));
                    ok = false;
                }
            } catch (Exception e) {
                amountEditText.setError(getResources().getString(R.string.invalid_amount));
                ok = false;
            }
        }

        return ok;
    }

    /**
     * Set-up revenue and payment buttons
     */
    private void setUpButtons() {
        expenseType = (TextView) findViewById(R.id.expense_type_tv);

        expenseTypeSwitch = (SwitchCompat) findViewById(R.id.expense_type_switch);
        expenseTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRevenue = isChecked;
                setExpenseTypeTextViewLayout();
            }
        });

        // Init value to check if already a revenue (can be true if we are editing an expense)
        if (isRevenue) {
            expenseTypeSwitch.setChecked(true);
            setExpenseTypeTextViewLayout();
        }

        fab = (FloatingActionButton) findViewById(R.id.save_expense_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    double value = Double.parseDouble(amountEditText.getText().toString());

                    Expense expenseToSave;
                    if (expense == null) {
                        expenseToSave = new Expense(category, memoEditText.getText().toString(), isRevenue ? -value : value, date);
                    } else {
                        expenseToSave = expense;
                        expenseToSave.setCategory(category);
                        expenseToSave.setTitle(memoEditText.getText().toString());
                        expenseToSave.setAmount(isRevenue ? -value : value);
                        expenseToSave.setDate(date);
                    }

                    db.persistExpense(expenseToSave);

                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    /**
     * Set revenue text view layout
     */
    private void setExpenseTypeTextViewLayout() {
        if (isRevenue) {
            expenseType.setText(R.string.income);
            expenseType.setTextColor(ContextCompat.getColor(this, R.color.budget_green));

            setTitle(isEdit() ? R.string.title_activity_edit_income : R.string.title_activity_add_income);

            category = Category.Income;
            if (categoryEditText != null)
                categoryEditText.setText(category.toString(this));
        } else {
            expenseType.setText(R.string.payment);
            expenseType.setTextColor(ContextCompat.getColor(this, R.color.budget_red));

            setTitle(isEdit() ? R.string.title_activity_edit_expense : R.string.title_activity_add_expense);

            if (category.equals(Category.Income)) {
                category = null;
                categoryEditText.setText("");
            }
        }
    }

    /**
     * Set up text field focus behavior
     */
    private void setUpInputs() {
        ((TextInputLayout) findViewById(R.id.amount_inputlayout)).setHint(getResources().getString(R.string.amount));

        memoEditText = (EditText) findViewById(R.id.memo_edittext);

        amountEditText = (EditText) findViewById(R.id.amount_edittext);
        UIHelper.preventUnsupportedInputForDecimals(amountEditText);

        categoryEditText = (ClickToSelectEditText) findViewById(R.id.category_selectedittext);
        categoryEditText.setItems(Category.values());
        categoryEditText.setOnItemSelectedListener(new ClickToSelectEditText.OnItemSelectedListener() {
            @Override
            public void onItemSelectedListener(Category item, int selectedIndex) {
                category = item;
                categoryEditText.setText(category.toString(getBaseContext()));

                expenseTypeSwitch.setChecked(category.equals(Category.Income));
            }
        });

        if (expense != null) {
            categoryEditText.setText(category.toString(this));

            memoEditText.setText(expense.getTitle());
            memoEditText.setSelection(memoEditText.getText().length()); // Put focus at the end of the text

            amountEditText.setText(CurrencyHelper.getFormattedAmountValue(Math.abs(expense.getAmount())));
        }
    }

    /**
     * Set up the date button
     */
    private void setUpDateButton() {
        dateButton = (Button) findViewById(R.id.date_button);
        UIHelper.removeButtonBorder(dateButton); // Remove border on lollipop

        updateDateButtonDisplay();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment fragment = new DatePickerDialogFragment(date, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();

                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        date = cal.getTime();
                        updateDateButtonDisplay();
                    }
                });

                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    private void updateDateButtonDisplay() {
        SimpleDateFormat formatter = new SimpleDateFormat(getResources().getString(R.string.add_expense_date_format), Locale.getDefault());
        dateButton.setText(formatter.format(date));
    }
}
