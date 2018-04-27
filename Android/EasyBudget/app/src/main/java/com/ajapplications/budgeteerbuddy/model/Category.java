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

package com.ajapplications.budgeteerbuddy.model;

import android.content.Context;

import com.ajapplications.budgeteerbuddy.R;

public enum Category {
    Income, Savings, Rent_Mortgage, Transportation, Food_Household, Medical, Entertainment, Other;

    public int getPriority(){
        switch(this){
            case Income:
                return 0;
            case Savings:
                return 1;
            case Rent_Mortgage:
            case Food_Household:
                return 2;
            case Transportation:
            case Medical:
                return 3;
            case Entertainment:
            case Other:
                return 4;
            default:
                return 100;
        }
    }

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
