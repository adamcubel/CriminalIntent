package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by adamc on 1/7/2018.
 */

// This class uses the RecyclerView to display the fragment on screen
// implementing the necessary methods to create what appears to be a scrolling list of
// crime fragments on the screen
public class CrimeListFragment extends Fragment {
    private static final String SAVED_POSITION = "SAVED_POSITION";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private Callbacks mCallbacks;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private LinearLayout mLinearLayout;
    private Button mAddButton;
    private int mAdapterPosition;
    private boolean mSubtitleVisible;


	public interface Callbacks {
	    void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
	    super.onAttach(context);
	    mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
	    super.onDetach();
	    mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLinearLayout = (LinearLayout) view.findViewById(R.id.empty_crime_list);
        mAddButton = (Button) view.findViewById(R.id.add_crime_button);

        if (savedInstanceState != null) {
            mAdapterPosition = savedInstanceState.getInt(SAVED_POSITION);
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
	    super.onResume();
	    updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle onSavedInstanceState) {
        super.onSaveInstanceState(onSavedInstanceState);
        onSavedInstanceState.putSerializable(SAVED_POSITION, mAdapterPosition);
        onSavedInstanceState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    inflater.inflate(R.menu.fragment_crime_list, menu);
	    MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
	    if (mSubtitleVisible) {
	        subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else {
	        subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                addCrime();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addCrime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        updateUI();
        mCallbacks.onCrimeSelected(crime);
    }

    // Sets up CrimeListFragment's UI
    public void updateUI() {
	    // Get the model singleton
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        // Get the list of crime objects from the singleton
        List<Crime> crimes = crimeLab.getCrimes();
        // Create a new adapter if one does not exist
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyItemChanged(mAdapterPosition);
        }
        // If there are crimes, dont display the "create crime" box in the middle of the screen
        if (crimes.size() > 0) {
            mLinearLayout.setVisibility(View.GONE);
        }
        else {
            mLinearLayout.setVisibility(View.VISIBLE);
            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCrime();
                }
            });
        }

        updateSubtitle();
    }

    private void updateSubtitle() {
	    CrimeLab crimeLab = CrimeLab.get(getActivity());
	    int crimeCount = crimeLab.getCrimes().size();
	    String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

	    if (mSubtitleVisible) {
	        subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Crime mCrime;
		private TextView mTitleTextView;
	    private TextView mDateTextView;
	    private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            // base ViewHolder class variable that holds onto the fragment_crime_list.xml view hierarchy
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            mAdapterPosition = getAdapterPosition();
            // calls callback in CrimeListActivity that starts CrimePagerActivity
            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        // Called by RecyclerView when it needs a new ViewHolder to display an item with.
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        //
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }
}
