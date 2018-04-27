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
 */

package com.ajapplications.budgeteerbuddy.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ajapplications.budgeteerbuddy.R;
import com.ajapplications.budgeteerbuddy.helper.CurrencyHelper;
import com.ajapplications.budgeteerbuddy.helper.Logger;
import com.ajapplications.budgeteerbuddy.helper.UIHelper;
import com.ajapplications.budgeteerbuddy.helper.UserHelper;
import com.ajapplications.budgeteerbuddy.model.Category;
import com.ajapplications.budgeteerbuddy.model.Expense;
import com.ajapplications.budgeteerbuddy.model.RecurringExpense;
import com.ajapplications.budgeteerbuddy.model.RecurringExpenseType;
import com.ajapplications.budgeteerbuddy.view.main.ClickToSelectEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecurringExpenseEditActivity extends DBActivity {
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
     * Spinner to display recurrence interval options
     */
    private Spinner recurringTypeSpinner;

    /**
     * Expense that is being edited (will be null if it's a new one)
     */
    private RecurringExpense expense;
    /**
     * The start date of the expense
     */
    private Date dateStart;
    /**
     * The end date of the expense (not implemented yet)
     */
    private Date dateEnd;
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

// ------------------------------------------->

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_expense_edit);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateStart = new Date(getIntent().getLongExtra("dateStart", 0));

        if (isEdit()) {
            expense = getIntent().getParcelableExtra("expense");
            isRevenue = expense.isRevenue();
            category = expense.getCategory();

            setTitle(isRevenue ? R.string.title_activity_recurring_income_edit : R.string.title_activity_recurring_expense_edit);
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
        if (category.trim().isEmpty()) {
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
                } else if (!this.category.equals(Category.Income) && (-db.getBalanceForDay(dateStart)) - value < 0) {
                    amountEditText.setError(getResources().getString(R.string.negative_balance_error));
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
                setMessage();
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

                    RecurringExpense expenseToSave = new RecurringExpense(category, memoEditText.getText().toString(), isRevenue ? -value : value, dateStart, getRecurringTypeFromSpinnerSelection(recurringTypeSpinner.getSelectedItemPosition()));

                    new SaveRecurringExpenseTask().execute(expenseToSave);
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

            setTitle(isEdit() ? R.string.title_activity_recurring_income_edit : R.string.title_activity_recurring_income_add);

            category = Category.Income;
            categoryEditText.setText(category.toString(this));
        } else {
            expenseType.setText(R.string.payment);
            expenseType.setTextColor(ContextCompat.getColor(this, R.color.budget_red));

            setTitle(isEdit() ? R.string.title_activity_recurring_expense_add : R.string.title_activity_recurring_expense_add);

            if (category.equals(Category.Income)) {
                category = null;
                categoryEditText.setText("");
            }
        }
    }

    /**
     * Set up text fields, spinner and focus behavior
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

                setMessage();
            }
        });
        setMessage();

        recurringTypeSpinner = (Spinner) findViewById(R.id.expense_type_spinner);

        final String[] recurringTypesString = new String[4];
        recurringTypesString[0] = getString(R.string.recurring_interval_weekly);
        recurringTypesString[1] = getString(R.string.recurring_interval_bi_weekly);
        recurringTypesString[2] = getString(R.string.recurring_interval_monthly);
        recurringTypesString[3] = getString(R.string.recurring_interval_yearly);

        final ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.spinner_item, recurringTypesString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringTypeSpinner.setAdapter(adapter);

        if (expense != null) {
            categoryEditText.setText(category.toString(this));

            memoEditText.setText(expense.getTitle());
            memoEditText.setSelection(memoEditText.getText().length()); // Put focus at the end of the text

            amountEditText.setText(CurrencyHelper.getFormattedAmountValue(Math.abs(expense.getAmount())));

            recurringTypeSpinner.setSelection(expense.getType().ordinal(), false);
        } else {
            recurringTypeSpinner.setSelection(2, false);
        }
    }

    /**
     * Get the recurring expense type associated with the spinner selection
     *
     * @param spinnerSelectedItem index of the spinner selection
     * @return the corresponding expense type
     */
    private RecurringExpenseType getRecurringTypeFromSpinnerSelection(int spinnerSelectedItem) {
        switch (spinnerSelectedItem) {
            case 0:
                return RecurringExpenseType.WEEKLY;
            case 1:
                return RecurringExpenseType.BI_WEEKLY;
            case 2:
                return RecurringExpenseType.MONTHLY;
            case 3:
                return RecurringExpenseType.YEARLY;
        }

        throw new IllegalStateException("getRecurringTypeFromSpinnerSelection unable to get value for " + spinnerSelectedItem);
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
                DatePickerDialogFragment fragment = new DatePickerDialogFragment(dateStart, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();

                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        dateStart = cal.getTime();
                        updateDateButtonDisplay();
                    }
                });

                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    private void updateDateButtonDisplay() {
        SimpleDateFormat formatter = new SimpleDateFormat(getResources().getString(R.string.add_expense_date_format), Locale.getDefault());
        dateButton.setText(formatter.format(dateStart));
    }

