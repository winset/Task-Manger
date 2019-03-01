package com.example.michail.fragmentedc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.List;


public class TimeListFragment extends Fragment {



    private RecyclerView mTimeRecyclerView;
    private TimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    //private int mPosition;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_list, container, false);
        mTimeRecyclerView = view.findViewById(R.id.time_recycler_view);
        mTimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_time_list, menu);

    }

    private void updateSubtile() {
        TimeLab time = TimeLab.get(getActivity());
        int countOfTask = time.getTimes().size();
        String subtitle = getString(R.string.subtitle_format, countOfTask);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        TimeLab timeLab = TimeLab.get(getActivity());
        List<Time> tasks = timeLab.getTimes();


        if (mAdapter == null) {
            mAdapter = new TimeAdapter(tasks);
            mTimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setTimes(tasks);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtile();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_task:
                Time time = new Time();
                TimeLab.get(getActivity()).addTask(time);
                Intent intent = TimePagerActivity.newIntent(getActivity(), time.getId());
                startActivity(intent);
                updateSubtile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class TimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        private Time mTime;
        private String dateString;
        private String dateOfWeekString;

        public TimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_time, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.time_title);
            mDateTextView = itemView.findViewById(R.id.time_date);
            mSolvedImageView = itemView.findViewById(R.id.task_solved);
        }

        public void bind(Time time) {
            mTime = time;
            SimpleDateFormat sd1 = new SimpleDateFormat("HH:mm");
            String clocktime = sd1.format(mTime.getClockTime());

            SimpleDateFormat sd = new SimpleDateFormat("EEEE");
            String dayofweek = sd.format(mTime.getDate());
            dateString = DateFormat.getDateInstance().format(mTime.getDate());

            mDateTextView.setText(dayofweek + "," + " " + dateString + " " + clocktime);//+" "+clocktime
            mTitleTextView.setText(mTime.getTitle());
            mSolvedImageView.setVisibility(mTime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            // Toast.makeText(getActivity(), mTime.getTitle() + " clicked", Toast.LENGTH_LONG).show();
            // mPosition = getAdapterPosition();
            Intent intent = TimePagerActivity.newIntent(getActivity(), mTime.getId());
            startActivity(intent);

        }
    }

    private class TimeAdapter extends RecyclerView.Adapter<TimeHolder> {
        private List<Time> mTasks;

        public TimeAdapter(List<Time> tasks) {
            mTasks = tasks;
        }

        @Override
        public TimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(TimeHolder holder, int position) {
            Time time = mTasks.get(position);
            holder.bind(time);
        }


        @Override
        public int getItemCount() {
            return mTasks.size();
        }
        public void setTimes(List<Time> tasks){
            mTasks = tasks;
        }

    }
}




