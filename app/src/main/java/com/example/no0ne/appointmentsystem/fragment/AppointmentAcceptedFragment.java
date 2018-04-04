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
import com.example.no0ne.appointmentsystem.ui.StudentActivity;
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
public class AppointmentAcceptedFragment extends Fragment {

    private String mCurrentUserId;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    private ListView mAppointmentAcceptedListView;

    private List<String> mTeacherList;
    private List<String> mDayList;
    private List<String> mStudentIdList;

    private TextView mEmptyTextView;

    public AppointmentAcceptedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment_accepted, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
//        Log.e("CURRENT_USER_ID", mCurrentUserId);

        mAppointmentAcceptedListView = view.findViewById(R.id.list_view_accepted_appointment);

        mEmptyTextView = view.findViewById(R.id.text_view_empty);

        setTeacherList();

        return view;
    }

    private void setTeacherList() {
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Appointment").child(mCurrentUserId);

        mTeacherList = new ArrayList<>();
        mDayList = new ArrayList<>();
        mStudentIdList = new ArrayList<>();

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                        final String studentId = childSnapShot.getKey().toString();
                        String response = childSnapShot.child("response").getValue().toString();

//                        Log.e("STUDENT_ID", studentId + " " + response);

                        if (response.equalsIgnoreCase("accepted")) {
                            mEmptyTextView.setVisibility(View.GONE);

                            mUserReference = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(studentId);

                            mUserReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.child("user_name").getValue().toString();
//                                    Log.e("USER_NAME", userName);
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
        mTeacherList.add(userName);
        mStudentIdList.add(studentId);
//        Log.e("STUDENT_LIST", String.valueOf(mStudentList.size()));
//        Log.e("DAY_LIST", String.valueOf(mDayList.size()));

        setAdapter();
    }

    private void setAdapter() {
        AppointmentAdapter adapter = new AppointmentAdapter(getActivity(), mTeacherList, mDayList, mStudentIdList);
        mAppointmentAcceptedListView.setAdapter(adapter);

        mAppointmentAcceptedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mAppointmentAcceptedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                        CharSequence[] options = new CharSequence[]{"Delete"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                refresh();
                                switch (i) {
                                    case 0:
                                        delete(adapterView.getItemAtPosition(position).toString());
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
        });
    }

    private void delete(final String teacherId) {
        Query teacherQuery = FirebaseDatabase.getInstance().getReference().child("Appointment")
                .child(mCurrentUserId).child(teacherId);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUserReference = FirebaseDatabase.getInstance().getReference().child("Appointment")
                        .child(mCurrentUserId).child(teacherId);

                mUserReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            String day = dataSnapshot.child("day").getValue().toString();
                            Log.e("DAY", day);
                            changeSlotStatus(teacherId, day);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        teacherQuery.orderByChild("response").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue().isSuccessful();

                Query studentQuery = FirebaseDatabase.getInstance().getReference().child("Appointment")
                        .child(teacherId).child(mCurrentUserId);

                studentQuery.orderByChild("response").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue().isSuccessful();
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

    private void changeSlotStatus(String teacherId, String day) {
        Log.e("DAY", day);

        mUserReference = FirebaseDatabase.getInstance().getReference().child("Teacher_Schedule")
                .child(teacherId).child(day);

        Map map = new HashMap();
        map.put("slot_status", "free");

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
        getActivity().startActivity(new Intent(getContext(), StudentActivity.class));
    }
}
