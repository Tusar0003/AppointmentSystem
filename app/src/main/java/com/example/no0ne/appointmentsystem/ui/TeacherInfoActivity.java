package com.example.no0ne.appointmentsystem.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherInfoActivity extends AppCompatActivity {

    private ImageView mTeacherImageView;
    private EditText mUserNameEditText;
    private EditText mEmailEditText;
    private EditText mDepartmentEditText;
    private EditText mPhoneNumberEditText;
    private Spinner mDaysSpinner;
    private EditText mFromEditText;
    private EditText mToEditText;
    private Button mAddViewButton;
    private Button mShowScheduleButton;

    private Calendar mCalendarTime;

    private String mFromTime;
    private String mToTime;
    private String mUserName;
    private String mEmail;
    private String mDepartment;
    private String mPhoneNumber;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    private List<String> mDayList;
    private List<String> mFromList;
    private List<String> mToList;
    private List<String> mStatusList;

    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);

        mTeacherImageView = findViewById(R.id.image_view_teacher);
        mUserNameEditText = findViewById(R.id.edit_text_user_name);
        mEmailEditText = findViewById(R.id.edit_text_email);
        mDepartmentEditText = findViewById(R.id.edit_text_department);
        mPhoneNumberEditText = findViewById(R.id.edit_text_phone_number);
        mDaysSpinner = findViewById(R.id.spinner_days);
        mFromEditText = findViewById(R.id.edit_text_from);
        mToEditText = findViewById(R.id.edit_text_to);
        mAddViewButton = findViewById(R.id.button_add_view);
        mShowScheduleButton = findViewById(R.id.button_show_schedule);

        mCalendarTime = Calendar.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference();

//        mDialog = new Dialog(TeacherInfoActivity.this);

        mFromEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    updateFromTime();
                }

                return true;
            }
        });

        mToEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    updateToTime();
                }

                return true;
            }
        });

        mAddViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSchedule();
            }
        });

        mShowScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSchedule();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setPersonalInfo();
        setSchedule();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveChanges();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFromTime() {
        new TimePickerDialog(this, fromTimeSetListener,
                mCalendarTime.get(Calendar.HOUR_OF_DAY),
                mCalendarTime.get(Calendar.MINUTE),
                false).show();
    }

    TimePickerDialog.OnTimeSetListener fromTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            mFromTime = String.valueOf(hourOfDay + ":" + minute);
            mFromEditText.setText(mFromTime);
        }
    };

    private void updateToTime() {
        new TimePickerDialog(this, toTimeSetListener,
                mCalendarTime.get(Calendar.HOUR_OF_DAY),
                mCalendarTime.get(Calendar.MINUTE),
                false).show();
    }

    TimePickerDialog.OnTimeSetListener toTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            mToTime = String.valueOf(hourOfDay + ":" + minute);
            mToEditText.setText(mToTime);
        }
    };

    private void saveChanges() {
//        Log.e("SPINNER", mDaysSpinner.getSelectedItem().toString());
        final String currentUserId = mAuth.getCurrentUser().getUid();

        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(currentUserId);

        mUserName = mUserNameEditText.getText().toString();
        mEmail = mEmailEditText.getText().toString();
        mDepartment = mDepartmentEditText.getText().toString();
        mPhoneNumber = mPhoneNumberEditText.getText().toString();

        if (TextUtils.isEmpty(mUserName)) {
            Toast.makeText(this, "User name is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mEmail)) {
            Toast.makeText(this, "Email is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDepartment)) {
            Toast.makeText(this, "Department is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mPhoneNumber)) {
            Toast.makeText(this, "Phone number is empty!", Toast.LENGTH_SHORT).show();
        } else {
            Map map = new HashMap();
            map.put("user_name", mUserName);
            map.put("email", mEmail);
            map.put("department", mDepartment);
            map.put("phone_number", mPhoneNumber);

            mUserReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Map map = new HashMap();
                        map.put("user_name", mUserName);
                        map.put("department", mDepartment);

                        mUserReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child("Teachers").child(currentUserId);

                        mUserReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(TeacherInfoActivity.this, "Updated successful!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(TeacherInfoActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else  {
                        Toast.makeText(TeacherInfoActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setPersonalInfo() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserName = dataSnapshot.child("user_name").getValue().toString();
                mEmail = dataSnapshot.child("email").getValue().toString();
                mPhoneNumber = dataSnapshot.child("phone_number").getValue().toString();

                mUserNameEditText.setText(mUserName);
                mEmailEditText.setText(mEmail);
                mPhoneNumberEditText.setText(mPhoneNumber);

                try {
                    mDepartment = dataSnapshot.child("department").getValue().toString();
                    mDepartmentEditText.setText(mDepartment);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addSchedule() {
        final String day = mDaysSpinner.getSelectedItem().toString();
        String currentUserId = mAuth.getCurrentUser().getUid();

        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("Teacher_Schedule").child(currentUserId);

        HashMap<String, String> scheduleMap = new HashMap<>();
        scheduleMap.put("from", mFromTime);
        scheduleMap.put("to", mToTime);
        scheduleMap.put("slot_status", "free");

        mUserReference.child(day).setValue(scheduleMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(TeacherInfoActivity.this, "Added as your counselling hour.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSchedule() {
//        Log.e("SHOW_SCHEDULE_CALLED", "showSchedule() is called");

        setSchedule();
        setAdapter(mDayList, mFromList, mToList, mStatusList);
    }

    private void setSchedule() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("Teacher_Schedule").child(currentUserId);

        mDayList = new ArrayList<>();
        mFromList = new ArrayList<>();
        mToList = new ArrayList<>();
        mStatusList = new ArrayList<>();

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    try {
                        mDayList.add(childSnapshot.getKey().toString());
                        mFromList.add(childSnapshot.child("from").getValue().toString());
                        mToList.add(childSnapshot.child("to").getValue().toString());
                        mStatusList.add(childSnapshot.child("slot_status").getValue().toString());
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter(List<String> dayList, List<String> fromList, List<String> toList, List<String> statusList) {
        mDialog = new Dialog(TeacherInfoActivity.this);
        mDialog.setContentView(R.layout.dialog_schedule);
        mDialog.setTitle("Schedule");
        mDialog.show();

        ScheduleAdapter adapter = new ScheduleAdapter(TeacherInfoActivity.this, dayList, fromList, toList, statusList);
        ListView scheduleListView = mDialog.findViewById(R.id.list_view_schedule);
        scheduleListView.setAdapter(adapter);

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String day = adapterView.getItemAtPosition(position).toString();
//                Toast.makeText(TeacherInfoActivity.this, "Position: " + day, Toast.LENGTH_SHORT).show();

                CharSequence[] options = new CharSequence[]{"Delete Schedule"};

                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherInfoActivity.this);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                deleteSchedule(day);
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

    private void deleteSchedule(String day) {
        String currentUserId = mAuth.getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Teacher_Schedule").child(currentUserId).child(day);

        query.orderByChild(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                mDialog.dismiss();
                Toast.makeText(TeacherInfoActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
