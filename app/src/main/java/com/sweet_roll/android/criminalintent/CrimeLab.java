package com.sweet_roll.android.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by trand_000 on 12/22/2015.
 */
public class CrimeLab{
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context)
    {
        if(sCrimeLab == null)//if no instance of this class exist
        {
            sCrimeLab = new CrimeLab(context);//then create and allocate an instance
        }
        return sCrimeLab;//return an existing instance if it exists
    }
    //private constructor; can only be called via public get() method
    private CrimeLab (Context context)
    {
        mCrimes = new ArrayList<Crime>();
    }
    //ADD CRIME
    public void addCrime(Crime c)
    {
        mCrimes.add(c);
    }
    //get crimes list
    public List<Crime> getCrimes()
    {
        return mCrimes;
    }
    //get a crime object using an ID
    public Crime getCrime(UUID id)
    {
        for(Crime crime : mCrimes)
        {
            if(crime.getId().equals(id))
            {
                return crime;
            }
        }
        return null;
    }
}
