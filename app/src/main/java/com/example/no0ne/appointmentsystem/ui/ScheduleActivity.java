package com.example.no0ne.appointmentsystem.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.no0ne.appointmentsystem.R;
import com.example.no0ne.appointmentsystem.adapter.ScheduleAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private String mTeacherId;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    private TextView mPhoneNumberTextView;
    private TextView mEmailTextView;

    private List<String> mDayList;
    private List<String> mFromList;
    private List<String> mToList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        mAuth = FirebaseAuth.getInstance();

        mPhoneNumberTextView = findViewById(R.id.text_view_phone_number);
        mEmailTextView = findViewById(R.id.text_view_email);

        mTeacherId = getIntent().getStringExtra("userId");

//        mUserReference = FirebaseDatabase.getInstance().getReference().child("Teacher_Schedule").child(mTeacherId);

        setContactInformation();
        setSchedule();
    }

    private void setContactInformation() {
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mTeacherId);

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    mPhoneNumberTextView.setText(dataSnapshot.child("phone_number").getValue().toString());
                    mEmailTextView.setText(dataSnapshot.child("email").getValue().toString());
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setSchedule() {
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Teacher_Schedule").child(mTeacherId);

        mDayList = new ArrayList<>();
        mFromList = new ArrayList<>();
        mToList = new ArrayList<>();

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                        mDayList.add(childSnapShot.getKey().toString());
                        mFromList.add(childSnapShot.child("from").getValue().toString());
                        mToList.add(childSnapShot.child("to").getValue().toString());
                    }
                } catch (Exception e) {

                }

                setAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter() {
        ScheduleAdapter adapter = new ScheduleAdapter(this, mDayList, mFromList, mToList);
        ListView scheduleListView = findViewById(R.id.list_view_schedule_activity);
        scheduleListView.setAdapter(adapter);

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String day = adapterView.getItemAtPosition(position).toString();
//                Toast.makeText(ScheduleActivity.this, "Position: " + day, Toast.LENGTH_SHORT).show();

                mUserReference = FirebaseDatabase.getInstance().getReference().child("Teacher_Schedule")
                        .child(mTeacherId).child(day);

                mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String slotStatus = dataSnapshot.child("slot_status").getValue().toString();

                        if (slotStatus.equalsIgnoreCase("free")) {
                            getAppointment(day);
                        } else {
                            Toast.makeText(ScheduleActivity.this, "Sorry, this slot is not free!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void getAppointment(final String day) {
        CharSequence[] options = new CharSequence[]{"Get Appointment"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recreate();

                switch (i) {
                    case 0:
                        final String currentUserId = mAuth.getCurrentUser().getUid();

                        mUserReference = FirebaseDatabase.getInstance().getReference().child("Appointment")
                                .child(mTeacherId).child(currentUserId);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("day", day);
                        hashMap.put("response", "pending");

                        mUserReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mUserReference = FirebaseDatabase.getInstance().getReference().child("Appointment")
                                            .child(currentUserId).child(mTeacherId);

                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("day", day);
                                    hashMap.put("response", "pending");

                                    mUserReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ScheduleActivity.this, "Appointment request sent.",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ScheduleActivity.this, "Something went wrong!",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(ScheduleActivity.this, "Something went wrong!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    default:
                        return;
                }
            }
        });

        builder.show();
    }
}
