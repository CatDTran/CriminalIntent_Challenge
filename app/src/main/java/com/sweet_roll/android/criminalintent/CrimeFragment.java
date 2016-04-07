package com.sweet_roll.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
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
    private Button  mSuspectButton;
    private Button mReportButton;
    private Button mCallSuspectButton;
    private String mSuspectID;
    private String mSuspectNumber;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

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
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);//set this fragment as the target fragment with request code: REQUEST_DATE
                dialog.show(manager, DIALOG_DATE);
            }
        });
        //WIRING CheckBox'S VIEW AND CODE
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override//update crime's solved property when check box is changed
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Set crime's solved status
                mCrime.setSolved(isChecked);
            }
        });
        //REPORT BUTTON
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            //called when button is clicked
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                                //.setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .startChooser();//start an Activity chooser

//                Intent i = new Intent();
//                i.setAction(Intent.ACTION_SEND);
//                i.setType(("text/plain"));//set intent type to "text/plain"
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_suspect));
//                i = Intent.createChooser(i, getString(R.string.send_report));//forcing an Activities chooser
//                startActivity(i);//send out intent for os to pick an appropriate activity
            }
        });
        //CHOOSE SUSPECT BUTTON
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        //call suspect button
        mCallSuspectButton = (Button) v.findViewById(R.id.call_suspect);
        Uri number = Uri.parse("tel: " + mSuspectNumber);
        final Intent callSuspect = new Intent(Intent.ACTION_DIAL, number);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(callSuspect);
            }
        });
        //guarding against when OS has no contacts app
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null)//ask PackageManager to find activities that match the intent; if not return null
        {
            mSuspectButton.setEnabled(false);
        }
        return v;
    }

    //ONRESUME
    @Override
    public void onResume()
    {
        super.onResume();
        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        else
        {
            mCallSuspectButton.setEnabled(false);
        }

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
        else if(requestCode == REQUEST_CONTACT && data != null)//if returned from Activity with REQUEST_CONTACT as request code
        {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};//specify which fields query will return
            //Cursor phoneNumberCursor = getActivity().getContentResolver().query()
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);//perform selection query
            try //in case of an exception
            {
                if (c.getCount() == 0)
                    return;
                c.moveToFirst();//move to first row
                mSuspectID = c.getString(0);
                String suspect = c.getString(1);//get the second column of the first row which is now the name of the contact
                String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone._ID,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor d = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,projection,null,null,null);
                int dCol = d.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                d.
                //mSuspectNumber = d.getString(dCol);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }
            finally
            {
                c.close();
            }
        }
    }
}
