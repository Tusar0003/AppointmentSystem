package com.example.no0ne.appointmentsystem.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.no0ne.appointmentsystem.R;
import com.example.no0ne.appointmentsystem.adapter.AppointmentAdapter;
import com.example.no0ne.appointmentsystem.ui.TeacherActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PendingFragment extends Fragment {

    private String mCurrentUserId;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    private ListView mPendingAppointmentListView;

    private List<String> mStudentList;
    private List<String> mDayList;
    private List<String> mStudentIdList;

    private AppointmentAdapter mAdapter;

    private TextView mEmptyTextView;

    public PendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
//        Log.e("CURRENT_USER_ID", mCurrentUserId);

        mPendingAppointmentListView = view.findViewById(R.id.list_view_pending_appointment);

        mEmptyTextView = view.findViewById(R.id.text_view_empty);

        setStudentList();

        return view;
    }

    private void setStudentList() {
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Appointment").child(mCurrentUserId);

        mStudentList = new ArrayList<>();
        mDayList = new ArrayList<>();
        mStudentIdList = new ArrayList<>();

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                        final String studentId = childSnapShot.getKey().toString();;

                        String response = childSnapShot.child("response").getValue().toString();

                        if (response.equalsIgnoreCase("pending")) {
                            mEmptyTextView.setVisibility(View.GONE);

                            mUserReference = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(studentId);

                            mUserReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.child("user_name").getValue().toString();
                                    setUserName(userName, studentId);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            mDayList.add(childSnapShot.child("day").getValue().toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUserName(String userName, String studentId) {
//        Log.e("ID", studentId);

        mStudentList.add(userName);
        mStudentIdList.add(studentId);

        setAdapter();
    }

    private void setAdapter() {
        mAdapter = new AppointmentAdapter(getActivity(), mStudentList, mDayList, mStudentIdList);
        mPendingAppointmentListView.setAdapter(mAdapter);

        mPendingAppointmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                Log.e("KEY", String.valueOf(adapterView.getItemAtPosition(position)));

                CharSequence[] options = new CharSequence[]{"Accept", "Decline"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        refresh();
                        switch (i) {
                            case 0:
                                accept(adapterView.getItemAtPosition(position).toString());
                                break;
                            case 1:
                                decline(adapterView.getItemAtPosition(position).toString());
                                break;
                            default:
                                return;
                        }
                    }
                });

                builder.show();
            }
        });
    }

    private void accept(final String studentId) {
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Appointment")
                .child(mCurrentUserId).child(studentId);

        Map map = new HashMap();
        map.put("response", "accepted");

        mUserReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isComplete()) {
                    mAdapter.notifyDataSetChanged();
                    mUserReference = FirebaseDatabase.getInstance().getReference().child("Appointment")
                            .child(studentId).child(mCurrentUserId);

                    Map map = new HashMap();
                    map.put("response", "accepted");

                    mUserReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isComplete()) {
//                                Log.e("STUDENT", "student");
                                Toast.makeText(getContext(), "Accepted.", Toast.LENGTH_SHORT).show();

                                mUserReference = FirebaseDatabase.getInstance().getReference().child("Appointment")
                                        .child(studentId).child(mCurrentUserId);

                                mUserReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            String day = dataSnapshot.child("day").getValue().toString();
                                            Log.e("DAY", day);
                                            changeSlotStatus(day);
                                        } catch (Exception e) {

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void decline(final String studentId) {
        Query teacherQuery = FirebaseDatabase.getInstance().getReference().child("Appointment")
                .child(mCurrentUserId).child(studentId);

        teacherQuery.orderByChild("response").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();

                Query studentQuery = FirebaseDatabase.getInstance().getReference().child("Appointment")
                        .child(studentId).child(mCurrentUserId);

                studentQuery.orderByChild("response").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                        Toast.makeText(getContext(), "Declined!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeSlotStatus(String day) {
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Teacher_Schedule")
                .child(mCurrentUserId).child(day);

        Map map = new HashMap();
        map.put("slot_status", "booked");

        mUserReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.e("SUCCESSFUL", "successful");
                }
            }
        });
    }

    private void refresh() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                getActivity().startActivity(new Intent(getContext(), TeacherActivity.class));
//            }
//        });

        getActivity().startActivity(new Intent(getContext(), TeacherActivity.class));
    }
}
