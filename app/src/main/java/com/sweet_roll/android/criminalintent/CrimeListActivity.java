package com.sweet_roll.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by trand_000 on 12/22/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment()
    {
        return new CrimeListFragment();
    }
}
