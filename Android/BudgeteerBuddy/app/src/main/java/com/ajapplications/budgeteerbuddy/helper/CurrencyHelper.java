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

package com.ajapplications.budgeteerbuddy.helper;

import com.ajapplications.budgeteerbuddy.BuildConfig;

import java.text.NumberFormat;
import java.util.Currency;

/**
 * Helper to work with currencies and display
 *
 * @author Benoit LETONDOR
 */
public class CurrencyHelper {
    public static Currency UsdCurrency = Currency.getInstance("USD");
    public static String CurrencySymbol = UsdCurrency.getSymbol();

    /**
     * Helper to display an amount using the user currency
     *
     * @param amount
     * @return
     */
    public static String getFormattedCurrencyString(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        // No fraction digits
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);

        currencyFormat.setCurrency(UsdCurrency);

        return currencyFormat.format(amount);
    }

    /**
     * Helper to display an amount into an edit text
     *
     * @param amount
     * @return
     */
    public static String getFormattedAmountValue(double amount) {
        NumberFormat format = NumberFormat.getInstance();

        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setGroupingUsed(false);

        return format.format(amount);
    }

    /**
     * Return the integer value of the double * 100 to store it as integer in DB. This is an ugly
     * method that shouldn't be there but rounding on doubles are a pain :/
     *
     * @param value the double value
     * @return the corresponding int value (double * 100)
     */
    public static long getDBValueForDouble(double value) {
        String stringValue = getFormattedAmountValue(value);
        if (BuildConfig.DEBUG_LOG) Logger.debug("getDBValueForDouble: " + stringValue);

        long ceiledValue = (long) Math.ceil(value * 100);
        double ceiledDoubleValue = ceiledValue / 100.d;

        if (getFormattedAmountValue(ceiledDoubleValue).equals(stringValue)) {
            if (BuildConfig.DEBUG_LOG)
                Logger.debug("getDBValueForDouble, return ceiled value: " + ceiledValue);
            return ceiledValue;
        }

        long normalValue = (long) value * 100;
        double normalDoubleValue = normalValue / 100.d;

        if (getFormattedAmountValue(normalDoubleValue).equals(stringValue)) {
            if (BuildConfig.DEBUG_LOG)
                Logger.debug("getDBValueForDouble, return normal value: " + normalValue);
            return normalValue;
        }

        long flooredValue = (long) Math.floor(value * 100);
        if (BuildConfig.DEBUG_LOG)
            Logger.debug("getDBValueForDouble, return floored value: " + flooredValue);

        return flooredValue;
    }
}
