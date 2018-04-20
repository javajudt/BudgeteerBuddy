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

package com.ajapplications.budgeteerbuddy;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ajapplications.budgeteerbuddy.helper.Logger;
import com.ajapplications.budgeteerbuddy.helper.ParameterKeys;
import com.ajapplications.budgeteerbuddy.helper.Parameters;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * BudgeteerBuddy application. Implements GA tracking, Batch set-up, Crashlytics set-up && iab.
 *
 * @author Benoit LETONDOR
 */
public class BudgeteerBuddy extends Application
{
    /**
     * Default amount use for low money warning (can be changed in settings)
     */
    public static final int DEFAULT_LOW_MONEY_WARNING_AMOUNT = 100;

// ------------------------------------------>

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Init actions
        init();
    }

    /**
     * Track that user comes from the given invitation id
     *
     * @param invitationId
     */
    public void trackInvitationId(String invitationId)
    {
    }

    /**
     * Track the number of invites sent by the user
     *
     * @param invitationsSent
     */
    public void trackNumberOfInvitsSent(int invitationsSent)
    {
        int invitSent = Parameters.getInstance(getApplicationContext()).getInt(ParameterKeys.NUMBER_OF_INVITATIONS, 0);
        invitSent += invitationsSent;
        Parameters.getInstance(getApplicationContext()).putInt(ParameterKeys.NUMBER_OF_INVITATIONS, invitSent);
    }

    /**
     * Init app const and parameters
     */
    private void init()
    {
        /*
         * Save first launch date if needed
         */
        long initDate = Parameters.getInstance(getApplicationContext()).getLong(ParameterKeys.INIT_DATE, 0);
        if( initDate <= 0 )
        {
            Logger.debug("Registering first launch date");
            Parameters.getInstance(getApplicationContext()).putLong(ParameterKeys.INIT_DATE, new Date().getTime());
        }

        /*
         * Create local ID if needed
         */
        String localId = Parameters.getInstance(getApplicationContext()).getString(ParameterKeys.LOCAL_ID);
        if( localId == null )
        {
            localId = UUID.randomUUID().toString();
            Logger.debug("Generating local id : "+localId);

            Parameters.getInstance(getApplicationContext()).putString(ParameterKeys.LOCAL_ID, localId);
        }
        else
        {
            Logger.debug("Local id : " + localId);
        }

        // Activity counter for app foreground & background
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            private int activityCounter = 0;

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState)
            {

            }

            @Override
            public void onActivityStarted(Activity activity)
            {
                if (activityCounter == 0)
                {
                    onAppForeground(activity);
                }

                activityCounter++;
            }

            @Override
            public void onActivityResumed(Activity activity)
            {

            }

            @Override
            public void onActivityPaused(Activity activity)
            {

            }

            @Override
            public void onActivityStopped(Activity activity)
            {
                if (activityCounter == 1)
                {
                    onAppBackground();
                }

                activityCounter--;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState)
            {

            }

            @Override
            public void onActivityDestroyed(Activity activity)
            {

            }
        });
    }

// -------------------------------------->

    /**
     * Called when the app goes foreground
     *
     * @param activity The activity that gone foreground
     */
    private void onAppForeground(@NonNull Activity activity)
    {
        Logger.debug("onAppForeground");

        /*
         * Increment the number of open
         */
        Parameters.getInstance(getApplicationContext()).putInt(ParameterKeys.NUMBER_OF_OPEN, Parameters.getInstance(getApplicationContext()).getInt(ParameterKeys.NUMBER_OF_OPEN, 0) + 1);

        /*
         * Check if last open is from another day
         */
        boolean shouldIncrementDailyOpen = false;

        long lastOpen = Parameters.getInstance(getApplicationContext()).getLong(ParameterKeys.LAST_OPEN_DATE, 0);
        if( lastOpen > 0 )
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(lastOpen));

            int lastDay = cal.get(Calendar.DAY_OF_YEAR);

            cal.setTime(new Date());
            int currentDay = cal.get(Calendar.DAY_OF_YEAR);

            if( lastDay != currentDay )
            {
                shouldIncrementDailyOpen = true;
            }
        }
        else
        {
            shouldIncrementDailyOpen = true;
        }

        // Increment daily open
        if( shouldIncrementDailyOpen )
        {
            Parameters.getInstance(getApplicationContext()).putInt(ParameterKeys.NUMBER_OF_DAILY_OPEN, Parameters.getInstance(getApplicationContext()).getInt(ParameterKeys.NUMBER_OF_DAILY_OPEN, 0) + 1);
        }

        /*
         * Save last open date
         */
        Parameters.getInstance(getApplicationContext()).putLong(ParameterKeys.LAST_OPEN_DATE, new Date().getTime());
    }

    /**
     * Called when the app goes background
     */
    private void onAppBackground()
    {
        Logger.debug("onAppBackground");
    }

    /**
     * Should be called by the activity calling {@link #launchPremiumPurchaseFlow(Activity, PremiumPurchaseListener)}
     * when receiving the {@link Activity#onActivityResult(int, int, Intent)} call. If this methods
     * returns true, the activity shouldn't do anything, not even calling super.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return true if the event has been handled by iab, false if the activity should handle it
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data)
    {
        //return iabHelper.handleActivityResult(requestCode, resultCode, data);
        return false;
    }
    //endregion
}
