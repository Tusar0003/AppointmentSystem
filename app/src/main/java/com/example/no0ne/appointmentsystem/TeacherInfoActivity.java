package com.example.no0ne.appointmentsystem;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TeacherInfoActivity extends AppCompatActivity {

    private LinearLayout mParentLinearLayout;
    private LinearLayout mLinearLayout;
    private ImageView mTeacherImageView;
    private EditText mUserNameEditText;
    private EditText mEmailEditText;
    private EditText mDepartmentEditText;
    private EditText mPhoneNumberEditText;
    private Spinner mDaysSpinner;
    private EditText mFromEditText;
    private EditText mToEditText;
    private TextView mDayTextView;
    private TextView mFromTextView;
    private TextView mToTextView;
    private Button mAddViewButton;
    private Button mShowScheduleButton;
    private Button mDeleteButton;

    private Calendar mCalendarTime;

    private String mFromTime;
    private String mToTime;
    private String mUserName;
    private String mEmail;
    private String mDepartment;
    private String mPhoneNumber;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);

        mParentLinearLayout = findViewById(R.id.parent_linear_layout);
        mLinearLayout = findViewById(R.id.linear_layout);
        mTeacherImageView = findViewById(R.id.image_view_teacher);
        mUserNameEditText = findViewById(R.id.edit_text_user_name);
        mEmailEditText = findViewById(R.id.edit_text_email);
        mDepartmentEditText = findViewById(R.id.edit_text_department);
        mPhoneNumberEditText = findViewById(R.id.edit_text_phone_number);
        mDaysSpinner = findViewById(R.id.spinner_days);
        mFromEditText = findViewById(R.id.edit_text_from);
        mToEditText = findViewById(R.id.edit_text_to);
        mDayTextView = findViewById(R.id.text_view_day);
        mFromTextView = findViewById(R.id.text_view_from);
        mToTextView = findViewById(R.id.text_view_to);
        mAddViewButton = findViewById(R.id.button_add_view);
        mShowScheduleButton = findViewById(R.id.button_show_schedule);
        mDeleteButton = findViewById(R.id.button_delete);

        mCalendarTime = Calendar.getInstance();

        mAuth = FirebaseAuth.getInstance();

        mFromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFromTime();
            }
        });

        mToEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateToTime();
            }
        });

        mAddViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSchedule();
                mLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        mShowScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSchedule();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSchedule();
                mLinearLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher_info, menu);
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
            Toast.makeText(TeacherInfoActivity.this, "User name is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mEmail)) {
            Toast.makeText(TeacherInfoActivity.this, "Email is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDepartment)) {
            Toast.makeText(TeacherInfoActivity.this, "Department is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mPhoneNumber)) {
            Toast.makeText(TeacherInfoActivity.this, "Phone number is empty!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(TeacherInfoActivity.this, "Update successful!", Toast.LENGTH_SHORT).show();
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

    private void addSchedule() {
        final String day = mDaysSpinner.getSelectedItem().toString();
        String currentUserId = mAuth.getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Teacher_Schedule").child(currentUserId);

        HashMap<String, String> scheduleMap = new HashMap<>();
//        scheduleMap.put("day", day);
        scheduleMap.put("from", mFromTime);
        scheduleMap.put("to", mToTime);

        reference.child(day).setValue(scheduleMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDayTextView.setText(day);
                    mFromTextView.setText(mFromTime);
                    mToTextView.setText(mToTime);

                    Toast.makeText(TeacherInfoActivity.this, "Added as your counselling hour.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSchedule() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("Teacher_Schedule").child(currentUserId);

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherInfoActivity.this);
                builder.setTitle("Schedule");
                builder.setMessage(dataSnapshot.getValue().toString());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void deleteSchedule() {
        String day = mDaysSpinner.getSelectedItem().toString();
        String currentUserId = mAuth.getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Teacher_Schedule").child(currentUserId).child(day);

        query.orderByChild(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                Toast.makeText(TeacherInfoActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                dataSnapshot.getRef().removeValue();
//                Log.e("REFERENCE", dataSnapshot.getRef().toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void setInfo() {
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
}
