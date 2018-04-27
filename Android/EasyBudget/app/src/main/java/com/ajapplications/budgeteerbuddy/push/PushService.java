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

package com.ajapplications.budgeteerbuddy.push;

import android.app.IntentService;
import android.content.Intent;

/**
 * Service that handles Batch pushes
 *
 * @author Benoit LETONDOR
 */
public class PushService extends IntentService {

// ----------------------------------->

    public PushService() {
        super("BudgeteerBuddyPushService");
    }

// ----------------------------------->

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
        } finally {
            PushReceiver.completeWakefulIntent(intent);
        }
    }
}
