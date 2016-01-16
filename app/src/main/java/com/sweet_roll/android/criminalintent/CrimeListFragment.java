package com.sweet_roll.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by trand_000 on 12/22/2015.
 */
public class CrimeListFragment extends Fragment{
    //////INNER CLASS ViewHolder for this Fragment//////
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;
        //inner class constructor
        public CrimeHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }
        //implement onClick() method
        @Override
        public void onClick(View v)
        {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
        public void bindCrime(Crime crime)
        {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }
    }
    //////INNER CLASS Adapter for this Fragment//////
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;
        //inner class constructor
        public CrimeAdapter(List <Crime> crimes){
            mCrimes = crimes;
        }
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent,int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime,parent,false);
            return new CrimeHolder(view);
        }
        @Override
        public void onBindViewHolder(CrimeHolder holder,int position)
        {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }
        @Override
        public int getItemCount()
        {
            return mCrimes.size();
        }
    }

    //////RecyclerView for this fragment///////
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    //CALLED BY OS
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    //////CALLED BY OS TO INSTANTIATE Fragment's view//////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
//        LinearLayoutManager mLinearManager = new LinearLayoutManager(getActivity());
//        mCrimeRecyclerView.setLayoutManager(mLinearManager);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }
    //CREATE AND INFALTE MENU
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
    }
    //method to update UI
    private void updateUI()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());//the one and only CrimeLab singleton for this Activity
        List<Crime> crimes = crimeLab.getCrimes();//get the list of crimes
        if(mAdapter == null)//if mAdapter was not previously initialized
        {
            mAdapter = new CrimeAdapter(crimes);//create a CrimeAdapter instance
            mCrimeRecyclerView.setAdapter(mAdapter);//set adapter for this RecyclerView
        }
        else//if mAdapter was already initialized
        {
            mAdapter.notifyDataSetChanged();
        }
    }
}
