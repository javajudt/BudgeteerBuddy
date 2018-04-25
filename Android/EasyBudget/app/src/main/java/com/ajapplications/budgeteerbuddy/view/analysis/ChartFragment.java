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

package com.ajapplications.budgeteerbuddy.view.analysis;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.ajapplications.budgeteerbuddy.model.db.DB;
import com.ajapplications.budgeteerbuddy.view.ExpenseAnalysisActivity;

/**
 * Abstract fragment that contains common methods of all onboarding fragments
 *
 * @author Benoit LETONDOR
 */
public abstract class ChartFragment extends Fragment
{
    /**
     * Get a DB connection if available
     *
     * @return a db connection if available
     */
    @Nullable
    protected DB getDB()
    {
        FragmentActivity activity = getActivity();
        if( activity instanceof ExpenseAnalysisActivity )
        {
            return ((ExpenseAnalysisActivity) activity).getDB();
        }

        return null;
    }
}
