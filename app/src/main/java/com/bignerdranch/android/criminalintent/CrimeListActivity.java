package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by adamc on 1/7/2018.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
