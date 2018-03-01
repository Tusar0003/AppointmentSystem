package com.example.no0ne.appointmentsystem;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TeacherActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private TeacherPagerAdapter mPagerAdapter;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

//    private Boolean mIsFragment = false;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;
    private FirebaseUser mCurrentUser;

    private Button mTeacherEditInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        // To remove shadow below action bar.
        getSupportActionBar().setElevation(0);

        mTeacherEditInfoButton = (Button) findViewById(R.id.button_teacher_edit_info);

        mViewPager = (ViewPager) findViewById(R.id.teacher_view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.teacher_tab_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.teacher_drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);

        mPagerAdapter = new TeacherPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        mTeacherEditInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherActivity.this, TeacherInfoActivity.class);
                startActivity(intent);

//                mIsFragment = true;
//                mTabLayout.setVisibility(View.GONE);
//                mViewPager.setVisibility(View.GONE);
//                mDrawerLayout.closeDrawers();
//
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.frame_layout, new TeacherInfoFragment());
//                transaction.commit();

            }
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        mCurrentUser = mAuth.getCurrentUser();
//
//        if (mCurrentUser == null) {
//            Intent intent = new Intent(TeacherActivity.this, MainActivity.class);
//            startActivity(intent);
//        }
//    }

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
                Intent intent = new Intent(TeacherActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                return false;
        }

        return true;
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        if (mIsFragment) {
//            Intent intent = new Intent(TeacherActivity.this, TeacherActivity.class);
//            startActivity(intent);
//        }
//    }

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
}
