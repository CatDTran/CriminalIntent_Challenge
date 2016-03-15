package com.sweet_roll.android.criminalintent.database;

/**
 * Created by trand_000 on 1/19/2016.
 */
//THIS CLASS DEFINE THE SCHEMA FOR OUR SQLITE DATABASE
public class CrimeDbSchema {
    public static final class CrimeTable{
        public static final String NAME= "crimes";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
