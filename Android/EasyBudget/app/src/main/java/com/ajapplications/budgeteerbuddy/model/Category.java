package com.ajapplications.budgeteerbuddy.model;

import android.content.Context;

import com.ajapplications.budgeteerbuddy.R;

public enum Category {
    Income, Savings, Rent_Mortgage, Transportation, Food_Household, Medical, Entertainment, Other;

    public String toString(Context context) {
        switch (this) {
            case Income:
                return context.getResources().getString(R.string.spending_category_income);
            case Savings:
                return context.getResources().getString(R.string.spending_category_savings);
            case Rent_Mortgage:
                return context.getResources().getString(R.string.spending_category_rent_mortgage);
            case Transportation:
                return context.getResources().getString(R.string.spending_category_transportation);
            case Food_Household:
                return context.getResources().getString(R.string.spending_category_food_household);
            case Medical:
                return context.getResources().getString(R.string.spending_category_medical);
            case Entertainment:
                return context.getResources().getString(R.string.spending_category_entertainment);
            case Other:
                return context.getResources().getString(R.string.spending_category_other);
            default:
                return "";
        }
    }
}
