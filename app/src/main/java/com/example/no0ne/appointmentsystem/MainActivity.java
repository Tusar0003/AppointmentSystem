package com.example.no0ne.appointmentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView mSignInTextView;
    private TextView mLogInTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mSignInTextView = (TextView) findViewById(R.id.text_view_sign_in);
        mLogInTextView = (TextView) findViewById(R.id.text_view_log_in);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        mSignInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        mLogInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            String userId = mCurrentUser.getUid();
            mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("account_for");

            mUserReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Toast.makeText(MainActivity.this, "Log In Successful!", Toast.LENGTH_SHORT).show();
//                    Log.e("Account For:", dataSnapshot.getValue().toString());

                    String accountFor = dataSnapshot.getValue().toString();
//                    Log.e("Account For:", accountFor);

                    if (accountFor.equalsIgnoreCase("Student")) {
                        Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                        startActivity(intent);
                    } else if (accountFor.equalsIgnoreCase("Teacher")) {
                        Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Unknown account type!"+accountFor, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
//                    Toast.makeText(MainActivity.this, "onCancelled!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("EXCEPTION", e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mCurrentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }
}
