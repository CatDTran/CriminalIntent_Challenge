package com.sweet_roll.android.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sweet_roll.android.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

import static com.sweet_roll.android.criminalintent.database.CrimeDbSchema.*;

/**
 * Created by trand_000 on 1/20/2016.
 */
public class CrimeCursorWrapper extends CursorWrapper{
    //CONSTRUCTOR
    public CrimeCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }
    //CALLED TO GET A CRIME
    public Crime getCrime()
    {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        return crime;
    }

}
