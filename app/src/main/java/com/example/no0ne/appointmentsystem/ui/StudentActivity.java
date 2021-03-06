package com.example.no0ne.appointmentsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.no0ne.appointmentsystem.R;
import com.example.no0ne.appointmentsystem.adapter.StudentPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentActivity extends AppCompatActivity {

    private TextView mUserNameTextView;
    private TextView mDepartmentTextView;
    private Button mEditInfoButton;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private StudentPagerAdapter mPagerAdapter;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // To remove shadow below action bar.
        getSupportActionBar().setElevation(0);

        mUserNameTextView = findViewById(R.id.text_view_user_name);
        mDepartmentTextView = findViewById(R.id.text_view_department);
        mEditInfoButton = findViewById(R.id.button_student_edit_info);

        mViewPager = findViewById(R.id.student_view_pager);
        mTabLayout = findViewById(R.id.student_tab_layout);
        mDrawerLayout = findViewById(R.id.student_drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);

        mPagerAdapter = new StudentPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        mEditInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this, StudentInfoActivity.class);
                startActivity(intent);
            }
        });

        setUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_log_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(StudentActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        finish();
//        System.exit(0);
    }

    private void setUserInfo() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String userName = dataSnapshot.child("user_name").getValue().toString();
                    String department = dataSnapshot.child("department").getValue().toString();

                    mUserNameTextView.setText(userName);
                    mDepartmentTextView.setText(department);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
