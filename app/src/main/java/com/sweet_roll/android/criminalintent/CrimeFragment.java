package com.sweet_roll.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.Date;
import java.util.UUID;

/**
 * Created by trand_000 on 12/19/2015.
 */
public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;

    public static CrimeFragment newInstance(UUID crimeId)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //ONCREATE()
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        //get crime from the one and only singleton CrimeLab associated with CrimePagerActivity
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }
    //CALLED BY HOSTING ACTIVITY TO CREATE FRAGMENT'S VIEW
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });
        //WIRING Button'S VIEW AND CODE
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();//display current date on button with time
//        mDateButton.setText(mCrime.getDateFormatString());//without time
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);//set this fragment as the target fragment with request code: REQUEST_DATE
                dialog.show(manager, DIALOG_DATE);
            }
        });
        //WIRING CheckBox'S VIEW AND CODE
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override//update crime's solved property when check box is changed
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //Set crime's solved status
                mCrime.setSolved(isChecked);
            }
        });
        return v;
    }
    //ONPAUSED()
    @Override
    public void onPause()
    {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }
    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    //Call to get crime report
    private String getCrimeReport()
    {
        String solvedString = null;
        if(mCrime.isSolved())
        {
            solvedString = getString(R.string.crime_report_solved);
        }
        else
        {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EE, MM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if(suspect == null)
        {
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else
        {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
    }
}
