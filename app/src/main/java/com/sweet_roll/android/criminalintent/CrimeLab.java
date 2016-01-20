package com.sweet_roll.android.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sweet_roll.android.criminalintent.database.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by trand_000 on 12/22/2015.
 */
public class CrimeLab{
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    private Context mContext;
    private SQLiteDatabase mDatabase;
    public static CrimeLab get(Context context)
    {
        if(sCrimeLab == null)//if no instance of this class exist
        {
            sCrimeLab = new CrimeLab(context);//then create and allocate an instance
        }
        return sCrimeLab;//return an existing instance if it exists
    }
    //CONSTRUCTOR: PRIVATE, CAN ONLY CALLED BY get()
    private CrimeLab (Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();//open or create a database for this context
        mCrimes = new ArrayList<Crime>();
    }
    //ADD CRIME
    public void addCrime(Crime c)
    {
        mCrimes.add(c);
    }
    //GET CRIMES LIST
    public List<Crime> getCrimes()
    {
        return mCrimes;
    }
    //GET CRIME BY ID
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
