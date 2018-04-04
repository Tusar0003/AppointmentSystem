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
 * Created by no0ne on 3/30/18.
 */

public class AppointmentAdapter extends ArrayAdapter<String> {

    private Activity mContext;
    private List<String> mStudentList, mDayList, mStudentIdList;

    public AppointmentAdapter(Activity context, List<String> studentList, List<String> dayList, List<String> idList) {
        super(context, R.layout.single_appointment_view, idList);

        this.mContext = context;
        this.mStudentList = studentList;
        this.mDayList = dayList;
        this.mStudentIdList = idList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.single_appointment_view, null, true);

        TextView userNameTextView = view.findViewById(R.id.text_view_user_name);
        TextView dayTextView = view.findViewById(R.id.text_view_day);

        userNameTextView.setText(mStudentList.get(position));
        dayTextView.setText(mDayList.get(position));

        return view;
    }
}