// ------------------------------------------->

    /**
     * An asynctask to save recurring expense to DB
     */
    private class SaveRecurringExpenseTask extends AsyncTask<RecurringExpense, Integer, Boolean> {
        /**
         * Dialog used to display loading to the user
         */
        private ProgressDialog dialog;

        @Override
        protected Boolean doInBackground(RecurringExpense... expenses) {
            for (RecurringExpense expense : expenses) {
                boolean inserted = db.addRecurringExpense(expense);
                if (!inserted) {
                    Logger.error(false, "Error while inserting recurring expense into DB: addRecurringExpense returned false");
                    return false;
                }

                if (!flattenExpensesForRecurringExpense(expense)) {
                    Logger.error(false, "Error while flattening expenses for recurring expense: flattenExpensesForRecurringExpense returned false");
                    return false;
                }
            }

            return true;
        }

        private boolean flattenExpensesForRecurringExpense(@NonNull RecurringExpense expense) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateStart);

            switch (expense.getType()) {
                case WEEKLY:
                    // Add up to 5 years of expenses
                    for (int i = 0; i < 12 * 4 * 5; i++) {
                        boolean expenseInserted = db.persistExpense(new Expense(category, expense.getTitle(), expense.getAmount(), cal.getTime(), expense));
                        if (!expenseInserted) {
                            Logger.error(false, "Error while inserting expense for recurring expense into DB: persistExpense returned false");
                            return false;
                        }

                        cal.add(Calendar.WEEK_OF_YEAR, 1);

                        if (dateEnd != null && cal.getTime().after(dateEnd)) // If we have an end date, stop to that one
                        {
                            break;
                        }
                    }
                    break;
                case BI_WEEKLY:
                    // Add up to 5 years of expenses
                    for (int i = 0; i < 12 * 4 * 5; i++) {
                        boolean expenseInserted = db.persistExpense(new Expense(category, expense.getTitle(), expense.getAmount(), cal.getTime(), expense));
                        if (!expenseInserted) {
                            Logger.error(false, "Error while inserting expense for recurring expense into DB: persistExpense returned false");
                            return false;
                        }

                        cal.add(Calendar.WEEK_OF_YEAR, 2);

                        if (dateEnd != null && cal.getTime().after(dateEnd)) // If we have an end date, stop to that one
                        {
                            break;
                        }
                    }
                    break;
                case MONTHLY:
                    // Add up to 10 years of expenses
                    for (int i = 0; i < 12 * 10; i++) {
                        boolean expenseInserted = db.persistExpense(new Expense(category, expense.getTitle(), expense.getAmount(), cal.getTime(), expense));
                        if (!expenseInserted) {
                            Logger.error(false, "Error while inserting expense for recurring expense into DB: persistExpense returned false");
                            return false;
                        }

                        cal.add(Calendar.MONTH, 1);

                        if (dateEnd != null && cal.getTime().after(dateEnd)) // If we have an end date, stop to that one
                        {
                            break;
                        }
                    }
                    break;
                case YEARLY:
                    // Add up to 100 years of expenses
                    for (int i = 0; i < 100; i++) {
                        boolean expenseInserted = db.persistExpense(new Expense(category, expense.getTitle(), expense.getAmount(), cal.getTime(), expense));
                        if (!expenseInserted) {
                            Logger.error(false, "Error while inserting expense for recurring expense into DB: persistExpense returned false");
                            return false;
                        }

                        cal.add(Calendar.YEAR, 1);

                        if (dateEnd != null && cal.getTime().after(dateEnd)) // If we have an end date, stop to that one
                        {
                            break;
                        }
                    }
                    break;
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            // Show a ProgressDialog
            dialog = new ProgressDialog(RecurringExpenseEditActivity.this);
            dialog.setIndeterminate(true);
            dialog.setTitle(R.string.recurring_expense_add_loading_title);
            dialog.setMessage(getResources().getString(isRevenue ? R.string.recurring_income_add_loading_message : R.string.recurring_expense_add_loading_message));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // Dismiss the dialog
            dialog.dismiss();

            if (result) {
                setResult(RESULT_OK);
                finish();
            } else {
                new AlertDialog.Builder(RecurringExpenseEditActivity.this)
                        .setTitle(R.string.recurring_expense_add_error_title)
                        .setMessage(getResources().getString(R.string.recurring_expense_add_error_message))
                        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    private void setMessage() {
        TextView messageTextView = (TextView) findViewById(R.id.message_textview);
        int goal = UserHelper.getSavingsGoal(getBaseContext());
        double savingsTotal = db.getTotalForCategory(Category.Savings);

        if (category == null && savingsTotal < goal) {
            messageTextView.setText("");
        } else if (savingsTotal >= goal) {
            messageTextView.setText(R.string.add_expense_message_goal_met);
            messageTextView.setTextColor(getResources().getColor(R.color.budget_green));
        } else if (category.getPriority() == 0) {
            messageTextView.setText(R.string.add_expense_message_0);
            messageTextView.setTextColor(getResources().getColor(R.color.budget_green));
        } else if (category.getPriority() == 1) {
            messageTextView.setText(R.string.add_expense_message_1);
            messageTextView.setTextColor(getResources().getColor(R.color.budget_orange));
        } else if (category.getPriority() == 2) {
            messageTextView.setText(R.string.add_expense_message_2);
            messageTextView.setTextColor(getResources().getColor(R.color.budget_red));
        } else if (category.getPriority() == 3) {
            messageTextView.setText(R.string.add_expense_message_3);
            messageTextView.setTextColor(getResources().getColor(R.color.budget_red));
        } else if (category.getPriority() == 4) {
            messageTextView.setText(R.string.add_expense_message_4);
            messageTextView.setTextColor(getResources().getColor(R.color.budget_red));
        } else {
            messageTextView.setText(R.string.add_expense_message_other);
            messageTextView.setTextColor(getResources().getColor(R.color.budget_red));
        }
    }
}
