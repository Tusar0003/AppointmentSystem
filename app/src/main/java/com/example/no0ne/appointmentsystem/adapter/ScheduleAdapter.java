package com.example.no0ne.appointmentsystem.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.no0ne.appointmentsystem.R;

import java.util.List;

/**
 * Created by no0ne on 3/22/18.
 */

public class ScheduleAdapter extends ArrayAdapter<String> {

    private Activity mContext;
    private List<String> mDayList, mFromList, mToList;

    public ScheduleAdapter(Activity context, List<String> dayList, List<String> fromList, List<String> toList) {
        super(context, R.layout.single_schedule_view, dayList);

        this.mContext = context;
        this.mDayList = dayList;
        this.mFromList = fromList;
        this.mToList = toList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.single_schedule_view, null, true);

        TextView dayTextView = view.findViewById(R.id.text_view_day);
        TextView fromTextView = view.findViewById(R.id.text_view_from);
        TextView toTextView = view.findViewById(R.id.text_view_to);

        dayTextView.setText(mDayList.get(position));
        fromTextView.setText(mFromList.get(position));
        toTextView.setText(mToList.get(position));

        return view;
    }
}
